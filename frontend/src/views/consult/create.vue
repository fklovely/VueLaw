<template>
  <div class="consult-create-page">
    <div class="container">
      <el-card>
        <div slot="header">
          <span>发起咨询</span>
        </div>
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" style="max-width: 600px">
          <el-form-item label="咨询律师">
            <span>{{ lawyer.lawyerName }} - {{ lawyer.lawFirm }}</span>
          </el-form-item>
          <el-form-item label="咨询分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择咨询分类">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="咨询方式">
            <span>图文咨询 (¥{{ lawyer.consultPrice }})</span>
          </el-form-item>
          <el-form-item label="咨询标题" prop="title">
            <el-input v-model="form.title" placeholder="请简要描述您的法律问题"></el-input>
          </el-form-item>
          <el-form-item label="详细描述" prop="content">
            <el-input type="textarea" v-model="form.content" :rows="6" placeholder="请详细描述您的法律问题，以便律师更好地为您解答"></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
          </el-form-item>
          <el-form-item label="附件">
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :on-success="handleUploadSuccess"
              :show-file-list="true"
              :file-list="fileList"
            >
              <el-button size="small" type="primary">上传附件</el-button>
              <div slot="tip" class="el-upload__tip">支持上传图片、文档等文件</div>
            </el-upload>
          </el-form-item>
          <el-form-item label="订单金额">
            <span class="price">¥{{ getPrice }}</span>
          </el-form-item>
          <el-form-item label="账户余额" v-if="walletInfo.balance !== undefined">
            <span :class="walletInfo.balance >= getPrice ? 'balance-ok' : 'balance-low'">¥{{ walletInfo.balance || '0.00' }}</span>
            <el-tag type="danger" size="small" v-if="walletInfo.balance < getPrice" class="ml-10">余额不足</el-tag>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting" :disabled="walletInfo.balance < getPrice && walletInfo.balance !== undefined">提交并支付</el-button>
            <el-button size="large" @click="$router.back()">取消</el-button>
            <el-link type="primary" @click="$router.push('/user/wallet')" class="ml-10">去充值</el-link>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <el-dialog title="确认支付" :visible.sync="payDialogVisible" width="400px" :close-on-click-modal="false">
      <div class="pay-info">
        <p><strong>订单金额：</strong><span class="price">¥{{ getPrice }}</span></p>
        <p><strong>支付方式：</strong>余额支付</p>
        <p><strong>账户余额：</strong>¥{{ walletInfo.balance || '0.00' }}</p>
      </div>
      <el-form ref="payForm" :model="payForm" :rules="payRules" label-width="100px" style="margin-top: 20px">
        <el-form-item label="支付密码" prop="payPassword">
          <el-input v-model="payForm.payPassword" type="password" placeholder="请输入6位支付密码" show-password maxlength="6"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay" :loading="paying">确认支付</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getLawyerDetail } from '@/api/lawyer'
import { getCategoryList } from '@/api/category'
import { createConsult } from '@/api/consult'
import { getWallet } from '@/api/wallet'

export default {
  name: 'ConsultCreate',
  data() {
    return {
      lawyer: {},
      categories: [],
      walletInfo: {},
      form: {
        lawyerId: null,
        categoryId: null,
        consultType: 1,
        title: '',
        content: '',
        phone: '',
        attachments: ''
      },
      rules: {
        categoryId: [{ required: true, message: '请选择咨询分类', trigger: 'change' }],
        title: [{ required: true, message: '请输入咨询标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入详细描述', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      },
      fileList: [],
      submitting: false,
      paying: false,
      uploadUrl: '/api/upload',
      uploadHeaders: {},
      payDialogVisible: false,
      payForm: {
        payPassword: ''
      },
      payRules: {
        payPassword: [{ required: true, message: '请输入支付密码', trigger: 'blur' }]
      }
    }
  },
  computed: {
    getPrice() {
      return this.lawyer.consultPrice || 0
    }
  },
  created() {
    const token = this.$store.getters.token
    if (token) {
      this.uploadHeaders = { Authorization: token }
    }
    this.loadLawyer()
    this.loadCategories()
    this.loadWallet()
  },
  methods: {
    async loadLawyer() {
      try {
        const res = await getLawyerDetail(this.$route.params.lawyerId)
        this.lawyer = res.data
        this.form.lawyerId = this.lawyer.userId
      } catch (error) {
        console.error(error)
      }
    },
    async loadCategories() {
      try {
        const res = await getCategoryList()
        this.categories = res.data
      } catch (error) {
        console.error(error)
      }
    },
    async loadWallet() {
      try {
        const res = await getWallet()
        if (res.data) {
          this.walletInfo = res.data
        }
      } catch (error) {
        console.error(error)
      }
    },
    handleUploadSuccess(res, file, fileList) {
      if (res.code === 200) {
        this.fileList = fileList
      }
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          if (this.walletInfo.payPasswordSet !== 1) {
            this.$confirm('您还未设置支付密码，是否前往设置？', '提示', { type: 'warning' }).then(() => {
              this.$router.push('/user/wallet')
            }).catch(() => {})
            return
          }
          this.payDialogVisible = true
        }
      })
    },
    async confirmPay() {
      this.$refs.payForm.validate(async valid => {
        if (valid) {
          this.paying = true
          try {
            await createConsult({
              ...this.form,
              payPassword: this.payForm.payPassword
            })
            
            this.$message.success('支付成功，订单已提交')
            this.payDialogVisible = false
            this.$router.push('/user/orders')
          } catch (error) {
            console.error(error)
          } finally {
            this.paying = false
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.consult-create-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  
  .container {
    max-width: 800px;
    margin: 0 auto;
  }
  
  .price {
    font-size: 24px;
    color: #f56c6c;
    font-weight: 600;
  }

  .balance-ok {
    color: #67c23a;
    font-size: 18px;
    font-weight: 600;
  }

  .balance-low {
    color: #f56c6c;
    font-size: 18px;
    font-weight: 600;
  }

  .ml-10 {
    margin-left: 10px;
  }

  .pay-info {
    p {
      margin: 12px 0;
      font-size: 15px;

      .price {
        font-size: 22px;
      }
    }
  }
}
</style>
