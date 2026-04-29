<template>
  <div class="case-list-page">
    <div class="page-header">
      <h2>案例检索</h2>
      <p>海量案例数据库，快速检索参考</p>
    </div>
    
    <div class="container">
      <div class="search-box">
        <el-form inline>
          <el-form-item>
            <el-input v-model="searchForm.keyword" placeholder="案例标题" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="searchForm.caseType" placeholder="案件类型" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="searchForm.province" placeholder="省份" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="case-list">
        <div class="case-item" v-for="item in caseList" :key="item.id" @click="$router.push(`/case/detail/${item.id}`)">
          <h4>{{ item.title }}</h4>
          <div class="case-meta">
            <span><i class="el-icon-office-building"></i> {{ item.courtName }}</span>
            <span><i class="el-icon-document"></i> {{ item.caseType }}</span>
            <span><i class="el-icon-location"></i> {{ item.province }}</span>
            <span><i class="el-icon-date"></i> {{ item.judgeDate }}</span>
          </div>
          <p class="case-summary">{{ item.summary }}</p>
          <div class="case-tags">
            <el-tag size="small" v-for="(tag, index) in getTags(item.tags)" :key="index">{{ tag }}</el-tag>
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
  </div>
</template>

<script>
import { getCaseList } from '@/api/case'

export default {
  name: 'CaseList',
  data() {
    return {
      searchForm: {
        keyword: '',
        caseType: '',
        province: ''
      },
      caseList: [],
      total: 0,
      currentPage: 1,
      pageSize: 10
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getCaseList({
          page: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.caseList = res.data.records
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
    getTags(str) {
      if (!str) return []
      return str.split(',').slice(0, 3)
    }
  }
}
</script>

<style lang="scss" scoped>
.case-list-page {
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
  
  .search-box {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
  
  .case-list {
    .case-item {
      background: #fff;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 15px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      h4 {
        margin: 0 0 10px;
        font-size: 18px;
        color: #333;
      }
      
      .case-meta {
        font-size: 13px;
        color: #999;
        margin-bottom: 10px;
        
        span {
          margin-right: 20px;
          
          i {
            margin-right: 5px;
          }
        }
      }
      
      .case-summary {
        color: #666;
        font-size: 14px;
        line-height: 1.6;
        margin: 0 0 10px;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      
      .case-tags {
        .el-tag {
          margin-right: 8px;
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
