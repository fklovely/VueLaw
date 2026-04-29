<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="案例标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="案件类型">
          <el-input v-model="searchForm.caseType" placeholder="案件类型" clearable></el-input>
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="searchForm.province" placeholder="省份" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">添加案例</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="caseNo" label="案例编号" width="140"></el-table-column>
      <el-table-column prop="title" label="案例标题" min-width="200"></el-table-column>
      <el-table-column prop="caseType" label="案件类型" width="100"></el-table-column>
      <el-table-column prop="courtName" label="法院名称" width="150"></el-table-column>
      <el-table-column prop="judgeDate" label="判决日期" width="120"></el-table-column>
      <el-table-column prop="province" label="省份" width="100"></el-table-column>
      <el-table-column prop="viewCount" label="浏览次数" width="100"></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
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
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="800px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="案例标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入案例标题"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案件类型" prop="caseType">
              <el-input v-model="form.caseType" placeholder="请输入案件类型"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="法院名称">
              <el-input v-model="form.courtName" placeholder="请输入法院名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案号">
              <el-input v-model="form.caseNoInner" placeholder="请输入案号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="判决日期">
              <el-date-picker v-model="form.judgeDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="省份">
              <el-input v-model="form.province" placeholder="请输入省份"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市">
              <el-input v-model="form.city" placeholder="请输入城市"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="案例摘要">
          <el-input type="textarea" v-model="form.summary" :rows="3" placeholder="请输入案例摘要"></el-input>
        </el-form-item>
        <el-form-item label="判决书全文">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="请输入判决书全文"></el-input>
        </el-form-item>
        <el-form-item label="判决要点">
          <el-input type="textarea" v-model="form.keyPoints" :rows="3" placeholder="请输入判决要点"></el-input>
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
import { getCaseList, addCase, updateCase, deleteCase } from '@/api/case'

export default {
  name: 'AdminCase',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        keyword: '',
        caseType: '',
        province: ''
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        title: '',
        caseType: '',
        courtName: '',
        caseNoInner: '',
        judgeDate: '',
        province: '',
        city: '',
        summary: '',
        content: '',
        keyPoints: '',
        tags: ''
      },
      rules: {
        title: [{ required: true, message: '请输入案例标题', trigger: 'blur' }],
        caseType: [{ required: true, message: '请输入案件类型', trigger: 'blur' }]
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
        const res = await getCaseList({
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
      this.searchForm = { keyword: '', caseType: '', province: '' }
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
      this.dialogTitle = '添加案例'
      this.form = { id: null, title: '', caseType: '', courtName: '', caseNoInner: '', judgeDate: '', province: '', city: '', summary: '', content: '', keyPoints: '', tags: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑案例'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该案例吗？', '提示', { type: 'warning' })
        await deleteCase(row.id)
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
            if (this.form.id) {
              await updateCase(this.form)
            } else {
              await addCase(this.form)
            }
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
    }
  }
}
</script>
