<template>
  <div>
    <div style="margin-bottom:16px;display:flex;gap:12px">
      <el-input v-model="keyword" placeholder="输入Customer ID搜索（如AMD）" style="width:300px" clearable @keyup.enter="search" />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>
    <el-table :data="customers" stripe v-loading="loading">
      <el-table-column prop="customerCode" label="Customer ID" width="120" />
      <el-table-column prop="companyName" label="公司名称" min-width="200" />
      <el-table-column prop="customerTier" label="等级" width="60" />
      <el-table-column prop="regionCover" label="区域" min-width="200" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}">{{ row.status===1?'已签约':'潜在' }}</template></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{row}"><el-button type="primary" size="small" @click="$router.push(`/customers/${row.id}`)">查看</el-button></template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { searchCustomers } from '@/api/customer'
const keyword = ref('AMD')
const customers = ref([])
const loading = ref(false)
const search = async () => { loading.value = true; try { customers.value = await searchCustomers(keyword.value) } finally { loading.value = false } }
onMounted(() => search())
</script>
