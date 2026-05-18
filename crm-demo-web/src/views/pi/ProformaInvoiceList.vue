<template>
  <div>
    <div style="margin-bottom:16px">
      <el-select v-model="customerId" placeholder="选择客户" clearable style="width:200px" @change="load">
        <el-option label="AMD" :value="1" />
      </el-select>
      <el-button type="primary" @click="load" style="margin-left:8px">查询</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading" @row-click="row => $router.push(`/proforma-invoices/${row.id}`)">
      <el-table-column prop="invoiceNo" label="Invoice #" width="200" />
      <el-table-column prop="contractNo" label="Contract #" width="200" />
      <el-table-column prop="invoiceDate" label="Date" width="120" />
      <el-table-column prop="totalValue" label="Total Value" width="120" />
      <el-table-column prop="status" label="状态" width="140"><template #default="{row}"><el-tag :type="st(row.status)">{{ row.status }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="100"><template #default="{row}"><el-button size="small" @click.stop="$router.push(`/proforma-invoices/${row.id}`)">查看</el-button></template></el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getPIList } from '@/api/proformaInvoice'
const customerId = ref(null); const list = ref([]); const loading = ref(false)
const st = (s) => ({ 'APPROVED':'success','PACKING_GENERATED':'primary' }[s]||'info')
const load = async () => { loading.value = true; try { list.value = await getPIList(customerId.value) } finally { loading.value = false } }
onMounted(() => load())
</script>
