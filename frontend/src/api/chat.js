import request from '@/utils/request'

export function getConversationList(userId) {
  return request({
    url: '/chat/conversations',
    method: 'get',
    params: { userId }
  })
}

export function getChatHistory(userId, targetId, page = 1, size = 20) {
  return request({
    url: '/chat/history',
    method: 'get',
    params: { userId, targetId, page, size }
  })
}

export function sendMessage(data) {
  return request({
    url: '/chat/send',
    method: 'post',
    data
  })
}

export function markAsRead(userId, targetId) {
  return request({
    url: '/chat/read',
    method: 'put',
    params: { userId, targetId }
  })
}

export function getUnreadCount(userId) {
  return request({
    url: '/chat/unread-count',
    method: 'get',
    params: { userId }
  })
}
