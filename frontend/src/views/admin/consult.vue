<!--<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="订单类型">
          <el-select v-model="searchForm.orderType" placeholder="全部" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="图文咨询" value="consult"></el-option>
            <el-option label="文书代写" value="document-write"></el-option>
            <el-option label="文书审核" value="document-audit"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待接单" :value="1"></el-option>
            <el-option label="处理中" :value="2"></el-option>
            <el-option label="已完成" :value="3"></el-option>
            <el-option label="已取消" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="160"></el-table-column>
      <el-table-column label="订单类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.orderType === 'consult' ? 'primary' : 'warning'" size="small">
            {{ getOrderTypeName(scope.row.orderType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题/内容" min-width="180" show-overflow-tooltip></el-table-column>
      <el-table-column prop="price" label="金额" width="100">
        <template slot-scope="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
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

    <el-dialog title="订单详情" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ getOrderTypeName(currentOrder.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ currentOrder.content || currentOrder.requirement }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ getStatusLabel(currentOrder.status) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="currentOrder.result || currentOrder.resultContent">{{ currentOrder.result || currentOrder.resultContent }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getConsultList } from '@/api/consult'
import { getDocumentOrderUserList } from '@/api/documentOrder'

export default {
  name: 'AdminConsult',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        orderType: '',
        status: null
      },
      dialogVisible: false,
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
        if (this.searchForm.orderType === '' || !this.searchForm.orderType) {
          const [consultRes, docRes] = await Promise.all([
            getConsultList({ page: 1, size: 1000, status: this.searchForm.status }),
            getDocumentOrderUserList({ page: 1, size: 1000 })
          ])
          const consultOrders = (consultRes.data?.records || []).map(item => ({ ...item, orderType: 'consult' }))
          const docOrders = (docRes.data?.records || []).map(item => ({
            id: item.id,
            orderNo: item.orderNo,
            title: item.title || '文书服务',
            price: item.price,
            status: item.status,
            createTime: item.createTime,
            content: item.requirement,
            result: item.resultContent,
            orderType: item.orderType === 1 ? 'document-write' : 'document-audit'
          }))
          this.tableData = [...consultOrders, ...docOrders].sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
          this.total = this.tableData.length
        } else if (this.searchForm.orderType === 'consult') {
          const res = await getConsultList({
            page: this.currentPage,
            size: this.pageSize,
            status: this.searchForm.status
          })
          this.tableData = (res.data?.records || []).map(item => ({ ...item, orderType: 'consult' }))
          this.total = res.data?.total || 0
        } else {
          const orderType = this.searchForm.orderType === 'document-write' ? 1 : 2
          const res = await getDocumentOrderUserList({
            page: this.currentPage,
            size: this.pageSize,
            orderType: orderType,
            status: this.searchForm.status
          })
          this.tableData = (res.data?.records || []).map(item => ({
            id: item.id,
            orderNo: item.orderNo,
            title: item.title || '文书服务',
            price: item.price,
            status: item.status,
            createTime: item.createTime,
            content: item.requirement,
            result: item.resultContent,
            orderType: item.orderType === 1 ? 'document-write' : 'document-audit'
          }))
          this.total = res.data?.total || 0
        }
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { orderType: '', status: null }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    handleView(row) {
      this.currentOrder = row
      this.dialogVisible = true
    },
    getOrderTypeName(type) {
      const types = { consult: '图文咨询', 'document-write': '文书代写', 'document-audit': '文书审核' }
      return types[type] || type
    },
    getStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' }
      return types[status] || 'info'
    },
    getStatusLabel(status) {
      const labels = { 1: '待接单', 2: '处理中', 3: '已完成', 4: '已取消' }
      return labels[status] || '未知'
    }
  }
}
</script>-->



<template>
  <div class="page-container">

    <div class="search-form">
      <el-form :inline="true">
        <el-form-item label="订单类型">
          <el-select v-model="searchForm.orderType" placeholder="全部" clearable @change="handleSearch">
            <el-option label="全部" value=""></el-option>
            <el-option label="图文咨询" value="consult"></el-option>
            <el-option label="文书代写" value="document-write"></el-option>
            <el-option label="文书审核" value="document-audit"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch">
            <el-option label="待接单" :value="1"></el-option>
            <el-option label="处理中" :value="2"></el-option>
            <el-option label="已完成" :value="3"></el-option>
            <el-option label="已取消" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单编号" width="180"></el-table-column>
      <el-table-column label="订单类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="getOrderTypeTag(scope.row.orderType)" size="small">
            {{ getOrderTypeName(scope.row.orderType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题/内容" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column label="客户" width="120">
        <template slot-scope="scope">{{ scope.row.userName || '--' }}</template>
      </el-table-column>
      <el-table-column label="接单律师" width="120">
        <template slot-scope="scope">{{ scope.row.lawyerName || '未分配' }}</template>
      </el-table-column>
      <el-table-column prop="price" label="金额" width="100">
        <template slot-scope="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.displayStatus)">
            {{ getStatusLabel(scope.row.displayStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看详情</el-button>
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

    <!-- 订单详情弹窗 -->
    <el-dialog :title="'订单详情 - ' + (currentOrder ? currentOrder.orderNo : '')" :visible.sync="dialogVisible" width="700px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ getOrderTypeName(currentOrder.orderType) }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.price }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.displayStatus)">
            {{ getStatusLabel(currentOrder.displayStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户">{{ currentOrder.userName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="律师">{{ currentOrder.lawyerName || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">
          {{ currentOrder.content || currentOrder.requirement || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="currentOrder.result || currentOrder.resultContent">
          {{ currentOrder.result || currentOrder.resultContent }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" v-if="currentOrder.finishTime">{{ currentOrder.finishTime }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getConsultList } from '@/api/consult'
import { getDocumentOrderUserList } from '@/api/documentOrder'
import { getLawyerList } from '@/api/lawyer'
import { getUserList } from '@/api/user'

export default {
  name: 'ConsultManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        orderType: '',
        status: null
      },
      dialogVisible: false,
      currentOrder: null,
      userMap: {},
      lawyerMap: {}
    }
  },
  created() {
    this.loadUserAndLawyerCache()
    this.loadData()
  },
  methods: {
    // 加载用户和律师信息缓存
    async loadUserAndLawyerCache() {
      try {
        const [userRes, lawyerRes] = await Promise.all([
          getUserList({ page: 1, size: 1000 }),
          getLawyerList({ page: 1, size: 1000 })
        ])
        const users = userRes.data?.records || []
        const lawyers = lawyerRes.data?.records || []
        users.forEach(u => { this.userMap[u.id] = u.realName || u.username })
        lawyers.forEach(l => { this.lawyerMap[l.id] = l.lawyerName })
      } catch (error) {
        console.error('加载用户/律师信息失败:', error)
      }
    },

    async loadData() {
      this.loading = true
      try {
        let allOrders = []

        // 1. 获取咨询订单
        const consultRes = await getConsultList({ page: 1, size: 1000 })
        const consultOrders = (consultRes.data?.records || []).map(item => ({
          id: item.id,
          orderNo: item.orderNo,
          orderType: 'consult',
          title: item.title,
          content: item.content,
          price: item.price,
          originalStatus: item.status,
          displayStatus: this.mapConsultStatus(item.status),
          createTime: item.createTime,
          finishTime: item.finishTime,
          result: item.result,
          userId: item.userId,
          lawyerId: item.lawyerId,
          userName: this.userMap[item.userId] || `用户${item.userId}`,
          lawyerName: this.lawyerMap[item.lawyerId] || `律师${item.lawyerId}`
        }))
        allOrders.push(...consultOrders)

        // 2. 获取文书订单
        const docRes = await getDocumentOrderUserList({ page: 1, size: 1000 })
        console.log('文书订单返回:', docRes)

        const docOrders = (docRes.data?.records || []).map(item => ({
          id: item.id,
          orderNo: item.orderNo,
          orderType: item.orderType === 1 ? 'document-write' : 'document-audit',
          title: item.title,
          requirement: item.requirement,
          price: item.price,
          originalStatus: item.status,
          displayStatus: this.mapDocStatus(item.status),
          createTime: item.createTime,
          finishTime: item.finishTime,
          resultContent: item.resultContent,
          userId: item.userId,
          lawyerId: item.lawyerId,
          userName: this.userMap[item.userId] || `用户${item.userId}`,
          lawyerName: this.lawyerMap[item.lawyerId] || `律师${item.lawyerId}`
        }))

        console.log('处理后文书订单:', docOrders)
        allOrders.push(...docOrders)

        // 3. 按订单类型筛选
        if (this.searchForm.orderType === 'consult') {
          allOrders = allOrders.filter(o => o.orderType === 'consult')
        } else if (this.searchForm.orderType === 'document-write') {
          allOrders = allOrders.filter(o => o.orderType === 'document-write')
        } else if (this.searchForm.orderType === 'document-audit') {
          allOrders = allOrders.filter(o => o.orderType === 'document-audit')
        }

        // 4. 按状态筛选
        if (this.searchForm.status) {
          allOrders = allOrders.filter(o => o.displayStatus === this.searchForm.status)
        }

        // 5. 排序
        allOrders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))

        // 6. 分页
        this.total = allOrders.length
        const start = (this.currentPage - 1) * this.pageSize
        this.tableData = allOrders.slice(start, start + this.pageSize)

      } catch (error) {
        console.error('加载订单失败:', error)
        this.$message.error('加载订单失败')
      } finally {
        this.loading = false
      }
    },

    // 咨询订单状态 -> 统一展示状态
    mapConsultStatus(status) {
      const map = { 1: 1, 2: 2, 3: 2, 4: 3, 5: 4 }
      return map[status] || status
    },

    // 文书订单状态 -> 统一展示状态
    mapDocStatus(status) {
      const map = { 1: 1, 2: 2, 3: 3, 4: 4, 5: 4 }
      return map[status] || status
    },

    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },

    handleReset() {
      this.searchForm = { orderType: '', status: null }
      this.handleSearch()
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadData()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },

    handleView(row) {
      this.currentOrder = row
      this.dialogVisible = true
    },

    getOrderTypeName(type) {
      const map = { consult: '图文咨询', 'document-write': '文书代写', 'document-audit': '文书审核' }
      return map[type] || type
    },

    getOrderTypeTag(type) {
      const tags = { consult: 'primary', 'document-write': 'warning', 'document-audit': 'danger' }
      return tags[type] || 'info'
    },

    getStatusType(status) {
      const map = { 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' }
      return map[status] || 'info'
    },

    getStatusLabel(status) {
      const map = { 1: '待接单', 2: '处理中', 3: '已完成', 4: '已取消' }
      return map[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0 0 8px 0;
      font-size: 24px;
      color: #303133;
    }

    p {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }

  .search-form {
    background: #fff;
    padding: 16px 20px;
    border-radius: 4px;
    margin-bottom: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }

  .mt-20 {
    margin-top: 20px;
    text-align: right;
  }
}
</style>