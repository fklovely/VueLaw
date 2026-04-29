<template>
  <div class="course-detail-page">
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card>
            <div class="course-header">
              <h1>{{ course.title }}</h1>
              <div class="course-meta">
                <span><i class="el-icon-user"></i> {{ course.teacherName }}</span>
                <span><i class="el-icon-time"></i> {{ course.duration }}分钟</span>
                <span><i class="el-icon-s-custom"></i> {{ course.studentCount }}人学习</span>
              </div>
            </div>
            <div class="course-video">
              <video 
                v-if="currentVideo" 
                ref="videoPlayer"
                :src="currentVideo.videoUrl" 
                controls 
                controlsList="nodownload"
                :poster="currentVideo.coverImage"
                class="video-player"
              ></video>
              <div v-else class="video-placeholder">
                <i class="el-icon-video-play"></i>
                <p>请选择课程章节播放</p>
              </div>
            </div>
            <div class="video-info" v-if="currentVideo">
              <h3>{{ currentVideo.title }}</h3>
            </div>
            <div class="course-desc">
              <h3>课程简介</h3>
              <p>{{ course.description }}</p>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="mb-20">
            <div class="price-info">
              <p class="price" v-if="course.isFree === 0">¥{{ course.price }}</p>
              <p class="price free" v-else>免费</p>
              <el-button type="primary" size="large" block @click="handleStudy">立即学习</el-button>
            </div>
          </el-card>
          
          <el-card>
            <div slot="header">课程目录</div>
            <div class="chapter-list">
              <div 
                class="chapter-item" 
                v-for="(chapter, index) in chapters" 
                :key="chapter.id"
                :class="{ active: currentChapterIndex === index }"
                @click="handlePlayChapter(index)"
              >
                <span class="chapter-no">{{ index + 1 }}</span>
                <span class="chapter-title">{{ chapter.title }}</span>
                <el-tag size="mini" v-if="chapter.isFree" type="success">免费</el-tag>
                <el-tag size="mini" v-else-if="!isPurchased" type="warning">付费</el-tag>
                <span class="duration">{{ formatDuration(chapter.duration) }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <el-dialog title="提示" :visible.sync="payDialogVisible" width="400px">
      <p>该课程需要付费购买后才能观看完整内容</p>
      <p class="pay-price">课程价格：<span class="price">¥{{ course.price }}</span></p>
      <div slot="footer">
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePay">立即购买</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCourseDetail } from '@/api/course'
import { getChapterList } from '@/api/chapter'

export default {
  name: 'CourseDetail',
  data() {
    return {
      course: {},
      chapters: [],
      currentChapterIndex: -1,
      currentVideo: null,
      isPurchased: false,
      payDialogVisible: false
    }
  },
  computed: {
    isLoggedIn() {
      return this.$store.getters.isLoggedIn
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const courseId = this.$route.params.id
        const res = await getCourseDetail(courseId)
        this.course = res.data || {}
        const chapterRes = await getChapterList(courseId)
        this.chapters = chapterRes.data || []
      } catch (error) {
        console.error(error)
      }
    },
    handleStudy() {
      if (this.chapters.length > 0) {
        this.handlePlayChapter(0)
      }
    },
    handlePlayChapter(index) {
      const chapter = this.chapters[index]
      if (!chapter.isFree && !this.isPurchased) {
        this.payDialogVisible = true
        return
      }
      if (!chapter.videoUrl) {
        this.$message.warning('该章节视频暂未上传')
        return
      }
      this.currentChapterIndex = index
      this.currentVideo = chapter
      this.$nextTick(() => {
        if (this.$refs.videoPlayer) {
          this.$refs.videoPlayer.play()
        }
      })
    },
    handlePay() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.$message.success('购买成功')
      this.isPurchased = true
      this.payDialogVisible = false
    },
    formatDuration(seconds) {
      if (!seconds) return '00:00'
      const minutes = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    }
  }
}
</script>

<style lang="scss" scoped>
.course-detail-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .course-header {
    margin-bottom: 20px;
    
    h1 {
      font-size: 24px;
      margin: 0 0 10px;
    }
    
    .course-meta {
      font-size: 14px;
      color: #999;
      
      span {
        margin-right: 20px;
      }
    }
  }
  
  .course-video {
    background: #000;
    border-radius: 8px;
    margin-bottom: 20px;
    overflow: hidden;
    
    .video-player {
      width: 100%;
      height: 400px;
      display: block;
    }
    
    .video-placeholder {
      height: 400px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #fff;
      
      i {
        font-size: 60px;
        margin-bottom: 10px;
        cursor: pointer;
        
        &:hover {
          color: #1890ff;
        }
      }
    }
  }
  
  .video-info {
    margin-bottom: 20px;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 8px;
    
    h3 {
      font-size: 18px;
      margin: 0 0 10px;
    }
    
    p {
      color: #666;
      margin: 0;
      line-height: 1.6;
    }
  }
  
  .course-desc {
    h3 {
      font-size: 18px;
      margin: 0 0 10px;
    }
    
    p {
      color: #666;
      line-height: 1.8;
      margin: 0;
    }
  }
  
  .price-info {
    text-align: center;
    
    .price {
      font-size: 32px;
      color: #f56c6c;
      font-weight: 600;
      margin: 0 0 20px;
      
      &.free {
        color: #52c41a;
      }
    }
  }
  
  .chapter-list {
    .chapter-item {
      display: flex;
      align-items: center;
      padding: 12px 10px;
      border-bottom: 1px solid #eee;
      cursor: pointer;
      transition: all 0.3s;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background: #f5f7fa;
      }
      
      &.active {
        background: #e6f7ff;
        color: #1890ff;
      }
      
      .chapter-no {
        width: 30px;
        color: #999;
        font-weight: 500;
      }
      
      .chapter-title {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .duration {
        font-size: 12px;
        color: #999;
        margin-left: 10px;
      }
    }
  }
  
  .pay-price {
    margin-top: 15px;
    
    .price {
      color: #f56c6c;
      font-size: 24px;
      font-weight: 600;
    }
  }
}
</style>
