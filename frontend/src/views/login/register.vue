<template>
  <div class="register-container">
    <div class="register-bg">
      <div class="register-box">
        <h2>用户注册</h2>
        <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" show-password></el-input>
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="registerForm.realName" placeholder="请输入真实姓名"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
          </el-form-item>
          <el-form-item label="用户类型" prop="role">
            <el-radio-group v-model="registerForm.role">
              <el-radio label="client">普通用户</el-radio>
              <el-radio label="lawyer">律师</el-radio>
            </el-radio-group>
            <div class="role-tip" v-if="registerForm.role === 'lawyer'">
              <i class="el-icon-warning"></i> 律师注册需要管理员审核通过后才能使用律师功能
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleRegister">注 册</el-button>
            <el-button @click="$router.push('/login')">返回登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'

export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        phone: '',
        email: '',
        role: 'client'
      },
      registerRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        role: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
      },
      loading: false
    }
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            await register(this.registerForm)
            this.$message.success('注册成功')
            this.$router.push('/login')
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
.register-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-bg {
  background-image: url('https://images.unsplash.com/photo-1589829545856-d10d557cf95f?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80');
  background-size: cover;
  background-position: center;
  padding: 40px;
  border-radius: 12px;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.4);
    border-radius: 12px;
  }
}

.register-box {
  position: relative;
  z-index: 1;
  width: 450px;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  
  h2 {
    text-align: center;
    margin-bottom: 20px;
    color: #333;
  }
}

.role-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #E6A23C;
  
  i {
    margin-right: 4px;
  }
}
</style>
