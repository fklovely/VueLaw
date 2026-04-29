<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="公告类型">
          <el-select v-model="searchForm.noticeType" placeholder="全部" clearable>
            <el-option label="公告" value="announce"></el-option>
            <el-option label="通知" value="notice"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">发布公告</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="title" label="公告标题" min-width="250"></el-table-column>
      <el-table-column prop="noticeType" label="类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.noticeType === 'announce' ? 'primary' : 'info'">
            {{ scope.row.noticeType === 'announce' ? '公告' : '通知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isTop" label="置顶" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isTop === 1 ? 'danger' : 'info'">
            {{ scope.row.isTop === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="80"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="160"></el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="small" v-if="scope.row.status !== 1" @click="handlePublish(scope.row)">发布</el-button>
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
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="form.noticeType" placeholder="请选择">
            <el-option label="公告" value="announce"></el-option>
            <el-option label="通知" value="notice"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="请输入公告内容"></el-input>
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
import { getNoticeList, addNotice, updateNotice, deleteNotice, publishNotice } from '@/api/notice'

export default {
  name: 'AdminNotice',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        noticeType: ''
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        title: '',
        noticeType: 'announce',
        content: '',
        isTop: 0
      },
      rules: {
        title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
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
        const res = await getNoticeList({
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
      this.searchForm = { noticeType: '' }
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
      this.dialogTitle = '发布公告'
      this.form = { id: null, title: '', noticeType: 'announce', content: '', isTop: 0 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑公告'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该公告吗？', '提示', { type: 'warning' })
        await deleteNotice(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    async handlePublish(row) {
      try {
        await this.$confirm('确定要发布该公告吗？', '提示', { type: 'warning' })
        await publishNotice(row.id)
        this.$message.success('发布成功')
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
              await updateNotice(this.form)
            } else {
              await addNotice(this.form)
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
