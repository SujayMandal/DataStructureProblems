package com.ca.umg.business.accessprivilege.delegate;

import com.ca.framework.core.exception.SystemException;

public interface RolesPrivilegesDelegate {

    void createDefaultRolesAndPrivileges(String tenantCode) throws SystemException;

    void deleteDefaultRolesAndPrivileges(String tenantCode) throws SystemException;

}
