<template>
  <div class="qa-page">
    <el-card>
      <div slot="header">
        <span>社区互动 - 用户提问</span>
      </div>
      <div class="search-form">
        <el-form inline>
          <el-form-item label="问题状态">
            <el-select v-model="searchForm.isSolved" placeholder="全部" clearable>
              <el-option label="待回答" :value="0"></el-option>
              <el-option label="已回答" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="title" label="问题标题" min-width="200"></el-table-column>
        <el-table-column prop="userName" label="提问用户" width="100"></el-table-column>
        <el-table-column prop="categoryName" label="问题分类" width="120"></el-table-column>
        <el-table-column prop="isSolved" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isSolved ? 'success' : 'warning'">
              {{ scope.row.isSolved ? '已回答' : '待回答' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提问时间" width="150"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" v-if="!scope.row.isSolved" @click="handleAnswer(scope.row)">回答</el-button>
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
    
    <el-dialog title="回答问题" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="问题">
          <p>{{ currentQuestion?.title }}</p>
          <p class="question-content">{{ currentQuestion?.content }}</p>
        </el-form-item>
        <el-form-item label="回答内容" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="请输入您的回答"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAnswer" :loading="submitting">提交回答</el-button>
      </div>
    </el-dialog>
    
    <el-dialog title="问题详情" :visible.sync="viewDialogVisible" width="600px">
      <el-descriptions :column="1" border v-if="currentQuestion">
        <el-descriptions-item label="问题标题">{{ currentQuestion.title }}</el-descriptions-item>
        <el-descriptions-item label="问题内容">{{ currentQuestion.content }}</el-descriptions-item>
        <el-descriptions-item label="提问用户">{{ currentQuestion.userName }}</el-descriptions-item>
        <el-descriptions-item label="提问时间">{{ currentQuestion.createTime }}</el-descriptions-item>
      </el-descriptions>
      <div class="answer-list" v-if="answers.length > 0">
        <h4>回答列表</h4>
        <div class="answer-item" v-for="item in answers" :key="item.id">
          <div class="answer-header">
            <span class="answer-user">{{ item.lawyerName }}</span>
            <span class="answer-time">{{ item.createTime }}</span>
          </div>
          <p class="answer-content">{{ item.content }}</p>
          <div class="answer-footer">
            <span>点赞: {{ item.likeCount || 0 }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getQuestionList, getQuestionDetail } from '@/api/question'
import { getAnswerList, addAnswer } from '@/api/answer'

export default {
  name: 'LawyerQA',
  data() {
    return {
      loading: false,
      searchForm: {
        isSolved: null
      },
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      viewDialogVisible: false,
      currentQuestion: null,
      answers: [],
      form: {
        content: ''
      },
      rules: {
        content: [{ required: true, message: '请输入回答内容', trigger: 'blur' }]
      },
      submitting: false
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
        this.tableData = res.data?.records || []
        this.total = res.data?.total || 0
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
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    handleAnswer(row) {
      this.currentQuestion = row
      this.form.content = ''
      this.dialogVisible = true
    },
    async handleView(row) {
      this.currentQuestion = row
      this.viewDialogVisible = true
      try {
        const res = await getAnswerList({ questionId: row.id })
        this.answers = res.data?.records || []
      } catch (error) {
        console.error(error)
      }
    },
    async submitAnswer() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            await addAnswer({
              questionId: this.currentQuestion.id,
              content: this.form.content
            })
            this.$message.success('回答成功')
            this.dialogVisible = false
            this.loadData()
          } catch (error) {
            console.error(error)
          } finally {
            this.submitting = false
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.qa-page {
  .search-form {
    margin-bottom: 20px;
  }
  
  .question-content {
    color: #666;
    font-size: 14px;
    background: #f5f7fa;
    padding: 10px;
    border-radius: 4px;
  }
  
  .answer-list {
    margin-top: 20px;
    
    h4 {
      margin-bottom: 15px;
      padding-bottom: 10px;
      border-bottom: 1px solid #eee;
    }
    
    .answer-item {
      padding: 15px;
      background: #f9f9f9;
      border-radius: 8px;
      margin-bottom: 10px;
      
      .answer-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;
        
        .answer-user {
          font-weight: 500;
          color: #1890ff;
        }
        
        .answer-time {
          font-size: 12px;
          color: #999;
        }
      }
      
      .answer-content {
        color: #333;
        line-height: 1.6;
        margin: 0 0 10px;
      }
      
      .answer-footer {
        font-size: 12px;
        color: #999;
      }
    }
  }
}
</style>
