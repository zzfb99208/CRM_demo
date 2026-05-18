-- CRM Demo Database Schema
-- Run: mysql -u root -proot crm_demo < crm_demo_schema.sql

-- 1. customer_contact
CREATE TABLE IF NOT EXISTS customer_contact (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT          NOT NULL COMMENT '关联客户ID',
    contact_name    VARCHAR(100)    DEFAULT NULL COMMENT '联系人姓名',
    title           VARCHAR(200)    DEFAULT NULL COMMENT '职位',
    email           VARCHAR(200)    DEFAULT NULL COMMENT '邮箱',
    phone           VARCHAR(50)     DEFAULT NULL COMMENT '电话',
    notes           VARCHAR(200)    DEFAULT NULL COMMENT '备注',
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户联系人表';

-- 2. product
CREATE TABLE IF NOT EXISTS product (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    menarini_code   VARCHAR(20)     DEFAULT NULL COMMENT 'Menarini内部编码(Ref No.)',
    coyote_code     VARCHAR(30)     NOT NULL UNIQUE COMMENT 'Coyote目录号(CAT.#)',
    product_name    VARCHAR(300)    NOT NULL COMMENT '产品名称',
    description     VARCHAR(500)    DEFAULT NULL COMMENT '产品描述',
    category        VARCHAR(30)     DEFAULT NULL COMMENT '分类: INSTRUMENT/ASSAY/EQC/CONSUMABLE',
    unit_price_kit  DECIMAL(10,2)   DEFAULT NULL COMMENT '每Kit价格(EUR)',
    unit_price_test DECIMAL(10,2)   DEFAULT NULL COMMENT '每Test价格(EUR)',
    dimension       VARCHAR(50)     DEFAULT NULL COMMENT '外箱尺寸',
    net_weight_kg   DECIMAL(8,2)    DEFAULT NULL COMMENT '单件净重(kg)',
    gross_weight_kg DECIMAL(8,2)    DEFAULT NULL COMMENT '单件毛重(kg)',
    dgm_name_cn     VARCHAR(200)    DEFAULT NULL COMMENT 'DGM中文品名',
    dgm_code        VARCHAR(50)     DEFAULT NULL COMMENT 'DGM编码',
    is_active       TINYINT         DEFAULT 1 COMMENT '是否启用',
    notes           VARCHAR(500)    DEFAULT NULL,
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_coyote_code (coyote_code),
    INDEX idx_menarini_code (menarini_code),
    INDEX idx_category (category),
    INDEX idx_product_name (product_name(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 3. inventory
CREATE TABLE IF NOT EXISTS inventory (
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

-- 4. purchase_order
CREATE TABLE IF NOT EXISTS purchase_order (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    contract_no     VARCHAR(50)     NOT NULL UNIQUE COMMENT '合同号',
    customer_id     BIGINT          NOT NULL COMMENT '客户ID',
    cyt_order_no    VARCHAR(50)     DEFAULT NULL COMMENT '内部订单号',
    delivery_terms  VARCHAR(50)     DEFAULT 'FCA' COMMENT '交货条款',
    payment_terms   VARCHAR(100)    DEFAULT 'Net 60 days from invoice date' COMMENT '付款条款',
    transport_method VARCHAR(50)    DEFAULT 'Air' COMMENT '运输方式',
    country_of_origin VARCHAR(50)   DEFAULT 'China' COMMENT '原产国',
    po_reference    VARCHAR(500)    DEFAULT NULL COMMENT '客户PO编号列表',
    status          VARCHAR(20)     DEFAULT 'IMPORTED' COMMENT '状态',
    created_by      BIGINT          DEFAULT NULL COMMENT '创建人ID',
    approved_by     BIGINT          DEFAULT NULL COMMENT '审批人ID',
    approved_at     DATETIME        DEFAULT NULL COMMENT '审批时间',
    reject_reason   VARCHAR(500)    DEFAULT NULL COMMENT '驳回原因',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_contract_no (contract_no),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单主表(PO)';

-- 5. purchase_order_item
CREATE TABLE IF NOT EXISTS purchase_order_item (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    po_id               BIGINT          NOT NULL COMMENT 'PO主表ID',
    line_no             INT             NOT NULL COMMENT '行号',
    customer_po_no      VARCHAR(50)     DEFAULT NULL COMMENT '客户PO分单号(Order #)',
    product_id          BIGINT          DEFAULT NULL COMMENT '商品ID',
    cat_no              VARCHAR(30)     DEFAULT NULL COMMENT 'CAT.#',
    description         VARCHAR(500)    DEFAULT NULL COMMENT '产品描述',
    qty1                INT             DEFAULT 0 COMMENT '数量(包装单位1)',
    unit1               VARCHAR(20)     DEFAULT 'kit' COMMENT '单位1',
    qty2                INT             DEFAULT 0 COMMENT '数量(包装单位2)',
    unit2               VARCHAR(20)     DEFAULT 'test' COMMENT '单位2',
    unit_price_multiplier DECIMAL(5,2)  DEFAULT 1.00 COMMENT '单价乘数',
    discount_flag       TINYINT         DEFAULT 0 COMMENT '折扣标识',
    line_value          DECIMAL(12,2)   DEFAULT 0.00 COMMENT '行金额(EUR)',
    INDEX idx_po_id (po_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PO行项目表';

-- 6. proforma_invoice
CREATE TABLE IF NOT EXISTS proforma_invoice (
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
    status              VARCHAR(20)     DEFAULT 'DRAFT' COMMENT '状态',
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

-- 7. proforma_invoice_item
CREATE TABLE IF NOT EXISTS proforma_invoice_item (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    pi_id               BIGINT          NOT NULL COMMENT 'PI主表ID',
    line_no             INT             NOT NULL COMMENT '行号',
    customer_po_no      VARCHAR(50)     DEFAULT NULL COMMENT 'PO分单号',
    product_id          BIGINT          DEFAULT NULL COMMENT '商品ID',
    cat_no              VARCHAR(30)     DEFAULT NULL COMMENT 'CAT.#',
    ref_no              VARCHAR(20)     DEFAULT NULL COMMENT 'Ref No. (menarini_code)',
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

-- 8. packing_list
CREATE TABLE IF NOT EXISTS packing_list (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    pi_id               BIGINT          NOT NULL COMMENT '关联PI ID',
    contract_no         VARCHAR(50)     DEFAULT NULL COMMENT '合同号',
    customer_id         BIGINT          NOT NULL COMMENT '客户ID',
    ship_to             TEXT            DEFAULT NULL COMMENT '收货地址',
    transport_method    VARCHAR(50)     DEFAULT 'Air' COMMENT '运输方式',
    transport_route     VARCHAR(200)    DEFAULT NULL COMMENT '运输路线',
    country_of_origin   VARCHAR(50)     DEFAULT 'China' COMMENT '原产国',
    po_reference        VARCHAR(500)    DEFAULT NULL COMMENT 'PO列表',
    total_volume_cbm    DECIMAL(10,4)   DEFAULT 0.0000 COMMENT '总体积(CBM)',
    total_net_weight_kg DECIMAL(10,2)   DEFAULT 0.00 COMMENT '总净重(kg)',
    total_gross_weight_kg DECIMAL(10,2) DEFAULT 0.00 COMMENT '总毛重(kg)',
    total_cartons       INT             DEFAULT 0 COMMENT '总箱数',
    status              VARCHAR(20)     DEFAULT 'DRAFT' COMMENT '状态',
    created_by          BIGINT          DEFAULT NULL COMMENT '创建人ID',
    approved_by         BIGINT          DEFAULT NULL COMMENT '审批人ID',
    approved_at         DATETIME        DEFAULT NULL COMMENT '审批时间',
    reject_reason       VARCHAR(500)    DEFAULT NULL COMMENT '驳回原因',
    created_at          DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pi_id (pi_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装箱单主表(PL)';

-- 9. packing_list_item
CREATE TABLE IF NOT EXISTS packing_list_item (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    pl_id               BIGINT          NOT NULL COMMENT '装箱单主表ID',
    line_no             INT             NOT NULL COMMENT '行号',
    customer_po_no      VARCHAR(50)     DEFAULT NULL COMMENT 'PO分单号',
    product_id          BIGINT          DEFAULT NULL COMMENT '商品ID',
    cat_no              VARCHAR(30)     DEFAULT NULL COMMENT 'CAT.#',
    ref_no              VARCHAR(20)     DEFAULT NULL COMMENT 'Ref No.',
    description         VARCHAR(500)    DEFAULT NULL COMMENT '产品描述',
    qty1                INT             DEFAULT 0 COMMENT '数量(包装单位1)',
    unit1               VARCHAR(20)     DEFAULT 'kit' COMMENT '单位1',
    qty2                INT             DEFAULT 0 COMMENT '数量(包装单位2)',
    unit2               VARCHAR(20)     DEFAULT 'test' COMMENT '单位2',
    lot_no              VARCHAR(50)     DEFAULT NULL COMMENT '批号',
    expiry_date         DATE            DEFAULT NULL COMMENT '失效日期',
    dimension           VARCHAR(50)     DEFAULT NULL COMMENT '外箱尺寸',
    qty_carton          INT             DEFAULT 0 COMMENT '该行箱数',
    carton_no_range     VARCHAR(50)     DEFAULT NULL COMMENT '箱号范围',
    volume_cbm          DECIMAL(10,4)   DEFAULT 0.0000 COMMENT '体积(CBM)',
    net_weight_kg       DECIMAL(8,2)    DEFAULT 0.00 COMMENT '净重(kg)',
    gross_weight_kg     DECIMAL(8,2)    DEFAULT 0.00 COMMENT '毛重(kg)',
    dgm_name_cn         VARCHAR(200)    DEFAULT NULL COMMENT 'DGM中文品名',
    dgm_code            VARCHAR(50)     DEFAULT NULL COMMENT 'DGM编码',
    INDEX idx_pl_id (pl_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装箱单行项目表(PL)';
