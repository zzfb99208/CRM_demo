package com.crm.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("customer_contact")
public class CustomerContact {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String contactName;
    private String title;
    private String email;
    private String phone;
    private String wechat;
    private String notes;
    private Integer isPrimary;
    private String department;
    private String gender;
    private LocalDate birthday;
    private String hobby;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getCustomerId() { return customerId; } public void setCustomerId(Long v) { this.customerId = v; }
    public String getContactName() { return contactName; } public void setContactName(String v) { this.contactName = v; }
    public String getTitle() { return title; } public void setTitle(String v) { this.title = v; }
    public String getEmail() { return email; } public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; } public void setPhone(String v) { this.phone = v; }
    public String getWechat() { return wechat; } public void setWechat(String v) { this.wechat = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { this.notes = v; }
    public Integer getIsPrimary() { return isPrimary; } public void setIsPrimary(Integer v) { this.isPrimary = v; }
    public String getDepartment() { return department; } public void setDepartment(String v) { this.department = v; }
    public String getGender() { return gender; } public void setGender(String v) { this.gender = v; }
    public LocalDate getBirthday() { return birthday; } public void setBirthday(LocalDate v) { this.birthday = v; }
    public String getHobby() { return hobby; } public void setHobby(String v) { this.hobby = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
