import request from '@/utils/request'

export function getOrderProgressList() {
  return request.get('/order-progress/list')
}

export function updateOrderProgress(data) {
  return request.post('/order-progress/update', data)
}
