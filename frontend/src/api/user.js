import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/auth/info',
    method: 'put',
    data
  })
}

export function updatePassword(data) {
  return request({
    url: '/auth/password',
    method: 'put',
    data
  })
}

export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/user/status/${id}`,
    method: 'put',
    params: { status }
  })
}

export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

export function auditUser(id, auditStatus, auditRemark) {
  return request({
    url: `/user/audit/${id}`,
    method: 'put',
    params: { auditStatus, auditRemark }
  })
}

export function getAdminStatistics() {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export function getRecentUsers() {
  return request({
    url: '/admin/recent-users',
    method: 'get'
  })
}

export function getRecentOrders() {
  return request({
    url: '/admin/recent-orders',
    method: 'get'
  })
}

export function getOrderDistribution() {
  return request({
    url: '/admin/order-distribution',
    method: 'get'
  })
}
