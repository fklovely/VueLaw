import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    name: 'FrontLayout',
    component: () => import('@/views/layout/FrontLayout.vue'),
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'lawyer',
        name: 'LawyerList',
        component: () => import('@/views/lawyer/list.vue'),
        meta: { title: '律师列表' }
      },
      {
        path: 'lawyer/detail/:id',
        name: 'LawyerDetail',
        component: () => import('@/views/lawyer/detail.vue'),
        meta: { title: '律师详情' }
      },
      {
        path: 'consult',
        name: 'ConsultList',
        component: () => import('@/views/consult/list.vue'),
        meta: { title: '咨询列表', requiresAuth: true }
      },
      {
        path: 'consult/create/:lawyerId',
        name: 'ConsultCreate',
        component: () => import('@/views/consult/create.vue'),
        meta: { title: '发起咨询', requiresAuth: true }
      },
      {
        path: 'document',
        name: 'DocumentList',
        component: () => import('@/views/document/list.vue'),
        meta: { title: '法律文书' }
      },
      {
        path: 'document/order',
        name: 'DocumentOrderCreate',
        component: () => import('@/views/document/order-create.vue'),
        meta: { title: '文书代写预约', requiresAuth: true }
      },
      {
        path: 'case',
        name: 'CaseList',
        component: () => import('@/views/case/list.vue'),
        meta: { title: '案例检索' }
      },
      {
        path: 'case/detail/:id',
        name: 'CaseDetail',
        component: () => import('@/views/case/detail.vue'),
        meta: { title: '案例详情' }
      },
      {
        path: 'regulation',
        name: 'RegulationList',
        component: () => import('@/views/regulation/list.vue'),
        meta: { title: '法规查询' }
      },
      {
        path: 'regulation/detail/:id',
        name: 'RegulationDetail',
        component: () => import('@/views/regulation/detail.vue'),
        meta: { title: '法规详情' }
      },
      {
        path: 'article',
        name: 'ArticleList',
        component: () => import('@/views/article/list.vue'),
        meta: { title: '普法文章' }
      },
      {
        path: 'article/detail/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/article/detail.vue'),
        meta: { title: '文章详情' }
      },
      {
        path: 'course',
        name: 'CourseList',
        component: () => import('@/views/course/list.vue'),
        meta: { title: '在线课堂' }
      },
      {
        path: 'course/detail/:id',
        name: 'CourseDetail',
        component: () => import('@/views/course/detail.vue'),
        meta: { title: '课程详情' }
      },
      {
        path: 'question',
        name: 'QuestionList',
        component: () => import('@/views/question/list.vue'),
        meta: { title: '互动问答' }
      },
      {
        path: 'question/detail/:id',
        name: 'QuestionDetail',
        component: () => import('@/views/question/detail.vue'),
        meta: { title: '问题详情' }
      },
      {
        path: 'user',
        name: 'UserCenter',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '个人中心', requiresAuth: true },
        children: [
          {
            path: 'profile',
            name: 'UserProfile',
            component: () => import('@/views/user/profile.vue'),
            meta: { title: '个人信息' }
          },
          {
            path: 'consult',
            name: 'UserConsult',
            component: () => import('@/views/user/consult.vue'),
            meta: { title: '我的咨询' }
          },
          {
            path: 'orders',
            name: 'UserOrders',
            component: () => import('@/views/user/orders.vue'),
            meta: { title: '我的订单' }
          },
          {
            path: 'wallet',
            name: 'UserWallet',
            component: () => import('@/views/user/wallet.vue'),
            meta: { title: '我的钱包' }
          },
          {
            path: 'favorite',
            name: 'UserFavorite',
            component: () => import('@/views/user/favorite.vue'),
            meta: { title: '我的收藏' }
          },
          {
            path: 'document-order',
            name: 'UserDocumentOrder',
            component: () => import('@/views/document/order-list.vue'),
            meta: { title: '我的文书订单' }
          },
          {
            path: 'follow',
            name: 'UserFollow',
            component: () => import('@/views/user/follow.vue'),
            meta: { title: '关注律师' }
          }
        ]
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/chat/index.vue'),
        meta: { title: '在线咨询', requiresAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/layout.vue'),
    meta: { title: '后台管理', requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('@/views/admin/user.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'lawyer',
        name: 'AdminLawyer',
        component: () => import('@/views/admin/lawyer.vue'),
        meta: { title: '律师管理' }
      },
      {
        path: 'consult',
        name: 'AdminConsult',
        component: () => import('@/views/admin/consult.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'document',
        name: 'AdminDocument',
        component: () => import('@/views/admin/document.vue'),
        meta: { title: '文书管理' }
      },
      {
        path: 'case',
        name: 'AdminCase',
        component: () => import('@/views/admin/case.vue'),
        meta: { title: '案例管理' }
      },
      {
        path: 'regulation',
        name: 'AdminRegulation',
        component: () => import('@/views/admin/regulation.vue'),
        meta: { title: '法规管理' }
      },
      {
        path: 'article',
        name: 'AdminArticle',
        component: () => import('@/views/admin/article.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'course',
        name: 'AdminCourse',
        component: () => import('@/views/admin/course.vue'),
        meta: { title: '课程管理' }
      },
      {
        path: 'question',
        name: 'AdminQuestion',
        component: () => import('@/views/admin/question.vue'),
        meta: { title: '问答管理' }
      },
      {
        path: 'notice',
        name: 'AdminNotice',
        component: () => import('@/views/admin/notice.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'category',
        name: 'AdminCategory',
        component: () => import('@/views/admin/category.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'config',
        name: 'AdminConfig',
        component: () => import('@/views/admin/config.vue'),
        meta: { title: '系统配置' }
      }
    ]
  },
  {
    path: '/lawyer-center',
    component: () => import('@/views/lawyer-center/layout.vue'),
    meta: { title: '律师中心', requiresAuth: true, requiresLawyer: true },
    children: [
      {
        path: '',
        redirect: '/lawyer-center/dashboard'
      },
      {
        path: 'dashboard',
        name: 'LawyerDashboard',
        component: () => import('@/views/lawyer-center/dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'evaluate',
        name: 'LawyerEvaluate',
        component: () => import('@/views/lawyer-center/evaluate.vue'),
        meta: { title: '服务评价' }
      },
      {
        path: 'consult',
        name: 'LawyerConsult',
        component: () => import('@/views/lawyer-center/consult.vue'),
        meta: { title: '咨询订单处理' }
      },
      {
        path: 'document-service',
        name: 'LawyerDocumentService',
        component: () => import('@/views/lawyer-center/document-service.vue'),
        meta: { title: '文书服务接单' }
      },
      {
        path: 'service-record',
        name: 'LawyerServiceRecord',
        component: () => import('@/views/lawyer-center/service-record.vue'),
        meta: { title: '我的服务记录' }
      },
      {
        path: 'profile',
        name: 'LawyerProfile',
        component: () => import('@/views/lawyer-center/profile.vue'),
        meta: { title: '资料维护' }
      },
      {
        path: 'expertise',
        name: 'LawyerExpertise',
        component: () => import('@/views/lawyer-center/expertise.vue'),
        meta: { title: '专业领域设置' }
      },
      {
        path: 'pricing',
        name: 'LawyerPricing',
        component: () => import('@/views/lawyer-center/pricing.vue'),
        meta: { title: '服务定价' }
      },
      {
        path: 'orders',
        name: 'LawyerOrders',
        component: () => import('@/views/lawyer-center/orders.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'progress',
        name: 'LawyerProgress',
        component: () => import('@/views/lawyer-center/progress.vue'),
        meta: { title: '进度更新' }
      },
      {
        path: 'article',
        name: 'LawyerArticle',
        component: () => import('@/views/lawyer-center/article.vue'),
        meta: { title: '文章发布' }
      },
      {
        path: 'course',
        name: 'LawyerCourse',
        component: () => import('@/views/lawyer-center/course.vue'),
        meta: { title: '课程管理' }
      },
      {
        path: 'qa',
        name: 'LawyerQA',
        component: () => import('@/views/lawyer-center/qa.vue'),
        meta: { title: '社区互动' }
      },
      {
        path: 'income',
        name: 'LawyerIncome',
        component: () => import('@/views/lawyer-center/income.vue'),
        meta: { title: '收益明细' }
      },
      {
        path: 'security',
        name: 'LawyerSecurity',
        component: () => import('@/views/lawyer-center/security.vue'),
        meta: { title: '账户安全' }
      },
      {
        path: 'privacy',
        name: 'LawyerPrivacy',
        component: () => import('@/views/lawyer-center/privacy.vue'),
        meta: { title: '隐私设置' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { x: 0, y: 0 }
    }
  }
})

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      return Promise.reject(err)
    }
  })
}

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 未成年人保护法律服务平台` : '未成年人保护法律服务平台'
  
  const token = store.getters.token
  const role = store.getters.role
  
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  if (to.meta.requiresAdmin && role !== 'admin') {
    next('/home')
    return
  }
  
  if (to.meta.requiresLawyer && role !== 'lawyer') {
    next('/home')
    return
  }
  
  next()
})

export default router
