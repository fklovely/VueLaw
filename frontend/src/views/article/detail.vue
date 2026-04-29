<template>
  <div class="article-detail-page">
    <div class="container">
      <el-card>
        <div class="article-header">
          <h1>{{ article.title }}</h1>
          <div class="article-meta">
            <span class="author">{{ article.authorName }}</span>
            <span class="time">{{ article.publishTime }}</span>
            <span class="views"><i class="el-icon-view"></i> {{ article.viewCount }}</span>
            <span class="likes"><i class="el-icon-star-off"></i> {{ article.likeCount }}</span>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="article-content">
          <div v-html="article.content"></div>
        </div>
        <div class="article-tags">
          <el-tag v-for="(tag, index) in getTags(article.tags)" :key="index" size="small">{{ tag }}</el-tag>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { getArticleDetail } from '@/api/article'

export default {
  name: 'ArticleDetail',
  data() {
    return {
      article: {}
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getArticleDetail(this.$route.params.id)
        this.article = res.data
      } catch (error) {
        console.error(error)
      }
    },
    getTags(str) {
      if (!str) return []
      return str.split(',')
    }
  }
}
</script>

<style lang="scss" scoped>
.article-detail-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  
  .container {
    max-width: 900px;
    margin: 0 auto;
  }
  
  .article-header {
    h1 {
      font-size: 28px;
      margin: 0 0 15px;
    }
    
    .article-meta {
      font-size: 14px;
      color: #999;
      
      span {
        margin-right: 20px;
      }
    }
  }
  
  .article-content {
    font-size: 16px;
    line-height: 1.8;
    color: #333;
    
    p {
      margin-bottom: 15px;
    }
  }
  
  .article-tags {
    margin-top: 20px;
    
    .el-tag {
      margin-right: 10px;
    }
  }
}
</style>
