import request from './request'

export function importPO(file, customerId) {
  const form = new FormData()
  form.append('file', file)
  form.append('customerId', customerId)
  return request.post('/purchase-orders/import', form, { headers: { 'Content-Type': 'multipart/form-data' } })
}

export function getPOList(customerId) {
  return request.get('/purchase-orders', { params: { customerId } })
}

export function getPODetail(id) {
  return request.get(`/purchase-orders/${id}`)
}
