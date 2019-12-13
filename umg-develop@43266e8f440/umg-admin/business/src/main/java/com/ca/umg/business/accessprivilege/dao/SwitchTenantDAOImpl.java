package com.ca.umg.business.accessprivilege.dao;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.accessprivilege.Privileges;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.dbauth.UMGUserDetails;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.util.AdminUtil;

@Named
@SuppressWarnings({"PMD.UseObjectForClearerAPI", "PMD.CyclomaticComplexity"})
public class SwitchTenantDAOImpl implements SwitchTenantDAO {

    private static final Logger LOGGER = getLogger(SwitchTenantDAOImpl.class);

    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    @Inject
    private AccessPrivilegeDAO accessPrivilegeDAO;
    
    @Inject
    private CacheRegistry cacheRegistry;

    private JdbcTemplate jdbcTemplate;

    private static final String SQL_USR_TNT_ROLES_PRIVILG = "select u.username, u.password, u.enabled, tum.tenant_code, u.sys_admin, "
            + "GROUP_CONCAT(ur.role) as role_list, GROUP_CONCAT(up.permission) as perm_list "
            + "from USERS u, TENANT_USER_MAPPING tum "
            + "join TENANT_USER_TENANT_ROLE_MAPPING tutrm on tum.Id = tutrm.tenant_user_map_id "
            + "join TENANT_ROLES_MAPPING trm on tutrm.tenant_role_map_id = trm.Id "
            + "join ROLES ur on ur.Id = trm.roles_id "
            + "left join PERMISSION_ROLES_MAPPING prm on trm.Id =  prm.tenant_roles_map_id "
            + "left join PERMISSIONS up on up.Id = prm.permission_id "
            + "where u.username = ? and u.Id = tum.user_id and tum.tenant_code = trm.tenant_code "
            + "and tum.tenant_code = ? "
            + "group by tum.tenant_code " + "order by tum.tenant_code asc";

    private static final String SQL_USER_TENANT = "select u.username, u.password, u.enabled, tum.tenant_code, u.sys_admin "
            + "from USERS u, TENANT_USER_MAPPING tum " 
            + "where u.username = ? and u.Id = tum.user_id and tum.tenant_code = ? ";
    
    private static final String SQL_USER_TENANT_LIST = "select u.username, u.password, u.enabled, tum.tenant_code, u.sys_admin "
            + "from USERS u, TENANT_USER_MAPPING tum "
            + "where u.username = ? and u.Id = tum.user_id "
            + "order by tum.tenant_code asc";
    
    private static final String SQL_USER = "select u.username, u.password, u.enabled, u.sys_admin "
            + "from USERS u "
            + "where u.username = ? ";

    private static final String ROLES_AND_PERMISSIONS = "ROLES_AND_PERMISSIONS";
    private static final String USER_AND_TENANT = "USER_AND_TENANT";
    private static final String USER = "USER";

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public UserDetails switchAndSetTenant(String tenantCode) throws SystemException {
        LOGGER.error("getting data for Switch tenant started for tenant Code: " + tenantCode);
        UserDetails userDetails= null;
        Boolean actualAdminAware = AdminUtil.getActualAdminAware();
        try {
	        final Authentication auth = getContext().getAuthentication();
	        String userName = null;
	        if (auth.getPrincipal() instanceof UMGUserDetails) {
	            userName = ((UMGUserDetails)auth.getPrincipal()).getUsername();
	        } else {
	            userName = auth.getName();
	        }
	        
	        if (StringUtils.isEmpty(userName) && StringUtils.isEmpty(tenantCode)) {
	            LOGGER.error("Username or tenantcode is not set");
	            SystemException.newSystemException(BusinessExceptionCodes.BSE000904, new Object[] {});
	        }
	        
        	AdminUtil.setAdminAwareTrue();
        	userDetails = loadUserByUsernameAndTenant(userName, tenantCode);
        }finally {
        	AdminUtil.setActualAdminAware(actualAdminAware);
        }
        
        RequestContext.getRequestContext().setAdminAware(Boolean.FALSE);
        LOGGER.error("getting data for Switch tenant ended for tenant Code: " + tenantCode);
        return userDetails;
    }

    public UserDetails loadUserByUsernameAndTenant(final String username, final String tenantCode) throws SystemException {
        LOGGER.debug("Username : " + username + " and tenant Code: " + tenantCode);
        UMGUserDetails userDetails = null;
        try {
            userDetails = getUserDetails(username, tenantCode, SQL_USR_TNT_ROLES_PRIVILG, ROLES_AND_PERMISSIONS);
            if (userDetails == null) {
                userDetails = getUserDetails(username, tenantCode, SQL_USER_TENANT, USER_AND_TENANT);
                if (userDetails == null) {
                    userDetails = getUserDetails (username, null, SQL_USER, USER);
                    if (userDetails == null || !userDetails.getIsSysAdmin()) {
                        LOGGER.error("User is not mapped to any tenant ");
                        throw new UsernameNotFoundException("There was an error with your Username/Password combination. Please try again");
                    } 
                }
            } 
            if (userDetails != null && !userDetails.getIsSysAdmin()) {
                List<String> tenantList = getTenantList (username, SQL_USER_TENANT_LIST);
                userDetails.setTenantList(tenantList);
            } else if (userDetails != null && userDetails.getIsSysAdmin()) {
                List<String> tenantList = new ArrayList<>();
                for(Object tntCode : cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).keySet()) {
                    tenantList.add((String)tntCode);
                }
                Collections.sort(tenantList);
                userDetails.setTenantCode(tenantCode);
                userDetails.setTenantList(tenantList);
            }
        } catch (SQLException sqle) {
            LOGGER.error("SQL exception occured while switching tenants : {}", sqle);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000903, new Object[] { username });
        }
        return userDetails;
    }

    private UMGUserDetails getUserDetails(final String username, final String tenantCode, final String query,
            final String rsFetchIdentifier) throws SystemException, SQLException {
        UMGUserDetails userDetails = null;
        try {
            LOGGER.error("Query to fecth users:" + query);
            userDetails = jdbcTemplate.execute(query, new PreparedStatementCallback<UMGUserDetails>() {
                @Override
                public UMGUserDetails doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                    try {
                        ps.setString(1, username);
                        if (tenantCode != null) {
                            ps.setString(2, tenantCode);
                        }
                        return createUMGUserDetailsRoles(ps.executeQuery(), rsFetchIdentifier);
                    } catch (SQLException sqle) {
                        LOGGER.error("Error occured while preparing the statement : {}", sqle);
                        throw sqle;
                    }
                }
            });
        } catch (DataAccessException dae) {
            LOGGER.error("Error occured while accesing the data : {}", dae);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000902, new Object[] { username });
        }

        return userDetails;
    }
    
    private UMGUserDetails createUMGUserDetailsRoles(final ResultSet rs, final String rsFetchIdentifier) throws SQLException {
        UMGUserDetails userDetails = null;
        try{
	        if(rs != null) {
	            try {
	                rs.next();
	                if (rs.getRow() > BusinessConstants.NUMBER_ZERO) {
	                    final String username = rs.getString("username");
	                    final String password = rs.getString("password");
	                    final int enabled = rs.getInt("enabled");
	                    final Boolean sys_admin = rs.getBoolean("sys_admin");
	                    String roleList = null;
	                    String permList = null;
	                    String tenantCode = null;
	                    if (StringUtils.equals(rsFetchIdentifier, ROLES_AND_PERMISSIONS)) {
	                        roleList = rs.getString("role_list");
	                        permList = rs.getString("perm_list");
	                        tenantCode = rs.getString("tenant_code");
	                    }
	
	                    if (StringUtils.equals(rsFetchIdentifier, USER_AND_TENANT)) {
	                        tenantCode = rs.getString("tenant_code");
	                    }
	
	                    List<Privileges> pagePrivilegesList = null;
	                    List<Privileges> actionPrivilegesList = null;
	                    if (sys_admin) {
	                        pagePrivilegesList = accessPrivilegeDAO.getPagesPrivilegesList();
	                        actionPrivilegesList = accessPrivilegeDAO.getActionPrivilegesList();
	                    }
	                    
	                    userDetails = new UMGUserDetails(username, password, enabled, tenantCode, roleList, permList, sys_admin,
	                            pagePrivilegesList, actionPrivilegesList);
	                    LOGGER.debug("Login User Details:" + userDetails.toString());
	                } else {
	                    LOGGER.debug("Login User Details: No user found");
	                }
	            } catch (SQLException sqle) {
	                LOGGER.error("User exists, but has some problem");
	                LOGGER.error("Error occured while setting result set object : {} ", sqle);
	                throw sqle;
	            } finally {
	                if (rs != null) {
	                        rs.close();
	                    }
	            }
	        }
        }catch (SQLException sqle) {
            LOGGER.error("Not able to close the Result set : {}", sqle);
            throw sqle;
        }

        return userDetails;
    }
    
    private List<String> getTenantList (final String username, final String query) throws SystemException {
        List<String> tenantList = null;
        try {
            LOGGER.debug("Query to fecth users:" + query);
            tenantList = jdbcTemplate.execute(query, new PreparedStatementCallback<List<String>>() {
                @Override
                public List<String> doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                    ResultSet rs = null;
                    try{
	                    try {
	                        List<String> tntList = null;
	                        ps.setString(1, username);
	                        rs = ps.executeQuery();
	                        if (rs != null) {
	                            //setting all the tenants for the user in a list
	                            tntList = new ArrayList<>();
	                            while (rs.next()) {
	                                tntList.add(rs.getString("tenant_code"));
	                            }
	                        }
	                        return tntList;
	                    } catch (SQLException sqle) {
	                        LOGGER.error("Error occured while setting result set object : {} ", sqle);
	                        throw sqle;
	                    } finally {
	                        if (rs != null) {
	                              rs.close();
	                        }
	                    }
	                }catch (SQLException sqle) {
	                	LOGGER.debug("Not able to close the Result set");
	                	throw sqle;
	                }
                }
            });
        } catch (DataAccessException dae) {
            LOGGER.error("Error occured while accesing the data for tenantList : {}", dae);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000902, new Object[] { username });
        }
        return tenantList;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
