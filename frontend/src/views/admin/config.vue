<template>
  <div class="page-container">
    <el-card>
      <div slot="header">
        <span>系统配置</span>
      </div>
      <el-form label-width="120px">
        <el-form-item label="平台名称">
          <el-input v-model="configForm.platform_name" style="width: 300px"></el-input>
        </el-form-item>
        <el-form-item label="佣金比例(%)">
          <el-input-number v-model="configForm.commission_rate" :min="0" :max="100" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="咨询超时时间">
          <el-input-number v-model="configForm.consult_timeout" :min="1"></el-input-number>
          <span class="ml-10">小时</span>
        </el-form-item>
        <el-form-item label="文件上传路径">
          <el-input v-model="configForm.file_upload_path" style="width: 300px"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getConfigMap, updateConfig } from '@/api/config'

export default {
  name: 'AdminConfig',
  data() {
    return {
      configForm: {
        platform_name: '',
        commission_rate: 10,
        consult_timeout: 24,
        file_upload_path: '/uploads'
      },
      saving: false
    }
  },
  created() {
    this.loadConfig()
  },
  methods: {
    async loadConfig() {
      try {
        const res = await getConfigMap()
        if (res.data) {
          Object.keys(this.configForm).forEach(key => {
            if (res.data[key]) {
              if (key === 'commission_rate' || key === 'consult_timeout') {
                this.configForm[key] = parseFloat(res.data[key])
              } else {
                this.configForm[key] = res.data[key]
              }
            }
          })
        }
      } catch (error) {
        console.error(error)
      }
    },
    async handleSave() {
      this.saving = true
      try {
        for (const key in this.configForm) {
          await updateConfig({ key, value: String(this.configForm[key]) })
        }
        this.$message.success('保存成功')
      } catch (error) {
        console.error(error)
      } finally {
        this.saving = false
      }
    }
  }
}
</script>
