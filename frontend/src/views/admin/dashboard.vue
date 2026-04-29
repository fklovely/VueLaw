<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea, #764ba2)">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.userCount }}</p>
            <p class="stat-label">注册用户</p>
            <p class="stat-today">今日新增 {{ statistics.todayUserCount }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb, #f5576c)">
            <i class="el-icon-s-custom"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.lawyerCount }}</p>
            <p class="stat-label">认证律师</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe)">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.consultCount }}</p>
            <p class="stat-label">咨询订单</p>
            <p class="stat-today">今日新增 {{ statistics.todayConsultCount }}</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7)">
            <i class="el-icon-money"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.totalAmount }}</p>
            <p class="stat-label">交易金额</p>
            <p class="stat-today">今日 {{ statistics.todayAmount }}</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="6">
        <div class="stat-card small">
          <div class="stat-icon small" style="background: linear-gradient(135deg, #fa709a, #fee140)">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.docOrderCount }}</p>
            <p class="stat-label">文书订单</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card small">
          <div class="stat-icon small" style="background: linear-gradient(135deg, #a8edea, #fed6e3)">
            <i class="el-icon-tickets"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.caseCount }}</p>
            <p class="stat-label">案例库</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card small">
          <div class="stat-icon small" style="background: linear-gradient(135deg, #d299c2, #fef9d7)">
            <i class="el-icon-reading"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.articleCount }}</p>
            <p class="stat-label">文章资讯</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card small">
          <div class="stat-icon small" style="background: linear-gradient(135deg, #89f7fe, #66a6ff)">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.evaluationCount }}</p>
            <p class="stat-label">服务评价</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card>
          <div slot="header" class="card-header">
            <span>待处理事项</span>
            <el-tag type="danger" v-if="totalPending > 0">{{ totalPending }}项待处理</el-tag>
          </div>
          <div class="todo-list">
            <div class="todo-item" @click="$router.push('/admin/lawyer')">
              <div class="todo-left">
                <i class="el-icon-s-check" style="color: #e6a23c"></i>
                <span class="todo-label">待审核律师</span>
              </div>
              <el-badge :value="statistics.pendingLawyer" type="warning" v-if="statistics.pendingLawyer > 0"></el-badge>
              <el-tag size="small" type="success" v-else>无</el-tag>
            </div>
            <div class="todo-item" @click="$router.push('/admin/consult')">
              <div class="todo-left">
                <i class="el-icon-chat-line-round" style="color: #f56c6c"></i>
                <span class="todo-label">待处理咨询</span>
              </div>
              <el-badge :value="statistics.pendingConsult" type="danger" v-if="statistics.pendingConsult > 0"></el-badge>
              <el-tag size="small" type="success" v-else>无</el-tag>
            </div>
            <div class="todo-item" @click="$router.push('/admin/article')">
              <div class="todo-left">
                <i class="el-icon-document-checked" style="color: #409eff"></i>
                <span class="todo-label">待审核文章</span>
              </div>
              <el-badge :value="statistics.pendingArticle" type="info" v-if="statistics.pendingArticle > 0"></el-badge>
              <el-tag size="small" type="success" v-else>无</el-tag>
            </div>
            <div class="todo-item" @click="$router.push('/admin/consult')">
              <div class="todo-left">
                <i class="el-icon-loading" style="color: #67c23a"></i>
                <span class="todo-label">进行中订单</span>
              </div>
              <el-badge :value="statistics.processingOrder" type="primary" v-if="statistics.processingOrder > 0"></el-badge>
              <el-tag size="small" type="success" v-else>无</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header">
            <span>快捷操作</span>
          </div>
          <div class="quick-actions">
            <el-button type="primary" icon="el-icon-plus" @click="$router.push('/admin/user')">添加用户</el-button>
            <el-button type="success" icon="el-icon-plus" @click="$router.push('/admin/case')">添加案例</el-button>
            <el-button type="warning" icon="el-icon-plus" @click="$router.push('/admin/notice')">发布公告</el-button>
            <el-button type="info" icon="el-icon-plus" @click="$router.push('/admin/article')">发布文章</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header">
            <span>最近注册用户</span>
            <el-link type="primary" @click="$router.push('/admin/user')">查看全部</el-link>
          </div>
          <el-table :data="recentUsers" style="width: 100%" size="small">
            <el-table-column label="用户" width="180">
              <template slot-scope="scope">
                <div class="user-info">
                  <el-avatar :size="32" :src="scope.row.avatar || defaultAvatar"></el-avatar>
                  <span class="user-name">{{ scope.row.realName || scope.row.username }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="类型" width="80">
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row.role === 'lawyer' ? 'warning' : 'primary'">
                  {{ scope.row.role === 'lawyer' ? '律师' : (scope.row.role === 'admin' ? '管理员' : '用户') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="注册时间" prop="createTime">
              <template slot-scope="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header">
            <span>最近咨询订单</span>
            <el-link type="primary" @click="$router.push('/admin/consult')">查看全部</el-link>
          </div>
          <el-table :data="recentOrders" style="width: 100%" size="small">
            <el-table-column label="订单号" prop="orderNo" width="140"></el-table-column>
            <el-table-column label="标题" prop="title" show-overflow-tooltip></el-table-column>
            <el-table-column label="用户" prop="userName" width="80"></el-table-column>
            <el-table-column label="状态" width="80">
              <template slot-scope="scope">
                <el-tag size="mini" :type="getOrderStatusType(scope.row.status)">
                  {{ getOrderStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="金额" width="80">
              <template slot-scope="scope">
                <span class="price">¥{{ scope.row.price }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>订单状态分布</span>
          </div>
          <div class="distribution-chart">
            <div class="chart-item" v-for="(item, index) in orderDistribution.statusDistribution" :key="index">
              <div class="chart-label">{{ item.name }}</div>
              <el-progress 
                :percentage="getPercentage(item.value, totalOrders)" 
                :color="getChartColor(index)"
                :stroke-width="20"
              ></el-progress>
              <div class="chart-value">{{ item.value }} 单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>咨询类型分布</span>
          </div>
          <div class="distribution-chart">
            <div class="chart-item" v-for="(item, index) in orderDistribution.typeDistribution" :key="index">
              <div class="chart-label">{{ item.name }}</div>
              <el-progress 
                :percentage="getPercentage(item.value, totalTypeOrders)" 
                :color="getTypeColor(index)"
                :stroke-width="20"
              ></el-progress>
              <div class="chart-value">{{ item.value }} 单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>近7天数据趋势</span>
          </div>
          <div class="trend-chart">
            <div class="trend-header">
              <div class="trend-legend">
                <span class="legend-item"><i class="dot" style="background: #409eff"></i>新增用户</span>
                <span class="legend-item"><i class="dot" style="background: #67c23a"></i>新增订单</span>
              </div>
            </div>
            <div class="trend-content">
              <div class="trend-bars">
                <div class="trend-bar-group" v-for="(item, index) in statistics.trendData" :key="index">
                  <div class="bar-container">
                    <div class="bar user-bar" :style="{ height: getBarHeight(item.userCount, maxTrendValue) }">
                      <span class="bar-value">{{ item.userCount }}</span>
                    </div>
                    <div class="bar order-bar" :style="{ height: getBarHeight(item.orderCount, maxTrendValue) }">
                      <span class="bar-value">{{ item.orderCount }}</span>
                    </div>
                  </div>
                  <div class="bar-date">{{ item.date }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getAdminStatistics, getRecentUsers, getRecentOrders, getOrderDistribution } from '@/api/user'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      statistics: {
        userCount: 0,
        todayUserCount: 0,
        lawyerCount: 0,
        consultCount: 0,
        todayConsultCount: 0,
        docOrderCount: 0,
        todayDocCount: 0,
        totalAmount: '¥0',
        todayAmount: '¥0',
        caseCount: 0,
        articleCount: 0,
        noticeCount: 0,
        evaluationCount: 0,
        pendingLawyer: 0,
        pendingConsult: 0,
        pendingArticle: 0,
        processingOrder: 0,
        trendData: []
      },
      recentUsers: [],
      recentOrders: [],
      orderDistribution: {
        statusDistribution: [],
        typeDistribution: []
      }
    }
  },
  computed: {
    totalPending() {
      return this.statistics.pendingLawyer + this.statistics.pendingConsult + this.statistics.pendingArticle
    },
    totalOrders() {
      return this.orderDistribution.statusDistribution.reduce((sum, item) => sum + item.value, 0) || 1
    },
    totalTypeOrders() {
      return this.orderDistribution.typeDistribution.reduce((sum, item) => sum + item.value, 0) || 1
    },
    maxTrendValue() {
      if (!this.statistics.trendData || this.statistics.trendData.length === 0) return 10
      let max = 0
      this.statistics.trendData.forEach(item => {
        max = Math.max(max, item.userCount, item.orderCount)
      })
      return max || 10
    }
  },
  created() {
    this.loadStatistics()
    this.loadRecentUsers()
    this.loadRecentOrders()
    this.loadOrderDistribution()
  },
  methods: {
    async loadStatistics() {
      try {
        const res = await getAdminStatistics()
        if (res.data) {
          this.statistics = res.data
        }
      } catch (error) {
        console.error(error)
      }
    },
    async loadRecentUsers() {
      try {
        const res = await getRecentUsers()
        if (res.data) {
          this.recentUsers = res.data
        }
      } catch (error) {
        console.error(error)
      }
    },
    async loadRecentOrders() {
      try {
        const res = await getRecentOrders()
        if (res.data) {
          this.recentOrders = res.data
        }
      } catch (error) {
        console.error(error)
      }
    },
    async loadOrderDistribution() {
      try {
        const res = await getOrderDistribution()
        if (res.data) {
          this.orderDistribution = res.data
        }
      } catch (error) {
        console.error(error)
      }
    },
    formatTime(time) {
      if (!time) return '-'
      const date = new Date(time)
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${month}-${day} ${hours}:${minutes}`
    },
    getOrderStatusType(status) {
      const types = { 1: 'info', 2: 'warning', 3: 'primary', 4: 'success', 5: 'danger' }
      return types[status] || 'info'
    },
    getOrderStatusText(status) {
      const texts = { 1: '待接单', 2: '已接单', 3: '进行中', 4: '已完成', 5: '已取消' }
      return texts[status] || '未知'
    },
    getPercentage(value, total) {
      if (total === 0) return 0
      return Math.round((value / total) * 100)
    },
    getChartColor(index) {
      const colors = ['#909399', '#e6a23c', '#409eff', '#67c23a', '#f56c6c']
      return colors[index] || '#409eff'
    },
    getTypeColor(index) {
      const colors = ['#409eff', '#67c23a', '#e6a23c']
      return colors[index] || '#409eff'
    },
    getBarHeight(value, max) {
      if (max === 0) return '0px'
      const height = Math.max(20, (value / max) * 150)
      return height + 'px'
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    
    &.small {
      padding: 15px;
    }
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      &.small {
        width: 45px;
        height: 45px;
        
        i {
          font-size: 20px;
        }
      }
      
      i {
        font-size: 28px;
        color: #fff;
      }
    }
    
    .stat-info {
      margin-left: 20px;
      
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #333;
      }
      
      .stat-label {
        font-size: 14px;
        color: #999;
        margin-top: 5px;
      }
      
      .stat-today {
        font-size: 12px;
        color: #67c23a;
        margin-top: 3px;
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .todo-list {
    .todo-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 15px 0;
      border-bottom: 1px solid #eee;
      cursor: pointer;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background: #f5f7fa;
        margin: 0 -20px;
        padding: 15px 20px;
      }
      
      .todo-left {
        display: flex;
        align-items: center;
        
        i {
          font-size: 18px;
          margin-right: 10px;
        }
      }
      
      .todo-label {
        font-size: 15px;
        color: #333;
      }
    }
  }
  
  .quick-actions {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
    
    .el-button {
      width: 100%;
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    
    .user-name {
      margin-left: 10px;
      color: #333;
    }
  }
  
  .price {
    color: #f56c6c;
    font-weight: 600;
  }
  
  .distribution-chart {
    .chart-item {
      display: flex;
      align-items: center;
      margin-bottom: 15px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .chart-label {
        width: 70px;
        font-size: 14px;
        color: #666;
      }
      
      .el-progress {
        flex: 1;
        margin: 0 15px;
      }
      
      .chart-value {
        width: 60px;
        text-align: right;
        font-size: 14px;
        color: #333;
        font-weight: 500;
      }
    }
  }
  
  .trend-chart {
    .trend-header {
      margin-bottom: 20px;
      
      .trend-legend {
        display: flex;
        justify-content: center;
        gap: 30px;
        
        .legend-item {
          display: flex;
          align-items: center;
          font-size: 14px;
          color: #666;
          
          .dot {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            margin-right: 8px;
          }
        }
      }
    }
    
    .trend-content {
      .trend-bars {
        display: flex;
        justify-content: space-around;
        align-items: flex-end;
        height: 200px;
        padding: 20px 0;
        border-bottom: 1px solid #eee;
        
        .trend-bar-group {
          display: flex;
          flex-direction: column;
          align-items: center;
          
          .bar-container {
            display: flex;
            align-items: flex-end;
            gap: 5px;
            height: 160px;
            
            .bar {
              width: 25px;
              border-radius: 4px 4px 0 0;
              display: flex;
              justify-content: center;
              align-items: flex-start;
              padding-top: 5px;
              transition: height 0.3s ease;
              
              &.user-bar {
                background: linear-gradient(180deg, #409eff, #79bbff);
              }
              
              &.order-bar {
                background: linear-gradient(180deg, #67c23a, #95d475);
              }
              
              .bar-value {
                font-size: 12px;
                color: #fff;
                font-weight: 500;
              }
            }
          }
          
          .bar-date {
            margin-top: 10px;
            font-size: 12px;
            color: #999;
          }
        }
      }
    }
  }
}

.mt-20 {
  margin-top: 20px;
}
</style>
