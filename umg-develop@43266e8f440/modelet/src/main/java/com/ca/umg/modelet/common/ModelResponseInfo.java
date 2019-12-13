package com.ca.umg.modelet.common;

public class ModelResponseInfo {
	
	private String modelName;
	private ResponseHeaderInfo responseHeaderInfo;
	private Object payload;
	
	/**
	 * Time taken to execute model
	 */
	private Long modelExecutionTime;
	
	/**
	 * Time taken to execute modelet (model execution time + initialization time + other time)
	 */
	private Long modeletExecutionTime;
	
	private String freeMemoreyAtStart;
	    
	private double cpuUsageAtStart;
	
    private String freeMemorey;
    
	private double cpuUsage;
	
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}
	public Object getPayload() {
		return payload;
	}
	public void setPayload(final Object payload) {
		this.payload = payload;
	}
    public ResponseHeaderInfo getResponseHeaderInfo() {
        return responseHeaderInfo;
    }
    public void setResponseHeaderInfo(ResponseHeaderInfo responseHeaderInfo) {
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
	public String getFreeMemorey() {
		return freeMemorey;
	}
	public void setFreeMemorey(String freeMemorey) {
		this.freeMemorey = freeMemorey;
	}
	public double getCpuUsage() {
		return cpuUsage;
	}
	public void setCpuUsage(double cpuUsage) {
		this.cpuUsage = cpuUsage;
	}
	public String getFreeMemoreyAtStart() {
		return freeMemoreyAtStart;
	}
	public void setFreeMemoreyAtStart(String freeMemoreyAtStart) {
		this.freeMemoreyAtStart = freeMemoreyAtStart;
	}
	public double getCpuUsageAtStart() {
		return cpuUsageAtStart;
	}
	public void setCpuUsageAtStart(double cpuUsageAtStart) {
		this.cpuUsageAtStart = cpuUsageAtStart;
	}
	
}