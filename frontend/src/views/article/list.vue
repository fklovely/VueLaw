<template>
  <div class="article-list-page">
    <div class="page-header">
      <h2>普法文章</h2>
      <p>了解法律知识，维护自身权益</p>
    </div>
    
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="article-list">
            <div class="article-item" v-for="item in articleList" :key="item.id" @click="$router.push(`/article/detail/${item.id}`)">
              <div class="article-cover" v-if="item.coverImage">
                <img :src="item.coverImage" alt="">
              </div>
              <div class="article-info">
                <h4>{{ item.title }}</h4>
                <p class="summary">{{ item.summary }}</p>
                <div class="meta">
                  <span class="author">{{ item.authorName }}</span>
                  <span class="time">{{ item.publishTime }}</span>
                  <span class="views"><i class="el-icon-view"></i> {{ item.viewCount }}</span>
                  <span class="likes"><i class="el-icon-star-off"></i> {{ item.likeCount }}</span>
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
          <el-card class="mb-20">
            <div slot="header">热门文章</div>
            <div class="hot-list">
              <div class="hot-item" v-for="(item, index) in hotArticles" :key="item.id">
                <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <span class="title" @click="$router.push(`/article/detail/${item.id}`)">{{ item.title }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getArticleList } from '@/api/article'

export default {
  name: 'ArticleList',
  data() {
    return {
      articleList: [],
      hotArticles: [],
      total: 0,
      currentPage: 1,
      pageSize: 10
    }
  },
  created() {
    this.loadData()
    this.loadHotArticles()
  },
  methods: {
    async loadData() {
      try {
        const res = await getArticleList({ page: this.currentPage, size: this.pageSize })
        this.articleList = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error(error)
      }
    },
    async loadHotArticles() {
      try {
        const res = await getArticleList({ page: 1, size: 10 })
        this.hotArticles = res.data.records.sort((a, b) => b.viewCount - a.viewCount).slice(0, 10)
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
    }
  }
}
</script>

<style lang="scss" scoped>
.article-list-page {
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
  
  .article-list {
    .article-item {
      display: flex;
      background: #fff;
      border-radius: 8px;
      margin-bottom: 15px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      .article-cover {
        width: 200px;
        height: 140px;
        flex-shrink: 0;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      
      .article-info {
        flex: 1;
        padding: 15px;
        display: flex;
        flex-direction: column;
        
        h4 {
          margin: 0 0 10px;
          font-size: 18px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .summary {
          flex: 1;
          color: #666;
          font-size: 14px;
          line-height: 1.6;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          margin: 0;
        }
        
        .meta {
          font-size: 13px;
          color: #999;
          margin-top: 10px;
          
          span {
            margin-right: 15px;
          }
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
