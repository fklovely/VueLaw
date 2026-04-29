<template>
  <div class="page-container">
    <el-card>
      <div slot="header">
        <span>收益明细</span>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="recordNo" label="记录编号" width="160"></el-table-column>
        <el-table-column prop="orderType" label="订单类型" width="100">
          <template slot-scope="scope">
            {{ getOrderType(scope.row.orderType) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderAmount" label="订单金额" width="120">
          <template slot-scope="scope">¥{{ scope.row.orderAmount }}</template>
        </el-table-column>
        <el-table-column prop="commissionAmount" label="平台佣金" width="120">
          <template slot-scope="scope">¥{{ scope.row.commissionAmount }}</template>
        </el-table-column>
        <el-table-column prop="incomeAmount" label="实际收入" width="120">
          <template slot-scope="scope">
            <span class="text-success">¥{{ scope.row.incomeAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
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
  </div>
</template>

<script>
import { getIncomeList } from '@/api/income'

export default {
  name: 'LawyerIncome',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getIncomeList({
          page: this.currentPage,
          size: this.pageSize
        })
        this.tableData = res.data?.records || []
        this.total = res.data?.total || 0
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
    getOrderType(type) {
      const types = { 1: '咨询', 2: '文书', 3: '课程' }
      return types[type] || '其他'
    },
    getStatusType(status) {
      const types = { 1: 'warning', 2: 'success', 3: 'info' }
      return types[status] || 'info'
    },
    getStatusLabel(status) {
      const labels = { 1: '待结算', 2: '已结算', 3: '已提现' }
      return labels[status] || '未知'
    }
  }
}
</script>
