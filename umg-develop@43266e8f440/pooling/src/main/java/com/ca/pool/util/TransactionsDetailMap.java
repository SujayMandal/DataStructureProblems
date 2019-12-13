package com.ca.pool.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.ca.pool.model.TransactionCriteria;

import javax.inject.Named;

/**
 * @author basanaga
 *This class used to hold the local cache details
 */
@Named
public class TransactionsDetailMap {

	private final Map<String, TransactionCriteria> excesiveRuntimeMap = new ConcurrentHashMap<String, TransactionCriteria>();

	private final Map<String, Boolean> txnTimeOutMap = new ConcurrentHashMap<String, Boolean>();

	/**
	 * This method used to get the txn time out map in runtime
	 * 
	 * @return
	 */
	public Map<String, Boolean> getTxnTimeOutMap() {
		return txnTimeOutMap;
	}

	/**
	 * This method used to get the excessive runtime map
	 * 
	 * @return
	 */
	public Map<String, TransactionCriteria> getExcesiveRuntimeMap() {
		return excesiveRuntimeMap;
	}
}
