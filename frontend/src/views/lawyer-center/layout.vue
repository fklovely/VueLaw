<template>
  <div class="lawyer-center-layout">
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/lawyer-center/dashboard')">
          <i class="el-icon-s-custom"></i>
          <span>律师工作台</span>
        </div>
        <nav class="nav-menu">
          <el-dropdown @command="handleNavCommand">
            <span class="nav-item" :class="{ active: isMenuActive('dashboard') }">
              工作台 <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/lawyer-center/dashboard">工作台首页</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/evaluate">服务评价</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-dropdown @command="handleNavCommand">
            <span class="nav-item" :class="{ active: isMenuActive('service') }">
              服务提供 <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/lawyer-center/consult">咨询订单处理</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/document-service">文书服务接单</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/service-record">我的服务记录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-dropdown @command="handleNavCommand">
            <span class="nav-item" :class="{ active: isMenuActive('profile') }">
              个人资料与业务 <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/lawyer-center/profile">资料维护</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/expertise">专业领域设置</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/pricing">服务定价</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-dropdown @command="handleNavCommand">
            <span class="nav-item" :class="{ active: isMenuActive('order') }">
              订单管理 <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/lawyer-center/orders">我的订单</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/progress">进度更新</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-dropdown @command="handleNavCommand">
            <span class="nav-item" :class="{ active: isMenuActive('knowledge') }">
              知识分享 <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/lawyer-center/article">文章发布</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/course">课程管理</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/qa">社区互动</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-dropdown @command="handleNavCommand">
            <span class="nav-item" :class="{ active: isMenuActive('income') }">
              数据与收益 <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/lawyer-center/evaluate">服务评价查看</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/income">收益明细</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-dropdown @command="handleNavCommand">
            <span class="nav-item" :class="{ active: isMenuActive('setting') }">
              个人设置 <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/lawyer-center/security">账户安全</el-dropdown-item>
              <el-dropdown-item command="/lawyer-center/privacy">隐私设置</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </nav>
        <div class="user-area">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar || defaultAvatar"></el-avatar>
              <span class="username">{{ userInfo.realName || userInfo.username }}</span>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="home">前台首页</el-dropdown-item>
              <el-dropdown-item command="profile">个人资料</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </header>
    
    <main class="main-content">
      <router-view></router-view>
    </main>
    
    <footer class="footer">
      <div class="footer-content">
        <p>Copyright © 2026 未成年人保护法律服务平台 - 律师工作台</p>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'LawyerCenterLayout',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  computed: {
    userInfo() {
      return this.$store.getters.userInfo
    }
  },
  methods: {
    isMenuActive(menu) {
      const path = this.$route.path
      const menuMap = {
        'dashboard': ['/lawyer-center/dashboard', '/lawyer-center/evaluate'],
        'service': ['/lawyer-center/consult', '/lawyer-center/document-service', '/lawyer-center/service-record'],
        'profile': ['/lawyer-center/profile', '/lawyer-center/expertise', '/lawyer-center/pricing'],
        'order': ['/lawyer-center/orders', '/lawyer-center/progress'],
        'knowledge': ['/lawyer-center/article', '/lawyer-center/course', '/lawyer-center/qa'],
        'income': ['/lawyer-center/income', '/lawyer-center/evaluate'],
        'setting': ['/lawyer-center/security', '/lawyer-center/privacy']
      }
      return menuMap[menu]?.some(p => path.startsWith(p)) || false
    },
    handleNavCommand(path) {
      this.$router.push(path)
    },
    handleUserCommand(command) {
      if (command === 'logout') {
        this.$store.dispatch('logout')
        this.$router.push('/home')
        this.$message.success('退出成功')
      } else if (command === 'home') {
        this.$router.push('/home')
      } else if (command === 'profile') {
        this.$router.push('/lawyer-center/profile')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.lawyer-center-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  
  .header-content {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .logo {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    i {
      font-size: 28px;
      color: #1890ff;
      margin-right: 10px;
    }
    
    span {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      white-space: nowrap;
    }
  }
  
  .nav-menu {
    display: flex;
    align-items: center;
    
    .nav-item {
      padding: 0 15px;
      color: #666;
      font-size: 14px;
      cursor: pointer;
      transition: color 0.3s;
      white-space: nowrap;
      
      &:hover, &.active {
        color: #1890ff;
      }
    }
  }
  
  .user-area {
    display: flex;
    align-items: center;
    
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      .username {
        margin-left: 8px;
        color: #333;
      }
    }
  }
}

.main-content {
  flex: 1;
  background: #f5f7fa;
  padding: 20px;
}

.footer {
  background: #1a1a2e;
  color: #fff;
  padding: 20px;
  
  .footer-content {
    max-width: 1400px;
    margin: 0 auto;
    text-align: center;
    
    p {
      margin: 0;
      color: #ccc;
      font-size: 13px;
    }
  }
}
</style>
