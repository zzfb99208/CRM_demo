<template>
  <div v-loading="loading">
    <el-button @click="$router.back()" style="margin-bottom:16px">← 返回</el-button>
    <div style="display:flex;justify-content:space-between;align-items:center">
      <h2 v-if="pi">Invoice: {{ pi.invoiceNo }} <el-tag :type="st(pi.status)">{{ pi.status }}</el-tag></h2>
    <el-alert v-if="pi && pi.status==='REJECTED' && pi.rejectReason" type="error" :title="'该 PI 已被驳回，驳回原因：' + pi.rejectReason" :description="'请修改后重新提交审核'" show-icon style="margin-bottom:12px" />
      <div>
        <el-button :type="editing?'warning':''" @click="toggleEdit">{{ editing?'退出编辑':'编辑 ✏' }}</el-button>
        <el-button @click="doExportPI">导出 PI ⬇</el-button>
        <el-upload :auto-upload="false" :on-change="handleReimport" :limit="1" accept=".xlsx,.xls" style="display:inline-block;margin-left:8px">
          <el-button type="warning">导入修改后的 PI ⬆</el-button>
        </el-upload>
      </div>
    </div>
    <el-descriptions v-if="pi" :column="2" border style="margin-top:10px">
      <el-descriptions-item label="Invoice #">{{ pi.invoiceNo }}</el-descriptions-item>
      <el-descriptions-item label="Date">
        <el-date-picker v-if="editing" v-model="pi.invoiceDate" type="date" size="small" value-format="YYYY-MM-DD" style="width:160px" />
        <span v-else>{{ pi.invoiceDate }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="Contract #">{{ pi.contractNo }}</el-descriptions-item>
      <el-descriptions-item label="Delivery">
        <el-select v-if="editing" v-model="pi.deliveryTerms" size="small" style="width:120px">
          <el-option v-for="t in ['FCA','FOB','CIF','EXW','DAP']" :key="t" :label="t" :value="t" />
        </el-select>
        <span v-else>{{ pi.deliveryTerms }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="Payment">
        <el-input v-if="editing" v-model="pi.paymentTerms" size="small" style="width:200px" />
        <span v-else>{{ pi.paymentTerms }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="Transport">
        <el-select v-if="editing" v-model="pi.transportMethod" size="small" style="width:120px">
          <el-option v-for="t in ['Air','Sea','Land']" :key="t" :label="t" :value="t" />
        </el-select>
        <span v-else>{{ pi.transportMethod }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="PO Reference" :span="2">{{ pi.poReference }}</el-descriptions-item>
    </el-descriptions>

    <el-table v-if="items.length" :data="items" stripe style="margin-top:10px">
      <el-table-column prop="lineNo" label="#" width="50" />
      <el-table-column prop="customerPoNo" label="Order #" width="100" />
      <el-table-column prop="catNo" label="CAT.#" width="150">
        <template #default="{row}">
          <el-input v-if="editing" v-model="row.catNo" size="small" />
          <span v-else>{{ row.catNo }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="refNo" label="Ref No." width="90">
        <template #default="{row}">
          <el-input v-if="editing" v-model="row.refNo" size="small" />
          <span v-else>{{ row.refNo }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="DESCRIPTION" min-width="200">
        <template #default="{row}">
          <el-input v-if="editing" v-model="row.description" size="small" />
          <span v-else>{{ row.description }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="qty1" label="Qty(K)" width="130">
        <template #default="{row}">
          <el-input-number v-if="editing" v-model="row.qty1" :min="1" size="small" controls-position="right" style="width:110px" />
          <span v-else>{{ row.qty1 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="unit1" label="Unit" width="60" />
      <el-table-column label="库存" width="70" v-if="editing">
        <template #default="{row}">
          <el-tag :type="(stockMap[row.lineNo]||0) >= row.qty1 ? 'success' : 'danger'" size="small">{{ stockMap[row.lineNo] || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lineValue" label="Value" width="90" />
      <el-table-column prop="discountFlag" label="Free" width="60">
        <template #default="{row}">{{ row.discountFlag?'Y':'' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" v-if="editing">
        <template #default="{row}">
          <el-button type="primary" size="small" @click="saveRow(row)">保存</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="pi" style="margin-top:10px">
      <strong>Total Value: {{ pi.totalValue }}</strong>
      <span v-if="editing" style="margin-left:12px;color:#909399">（保存单项后自动更新）</span>
    </div>
    <div v-if="warnings.length" style="margin-top:10px">
      <el-alert v-for="w in warnings" :key="w.lineNo" :title="`行${w.lineNo}: ${w.productName} 需求${w.required}, 库存${w.available}`" type="warning" show-icon />
    </div>

    <div style="margin-top:20px">
      <template v-if="editing">
        <el-button @click="cancelEdit">取消编辑</el-button>
        <el-button type="success" @click="saveAll">保存全部修改</el-button>
      </template>
      <el-button v-if="pi && pi.status!=='SUBMITTED' && pi.status!=='PACKING_GENERATED'" type="warning" @click="submitForApproval" :loading="submitting">提交审核</el-button>
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
      <template #footer><el-button @click="diffVisible=false">取消</el-button><el-button type="primary" @click="confirmReimport">确认</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPIDetail, exportPI, reimportPI, updatePIItem } from '@/api/proformaInvoice'
import { generatePL as apiGeneratePL } from '@/api/packingList'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const route = useRoute(); const router = useRouter()
const pi = ref(null); const items = ref([]); const warnings = ref([]); const loading = ref(false)
const editing = ref(false); const originalItems = ref([]); const submitting = ref(false)
const stockMap = reactive({})
const diffVisible = ref(false); const diffResult = ref(null)

const st = (s) => ({ 'APPROVED':'success','PACKING_GENERATED':'primary' }[s]||'info')
const dt = (t) => ({ 'MODIFIED':'warning','ADDED':'primary','DELETED':'danger' }[t]||'info')

const load = async () => {
  loading.value = true
  try {
    const d = await getPIDetail(route.params.id)
    pi.value = d.pi; items.value = d.items; warnings.value = d.stockWarnings||[]
    // Load stock info for each item
    for (const it of items.value) {
      stockMap[it.lineNo] = it.stockAvailable ?? '-'
    }
  } finally { loading.value = false }
}

const toggleEdit = () => {
  if (!editing.value) {
    originalItems.value = JSON.parse(JSON.stringify(items.value))
    editing.value = true
  } else {
    editing.value = false
  }
}

const cancelEdit = () => {
  items.value = JSON.parse(JSON.stringify(originalItems.value))
  editing.value = false
}

const saveRow = async (row) => {
  loading.value = true
  try {
    const result = await updatePIItem(pi.value.id, row.id, {
      catNo: row.catNo, refNo: row.refNo, description: row.description, qty1: row.qty1
    })
    stockMap[row.lineNo] = result.stockAvailable
    pi.value.totalValue = result.totalValue
    ElMessage.success(`行 ${row.lineNo} 已保存`)
  } catch (e) { ElMessage.error('保存失败') }
  finally { loading.value = false }
}

const saveAll = async () => {
  // Check stock limits before saving
  const overStockItems = items.value.filter(row =>
    (stockMap[row.lineNo] || 0) > 0 && row.qty1 > (stockMap[row.lineNo] || 0)
  )
  if (overStockItems.length > 0) {
    const names = overStockItems.map(r => `行${r.lineNo}: 需求${r.qty1} > 库存${stockMap[r.lineNo]}`).join('\n')
    try {
      await ElMessageBox.confirm(
        `以下行超出库存：\n\n${names}\n\n确定继续保存吗？`,
        '库存不足确认',
        { confirmButtonText: '确认保存', cancelButtonText: '取消', type: 'warning' }
      )
    } catch { return }
  }

  loading.value = true
  try {
    await axios.put(`/api/proforma-invoices/${pi.value.id}`, {
      invoiceDate: pi.value.invoiceDate,
      deliveryTerms: pi.value.deliveryTerms,
      paymentTerms: pi.value.paymentTerms,
      transportMethod: pi.value.transportMethod
    }, { headers: { Authorization: 'Bearer ' + localStorage.getItem('token') } })
    for (const row of items.value) {
      await updatePIItem(pi.value.id, row.id, {
        catNo: row.catNo, refNo: row.refNo, description: row.description, qty1: row.qty1
      })
    }
    const d = await getPIDetail(route.params.id)
    pi.value = d.pi; items.value = d.items; warnings.value = d.stockWarnings||[]
    editing.value = false
    ElMessage.success('全部修改已保存')
  } catch (e) { ElMessage.error('保存失败') }
  finally { loading.value = false }
}

const submitForApproval = async () => {
  submitting.value = true
  try {
    const { data } = await axios.post(`/api/proforma-invoices/${pi.value.id}/submit`, {}, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })
    if (data.code === 200) { ElMessage.success('已提交审核'); load() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('提交失败') }
  finally { submitting.value = false }
}
const doExportPI = () => exportPI(pi.value.id)
const handleReimport = async (f) => {
  loading.value = true
  try { diffResult.value = await reimportPI(pi.value.id, f.raw); diffVisible.value = true }
  catch(e) { ElMessage.error('导入失败') }
  finally { loading.value = false }
}
const confirmReimport = () => {
  diffVisible.value = false
  load()
}
const generatePL = async () => {
  loading.value = true
  try { const pl = await apiGeneratePL(pi.value.id); ElMessage.success('PL 已生成'); router.push(`/packing-lists/${pl.pl.id}`) }
  catch(e) { ElMessage.error('生成失败') }
  finally { loading.value = false }
}
onMounted(() => load())
</script>
