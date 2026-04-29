<template>
  <div class="page-container">
    <el-button type="primary" class="mb-20" @click="handleAdd">发布文章</el-button>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="title" label="文章标题" min-width="200"></el-table-column>
      <el-table-column prop="articleType" label="类型" width="100">
        <template slot-scope="scope">
          {{ scope.row.articleType === 1 ? '普法文章' : '案例解读' }}
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="80"></el-table-column>
      <el-table-column prop="likeCount" label="点赞数" width="80"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="160"></el-table-column>
      <el-table-column label="操作" width="180">
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
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="800px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题"></el-input>
        </el-form-item>
        <el-form-item label="文章类型">
          <el-select v-model="form.articleType" placeholder="请选择">
            <el-option label="普法文章" :value="1"></el-option>
            <el-option label="案例解读" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文章摘要">
          <el-input type="textarea" v-model="form.summary" :rows="3" placeholder="请输入文章摘要"></el-input>
        </el-form-item>
        <el-form-item label="文章内容" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="10" placeholder="请输入文章内容"></el-input>
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
import { getMyArticleList, addArticle, updateArticle, deleteArticle, publishArticle } from '@/api/article'

export default {
  name: 'LawyerArticle',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        title: '',
        articleType: 1,
        summary: '',
        content: '',
        tags: ''
      },
      rules: {
        title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
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
        const res = await getMyArticleList({ page: this.currentPage, size: this.pageSize })
        this.tableData = res.data.records
        this.total = res.data.total
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
    handleAdd() {
      this.dialogTitle = '发布文章'
      this.form = { id: null, title: '', articleType: 1, summary: '', content: '', tags: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑文章'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该文章吗？', '提示', { type: 'warning' })
        await deleteArticle(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') console.error(error)
      }
    },
    async handlePublish(row) {
      try {
        await this.$confirm('确定要发布该文章吗？', '提示', { type: 'warning' })
        await publishArticle(row.id)
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
              await updateArticle(this.form)
            } else {
              await addArticle(this.form)
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
