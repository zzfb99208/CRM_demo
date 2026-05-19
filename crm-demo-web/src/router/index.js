import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/', redirect: '/customers' },
  { path: '/customers', name: 'CustomerList', component: () => import('@/views/customer/CustomerList.vue') },
  { path: '/customers/:id', name: 'CustomerDetail', component: () => import('@/views/customer/CustomerDetail.vue') },
  { path: '/products', name: 'ProductList', component: () => import('@/views/product/ProductList.vue') },
  { path: '/purchase-orders', name: 'POList', component: () => import('@/views/po/PurchaseOrderList.vue') },
  { path: '/purchase-orders/import/:customerId', name: 'POImport', component: () => import('@/views/po/PurchaseOrderImport.vue') },
  { path: '/purchase-orders/:id', name: 'PODetail', component: () => import('@/views/po/PurchaseOrderDetail.vue') },
  { path: '/proforma-invoices', name: 'PIList', component: () => import('@/views/pi/ProformaInvoiceList.vue') },
  { path: '/proforma-invoices/:id', name: 'PIDetail', component: () => import('@/views/pi/ProformaInvoiceDetail.vue') },
  { path: '/packing-lists', name: 'PLList', component: () => import('@/views/pl/PackingListList.vue') },
  { path: '/packing-lists/:id', name: 'PLDetail', component: () => import('@/views/pl/PackingListDetail.vue') },
  { path: '/approvals', name: 'Approvals', component: () => import('@/views/approval/ApprovalWorkbench.vue') }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.path === '/') return '/login'
  if (to.path !== '/login' && !token) return '/login'
  return true
})

export default router
