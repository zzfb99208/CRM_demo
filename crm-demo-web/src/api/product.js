import request from './request'

export function getProducts(keyword, category) {
  return request.get('/products', { params: { keyword, category } })
}
