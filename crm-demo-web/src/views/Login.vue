<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;background:#f0f2f5">
    <el-card style="width:400px">
      <h2 style="text-align:center;margin-bottom:24px">CRM Demo 登录</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" placeholder="sales 或 approver" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" placeholder="123456" @keyup.enter="login" /></el-form-item>
        <el-form-item><el-button type="primary" style="width:100%" :loading="loading" @click="login">登录</el-button></el-form-item>
        <p style="color:#909399;font-size:12px;text-align:center">Demo账号: sales/123456 (业务员) | approver/123456 (审核者)</p>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import { reactive, ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false)
const login = async () => {
  loading.value = true
  try {
    const { data } = await axios.post('/api/auth/login', form)
    if (data.code === 200) {
      localStorage.setItem('token', data.data.token)
      localStorage.setItem('role', data.data.role)
      localStorage.setItem('displayName', data.data.displayName)
      window.location.href = data.data.role === 'APPROVER' ? '/approvals' : '/customers'
    } else { ElMessage.error(data.message) }
  } catch (e) { ElMessage.error('登录失败') }
  finally { loading.value = false }
}
</script>
