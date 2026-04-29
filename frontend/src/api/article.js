import request from '@/utils/request'

export function getArticleList(params) {
  return request({
    url: '/article/list',
    method: 'get',
    params
  })
}

export function getMyArticleList(params) {
  return request({
    url: '/article/my-list',
    method: 'get',
    params
  })
}

export function getArticleDetail(id) {
  return request({
    url: `/article/detail/${id}`,
    method: 'get'
  })
}

export function addArticle(data) {
  return request({
    url: '/article',
    method: 'post',
    data
  })
}

export function updateArticle(data) {
  return request({
    url: '/article',
    method: 'put',
    data
  })
}

export function deleteArticle(id) {
  return request({
    url: `/article/${id}`,
    method: 'delete'
  })
}

export function publishArticle(id) {
  return request({
    url: `/article/publish/${id}`,
    method: 'post'
  })
}

export function auditArticle(id, status) {
  return request({
    url: `/article/audit/${id}`,
    method: 'put',
    params: { status }
  })
}
