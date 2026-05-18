<template>
  <div>
    <h2>导入 PO Excel — 客户 ID: {{ customerId }}</h2>
    <el-upload drag :auto-upload="false" :on-change="handleFile" :limit="1" accept=".xlsx,.xls" style="margin:20px 0">
      <el-icon style="font-size:48px"><UploadFilled /></el-icon>
      <div>拖拽或点击上传 PO Excel 文件</div>
      <template #tip>支持 .xlsx / .xls 格式</template>
    </el-upload>
    <el-button type="primary" :disabled="!file" :loading="importing" @click="doImport">上传并解析</el-button>
    <div v-if="result" style="margin-top:20px">
      <h3>解析结果</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="Contract #">{{ result.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="Delivery">{{ result.deliveryTerms }}</el-descriptions-item>
        <el-descriptions-item label="Payment">{{ result.paymentTerms }}</el-descriptions-item>
        <el-descriptions-item label="Transport">{{ result.transportMethod }}</el-descriptions-item>
        <el-descriptions-item label="PO Ref">{{ result.poReference }}</el-descriptions-item>
      </el-descriptions>
      <el-alert v-for="w in result.warnings" :key="w" :title="w" type="warning" show-icon style="margin:8px 0" />
      <el-table :data="result.items" stripe style="margin-top:10px">
        <el-table-column prop="lineNo" label="#" width="50" />
        <el-table-column prop="customerPoNo" label="Order #" width="100" />
        <el-table-column prop="catNo" label="CAT.#" width="150" />
        <el-table-column prop="description" label="DESCRIPTION" min-width="200" />
        <el-table-column prop="qty1" label="Qty" width="60" />
        <el-table-column prop="unit1" label="Unit" width="60" />
        <el-table-column prop="lineValue" label="Value" width="80" />
        <el-table-column label="匹配" width="60"><template #default="{row}"><el-tag :type="row.productMatched?'success':'warning'" size="small">{{ row.productMatched?'OK':'?' }}</el-tag></template></el-table-column>
      </el-table>
      <el-button type="success" style="margin-top:16px" @click="$router.push(`/purchase-orders/${result.poId}`)">确认,查看 PO 详情</el-button>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { importPO } from '@/api/purchaseOrder'
import { UploadFilled } from '@element-plus/icons-vue'
const route = useRoute()
const customerId = route.params.customerId
const file = ref(null)
const result = ref(null)
const importing = ref(false)
const handleFile = (f) => { file.value = f.raw }
const doImport = async () => { importing.value = true; try { result.value = await importPO(file.value, customerId) } finally { importing.value = false } }
</script>
