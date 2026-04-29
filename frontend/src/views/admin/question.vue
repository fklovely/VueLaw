<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="问题标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="解决状态">
          <el-select v-model="searchForm.isSolved" placeholder="全部" clearable>
            <el-option label="已解决" :value="1"></el-option>
            <el-option label="未解决" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="questionNo" label="问题编号" width="140"></el-table-column>
      <el-table-column prop="title" label="问题标题" min-width="250"></el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="80"></el-table-column>
      <el-table-column prop="answerCount" label="回答数" width="80"></el-table-column>
      <el-table-column prop="likeCount" label="点赞数" width="80"></el-table-column>
      <el-table-column prop="isSolved" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isSolved === 1 ? 'success' : 'warning'">
            {{ scope.row.isSolved === 1 ? '已解决' : '未解决' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提问时间" width="160"></el-table-column>
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
    
    <el-dialog title="问题详情" :visible.sync="dialogVisible" width="700px">
      <el-descriptions :column="2" border v-if="currentQuestion">
        <el-descriptions-item label="问题标题" :span="2">{{ currentQuestion.title }}</el-descriptions-item>
        <el-descriptions-item label="问题内容" :span="2">{{ currentQuestion.content }}</el-descriptions-item>
        <el-descriptions-item label="浏览量">{{ currentQuestion.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="回答数">{{ currentQuestion.answerCount }}</el-descriptions-item>
        <el-descriptions-item label="提问时间">{{ currentQuestion.createTime }}</el-descriptions-item>
        <el-descriptions-item label="解决状态">
          <el-tag :type="currentQuestion.isSolved === 1 ? 'success' : 'warning'">
            {{ currentQuestion.isSolved === 1 ? '已解决' : '未解决' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getQuestionList } from '@/api/question'

export default {
  name: 'AdminQuestion',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        keyword: '',
        isSolved: null
      },
      dialogVisible: false,
      currentQuestion: null
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getQuestionList({
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
      this.searchForm = { keyword: '', isSolved: null }
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
      this.currentQuestion = row
      this.dialogVisible = true
    }
  }
}
</script>
