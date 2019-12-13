package com.ca.umg.business.accessprivilege.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.dbauth.UMGUserDetails;

public interface AccessPrivilegeBO {
    
    //public void setPrivileges(AccessPrivilege accessPrivilege);

	 /**
     * gets the {@link UMGUserDetails} object for the passed isSysAdmin and tenant code
     * @param isSysAdmin
     * @param tenantCode
     * @return
     */
    public Map<String,Object> getRolesPrivilegesMap(String tenantCode) throws SystemException;

	public void setRolesPrivilegesMap(String tenantCode, String role, List<String> prvilegeAsList) throws SystemException, SQLException;
}
