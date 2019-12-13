package com.ca.umg.rt.core.flow.entity;

/**
 * added this object for passing parameters for ime2 umg-3555  
 * @author raddibas
 *
 */
public class TransactionType {

	private String tenantCode;
	private String requestType;
	private String executionEnvironment;
	//this property can be set as null
	private String executionEnvironmentVersion;
	
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getExecutionEnvironment() {
		return executionEnvironment;
	}
	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}
	public String getExecutionEnvironmentVersion() {
		return executionEnvironmentVersion;
	}
	public void setExecutionEnvironmentVersion(String executionEnvironmentVersion) {
		this.executionEnvironmentVersion = executionEnvironmentVersion;
	}
	
	
}
