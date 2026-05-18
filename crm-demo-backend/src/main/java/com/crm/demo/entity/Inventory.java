package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*; import java.time.LocalDate; import java.time.LocalDateTime;

@TableName("inventory")
public class Inventory {
    @TableId(type = IdType.AUTO) private Long id;
    private Long productId; private String lotNo; private LocalDate expiryDate;
    private Integer quantityKit; private Integer quantityTest; private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getProductId() { return productId; } public void setProductId(Long v) { this.productId = v; }
    public String getLotNo() { return lotNo; } public void setLotNo(String v) { this.lotNo = v; }
    public LocalDate getExpiryDate() { return expiryDate; } public void setExpiryDate(LocalDate v) { this.expiryDate = v; }
    public Integer getQuantityKit() { return quantityKit; } public void setQuantityKit(Integer v) { this.quantityKit = v; }
    public Integer getQuantityTest() { return quantityTest; } public void setQuantityTest(Integer v) { this.quantityTest = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
