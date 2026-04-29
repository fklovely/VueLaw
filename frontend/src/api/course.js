import request from '@/utils/request'

export function getCourseList(params) {
  return request({
    url: '/course/list',
    method: 'get',
    params
  })
}

export function getMyCourseList(params) {
  return request({
    url: '/course/my-list',
    method: 'get',
    params
  })
}

export function getCourseDetail(id) {
  return request({
    url: `/course/detail/${id}`,
    method: 'get'
  })
}

export function addCourse(data) {
  return request({
    url: '/course',
    method: 'post',
    data
  })
}

export function updateCourse(data) {
  return request({
    url: '/course',
    method: 'put',
    data
  })
}

export function deleteCourse(id) {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}

export function publishCourse(id) {
  return request({
    url: `/course/publish/${id}`,
    method: 'post'
  })
}

export function auditCourse(id, status) {
  return request({
    url: `/course/audit/${id}`,
    method: 'put',
    params: { status }
  })
}

export function getRecommendCourses() {
  return request({
    url: '/course/recommend',
    method: 'get'
  })
}
