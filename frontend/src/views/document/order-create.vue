<template>
  <div class="document-order-page">
    <div class="page-header">
      <h2>文书代写预约</h2>
      <p>专业律师为您提供法律文书代写、审核服务</p>
    </div>
    
    <div class="container">
      <el-card class="form-card">
        <el-form ref="orderForm" :model="orderForm" :rules="orderRules" label-width="120px">
          <el-form-item label="选择律师" prop="lawyerId">
            <el-select v-model="orderForm.lawyerId" placeholder="请选择律师" style="width: 100%" filterable>
              <el-option
                v-for="lawyer in lawyerList"
                :key="lawyer.id"
                :label="lawyer.lawyerName"
                :value="lawyer.id"
              >
                <span>{{ lawyer.lawyerName }}</span>
                <span style="float: right; color: #999; font-size: 12px">
                  代写: ¥{{ lawyer.documentPrice || 0 }} / 审核: ¥{{ lawyer.auditPrice || 0 }}
                </span>
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="服务类型" prop="orderType">
            <el-radio-group v-model="orderForm.orderType">
              <el-radio :label="1">文书代写</el-radio>
              <el-radio :label="2">文书审核</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="文书标题" prop="title">
            <el-input v-model="orderForm.title" placeholder="请输入文书标题"></el-input>
          </el-form-item>
          
          <el-form-item label="需求描述" prop="requirement">
            <el-input
              type="textarea"
              v-model="orderForm.requirement"
              :rows="5"
              placeholder="请详细描述您的需求，包括文书用途、具体要求等"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="附件上传">
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :on-success="handleUploadSuccess"
              :show-file-list="true"
              :file-list="fileList"
              accept=".doc,.docx,.pdf,.txt,.jpg,.png"
            >
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">支持 Word、PDF、TXT、图片格式文件</div>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="预估费用">
            <span class="price">¥{{ estimatedPrice }}</span>
          </el-form-item>

          <el-form-item label="账户余额" v-if="walletInfo.balance !== undefined">
            <span :class="walletInfo.balance >= estimatedPrice ? 'balance-ok' : 'balance-low'">¥{{ walletInfo.balance || '0.00' }}</span>
            <el-tag type="danger" size="small" v-if="walletInfo.balance < estimatedPrice" class="ml-10">余额不足</el-tag>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting" :disabled="walletInfo.balance < estimatedPrice && walletInfo.balance !== undefined">提交并支付</el-button>
            <el-button @click="$router.back()">取消</el-button>
            <el-link type="primary" @click="$router.push('/user/wallet')" class="ml-10">去充值</el-link>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <el-dialog title="确认支付" :visible.sync="payDialogVisible" width="400px" :close-on-click-modal="false">
      <div class="pay-info">
        <p><strong>订单金额：</strong><span class="price">¥{{ estimatedPrice }}</span></p>
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
import { getLawyerList } from '@/api/lawyer'
import { createDocumentOrder } from '@/api/document'
import { getWallet } from '@/api/wallet'

export default {
  name: 'DocumentOrderCreate',
  data() {
    return {
      lawyerList: [],
      walletInfo: {},
      orderForm: {
        lawyerId: null,
        orderType: 1,
        title: '',
        requirement: '',
        attachmentUrl: ''
      },
      orderRules: {
        lawyerId: [{ required: true, message: '请选择律师', trigger: 'change' }],
        orderType: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
        title: [{ required: true, message: '请输入文书标题', trigger: 'blur' }],
        requirement: [{ required: true, message: '请输入需求描述', trigger: 'blur' }]
      },
      fileList: [],
      uploadUrl: '/api/upload',
      uploadHeaders: {},
      submitting: false,
      paying: false,
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
    estimatedPrice() {
      const lawyer = this.lawyerList.find(l => l.id === this.orderForm.lawyerId)
      if (!lawyer) return 0
      if (this.orderForm.orderType === 1) {
        return lawyer.documentPrice || 0
      } else {
        return lawyer.auditPrice || 0
      }
    }
  },
  created() {
    const token = this.$store.getters.token
    if (token) {
      this.uploadHeaders = { Authorization: token }
    }
    this.loadLawyers()
    this.loadWallet()
    
    if (this.$route.query.lawyerId) {
      this.orderForm.lawyerId = parseInt(this.$route.query.lawyerId)
    }
  },
  methods: {
    async loadLawyers() {
      try {
        const res = await getLawyerList({ page: 1, size: 100, status: 1 })
        this.lawyerList = res.data?.records || []
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
        this.orderForm.attachmentUrl = res.data.url
        this.fileList = fileList
      }
    },
    handleSubmit() {
      this.$refs.orderForm.validate(async valid => {
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
    confirmPay() {
      this.$refs.payForm.validate(async valid => {
        if (valid) {
          this.paying = true
          try {
            await createDocumentOrder({
              ...this.orderForm,
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
.document-order-page {
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
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .form-card {
    padding: 20px;
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
    }
  }
}
</style>
