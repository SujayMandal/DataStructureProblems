package com.ca.umg.notification.model;

public enum NotificationStatus {

	INITIATED("Notification is initiated"),
	SUCCESS("Success"), //
	FAILED("Failed"), //
	IN_PROGRESS("In Progress"), //
	NOTIFICATION_DISABLED("Notification Disabled");
	
	private final String status;
	
	private NotificationStatus(final String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
}