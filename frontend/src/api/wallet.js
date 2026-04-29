import request from '@/utils/request'

export function getWallet() {
  return request.get('/wallet')
}

export function getBalance() {
  return request.get('/wallet/balance')
}

export function recharge(data) {
  return request.post('/wallet/recharge', data)
}

export function payOrder(data) {
  return request.post('/wallet/pay', data)
}

export function setPayPassword(data) {
  return request.put('/wallet/pay-password', data)
}

export function getWalletRecords(params) {
  return request.get('/wallet/records', { params })
}
