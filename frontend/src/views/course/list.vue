<template>
  <div class="course-list-page">
    <div class="page-header">
      <h2>在线课堂</h2>
      <p>律师开设法律公开课，讲解常见法律问题解决方法</p>
    </div>
    
    <div class="container">
      <div class="filter-bar">
        <el-radio-group v-model="isFree" @change="handleSearch">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button :label="1">免费课程</el-radio-button>
          <el-radio-button :label="0">付费课程</el-radio-button>
        </el-radio-group>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="6" v-for="course in courseList" :key="course.id">
          <el-card class="course-card" @click.native="$router.push(`/course/detail/${course.id}`)">
            <div class="course-cover">
              <img :src="course.coverImage || defaultCover" alt="">
              <span class="duration">{{ formatDuration(course.duration) }}</span>
            </div>
            <div class="course-info">
              <h4>{{ course.title }}</h4>
              <p class="teacher">{{ course.teacherName }}</p>
              <div class="course-footer">
                <span class="price" v-if="course.isFree === 0">¥{{ course.price }}</span>
                <span class="price free" v-else>免费</span>
                <span class="students">{{ course.studentCount }}人学习</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
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
import { getCourseList } from '@/api/course'

export default {
  name: 'CourseList',
  data() {
    return {
      defaultCover: 'https://images.unsplash.com/photo-1505664194779-8beaceb93744?w=400',
      isFree: '',
      courseList: [],
      total: 0,
      currentPage: 1,
      pageSize: 12
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize
        }
        if (this.isFree !== '') {
          params.isFree = this.isFree
        }
        const res = await getCourseList(params)
        this.courseList = res.data.records
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
    formatDuration(minutes) {
      if (!minutes) return '00:00'
      const h = Math.floor(minutes / 60)
      const m = minutes % 60
      return h > 0 ? `${h}:${m.toString().padStart(2, '0')}` : `${m}:00`
    }
  }
}
</script>

<style lang="scss" scoped>
.course-list-page {
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
  
  .filter-bar {
    margin-bottom: 20px;
  }
  
  .course-card {
    margin-bottom: 20px;
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    .course-cover {
      position: relative;
      height: 140px;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .duration {
        position: absolute;
        bottom: 10px;
        right: 10px;
        background: rgba(0, 0, 0, 0.7);
        color: #fff;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
      }
    }
    
    .course-info {
      padding: 15px 0 0;
      
      h4 {
        margin: 0 0 10px;
        font-size: 16px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .teacher {
        color: #999;
        font-size: 13px;
        margin: 0 0 10px;
      }
      
      .course-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .price {
          font-size: 18px;
          color: #f56c6c;
          font-weight: 600;
          
          &.free {
            color: #52c41a;
          }
        }
        
        .students {
          color: #999;
          font-size: 13px;
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
