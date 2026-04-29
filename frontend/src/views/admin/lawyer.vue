<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/律所" clearable></el-input>
        </el-form-item>
        <el-form-item label="擅长领域">
          <el-input v-model="searchForm.expertise" placeholder="擅长领域" clearable></el-input>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待审核" :value="0"></el-option>
            <el-option label="已通过" :value="1"></el-option>
            <el-option label="已拒绝" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="lawyerName" label="姓名" width="100"></el-table-column>
      <el-table-column prop="lawyerNo" label="执业证号" width="140"></el-table-column>
      <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
      <el-table-column prop="lawFirm" label="所属律所" min-width="150"></el-table-column>
      <el-table-column prop="expertises" label="擅长领域" min-width="180"></el-table-column>
      <el-table-column prop="rating" label="评分" width="100">
        <template slot-scope="scope">
          <el-rate :value="scope.row.rating" disabled show-score text-color="#ff9900"></el-rate>
        </template>
      </el-table-column>
      <el-table-column prop="serviceCount" label="服务次数" width="100"></el-table-column>
      <el-table-column prop="status" label="审核状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button type="text" size="small" v-if="scope.row.status === 0" @click="handleAudit(scope.row, 1)">通过</el-button>
          <el-button type="text" size="small" class="text-danger" v-if="scope.row.status === 0" @click="handleAudit(scope.row, 2)">拒绝</el-button>
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
    
    <el-dialog title="律师详情" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentLawyer">
        <el-descriptions-item label="姓名">{{ currentLawyer.lawyerName }}</el-descriptions-item>
        <el-descriptions-item label="执业证号">{{ currentLawyer.lawyerNo }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentLawyer.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentLawyer.email }}</el-descriptions-item>
        <el-descriptions-item label="省份">{{ currentLawyer.province }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ currentLawyer.city }}</el-descriptions-item>
        <el-descriptions-item label="所属律所" :span="2">{{ currentLawyer.lawFirm }}</el-descriptions-item>
        <el-descriptions-item label="学历">{{ currentLawyer.education }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentLawyer.professionalTitle }}</el-descriptions-item>
        <el-descriptions-item label="擅长领域" :span="2">{{ currentLawyer.expertises }}</el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="2">{{ currentLawyer.profile }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getLawyerList, auditLawyer } from '@/api/lawyer'

export default {
  name: 'AdminLawyer',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        keyword: '',
        expertise: '',
        status: null
      },
      dialogVisible: false,
      currentLawyer: null
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getLawyerList({
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
      this.searchForm = { keyword: '', expertise: '', status: null }
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
    handleView(row) {
      this.currentLawyer = row
      this.dialogVisible = true
    },
    async handleAudit(row, status) {
      const action = status === 1 ? '通过' : '拒绝'
      try {
        const { value } = await this.$prompt(status === 2 ? '请输入拒绝原因' : '确认通过审核？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: status === 2 ? /.+/ : null,
          inputErrorMessage: '请输入拒绝原因'
        })
        await auditLawyer({ id: row.id, status, rejectReason: value || '' })
        this.$message.success(`审核${action}成功`)
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'success', 2: 'danger' }
      return types[status]
    },
    getStatusLabel(status) {
      const labels = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
      return labels[status]
    }
  }
}
</script>
