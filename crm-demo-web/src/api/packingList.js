import request from './request'

export function generatePL(piId) {
  return request.post(`/packing-lists/generate/${piId}`)
}

export function getPLList(customerId) {
  return request.get('/packing-lists', { params: { customerId } })
}

export function getPLDetail(id) {
  return request.get(`/packing-lists/${id}`)
}

export function exportPL(id) {
  window.open(`/api/packing-lists/${id}/export`)
}
