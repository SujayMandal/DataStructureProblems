package com.ca.umg.business.systemparam.info;

import com.ca.framework.core.info.BaseInfo;

public class SystemParameterInfo extends BaseInfo {

	private static final long serialVersionUID = 1L;

	private String sysKey;
	private String sysValue;
	private String description;
	private char isActive;

	public String getSysKey() {
		return sysKey;
	}

	public void setSysKey(String sysKey) {
		this.sysKey = sysKey;
	}

	public String getSysValue() {
		return sysValue;
	}

	public void setSysValue(String sysValue) {
		this.sysValue = sysValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getIsActive() {
		return isActive;
	}

	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}
}
