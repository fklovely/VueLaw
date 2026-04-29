import request from '@/utils/request'

export function getLawyerList(params) {
  return request({
    url: '/lawyer/list',
    method: 'get',
    params
  })
}

export function intelligentMatch(data) {
  return request({
    url: '/lawyer/intelligent-match',
    method: 'post',
    data
  })
}

export function getLawyerDetail(id) {
  return request({
    url: `/lawyer/detail/${id}`,
    method: 'get'
  })
}

export function getLawyerInfo() {
  return request({
    url: '/lawyer/info',
    method: 'get'
  })
}

export function applyLawyer(data) {
  return request({
    url: '/lawyer/apply',
    method: 'post',
    data
  })
}

export function auditLawyer(data) {
  return request({
    url: '/lawyer/audit',
    method: 'post',
    data
  })
}

export function updateLawyerInfo(data) {
  return request({
    url: '/lawyer',
    method: 'put',
    data
  })
}

export function getLawyerStatistics() {
  return request({
    url: '/lawyer/statistics',
    method: 'get'
  })
}

export function resubmitAudit(data) {
  return request({
    url: '/lawyer/resubmit',
    method: 'post',
    data
  })
}

export function getAuditStatus() {
  return request({
    url: '/lawyer/audit-status',
    method: 'get'
  })
}
