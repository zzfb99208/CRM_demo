<template>
  <div>
    <div style="margin-bottom:16px">
      <el-select v-model="customerId" placeholder="选择客户" clearable @change="load"><el-option label="AMD" :value="1" /></el-select>
      <el-button type="primary" @click="load" style="margin-left:8px">查询</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading" @row-click="row => $router.push(`/packing-lists/${row.id}`)">
      <el-table-column prop="contractNo" label="Contract #" width="200" />
      <el-table-column prop="totalCartons" label="Total Cartons" width="120" />
      <el-table-column prop="totalVolumeCbm" label="Vol (CBM)" width="100" />
      <el-table-column prop="totalNetWeightKg" label="Net (KG)" width="100" />
      <el-table-column prop="status" label="状态" width="100"><template #default="{row}"><el-tag type="success">{{ row.status }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="100"><template #default="{row}"><el-button size="small" @click.stop="$router.push(`/packing-lists/${row.id}`)">查看</el-button></template></el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getPLList } from '@/api/packingList'
const customerId = ref(null); const list = ref([]); const loading = ref(false)
const load = async () => { loading.value = true; try { list.value = await getPLList(customerId.value) } finally { loading.value = false } }
onMounted(() => load())
</script>
