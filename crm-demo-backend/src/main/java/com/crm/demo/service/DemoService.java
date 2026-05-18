package com.crm.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.demo.dto.PoImportResult;
import com.crm.demo.dto.PoImportResult.PoItemRow;
import com.crm.demo.entity.*;
import com.crm.demo.mapper.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DemoService {

    private final CustomerMapper customerMapper;
    private final CustomerContactMapper contactMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final PurchaseOrderMapper poMapper;
    private final PurchaseOrderItemMapper poItemMapper;
    private final ProformaInvoiceMapper piMapper;
    private final ProformaInvoiceItemMapper piItemMapper;
    private final PackingListMapper plMapper;
    private final PackingListItemMapper plItemMapper;

    public DemoService(CustomerMapper cm, CustomerContactMapper ccm, ProductMapper pm,
                       InventoryMapper im, PurchaseOrderMapper pom, PurchaseOrderItemMapper poim,
                       ProformaInvoiceMapper pim, ProformaInvoiceItemMapper piim,
                       PackingListMapper plm, PackingListItemMapper plim) {
        this.customerMapper = cm; this.contactMapper = ccm; this.productMapper = pm;
        this.inventoryMapper = im; this.poMapper = pom; this.poItemMapper = poim;
        this.piMapper = pim; this.piItemMapper = piim; this.plMapper = plm; this.plItemMapper = plim;
    }

    // ==================== Customer ====================
    public List<Customer> searchCustomers(String keyword) {
        return customerMapper.selectList(new LambdaQueryWrapper<Customer>()
            .eq(Customer::getStatus, 1)
            .and(w -> w.like(Customer::getCustomerCode, keyword).or().like(Customer::getCompanyName, keyword)));
    }

    public Map<String, Object> getCustomerDetail(Long id) {
        Customer c = customerMapper.selectById(id);
        if (c == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("customer", c);
        m.put("contacts", contactMapper.selectList(
            new LambdaQueryWrapper<CustomerContact>().eq(CustomerContact::getCustomerId, id)));
        return m;
    }

    // ==================== Product ====================
    public List<Product> getProducts(String keyword, String category) {
        LambdaQueryWrapper<Product> q = new LambdaQueryWrapper<Product>().eq(Product::getIsActive, 1);
        if (category != null && !category.isEmpty()) q.eq(Product::getCategory, category);
        if (keyword != null && !keyword.isEmpty())
            q.and(w -> w.like(Product::getProductName, keyword).or().like(Product::getCoyoteCode, keyword));
        return productMapper.selectList(q);
    }

    // ==================== PO Import ====================
    @Transactional
    public PoImportResult importPoExcel(MultipartFile file, Long customerId) throws IOException {
        Customer customer = customerMapper.selectById(customerId);
        Workbook wb = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = wb.getSheetAt(0);
        PoImportResult result = new PoImportResult();
        result.setCustomerId(customerId);

        // Parse header - scan for keyword rows
        Map<String, String> header = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(30, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String line = getRowText(row, 10);
            if (line.contains("Contract #")) header.put("contractNo", getCellStr(row, 4));
            else if (line.contains("Terms of delivery")) header.put("deliveryTerms", extractAfterColon(row, 5));
            else if (line.contains("Terms of payment")) header.put("paymentTerms", extractAfterColon(row, 5));
            else if (line.contains("Transport by")) header.put("transportMethod", extractAfterColon(row, 5));
            else if (line.contains("Country of Origin")) header.put("countryOfOrigin", extractAfterColon(row, 5));
            else if (line.toUpperCase().contains("PO:")) header.put("poReference", extractAfterColon(row, 5));
        }
        result.setDeliveryTerms(header.getOrDefault("deliveryTerms", "FCA"));
        result.setPaymentTerms(header.getOrDefault("paymentTerms", "Net 60 days from invoice date"));
        result.setTransportMethod(header.getOrDefault("transportMethod", "Air"));
        result.setCountryOfOrigin(header.getOrDefault("countryOfOrigin", "China"));
        result.setPoReference(header.getOrDefault("poReference", ""));

        String contractNo = "IT-" + customer.getCustomerCode() + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        result.setContractNo(contractNo);

        // Find data start row (column header row)
        int dataStart = -1;
        for (int i = 0; i < Math.min(30, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String text = getRowText(row, 6);
            if (text.contains("NO.") && text.contains("Order") && text.contains("CAT")) { dataStart = i + 1; break; }
        }
        if (dataStart == -1) dataStart = 17; // fallback

        // Create PO
        PurchaseOrder po = new PurchaseOrder();
        po.setContractNo(contractNo);
        po.setCustomerId(customerId);
        po.setDeliveryTerms(result.getDeliveryTerms());
        po.setPaymentTerms(result.getPaymentTerms());
        po.setTransportMethod(result.getTransportMethod());
        po.setCountryOfOrigin(result.getCountryOfOrigin());
        po.setPoReference(result.getPoReference());
        po.setStatus("IMPORTED");
        poMapper.insert(po);
        result.setPoId(po.getId());

        // Parse rows
        int totalKits = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        for (int i = dataStart; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row, 10)) continue;
            PoItemRow item = new PoItemRow();
            item.setLineNo(result.getItems().size() + 1);
            item.setCustomerPoNo(getCellStr(row, 1));
            item.setCatNo(getCellStr(row, 2));
            item.setDescription(getCellStr(row, 3));
            item.setQty1(getCellInt(row, 4));
            item.setUnit1(getCellStr(row, 5));
            item.setQty2(getCellInt(row, 6));
            item.setUnit2(getCellStr(row, 7));
            item.setUnitPriceMultiplier(parseBigDecimal(getCellStr(row, 8)));
            item.setLineValue(parseBigDecimal(extractValue(getCellStr(row, 10))));

            // Detect free of charge
            String valStr = getCellStr(row, 10);
            if (valStr != null && valStr.toLowerCase().contains("free of charge")) item.setDiscountFlag(1);

            // Match product
            Product p = productMapper.selectOne(new LambdaQueryWrapper<Product>().eq(Product::getCoyoteCode, item.getCatNo()));
            if (p != null) {
                item.setProductId(p.getId());
                item.setProductMatched(true);
            } else {
                item.setProductMatched(false);
                item.setWarning("CAT.# " + item.getCatNo() + " not found in Product table");
                result.getWarnings().add("Line " + item.getLineNo() + ": " + item.getWarning());
            }
            result.getItems().add(item);
            totalKits += "unit".equals(item.getUnit1()) ? item.getQty1() : item.getQty1();
            totalValue = totalValue.add(item.getLineValue());
        }
        result.setTotalLines(result.getItems().size());
        result.setTotalKits(totalKits);
        result.setTotalValue(totalValue);

        // Save items
        for (PoItemRow item : result.getItems()) {
            PurchaseOrderItem poi = new PurchaseOrderItem();
            poi.setPoId(po.getId()); poi.setLineNo(item.getLineNo()); poi.setCustomerPoNo(item.getCustomerPoNo());
            poi.setProductId(item.getProductId()); poi.setCatNo(item.getCatNo()); poi.setDescription(item.getDescription());
            poi.setQty1(item.getQty1()); poi.setUnit1(item.getUnit1()); poi.setQty2(item.getQty2()); poi.setUnit2(item.getUnit2());
            poi.setUnitPriceMultiplier(item.getUnitPriceMultiplier()); poi.setDiscountFlag(item.getDiscountFlag());
            poi.setLineValue(item.getLineValue());
            poItemMapper.insert(poi);
        }

        // Auto-approve for demo
        po.setStatus("APPROVED");
        po.setApprovedAt(LocalDateTime.now());
        poMapper.updateById(po);

        return result;
    }

    // ==================== PI Generation ====================
    @Transactional
    public Map<String, Object> generatePI(Long poId) {
        PurchaseOrder po = poMapper.selectById(poId);
        List<PurchaseOrderItem> poItems = poItemMapper.selectList(
            new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getPoId, poId));

        ProformaInvoice pi = new ProformaInvoice();
        pi.setInvoiceNo(po.getContractNo());
        pi.setPoId(poId); pi.setContractNo(po.getContractNo());
        pi.setCustomerId(po.getCustomerId()); pi.setInvoiceDate(LocalDate.now());
        pi.setDeliveryTerms(po.getDeliveryTerms()); pi.setPaymentTerms(po.getPaymentTerms());
        pi.setTransportMethod(po.getTransportMethod()); pi.setCountryOfOrigin(po.getCountryOfOrigin());
        pi.setPoReference(po.getPoReference()); pi.setStatus("DRAFT");
        piMapper.insert(pi);

        BigDecimal totalValue = BigDecimal.ZERO;
        List<Map<String, Object>> stockWarnings = new ArrayList<>();
        List<ProformaInvoiceItem> piItems = new ArrayList<>();

        for (int i = 0; i < poItems.size(); i++) {
            PurchaseOrderItem poi = poItems.get(i);
            ProformaInvoiceItem pii = new ProformaInvoiceItem();
            pii.setPiId(pi.getId()); pii.setLineNo(i + 1); pii.setCustomerPoNo(poi.getCustomerPoNo());
            pii.setProductId(poi.getProductId()); pii.setCatNo(poi.getCatNo()); pii.setDescription(poi.getDescription());
            pii.setQty1(poi.getQty1()); pii.setUnit1(poi.getUnit1()); pii.setQty2(poi.getQty2()); pii.setUnit2(poi.getUnit2());
            pii.setUnitPriceMultiplier(poi.getUnitPriceMultiplier()); pii.setDiscountFlag(poi.getDiscountFlag());
            pii.setLineValue(poi.getLineValue());

            // Menarini code mapping
            if (poi.getCatNo() != null) {
                Product p = productMapper.selectOne(new LambdaQueryWrapper<Product>().eq(Product::getCoyoteCode, poi.getCatNo()));
                if (p != null) { pii.setProductId(p.getId()); pii.setRefNo(p.getMenariniCode()); }
            }
            // Stock check
            if (pii.getProductId() != null) {
                int avail = inventoryMapper.selectList(new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, pii.getProductId()))
                    .stream().mapToInt(Inventory::getQuantityKit).sum();
                if (avail < poi.getQty1()) stockWarnings.add(Map.of("lineNo", i+1, "productName", poi.getDescription(), "required", poi.getQty1(), "available", avail));
            }
            totalValue = totalValue.add(pii.getLineValue());
            piItems.add(pii);
        }
        for (ProformaInvoiceItem item : piItems) piItemMapper.insert(item);

        pi.setTotalValue(totalValue);
        pi.setStatus("APPROVED"); // demo auto-approve
        piMapper.updateById(pi);

        po.setStatus("PI_GENERATED");
        poMapper.updateById(po);

        Map<String, Object> result = buildPIResponse(pi, piItems);
        result.put("stockWarnings", stockWarnings);
        return result;
    }

    public Map<String, Object> getPIDetail(Long piId) {
        ProformaInvoice pi = piMapper.selectById(piId);
        List<ProformaInvoiceItem> items = piItemMapper.selectList(
            new LambdaQueryWrapper<ProformaInvoiceItem>().eq(ProformaInvoiceItem::getPiId, piId));
        Map<String, Object> result = buildPIResponse(pi, items);
        // Add per-item stock info
        List<Map<String, Object>> enrichedItems = new ArrayList<>();
        for (ProformaInvoiceItem item : items) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", item.getId()); m.put("lineNo", item.getLineNo());
            m.put("customerPoNo", item.getCustomerPoNo()); m.put("productId", item.getProductId());
            m.put("catNo", item.getCatNo()); m.put("refNo", item.getRefNo());
            m.put("description", item.getDescription()); m.put("qty1", item.getQty1());
            m.put("unit1", item.getUnit1()); m.put("qty2", item.getQty2()); m.put("unit2", item.getUnit2());
            m.put("unitPriceMultiplier", item.getUnitPriceMultiplier());
            m.put("discountFlag", item.getDiscountFlag()); m.put("lineValue", item.getLineValue());
            if (item.getProductId() != null) {
                int avail = inventoryMapper.selectList(new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, item.getProductId()))
                    .stream().mapToInt(Inventory::getQuantityKit).sum();
                m.put("stockAvailable", avail);
            }
            enrichedItems.add(m);
        }
        result.put("items", enrichedItems);
        return result;
    }

    // ==================== PI Online Edit ====================
    @Transactional
    public Map<String, Object> updatePIItem(Long piId, Long itemId, Map<String, Object> updates) {
        ProformaInvoiceItem item = piItemMapper.selectById(itemId);
        if (item == null || !item.getPiId().equals(piId)) {
            throw new RuntimeException("PI item not found");
        }
        // Update editable fields
        if (updates.containsKey("catNo")) {
            String catNo = (String) updates.get("catNo");
            item.setCatNo(catNo);
            Product p = productMapper.selectOne(new LambdaQueryWrapper<Product>().eq(Product::getCoyoteCode, catNo));
            if (p != null) { item.setProductId(p.getId()); item.setRefNo(p.getMenariniCode()); }
        }
        if (updates.containsKey("refNo")) item.setRefNo((String) updates.get("refNo"));
        if (updates.containsKey("description")) item.setDescription((String) updates.get("description"));
        if (updates.containsKey("qty1")) {
            int newQty = ((Number) updates.get("qty1")).intValue();
            item.setQty1(newQty);
            item.setQty2(newQty * 24); // approx: each kit = 24 tests
        }
        if (updates.containsKey("qty2")) item.setQty2(((Number) updates.get("qty2")).intValue());
        piItemMapper.updateById(item);

        // Recalculate total value
        ProformaInvoice pi = piMapper.selectById(piId);
        List<ProformaInvoiceItem> items = piItemMapper.selectList(
            new LambdaQueryWrapper<ProformaInvoiceItem>().eq(ProformaInvoiceItem::getPiId, piId));
        BigDecimal total = items.stream().map(i -> i.getLineValue() != null ? i.getLineValue() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        pi.setTotalValue(total);
        piMapper.updateById(pi);

        // Stock check
        int avail = 0;
        if (item.getProductId() != null) {
            avail = inventoryMapper.selectList(new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, item.getProductId()))
                .stream().mapToInt(Inventory::getQuantityKit).sum();
        }
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("item", item);
        result.put("totalValue", total);
        result.put("stockAvailable", avail);
        result.put("stockSufficient", avail >= item.getQty1());
        return result;
    }

    // ==================== PI Reimport ====================
    @Transactional
    public Map<String, Object> reimportPI(Long piId, MultipartFile file) throws IOException {
        Workbook wb = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = wb.getSheetAt(0);
        ProformaInvoice pi = piMapper.selectById(piId);
        List<ProformaInvoiceItem> current = piItemMapper.selectList(
            new LambdaQueryWrapper<ProformaInvoiceItem>().eq(ProformaInvoiceItem::getPiId, piId));

        // Find data start
        int dataStart = -1;
        for (int i = 0; i < Math.min(30, sheet.getLastRowNum()); i++) {
            Row r = sheet.getRow(i); if (r == null) continue;
            if (getRowText(r, 8).contains("NO.") && getRowText(r, 8).contains("Order")) { dataStart = i + 1; break; }
        }
        if (dataStart == -1) dataStart = 17;

        Map<Integer, ProformaInvoiceItem> currMap = new LinkedHashMap<>();
        for (ProformaInvoiceItem item : current) currMap.put(item.getLineNo(), item);

        List<Map<String, Object>> changes = new ArrayList<>();
        Set<Integer> importedLines = new HashSet<>();

        for (int i = dataStart; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i); if (row == null || isRowEmpty(row, 8)) continue;
            int lineNo = getCellInt(row, 0);
            importedLines.add(lineNo);
            ProformaInvoiceItem existing = currMap.get(lineNo);
            if (existing == null) {
                changes.add(Map.of("lineNo", lineNo, "type", "ADDED", "field", "ALL"));
            } else {
                // Compare fields
                int newQty = getCellInt(row, 4);
                String newRef = getCellStr(row, 3);
                if (newQty != existing.getQty1()) changes.add(Map.of("lineNo", lineNo, "type", "MODIFIED", "field", "qty1", "oldValue", String.valueOf(existing.getQty1()), "newValue", String.valueOf(newQty)));
                if (!Objects.equals(newRef, existing.getRefNo())) changes.add(Map.of("lineNo", lineNo, "type", "MODIFIED", "field", "refNo", "oldValue", String.valueOf(existing.getRefNo()), "newValue", String.valueOf(newRef)));
            }
        }
        // Deleted rows
        for (Integer ln : currMap.keySet()) {
            if (!importedLines.contains(ln)) changes.add(Map.of("lineNo", ln, "type", "DELETED", "field", "ALL"));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("piId", piId);
        result.put("changes", changes);
        result.put("summary", Map.of("modifiedCount", changes.stream().filter(c -> "MODIFIED".equals(c.get("type"))).count(),
            "addedCount", changes.stream().filter(c -> "ADDED".equals(c.get("type"))).count(),
            "deletedCount", changes.stream().filter(c -> "DELETED".equals(c.get("type"))).count()));
        return result;
    }

    // ==================== PL Generation ====================
    @Transactional
    public Map<String, Object> generatePL(Long piId) {
        ProformaInvoice pi = piMapper.selectById(piId);
        List<ProformaInvoiceItem> piItems = piItemMapper.selectList(
            new LambdaQueryWrapper<ProformaInvoiceItem>().eq(ProformaInvoiceItem::getPiId, piId));
        Customer customer = customerMapper.selectById(pi.getCustomerId());

        PackingList pl = new PackingList();
        pl.setPiId(piId); pl.setContractNo(pi.getContractNo()); pl.setCustomerId(pi.getCustomerId());
        pl.setShipTo(customer.getShipToAddress()); pl.setTransportMethod(pi.getTransportMethod());
        pl.setTransportRoute("Beijing to Italy"); pl.setCountryOfOrigin(pi.getCountryOfOrigin());
        pl.setPoReference(pi.getPoReference()); pl.setStatus("DRAFT");
        plMapper.insert(pl);

        int globalCartonNo = 0;
        BigDecimal totalVol = BigDecimal.ZERO, totalNet = BigDecimal.ZERO, totalGross = BigDecimal.ZERO;
        int totalCartons = 0;
        List<PackingListItem> plItems = new ArrayList<>();

        for (int i = 0; i < piItems.size(); i++) {
            ProformaInvoiceItem pii = piItems.get(i);
            Product product = pii.getProductId() != null ? productMapper.selectById(pii.getProductId()) : null;

            PackingListItem pli = new PackingListItem();
            pli.setPlId(pl.getId()); pli.setLineNo(i + 1); pli.setCustomerPoNo(pii.getCustomerPoNo());
            pli.setProductId(pii.getProductId()); pli.setCatNo(pii.getCatNo()); pli.setRefNo(pii.getRefNo());
            pli.setDescription(pii.getDescription()); pli.setQty1(pii.getQty1()); pli.setUnit1(pii.getUnit1());
            pli.setQty2(pii.getQty2()); pli.setUnit2(pii.getUnit2());

            int totalUnits = pii.getQty1();
            int cartonCount; String dimension; BigDecimal singleVol;
            if ("unit".equals(pii.getUnit1())) {
                cartonCount = totalUnits; dimension = "57*45*55cm"; singleVol = new BigDecimal("0.141075");
            } else {
                int full = totalUnits / 6, rem = totalUnits % 6;
                cartonCount = full;
                if (rem > 0) {
                    cartonCount++;
                    if (rem < 4) { dimension = "35*38*26cm"; singleVol = new BigDecimal("0.03458"); }
                    else { dimension = "57*26*37cm"; singleVol = new BigDecimal("0.054834"); }
                } else { dimension = "57*26*37cm"; singleVol = new BigDecimal("0.054834"); }
            }
            String cartonNoRange;
            if (cartonCount == 1) { globalCartonNo++; cartonNoRange = String.valueOf(globalCartonNo); }
            else { int s = globalCartonNo + 1; globalCartonNo += cartonCount; cartonNoRange = s + "-" + globalCartonNo; }

            BigDecimal rowVol = singleVol.multiply(BigDecimal.valueOf(cartonCount));
            BigDecimal rowNet = product != null && product.getNetWeightKg() != null ? product.getNetWeightKg().multiply(BigDecimal.valueOf(totalUnits)) : BigDecimal.ZERO;
            BigDecimal rowGross = product != null && product.getGrossWeightKg() != null ? product.getGrossWeightKg().multiply(BigDecimal.valueOf(totalUnits)) : rowNet.multiply(new BigDecimal("1.15"));

            pli.setDimension(dimension); pli.setQtyCarton(cartonCount); pli.setCartonNoRange(cartonNoRange);
            pli.setVolumeCbm(rowVol); pli.setNetWeightKg(rowNet); pli.setGrossWeightKg(rowGross);
            if (product != null) { pli.setDgmNameCn(product.getDgmNameCn()); pli.setDgmCode(product.getDgmCode()); }

            // Auto-assign lot
            List<Inventory> lots = inventoryMapper.selectList(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, pii.getProductId()).ge(Inventory::getQuantityKit, pii.getQty1())
                .orderByAsc(Inventory::getExpiryDate));
            if (!lots.isEmpty()) { pli.setLotNo(lots.get(0).getLotNo()); pli.setExpiryDate(lots.get(0).getExpiryDate()); }

            totalVol = totalVol.add(rowVol); totalNet = totalNet.add(rowNet); totalGross = totalGross.add(rowGross);
            totalCartons += cartonCount;
            plItems.add(pli);
        }
        for (PackingListItem item : plItems) plItemMapper.insert(item);

        pl.setTotalVolumeCbm(totalVol); pl.setTotalNetWeightKg(totalNet); pl.setTotalGrossWeightKg(totalGross);
        pl.setTotalCartons(totalCartons); pl.setStatus("APPROVED");
        plMapper.updateById(pl);

        pi.setStatus("PACKING_GENERATED"); piMapper.updateById(pi);
        PurchaseOrder po = poMapper.selectById(pi.getPoId());
        po.setStatus("COMPLETED"); poMapper.updateById(po);

        return buildPLResponse(pl, plItems);
    }

    public Map<String, Object> getPLDetail(Long plId) {
        PackingList pl = plMapper.selectById(plId);
        List<PackingListItem> items = plItemMapper.selectList(
            new LambdaQueryWrapper<PackingListItem>().eq(PackingListItem::getPlId, plId));
        return buildPLResponse(pl, items);
    }

    public List<PurchaseOrder> getPOList(Long customerId) {
        LambdaQueryWrapper<PurchaseOrder> q = new LambdaQueryWrapper<PurchaseOrder>();
        if (customerId != null) q.eq(PurchaseOrder::getCustomerId, customerId);
        q.orderByDesc(PurchaseOrder::getCreatedAt);
        return poMapper.selectList(q);
    }

    public Map<String, Object> getPODetail(Long poId) {
        PurchaseOrder po = poMapper.selectById(poId);
        List<PurchaseOrderItem> items = poItemMapper.selectList(
            new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getPoId, poId));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("po", po); m.put("items", items);
        return m;
    }

    public List<ProformaInvoice> getPIList(Long customerId) {
        LambdaQueryWrapper<ProformaInvoice> q = new LambdaQueryWrapper<ProformaInvoice>();
        if (customerId != null) q.eq(ProformaInvoice::getCustomerId, customerId);
        q.orderByDesc(ProformaInvoice::getCreatedAt);
        return piMapper.selectList(q);
    }

    public List<PackingList> getPLList(Long customerId) {
        LambdaQueryWrapper<PackingList> q = new LambdaQueryWrapper<PackingList>();
        if (customerId != null) q.eq(PackingList::getCustomerId, customerId);
        q.orderByDesc(PackingList::getCreatedAt);
        return plMapper.selectList(q);
    }

    // ==================== Excel Export ====================
    public void exportPI(Long piId, HttpServletResponse response) throws IOException {
        ProformaInvoice pi = piMapper.selectById(piId);
        List<ProformaInvoiceItem> items = piItemMapper.selectList(
            new LambdaQueryWrapper<ProformaInvoiceItem>().eq(ProformaInvoiceItem::getPiId, piId));
        Customer c = customerMapper.selectById(pi.getCustomerId());

        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Invoice");
        CellStyle headerStyle = wb.createCellStyle(); headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        int r = 0;

        // Title
        createCell(sheet, r++, 0, "Coyote Bioscience Co., Ltd.");
        createCell(sheet, r++, 0, "Commercial Invoice"); r++;

        // Header info
        createCell(sheet, r, 0, "Bill To:"); createCell(sheet, r, 4, "Contract #: " + pi.getContractNo()); r++;
        createCell(sheet, r, 0, c.getCompanyName()); createCell(sheet, r, 4, "Invoice #: " + pi.getInvoiceNo()); r++;
        createCell(sheet, r, 0, "Ship To:"); createCell(sheet, r, 4, "Date: " + pi.getInvoiceDate()); r++;
        createCell(sheet, r, 0, c.getShipToAddress()); createCell(sheet, r, 4, "Delivery: " + pi.getDeliveryTerms()); r++;
        r++; createCell(sheet, r++, 4, "Payment: " + pi.getPaymentTerms());
        createCell(sheet, r++, 4, "Transport: " + pi.getTransportMethod());
        createCell(sheet, r++, 4, "Origin: " + pi.getCountryOfOrigin());
        createCell(sheet, r++, 4, "PO: " + (pi.getPoReference() != null ? pi.getPoReference() : "")); r++;

        // Column headers
        String[] cols = {"NO.", "Order #", "CAT. #", "Ref No.", "DESCRIPTION", "QTY-1", "UNIT-1", "QTY-2", "UNIT-2", "UNIT PRICE", "Discount", "VALUE"};
        Row hRow = sheet.createRow(r++);
        for (int i = 0; i < cols.length; i++) { org.apache.poi.ss.usermodel.Cell cell = hRow.createCell(i); cell.setCellValue(cols[i]); cell.setCellStyle(headerStyle); }

        for (ProformaInvoiceItem item : items) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(item.getLineNo()); row.createCell(1).setCellValue(item.getCustomerPoNo());
            row.createCell(2).setCellValue(item.getCatNo()); row.createCell(3).setCellValue(item.getRefNo());
            row.createCell(4).setCellValue(item.getDescription()); row.createCell(5).setCellValue(item.getQty1());
            row.createCell(6).setCellValue(item.getUnit1()); row.createCell(7).setCellValue(item.getQty2());
            row.createCell(8).setCellValue(item.getUnit2()); row.createCell(9).setCellValue(item.getUnitPriceMultiplier() != null ? item.getUnitPriceMultiplier().toString() : "");
            row.createCell(10).setCellValue(item.getDiscountFlag()); row.createCell(11).setCellValue(item.getLineValue() != null ? item.getLineValue().toString() : "");
        }
        r++; createCell(sheet, r, 0, "Total Value: " + (pi.getTotalValue() != null ? pi.getTotalValue().toString() : ""));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Invoice_" + pi.getContractNo() + "_" + pi.getInvoiceDate() + ".xlsx");
        wb.write(response.getOutputStream()); wb.close();
    }

    public void exportPL(Long plId, HttpServletResponse response) throws IOException {
        PackingList pl = plMapper.selectById(plId);
        List<PackingListItem> items = plItemMapper.selectList(
            new LambdaQueryWrapper<PackingListItem>().eq(PackingListItem::getPlId, plId));
        Customer c = customerMapper.selectById(pl.getCustomerId());

        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Packing List");
        int r = 0;
        createCell(sheet, r++, 0, "Coyote Bioscience Co., Ltd.");
        createCell(sheet, r++, 0, "Packing List"); r++;
        createCell(sheet, r, 0, "Ship To:"); createCell(sheet, r, 4, "Contract #: " + pl.getContractNo()); r++;
        createCell(sheet, r, 0, c.getCompanyName()); createCell(sheet, r, 4, "Transport: " + pl.getTransportMethod()); r++;
        createCell(sheet, r, 0, c.getShipToAddress()); createCell(sheet, r, 4, "Origin: " + pl.getCountryOfOrigin()); r++;
        r++; createCell(sheet, r++, 4, "Route: " + pl.getTransportRoute());

        String[] cols = {"NO.", "Order #", "CAT. #", "Ref No.", "DESCRIPTION", "QTY-1", "UNIT-1", "QTY-2", "UNIT-2", "Lot NO.", "Expiry date", "DIMENSION", "Qty. of Carton", "Carton #", "VOLUME CBM", "NET WEIGHT/KG", "GROSS WEIGHT/KG", "DGM", "DGM Code"};
        Row hRow = sheet.createRow(r++);
        for (int i = 0; i < cols.length; i++) { org.apache.poi.ss.usermodel.Cell cell = hRow.createCell(i); cell.setCellValue(cols[i]); }

        for (PackingListItem item : items) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(item.getLineNo()); row.createCell(1).setCellValue(item.getCustomerPoNo());
            row.createCell(2).setCellValue(item.getCatNo()); row.createCell(3).setCellValue(item.getRefNo());
            row.createCell(4).setCellValue(item.getDescription()); row.createCell(5).setCellValue(item.getQty1());
            row.createCell(6).setCellValue(item.getUnit1()); row.createCell(7).setCellValue(item.getQty2());
            row.createCell(8).setCellValue(item.getUnit2()); row.createCell(9).setCellValue(item.getLotNo());
            row.createCell(10).setCellValue(item.getExpiryDate() != null ? item.getExpiryDate().toString() : "");
            row.createCell(11).setCellValue(item.getDimension()); row.createCell(12).setCellValue(item.getQtyCarton());
            row.createCell(13).setCellValue(item.getCartonNoRange()); row.createCell(14).setCellValue(item.getVolumeCbm() != null ? item.getVolumeCbm().toString() : "");
            row.createCell(15).setCellValue(item.getNetWeightKg() != null ? item.getNetWeightKg().toString() : "");
            row.createCell(16).setCellValue(item.getGrossWeightKg() != null ? item.getGrossWeightKg().toString() : "");
            row.createCell(17).setCellValue(item.getDgmNameCn()); row.createCell(18).setCellValue(item.getDgmCode());
        }
        r++; createCell(sheet, r, 0, "TOTAL: " + pl.getTotalCartons() + " Cartons | " + pl.getTotalVolumeCbm() + " CBM | " + pl.getTotalNetWeightKg() + " KG Net | " + pl.getTotalGrossWeightKg() + " KG Gross");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=PackingList_" + pl.getContractNo() + "_" + LocalDate.now() + ".xlsx");
        wb.write(response.getOutputStream()); wb.close();
    }

    // ==================== Helpers ====================
    private String getCellStr(Row row, int col) {
        org.apache.poi.ss.usermodel.Cell c = row.getCell(col); if (c == null) return null;
        return c.toString().trim();
    }
    private int getCellInt(Row row, int col) {
        try { org.apache.poi.ss.usermodel.Cell c = row.getCell(col); if (c == null) return 0; return (int) c.getNumericCellValue(); } catch (Exception e) { try { return Integer.parseInt(getCellStr(row, col)); } catch (Exception e2) { return 0; } }
    }
    private String getRowText(Row row, int maxCol) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxCol; i++) { org.apache.poi.ss.usermodel.Cell c = row.getCell(i); if (c != null) sb.append(c.toString()).append(" "); }
        return sb.toString();
    }
    private boolean isRowEmpty(Row row, int maxCol) { return getRowText(row, maxCol).trim().isEmpty(); }
    private BigDecimal parseBigDecimal(String s) { try { return new BigDecimal(s); } catch (Exception e) { return BigDecimal.ONE; } }
    private String extractAfterColon(Row row, int col) {
        String s = getCellStr(row, col); if (s == null) return "";
        int idx = s.indexOf(":"); return idx >= 0 ? s.substring(idx + 1).trim() : s.trim();
    }
    private String extractValue(String s) { if (s == null) return "0"; return s.replaceAll("[^0-9.]", ""); }
    private void createCell(Sheet sheet, int row, int col, String value) {
        Row r = sheet.getRow(row); if (r == null) r = sheet.createRow(row);
        org.apache.poi.ss.usermodel.Cell c = r.createCell(col); c.setCellValue(value != null ? value : "");
    }
    private Map<String, Object> buildPIResponse(ProformaInvoice pi, List<ProformaInvoiceItem> items) {
        Map<String, Object> m = new LinkedHashMap<>(); m.put("pi", pi); m.put("items", items);
        if (pi.getCustomerId() != null) m.put("customer", customerMapper.selectById(pi.getCustomerId()));
        return m;
    }
    private Map<String, Object> buildPLResponse(PackingList pl, List<PackingListItem> items) {
        Map<String, Object> m = new LinkedHashMap<>(); m.put("pl", pl); m.put("items", items);
        if (pl.getCustomerId() != null) m.put("customer", customerMapper.selectById(pl.getCustomerId()));
        return m;
    }
}
