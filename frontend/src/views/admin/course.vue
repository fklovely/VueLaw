<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="课程标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待审核" :value="0"></el-option>
            <el-option label="已上架" :value="1"></el-option>
            <el-option label="已拒绝" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="courseNo" label="课程编号" width="140"></el-table-column>
      <el-table-column prop="title" label="课程标题" min-width="200"></el-table-column>
      <el-table-column prop="teacherName" label="讲师" width="100"></el-table-column>
      <el-table-column prop="courseType" label="类型" width="100">
        <template slot-scope="scope">
          {{ scope.row.courseType === 1 ? '公开课' : '系列课' }}
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100">
        <template slot-scope="scope">
          {{ scope.row.isFree === 1 ? '免费' : '¥' + scope.row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="studentCount" label="学习人数" width="100"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="small" v-if="scope.row.status === 0" @click="handleAudit(scope.row, 1)">通过</el-button>
          <el-button type="text" size="small" class="text-warning" v-if="scope.row.status === 0" @click="handleAudit(scope.row, 2)">拒绝</el-button>
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
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入课程标题"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程类型">
              <el-select v-model="form.courseType" placeholder="请选择">
                <el-option label="公开课" :value="1"></el-option>
                <el-option label="系列课" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否免费">
              <el-radio-group v-model="form.isFree">
                <el-radio :label="1">免费</el-radio>
                <el-radio :label="0">付费</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="价格" v-if="form.isFree === 0">
          <el-input-number v-model="form.price" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入课程简介"></el-input>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCourseList, updateCourse, deleteCourse, auditCourse } from '@/api/course'

export default {
  name: 'AdminCourse',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        keyword: '',
        status: null
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        title: '',
        courseType: 1,
        isFree: 1,
        price: 0,
        description: '',
        tags: ''
      },
      rules: {
        title: [{ required: true, message: '请输入课程标题', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getCourseList({
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
      this.searchForm = { keyword: '', status: null }
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
    handleEdit(row) {
      this.dialogTitle = '编辑课程'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleAudit(row, status) {
      const action = status === 1 ? '通过' : '拒绝'
      try {
        await this.$confirm(`确定要${action}该课程吗？`, '提示', { type: 'warning' })
        await auditCourse(row.id, status)
        this.$message.success(`${action}成功`)
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该课程吗？', '提示', { type: 'warning' })
        await deleteCourse(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            await updateCourse(this.form)
            this.$message.success('操作成功')
            this.dialogVisible = false
            this.loadData()
          } catch (error) {
            console.error(error)
          } finally {
            this.submitting = false
          }
        }
      })
    },
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'success', 2: 'danger' }
      return types[status] || 'info'
    },
    getStatusLabel(status) {
      const labels = { 0: '待审核', 1: '已上架', 2: '已拒绝' }
      return labels[status] || '未知'
    }
  }
}
</script>
