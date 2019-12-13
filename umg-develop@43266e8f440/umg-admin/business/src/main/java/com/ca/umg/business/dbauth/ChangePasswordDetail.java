/**
 * 
 */
package com.ca.umg.business.dbauth;

/**
 * @author nigampra
 *
 */
public class ChangePasswordDetail {

	private String userName;
	private String currentPassword;
	private String newPassword;
	private String confirmPassword;
	private String tenantCode;

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(final String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(final String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(final String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(final String tenantCode) {
		this.tenantCode = tenantCode;
	}
}