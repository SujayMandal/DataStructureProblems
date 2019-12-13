package com.ca.umg.business.dashboard.info;

public class ModelVersionStatus {//NOPMD
	
	private Long count;
	
	private String versionStatus;
	
	private String modelName;
	
	private Long successCount;
	
	private Long failureCount;
	
	private String modelVersion;
	
	private String modelResponseTime;
	
	private String endToEndTime;
	
	private String modelUtilization;
	
	private Long inputValidationFailure;
	
	private Long outputValidationFailure;
	
	private String transactionId;
	
	private String errorCode;
	
	private String runDate;
	
	private String errorDescription;
	
	private String transactionMode;
	
	private String clientTransactionId;
	
	
	
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getVersionStatus() {
		return versionStatus;
	}

	public void setVersionStatus(String versionStatus) {
		this.versionStatus = versionStatus;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Long successCount) {
		this.successCount = successCount;
	}

	public Long getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(Long failureCount) {
		this.failureCount = failureCount;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

	public String getModelResponseTime() {
		return modelResponseTime;
	}

	public void setModelResponseTime(String modelResponseTime) {
		this.modelResponseTime = modelResponseTime;
	}

	public String getEndToEndTime() {
		return endToEndTime;
	}

	public void setEndToEndTime(String endToEndTime) {
		this.endToEndTime = endToEndTime;
	}

	public String getModelUtilization() {
		return modelUtilization;
	}

	public void setModelUtilization(String modelUtilization) {
		this.modelUtilization = modelUtilization;
	}

	public Long getInputValidationFailure() {
		return inputValidationFailure;
	}

	public void setInputValidationFailure(Long inputValidationFailure) {
		this.inputValidationFailure = inputValidationFailure;
	}

	public Long getOutputValidationFailure() {
		return outputValidationFailure;
	}

	public void setOutputValidationFailure(Long outputValidationFailure) {
		this.outputValidationFailure = outputValidationFailure;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactioId) {
		this.transactionId = transactioId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getRunDate() {
		return runDate;
	}

	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getClientTransactionId() {
		return clientTransactionId;
	}

	public void setClientTransactionId(String clientTransactionId) {
		this.clientTransactionId = clientTransactionId;
	}
	
}
