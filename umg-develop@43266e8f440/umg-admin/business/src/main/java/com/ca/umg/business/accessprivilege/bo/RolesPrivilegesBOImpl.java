package com.ca.umg.business.accessprivilege.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.dao.RolesPrivilegesDAO;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.util.AdminUtil;

/**
 * @author basanaga
 *
 */
@Named
public class RolesPrivilegesBOImpl implements RolesPrivilegesBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(RolesPrivilegesBOImpl.class);

    @Inject
    private RolesPrivilegesDAO rolesPrivilegesDAO;

    @Override
    public void createDefaultRolesAndPrivileges(String tenantCode) throws SystemException {
        try {
            AdminUtil.setAdminAwareTrue();
            LOGGER.info("Started creating  defaultRoles");
            rolesPrivilegesDAO.createDefaultRoles(tenantCode, rolesPrivilegesDAO.getDefaultRoles());
            LOGGER.info("Default Roles created successfully");
            LOGGER.info("Started creating  permissions for ROLE_TENANT_ADMIN roles");
            rolesPrivilegesDAO.createPermissionsForTenant(rolesPrivilegesDAO.getTenantAdminRoles(tenantCode),
                    rolesPrivilegesDAO.getDefaultPermissions());
            LOGGER.info("Permissions for ROLE_TENANT_ADMIN roles created successfully");
            LOGGER.info("Started creating  permissions for ROLE_MODELER roles");
            rolesPrivilegesDAO.createPermissionsForTenant(rolesPrivilegesDAO.getTenantModelerRoles(tenantCode),
                    rolesPrivilegesDAO.getModelerPermissions());
            LOGGER.info("Permissions for ROLE_MODELER roles created successfully");
            LOGGER.info("Started creating  permissions for ROLE_TENANT_USER role");
            rolesPrivilegesDAO.createPermissionsForTenant(rolesPrivilegesDAO.getTenantRolesForTenantUser(tenantCode),
                    rolesPrivilegesDAO.getPermissionsForTenantUser());
            LOGGER.info("Permissions for ROLE_TENANT_USER role created successfully");
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Exception while creation of default roles for the tenant : {}. Exception is : ", tenantCode, ex);
            throw new SystemException(BusinessExceptionCodes.BSE000144, new Object[] { ex.getMessage() });// NOPMD
        } finally {
            AdminUtil.getActualAdminAware();
        }

    }

    @Override
    public void deleteDefaultRolesAndPrivileges(String tenantCode) throws SystemException {
        List<String> defaultRoles = rolesPrivilegesDAO.getTenantRoles(tenantCode);
        for (String tenantRoleId : defaultRoles) {
            rolesPrivilegesDAO.deletePrivilegesFordefaultRole(tenantRoleId);
        }
        rolesPrivilegesDAO.deleteDefaultRoles(tenantCode);
    }

}
