<template>
  <div class="front-layout">
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/home')">
          <i class="el-icon-s-platform"></i>
          <span>未成年人保护法律服务平台</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item" :class="{ active: $route.path === '/home' }">首页</router-link>
          <router-link to="/lawyer" class="nav-item" :class="{ active: $route.path.startsWith('/lawyer') }">找律师</router-link>
          <router-link to="/match" class="nav-item" :class="{ active: $route.path.startsWith('/match') }">智能匹配</router-link>
          <router-link to="/consult" class="nav-item" :class="{ active: $route.path.startsWith('/consult') }">法律咨询</router-link>
          <router-link to="/document" class="nav-item" :class="{ active: $route.path === '/document' }">法律文书</router-link>
          <router-link to="/case" class="nav-item" :class="{ active: $route.path.startsWith('/case') }">案例检索</router-link>
          <router-link to="/regulation" class="nav-item" :class="{ active: $route.path.startsWith('/regulation') }">法规查询</router-link>
          <router-link to="/article" class="nav-item" :class="{ active: $route.path.startsWith('/article') }">普法文章</router-link>
          <router-link to="/course" class="nav-item" :class="{ active: $route.path.startsWith('/course') }">在线课堂</router-link>
          <router-link to="/question" class="nav-item" :class="{ active: $route.path.startsWith('/question') }">互动问答</router-link>
        </nav>
        <div class="user-area">
          <template v-if="isLoggedIn">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="chat-badge">
              <el-button type="text" icon="el-icon-chat-dot-round" @click="$router.push('/chat')">消息</el-button>
            </el-badge>
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userInfo.avatar || defaultAvatar"></el-avatar>
                <span class="username">{{ userInfo.realName || userInfo.username }}</span>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="chat">我的消息</el-dropdown-item>
                <el-dropdown-item command="consult" v-if="role === 'client'">我的咨询</el-dropdown-item>
                <el-dropdown-item command="lawyer-center" v-if="role === 'lawyer'">律师中心</el-dropdown-item>
                <el-dropdown-item command="admin" v-if="role === 'admin'">后台管理</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" size="small" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    
    <main class="main-content">
      <router-view></router-view>
    </main>
    
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-info">
          <p>未成年人保护在线法律咨询服务平台</p>
          <p>专业、便捷、贴心的法律咨询服务</p>
        </div>
        <div class="footer-links">
          <router-link to="/home">首页</router-link>
          <router-link to="/lawyer">找律师</router-link>
          <router-link to="/match">智能匹配</router-link>
          <router-link to="/case">案例检索</router-link>
          <router-link to="/regulation">法规查询</router-link>
          <router-link to="/article">普法文章</router-link>
          <router-link to="/course">在线课堂</router-link>
          <router-link to="/question">互动问答</router-link>
        </div>
        <div class="copyright">
          <p>Copyright © 2026 未成年人保护法律服务平台 All Rights Reserved</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { getUnreadCount } from '@/api/chat'

export default {
  name: 'FrontLayout',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      unreadCount: 0
    }
  },
  computed: {
    isLoggedIn() {
      return this.$store.getters.isLoggedIn
    },
    userInfo() {
      return this.$store.getters.userInfo
    },
    role() {
      return this.$store.getters.role
    }
  },
  watch: {
    isLoggedIn(val) {
      if (val) {
        this.loadUnreadCount()
      }
    }
  },
  created() {
    if (this.isLoggedIn) {
      this.loadUnreadCount()
    }
  },
  methods: {
    async loadUnreadCount() {
      try {
        const userId = this.$store.getters.userInfo?.userId
        if (userId) {
          const res = await getUnreadCount(userId)
          this.unreadCount = res.data || 0
        }
      } catch (error) {
        console.error(error)
      }
    },
    handleUserCommand(command) {
      if (command === 'logout') {
        this.$store.dispatch('logout')
        this.$router.push('/home')
        this.$message.success('退出成功')
      } else if (command === 'profile') {
        this.$router.push('/user/profile')
      } else if (command === 'chat') {
        this.$router.push('/chat')
      } else if (command === 'consult') {
        this.$router.push('/user/consult')
      } else if (command === 'lawyer-center') {
        this.$router.push('/lawyer-center/dashboard')
      } else if (command === 'admin') {
        this.$router.push('/admin/dashboard')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.front-layout {
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
    flex-shrink: 0;
    
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
    flex: 1;
    justify-content: center;
    margin: 0 16px;
    overflow-x: auto;
    
    .nav-item {
      padding: 0 8px;
      color: #666;
      font-size: 14px;
      cursor: pointer;
      transition: color 0.3s;
      white-space: nowrap;
      flex-shrink: 0;
      
      &:hover, &.active {
        color: #1890ff;
      }
    }
  }
  
  .user-area {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    
    .chat-badge {
      margin-right: 16px;
    }
    
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
}

.footer {
  background: #1a1a2e;
  color: #fff;
  padding: 40px 0 20px;
  margin-top: 40px;
  
  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    text-align: center;
  }
  
  .footer-info {
    margin-bottom: 20px;
    
    p {
      margin: 5px 0;
      color: #ccc;
    }
  }
  
  .footer-links {
    margin-bottom: 20px;
    
    a {
      color: #ccc;
      margin: 0 15px;
      
      &:hover {
        color: #1890ff;
      }
    }
  }
  
  .copyright {
    color: #666;
    font-size: 13px;
  }
}
</style>
