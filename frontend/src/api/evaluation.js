import request from '@/utils/request'

export function getEvaluationList(params) {
  return request.get('/evaluation/list', { params })
}

export function getEvaluationStatistics() {
  return request.get('/evaluation/statistics')
}

export function replyEvaluation(id, data) {
  return request.post(`/evaluation/reply/${id}`, data)
}
