<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待接单" :value="1"></el-option>
            <el-option label="已接单" :value="2"></el-option>
            <el-option label="处理中" :value="3"></el-option>
            <el-option label="已完成" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="160"></el-table-column>
      <el-table-column prop="title" label="咨询标题" min-width="180"></el-table-column>
      <el-table-column prop="consultType" label="咨询方式" width="100">
        <template slot-scope="scope">
          {{ getConsultType(scope.row.consultType) }}
        </template>
      </el-table-column>
      <el-table-column prop="price" label="金额" width="100">
        <template slot-scope="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button type="primary" size="small" v-if="scope.row.status === 1" @click="handleAccept(scope.row)">接单</el-button>
          <el-button type="success" size="small" v-if="scope.row.status === 2" @click="handleProcess(scope.row)">处理</el-button>
          <el-button type="warning" size="small" v-if="scope.row.status >= 2 && scope.row.status <= 3" @click="handleChat(scope.row)">沟通</el-button>
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      class="mt-20"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      :current-page="currentPage"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    ></el-pagination>
    
    <el-dialog title="订单详情" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="咨询方式">{{ getConsultType(currentOrder.consultType) }}</el-descriptions-item>
        <el-descriptions-item label="咨询标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="咨询内容" :span="2">{{ currentOrder.content }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="currentOrder.result">{{ currentOrder.result }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <el-dialog title="处理订单" :visible.sync="processDialogVisible" width="500px">
      <el-form ref="processForm" :model="processForm" label-width="80px">
        <el-form-item label="处理结果">
          <el-input type="textarea" v-model="processForm.result" :rows="5" placeholder="请输入处理结果"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess">提交并完成</el-button>
      </div>
    </el-dialog>
    
    <el-dialog title="订单沟通" :visible.sync="chatDialogVisible" width="600px" @close="closeChat">
      <div class="chat-container" v-if="chatTargetId">
        <div class="chat-messages" ref="chatMessageContainer">
          <div
            v-for="msg in chatMessages"
            :key="msg.id"
            :class="['message-item', { 'message-self': msg.fromUserId === currentUserId }]"
          >
            <div class="message-content">
              <div class="message-bubble">{{ msg.content }}</div>
              <div class="message-time">{{ formatChatTime(msg.createTime) }}</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="chatInput"
            placeholder="输入消息..."
            @keyup.enter.native="sendChatMessage"
          >
            <el-button slot="append" type="primary" @click="sendChatMessage">发送</el-button>
          </el-input>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getConsultList, acceptConsult, processConsult, finishConsult } from '@/api/consult'
import { getChatHistory, sendMessage } from '@/api/chat'

export default {
  name: 'LawyerConsult',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        status: null
      },
      dialogVisible: false,
      currentOrder: null,
      processDialogVisible: false,
      processForm: {
        id: null,
        result: ''
      },
      chatDialogVisible: false,
      chatTargetId: null,
      chatMessages: [],
      chatInput: ''
    }
  },
  computed: {
    currentUserId() {
      const userInfo = this.$store.getters.userInfo
      return userInfo?.userId || userInfo?.id
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getConsultList({
          page: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    async handleAccept(row) {
      try {
        await acceptConsult(row.id)
        this.$message.success('接单成功')
        this.loadData()
      } catch (error) {
        console.error(error)
      }
    },
    handleProcess(row) {
      this.processForm = { id: row.id, result: '' }
      this.processDialogVisible = true
    },
    async submitProcess() {
      try {
        await processConsult(this.processForm.id, this.processForm.result)
        await finishConsult(this.processForm.id)
        this.$message.success('处理完成')
        this.processDialogVisible = false
        this.loadData()
      } catch (error) {
        console.error(error)
      }
    },
    handleView(row) {
      this.currentOrder = row
      this.dialogVisible = true
    },
    async handleChat(row) {
      if (!row.userId) {
        this.$message.warning('该订单没有用户信息')
        return
      }
      if (!this.currentUserId) {
        this.$message.warning('请先登录')
        return
      }
      this.chatTargetId = row.userId
      this.chatMessages = []
      this.chatDialogVisible = true
      await this.loadChatMessages()
    },
    async loadChatMessages() {
      if (!this.currentUserId || !this.chatTargetId) return
      try {
        const res = await getChatHistory(this.currentUserId, this.chatTargetId, 1, 50)
        this.chatMessages = res.data || []
        this.$nextTick(() => {
          this.scrollChatToBottom()
        })
      } catch (error) {
        console.error('加载聊天记录失败:', error)
      }
    },
    async sendChatMessage() {
      if (!this.chatInput.trim()) return
      try {
        await sendMessage({
          fromUserId: this.currentUserId,
          toUserId: this.chatTargetId,
          messageType: 1,
          content: this.chatInput.trim()
        })
        this.chatInput = ''
        await this.loadChatMessages()
      } catch (error) {
        console.error(error)
      }
    },
    closeChat() {
      this.chatTargetId = null
      this.chatMessages = []
      this.chatInput = ''
    },
    scrollChatToBottom() {
      const container = this.$refs.chatMessageContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },
    formatChatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString()
    },
    getConsultType(type) {
      const types = { 1: '图文咨询' }
      return types[type] || '图文咨询'
    },
    getStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success' }
      return types[status]
    },
    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '已接单', 3: '处理中', 4: '已完成' }
      return labels[status]
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-container {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.message-item {
  margin-bottom: 16px;
  
  &.message-self {
    .message-content {
      margin-left: auto;
    }
    
    .message-bubble {
      background: #409eff;
      color: #fff;
    }
    
    .message-time {
      text-align: right;
    }
  }
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 10px 14px;
  background: #fff;
  border-radius: 8px;
  word-break: break-word;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.chat-input {
  flex-shrink: 0;
}
</style>
