-- ============================================================
-- CRM Demo v2 — Updated Seed Data for Expanded Schema
-- Run: mysql -u root -proot crm_demo < seed_v2_comprehensive.sql
-- ============================================================

-- ============================================================
-- Update existing AMD customer with comprehensive CRM fields
-- ============================================================
UPDATE customer SET
    -- Classification
    customer_source = '外贸选',
    customer_category = '外贸客户',
    customer_level = 'A',
    customer_owner_name = 'Sales Manager',
    common_name = 'Menarini',
    alias_name = 'AMD',
    customer_type = 'Exclusive Distributor',
    tax_identification_no = '05688870483',
    -- Flags
    historical_data = 0,
    cooperated_customer = 1,
    stock_customer = 1,
    key_mark = 'S级',
    success_probability = 100,
    instrument_stock = '有库存',
    reagent_stock = '有库存',
    -- Location (overseas)
    continent = '欧洲',
    country = 'Italy',
    province = 'Firenze',
    city = 'Firenze',
    detailed_address = 'VIA DEI SETTE SANTI, 3, 50131 FIRENZE, Italy',
    detailed_delivery_address = 'VIA DEI SETTE SANTI, 3, 50131 FIRENZE, Italy',
    -- Business
    business_department = '海外业务部',
    first_level_business = '外贸',
    second_level_business = '海外',
    third_level_business = '欧洲',
    business_region = '欧洲大区',
    direct_superior = 'International Sales Director',
    sales_leader = 'Sales Manager',
    contact_person = 'Contact D',
    -- Metadata
    administrative_level = '总部',
    is_benchmark = 1,
    remark = 'Menarini Diagnostics — 意大利独家经销商, 覆盖意大利/荷兰/比利时/西班牙/法国'
WHERE customer_code = 'AMD';

-- ============================================================
-- Update existing customer contacts with expanded fields
-- ============================================================
UPDATE customer_contact SET is_primary = 1, department = 'Marketing' WHERE contact_name = 'Contact A';
UPDATE customer_contact SET is_primary = 0, department = 'Marketing' WHERE contact_name = 'Contact B';
UPDATE customer_contact SET is_primary = 0, department = 'Marketing' WHERE contact_name = 'Contact C';
UPDATE customer_contact SET is_primary = 0, department = 'Order Management' WHERE contact_name = 'Contact D';

-- ============================================================
-- Update existing products with comprehensive CRM fields
-- ============================================================

-- Instruments (category=INSTRUMENT)
UPDATE product SET
    category_l1 = '仪器',
    category_l2 = 'Flash10',
    prod_type = '实体产品',
    product_type = '产品',
    biz_type = '仪器',
    unit_of_measure = '台',
    spec_test_per_unit = '1台/箱',
    tests_per_unit = 1,
    min_order_qty = 1,
    model_spec = 'Flash10',
    market_price_cny = 98000.00,
    market_price_usd = 14000.00,
    standard_price_local = 98000.00,
    floor_price_usd = 100.00,
    tax_rate = 13.00,
    currency = 'EUR',
    is_bundle = 0,
    can_sell_alone = 1,
    need_retrieve = '否',
    can_be_asset = 1,
    can_deploy = '是',
    can_trial = '是',
    is_consumable = '否',
    prod_material_category = '仪器设备',
    prod_material_class = '专机专用',
    sellable_company1 = '利德曼北京',
    status = '启用'
WHERE category = 'INSTRUMENT';

-- Assays (category=ASSAY)
UPDATE product SET
    category_l1 = '试剂',
    category_l2 = CASE
        WHEN product_name LIKE '%SARS-CoV-2%Flu%' THEN '呼吸道多联'
        WHEN product_name LIKE '%SARS-CoV-2%' THEN '新冠'
        WHEN product_name LIKE '%MG&TV&MH%' THEN '性病'
        WHEN product_name LIKE '%CT&NG&UU%' THEN '性病'
        WHEN product_name LIKE '%NG&UU%' THEN '性病'
        WHEN product_name LIKE '%MTB%' THEN '结核'
        WHEN product_name LIKE '%Monkeypox%' THEN '猴痘'
        ELSE '其他'
    END,
    prod_type = '实体产品',
    product_type = '产品',
    biz_type = '试剂',
    unit_of_measure = '盒',
    spec_test_per_unit = '24人份/盒',
    tests_per_unit = 24,
    min_order_qty = 1,
    market_price_cny = 2800.00,
    market_price_usd = 10.00,
    standard_price_local = 2800.00,
    floor_price_usd = 8.00,
    tax_rate = 13.00,
    is_bundle = 0,
    can_sell_alone = 1,
    need_retrieve = '否',
    can_be_asset = 0,
    can_deploy = '否',
    can_trial = '是',
    is_consumable = '是',
    prod_material_category = '试剂耗材',
    prod_material_class = '专机专用',
    sellable_company1 = '利德曼北京',
    status = '启用'
WHERE category = 'ASSAY';

-- EQC
UPDATE product SET
    category_l1 = '质控品',
    category_l2 = 'EQC',
    prod_type = '实体产品',
    product_type = '产品',
    biz_type = '试剂',
    unit_of_measure = '盒',
    spec_test_per_unit = '质控品',
    tests_per_unit = 1,
    min_order_qty = 1,
    market_price_cny = 1400.00,
    market_price_usd = 20.00,
    standard_price_local = 1400.00,
    floor_price_usd = 15.00,
    tax_rate = 13.00,
    is_bundle = 0,
    can_sell_alone = 1,
    need_retrieve = '否',
    can_be_asset = 0,
    can_deploy = '否',
    can_trial = '否',
    is_consumable = '是',
    prod_material_category = '试剂耗材',
    prod_material_class = '质控品',
    sellable_company1 = '利德曼北京',
    status = '启用'
WHERE category = 'EQC';

-- Consumables
UPDATE product SET
    category_l1 = '耗材',
    category_l2 = '采样套装',
    prod_type = '实体产品',
    product_type = '产品',
    biz_type = '试剂',
    unit_of_measure = '盒',
    spec_test_per_unit = '48套/盒',
    tests_per_unit = 48,
    min_order_qty = 1,
    market_price_cny = 700.00,
    market_price_usd = 0.00,
    standard_price_local = 700.00,
    floor_price_usd = 0.00,
    tax_rate = 13.00,
    is_bundle = 0,
    can_sell_alone = 1,
    need_retrieve = '否',
    can_be_asset = 0,
    can_deploy = '否',
    can_trial = '是',
    is_consumable = '是',
    prod_material_category = '耗材配件',
    prod_material_class = '通用',
    sellable_company1 = '利德曼北京',
    status = '启用'
WHERE category = 'CONSUMABLE';

-- ============================================================
-- Create Standard Price List for Overseas Distributor (AMD)
-- ============================================================
INSERT INTO price_list (price_list_no, price_list_name, biz_level1_tag, biz_level2_tag, biz_level3_tag,
    customer_type, region, is_standard, status, remark, created_by)
VALUES ('PL-STD-2026', '海外标准价格表(2026)', '外贸', '海外', '综合',
    '老客户', '欧洲', 1, '启用',
    'Menarini Diagnostics 专用标准价格表, 合同价基于此表协商', 'admin');

-- ============================================================
-- Populate price list items from existing products
-- ============================================================
INSERT INTO price_list_item (price_list_id, product_id, prod_code, prod_name, unit_of_measure, list_price, market_price, price_desc)
SELECT
    (SELECT id FROM price_list WHERE price_list_no = 'PL-STD-2026'),
    p.id,
    p.coyote_code,
    p.product_name,
    COALESCE(p.unit_of_measure, 'kit'),
    COALESCE(p.unit_price_test, p.unit_price_kit, 0),
    COALESCE(p.market_price_usd, 0),
    CASE
        WHEN p.category = 'INSTRUMENT' THEN '仪器目录价'
        WHEN p.category = 'ASSAY' THEN CONCAT('试剂目录价 (', p.menarini_code, ')')
        WHEN p.category = 'EQC' THEN CONCAT('质控品目录价 (', p.menarini_code, ')')
        WHEN p.category = 'CONSUMABLE' THEN '耗材目录价'
        ELSE '标准目录价'
    END
FROM product p
WHERE p.is_active = 1;

-- ============================================================
-- Set exchange rate for EUR-based products
-- ============================================================
UPDATE product SET currency = 'EUR', exchange_rate = 7.8000 WHERE category IN ('INSTRUMENT', 'ASSAY', 'EQC', 'CONSUMABLE');

SELECT 'Seed v2 completed successfully.' AS result;
