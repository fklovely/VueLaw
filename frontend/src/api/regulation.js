import request from '@/utils/request'

export function getRegulationList(params) {
  return request({
    url: '/regulation/list',
    method: 'get',
    params
  })
}

export function getRegulationDetail(id) {
  return request({
    url: `/regulation/detail/${id}`,
    method: 'get'
  })
}

export function addRegulation(data) {
  return request({
    url: '/regulation',
    method: 'post',
    data
  })
}

export function updateRegulation(data) {
  return request({
    url: '/regulation',
    method: 'put',
    data
  })
}

export function deleteRegulation(id) {
  return request({
    url: `/regulation/${id}`,
    method: 'delete'
  })
}
