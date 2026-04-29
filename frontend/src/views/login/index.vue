<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="login-content">
        <div class="login-left">
          <div class="logo-section">
            <h1>未成年人保护法律服务平台</h1>
            <p class="subtitle">专业、便捷、贴心的法律咨询服务</p>
          </div>
          <div class="feature-list">
            <div class="feature-item">
              <i class="el-icon-user"></i>
              <span>专业律师团队</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-chat-dot-round"></i>
              <span>在线法律咨询</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-document"></i>
              <span>法律文书服务</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-reading"></i>
              <span>案例法规查询</span>
            </div>
          </div>
        </div>
        <div class="login-right">
          <div class="login-box">
            <h2>用户登录</h2>
            <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
              <el-form-item prop="username">
                <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="请输入用户名"></el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" placeholder="请输入密码" show-password @keyup.enter.native="handleLogin"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">登 录</el-button>
              </el-form-item>
              <div class="login-footer">
                <span>还没有账号？</span>
                <router-link to="/register">立即注册</router-link>
              </div>
            </el-form>
            <div class="demo-account">
              <p>测试账号：</p>
              <p>管理员：admin / admin123</p>
              <p>律师：lawyer1 / lawyer123</p>
              <p>客户：user1 / user123</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/user'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            const res = await login(this.loginForm)
            this.$store.dispatch('login', res.data)
            this.$message.success('登录成功')
            const role = res.data.role
            if (role === 'admin') {
              this.$router.push('/admin/dashboard')
            } else if (role === 'lawyer') {
              this.$router.push('/lawyer-center/dashboard')
            } else {
              this.$router.push('/home')
            }
          } catch (error) {
            console.error(error)
          } finally {
            this.loading = false
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-bg {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('https://images.unsplash.com/photo-1589829545856-d10d557cf95f?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80');
  background-size: cover;
  background-position: center;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.4);
  }
}

.login-content {
  display: flex;
  width: 900px;
  height: 500px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
  
  .logo-section {
    margin-bottom: 40px;
    
    h1 {
      font-size: 28px;
      font-weight: 600;
      margin-bottom: 10px;
    }
    
    .subtitle {
      font-size: 16px;
      opacity: 0.9;
    }
  }
  
  .feature-list {
    .feature-item {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      font-size: 16px;
      
      i {
        font-size: 24px;
        margin-right: 15px;
        width: 30px;
      }
    }
  }
}

.login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .login-box {
    width: 100%;
    max-width: 320px;
    
    h2 {
      text-align: center;
      margin-bottom: 30px;
      color: #333;
      font-size: 24px;
    }
  }
}

.login-form {
  .login-btn {
    width: 100%;
    height: 42px;
    font-size: 16px;
  }
}

.login-footer {
  text-align: center;
  margin-top: 15px;
  color: #666;
  
  a {
    color: #409EFF;
    margin-left: 5px;
  }
}

.demo-account {
  margin-top: 30px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 12px;
  color: #666;
  
  p {
    margin: 5px 0;
  }
}
</style>
