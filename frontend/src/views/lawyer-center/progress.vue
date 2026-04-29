<template>
  <div class="progress-page">
    <el-card>
      <div slot="header">
        <span>进度更新</span>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="180"></el-table-column>
        <el-table-column prop="orderTypeName" label="订单类型" width="110"></el-table-column>
        <el-table-column prop="title" label="服务内容" min-width="180" show-overflow-tooltip></el-table-column>
        <el-table-column prop="clientName" label="客户" width="110"></el-table-column>
        <el-table-column prop="currentProgress" label="当前进度" width="170">
          <template slot-scope="scope">
            <el-progress
              :percentage="Number(scope.row.currentProgress || 0)"
              :status="Number(scope.row.currentProgress || 0) === 100 ? 'success' : null"
            ></el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="statusLabel" label="订单状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row)">{{ scope.row.statusLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="进度说明" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ scope.row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170">
          <template slot-scope="scope">
            {{ scope.row.updateTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="190" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="small"
              :disabled="!scope.row.canUpdate"
              @click="handleUpdate(scope.row)"
            >
              更新进度
            </el-button>
            <el-button
              type="text"
              size="small"
              v-if="scope.row.canUpdate && Number(scope.row.currentProgress || 0) < 100"
              @click="handleSetFull(scope.row)"
            >
              设为100%
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="更新进度" :visible.sync="dialogVisible" width="500px" @close="resetForm">
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
        <el-button type="primary" @click="submitUpdate" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getOrderProgressList, updateOrderProgress } from '@/api/orderProgress'

export default {
  name: 'ProgressUpdate',
  data() {
    return {
      loading: false,
      submitting: false,
      tableData: [],
      dialogVisible: false,
      form: {
        orderType: null,
        orderId: null,
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
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getOrderProgressList()
        this.tableData = res.data || []
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    handleUpdate(row) {
      if (!row.canUpdate) return
      this.form = {
        orderType: row.orderType,
        orderId: row.orderId,
        progress: Number(row.currentProgress || 0),
        remark: row.remark || ''
      }
      this.dialogVisible = true
    },
    async handleSetFull(row) {
      try {
        await updateOrderProgress({
          orderType: row.orderType,
          orderId: row.orderId,
          progress: 100,
          remark: row.remark || '进度已更新至100%'
        })
        this.$message.success('进度更新成功')
        this.loadData()
      } catch (error) {
        console.error(error)
      }
    },
    async submitUpdate() {
      this.submitting = true
      try {
        await updateOrderProgress(this.form)
        this.$message.success('进度更新成功')
        this.dialogVisible = false
        this.loadData()
      } catch (error) {
        console.error(error)
      } finally {
        this.submitting = false
      }
    },
    resetForm() {
      this.form = {
        orderType: null,
        orderId: null,
        progress: 0,
        remark: ''
      }
    },
    getStatusType(row) {
      if (row.orderType === 1) {
        const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success', 5: 'danger' }
        return types[row.status] || 'info'
      }
      const types = { 1: 'info', 2: 'primary', 3: 'success', 4: 'danger' }
      return types[row.status] || 'info'
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
