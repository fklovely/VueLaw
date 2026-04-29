<template>
  <div class="regulation-detail-page">
    <div class="container">
      <el-card class="mb-20">
        <div class="reg-header">
          <h1>{{ regulation.title }}</h1>
          <div class="reg-meta">
            <span><i class="el-icon-document"></i> {{ regulation.regType }}</span>
            <span><i class="el-icon-office-building"></i> {{ regulation.issueOrg }}</span>
            <span><i class="el-icon-date"></i> 发布日期：{{ regulation.issueDate }}</span>
            <span><i class="el-icon-check"></i> 生效日期：{{ regulation.effectiveDate }}</span>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="reg-content">
          <h3>法规摘要</h3>
          <p>{{ regulation.summary }}</p>
          <h3>条文解读</h3>
          <p>{{ regulation.interpretation }}</p>
          <h3>法规全文</h3>
          <div class="full-content">{{ regulation.content }}</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { getRegulationDetail } from '@/api/regulation'

export default {
  name: 'RegulationDetail',
  data() {
    return {
      regulation: {}
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getRegulationDetail(this.$route.params.id)
        this.regulation = res.data
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.regulation-detail-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  
  .container {
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .reg-header {
    h1 {
      font-size: 24px;
      margin: 0 0 15px;
    }
    
    .reg-meta {
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
  
  .reg-content {
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
