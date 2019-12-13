package com.ca.umg.business.accessprivilege.dao;

import java.util.List;

/**
 * This class used to create default roles and permissions for tenant
 * 
 * @author basanaga
 * 
 *
 */
public interface RolesPrivilegesDAO {

    /**
     * This method used to get all the permissions present in permissions table
     * 
     * @return
     */
    List<String> getDefaultPermissions();

    /**
     * This method used to get all the permissions for the role ROLE_TENANT_USER
     * 
     * @return
     */

    List<String> getPermissionsForTenantUser();

    /**
     * This method gets ids for ROLE_TENANT_ADMIN,ROLE_USER,ROLE_MODELER
     * 
     * 
     * @return
     */
    List<String> getDefaultRoles();

    /**
     * This method used to create the default roles for the given tenant
     * 
     * @param tenantId
     * @param defaultRoles
     */
    void createDefaultRoles(String tenantCode, List<String> defaultRoles);

    /**
     * This method used to get the roles assigned to the tenant
     * 
     * @param tenantId
     * @return
     */
    List<String> getTenantAdminRoles(String tenantCode);
    
    /**
     * This method used to get the roles assigned to the tenant
     * 
     * @param tenantId
     * @return
     */
    List<String> getTenantModelerRoles(String tenantCode);

    /**
     * This method used to get the tenant roles for the role ROLE_TENANT_USEr
     * 
     * @param tenantId
     * @return
     */
    List<String> getTenantRolesForTenantUser(final String tenantId);

    /**
     * This method used to create the permissions for default roles
     * 
     * @param tenantRoles
     * @param permissions
     */
    void createPermissionsForTenant(List<String> tenantRoles, List<String> permissions);

    /**
     * This method used to delete the default Roles when tenant is on boarding
     * 
     * @param tenantCode
     */
    void deleteDefaultRoles(String tenantCode);

    /**
     * This method used to delete the privileges for the default roles
     * 
     * @param roleId
     */
    void deletePrivilegesFordefaultRole(String roleId);

	List<String> getModelerPermissions();

	List<String> getTenantRoles(String tenantCode);

}
