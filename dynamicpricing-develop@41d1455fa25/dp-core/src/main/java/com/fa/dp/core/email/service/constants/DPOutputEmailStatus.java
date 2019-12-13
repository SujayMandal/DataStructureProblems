package com.fa.dp.core.email.service.constants;

public enum DPOutputEmailStatus {

	INITIATED("Notification is initiated"),
	SUCCESS("Success"), //
	FAILED("Failed"), //
	IN_PROGRESS("In Progress"), //
	NOTIFICATION_DISABLED("Notification Disabled");
	
	private final String status;
	
	private DPOutputEmailStatus(final String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
}