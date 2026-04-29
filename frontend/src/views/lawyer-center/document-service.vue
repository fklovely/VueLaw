<!--<template>
  <div class="document-service-page">
    <el-card>
      <div slot="header">
        <span>文书服务接单</span>
      </div>
      <div class="search-form">
        <el-form inline>
          <el-form-item label="服务类型">
            <el-select v-model="searchForm.serviceType" placeholder="全部" clearable>
              <el-option label="文书代写" value="write"></el-option>
              <el-option label="文书审核" value="audit"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待接单" :value="1"></el-option>
              <el-option label="进行中" :value="2"></el-option>
              <el-option label="已完成" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
        <el-table-column prop="serviceType" label="服务类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.serviceType === 'write' ? 'primary' : 'success'">
              {{ scope.row.serviceType === 'write' ? '文书代写' : '文书审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="documentType" label="文书类型" width="120"></el-table-column>
        <el-table-column prop="title" label="需求标题" min-width="180"></el-table-column>
        <el-table-column prop="price" label="金额" width="100">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" v-if="scope.row.status === 1" @click="handleAccept(scope.row)">接单</el-button>
            <el-button type="success" size="small" v-if="scope.row.status === 2" @click="handleUpload(scope.row)">上传文书</el-button>
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
    </el-card>
    
    <el-dialog title="订单详情" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ currentOrder.serviceType === 'write' ? '文书代写' : '文书审核' }}</el-descriptions-item>
        <el-descriptions-item label="文书类型">{{ currentOrder.documentType }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="需求标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="需求描述" :span="2">{{ currentOrder.description }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <el-dialog title="上传文书" :visible.sync="uploadDialogVisible" width="500px">
      <el-form ref="uploadForm" :model="uploadForm" label-width="80px">
        <el-form-item label="文书文件">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :show-file-list="true"
            :file-list="fileList"
          >
            <el-button size="small" type="primary">上传文件</el-button>
            <div slot="tip" class="el-upload__tip">支持上传Word、PDF等格式文件</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input type="textarea" v-model="uploadForm.remark" :rows="3" placeholder="请输入备注说明"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DocumentService',
  data() {
    return {
      loading: false,
      searchForm: {
        serviceType: '',
        status: null
      },
      tableData: [
        { id: 1, orderNo: 'DOC202601150001', serviceType: 'write', documentType: '起诉状', title: '未成年人监护权起诉状代写', price: 500, status: 1, description: '需要代写一份监护权变更起诉状', phone: '13800138000', createTime: '2026-01-15 10:00:00' },
        { id: 2, orderNo: 'DOC202601150002', serviceType: 'audit', documentType: '合同', title: '教育培训合同审核', price: 300, status: 2, description: '审核一份教育培训服务合同', phone: '13900139000', createTime: '2026-01-14 15:30:00' }
      ],
      total: 2,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      currentOrder: null,
      isViewingDocument: false,
      viewDocumentDialogVisible: false,
      currentDocumentOrder: null,
      uploadDialogVisible: false,
      uploadForm: {
        id: null,
        remark: ''
      },
      fileList: [],
      uploadUrl: '/api/upload',
      uploadHeaders: {}
    }
  },
  created() {
    const token = this.$store.getters.token
    if (token) {
      this.uploadHeaders = { Authorization: token }
    }
  },
  methods: {
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
    loadData() {
      this.loading = true
      setTimeout(() => {
        this.loading = false
      }, 500)
    },
    handleAccept(row) {
      this.$confirm('确定要接单吗？', '提示', { type: 'warning' }).then(() => {
        row.status = 2
        this.$message.success('接单成功')
      }).catch(() => {})
    },
    handleUpload(row) {
      this.uploadForm = { id: row.id, remark: '' }
      this.fileList = []
      this.uploadDialogVisible = true
    },
    handleUploadSuccess(res, file, fileList) {
      this.fileList = fileList
    },
    submitUpload() {
      if (this.fileList.length === 0) {
        this.$message.warning('请上传文书文件')
        return
      }
      this.$message.success('上传成功')
      this.uploadDialogVisible = false
      this.loadData()
    },
    handleView(row) {
      this.currentOrder = row
      this.isViewingDocument = false
      this.viewDocumentDialogVisible = false
      this.uploadDialogVisible = false
      this.chatDialogVisible = false
      this.dialogVisible = true
    },
    getStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'success' }
      return types[status]
    },
    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '进行中', 3: '已完成' }
      return labels[status]
    }
  }
}
</script>

<style lang="scss" scoped>
.document-service-page {
  .search-form {
    margin-bottom: 20px;
  }
}
</style>-->


<template>
  <div class="document-service-page">
    <el-card>
      <div slot="header">
        <span>文书服务接单</span>
      </div>

      <div class="search-form">
        <el-form inline>
          <el-form-item label="服务类型">
            <el-select v-model="searchForm.orderType" placeholder="全部" clearable>
              <el-option label="文书代写" :value="1"></el-option>
              <el-option label="文书审核" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待接单" :value="1"></el-option>
              <el-option label="进行中" :value="2"></el-option>
              <el-option label="已完成" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="160"></el-table-column>
        <el-table-column prop="orderTypeName" label="服务类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.orderTypeName === '文书代写' ? 'primary' : 'success'">
              {{ scope.row.orderTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="需求标题" min-width="180"></el-table-column>
        <el-table-column prop="requirement" label="需求描述" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="price" label="金额" width="100">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small"
                       v-if="scope.row.status === 1"
                       @click="handleAccept(scope.row)"
                       :loading="scope.row.acceptLoading">
              接单
            </el-button>

            <!-- 进行中状态 - 文书审核：查看用户文书 + 上传修改后文书 -->
            <template v-if="scope.row.status == 2 && scope.row.orderTypeName === '文书审核'">
              <el-button type="primary" size="small" @click="handleViewDocument(scope.row)">
                查看用户文书
              </el-button>
              <el-button type="success" size="small" @click="handleUploadRevised(scope.row)">
                上传修改后文书
              </el-button>
            </template>

            <!-- 进行中状态 - 文书代写：只有上传修改后文书 -->
            <el-button type="success" size="small"
                       v-if="scope.row.status == 2 && scope.row.orderTypeName === '文书代写'"
                       @click="handleUploadRevised(scope.row)">
              上传文书
            </el-button>

            <el-button type="text" size="small"
                       @click="handleView(scope.row)">
              查看详情
            </el-button>
            <el-button type="text" size="small"
                       v-if="scope.row.userId"
                       @click="handleCommunicate(scope.row)">
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

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" :visible.sync="dialogVisible" width="650px" key="detail-dialog" @close="dialogVisible = false; isViewingDocument = false">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">
          {{ currentOrder.orderTypeName || (currentOrder.orderType == 1 ? '文书代写' : '文书审核') }}
        </el-descriptions-item>
        <el-descriptions-item label="文书类型">{{ currentOrder.documentType || '通用' }}</el-descriptions-item>
        <el-descriptions-item label="订单金额" :span="2">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="需求标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="需求描述" :span="2">
          <div class="requirement-content">{{ currentOrder.requirement }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="附件" :span="2" v-if="currentOrder.attachmentUrl">
          <el-link :href="currentOrder.attachmentUrl" target="_blank" type="primary">
            <i class="el-icon-download"></i> 下载附件
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.phone || '未提供' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" v-if="currentOrder.finishTime">{{ currentOrder.finishTime }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="currentOrder.resultContent">
          {{ currentOrder.resultContent }}
        </el-descriptions-item>
        <el-descriptions-item label="结果文件" :span="2" v-if="currentOrder.resultFileUrl">
          <el-link :href="currentOrder.resultFileUrl" target="_blank" type="success">
            <i class="el-icon-document"></i> 下载结果文件
          </el-link>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 查看用户文书弹窗（文书审核专用） -->
    <el-dialog title="查看用户文书" :visible.sync="viewDocumentDialogVisible" width="650px" key="document-dialog" @close="viewDocumentDialogVisible = false">
      <el-descriptions :column="1" border v-if="currentDocumentOrder">
        <el-descriptions-item label="订单编号">{{ currentDocumentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">
          {{ currentDocumentOrder.orderTypeName || (currentDocumentOrder.orderType == 1 ? '文书代写' : '文书审核') }}
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

    <!-- 上传文书弹窗 -->
    <el-dialog title="上传文书" :visible.sync="uploadDialogVisible" width="550px" key="upload-dialog" @close="resetUploadForm">
      <el-form ref="uploadForm" :model="uploadForm" label-width="100px">
        <el-form-item label="订单信息">
          <div class="order-info">
            <p><strong>订单编号：</strong>{{ currentDocumentOrder?.orderNo }}</p>
            <p><strong>服务类型：</strong>{{ currentDocumentOrder?.orderTypeName || (currentDocumentOrder?.orderType == 1 ? '文书代写' : '文书审核') }}</p>
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
        <el-form-item label="文书文件" required>
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
    <el-dialog title="订单沟通" :visible.sync="chatDialogVisible" width="600px" key="chat-dialog" @close="closeChat" class="chat-dialog">
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
import { getDocumentOrderList, acceptDocumentOrder, completeDocumentOrder } from '@/api/documentOrder'
import { getChatHistory, sendMessage } from '@/api/chat'

export default {
  name: 'DocumentService',
  data() {
    return {
      loading: false,
      searchForm: {
        orderType: null,
        status: null
      },
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      currentOrder: null,
      isViewingDocument: false,
      viewDocumentDialogVisible: false,
      currentDocumentOrder: null,
      uploadDialogVisible: false,
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
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          orderType: this.searchForm.orderType,
          status: this.searchForm.status
        }
        const res = await getDocumentOrderList(params)
        this.tableData = (res.data?.records || []).map(item => ({
          ...item,
          orderTypeName: item.orderType == 1 ? '文书代写' : '文书审核',
          acceptLoading: false
        }))
        this.total = res.data?.total || 0
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
      this.searchForm = { orderType: null, status: null }
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

    /*async handleAccept(row) {
      try {
        await this.$confirm('确定要接单吗？接单后需要按时完成服务。', '提示', { type: 'warning' })
        row.acceptLoading = true
        await acceptDocumentOrder(row.id)
        this.$message.success('接单成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          this.$message.error('接单失败')
        }
      } finally {
        row.acceptLoading = false
      }
    },*/
    async handleAccept(row) {
      try {
        await this.$confirm('确定要接单吗？接单后需要按时完成服务。', '提示', { type: 'warning' })
        row.acceptLoading = true

        const res = await acceptDocumentOrder(row.id)

        if (res.code === 200 || res.code === 0) {
          this.$message.success('接单成功')
          this.loadData()
        } else {
          this.$message.error(res.message || '接单失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('接单失败:', error)
          const errorMsg = error.response?.data?.message || error.message || '接单失败'
          this.$message.error(errorMsg)
        }
      } finally {
        row.acceptLoading = false
      }
    },

    handleViewDocument(row) {
      this.currentDocumentOrder = row
      this.currentOrder = null
      this.isViewingDocument = true
      this.dialogVisible = false
      this.uploadDialogVisible = false
      this.chatDialogVisible = false
      this.viewDocumentDialogVisible = true
    },

    handleUploadRevised(row) {
      if (!row) return
      this.currentDocumentOrder = row
      this.uploadForm = { id: row.id, remark: '' }
      this.fileList = []
      this.uploadProgress = 0
      this.dialogVisible = false
      this.chatDialogVisible = false
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

    handleUploadProgress(event, file, fileList) {
      this.uploadProgress = Math.floor(event.percent)
    },

    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.fileList = [{ name: file.name, url: response.data.url, raw: file }]
        this.uploadProgress = 100
        this.$message.success('文件上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
        this.uploadProgress = 0
      }
    },

    handleUploadError(error) {
      console.error('上传失败:', error)
      this.$message.error('文件上传失败，请重试')
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
        this.$message.warning('文件上传未完成，请等待上传完成')
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
        const errorMsg = error.response?.data?.message || error.message || '提交失败'
        this.$message.error(errorMsg)
      } finally {
        this.uploadSubmitting = false
      }
    },

    handleView(row) {
      this.currentOrder = row
      this.currentDocumentOrder = null
      this.isViewingDocument = false
      this.viewDocumentDialogVisible = false
      this.uploadDialogVisible = false
      this.chatDialogVisible = false
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

    getStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'success' }
      return types[status] || 'info'
    },

    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '进行中', 3: '已完成' }
      return labels[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.document-service-page {
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
        width: 80px;
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

      ::v-deep .el-textarea__inner {
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

      ::v-deep .el-input__count {
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

.mt-20 {
  margin-top: 20px;
}
</style>
