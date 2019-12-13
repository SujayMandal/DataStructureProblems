package com.ca.umg.business.dbauth;

import com.google.common.base.Objects;

public class UMGUser {

	private String username;
	private String password;
	private int enabled;
	private String tenantCode;
	private String name;
	private String officialEmail;
	private String organization;
	private String comments;
	private Long createdDate;
	private Long lastActivatedDate;
	private Long lastDeactivatedDate;
	private String role;
	private UMGUserStatus userStatus;

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(final int enabled) {
		this.enabled = enabled;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(final String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getOfficialEmail() {
		return officialEmail;
	}

	public void setOfficialEmail(final String officialEmail) {
		this.officialEmail = officialEmail;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(final String organization) {
		this.organization = organization;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Long createdDate) {
		this.createdDate = createdDate;
	}

	public Long getLastActivatedDate() {
		return lastActivatedDate;
	}

	public void setLastActivatedDate(final Long lastActivatedDate) {
		this.lastActivatedDate = lastActivatedDate;
	}

	public Long getLastDeactivatedDate() {
		return lastDeactivatedDate;
	}

	public void setLastDeactivatedDate(final Long lastDeactivatedDate) {
		this.lastDeactivatedDate = lastDeactivatedDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(final String role) {
		this.role = role;
	}

	public void setUserStatus(final UMGUserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public UMGUserStatus getUserStatus() {
		return userStatus;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(UMGUser.class).add("Username", username).add("Name", name).add("Role", role).toString();
	}
}