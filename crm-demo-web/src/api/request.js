import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 30000 })

request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    if (code === 200) return data
    ElMessage.error(message || 'Request failed')
    return Promise.reject(new Error(message))
  },
  error => {
    ElMessage.error(error.message || 'Network error')
    return Promise.reject(error)
  }
)

export default request
