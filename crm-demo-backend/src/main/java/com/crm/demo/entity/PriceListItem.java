package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;

@TableName("price_list_item")
public class PriceListItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long priceListId;
    private Long productId;
    private String prodCode;
    private String prodName;
    private String unitOfMeasure;
    private BigDecimal listPrice;
    private BigDecimal marketPrice;
    private String priceDesc;
    private String status;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getPriceListId() { return priceListId; } public void setPriceListId(Long v) { this.priceListId = v; }
    public Long getProductId() { return productId; } public void setProductId(Long v) { this.productId = v; }
    public String getProdCode() { return prodCode; } public void setProdCode(String v) { this.prodCode = v; }
    public String getProdName() { return prodName; } public void setProdName(String v) { this.prodName = v; }
    public String getUnitOfMeasure() { return unitOfMeasure; } public void setUnitOfMeasure(String v) { this.unitOfMeasure = v; }
    public BigDecimal getListPrice() { return listPrice; } public void setListPrice(BigDecimal v) { this.listPrice = v; }
    public BigDecimal getMarketPrice() { return marketPrice; } public void setMarketPrice(BigDecimal v) { this.marketPrice = v; }
    public String getPriceDesc() { return priceDesc; } public void setPriceDesc(String v) { this.priceDesc = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
}
