<template>
  <div class="page-container">
    <el-card>
      <div slot="header">
        <span>服务评价查看</span>
      </div>
      <div class="search-form">
        <el-form inline>
          <el-form-item label="评分">
            <el-select v-model="searchForm.score" placeholder="全部" clearable>
              <el-option label="5星" :value="5"></el-option>
              <el-option label="4星" :value="4"></el-option>
              <el-option label="3星" :value="3"></el-option>
              <el-option label="2星" :value="2"></el-option>
              <el-option label="1星" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="statistics">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <p class="stat-value">{{ statistics.avgScore }}</p>
              <p class="stat-label">平均评分</p>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <p class="stat-value">{{ statistics.totalCount }}</p>
              <p class="stat-label">评价总数</p>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <p class="stat-value">{{ statistics.goodRate }}%</p>
              <p class="stat-label">好评率</p>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <p class="stat-value">{{ statistics.fiveStarCount }}</p>
              <p class="stat-label">五星评价</p>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <el-table :data="tableData" border stripe v-loading="loading" class="mt-20">
        <el-table-column prop="userName" label="客户" width="100"></el-table-column>
        <el-table-column prop="orderTitle" label="服务内容" min-width="180"></el-table-column>
        <el-table-column prop="totalScore" label="评分" width="150">
          <template slot-scope="scope">
            <el-rate :value="scope.row.totalScore" disabled></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200"></el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="150"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleReply(scope.row)" v-if="!scope.row.reply">回复</el-button>
            <el-button type="text" size="small" @click="handleView(scope.row)" v-else>查看回复</el-button>
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
    </el-card>
    
    <el-dialog title="回复评价" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="评价内容">
          <p>{{ currentEvaluate?.content }}</p>
        </el-form-item>
        <el-form-item label="回复内容" prop="reply">
          <el-input type="textarea" v-model="form.reply" :rows="4" placeholder="请输入回复内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="submitting">提交回复</el-button>
      </div>
    </el-dialog>
    
    <el-dialog title="回复详情" :visible.sync="viewDialogVisible" width="500px">
      <el-descriptions :column="1" border v-if="currentEvaluate">
        <el-descriptions-item label="评价内容">{{ currentEvaluate.content }}</el-descriptions-item>
        <el-descriptions-item label="回复内容">{{ currentEvaluate.reply }}</el-descriptions-item>
        <el-descriptions-item label="回复时间">{{ currentEvaluate.replyTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getEvaluationList, getEvaluationStatistics, replyEvaluation } from '@/api/evaluation'

export default {
  name: 'LawyerEvaluate',
  data() {
    return {
      loading: false,
      searchForm: {
        score: null
      },
      statistics: {
        avgScore: 5.0,
        totalCount: 0,
        goodRate: 100,
        fiveStarCount: 0
      },
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      viewDialogVisible: false,
      currentEvaluate: null,
      form: {
        reply: ''
      },
      rules: {
        reply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  created() {
    this.loadData()
    this.loadStatistics()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getEvaluationList({
          page: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.tableData = res.data?.records || []
        this.total = res.data?.total || 0
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async loadStatistics() {
      try {
        const res = await getEvaluationStatistics()
        if (res.data) {
          this.statistics = {
            avgScore: res.data.avgScore || 5.0,
            totalCount: res.data.totalCount || 0,
            goodRate: res.data.goodRate || 100,
            fiveStarCount: res.data.fiveStarCount || 0
          }
        }
      } catch (error) {
        console.error(error)
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
    handleReply(row) {
      this.currentEvaluate = row
      this.form.reply = ''
      this.dialogVisible = true
    },
    handleView(row) {
      this.currentEvaluate = row
      this.viewDialogVisible = true
    },
    async submitReply() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            await replyEvaluation(this.currentEvaluate.id, { reply: this.form.reply })
            this.$message.success('回复成功')
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

<style lang="scss" scoped>
.page-container {
  .search-form {
    margin-bottom: 20px;
  }
  
  .statistics {
    background: #f5f7fa;
    padding: 20px;
    border-radius: 8px;
    
    .stat-item {
      text-align: center;
      
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #1890ff;
        margin: 0;
      }
      
      .stat-label {
        font-size: 14px;
        color: #666;
        margin: 5px 0 0;
      }
    }
  }
}
</style>
