<template>
  <div class="wallet-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="balance-card">
          <div class="balance-header">
            <span class="label">账户余额</span>
            <span class="balance">¥{{ wallet.balance || '0.00' }}</span>
            <span class="frozen" v-if="wallet.frozenAmount && Number(wallet.frozenAmount) > 0">平台托管：¥{{ wallet.frozenAmount }}</span>
          </div>
          <div class="balance-actions">
            <el-button type="primary" icon="el-icon-plus" @click="showRechargeDialog">充值</el-button>
            <el-button type="warning" icon="el-icon-lock" @click="showPasswordDialog" v-if="!wallet.payPasswordSet || wallet.payPasswordSet !== 1">设置支付密码</el-button>
            <el-button type="warning" icon="el-icon-edit" @click="showPasswordDialog" v-else>修改支付密码</el-button>
          </div>
        </el-card>

        <el-card class="mt-20">
          <div slot="header">快捷充值</div>
          <div class="quick-recharge">
            <el-button 
              v-for="amount in quickAmounts" 
              :key="amount"
              :type="selectedQuick === amount ? 'primary' : ''"
              @click="selectQuick(amount)"
            >
              ¥{{ amount }}
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <div slot="header">交易记录</div>
          <el-table :data="records" v-loading="recordsLoading" size="small">
            <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
            <el-table-column label="类型" width="80">
              <template slot-scope="scope">
                <el-tag :type="getRecordTypeTag(scope.row.type)" size="small">
                  {{ getRecordTypeName(scope.row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="100">
              <template slot-scope="scope">
                <span :class="getRecordAmountClass(scope.row.type)">
                  {{ getRecordAmountPrefix(scope.row.type) }}¥{{ scope.row.amount }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="变动" width="150">
              <template slot-scope="scope">
                {{ scope.row.beforeBalance }} → {{ scope.row.afterBalance }}
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明"></el-table-column>
            <el-table-column prop="createTime" label="时间" width="160"></el-table-column>
          </el-table>
          <el-pagination
            class="mt-10"
            small
            layout="total, prev, pager, next"
            :total="recordTotal"
            :page-size="20"
            :current-page="currentPage"
            @current-change="loadRecords"
          ></el-pagination>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog title="账户充值" :visible.sync="rechargeDialogVisible" width="450px">
      <el-form ref="rechargeForm" :model="rechargeForm" :rules="rechargeRules" label-width="100px">
        <el-form-item label="充值金额" prop="amount">
          <el-input-number v-model="rechargeForm.amount" :min="1" :max="100000" :precision="2" style="width: 200px"></el-input-number>
          <span class="ml-10 text-muted">元</span>
        </el-form-item>
        <el-form-item label="支付密码" prop="payPassword" v-if="wallet.payPasswordSet === 1">
          <el-input v-model="rechargeForm.payPassword" type="password" placeholder="请输入支付密码" show-password style="width: 250px"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge" :loading="recharging">确认充值</el-button>
      </div>
    </el-dialog>

    <el-dialog title="设置支付密码" :visible.sync="passwordDialogVisible" width="450px">
      <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="120px">
        <el-form-item label="原支付密码" prop="oldPassword" v-if="wallet.payPasswordSet === 1">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原支付密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="新支付密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="6位数字密码" show-password maxlength="6"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="再次输入新密码" show-password maxlength="6"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSetPassword" :loading="settingPassword">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getWallet, recharge, setPayPassword, getWalletRecords } from '@/api/wallet'

export default {
  name: 'UserWallet',
  data() {
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入支付密码'))
      } else if (!/^\d{6}$/.test(value)) {
        callback(new Error('支付密码必须为6位数字'))
      } else {
        callback()
      }
    }
    return {
      wallet: {},
      records: [],
      recordsLoading: false,
      recordTotal: 0,
      currentPage: 1,
      quickAmounts: [50, 100, 200, 500, 1000],
      selectedQuick: null,
      rechargeDialogVisible: false,
      recharging: false,
      rechargeForm: {
        amount: 100,
        payPassword: ''
      },
      rechargeRules: {
        amount: [{ required: true, message: '请输入充值金额', trigger: 'blur' }],
        payPassword: [{ required: true, message: '请输入支付密码', trigger: 'blur' }]
      },
      passwordDialogVisible: false,
      settingPassword: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入原支付密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新支付密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadWallet()
    this.loadRecords()
  },
  methods: {
    async loadWallet() {
      try {
        const res = await getWallet()
        if (res.data) {
          this.wallet = res.data
        }
      } catch (error) {
        console.error(error)
      }
    },
    async loadRecords() {
      this.recordsLoading = true
      try {
        const res = await getWalletRecords({ page: this.currentPage, size: 20 })
        if (res.data) {
          this.records = res.data
        }
      } catch (error) {
        console.error(error)
      } finally {
        this.recordsLoading = false
      }
    },
    selectQuick(amount) {
      this.selectedQuick = amount
      this.rechargeForm.amount = amount
      this.showRechargeDialog()
    },
    showRechargeDialog() {
      this.rechargeForm.payPassword = ''
      this.rechargeDialogVisible = true
    },
    showPasswordDialog() {
      this.passwordForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
      this.passwordDialogVisible = true
    },
    async handleRecharge() {
      this.$refs.rechargeForm.validate(async valid => {
        if (valid) {
          this.recharging = true
          try {
            await recharge({
              amount: this.rechargeForm.amount,
              payPassword: this.rechargeForm.payPassword || undefined
            })
            this.$message.success('充值成功')
            this.rechargeDialogVisible = false
            this.selectedQuick = null
            this.loadWallet()
            this.loadRecords()
          } catch (error) {
            console.error(error)
          } finally {
            this.recharging = false
          }
        }
      })
    },
    handleSetPassword() {
      this.$refs.passwordForm.validate(async valid => {
        if (valid) {
          if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
            this.$message.error('两次输入的密码不一致')
            return
          }
          this.settingPassword = true
          try {
            await setPayPassword(this.passwordForm)
            this.$message.success('支付密码设置成功')
            this.passwordDialogVisible = false
            this.loadWallet()
          } catch (error) {
            console.error(error)
          } finally {
            this.settingPassword = false
          }
        }
      })
    },
    validateConfirmPassword(rule, value, callback) {
      if (!value) {
        callback(new Error('请确认新密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    getRecordTypeName(type) {
      const map = { 1: '充值', 2: '支付', 3: '退款', 4: '收入' }
      return map[type] || '变动'
    },
    getRecordTypeTag(type) {
      const map = { 1: 'success', 2: 'danger', 3: 'warning', 4: 'success' }
      return map[type] || 'info'
    },
    getRecordAmountClass(type) {
      return type === 2 ? 'text-danger' : 'text-success'
    },
    getRecordAmountPrefix(type) {
      return type === 2 ? '-' : '+'
    }
  }
}
</script>

<style lang="scss" scoped>
.wallet-page {
  .balance-card {
    .balance-header {
      text-align: center;
      padding: 30px 0;
      
      .label {
        display: block;
        color: #999;
        font-size: 14px;
        margin-bottom: 15px;
      }
      
      .balance {
        display: block;
        font-size: 42px;
        font-weight: 600;
        color: #409eff;
      }

      .frozen {
        display: block;
        margin-top: 10px;
        color: #e6a23c;
        font-size: 14px;
      }
    }
    
    .balance-actions {
      display: flex;
      justify-content: center;
      gap: 15px;
      padding-top: 20px;
      border-top: 1px solid #eee;
    }
  }

  .quick-recharge {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;

    .el-button {
      flex: 1;
      min-width: 70px;
    }
  }

  .text-success {
    color: #67c23a;
    font-weight: 600;
  }

  .text-danger {
    color: #f56c6c;
    font-weight: 600;
  }

  .mt-10 {
    margin-top: 10px;
  }

  .mt-20 {
    margin-top: 20px;
  }

  .ml-10 {
    margin-left: 10px;
  }

  .text-muted {
    color: #999;
  }
}
</style>
