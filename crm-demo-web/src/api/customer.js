import request from './request'

export function searchCustomers(keyword) {
  return request.get('/customers/search', { params: { keyword } })
}

export function getCustomerDetail(id) {
  return request.get(`/customers/${id}`)
}
