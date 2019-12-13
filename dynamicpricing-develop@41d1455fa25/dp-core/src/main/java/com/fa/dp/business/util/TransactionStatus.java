package com.fa.dp.business.util;

public enum TransactionStatus {
 
	SUCCESS("SUCCESS"),
	FAIL("FAIL");
	
	private TransactionStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}

	private final String tranStatus;
	
	
	public String getTranStatus() {
		return tranStatus;
	}


	
	
	
}
