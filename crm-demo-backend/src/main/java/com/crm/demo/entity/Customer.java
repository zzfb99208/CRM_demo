package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("customer")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerCode;
    private String companyName;
    private String customerTier;
    private String paymentTerms;
    private String currency;
    private String incoterms;
    private String website;
    private String assayExpiryReq;
    private String regionCover;
    private String distributionType;
    private String termOfAgreement;
    private String taxNo;
    private String pIva;
    private String bankInfo;
    private String shipToAddress;
    private String specialNotes;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getCustomerCode() { return customerCode; } public void setCustomerCode(String v) { this.customerCode = v; }
    public String getCompanyName() { return companyName; } public void setCompanyName(String v) { this.companyName = v; }
    public String getCustomerTier() { return customerTier; } public void setCustomerTier(String v) { this.customerTier = v; }
    public String getPaymentTerms() { return paymentTerms; } public void setPaymentTerms(String v) { this.paymentTerms = v; }
    public String getCurrency() { return currency; } public void setCurrency(String v) { this.currency = v; }
    public String getIncoterms() { return incoterms; } public void setIncoterms(String v) { this.incoterms = v; }
    public String getWebsite() { return website; } public void setWebsite(String v) { this.website = v; }
    public String getAssayExpiryReq() { return assayExpiryReq; } public void setAssayExpiryReq(String v) { this.assayExpiryReq = v; }
    public String getRegionCover() { return regionCover; } public void setRegionCover(String v) { this.regionCover = v; }
    public String getDistributionType() { return distributionType; } public void setDistributionType(String v) { this.distributionType = v; }
    public String getTermOfAgreement() { return termOfAgreement; } public void setTermOfAgreement(String v) { this.termOfAgreement = v; }
    public String getTaxNo() { return taxNo; } public void setTaxNo(String v) { this.taxNo = v; }
    public String getpIva() { return pIva; } public void setpIva(String v) { this.pIva = v; }
    public String getBankInfo() { return bankInfo; } public void setBankInfo(String v) { this.bankInfo = v; }
    public String getShipToAddress() { return shipToAddress; } public void setShipToAddress(String v) { this.shipToAddress = v; }
    public String getSpecialNotes() { return specialNotes; } public void setSpecialNotes(String v) { this.specialNotes = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
