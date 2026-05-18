<template>
  <div>
    <div style="margin-bottom:16px;display:flex;gap:12px">
      <el-select v-model="customerId" placeholder="选择客户" clearable style="width:200px" @change="load">
        <el-option label="AMD" :value="1" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading" @row-click="row => $router.push(`/purchase-orders/${row.id}`)">
      <el-table-column prop="contractNo" label="Contract No" width="200" />
      <el-table-column prop="deliveryTerms" label="Delivery" width="80" />
      <el-table-column prop="paymentTerms" label="Payment" width="200" />
      <el-table-column prop="status" label="状态" width="120"><template #default="{row}"><el-tag :type="statusType(row.status)">{{ row.status }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="100"><template #default="{row}"><el-button size="small" @click.stop="$router.push(`/purchase-orders/${row.id}`)">查看</el-button></template></el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getPOList } from '@/api/purchaseOrder'
const customerId = ref(null); const list = ref([]); const loading = ref(false)
const load = async () => { loading.value = true; try { list.value = await getPOList(customerId.value) } finally { loading.value = false } }
const statusType = (s) => ({ 'APPROVED': 'success', 'COMPLETED': 'success', 'IMPORTED': 'info', 'PI_GENERATED': 'primary' }[s] || 'info')
onMounted(() => load())
</script>
