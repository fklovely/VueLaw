<template>
  <div class="page-container">
    <el-button type="primary" class="mb-20" @click="handleAdd">添加分类</el-button>
    
    <el-table :data="tableData" border stripe v-loading="loading" row-key="id">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="分类名称" min-width="150"></el-table-column>
      <el-table-column prop="parentId" label="父分类ID" width="100"></el-table-column>
      <el-table-column prop="level" label="层级" width="80"></el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="small" class="text-danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" placeholder="请选择" clearable>
            <el-option label="无（顶级分类）" :value="0"></el-option>
            <el-option v-for="item in parentCategories" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0"></el-switch>
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
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/category'

export default {
  name: 'AdminCategory',
  data() {
    return {
      loading: false,
      tableData: [],
      parentCategories: [],
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        name: '',
        parentId: 0,
        level: 1,
        sortOrder: 0,
        status: 1
      },
      rules: {
        name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
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
        const res = await getCategoryList()
        this.tableData = res.data
        this.parentCategories = res.data.filter(item => item.parentId === 0)
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    handleAdd() {
      this.dialogTitle = '添加分类'
      this.form = { id: null, name: '', parentId: 0, level: 1, sortOrder: 0, status: 1 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑分类'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该分类吗？', '提示', { type: 'warning' })
        await deleteCategory(row.id)
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
            this.form.level = this.form.parentId === 0 ? 1 : 2
            if (this.form.id) {
              await updateCategory(this.form)
            } else {
              await addCategory(this.form)
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
