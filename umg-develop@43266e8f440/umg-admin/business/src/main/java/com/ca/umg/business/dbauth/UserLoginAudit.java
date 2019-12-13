package com.ca.umg.business.dbauth;


public class UserLoginAudit {

	private String username;
	private String ipAddress;
	private Long accessTime;
	private UserLoginActivity activity;
	private String reasonCode;
	private String tenantCode;

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Long getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(final Long accessTime) {
		this.accessTime = accessTime;
	}

	public UserLoginActivity getActivity() {
		return activity;
	}

	public void setActivity(final UserLoginActivity activity) {
		this.activity = activity;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(final String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(final String tenantCode) {
		this.tenantCode = tenantCode;
	}
}