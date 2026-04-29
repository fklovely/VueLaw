<template>
  <div class="consult-page">
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
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button type="text" size="small" v-if="scope.row.status === 4 && scope.row.evaluateStatus !== 1" @click="handleEvaluate(scope.row)">评价</el-button>
          <el-button type="text" size="small" class="text-danger" v-if="scope.row.status === 1" @click="handleCancel(scope.row)">取消</el-button>
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
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ getStatusLabel(currentOrder.status) }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="currentOrder.result">{{ currentOrder.result }}</el-descriptions-item>
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

export default {
  name: 'UserConsult',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      currentOrder: null,
      evaluateDialogVisible: false,
      evaluateForm: {
        orderId: null,
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
        const res = await getConsultList({ page: this.currentPage, size: this.pageSize })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
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
        await cancelConsult(row.id)
        this.$message.success('取消成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    handleEvaluate(row) {
      this.evaluateForm = { orderId: row.id, score: 5, content: '' }
      this.evaluateDialogVisible = true
    },
    async submitEvaluate() {
      try {
        await evaluateConsult(this.evaluateForm.orderId, {
          score: this.evaluateForm.score,
          content: this.evaluateForm.content
        })
        this.$message.success('评价成功')
        this.evaluateDialogVisible = false
        this.loadData()
      } catch (error) {
        console.error(error)
      }
    },
    getConsultType(type) {
      const types = { 1: '图文咨询' }
      return types[type] || '图文咨询'
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
