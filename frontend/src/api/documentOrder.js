import request from '@/utils/request'

export function getDocumentOrderList(params) {
  return request.get('/document-order/list', { params })
}

export function getDocumentOrderUserList(params) {
  return request.get('/document-order/user-list', { params })
}

export function getDocumentOrderStatistics() {
  return request.get('/document-order/statistics')
}

export function acceptDocumentOrder(id) {
  return request.post(`/document-order/accept/${id}`)
}

export function completeDocumentOrder(id, data) {
  return request.post(`/document-order/complete/${id}`, data)
}

// 取消订单（用户端）
export function cancelDocumentOrder(id) {
  return request.post(`/document-order/cancel/${id}`)
}
