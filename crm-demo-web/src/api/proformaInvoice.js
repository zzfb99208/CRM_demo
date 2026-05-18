import request from './request'

export function generatePI(poId) {
  return request.post(`/proforma-invoices/generate/${poId}`)
}

export function getPIList(customerId) {
  return request.get('/proforma-invoices', { params: { customerId } })
}

export function getPIDetail(id) {
  return request.get(`/proforma-invoices/${id}`)
}

export function exportPI(id) {
  window.open(`/api/proforma-invoices/${id}/export`)
}

export function updatePIItem(piId, itemId, data) {
  return request.put(`/proforma-invoices/${piId}/items/${itemId}`, data)
}

export function reimportPI(id, file) {
  const form = new FormData()
  form.append('file', file)
  return request.post(`/proforma-invoices/${id}/import`, form, { headers: { 'Content-Type': 'multipart/form-data' } })
}
