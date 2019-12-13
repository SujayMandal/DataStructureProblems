package com.ca.pool;

import com.ca.framework.core.constants.PoolConstants;

public enum TransactionType {

	PROD("Prod"),
	TEST("Test"),
	ANY(PoolConstants.ANY);
	
	private final String type;
	
	private TransactionType(final String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public static TransactionType getTransactionTypeByType(final String type) {
		TransactionType transactionType;
		if (type != null && type.equalsIgnoreCase(PROD.getType())) {
			transactionType = PROD;
		} else if (type != null && type.equalsIgnoreCase(TEST.getType())){
			transactionType = TEST;
		} else {
			transactionType = ANY;
		}
		
		return transactionType;
	}
}