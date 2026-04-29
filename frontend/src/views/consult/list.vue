<template>
  <div class="consult-list-page">
    <div class="container">
      <el-card>
        <div slot="header">
          <span>我的咨询</span>
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
          <el-table-column label="操作" width="150" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
              <el-button type="text" size="small" v-if="scope.row.status === 4 && scope.row.evaluateStatus !== 1" @click="handleEvaluate(scope.row)">评价</el-button>
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
    </div>
  </div>
</template>

<script>
import { getConsultList } from '@/api/consult'

export default {
  name: 'ConsultList',
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
      this.$router.push(`/consult/detail/${row.id}`)
    },
    handleEvaluate(row) {
      this.$router.push(`/consult/evaluate/${row.id}`)
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

<style lang="scss" scoped>
.consult-list-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
  }
}
</style>
