package com.ca.umg.notification.model;

public enum NotificationHeaderEnum {

	ENVIRONMENT("environment"),
	MODEL_NAME("modelName"),
	MODEL_VERSION("modelVersion"),
	MAJOR_VERSION("majorVersion"),
	MINOR_VERSION("minorVersion"),
	TRANSACTION_ID("transactionId"),
    TRANSACTION_RUN_DATE("transactionRunDate"),
	PUBLISHER_NAME("publisherName"),
	PUBLISHED_DATE_TIME("publishedDate"),
	TENANT_NAME("tenantName"),
	TENANT_CODE("tenantCode"),
	TRAN_EXEC_DATE_TIME("executionTime"),
	ERROR_CODE("errorCode"),
	ERROR_MESSAGE("errorMessage"),
	UMG_ADMIN_URL("umgAdminUrl"),
	VERSION_ID("modelId"),
	MODEL_APPROVAL_URL("modelApprovalURL"),
	AUTH_TOKEN("authenticationCode"),
	ACTIVATION_DATE("activationDate"),
	ACTIVE_FROM("activeFrom"),
	ACTIVE_UNTIL("activeUntil"),
	RESET_REASON("resetReason"),
	RESET_BY("resetBy"),
	BATCH_ENABLED("batchEnabled"),
	BULK_ENABLED("bulkEnabled"),
	ModelOutput_Validation("modelOutputValidation"), 
	EMAIL_NOTIFICATION_ENABLED("emailNotificationsEnabled"),
	TENANT_ONBOARDED_ON("tenantOnboardedOn"),
	TENANT_ONBOARDED_BY("tenantOnboardedBy"),
    MODELET_HOST("modeletHost"),
    PORT("port"),
    POOL_NAME("poolName"),
    R_SERVE_PORT("rServePort"),
    NEW_POOL_NAME("newPoolName"),
    LOADED_MODEL("loadedModel"),
    LOADED_MODEL_VERSION("loadedModelVersion"),
    LOADED_MODEL_VERSION_NAME("loadedModelVersionName"),
    REASON("reason"),
    MODEL_TO_LOAD("modelToLoad"),
    MODEL_VERSION_TO_LOAD("modelVersionToLoad"),
    MODEL_VERSION_NAME_TO_LOAD("modelVersionNameToLoad"),
    RESTART_INITIATED_TIME("modeletRestartTime"),
	MODELET_LIST("modeletList"),
	MODELET_START_TIME("modelStartTime"),
	CLIENT_TRANSACTION_ID("clienttransactionId"),
	EXCESS_RUNTIME("excessRuntime"),
	EXEC_COUNT("execLimit"),
	RESTART_COUNT("restartCount"),
    NEW_STSTUS("newStatus"),
    OLD_STSTUS("oldStatus");
	private final String headerName;
	
	private NotificationHeaderEnum(final String headerName) {
		this.headerName = headerName;
	}

	public String getHeaderName() {
		return headerName;
	}	
}
