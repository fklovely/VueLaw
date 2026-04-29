<template>
  <div class="service-record-page">
    <el-card>
      <div slot="header">
        <span>我的服务记录</span>
      </div>
      <div class="search-form">
        <el-form inline>
          <el-form-item label="服务类型">
            <el-select v-model="searchForm.serviceType" placeholder="全部" clearable>
              <el-option label="法律咨询" value="consult"></el-option>
              <el-option label="文书代写" value="write"></el-option>
              <el-option label="文书审核" value="audit"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
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
            <el-tag>{{ getServiceType(scope.row.serviceType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="服务内容" min-width="180"></el-table-column>
        <el-table-column prop="clientName" label="客户" width="100"></el-table-column>
        <el-table-column prop="price" label="金额" width="100">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="120">
          <template slot-scope="scope">
            <el-rate :value="scope.row.score" disabled v-if="scope.row.score"></el-rate>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="完成时间" width="150"></el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
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
    
    <el-dialog title="服务详情" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="订单编号">{{ currentRecord.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ getServiceType(currentRecord.serviceType) }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentRecord.clientName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRecord.clientPhone }}</el-descriptions-item>
        <el-descriptions-item label="服务内容" :span="2">{{ currentRecord.title }}</el-descriptions-item>
        <el-descriptions-item label="服务详情" :span="2">{{ currentRecord.content }}</el-descriptions-item>
        <el-descriptions-item label="服务金额">¥{{ currentRecord.price }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentRecord.createTime }}</el-descriptions-item>
        <el-descriptions-item label="客户评价" :span="2" v-if="currentRecord.evaluate">
          <el-rate :value="currentRecord.score" disabled></el-rate>
          <p>{{ currentRecord.evaluate }}</p>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ServiceRecord',
  data() {
    return {
      loading: false,
      searchForm: {
        serviceType: '',
        dateRange: []
      },
      tableData: [
        { id: 1, orderNo: 'CON202601150001', serviceType: 'consult', title: '关于校园欺凌的法律咨询', clientName: '张先生', clientPhone: '13800138000', price: 200, status: 4, score: 5, evaluate: '律师非常专业，解答很详细', createTime: '2026-01-15 10:00:00', content: '咨询关于孩子在学校遭受欺凌的处理方式' },
        { id: 2, orderNo: 'DOC202601140002', serviceType: 'write', title: '监护权变更起诉状代写', clientName: '李女士', clientPhone: '13900139000', price: 500, status: 4, score: 4, evaluate: '文书质量很好，服务态度好', createTime: '2026-01-14 15:30:00', content: '代写监护权变更起诉状' }
      ],
      total: 2,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      currentRecord: null
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
    handleView(row) {
      this.currentRecord = row
      this.dialogVisible = true
    },
    getServiceType(type) {
      const types = { consult: '法律咨询', write: '文书代写', audit: '文书审核' }
      return types[type]
    },
    getStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success' }
      return types[status]
    },
    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '进行中', 3: '处理中', 4: '已完成' }
      return labels[status]
    }
  }
}
</script>

<style lang="scss" scoped>
.service-record-page {
  .search-form {
    margin-bottom: 20px;
  }
}
</style>
