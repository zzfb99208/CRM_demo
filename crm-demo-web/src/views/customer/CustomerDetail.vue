<template>
  <div v-loading="loading">
    <el-button @click="$router.back()" style="margin-bottom:16px">← 返回</el-button>
    <h2 v-if="customer">{{ customer.customerCode }} - {{ customer.companyName }}</h2>
    <el-tabs v-model="tab" v-if="customer">
      <el-tab-pane label="基本信息" name="basic">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Customer Code">{{ customer.customerCode }}</el-descriptions-item>
          <el-descriptions-item label="Tier">{{ customer.customerTier }}</el-descriptions-item>
          <el-descriptions-item label="Payment Terms">{{ customer.paymentTerms }}</el-descriptions-item>
          <el-descriptions-item label="Currency">{{ customer.currency }}</el-descriptions-item>
          <el-descriptions-item label="Incoterms">{{ customer.incoterms }}</el-descriptions-item>
          <el-descriptions-item label="Region">{{ customer.regionCover }}</el-descriptions-item>
          <el-descriptions-item label="Distribution">{{ customer.distributionType }}</el-descriptions-item>
          <el-descriptions-item label="Assay Expiry">{{ customer.assayExpiryReq }}</el-descriptions-item>
          <el-descriptions-item label="Special Notes" :span="2">{{ customer.specialNotes }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
      <el-tab-pane label="联系人" name="contacts">
        <el-table :data="contacts" stripe>
          <el-table-column prop="contactName" label="姓名" />
          <el-table-column prop="title" label="职位" />
          <el-table-column prop="email" label="Email" />
          <el-table-column prop="phone" label="电话" />
          <el-table-column prop="notes" label="备注" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <div style="margin-top:20px">
      <el-button type="primary" @click="$router.push(`/purchase-orders/import/${id}`)">导入 PO Excel</el-button>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCustomerDetail } from '@/api/customer'
const route = useRoute()
const id = route.params.id
const customer = ref(null)
const contacts = ref([])
const tab = ref('basic')
const loading = ref(false)
onMounted(async () => { loading.value = true; try { const d = await getCustomerDetail(id); customer.value = d.customer; contacts.value = d.contacts } finally { loading.value = false } })
</script>
