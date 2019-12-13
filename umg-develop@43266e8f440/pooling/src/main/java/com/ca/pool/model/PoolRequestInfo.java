package com.ca.pool.model;

import java.util.List;

import com.ca.modelet.ModeletClientInfo;

public class PoolRequestInfo {

	private String request;
	private String deletedPoolName;
	private List<ModeletClientInfo> movedClientInfo;
	
	public String getRequest() {
		return request;
	}
	
	public void setRequest(String request) {
		this.request = request;
	}
	
	public List<ModeletClientInfo> getMovedClientInfo() {
		return movedClientInfo;
	}
	
	public void setMovedClientInfo(List<ModeletClientInfo> movedClientInfo) {
		this.movedClientInfo = movedClientInfo;
	}

	public String getDeletedPoolName() {
		return deletedPoolName;
	}

	public void setDeletedPoolName(String deletedPoolName) {
		this.deletedPoolName = deletedPoolName;
	}
}
