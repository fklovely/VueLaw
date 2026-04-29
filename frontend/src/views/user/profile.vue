<template>
  <div class="profile-page">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px" style="max-width: 500px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" disabled></el-input>
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="form.realName" placeholder="请输入真实姓名"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio :label="0">保密</el-radio>
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input-number v-model="form.age" :min="0" :max="150"></el-input-number>
      </el-form-item>
      <el-form-item label="头像">
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          :on-success="handleUploadSuccess"
          :show-file-list="false"
        >
          <el-avatar :size="60" :src="form.avatar || defaultAvatar"></el-avatar>
          <el-button size="small" type="text" class="ml-10">更换头像</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">保存修改</el-button>
      </el-form-item>
    </el-form>
    
    <el-divider></el-divider>
    
    <h3>修改密码</h3>
    <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="100px" style="max-width: 500px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handlePasswordSubmit" :loading="passwordSubmitting">修改密码</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'

export default {
  name: 'UserProfile',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      form: {
        username: '',
        realName: '',
        phone: '',
        email: '',
        gender: 0,
        age: null,
        avatar: ''
      },
      rules: {
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      },
      submitting: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
        confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }]
      },
      passwordSubmitting: false,
      uploadUrl: '/api/upload',
      uploadHeaders: {}
    }
  },
  created() {
    const token = this.$store.getters.token
    if (token) {
      this.uploadHeaders = { Authorization: token }
    }
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        const res = await getUserInfo()
        if (res.data && res.data.user) {
          this.form = { ...res.data.user }
        }
      } catch (error) {
        console.error(error)
      }
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            await updateUserInfo(this.form)
            this.$store.dispatch('updateUserInfo', {
              ...this.$store.getters.userInfo,
              realName: this.form.realName,
              phone: this.form.phone,
              email: this.form.email,
              avatar: this.form.avatar
            })
            this.$message.success('保存成功')
          } catch (error) {
            console.error(error)
          } finally {
            this.submitting = false
          }
        }
      })
    },
    handlePasswordSubmit() {
      this.$refs.passwordForm.validate(async valid => {
        if (valid) {
          this.passwordSubmitting = true
          try {
            await updatePassword({
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            })
            this.$message.success('密码修改成功')
            this.$refs.passwordForm.resetFields()
          } catch (error) {
            console.error(error)
          } finally {
            this.passwordSubmitting = false
          }
        }
      })
    },
    handleUploadSuccess(res) {
      if (res.code === 200) {
        this.form.avatar = res.data.url
        this.saveAvatar()
      }
    },
    async saveAvatar() {
      try {
        await updateUserInfo({ id: this.form.id, avatar: this.form.avatar })
        this.$store.dispatch('updateUserInfo', {
          ...this.$store.getters.userInfo,
          avatar: this.form.avatar
        })
        this.$message.success('头像更新成功')
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>
