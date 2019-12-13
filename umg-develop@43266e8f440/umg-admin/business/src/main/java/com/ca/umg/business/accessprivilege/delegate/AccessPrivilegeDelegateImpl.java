package com.ca.umg.business.accessprivilege.delegate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.bo.AccessPrivilegeBO;
import com.ca.umg.business.util.AdminUtil;

@Named
public class AccessPrivilegeDelegateImpl implements AccessPrivilegeDelegate {

	@Inject
	private AccessPrivilegeBO accessPrivilegeBO;
	
	@Override
	public Map<String, Object> getRolesPrivilegesMap(String tenantCode) throws SystemException {
		Boolean actualAdminAware = AdminUtil.getActualAdminAware();
		try {
			AdminUtil.setAdminAwareTrue();
			return accessPrivilegeBO.getRolesPrivilegesMap(tenantCode);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
	}

	@Override
	public void setRolesPrivilegesMap(String tenantCode, String role, List<String> prvilegeAsList)
			throws SystemException, SQLException {
		Boolean actualAdminAware = AdminUtil.getActualAdminAware();
		try {
			AdminUtil.setAdminAwareTrue();
			accessPrivilegeBO.setRolesPrivilegesMap(tenantCode, role, prvilegeAsList);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
	}

}
