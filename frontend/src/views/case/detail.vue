<template>
  <div class="case-detail-page">
    <div class="container">
      <el-card class="mb-20">
        <div class="case-header">
          <h1>{{ caseInfo.title }}</h1>
          <div class="case-meta">
            <span><i class="el-icon-office-building"></i> {{ caseInfo.courtName }}</span>
            <span><i class="el-icon-document"></i> {{ caseInfo.caseType }}</span>
            <span><i class="el-icon-location"></i> {{ caseInfo.province }} {{ caseInfo.city }}</span>
            <span><i class="el-icon-date"></i> {{ caseInfo.judgeDate }}</span>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="case-content">
          <h3>案例摘要</h3>
          <p>{{ caseInfo.summary }}</p>
          <h3>判决要点</h3>
          <p>{{ caseInfo.keyPoints }}</p>
          <h3>法律依据</h3>
          <p>{{ caseInfo.legalBasis }}</p>
          <h3>判决书全文</h3>
          <div class="full-content">{{ caseInfo.content }}</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { getCaseDetail } from '@/api/case'

export default {
  name: 'CaseDetail',
  data() {
    return {
      caseInfo: {}
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getCaseDetail(this.$route.params.id)
        this.caseInfo = res.data
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.case-detail-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  
  .container {
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .case-header {
    h1 {
      font-size: 24px;
      margin: 0 0 15px;
    }
    
    .case-meta {
      font-size: 14px;
      color: #999;
      
      span {
        margin-right: 20px;
        
        i {
          margin-right: 5px;
        }
      }
    }
  }
  
  .case-content {
    h3 {
      font-size: 18px;
      margin: 20px 0 10px;
      padding-left: 10px;
      border-left: 3px solid #1890ff;
    }
    
    p {
      color: #666;
      line-height: 1.8;
      margin: 0;
    }
    
    .full-content {
      white-space: pre-wrap;
      line-height: 1.8;
      color: #666;
    }
  }
}
</style>
