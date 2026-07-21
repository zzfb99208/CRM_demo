-- CRM Demo Database Schema
-- Run: mysql -u root -proot crm_demo < crm_demo_schema.sql

SET SESSION innodb_strict_mode = 0;

-- 0. user (login/auth)
CREATE TABLE IF NOT EXISTS user (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL UNIQUE COMMENT '登录用户名',
    password        VARCHAR(100)    NOT NULL COMMENT '密码(Demo明文)',
    role            VARCHAR(20)     NOT NULL COMMENT 'SALES/APPROVER',
    display_name    VARCHAR(100)    DEFAULT NULL COMMENT '显示名称',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 1. customer (comprehensive — 84 columns from 客户所有字段汇总.xlsx)
CREATE TABLE IF NOT EXISTS customer (
    id                      BIGINT          AUTO_INCREMENT PRIMARY KEY,
    customer_code           VARCHAR(32)     NOT NULL UNIQUE COMMENT '客户编号(系统唯一)',
    customer_source         VARCHAR(50)     DEFAULT NULL COMMENT '客户来源(代理商/终端用户/外贸选等)',
    customer_category       VARCHAR(30)     DEFAULT NULL COMMENT '客户类别(代理商/终端用户/外贸客户)',
    customer_level          VARCHAR(30)     DEFAULT NULL COMMENT '客户等级(外贸专用)',
    customer_owner_name     VARCHAR(50)     DEFAULT NULL COMMENT '客户负责人姓名',
    company_name            VARCHAR(200)    NOT NULL COMMENT '公司全称',
    common_name             VARCHAR(80)     DEFAULT NULL COMMENT '客户简称/日常称呼',
    alias_name              VARCHAR(80)     DEFAULT NULL COMMENT '客户别名/曾用名',
    customer_type           VARCHAR(30)     DEFAULT NULL COMMENT '客户细分类型',
    customer_tier           VARCHAR(10)     DEFAULT NULL COMMENT '客户等级(旧)',
    payment_terms           VARCHAR(100)    DEFAULT 'Net 60 days from invoice date' COMMENT '付款条款',
    currency                VARCHAR(10)     DEFAULT 'EUR' COMMENT '结算货币',
    incoterms               VARCHAR(20)     DEFAULT 'FCA' COMMENT '贸易术语',
    website                 VARCHAR(200)    DEFAULT NULL COMMENT '公司网址',
    assay_expiry_req        VARCHAR(100)    DEFAULT 'not less than 6 months' COMMENT '效期要求',
    region_cover            VARCHAR(500)    DEFAULT NULL COMMENT '覆盖区域',
    distribution_type       VARCHAR(50)     DEFAULT NULL COMMENT '分销类型',
    term_of_agreement       VARCHAR(50)     DEFAULT NULL COMMENT '协议期限',
    tax_no                  VARCHAR(50)     DEFAULT NULL COMMENT '税号',
    tax_identification_no   VARCHAR(30)     DEFAULT NULL COMMENT '统一纳税识别号',
    p_iva                   VARCHAR(50)     DEFAULT NULL COMMENT 'VAT号(意大利)',
    bank_info               TEXT            DEFAULT NULL COMMENT '银行信息',
    ship_to_address         TEXT            DEFAULT NULL COMMENT '收货地址',
    special_notes           VARCHAR(500)    DEFAULT NULL COMMENT '特殊备注',
    status                  TINYINT         DEFAULT 0 COMMENT '0=潜在 1=已签约 2=已终止',
    created_at              DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Expansion: flags & state
    historical_data         TINYINT(1)      DEFAULT NULL COMMENT '历史数据标识',
    cooperated_customer     TINYINT(1)      DEFAULT NULL COMMENT '是否已合作',
    stock_customer          TINYINT(1)      DEFAULT NULL COMMENT '是否库存客户',
    key_mark                VARCHAR(30)     DEFAULT NULL COMMENT '重点客户标记等级',
    success_probability     TINYINT         DEFAULT NULL COMMENT '成交概率(0-100)',
    instrument_stock        VARCHAR(30)     DEFAULT NULL COMMENT '仪器库存状态',
    reagent_stock           VARCHAR(30)     DEFAULT NULL COMMENT '试剂库存状态',
    disable_date            DATE            DEFAULT NULL COMMENT '停用日期',
    business_registration   VARCHAR(30)     DEFAULT NULL COMMENT '工商注册状态(终端用户专用)',
    -- Expansion: location
    province                VARCHAR(30)     DEFAULT NULL COMMENT '省/直辖市',
    city                    VARCHAR(30)     DEFAULT NULL COMMENT '市',
    district                VARCHAR(30)     DEFAULT NULL COMMENT '区/县',
    detailed_address        VARCHAR(200)    DEFAULT NULL COMMENT '详细办公/经营地址',
    detailed_delivery_address VARCHAR(200)  DEFAULT NULL COMMENT '详细收货地址',
    continent               VARCHAR(30)     DEFAULT NULL COMMENT '大洲(外贸专用)',
    country                 VARCHAR(30)     DEFAULT NULL COMMENT '国家(外贸专用)',
    -- Expansion: business dimensions
    coverage_population     DECIMAL(8,2)    DEFAULT NULL COMMENT '覆盖人口(万)',
    business_department     VARCHAR(50)     DEFAULT NULL COMMENT '业务所属部门',
    direct_superior         VARCHAR(50)     DEFAULT NULL COMMENT '直属上级',
    first_level_business    VARCHAR(50)     DEFAULT NULL COMMENT '一级业务分类',
    second_level_business   VARCHAR(50)     DEFAULT NULL COMMENT '二级业务分类',
    third_level_business    VARCHAR(50)     DEFAULT NULL COMMENT '三级业务分类',
    economic_type           VARCHAR(30)     DEFAULT NULL COMMENT '经济类型',
    region_type             VARCHAR(30)     DEFAULT NULL COMMENT '区域类型',
    region_level            VARCHAR(30)     DEFAULT NULL COMMENT '区域级别',
    business_region         VARCHAR(50)     DEFAULT NULL COMMENT '业务大区',
    region_public_pool      VARCHAR(50)     DEFAULT NULL COMMENT '区域公共池',
    -- Expansion: key contacts
    technical_support_leader VARCHAR(50)    DEFAULT NULL COMMENT '技术支持负责人',
    sales_leader            VARCHAR(50)     DEFAULT NULL COMMENT '销售负责人',
    superior_customer       VARCHAR(80)     DEFAULT NULL COMMENT '上级关联客户',
    admitted_product        VARCHAR(100)    DEFAULT NULL COMMENT '入院产品名称',
    activate_date           DATE            DEFAULT NULL COMMENT '入院/激活日期',
    customer_advance        DECIMAL(12,2)   DEFAULT NULL COMMENT '客户预收款(元)',
    do_not_disturb          TINYINT(1)      DEFAULT NULL COMMENT '勿扰开关',
    delivery_contact        VARCHAR(50)     DEFAULT NULL COMMENT '收货联系人',
    delivery_contact_phone  VARCHAR(20)     DEFAULT NULL COMMENT '收货联系人电话',
    terminal_agent_leader   VARCHAR(50)     DEFAULT NULL COMMENT '终端代理商负责人',
    terminal_agent_contact  VARCHAR(20)     DEFAULT NULL COMMENT '终端代理商联系方式',
    department_director     VARCHAR(50)     DEFAULT NULL COMMENT '科室主任',
    department_director_contact VARCHAR(20) DEFAULT NULL COMMENT '科室主任联系方式',
    department_director_wechat VARCHAR(50)  DEFAULT NULL COMMENT '科室主任微信号',
    equipment_chief         VARCHAR(50)     DEFAULT NULL COMMENT '设备科长',
    equipment_chief_contact VARCHAR(20)     DEFAULT NULL COMMENT '设备科长联系方式',
    managing_dean           VARCHAR(50)     DEFAULT NULL COMMENT '分管院长',
    managing_dean_contact   VARCHAR(20)     DEFAULT NULL COMMENT '分管院长联系方式',
    contact_person          VARCHAR(50)     DEFAULT NULL COMMENT '通用联系人(外贸/渠道)',
    -- Expansion: files & metadata
    stamped_qualification_upload VARCHAR(255) DEFAULT NULL COMMENT '盖章资质文件URL',
    qualification_file_upload    VARCHAR(255) DEFAULT NULL COMMENT '资质文件上传URL',
    latest_activity_time    DATETIME        DEFAULT NULL COMMENT '最近活动记录时间',
    administrative_level    VARCHAR(30)     DEFAULT NULL COMMENT '行政等级(终端医院)',
    bed_count               INT             DEFAULT NULL COMMENT '床位数',
    annual_outpatient_volume DECIMAL(8,2)   DEFAULT NULL COMMENT '年门诊量(万/年)',
    is_benchmark            TINYINT(1)      DEFAULT NULL COMMENT '是否标杆(0=否 1=是)',
    sync_return_code        VARCHAR(60)     DEFAULT NULL COMMENT '同步返回码',
    sync_return_log         TEXT            DEFAULT NULL COMMENT '同步日志',
    remark                  TEXT            DEFAULT NULL COMMENT '备注',
    INDEX idx_customer_code (customer_code),
    INDEX idx_customer_source (customer_source),
    INDEX idx_customer_category (customer_category),
    INDEX idx_status (status),
    INDEX idx_province (province),
    INDEX idx_country (country),
    INDEX idx_business_region (business_region)
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC DEFAULT CHARSET=utf8mb4 COMMENT='客户表(84列)';

-- 2. customer_contact (expanded)
CREATE TABLE IF NOT EXISTS customer_contact (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT          NOT NULL COMMENT '关联客户ID',
    contact_name    VARCHAR(100)    DEFAULT NULL COMMENT '联系人姓名',
    title           VARCHAR(200)    DEFAULT NULL COMMENT '职位',
    email           VARCHAR(200)    DEFAULT NULL COMMENT '邮箱',
    phone           VARCHAR(50)     DEFAULT NULL COMMENT '电话',
    wechat          VARCHAR(100)    DEFAULT NULL COMMENT '微信号',
    notes           VARCHAR(200)    DEFAULT NULL COMMENT '备注',
    is_primary      TINYINT(1)      DEFAULT 0 COMMENT '是否首要联系人',
    department      VARCHAR(100)    DEFAULT NULL COMMENT '所属部门',
    gender          VARCHAR(10)     DEFAULT NULL COMMENT '性别',
    birthday        DATE            DEFAULT NULL COMMENT '生日',
    hobby           VARCHAR(200)    DEFAULT NULL COMMENT '兴趣爱好',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户联系人表';

-- 3. product (comprehensive — 58 columns from 产品所有字段汇总.xlsx)
CREATE TABLE IF NOT EXISTS product (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    menarini_code       VARCHAR(20)     DEFAULT NULL COMMENT 'Menarini内部编码(Ref No.)',
    coyote_code         VARCHAR(30)     NOT NULL UNIQUE COMMENT 'Coyote目录号(CAT.#)',
    prod_code           VARCHAR(50)     DEFAULT NULL COMMENT '产品编码(内部生产编码)',
    product_name        VARCHAR(300)    NOT NULL COMMENT '产品名称',
    description         VARCHAR(500)    DEFAULT NULL COMMENT '产品描述',
    category            VARCHAR(30)     DEFAULT NULL COMMENT '分类: INSTRUMENT/ASSAY/EQC/CONSUMABLE/ACCESSORY',
    category_l1         VARCHAR(30)     DEFAULT NULL COMMENT '一级分类(仪器/试剂/耗材/质控品/配件/服务/套装)',
    category_l2         VARCHAR(50)     DEFAULT NULL COMMENT '二级分类(仪器型号/试剂系列)',
    unit_price_kit      DECIMAL(10,2)   DEFAULT NULL COMMENT '每Kit价格(EUR)',
    unit_price_test     DECIMAL(10,2)   DEFAULT NULL COMMENT '每Test价格(EUR)',
    currency            VARCHAR(10)     DEFAULT 'EUR' COMMENT '计价货币(EUR/CNY/USD)',
    dimension           VARCHAR(50)     DEFAULT NULL COMMENT '外箱尺寸(cm)',
    net_weight_kg       DECIMAL(8,2)    DEFAULT NULL COMMENT '单件净重(kg)',
    gross_weight_kg     DECIMAL(8,2)    DEFAULT NULL COMMENT '单件毛重(kg)',
    dgm_name_cn         VARCHAR(200)    DEFAULT NULL COMMENT 'DGM中文品名',
    dgm_code            VARCHAR(50)     DEFAULT NULL COMMENT 'DGM编码',
    is_active           TINYINT         DEFAULT 1 COMMENT '是否启用(旧)',
    notes               VARCHAR(500)    DEFAULT NULL COMMENT '备注(旧)',
    created_at          DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Expansion: classification
    prod_type           VARCHAR(30)     DEFAULT NULL COMMENT '产品形态(实体产品/服务产品/虚拟产品)',
    product_type        VARCHAR(30)     DEFAULT NULL COMMENT '产品类型(产品/服务/套装)',
    biz_type            VARCHAR(30)     DEFAULT NULL COMMENT '业务类型(试剂/套餐/仪器/服务)',
    unit_of_measure     VARCHAR(50)     DEFAULT NULL COMMENT '计量单位',
    spec_test_per_unit  VARCHAR(100)    DEFAULT NULL COMMENT '台/盒/人份规格描述',
    tests_per_unit      INT             DEFAULT NULL COMMENT '每包装人份数',
    min_order_qty       INT             DEFAULT NULL COMMENT '最小采购量',
    model_spec          VARCHAR(200)    DEFAULT NULL COMMENT '型号规格',
    -- Expansion: pricing
    market_price_cny    DECIMAL(12,2)   DEFAULT NULL COMMENT '市场价(人民币)',
    market_price_usd    DECIMAL(12,2)   DEFAULT NULL COMMENT '市场价(美元)',
    standard_price_local DECIMAL(12,2)  DEFAULT NULL COMMENT '基准本地价格',
    floor_price_usd     DECIMAL(12,2)   DEFAULT NULL COMMENT '底价(美元, 套装专用)',
    tax_rate            DECIMAL(5,2)    DEFAULT NULL COMMENT '税率(%)',
    exchange_rate       DECIMAL(10,4)   DEFAULT NULL COMMENT '汇率',
    price_desc          VARCHAR(500)    DEFAULT NULL COMMENT '价格说明',
    -- Expansion: bundle & config
    is_bundle           TINYINT(1)      DEFAULT NULL COMMENT '是否组合产品(套装)',
    can_sell_alone      TINYINT(1)      DEFAULT NULL COMMENT '是否可单独销售',
    has_config          VARCHAR(10)     DEFAULT NULL COMMENT '配置权限(允许/禁止/强制)',
    config_timing       VARCHAR(20)     DEFAULT NULL COMMENT '配置时机(添加时/合同签订时/发货前)',
    need_retrieve       VARCHAR(3)      DEFAULT NULL COMMENT '需要回收(是/否)',
    -- Expansion: asset & deploy
    can_be_asset        TINYINT(1)      DEFAULT NULL COMMENT '是否可转为固定资产',
    can_deploy          VARCHAR(3)      DEFAULT NULL COMMENT '是否可投放/借出(是/否)',
    can_trial           VARCHAR(3)      DEFAULT NULL COMMENT '是否可试用(是/否)',
    is_consumable       VARCHAR(3)      DEFAULT NULL COMMENT '是否耗材(是/否)',
    -- Expansion: lifecycle
    effective_date      DATE            DEFAULT NULL COMMENT '生效日期',
    expiration_date     DATE            DEFAULT NULL COMMENT '失效日期',
    prod_image          VARCHAR(255)    DEFAULT NULL COMMENT '产品图片URL',
    prod_desc           TEXT            DEFAULT NULL COMMENT '产品描述(富文本)',
    prod_catalog        VARCHAR(200)    DEFAULT NULL COMMENT '产品目录分类',
    prod_owner          BIGINT          DEFAULT NULL COMMENT '产品负责人(用户ID)',
    dept_owner          BIGINT          DEFAULT NULL COMMENT '产品所属部门(组织ID)',
    -- Expansion: material & import
    prod_material_category VARCHAR(30)  DEFAULT NULL COMMENT '产品物料类别',
    prod_material_class VARCHAR(30)     DEFAULT NULL COMMENT '产品物料分类',
    sellable_company1   VARCHAR(30)     DEFAULT NULL COMMENT '可销售公司1',
    sellable_company2   VARCHAR(30)     DEFAULT NULL COMMENT '可销售公司2',
    import_code         VARCHAR(100)    DEFAULT NULL COMMENT '进口海关编码',
    market_level3_tags  VARCHAR(500)    DEFAULT NULL COMMENT '三级市场标签(逗号分隔)',
    status              VARCHAR(20)     DEFAULT '启用' COMMENT '产品状态(启用/禁用)',
    INDEX idx_coyote_code (coyote_code),
    INDEX idx_menarini_code (menarini_code),
    INDEX idx_prod_code (prod_code),
    INDEX idx_category (category),
    INDEX idx_category_l1 (category_l1),
    INDEX idx_category_l2 (category_l2),
    INDEX idx_product_name (product_name(100)),
    INDEX idx_biz_type (biz_type),
    INDEX idx_product_type (product_type),
    INDEX idx_status (status)
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC DEFAULT CHARSET=utf8mb4 COMMENT='商品表(58列)';

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

-- ============================================================
-- 10. price_list — General Price List Header (from 价格表字段汇总)
-- ============================================================
CREATE TABLE IF NOT EXISTS price_list (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    price_list_no       VARCHAR(50)     NOT NULL UNIQUE COMMENT '价格表编码(系统生成)',
    price_list_name     VARCHAR(255)    NOT NULL COMMENT '价格表名称',
    biz_level1_tag      VARCHAR(50)     DEFAULT NULL COMMENT '一级业务标签(临床/非临床/畜牧水产/外贸)',
    biz_level2_tag      VARCHAR(50)     DEFAULT NULL COMMENT '二级业务标签(医院/疾控/科研院所/企业/海外)',
    biz_level3_tag      VARCHAR(50)     DEFAULT NULL COMMENT '三级业务标签(综合医院/妇幼/儿童/专科/基层医疗/公共卫生/血液系统)',
    customer_type       VARCHAR(30)     DEFAULT NULL COMMENT '适用客户类型(新客户/老客户)',
    sale_province       VARCHAR(50)     DEFAULT NULL COMMENT '销售省份',
    region              VARCHAR(50)     DEFAULT NULL COMMENT '区域',
    is_standard         TINYINT(1)      DEFAULT 0 COMMENT '是否标准价格表(0=否 1=是)',
    status              VARCHAR(20)     DEFAULT '启用' COMMENT '状态(启用/禁用)',
    remark              VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    created_by          VARCHAR(50)     DEFAULT NULL COMMENT '创建人',
    updated_by          VARCHAR(50)     DEFAULT NULL COMMENT '修改人',
    created_at          DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_biz_level1 (biz_level1_tag),
    INDEX idx_biz_level2 (biz_level2_tag),
    INDEX idx_customer_type (customer_type),
    INDEX idx_region (region),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格表主表';

-- ============================================================
-- 11. price_list_item — Price List Detail Line
-- ============================================================
CREATE TABLE IF NOT EXISTS price_list_item (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    price_list_id       BIGINT          NOT NULL COMMENT '价格表ID',
    product_id          BIGINT          NOT NULL COMMENT '产品ID',
    prod_code           VARCHAR(100)    DEFAULT NULL COMMENT '产品编码(冗余, 方便查询)',
    prod_name           VARCHAR(200)    DEFAULT NULL COMMENT '产品名称(冗余, 联动带入)',
    unit_of_measure     VARCHAR(50)     DEFAULT NULL COMMENT '计量单位(冗余)',
    list_price          DECIMAL(12,2)   NOT NULL COMMENT '价格表价格',
    market_price        DECIMAL(12,2)   DEFAULT NULL COMMENT '市场价(参考)',
    price_desc          VARCHAR(500)    DEFAULT NULL COMMENT '价格说明',
    status              VARCHAR(20)     DEFAULT '启用' COMMENT '行状态(启用/禁用)',
    UNIQUE KEY uk_plist_product (price_list_id, product_id),
    INDEX idx_price_list_id (price_list_id),
    INDEX idx_product_id (product_id),
    INDEX idx_prod_code (prod_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格表明细行';
