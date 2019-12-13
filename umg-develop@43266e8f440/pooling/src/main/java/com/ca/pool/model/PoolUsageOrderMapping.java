package com.ca.pool.model;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;

public class PoolUsageOrderMapping implements Serializable, Comparable<PoolUsageOrderMapping>{

    private static final long serialVersionUID = 6512657658736110721L;

    private String id;
	private String poolId;
	private String poolName;
	private String poolUsageId;
	private String poolUsageName;
	private Integer poolTryOrder;
	
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
	
	public String getPoolUsageId() {
		return poolUsageId;
	}
	
	public void setPoolUsageId(String poolUsageId) {
		this.poolUsageId = poolUsageId;
	}
	
	public String getPoolUsageName() {
		return poolUsageName;
	}
	
	public void setPoolUsageName(String poolUsageName) {
		this.poolUsageName = poolUsageName;
	}
	
	public Integer getPoolTryOrder() {
		return poolTryOrder;
	}
	
	public void setPoolTryOrder(Integer poolTryOrder) {
		this.poolTryOrder = poolTryOrder;
	}

	@Override
    public int compareTo(final PoolUsageOrderMapping that) {
	    return this.getPoolTryOrder().compareTo(that.getPoolTryOrder());
    }
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof PoolUsageOrderMapping) {
			return this.getPoolTryOrder().equals(((PoolUsageOrderMapping) obj).getPoolTryOrder());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getPoolTryOrder();
	}

	@Override
    public String toString() {
        return toStringHelper(this.getClass()).add("Id:", id).
        		add("Pool Id:", poolId).
        		add("Pool Name:", poolName).
        		add("Pool Usage Id:", poolUsageId).
        		add("Pool Usage Name:", poolUsageName).
        		add("Pool Try Oder:", poolTryOrder).toString();
    }
}