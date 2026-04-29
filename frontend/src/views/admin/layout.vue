<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px">
        <div class="logo">
          <i class="el-icon-s-platform"></i>
          <span>管理后台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#1a1a2e"
          text-color="#fff"
          active-text-color="#1890ff"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <i class="el-icon-s-data"></i>
            <span>数据看板</span>
          </el-menu-item>
          <el-menu-item index="/admin/user">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/lawyer">
            <i class="el-icon-s-custom"></i>
            <span>律师管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/consult">
            <i class="el-icon-chat-dot-round"></i>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/document">
            <i class="el-icon-document"></i>
            <span>文书管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/case">
            <i class="el-icon-s-order"></i>
            <span>案例管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/regulation">
            <i class="el-icon-reading"></i>
            <span>法规管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/article">
            <i class="el-icon-document-copy"></i>
            <span>文章管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/course">
            <i class="el-icon-video-camera"></i>
            <span>课程管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/question">
            <i class="el-icon-question"></i>
            <span>问答管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/notice">
            <i class="el-icon-bell"></i>
            <span>公告管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/category">
            <i class="el-icon-menu"></i>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/config">
            <i class="el-icon-setting"></i>
            <span>系统配置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userInfo.avatar || defaultAvatar"></el-avatar>
                <span>{{ userInfo.realName || userInfo.username }}</span>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="home">前台首页</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'AdminLayout',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    },
    currentTitle() {
      return this.$route.meta.title || ''
    },
    userInfo() {
      return this.$store.getters.userInfo
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.$store.dispatch('logout')
        this.$router.push('/login')
        this.$message.success('退出成功')
      } else if (command === 'home') {
        this.$router.push('/home')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
  
  .el-aside {
    background: #1a1a2e;
    height: 100%;
    overflow: hidden;
  }
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: 600;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    i {
      font-size: 24px;
      margin-right: 10px;
      color: #1890ff;
    }
  }
  
  .sidebar-menu {
    border-right: none;
    
    .el-menu-item {
      height: 50px;
      line-height: 50px;
      
      i {
        margin-right: 10px;
      }
      
      &.is-active {
        background: linear-gradient(90deg, #1890ff, transparent) !important;
      }
    }
  }
  
  .el-header {
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      span {
        margin-left: 8px;
        color: #333;
      }
    }
  }
  
  .el-main {
    background: #f5f7fa;
    padding: 20px;
  }
}
</style>
