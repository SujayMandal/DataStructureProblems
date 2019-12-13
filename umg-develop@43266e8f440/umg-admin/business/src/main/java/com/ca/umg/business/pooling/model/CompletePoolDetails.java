
package com.ca.umg.business.pooling.model;

import java.util.List;
import java.util.Set;

import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteriaDetails;
import com.ca.pool.model.PoolUsageOrderMapping;

public class CompletePoolDetails {

	private Pool pool;
	private Set<PoolUsageOrderMapping> poolUsageOrderMapping;
	private List<ModeletClientInfo> modeletClientInfoList;
	private String poolCriteria;
	private PoolCriteriaDetails poolCriteriaDetails;
	private List<String> tenantSpecificModel;
	
	public Pool getPool() {
		return pool;
	}

	public void setPool(Pool pool) {
		this.pool = pool;
	}

	public Set<PoolUsageOrderMapping> getPoolUsageOrderMapping() {
		return poolUsageOrderMapping;
	}

	public void setPoolUsageOrderMapping(Set<PoolUsageOrderMapping> poolUsageOrderMapping) {
		this.poolUsageOrderMapping = poolUsageOrderMapping;
	}

	public List<ModeletClientInfo> getModeletClientInfoList() {
		return modeletClientInfoList;
	}

	public void setModeletClientInfoList(List<ModeletClientInfo> modeletClientInfoList) {
		this.modeletClientInfoList = modeletClientInfoList;
	}

	public String getPoolCriteria() {
		return poolCriteria;
	}

	public void setPoolCriteria(String poolCriteria) {
		this.poolCriteria = poolCriteria;
	}

	public PoolCriteriaDetails getPoolCriteriaDetails() {
		return poolCriteriaDetails;
	}

	public void setPoolCriteriaDetails(PoolCriteriaDetails poolCriteriaDetails) {
		this.poolCriteriaDetails = poolCriteriaDetails;
	}

	public List<String> getTenantSpecificModel() {
		return tenantSpecificModel;
	}

	public void setTenantSpecificModel(List<String> tenantSpecificModel) {
		this.tenantSpecificModel = tenantSpecificModel;
	}
}