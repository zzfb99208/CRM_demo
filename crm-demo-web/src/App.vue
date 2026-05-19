<template>
  <router-view v-if="$route.path==='/login'" />
  <el-container v-else style="height: 100vh">
    <el-aside width="200px" style="background:#304156">
      <div style="color:white;padding:16px;font-size:16px;font-weight:bold">CRM Demo</div>
      <div style="color:#bfcbd9;padding:0 16px 12px;font-size:12px">{{ roleLabel }}</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
<!-- Both roles share these 4 core modules -->
        <template v-if="role==='APPROVER'">
          <el-menu-item index="/approvals">PI审核&审核历史</el-menu-item>
        </template>
        <template v-if="role==='SALES'">
          <el-menu-item index="/proforma-invoices">审核历史&形式发票</el-menu-item>
        </template>
        <el-menu-item index="/customers">客户管理</el-menu-item>
        <el-menu-item index="/products">产品总表</el-menu-item>
        <template v-if="role==='SALES'">
          <el-menu-item index="/purchase-orders">采购订单</el-menu-item>
        </template>
        <el-menu-item index="/packing-lists">装箱单</el-menu-item>
      </el-menu>
      <div style="position:absolute;bottom:16px;left:16px">
        <el-button text style="color:#bfcbd9;font-size:12px" @click="logout">退出登录</el-button>
      </div>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #dcdfe6;display:flex;align-items:center;padding:0 20px">
        <span style="font-size:14px;color:#606266">Coyote Bioscience 海外 CRM 演示系统</span>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const role = computed(() => localStorage.getItem('role'))
const roleLabel = computed(() => role.value === 'APPROVER' ? '审核者' : '业务员')
const logout = () => { localStorage.clear(); router.push('/login') }
</script>
