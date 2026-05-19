package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;

@TableName("proforma_invoice")
public class ProformaInvoice {
    @TableId(type = IdType.AUTO) private Long id;
    private String invoiceNo; private Long poId; private String contractNo; private Long customerId;
    private String cytOrderNo; private LocalDate invoiceDate; private String deliveryTerms;
    private String paymentTerms; private String transportMethod; private String countryOfOrigin;
    private String poReference; private BigDecimal totalValue; private String status;
    private Long createdBy; private Long approvedBy; private LocalDateTime approvedAt;
    private String rejectReason; private Long submittedBy; private LocalDateTime submittedAt;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getInvoiceNo() { return invoiceNo; } public void setInvoiceNo(String v) { this.invoiceNo = v; }
    public Long getPoId() { return poId; } public void setPoId(Long v) { this.poId = v; }
    public String getContractNo() { return contractNo; } public void setContractNo(String v) { this.contractNo = v; }
    public Long getCustomerId() { return customerId; } public void setCustomerId(Long v) { this.customerId = v; }
    public String getCytOrderNo() { return cytOrderNo; } public void setCytOrderNo(String v) { this.cytOrderNo = v; }
    public LocalDate getInvoiceDate() { return invoiceDate; } public void setInvoiceDate(LocalDate v) { this.invoiceDate = v; }
    public String getDeliveryTerms() { return deliveryTerms; } public void setDeliveryTerms(String v) { this.deliveryTerms = v; }
    public String getPaymentTerms() { return paymentTerms; } public void setPaymentTerms(String v) { this.paymentTerms = v; }
    public String getTransportMethod() { return transportMethod; } public void setTransportMethod(String v) { this.transportMethod = v; }
    public String getCountryOfOrigin() { return countryOfOrigin; } public void setCountryOfOrigin(String v) { this.countryOfOrigin = v; }
    public String getPoReference() { return poReference; } public void setPoReference(String v) { this.poReference = v; }
    public BigDecimal getTotalValue() { return totalValue; } public void setTotalValue(BigDecimal v) { this.totalValue = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public Long getCreatedBy() { return createdBy; } public void setCreatedBy(Long v) { this.createdBy = v; }
    public Long getApprovedBy() { return approvedBy; } public void setApprovedBy(Long v) { this.approvedBy = v; }
    public LocalDateTime getApprovedAt() { return approvedAt; } public void setApprovedAt(LocalDateTime v) { this.approvedAt = v; }
    public String getRejectReason() { return rejectReason; } public void setRejectReason(String v) { this.rejectReason = v; }
    public Long getSubmittedBy() { return submittedBy; } public void setSubmittedBy(Long v) { this.submittedBy = v; }
    public LocalDateTime getSubmittedAt() { return submittedAt; } public void setSubmittedAt(LocalDateTime v) { this.submittedAt = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
