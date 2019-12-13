/**
 * 
 */
package com.ca.umg.rt.batching.http;

import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

/**
 * @author mandasuj
 *
 */
public class BatchTransactionCounter {
	
	private final String batchId;
	
	private final int totalCount;
	
	private int successCount;
	
	private int failCount;
	
	private int notPickedCount;
	
	BatchTransactionCounter(String batchId,int totalCount){
		this.batchId = batchId;
		this.totalCount = totalCount;
		this.notPickedCount = totalCount;
		this.successCount = RuntimeConstants.INT_ZERO;
		this.failCount = RuntimeConstants.INT_ZERO;
	}
	
	public void updateNotPickedCount(int requestCount){
		this.notPickedCount = this.totalCount-requestCount;
	}
	
	public void incrementCount(boolean success){
		if(success){
			this.successCount++;
		} else {
			this.failCount++;
		}
	}

	public String getBatchId() {
		return batchId;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public int getNotPickedCount() {
		return notPickedCount;
	}

}
