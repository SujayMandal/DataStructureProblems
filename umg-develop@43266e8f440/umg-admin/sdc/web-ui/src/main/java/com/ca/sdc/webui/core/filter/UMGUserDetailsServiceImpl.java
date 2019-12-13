package com.ca.sdc.webui.core.filter;

import static org.slf4j.LoggerFactory.getLogger;

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.umg.business.accessprivilege.Privileges;
import com.ca.umg.business.accessprivilege.dao.AccessPrivilegeDAO;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.dbauth.UMGUserDetails;

@SuppressWarnings("PMD")
@Named
public class UMGUserDetailsServiceImpl implements UserDetailsService {

	private static final Logger LOGGER = getLogger(UMGUserDetailsServiceImpl.class);

	private static final String SQL_USER_TENANT_ROLES_PRIVILEGES = "select u.username, u.password, u.enabled, tum.tenant_code, u.sys_admin, "
	        + "GROUP_CONCAT(ur.role) as role_list, GROUP_CONCAT(up.permission) as perm_list "
	        + "from USERS u, TENANT_USER_MAPPING tum "
	        + "join TENANT_USER_TENANT_ROLE_MAPPING tutrm on tum.Id = tutrm.tenant_user_map_id "
	        + "join TENANT_ROLES_MAPPING trm on tutrm.tenant_role_map_id = trm.Id "
	        + "join ROLES ur on ur.Id = trm.roles_id "
	        + "left join PERMISSION_ROLES_MAPPING prm on trm.Id =  prm.tenant_roles_map_id "
	        + "left join PERMISSIONS up on up.Id = prm.permission_id "
	        + "where u.username = ? and u.Id = tum.user_id and tum.tenant_code = trm.tenant_code "
	        + "and trm.tenant_code = ? "
	        + "group by tum.tenant_code "
	        + "order by tum.tenant_code asc";
	
	private static final String SQL_USER_TENANT = "select u.username, u.password, u.enabled, tum.tenant_code, u.sys_admin "
	        + "from USERS u, TENANT_USER_MAPPING tum "
	        + "where u.username = ? and u.Id = tum.user_id "
	        + "order by tum.tenant_code asc";
	
	private static final String SQL_USER_TENANT_SELECT = "select u.username, u.password, u.enabled, tum.tenant_code, u.sys_admin "
	        + "from USERS u, TENANT_USER_MAPPING tum "
	        + "where u.username = ? and u.Id = tum.user_id "
	        + "and tum.tenant_code = ? "
	        + "order by tum.tenant_code asc";
	
	private static final String SQL_USER = "select u.username, u.password, u.enabled, u.sys_admin "
	        + "from USERS u "
	        + "where u.username = ? ";
	
	private static final String ROLES_AND_PERMISSIONS = "ROLES_AND_PERMISSIONS";
	private static final String USER_AND_TENANT = "USER_AND_TENANT";
	private static final String USER = "USER";

	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Inject
	private AccessPrivilegeDAO accessPrivilegeDAO;
	
	@Inject
    private CacheRegistry cacheRegistry;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		LOGGER.debug("Username: " + username);
		UMGUserDetails userDetails = null;
		List<String> tenantLst = getTenantList (username, SQL_USER_TENANT);
		userDetails = getUserDetails (username, null, SQL_USER, USER);
		
		if (userDetails != null && userDetails.getIsSysAdmin()) {//checking if user is sys-admin
		    List<String> tenantList = getSortedTntListFromCache();
            userDetails.setTenantCode(tenantList.get(BusinessConstants.NUMBER_ZERO));
            userDetails.setTenantList(tenantList);
		} else if (CollectionUtils.isEmpty(tenantLst)) {
		    throw new UsernameNotFoundException("There was an error with your Username/Password combination. Please try again");
		} else {
			String tenantCode = tenantLst.get(BusinessConstants.NUMBER_ZERO);
			userDetails = getUserDetails (username, tenantCode, SQL_USER_TENANT_ROLES_PRIVILEGES, ROLES_AND_PERMISSIONS);
			if (userDetails == null) {
			    userDetails = getUserDetails (username, tenantCode, SQL_USER_TENANT_SELECT, USER_AND_TENANT);
			    if (userDetails == null) {
			    	throw new UsernameNotFoundException("There was an error with your Username/Password combination. Please try again");
			    }
			} 
		}
		
		//setting the tenant list based on sysadmin or not
		if (userDetails != null && !userDetails.getIsSysAdmin()) {
            userDetails.setTenantList(tenantLst);
        } 
		
		return userDetails;
	}
	
	private List<String> getSortedTntListFromCache () {
	    List<String> tenantList = new ArrayList<>();
        for(Object tntCode : cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).keySet()) {
            tenantList.add((String)tntCode);
        }
        Collections.sort(tenantList);
        return tenantList;
	}

	private UMGUserDetails getUserDetails (final String username, final String tenantCode, final String query, final String rsFetchIdentifier) {
	    UMGUserDetails userDetails = null;
        try {
            LOGGER.debug("Query to fecth users:" + query);
            userDetails = jdbcTemplate.execute(query, new PreparedStatementCallback<UMGUserDetails>() {
                @Override
                public UMGUserDetails doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                    try {
                        ps.setString(1, username);
                        if(tenantCode != null){
                        	ps.setString(2, tenantCode);
                        }
                        return createUMGUserDetailsRoles(ps.executeQuery(), rsFetchIdentifier);
                    } catch (SQLException sqle) {
                        throw sqle;
                    }
                }
            });
        } catch (DataAccessException dae) {
            LOGGER.debug(dae.getMessage());
            throw dae;
        }

        return userDetails;
	}

	private UMGUserDetails createUMGUserDetailsRoles(final ResultSet rs, final String rsFetchIdentifier) throws SQLException {
		UMGUserDetails userDetails = null;
		if (rs != null) {
			try {
				rs.next();
				if (rs.getRow() > 0) {
					final String username = rs.getString("username");
					final String password = rs.getString("password");
					final int enabled = rs.getInt("enabled");
					final Boolean sys_admin = rs.getBoolean("sys_admin");
					String role_list = null;
					String perm_list = null;
					String tenantCode = null;
					if (StringUtils.equals(rsFetchIdentifier, ROLES_AND_PERMISSIONS)) {
					    role_list = rs.getString("role_list");
	                    perm_list = rs.getString("perm_list");
	                    tenantCode = rs.getString("tenant_code");
					} 
					
					List<String> tenantList = null;
					if (StringUtils.equals(rsFetchIdentifier, USER_AND_TENANT)) {
					    tenantCode = rs.getString("tenant_code");
					  //setting all the mapped tenants for the user in a list
	                    tenantList = new ArrayList<>();
	                    tenantList.add(tenantCode);
	                    while (rs.next()) {
	                        tenantList.add(rs.getString("tenant_code"));
	                    }
					}
					
					List<Privileges> pagePrivilegesList = null;
                    List<Privileges> actionPrivilegesList = null;
					if (sys_admin) {
					    pagePrivilegesList = accessPrivilegeDAO.getPagesPrivilegesList();
		                actionPrivilegesList = accessPrivilegeDAO.getActionPrivilegesList();
					}
					
					userDetails = new UMGUserDetails(username, password, enabled, tenantCode, role_list, perm_list, 
					        sys_admin, pagePrivilegesList, actionPrivilegesList);
					
					if (tenantList != null && userDetails != null ) {
					    userDetails.setTenantList(tenantList);
					}
					LOGGER.debug("Login User Details:" + userDetails.toString());
				} else {
					LOGGER.debug("Login User Details: No user found");
				}
			} catch (SQLException sqle) {
				LOGGER.debug("User exists, but has some problem");
				LOGGER.debug(sqle.getMessage());
				throw sqle;
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqle) {
						LOGGER.debug("Not able to close the Result set");
					}
				}
			}
		}

		return userDetails;
	}
	
	private List<String> getTenantList (final String username, final String query) {
	    List<String> tenantList = null;
        try {
            LOGGER.debug("Query to fecth users:" + query);
            tenantList = jdbcTemplate.execute(query, new PreparedStatementCallback<List<String>>() {
                @Override
                public List<String> doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                    ResultSet rs = null;
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
                        throw sqle;
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException sqle) {
                                LOGGER.debug("Not able to close the Result set");
                            }
                        }
                    }
                }
            });
        } catch (DataAccessException dae) {
            LOGGER.debug(dae.getMessage());
            throw dae;
        }
        return tenantList;
    }
	
	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}
}