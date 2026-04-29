<template>
  <div class="document-list-page">
    <div class="page-header">
      <h2>法律文书</h2>
      <p>提供常用法律文书模板下载、定制代写、审核校对服务</p>
    </div>
    
    <div class="container">
      <div class="service-cards">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card class="service-card" shadow="hover" @click.native="$router.push('/document/order')">
              <div class="service-icon" style="background: #e6f7ff;">
                <i class="el-icon-edit" style="color: #1890ff;"></i>
              </div>
              <h3>文书代写</h3>
              <p>专业律师为您代写各类法律文书</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="service-card" shadow="hover" @click.native="$router.push('/document/order')">
              <div class="service-icon" style="background: #fff7e6;">
                <i class="el-icon-document-checked" style="color: #fa8c16;"></i>
              </div>
              <h3>文书审核</h3>
              <p>律师帮您审核文书，规避法律风险</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="service-card" shadow="hover">
              <div class="service-icon" style="background: #f6ffed;">
                <i class="el-icon-download" style="color: #52c41a;"></i>
              </div>
              <h3>模板下载</h3>
              <p>海量法律文书模板免费下载</p>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <div class="search-box">
        <el-input v-model="keyword" placeholder="搜索文书名称" class="search-input" @keyup.enter.native="handleSearch">
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
        <div class="filter-tags">
          <span class="label">文书类型：</span>
          <el-tag v-for="item in docTypeOptions" :key="item" :effect="docType === item ? 'dark' : 'plain'" @click="docType = docType === item ? '' : item">{{ item }}</el-tag>
        </div>
      </div>
      
      <el-empty v-if="documentList.length === 0 && !loading" description="暂无文书数据">
        <el-button type="primary" @click="handleUpload">上传文书</el-button>
      </el-empty>
      
      <template v-else>
        <el-row :gutter="20">
          <el-col :span="6" v-for="doc in documentList" :key="doc.id">
            <el-card class="document-card" @click.native="handleDetail(doc)">
              <div class="doc-icon">
                <i class="el-icon-document"></i>
              </div>
              <h4>{{ doc.name }}</h4>
              <p class="doc-type">{{ doc.docType }}</p>
              <p class="doc-desc">{{ doc.description }}</p>
              <div class="doc-footer">
                <span class="price" v-if="doc.isPremium === 1">¥{{ doc.price }}</span>
                <span class="price free" v-else>免费</span>
                <span class="downloads"><i class="el-icon-download"></i> {{ doc.downloadCount }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-pagination
          class="pagination"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </template>
    </div>
    
    <el-dialog title="上传文书" :visible.sync="uploadDialogVisible" width="600px">
      <el-form ref="uploadForm" :model="uploadForm" :rules="uploadRules" label-width="100px">
        <el-form-item label="文书名称" prop="name">
          <el-input v-model="uploadForm.name" placeholder="请输入文书名称"></el-input>
        </el-form-item>
        <el-form-item label="文书类型" prop="docType">
          <el-select v-model="uploadForm.docType" placeholder="请选择文书类型" style="width: 100%">
            <el-option label="合同" value="合同"></el-option>
            <el-option label="起诉状" value="起诉状"></el-option>
            <el-option label="遗嘱" value="遗嘱"></el-option>
            <el-option label="委托书" value="委托书"></el-option>
            <el-option label="申请书" value="申请书"></el-option>
            <el-option label="答辩状" value="答辩状"></el-option>
            <el-option label="上诉状" value="上诉状"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文书描述" prop="description">
          <el-input type="textarea" v-model="uploadForm.description" :rows="3" placeholder="请输入文书描述"></el-input>
        </el-form-item>
        <el-form-item label="文书文件" prop="fileUrl">
          <el-upload
            class="upload-area"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :show-file-list="true"
            :file-list="fileList"
            accept=".doc,.docx,.pdf,.txt"
            :limit="1"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">支持 Word、PDF、TXT 格式文件</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="是否付费">
          <el-radio-group v-model="uploadForm.isPremium">
            <el-radio :label="0">免费</el-radio>
            <el-radio :label="1">付费</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="价格" v-if="uploadForm.isPremium === 1" prop="price">
          <el-input-number v-model="uploadForm.price" :min="0" :precision="2"></el-input-number>
          <span class="ml-10">元</span>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDocumentList, addDocument, downloadDocument } from '@/api/document'

export default {
  name: 'DocumentList',
  data() {
    return {
      loading: false,
      keyword: '',
      docType: '',
      docTypeOptions: ['合同', '起诉状', '遗嘱', '委托书', '申请书'],
      documentList: [],
      total: 0,
      currentPage: 1,
      pageSize: 12,
      uploadDialogVisible: false,
      uploadForm: {
        name: '',
        docType: '',
        description: '',
        fileUrl: '',
        isPremium: 0,
        price: 0
      },
      uploadRules: {
        name: [{ required: true, message: '请输入文书名称', trigger: 'blur' }],
        docType: [{ required: true, message: '请选择文书类型', trigger: 'change' }],
        description: [{ required: true, message: '请输入文书描述', trigger: 'blur' }],
        fileUrl: [{ required: true, message: '请上传文书文件', trigger: 'change' }]
      },
      fileList: [],
      uploadUrl: '/api/upload',
      uploadHeaders: {},
      uploading: false
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
          keyword: this.keyword,
          docType: this.docType
        })
        this.documentList = res.data?.records || []
        this.total = res.data?.total || 0
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
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    handleDetail(doc) {
      if (doc.fileUrl) {
        const link = document.createElement('a')
        link.href = doc.fileUrl
        link.download = doc.name || '文书文件'
        link.target = '_blank'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        downloadDocument(doc.id)
      } else {
        this.$message.warning('该文书暂无可下载文件')
      }
    },
    handleUpload() {
      this.uploadForm = {
        name: '',
        docType: '',
        description: '',
        fileUrl: '',
        isPremium: 0,
        price: 0
      }
      this.fileList = []
      this.uploadDialogVisible = true
    },
    handleUploadSuccess(res, file, fileList) {
      if (res.code === 200) {
        this.uploadForm.fileUrl = res.data.url
        this.fileList = fileList
        this.$message.success('上传成功')
      }
    },
    submitUpload() {
      this.$refs.uploadForm.validate(async valid => {
        if (valid) {
          if (!this.uploadForm.fileUrl) {
            this.$message.warning('请上传文书文件')
            return
          }
          this.uploading = true
          try {
            await addDocument(this.uploadForm)
            this.$message.success('上传成功')
            this.uploadDialogVisible = false
            this.loadData()
          } catch (error) {
            console.error(error)
          } finally {
            this.uploading = false
          }
        }
      })
    }
  },
  watch: {
    docType() {
      this.handleSearch()
    }
  }
}
</script>

<style lang="scss" scoped>
.document-list-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  
  .page-header {
    background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
    padding: 40px 0;
    text-align: center;
    color: #fff;
    
    h2 {
      font-size: 28px;
      margin: 0 0 10px;
    }
    
    p {
      margin: 0;
      opacity: 0.9;
    }
  }
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .service-cards {
    margin-bottom: 30px;
    
    .service-card {
      text-align: center;
      cursor: pointer;
      padding: 20px;
      
      &:hover {
        transform: translateY(-5px);
      }
      
      .service-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 15px;
        
        i {
          font-size: 28px;
        }
      }
      
      h3 {
        font-size: 18px;
        margin: 0 0 8px;
        color: #333;
      }
      
      p {
        font-size: 14px;
        color: #999;
        margin: 0;
      }
    }
  }
  
  .search-box {
    margin-bottom: 20px;
    
    .search-input {
      max-width: 500px;
      margin-bottom: 15px;
    }
    
    .filter-tags {
      .label {
        margin-right: 10px;
        color: #666;
      }
      
      .el-tag {
        margin-right: 10px;
        cursor: pointer;
      }
    }
  }
  
  .document-card {
    margin-bottom: 20px;
    cursor: pointer;
    transition: all 0.3s;
    text-align: center;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    .doc-icon {
      width: 60px;
      height: 60px;
      background: #e6f7ff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 15px;
      
      i {
        font-size: 28px;
        color: #1890ff;
      }
    }
    
    h4 {
      margin: 0 0 10px;
      font-size: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .doc-type {
      color: #1890ff;
      font-size: 13px;
      margin: 0 0 10px;
    }
    
    .doc-desc {
      color: #999;
      font-size: 13px;
      line-height: 1.5;
      height: 40px;
      overflow: hidden;
      margin: 0 0 15px;
    }
    
    .doc-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .price {
        font-size: 18px;
        color: #f56c6c;
        font-weight: 600;
        
        &.free {
          color: #52c41a;
        }
      }
      
      .downloads {
        color: #999;
        font-size: 13px;
      }
    }
  }
  
  .pagination {
    text-align: center;
    margin-top: 20px;
  }
  
  .upload-area {
    width: 100%;
  }
}
</style>
