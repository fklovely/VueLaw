<template>
  <div class="security-page">
    <el-card>
      <div slot="header">
        <span>账户安全</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" style="max-width: 500px">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input type="password" v-model="form.oldPassword" placeholder="请输入当前密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="form.newPassword" placeholder="请输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="form.confirmPassword" placeholder="请再次输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="mt-20">
      <div slot="header">
        <span>安全设置</span>
      </div>
      <div class="security-item">
        <div class="security-info">
          <span class="label">手机绑定</span>
          <span class="value">{{ phone }}</span>
        </div>
        <el-button type="text" @click="handleBindPhone">修改</el-button>
      </div>
      <div class="security-item">
        <div class="security-info">
          <span class="label">邮箱绑定</span>
          <span class="value">{{ email || '未绑定' }}</span>
        </div>
        <el-button type="text" @click="handleBindEmail">{{ email ? '修改' : '绑定' }}</el-button>
      </div>
      <div class="security-item">
        <div class="security-info">
          <span class="label">登录记录</span>
          <span class="value">上次登录：{{ lastLoginTime }}</span>
        </div>
        <el-button type="text" @click="handleViewLoginLog">查看</el-button>
      </div>
    </el-card>
    
    <el-dialog title="绑定手机" :visible.sync="phoneDialogVisible" width="400px">
      <el-form ref="phoneForm" :model="phoneForm" label-width="80px">
        <el-form-item label="手机号">
          <el-input v-model="phoneForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="验证码">
          <el-input v-model="phoneForm.code" placeholder="请输入验证码">
            <el-button slot="append" :disabled="countdown > 0" @click="sendCode">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="phoneDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPhone">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'LawyerSecurity',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      submitting: false,
      phone: '138****8000',
      email: 'lawyer@example.com',
      lastLoginTime: '2026-01-15 10:00:00',
      phoneDialogVisible: false,
      phoneForm: {
        phone: '',
        code: ''
      },
      countdown: 0
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          setTimeout(() => {
            this.$message.success('密码修改成功')
            this.submitting = false
            this.$refs.form.resetFields()
          }, 500)
        }
      })
    },
    handleBindPhone() {
      this.phoneForm = { phone: '', code: '' }
      this.phoneDialogVisible = true
    },
    handleBindEmail() {
      this.$prompt('请输入邮箱地址', '绑定邮箱', {
        inputPattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
        inputErrorMessage: '请输入正确的邮箱地址'
      }).then(({ value }) => {
        this.email = value
        this.$message.success('邮箱绑定成功')
      }).catch(() => {})
    },
    handleViewLoginLog() {
      this.$message.info('登录记录功能开发中')
    },
    sendCode() {
      if (!this.phoneForm.phone) {
        this.$message.warning('请输入手机号')
        return
      }
      this.countdown = 60
      const timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
      this.$message.success('验证码已发送')
    },
    submitPhone() {
      if (!this.phoneForm.phone || !this.phoneForm.code) {
        this.$message.warning('请填写完整信息')
        return
      }
      this.phone = this.phoneForm.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
      this.$message.success('手机号绑定成功')
      this.phoneDialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.security-page {
  .security-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    .security-info {
      .label {
        font-weight: 500;
        margin-right: 20px;
      }
      
      .value {
        color: #666;
      }
    }
  }
}
</style>
