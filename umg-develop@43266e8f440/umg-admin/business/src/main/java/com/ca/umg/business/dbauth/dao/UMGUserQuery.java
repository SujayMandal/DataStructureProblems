package com.ca.umg.business.dbauth.dao;

import static com.ca.umg.business.dbauth.UMGUserStatus.LOCKED;
import static com.ca.umg.business.dbauth.bo.UMGUserLoginActivityBOImpl.LOCK_COUNT_FOR_FAILURE;

public final class UMGUserQuery {

	public static final String FIND_USER_QUERY = "select u.username, u.PASSWORD, u.enabled, u.TENANT_CODE, u.name, u.official_email, u.organization, u.comments, "
			+ "u.created_on, u.last_activated_on, u.last_deactivated_on, ur.role "
			+ "from USERS u inner join USER_ROLES ur on u.username = ur.username and u.TENANT_CODE = ur.TENANT_CODE "
			+ "where u.username = ? and u.TENANT_CODE = ?";

	public static final String UPDATE_PASSWORD_QUERY = "update USERS set PASSWORD = ?, enabled = 1 where username = ? and TENANT_CODE = ?";

	public static final String INSERT_USER_LOG_ACTIVITY = "insert into USERS_LOGIN_AUDIT (username, tenant_code, sys_ip_address, access_on, activity, reason_code) "
			+ "values (?, ?, ?, ?, ?, ?)";

	public static final String SELECT_TOP_3_USER_ACTIVITIES = "select username, tenant_code, sys_ip_address, access_on, activity, reason_code "
			+ "from USERS_LOGIN_AUDIT where username = ? and tenant_code = ? order by access_on desc limit " + LOCK_COUNT_FOR_FAILURE;

	public static final String LOCK_USER_QUERY = "update USERS set enabled = " + LOCKED.getCode() + " where username = ? and TENANT_CODE = ?";

	private UMGUserQuery() {

	}
}