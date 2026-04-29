<template>
  <div class="user-center">
    <div class="user-header">
      <div class="user-info">
        <el-avatar :size="80" :src="userInfo.avatar || defaultAvatar"></el-avatar>
        <div class="user-detail">
          <h2>{{ userInfo.realName || userInfo.username }}</h2>
          <p>{{ getRoleLabel(userInfo.role) }}</p>
        </div>
      </div>
      <el-menu mode="horizontal" :default-active="activeMenu" router>
        <el-menu-item index="/user/profile">个人信息</el-menu-item>
        <el-menu-item index="/user/orders">我的订单</el-menu-item>
        <el-menu-item index="/user/wallet">我的钱包</el-menu-item>
        <el-menu-item index="/user/favorite">我的收藏</el-menu-item>
        <el-menu-item index="/user/follow">关注律师</el-menu-item>
      </el-menu>
    </div>
    <div class="user-content">
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserCenter',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  computed: {
    userInfo() {
      return this.$store.getters.userInfo
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    getRoleLabel(role) {
      const labels = { admin: '管理员', lawyer: '律师', client: '客户' }
      return labels[role] || '用户'
    }
  }
}
</script>

<style lang="scss" scoped>
.user-center {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  
  .user-header {
    background: #fff;
    border-radius: 8px;
    padding: 30px;
    margin-bottom: 20px;
    
    .user-info {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      
      .user-detail {
        margin-left: 20px;
        
        h2 {
          margin: 0 0 5px;
          font-size: 24px;
        }
        
        p {
          margin: 0;
          color: #999;
        }
      }
    }
    
    .el-menu {
      border-bottom: none;
    }
  }
  
  .user-content {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    min-height: 400px;
  }
}
</style>
