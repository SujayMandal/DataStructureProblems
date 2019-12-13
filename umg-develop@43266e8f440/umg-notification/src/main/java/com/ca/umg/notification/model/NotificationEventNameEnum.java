package com.ca.umg.notification.model;

public enum NotificationEventNameEnum {

	ON_MODEL_PUBLISH("On Model Publish"), //
	MODEL_PUBLISH_APPROVAL("Model Publish Approval"), //
	RUNTIME_TRANSACTION_FAILURE("Runtime Transaction Failure"), //
	NEW_TENANT_ADDED("New Tenant Added"), //
    MODELET_RESTART("Modelet Restart"),
	EXCESSIVE_MODEL_EXEC_TIME("Excessive Model Exec Time"),
	NOTIFY_MODELET_STATUS_CHANGE("Modelet Pool Mismatch");
	private final String name;
	
	private NotificationEventNameEnum(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}		
}