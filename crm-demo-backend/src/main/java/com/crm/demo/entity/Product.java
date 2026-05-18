package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String menariniCode;
    private String coyoteCode;
    private String productName;
    private String description;
    private String category;
    private BigDecimal unitPriceKit;
    private BigDecimal unitPriceTest;
    private String dimension;
    private BigDecimal netWeightKg;
    private BigDecimal grossWeightKg;
    private String dgmNameCn;
    private String dgmCode;
    private Integer isActive;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getMenariniCode() { return menariniCode; } public void setMenariniCode(String v) { this.menariniCode = v; }
    public String getCoyoteCode() { return coyoteCode; } public void setCoyoteCode(String v) { this.coyoteCode = v; }
    public String getProductName() { return productName; } public void setProductName(String v) { this.productName = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getCategory() { return category; } public void setCategory(String v) { this.category = v; }
    public BigDecimal getUnitPriceKit() { return unitPriceKit; } public void setUnitPriceKit(BigDecimal v) { this.unitPriceKit = v; }
    public BigDecimal getUnitPriceTest() { return unitPriceTest; } public void setUnitPriceTest(BigDecimal v) { this.unitPriceTest = v; }
    public String getDimension() { return dimension; } public void setDimension(String v) { this.dimension = v; }
    public BigDecimal getNetWeightKg() { return netWeightKg; } public void setNetWeightKg(BigDecimal v) { this.netWeightKg = v; }
    public BigDecimal getGrossWeightKg() { return grossWeightKg; } public void setGrossWeightKg(BigDecimal v) { this.grossWeightKg = v; }
    public String getDgmNameCn() { return dgmNameCn; } public void setDgmNameCn(String v) { this.dgmNameCn = v; }
    public String getDgmCode() { return dgmCode; } public void setDgmCode(String v) { this.dgmCode = v; }
    public Integer getIsActive() { return isActive; } public void setIsActive(Integer v) { this.isActive = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { this.notes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
