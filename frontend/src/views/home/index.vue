<template>
  <div class="home-page">
    <div class="banner-section">
      <el-carousel height="400px">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
            <div class="banner-content">
              <h2>{{ item.title }}</h2>
              <p>{{ item.desc }}</p>
              <el-button type="primary" size="large" @click="$router.push(item.link)">立即咨询</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    
    <div class="service-section">
      <div class="section-title">
        <h3>我们的服务</h3>
        <p>专业、便捷、贴心的法律咨询服务</p>
      </div>
      <div class="service-list">
        <div class="service-item" v-for="service in services" :key="service.id" @click="$router.push(service.link)">
          <div class="service-icon">
            <i :class="service.icon"></i>
          </div>
          <h4>{{ service.title }}</h4>
          <p>{{ service.desc }}</p>
        </div>
      </div>
    </div>
    
    <div class="lawyer-section">
      <div class="section-title">
        <h3>推荐律师</h3>
        <p>专业律师团队为您保驾护航</p>
      </div>
      <div class="lawyer-list">
        <div class="lawyer-card" v-for="lawyer in lawyers" :key="lawyer.id" @click="$router.push(`/lawyer/detail/${lawyer.id}`)">
          <el-avatar :size="80" :src="lawyer.avatar || defaultAvatar"></el-avatar>
          <h4>{{ lawyer.lawyerName }}</h4>
          <p class="law-firm">{{ lawyer.lawFirm }}</p>
          <p class="expertise">{{ lawyer.expertises }}</p>
          <div class="rating">
            <el-rate :value="lawyer.rating" disabled show-score text-color="#ff9900"></el-rate>
          </div>
          <el-button type="primary" size="small" @click.stop="$router.push(`/consult/create/${lawyer.id}`)">立即咨询</el-button>
        </div>
      </div>
      <div class="view-more">
        <el-button type="text" @click="$router.push('/lawyer')">查看更多律师 <i class="el-icon-arrow-right"></i></el-button>
      </div>
    </div>
    
    <div class="article-section">
      <div class="section-title">
        <h3>普法文章</h3>
        <p>了解法律知识，维护自身权益</p>
      </div>
      <div class="article-list">
        <div class="article-card" v-for="article in articles" :key="article.id" @click="$router.push(`/article/detail/${article.id}`)">
          <div class="article-cover" v-if="article.coverImage">
            <img :src="article.coverImage" alt="">
          </div>
          <div class="article-info">
            <h4>{{ article.title }}</h4>
            <p class="summary">{{ article.summary }}</p>
            <div class="meta">
              <span>{{ article.authorName }}</span>
              <span>{{ article.publishTime }}</span>
              <span><i class="el-icon-view"></i> {{ article.viewCount }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="view-more">
        <el-button type="text" @click="$router.push('/article')">查看更多文章 <i class="el-icon-arrow-right"></i></el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getLawyerList } from '@/api/lawyer'
import { getArticleList } from '@/api/article'

export default {
  name: 'Home',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      banners: [
        { id: 1, title: '专业法律咨询服务', desc: '为未成年人提供全方位的法律保护', image: 'https://images.unsplash.com/photo-1589829545856-d10d557cf95f?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80', link: '/lawyer' },
        { id: 2, title: '在线法律课堂', desc: '学习法律知识，提升法律意识', image: 'https://images.unsplash.com/photo-1505664194779-8beaceb93744?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80', link: '/course' },
        { id: 3, title: '法律文书服务', desc: '专业文书定制，保障您的权益', image: 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80', link: '/document' }
      ],
      services: [
        { id: 1, title: '需求智能匹配', desc: '描述问题，推荐对应领域律师', icon: 'el-icon-magic-stick', link: '/match' },
        { id: 2, title: '法律咨询', desc: '图文咨询，专业律师在线解答', icon: 'el-icon-chat-dot-round', link: '/lawyer' },
        { id: 3, title: '法律文书', desc: '文书模板下载、定制代写、审核校对', icon: 'el-icon-document', link: '/document' },
        { id: 4, title: '案例检索', desc: '海量案例数据库，快速检索参考', icon: 'el-icon-search', link: '/case' },
        { id: 5, title: '法规查询', desc: '最新法律法规，条文解读', icon: 'el-icon-reading', link: '/regulation' }
      ],
      lawyers: [],
      articles: []
    }
  },
  created() {
    this.loadLawyers()
    this.loadArticles()
  },
  methods: {
    async loadLawyers() {
      try {
        const res = await getLawyerList({ page: 1, size: 4, status: 1 })
        this.lawyers = res.data.records
      } catch (error) {
        console.error(error)
      }
    },
    async loadArticles() {
      try {
        const res = await getArticleList({ page: 1, size: 4 })
        this.articles = res.data.records
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  background: #f5f7fa;
}

.banner-section {
  .banner-item {
    height: 400px;
    background-size: cover;
    background-position: center;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.4);
    }
    
    .banner-content {
      position: relative;
      z-index: 1;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #fff;
      text-align: center;
      
      h2 {
        font-size: 36px;
        margin-bottom: 15px;
      }
      
      p {
        font-size: 18px;
        margin-bottom: 30px;
        opacity: 0.9;
      }
    }
  }
}

.section-title {
  text-align: center;
  padding: 40px 0 30px;
  
  h3 {
    font-size: 28px;
    color: #333;
    margin-bottom: 10px;
  }
  
  p {
    color: #999;
    font-size: 16px;
  }
}

.service-section {
  background: #fff;
  
  .service-list {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 40px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(190px, 1fr));
    gap: 20px;
  }
  
  .service-item {
    background: #f8f9fa;
    padding: 30px;
    border-radius: 8px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      background: #1890ff;
      color: #fff;
      transform: translateY(-5px);
      
      .service-icon {
        background: rgba(255, 255, 255, 0.2);
      }
    }
    
    .service-icon {
      width: 70px;
      height: 70px;
      background: #e6f7ff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      
      i {
        font-size: 32px;
        color: #1890ff;
      }
    }
    
    h4 {
      font-size: 18px;
      margin-bottom: 10px;
    }
    
    p {
      font-size: 14px;
      color: #666;
    }
  }
}

.lawyer-section {
  background: #fff;
  margin-top: 20px;
  
  .lawyer-list {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }
  
  .lawyer-card {
    background: #fff;
    border: 1px solid #eee;
    border-radius: 8px;
    padding: 25px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      border-color: #1890ff;
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
    }
    
    h4 {
      margin: 15px 0 5px;
      font-size: 18px;
    }
    
    .law-firm {
      color: #999;
      font-size: 13px;
      margin-bottom: 5px;
    }
    
    .expertise {
      color: #1890ff;
      font-size: 13px;
      margin-bottom: 10px;
    }
    
    .rating {
      margin-bottom: 15px;
    }
  }
}

.view-more {
  text-align: center;
  padding: 20px 0 40px;
}

.article-section {
  background: #fff;
  margin-top: 20px;
  
  .article-list {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  
  .article-card {
    display: flex;
    background: #fff;
    border: 1px solid #eee;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      border-color: #1890ff;
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
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
        font-size: 16px;
        margin-bottom: 10px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .summary {
        flex: 1;
        font-size: 14px;
        color: #666;
        line-height: 1.6;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      
      .meta {
        font-size: 12px;
        color: #999;
        margin-top: 10px;
        
        span {
          margin-right: 15px;
        }
      }
    }
  }
}
</style>
