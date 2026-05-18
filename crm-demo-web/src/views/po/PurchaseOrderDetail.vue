<template>
  <div v-loading="loading">
    <el-button @click="$router.back()" style="margin-bottom:16px">← 返回</el-button>
    <h2 v-if="po">PO: {{ po.contractNo }} <el-tag :type="statusType(po.status)">{{ po.status }}</el-tag></h2>
    <el-descriptions v-if="po" :column="2" border>
      <el-descriptions-item label="Contract #">{{ po.contractNo }}</el-descriptions-item>
      <el-descriptions-item label="Delivery">{{ po.deliveryTerms }}</el-descriptions-item>
      <el-descriptions-item label="Payment">{{ po.paymentTerms }}</el-descriptions-item>
      <el-descriptions-item label="Transport">{{ po.transportMethod }}</el-descriptions-item>
      <el-descriptions-item label="PO Reference" :span="2">{{ po.poReference }}</el-descriptions-item>
    </el-descriptions>
    <el-table v-if="items.length" :data="items" stripe style="margin-top:10px">
      <el-table-column prop="lineNo" label="#" width="50" />
      <el-table-column prop="customerPoNo" label="Order #" width="100" />
      <el-table-column prop="catNo" label="CAT.#" width="150" />
      <el-table-column prop="description" label="DESCRIPTION" min-width="200" />
      <el-table-column prop="qty1" label="Qty" width="60" />
      <el-table-column prop="unit1" label="Unit" width="60" />
      <el-table-column prop="lineValue" label="Value" width="80" />
      <el-table-column prop="discountFlag" label="Free" width="60"><template #default="{row}">{{ row.discountFlag?'Y':'' }}</template></el-table-column>
    </el-table>
    <div style="margin-top:20px">
      <el-button v-if="po && po.status==='APPROVED'" type="primary" @click="generatePI">生成 PI</el-button>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPODetail } from '@/api/purchaseOrder'
import { generatePI as apiGeneratePI } from '@/api/proformaInvoice'
import { ElMessage } from 'element-plus'
const route = useRoute(); const router = useRouter()
const po = ref(null); const items = ref([]); const loading = ref(false)
const statusType = (s) => ({ 'APPROVED':'success','COMPLETED':'success','PI_GENERATED':'primary' }[s]||'info')
const load = async () => { loading.value = true; try { const d = await getPODetail(route.params.id); po.value = d.po; items.value = d.items } finally { loading.value = false } }
const generatePI = async () => { loading.value = true; try { const pi = await apiGeneratePI(po.value.id); ElMessage.success('PI 已生成'); router.push(`/proforma-invoices/${pi.pi.id}`) } catch (e) { ElMessage.error('生成失败') } finally { loading.value = false } }
onMounted(() => load())
</script>
