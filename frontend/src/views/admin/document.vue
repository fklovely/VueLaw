<template>
  <div class="page-container">
    <div class="search-form">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="文书名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="文书类型">
          <el-select v-model="searchForm.docType" placeholder="全部" clearable>
            <el-option label="合同" value="合同"></el-option>
            <el-option label="起诉状" value="起诉状"></el-option>
            <el-option label="遗嘱" value="遗嘱"></el-option>
            <el-option label="委托书" value="委托书"></el-option>
            <el-option label="申请书" value="申请书"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">添加文书</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="docNo" label="文书编号" width="140"></el-table-column>
      <el-table-column prop="name" label="文书名称" min-width="180"></el-table-column>
      <el-table-column prop="docType" label="文书类型" width="100"></el-table-column>
      <el-table-column prop="isPremium" label="付费类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isPremium === 1 ? 'warning' : 'success'">
            {{ scope.row.isPremium === 1 ? '付费' : '免费' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100">
        <template slot-scope="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="downloadCount" label="下载次数" width="100"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
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
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="文书名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入文书名称"></el-input>
        </el-form-item>
        <el-form-item label="文书类型" prop="docType">
          <el-select v-model="form.docType" placeholder="请选择">
            <el-option label="合同" value="合同"></el-option>
            <el-option label="起诉状" value="起诉状"></el-option>
            <el-option label="遗嘱" value="遗嘱"></el-option>
            <el-option label="委托书" value="委托书"></el-option>
            <el-option label="申请书" value="申请书"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文书说明" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入文书说明"></el-input>
        </el-form-item>
        <el-form-item label="付费类型">
          <el-radio-group v-model="form.isPremium">
            <el-radio :label="0">免费</el-radio>
            <el-radio :label="1">付费</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="价格" v-if="form.isPremium === 1">
          <el-input-number v-model="form.price" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="模板内容">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="请输入文书模板内容"></el-input>
        </el-form-item>
        <el-form-item label="文件上传">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :show-file-list="false"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <span class="ml-10" v-if="form.fileUrl">{{ form.fileUrl }}</span>
          </el-upload>
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
import { getDocumentList, addDocument, updateDocument, deleteDocument } from '@/api/document'

export default {
  name: 'AdminDocument',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      searchForm: {
        keyword: '',
        docType: ''
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        name: '',
        docType: '',
        description: '',
        content: '',
        fileUrl: '',
        isPremium: 0,
        price: 0
      },
      rules: {
        name: [{ required: true, message: '请输入文书名称', trigger: 'blur' }],
        docType: [{ required: true, message: '请选择文书类型', trigger: 'change' }]
      },
      submitting: false,
      uploadUrl: '/api/upload',
      uploadHeaders: {}
    }
  },
  created() {
    const token = this.$store.getters.token
    if (token) {
      this.uploadHeaders = { Authorization: token }
    }
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getDocumentList({
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
      this.searchForm = { keyword: '', docType: '' }
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
      this.dialogTitle = '添加文书'
      this.form = { id: null, name: '', docType: '', description: '', content: '', fileUrl: '', isPremium: 0, price: 0 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑文书'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该文书吗？', '提示', { type: 'warning' })
        await deleteDocument(row.id)
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
              await updateDocument(this.form)
            } else {
              await addDocument(this.form)
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
    },
    handleUploadSuccess(res) {
      if (res.code === 200) {
        this.form.fileUrl = res.data.url
        this.$message.success('上传成功')
      }
    }
  }
}
</script>
