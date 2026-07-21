package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("customer")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerCode;
    private String customerSource;
    private String customerCategory;
    private String customerLevel;
    private String customerOwnerName;
    private String companyName;
    private String commonName;
    private String aliasName;
    private String customerType;
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
    private String taxIdentificationNo;
    private String pIva;
    private String bankInfo;
    private String shipToAddress;
    private String specialNotes;
    private Integer status;
    private Integer historicalData;
    private Integer cooperatedCustomer;
    private Integer stockCustomer;
    private String keyMark;
    private Integer successProbability;
    private String instrumentStock;
    private String reagentStock;
    private LocalDate disableDate;
    private String businessRegistration;
    private String province;
    private String city;
    private String district;
    private String detailedAddress;
    private String detailedDeliveryAddress;
    private String continent;
    private String country;
    private BigDecimal coveragePopulation;
    private String businessDepartment;
    private String directSuperior;
    private String firstLevelBusiness;
    private String secondLevelBusiness;
    private String thirdLevelBusiness;
    private String economicType;
    private String regionType;
    private String regionLevel;
    private String businessRegion;
    private String regionPublicPool;
    private String technicalSupportLeader;
    private String salesLeader;
    private String superiorCustomer;
    private String admittedProduct;
    private LocalDate activateDate;
    private BigDecimal customerAdvance;
    private Integer doNotDisturb;
    private String deliveryContact;
    private String deliveryContactPhone;
    private String terminalAgentLeader;
    private String terminalAgentContact;
    private String departmentDirector;
    private String departmentDirectorContact;
    private String departmentDirectorWechat;
    private String equipmentChief;
    private String equipmentChiefContact;
    private String managingDean;
    private String managingDeanContact;
    private String contactPerson;
    private String stampedQualificationUpload;
    private String qualificationFileUpload;
    private LocalDateTime latestActivityTime;
    private String administrativeLevel;
    private Integer bedCount;
    private BigDecimal annualOutpatientVolume;
    private Integer isBenchmark;
    private String syncReturnCode;
    private String syncReturnLog;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getCustomerCode() { return customerCode; } public void setCustomerCode(String v) { this.customerCode = v; }
    public String getCustomerSource() { return customerSource; } public void setCustomerSource(String v) { this.customerSource = v; }
    public String getCustomerCategory() { return customerCategory; } public void setCustomerCategory(String v) { this.customerCategory = v; }
    public String getCustomerLevel() { return customerLevel; } public void setCustomerLevel(String v) { this.customerLevel = v; }
    public String getCustomerOwnerName() { return customerOwnerName; } public void setCustomerOwnerName(String v) { this.customerOwnerName = v; }
    public String getCompanyName() { return companyName; } public void setCompanyName(String v) { this.companyName = v; }
    public String getCommonName() { return commonName; } public void setCommonName(String v) { this.commonName = v; }
    public String getAliasName() { return aliasName; } public void setAliasName(String v) { this.aliasName = v; }
    public String getCustomerType() { return customerType; } public void setCustomerType(String v) { this.customerType = v; }
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
    public String getTaxIdentificationNo() { return taxIdentificationNo; } public void setTaxIdentificationNo(String v) { this.taxIdentificationNo = v; }
    public String getpIva() { return pIva; } public void setpIva(String v) { this.pIva = v; }
    public String getBankInfo() { return bankInfo; } public void setBankInfo(String v) { this.bankInfo = v; }
    public String getShipToAddress() { return shipToAddress; } public void setShipToAddress(String v) { this.shipToAddress = v; }
    public String getSpecialNotes() { return specialNotes; } public void setSpecialNotes(String v) { this.specialNotes = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
    public Integer getHistoricalData() { return historicalData; } public void setHistoricalData(Integer v) { this.historicalData = v; }
    public Integer getCooperatedCustomer() { return cooperatedCustomer; } public void setCooperatedCustomer(Integer v) { this.cooperatedCustomer = v; }
    public Integer getStockCustomer() { return stockCustomer; } public void setStockCustomer(Integer v) { this.stockCustomer = v; }
    public String getKeyMark() { return keyMark; } public void setKeyMark(String v) { this.keyMark = v; }
    public Integer getSuccessProbability() { return successProbability; } public void setSuccessProbability(Integer v) { this.successProbability = v; }
    public String getInstrumentStock() { return instrumentStock; } public void setInstrumentStock(String v) { this.instrumentStock = v; }
    public String getReagentStock() { return reagentStock; } public void setReagentStock(String v) { this.reagentStock = v; }
    public LocalDate getDisableDate() { return disableDate; } public void setDisableDate(LocalDate v) { this.disableDate = v; }
    public String getBusinessRegistration() { return businessRegistration; } public void setBusinessRegistration(String v) { this.businessRegistration = v; }
    public String getProvince() { return province; } public void setProvince(String v) { this.province = v; }
    public String getCity() { return city; } public void setCity(String v) { this.city = v; }
    public String getDistrict() { return district; } public void setDistrict(String v) { this.district = v; }
    public String getDetailedAddress() { return detailedAddress; } public void setDetailedAddress(String v) { this.detailedAddress = v; }
    public String getDetailedDeliveryAddress() { return detailedDeliveryAddress; } public void setDetailedDeliveryAddress(String v) { this.detailedDeliveryAddress = v; }
    public String getContinent() { return continent; } public void setContinent(String v) { this.continent = v; }
    public String getCountry() { return country; } public void setCountry(String v) { this.country = v; }
    public BigDecimal getCoveragePopulation() { return coveragePopulation; } public void setCoveragePopulation(BigDecimal v) { this.coveragePopulation = v; }
    public String getBusinessDepartment() { return businessDepartment; } public void setBusinessDepartment(String v) { this.businessDepartment = v; }
    public String getDirectSuperior() { return directSuperior; } public void setDirectSuperior(String v) { this.directSuperior = v; }
    public String getFirstLevelBusiness() { return firstLevelBusiness; } public void setFirstLevelBusiness(String v) { this.firstLevelBusiness = v; }
    public String getSecondLevelBusiness() { return secondLevelBusiness; } public void setSecondLevelBusiness(String v) { this.secondLevelBusiness = v; }
    public String getThirdLevelBusiness() { return thirdLevelBusiness; } public void setThirdLevelBusiness(String v) { this.thirdLevelBusiness = v; }
    public String getEconomicType() { return economicType; } public void setEconomicType(String v) { this.economicType = v; }
    public String getRegionType() { return regionType; } public void setRegionType(String v) { this.regionType = v; }
    public String getRegionLevel() { return regionLevel; } public void setRegionLevel(String v) { this.regionLevel = v; }
    public String getBusinessRegion() { return businessRegion; } public void setBusinessRegion(String v) { this.businessRegion = v; }
    public String getRegionPublicPool() { return regionPublicPool; } public void setRegionPublicPool(String v) { this.regionPublicPool = v; }
    public String getTechnicalSupportLeader() { return technicalSupportLeader; } public void setTechnicalSupportLeader(String v) { this.technicalSupportLeader = v; }
    public String getSalesLeader() { return salesLeader; } public void setSalesLeader(String v) { this.salesLeader = v; }
    public String getSuperiorCustomer() { return superiorCustomer; } public void setSuperiorCustomer(String v) { this.superiorCustomer = v; }
    public String getAdmittedProduct() { return admittedProduct; } public void setAdmittedProduct(String v) { this.admittedProduct = v; }
    public LocalDate getActivateDate() { return activateDate; } public void setActivateDate(LocalDate v) { this.activateDate = v; }
    public BigDecimal getCustomerAdvance() { return customerAdvance; } public void setCustomerAdvance(BigDecimal v) { this.customerAdvance = v; }
    public Integer getDoNotDisturb() { return doNotDisturb; } public void setDoNotDisturb(Integer v) { this.doNotDisturb = v; }
    public String getDeliveryContact() { return deliveryContact; } public void setDeliveryContact(String v) { this.deliveryContact = v; }
    public String getDeliveryContactPhone() { return deliveryContactPhone; } public void setDeliveryContactPhone(String v) { this.deliveryContactPhone = v; }
    public String getTerminalAgentLeader() { return terminalAgentLeader; } public void setTerminalAgentLeader(String v) { this.terminalAgentLeader = v; }
    public String getTerminalAgentContact() { return terminalAgentContact; } public void setTerminalAgentContact(String v) { this.terminalAgentContact = v; }
    public String getDepartmentDirector() { return departmentDirector; } public void setDepartmentDirector(String v) { this.departmentDirector = v; }
    public String getDepartmentDirectorContact() { return departmentDirectorContact; } public void setDepartmentDirectorContact(String v) { this.departmentDirectorContact = v; }
    public String getDepartmentDirectorWechat() { return departmentDirectorWechat; } public void setDepartmentDirectorWechat(String v) { this.departmentDirectorWechat = v; }
    public String getEquipmentChief() { return equipmentChief; } public void setEquipmentChief(String v) { this.equipmentChief = v; }
    public String getEquipmentChiefContact() { return equipmentChiefContact; } public void setEquipmentChiefContact(String v) { this.equipmentChiefContact = v; }
    public String getManagingDean() { return managingDean; } public void setManagingDean(String v) { this.managingDean = v; }
    public String getManagingDeanContact() { return managingDeanContact; } public void setManagingDeanContact(String v) { this.managingDeanContact = v; }
    public String getContactPerson() { return contactPerson; } public void setContactPerson(String v) { this.contactPerson = v; }
    public String getStampedQualificationUpload() { return stampedQualificationUpload; } public void setStampedQualificationUpload(String v) { this.stampedQualificationUpload = v; }
    public String getQualificationFileUpload() { return qualificationFileUpload; } public void setQualificationFileUpload(String v) { this.qualificationFileUpload = v; }
    public LocalDateTime getLatestActivityTime() { return latestActivityTime; } public void setLatestActivityTime(LocalDateTime v) { this.latestActivityTime = v; }
    public String getAdministrativeLevel() { return administrativeLevel; } public void setAdministrativeLevel(String v) { this.administrativeLevel = v; }
    public Integer getBedCount() { return bedCount; } public void setBedCount(Integer v) { this.bedCount = v; }
    public BigDecimal getAnnualOutpatientVolume() { return annualOutpatientVolume; } public void setAnnualOutpatientVolume(BigDecimal v) { this.annualOutpatientVolume = v; }
    public Integer getIsBenchmark() { return isBenchmark; } public void setIsBenchmark(Integer v) { this.isBenchmark = v; }
    public String getSyncReturnCode() { return syncReturnCode; } public void setSyncReturnCode(String v) { this.syncReturnCode = v; }
    public String getSyncReturnLog() { return syncReturnLog; } public void setSyncReturnLog(String v) { this.syncReturnLog = v; }
    public String getRemark() { return remark; } public void setRemark(String v) { this.remark = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
