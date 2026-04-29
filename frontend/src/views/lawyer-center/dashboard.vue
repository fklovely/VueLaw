<!--<template>
  <div class="dashboard-page">
    <el-alert
      v-if="auditStatus && auditStatus.auditStatus === 1"
      title="您的律师认证申请正在审核中，请耐心等待"
      type="warning"
      :closable="false"
      show-icon
      class="mb-20"
    ></el-alert>
    
    <el-alert
      v-if="auditStatus && auditStatus.auditStatus === 3"
      :title="'您的律师认证申请被驳回：' + (auditStatus.auditRemark || '无原因')"
      type="error"
      :closable="false"
      show-icon
      class="mb-20"
    >
      <el-button type="text" @click="showResubmitDialog">重新提交审核</el-button>
    </el-alert>

    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea, #764ba2)">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.pendingOrders }}</p>
            <p class="stat-label">待处理订单</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c)">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.completedOrders }}</p>
            <p class="stat-label">已完成订单</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe)">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.avgScore }}</p>
            <p class="stat-label">平均评分</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7)">
            <i class="el-icon-money"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">¥{{ statistics.totalIncome }}</p>
            <p class="stat-label">累计收益</p>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card>
          <div slot="header" class="card-header">
            <span>待处理订单</span>
            <el-button type="text" @click="$router.push('/lawyer-center/orders')">查看全部</el-button>
          </div>
          <el-table :data="pendingOrders" border stripe v-loading="loading">
            <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
            <el-table-column prop="title" label="咨询标题" min-width="150"></el-table-column>
            <el-table-column prop="price" label="金额" width="100">
              <template slot-scope="scope">¥{{ scope.row.price }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150"></el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template slot-scope="scope">
                <el-button type="primary" size="small" v-if="scope.row.status === 1" @click="handleAccept(scope.row)">接单</el-button>
                <el-button type="success" size="small" v-if="scope.row.status === 2" @click="handleProcess(scope.row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="mb-20">
          <div slot="header">收益概览</div>
          <div class="income-overview">
            <div class="income-item">
              <span class="label">本月收益</span>
              <span class="value">¥{{ incomeData.monthIncome }}</span>
            </div>
            <div class="income-item">
              <span class="label">待结算</span>
              <span class="value">¥{{ incomeData.pendingIncome }}</span>
            </div>
            <div class="income-item">
              <span class="label">已结算</span>
              <span class="value">¥{{ incomeData.settledIncome }}</span>
            </div>
          </div>
        </el-card>
        
        <el-card>
          <div slot="header" class="card-header">
            <span>最新评价</span>
            <el-button type="text" @click="$router.push('/lawyer-center/evaluate')">查看全部</el-button>
          </div>
          <div class="evaluate-list" v-loading="evaluateLoading">
            <div class="evaluate-item" v-for="item in recentEvaluates" :key="item.id">
              <div class="evaluate-header">
                <span class="user">{{ item.userName || '匿名用户' }}</span>
                <el-rate :value="item.totalScore || item.score" disabled></el-rate>
              </div>
              <p class="evaluate-content">{{ item.content }}</p>
              <span class="evaluate-time">{{ item.createTime }}</span>
            </div>
            <el-empty v-if="recentEvaluates.length === 0" description="暂无评价"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
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

    <el-dialog title="重新提交律师认证" :visible.sync="resubmitDialogVisible" width="500px">
      <el-form ref="resubmitForm" :model="resubmitForm" :rules="resubmitRules" label-width="100px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="resubmitForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="resubmitForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="resubmitForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="拒绝原因">
          <el-input type="textarea" :value="auditStatus.auditRemark" disabled :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="resubmitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResubmit" :loading="resubmitLoading">重新提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getLawyerStatistics, getAuditStatus, resubmitAudit } from '@/api/lawyer'
import { getConsultList, acceptConsult, processConsult, finishConsult } from '@/api/consult'
import { getIncomeStatistics } from '@/api/income'
import { getEvaluationList } from '@/api/evaluation'



export default {
  name: 'LawyerDashboard',
  data() {
    return {
      loading: false,
      evaluateLoading: false,
      auditStatus: null,
      statistics: {
        pendingOrders: 0,
        completedOrders: 0,
        avgScore: 5.0,
        totalIncome: 0
      },
      pendingOrders: [],
      incomeData: {
        monthIncome: 0,
        pendingIncome: 0,
        settledIncome: 0
      },
      recentEvaluates: [],
      processDialogVisible: false,
      processForm: {
        id: null,
        result: ''
      },
      resubmitDialogVisible: false,
      resubmitLoading: false,
      resubmitForm: {
        realName: '',
        phone: '',
        email: ''
      },
      resubmitRules: {
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadAuditStatus()
    this.loadStatistics()
    this.loadPendingOrders()
    this.loadIncomeData()
    this.loadEvaluates()
  },
  methods: {
    async loadAuditStatus() {
      try {
        const res = await getAuditStatus()
        if (res.data) {
          this.auditStatus = res.data
        }
      } catch (error) {
        console.error(error)
      }
    },
    async loadStatistics() {
      try {
        const res = await getLawyerStatistics()
        if (res.data) {
          this.statistics = {
            pendingOrders: res.data.pendingOrders || 0,
            completedOrders: res.data.completedOrders || 0,
            avgScore: res.data.avgScore || 5.0,
            totalIncome: res.data.totalIncome || 0
          }
        }
      } catch (error) {
        console.error(error)
      }
    },
    async loadPendingOrders() {
      this.loading = true
      try {
        const [res1, res2] = await Promise.all([
          getConsultList({ page: 1, size: 5, status: 1 }),
          getConsultList({ page: 1, size: 5, status: 2 })
        ])
        const orders1 = res1.data?.records || []
        const orders2 = res2.data?.records || []
        this.pendingOrders = [...orders1, ...orders2].slice(0, 5)
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async loadIncomeData() {
      try {
        const res = await getIncomeStatistics()
        if (res.data) {
          this.incomeData = {
            monthIncome: res.data.monthIncome || 0,
            pendingIncome: res.data.pendingIncome || 0,
            settledIncome: res.data.settledIncome || 0
          }
        }
      } catch (error) {
        console.error(error)
      }
    },

    async loadEvaluates() {
      this.evaluateLoading = true
      try {
        const res = await getEvaluationList({ page: 1, size: 3 })
        this.recentEvaluates = res.data?.records || []
      } catch (error) {
        console.error(error)
      } finally {
        this.evaluateLoading = false
      }
    },
    async handleAccept(row) {
      try {
        await acceptConsult(row.id)
        this.$message.success('接单成功')
        this.loadPendingOrders()
        this.loadStatistics()
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
        this.loadPendingOrders()
        this.loadStatistics()
      } catch (error) {
        console.error(error)
      }
    },
    showResubmitDialog() {
      this.resubmitForm = {
        realName: this.auditStatus.realName || '',
        phone: this.auditStatus.phone || '',
        email: this.auditStatus.email || ''
      }
      this.resubmitDialogVisible = true
    },
    async handleResubmit() {
      this.$refs.resubmitForm.validate(async valid => {
        if (valid) {
          this.resubmitLoading = true
          try {
            await resubmitAudit(this.resubmitForm)
            this.$message.success('重新提交成功，请等待审核')
            this.resubmitDialogVisible = false
            this.loadAuditStatus()
          } catch (error) {
            console.error(error)
          } finally {
            this.resubmitLoading = false
          }
        }
      })
    },
    getStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success', 5: 'danger' }
      return types[status]
    },
    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '已接单', 3: '处理中', 4: '已完成', 5: '已取消' }
      return labels[status]
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      i {
        font-size: 28px;
        color: #fff;
      }
    }
    
    .stat-info {
      margin-left: 20px;
      
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #333;
      }
      
      .stat-label {
        font-size: 14px;
        color: #999;
        margin-top: 5px;
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .income-overview {
    .income-item {
      display: flex;
      justify-content: space-between;
      padding: 15px 0;
      border-bottom: 1px solid #eee;
      
      &:last-child {
        border-bottom: none;
      }
      
      .label {
        color: #666;
      }
      
      .value {
        font-size: 18px;
        font-weight: 600;
        color: #f56c6c;
      }
    }
  }
  
  .evaluate-list {
    .evaluate-item {
      padding: 15px 0;
      border-bottom: 1px solid #eee;
      
      &:last-child {
        border-bottom: none;
      }
      
      .evaluate-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
        
        .user {
          font-weight: 500;
        }
      }
      
      .evaluate-content {
        color: #666;
        font-size: 14px;
        margin: 0 0 5px;
      }
      
      .evaluate-time {
        font-size: 12px;
        color: #999;
      }
    }
  }
}

.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}
</style>-->



<template>
  <div class="dashboard-page">
    <el-alert
        v-if="auditStatus && auditStatus.auditStatus === 1"
        title="您的律师认证申请正在审核中，请耐心等待"
        type="warning"
        :closable="false"
        show-icon
        class="mb-20"
    ></el-alert>

    <el-alert
        v-if="auditStatus && auditStatus.auditStatus === 3"
        :title="'您的律师认证申请被驳回：' + (auditStatus.auditRemark || '无原因')"
        type="error"
        :closable="false"
        show-icon
        class="mb-20"
    >
      <el-button type="text" @click="showResubmitDialog">重新提交审核</el-button>
    </el-alert>

    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea, #764ba2)">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.pendingOrders || 0 }}</p>
            <p class="stat-label">待处理订单</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c)">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.completedOrders || 0 }}</p>
            <p class="stat-label">已完成订单</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe)">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.avgScore || 5.0 }}</p>
            <p class="stat-label">平均评分</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7)">
            <i class="el-icon-money"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">¥{{ statistics.totalIncome || 0 }}</p>
            <p class="stat-label">累计收益</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card>
          <div slot="header" class="card-header">
            <span>待处理订单</span>
            <el-button type="text" @click="$router.push('/lawyer-center/orders')">查看全部</el-button>
          </div>
          <el-table :data="pendingOrders" border stripe v-loading="loading">
            <el-table-column prop="orderNo" label="订单编号" width="180"></el-table-column>
            <el-table-column prop="orderTypeName" label="订单类型" width="100">
              <template slot-scope="scope">
                <el-tag size="small" :type="scope.row.orderType === 'consult' ? 'primary' : 'success'">
                  {{ scope.row.orderTypeName || (scope.row.orderType === 'consult' ? '咨询订单' : '文书订单') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" min-width="150"></el-table-column>
            <el-table-column prop="price" label="金额" width="100">
              <template slot-scope="scope">¥{{ scope.row.price }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status, scope.row.orderType)">
                  {{ getStatusLabel(scope.row.status, scope.row.orderType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
            <el-table-column label="操作" width="280" fixed="right">
              <template slot-scope="scope">
                <el-button type="primary" size="small"
                           v-if="scope.row.status === 1"
                           @click="handleAccept(scope.row)">
                  接单
                </el-button>
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
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="mb-20">
          <div slot="header">收益概览</div>
          <div class="income-overview">
            <div class="income-item">
              <span class="label">本月收益</span>
              <span class="value">¥{{ incomeData.monthIncome || 0 }}</span>
            </div>
            <div class="income-item">
              <span class="label">待结算</span>
              <span class="value">¥{{ incomeData.pendingIncome || 0 }}</span>
            </div>
            <div class="income-item">
              <span class="label">已结算</span>
              <span class="value">¥{{ incomeData.settledIncome || 0 }}</span>
            </div>
          </div>
        </el-card>

        <el-card>
          <div slot="header" class="card-header">
            <span>最新评价</span>
            <el-button type="text" @click="$router.push('/lawyer-center/evaluate')">查看全部</el-button>
          </div>
          <div class="evaluate-list" v-loading="evaluateLoading">
            <div class="evaluate-item" v-for="item in recentEvaluates" :key="item.id">
              <div class="evaluate-header">
                <span class="user">{{ item.userName || '匿名用户' }}</span>
                <el-rate :value="item.totalScore || item.score" disabled></el-rate>
              </div>
              <p class="evaluate-content">{{ item.content }}</p>
              <span class="evaluate-time">{{ item.createTime }}</span>
            </div>
            <el-empty v-if="recentEvaluates.length === 0" description="暂无评价"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 咨询订单处理弹窗 -->
    <el-dialog title="处理订单" :visible.sync="processDialogVisible" width="500px" key="process-dialog" @close="processDialogVisible = false">
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
    <el-dialog title="查看用户文书" :visible.sync="viewDocumentDialogVisible" width="650px" key="doc-view-dialog" @close="viewDocumentDialogVisible = false">
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

    <!-- 文书订单上传弹窗 -->
    <el-dialog title="上传文书" :visible.sync="uploadDialogVisible" width="550px" key="upload-dialog" @close="resetUploadForm">
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
            <el-button size="small" type="primary">选择文件</el-button>
            <div slot="tip" class="el-upload__tip">支持上传 .doc、.docx、.pdf、.txt 格式文件，大小不超过10MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input type="textarea" v-model="uploadForm.remark" :rows="3" placeholder="请输入备注说明（可选）" maxlength="500" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div class="upload-progress" v-if="uploadProgress > 0 && uploadProgress < 100">
        <el-progress :percentage="uploadProgress" :stroke-width="8"></el-progress>
      </div>
      <div slot="footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploadSubmitting" :disabled="!canSubmit">提交并完成</el-button>
      </div>
    </el-dialog>

    <!-- 重新提交律师认证弹窗 -->
    <el-dialog title="重新提交律师认证" :visible.sync="resubmitDialogVisible" width="500px">
      <el-form ref="resubmitForm" :model="resubmitForm" :rules="resubmitRules" label-width="100px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="resubmitForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="resubmitForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="resubmitForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="拒绝原因" v-if="auditStatus && auditStatus.auditRemark">
          <el-input type="textarea" :value="auditStatus.auditRemark" disabled :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="resubmitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResubmit" :loading="resubmitLoading">重新提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getLawyerStatistics, getAuditStatus, resubmitAudit } from '@/api/lawyer'
import { getConsultList, acceptConsult, processConsult, finishConsult } from '@/api/consult'
import { getDocumentOrderList, acceptDocumentOrder, completeDocumentOrder } from '@/api/documentOrder'
import { getIncomeStatistics } from '@/api/income'
import { getEvaluationList } from '@/api/evaluation'

export default {
  name: 'LawyerDashboard',
  data() {
    return {
      loading: false,
      evaluateLoading: false,
      auditStatus: null,
      statistics: {
        pendingOrders: 0,
        completedOrders: 0,
        avgScore: 5.0,
        totalIncome: 0
      },
      pendingOrders: [],
      incomeData: {
        monthIncome: 0,
        pendingIncome: 0,
        settledIncome: 0
      },
      recentEvaluates: [],
      processDialogVisible: false,
      processForm: {
        id: null,
        result: ''
      },
      uploadDialogVisible: false,
      uploadForm: {
        id: null,
        remark: ''
      },
      currentOrder: null,
      viewDocumentDialogVisible: false,
      currentDocumentOrder: null,
      fileList: [],
      uploadProgress: 0,
      uploadSubmitting: false,
      uploadUrl: '/api/upload',
      uploadHeaders: {},
      resubmitDialogVisible: false,
      resubmitLoading: false,
      resubmitForm: {
        realName: '',
        phone: '',
        email: ''
      },
      resubmitRules: {
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      }
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
    this.loadAuditStatus()
    this.loadStatistics()
    this.loadPendingOrders()
    this.loadIncomeData()
    this.loadEvaluates()
  },
  methods: {
    async loadAuditStatus() {
      try {
        const res = await getAuditStatus()
        if (res && res.data) {
          this.auditStatus = res.data
        }
      } catch (error) {
        console.error('加载审核状态失败:', error)
      }
    },

    async loadStatistics() {
      try {
        const res = await getLawyerStatistics()
        if (res && res.data) {
          this.statistics = {
            pendingOrders: res.data.pendingOrders || res.data.pendingCount || 0,
            completedOrders: res.data.completedOrders || res.data.completedCount || 0,
            avgScore: res.data.avgScore || 5.0,
            totalIncome: res.data.totalIncome || 0
          }
        }
        // 同时获取文书订单统计
        const docRes = await getDocumentOrderList({ page: 1, size: 1 })
        if (docRes && docRes.data) {
          // 文书订单统计会通过loadPendingOrders体现
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },

    async loadPendingOrders() {
      this.loading = true
      try {
        // 查询咨询订单
        const [consultRes1, consultRes2] = await Promise.all([
          getConsultList({ page: 1, size: 10, status: 1 }),
          getConsultList({ page: 1, size: 10, status: 2 })
        ])

        const consultOrders1 = (consultRes1.data?.records || []).map(order => ({
          ...order,
          orderType: 'consult',
          orderTypeName: '咨询订单'
        }))
        const consultOrders2 = (consultRes2.data?.records || []).map(order => ({
          ...order,
          orderType: 'consult',
          orderTypeName: '咨询订单'
        }))

        // 查询文书订单
        const [docRes1, docRes2] = await Promise.all([
          getDocumentOrderList({ page: 1, size: 10, status: 1 }),
          getDocumentOrderList({ page: 1, size: 10, status: 2 })
        ])

        const docOrders1 = (docRes1.data?.records || []).map(order => ({
          ...order,
          orderType: 'document',
          orderTypeName: order.orderType == 1 ? '文书代写' : '文书审核'
        }))
        const docOrders2 = (docRes2.data?.records || []).map(order => ({
          ...order,
          orderType: 'document',
          orderTypeName: order.orderType == 1 ? '文书代写' : '文书审核'
        }))

        // 合并所有待处理订单
        let allOrders = [...consultOrders1, ...consultOrders2, ...docOrders1, ...docOrders2]
        allOrders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        this.pendingOrders = allOrders.slice(0, 5)

        // 更新统计数据中的待处理订单数量
        this.statistics.pendingOrders = allOrders.length
      } catch (error) {
        console.error('加载待处理订单失败:', error)
      } finally {
        this.loading = false
      }
    },

    async loadIncomeData() {
      try {
        const res = await getIncomeStatistics()
        if (res && res.data) {
          this.incomeData = {
            monthIncome: res.data.monthIncome || 0,
            pendingIncome: res.data.pendingIncome || 0,
            settledIncome: res.data.settledIncome || 0
          }
        }
      } catch (error) {
        console.error('加载收益数据失败:', error)
        // 设置默认值，避免页面报错
        this.incomeData = {
          monthIncome: 0,
          pendingIncome: 0,
          settledIncome: 0
        }
      }
    },

    async loadEvaluates() {
      this.evaluateLoading = true
      try {
        const res = await getEvaluationList({ page: 1, size: 3 })
        this.recentEvaluates = res.data?.records || []
      } catch (error) {
        console.error('加载评价失败:', error)
      } finally {
        this.evaluateLoading = false
      }
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
        this.loadPendingOrders()
        this.loadStatistics()
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
        this.loadPendingOrders()
        this.loadStatistics()
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

    handleUploadProgress(event, file, fileList) {
      this.uploadProgress = Math.floor(event.percent)
    },

    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.fileList = [{ name: file.name, url: response.data.url }]
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
        this.loadPendingOrders()
        this.loadStatistics()
      } catch (error) {
        console.error('提交失败:', error)
        this.$message.error('提交失败')
      } finally {
        this.uploadSubmitting = false
      }
    },

    showResubmitDialog() {
      this.resubmitForm = {
        realName: this.auditStatus?.realName || '',
        phone: this.auditStatus?.phone || '',
        email: this.auditStatus?.email || ''
      }
      this.resubmitDialogVisible = true
    },

    async handleResubmit() {
      this.$refs.resubmitForm.validate(async valid => {
        if (valid) {
          this.resubmitLoading = true
          try {
            await resubmitAudit(this.resubmitForm)
            this.$message.success('重新提交成功，请等待审核')
            this.resubmitDialogVisible = false
            this.loadAuditStatus()
          } catch (error) {
            console.error(error)
            this.$message.error('提交失败')
          } finally {
            this.resubmitLoading = false
          }
        }
      })
    },

    getStatusType(status, orderType) {
      if (orderType === 'document') {
        const types = { 1: 'info', 2: 'warning', 3: 'success' }
        return types[status] || 'info'
      }
      const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success', 5: 'danger' }
      return types[status] || 'info'
    },

    getStatusLabel(status, orderType) {
      if (orderType === 'document') {
        const labels = { 1: '待接单', 2: '进行中', 3: '已完成' }
        return labels[status] || '未知'
      }
      const labels = { 1: '待接单', 2: '已接单', 3: '处理中', 4: '已完成', 5: '已取消' }
      return labels[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;

      i {
        font-size: 28px;
        color: #fff;
      }
    }

    .stat-info {
      margin-left: 20px;

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #333;
      }

      .stat-label {
        font-size: 14px;
        color: #999;
        margin-top: 5px;
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .order-info {
    background: #f5f7fa;
    padding: 10px;
    border-radius: 4px;
    margin-bottom: 10px;

    p {
      margin: 5px 0;
      font-size: 13px;

      strong {
        color: #333;
      }
    }
  }

  .income-overview {
    .income-item {
      display: flex;
      justify-content: space-between;
      padding: 15px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .label {
        color: #666;
      }

      .value {
        font-size: 18px;
        font-weight: 600;
        color: #f56c6c;
      }
    }
  }

  .evaluate-list {
    .evaluate-item {
      padding: 15px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .evaluate-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;

        .user {
          font-weight: 500;
        }
      }

      .evaluate-content {
        color: #666;
        font-size: 14px;
        margin: 0 0 5px;
      }

      .evaluate-time {
        font-size: 12px;
        color: #999;
      }
    }
  }
}

.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}
</style>
