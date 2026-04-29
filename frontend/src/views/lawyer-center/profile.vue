<template>
  <div class="page-container">
    <el-card>
      <div slot="header">
        <span>个人资料</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" style="max-width: 600px">
        <el-form-item label="姓名" prop="lawyerName">
          <el-input v-model="form.lawyerName" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="执业证号">
          <el-input v-model="form.lawyerNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="form.province" placeholder="请输入省份"></el-input>
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="form.city" placeholder="请输入城市"></el-input>
        </el-form-item>
        <el-form-item label="所属律所">
          <el-input v-model="form.lawFirm" placeholder="请输入所属律所"></el-input>
        </el-form-item>
        <el-form-item label="学历">
          <el-input v-model="form.education" placeholder="请输入学历"></el-input>
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="form.professionalTitle" placeholder="请输入职称"></el-input>
        </el-form-item>
        <el-form-item label="擅长领域">
          <el-input v-model="form.expertises" placeholder="多个领域用逗号分隔"></el-input>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input type="textarea" v-model="form.profile" :rows="4" placeholder="请输入个人简介"></el-input>
        </el-form-item>
        <el-divider>服务定价</el-divider>
        <el-form-item label="普通咨询价格">
          <el-input-number v-model="form.consultPrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元</span>
        </el-form-item>
        <el-form-item label="深度咨询价格">
          <el-input-number v-model="form.deepConsultPrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元</span>
        </el-form-item>
        <el-form-item label="文书代写价格">
          <el-input-number v-model="form.documentPrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元</span>
        </el-form-item>
        <el-form-item label="文书审核价格">
          <el-input-number v-model="form.auditPrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getLawyerInfo, updateLawyerInfo } from '@/api/lawyer'

export default {
  name: 'LawyerProfile',
  data() {
    return {
      form: {
        id: null,
        lawyerName: '',
        lawyerNo: '',
        phone: '',
        email: '',
        province: '',
        city: '',
        lawFirm: '',
        education: '',
        professionalTitle: '',
        expertises: '',
        profile: '',
        consultPrice: 0,
        deepConsultPrice: 0,
        documentPrice: 0,
        auditPrice: 0
      },
      rules: {
        lawyerName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  created() {
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        const res = await getLawyerInfo()
        if (res.data) {
          this.form = res.data
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
            await updateLawyerInfo(this.form)
            this.$store.dispatch('updateUserInfo', {
              ...this.$store.getters.userInfo,
              realName: this.form.lawyerName
            })
            this.$message.success('保存成功')
          } catch (error) {
            console.error(error)
          } finally {
            this.submitting = false
          }
        }
      })
    }
  }
}
</script>
