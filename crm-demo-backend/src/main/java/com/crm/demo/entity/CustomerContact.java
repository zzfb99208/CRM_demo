package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;

@TableName("customer_contact")
public class CustomerContact {
    @TableId(type = IdType.AUTO) private Long id;
    private Long customerId; private String contactName; private String title;
    private String email; private String phone; private String notes;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getCustomerId() { return customerId; } public void setCustomerId(Long v) { this.customerId = v; }
    public String getContactName() { return contactName; } public void setContactName(String v) { this.contactName = v; }
    public String getTitle() { return title; } public void setTitle(String v) { this.title = v; }
    public String getEmail() { return email; } public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; } public void setPhone(String v) { this.phone = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { this.notes = v; }
}
