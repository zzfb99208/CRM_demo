package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*; import java.math.BigDecimal;

@TableName("proforma_invoice_item")
public class ProformaInvoiceItem {
    @TableId(type = IdType.AUTO) private Long id;
    private Long piId; private Integer lineNo; private String customerPoNo; private Long productId;
    private String catNo; private String refNo; private String description;
    private Integer qty1; private String unit1; private Integer qty2; private String unit2;
    private BigDecimal unitPriceMultiplier; private Integer discountFlag; private BigDecimal lineValue;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getPiId() { return piId; } public void setPiId(Long v) { this.piId = v; }
    public Integer getLineNo() { return lineNo; } public void setLineNo(Integer v) { this.lineNo = v; }
    public String getCustomerPoNo() { return customerPoNo; } public void setCustomerPoNo(String v) { this.customerPoNo = v; }
    public Long getProductId() { return productId; } public void setProductId(Long v) { this.productId = v; }
    public String getCatNo() { return catNo; } public void setCatNo(String v) { this.catNo = v; }
    public String getRefNo() { return refNo; } public void setRefNo(String v) { this.refNo = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public Integer getQty1() { return qty1; } public void setQty1(Integer v) { this.qty1 = v; }
    public String getUnit1() { return unit1; } public void setUnit1(String v) { this.unit1 = v; }
    public Integer getQty2() { return qty2; } public void setQty2(Integer v) { this.qty2 = v; }
    public String getUnit2() { return unit2; } public void setUnit2(String v) { this.unit2 = v; }
    public BigDecimal getUnitPriceMultiplier() { return unitPriceMultiplier; } public void setUnitPriceMultiplier(BigDecimal v) { this.unitPriceMultiplier = v; }
    public Integer getDiscountFlag() { return discountFlag; } public void setDiscountFlag(Integer v) { this.discountFlag = v; }
    public BigDecimal getLineValue() { return lineValue; } public void setLineValue(BigDecimal v) { this.lineValue = v; }
}
