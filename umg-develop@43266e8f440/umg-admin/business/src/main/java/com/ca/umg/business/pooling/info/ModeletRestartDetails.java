package com.ca.umg.business.pooling.info;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.framework.core.entity.ModeletRestartInfo;

public class ModeletRestartDetails {
	
	private List<ModeletRestartInfo> modeletRestartInfoList;
	private Map<String , String> tenants;	
	private Map<String, Set<String>> modelNamesByTenant;
	
	public Map<String, String> getTenants() {
		return tenants;
	}

	public void setTenants(Map<String, String> tenants) {
		this.tenants = tenants;
	}

	public Map<String, Set<String>> getModelNamesByTenant() {
		return modelNamesByTenant;
	}

	public void setModelNamesByTenant(Map<String, Set<String>> modelNamesByTenant) {
		this.modelNamesByTenant = modelNamesByTenant;
	}

	public List<ModeletRestartInfo> getModeletRestartInfoList() {
		return modeletRestartInfoList;
	}

	public void setModeletRestartInfoList(List<ModeletRestartInfo> modeletRestartInfoList) {
		this.modeletRestartInfoList = modeletRestartInfoList;
	}

	
}
