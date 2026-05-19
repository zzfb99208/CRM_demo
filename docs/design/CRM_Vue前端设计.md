# CRM Demo 系统 — Vue 前端设计

> Vue 3 + Element Plus + Vite  
> Demo 仅业务员单一角色，PO 通过 Excel 上传导入，PI/PL 支持 Excel 导出

---

## 一、技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.x | Composition API + `<script setup>` |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.6.x | UI 组件库 |
| Vue Router | 4.x | 路由 |
| Pinia | 2.x | 状态管理 |
| Axios | 1.x | HTTP 请求 |
| dayjs | 1.x | 日期处理 |

---

## 二、项目目录结构

```
crm-demo-web/
├── index.html
├── vite.config.js
├── package.json
├── src/
│   ├── App.vue
│   ├── main.js
│   ├── api/
│   │   ├── request.js                # Axios 实例（baseURL: /api）
│   │   ├── customer.js               # 客户 API
│   │   ├── product.js                # 产品 API
│   │   ├── purchaseOrder.js          # PO 导入/列表/详情 API
│   │   ├── proformaInvoice.js        # PI 生成/详情/导出 API
│   │   └── packingList.js            # PL 生成/详情/导出 API
│   ├── router/
│   │   └── index.js                  # 路由（无角色守卫）
│   ├── views/
│   │   ├── customer/
│   │   │   ├── CustomerList.vue      # 客户列表（搜索 + 表格）
│   │   │   └── CustomerDetail.vue    # 客户详情（含联系人 + 历史订单）
│   │   ├── product/
│   │   │   └── ProductList.vue       # 产品总表
│   │   ├── po/
│   │   │   ├── PurchaseOrderList.vue # PO 列表
│   │   │   ├── PurchaseOrderImport.vue # ★ PO Excel 导入页
│   │   │   └── PurchaseOrderDetail.vue # PO 详情（解析结果预览）
│   │   ├── pi/
│   │   │   ├── ProformaInvoiceList.vue # PI 列表
│   │   │   └── ProformaInvoiceDetail.vue # ★ PI 详情（Invoice 格式 + 导出 + 重新导入按钮）
│   │   └── pl/
│   │       ├── PackingListList.vue   # PL 列表
│   │       └── PackingListDetail.vue # ★ PL 详情（Packing List 格式 + 导出按钮）
│   ├── components/
│   │   ├── common/
│   │   │   └── StatusTag.vue         # 状态标签
│   │   ├── customer/
│   │   │   ├── CustomerSearch.vue    # 客户搜索框（按 Customer ID）
│   │   │   └── ContactTable.vue      # 联系人表格
│   │   └── order/
│   │       ├── InvoiceHeader.vue     # Invoice 格式表头组件
│   │       ├── PackingListHeader.vue # Packing List 格式表头组件
│   │       ├── PackingSummary.vue    # PL 底部汇总行
│   │       └── OrderItemTable.vue    # 行项目通用表格
│   └── utils/
│       ├── constants.js              # 状态/分类枚举
│       └── format.js                 # 金额、日期、体积、重量格式化
```

---

## 三、路由设计

```javascript
const routes = [
  {
    path: '/',
    redirect: '/customers'
  },
  {
    path: '/customers',
    name: 'CustomerList',
    component: () => import('@/views/customer/CustomerList.vue'),
    meta: { title: '客户管理' }
  },
  {
    path: '/customers/:id',
    name: 'CustomerDetail',
    component: () => import('@/views/customer/CustomerDetail.vue'),
    meta: { title: '客户详情' }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/product/ProductList.vue'),
    meta: { title: '产品总表' }
  },
  {
    path: '/purchase-orders',
    name: 'POList',
    component: () => import('@/views/po/PurchaseOrderList.vue'),
    meta: { title: '采购订单' }
  },
  {
    path: '/purchase-orders/import/:customerId',
    name: 'POImport',
    component: () => import('@/views/po/PurchaseOrderImport.vue'),
    meta: { title: '导入 PO' }
  },
  {
    path: '/purchase-orders/:id',
    name: 'PODetail',
    component: () => import('@/views/po/PurchaseOrderDetail.vue'),
    meta: { title: 'PO 详情' }
  },
  {
    path: '/proforma-invoices',
    name: 'PIList',
    component: () => import('@/views/pi/ProformaInvoiceList.vue'),
    meta: { title: '形式发票' }
  },
  {
    path: '/proforma-invoices/:id',
    name: 'PIDetail',
    component: () => import('@/views/pi/ProformaInvoiceDetail.vue'),
    meta: { title: 'PI 详情' }
  },
  {
    path: '/packing-lists',
    name: 'PLList',
    component: () => import('@/views/pl/PackingListList.vue'),
    meta: { title: '装箱单' }
  },
  {
    path: '/packing-lists/:id',
    name: 'PLDetail',
    component: () => import('@/views/pl/PackingListDetail.vue'),
    meta: { title: 'PL 详情' }
  }
];
```

---

## 四、侧边栏菜单

### 业务员 (SALES)
```
├── 客户管理
├── 产品总表
├── 采购订单 (PO)
│   ├── PO 列表
│   └── 导入 PO Excel
├── 审核历史&形式发票
│   └── PI 列表（含审核状态 + 驳回原因）
└── 装箱单 (PL)
    └── PL 列表
```

### 审核者 (APPROVER)
```
├── PI审核&审核历史
│   ├── 待审核 Tab
│   └── 已审核 Tab（支持状态筛选 + 驳回原因列）
├── 客户管理（只读）
├── 产品总表（只读）
└── 装箱单 (PL)
```

共 **9 个页面**。

---

## 五、核心页面设计

### 5.1 客户搜索页 (CustomerList.vue)

```
┌──────────────────────────────────────────────────────┐
│  客户管理                                            │
│──────────────────────────────────────────────────────│
│  [🔍 输入 Customer ID 搜索...         ] [搜索]        │
│──────────────────────────────────────────────────────│
│  Customer ID │ 公司名称      │ 等级 │ 区域    │ 操作  │
│  AMD         │ A.Menarini..  │ A    │ Italy.. │ 查看  │
│──────────────────────────────────────────────────────│
│  共 N 条记录                  [< 1 2 3 ... >]        │
└──────────────────────────────────────────────────────┘
```

- 输入 "AMD" → 实时搜索过滤
- 点击"查看" → CustomerDetail

### 5.2 客户详情页 (CustomerDetail.vue)

```
┌──────────────────────────────────────────────────────┐
│  ← 返回  │  AMD - A. Menarini Diagnostics SRL        │
│──────────────────────────────────────────────────────│
│  [基本信息] [联系人] [历史订单]                        │
│──────────────────────────────────────────────────────│
│  Tab 基本信息:                                       │
│    Customer Code: AMD      Tier: A   Payment: Net 60d│
│    Currency: EUR           Incoterms: FCA            │
│    Region: Italy, Netherlands, Belgium, Spain, France│
│    Distribution: Exclusive  Ship To: VIA DEI SETTE..│
│──────────────────────────────────────────────────────│
│  Tab 联系人: 姓名 | 职位 | Email | 电话 | 备注        │
│──────────────────────────────────────────────────────│
│  Tab 历史订单:                                       │
│    Contract No | 类型 | 日期 | 金额 | 状态            │
│    IT-AMD-..   | PI   | ...  | ...  | COMPLETED     │
│──────────────────────────────────────────────────────│
│  [导入 PO Excel]                                     │
└──────────────────────────────────────────────────────┘
```

### 5.3 PO Excel 导入页 (PurchaseOrderImport.vue) ★

```
┌──────────────────────────────────────────────────────┐
│  导入 PO Excel — AMD / A. Menarini Diagnostics       │
│──────────────────────────────────────────────────────│
│                                                      │
│  ┌──────────────────────────────────────────────┐    │
│  │                                              │    │
│  │        📁  拖拽或点击上传 PO Excel 文件       │    │
│  │            支持 .xlsx / .xls 格式             │    │
│  │                                              │    │
│  └──────────────────────────────────────────────┘    │
│                                                      │
│  [已选择: CIP_IT-AMD-20260130 pending orders.xlsx]   │
│──────────────────────────────────────────────────────│
│  解析结果预览:                                       │
│  ┌──────────────────────────────────────────────┐    │
│  │ Contract #: IT-AMD-20260514                  │    │
│  │ Delivery: FCA    Payment: Net 60 days        │    │
│  │ Transport: Air   Origin: China               │    │
│  │ PO Ref: 1429, 31, 32, 33, 47...             │    │
│  └──────────────────────────────────────────────┘    │
│                                                      │
│  解析行项目 (共 20 行):                              │
│  ┌──────────────────────────────────────────────┐    │
│  │# │Order # │CAT.#         │DESCRIPTION  │Qty │    │
│  │1 │1429   │6018006902-CE │FlashDetect..│ 1  │ ✅ │
│  │2 │0001719│6018034205-EN │FlashDetect..│10  │ ✅ │
│  │3 │UNKNOWN│UNKNOWN-CODE  │(未识别)     │ 5  │ ⚠️ │
│  └──────────────────────────────────────────────┘    │
│                                                      │
│  ⚠ 警告: 行 3 CAT.# 未在 Product 表中找到             │
│──────────────────────────────────────────────────────│
│  [返回修改]                    [确认导入]             │
└──────────────────────────────────────────────────────┘
```

**交互流程**：
1. 从客户详情页点击"导入 PO Excel"进入（携带 customerId）
2. 拖拽或点击上传 Excel 文件
3. 上传后系统自动解析，展示表头信息 + 行项目预览
4. 绿色 ✅ = 产品识别成功，黄色 ⚠️ = 未识别产品
5. 业务员可手动修正未识别行的 CAT.#
6. 确认无误 → 点击"确认导入" → 跳转 PO 详情

### 5.4 PO 详情页 (PurchaseOrderDetail.vue)

```
┌──────────────────────────────────────────────────────┐
│  PO 详情 — IT-AMD-20260514         状态: APPROVED    │
│──────────────────────────────────────────────────────│
│  客户: AMD / A.Menarini Diagnostics                  │
│  Delivery: FCA  |  Payment: Net 60 days              │
│  Transport: Air  |  Origin: China                    │
│  PO Reference: 1429, 31, 32...                       │
│──────────────────────────────────────────────────────│
│  行项目:                                            │
│  # │Order # │CAT.#         │DESCRIPTION │Qty│Val│Dis│
│  1 │1429   │6018006902-CE │FlashDetect..│1  │1  │0  │
│  2 │0001719│6018034205-EN │FlashDetect..│10 │2400│1 │
│  ...                                                │
│──────────────────────────────────────────────────────│
│  总行数: 20  |  总 Kit 数: 150  |  总金额: €45,000  │
│──────────────────────────────────────────────────────│
│  [生成 PI]                       [返回]              │
└──────────────────────────────────────────────────────┘
```

- 点击"生成 PI"→ 调用 API `POST /api/proforma-invoices/generate/{poId}`

### 5.5 PI 详情页 — Invoice 格式 (ProformaInvoiceDetail.vue) ★

```
┌──────────────────────────────────────────────────────┐
│  Coyote Bioscience Co., Ltd.   [导出 PI ⬇] [导入修改后的 PI ⬆] │
│  Commercial Invoice                                              │
│──────────────────────────────────────────────────────────────────│
│  Bill To:                   │ Contract #: IT-AMD-..             │
│  A. Menarini Diagnostics..  │ Invoice #: IT-AMD-..              │
│                             │ Date: 2026-05-14                  │
│  Ship To:                   │ Delivery: FCA                     │
│  VIA DEI SETTE SANTI, 3..  │ Payment: Net 60 days              │
│                             │ Transport: Air                    │
│                             │ Origin: China                     │
│                             │ PO: 1429, 31, 32...               │
│──────────────────────────────────────────────────────────────────│
│ NO│Order#│CAT.#     │Ref No│DESCRIPTION     │Qty│..│
│  1│1429  │6018006902│59824 │FlashDetect™.. │ 1 │..│
│  2│001719│6018034205│59786 │FlashDetect™.. │10 │..│
│ ...                                                             │
│──────────────────────────────────────────────────────────────────│
│  Total Value: € 45,000.00                                       │
│──────────────────────────────────────────────────────────────────│
│  ⚠ 库存警告: 行 5 (6018007205-CE) 需求 40, 当前可用 30 kits      │
│──────────────────────────────────────────────────────────────────│
│  [生成 Packing List]                          [返回]             │
└──────────────────────────────────────────────────────┘
```

- 编辑模式下，表头 Invoice Date / Delivery / Payment / Transport / Origin 可内联编辑
- 顶部 **"导出 PI"** 按钮 → `GET /api/proforma-invoices/{id}/export` → 下载
- 点击"生成 Packing List"→ 调用 API `POST /api/packing-lists/generate/{piId}`

### 5.6 PL 详情页 — Packing List 格式 (PackingListDetail.vue) ★

```
┌──────────────────────────────────────────────────────┐
│  Coyote Bioscience Co., Ltd.            [导出 PL ⬇] │
│  Packing List                                        │
│──────────────────────────────────────────────────────│
│  Address: Building 22...    │ Contract #: IT-AMD-.. │
│                             │ Transport: Air        │
│  Ship To:                   │ Origin: China         │
│  A.MENARINI DIAGNOSTICS SRL │ Route: Beijing→Italy │
│  VIA DEI SETTE SANTI, 3..  │ PO: 1429, 31, 32...  │
│──────────────────────────────────────────────────────│
│ NO│Order#│CAT.#│Ref No│DESCRIPTION│Qty│Lot NO│Exp..│ │
│  1│000.. │200..│61312│Flash10.. │ 1 │3040..│ /   │ │
│  2│000.. │601..│59406│MG&TV&MH..│ 4 │2026..│27/01│ │
│──────────────────────────────────────────────────────│
│ DIMENSION │Carton│Carton#│VOL CBM│NET KG│GROSS KG│  │
│ 57*45*55  │  1   │   1   │0.141..│ 21   │  33    │  │
│ 57*26*37  │  1   │  10   │0.054..│ 5.6  │  8.7   │  │
│──────────────────────────────────────────────────────│
│ TOTAL:     125 Cartons │ 7.5494 CBM                 │
│            1219.5 KG Net │ 1786.8 KG Gross          │
└──────────────────────────────────────────────────────┘
```

- 顶部 **"导出 PL"** 按钮 → `GET /api/packing-lists/{id}/export` → 下载 `PackingList_IT-AMD-20260514_2026-05-14.xlsx`
- 行项目 18 个字段分两行展示（上半 10 列 + 下半 8 列），参照真实 Excel 的 Packing List 格式

### 5.5.1 审核工作台页 — PI审核&审核历史 (ApprovalWorkbench.vue) ★

审核者登录后的首页，合并了原"PI 审核"和"形式发票"两个模块。

```
┌──────────────────────────────────────────────────────────────┐
│  PI审核&审核历史                                            │
│──────────────────────────────────────────────────────────────│
│  [待审核] [已审核]                                           │
│──────────────────────────────────────────────────────────────│
│  Tab 待审核:                                                 │
│  Contract No       │ 业务员 │ 提交时间   │ 金额  │操作       │
│  IT-AMD-20260519-01│ sales  │ 2026-05-19 │ €45K  │查看 通过 驳回│
│──────────────────────────────────────────────────────────────│
│  Tab 已审核:                                                 │
│  状态筛选: [全部 ▼] [通过] [驳回]                            │
│  Contract No       │ 业务员 │ 审核  │ 审核时间  │ 驳回原因   │
│  IT-AMD-20260130   │ sales  │ ✅通过│ 2026-01-30│ -         │
│  IT-AMD-20260518   │ sales  │ ❌驳回│ 2026-05-18│ 数量不符  │
└──────────────────────────────────────────────────────────────┘
```

- 待审核 Tab：调用 `GET /api/approvals/pending`
- 已审核 Tab：调用 `GET /api/approvals/history?status=`，支持按状态筛选
- 驳回的 PI 行显示红色驳回原因列
- 点击"查看"跳转 PI 详情（只读模式）

### 5.5.2 PI 详情页 — 驳回原因显示

当 PI 状态为 `REJECTED` 时，PI 详情页顶部显示红色驳回原因横幅：
```
┌──────────────────────────────────────────────────┐
│  ⚠ 该 PI 已被驳回，驳回原因：数量与合同不符      │
│    请修改后重新提交审核                           │
└──────────────────────────────────────────────────┘
```

业务员在"审核历史&形式发票"列表中也能看到驳回状态和原因。

---

**PI 重新导入差异对比弹窗**：

点击"导入修改后的 PI"→ 选择文件 → 系统解析返回差异：

```
┌──────────────────────────────────────────────────────────────┐
│  PI 重新导入 — 差异对比                        [应用修改] [取消]│
│──────────────────────────────────────────────────────────────│
│  文件: Invoice_IT-AMD-20260514_modified.xlsx                │
│  变更统计: 修改 2 行 | 新增 1 行 | 删除 1 行 | 未变更 17 行  │
│──────────────────────────────────────────────────────────────│
│  ┌──────────────────────────────────────────────────────┐    │
│  │ # │ 状态   │ CAT.#         │ Ref No │ Qty │ 变更详情  │    │
│  │ 3 │ ✏️修改  │ 6018007205-CE │ 59400  │ 10→12│ qty1:+2 │    │
│  │ 5 │ ✏️修改  │ 6018007205-CE │ 59400→59406│ 4 │ refNo改 │    │
│  │ 8 │ ❌删除  │ 6018010301-CE │ 59827  │ 3   │ 此行被删 │    │
│  │ 21│ ✅新增  │ 6018013905-CE │ 61197  │ 5   │ 新行     │    │
│  │ 1 │ 未变更  │ 6018006902-CE │ 59824  │ 1   │ —       │    │
│  │ 2 │ 未变更  │ 6018034205-EN │ 59786  │ 10  │ —       │    │
│  └──────────────────────────────────────────────────────┘    │
│──────────────────────────────────────────────────────────────│
│  ⚠ 行 3 数量变更后需重新检查库存                              │
└──────────────────────────────────────────────────────────────┘
```

- 橙色 ✏️ = 修改行，蓝色 ✅ = 新增行，红色 ❌ = 待删除行
- 点击列可手动修正单个变更
- 确认后点击"应用修改"→ 系统更新 PI → 可进入 PL 生成

### 5.7 列表页（PO List / PI List / PL List）

以 PO List 为例，PI/PL List 结构相同：

```
┌──────────────────────────────────────────────────────┐
│  采购订单 (PO)                                       │
│──────────────────────────────────────────────────────│
│  客户: [全部 ▼]  状态: [全部 ▼]                       │
│──────────────────────────────────────────────────────│
│  Contract No      │ 客户     │ 日期       │ 状态 │操作│
│  IT-AMD-20260514  │ A.Menari │ 2026-05-14 │APPROVED│查看│
│  IT-AMD-20260130  │ A.Menari │ 2026-01-30 │COMPLETED│查看│
│──────────────────────────────────────────────────────│
│  共 N 条                      [< 1 2 3 ... >]        │
└──────────────────────────────────────────────────────┘
```

### 5.8 产品总表 (ProductList.vue)

```
┌──────────────────────────────────────────────────────┐
│  产品总表                                            │
│──────────────────────────────────────────────────────│
│  分类: [全部 ▼]  [🔍 搜索产品名称或 CAT.#...]         │
│──────────────────────────────────────────────────────│
│  Menarini│CAT.#        │Product Name       │Price/Kit│
│  61312   │2004009001   │FlashDetect Flash10│€100     │
│  59824   │6018006902-CE│FlashDetect SARS.. │€100     │
│──────────────────────────────────────────────────────│
│  共 113 条                    [< 1 2 3 ... >]        │
└──────────────────────────────────────────────────────┘
```

---

## 六、状态标签配色

| 状态 | Element Plus Type |
|------|-------------------|
| IMPORTED | `info` (灰) |
| DRAFT | `info` (灰) |
| APPROVED | `success` (绿) |
| PI_GENERATED | `primary` (蓝) |
| PACKING_GENERATED | `primary` (蓝) |
| COMPLETED | `success` (绿) |
| REJECTED | `danger` (红) — 预留 |

---

## 七、Axios 封装

```javascript
// api/request.js
import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
});

request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data;
    if (code === 200) return data;
    ElMessage.error(message || '请求失败');
    return Promise.reject(new Error(message));
  },
  error => {
    ElMessage.error(error.message || '网络错误');
    return Promise.reject(error);
  }
);

export default request;
```

PO 导入使用原生 `FormData` + `multipart/form-data`，不走 JSON 拦截器。

---

## 八、交互流程总结

```
客户列表 ──搜索──▶ 客户详情 ──[导入PO Excel]──▶ PO导入页
                                                    │
                                         上传 → 解析 → 预览 → 确认
                                                    │
                                                    ▼
                                              PO 详情页
                                                    │
                                              [生成 PI]
                                                    │
                                                    ▼
                                              PI 详情页
                                         (Invoice 格式)
                                         [导出 PI ⬇]
                                         [导入修改后的 PI ⬆]
                                                    │
                                         上传 → 差异对比 → 应用修改
                                                    │
                                              [生成 PL]
                                                    │
                                                    ▼
                                              PL 详情页
                                         (Packing List 格式)
                                         [导出 PL ⬇]
```

---

## 九、交付大模型的 Vue 代码生成提示词

```
你是一名资深 Vue 3 前端开发工程师。请生成一个完整的 CRM Demo 前端项目。

## 项目背景
IVD 公司海外 CRM Demo 系统。单一业务员视角，核心流程（7 步）：
搜索客户 → 导入客户发来的 PO Excel → 生成 PI (Invoice格式) → 导出 PI → ★ 重新导入修改后的 PI（差异对比 → 应用修改）→ 生成 PL (Packing List格式) → 导出 PL

## 技术栈
Vue 3.4 (Composition API + <script setup>) + Vite 5.x + Element Plus 2.6.x + Vue Router 4.x + Pinia 2.x + Axios 1.x + dayjs

## 页面清单（8 个页面）
1. CustomerList.vue      — 客户列表（搜索 + 分页表格）
2. CustomerDetail.vue    — 客户详情（基本信息 / 联系人 / 历史订单 TAB + [导入PO]按钮）
3. ProductList.vue       — 产品总表（分类筛选 + 搜索 + 分页）
4. PurchaseOrderList.vue — PO 列表（客户/状态筛选 + 分页）
5. PurchaseOrderImport.vue — ★ PO Excel 上传导入（拖拽上传 → 解析预览 → 确认）
6. PurchaseOrderDetail.vue — PO 详情（解析结果 + [生成PI]按钮）
7. ProformaInvoiceDetail.vue — ★ PI 详情（Invoice 类 Excel 格式 + [导出PI] + [生成PL]按钮）
8. PackingListDetail.vue — ★ PL 详情（Packing List 类 Excel 格式 + [导出PL]按钮）

## 路由
[此处贴入第三章路由配置]

## 核心页面布局
[此处贴入第五章每个页面的 ASCII 布局]

## 后端 API
baseURL: /api，返回格式 { code: 200, message: "success", data: {} }

关键端点：
POST  /api/purchase-orders/import        — 上传 PO Excel (FormData: file + customerId)
GET   /api/customers/search?keyword=     — 搜索客户
GET   /api/products                      — 产品列表
POST  /api/proforma-invoices/generate/{poId}  — PO→PI 转换
GET   /api/proforma-invoices/{id}        — PI 详情
GET   /api/proforma-invoices/{id}/export — 导出 PI (blob 下载)
POST  /api/proforma-invoices/{id}/import — ★ 重新导入修改后的 PI (FormData)
POST  /api/proforma-invoices/{id}/apply-import — ★ 确认应用导入变更
POST  /api/packing-lists/generate/{piId} — 打包生成 PL
GET   /api/packing-lists/{id}            — PL 详情
GET   /api/packing-lists/{id}/export     — ★ 导出 PL (blob 下载)
GET   /api/trace/{contractNo}            — 全链路追溯

## 特殊要求
1. PI 详情页按 Invoice 类 Excel 格式展示，顶部 [导出 PI] + [导入修改后的 PI] 按钮
2. PI 重新导入差异对比弹窗：上传文件 → 按行号匹配 → 橙色✏️修改/蓝色✅新增/红色❌删除/灰色未变更 → [应用修改]
3. PL 详情页按 Packing List 类 Excel 格式展示（18 字段分上下两行），顶部 [导出 PL] 按钮
4. PO 导入页：拖拽/点击上传 → 解析预览（成功✅/失败⚠️标记） → 确认导入
5. 客户搜索支持按 Customer ID 精确匹配
6. 导出文件：blob 下载，文件名为 Invoice_{合同号}_{日期}.xlsx / PackingList_{合同号}_{日期}.xlsx
7. 无需登录页、无需角色切换、无需审批页面

## 状态标签配色
IMPORTED/DRAFT=info(灰), APPROVED/COMPLETED=success(绿), PI_GENERATED/PACKING_GENERATED=primary(蓝)

## 输出要求
1. 每个 Vue 文件完整输出：<template> + <script setup> + <style scoped>
2. 所有组件使用 Element Plus
3. 用 // File: src/views/... 标注文件路径
4. 完整路由配置、API 封装、Pinia store
5. package.json 含所有依赖
6. vite.config.js 配置代理到 localhost:8080
```
