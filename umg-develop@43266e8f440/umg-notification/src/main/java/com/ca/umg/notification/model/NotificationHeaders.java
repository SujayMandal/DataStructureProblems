package com.ca.umg.notification.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationHeaders implements Serializable {

	private static final long serialVersionUID = 1l;
	
	private String environment;
	
	private String modelName;
	
	private String modelVersion;
	
	private int majorVersion;
	
	private int minorVersion;
	
	private String transactionId;
	
	private String publisherName;
	
	private String publishedDateTime;
	
	private String tenantName;
	
	private String tenantCode;

	private String tranExecDateTime;
	
	private String errorCode;
	
	private String errorMessage;
	
	private String umgAdminUrl;
	
	private String versionId;
	
	private String modelApprovalURL;
	
	private String authCode;
	
	private String authCodeActiveDate;
	
	private String activeFrom;
	
	private String activeUntil;
	
	private String resetReason;
	
	private String resetBy;
	
	private boolean batchEnabled;
	
	private boolean bulkEnabled;
	
	private boolean emailNotificationEnabled;
	
	private String tenantOnboardedOn;
	
	private String tenantOnboardedBy;

    private String modeletHost;
    private String port;
    private String poolName;
    private String newPoolName;
    private String loadedModel;
    private String loadedModelVersion;
    private String reason;
    private String modelToLoad;
    private String modelVersionToLoad;
    private String modelVersionNameToLoad;
    private String transactionRunDate;
    private String restartInitiatedTime;    
    private String modelStartTime;
	private String clienttransactionId;	
	private String excessiveRuntime;
	private String execLimit;
	private String restartCount;
    
	private String rServePort;
	private String oldstatus;
    private String newstatus;
	private List<Map<String, Object>> modeletList = new ArrayList<>();

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getPublishedDateTime() {
		return publishedDateTime;
	}

	public void setPublishedDateTime(String publishedDateTime) {
		this.publishedDateTime = publishedDateTime;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	
	public String getTranExecDateTime() {
		return tranExecDateTime;
	}

	public void setTranExecDateTime(String tranExecDateTime) {
		this.tranExecDateTime = tranExecDateTime;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
	public String getUmgAdminUrl() {
		return umgAdminUrl;
	}

	public void setUmgAdminUrl(String umgAdminUrl) {
		this.umgAdminUrl = umgAdminUrl;
	}
	
	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
	public String getModelApprovalURL() {
		return modelApprovalURL;
	}

	public void setModelApprovalURL(String modelApprovalURL) {
		this.modelApprovalURL = modelApprovalURL;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthCodeActiveDate() {
		return authCodeActiveDate;
	}

	public void setAuthCodeActiveDate(String authCodeActiveDate) {
		this.authCodeActiveDate = authCodeActiveDate;
	}

	public String getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(String activeFrom) {
		this.activeFrom = activeFrom;
	}

	public String getActiveUntil() {
		return activeUntil;
	}

	public void setActiveUntil(String activeUntil) {
		this.activeUntil = activeUntil;
	}

	public String getResetReason() {
		return resetReason;
	}

	public void setResetReason(String resetReason) {
		this.resetReason = resetReason;
	}

	public String getResetBy() {
		return resetBy;
	}

	public void setResetBy(String resetBy) {
		this.resetBy = resetBy;
	}
	
	public boolean isBatchEnabled() {
		return batchEnabled;
	}

	public void setBatchEnabled(boolean batchEnabled) {
		this.batchEnabled = batchEnabled;
	}

	public boolean isBulkEnabled() {
		return bulkEnabled;
	}

	public void setBulkEnabled(boolean bulkEnabled) {
		this.bulkEnabled = bulkEnabled;
	}

	public boolean isEmailNotificationEnabled() {
		return emailNotificationEnabled;
	}

	public void setEmailNotificationEnabled(boolean emailNotificationEnabled) {
		this.emailNotificationEnabled = emailNotificationEnabled;
	}

	public String getTenantOnboardedOn() {
		return tenantOnboardedOn;
	}

	public void setTenantOnboardedOn(String tenantOnboardedOn) {
		this.tenantOnboardedOn = tenantOnboardedOn;
	}

	public String getTenantOnboardedBy() {
		return tenantOnboardedBy;
	}

	public void setTenantOnboardedBy(String tenantOnboardedBy) {
		this.tenantOnboardedBy = tenantOnboardedBy;
	}

    public String getModeletHost() {
        return modeletHost;
    }

    public void setModeletHost(String modeletHost) {
        this.modeletHost = modeletHost;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getLoadedModel() {
        return loadedModel;
    }

    public void setLoadedModel(String loadedModel) {
        this.loadedModel = loadedModel;
    }

    public String getLoadedModelVersion() {
        return loadedModelVersion;
    }

    public void setLoadedModelVersion(String loadedModelVersion) {
        this.loadedModelVersion = loadedModelVersion;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getModelToLoad() {
        return modelToLoad;
    }

    public void setModelToLoad(String modelToLoad) {
        this.modelToLoad = modelToLoad;
    }

    public String getModelVersionToLoad() {
        return modelVersionToLoad;
    }

    public void setModelVersionToLoad(String modelVersionToLoad) {
        this.modelVersionToLoad = modelVersionToLoad;
    }

    public String getModelVersionNameToLoad() {
        return modelVersionNameToLoad;
    }

    public void setModelVersionNameToLoad(String modelVersionNameToLoad) {
        this.modelVersionNameToLoad = modelVersionNameToLoad;
    }

    public String getNewPoolName() {
        return newPoolName;
    }

    public void setNewPoolName(String newPoolName) {
        this.newPoolName = newPoolName;
    }

    public String getTransactionRunDate() {
        return transactionRunDate;
    }

    public void setTransactionRunDate(String transactionRunDate) {
        this.transactionRunDate = transactionRunDate;
    }

    public String getRestartInitiatedTime() {
        return restartInitiatedTime;
    }

    public void setRestartInitiatedTime(String restartInitiatedTime) {
        this.restartInitiatedTime = restartInitiatedTime;
    }

    public List<Map<String, Object>> getModeletList() {
        return modeletList;
    }

    public void setModeletList(List<Map<String, Object>> modeletList) {
        this.modeletList = modeletList;
    }
    public String getModelStartTime() {
		return modelStartTime;
	}

	public void setModelStartTime(String modelStartTime) {
		this.modelStartTime = modelStartTime;
	}

	public String getClienttransactionId() {
		return clienttransactionId;
	}

	public void setClienttransactionId(String clienttransactionId) {
		this.clienttransactionId = clienttransactionId;
	}

	public String getExcessiveRuntime() {
		return excessiveRuntime;
	}

	public void setExcessiveRuntime(String excessiveRuntime) {
		this.excessiveRuntime = excessiveRuntime;
	}
	
	public String getExecLimit() {
		return execLimit;
	}

	public void setExecLimit(String execLimit) {
		this.execLimit = execLimit;
	}

	public String getRestartCount() {
		return restartCount;
	}

	public void setRestartCount(String restartCount) {
		this.restartCount = restartCount;
	}

    public String getrServePort() {
		return rServePort;
	}

	public void setrServePort(String rServePort) {
		this.rServePort = rServePort;
	}

	public String getOldstatus() {
		return oldstatus;
	}

	public void setOldstatus(String oldstatus) {
		this.oldstatus = oldstatus;
	}

	public String getNewstatus() {
		return newstatus;
	}

	public void setNewstatus(String newstatus) {
		this.newstatus = newstatus;
	}

	public Map<String, Object> getHeadersMap() {
		final Map<String, Object> headersMap = new HashMap<>();

		headersMap.put(NotificationHeaderEnum.ENVIRONMENT.getHeaderName(), getEnvironment());
		headersMap.put(NotificationHeaderEnum.MAJOR_VERSION.getHeaderName(), getMajorVersion() + "");
		headersMap.put(NotificationHeaderEnum.MINOR_VERSION.getHeaderName(), getMinorVersion() + "");
		headersMap.put(NotificationHeaderEnum.MODEL_NAME.getHeaderName(), getModelName());		
		headersMap.put(NotificationHeaderEnum.MODEL_VERSION.getHeaderName(), getModelVersion());
		
		headersMap.put(NotificationHeaderEnum.PUBLISHED_DATE_TIME.getHeaderName(), getPublishedDateTime());
		headersMap.put(NotificationHeaderEnum.PUBLISHER_NAME.getHeaderName(), getPublisherName());
		headersMap.put(NotificationHeaderEnum.TENANT_CODE.getHeaderName(), getTenantCode());
		headersMap.put(NotificationHeaderEnum.TENANT_NAME.getHeaderName(), getTenantName());

		headersMap.put(NotificationHeaderEnum.TRANSACTION_ID.getHeaderName(), getTransactionId());
		headersMap.put(NotificationHeaderEnum.TRAN_EXEC_DATE_TIME.getHeaderName(), getTranExecDateTime());
		
		headersMap.put(NotificationHeaderEnum.ERROR_CODE.getHeaderName(), getErrorCode());
		headersMap.put(NotificationHeaderEnum.ERROR_MESSAGE.getHeaderName(), getErrorMessage());
		
		headersMap.put(NotificationHeaderEnum.UMG_ADMIN_URL.getHeaderName(), getUmgAdminUrl());
		
		headersMap.put(NotificationHeaderEnum.VERSION_ID.getHeaderName(), getVersionId());
		headersMap.put(NotificationHeaderEnum.MODEL_APPROVAL_URL.getHeaderName(), getModelApprovalURL());
		
		headersMap.put(NotificationHeaderEnum.AUTH_TOKEN.getHeaderName(), getAuthCode());
		headersMap.put(NotificationHeaderEnum.ACTIVATION_DATE.getHeaderName(), getAuthCodeActiveDate());
		
		headersMap.put(NotificationHeaderEnum.ACTIVE_FROM.getHeaderName(), getActiveFrom());
		headersMap.put(NotificationHeaderEnum.ACTIVE_UNTIL.getHeaderName(), getActiveUntil());
		
		headersMap.put(NotificationHeaderEnum.BATCH_ENABLED.getHeaderName(), String.valueOf(isBatchEnabled()));
		headersMap.put(NotificationHeaderEnum.BULK_ENABLED.getHeaderName(), String.valueOf(isBulkEnabled()));
		
		headersMap.put(NotificationHeaderEnum.EMAIL_NOTIFICATION_ENABLED.getHeaderName(),String.valueOf(isEmailNotificationEnabled()));
		headersMap.put(NotificationHeaderEnum.RESET_REASON.getHeaderName(), getResetReason());
		
		headersMap.put(NotificationHeaderEnum.RESET_BY.getHeaderName(), getResetBy());
		headersMap.put(NotificationHeaderEnum.TENANT_ONBOARDED_BY.getHeaderName(), getTenantOnboardedBy());
		
		headersMap.put(NotificationHeaderEnum.TENANT_ONBOARDED_ON.getHeaderName(), getTenantOnboardedOn());

        headersMap.put(NotificationHeaderEnum.MODELET_HOST.getHeaderName(), getModeletHost());
        headersMap.put(NotificationHeaderEnum.PORT.getHeaderName(), String.valueOf(getPort()));
        headersMap.put(NotificationHeaderEnum.POOL_NAME.getHeaderName(), getPoolName());
        headersMap.put(NotificationHeaderEnum.R_SERVE_PORT.getHeaderName(), getrServePort());
        headersMap.put(NotificationHeaderEnum.LOADED_MODEL.getHeaderName(), getLoadedModel());
        headersMap.put(NotificationHeaderEnum.LOADED_MODEL_VERSION.getHeaderName(), getLoadedModelVersion());
        headersMap.put(NotificationHeaderEnum.REASON.getHeaderName(), getReason());
        headersMap.put(NotificationHeaderEnum.MODEL_TO_LOAD.getHeaderName(), getModelToLoad());
        headersMap.put(NotificationHeaderEnum.MODEL_VERSION_TO_LOAD.getHeaderName(), getModelVersionToLoad());
        headersMap.put(NotificationHeaderEnum.MODEL_VERSION_NAME_TO_LOAD.getHeaderName(), getModelVersionNameToLoad());
        headersMap.put(NotificationHeaderEnum.NEW_POOL_NAME.getHeaderName(), getNewPoolName());
        headersMap.put(NotificationHeaderEnum.TRANSACTION_RUN_DATE.getHeaderName(), getTransactionRunDate());
        headersMap.put(NotificationHeaderEnum.RESTART_INITIATED_TIME.getHeaderName(), getRestartInitiatedTime());
		headersMap.put(NotificationHeaderEnum.MODELET_LIST.getHeaderName() , getModeletList());
		headersMap.put(NotificationHeaderEnum.MODELET_START_TIME.getHeaderName(), getModelStartTime());
		headersMap.put(NotificationHeaderEnum.CLIENT_TRANSACTION_ID.getHeaderName(), getClienttransactionId());
		headersMap.put(NotificationHeaderEnum.EXCESS_RUNTIME.getHeaderName(), getExcessiveRuntime());		
		headersMap.put(NotificationHeaderEnum.EXEC_COUNT.getHeaderName(), getExecLimit());
		headersMap.put(NotificationHeaderEnum.RESTART_COUNT.getHeaderName(), getRestartCount());
		headersMap.put(NotificationHeaderEnum.NEW_STSTUS.getHeaderName(), getNewstatus());
		headersMap.put(NotificationHeaderEnum.OLD_STSTUS.getHeaderName(), getOldstatus());
		return headersMap;
	}	
}
