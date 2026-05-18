# CRM Demo 系统 — MySQL 数据库设计

> 包含 10 张表的完整 DDL、索引设计、关系说明，以及可交付大模型生成代码的提示词。

---

## 一、数据库 ER 关系概要

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│   customer   │       │   product    │       │  inventory   │
│──────────────│       │──────────────│       │──────────────│
│ id (PK)      │       │ id (PK)      │       │ id (PK)      │
│ customer_code│       │ coyote_code  │──┐    │ product_id   │──┐
│ company_name │       │ menarini_code│  │    │ lot_no       │  │
│ ...          │       │ product_name │  │    │ expiry_date  │  │
└──────┬───────┘       │ category     │  │    │ quantity_kit │  │
       │               │ unit_price.. │  │    │ quantity_test│  │
       │               │ dimension    │  │    └──────────────┘  │
       │               │ net_weight.. │  │                      │
       │               │ dgm_name_cn  │  │                      │
       │               │ dgm_code     │  │                      │
       │               └──────┬───────┘  │                      │
       │                      │          │                      │
       │    ┌─────────────────┘          │                      │
       │    │                            │                      │
       ▼    ▼                            ▼                      ▼
┌────────────────┐  ┌────────────────┐  ┌────────────────┐
│ purchase_order │  │proforma_invoice│  │  packing_list  │
│────────────────│  │────────────────│  │────────────────│
│ id (PK)        │  │ id (PK)        │  │ id (PK)        │
│ contract_no    │  │ invoice_no     │  │ pi_id (FK)     │
│ customer_id(FK)│  │ po_id (FK)     │  │ contract_no    │
│ status         │  │ contract_no    │  │ customer_id(FK)│
└───────┬────────┘  │ customer_id(FK)│  │ total_volume.. │
        │           │ total_value    │  │ total_net_w..  │
        │           │ status         │  │ total_gross..  │
        │           └───────┬────────┘  │ total_cartons  │
        │                   │           │ status         │
        │                   │           └───────┬────────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌────────────────┐  ┌────────────────┐  ┌────────────────┐
│ po_item        │  │ pi_item        │  │ pl_item        │
│────────────────│  │────────────────│  │────────────────│
│ id (PK)        │  │ id (PK)        │  │ id (PK)        │
│ po_id (FK)     │  │ pi_id (FK)     │  │ pl_id (FK)     │
│ product_id(FK) │  │ product_id(FK) │  │ product_id(FK) │
│ cat_no         │  │ cat_no         │  │ cat_no         │
│ ...            │  │ ref_no ⬅ 新增  │  │ ref_no         │
│                │  │ ...            │  │ lot_no ⬅ 新增   │
│                │  │                │  │ expiry_date ⬅  │
│                │  │                │  │ dimension ⬅    │
│                │  │                │  │ carton # ⬅     │
│                │  │                │  │ volume CBM ⬅   │
│                │  │                │  │ net/gross wt⬅  │
│                │  │                │  │ dgm info ⬅     │
└────────────────┘  └────────────────┘  └────────────────┘

┌──────────────────┐
│ customer_contact │
│──────────────────│
│ id (PK)          │
│ customer_id (FK) │
│ contact_name     │
│ title            │
│ email            │
│ phone            │
│ notes            │
└──────────────────┘
```

---

## 二、完整 DDL

### 2.1 customer（客户表）

```sql
CREATE TABLE customer (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    customer_code   VARCHAR(32)     NOT NULL UNIQUE COMMENT '客户编号，如 AMD',
    company_name    VARCHAR(200)    NOT NULL COMMENT '公司全称',
    customer_tier   VARCHAR(10)     DEFAULT NULL COMMENT '客户等级 A/B/C',
    payment_terms   VARCHAR(100)    DEFAULT 'Net 60 days from invoice date' COMMENT '付款条款',
    currency        VARCHAR(10)     DEFAULT 'EUR' COMMENT '结算货币',
    incoterms       VARCHAR(20)     DEFAULT 'FCA' COMMENT '贸易术语',
    website         VARCHAR(200)    DEFAULT NULL COMMENT '公司网址',
    assay_expiry_req VARCHAR(100)   DEFAULT 'not less than 6 months' COMMENT '效期要求',
    region_cover    VARCHAR(500)    DEFAULT NULL COMMENT '覆盖区域',
    distribution_type VARCHAR(50)   DEFAULT NULL COMMENT '分销类型，如 Exclusive distributor',
    term_of_agreement VARCHAR(50)   DEFAULT NULL COMMENT '协议期限',
    tax_no          VARCHAR(50)     DEFAULT NULL COMMENT '税号',
    p_iva           VARCHAR(50)     DEFAULT NULL COMMENT 'VAT号（意大利）',
    bank_info       TEXT            DEFAULT NULL COMMENT '银行信息',
    ship_to_address TEXT            DEFAULT NULL COMMENT '收货地址',
    special_notes   VARCHAR(500)    DEFAULT NULL COMMENT '特殊备注，如标签/电池要求',
    status          TINYINT         DEFAULT 0 COMMENT '0=潜在 1=已签约 2=已终止',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_code (customer_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';
```

### 2.2 customer_contact（客户联系人表）

```sql
CREATE TABLE customer_contact (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT          NOT NULL COMMENT '关联客户ID',
    contact_name    VARCHAR(100)    DEFAULT NULL COMMENT '联系人姓名',
    title           VARCHAR(200)    DEFAULT NULL COMMENT '职位',
    email           VARCHAR(200)    DEFAULT NULL COMMENT '邮箱',
    phone           VARCHAR(50)     DEFAULT NULL COMMENT '电话',
    notes           VARCHAR(200)    DEFAULT NULL COMMENT '备注，如 Marketing 或 ORDER 对接人',
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户联系人表';
```

### 2.3 product（商品表）

```sql
CREATE TABLE product (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    menarini_code   VARCHAR(20)     DEFAULT NULL COMMENT 'Menarini内部编码(Ref No.)',
    coyote_code     VARCHAR(30)     NOT NULL UNIQUE COMMENT 'Coyote目录号(CAT.#)',
    product_name    VARCHAR(300)    NOT NULL COMMENT '产品名称',
    description     VARCHAR(500)    DEFAULT NULL COMMENT '产品描述',
    category        VARCHAR(30)     DEFAULT NULL COMMENT '分类: INSTRUMENT/ASSAY/EQC/CONSUMABLE/ACCESSORY/SERVICE',
    unit_price_kit  DECIMAL(10,2)   DEFAULT NULL COMMENT '每Kit价格(EUR)',
    unit_price_test DECIMAL(10,2)   DEFAULT NULL COMMENT '每Test价格(EUR)',
    dimension       VARCHAR(50)     DEFAULT NULL COMMENT '外箱尺寸, 如 57*26*37cm',
    net_weight_kg   DECIMAL(8,2)    DEFAULT NULL COMMENT '单件净重(kg)',
    gross_weight_kg DECIMAL(8,2)    DEFAULT NULL COMMENT '单件毛重(kg)',
    dgm_name_cn     VARCHAR(200)    DEFAULT NULL COMMENT 'DGM中文品名',
    dgm_code        VARCHAR(50)     DEFAULT NULL COMMENT 'DGM编码',
    is_active       TINYINT         DEFAULT 1 COMMENT '是否启用 0=禁用 1=启用',
    notes           VARCHAR(500)    DEFAULT NULL,
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_coyote_code (coyote_code),
    INDEX idx_menarini_code (menarini_code),
    INDEX idx_category (category),
    INDEX idx_product_name (product_name(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

### 2.4 inventory（库存表）

```sql
CREATE TABLE inventory (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT          NOT NULL COMMENT '商品ID',
    lot_no          VARCHAR(50)     NOT NULL COMMENT '批号',
    expiry_date     DATE            DEFAULT NULL COMMENT '失效日期',
    quantity_kit    INT             DEFAULT 0 COMMENT 'Kit库存数量',
    quantity_test   INT             DEFAULT 0 COMMENT 'Test库存数量',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    INDEX idx_product_id (product_id),
    INDEX idx_lot_no (lot_no),
    INDEX idx_expiry_date (expiry_date),
    UNIQUE KEY uk_product_lot (product_id, lot_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';
```

### 2.5 purchase_order（PO 采购订单主表）

```sql
CREATE TABLE purchase_order (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    contract_no     VARCHAR(50)     NOT NULL UNIQUE COMMENT '合同号, 如 IT-AMD-20260130',
    customer_id     BIGINT          NOT NULL COMMENT '客户ID',
    cyt_order_no    VARCHAR(50)     DEFAULT NULL COMMENT '内部订单号, 如 AMD01',
    delivery_terms  VARCHAR(50)     DEFAULT 'FCA' COMMENT '交货条款',
    payment_terms   VARCHAR(100)    DEFAULT 'Net 60 days from invoice date' COMMENT '付款条款',
    transport_method VARCHAR(50)    DEFAULT 'Air' COMMENT '运输方式',
    country_of_origin VARCHAR(50)   DEFAULT 'China' COMMENT '原产国',
    po_reference    VARCHAR(500)    DEFAULT NULL COMMENT '客户PO编号列表',
    status          VARCHAR(20)     DEFAULT 'IMPORTED' COMMENT 'IMPORTED/APPROVED/PI_GENERATED/COMPLETED (Demo); PENDING_APPROVAL/REJECTED (扩展预留)',
    created_by      BIGINT          DEFAULT NULL COMMENT '创建人(业务员)ID',
    approved_by     BIGINT          DEFAULT NULL COMMENT '审批人ID',
    approved_at     DATETIME        DEFAULT NULL COMMENT '审批时间',
    reject_reason   VARCHAR(500)    DEFAULT NULL COMMENT '驳回原因',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_contract_no (contract_no),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单主表(PO)';
```

### 2.6 purchase_order_item（PO 行项目表）

```sql
CREATE TABLE purchase_order_item (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    po_id               BIGINT          NOT NULL COMMENT 'PO主表ID',
    line_no             INT             NOT NULL COMMENT '行号',
    customer_po_no      VARCHAR(50)     DEFAULT NULL COMMENT '客户PO分单号(Order #)',
    product_id          BIGINT          NOT NULL COMMENT '商品ID',
    cat_no              VARCHAR(30)     DEFAULT NULL COMMENT 'CAT.# (coyote_code)',
    description         VARCHAR(500)    DEFAULT NULL COMMENT '产品描述',
    qty1                INT             DEFAULT 0 COMMENT '数量(包装单位1)',
    unit1               VARCHAR(20)     DEFAULT 'kit' COMMENT '单位1',
    qty2                INT             DEFAULT 0 COMMENT '数量(包装单位2, test数)',
    unit2               VARCHAR(20)     DEFAULT 'test' COMMENT '单位2',
    unit_price_multiplier DECIMAL(5,2)  DEFAULT 1.00 COMMENT '单价乘数',
    discount_flag       TINYINT         DEFAULT 0 COMMENT '折扣标识 0=正常 1=免费样品',
    line_value          DECIMAL(12,2)   DEFAULT 0.00 COMMENT '行金额(EUR)',
    INDEX idx_po_id (po_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PO行项目表';
```

### 2.7 proforma_invoice（PI 形式发票主表）

```sql
CREATE TABLE proforma_invoice (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    invoice_no          VARCHAR(50)     NOT NULL UNIQUE COMMENT '发票号',
    po_id               BIGINT          NOT NULL COMMENT '关联PO ID',
    contract_no         VARCHAR(50)     DEFAULT NULL COMMENT '关联合同号',
    customer_id         BIGINT          NOT NULL COMMENT '客户ID',
    cyt_order_no        VARCHAR(50)     DEFAULT NULL COMMENT '内部订单号',
    invoice_date        DATE            NOT NULL COMMENT '开票日期',
    delivery_terms      VARCHAR(50)     DEFAULT 'FCA' COMMENT '交货条款',
    payment_terms       VARCHAR(100)    DEFAULT NULL COMMENT '付款条款',
    transport_method    VARCHAR(50)     DEFAULT 'Air' COMMENT '运输方式',
    country_of_origin   VARCHAR(50)     DEFAULT 'China' COMMENT '原产国',
    po_reference        VARCHAR(500)    DEFAULT NULL COMMENT '客户PO列表',
    total_value         DECIMAL(14,2)   DEFAULT 0.00 COMMENT '总金额(EUR)',
    status              VARCHAR(20)     DEFAULT 'DRAFT' COMMENT 'DRAFT/APPROVED/PACKING_GENERATED (Demo); PENDING_APPROVAL/REJECTED (扩展预留)',
    created_by          BIGINT          DEFAULT NULL COMMENT '创建人ID',
    approved_by         BIGINT          DEFAULT NULL COMMENT '审批人ID',
    approved_at         DATETIME        DEFAULT NULL COMMENT '审批时间',
    reject_reason       VARCHAR(500)    DEFAULT NULL COMMENT '驳回原因',
    created_at          DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_invoice_no (invoice_no),
    INDEX idx_po_id (po_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='形式发票主表(PI)';
```

### 2.8 proforma_invoice_item（PI 行项目表）

```sql
CREATE TABLE proforma_invoice_item (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    pi_id               BIGINT          NOT NULL COMMENT 'PI主表ID',
    line_no             INT             NOT NULL COMMENT '行号',
    customer_po_no      VARCHAR(50)     DEFAULT NULL COMMENT 'PO分单号(Order #)',
    product_id          BIGINT          NOT NULL COMMENT '商品ID',
    cat_no              VARCHAR(30)     DEFAULT NULL COMMENT 'CAT.# (coyote_code)',
    ref_no              VARCHAR(20)     DEFAULT NULL COMMENT 'Ref No. (menarini_code) ⬅ PI独有字段',
    description         VARCHAR(500)    DEFAULT NULL COMMENT '产品描述',
    qty1                INT             DEFAULT 0 COMMENT '数量1',
    unit1               VARCHAR(20)     DEFAULT 'kit' COMMENT '单位1',
    qty2                INT             DEFAULT 0 COMMENT '数量2',
    unit2               VARCHAR(20)     DEFAULT 'test' COMMENT '单位2',
    unit_price_multiplier DECIMAL(5,2)  DEFAULT 1.00 COMMENT '单价乘数',
    discount_flag       TINYINT         DEFAULT 0 COMMENT '折扣标识',
    line_value          DECIMAL(12,2)   DEFAULT 0.00 COMMENT '行金额(EUR)',
    INDEX idx_pi_id (pi_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PI行项目表';
```

### 2.9 packing_list（装箱单主表）

```sql
CREATE TABLE packing_list (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    pi_id               BIGINT          NOT NULL COMMENT '关联PI ID',
    contract_no         VARCHAR(50)     DEFAULT NULL COMMENT '合同号',
    customer_id         BIGINT          NOT NULL COMMENT '客户ID',
    ship_to             TEXT            DEFAULT NULL COMMENT '收货地址',
    transport_method    VARCHAR(50)     DEFAULT 'Air' COMMENT '运输方式',
    transport_route     VARCHAR(200)    DEFAULT NULL COMMENT '运输路线, 如 Beijing to Italy',
    country_of_origin   VARCHAR(50)     DEFAULT 'China' COMMENT '原产国',
    po_reference        VARCHAR(500)    DEFAULT NULL COMMENT 'PO列表',
    total_volume_cbm    DECIMAL(10,4)   DEFAULT 0.0000 COMMENT '总体积(CBM)',
    total_net_weight_kg DECIMAL(10,2)   DEFAULT 0.00 COMMENT '总净重(kg)',
    total_gross_weight_kg DECIMAL(10,2) DEFAULT 0.00 COMMENT '总毛重(kg)',
    total_cartons       INT             DEFAULT 0 COMMENT '总箱数',
    status              VARCHAR(20)     DEFAULT 'DRAFT' COMMENT 'DRAFT/APPROVED (Demo); PENDING_APPROVAL/REJECTED (扩展预留)',
    created_by          BIGINT          DEFAULT NULL COMMENT '创建人(仓库管理员)ID',
    approved_by         BIGINT          DEFAULT NULL COMMENT '审批人ID',
    approved_at         DATETIME        DEFAULT NULL COMMENT '审批时间',
    reject_reason       VARCHAR(500)    DEFAULT NULL COMMENT '驳回原因',
    created_at          DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pi_id (pi_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装箱单主表(PL)';
```

### 2.10 packing_list_item（装箱单行项目表）

```sql
CREATE TABLE packing_list_item (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    pl_id               BIGINT          NOT NULL COMMENT '装箱单主表ID',
    line_no             INT             NOT NULL COMMENT '行号',
    customer_po_no      VARCHAR(50)     DEFAULT NULL COMMENT 'PO分单号(Order #)',
    product_id          BIGINT          NOT NULL COMMENT '商品ID',
    cat_no              VARCHAR(30)     DEFAULT NULL COMMENT 'CAT.#',
    ref_no              VARCHAR(20)     DEFAULT NULL COMMENT 'Ref No.',
    description         VARCHAR(500)    DEFAULT NULL COMMENT '产品描述',
    qty1                INT             DEFAULT 0 COMMENT '数量(包装单位1)',
    unit1               VARCHAR(20)     DEFAULT 'kit' COMMENT '单位1',
    qty2                INT             DEFAULT 0 COMMENT '数量(包装单位2)',
    unit2               VARCHAR(20)     DEFAULT 'test' COMMENT '单位2',
    lot_no              VARCHAR(50)     DEFAULT NULL COMMENT '批号 ⬅ PL独有',
    expiry_date         DATE            DEFAULT NULL COMMENT '失效日期 ⬅ PL独有',
    dimension           VARCHAR(50)     DEFAULT NULL COMMENT '外箱尺寸 ⬅ PL独有',
    qty_carton          INT             DEFAULT 0 COMMENT '该行箱数 ⬅ PL独有',
    carton_no_range     VARCHAR(50)     DEFAULT NULL COMMENT '箱号范围 ⬅ PL独有, 如 11-15',
    volume_cbm          DECIMAL(10,4)   DEFAULT 0.0000 COMMENT '体积(CBM) ⬅ PL独有',
    net_weight_kg       DECIMAL(8,2)    DEFAULT 0.00 COMMENT '净重(kg) ⬅ PL独有',
    gross_weight_kg     DECIMAL(8,2)    DEFAULT 0.00 COMMENT '毛重(kg) ⬅ PL独有',
    dgm_name_cn         VARCHAR(200)    DEFAULT NULL COMMENT 'DGM中文品名 ⬅ PL独有',
    dgm_code            VARCHAR(50)     DEFAULT NULL COMMENT 'DGM编码 ⬅ PL独有',
    INDEX idx_pl_id (pl_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装箱单行项目表(PL)';
```

---

## 三、索引设计说明

| 表名 | 索引 | 用途 |
|------|------|------|
| customer | idx_customer_code | 按客户编号搜索（核心检索字段） |
| customer | idx_status | 筛选已签约/潜在客户 |
| product | idx_coyote_code | PO→PI 时按 CAT.# 匹配 |
| product | idx_menarini_code | 按 Ref No. 查询 |
| product | idx_product_name | Product Name 模糊搜索 |
| product | idx_category | 按分类筛选 |
| inventory | uk_product_lot | 同一产品同一批号唯一 |
| inventory | idx_expiry_date | 按效期筛选库存 |
| purchase_order | idx_contract_no | 全链路追溯查询 |
| purchase_order | idx_status | 审批列表按状态筛选 |
| proforma_invoice | idx_invoice_no | 按发票号查询 |
| packing_list | idx_pi_id | 按 PI 关联查询 PL |

---

## 四、PO→PI→PL 字段流转对照

```
PO Item                PI Item                     PL Item
─────────────────────  ───────────────────────     ──────────────────────────
line_no          ───▶  line_no               ───▶  line_no
customer_po_no   ───▶  customer_po_no        ───▶  customer_po_no
cat_no           ───▶  cat_no                ───▶  cat_no
                        ref_no ⬅ 新增(转换)   ───▶  ref_no
description      ───▶  description           ───▶  description
qty1             ───▶  qty1                  ───▶  qty1
unit1            ───▶  unit1                 ───▶  unit1
qty2             ───▶  qty2                  ───▶  qty2
unit2            ───▶  unit2                 ───▶  unit2
unit_price_mult..───▶  unit_price_multiplier
discount_flag    ───▶  discount_flag
line_value       ───▶  line_value
                                                 lot_no ⬅ 新增(库存分配)
                                                 expiry_date ⬅ 新增
                                                 dimension ⬅ 新增(打包算法)
                                                 qty_carton ⬅ 新增
                                                 carton_no_range ⬅ 新增
                                                 volume_cbm ⬅ 新增
                                                 net_weight_kg ⬅ 新增
                                                 gross_weight_kg ⬅ 新增
                                                 dgm_name_cn ⬅ 新增
                                                 dgm_code ⬅ 新增
```

---

## 五、种子数据（Demo 用）

基于 `01 AMD - 清洁版.xlsx` 的真实数据子集：

```sql
-- 客户数据 (取自 Customers sheet)
INSERT INTO customer (customer_code, company_name, customer_tier, payment_terms, currency, incoterms, region_cover, distribution_type, term_of_agreement, assay_expiry_req, special_notes, status)
VALUES ('AMD', 'A. Menarini Diagnostics SRL', 'A', 'Net 60 days from invoice date', 'EUR', 'FCA', 'Italy, Netherlands, Belgium, Spain, France', 'Exclusive distributor', '2025-2027', 'not less than 6 months', 'Special Label and battery label', 1);

-- 联系人数据
INSERT INTO customer_contact (customer_id, contact_name, title, email, phone, notes) VALUES
(1, 'Contact A', 'Global Marketing and Business Development Director', 'contactA@menarini.com', NULL, NULL),
(1, 'Contact B', 'Global Marketing Manager Molecular & Advanced Technologies', 'contactB@menarini.com', NULL, 'Marketing'),
(1, 'Contact C', 'Global Application Lead - Molecular Diagnostics', 'contactC@menarini.com', NULL, 'Marketing'),
(1, 'Contact D', NULL, 'contactD@menarini.com', NULL, 'ORDER');

-- 产品数据 (取自 Products sheet, 选取典型产品)
INSERT INTO product (menarini_code, coyote_code, product_name, description, category, unit_price_kit, unit_price_test, dimension, net_weight_kg, gross_weight_kg) VALUES
('61312', '2004009001', 'FlashDetect Flash10 System', 'COYOTE FLASH10 SYSTEM', 'INSTRUMENT', 100, 14000, '57*45*55cm', 21.00, 33.00),
('59390', '2004009001', 'FlashDetect Flash10 System', 'COYOTE FLASH10 SYSTEM', 'INSTRUMENT', 100, 14000, '57*45*55cm', 21.00, 33.00),
('59391', '2004009202', 'FlashDetect Nano System', 'COYOTE NANO SYSTEM', 'INSTRUMENT', 100, 7000, '57*45*55cm', 18.00, 28.00),
('59393', '6018006402-CE', 'FlashDetect LyocartD SARS-CoV-2 Assay', 'LCD-A SARS', 'ASSAY', 100, 8, '57*26*37cm', 1.40, 2.00),
('59394', '6018006405-CE', 'FlashDetect LyocartD SARS-CoV-2 Assay', 'LCD-B SARS', 'ASSAY', 100, 8, '57*26*37cm', 1.40, 2.00),
('59397', '6018006802-CE', 'FlashDetect LyocartD SARS-CoV-2&Flu A&Flu B Assay', 'MD.LCD-A SARS FLA FLB', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('59398', '6018006805-CE', 'FlashDetect LyocartD SARS-CoV-2&Flu A&Flu B Assay', 'MD.LCD-B SARS FLA FLB', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('59399', '6018007202-CE', 'FlashDetect LyocartE SARS-CoV-2&Flu A&Flu B Assay', 'LCE-A SARS FLUA FLUB', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('59400', '6018007205-CE', 'FlashDetect LyocartE SARS-CoV-2&Flu A&Flu B Assay', 'LCE-B SARS FLUA FLUB', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('59406', '6018012305-CE', 'FlashDetect LyocartD MG&TV&MH Assay', 'MD.LCD-B MG TV MH', 'ASSAY', 100, 10, '57*26*37cm', 1.40, 1.80),
('59823', '6018006901-CE', 'FlashDetect SARS-CoV-2&FluA&FluB EQC (SPEC.A)', 'MD.EQC-A SARS FLA FLB', 'EQC', 100, 20, NULL, 0.10, NULL),
('59824', '6018006902-CE', 'FlashDetect SARS-CoV-2&FluA&FluB EQC (SPEC.B)', 'MD.EQC-B SARS FLA FLB', 'EQC', 100, 20, NULL, 0.10, NULL),
('59827', '6018010301-CE', 'FlashDetect MG&TV&MH EQC (SPEC. A)', 'MD.EQC-A MG TV MH', 'EQC', 100, 20, NULL, 0.10, NULL),
('59828', '6018010302-CE', 'FlashDetect MG&TV&MH EQC (SPEC. B)', 'MD.EQC-B MG TV MH', 'EQC', 100, 20, NULL, 0.10, NULL),
('61200', '6018037401-CE', 'COYOTE CCTS Sputum Liquefaction Kit', 'MD.SPUTUM LIQ KIT', 'CONSUMABLE', 100, 0.00, '57*26*37cm', 1.00, 1.20),
('61201', '6018032401-CE', 'COYOTE CCTS Microbial Sample Collection Kit', 'MD.MICROB COLL KIT', 'CONSUMABLE', 100, 0.00, '51*34*58cm', 1.80, 1.93);

-- 库存数据 (3个批次的示例)
INSERT INTO inventory (product_id, lot_no, expiry_date, quantity_kit, quantity_test) VALUES
(4, '20260108A', '2027-01-07', 50, 1200),
(4, '20251210C', '2026-12-09', 30, 720),
(9, '20260108D', '2027-01-07', 100, 2400),
(9, '20251210B', '2026-12-09', 80, 1920),
(10, '20260108A', '2027-01-07', 40, 960);
```

---

## 六、交付大模型的数据库生成提示词

> 以下提示词可直接复制给其他大模型，用于生成完整的 SQL 脚本。

```
你是一名数据库工程师。请根据以下需求，生成一个海外 CRM Demo 系统的 MySQL 8.0 完整数据库脚本。

## 系统背景
这是一个 IVD（体外诊断）公司的海外 CRM Demo 系统，覆盖 PO→PI→PL 全链路。Demo 仅使用业务员单一角色，审批自动通过，审批者/仓库管理员接口预留后续扩展。
核心业务规则：
1. PO 来源于签约客户发来的 Excel 文件，系统自动导入解析（不是手动创建）
2. PO 中产品用 CAT.# (coyote_code) 标识，PI 中需新增 Ref No. (menarini_code)，通过 product_name 和 CAT.# 匹配转换
3. 打包逻辑：试剂类产品一箱最多 6 盒，剩余 < 4 盒用小箱(35*38*26cm)，>= 4 盒用标准箱(57*26*37cm)；仪器类每台一箱(57*45*55cm)
4. Demo 状态流转（跳过审批）：PO: IMPORTED→APPROVED→PI_GENERATED→COMPLETED; PI: DRAFT→APPROVED→PACKING_GENERATED; PL: DRAFT→APPROVED
5. 需支持 PI 和 PL 的 Excel 导出（分别按 Invoice 和 Packing List 格式）
6. ★ PI 支持"导出→外部修改→重新导入"回路：PI 重新导入时按行号(NO.)匹配更新，变更项需支持差异标记，表结构无需变动（复用 proforma_invoice + proforma_invoice_item）

## 需要生成的表（共10张）
[参照上方 2.1~2.10 每张表的完整 DDL]

## 要求
1. 所有表使用 InnoDB 引擎，utf8mb4 字符集
2. 每个字段添加 COMMENT
3. 主键使用 BIGINT AUTO_INCREMENT
4. 所有外键关联字段添加 INDEX
5. 状态字段使用有意义的字符串（如 'IMPORTED', 'APPROVED', 'COMPLETED'）
6. 金额字段使用 DECIMAL(12,2)，体积使用 DECIMAL(10,4)
7. 包含 created_at, updated_at 时间戳
8. 预留审批相关字段（approved_by, approved_at, reject_reason）但 Demo 中自动填充
9. 输出完整可执行的 CREATE TABLE 语句
10. 附带种子数据 INSERT 语句（1 客户 AMD + 16 产品 + 5 库存批次）
```
