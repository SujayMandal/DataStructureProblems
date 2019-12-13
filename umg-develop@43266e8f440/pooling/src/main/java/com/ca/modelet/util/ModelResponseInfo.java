package com.ca.modelet.util;

public class ModelResponseInfo {

	private String modelName;
	private String responseHeaderInfo;
	private Long modelExecutionTime;
	private Long modeletExecutionTime;

	private Object payload;

	public Object getPayload() {
		return payload;
	}

	public void setPayload(final Object payload) {
		this.payload = payload;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getResponseHeaderInfo() {
		return responseHeaderInfo;
	}

	public void setResponseHeaderInfo(String responseHeaderInfo) {
		this.responseHeaderInfo = responseHeaderInfo;
	}

	public Long getModelExecutionTime() {
		return modelExecutionTime;
	}

	public void setModelExecutionTime(Long modelExecutionTime) {
		this.modelExecutionTime = modelExecutionTime;
	}

	public Long getModeletExecutionTime() {
		return modeletExecutionTime;
	}

	public void setModeletExecutionTime(Long modeletExecutionTime) {
		this.modeletExecutionTime = modeletExecutionTime;
	}

}