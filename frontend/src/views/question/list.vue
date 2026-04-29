<template>
  <div class="question-list-page">
    <div class="page-header">
      <h2>互动问答</h2>
      <p>提出法律疑问，律师和用户共同解答</p>
    </div>
    
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="question-list">
            <div class="question-item" v-for="item in questionList" :key="item.id" @click="$router.push(`/question/detail/${item.id}`)">
              <div class="question-stats">
                <div class="stat-item">
                  <span class="value">{{ item.answerCount }}</span>
                  <span class="label">回答</span>
                </div>
                <div class="stat-item">
                  <span class="value">{{ item.viewCount }}</span>
                  <span class="label">浏览</span>
                </div>
              </div>
              <div class="question-content">
                <h4>
                  <el-tag size="mini" type="success" v-if="item.isSolved === 1">已解决</el-tag>
                  {{ item.title }}
                </h4>
                <p class="summary">{{ item.content }}</p>
                <div class="meta">
                  <span>{{ item.createTime }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <el-pagination
            class="pagination"
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          ></el-pagination>
        </el-col>
        
        <el-col :span="8">
          <el-button type="primary" block @click="showAskDialog">我要提问</el-button>
          
          <el-card class="mt-20">
            <div slot="header">热门问题</div>
            <div class="hot-list">
              <div class="hot-item" v-for="(item, index) in hotQuestions" :key="item.id">
                <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <span class="title" @click="$router.push(`/question/detail/${item.id}`)">{{ item.title }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <el-dialog title="我要提问" :visible.sync="askDialogVisible" width="600px">
      <el-form ref="askForm" :model="askForm" :rules="askRules" label-width="80px">
        <el-form-item label="问题标题" prop="title">
          <el-input v-model="askForm.title" placeholder="请输入问题标题"></el-input>
        </el-form-item>
        <el-form-item label="问题内容" prop="content">
          <el-input type="textarea" v-model="askForm.content" :rows="5" placeholder="请详细描述您的问题"></el-input>
        </el-form-item>
        <el-form-item label="匿名提问">
          <el-switch v-model="askForm.anonymous" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="askDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitQuestion">提交问题</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getQuestionList, addQuestion } from '@/api/question'

export default {
  name: 'QuestionList',
  data() {
    return {
      questionList: [],
      hotQuestions: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      askDialogVisible: false,
      askForm: {
        title: '',
        content: '',
        anonymous: 0
      },
      askRules: {
        title: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入问题内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadData()
    this.loadHotQuestions()
  },
  methods: {
    async loadData() {
      try {
        const res = await getQuestionList({ page: this.currentPage, size: this.pageSize })
        this.questionList = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error(error)
      }
    },
    async loadHotQuestions() {
      try {
        const res = await getQuestionList({ page: 1, size: 10 })
        this.hotQuestions = res.data.records.sort((a, b) => b.viewCount - a.viewCount).slice(0, 10)
      } catch (error) {
        console.error(error)
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
    showAskDialog() {
      if (!this.$store.getters.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.askForm = { title: '', content: '', anonymous: 0 }
      this.askDialogVisible = true
    },
    async submitQuestion() {
      this.$refs.askForm.validate(async valid => {
        if (valid) {
          try {
            await addQuestion(this.askForm)
            this.$message.success('提问成功')
            this.askDialogVisible = false
            this.loadData()
          } catch (error) {
            console.error(error)
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.question-list-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  
  .page-header {
    background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
    padding: 40px 0;
    text-align: center;
    color: #fff;
    
    h2 {
      font-size: 28px;
      margin: 0 0 10px;
    }
    
    p {
      margin: 0;
      opacity: 0.9;
    }
  }
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .question-list {
    .question-item {
      display: flex;
      background: #fff;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 15px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      .question-stats {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-right: 20px;
        
        .stat-item {
          text-align: center;
          margin-bottom: 10px;
          
          .value {
            display: block;
            font-size: 20px;
            font-weight: 600;
            color: #1890ff;
          }
          
          .label {
            font-size: 12px;
            color: #999;
          }
        }
      }
      
      .question-content {
        flex: 1;
        
        h4 {
          margin: 0 0 10px;
          font-size: 16px;
          
          .el-tag {
            margin-right: 8px;
          }
        }
        
        .summary {
          color: #666;
          font-size: 14px;
          line-height: 1.6;
          margin: 0 0 10px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }
        
        .meta {
          font-size: 13px;
          color: #999;
        }
      }
    }
  }
  
  .hot-list {
    .hot-item {
      display: flex;
      align-items: flex-start;
      padding: 10px 0;
      border-bottom: 1px solid #eee;
      
      &:last-child {
        border-bottom: none;
      }
      
      .rank {
        width: 20px;
        height: 20px;
        background: #999;
        color: #fff;
        border-radius: 4px;
        font-size: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 10px;
        flex-shrink: 0;
        
        &.top {
          background: #f56c6c;
        }
      }
      
      .title {
        font-size: 14px;
        color: #333;
        cursor: pointer;
        
        &:hover {
          color: #1890ff;
        }
      }
    }
  }
  
  .pagination {
    text-align: center;
    margin-top: 20px;
  }
}
</style>
