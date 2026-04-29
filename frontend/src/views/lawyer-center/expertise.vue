<template>
  <div class="expertise-page">
    <el-card>
      <div slot="header">
        <span>专业领域设置</span>
      </div>
      <el-form ref="form" :model="form" label-width="120px" style="max-width: 600px">
        <el-form-item label="擅长领域">
          <el-checkbox-group v-model="form.expertises">
            <el-checkbox label="未成年人监护权"></el-checkbox>
            <el-checkbox label="校园欺凌"></el-checkbox>
            <el-checkbox label="家庭暴力"></el-checkbox>
            <el-checkbox label="离婚纠纷"></el-checkbox>
            <el-checkbox label="财产继承"></el-checkbox>
            <el-checkbox label="刑事辩护"></el-checkbox>
            <el-checkbox label="交通事故"></el-checkbox>
            <el-checkbox label="劳动纠纷"></el-checkbox>
            <el-checkbox label="合同纠纷"></el-checkbox>
            <el-checkbox label="房产纠纷"></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="专业资质">
          <el-input type="textarea" v-model="form.qualifications" :rows="4" placeholder="请输入您的专业资质、荣誉证书等"></el-input>
        </el-form-item>
        <el-form-item label="从业年限">
          <el-input-number v-model="form.experienceYears" :min="0" :max="50"></el-input-number>
          <span class="ml-10">年</span>
        </el-form-item>
        <el-form-item label="成功案例数">
          <el-input-number v-model="form.successCases" :min="0"></el-input-number>
          <span class="ml-10">件</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'LawyerExpertise',
  data() {
    return {
      form: {
        expertises: ['未成年人监护权', '校园欺凌'],
        qualifications: '',
        experienceYears: 5,
        successCases: 100
      },
      submitting: false
    }
  },
  methods: {
    handleSubmit() {
      this.submitting = true
      setTimeout(() => {
        this.$message.success('保存成功')
        this.submitting = false
      }, 500)
    }
  }
}
</script>

<style lang="scss" scoped>
.expertise-page {
  .el-checkbox {
    margin-right: 20px;
    margin-bottom: 10px;
  }
}
</style>
