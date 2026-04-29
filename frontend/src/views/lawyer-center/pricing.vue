<template>
  <div class="pricing-page">
    <el-card>
      <div slot="header">
        <span>服务定价</span>
      </div>
      <el-form ref="form" :model="form" label-width="120px" style="max-width: 600px">
        <el-divider>咨询服务</el-divider>
        <el-form-item label="图文咨询价格">
          <el-input-number v-model="form.consultPrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元/次</span>
        </el-form-item>
        <el-form-item label="深度咨询价格">
          <el-input-number v-model="form.deepConsultPrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元/次</span>
        </el-form-item>
        
        <el-divider>文书服务</el-divider>
        <el-form-item label="文书代写价格">
          <el-input-number v-model="form.documentPrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元/份</span>
        </el-form-item>
        <el-form-item label="文书审核价格">
          <el-input-number v-model="form.auditPrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元/份</span>
        </el-form-item>
        
        <el-divider>其他服务</el-divider>
        <el-form-item label="案件代理起步价">
          <el-input-number v-model="form.casePrice" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元</span>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">保存定价</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'LawyerPricing',
  data() {
    return {
      form: {
        consultPrice: 100,
        deepConsultPrice: 300,
        documentPrice: 500,
        auditPrice: 200,
        casePrice: 5000
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
.pricing-page {
  .el-divider {
    margin: 20px 0;
  }
}
</style>
