package com.ca.umg.business.version.info;

public class SwitchTenantUrlInfo {
	 private String currentTenantCode;
	 private String switchToTenantCode;
	 private Boolean tenantMismatchFlag = false;
	 
	public String getCurrentTenantCode() {
		return currentTenantCode;
	}
	public void setCurrentTenantCode(String currentTenantCode) {
		this.currentTenantCode = currentTenantCode;
	}
	public String getSwitchToTenantCode() {
		return switchToTenantCode;
	}
	public void setSwitchToTenantCode(String switchToTenantCode) {
		this.switchToTenantCode = switchToTenantCode;
	}
	public Boolean getTenantMismatchFlag() {
		return tenantMismatchFlag;
	}
	public void setTenantMismatchFlag(Boolean tenantMismatchFlag) {
		this.tenantMismatchFlag = tenantMismatchFlag;
	}
}
