package com.ca.umg.business.mappingnotification.entity;

public class MappingNotification {
   
    private String notificationEventId;; 
    private String notificationTemplateId;  
    private String notifiacationTypeId;
    private String tenantCode;
    private String toAddress;
    private String fromAddress;
    private String ccAddress;  
    private String bccAddress;   
    private String mobile;  
    private String createdBy;   
    private long createdOn;
    private String lastUpdatedBy;
    private long lastUpdatedOn;
    private String id;
    private String name;

	public String getNotificationEventId() {
		return notificationEventId;
	}

	public void setNotificationEventId(String notificationEventId) {
		this.notificationEventId = notificationEventId;
	}

	public String getNotificationTemplateId() {
		return notificationTemplateId;
	}

	public void setNotificationTemplateId(String notificationTemplateId) {
		this.notificationTemplateId = notificationTemplateId;
	}

	public String getNotifiacationTypeId() {
		return notifiacationTypeId;
	}

	public void setNotifiacationTypeId(String notifiacationTypeId) {
		this.notifiacationTypeId = notifiacationTypeId;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getCcAddress() {
		return ccAddress;
	}

	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}

	public String getBccAddress() {
		return bccAddress;
	}

	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public long getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(long lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
