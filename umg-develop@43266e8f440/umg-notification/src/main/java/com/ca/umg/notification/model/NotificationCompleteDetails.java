package com.ca.umg.notification.model;

public class NotificationCompleteDetails {

	private String id;
	
	private String status;
	
	private NotificationEvent notificationEvent;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public NotificationEvent getNotificationEvent() {
		return notificationEvent;
	}

	public void setNotificationEvent(NotificationEvent notificationEvent) {
		this.notificationEvent = notificationEvent;
	}
}
