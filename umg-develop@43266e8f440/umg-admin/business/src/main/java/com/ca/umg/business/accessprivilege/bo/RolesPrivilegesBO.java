package com.ca.umg.business.accessprivilege.bo;

import com.ca.framework.core.exception.SystemException;

public interface RolesPrivilegesBO {

    void createDefaultRolesAndPrivileges(String tenantCode) throws SystemException;

    void deleteDefaultRolesAndPrivileges(String tenantCode) throws SystemException;

}
