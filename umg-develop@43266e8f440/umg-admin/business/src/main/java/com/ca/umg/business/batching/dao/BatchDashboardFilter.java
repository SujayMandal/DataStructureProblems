package com.ca.umg.business.batching.dao;

import com.ca.umg.business.common.info.PagingInfo;

@SuppressWarnings("PMD")
public class BatchDashboardFilter extends PagingInfo{

    private static final long serialVersionUID = 1L;

    private String batchId;
	private String inputFileName;
	private String fromDate;
	private String toDate;
	private long matchedTransactionCount;
	private Long startTime;
    private Long endTime;
	
	public String getBatchId() {
		return batchId;
	}
	
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public String getInputFileName() {
		return inputFileName;
	}
	
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
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
	
	public long getMatchedTransactionCount() {
		return matchedTransactionCount;
	}

	public void setMatchedTransactionCount(final long matchedTransactionCount) {
		this.matchedTransactionCount = matchedTransactionCount;
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
}