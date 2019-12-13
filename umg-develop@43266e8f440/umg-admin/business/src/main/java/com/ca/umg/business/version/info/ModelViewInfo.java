package com.ca.umg.business.version.info;

import org.joda.time.DateTime;

@SuppressWarnings({"PMD.TooManyFields"})

public class ModelViewInfo {

	 private String notificationStatus;
	 private String tenantCode;
	 private String tenantName;
	 private String status;
	 private String name;
	 private Integer majorVersion;
	 private Integer minorVersion;
	 private String createdBy;
	 private DateTime createdOn;
	 private String createdDateTime;
	 private DateTime publishedOn;
	 private String publishedBy;
	 private String publishedDateTime;
	 private String description;
	 private Boolean responseSuccessFailureMsg = false;
	 private Boolean switchTenantFlag = false;
	 
	public String getNotificationStatus() {
		return notificationStatus;
	}
	public void setNotificationStatus(String notificationStatus) {
		this.notificationStatus = notificationStatus;
	}
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}
	public Integer getMinorVersion() {
		return minorVersion;
	}
	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public DateTime getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(DateTime createdOn) {
		this.createdOn = createdOn;
	}
	public String getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public DateTime getPublishedOn() {
		return publishedOn;
	}
	public void setPublishedOn(DateTime publishedOn) {
		this.publishedOn = publishedOn;
	}
	public String getPublishedBy() {
		return publishedBy;
	}
	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}
	public String getPublishedDateTime() {
		return publishedDateTime;
	}
	public void setPublishedDateTime(String publishedDateTime) {
		this.publishedDateTime = publishedDateTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getResponseSuccessFailureMsg() {
		return responseSuccessFailureMsg;
	}
	public void setResponseSuccessFailureMsg(Boolean responseSuccessFailureMsg) {
		this.responseSuccessFailureMsg = responseSuccessFailureMsg;
	}
	public Boolean getSwitchTenantFlag() {
		return switchTenantFlag;
	}
	public void setSwitchTenantFlag(Boolean switchTenantFlag) {
		this.switchTenantFlag = switchTenantFlag;
	}
	 
}
