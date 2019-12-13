package com.ca.umg.business.accessprivilege.delegate;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.bo.RolesPrivilegesBO;

@Named
public class RolesPrivilegesDelegateImpl implements RolesPrivilegesDelegate {
	
	@Inject
	private RolesPrivilegesBO rolesPrivilegesBO;

	@Override
	public void createDefaultRolesAndPrivileges(String tenantCode) throws SystemException {
		rolesPrivilegesBO.createDefaultRolesAndPrivileges(tenantCode);
	}

	@Override
	public void deleteDefaultRolesAndPrivileges(String tenantCode) throws SystemException {
		rolesPrivilegesBO.deleteDefaultRolesAndPrivileges(tenantCode);

	}

}
