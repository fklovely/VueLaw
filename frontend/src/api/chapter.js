import request from '@/utils/request'

export function getChapterList(courseId) {
  return request.get(`/course-chapter/list/${courseId}`)
}

export function addChapter(data) {
  return request.post('/course-chapter', data)
}

export function updateChapter(data) {
  return request.put('/course-chapter', data)
}

export function deleteChapter(id) {
  return request.delete(`/course-chapter/${id}`)
}
