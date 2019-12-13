package com.ca.pool.model;

import java.io.Serializable;
import java.util.List;

import com.ca.modelet.ModeletClientInfo;

public class PoolStatusStats implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ModeletClientInfo> registeredModelets;
	
	private List<ModeletClientInfo> unRegisteredModelets;
	
	private List<ModeletClientInfo> registeredInProgressModelets;
	
	private List<ModeletClientInfo> busyModelets;
	
	private List<ModeletClientInfo> failedModelets;

	public List<ModeletClientInfo> getRegisteredModelets() {
		return registeredModelets;
	}

	public void setRegisteredModelets(List<ModeletClientInfo> registeredModelets) {
		this.registeredModelets = registeredModelets;
	}

	public List<ModeletClientInfo> getUnRegisteredModelets() {
		return unRegisteredModelets;
	}

	public void setUnRegisteredModelets(List<ModeletClientInfo> unRegisteredModelets) {
		this.unRegisteredModelets = unRegisteredModelets;
	}

	public List<ModeletClientInfo> getRegisteredInProgressModelets() {
		return registeredInProgressModelets;
	}

	public void setRegisteredInProgressModelets(List<ModeletClientInfo> registeredInProgressModelets) {
		this.registeredInProgressModelets = registeredInProgressModelets;
	}

	public List<ModeletClientInfo> getBusyModelets() {
		return busyModelets;
	}

	public void setBusyModelets(List<ModeletClientInfo> busyModelets) {
		this.busyModelets = busyModelets;
	}

	public List<ModeletClientInfo> getFailedModelets() {
		return failedModelets;
	}

	public void setFailedModelets(List<ModeletClientInfo> failedModelets) {
		this.failedModelets = failedModelets;
	}
}
