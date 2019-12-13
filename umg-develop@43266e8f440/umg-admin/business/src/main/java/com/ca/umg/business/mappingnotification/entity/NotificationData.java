package com.ca.umg.business.mappingnotification.entity;

public class NotificationData {

	private String id;
	
	private String tenantCode;
	
	private String lastUpdatedBy;
	
	private long lastUpdatedOn;
	
	private String eventName;
	
	private String description;
	
	private String lastupdatedOnDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLastupdatedOnDate() {
		return lastupdatedOnDate;
	}

	public void setLastupdatedOnDate(String lastupdatedOnDate) {
		this.lastupdatedOnDate = lastupdatedOnDate;
	}
	
}
