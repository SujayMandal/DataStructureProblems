package com.ca.umg.business.dbauth;

import static com.google.common.base.Objects.toStringHelper;

import org.springframework.security.core.GrantedAuthority;

public class UMGUserRoleDetails implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	private String authority;
	private String tenantCode;

	public UMGUserRoleDetails(final String authority, final String tenantCode/*, final List<String> privileges*/) {
		this.authority = authority;
		this.tenantCode = tenantCode;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(final String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public void setAuthority(final String authority) {
		this.authority = authority;
	}

    @Override
	public boolean equals(final Object obj) {
		boolean eq = false;
		if (obj instanceof UMGUserRoleDetails) {
			eq = ((UMGUserRoleDetails) obj).getAuthority().equals(authority);
		}

		return eq;
	}

	@Override
	public int hashCode() {
		return authority.hashCode();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("Authority", authority).toString();
	}
}