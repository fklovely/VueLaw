import request from '@/utils/request'

export function getIncomeList(params) {
  return request.get('/income/list', { params })
}

export function getIncomeStatistics() {
  return request.get('/income/statistics')
}
