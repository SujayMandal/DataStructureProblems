package com.ca.umg.business.accessprivilege.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ca.umg.business.accessprivilege.AccessPrivilegeEnum;

/**
 * @author basanaga
 *
 */
@Repository
public class RolesPrivilegesDAOImpl implements RolesPrivilegesDAO {

    private static final String COMMA_WITHIN_QUOTES = "','";

    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private static final String DEFAULT_PRIVILEGES = "SELECT DISTINCT ID FROM  PERMISSIONS";
    private static final String GET_PERMISSIONS_FOR_MODELER = "SELECT DISTINCT ID FROM  PERMISSIONS WHERE permission NOT IN( '"
            + AccessPrivilegeEnum.getPrivilegeField("Notifications.Add") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Notifications.Manage")+ "');";
    private static final String DEFAULT_ROLES = "SELECT ID FROM  ROLES WHERE ROLE IN('ROLE_MODELER','ROLE_ADMIN','ROLE_TENANT_USER')";
    private static final String CREATE_DEFAULT_ROLES = "INSERT INTO TENANT_ROLES_MAPPING(Id,roles_id,tenant_code) VALUES (?,?,?) ";
    private static final String GET_TENANT_ROLES = "SELECT trm.Id FROM TENANT_ROLES_MAPPING trm,ROLES r WHERE trm.tenant_code=? AND trm.roles_id=r.Id and r.ROLE IN ( 'ROLE_ADMIN','ROLE_MODELER')";
    private static final String GET_ADMIN_ROLES = "SELECT trm.Id FROM TENANT_ROLES_MAPPING trm,ROLES r WHERE trm.tenant_code=? AND trm.roles_id=r.Id and r.ROLE = 'ROLE_ADMIN'";
    private static final String GET_MODELER_ROLES = "SELECT trm.Id FROM TENANT_ROLES_MAPPING trm,ROLES r WHERE trm.tenant_code=? AND trm.roles_id=r.Id and r.ROLE = 'ROLE_MODELER'";
    private static final String CREATE_DEFAULT_PERMISSIONS = "INSERT INTO PERMISSION_ROLES_MAPPING(Id,tenant_roles_map_id,permission_id) VALUES (?,?,?) ";
    private static final String DELETE_DEFAULT_ROLES = "DELETE FROM TENANT_ROLES_MAPPING WHERE tenant_code = ?";
    private static final String DELETE_DEFAULT_PERMISSIONS = "DELETE FROM PERMISSION_ROLES_MAPPING WHERE tenant_roles_map_id = ?";
    private static final String GET_TENANT_ROLE_TENANT_USER = "SELECT trm.Id FROM TENANT_ROLES_MAPPING trm,ROLES r WHERE trm.tenant_code=? AND trm.roles_id=r.Id and r.ROLE IN('ROLE_TENANT_USER')";
    private static final String GET_TENANT_USER_PERMISSIONS = "SELECT ID FROM PERMISSIONS WHERE permission IN( '"
            + AccessPrivilegeEnum.getPrivilegeField("Model.Manage") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Model.Manage.ExportVersionAPI") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Model.Manage.View") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Model.Manage.View.DownloadReleaseNotes") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Lookup.Manage") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.BatchBulk") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.BatchBulk.DownloadIO") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.BatchBulk.TerminateBatch") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.BatchBulk.Upload") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.AdvancedSearch") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.DownloadExcelUsageReport") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.DownloadIOExcel") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.DownloadIOJson") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.DownloadModelIO") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.DownloadReport") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.DownloadTenantIO") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.PayloadField") + COMMA_WITHIN_QUOTES
            + AccessPrivilegeEnum.getPrivilegeField("Dashboard.Transaction.Re-run") + "');";

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#getDefaultPermissions()
     */
    @Override
    public List<String> getDefaultPermissions() {
        return jdbcTemplate.queryForList(DEFAULT_PRIVILEGES, String.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#getDefaultPermissions()
     */
    @Override
    public List<String> getPermissionsForTenantUser() {
        return jdbcTemplate.queryForList(GET_TENANT_USER_PERMISSIONS, String.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#getDefaultRoles()
     */
    @Override
    public List<String> getDefaultRoles() {
        return jdbcTemplate.queryForList(DEFAULT_ROLES, String.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#createDefaultRoles(java.lang.String, java.util.List)
     */
    @Override
    @Transactional
    public void createDefaultRoles(final String tenantId, final List<String> defaultRoles) {

        jdbcTemplate.batchUpdate(CREATE_DEFAULT_ROLES, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, defaultRoles.get(i));
                ps.setString(3, tenantId);
            }

            @Override
            public int getBatchSize() {
                return defaultRoles.size();
            }

        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#getTenantRoles(java.lang.String)
     */
    @Override
    public List<String> getTenantAdminRoles(final String tenantId) {
        return jdbcTemplate.queryForList(GET_ADMIN_ROLES, new Object[] { tenantId }, String.class);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#getTenantRoles(java.lang.String)
     */
    @Override
    public List<String> getTenantModelerRoles(final String tenantId) {
        return jdbcTemplate.queryForList(GET_MODELER_ROLES, new Object[] { tenantId }, String.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#getTenantRoles(java.lang.String)
     */
    @Override
    public List<String> getTenantRolesForTenantUser(final String tenantId) {
        return jdbcTemplate.queryForList(GET_TENANT_ROLE_TENANT_USER, new Object[] { tenantId }, String.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#createPermissionsForTenant(java.util.List,
     * java.util.List)
     */
    @Override
    public void createPermissionsForTenant(final List<String> tenantRoles, final List<String> permissions) {

        for (final String roleId : tenantRoles) {// NOPMD
            jdbcTemplate.batchUpdate(CREATE_DEFAULT_PERMISSIONS, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {

                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, roleId);
                    ps.setString(3, permissions.get(i));
                }

                @Override
                public int getBatchSize() {
                    return permissions.size();
                }

            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#deleteDefaultRolesAndPrivileges(java.lang.String)
     */
    @Override
    public void deleteDefaultRoles(String tenantCode) {
        jdbcTemplate.update(DELETE_DEFAULT_ROLES, new Object[] { tenantCode });

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.rolesandprivileges.dao.RolesPrivilegesDAO#deletePrivilegesFordefaultRole(java.lang.String)
     */
    @Override
    public void deletePrivilegesFordefaultRole(String roleId) {
        jdbcTemplate.update(DELETE_DEFAULT_PERMISSIONS, new Object[] { roleId });

    }

	@Override
	public List<String> getModelerPermissions() {
		// TODO Auto-generated method stub
		return jdbcTemplate.queryForList(GET_PERMISSIONS_FOR_MODELER, String.class);
	}

	@Override
	public List<String> getTenantRoles(final String tenantId) {
        return jdbcTemplate.queryForList(GET_TENANT_ROLES, new Object[] { tenantId }, String.class);
	}

}
