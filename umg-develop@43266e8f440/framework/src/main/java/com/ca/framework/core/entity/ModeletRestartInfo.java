package com.ca.framework.core.entity;

import java.io.Serializable;

public class ModeletRestartInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5095256746770057039L;

	private String id;
	
	private String tenantId;
	
	private String modelNameAndVersion;		

	private int restartCount;
	
	private int execCount;
	
	private String modeletHostKey;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public int getRestartCount() {
		return restartCount;
	}

	public void setRestartCount(int restartCount) {
		this.restartCount = restartCount;
	}
	
	public String getModelNameAndVersion() {
		return modelNameAndVersion;
	}

	public void setModelNameAndVersion(String modelNameAndVersion) {
		this.modelNameAndVersion = modelNameAndVersion;
	}
	
	public int getExecCount() {
		return execCount;
	}

	public void setExecCount(int execCount) {
		this.execCount = execCount;
	}
	
	public String getModeletHostKey() {
		return modeletHostKey;
	}

	public void setModeletHostKey(String modeletHostKey) {
		this.modeletHostKey = modeletHostKey;
	}

	

}
