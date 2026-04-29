<template>
  <div class="follow-page">
    <el-row :gutter="20">
      <el-col :span="6" v-for="lawyer in lawyerList" :key="lawyer.id">
        <el-card class="lawyer-card">
          <div class="lawyer-avatar">
            <el-avatar :size="80" :src="lawyer.avatar || defaultAvatar"></el-avatar>
          </div>
          <h4>{{ lawyer.lawyerName }}</h4>
          <p class="law-firm">{{ lawyer.lawFirm }}</p>
          <p class="expertise">{{ lawyer.expertises }}</p>
          <div class="rating">
            <el-rate :value="lawyer.rating" disabled show-score text-color="#ff9900"></el-rate>
          </div>
          <div class="actions">
            <el-button type="primary" size="small" @click="$router.push(`/consult/create/${lawyer.id}`)">立即咨询</el-button>
            <el-button size="small" @click="handleUnfollow(lawyer.id)">取消关注</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'UserFollow',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      lawyerList: [
        { id: 1, lawyerName: '张明华', lawFirm: '明华律师事务所', expertises: '未成年人监护权,校园欺凌', rating: 4.9, avatar: '' },
        { id: 2, lawyerName: '李雪梅', lawFirm: '雪梅律师事务所', expertises: '家庭财产继承,离婚纠纷', rating: 4.85, avatar: '' }
      ]
    }
  },
  methods: {
    handleUnfollow(id) {
      this.$message.success('取消关注成功')
      this.lawyerList = this.lawyerList.filter(item => item.id !== id)
    }
  }
}
</script>

<style lang="scss" scoped>
.follow-page {
  .lawyer-card {
    text-align: center;
    margin-bottom: 20px;
    
    .lawyer-avatar {
      margin-bottom: 15px;
    }
    
    h4 {
      margin: 0 0 5px;
      font-size: 18px;
    }
    
    .law-firm {
      color: #999;
      font-size: 13px;
      margin: 0 0 5px;
    }
    
    .expertise {
      color: #1890ff;
      font-size: 13px;
      margin: 0 0 10px;
    }
    
    .rating {
      margin-bottom: 15px;
    }
    
    .actions {
      display: flex;
      justify-content: center;
      gap: 10px;
    }
  }
}
</style>
