package com.ca.umg.business.batching.info;

@SuppressWarnings("PMD.TooManyFields")
public class BatchTransactionInfo {

	private String id;

	private String batchInputFile;

	private String batchOutputFile;

	private String status;

	private boolean isTestBatch;

	private Long startTime;

	private Long endTime;

	private Long totalRecords;

	private Long successCount;

	private Long failCount;
	
	private String fromDate;

	private String toDate;
	
	private String batchExecTime;
	
	private Long createdDate;
	
	private String createdDateTime;
	
	private String transactionMode;
	
	private String execEnv;
	
	private String modellingEnv;
	
	private Long notPickedCount;
	
	private Long txnInProgressCount; 
	 
	public Long getTxnInProgressCount() {
		return txnInProgressCount;
	}

	public void setTxnInProgressCount(Long txnInProgressCount) {
		this.txnInProgressCount = txnInProgressCount;
	}

	public Long getNotPickedCount() {
		return notPickedCount;
	}

	public void setNotPickedCount(Long notPickedCount) {
		this.notPickedCount = notPickedCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchInputFile() {
		return batchInputFile;
	}

	public void setBatchInputFile(String batchInputFile) {
		this.batchInputFile = batchInputFile;
	}

	public String getBatchOutputFile() {
		return batchOutputFile;
	}

	public void setBatchOutputFile(String batchOutputFile) {
		this.batchOutputFile = batchOutputFile;
	}

	public boolean isTest() {
		return isTestBatch;
	}

	public void setTest(boolean isTestBatch) {
		this.isTestBatch = isTestBatch;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Long successCount) {
		this.successCount = successCount;
	}

	public Long getFailCount() {
		return failCount;
	}

	public void setFailCount(Long failCount) {
		this.failCount = failCount;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getBatchExecTime() {
		return batchExecTime;
	}

	public void setBatchExecTime(String batchExecTime) {
		this.batchExecTime = batchExecTime;
	}
	
	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}
	
	public String getExecEnv() {
		return execEnv;
	}

	public void setExecEnv(String execEnv) {
		this.execEnv = execEnv;
	}

	public String getModellingEnv() {
		return modellingEnv;
	}

	public void setModellingEnv(String modellingEnv) {
		this.modellingEnv = modellingEnv;
	}

}