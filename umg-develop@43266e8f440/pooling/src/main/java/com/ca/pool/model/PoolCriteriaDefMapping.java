package com.ca.pool.model;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;

public class PoolCriteriaDefMapping implements Serializable{

    private static final long serialVersionUID = -789196778281352042L;

    private String id;
	private String poolId;
	private String poolName;
	private String poolCriteriaValue;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPoolId() {
		return poolId;
	}
	
	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}
	
	public String getPoolName() {
		return poolName;
	}
	
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	
	public String getPoolCriteriaValue() {
		return poolCriteriaValue;
	}
	
	public void setPoolCriteriaValue(String poolCriteriaValue) {
		this.poolCriteriaValue = poolCriteriaValue;
	}
	
	@Override
	public String toString() {
		return toStringHelper(this.getClass()).add("Id:", id).
				add("Pool Id:", poolId).
				add("Pool Name:", poolName).
				add("Pool Criteria Value:", poolCriteriaValue).toString();	
	}
}