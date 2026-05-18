<template>
  <div v-loading="loading">
    <el-button @click="$router.back()" style="margin-bottom:16px">← 返回</el-button>
    <div style="display:flex;justify-content:space-between;align-items:center">
      <h2 v-if="pi">Invoice: {{ pi.invoiceNo }} <el-tag :type="st(pi.status)">{{ pi.status }}</el-tag></h2>
      <div>
        <el-button @click="doExportPI">导出 PI ⬇</el-button>
        <el-upload :auto-upload="false" :on-change="handleReimport" :limit="1" accept=".xlsx,.xls" style="display:inline-block;margin-left:8px">
          <el-button type="warning">导入修改后的 PI ⬆</el-button>
        </el-upload>
      </div>
    </div>
    <el-descriptions v-if="pi" :column="2" border style="margin-top:10px">
      <el-descriptions-item label="Invoice #">{{ pi.invoiceNo }}</el-descriptions-item>
      <el-descriptions-item label="Date">{{ pi.invoiceDate }}</el-descriptions-item>
      <el-descriptions-item label="Contract #">{{ pi.contractNo }}</el-descriptions-item>
      <el-descriptions-item label="Delivery">{{ pi.deliveryTerms }}</el-descriptions-item>
      <el-descriptions-item label="Payment">{{ pi.paymentTerms }}</el-descriptions-item>
      <el-descriptions-item label="Transport">{{ pi.transportMethod }}</el-descriptions-item>
      <el-descriptions-item label="PO Reference" :span="2">{{ pi.poReference }}</el-descriptions-item>
    </el-descriptions>
    <el-table v-if="items.length" :data="items" stripe style="margin-top:10px">
      <el-table-column prop="lineNo" label="#" width="50" />
      <el-table-column prop="customerPoNo" label="Order #" width="100" />
      <el-table-column prop="catNo" label="CAT.#" width="150" />
      <el-table-column prop="refNo" label="Ref No." width="90" />
      <el-table-column prop="description" label="DESCRIPTION" min-width="200" />
      <el-table-column prop="qty1" label="Qty" width="60" />
      <el-table-column prop="unit1" label="Unit" width="60" />
      <el-table-column prop="lineValue" label="Value" width="90" />
      <el-table-column prop="discountFlag" label="Free" width="60"><template #default="{row}">{{ row.discountFlag?'Y':'' }}</template></el-table-column>
    </el-table>
    <div v-if="pi" style="margin-top:10px"><strong>Total Value: {{ pi.totalValue }}</strong></div>
    <div v-if="warnings.length" style="margin-top:10px"><el-alert v-for="w in warnings" :key="w.lineNo" :title="`行${w.lineNo}: ${w.productName} 需求${w.required}, 库存${w.available}`" type="warning" show-icon /></div>
    <div style="margin-top:20px">
      <el-button v-if="pi && pi.status==='APPROVED'" type="primary" @click="generatePL">生成 Packing List</el-button>
    </div>
    <!-- Reimport diff dialog -->
    <el-dialog v-model="diffVisible" title="PI 重新导入 — 差异对比" width="80%">
      <div v-if="diffResult">
        <p>变更统计: 修改 {{ diffResult.summary.modifiedCount }} | 新增 {{ diffResult.summary.addedCount }} | 删除 {{ diffResult.summary.deletedCount }}</p>
        <el-table :data="diffResult.changes" stripe>
          <el-table-column prop="lineNo" label="行号" width="60" />
          <el-table-column prop="type" label="状态" width="80"><template #default="{row}"><el-tag :type="dt(row.type)" size="small">{{ row.type }}</el-tag></template></el-table-column>
          <el-table-column prop="field" label="变更字段" width="80" />
          <el-table-column prop="oldValue" label="原值" width="100" />
          <el-table-column prop="newValue" label="新值" width="100" />
        </el-table>
      </div>
      <template #footer><el-button @click="diffVisible=false">取消</el-button><el-button type="primary" @click="diffVisible=false">确认</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPIDetail, exportPI, reimportPI } from '@/api/proformaInvoice'
import { generatePL as apiGeneratePL } from '@/api/packingList'
import { ElMessage } from 'element-plus'
const route = useRoute(); const router = useRouter()
const pi = ref(null); const items = ref([]); const warnings = ref([]); const loading = ref(false)
const diffVisible = ref(false); const diffResult = ref(null); const reimportFile = ref(null)
const st = (s) => ({ 'APPROVED':'success','PACKING_GENERATED':'primary' }[s]||'info')
const dt = (t) => ({ 'MODIFIED':'warning','ADDED':'primary','DELETED':'danger' }[t]||'info')
const load = async () => { loading.value = true; try { const d = await getPIDetail(route.params.id); pi.value = d.pi; items.value = d.items; warnings.value = d.stockWarnings||[] } finally { loading.value = false } }
const doExportPI = () => exportPI(pi.value.id)
const handleReimport = async (f) => { loading.value = true; try { diffResult.value = await reimportPI(pi.value.id, f.raw); diffVisible.value = true } catch(e) { ElMessage.error('导入失败') } finally { loading.value = false } }
const generatePL = async () => { loading.value = true; try { const pl = await apiGeneratePL(pi.value.id); ElMessage.success('PL 已生成'); router.push(`/packing-lists/${pl.pl.id}`) } catch(e) { ElMessage.error('生成失败') } finally { loading.value = false } }
onMounted(() => load())
</script>
