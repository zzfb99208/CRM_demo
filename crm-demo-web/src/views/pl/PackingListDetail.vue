<template>
  <div v-loading="loading">
    <el-button @click="goBack" style="margin-bottom:16px">← 返回 PI 详情</el-button>
    <div style="display:flex;justify-content:space-between;align-items:center">
      <h2 v-if="pl">Packing List — {{ pl.contractNo }} <el-tag type="success">{{ pl.status }}</el-tag></h2>
      <el-button type="primary" @click="doExportPL">导出 PL ⬇</el-button>
    </div>
    <el-descriptions v-if="pl" :column="2" border>
      <el-descriptions-item label="Contract #">{{ pl.contractNo }}</el-descriptions-item>
      <el-descriptions-item label="Transport">{{ pl.transportMethod }}</el-descriptions-item>
      <el-descriptions-item label="Route">{{ pl.transportRoute }}</el-descriptions-item>
      <el-descriptions-item label="Origin">{{ pl.countryOfOrigin }}</el-descriptions-item>
      <el-descriptions-item label="Ship To" :span="2">{{ pl.shipTo }}</el-descriptions-item>
      <el-descriptions-item label="PO Reference" :span="2">{{ pl.poReference }}</el-descriptions-item>
    </el-descriptions>
    <el-table :data="items" stripe style="margin-top:10px">
      <el-table-column prop="lineNo" label="#" width="50" />
      <el-table-column prop="customerPoNo" label="Order #" width="100" />
      <el-table-column prop="catNo" label="CAT.#" width="140" />
      <el-table-column prop="refNo" label="Ref No." width="80" />
      <el-table-column prop="description" label="DESCRIPTION" min-width="180" />
      <el-table-column prop="qty1" label="Qty" width="60" />
      <el-table-column prop="unit1" label="Unit" width="60" />
      <el-table-column prop="lotNo" label="Lot NO." width="100" />
      <el-table-column prop="expiryDate" label="Expiry" width="110" />
      <el-table-column prop="dimension" label="DIMENSION" width="120" />
      <el-table-column prop="qtyCarton" label="Carton" width="70" />
      <el-table-column prop="cartonNoRange" label="Carton #" width="90" />
      <el-table-column prop="volumeCbm" label="Vol CBM" width="90" />
      <el-table-column prop="netWeightKg" label="Net KG" width="80" />
      <el-table-column prop="grossWeightKg" label="Gross KG" width="90" />
      <el-table-column prop="dgmNameCn" label="DGM" min-width="150" />
    </el-table>
    <div v-if="pl" style="margin-top:16px;padding:12px;background:#f5f7fa">
      <strong>TOTAL:</strong> {{ pl.totalCartons }} Cartons | {{ pl.totalVolumeCbm }} CBM | {{ pl.totalNetWeightKg }} KG Net | {{ pl.totalGrossWeightKg }} KG Gross
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPLDetail, exportPL } from '@/api/packingList'
const route = useRoute(); const router = useRouter()
const pl = ref(null); const items = ref([]); const loading = ref(false)
const load = async () => { loading.value = true; try { const d = await getPLDetail(route.params.id); pl.value = d.pl; items.value = d.items } finally { loading.value = false } }
const doExportPL = () => exportPL(pl.value.id)
const goBack = () => { pl.value?.piId ? router.push(`/proforma-invoices/${pl.value.piId}`) : router.back() }
onMounted(() => load())
</script>
