<template>
  <div class="privacy-page">
    <el-card>
      <div slot="header">
        <span>隐私设置</span>
      </div>
      <el-form label-width="150px" style="max-width: 600px">
        <el-form-item label="公开联系方式">
          <el-switch v-model="settings.showContact"></el-switch>
          <span class="setting-desc">开启后，用户可查看您的联系电话</span>
        </el-form-item>
        <el-form-item label="公开邮箱">
          <el-switch v-model="settings.showEmail"></el-switch>
          <span class="setting-desc">开启后，用户可查看您的邮箱地址</span>
        </el-form-item>
        <el-form-item label="公开执业证号">
          <el-switch v-model="settings.showLicense"></el-switch>
          <span class="setting-desc">开启后，用户可查看您的执业证号</span>
        </el-form-item>
        <el-form-item label="公开服务记录">
          <el-switch v-model="settings.showServiceRecord"></el-switch>
          <span class="setting-desc">开启后，用户可查看您的服务记录统计</span>
        </el-form-item>
        <el-form-item label="公开评价内容">
          <el-switch v-model="settings.showEvaluation"></el-switch>
          <span class="setting-desc">开启后，用户可查看您收到的评价</span>
        </el-form-item>
        <el-form-item label="允许用户关注">
          <el-switch v-model="settings.allowFollow"></el-switch>
          <span class="setting-desc">开启后，用户可以关注您</span>
        </el-form-item>
        <el-form-item label="接收系统通知">
          <el-switch v-model="settings.receiveNotification"></el-switch>
          <span class="setting-desc">开启后，您将收到系统通知消息</span>
        </el-form-item>
        <el-form-item label="接收订单提醒">
          <el-switch v-model="settings.receiveOrderNotice"></el-switch>
          <span class="setting-desc">开启后，有新订单时会收到提醒</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'LawyerPrivacy',
  data() {
    return {
      settings: {
        showContact: true,
        showEmail: false,
        showLicense: true,
        showServiceRecord: true,
        showEvaluation: true,
        allowFollow: true,
        receiveNotification: true,
        receiveOrderNotice: true
      },
      saving: false
    }
  },
  methods: {
    handleSave() {
      this.saving = true
      setTimeout(() => {
        this.$message.success('保存成功')
        this.saving = false
      }, 500)
    }
  }
}
</script>

<style lang="scss" scoped>
.privacy-page {
  .setting-desc {
    margin-left: 15px;
    color: #999;
    font-size: 13px;
  }
}
</style>
