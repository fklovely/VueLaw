<template>
  <div class="lawyer-list-page">
    <div class="page-header">
      <h2>找律师</h2>
      <p>专业律师团队为您提供优质法律服务</p>
    </div>
    
    <div class="search-box">
      <div class="search-row">
        <el-input v-model="keyword" placeholder="搜索律师姓名、律所" class="search-input" @keyup.enter.native="handleSearch">
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
        <el-button type="primary" icon="el-icon-magic-stick" @click="$router.push('/match')">智能匹配</el-button>
      </div>
      <div class="filter-tags">
        <span class="label">擅长领域：</span>
        <el-tag v-for="item in expertiseOptions" :key="item" :effect="expertise === item ? 'dark' : 'plain'" @click="expertise = item === expertise ? '' : item">{{ item }}</el-tag>
      </div>
    </div>
    
    <div class="lawyer-grid">
      <div class="lawyer-card" v-for="lawyer in lawyerList" :key="lawyer.id" @click="$router.push(`/lawyer/detail/${lawyer.id}`)">
        <div class="card-header">
          <el-avatar :size="80" :src="lawyer.avatar || defaultAvatar"></el-avatar>
          <div class="lawyer-info">
            <h3>{{ lawyer.lawyerName }}</h3>
            <p class="law-firm">{{ lawyer.lawFirm }}</p>
            <p class="location"><i class="el-icon-location"></i> {{ lawyer.province }} {{ lawyer.city }}</p>
          </div>
        </div>
        <div class="card-body">
          <div class="expertise-tags">
            <el-tag v-for="(exp, index) in getExpertises(lawyer.expertises)" :key="index" size="small" type="info">{{ exp }}</el-tag>
          </div>
          <p class="profile">{{ lawyer.profile }}</p>
        </div>
        <div class="card-footer">
          <div class="stats">
            <span><i class="el-icon-star-on"></i> {{ lawyer.rating }}分</span>
            <span><i class="el-icon-s-custom"></i> {{ lawyer.serviceCount }}次服务</span>
          </div>
          <div class="price">
            <span class="label">咨询</span>
            <span class="amount">¥{{ lawyer.consultPrice }}</span>
            <span class="unit">起</span>
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
  </div>
</template>

<script>
import { getLawyerList } from '@/api/lawyer'

export default {
  name: 'LawyerList',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      keyword: this.$route.query.keyword || '',
      expertise: this.$route.query.expertise || '',
      expertiseOptions: ['未成年人监护权', '校园欺凌', '家庭暴力', '离婚纠纷', '财产继承', '刑事辩护'],
      lawyerList: [],
      total: 0,
      currentPage: 1,
      pageSize: 12,
      syncingQuery: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getLawyerList({
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.keyword,
          expertise: this.expertise,
          status: 1
        })
        this.lawyerList = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error(error)
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
    getExpertises(str) {
      if (!str) return []
      return str.split(',').slice(0, 3)
    }
  },
  watch: {
    expertise() {
      if (!this.syncingQuery) {
        this.handleSearch()
      }
    },
    '$route.query'(query) {
      const nextKeyword = query.keyword || ''
      const nextExpertise = query.expertise || ''
      if (nextKeyword !== this.keyword || nextExpertise !== this.expertise) {
        this.syncingQuery = true
        this.keyword = nextKeyword
        this.expertise = nextExpertise
        this.syncingQuery = false
        this.handleSearch()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.lawyer-list-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  
  .page-header {
    text-align: center;
    padding: 30px 0;
    
    h2 {
      font-size: 28px;
      margin: 0 0 10px;
    }
    
    p {
      color: #999;
      margin: 0;
    }
  }
  
  .search-box {
    max-width: 800px;
    margin: 0 auto 30px;
    
    .search-row {
      display: flex;
      gap: 12px;
      margin-bottom: 15px;
      
      .el-button {
        flex-shrink: 0;
      }
    }
    
    .search-input {
      flex: 1;
    }
    
    .filter-tags {
      .label {
        margin-right: 10px;
        color: #666;
      }
      
      .el-tag {
        margin-right: 10px;
        cursor: pointer;
      }
    }
  }
  
  .lawyer-grid {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }
  
  .lawyer-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      transform: translateY(-3px);
    }
    
    .card-header {
      display: flex;
      margin-bottom: 15px;
      
      .lawyer-info {
        margin-left: 15px;
        
        h3 {
          margin: 0 0 5px;
          font-size: 18px;
        }
        
        .law-firm {
          color: #666;
          font-size: 13px;
          margin: 0 0 5px;
        }
        
        .location {
          color: #999;
          font-size: 12px;
          margin: 0;
        }
      }
    }
    
    .card-body {
      .expertise-tags {
        margin-bottom: 10px;
        
        .el-tag {
          margin-right: 5px;
          margin-bottom: 5px;
        }
      }
      
      .profile {
        color: #666;
        font-size: 13px;
        line-height: 1.6;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        margin: 0;
      }
    }
    
    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px solid #eee;
      
      .stats {
        font-size: 13px;
        color: #999;
        
        span {
          margin-right: 15px;
          
          i {
            color: #f7ba2a;
          }
        }
      }
      
      .price {
        .label {
          font-size: 12px;
          color: #999;
        }
        
        .amount {
          font-size: 20px;
          color: #f56c6c;
          font-weight: 600;
        }
        
        .unit {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
  
  .pagination {
    text-align: center;
    margin-top: 30px;
  }
}

@media (max-width: 960px) {
  .lawyer-list-page {
    .lawyer-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 640px) {
  .lawyer-list-page {
    .search-box {
      .search-row {
        flex-direction: column;
      }
    }

    .lawyer-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
