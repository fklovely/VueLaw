<template>
  <div class="document-order-list">
    <el-card>
      <div slot="header">
        <span>我的文书订单</span>
      </div>
      
      <el-table :data="orderList" v-loading="loading" border stripe>
        <el-table-column prop="orderNo" label="订单号" width="160"></el-table-column>
        <el-table-column prop="title" label="文书标题" min-width="150"></el-table-column>
        <el-table-column prop="orderType" label="服务类型" width="100">
          <template slot-scope="scope">
            {{ scope.row.orderType === 1 ? '文书代写' : '文书审核' }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="费用" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope.row)">查看</el-button>
            <el-button type="text" size="small" v-if="scope.row.status === 3 && scope.row.resultFileUrl" @click="handleDownload(scope.row)">下载</el-button>
            <el-button type="text" size="small" class="text-danger" v-if="scope.row.status === 1" @click="handleCancel(scope.row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        class="mt-20"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </el-card>
    
    <el-dialog title="订单详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="1" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="文书标题">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ currentOrder.orderType === 1 ? '文书代写' : '文书审核' }}</el-descriptions-item>
        <el-descriptions-item label="费用">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusLabel(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="需求描述">{{ currentOrder.requirement }}</el-descriptions-item>
        <el-descriptions-item label="附件" v-if="currentOrder.attachmentUrl">
          <el-link :href="currentOrder.attachmentUrl" target="_blank">查看附件</el-link>
        </el-descriptions-item>
        <el-descriptions-item label="结果文件" v-if="currentOrder.resultFileUrl">
          <el-link :href="currentOrder.resultFileUrl" target="_blank">下载结果文件</el-link>
        </el-descriptions-item>
        <el-descriptions-item label="备注" v-if="currentOrder.resultContent">{{ currentOrder.resultContent }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getUserDocumentOrders } from '@/api/document'
import { cancelDocumentOrder } from '@/api/documentOrder'

export default {
  name: 'UserDocumentOrder',
  data() {
    return {
      loading: false,
      orderList: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      detailDialogVisible: false,
      currentOrder: null
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getUserDocumentOrders({
          page: this.currentPage,
          size: this.pageSize
        })
        this.orderList = res.data?.records || []
        this.total = res.data?.total || 0
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    handleDetail(row) {
      this.currentOrder = row
      this.detailDialogVisible = true
    },
    handleDownload(row) {
      if (row.resultFileUrl) {
        window.open(row.resultFileUrl, '_blank')
      }
    },
    async handleCancel(row) {
      try {
        await this.$confirm('确定要取消该订单吗？取消后费用将原路退回余额。', '提示', { type: 'warning' })
        await cancelDocumentOrder(row.id)
        this.$message.success('取消成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          this.$message.error('取消失败')
        }
      }
    },
    getStatusType(status) {
      const types = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }
      return types[status] || 'info'
    },
    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '处理中', 3: '已完成', 4: '已取消' }
      return labels[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.document-order-list {
  padding: 20px;

  .text-danger {
    color: #F56C6C;
  }
}
</style>
