<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable>
            <el-option label="客户" value="client"></el-option>
            <el-option label="律师" value="lawyer"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/姓名/手机号" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.auditStatus" placeholder="全部" clearable>
            <el-option label="无需审核" :value="0"></el-option>
            <el-option label="待审核" :value="1"></el-option>
            <el-option label="审核通过" :value="2"></el-option>
            <el-option label="审核拒绝" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增用户</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名" width="120"></el-table-column>
      <el-table-column prop="realName" label="姓名" width="100"></el-table-column>
      <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="180"></el-table-column>
      <el-table-column prop="role" label="角色" width="100">
        <template slot-scope="scope">
          <el-tag :type="getRoleType(scope.row.role, scope.row.auditStatus)">{{ getRoleLabel(scope.row.role, scope.row.auditStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditStatus" label="审核状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getAuditStatusType(scope.row.auditStatus)">
            {{ getAuditStatusLabel(scope.row.auditStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleStatus(scope.row)">
            {{ scope.row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button type="text" size="small" v-if="scope.row.auditStatus === 1" @click="handleAudit(scope.row, 2)">通过</el-button>
          <el-button type="text" size="small" class="text-warning" v-if="scope.row.auditStatus === 1" @click="handleAudit(scope.row, 3)">拒绝</el-button>
          <el-button type="text" size="small" class="text-danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog title="新增用户" :visible.sync="addDialogVisible" width="500px">
      <el-form ref="addForm" :model="addForm" :rules="addRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="addForm.realName" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="addForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="addForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="客户" value="client"></el-option>
            <el-option label="律师" value="lawyer"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="addLoading">确定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="审核拒绝原因" :visible.sync="rejectDialogVisible" width="400px">
      <el-input type="textarea" v-model="rejectRemark" placeholder="请输入拒绝原因" :rows="3"></el-input>
      <span slot="footer">
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, updateUserStatus, deleteUser, auditUser, addUser } from '@/api/user'

export default {
  name: 'AdminUser',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        role: '',
        keyword: '',
        status: null,
        auditStatus: null
      },
      rejectDialogVisible: false,
      rejectRemark: '',
      currentUser: null,
      addDialogVisible: false,
      addLoading: false,
      addForm: {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: '',
        role: 'client'
      },
      addRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }]
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
        const res = await getUserList({
          page: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.tableData = res.data.records
        this.total = res.data.total
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
      this.searchForm = { role: '', keyword: '', status: null, auditStatus: null }
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
    handleAdd() {
      this.dialogTitle = '新增用户'
      this.addForm = {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: '',
        role: 'client'
      }
      this.addDialogVisible = true
    },
    submitAdd() {
      this.$refs.addForm.validate(async valid => {
        if (valid) {
          this.addLoading = true
          try {
            await addUser(this.addForm)
            this.$message.success('添加成功')
            this.addDialogVisible = false
            this.loadData()
          } catch (error) {
            console.error(error)
          } finally {
            this.addLoading = false
          }
        }
      })
    },
    async handleStatus(row) {
      const status = row.status === 1 ? 0 : 1
      const action = status === 0 ? '禁用' : '启用'
      try {
        await this.$confirm(`确定要${action}该用户吗？`, '提示', { type: 'warning' })
        await updateUserStatus(row.id, status)
        this.$message.success(`${action}成功`)
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
        await deleteUser(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    async handleAudit(row, auditStatus) {
      if (auditStatus === 2) {
        try {
          await this.$confirm('确定通过该用户的律师申请吗？', '提示', { type: 'warning' })
          await auditUser(row.id, auditStatus, '')
          this.$message.success('审核通过，该用户已成为律师')
          this.loadData()
        } catch (error) {
          if (error !== 'cancel') console.error(error)
        }
      } else {
        this.currentUser = row
        this.rejectRemark = ''
        this.rejectDialogVisible = true
      }
    },
    async confirmReject() {
      if (!this.rejectRemark.trim()) {
        this.$message.warning('请输入拒绝原因')
        return
      }
      try {
        const res = await auditUser(this.currentUser.id, 3, this.rejectRemark)
        this.$message.success(res.message || '已拒绝该用户的律师申请，已发送短信通知用户')
        this.rejectDialogVisible = false
        this.loadData()
      } catch (error) {
        console.error(error)
      }
    },
    getRoleType(role, auditStatus) {
      if (auditStatus === 1) return 'warning'
      if (auditStatus === 3) return 'danger'
      const types = { admin: 'danger', lawyer: 'warning', client: 'primary' }
      return types[role] || 'info'
    },
    getRoleLabel(role, auditStatus) {
      if (auditStatus === 1) return '待审核律师'
      if (auditStatus === 3) return '律师(已拒绝)'
      if (auditStatus === 2) return '律师'
      const labels = { admin: '管理员', lawyer: '律师', client: '客户' }
      return labels[role] || role
    },
    getAuditStatusType(auditStatus) {
      const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
      return types[auditStatus] || 'info'
    },
    getAuditStatusLabel(auditStatus) {
      const labels = { 0: '无需审核', 1: '待审核', 2: '审核通过', 3: '审核拒绝' }
      return labels[auditStatus] || '无需审核'
    }
  }
}
</script>

<style lang="scss" scoped>
.text-warning {
  color: #E6A23C;
}

.text-danger {
  color: #F56C6C;
}
</style>
