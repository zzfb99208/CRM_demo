<template>
  <div>
    <div style="margin-bottom:16px;display:flex;gap:12px">
      <el-input v-model="keyword" placeholder="搜索产品名称或CAT.#" style="width:300px" clearable @keyup.enter="load" />
      <el-select v-model="category" placeholder="分类" clearable style="width:160px" @change="load">
        <el-option label="仪器 INSTRUMENT" value="INSTRUMENT" /><el-option label="试剂 ASSAY" value="ASSAY" />
        <el-option label="质控品 EQC" value="EQC" /><el-option label="耗材 CONSUMABLE" value="CONSUMABLE" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="products" stripe v-loading="loading">
      <el-table-column prop="menariniCode" label="Menarini Code" width="100" />
      <el-table-column prop="coyoteCode" label="CAT.#" width="150" />
      <el-table-column prop="productName" label="Product Name" min-width="250" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="unitPriceKit" label="Price/Kit" width="100" />
      <el-table-column prop="unitPriceTest" label="Price/Test" width="100" />
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getProducts } from '@/api/product'
const keyword = ref(''); const category = ref(''); const products = ref([]); const loading = ref(false)
const load = async () => { loading.value = true; try { products.value = await getProducts(keyword.value, category.value) } finally { loading.value = false } }
onMounted(() => load())
</script>
