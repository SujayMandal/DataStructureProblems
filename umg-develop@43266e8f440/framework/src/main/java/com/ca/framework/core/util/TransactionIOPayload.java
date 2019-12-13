package com.ca.framework.core.util;

import java.util.Map;

/**
 * @author basanaga
 * 
 * This class used to store Tenant IO and Model IO
 *
 */
public class TransactionIOPayload {

	private String transactionId;

	private Map<String, Object> txnIOPayload;	

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public Map<String, Object> getTxnIOPayload() {
		return txnIOPayload;
	}

	public void setTxnIOPayload(Map<String, Object> txnIOPayload) {
		this.txnIOPayload = txnIOPayload;
	}

	
}
