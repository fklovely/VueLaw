<!--<template>
  <div class="orders-page">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="图文咨询" name="consult"></el-tab-pane>
      <el-tab-pane label="文书代写" name="document-write"></el-tab-pane>
      <el-tab-pane label="文书审核" name="document-audit"></el-tab-pane>
    </el-tabs>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="160"></el-table-column>
      <el-table-column label="订单类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.orderType === 'consult' ? 'primary' : 'warning'" size="small">
            {{ getOrderTypeName(scope.row.orderType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题/内容" min-width="180" show-overflow-tooltip></el-table-column>
      <el-table-column prop="price" label="金额" width="100">
        <template slot-scope="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status, scope.row.orderType)">{{ getStatusLabel(scope.row.status, scope.row.orderType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button type="text" size="small" v-if="canEvaluate(scope.row)" @click="handleEvaluate(scope.row)">评价</el-button>
          <el-button type="text" size="small" class="text-danger" v-if="canCancel(scope.row)" @click="handleCancel(scope.row)">取消</el-button>
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
        <el-descriptions-item label="订单类型">{{ getOrderTypeName(currentOrder.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2" v-if="currentOrder.content || currentOrder.requirement">
          {{ currentOrder.content || currentOrder.requirement }}
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ getStatusLabel(currentOrder.status, currentOrder.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="currentOrder.result || currentOrder.resultContent">
          {{ currentOrder.result || currentOrder.resultContent }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="评价服务" :visible.sync="evaluateDialogVisible" width="500px">
      <el-form ref="evaluateForm" :model="evaluateForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="evaluateForm.score" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input type="textarea" v-model="evaluateForm.content" :rows="4" placeholder="请输入评价内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="evaluateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交评价</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getConsultList, cancelConsult, evaluateConsult } from '@/api/consult'
import { getDocumentOrderUserList } from '@/api/documentOrder'

export default {
  name: 'UserOrders',
  data() {
    return {
      loading: false,
      activeTab: 'all',
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      currentOrder: null,
      evaluateDialogVisible: false,
      evaluateForm: {
        orderId: null,
        orderType: '',
        score: 5,
        content: ''
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        if (this.activeTab === 'all') {
          await Promise.all([this.loadConsultOrders(), this.loadDocumentOrders()])
        } else if (this.activeTab === 'consult') {
          await this.loadConsultOrders()
        } else {
          await this.loadDocumentOrders()
        }
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async loadConsultOrders() {
      try {
        const res = await getConsultList({ page: this.currentPage, size: this.pageSize })
        const consultOrders = (res.data?.records || []).map(item => ({
          ...item,
          orderType: 'consult'
        }))
        if (this.activeTab === 'all' || this.activeTab === 'consult') {
          if (this.activeTab === 'consult') {
            this.tableData = consultOrders
            this.total = res.data?.total || 0
          }
        }
        return consultOrders
      } catch (error) {
        console.error(error)
        return []
      }
    },
    async loadDocumentOrders() {
      const orderType = this.activeTab === 'document-write' ? 1 : (this.activeTab === 'document-audit' ? 2 : null)
      try {
        const params = { page: this.currentPage, size: this.pageSize }
        if (orderType) params.orderType = orderType
        const res = await getDocumentOrderUserList(params)
        const docOrders = (res.data?.records || []).map(item => ({
          id: item.id,
          orderNo: item.orderNo,
          title: item.title || '文书服务',
          price: item.price,
          status: item.docStatusToConsultStatus(item.status),
          createTime: item.createTime,
          content: item.requirement,
          result: item.resultContent,
          evaluateStatus: item.evaluateStatus,
          orderType: item.orderType === 1 ? 'document-write' : 'document-audit',
          originalStatus: item.status
        }))
        if (this.activeTab !== 'consult') {
          if (this.activeTab === 'all') {
            const consultRes = await getConsultList({ page: 1, size: 1000 })
            const consultOrders = (consultRes.data?.records || []).map(item => ({ ...item, orderType: 'consult' }))
            this.tableData = [...consultOrders, ...docOrders].sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
            this.total = this.tableData.length
          } else {
            this.tableData = docOrders
            this.total = res.data?.total || 0
          }
        }
        return docOrders
      } catch (error) {
        console.error(error)
        return []
      }
    },

    handleTabChange() {
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
    handleView(row) {
      this.currentOrder = row
      this.dialogVisible = true
    },
    async handleCancel(row) {
      try {
        await this.$confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
        if (row.orderType === 'consult') {
          await cancelConsult(row.id)
        }
        this.$message.success('取消成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    handleEvaluate(row) {
      this.evaluateForm = { orderId: row.id, orderType: row.orderType, score: 5, content: '' }
      this.evaluateDialogVisible = true
    },
    async submitEvaluate() {
      try {
        if (this.evaluateForm.orderType === 'consult') {
          await evaluateConsult(this.evaluateForm.orderId, {
            score: this.evaluateForm.score,
            content: this.evaluateForm.content
          })
        }
        this.$message.success('评价成功')
        this.evaluateDialogVisible = false
        this.loadData()
      } catch (error) {
        console.error(error)
      }
    },
    canEvaluate(row) {
      const status = row.originalStatus || row.status
      return (status === 4 || status === 3) && row.evaluateStatus !== 1
    },
    canCancel(row) {
      const status = row.originalStatus || row.status
      return status === 1 && row.orderType === 'consult'
    },
    getOrderTypeName(type) {
      const types = { consult: '图文咨询', 'document-write': '文书代写', 'document-audit': '文书审核' }
      return types[type] || type
    },
    getStatusType(status, orderType) {
      if (orderType === 'consult') {
        const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success', 5: 'danger' }
        return types[status] || 'info'
      }
      const types = { 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' }
      return types[status] || 'info'
    },
    getStatusLabel(status, orderType) {
      if (orderType === 'consult') {
        const labels = { 1: '待接单', 2: '已接单', 3: '处理中', 4: '已完成', 5: '已取消' }
        return labels[status] || '未知'
      }
      const labels = { 1: '待接单', 2: '处理中', 3: '已完成', 4: '已取消' }
      return labels[status] || '未知'
    },
    docStatusToConsultStatus(status) {
      const map = { 1: 1, 2: 3, 3: 4, 4: 5 }
      return map[status] || status
    }
  }
}
</script>

<style lang="scss" scoped>
.orders-page {
  .mt-20 {
    margin-top: 20px;
  }

  .text-danger {
    color: #F56C6C;
  }
}
</style>-->



<template>
  <div class="orders-page">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="图文咨询" name="consult"></el-tab-pane>
      <el-tab-pane label="文书代写" name="document-write"></el-tab-pane>
      <el-tab-pane label="文书审核" name="document-audit"></el-tab-pane>
    </el-tabs>

    <el-table :data="pagedData" border stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="200"></el-table-column>
      <el-table-column label="订单类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="getOrderTypeTagType(scope.row.orderType)" size="small">
            {{ getOrderTypeName(scope.row.orderType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题/内容" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="price" label="金额" width="100">
        <template slot-scope="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row)">
            {{ getStatusLabel(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170"></el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看详情</el-button>

          <el-button type="text" size="small"
                     v-if="scope.row.orderType !== 'consult' && scope.row.originalStatus === 3 && scope.row.resultFileUrl"
                     @click="handleViewResult(scope.row)">
            查看结果
          </el-button>

          <el-button type="text" size="small"
                     v-if="canEvaluate(scope.row)"
                     @click="handleEvaluate(scope.row)">
            评价
          </el-button>

          <el-button type="text" size="small" class="text-danger"
                     v-if="canCancel(scope.row)"
                     @click="handleCancel(scope.row)">
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        class="mt-20"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="filteredData.length"
        :page-size="pageSize"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    ></el-pagination>

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" :visible.sync="dialogVisible" width="650px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ getOrderTypeName(currentOrder.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ getStatusLabel(currentOrder) }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="内容/需求" :span="2" v-if="currentOrder.content || currentOrder.requirement">
          {{ currentOrder.content || currentOrder.requirement }}
        </el-descriptions-item>
        <el-descriptions-item label="我上传的附件" :span="2" v-if="currentOrder.attachmentUrl">
          <el-link :href="currentOrder.attachmentUrl" target="_blank" type="primary">
            <i class="el-icon-download"></i> 下载附件
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" :span="2" v-if="currentOrder.finishTime">{{ currentOrder.finishTime }}</el-descriptions-item>
        <el-descriptions-item label="处理结果/修改说明" :span="2" v-if="currentOrder.resultContent">
          {{ currentOrder.resultContent }}
        </el-descriptions-item>
        <el-descriptions-item label="结果文件" :span="2" v-if="currentOrder.resultFileUrl">
          <el-link :href="currentOrder.resultFileUrl" target="_blank" type="success">
            <i class="el-icon-document"></i> 下载结果文件
          </el-link>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 查看结果文件弹窗 -->
    <el-dialog title="律师返回的文书" :visible.sync="resultDialogVisible" width="550px">
      <el-descriptions :column="1" border v-if="resultOrder">
        <el-descriptions-item label="订单编号">{{ resultOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ getOrderTypeName(resultOrder.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="需求标题">{{ resultOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="修改/审核说明">
          <div class="remark-content">{{ resultOrder.resultContent || '无' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="结果文件">
          <el-link :href="resultOrder.resultFileUrl" target="_blank" type="success" :underline="false">
            <i class="el-icon-document"></i> {{ getFileName(resultOrder.resultFileUrl) }}
          </el-link>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button type="primary" @click="downloadResultFile" v-if="resultOrder && resultOrder.resultFileUrl">
          <i class="el-icon-download"></i> 下载文件
        </el-button>
        <el-button @click="resultDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog title="评价服务" :visible.sync="evaluateDialogVisible" width="500px">
      <el-form ref="evaluateForm" :model="evaluateForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="evaluateForm.score" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input type="textarea" v-model="evaluateForm.content" :rows="4" placeholder="请输入评价内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="evaluateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交评价</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getConsultList, cancelConsult, evaluateConsult } from '@/api/consult'
// 使用正确的导入函数名
import { getDocumentOrderUserList, cancelDocumentOrder } from '@/api/documentOrder'

export default {
  name: 'UserOrders',
  data() {
    return {
      loading: false,
      activeTab: 'all',
      allOrders: [],
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      currentOrder: null,
      resultDialogVisible: false,
      resultOrder: null,
      evaluateDialogVisible: false,
      evaluateForm: {
        orderId: null,
        orderType: '',
        score: 5,
        content: ''
      }
    }
  },
  computed: {
    filteredData() {
      if (this.activeTab === 'all') {
        return this.allOrders
      }
      return this.allOrders.filter(order => order.orderType === this.activeTab)
    },
    pagedData() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredData.slice(start, end)
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const [consultOrders, docOrders] = await Promise.all([
          this.fetchConsultOrders(),
          this.fetchDocumentOrders()
        ])

        console.log('咨询订单数量:', consultOrders.length)
        console.log('文书订单数量:', docOrders.length)
        console.log('文书订单详情:', docOrders)

        this.allOrders = [...consultOrders, ...docOrders].sort((a, b) =>
            new Date(b.createTime) - new Date(a.createTime)
        )

        console.log('总订单数量:', this.allOrders.length)

      } catch (error) {
        console.error('加载订单失败:', error)
        this.$message.error('加载订单失败')
      } finally {
        this.loading = false
      }
    },

    async fetchConsultOrders() {
      try {
        const res = await getConsultList({ page: 1, size: 100 })
        console.log('咨询订单API返回:', res.data)

        const orders = (res.data?.records || []).map(item => ({
          id: item.id,
          orderNo: item.orderNo,
          orderType: 'consult',
          title: item.title,
          content: item.content,
          price: item.price,
          originalStatus: item.status,
          createTime: item.createTime,
          finishTime: item.finishTime,
          result: item.result,
          evaluateStatus: item.evaluateStatus,
          lawyerId: item.lawyerId,
          phone: item.phone,
          attachmentUrl: null,
          resultFileUrl: null,
          resultContent: item.result
        }))
        return orders
      } catch (error) {
        console.error('获取咨询订单失败:', error)
        return []
      }
    },

    async fetchDocumentOrders() {
      try {
        // 使用正确的API函数
        const res = await getDocumentOrderUserList({ page: 1, size: 100 })
        console.log('文书订单API返回:', res)

        let records = []
        if (res.data && res.data.records) {
          records = res.data.records
        } else if (res.records) {
          records = res.records
        }

        console.log('文书订单数量:', records.length)
        console.log('文书订单详情:', records)

        if (records.length === 0) {
          return []
        }

        const orders = records.map(item => {
          // orderType: 1-文书代写, 2-文书审核
          let orderType = item.orderType === 1 ? 'document-write' : 'document-audit'

          return {
            id: item.id,
            orderNo: item.orderNo,
            orderType: orderType,
            title: item.title,
            requirement: item.requirement,
            content: item.requirement,
            price: item.price,
            originalStatus: item.status,     // 1-待接单,2-处理中,3-已完成,4-已取消
            createTime: item.createTime,
            finishTime: item.finishTime,
            resultContent: item.resultContent,
            resultFileUrl: item.resultFileUrl,
            evaluateStatus: item.evaluateStatus,
            lawyerId: item.lawyerId,
            attachmentUrl: item.attachmentUrl
          }
        })

        console.log('处理后文书订单:', orders)
        return orders

      } catch (error) {
        console.error('获取文书订单失败:', error)
        return []
      }
    },

    handleTabClick() {
      this.currentPage = 1
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
    },

    handleCurrentChange(val) {
      this.currentPage = val
    },

    handleView(row) {
      this.currentOrder = row
      this.dialogVisible = true
    },

    handleViewResult(row) {
      this.resultOrder = row
      this.resultDialogVisible = true
    },

    downloadResultFile() {
      if (this.resultOrder && this.resultOrder.resultFileUrl) {
        window.open(this.resultOrder.resultFileUrl, '_blank')
      }
    },

    getFileName(url) {
      if (!url) return '文件'
      return decodeURIComponent(url.split('/').pop())
    },

    async handleCancel(row) {
      try {
        await this.$confirm('确定要取消该订单吗？取消后无法恢复。', '提示', { type: 'warning' })
        if (row.orderType === 'consult') {
          await cancelConsult(row.id)
          this.$message.success('取消成功')
          this.loadData()
        } else {
          await cancelDocumentOrder(row.id)
          this.$message.success('取消成功')
          this.loadData()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          this.$message.error('取消失败')
        }
      }
    },

    handleEvaluate(row) {
      this.evaluateForm = {
        orderId: row.id,
        orderType: row.orderType,
        score: 5,
        content: ''
      }
      this.evaluateDialogVisible = true
    },

    async submitEvaluate() {
      if (!this.evaluateForm.content) {
        this.$message.warning('请输入评价内容')
        return
      }

      try {
        if (this.evaluateForm.orderType === 'consult') {
          await evaluateConsult(this.evaluateForm.orderId, {
            score: this.evaluateForm.score,
            content: this.evaluateForm.content
          })
          this.$message.success('评价成功')
          this.evaluateDialogVisible = false
          this.loadData()
        } else {
          this.$message.warning('暂不支持评价文书订单')
        }
      } catch (error) {
        console.error(error)
        this.$message.error('评价失败')
      }
    },

    canEvaluate(row) {
      if (row.orderType === 'consult') {
        return row.originalStatus === 4 && row.evaluateStatus !== 1
      }
      return row.originalStatus === 3 && row.evaluateStatus !== 1
    },

    canCancel(row) {
      return row.originalStatus === 1
    },

    getOrderTypeName(type) {
      const map = {
        consult: '图文咨询',
        'document-write': '文书代写',
        'document-audit': '文书审核'
      }
      return map[type] || type
    },

    getOrderTypeTagType(type) {
      const map = {
        consult: 'primary',
        'document-write': 'success',
        'document-audit': 'warning'
      }
      return map[type] || 'info'
    },

    getDisplayStatus(row) {
      if (row.orderType === 'consult') {
        return row.originalStatus
      }
      const statusMap = { 1: 1, 2: 3, 3: 4, 4: 5 }
      return statusMap[row.originalStatus] || row.originalStatus
    },

    getStatusType(row) {
      const status = this.getDisplayStatus(row)
      const map = {
        1: 'info',
        2: 'warning',
        3: 'primary',
        4: 'success',
        5: 'danger'
      }
      return map[status] || 'info'
    },

    getStatusLabel(row) {
      const status = this.getDisplayStatus(row)
      const map = {
        1: '待接单',
        2: '已接单',
        3: '处理中',
        4: '已完成',
        5: '已取消'
      }
      return map[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.orders-page {
  padding: 20px;

  .mt-20 {
    margin-top: 20px;
  }

  .text-danger {
    color: #F56C6C;
  }

  .remark-content {
    white-space: pre-wrap;
    word-break: break-word;
    line-height: 1.6;
  }
}
</style>
