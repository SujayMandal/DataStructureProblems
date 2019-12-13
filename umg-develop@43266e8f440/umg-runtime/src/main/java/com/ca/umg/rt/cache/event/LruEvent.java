/**
 * 
 */
package com.ca.umg.rt.cache.event;

import java.io.Serializable;

/**
 * @author raddibas
 * 
 */
public class LruEvent implements Serializable {
	private static final long serialVersionUID = 5454686393029209589L;
	public static final String SUCCESS = "success";
    public static final String TIMEOUT = "Time-Out";
    public static final String TERMINATED = "terminated";
	private String status;
	private String batchId;
	private int processedCount;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public int getProcessedCount() {
		return processedCount;
	}

	public void setProcessedCount(int processedCount) {
		this.processedCount = processedCount;
	}
}
