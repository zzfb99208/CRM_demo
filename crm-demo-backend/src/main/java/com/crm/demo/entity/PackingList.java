package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*; import java.math.BigDecimal; import java.time.LocalDateTime;

@TableName("packing_list")
public class PackingList {
    @TableId(type = IdType.AUTO) private Long id;
    private Long piId; private String contractNo; private Long customerId; private String shipTo;
    private String transportMethod; private String transportRoute; private String countryOfOrigin;
    private String poReference; private BigDecimal totalVolumeCbm; private BigDecimal totalNetWeightKg;
    private BigDecimal totalGrossWeightKg; private Integer totalCartons; private String status;
    private Long createdBy; private Long approvedBy; private LocalDateTime approvedAt;
    private String rejectReason; private LocalDateTime createdAt; private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getPiId() { return piId; } public void setPiId(Long v) { this.piId = v; }
    public String getContractNo() { return contractNo; } public void setContractNo(String v) { this.contractNo = v; }
    public Long getCustomerId() { return customerId; } public void setCustomerId(Long v) { this.customerId = v; }
    public String getShipTo() { return shipTo; } public void setShipTo(String v) { this.shipTo = v; }
    public String getTransportMethod() { return transportMethod; } public void setTransportMethod(String v) { this.transportMethod = v; }
    public String getTransportRoute() { return transportRoute; } public void setTransportRoute(String v) { this.transportRoute = v; }
    public String getCountryOfOrigin() { return countryOfOrigin; } public void setCountryOfOrigin(String v) { this.countryOfOrigin = v; }
    public String getPoReference() { return poReference; } public void setPoReference(String v) { this.poReference = v; }
    public BigDecimal getTotalVolumeCbm() { return totalVolumeCbm; } public void setTotalVolumeCbm(BigDecimal v) { this.totalVolumeCbm = v; }
    public BigDecimal getTotalNetWeightKg() { return totalNetWeightKg; } public void setTotalNetWeightKg(BigDecimal v) { this.totalNetWeightKg = v; }
    public BigDecimal getTotalGrossWeightKg() { return totalGrossWeightKg; } public void setTotalGrossWeightKg(BigDecimal v) { this.totalGrossWeightKg = v; }
    public Integer getTotalCartons() { return totalCartons; } public void setTotalCartons(Integer v) { this.totalCartons = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public Long getCreatedBy() { return createdBy; } public void setCreatedBy(Long v) { this.createdBy = v; }
    public Long getApprovedBy() { return approvedBy; } public void setApprovedBy(Long v) { this.approvedBy = v; }
    public LocalDateTime getApprovedAt() { return approvedAt; } public void setApprovedAt(LocalDateTime v) { this.approvedAt = v; }
    public String getRejectReason() { return rejectReason; } public void setRejectReason(String v) { this.rejectReason = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
