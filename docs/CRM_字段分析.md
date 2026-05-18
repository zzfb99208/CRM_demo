# CRM 系统字段分析

> 基于 `01 AMD - 清洁版.xlsx`（客户&产品主数据）和 `清洁版 CIP_IT-AMD-20260130 pending orders.xlsx`（业务单据）分析得出。

---

## 一、PO（Purchase Order / Contract）采购订单关键字段

PO 在 Excel 中对应 **Contract** 工作表，是客户向供应商发出的采购意向单据。

### 1.1 单据头 (Header)

| 字段名 | 示例值 | 说明 |
|--------|--------|------|
| Contract # | IT-AMD-20260130 | 合同/PO 编号，唯一标识 |
| The Buyer | A.MENARINI DIAGNOSTICS SRL | 买方（客户）公司名称及地址 |
| The Seller | Coyote Bioscience Co., Ltd. | 卖方（供应商）公司名称及地址 |
| CYT Order # | AMD01 | 卖方内部订单号 |
| Terms of delivery | FCA | 贸易术语/交货条款 |
| Terms of payment | Net 60 days from invoice date | 付款条款 |
| Transport by | Air | 运输方式 |
| Country of Origin | China | 原产国 |
| PO Reference List | 1429, 31, 32, 33, 47, 1719, ... | 关联的客户 PO 编号列表 |
| Ship To | 客户地址 | 收货地址 |

### 1.2 单据行 (Line Items)

| 字段名 | 示例值 | 说明 |
|--------|--------|------|
| NO. | 1, 2, 3... | 行号 |
| Order # | 1429 / 0001719 / 0001908 | 客户 PO 分单号 |
| CAT. # | 6018006902-CE | 产品目录号（Coyote code） |
| DESCRIPTION | FlashDetect™ SARS-CoV-2... | 产品描述 |
| QTY-1 | 1 / 10 / 40 | 数量（按包装单位1） |
| UNIT-1 | kit / unit | 单位（包装单位1） |
| QTY-2 | 1 / 240 / 960 | 数量（按包装单位2，通常为 test） |
| UNIT-2 | kit / test | 单位（包装单位2） |
| UNIT PRICE | 1 | 单价乘数 |
| Discount | 0 / 1 | 折扣标识（1=免费样品） |
| VALUE | 1 / 2400.00 / 9600.00 | 行总金额（EUR） |

---

## 二、PI（Proforma Invoice / Commercial Invoice）形式发票关键字段

PI 在 Excel 中对应 **Invoice** 工作表，是供应商根据 PO 生成的报价/发票单据。

### 2.1 单据头 (Header)

| 字段名 | 示例值 | 说明 |
|--------|--------|------|
| Invoice # | IT-AMD-20260130 | 发票编号 |
| Date | 2026/1/30 | 开票日期 |
| Contract # | IT-AMD-20260130 | 关联合同号 |
| Bill To | 客户信息 | 付款方 |
| Ship To | 客户信息 | 收货方 |
| Company Name | Coyote Bioscience Co., Ltd. | 供应商名称 |
| Address | Building 22, Zone 3, Gaolizhang Road... | 供应商地址 |
| CYT Order # | AMD02 | 内部订单号 |
| Terms of delivery | FCA | 交货条款 |
| Terms of payment | Net 60 days | 付款条款 |
| Transport by | Air | 运输方式 |
| Country of Origin | China | 原产国 |
| PO Reference List | 1429, 31, 32, ... | 客户 PO 编号列表 |

### 2.2 单据行 (Line Items)

| 字段名 | 示例值 | 说明 |
|--------|--------|------|
| NO. | 1, 2, 3... | 行号 |
| Order # | 1429 / 0001719 | PO 分单号 |
| CAT. # | 6018006902-CE | 产品目录号 |
| Ref No. | 59824 / 59786 | 产品内部参考号（Menarini code） |
| DESCRIPTION | FlashDetect™... | 产品描述 |
| QTY-1 | 1 / 10 | 数量（包装单位1） |
| UNIT-1 | kit / unit | 单位1 |
| QTY-2 | 1 / 240 | 数量（包装单位2） |
| UNIT-2 | kit / test | 单位2 |
| UNIT PRICE | 1 | 单价乘数 |
| Discount | 0 / 1 | 折扣标识 |
| VALUE | 1 / 2400.00 | 行金额（EUR） |

> **与 PO 的差异**: PI 比 PO 多了 **Ref No.**（产品内部参考号）和 **Date**（开票日期），且 Invoice # 与 Contract # 可以相同。

---

## 三、Packing List 装箱单关键字段

Packing List 对应 **! Packing List (给客户）** 工作表，在 PI 确认后生成，用于物流装箱。

### 3.1 单据头 (Header)

| 字段名 | 示例值 | 说明 |
|--------|--------|------|
| Contract # | IT-AMD-20260130 | 关联合同号 |
| Company | Coyote Bioscience Co., Ltd. | 发货方信息 |
| Address | Building 22, Zone 3... | 发货方地址 |
| Ship To | A.MENARINI DIAGNOSTICS SRL... | 收货方完整信息（含 VAT 号） |
| Transport by | Air | 运输方式 |
| Country of Origin | China | 原产国 |
| Transport Route | Beijing to Italy | 运输路线 |
| PO Reference List | 1429, 31, 32, ... | 关联 PO 列表 |

### 3.2 单据行 (Line Items)

| 字段名 | 示例值 | 说明 |
|--------|--------|------|
| NO. | 1, 2, 3... | 行号 |
| Order # | 0002058 / 0001908 | PO 分单号 |
| CAT. # | 2004009001 / 6018012305-CE | 产品目录号 |
| Ref No. | 61312 / 59406 | 产品内部参考号 |
| DESCRIPTION | FlashDetect™ Flash10 System... | 产品描述 |
| QTY-1 | 1 / 4 | 数量（包装单位1） |
| UNIT-1 | unit / kit | 单位1 |
| QTY-2 | 1 / 96 | 数量（包装单位2） |
| UNIT-2 | unit / test | 单位2 |
| Lot NO. | 304090251212009 / 20260108A | **批号**（关键追溯字段） |
| Expiry date | / / 2027.01.07 | **有效期/失效日期**（关键合规字段） |
| DIMENSION | 57\*45\*55cm / 57\*26\*37cm | **外箱尺寸**（长\*宽\*高） |
| Qty. of Carton | 1 / 5 | **每行箱数** |
| Carton # | 1-15 / 17-23 | **箱号范围** |
| VOLUME (CBM) | 0.141075 / 0.054834 | **体积（立方米）** |
| NET WEIGHT (KG) | 21 / 5.6 | **净重（千克）** |
| GROSS WEIGHT (KG) | 33 / 8.7 | **毛重（千克）** |
| 对应DGM中文名 | 基因扩增仪 / MG&TV&MH核酸检测试剂盒 | 中文品名（DGM 危申报用） |
| 对应DGM编码 | PEKBJ202601052581BH640001 | DGM 鉴定编码（危化品鉴定） |

> **PL 独有字段**: Lot NO.、Expiry date、DIMENSION、Qty. of Carton、Carton #、VOLUME CBM、NET/GROSS WEIGHT、DGM 中文名、DGM 编码。这些是物流和报关的核心字段。

---

## 四、Customer（客户）实体字段设计

基于 Excel 中 **Customers** 工作表及 Contract/Invoice/Packing List 中出现的客户信息。

### 4.1 公司主表 (customer)

| 字段名 | 数据类型 | 示例值 | 说明 |
|--------|----------|--------|------|
| customer_id | VARCHAR(32) | AMD | 客户编号（主键） |
| company_name | VARCHAR(200) | A. Menarini Diagnostics SRL | 公司全称 |
| customer_tier | VARCHAR(10) | A | 客户等级（A ≥1.5M） |
| payment_terms | VARCHAR(100) | Net 60 days from invoice date | 付款条款 |
| currency | VARCHAR(10) | EUR | 结算货币 |
| incoterms | VARCHAR(20) | FCA | 贸易术语 |
| website | VARCHAR(200) | — | 公司网址 |
| assay_expiry_date_req | VARCHAR(100) | not less than 6 months | 有效期要求 |
| region_cover | VARCHAR(500) | Italy, Netherlands, Belgium, Spain, France | 覆盖区域 |
| distribution_type | VARCHAR(50) | Exclusive distributor | 分销类型 |
| special_notes | VARCHAR(500) | Special Label and battery label | 特殊备注 |
| term_of_agreement | VARCHAR(50) | 2025-2027 | 协议期限 |
| tax_no | VARCHAR(50) | — | 税号 |
| bank_information | TEXT | — | 银行信息 |
| ship_to_address | TEXT | VIA DEI SETTE SANTI, 3, 50131 FIRENZE, Italy | 收货地址 |
| p_iva | VARCHAR(50) | 05688870483 | VAT 号（意大利） |
| status | TINYINT | 0/1/2 | 状态（0=潜在/1=已签约/2=已终止） |
| created_at | DATETIME | — | 创建时间 |
| updated_at | DATETIME | — | 更新时间 |

### 4.2 联系人子表 (customer_contact)

| 字段名 | 数据类型 | 示例值 | 说明 |
|--------|----------|--------|------|
| contact_id | BIGINT | 自增 | 联系人ID（主键） |
| customer_id | VARCHAR(32) | AMD | 关联客户编号（外键） |
| name | VARCHAR(100) | （匿名化） | 联系人姓名 |
| title | VARCHAR(200) | Global Marketing and Business Development Director | 职位 |
| email | VARCHAR(200) | xxx@xxx.com | 邮箱 |
| phone | VARCHAR(50) | — | 电话 |
| notes | VARCHAR(200) | Marketing / ORDER | 备注（角色标注：Marketing 或 ORDER 对接人） |

---

## 五、Product（商品）实体字段设计

基于 Excel 中 **Products** 工作表及业务单据中出现的产品属性。

### 5.1 商品主表 (product)

| 字段名 | 数据类型 | 示例值 | 说明 |
|--------|----------|--------|------|
| product_id | BIGINT | 自增 | 商品内部ID（主键） |
| menarini_code | VARCHAR(20) | 59390 / 59786 | Menarini 内部编码（Ref No.） |
| coyote_code | VARCHAR(30) | 2004009001 / 6018006902-CE | Coyote 目录号（CAT. #） |
| product_name | VARCHAR(300) | FlashDetect™ Flash10 System | 产品名称 |
| description | VARCHAR(500) | COYOTE FLASH10 SYSTEM 2004009001 | 产品描述 |
| unit_price_kit | DECIMAL(10,2) | 100 | 每 Kit 价格 |
| unit_price_test | DECIMAL(10,2) | 14000 / 8 | 每 Test 价格 |
| currency | VARCHAR(10) | EUR | 价格货币 |
| is_active | TINYINT | 0/1 | 是否激活 |
| notes | VARCHAR(500) | — | 备注 |
| category | VARCHAR(50) | ASSAY / SYSTEM / CONSUMABLE / EQC | 产品分类（可基于数据推导） |
| dimension | VARCHAR(50) | 57\*45\*55cm | 外箱尺寸（从 PL 补充） |
| net_weight_kg | DECIMAL(8,2) | 21 | 净重 kg（从 PL 补充） |
| gross_weight_kg | DECIMAL(8,2) | 33 | 毛重 kg（从 PL 补充） |
| created_at | DATETIME | — | 创建时间 |
| updated_at | DATETIME | — | 更新时间 |

### 5.2 产品分类说明

根据实际数据分析，产品大致分为以下类别：

| 类别 | 说明 | 典型产品 |
|------|------|----------|
| 仪器/系统 | PCR 检测仪器 | Flash10 System, Nano System |
| 检测试剂盒 | 各类病原体检测 Assay | SARS-CoV-2, Flu A/B, MTB, HPV 等 |
| 质控品 | 外部质控品 (EQC) | SPEC.A / SPEC.B |
| 耗材/采样套装 | 运输培养基、采样管等 | UTM Collection Kit, Sputum Liquefaction Kit |
| 配件 | 仪器备件 | 风扇、电源、屏幕、保险丝等 |
| 租赁/服务 | 月度租赁、全风险服务 | Monthly Rent, Full Risk |

---

## 六、PO → PI → PL 字段流转关系

```
┌──────────────────────────────────────────────────────────────────┐
│  PO (Contract)               PI (Invoice)           Packing List │
│  ────────────                ───────────           ──────────── │
│  Order #          ────────▶  Order #               Order #      │
│  CAT. #           ────────▶  CAT. #                CAT. #       │
│  DESCRIPTION      ────────▶  DESCRIPTION           DESCRIPTION  │
│  QTY-1/UNIT-1     ────────▶  QTY-1/UNIT-1           QTY-1/UNIT-1│
│  QTY-2/UNIT-2     ────────▶  QTY-2/UNIT-2           QTY-2/UNIT-2│
│  UNIT PRICE       ────────▶  UNIT PRICE             —            │
│  Discount         ────────▶  Discount               —            │
│  VALUE            ────────▶  VALUE                  —            │
│  —                           Ref No. (Menarini)     Ref No.      │
│  —                           —                       Lot NO. ⬅ 新增│
│  —                           —                       Expiry date ⬅│
│  —                           —                       DIMENSION ⬅  │
│  —                           —                       Carton Qty ⬅ │
│  —                           —                       Carton # ⬅   │
│  —                           —                       VOLUME CBM ⬅ │
│  —                           —                       NET WEIGHT ⬅ │
│  —                           —                       GROSS WEIGHT⬅│
│  —                           —                       DGM 中文名 ⬅ │
│  —                           —                       DGM 编码 ⬅   │
└──────────────────────────────────────────────────────────────────┘
```

**核心流转链路**：客户提供 PO → 系统生成 PI（补充 Ref No.，确认价格）→ 客户确认 PI → 系统生成 Packing List（补充批号、效期、箱规、重量、DGM 信息）。
