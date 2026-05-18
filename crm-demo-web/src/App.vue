<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background:#304156">
      <div style="color:white;padding:16px;font-size:16px;font-weight:bold">CRM Demo</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/customers">客户管理</el-menu-item>
        <el-menu-item index="/products">产品总表</el-menu-item>
        <el-menu-item index="/purchase-orders">采购订单</el-menu-item>
        <el-menu-item index="/proforma-invoices">形式发票</el-menu-item>
        <el-menu-item index="/packing-lists">装箱单</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #dcdfe6;display:flex;align-items:center;padding:0 20px">
        <span style="font-size:14px;color:#606266">Coyote Bioscience 海外 CRM 演示系统</span>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
