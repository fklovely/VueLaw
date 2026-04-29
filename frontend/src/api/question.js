import request from '@/utils/request'

export function getQuestionList(params) {
  return request({
    url: '/question/list',
    method: 'get',
    params
  })
}

export function getQuestionDetail(id) {
  return request({
    url: `/question/detail/${id}`,
    method: 'get'
  })
}

export function addQuestion(data) {
  return request({
    url: '/question',
    method: 'post',
    data
  })
}

export function adoptAnswer(data) {
  return request({
    url: '/question/adopt',
    method: 'post',
    data
  })
}

export function deleteQuestion(id) {
  return request({
    url: `/question/${id}`,
    method: 'delete'
  })
}

export function getAnswerList(params) {
  return request({
    url: '/question/answer/list',
    method: 'get',
    params
  })
}

export function addAnswer(data) {
  return request({
    url: '/question/answer',
    method: 'post',
    data
  })
}

export function likeAnswer(id) {
  return request({
    url: `/question/answer/like/${id}`,
    method: 'post'
  })
}
