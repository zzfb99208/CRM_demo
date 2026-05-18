package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;

@TableName("purchase_order_item")
public class PurchaseOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long poId;
    private Integer lineNo;
    private String customerPoNo;
    private Long productId;
    private String catNo;
    private String description;
    private Integer qty1;
    private String unit1;
    private Integer qty2;
    private String unit2;
    private BigDecimal unitPriceMultiplier;
    private Integer discountFlag;
    private BigDecimal lineValue;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getPoId() { return poId; } public void setPoId(Long poId) { this.poId = poId; }
    public Integer getLineNo() { return lineNo; } public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public String getCustomerPoNo() { return customerPoNo; } public void setCustomerPoNo(String customerPoNo) { this.customerPoNo = customerPoNo; }
    public Long getProductId() { return productId; } public void setProductId(Long productId) { this.productId = productId; }
    public String getCatNo() { return catNo; } public void setCatNo(String catNo) { this.catNo = catNo; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public Integer getQty1() { return qty1; } public void setQty1(Integer qty1) { this.qty1 = qty1; }
    public String getUnit1() { return unit1; } public void setUnit1(String unit1) { this.unit1 = unit1; }
    public Integer getQty2() { return qty2; } public void setQty2(Integer qty2) { this.qty2 = qty2; }
    public String getUnit2() { return unit2; } public void setUnit2(String unit2) { this.unit2 = unit2; }
    public BigDecimal getUnitPriceMultiplier() { return unitPriceMultiplier; } public void setUnitPriceMultiplier(BigDecimal unitPriceMultiplier) { this.unitPriceMultiplier = unitPriceMultiplier; }
    public Integer getDiscountFlag() { return discountFlag; } public void setDiscountFlag(Integer discountFlag) { this.discountFlag = discountFlag; }
    public BigDecimal getLineValue() { return lineValue; } public void setLineValue(BigDecimal lineValue) { this.lineValue = lineValue; }
}
