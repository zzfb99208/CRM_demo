package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*; import java.math.BigDecimal; import java.time.LocalDate;

@TableName("packing_list_item")
public class PackingListItem {
    @TableId(type = IdType.AUTO) private Long id;
    private Long plId; private Integer lineNo; private String customerPoNo; private Long productId;
    private String catNo; private String refNo; private String description;
    private Integer qty1; private String unit1; private Integer qty2; private String unit2;
    private String lotNo; private LocalDate expiryDate; private String dimension;
    private Integer qtyCarton; private String cartonNoRange; private BigDecimal volumeCbm;
    private BigDecimal netWeightKg; private BigDecimal grossWeightKg;
    private String dgmNameCn; private String dgmCode;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getPlId() { return plId; } public void setPlId(Long v) { this.plId = v; }
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
    public String getLotNo() { return lotNo; } public void setLotNo(String v) { this.lotNo = v; }
    public LocalDate getExpiryDate() { return expiryDate; } public void setExpiryDate(LocalDate v) { this.expiryDate = v; }
    public String getDimension() { return dimension; } public void setDimension(String v) { this.dimension = v; }
    public Integer getQtyCarton() { return qtyCarton; } public void setQtyCarton(Integer v) { this.qtyCarton = v; }
    public String getCartonNoRange() { return cartonNoRange; } public void setCartonNoRange(String v) { this.cartonNoRange = v; }
    public BigDecimal getVolumeCbm() { return volumeCbm; } public void setVolumeCbm(BigDecimal v) { this.volumeCbm = v; }
    public BigDecimal getNetWeightKg() { return netWeightKg; } public void setNetWeightKg(BigDecimal v) { this.netWeightKg = v; }
    public BigDecimal getGrossWeightKg() { return grossWeightKg; } public void setGrossWeightKg(BigDecimal v) { this.grossWeightKg = v; }
    public String getDgmNameCn() { return dgmNameCn; } public void setDgmNameCn(String v) { this.dgmNameCn = v; }
    public String getDgmCode() { return dgmCode; } public void setDgmCode(String v) { this.dgmCode = v; }
}
