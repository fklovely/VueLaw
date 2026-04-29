import request from '@/utils/request'

export function getConfigMap() {
  return request({
    url: '/config/map',
    method: 'get'
  })
}

export function getConfigByKey(key) {
  return request({
    url: `/config/${key}`,
    method: 'get'
  })
}

export function updateConfig(data) {
  return request({
    url: '/config',
    method: 'put',
    data
  })
}
