# CRM Demo 系统 — Java 后端设计

> Spring Boot 3.x + MyBatis-Plus 3.5.x + MySQL 8.0 + Apache POI  
> Demo 仅业务员单角色，PO 通过 Excel 导入，PI/PL 支持导出

---

## 一、技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | LTS |
| Spring Boot | 3.2.x | 主框架 |
| MyBatis-Plus | 3.5.5 | ORM |
| MySQL | 8.0 | 数据库 |
| Apache POI | 5.2.x | Excel 导入/导出 |
| Lombok | 1.18.x | 代码简化 |
| Hutool | 5.8.x | 工具库 |
| Knife4j | 4.x | API 文档 |

---

## 二、项目包结构

```
com.crm.demo
├── CrmDemoApplication.java
├── common
│   ├── R.java                           # 统一返回体 { code, message, data }
│   ├── GlobalExceptionHandler.java      # @RestControllerAdvice
│   └── BusinessException.java
├── config
│   ├── MyBatisPlusConfig.java
│   ├── CorsConfig.java                  # 允许所有来源（演示用）
│   └── Knife4jConfig.java
├── entity
│   ├── Customer.java
│   ├── CustomerContact.java
│   ├── Product.java
│   ├── Inventory.java
│   ├── PurchaseOrder.java
│   ├── PurchaseOrderItem.java
│   ├── ProformaInvoice.java
│   ├── ProformaInvoiceItem.java
│   ├── PackingList.java
│   └── PackingListItem.java
├── enums
│   ├── OrderStatus.java                 # IMPORTED, APPROVED, PI_GENERATED, COMPLETED, PENDING_APPROVAL, REJECTED
│   ├── InvoiceStatus.java               # DRAFT, APPROVED, PACKING_GENERATED, PENDING_APPROVAL, REJECTED
│   ├── PackingStatus.java               # DRAFT, APPROVED, PENDING_APPROVAL, REJECTED
│   └── ProductCategory.java             # INSTRUMENT, ASSAY, EQC, CONSUMABLE, ACCESSORY, SERVICE
├── dto
│   ├── PoImportResultDTO.java           # PO Excel 导入解析结果
│   ├── PoItemParsedDTO.java             # 解析出的 PO 行项目
│   ├── PiGenerateDTO.java               # 生成 PI 请求
│   ├── PlGenerateDTO.java               # 生成 PL 请求（含装箱信息补充）
│   ├── PlItemFillDTO.java               # PL 行装箱详情（批号/箱规/重量/DGM）
│   └── ApprovalDTO.java                 # 审批请求体 (预留)
├── vo
│   ├── CustomerVO.java                  # 客户详情（含联系人+历史订单）
│   ├── ProductVO.java
│   ├── PurchaseOrderVO.java             # PO 详情（含行项目+解析状态）
│   ├── ProformaInvoiceVO.java           # PI 详情（Invoice 格式，含库存警告）
│   ├── PackingListVO.java               # PL 详情（Packing List 格式，含汇总）
│   └── TraceVO.java                     # 全链路追溯
├── mapper
│   ├── CustomerMapper.java
│   ├── CustomerContactMapper.java
│   ├── ProductMapper.java
│   ├── InventoryMapper.java
│   ├── PurchaseOrderMapper.java
│   ├── PurchaseOrderItemMapper.java
│   ├── ProformaInvoiceMapper.java
│   ├── ProformaInvoiceItemMapper.java
│   ├── PackingListMapper.java
│   └── PackingListItemMapper.java
├── service
│   ├── CustomerService.java
│   ├── ProductService.java
│   ├── InventoryService.java
│   ├── PurchaseOrderService.java
│   ├── PoImportService.java             # ★ 核心：PO Excel 导入解析
│   ├── ProformaInvoiceService.java      # ★ 核心：PO→PI 转换 + PI 导出 + PI 重新导入
│   ├── PackingListService.java          # ★ 核心：打包算法 + PL 导出
│   ├── ApprovalService.java             # 审批服务 (预留)
│   └── TraceService.java
└── service.impl
    ├── CustomerServiceImpl.java
    ├── ProductServiceImpl.java
    ├── InventoryServiceImpl.java
    ├── PurchaseOrderServiceImpl.java
    ├── PoImportServiceImpl.java
    ├── ProformaInvoiceServiceImpl.java
    ├── PackingListServiceImpl.java
    ├── ApprovalServiceImpl.java
    └── TraceServiceImpl.java
```

---

## 三、REST API 清单

### 3.1 客户管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/customers` | 客户分页列表 |
| GET | `/api/customers/search?keyword=AMD` | 按 Customer ID 搜索 |
| GET | `/api/customers/{id}` | 客户详情（含联系人+历史订单） |

### 3.2 产品管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/products` | 产品分页列表（可选 category 筛选） |
| GET | `/api/products/search?keyword=` | 按名称/CAT.# 模糊搜索 |

### 3.3 PO Excel 导入（★ Demo 核心）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/purchase-orders/import` | **上传 PO Excel 文件，自动解析导入** |
| GET | `/api/purchase-orders` | PO 列表（按 customer_id/status 筛选） |
| GET | `/api/purchase-orders/{id}` | PO 详情（含解析后的行项目） |
| PUT | `/api/purchase-orders/{id}` | 修正 PO 行项目（修正未识别产品等） |
| POST | `/api/purchase-orders/{id}/confirm` | 确认 PO 无误，Demo 中自动 APPROVED |

**PO 导入请求**：`multipart/form-data`，字段 `file` (Excel) + `customerId`

**PO 导入响应 (PoImportResultDTO)**：
```json
{
  "code": 200,
  "data": {
    "contractNo": "IT-AMD-20260514",
    "customerId": 1,
    "deliveryTerms": "FCA",
    "paymentTerms": "Net 60 days from invoice date",
    "transportMethod": "Air",
    "countryOfOrigin": "China",
    "poReference": "1429, 31, 32, 33, 47...",
    "items": [
      {
        "lineNo": 1,
        "customerPoNo": "1429",
        "catNo": "6018006902-CE",
        "description": "FlashDetect™ SARS-CoV-2&FluA&FluB EQC (SPEC.B)",
        "qty1": 1, "unit1": "kit",
        "qty2": 1, "unit2": "kit",
        "unitPriceMultiplier": 1,
        "discountFlag": 0,
        "lineValue": 1.00,
        "productMatched": true,
        "warning": null
      }
    ],
    "warnings": [
      { "lineNo": 3, "catNo": "UNKNOWN-CODE", "message": "未识别产品：CAT.# 在 Product 表中不存在" }
    ],
    "totalLines": 20,
    "totalKits": 150,
    "totalValue": 45000.00
  }
}
```

### 3.4 PI 生成与导出（★ Demo 核心）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/proforma-invoices/generate/{poId}` | **PO→PI 自动转换**（商品名称关联编码映射） |
| GET | `/api/proforma-invoices` | PI 列表 |
| GET | `/api/proforma-invoices/{id}` | PI 详情（Invoice 格式） |
| PUT | `/api/proforma-invoices/{id}` | 修正 PI 行项目 Ref No. |
| GET | `/api/proforma-invoices/{id}/export` | **导出 PI 为 Excel (Invoice 格式)** |
| POST | `/api/proforma-invoices/{id}/import` | **★ 重新导入修改后的 PI Excel** |

**PI 重新导入请求**：`multipart/form-data`，字段 `file` (修改后的 PI Excel)

**PI 重新导入响应（差异对比）**：
```json
{
  "code": 200,
  "data": {
    "piId": 1,
    "invoiceNo": "IT-AMD-20260514",
    "changes": [
      { "lineNo": 3, "field": "qty1", "oldValue": "10", "newValue": "12", "type": "MODIFIED" },
      { "lineNo": 5, "field": "refNo", "oldValue": "59400", "newValue": "59406", "type": "MODIFIED" },
      { "lineNo": 21, "field": "ALL", "oldValue": null, "newValue": "新行", "type": "ADDED" },
      { "lineNo": 8, "field": "ALL", "oldValue": "存在", "newValue": null, "type": "DELETED" }
    ],
    "summary": {
      "modifiedCount": 2,
      "addedCount": 1,
      "deletedCount": 1,
      "unchangedCount": 17
    },
    "updatedPi": { /* 应用修改后的完整 PI 数据 */ }
  }
}
```

**PI 详情响应**：
```json
{
  "code": 200,
  "data": {
    "invoiceNo": "IT-AMD-20260514",
    "contractNo": "IT-AMD-20260514",
    "invoiceDate": "2026-05-14",
    "customer": { "companyName": "A. Menarini Diagnostics SRL", "shipToAddress": "...", ... },
    "deliveryTerms": "FCA",
    "paymentTerms": "Net 60 days from invoice date",
    "transportMethod": "Air",
    "countryOfOrigin": "China",
    "poReference": "1429, 31, 32...",
    "totalValue": 45000.00,
    "items": [
      {
        "lineNo": 1, "customerPoNo": "1429",
        "catNo": "6018006902-CE", "refNo": "59824",
        "description": "FlashDetect™ SARS-CoV-2&FluA&FluB EQC (SPEC.B)",
        "qty1": 1, "unit1": "kit", "qty2": 1, "unit2": "kit",
        "unitPriceMultiplier": 1, "discountFlag": 0, "lineValue": 1.00
      }
    ],
    "stockWarnings": [
      { "lineNo": 5, "productName": "...", "required": 40, "available": 30 }
    ]
  }
}
```

### 3.5 PL 生成与导出（★ Demo 核心）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/packing-lists/generate/{piId}` | **打包算法生成 PL**（自动分配批号/箱规/箱号） |
| GET | `/api/packing-lists` | PL 列表 |
| GET | `/api/packing-lists/{id}` | PL 详情（Packing List 格式） |
| PUT | `/api/packing-lists/{id}/items/{itemId}` | 修正 PL 行装箱信息（预留仓库管理员接口） |
| GET | `/api/packing-lists/{id}/export` | **导出 PL 为 Excel (Packing List 格式)** |

**PL 详情响应**：
```json
{
  "code": 200,
  "data": {
    "contractNo": "IT-AMD-20260514",
    "shipTo": "A.MENARINI DIAGNOSTICS SRL\nVIA DEI SETTE SANTI, 3, 50131 FIRENZE, Italy",
    "transportMethod": "Air",
    "transportRoute": "Beijing to Italy",
    "countryOfOrigin": "China",
    "poReference": "1429, 31, 32...",
    "totalCartons": 125,
    "totalVolumeCbm": 7.5494,
    "totalNetWeightKg": 1219.50,
    "totalGrossWeightKg": 1786.80,
    "items": [
      {
        "lineNo": 1, "customerPoNo": "0002058", "catNo": "2004009001", "refNo": "61312",
        "description": "FlashDetect™ Flash10 System",
        "qty1": 1, "unit1": "unit", "qty2": 1, "unit2": "unit",
        "lotNo": "304090251212009", "expiryDate": null,
        "dimension": "57*45*55cm", "qtyCarton": 1, "cartonNoRange": "1",
        "volumeCbm": 0.141075, "netWeightKg": 21.00, "grossWeightKg": 33.00,
        "dgmNameCn": "基因扩增仪", "dgmCode": "PEKBJ202601052581BH640001"
      }
    ]
  }
}
```

### 3.6 全链路追溯

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/trace/{contractNo}` | PO→PI→PL 全链路数据 |

### 3.7 预留接口（Demo 不展示前端页面，后续扩展）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/approvals/po/{id}` | 审批 PO（预留） |
| POST | `/api/approvals/pi/{id}` | 审批 PI（预留） |
| POST | `/api/approvals/pl/{id}` | 审批 PL（预留） |
| GET | `/api/approvals/pending` | 待审批列表（预留） |
| GET | `/api/inventories` | 库存列表（预留） |
| POST | `/api/inventories` | 入库（预留） |
| PUT | `/api/inventories/{id}` | 编辑批次（预留） |

---

## 四、核心业务逻辑

### 4.1 PO Excel 导入解析（PoImportServiceImpl）

```java
@Service
public class PoImportServiceImpl implements PoImportService {

    @Transactional
    public PoImportResultDTO importPoExcel(MultipartFile file, Long customerId) {
        // 1. 校验文件格式
        String filename = file.getOriginalFilename();
        if (!filename.matches(".*\\.(xlsx|xls)$")) {
            throw new BusinessException("仅支持 .xlsx / .xls 格式");
        }

        // 2. 读取 Excel (Apache POI)
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);  // 取第一个 sheet

        // 3. 解析表头区域（逐行扫描关键字）
        PoHeaderInfo header = parseHeader(sheet);
        // 关键字匹配: "Contract #" → contractNo, "Terms of delivery" → deliveryTerms,
        // "Terms of payment" → paymentTerms, "Transport by" → transportMethod,
        // "Country of Origin" → countryOfOrigin,  "PO:" → poReference

        // 4. 定位行项目起始行（找到 "NO." / "Order #" / "CAT. #" 列头行）
        int dataStartRow = findDataStartRow(sheet);  // 列头行的下一行

        // 5. 解析行项目
        List<PoItemParsedDTO> items = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (int rowIdx = dataStartRow; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row == null || isRowEmpty(row)) continue;

            PoItemParsedDTO item = new PoItemParsedDTO();
            item.setLineNo(items.size() + 1);
            item.setCustomerPoNo(getCellString(row, 1));   // Order #
            item.setCatNo(getCellString(row, 2));           // CAT. #
            item.setDescription(getCellString(row, 3));     // DESCRIPTION
            item.setQty1(getCellInt(row, 4));               // QTY-1
            item.setUnit1(getCellString(row, 5));           // UNIT-1
            item.setQty2(getCellInt(row, 6));               // QTY-2
            item.setUnit2(getCellString(row, 7));           // UNIT-2
            item.setUnitPriceMultiplier(getCellBigDecimal(row, 8)); // UNIT PRICE
            item.setDiscountFlag(detectDiscount(row, 9, 10));       // Discount + VALUE 分析
            item.setLineValue(parseLineValue(row, 10));             // VALUE

            // ★ CAT.# 匹配 product 表
            Product product = productMapper.selectOne(
                new LambdaQueryWrapper<Product>().eq(Product::getCoyoteCode, item.getCatNo())
            );
            if (product != null) {
                item.setProductId(product.getId());
                item.setProductMatched(true);
            } else {
                item.setProductMatched(false);
                warnings.add(String.format("行%d: CAT.# %s 未在 Product 表中找到",
                    item.getLineNo(), item.getCatNo()));
            }
            items.add(item);
        }

        // 6. 生成合同号
        Customer customer = customerMapper.selectById(customerId);
        String contractNo = "IT-" + customer.getCustomerCode() + "-" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 7. 创建 PO 主表
        PurchaseOrder po = new PurchaseOrder();
        po.setContractNo(contractNo);
        po.setCustomerId(customerId);
        po.setDeliveryTerms(header.getDeliveryTerms());
        po.setPaymentTerms(header.getPaymentTerms());
        po.setTransportMethod(header.getTransportMethod());
        po.setCountryOfOrigin(header.getCountryOfOrigin());
        po.setPoReference(header.getPoReference());
        po.setStatus("IMPORTED");  // Demo 初始状态
        purchaseOrderMapper.insert(po);

        // 8. 批量插入 PO 行项目
        for (PoItemParsedDTO item : items) {
            PurchaseOrderItem poItem = new PurchaseOrderItem();
            poItem.setPoId(po.getId());
            // ... 复制字段
            purchaseOrderItemMapper.insert(poItem);
        }

        // 9. 自动通过审批（Demo 简化）
        po.setStatus("APPROVED");
        po.setApprovedAt(LocalDateTime.now());
        purchaseOrderMapper.updateById(po);

        // 10. 返回解析结果
        PoImportResultDTO result = new PoImportResultDTO();
        result.setPoId(po.getId());
        result.setContractNo(contractNo);
        result.setItems(items);
        result.setWarnings(warnings);
        return result;
    }

    // ★ 免费样品识别
    private int detectDiscount(Row row, int discountCol, int valueCol) {
        String valueStr = getCellString(row, valueCol);
        return (valueStr != null && valueStr.toLowerCase().contains("free of charge")) ? 1 : 0;
    }
}
```

### 4.2 PO → PI 自动转换（ProformaInvoiceServiceImpl）

```java
@Transactional
public ProformaInvoiceVO generateFromPo(Long poId) {
    PurchaseOrder po = purchaseOrderMapper.selectById(poId);
    if (!"APPROVED".equals(po.getStatus())) {
        throw new BusinessException("PO 状态不是 APPROVED，无法生成 PI");
    }

    List<PurchaseOrderItem> poItems = poItemMapper.selectList(
        new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getPoId, poId));

    // 创建 PI 主表
    ProformaInvoice pi = new ProformaInvoice();
    pi.setInvoiceNo(po.getContractNo());
    pi.setPoId(poId);
    pi.setContractNo(po.getContractNo());
    pi.setCustomerId(po.getCustomerId());
    pi.setInvoiceDate(LocalDate.now());
    pi.setDeliveryTerms(po.getDeliveryTerms());
    pi.setPaymentTerms(po.getPaymentTerms());
    pi.setTransportMethod(po.getTransportMethod());
    pi.setCountryOfOrigin(po.getCountryOfOrigin());
    pi.setPoReference(po.getPoReference());
    pi.setStatus("DRAFT");
    proformaInvoiceMapper.insert(pi);

    // 逐行转换
    BigDecimal totalValue = BigDecimal.ZERO;
    List<StockWarning> stockWarnings = new ArrayList<>();

    for (int i = 0; i < poItems.size(); i++) {
        PurchaseOrderItem poItem = poItems.get(i);
        ProformaInvoiceItem piItem = copyFromPoItem(poItem, pi.getId(), i + 1);

        // ★ 核心：通过 CAT.# 和 Description 匹配 product，获取 menarini_code → Ref No.
        Product product = productMapper.selectOne(
            new LambdaQueryWrapper<Product>().eq(Product::getCoyoteCode, poItem.getCatNo()));
        if (product == null) {
            // 兜底：通过 description 关键词匹配 product_name
            product = productMapper.selectOne(
                new LambdaQueryWrapper<Product>()
                    .like(Product::getProductName, extractCoreName(poItem.getDescription()))
                    .last("LIMIT 1"));
        }
        if (product != null) {
            piItem.setProductId(product.getId());
            piItem.setRefNo(product.getMenariniCode());  // ← 编码转换
        }

        // 库存检查
        int available = inventoryMapper.sumKitByProductId(poItem.getProductId());
        if (available < poItem.getQty1()) {
            stockWarnings.add(new StockWarning(i + 1, poItem.getDescription(), poItem.getQty1(), available));
        }

        totalValue = totalValue.add(poItem.getLineValue());
        proformaInvoiceItemMapper.insert(piItem);
    }

    // 更新 PI
    pi.setTotalValue(totalValue);
    pi.setStatus("APPROVED");  // Demo 自动通过
    proformaInvoiceMapper.updateById(pi);

    // 更新 PO
    po.setStatus("PI_GENERATED");
    purchaseOrderMapper.updateById(po);

    ProformaInvoiceVO vo = buildPIVO(pi);
    vo.setStockWarnings(stockWarnings);
    return vo;
}
```

### 4.2.1 PI 重新导入（ProformaInvoiceServiceImpl.reimportPi）

```java
@Transactional
public PiImportResultDTO reimportPi(Long piId, MultipartFile file) {
    // 1. 查询当前 PI
    ProformaInvoice pi = piMapper.selectById(piId);
    List<ProformaInvoiceItem> currentItems = piItemMapper.selectByPiId(piId);

    // 2. 解析上传的 Excel
    Workbook workbook = WorkbookFactory.create(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(0);

    // 3. 解析表头 + 行项目（复用 PO 导入的 Excel 解析引擎）
    List<ParsedPiItem> importedItems = parsePiItems(sheet);

    // 4. ★ 按行号(NO.) 逐一对比
    List<ChangeRecord> changes = new ArrayList<>();
    Map<Integer, ProformaInvoiceItem> currentMap = currentItems.stream()
        .collect(Collectors.toMap(ProformaInvoiceItem::getLineNo, i -> i));

    for (ParsedPiItem imported : importedItems) {
        int lineNo = imported.getLineNo();
        ProformaInvoiceItem current = currentMap.get(lineNo);

        if (current == null) {
            // 新增行
            changes.add(ChangeRecord.added(lineNo));
        } else {
            // 逐字段对比
            if (!Objects.equals(current.getQty1(), imported.getQty1()))
                changes.add(ChangeRecord.modified(lineNo, "qty1", current.getQty1(), imported.getQty1()));
            if (!Objects.equals(current.getRefNo(), imported.getRefNo()))
                changes.add(ChangeRecord.modified(lineNo, "refNo", current.getRefNo(), imported.getRefNo()));
            if (!Objects.equals(current.getCatNo(), imported.getCatNo()))
                changes.add(ChangeRecord.modified(lineNo, "catNo", current.getCatNo(), imported.getCatNo()));
            // ... 其他字段对比
            currentMap.remove(lineNo);
        }
    }
    // Excel 中不存在的行 → 待删除
    for (Integer missingLineNo : currentMap.keySet()) {
        changes.add(ChangeRecord.deleted(missingLineNo));
    }

    // 5. 返回差异对比（不立即写入，由前端确认后调用 apply 接口）
    PiImportResultDTO result = new PiImportResultDTO();
    result.setPiId(piId);
    result.setChanges(changes);
    result.setSummary(buildChangeSummary(changes));
    return result;
}

@Transactional
public ProformaInvoiceVO applyImportChanges(Long piId, List<ChangeRecord> confirmedChanges) {
    // 业务员确认后，执行实际数据更新
    for (ChangeRecord change : confirmedChanges) {
        switch (change.getType()) {
            case "MODIFIED":
                ProformaInvoiceItem item = piItemMapper.selectByPiIdAndLineNo(piId, change.getLineNo());
                applyFieldChange(item, change.getField(), change.getNewValue());
                piItemMapper.updateById(item);
                break;
            case "ADDED":
                ProformaInvoiceItem newItem = buildFromParsedRow(change.getParsedRow());
                newItem.setPiId(piId);
                newItem.setLineNo(change.getLineNo());
                piItemMapper.insert(newItem);
                break;
            case "DELETED":
                piItemMapper.deleteByPiIdAndLineNo(piId, change.getLineNo());
                break;
        }
    }

    // 重新计算 total_value、库存检查
    recalculateTotalValue(piId);
    pi.setStatus("APPROVED");  // Demo 自动通过
    piMapper.updateById(pi);
    return buildPIVO(pi);
}
```

### 4.3 打包算法（PackingListServiceImpl）

```java
@Transactional
public PackingListVO generateFromPi(Long piId) {
    ProformaInvoice pi = piMapper.selectById(piId);
    if (!"APPROVED".equals(pi.getStatus())) {
        throw new BusinessException("PI 状态不是 APPROVED");
    }

    List<ProformaInvoiceItem> piItems = piItemMapper.selectList(
        new LambdaQueryWrapper<ProformaInvoiceItem>().eq(ProformaInvoiceItem::getPiId, piId));
    Customer customer = customerMapper.selectById(pi.getCustomerId());

    // 创建 PL 主表
    PackingList pl = new PackingList();
    pl.setPiId(piId);
    pl.setContractNo(pi.getContractNo());
    pl.setCustomerId(pi.getCustomerId());
    pl.setShipTo(customer.getShipToAddress());
    pl.setTransportMethod(pi.getTransportMethod());
    pl.setTransportRoute("Beijing to Italy");
    pl.setCountryOfOrigin(pi.getCountryOfOrigin());
    pl.setPoReference(pi.getPoReference());
    pl.setStatus("DRAFT");
    packingListMapper.insert(pl);

    // ★ 执行打包算法
    int globalCartonNo = 0;
    BigDecimal totalVolume = BigDecimal.ZERO, totalNet = BigDecimal.ZERO, totalGross = BigDecimal.ZERO;
    int totalCartons = 0;

    for (int i = 0; i < piItems.size(); i++) {
        ProformaInvoiceItem piItem = piItems.get(i);
        Product product = productMapper.selectById(piItem.getProductId());

        // 复制基础字段
        PackingListItem plItem = copyFromPiItem(piItem, pl.getId(), i + 1);

        // ★ 打包算法核心
        int totalUnits = piItem.getQty1();
        String unit = piItem.getUnit1();
        int cartonCount;
        String dimension;
        BigDecimal singleVol;

        if ("unit".equals(unit)) {
            cartonCount = totalUnits;
            dimension = "57*45*55cm";
            singleVol = new BigDecimal("0.141075");
        } else {
            int fullCartons = totalUnits / 6;
            int remaining = totalUnits % 6;
            cartonCount = fullCartons;
            if (remaining > 0) {
                cartonCount++;
                if (remaining < 4) {
                    dimension = "35*38*26cm";
                    singleVol = new BigDecimal("0.03458");
                } else {
                    dimension = "57*26*37cm";
                    singleVol = new BigDecimal("0.054834");
                }
            } else {
                dimension = "57*26*37cm";
                singleVol = new BigDecimal("0.054834");
            }
        }

        // 箱号分配
        String cartonNoRange;
        if (cartonCount == 1) { globalCartonNo++; cartonNoRange = String.valueOf(globalCartonNo); }
        else { int start = globalCartonNo + 1; globalCartonNo += cartonCount; cartonNoRange = start + "-" + globalCartonNo; }

        // 体积重量
        BigDecimal rowVolume = singleVol.multiply(BigDecimal.valueOf(cartonCount));
        BigDecimal rowNet = (product.getNetWeightKg() != null) 
            ? product.getNetWeightKg().multiply(BigDecimal.valueOf(totalUnits)) : BigDecimal.ZERO;
        BigDecimal rowGross = (product.getGrossWeightKg() != null)
            ? product.getGrossWeightKg().multiply(BigDecimal.valueOf(totalUnits))
            : rowNet.multiply(new BigDecimal("1.15"));

        plItem.setDimension(dimension);
        plItem.setQtyCarton(cartonCount);
        plItem.setCartonNoRange(cartonNoRange);
        plItem.setVolumeCbm(rowVolume);
        plItem.setNetWeightKg(rowNet);
        plItem.setGrossWeightKg(rowGross);
        plItem.setDgmNameCn(product.getDgmNameCn());
        plItem.setDgmCode(product.getDgmCode());

        // ★ Demo 自动分配批号（取库存充足且效期最近的批次）
        Inventory bestLot = inventoryMapper.selectOne(
            new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, piItem.getProductId())
                .ge(Inventory::getQuantityKit, piItem.getQty1())
                .orderByAsc(Inventory::getExpiryDate)
                .last("LIMIT 1"));
        if (bestLot != null) {
            plItem.setLotNo(bestLot.getLotNo());
            plItem.setExpiryDate(bestLot.getExpiryDate());
        }

        totalVolume = totalVolume.add(rowVolume);
        totalNet = totalNet.add(rowNet);
        totalGross = totalGross.add(rowGross);
        totalCartons += cartonCount;

        packingListItemMapper.insert(plItem);
    }

    // 更新 PL 汇总
    pl.setTotalVolumeCbm(totalVolume);
    pl.setTotalNetWeightKg(totalNet);
    pl.setTotalGrossWeightKg(totalGross);
    pl.setTotalCartons(totalCartons);
    pl.setStatus("APPROVED");  // Demo 自动通过
    packingListMapper.updateById(pl);

    // 更新 PI
    pi.setStatus("PACKING_GENERATED");
    piMapper.updateById(pi);

    // 更新 PO
    PurchaseOrder po = poMapper.selectById(pi.getPoId());
    po.setStatus("COMPLETED");
    poMapper.updateById(po);

    return buildPLVO(pl);
}
```

### 4.4 PI/PL Excel 导出

```java
// PI 导出（Invoice 格式）
@GetMapping("/{id}/export")
public void exportInvoice(@PathVariable Long id, HttpServletResponse response) {
    ProformaInvoice pi = piMapper.selectById(id);
    List<ProformaInvoiceItem> items = piItemMapper.selectByPiId(id);
    Customer customer = customerMapper.selectById(pi.getCustomerId());

    Workbook wb = new XSSFWorkbook();
    Sheet sheet = wb.createSheet("Invoice");
    // 按 Invoice 模板格式写入：表头信息 + 行项目 + Total Value
    // ... 使用 Apache POI 构建 Excel
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", 
        "attachment; filename=Invoice_" + pi.getContractNo() + "_" + pi.getInvoiceDate() + ".xlsx");
    wb.write(response.getOutputStream());
}

// PL 导出（Packing List 格式）
@GetMapping("/{id}/export")
public void exportPackingList(@PathVariable Long id, HttpServletResponse response) {
    // 类似逻辑，按 Packing List 模板格式：表头 + 18 字段行项目 + 汇总行
}
```

---

## 五、状态机（Demo 简化版）

```
PO:  IMPORTED ──(自动通过)──▶ APPROVED ──生成PI──▶ PI_GENERATED ──PI确认──▶ COMPLETED

PI:  DRAFT ──(自动通过)──▶ APPROVED ──打包完成──▶ PACKING_GENERATED

PL:  DRAFT ──(自动通过)──▶ APPROVED (FINAL)
```

> `PENDING_APPROVAL` / `REJECTED` 在枚举中定义但 Demo 不使用，后续启用审批流即可激活。

---

## 六、统一返回体

```java
@Data
@NoArgsConstructor @AllArgsConstructor
public class R<T> {
    private int code;
    private String message;
    private T data;
    public static <T> R<T> ok(T data) { return new R<>(200, "success", data); }
    public static <T> R<T> fail(String msg) { return new R<>(500, msg, null); }
}
```

---

## 七、application.yml

```yaml
server:
  port: 8080
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/crm_demo?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
  servlet:
    multipart:
      max-file-size: 10MB

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
```

---

## 八、交付大模型的 Java 代码生成提示词

```
你是一名资深 Java 后端开发工程师。请生成一个完整的 Spring Boot 3.x CRM Demo 项目。

## 项目背景
IVD 公司海外 CRM Demo 系统，单一业务员账号，覆盖"PO Excel 导入 → PI 自动生成 → PL 打包生成"全链路。
Demo 中审批自动通过，审批者/仓库管理员接口预留后续扩展。

## 核心业务流程（7 步）
1. 业务员搜索客户 (GET /api/customers/search?keyword=AMD)
2. 业务员导入客户发来的 PO Excel (POST /api/purchase-orders/import)
3. 系统自动生成 PI — 通过 CAT.# 和 Product Name 匹配，映射 Menarini code 为 Ref No. (POST /api/proforma-invoices/generate/{poId})
4. 业务员查看并导出 PI Excel — Invoice 格式 (GET /api/proforma-invoices/{id}/export)
5. ★ 业务员重新导入修改后的 PI Excel — 系统按行号匹配差异，返回变更对比 (POST /api/proforma-invoices/{id}/import + POST /api/proforma-invoices/{id}/apply-import)
6. 业务员触发 PL 生成 — 系统执行打包算法：6 盒/箱，剩余<4 用 small box，全局箱号递增 (POST /api/packing-lists/generate/{piId})
7. 业务员查看并导出 PL Excel — Packing List 格式 (GET /api/packing-lists/{id}/export)

## 技术栈
Spring Boot 3.2.x + MyBatis-Plus 3.5.5 + MySQL 8.0 + Apache POI 5.2.x + JDK 17 + Lombok + Hutool + Knife4j

## 数据库（10 张表 DDL）
[此处贴入 CRM_数据库设计.md 中的完整 DDL]

## API 清单
[此处贴入本文档第三章的 API 表格]

## 核心算法
### 1. PO Excel 导入解析 (PoImportServiceImpl.importPoExcel)
[此处贴入 4.1 伪代码]

### 2. PO→PI 转换 (ProformaInvoiceServiceImpl.generateFromPo)
[此处贴入 4.2 伪代码]

### 3. 打包算法 (PackingListServiceImpl.generateFromPi)
[此处贴入 4.3 伪代码]

### 4. PI 重新导入 (ProformaInvoiceServiceImpl.reimportPi + applyImportChanges)
[此处贴入 4.2.1 伪代码]

## PI 重新导入规则
- 按行号 (NO.) 匹配当前 PI 行项目：匹配→字段差异对比(MODIFIED)，新行号→新增(ADDED)，缺失行号→待删除(DELETED)
- 差异对比字段：qty1, refNo, catNo, description, unitPriceMultiplier, lineValue
- 首次调用 /import 仅返回差异对比，不写入数据库
- 业务员确认后调用 /apply-import 执行实际更新
- 更新后重新计算 total_value 和库存检查

## 打包规则
- 试剂/耗材 (unit=kit): 标准箱 57*26*37cm, 6 盒/箱, 剩余 <4→小箱 35*38*26cm, >=4→标准箱
- 仪器 (unit=unit): 每台一箱 57*45*55cm
- 箱号全局跨行递增，多箱用 "11-15" 范围格式
- demo 自动分配批号（取库存最充足且效期最近批次）
- 毛重=净重×1.15（无 product 数据时的默认系数）

## 状态机（Demo 简化，自动跳过审批）
PO: IMPORTED→APPROVED→PI_GENERATED→COMPLETED
PI: DRAFT→APPROVED→PACKING_GENERATED
PL: DRAFT→APPROVED

## PI/PL Excel 导出
- PI 导出格式参照 Invoice 表（表头+行项目+Total Value）
- PL 导出格式参照 Packing List 表（表头+18字段行项目+汇总行）

## 要求
1. 完整项目结构 com.crm.demo
2. 所有 Controller/Service/ServiceImpl/Mapper/Entity/DTO/VO
3. 每个文件完整可编译，含所有 import
4. 使用 @Transactional 保证数据一致性
5. 不要生成测试代码、前端代码、Dockerfile
6. 用 // File: src/main/java/com/crm/demo/... 标注路径
```
