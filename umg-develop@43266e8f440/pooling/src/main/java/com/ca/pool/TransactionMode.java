package com.ca.pool;

import com.ca.framework.core.constants.PoolConstants;

public enum TransactionMode {

	BATCH("Batch"),
	ONLINE("Online"),
	BULK("Bulk"),
	ANY(PoolConstants.ANY);
	
	private final String mode;
	
	private TransactionMode(final String mode) {
		this.mode = mode;
	}
	
	public static TransactionMode getTransactionMode(final String batchId) {
		TransactionMode transactionMode;
		if (batchId != null) {
			transactionMode = BATCH;
		} else {
			transactionMode = ONLINE;
		}
		
		return transactionMode;
	}
	
	public static TransactionMode getTransactionModeByMode(final String type) {
		TransactionMode transactionMode;
		if (type != null && type.equalsIgnoreCase(BATCH.getMode())) {
			transactionMode = BATCH;
		} else if (type != null && type.equalsIgnoreCase(ONLINE.getMode())){
			transactionMode = ONLINE;
		} else if (type != null && type.equalsIgnoreCase(BULK.getMode())){
			transactionMode = BULK;
		} else {
			transactionMode = ANY;
		}
		
		return transactionMode;
	}
	
	public String getMode() {
		return mode;
	}
}