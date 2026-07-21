package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("price_list")
public class PriceList {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String priceListNo;
    private String priceListName;
    private String bizLevel1Tag;
    private String bizLevel2Tag;
    private String bizLevel3Tag;
    private String customerType;
    private String saleProvince;
    private String region;
    private Integer isStandard;
    private String status;
    private String remark;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getPriceListNo() { return priceListNo; } public void setPriceListNo(String v) { this.priceListNo = v; }
    public String getPriceListName() { return priceListName; } public void setPriceListName(String v) { this.priceListName = v; }
    public String getBizLevel1Tag() { return bizLevel1Tag; } public void setBizLevel1Tag(String v) { this.bizLevel1Tag = v; }
    public String getBizLevel2Tag() { return bizLevel2Tag; } public void setBizLevel2Tag(String v) { this.bizLevel2Tag = v; }
    public String getBizLevel3Tag() { return bizLevel3Tag; } public void setBizLevel3Tag(String v) { this.bizLevel3Tag = v; }
    public String getCustomerType() { return customerType; } public void setCustomerType(String v) { this.customerType = v; }
    public String getSaleProvince() { return saleProvince; } public void setSaleProvince(String v) { this.saleProvince = v; }
    public String getRegion() { return region; } public void setRegion(String v) { this.region = v; }
    public Integer getIsStandard() { return isStandard; } public void setIsStandard(Integer v) { this.isStandard = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public String getRemark() { return remark; } public void setRemark(String v) { this.remark = v; }
    public String getCreatedBy() { return createdBy; } public void setCreatedBy(String v) { this.createdBy = v; }
    public String getUpdatedBy() { return updatedBy; } public void setUpdatedBy(String v) { this.updatedBy = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
