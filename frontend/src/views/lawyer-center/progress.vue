<template>
  <div class="progress-page">
    <el-card>
      <div slot="header">
        <span>进度更新</span>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
        <el-table-column prop="title" label="服务内容" min-width="180"></el-table-column>
        <el-table-column prop="clientName" label="客户" width="100"></el-table-column>
        <el-table-column prop="currentProgress" label="当前进度" width="150">
          <template slot-scope="scope">
            <el-progress :percentage="scope.row.currentProgress" :status="scope.row.currentProgress === 100 ? 'success' : null"></el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="150"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" v-if="scope.row.status !== 4" @click="handleUpdate(scope.row)">更新进度</el-button>
            <el-button type="success" size="small" v-if="scope.row.currentProgress === 100 && scope.row.status !== 4" @click="handleFinish(scope.row)">标记完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog title="更新进度" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="当前进度">
          <el-slider v-model="form.progress" :marks="marks"></el-slider>
        </el-form-item>
        <el-form-item label="进度说明">
          <el-input type="textarea" v-model="form.remark" :rows="3" placeholder="请输入进度说明"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpdate">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ProgressUpdate',
  data() {
    return {
      loading: false,
      tableData: [
        { id: 1, orderNo: 'CON202601150001', title: '关于校园欺凌的法律咨询', clientName: '张先生', currentProgress: 50, status: 3, updateTime: '2026-01-15 10:00:00' },
        { id: 2, orderNo: 'DOC202601140002', title: '监护权变更起诉状代写', clientName: '李女士', currentProgress: 80, status: 3, updateTime: '2026-01-14 15:30:00' }
      ],
      dialogVisible: false,
      form: {
        id: null,
        progress: 0,
        remark: ''
      },
      marks: {
        0: '开始',
        25: '接单',
        50: '处理中',
        75: '即将完成',
        100: '完成'
      }
    }
  },
  methods: {
    handleUpdate(row) {
      this.form = { id: row.id, progress: row.currentProgress, remark: '' }
      this.dialogVisible = true
    },
    submitUpdate() {
      const row = this.tableData.find(item => item.id === this.form.id)
      if (row) {
        row.currentProgress = this.form.progress
        row.updateTime = new Date().toLocaleString()
      }
      this.$message.success('进度更新成功')
      this.dialogVisible = false
    },
    handleFinish(row) {
      this.$confirm('确定要标记为完成吗？', '提示', { type: 'warning' }).then(() => {
        row.status = 4
        row.currentProgress = 100
        this.$message.success('已标记为完成')
      }).catch(() => {})
    },
    getStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success' }
      return types[status]
    },
    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '已接单', 3: '处理中', 4: '已完成' }
      return labels[status]
    }
  }
}
</script>

<style lang="scss" scoped>
.progress-page {
  .el-slider {
    margin: 0 20px;
  }
}
</style>
