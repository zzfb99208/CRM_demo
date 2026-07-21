-- ============================================================
-- CRM Demo v1 → v2 Migration Script
-- ============================================================
-- 用途: 将 v1 数据库升级到 v2
-- 适用: 已有 v1 数据库 (旧版 customer/product/customer_contact)，需要升级
-- 注意: 如果是从零搭建，直接运行 schema.sql 即可，不需要此脚本
--
-- 本脚本包含:
--   A. 已有表的列扩展 (ALTER TABLE ... ADD COLUMN)
--   B. v2 新增表的创建 (CREATE TABLE IF NOT EXISTS)
--
-- 运行方式:
--   mysql -u root -proot crm_demo < migration_v2_comprehensive.sql
--
-- 权威 DDL 参考: schema.sql (包含所有表的完整定义)
-- ============================================================

SET NAMES utf8mb4;

-- ============================================================
-- A1. customer 表扩展 — 从 ~20 列扩展到 84 列
--     来源: 客户所有字段汇总.xlsx
--     详细列定义请参见 schema.sql
-- ============================================================
ALTER TABLE customer
    ADD COLUMN customer_source VARCHAR(50) DEFAULT NULL COMMENT '客户来源(代理商/终端用户/外贸选等)' AFTER customer_code,
    ADD COLUMN customer_category VARCHAR(30) DEFAULT NULL COMMENT '客户类别(代理商/终端用户/外贸客户)' AFTER customer_source,
    ADD COLUMN customer_level VARCHAR(30) DEFAULT NULL COMMENT '客户等级(外贸专用)' AFTER customer_category,
    ADD COLUMN customer_owner_name VARCHAR(50) DEFAULT NULL COMMENT '客户负责人姓名' AFTER customer_level,
    ADD COLUMN common_name VARCHAR(80) DEFAULT NULL COMMENT '客户简称/日常称呼' AFTER company_name,
    ADD COLUMN alias_name VARCHAR(80) DEFAULT NULL COMMENT '客户别名/曾用名' AFTER common_name,
    ADD COLUMN customer_type VARCHAR(30) DEFAULT NULL COMMENT '客户细分类型' AFTER alias_name,
    ADD COLUMN tax_identification_no VARCHAR(30) DEFAULT NULL COMMENT '统一纳税识别号' AFTER tax_no,
    ADD COLUMN historical_data TINYINT(1) DEFAULT NULL COMMENT '历史数据标识',
    ADD COLUMN cooperated_customer TINYINT(1) DEFAULT NULL COMMENT '是否已合作',
    ADD COLUMN stock_customer TINYINT(1) DEFAULT NULL COMMENT '是否库存客户',
    ADD COLUMN key_mark VARCHAR(30) DEFAULT NULL COMMENT '重点客户标记等级',
    ADD COLUMN success_probability TINYINT DEFAULT NULL COMMENT '成交概率(0-100)',
    ADD COLUMN instrument_stock VARCHAR(30) DEFAULT NULL COMMENT '仪器库存状态',
    ADD COLUMN reagent_stock VARCHAR(30) DEFAULT NULL COMMENT '试剂库存状态',
    ADD COLUMN disable_date DATE DEFAULT NULL COMMENT '停用日期',
    ADD COLUMN business_registration VARCHAR(30) DEFAULT NULL COMMENT '工商注册状态(终端用户专用)',
    ADD COLUMN province VARCHAR(30) DEFAULT NULL COMMENT '省/直辖市',
    ADD COLUMN city VARCHAR(30) DEFAULT NULL COMMENT '市',
    ADD COLUMN district VARCHAR(30) DEFAULT NULL COMMENT '区/县',
    ADD COLUMN detailed_address VARCHAR(200) DEFAULT NULL COMMENT '详细办公/经营地址',
    ADD COLUMN detailed_delivery_address VARCHAR(200) DEFAULT NULL COMMENT '详细收货地址',
    ADD COLUMN continent VARCHAR(30) DEFAULT NULL COMMENT '大洲(外贸专用)',
    ADD COLUMN country VARCHAR(30) DEFAULT NULL COMMENT '国家(外贸专用)',
    ADD COLUMN coverage_population DECIMAL(8,2) DEFAULT NULL COMMENT '覆盖人口(万)',
    ADD COLUMN business_department VARCHAR(50) DEFAULT NULL COMMENT '业务所属部门',
    ADD COLUMN direct_superior VARCHAR(50) DEFAULT NULL COMMENT '直属上级',
    ADD COLUMN first_level_business VARCHAR(50) DEFAULT NULL COMMENT '一级业务分类',
    ADD COLUMN second_level_business VARCHAR(50) DEFAULT NULL COMMENT '二级业务分类',
    ADD COLUMN third_level_business VARCHAR(50) DEFAULT NULL COMMENT '三级业务分类',
    ADD COLUMN economic_type VARCHAR(30) DEFAULT NULL COMMENT '经济类型',
    ADD COLUMN region_type VARCHAR(30) DEFAULT NULL COMMENT '区域类型',
    ADD COLUMN region_level VARCHAR(30) DEFAULT NULL COMMENT '区域级别',
    ADD COLUMN business_region VARCHAR(50) DEFAULT NULL COMMENT '业务大区',
    ADD COLUMN region_public_pool VARCHAR(50) DEFAULT NULL COMMENT '区域公共池',
    ADD COLUMN technical_support_leader VARCHAR(50) DEFAULT NULL COMMENT '技术支持负责人',
    ADD COLUMN sales_leader VARCHAR(50) DEFAULT NULL COMMENT '销售负责人',
    ADD COLUMN superior_customer VARCHAR(80) DEFAULT NULL COMMENT '上级关联客户',
    ADD COLUMN admitted_product VARCHAR(100) DEFAULT NULL COMMENT '入院产品名称',
    ADD COLUMN activate_date DATE DEFAULT NULL COMMENT '入院/激活日期',
    ADD COLUMN customer_advance DECIMAL(12,2) DEFAULT NULL COMMENT '客户预收款(元)',
    ADD COLUMN do_not_disturb TINYINT(1) DEFAULT NULL COMMENT '勿扰开关',
    ADD COLUMN delivery_contact VARCHAR(50) DEFAULT NULL COMMENT '收货联系人',
    ADD COLUMN delivery_contact_phone VARCHAR(20) DEFAULT NULL COMMENT '收货联系人电话',
    ADD COLUMN terminal_agent_leader VARCHAR(50) DEFAULT NULL COMMENT '终端代理商负责人',
    ADD COLUMN terminal_agent_contact VARCHAR(20) DEFAULT NULL COMMENT '终端代理商联系方式',
    ADD COLUMN department_director VARCHAR(50) DEFAULT NULL COMMENT '科室主任',
    ADD COLUMN department_director_contact VARCHAR(20) DEFAULT NULL COMMENT '科室主任联系方式',
    ADD COLUMN department_director_wechat VARCHAR(50) DEFAULT NULL COMMENT '科室主任微信号',
    ADD COLUMN equipment_chief VARCHAR(50) DEFAULT NULL COMMENT '设备科长',
    ADD COLUMN equipment_chief_contact VARCHAR(20) DEFAULT NULL COMMENT '设备科长联系方式',
    ADD COLUMN managing_dean VARCHAR(50) DEFAULT NULL COMMENT '分管院长',
    ADD COLUMN managing_dean_contact VARCHAR(20) DEFAULT NULL COMMENT '分管院长联系方式',
    ADD COLUMN contact_person VARCHAR(50) DEFAULT NULL COMMENT '通用联系人(外贸/渠道)',
    ADD COLUMN stamped_qualification_upload VARCHAR(255) DEFAULT NULL COMMENT '盖章资质文件URL',
    ADD COLUMN qualification_file_upload VARCHAR(255) DEFAULT NULL COMMENT '资质文件上传URL',
    ADD COLUMN latest_activity_time DATETIME DEFAULT NULL COMMENT '最近活动记录时间',
    ADD COLUMN administrative_level VARCHAR(30) DEFAULT NULL COMMENT '行政等级(终端医院)',
    ADD COLUMN bed_count INT DEFAULT NULL COMMENT '床位数',
    ADD COLUMN annual_outpatient_volume DECIMAL(8,2) DEFAULT NULL COMMENT '年门诊量(万/年)',
    ADD COLUMN is_benchmark TINYINT(1) DEFAULT NULL COMMENT '是否标杆(0=否 1=是)',
    ADD COLUMN sync_return_code VARCHAR(60) DEFAULT NULL COMMENT '同步返回码',
    ADD COLUMN sync_return_log TEXT DEFAULT NULL COMMENT '同步日志',
    ADD COLUMN remark TEXT DEFAULT NULL COMMENT '备注';

ALTER TABLE customer ADD INDEX idx_customer_source (customer_source);
ALTER TABLE customer ADD INDEX idx_customer_category (customer_category);
ALTER TABLE customer ADD INDEX idx_province (province);
ALTER TABLE customer ADD INDEX idx_country (country);
ALTER TABLE customer ADD INDEX idx_business_region (business_region);

-- ============================================================
-- A2. product 表扩展 — 从 ~17 列扩展到 58 列
--     来源: 产品所有字段汇总.xlsx
-- ============================================================
ALTER TABLE product
    ADD COLUMN prod_code VARCHAR(50) DEFAULT NULL COMMENT '产品编码(内部生产编码)' AFTER coyote_code,
    ADD COLUMN category_l1 VARCHAR(30) DEFAULT NULL COMMENT '一级分类(仪器/试剂/耗材/服务/套装)' AFTER category,
    ADD COLUMN category_l2 VARCHAR(50) DEFAULT NULL COMMENT '二级分类(仪器型号/试剂系列)',
    ADD COLUMN prod_type VARCHAR(30) DEFAULT NULL COMMENT '产品形态(实体产品/服务产品/虚拟产品)',
    ADD COLUMN product_type VARCHAR(30) DEFAULT NULL COMMENT '产品类型(产品/服务/套装)',
    ADD COLUMN biz_type VARCHAR(30) DEFAULT NULL COMMENT '业务类型(试剂/套餐/仪器/服务)',
    ADD COLUMN unit_of_measure VARCHAR(50) DEFAULT NULL COMMENT '计量单位',
    ADD COLUMN currency VARCHAR(10) DEFAULT 'EUR' COMMENT '计价货币(EUR/CNY/USD)',
    ADD COLUMN spec_test_per_unit VARCHAR(100) DEFAULT NULL COMMENT '台/盒/人份规格描述',
    ADD COLUMN tests_per_unit INT DEFAULT NULL COMMENT '每包装人份数',
    ADD COLUMN min_order_qty INT DEFAULT NULL COMMENT '最小采购量',
    ADD COLUMN model_spec VARCHAR(200) DEFAULT NULL COMMENT '型号规格',
    ADD COLUMN market_price_cny DECIMAL(12,2) DEFAULT NULL COMMENT '市场价(人民币)',
    ADD COLUMN market_price_usd DECIMAL(12,2) DEFAULT NULL COMMENT '市场价(美元)',
    ADD COLUMN standard_price_local DECIMAL(12,2) DEFAULT NULL COMMENT '基准本地价格',
    ADD COLUMN floor_price_usd DECIMAL(12,2) DEFAULT NULL COMMENT '底价(美元, 套装专用)',
    ADD COLUMN tax_rate DECIMAL(5,2) DEFAULT NULL COMMENT '税率(%)',
    ADD COLUMN exchange_rate DECIMAL(10,4) DEFAULT NULL COMMENT '汇率',
    ADD COLUMN price_desc VARCHAR(500) DEFAULT NULL COMMENT '价格说明',
    ADD COLUMN is_bundle TINYINT(1) DEFAULT NULL COMMENT '是否组合产品(套装)',
    ADD COLUMN can_sell_alone TINYINT(1) DEFAULT NULL COMMENT '是否可单独销售',
    ADD COLUMN has_config VARCHAR(10) DEFAULT NULL COMMENT '配置权限(允许/禁止/强制)',
    ADD COLUMN config_timing VARCHAR(20) DEFAULT NULL COMMENT '配置时机(添加时/合同签订时/发货前)',
    ADD COLUMN need_retrieve VARCHAR(3) DEFAULT NULL COMMENT '需要回收(是/否)',
    ADD COLUMN can_be_asset TINYINT(1) DEFAULT NULL COMMENT '是否可转为固定资产',
    ADD COLUMN can_deploy VARCHAR(3) DEFAULT NULL COMMENT '是否可投放/借出(是/否)',
    ADD COLUMN can_trial VARCHAR(3) DEFAULT NULL COMMENT '是否可试用(是/否)',
    ADD COLUMN is_consumable VARCHAR(3) DEFAULT NULL COMMENT '是否耗材(是/否)',
    ADD COLUMN effective_date DATE DEFAULT NULL COMMENT '生效日期',
    ADD COLUMN expiration_date DATE DEFAULT NULL COMMENT '失效日期',
    ADD COLUMN prod_image VARCHAR(255) DEFAULT NULL COMMENT '产品图片URL',
    ADD COLUMN prod_desc TEXT DEFAULT NULL COMMENT '产品描述(富文本)',
    ADD COLUMN prod_catalog VARCHAR(200) DEFAULT NULL COMMENT '产品目录分类',
    ADD COLUMN prod_owner BIGINT DEFAULT NULL COMMENT '产品负责人(用户ID)',
    ADD COLUMN dept_owner BIGINT DEFAULT NULL COMMENT '产品所属部门(组织ID)',
    ADD COLUMN prod_material_category VARCHAR(30) DEFAULT NULL COMMENT '产品物料类别',
    ADD COLUMN prod_material_class VARCHAR(30) DEFAULT NULL COMMENT '产品物料分类',
    ADD COLUMN sellable_company1 VARCHAR(30) DEFAULT NULL COMMENT '可销售公司1',
    ADD COLUMN sellable_company2 VARCHAR(30) DEFAULT NULL COMMENT '可销售公司2',
    ADD COLUMN import_code VARCHAR(100) DEFAULT NULL COMMENT '进口海关编码',
    ADD COLUMN market_level3_tags VARCHAR(500) DEFAULT NULL COMMENT '三级市场标签(逗号分隔)',
    ADD COLUMN status VARCHAR(20) DEFAULT '启用' COMMENT '产品状态(启用/禁用)';

ALTER TABLE product ADD INDEX idx_prod_code (prod_code);
ALTER TABLE product ADD INDEX idx_category_l1 (category_l1);
ALTER TABLE product ADD INDEX idx_category_l2 (category_l2);
ALTER TABLE product ADD INDEX idx_biz_type (biz_type);
ALTER TABLE product ADD INDEX idx_product_type (product_type);
ALTER TABLE product ADD INDEX idx_status (status);

-- ============================================================
-- A3. customer_contact 表扩展 — 从 7 列扩展到 15 列
-- ============================================================
ALTER TABLE customer_contact
    ADD COLUMN wechat VARCHAR(100) DEFAULT NULL COMMENT '微信号' AFTER phone,
    ADD COLUMN is_primary TINYINT(1) DEFAULT 0 COMMENT '是否首要联系人' AFTER notes,
    ADD COLUMN department VARCHAR(100) DEFAULT NULL COMMENT '所属部门' AFTER is_primary,
    ADD COLUMN gender VARCHAR(10) DEFAULT NULL COMMENT '性别' AFTER department,
    ADD COLUMN birthday DATE DEFAULT NULL COMMENT '生日' AFTER gender,
    ADD COLUMN hobby VARCHAR(200) DEFAULT NULL COMMENT '兴趣爱好' AFTER birthday,
    ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP AFTER hobby,
    ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- ============================================================
-- B. v2 新增表
--    权威 DDL 定义: schema.sql (价格表 section)
--    此处用 CREATE TABLE IF NOT EXISTS 确保迁移自包含
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

SELECT 'Migration v1 → v2 completed successfully.' AS result;
