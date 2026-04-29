<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="文章标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="文章类型">
          <el-select v-model="searchForm.articleType" placeholder="全部" clearable>
            <el-option label="普法文章" :value="1"></el-option>
            <el-option label="案例解读" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待审核" :value="0"></el-option>
            <el-option label="已发布" :value="1"></el-option>
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
      <el-table-column prop="articleNo" label="文章编号" width="140"></el-table-column>
      <el-table-column prop="title" label="文章标题" min-width="200"></el-table-column>
      <el-table-column prop="authorName" label="作者" width="100"></el-table-column>
      <el-table-column prop="articleType" label="类型" width="100">
        <template slot-scope="scope">
          {{ scope.row.articleType === 1 ? '普法文章' : '案例解读' }}
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="80"></el-table-column>
      <el-table-column prop="likeCount" label="点赞数" width="80"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="提交时间" width="160"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" v-if="scope.row.status === 0" @click="handleAudit(scope.row, 1)">通过</el-button>
          <el-button type="text" size="small" class="text-warning" v-if="scope.row.status === 0" @click="handleAudit(scope.row, 2)">拒绝</el-button>
          <el-button type="text" size="small" class="text-danger" @click="handleDelete(scope.row)">删除</el-button>
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
  </div>
</template>

<script>
import { getArticleList, deleteArticle, auditArticle } from '@/api/article'

export default {
  name: 'AdminArticle',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        keyword: '',
        articleType: null,
        status: null
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
        const res = await getArticleList({
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
      this.searchForm = { keyword: '', articleType: null, status: null }
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
    async handleAudit(row, status) {
      const action = status === 1 ? '通过' : '拒绝'
      try {
        await this.$confirm(`确定要${action}该文章吗？`, '提示', { type: 'warning' })
        await auditArticle(row.id, status)
        this.$message.success(`${action}成功`)
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该文章吗？', '提示', { type: 'warning' })
        await deleteArticle(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'success', 2: 'danger' }
      return types[status] || 'info'
    },
    getStatusLabel(status) {
      const labels = { 0: '待审核', 1: '已发布', 2: '已拒绝' }
      return labels[status] || '未知'
    }
  }
}
</script>
