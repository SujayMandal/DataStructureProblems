package com.ca.umg.business.batching.execution;

import java.io.Serializable;
import java.util.Map;

public class RequestObj implements Serializable {
	private static final long serialVersionUID = 1L;
	private String batchId;
	private Map<String, Object> jsonData;
	
	public RequestObj(String batchId, Map<String, Object> jsonData) {
		this.batchId = batchId;
		this.jsonData = jsonData;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public Map<String, Object> getJsonData() {
		return jsonData;
	}

	public void setJsonData(Map<String, Object> jsonData) {
		this.jsonData = jsonData;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "RequestObj [batchId=" + batchId + ", jsonData=" + jsonData
				+ "]";
	}

}