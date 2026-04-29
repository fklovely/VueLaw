import request from '@/utils/request'

export function getAnswerList(params) {
  return request.get('/question/answer/list', { params })
}

export function addAnswer(data) {
  return request.post('/question/answer', data)
}

export function likeAnswer(id) {
  return request.post(`/question/answer/like/${id}`)
}
