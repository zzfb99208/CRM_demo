package com.crm.demo.dto;

import java.math.BigDecimal;
import java.util.*;

public class PoImportResult {
    private Long poId;
    private String contractNo;
    private Long customerId;
    private String deliveryTerms;
    private String paymentTerms;
    private String transportMethod;
    private String countryOfOrigin;
    private String poReference;
    private List<PoItemRow> items = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private int totalLines;
    private int totalKits;
    private BigDecimal totalValue;

    public Long getPoId() { return poId; } public void setPoId(Long v) { this.poId = v; }
    public String getContractNo() { return contractNo; } public void setContractNo(String v) { this.contractNo = v; }
    public Long getCustomerId() { return customerId; } public void setCustomerId(Long v) { this.customerId = v; }
    public String getDeliveryTerms() { return deliveryTerms; } public void setDeliveryTerms(String v) { this.deliveryTerms = v; }
    public String getPaymentTerms() { return paymentTerms; } public void setPaymentTerms(String v) { this.paymentTerms = v; }
    public String getTransportMethod() { return transportMethod; } public void setTransportMethod(String v) { this.transportMethod = v; }
    public String getCountryOfOrigin() { return countryOfOrigin; } public void setCountryOfOrigin(String v) { this.countryOfOrigin = v; }
    public String getPoReference() { return poReference; } public void setPoReference(String v) { this.poReference = v; }
    public List<PoItemRow> getItems() { return items; } public void setItems(List<PoItemRow> v) { this.items = v; }
    public List<String> getWarnings() { return warnings; } public void setWarnings(List<String> v) { this.warnings = v; }
    public int getTotalLines() { return totalLines; } public void setTotalLines(int v) { this.totalLines = v; }
    public int getTotalKits() { return totalKits; } public void setTotalKits(int v) { this.totalKits = v; }
    public BigDecimal getTotalValue() { return totalValue; } public void setTotalValue(BigDecimal v) { this.totalValue = v; }

    public static class PoItemRow {
        private int lineNo;
        private String customerPoNo;
        private String catNo;
        private String description;
        private int qty1;
        private String unit1;
        private int qty2;
        private String unit2;
        private BigDecimal unitPriceMultiplier;
        private int discountFlag;
        private BigDecimal lineValue;
        private Long productId;
        private boolean productMatched;
        private String warning;

        public int getLineNo() { return lineNo; } public void setLineNo(int v) { this.lineNo = v; }
        public String getCustomerPoNo() { return customerPoNo; } public void setCustomerPoNo(String v) { this.customerPoNo = v; }
        public String getCatNo() { return catNo; } public void setCatNo(String v) { this.catNo = v; }
        public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
        public int getQty1() { return qty1; } public void setQty1(int v) { this.qty1 = v; }
        public String getUnit1() { return unit1; } public void setUnit1(String v) { this.unit1 = v; }
        public int getQty2() { return qty2; } public void setQty2(int v) { this.qty2 = v; }
        public String getUnit2() { return unit2; } public void setUnit2(String v) { this.unit2 = v; }
        public BigDecimal getUnitPriceMultiplier() { return unitPriceMultiplier; } public void setUnitPriceMultiplier(BigDecimal v) { this.unitPriceMultiplier = v; }
        public int getDiscountFlag() { return discountFlag; } public void setDiscountFlag(int v) { this.discountFlag = v; }
        public BigDecimal getLineValue() { return lineValue; } public void setLineValue(BigDecimal v) { this.lineValue = v; }
        public Long getProductId() { return productId; } public void setProductId(Long v) { this.productId = v; }
        public boolean isProductMatched() { return productMatched; } public void setProductMatched(boolean v) { this.productMatched = v; }
        public String getWarning() { return warning; } public void setWarning(String v) { this.warning = v; }
    }
}
