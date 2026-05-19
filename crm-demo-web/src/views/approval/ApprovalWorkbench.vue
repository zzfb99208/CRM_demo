<template>
  <div>
    <h2>PI审核&审核历史</h2>
    <el-tabs v-model="tab" @tab-change="onTabChange">
      <el-tab-pane label="待审核" name="pending">
        <el-table :data="pending" stripe v-loading="loading">
          <el-table-column prop="contractNo" label="Contract #" width="200" />
          <el-table-column prop="customerName" label="客户" min-width="180" />
          <el-table-column prop="totalValue" label="金额" width="120" />
          <el-table-column prop="submittedAt" label="提交时间" width="180" />
          <el-table-column label="操作" width="220">
            <template #default="{row}">
              <el-button size="small" @click="$router.push(`/proforma-invoices/${row.piId}`)">查看</el-button>
              <el-button size="small" type="success" @click="handleApprove(row.piId, true)">通过</el-button>
              <el-button size="small" type="danger" @click="handleApprove(row.piId, false)">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已审核" name="history">
        <div style="margin-bottom:12px">
          <el-radio-group v-model="historyFilter" @change="loadHistory">
            <el-radio-button label="ALL">全部</el-radio-button>
            <el-radio-button label="APPROVED">通过</el-radio-button>
            <el-radio-button label="REJECTED">驳回</el-radio-button>
          </el-radio-group>
        </div>
        <el-table :data="history" stripe v-loading="loading">
          <el-table-column prop="contractNo" label="Contract #" width="200" />
          <el-table-column prop="customerName" label="客户" min-width="150" />
          <el-table-column prop="status" label="审核结果" width="100">
            <template #default="{row}">
              <el-tag :type="row.status==='APPROVED'||row.status==='PACKING_GENERATED'?'success':'danger'">
                {{ row.status==='REJECTED' ? '驳回' : '通过' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="approvedAt" label="审核时间" width="180" />
          <el-table-column prop="rejectReason" label="驳回原因" min-width="150">
            <template #default="{row}">
              <span v-if="row.status==='REJECTED'" style="color:#f56c6c">{{ row.rejectReason || '-' }}</span>
              <span v-else style="color:#909399">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{row}">
              <el-button size="small" @click="$router.push(`/proforma-invoices/${row.piId}`)">查看</el-button>
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
const history = ref([])
const tab = ref('pending')
const historyFilter = ref('ALL')
const loading = ref(false)

const headers = () => ({ Authorization: 'Bearer ' + localStorage.getItem('token') })

const loadPending = async () => {
  loading.value = true
  try {
    const { data } = await axios.get('/api/approvals/pending', { headers: headers() })
    if (data.code === 200) pending.value = data.data
  } finally { loading.value = false }
}

const loadHistory = async () => {
  loading.value = true
  try {
    const params = historyFilter.value === 'ALL' ? {} : { status: historyFilter.value }
    const { data } = await axios.get('/api/approvals/history', { headers: headers(), params })
    if (data.code === 200) history.value = data.data
  } finally { loading.value = false }
}

const onTabChange = (name) => {
  if (name === 'pending') loadPending()
  else loadHistory()
}

const handleApprove = async (piId, approved) => {
  let reason = ''
  if (!approved) {
    const input = prompt('驳回原因:')
    if (input === null) return  // Cancel clicked, abort
    reason = input || ''
  }
  try {
    const { data } = await axios.post(`/api/approvals/pi/${piId}`, { approved, reason }, { headers: headers() })
    if (data.code === 200) { ElMessage.success(approved ? '已通过' : '已驳回'); loadPending() }
    else ElMessage.error(data.message)
  } catch (e) { ElMessage.error('操作失败: ' + (e.response?.data?.message || e.message)) }
}

onMounted(() => loadPending())
</script>
