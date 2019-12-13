package com.ca.umg.notification.model;

import java.io.Serializable;

public class NotificationAuditDocument implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String emailContent;
	
	private String subject;
	
	private String to;
	
	private String cc;
	
	private String from;
	
	private String eventName;
	
	private String eventTriggerTimestamp;
	
	private String userId;
	
	private String tenant;
	
	private String modelNameAndVersion;
	
	private String emailTriggerTimestamp;
	
	private String status;
	
	private String mobile;
	
	private String notificationType;
	
	private String transactionId;
	
	private long eventTriggerTimestampInMillies;

	private long emailTriggerTimestampInMillies;
	
	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventTriggerTimestamp() {
		return eventTriggerTimestamp;
	}

	public void setEventTriggerTimestamp(String eventTriggerTimestamp) {
		this.eventTriggerTimestamp = eventTriggerTimestamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmailTriggerTimestamp() {
		return emailTriggerTimestamp;
	}

	public void setEmailTriggerTimestamp(String emailTriggerTimestamp) {
		this.emailTriggerTimestamp = emailTriggerTimestamp;
	}

	public String getModelNameAndVersion() {
		return modelNameAndVersion;
	}

	public void setModelNameAndVersion(String modelNameAndVersion) {
		this.modelNameAndVersion = modelNameAndVersion;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getEventTriggerTimestampInMillies() {
		return eventTriggerTimestampInMillies;
	}

	public void setEventTriggerTimestampInMillies(long eventTriggerTimestampInMillies) {
		this.eventTriggerTimestampInMillies = eventTriggerTimestampInMillies;
	}

	public long getEmailTriggerTimestampInMillies() {
		return emailTriggerTimestampInMillies;
	}

	public void setEmailTriggerTimestampInMillies(long emailTriggerTimestampInMillies) {
		this.emailTriggerTimestampInMillies = emailTriggerTimestampInMillies;
	}		
}