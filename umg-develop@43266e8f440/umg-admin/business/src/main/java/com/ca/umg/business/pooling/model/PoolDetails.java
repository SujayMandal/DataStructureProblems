package com.ca.umg.business.pooling.model;

public class PoolDetails {

	private String poolName;
	private String poolDescription;
	private String environment;
	private String tenant;
	private String executionMode;
	private String executionType;
	private String model;
	private Integer allocationPriority;
	private Integer totalModelets;
	
	public String getPoolName() {
		return poolName;
	}
	
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	
	public String getPoolDescription() {
		return poolDescription;
	}
	
	public void setPoolDescription(String poolDescription) {
		this.poolDescription = poolDescription;
	}
	
	public String getEnvironment() {
		return environment;
	}
	
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	public String getTenant() {
		return tenant;
	}
	
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	
	public String getExecutionMode() {
		return executionMode;
	}
	
	public void setExecutionMode(String executionMode) {
		this.executionMode = executionMode;
	}
	
	public String getExecutionType() {
		return executionType;
	}
	
	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public Integer getAllocationPriority() {
		return allocationPriority;
	}
	
	public void setAllocationPriority(Integer allocationPriority) {
		this.allocationPriority = allocationPriority;
	}
	
	public Integer getTotalModelets() {
		return totalModelets;
	}

	public void setTotalModelets(Integer totalModelets) {
		this.totalModelets = totalModelets;
	}
}