package com.ca.umg.business.dashboard.info;


public class ModelUsageInfo {

	private Long transactionCount;
	
	private String modelName;
	
	private String interval;

	public Long getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(Long transactionCount) {
		this.transactionCount = transactionCount;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

}
