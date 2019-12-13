package com.ca.umg.business.transaction.mongo.info;

import java.util.Map;


@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessivePublicCount" })
public class TransactionDocumentForApi {

    private String transactionId;
    private String clientTransactionID;
    private String versionName;
    private Integer majorVersion;
    private Integer minorVersion;
    private String status;
    private String errorCode;
    private String errorDescription;
    private String tenantId;
    private Long createdDate;
    private Long runAsOfDate;
    private String runAsOfDateTime;
    private String createdDateTime;
    //added createdBy and execution group for umg-4698
    private String createdBy;
    private String executionGroup;
    
  //added for umg-4849
    private Map<String, Object> tenantOutput;
    private Map<String, Object> tenantInput;

    public Map<String, Object> getTenantOutput() {
        return tenantOutput;
    }

    public void setTenantOutput(Map<String, Object> tenantOutput) {
        this.tenantOutput = tenantOutput;
    }

    public Map<String, Object> getTenantInput() {
        return tenantInput;
    }

    public void setTenantInput(Map<String, Object> tenantInput) {
        this.tenantInput = tenantInput;
    }

    public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getClientTransactionID() {
        return clientTransactionID;
    }

    public void setClientTransactionID(String clientTransactionID) {
        this.clientTransactionID = clientTransactionID;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Long populateCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long populateRunAsOfDate() {
        return runAsOfDate;
    }

    public void setRunAsOfDate(Long runAsOfDate) {
        this.runAsOfDate = runAsOfDate;
    }

    public String getRunAsOfDateTime() {
        return runAsOfDateTime;
    }

    public void setRunAsOfDateTime(String runAsOfDateTime) {
        this.runAsOfDateTime = runAsOfDateTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getExecutionGroup() {
        return executionGroup;
    }

    public void setExecutionGroup(String executionGroup) {
        this.executionGroup = executionGroup;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
