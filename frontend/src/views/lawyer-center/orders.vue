<!--<template>
  <div class="orders-page">
    <el-card>
      <div slot="header">
        <span>我的订单</span>
      </div>
      <div class="search-form">
        <el-form inline>
          <el-form-item label="订单状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待接单" :value="1"></el-option>
              <el-option label="已接单" :value="2"></el-option>
              <el-option label="处理中" :value="3"></el-option>
              <el-option label="已完成" :value="4"></el-option>
              <el-option label="已取消" :value="5"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="订单编号">
            <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
        <el-table-column prop="title" label="咨询标题" min-width="180"></el-table-column>
        <el-table-column prop="clientName" label="客户" width="100"></el-table-column>
        <el-table-column prop="price" label="金额" width="100">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template slot-scope="scope">
            <el-progress :percentage="getProgress(scope.row.status)" :status="getProgressStatus(scope.row.status)"></el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" v-if="scope.row.status === 1" @click="handleAccept(scope.row)">接单</el-button>
            <el-button type="success" size="small" v-if="scope.row.status === 2" @click="handleProcess(scope.row)">处理</el-button>
            <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" size="small" @click="handleCommunicate(scope.row)">沟通</el-button>
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
    </el-card>
    
    <el-dialog title="订单详情" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ getStatusLabel(currentOrder.status) }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentOrder.clientName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="咨询标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="咨询内容" :span="2">{{ currentOrder.content }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
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
    
    <el-dialog title="订单沟通" :visible.sync="chatDialogVisible" width="600px" @close="closeChat" class="chat-dialog">
      <div class="chat-container" v-if="chatTargetId">
        <div class="chat-messages" ref="chatMessageContainer">
          <div v-for="(msg, index) in chatMessages" :key="msg.id || index" :class="['message-item', { 'message-self': msg.fromUserId === currentUserId }]">
            <div class="message-main">
              <div class="message-bubble">{{ msg.content }}</div>
            </div>
            <div class="message-time">{{ formatChatTime(msg.createTime) }}</div>
          </div>
          <el-empty v-if="chatMessages.length === 0" description="暂无聊天记录" :image-size="80"></el-empty>
        </div>
        <div class="chat-input-area">
          <el-input type="textarea" v-model="chatInput" placeholder="输入消息..." :rows="2" resize="none" maxlength="500" show-word-limit @keyup.enter.native.exact="sendChatMessage"></el-input>
          <div class="chat-send-btn">
            <el-button type="primary" :disabled="!chatInput.trim()" @click="sendChatMessage">发送</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getConsultList, acceptConsult, processConsult, finishConsult } from '@/api/consult'
import { getChatHistory, sendMessage } from '@/api/chat'

export default {
  name: 'LawyerOrders',
  data() {
    return {
      loading: false,
      searchForm: {
        status: null,
        orderNo: ''
      },
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
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
    handleReset() {
      this.searchForm = { status: null, orderNo: '' }
      this.handleSearch()
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
    async handleCommunicate(row) {
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
      const content = this.chatInput.trim()
      this.chatInput = ''
      
      const tempMsg = {
        id: Date.now(),
        fromUserId: this.currentUserId,
        toUserId: this.chatTargetId,
        messageType: 1,
        content: content,
        createTime: new Date().toISOString()
      }
      this.chatMessages.push(tempMsg)
      this.$nextTick(() => {
        this.scrollChatToBottom()
      })
      
      try {
        await sendMessage({
          fromUserId: this.currentUserId,
          toUserId: this.chatTargetId,
          messageType: 1,
          content: content
        })
        await this.loadChatMessages()
      } catch (error) {
        console.error('发送消息失败:', error)
        const index = this.chatMessages.findIndex(m => m.id === tempMsg.id)
        if (index > -1) {
          this.chatMessages.splice(index, 1)
        }
        this.$message.error('发送失败')
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
    getStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success', 5: 'danger' }
      return types[status]
    },
    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '已接单', 3: '处理中', 4: '已完成', 5: '已取消' }
      return labels[status]
    },
    getProgress(status) {
      const progress = { 1: 25, 2: 50, 3: 75, 4: 100, 5: 0 }
      return progress[status]
    },
    getProgressStatus(status) {
      if (status === 4) return 'success'
      if (status === 5) return 'exception'
      return null
    }
  }
}
</script>

<style lang="scss" scoped>
.orders-page {
  .search-form {
    margin-bottom: 20px;
  }
  
  .chat-container {
    height: 450px;
    display: flex;
    flex-direction: column;
    background: #ebebeb;
    border-radius: 8px;
    overflow: hidden;

    .chat-messages {
      flex: 1;
      overflow-y: auto;
      padding: 16px;
      
      &::-webkit-scrollbar {
        width: 6px;
      }
      
      &::-webkit-scrollbar-thumb {
        background-color: #c1c1c1;
        border-radius: 3px;
      }

      .message-item {
        margin-bottom: 16px;
        display: flex;
        flex-direction: column;

        &.message-self {
          align-items: flex-end;

          .message-bubble {
            background: linear-gradient(135deg, #409EFF, #66b1ff);
            color: white;
            border-radius: 12px 4px 12px 12px;
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
          }
          
          .message-time {
            text-align: right;
            padding-right: 8px;
          }
        }

        &:not(.message-self) {
          align-items: flex-start;

          .message-bubble {
            background: white;
            color: #303133;
            border-radius: 4px 12px 12px 12px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
          }
          
          .message-time {
            text-align: left;
            padding-left: 8px;
          }
        }

        .message-main {
          max-width: 75%;
        }

        .message-bubble {
          padding: 10px 14px;
          font-size: 14px;
          line-height: 1.6;
          word-break: break-word;
          transition: transform 0.15s ease;
          
          &:hover {
            transform: scale(1.01);
          }
        }

        .message-time {
          font-size: 11px;
          color: #b0b0b0;
          margin-top: 6px;
          letter-spacing: 0.5px;
        }
      }
    }

    .chat-input-area {
      padding: 12px 16px;
      background: white;
      border-top: 1px solid #e8e8e8;

      :deep(.el-textarea__inner) {
        border: none;
        box-shadow: none;
        resize: none;
        font-size: 14px;
        
        &::placeholder {
          color: #c0c4cc;
        }
        
        &:focus {
          box-shadow: none;
        }
      }

      :deep(.el-input__count) {
        bottom: 2px;
        right: 20px;
        font-size: 11px;
      }

      .chat-send-btn {
        display: flex;
        justify-content: flex-end;
        margin-top: 10px;

        .el-button {
          min-width: 80px;
          border-radius: 18px;
          font-weight: 500;
          letter-spacing: 2px;
        }
      }
    }
  }
}
</style>-->



<template>
  <div class="orders-page">
    <el-card>
      <div slot="header">
        <span>我的订单</span>
      </div>

      <!-- 订单类型切换 -->
      <div class="order-tabs">
        <el-radio-group v-model="orderType" @change="handleOrderTypeChange">
          <el-radio-button label="all">全部订单</el-radio-button>
          <el-radio-button label="consult">咨询订单</el-radio-button>
          <el-radio-button label="document">文书订单</el-radio-button>
        </el-radio-group>
      </div>

      <div class="search-form">
        <el-form inline>
          <el-form-item label="订单状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待接单" :value="1"></el-option>
              <el-option label="已接单/进行中" :value="2"></el-option>
              <el-option label="已完成" :value="3"></el-option>
              <el-option label="已取消" :value="4"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="订单编号">
            <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="180"></el-table-column>
        <el-table-column prop="orderTypeName" label="订单类型" width="120">
          <template slot-scope="scope">
            <el-tag size="small" :type="getOrderTypeTagType(scope.row)">
              {{ scope.row.orderTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180"></el-table-column>
        <el-table-column prop="clientName" label="客户" width="100">
          <template slot-scope="scope">
            {{ scope.row.clientName || scope.row.userName || '匿名用户' }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="金额" width="100">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="displayStatus" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status, scope.row.orderType)">
              {{ getStatusLabel(scope.row.status, scope.row.orderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template slot-scope="scope">
            <el-progress :percentage="getProgress(scope.row.status, scope.row.orderType)"
                         :status="getProgressStatus(scope.row.status)"></el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <!-- 待接单状态 - 咨询订单/文书订单 -->
            <el-button type="primary" size="small" v-if="scope.row.status === 1" @click="handleAccept(scope.row)">
              接单
            </el-button>

            <!-- 进行中状态 - 咨询订单 -->
            <el-button type="success" size="small"
                       v-if="scope.row.status === 2 && scope.row.orderType === 'consult'"
                       @click="handleProcess(scope.row)">
              处理
            </el-button>

            <!-- 进行中状态 - 文书审核：查看用户文书 + 上传修改后文书 -->
            <template v-if="scope.row.status === 2 && scope.row.orderType === 'document' && scope.row.orderTypeName === '文书审核'">
              <el-button type="primary" size="small" @click="handleViewDocument(scope.row)">
                查看用户文书
              </el-button>
              <el-button type="success" size="small" @click="handleUploadRevised(scope.row)">
                上传修改后文书
              </el-button>
            </template>

            <!-- 进行中状态 - 文书代写：只有上传修改后文书 -->
            <el-button type="success" size="small"
                       v-if="scope.row.status === 2 && scope.row.orderType === 'document' && scope.row.orderTypeName === '文书代写'"
                       @click="handleUploadRevised(scope.row)">
              上传文书
            </el-button>

            <el-button type="text" size="small" @click="handleView(scope.row)">
              查看详情
            </el-button>
            <el-button type="text" size="small" @click="handleCommunicate(scope.row)">
              沟通
            </el-button>
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
    </el-card>

    <!-- 咨询订单详情弹窗 -->
    <el-dialog title="订单详情" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ currentOrder.orderTypeName }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ getStatusLabel(currentOrder.status, currentOrder.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentOrder.clientName || currentOrder.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="内容/需求" :span="2">{{ currentOrder.content || currentOrder.requirement }}</el-descriptions-item>
        <el-descriptions-item label="附件" :span="2" v-if="currentOrder.attachmentUrl">
          <el-link :href="currentOrder.attachmentUrl" target="_blank" type="primary">
            <i class="el-icon-download"></i> 下载用户附件
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="currentOrder.result || currentOrder.resultContent">
          {{ currentOrder.result || currentOrder.resultContent }}
        </el-descriptions-item>
        <el-descriptions-item label="结果文件" :span="2" v-if="currentOrder.resultFileUrl">
          <el-link :href="currentOrder.resultFileUrl" target="_blank" type="success">
            <i class="el-icon-download"></i> 下载结果文件
          </el-link>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 咨询订单处理弹窗 -->
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

    <!-- 查看用户文书弹窗（文书审核专用） -->
    <el-dialog title="查看用户文书" :visible.sync="viewDocumentDialogVisible" width="650px">
      <el-descriptions :column="1" border v-if="currentDocumentOrder">
        <el-descriptions-item label="订单编号">{{ currentDocumentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">
          {{ currentDocumentOrder.orderTypeName }}
        </el-descriptions-item>
        <el-descriptions-item label="需求标题">{{ currentDocumentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="需求描述">
          <div class="requirement-content">{{ currentDocumentOrder.requirement }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="用户附件" v-if="currentDocumentOrder.attachmentUrl">
          <el-link :href="currentDocumentOrder.attachmentUrl" target="_blank" type="primary">
            <i class="el-icon-download"></i> 下载用户上传的文书
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="备注" v-else>
          用户未上传附件
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="viewDocumentDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="handleUploadRevised(currentDocumentOrder)">
          上传修改后文书
        </el-button>
      </div>
    </el-dialog>

    <!-- 上传文书弹窗（文书代写/文书审核共用） -->
    <el-dialog title="上传文书" :visible.sync="uploadDialogVisible" width="550px" @close="resetUploadForm">
      <el-form ref="uploadForm" :model="uploadForm" label-width="100px">
        <el-form-item label="订单信息">
          <div class="order-info">
            <p><strong>订单编号：</strong>{{ currentDocumentOrder?.orderNo }}</p>
            <p><strong>服务类型：</strong>{{ currentDocumentOrder?.orderTypeName }}</p>
            <p><strong>需求标题：</strong>{{ currentDocumentOrder?.title }}</p>
            <p><strong>需求描述：</strong>{{ currentDocumentOrder?.requirement }}</p>
            <p v-if="currentDocumentOrder?.attachmentUrl">
              <strong>用户原文件：</strong>
              <el-link :href="currentDocumentOrder.attachmentUrl" target="_blank" type="primary">
                点击下载用户上传的文书
              </el-link>
            </p>
          </div>
        </el-form-item>
        <el-form-item label="上传文书" required>
          <el-upload
              ref="upload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :on-progress="handleUploadProgress"
              :show-file-list="true"
              :file-list="fileList"
              :limit="1"
              accept=".doc,.docx,.pdf,.txt"
          >
            <el-button size="small" type="primary">
              <i class="el-icon-upload"></i> 选择文件
            </el-button>
            <div slot="tip" class="el-upload__tip">
              支持上传 .doc、.docx、.pdf、.txt 格式文件，大小不超过10MB
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input
              type="textarea"
              v-model="uploadForm.remark"
              :rows="3"
              placeholder="请输入备注说明（可选）"
              maxlength="500"
              show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="upload-progress" v-if="uploadProgress > 0 && uploadProgress < 100">
        <el-progress :percentage="uploadProgress" :stroke-width="8"></el-progress>
      </div>
      <div slot="footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploadSubmitting" :disabled="!canSubmit">
          提交并完成
        </el-button>
      </div>
    </el-dialog>

    <!-- 沟通弹窗 -->
    <el-dialog title="订单沟通" :visible.sync="chatDialogVisible" width="600px" @close="closeChat" class="chat-dialog">
      <div class="chat-container" v-if="chatTargetId">
        <div class="chat-messages" ref="chatMessageContainer">
          <div v-for="(msg, index) in chatMessages" :key="msg.id || index"
               :class="['message-item', { 'message-self': msg.fromUserId === currentUserId }]">
            <div class="message-main">
              <div class="message-bubble">{{ msg.content }}</div>
            </div>
            <div class="message-time">{{ formatChatTime(msg.createTime) }}</div>
          </div>
          <el-empty v-if="chatMessages.length === 0" description="暂无聊天记录" :image-size="80"></el-empty>
        </div>
        <div class="chat-input-area">
          <el-input type="textarea"
                    v-model="chatInput"
                    placeholder="输入消息..."
                    :rows="2"
                    resize="none"
                    maxlength="500"
                    show-word-limit
                    @keyup.enter.native.exact="sendChatMessage">
          </el-input>
          <div class="chat-send-btn">
            <el-button type="primary" :disabled="!chatInput.trim()" @click="sendChatMessage">发送</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getConsultList, acceptConsult, processConsult, finishConsult } from '@/api/consult'
import { getDocumentOrderList, acceptDocumentOrder, completeDocumentOrder } from '@/api/documentOrder'
import { getChatHistory, sendMessage } from '@/api/chat'

export default {
  name: 'LawyerOrders',
  data() {
    return {
      loading: false,
      orderType: 'all',
      searchForm: {
        status: null,
        orderNo: ''
      },
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      currentOrder: null,
      processDialogVisible: false,
      processForm: {
        id: null,
        result: ''
      },
      viewDocumentDialogVisible: false,
      uploadDialogVisible: false,
      currentDocumentOrder: null,
      uploadForm: {
        id: null,
        remark: ''
      },
      fileList: [],
      uploadProgress: 0,
      uploadSubmitting: false,
      uploadUrl: '/api/upload',
      uploadHeaders: {},
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
    },
    canSubmit() {
      return this.fileList.length > 0 && this.fileList[0].url
    }
  },
  created() {
    const token = this.$store.getters.token
    if (token) {
      this.uploadHeaders = { Authorization: `Bearer ${token}` }
    }
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        let consultOrders = []
        let documentOrders = []
        let totalConsult = 0
        let totalDocument = 0

        if (this.orderType === 'all' || this.orderType === 'consult') {
          const consultParams = {
            page: this.orderType === 'all' ? 1 : this.currentPage,
            size: this.orderType === 'all' ? 100 : this.pageSize,
            status: this.searchForm.status
          }
          if (this.searchForm.orderNo) {
            consultParams.orderNo = this.searchForm.orderNo
          }
          const consultRes = await getConsultList(consultParams)
          consultOrders = (consultRes.data?.records || []).map(order => ({
            ...order,
            orderType: 'consult',
            orderTypeName: '图文咨询',
            clientName: order.userName || '匿名用户'
          }))
          totalConsult = consultRes.data?.total || 0
        }

        if (this.orderType === 'all' || this.orderType === 'document') {
          const documentParams = {
            page: this.orderType === 'all' ? 1 : this.currentPage,
            size: this.orderType === 'all' ? 100 : this.pageSize,
            status: this.searchForm.status
          }
          const documentRes = await getDocumentOrderList(documentParams)
          documentOrders = (documentRes.data?.records || []).map(order => ({
            ...order,
            orderType: 'document',
            orderTypeName: order.orderType === 1 ? '文书代写' : '文书审核',
            clientName: order.userName || '匿名用户',
            status: order.status,
            requirement: order.requirement,
            attachmentUrl: order.attachmentUrl,
            resultFileUrl: order.resultFileUrl,
            resultContent: order.resultContent
          }))
          totalDocument = documentRes.data?.total || 0
        }

        if (this.orderType === 'all') {
          let allOrders = [...consultOrders, ...documentOrders]
          allOrders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
          this.total = allOrders.length
          const start = (this.currentPage - 1) * this.pageSize
          const end = start + this.pageSize
          this.tableData = allOrders.slice(start, end)
        } else if (this.orderType === 'consult') {
          this.tableData = consultOrders
          this.total = totalConsult
        } else {
          this.tableData = documentOrders
          this.total = totalDocument
        }
      } catch (error) {
        console.error('加载订单失败:', error)
        this.$message.error('加载订单失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },

    handleReset() {
      this.searchForm = { status: null, orderNo: '' }
      this.handleSearch()
    },

    handleOrderTypeChange() {
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
        await this.$confirm('确定要接单吗？', '提示', { type: 'warning' })
        if (row.orderType === 'document') {
          await acceptDocumentOrder(row.id)
        } else {
          await acceptConsult(row.id)
        }
        this.$message.success('接单成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          this.$message.error('接单失败')
        }
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
        this.$message.error('处理失败')
      }
    },

    handleViewDocument(row) {
      this.currentDocumentOrder = row
      this.viewDocumentDialogVisible = true
    },

    handleUploadRevised(row) {
      this.currentDocumentOrder = row
      this.uploadForm = { id: row.id, remark: '' }
      this.fileList = []
      this.uploadProgress = 0
      this.viewDocumentDialogVisible = false
      this.uploadDialogVisible = true
    },

    beforeUpload(file) {
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error('文件大小不能超过10MB')
        return false
      }
      const isValidType = ['.doc', '.docx', '.pdf', '.txt'].some(ext =>
          file.name.toLowerCase().endsWith(ext)
      )
      if (!isValidType) {
        this.$message.error('只支持 .doc、.docx、.pdf、.txt 格式文件')
        return false
      }
      this.uploadProgress = 0
      return true
    },

    handleUploadProgress(event) {
      this.uploadProgress = Math.floor(event.percent)
    },

    handleUploadSuccess(response) {
      if (response.code === 200 || response.code === 0) {
        this.fileList = [{ name: response.data.originalName, url: response.data.url }]
        this.uploadProgress = 100
        this.$message.success('文件上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
        this.uploadProgress = 0
      }
    },

    handleUploadError(error) {
      console.error('上传失败:', error)
      this.$message.error('文件上传失败')
      this.uploadProgress = 0
    },

    resetUploadForm() {
      this.uploadForm = { id: null, remark: '' }
      this.fileList = []
      this.uploadProgress = 0
      this.uploadSubmitting = false
      this.currentDocumentOrder = null
    },

    async submitUpload() {
      if (this.fileList.length === 0) {
        this.$message.warning('请上传文书文件')
        return
      }

      const fileUrl = this.fileList[0].url
      if (!fileUrl) {
        this.$message.warning('请等待文件上传完成')
        return
      }

      this.uploadSubmitting = true
      try {
        await completeDocumentOrder(this.uploadForm.id, {
          resultFileUrl: fileUrl,
          resultContent: this.uploadForm.remark
        })
        this.$message.success('文书上传成功，订单已完成')
        this.uploadDialogVisible = false
        this.loadData()
      } catch (error) {
        console.error('提交失败:', error)
        this.$message.error('提交失败')
      } finally {
        this.uploadSubmitting = false
      }
    },

    handleView(row) {
      this.currentOrder = row
      this.dialogVisible = true
    },

    async handleCommunicate(row) {
      const targetUserId = row.userId
      if (!targetUserId) {
        this.$message.warning('该订单没有用户信息')
        return
      }
      if (!this.currentUserId) {
        this.$message.warning('请先登录')
        return
      }
      this.chatTargetId = targetUserId
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
      const content = this.chatInput.trim()
      this.chatInput = ''

      const tempMsg = {
        id: Date.now(),
        fromUserId: this.currentUserId,
        toUserId: this.chatTargetId,
        messageType: 1,
        content: content,
        createTime: new Date().toISOString()
      }
      this.chatMessages.push(tempMsg)
      this.$nextTick(() => {
        this.scrollChatToBottom()
      })

      try {
        await sendMessage({
          fromUserId: this.currentUserId,
          toUserId: this.chatTargetId,
          messageType: 1,
          content: content
        })
        await this.loadChatMessages()
      } catch (error) {
        console.error('发送消息失败:', error)
        const index = this.chatMessages.findIndex(m => m.id === tempMsg.id)
        if (index > -1) {
          this.chatMessages.splice(index, 1)
        }
        this.$message.error('发送失败')
      }
    },

    closeChat() {
      this.chatTargetId = null
      this.chatMessages = []
      this.chatInput = ''
    },

    scrollChatToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.chatMessageContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    formatChatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString()
    },

    getOrderTypeTagType(row) {
      if (row.orderType === 'consult') {
        return 'primary'
      }
      if (row.orderTypeName === '文书代写') {
        return 'success'
      }
      if (row.orderTypeName === '文书审核') {
        return 'warning'
      }
      return 'info'
    },

    getStatusType(status, orderType) {
      if (orderType === 'document') {
        const types = { 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' }
        return types[status] || 'info'
      }
      const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success', 5: 'danger' }
      return types[status] || 'info'
    },

    getStatusLabel(status, orderType) {
      if (orderType === 'document') {
        const labels = { 1: '待接单', 2: '进行中', 3: '已完成', 4: '已取消' }
        return labels[status] || '未知'
      }
      const labels = { 1: '待接单', 2: '已接单', 3: '处理中', 4: '已完成', 5: '已取消' }
      return labels[status] || '未知'
    },

    getProgress(status, orderType) {
      if (orderType === 'document') {
        const progress = { 1: 25, 2: 60, 3: 100, 4: 0 }
        return progress[status] || 0
      }
      const progress = { 1: 25, 2: 50, 3: 75, 4: 100, 5: 0 }
      return progress[status] || 0
    },

    getProgressStatus(status) {
      if (status === 4 || status === 3) return 'success'
      if (status === 5 || status === 4) return 'exception'
      return null
    }
  }
}
</script>

<style lang="scss" scoped>
.orders-page {
  .order-tabs {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
  }

  .search-form {
    margin-bottom: 20px;
  }

  .order-info {
    background: #f5f7fa;
    padding: 12px;
    border-radius: 4px;
    margin-bottom: 10px;

    p {
      margin: 6px 0;
      font-size: 13px;
      line-height: 1.5;

      strong {
        color: #333;
        width: 100px;
        display: inline-block;
      }
    }
  }

  .requirement-content {
    white-space: pre-wrap;
    word-break: break-word;
    line-height: 1.6;
    max-height: 200px;
    overflow-y: auto;
  }

  .upload-progress {
    margin: 10px 20px;
  }

  .chat-container {
    height: 450px;
    display: flex;
    flex-direction: column;
    background: #f5f7fa;
    border-radius: 8px;
    overflow: hidden;

    .chat-messages {
      flex: 1;
      overflow-y: auto;
      padding: 16px;

      .message-item {
        margin-bottom: 16px;
        display: flex;
        flex-direction: column;

        &.message-self {
          align-items: flex-end;

          .message-bubble {
            background: linear-gradient(135deg, #409EFF, #66b1ff);
            color: white;
            border-radius: 12px 4px 12px 12px;
          }

          .message-time {
            text-align: right;
            padding-right: 8px;
          }
        }

        &:not(.message-self) {
          align-items: flex-start;

          .message-bubble {
            background: white;
            color: #303133;
            border-radius: 4px 12px 12px 12px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
          }

          .message-time {
            text-align: left;
            padding-left: 8px;
          }
        }

        .message-main {
          max-width: 75%;
        }

        .message-bubble {
          padding: 10px 14px;
          font-size: 14px;
          line-height: 1.6;
          word-break: break-word;
        }

        .message-time {
          font-size: 11px;
          color: #b0b0b0;
          margin-top: 6px;
        }
      }
    }

    .chat-input-area {
      padding: 12px 16px;
      background: white;
      border-top: 1px solid #e8e8e8;
    }
  }
}

.mt-20 {
  margin-top: 20px;
}
</style>