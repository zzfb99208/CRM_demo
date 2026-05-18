# CRM Demo 系统 — 产品需求文档 (PRD)

> **系统名称**：Coyote Bioscience 海外 CRM 演示系统  
> **Demo 账号**：仅保留业务员视角，审批者/仓库管理员接口预留后续扩展  
> **核心流程**：客户签约 → PO Excel 导入 → PI 自动生成 → 导出 PI → (外部修改后)重新导入 PI → 触发 PL 生成 → 导出 PL

---

## 一、系统概述

本 Demo 演示一家 IVD 试剂公司（Coyote Bioscience）与海外分销商（如意大利 A. Menarini Diagnostics）之间的 PO→PI→PL 三单流转全链路。

**Demo 聚焦**：业务员从收到客户 PO Excel 到最终导出 Packing List 的完整操作流程。

**简化说明**：
- Demo 使用单一业务员账号，不涉及多角色登录
- 审批环节在 Demo 中自动通过（跳过审批流程），但数据库和 API 已预留审批者角色接口
- 仓库管理员功能（库存管理、装箱信息补充）在 Demo 中简化为系统自动填充 + 业务员手动确认，相关 API 已预留

### 1.1 数据来源

| 数据文件 | 内容 | 用途 |
|---------|------|------|
| `01 AMD - 清洁版.xlsx` → Customers | 客户主数据（1 条：AMD） | 客户信息展示与检索 |
| `01 AMD - 清洁版.xlsx` → Products | 产品主数据（113 条） | PO→PI 编码转换的核心关联表 |
| `清洁版 CIP_IT-AMD-20260130 pending orders.xlsx` → Contract | PO 模板 | PO Excel 导入的参考格式 |
| `清洁版 CIP_IT-AMD-20260130 pending orders.xlsx` → Invoice | PI 模板 | PI 生成与导出的参考格式 |
| `清洁版 CIP_IT-AMD-20260130 pending orders.xlsx` → ! Packing List | PL 模板 | PL 生成与导出的参考格式 |

### 1.2 核心数据流

```
Customer ──签约──▶ Product (产品总表)
   │                   │
   │  customer_id      │  coyote_code (CAT.#) ──┐
   │  company_name     │  menarini_code (Ref No.)│
   │  payment_terms    │  product_name ──────────┤
   │  incoterms        │  unit_price             │
   │                   │  dimension/weight/dgm   │
   ▼                   ▼                         │
 PO Excel ──导入解析──▶ PI ──导出PI(Excel)        │
 (客户发送)           (Invoice)    │               │
                       │          │ 外部修改       │
                       │          ▼               │
                       │   修改后的PI Excel        │
                       │          │               │
                       │    重新导入PI ◀──────────┘
                       │          │               │
                       │    打包算法──▶ PL         │
                       │              (Packing List)
                       │                │          │
                       └── 商品名称关联 ─┘          │
                       └── CAT.# → Ref No. 映射 ───┘
```

---

## 二、业务员操作流程（Demo 演示路径）

业务员登录系统后，按以下步骤完成一次完整的 PO→PI→PL 流转：

```
Step 1 ──▶ Step 2 ──▶ Step 3 ──▶ Step 4 ──▶ Step 5 ──▶ Step 6 ──▶ Step 7
搜索客户    导入PO    生成PI    查看导出    重新导入   触发PL    查看导出
           Excel              PI(Invoice) 修改后的PI  生成      PL(Packing
                                                       List)
```

---

### 2.1 客户信息查看与检索

**入口**：首页 → 客户管理

**功能**：
- 展示已签约客户列表，表格列包含：Customer ID、Company Name、Tier、Payment Terms、Currency、Incoterms、Region Cover、Distribution Type、Status
- 支持按 **Customer ID**（如 "AMD"）搜索
- 点击客户行查看详情，Tab 包含：
  - **基本信息**：等级、付款条款、币种、Incoterms、覆盖区域、分销类型、特殊备注、收货地址
  - **联系人**：姓名、职位、邮箱、电话、备注（Marketing / ORDER 对接人）
  - **历史订单**：该客户关联的 PO/PI/PL 列表

**数据来源**：`01 AMD - 清洁版.xlsx` → Sheet "Customers"

---

### 2.2 产品总表查看

**入口**：产品总表菜单

**功能**：
- 展示所有可用产品（is_active=1），字段含：Menarini code、Coyote code (CAT.#)、Product Name、Description、Category、Unit Price/kit、Unit Price/test
- 支持按产品名称、CAT.#、分类筛选
- 此表是 PO→PI 编码转换的**核心关联数据源**

**数据来源**：`01 AMD - 清洁版.xlsx` → Sheet "Products"（113 条）

---

### 2.3 PO Excel 导入（★ 核心步骤）

**入口**：客户详情 → 导入 PO Excel

**流程**：

**① 上传** — 选择客户发来的 PO Excel 文件（`.xlsx` / `.xls`），系统自动关联当前客户。

**② 解析** — 系统读取 Excel，自动识别表头区域和行项目区域，提取以下字段：

| 区域 | 识别字段 | 存储目标 |
|------|---------|---------|
| 表头 | Contract # | `purchase_order.contract_no` |
| 表头 | Terms of delivery | `purchase_order.delivery_terms` |
| 表头 | Terms of payment | `purchase_order.payment_terms` |
| 表头 | Transport by | `purchase_order.transport_method` |
| 表头 | Country of Origin | `purchase_order.country_of_origin` |
| 表头 | PO Reference 列表 | `purchase_order.po_reference` |
| 行项目 | Order # | `purchase_order_item.customer_po_no` |
| 行项目 | CAT. # | `purchase_order_item.cat_no` |
| 行项目 | DESCRIPTION | `purchase_order_item.description` |
| 行项目 | QTY-1 / UNIT-1 | `purchase_order_item.qty1` / `unit1` |
| 行项目 | QTY-2 / UNIT-2 | `purchase_order_item.qty2` / `unit2` |
| 行项目 | UNIT PRICE | `purchase_order_item.unit_price_multiplier` |
| 行项目 | Discount / VALUE | `purchase_order_item.discount_flag` / `line_value` |

**③ 校验** — 自动生成 Contract No（`IT-{CustomerCode}-{yyyyMMdd}`），校验 CAT.# 是否在 product 表中存在，识别免费样品行（VALUE 含 "free of charge" → discount_flag=1）。

**④ 预览** — 表格展示解析结果，黄色标记"未识别产品"，绿色标记"免费样品"。业务员可手动修正。

**⑤ 保存** — 确认无误后保存，PO 状态 = `IMPORTED`。Demo 中自动通过审批 → `APPROVED`，可立即生成 PI。

> **参考格式**：`清洁版 CIP_IT-AMD-20260130 pending orders.xlsx` → Sheet "Contract"

---

### 2.4 PO → PI 自动生成（★ 核心步骤）

**入口**：PO 详情 → 生成 PI

**转换逻辑**（系统自动执行）：

```
对每个 PO 行项目：
  1. 读取 CAT. # (coyote_code) + DESCRIPTION
  2. 在 Product 表中匹配：
     优先：CAT. # 精确匹配 product.coyote_code
     兜底：DESCRIPTION 关键词匹配 product.product_name
  3. 匹配成功 → 获取 Ref No. = product.menarini_code
     匹配失败 → Ref No. 留空，标记 warning
  4. 库存检查 → 汇总 inventory 可用量，不足时标记 warning
  5. line_value = unit_price_multiplier × qty1
  6. 汇总 total_value
```

**转换效果示例**：

| PO 中 CAT.# | → Product Name 匹配 → | PI 中 Ref No. |
|------------|----------------------|--------------|
| 2004009001 | FlashDetect™ Flash10 System | 61312 |
| 6018006902-CE | FlashDetect™ SARS-CoV-2&FluA&FluB EQC (SPEC.B) | 59824 |
| 6018012305-CE | FlashDetect™ LyocartD MG&TV&MH Assay | 59406 |

**生成结果**：
- PI 表头从 PO 继承（Contract #, Customer, Terms, Transport 等）
- Invoice # 默认等于 Contract #，Invoice Date = 当前日期
- 行项目比 PO 多 **Ref No.** 列（Menarini code）
- 未匹配行标记 warning，业务员可手动补充 Ref No.
- PI 状态 = `DRAFT`，Demo 中自动通过审批 → `APPROVED`

---

### 2.5 Invoice (PI) 查看与导出

**入口**：PI 详情页

**查看**：
- 按 Invoice 格式展示：表头（Invoice #, Date, Contract #, Bill To, Ship To, Terms, Transport, PO Ref）+ 行项目表格（含 Ref No. 列）+ Total Value
- 库存不足行黄色高亮

**导出**：
- 点击"导出 PI"，生成 `Invoice_{ContractNo}_{yyyyMMdd}.xlsx`
- 格式参照 `清洁版 CIP_IT-AMD-20260130 pending orders.xlsx` → Sheet "Invoice"
- 导出的 Excel 可供业务员发送给客户确认，客户或业务员可在此 Excel 上直接修改（如调整数量、修正 Ref No.、标注确认意见等）

---

### 2.5.1 PI 修改后重新导入（★ 新增步骤）

**入口**：PI 详情页 → 导入修改后的 PI

**背景**：业务员将系统生成的 PI 导出为 Excel 后，通常需要发送给客户确认。客户可能在 Excel 中直接修改（如调整订购数量、修正产品信息等）并回传。业务员需要将修改后的 PI Excel 重新导入系统，覆盖当前 PI 数据，然后基于最终确认的 PI 进入 PL 生成流程。

**流程**：

**① 上传修改后的 PI Excel**
- 在 PI 详情页点击"导入修改后的 PI"
- 选择客户回传的 PI Excel 文件（`.xlsx` / `.xls`）
- 系统按 PI 表关联当前 PI ID

**② 系统解析识别**
系统读取修改后的 PI Excel，自动识别并提取字段：

| 区域 | 识别字段 | 更新目标 |
|------|---------|---------|
| 表头 | Invoice # | 校验是否与当前 PI 一致 |
| 表头 | Date | `proforma_invoice.invoice_date` |
| 表头 | Terms of delivery | `proforma_invoice.delivery_terms`（如有变更） |
| 表头 | Terms of payment | `proforma_invoice.payment_terms`（如有变更） |
| 行项目 | Order # | `proforma_invoice_item.customer_po_no` |
| 行项目 | CAT. # | `proforma_invoice_item.cat_no` |
| 行项目 | **Ref No.** | `proforma_invoice_item.ref_no`（客户可能修正） |
| 行项目 | DESCRIPTION | `proforma_invoice_item.description` |
| 行项目 | QTY-1 / UNIT-1 | `proforma_invoice_item.qty1` / `unit1`（客户可能调整数量） |
| 行项目 | QTY-2 / UNIT-2 | `proforma_invoice_item.qty2` / `unit2` |
| 行项目 | UNIT PRICE | `proforma_invoice_item.unit_price_multiplier` |
| 行项目 | VALUE | `proforma_invoice_item.line_value` |

**③ 差异对比与预览**
- 系统逐行对比导入数据与当前 PI 数据
- 变更项用**橙色高亮**标记（数量变更、Ref No. 变更、新增行、删除行）
- 未变更项保持正常显示
- 顶部汇总变更统计：`共 X 行变更：数量调整 Y 行 / Ref No. 修正 Z 行 / 新增 W 行 / 删除 V 行`

**④ 确认更新**
- 业务员审核差异，确认无误后点击"应用修改"
- 系统更新 PI 主表和行项目表
- 重新计算 `total_value`
- 重新执行库存检查（数量变更可能导致新的库存不足警告）
- PI 状态重置为 `DRAFT` → Demo 中自动通过 → `APPROVED`

**⑤ 进入 PL 生成**
- 更新完成后，业务员可立即点击"生成 Packing List"进入打包流程

**PI 重新导入的容错处理**：
- 行项目匹配方式：按 `line_no` (NO.) 匹配行号，行号不存在则视为新增行
- Excel 中存在但系统中不存在的行号 → 新增行
- 系统中存在但 Excel 中不存在的行号 → 标记为待删除（业务员确认后删除）
- CAT.# 无法匹配 Product 表 → 标记 warning，Ref No. 留空

---

### 2.6 触发 PL 生成与导出（★ 核心步骤）

**入口**：PI 详情 → 生成 Packing List

**触发**：业务员确认 PI 数据为最终版本（无论是系统初次生成的 PI 还是经过 2.5.1 重新导入修改后的 PI）后，点击"生成 PL"，系统自动执行打包算法。

#### 打包算法

```
对 PI 每个行项目：

1. 判类型：
   仪器类 (unit="unit") → 每台一箱，dimension="57*45*55cm"，单箱体积=0.141075 CBM
   试剂类 (unit="kit")  → total_kits = qty1

2. 算箱数（试剂类）：
   full_cartons = total_kits ÷ 6
   remaining    = total_kits % 6

   if remaining > 0:
       if remaining < 4  → 小箱 dimension="35*38*26cm" 体积=0.03458 CBM
       if remaining >= 4 → 标准箱 dimension="57*26*37cm" 体积=0.054834 CBM
       carton_count = full_cartons + 1
   else:
       carton_count = full_cartons  (全部标准箱)

3. 箱号：全局递增（跨行连续编号），多箱用 "11-15" 范围格式

4. 体积重量：
   行体积 = carton_count × 单箱体积
   行净重 = qty1 × product.net_weight_kg
   行毛重 = 净重 × 1.15（无数据时默认系数）
```

**验证示例**：

| 场景 | 计算 | 结果 |
|------|------|------|
| 30 kits | 30÷6=5 满箱, rem=0 | 5 标准箱, #11-15, 0.27417 CBM |
| 4 kits | 4÷6=0 满箱, rem=4≥4 | 1 标准箱, #16, 0.054834 CBM |
| 2 kits | 2÷6=0 满箱, rem=2<4 | 1 小箱, #114, 0.03458 CBM |
| 1 unit | 仪器类 | 1 大箱, 57\*45\*55cm, 0.141075 CBM |

**补充信息**：系统自动计算箱数/箱号/箱型/体积/重量/DGM 信息，业务员可手动修正。Demo 中批号和效期从 inventory 表自动分配（取库存充足且效期最近的批次）。

**PL 行项目字段（18 个）**：
NO. / Order # / CAT. # / Ref No. / DESCRIPTION / QTY-1 / UNIT-1 / QTY-2 / UNIT-2 / Lot NO. / Expiry date / DIMENSION / Qty. of Carton / Carton # / VOLUME CBM / NET WEIGHT KG / GROSS WEIGHT KG / DGM 中文名 / DGM 编码

#### 查看与导出

**查看**：按 Packing List 格式展示，底部汇总 Total Cartons / Total Volume / Total Net Weight / Total Gross Weight。

**导出**：点击"导出 PL"，生成 `PackingList_{ContractNo}_{yyyyMMdd}.xlsx`，格式参照 `! Packing List (给客户）` 表。

---

## 三、Demo 简化与接口预留

### 3.1 Demo 简化项

| 简化项 | Demo 处理方式 | 说明 |
|--------|-------------|------|
| 多账号登录 | 单一业务员账号 | 无角色切换 |
| PO 审批 | 导入后自动通过 (IMPORTED→APPROVED) | 跳过审批流 |
| PI 审批 | 生成/重新导入后自动通过 (DRAFT→APPROVED) | 跳过审批流 |
| PL 审批 | 生成后自动通过 (DRAFT→FINAL) | 跳过审批流 |
| 库存管理 | 系统自动分配批号/效期 | 取库存充足且效期最近批次 |
| 装箱信息补充 | 系统自动计算 + 业务员手动确认 | 仓库管理员接口预留 |

### 3.2 预留接口（后续扩展）

以下 API 已在数据库和代码中预留，Demo 中不展示前端页面：

| 预留模块 | API 端点 | 说明 |
|---------|---------|------|
| **业务员 — PI 重新导入** | `POST /api/proforma-invoices/{id}/import` | ★ Demo 使用：上传修改后的 PI Excel，解析并更新 PI |
| 审批者 — PO 审批 | `POST /api/approvals/po/{id}` | 审批通过/驳回 + 审批意见 |
| 审批者 — PI 审批 | `POST /api/approvals/pi/{id}` | 审批通过/驳回 + 审批意见 |
| 审批者 — PL 审批 | `POST /api/approvals/pl/{id}` | 审批通过/驳回 + 审批意见 |
| 审批者 — 待审批列表 | `GET /api/approvals/pending` | 按 PO/PI/PL 分类 |
| 仓库管理员 — 库存管理 | `GET/POST/PUT /api/inventories` | 批次增删改查 |
| 仓库管理员 — PL 装箱详情 | `PUT /api/packing-lists/{id}/items/{itemId}` | 逐行补充批号/箱规/重量/DGM |

---

## 四、单据状态（Demo 简化版）

Demo 中 PO/PI/PL 均跳过审批环节：

```
PO:  IMPORTED ──(自动通过)──▶ APPROVED ──生成PI──▶ PI_GENERATED ──PI确认──▶ COMPLETED

PI:  DRAFT ──(自动通过)──▶ APPROVED ──打包完成──▶ PACKING_GENERATED

PL:  DRAFT ──(自动通过)──▶ APPROVED (FINAL)
```

> 注：`PENDING_APPROVAL` 和 `REJECTED` 状态在数据库枚举中已定义，Demo 中不使用但保留以支持后续审批流扩展。

---

## 五、文件导入/导出规范

### 5.1 PO Excel 导入

| 项目 | 规范 |
|------|------|
| 支持格式 | `.xlsx` / `.xls` |
| 参考模板 | `清洁版 CIP_IT-AMD-20260130 pending orders.xlsx` → Sheet "Contract" |
| 解析方式 | Apache POI 按行读取，通过关键字匹配表头/行项目区域 |
| 表头关键字 | "Contract #", "The Buyer", "Terms of", "Ship To", "Transport by", "Country of Origin" |
| 行项目起始 | "NO." / "Order #" / "CAT. #" 列头行的下一行 |
| 容错 | CAT.# 匹配不到 → 标记"未识别产品"；VALUE 含 "free of charge" → 标记免费样品 |

### 5.2 PI 导出

| 项目 | 规范 |
|------|------|
| 格式 | `.xlsx`，参照 Invoice 表 |
| 文件名 | `Invoice_{ContractNo}_{yyyyMMdd}.xlsx` |
| 内容 | 表头 + 行项目（含 Ref No.）+ Total Value |

### 5.2.1 PI 重新导入

| 项目 | 规范 |
|------|------|
| 支持格式 | `.xlsx` / `.xls` |
| 参考模板 | 与 PI 导出格式完全一致（即导出的 PI Excel 可直接修改后重新导入） |
| 解析方式 | Apache POI 按行读取，与 PO 导入共用解析引擎 |
| 匹配方式 | 按 NO. (行号) 匹配当前 PI 行项目，行号不存在→新增，Excel 中缺失→待删除 |
| 差异标记 | 数量变更、Ref No. 变更、新增行→橙色高亮；未变更→正常显示 |
| 容错 | CAT.# 无法匹配→标记 warning；金额无法解析→标记"需手动确认" |

### 5.3 PL 导出

| 项目 | 规范 |
|------|------|
| 格式 | `.xlsx`，参照 Packing List 表 |
| 文件名 | `PackingList_{ContractNo}_{yyyyMMdd}.xlsx` |
| 内容 | 表头 + 18 字段行项目 + 汇总行（Total Cartons / Volume / Net Weight / Gross Weight） |

---

## 六、非功能需求

| 需求项 | 说明 |
|--------|------|
| 响应时间 | PO Excel 导入解析 < 10s，PI/PL 生成 < 5s |
| 用户界面 | 简洁表格型 UI，单据以类 Excel 格式展示，导入/导出按钮突出 |
| 编码规范 | Contract No: `IT-{CustomerCode}-{yyyyMMdd}` |
| Excel 兼容 | Apache POI 处理 `.xlsx` / `.xls` |
| Demo 预置数据 | 1 客户 (AMD) + 20 产品 + 5 库存批次 + 1 个可直接导入的 PO Excel |
| 前端页面数 | 约 8 个页面：客户列表/详情、产品总表、PO 导入/详情、PI 详情、PL 详情 |
