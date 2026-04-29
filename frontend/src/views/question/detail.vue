<template>
  <div class="question-detail-page">
    <div class="container">
      <el-card class="mb-20">
        <div class="question-header">
          <h1>
            <el-tag size="small" type="success" v-if="question.isSolved === 1">已解决</el-tag>
            {{ question.title }}
          </h1>
          <div class="question-meta">
            <span>{{ question.createTime }}</span>
            <span><i class="el-icon-view"></i> {{ question.viewCount }}次浏览</span>
          </div>
        </div>
        <div class="question-content">
          <p>{{ question.content }}</p>
        </div>
      </el-card>
      
      <el-card class="mb-20">
        <div slot="header">
          <span>回答 ({{ answers.length }})</span>
        </div>
        <div class="answer-list">
          <div class="answer-item" v-for="answer in answers" :key="answer.id">
            <div class="answer-user">
              <el-avatar :size="40" :src="defaultAvatar"></el-avatar>
              <div class="user-info">
                <span class="name">{{ answer.userName || '匿名用户' }}</span>
                <span class="time">{{ answer.createTime }}</span>
              </div>
              <el-tag size="small" type="success" v-if="answer.isBest === 1">最佳答案</el-tag>
            </div>
            <div class="answer-content">
              <p>{{ answer.content }}</p>
            </div>
            <div class="answer-actions">
              <el-button type="text" size="small" @click="handleLike(answer)">
                <i class="el-icon-star-off"></i> {{ answer.likeCount }}
              </el-button>
              <el-button type="text" size="small" v-if="question.isSolved !== 1 && isOwner" @click="handleAdopt(answer)">采纳</el-button>
            </div>
          </div>
        </div>
      </el-card>
      
      <el-card>
        <div slot="header">我要回答</div>
        <el-form ref="answerForm" :model="answerForm" :rules="answerRules">
          <el-form-item prop="content">
            <el-input type="textarea" v-model="answerForm.content" :rows="5" placeholder="请输入您的回答"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitAnswer">提交回答</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import { getQuestionDetail, adoptAnswer } from '@/api/question'
import { getAnswerList, addAnswer, likeAnswer } from '@/api/question'

export default {
  name: 'QuestionDetail',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      question: {},
      answers: [],
      answerForm: {
        questionId: null,
        content: ''
      },
      answerRules: {
        content: [{ required: true, message: '请输入回答内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isOwner() {
      return this.question.userId === this.$store.getters.userInfo?.userId
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getQuestionDetail(this.$route.params.id)
        this.question = res.data
        this.answerForm.questionId = this.question.id
        this.loadAnswers()
      } catch (error) {
        console.error(error)
      }
    },
    async loadAnswers() {
      try {
        const res = await getAnswerList({ questionId: this.question.id, page: 1, size: 100 })
        this.answers = res.data.records
      } catch (error) {
        console.error(error)
      }
    },
    async submitAnswer() {
      if (!this.$store.getters.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      this.$refs.answerForm.validate(async valid => {
        if (valid) {
          try {
            await addAnswer(this.answerForm)
            this.$message.success('回答成功')
            this.answerForm.content = ''
            this.loadAnswers()
          } catch (error) {
            console.error(error)
          }
        }
      })
    },
    async handleLike(answer) {
      try {
        await likeAnswer(answer.id)
        answer.likeCount++
        this.$message.success('点赞成功')
      } catch (error) {
        console.error(error)
      }
    },
    async handleAdopt(answer) {
      try {
        await this.$confirm('确定采纳此回答为最佳答案吗？', '提示', { type: 'warning' })
        await adoptAnswer({ questionId: this.question.id, answerId: answer.id })
        this.$message.success('采纳成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.question-detail-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  
  .container {
    max-width: 900px;
    margin: 0 auto;
  }
  
  .question-header {
    h1 {
      font-size: 24px;
      margin: 0 0 15px;
      
      .el-tag {
        margin-right: 10px;
      }
    }
    
    .question-meta {
      font-size: 14px;
      color: #999;
      
      span {
        margin-right: 20px;
      }
    }
  }
  
  .question-content {
    font-size: 16px;
    line-height: 1.8;
    color: #333;
  }
  
  .answer-list {
    .answer-item {
      padding: 20px 0;
      border-bottom: 1px solid #eee;
      
      &:last-child {
        border-bottom: none;
      }
      
      .answer-user {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
        
        .user-info {
          flex: 1;
          margin-left: 10px;
          
          .name {
            font-weight: 600;
            margin-right: 10px;
          }
          
          .time {
            color: #999;
            font-size: 13px;
          }
        }
      }
      
      .answer-content {
        font-size: 15px;
        line-height: 1.8;
        color: #333;
        margin-bottom: 15px;
      }
      
      .answer-actions {
        .el-button {
          padding: 0;
          margin-right: 20px;
        }
      }
    }
  }
}
</style>
