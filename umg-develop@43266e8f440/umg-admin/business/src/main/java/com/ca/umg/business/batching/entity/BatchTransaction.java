package com.ca.umg.business.batching.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

@Entity
@Table(name = "BATCH_TRANSACTION")
public class BatchTransaction extends MultiTenantEntity {

	private static final long serialVersionUID = 7021977357117821435L;

	@NotNull(message = "Batch file name cannot be null")
	@NotBlank(message = "Batch file name cannot be empty")
	@Column(name = "BATCH_INPUT_FILE")
	@Property
	private String batchInputFile;

	@Column(name = "BATCH_OUTPUT_FILE")
	@Property
	private String batchOutputFile;

	@NotNull(message = "Status cannot be null")
	@NotBlank(message = "Status cannot be empty")
	@Column(name = "STATUS")
	@Property
	private String status;

	@Column(name = "IS_TEST")
	@Property
	private boolean isTestBatch;

	@Column(name = "START_TIME")
	@Property
	private Long startTime;

	@Column(name = "END_TIME")
	@Property
	private Long endTime;

	@Column(name = "TOTAL_RECORDS")
	@Property
	private Long totalRecords;

	@Column(name = "SUCCESS_COUNT")
	@Property
	private Long successCount;

	@Column(name = "FAIL_COUNT")
	@Property
	private Long failCount;
	
	@Column(name = "NOT_PICKED_COUNT")
    @Property
    private Long notPickedCount;
	
	@Column(name = "TRANSACTION_MODE")
	@NotNull(message = "Transaction Mode cannot be null")
	@Property
	private String transactionMode;
	
	@Column(name = "USER")
	@Property		
	private String user;
	
	@Column(name = "MODEL_NAME")
	@Property		
	private String modelName;
	
	@Column(name = "MODEL_VERSION")
	@Property		
	private Double modelVersion;
	
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
	
	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Double getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(Double modelVersion) {
		this.modelVersion = modelVersion;
	}
	
	public Long getNotPickedCount() {
		return notPickedCount;
	}

	public void setNotPickedCount(Long notPickedCount) {
		this.notPickedCount = notPickedCount;
	}
	
}