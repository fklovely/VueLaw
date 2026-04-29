import request from '@/utils/request'

export function getConsultList(params) {
  return request({
    url: '/consult/list',
    method: 'get',
    params
  })
}

export function getConsultDetail(id) {
  return request({
    url: `/consult/detail/${id}`,
    method: 'get'
  })
}

export function createConsult(data) {
  return request({
    url: '/consult',
    method: 'post',
    data
  })
}

export function acceptConsult(id) {
  return request({
    url: `/consult/accept/${id}`,
    method: 'post'
  })
}

export function processConsult(id, result) {
  return request({
    url: `/consult/process/${id}`,
    method: 'post',
    data: { result }
  })
}

export function finishConsult(id) {
  return request({
    url: `/consult/finish/${id}`,
    method: 'post'
  })
}

export function cancelConsult(id) {
  return request({
    url: `/consult/cancel/${id}`,
    method: 'post'
  })
}

export function evaluateConsult(id, data) {
  return request({
    url: `/consult/evaluate/${id}`,
    method: 'post',
    data
  })
}

export function getConsultStatistics() {
  return request({
    url: '/consult/statistics',
    method: 'get'
  })
}
