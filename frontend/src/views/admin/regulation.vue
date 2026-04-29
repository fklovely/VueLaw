<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="法规标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="法规类型">
          <el-select v-model="searchForm.regType" placeholder="全部" clearable>
            <el-option label="法律" value="法律"></el-option>
            <el-option label="行政法规" value="行政法规"></el-option>
            <el-option label="司法解释" value="司法解释"></el-option>
            <el-option label="部门规章" value="部门规章"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">添加法规</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="regNo" label="法规编号" width="140"></el-table-column>
      <el-table-column prop="title" label="法规标题" min-width="250"></el-table-column>
      <el-table-column prop="regType" label="法规类型" width="100"></el-table-column>
      <el-table-column prop="issueOrg" label="发布机构" width="150"></el-table-column>
      <el-table-column prop="issueDate" label="发布日期" width="120"></el-table-column>
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
        <el-form-item label="法规标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入法规标题"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="法规类型" prop="regType">
              <el-select v-model="form.regType" placeholder="请选择">
                <el-option label="法律" value="法律"></el-option>
                <el-option label="行政法规" value="行政法规"></el-option>
                <el-option label="司法解释" value="司法解释"></el-option>
                <el-option label="部门规章" value="部门规章"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布机构">
              <el-input v-model="form.issueOrg" placeholder="请输入发布机构"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发布日期">
              <el-date-picker v-model="form.issueDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生效日期">
              <el-date-picker v-model="form.effectiveDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="法规摘要">
          <el-input type="textarea" v-model="form.summary" :rows="3" placeholder="请输入法规摘要"></el-input>
        </el-form-item>
        <el-form-item label="法规内容">
          <el-input type="textarea" v-model="form.content" :rows="8" placeholder="请输入法规内容"></el-input>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="form.keywords" placeholder="多个关键词用逗号分隔"></el-input>
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
import { getRegulationList, addRegulation, updateRegulation, deleteRegulation } from '@/api/regulation'

export default {
  name: 'AdminRegulation',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        keyword: '',
        regType: ''
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        title: '',
        regType: '',
        issueOrg: '',
        issueDate: '',
        effectiveDate: '',
        summary: '',
        content: '',
        keywords: ''
      },
      rules: {
        title: [{ required: true, message: '请输入法规标题', trigger: 'blur' }],
        regType: [{ required: true, message: '请选择法规类型', trigger: 'change' }]
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
        const res = await getRegulationList({
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
      this.searchForm = { keyword: '', regType: '' }
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
      this.dialogTitle = '添加法规'
      this.form = { id: null, title: '', regType: '', issueOrg: '', issueDate: '', effectiveDate: '', summary: '', content: '', keywords: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑法规'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该法规吗？', '提示', { type: 'warning' })
        await deleteRegulation(row.id)
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
              await updateRegulation(this.form)
            } else {
              await addRegulation(this.form)
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
