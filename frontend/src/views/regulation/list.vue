<template>
  <div class="regulation-list-page">
    <div class="page-header">
      <h2>法规查询</h2>
      <p>最新法律法规，条文解读</p>
    </div>
    
    <div class="container">
      <div class="search-box">
        <el-form inline>
          <el-form-item>
            <el-input v-model="searchForm.keyword" placeholder="法规标题" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-select v-model="searchForm.regType" placeholder="法规类型" clearable>
              <el-option label="法律" value="法律"></el-option>
              <el-option label="行政法规" value="行政法规"></el-option>
              <el-option label="司法解释" value="司法解释"></el-option>
              <el-option label="部门规章" value="部门规章"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="regulation-list">
        <div class="regulation-item" v-for="item in regulationList" :key="item.id" @click="$router.push(`/regulation/detail/${item.id}`)">
          <h4>{{ item.title }}</h4>
          <div class="reg-meta">
            <span><i class="el-icon-document"></i> {{ item.regType }}</span>
            <span><i class="el-icon-office-building"></i> {{ item.issueOrg }}</span>
            <span><i class="el-icon-date"></i> {{ item.issueDate }}</span>
          </div>
          <p class="reg-summary">{{ item.summary }}</p>
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
import { getRegulationList } from '@/api/regulation'

export default {
  name: 'RegulationList',
  data() {
    return {
      searchForm: {
        keyword: '',
        regType: ''
      },
      regulationList: [],
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
        const res = await getRegulationList({
          page: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.regulationList = res.data.records
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
    }
  }
}
</script>

<style lang="scss" scoped>
.regulation-list-page {
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
  
  .regulation-list {
    .regulation-item {
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
      
      .reg-meta {
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
      
      .reg-summary {
        color: #666;
        font-size: 14px;
        line-height: 1.6;
        margin: 0;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }
  }
  
  .pagination {
    text-align: center;
    margin-top: 20px;
  }
}
</style>
