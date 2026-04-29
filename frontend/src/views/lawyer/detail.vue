<template>
  <div class="lawyer-detail-page">
    <div class="detail-header">
      <div class="container">
        <div class="lawyer-info">
          <el-avatar :size="120" :src="lawyer.avatar || defaultAvatar"></el-avatar>
          <div class="info-content">
            <h1>{{ lawyer.lawyerName }}</h1>
            <p class="law-firm">{{ lawyer.lawFirm }}</p>
            <div class="tags">
              <el-tag v-for="(exp, index) in getExpertises(lawyer.expertises)" :key="index" size="small">{{ exp }}</el-tag>
            </div>
            <div class="stats">
              <span><i class="el-icon-star-on"></i> {{ lawyer.rating }}分</span>
              <span><i class="el-icon-s-custom"></i> {{ lawyer.serviceCount }}次服务</span>
              <span><i class="el-icon-location"></i> {{ lawyer.province }} {{ lawyer.city }}</span>
            </div>
          </div>
          <div class="action-area">
            <el-button type="primary" size="large" @click="$router.push(`/consult/create/${lawyer.id}`)">立即咨询</el-button>
            <el-button size="large" @click="handleFollow">{{ isFollowed ? '已关注' : '关注律师' }}</el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="detail-content container">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="mb-20">
            <div slot="header">个人简介</div>
            <p>{{ lawyer.profile || '暂无简介' }}</p>
          </el-card>
          
          <el-card class="mb-20">
            <div slot="header">服务项目</div>
            <div class="service-list">
              <div class="service-item">
                <span class="name">图文咨询</span>
                <span class="price">¥{{ lawyer.consultPrice }}</span>
              </div>
              <div class="service-item">
                <span class="name">深度咨询</span>
                <span class="price">¥{{ lawyer.deepConsultPrice }}</span>
              </div>
              <div class="service-item">
                <span class="name">文书代写</span>
                <span class="price">¥{{ lawyer.documentPrice }}</span>
              </div>
              <div class="service-item">
                <span class="name">文书审核</span>
                <span class="price">¥{{ lawyer.auditPrice }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="mb-20">
            <div slot="header">基本信息</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="执业证号">{{ lawyer.lawyerNo }}</el-descriptions-item>
              <el-descriptions-item label="学历">{{ lawyer.education }}</el-descriptions-item>
              <el-descriptions-item label="职称">{{ lawyer.professionalTitle }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ lawyer.phone }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ lawyer.email }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getLawyerDetail } from '@/api/lawyer'

export default {
  name: 'LawyerDetail',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      lawyer: {},
      isFollowed: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getLawyerDetail(this.$route.params.id)
        this.lawyer = res.data
      } catch (error) {
        console.error(error)
      }
    },
    getExpertises(str) {
      if (!str) return []
      return str.split(',')
    },
    handleFollow() {
      this.isFollowed = !this.isFollowed
      this.$message.success(this.isFollowed ? '关注成功' : '取消关注成功')
    }
  }
}
</script>

<style lang="scss" scoped>
.lawyer-detail-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
  
  .detail-header {
    background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
    padding: 40px 0;
    
    .lawyer-info {
      display: flex;
      align-items: center;
      
      .info-content {
        flex: 1;
        margin-left: 30px;
        color: #fff;
        
        h1 {
          margin: 0 0 10px;
          font-size: 28px;
        }
        
        .law-firm {
          margin: 0 0 15px;
          opacity: 0.9;
        }
        
        .tags {
          margin-bottom: 15px;
          
          .el-tag {
            margin-right: 10px;
          }
        }
        
        .stats {
          font-size: 14px;
          opacity: 0.9;
          
          span {
            margin-right: 20px;
            
            i {
              margin-right: 5px;
            }
          }
        }
      }
      
      .action-area {
        .el-button {
          margin-left: 10px;
        }
      }
    }
  }
  
  .detail-content {
    margin-top: 20px;
    padding-bottom: 40px;
    
    .service-list {
      .service-item {
        display: flex;
        justify-content: space-between;
        padding: 15px 0;
        border-bottom: 1px solid #eee;
        
        &:last-child {
          border-bottom: none;
        }
        
        .name {
          font-size: 15px;
        }
        
        .price {
          font-size: 18px;
          color: #f56c6c;
          font-weight: 600;
        }
      }
    }
  }
}
</style>
