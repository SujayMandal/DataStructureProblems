package com.ca.umg.business.tenant.report.usage;

public class UsageTransactionInfo {
	private String tenantTransactionId;
	private String tenantId;
	private String transactionMode;
	private String batchId;
	private String model;
	private String modelVersion;
	private String runDateTime;
	private String processingStatus;
	private String failureReason;
    private String processingTime;
    private String umgTransactionId;
    private String transactionType;
	private boolean selected;

	public String getTenantTransactionId() {
		return tenantTransactionId;
	}

	public void setTenantTransactionId(final String tenantTransactionId) {
		this.tenantTransactionId = tenantTransactionId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(final String tenantId) {
		this.tenantId = tenantId;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(final String batchId) {
		this.batchId = batchId;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(final String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getModel() {
		return model;
	}

	public void setModel(final String model) {
		this.model = model;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(final String modelVersion) {
		this.modelVersion = modelVersion;
	}

	public void setRunDateTime(final String runDateTime) {
		this.runDateTime = runDateTime;
	}

	public String getRunDateTime() {
		return runDateTime;
	}

	public void setProcessingStatus(final String processingStatus) {
		this.processingStatus = processingStatus;
	}

	public String getProcessingStatus() {
		return processingStatus;
	}

	public void setFailureReason(final String failureReason) {
		this.failureReason = failureReason;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setProcessingTime(final String processingTime) {
		this.processingTime = processingTime;
	}

	public String getProcessingTime() {
		return processingTime;
	}

	public void setUMGTransactionId(final String umgTransactionId) {
		this.umgTransactionId = umgTransactionId;
	}

	public String getUMGTransactionId() {
		return umgTransactionId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(final boolean selected) {
		this.selected = selected;
	}
}