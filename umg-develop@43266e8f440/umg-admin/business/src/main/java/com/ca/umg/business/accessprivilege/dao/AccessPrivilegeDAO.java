package com.ca.umg.business.accessprivilege.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.AccessPrivilege;
import com.ca.umg.business.accessprivilege.Privileges;
import com.ca.umg.business.accessprivilege.bo.AccessPrivilegeBOImpl;

public interface AccessPrivilegeDAO {
    
    /**
     * method called from {@link AccessPrivilegeBOImpl} on post-construct 
     * to populate static list of all pages and all actions used for super admin and 
     * also populate the values for all properties of {@link AccessPrivilege} used for 
     * permission checks in {@link PreAuthorize} hasrole function 
     * @param accessPrivilege
     * @throws BusinessException
     */
    public void setPrivileges (AccessPrivilege accessPrivilege) throws BusinessException;
    
    /**
     * gets the privilege list for the tenant code and roles passed 
     * @param roleList
     * @param tenantCode
     * @return
     * @throws BusinessException
     */
    public String getPrivilegesForRFTntRoles (List<String> roleList, String tenantCode) throws SystemException;
    
    /**
     * gets the list of {@link Privileges} for all pages from permission table whose permission type is page
     * @return
     */
    public List<Privileges> getPagesPrivilegesList();
    
    /**
     * gets the list of {@link Privileges} for all buttons/fields from permission table whose permission type is action
     * @return
     */
    public List<Privileges> getActionPrivilegesList();
    
    /**
     * populates a map with key as tenant-role and value as list of privileges for (tenant to role) 
     * @throws BusinessException
     */
    //public void populateRolesToTenantPrivilegesMap() throws BusinessException;
    
    /**
     * returns a map containing the list of privileges for tenant to role 
     * @return
     */
    //public Map<String,Object> getRolesToTenantPrivilegeMap();
    
    /**
     * returns the list of users who have rf_user flag set as true in users table
     * @throws BusinessException
     */
    public List<String> getRfUsersList() throws SystemException;
    
    /**
     * returns the map of roles and privileges
     * @param isSysAdmin 
     * @param tenantCode 
     * @param userName 
     * @throws BusinessException
     */
    public Map<String,Object> rolePrivilegeMapping(String tenantCode) throws SystemException;

	public void setrolePrivilegeMapping(String tenantCode, String role, List<String> prvilegeAsList) throws SystemException, SQLException;
    
}
