<template>
  <div>
    <h2>PI 审核工作台</h2>
    <el-tabs v-model="tab">
      <el-tab-pane label="待审核" name="pending">
        <el-table :data="pending" stripe v-loading="loading">
          <el-table-column prop="contractNo" label="Contract #" width="200" />
          <el-table-column prop="customerName" label="客户" width="200" />
          <el-table-column prop="totalValue" label="金额" width="120" />
          <el-table-column prop="submittedAt" label="提交时间" width="180" />
          <el-table-column label="操作" width="200">
            <template #default="{row}">
              <el-button size="small" @click="$router.push(`/proforma-invoices/${row.piId}`)">查看</el-button>
              <el-button size="small" type="success" @click="handleApprove(row.piId, true)">通过</el-button>
              <el-button size="small" type="danger" @click="handleApprove(row.piId, false)">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const pending = ref([])
const tab = ref('pending')
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    const { data } = await axios.get('/api/approvals/pending', {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })
    if (data.code === 200) pending.value = data.data
  } finally { loading.value = false }
}

const handleApprove = async (piId, approved) => {
  try {
    const reason = approved ? '' : prompt('驳回原因(可选):')
    const { data } = await axios.post(`/api/approvals/pi/${piId}`, { approved, reason }, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    })
    if (data.code === 200) { ElMessage.success(approved ? '已通过' : '已驳回'); load() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('操作失败') }
}

onMounted(() => load())
</script>
