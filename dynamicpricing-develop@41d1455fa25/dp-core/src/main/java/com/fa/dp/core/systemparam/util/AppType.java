package com.fa.dp.core.systemparam.util;

public enum AppType {
	
	DPA("dpa");
	
	AppType(final String appCode){
		this.appCode = appCode;
	}
	
	private final String appCode;

	public String getAppCode() {
		return appCode;
	}
	
	

}
