import request from '@/utils/request'

export function getDocumentList(params) {
  return request({
    url: '/document/list',
    method: 'get',
    params
  })
}

export function getDocumentDetail(id) {
  return request({
    url: `/document/detail/${id}`,
    method: 'get'
  })
}

export function addDocument(data) {
  return request({
    url: '/document',
    method: 'post',
    data
  })
}

export function updateDocument(data) {
  return request({
    url: '/document',
    method: 'put',
    data
  })
}

export function deleteDocument(id) {
  return request({
    url: `/document/${id}`,
    method: 'delete'
  })
}

export function downloadDocument(id) {
  return request({
    url: `/document/download/${id}`,
    method: 'post'
  })
}

export function createDocumentOrder(data) {
  return request({
    url: '/document-order/create',
    method: 'post',
    data
  })
}

export function getUserDocumentOrders(params) {
  return request({
    url: '/document-order/user-list',
    method: 'get',
    params
  })
}
