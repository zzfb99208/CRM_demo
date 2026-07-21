package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String menariniCode;
    private String coyoteCode;
    private String prodCode;
    private String productName;
    private String description;
    private String category;
    private String categoryL1;
    private String categoryL2;
    private String prodType;
    private String productType;
    private String bizType;
    private BigDecimal unitPriceKit;
    private BigDecimal unitPriceTest;
    private String currency;
    private String unitOfMeasure;
    private String specTestPerUnit;
    private Integer testsPerUnit;
    private Integer minOrderQty;
    private String modelSpec;
    private BigDecimal marketPriceCny;
    private BigDecimal marketPriceUsd;
    private BigDecimal standardPriceLocal;
    private BigDecimal floorPriceUsd;
    private BigDecimal taxRate;
    private BigDecimal exchangeRate;
    private String priceDesc;
    private Integer isBundle;
    private Integer canSellAlone;
    private String hasConfig;
    private String configTiming;
    private String needRetrieve;
    private Integer canBeAsset;
    private String canDeploy;
    private String canTrial;
    private String isConsumable;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private String prodImage;
    private String prodDesc;
    private String prodCatalog;
    private Long prodOwner;
    private Long deptOwner;
    private String prodMaterialCategory;
    private String prodMaterialClass;
    private String sellableCompany1;
    private String sellableCompany2;
    private String importCode;
    private String marketLevel3Tags;
    private String dimension;
    private BigDecimal netWeightKg;
    private BigDecimal grossWeightKg;
    private String dgmNameCn;
    private String dgmCode;
    private Integer isActive;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getMenariniCode() { return menariniCode; } public void setMenariniCode(String v) { this.menariniCode = v; }
    public String getCoyoteCode() { return coyoteCode; } public void setCoyoteCode(String v) { this.coyoteCode = v; }
    public String getProdCode() { return prodCode; } public void setProdCode(String v) { this.prodCode = v; }
    public String getProductName() { return productName; } public void setProductName(String v) { this.productName = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getCategory() { return category; } public void setCategory(String v) { this.category = v; }
    public String getCategoryL1() { return categoryL1; } public void setCategoryL1(String v) { this.categoryL1 = v; }
    public String getCategoryL2() { return categoryL2; } public void setCategoryL2(String v) { this.categoryL2 = v; }
    public String getProdType() { return prodType; } public void setProdType(String v) { this.prodType = v; }
    public String getProductType() { return productType; } public void setProductType(String v) { this.productType = v; }
    public String getBizType() { return bizType; } public void setBizType(String v) { this.bizType = v; }
    public BigDecimal getUnitPriceKit() { return unitPriceKit; } public void setUnitPriceKit(BigDecimal v) { this.unitPriceKit = v; }
    public BigDecimal getUnitPriceTest() { return unitPriceTest; } public void setUnitPriceTest(BigDecimal v) { this.unitPriceTest = v; }
    public String getCurrency() { return currency; } public void setCurrency(String v) { this.currency = v; }
    public String getUnitOfMeasure() { return unitOfMeasure; } public void setUnitOfMeasure(String v) { this.unitOfMeasure = v; }
    public String getSpecTestPerUnit() { return specTestPerUnit; } public void setSpecTestPerUnit(String v) { this.specTestPerUnit = v; }
    public Integer getTestsPerUnit() { return testsPerUnit; } public void setTestsPerUnit(Integer v) { this.testsPerUnit = v; }
    public Integer getMinOrderQty() { return minOrderQty; } public void setMinOrderQty(Integer v) { this.minOrderQty = v; }
    public String getModelSpec() { return modelSpec; } public void setModelSpec(String v) { this.modelSpec = v; }
    public BigDecimal getMarketPriceCny() { return marketPriceCny; } public void setMarketPriceCny(BigDecimal v) { this.marketPriceCny = v; }
    public BigDecimal getMarketPriceUsd() { return marketPriceUsd; } public void setMarketPriceUsd(BigDecimal v) { this.marketPriceUsd = v; }
    public BigDecimal getStandardPriceLocal() { return standardPriceLocal; } public void setStandardPriceLocal(BigDecimal v) { this.standardPriceLocal = v; }
    public BigDecimal getFloorPriceUsd() { return floorPriceUsd; } public void setFloorPriceUsd(BigDecimal v) { this.floorPriceUsd = v; }
    public BigDecimal getTaxRate() { return taxRate; } public void setTaxRate(BigDecimal v) { this.taxRate = v; }
    public BigDecimal getExchangeRate() { return exchangeRate; } public void setExchangeRate(BigDecimal v) { this.exchangeRate = v; }
    public String getPriceDesc() { return priceDesc; } public void setPriceDesc(String v) { this.priceDesc = v; }
    public Integer getIsBundle() { return isBundle; } public void setIsBundle(Integer v) { this.isBundle = v; }
    public Integer getCanSellAlone() { return canSellAlone; } public void setCanSellAlone(Integer v) { this.canSellAlone = v; }
    public String getHasConfig() { return hasConfig; } public void setHasConfig(String v) { this.hasConfig = v; }
    public String getConfigTiming() { return configTiming; } public void setConfigTiming(String v) { this.configTiming = v; }
    public String getNeedRetrieve() { return needRetrieve; } public void setNeedRetrieve(String v) { this.needRetrieve = v; }
    public Integer getCanBeAsset() { return canBeAsset; } public void setCanBeAsset(Integer v) { this.canBeAsset = v; }
    public String getCanDeploy() { return canDeploy; } public void setCanDeploy(String v) { this.canDeploy = v; }
    public String getCanTrial() { return canTrial; } public void setCanTrial(String v) { this.canTrial = v; }
    public String getIsConsumable() { return isConsumable; } public void setIsConsumable(String v) { this.isConsumable = v; }
    public LocalDate getEffectiveDate() { return effectiveDate; } public void setEffectiveDate(LocalDate v) { this.effectiveDate = v; }
    public LocalDate getExpirationDate() { return expirationDate; } public void setExpirationDate(LocalDate v) { this.expirationDate = v; }
    public String getProdImage() { return prodImage; } public void setProdImage(String v) { this.prodImage = v; }
    public String getProdDesc() { return prodDesc; } public void setProdDesc(String v) { this.prodDesc = v; }
    public String getProdCatalog() { return prodCatalog; } public void setProdCatalog(String v) { this.prodCatalog = v; }
    public Long getProdOwner() { return prodOwner; } public void setProdOwner(Long v) { this.prodOwner = v; }
    public Long getDeptOwner() { return deptOwner; } public void setDeptOwner(Long v) { this.deptOwner = v; }
    public String getProdMaterialCategory() { return prodMaterialCategory; } public void setProdMaterialCategory(String v) { this.prodMaterialCategory = v; }
    public String getProdMaterialClass() { return prodMaterialClass; } public void setProdMaterialClass(String v) { this.prodMaterialClass = v; }
    public String getSellableCompany1() { return sellableCompany1; } public void setSellableCompany1(String v) { this.sellableCompany1 = v; }
    public String getSellableCompany2() { return sellableCompany2; } public void setSellableCompany2(String v) { this.sellableCompany2 = v; }
    public String getImportCode() { return importCode; } public void setImportCode(String v) { this.importCode = v; }
    public String getMarketLevel3Tags() { return marketLevel3Tags; } public void setMarketLevel3Tags(String v) { this.marketLevel3Tags = v; }
    public String getDimension() { return dimension; } public void setDimension(String v) { this.dimension = v; }
    public BigDecimal getNetWeightKg() { return netWeightKg; } public void setNetWeightKg(BigDecimal v) { this.netWeightKg = v; }
    public BigDecimal getGrossWeightKg() { return grossWeightKg; } public void setGrossWeightKg(BigDecimal v) { this.grossWeightKg = v; }
    public String getDgmNameCn() { return dgmNameCn; } public void setDgmNameCn(String v) { this.dgmNameCn = v; }
    public String getDgmCode() { return dgmCode; } public void setDgmCode(String v) { this.dgmCode = v; }
    public Integer getIsActive() { return isActive; } public void setIsActive(Integer v) { this.isActive = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { this.notes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
