package com.ca.umg.business.accessprivilege.dao;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000810;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.AccessPrivilege;
import com.ca.umg.business.accessprivilege.AccessPrivilegeEnum;
import com.ca.umg.business.accessprivilege.Privileges;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;

@Named
public class AccessPrivilegeDAOImpl implements AccessPrivilegeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessPrivilegeDAOImpl.class);
    
    private static final String FIND_ALL_PRIVILEGES = "select * from PERMISSIONS";
    
    private static final String GET_ROLE_TO_TENANT_PERM_RF = "select GROUP_CONCAT(p.permission) as perm_list "
            + "from PERMISSIONS p, ROLES r "
            + "left join TENANT_ROLES_MAPPING trm on r.Id = trm.roles_id "
            + "left join PERMISSION_ROLES_MAPPING prm on trm.Id = prm.tenant_roles_map_id "
            + "where prm.permission_id = p.Id and trm.tenant_code = :tenantCode "
            + "and r.ROLE IN (:roleList) ";
    
    private static final String GET_ROLE_PRIVILEGE_MAP = "select r.role as role, GROUP_CONCAT(DISTINCT(p.permission)) as perm_list "
    		+ "from USERS u, TENANT_ROLES_MAPPING trm "
    		+ "left join ROLES r on r.Id = trm.roles_id "
    		+ "left join PERMISSION_ROLES_MAPPING prm on trm.Id = prm.tenant_roles_map_id "
    		+ "left join PERMISSIONS p on prm.permission_id = p.Id "
    		+ "where trm.tenant_code = ? "   
    		+ "group by r.Id "
    		+ "order by r.role ASC";
    
    private static final String GET_TENANT_ROLE_MAP_ID = "Select trm.Id as Id from TENANT_ROLES_MAPPING trm " 
    		+ "left join ROLES r on r.Id = trm.roles_id "
    		+ "where trm.tenant_code = ? and r.ROLE = ?";
    
    private static final String DEL_EXISTING_PERM_ROLE_MAP = "delete prm from PERMISSION_ROLES_MAPPING prm, TENANT_ROLES_MAPPING trm "
    		+ "left join ROLES r on r.Id = trm.roles_id  where r.Role = ? "
    		+ "and trm.tenant_code = ? "
    		+ "and prm.tenant_roles_map_id = trm.id"; 
    
    private static final String INSERT_PERM_ROLE_MAP = "insert into PERMISSION_ROLES_MAPPING "
    		+ "values (uuid(), ?, "
    		+ "(Select Id from PERMISSIONS p where p.permission = ?))";
    
    private static final String FIND_RF_USERS = "select u.username from USERS u "
            + "where u.sys_admin = 'true'";
    
    private static final String PAGE = "page";
    
    private static final String ACTION = "action";

    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;
    
    private JdbcTemplate jdbcTemplate;
    
    private List<Privileges> pagesPrivilegesList;
    
    private List<Privileges> actionPrivilegesList;
    
    //private Map<String,Object> rolesToTenantPrivilegesMap;
    
    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Override
    public List<Privileges> getPagesPrivilegesList() {
        return pagesPrivilegesList;
    }
    
    @Override
    public List<Privileges> getActionPrivilegesList() {
        return actionPrivilegesList;
    }
    
    @Override
    public String getPrivilegesForRFTntRoles (List<String> roleList, String tenantCode) throws SystemException {
        Object permList = null;
        try {
            LOGGER.error("Role list to be searched {} for tenant {} ",roleList,tenantCode);
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("roleList",roleList);
            parameters.addValue("tenantCode", tenantCode);
            NamedParameterJdbcTemplate  namedParameterJdbcTemplate = 
                    new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
            permList = namedParameterJdbcTemplate.queryForObject(GET_ROLE_TO_TENANT_PERM_RF, parameters, String.class);
            LOGGER.error("Permission list returned is : {}",permList);
        } catch (DataAccessException dae) {
            LOGGER.error("Error occured while getting the privileges : {} ",dae);
            Object[] arguments = new Object[1];
            throw newSystemException(BSE0000810, arguments, dae);
        }
        return ((String) permList);
    }

    @Override
    public void setPrivileges(final AccessPrivilege accessPrivilege) throws BusinessException {
        try {
            LOGGER.debug("Query to fecth privileges:" + FIND_ALL_PRIVILEGES);
            pagesPrivilegesList = new ArrayList<>();
            actionPrivilegesList = new ArrayList<>();
            jdbcTemplate.execute(FIND_ALL_PRIVILEGES, new PreparedStatementCallback<AccessPrivilege>() {
                @Override
                public AccessPrivilege doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                    try {
                        return createPrivilege(ps.executeQuery(), accessPrivilege);
                    } catch (SQLException sqle) {
                        LOGGER.error("Error occured while getting privileges",sqle);
                        throw sqle;
                    }
                }
            });

        } catch (DataAccessException dae) {
            LOGGER.error("Error occured in getting the data for all privileges {} ",dae);
            Object[] arguments = new Object[1];
            throw newBusinessException(BSE0000810, arguments, dae);
        }
        
    }
    
    private AccessPrivilege createPrivilege(final ResultSet rs, AccessPrivilege accessPrivilege) throws SQLException {
    	try{
        if (rs != null) {
            try {
                while (rs.next()) {
                    String value = rs.getString("permission");
                    setValueToProperty (value, accessPrivilege);
                    Privileges privileges = new Privileges();
                    privileges.setPermission(value);
                    privileges.setUiElementId(rs.getString("ui_element_id"));
                    String permissionType = rs.getString("permission_type");
                    if (StringUtils.equalsIgnoreCase(permissionType, PAGE)) {
                        privileges.setPermissionType(permissionType);
                        pagesPrivilegesList.add(privileges);
                    } else if (StringUtils.equalsIgnoreCase(permissionType, ACTION)){
                        privileges.setPermissionType(permissionType);
                        actionPrivilegesList.add(privileges);
                    }
                }
            } catch (SystemException e) {
                LOGGER.error("Error occured while setting value to privilege object : {}",e);
            }  finally {
                if (rs != null) {                    
                        rs.close();                     
                }
             }
          }
        } catch (SQLException sqle) {
            LOGGER.error("Error occured while creating the privileges : {} ",sqle);
            throw sqle;
        }

        return accessPrivilege;
    }
    
    /**
     * sets the value in for corresponding property in {@link AccessPrivilege} 
     * @param value
     * @param accessPrivilege
     * @throws SystemException
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public void setValueToProperty (String value, AccessPrivilege accessPrivilege) throws SystemException {
        Field fld[] =  accessPrivilege.getClass().getDeclaredFields();
        for(Field x : fld)
        {
            if (StringUtils.equalsIgnoreCase(AccessPrivilegeEnum.getPrivilegeFieldForValueInDb(value), x.getName())) {
                x.setAccessible(true);
                try {
                    x.set(accessPrivilege, value);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    LOGGER.error("Error {} occured while setting value : {} for privilege field : {}" ,e,value,x.getName());
                    throw newSystemException(BusinessExceptionCodes.BSE0000820, new Object[]{value ,x.getName()});
                }
                break;
            }
        }
    }
    
    @Override
    public List<String> getRfUsersList() throws SystemException {
        List<String> rfUsersList = null;
        try {
            LOGGER.debug("Query to fecth rf users:" + FIND_RF_USERS);
            rfUsersList = jdbcTemplate.execute(FIND_RF_USERS, new PreparedStatementCallback<List<String>>() {
                @Override
                public List<String> doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                    try {
                        return getRfUserList(ps.executeQuery());
                    } catch (SQLException sqle) {
                        LOGGER.error("Error occured while getting rf users list : {}",sqle);
                        throw sqle;
                    }
                }
            });
        } catch (DataAccessException dae) {
            LOGGER.error(dae.getMessage(),dae);
            Object[] arguments = new Object[1];
            throw newSystemException(BSE0000810, arguments, dae);
        }
        return rfUsersList;
    }
    
    private List<String> getRfUserList (final ResultSet rs) throws SQLException {
        List<String> rfUsersList = null;
        try{
	        if (rs != null) {
	            rfUsersList = new ArrayList<>();
	            try {
	                while (rs.next()) {
	                    rfUsersList.add(rs.getString("username"));
	                }
	            } catch (SQLException sqle) {
	                LOGGER.error("Error occured while getting rf users : {} ",sqle);
	                throw sqle;
	            } finally {
	                if (rs != null) {
	                    rs.close();
	                }
	            }
	        }
        }catch (SQLException sqle) {
            LOGGER.error("Not able to close the Result set : {}",sqle);
            throw sqle;
        }
        return rfUsersList;
    }

	@Override
	public Map<String,Object> rolePrivilegeMapping(final String tenantCode)
			throws SystemException {
		Map<String,Object> resultList = null;
	        try {
	            LOGGER.debug("Query to get map of role and privileges:" + GET_ROLE_PRIVILEGE_MAP);
	            resultList = jdbcTemplate.execute(GET_ROLE_PRIVILEGE_MAP, new PreparedStatementCallback<Map<String,Object>>() {
	                @Override
	                public Map<String,Object> doInPreparedStatement(final PreparedStatement ps) throws SQLException {
	                    try {
	                    	ps.setString(1, tenantCode);
	                        return getResult(ps.executeQuery());
	                    } catch (SQLException sqle) {
	                        LOGGER.error("Error occured while getting rolePrivilegeMapping : {}",sqle);
	                        throw sqle;
	                    }
	                }
	            });
	        } catch (DataAccessException dae) {
	            LOGGER.error(dae.getMessage(),dae);
	            Object[] arguments = new Object[1];
	            throw newSystemException(BSE0000810, arguments, dae);
	        } 
	        return resultList;
	}
	
    private Map<String,Object> getResult (final ResultSet rs) throws SQLException {
        Map<String,Object> resultList = null;
        try{
	        if (rs != null) {
	        	resultList = new HashMap<>();
	            try {
	                while (rs.next()) {
	                	resultList.put(rs.getString("role"), rs.getString("perm_list"));
	                }
	            } catch (SQLException sqle) {
	                LOGGER.error("Error occured while executing Query to get map of role and privileges : {} ",sqle);
	                throw sqle;
	            } finally {
	                if (rs != null) {
	                        rs.close();
	                    }
	                }
	            }
        }catch (SQLException sqle) {
            LOGGER.error("Not able to close the Result set : {}",sqle);
            throw sqle;
        }
        return resultList;
    }

	@Override
	//@Transactional
	public void setrolePrivilegeMapping(final String tenantCode, final String role, final List<String> privilegeAsList)
			throws SystemException, SQLException {

	        try {
	        	deleteRolePermission(role,tenantCode);
	        	LOGGER.debug("Query to get tenant role mapping :" + GET_TENANT_ROLE_MAP_ID);
	        	List<String> id = jdbcTemplate.execute(GET_TENANT_ROLE_MAP_ID, new PreparedStatementCallback<List<String>>() {
	                @Override
	                public List<String> doInPreparedStatement(final PreparedStatement ps) throws SQLException {
	                    try {
	                    	ps.setString(1, tenantCode);
	                    	ps.setString(2, role);
	                        return getResultList(ps.executeQuery());
	                    } catch (SQLException sqle) {
	                        LOGGER.error("Error occured while deleting from perm_role_mapping : {}",sqle);
	                        throw sqle;
	                    }
	                }
	            });
	            for(String k : privilegeAsList){
	            	insertRolePermission(id.get(0),k);
	            }
	        } catch (DataAccessException dae) {
	            LOGGER.error(dae.getMessage(),dae);
	            Object[] arguments = new Object[1];
	            throw newSystemException(BSE0000810, arguments, dae);
	        }
	
		
	}
	
	private List<String> getResultList (final ResultSet rs) throws SQLException {
        List<String> resultList = null;
        try{
	        if (rs != null) {
	        	resultList = new ArrayList<>();
	            try {
	                while (rs.next()) {
	                	resultList.add(rs.getString("Id"));
	                }
	            } catch (SQLException sqle) {
	                LOGGER.error("Error occured while executing Query to get map of role and privileges : {} ",sqle);
	                throw sqle;
	            } finally {
	                if (rs != null) {
	                        rs.close();
	                    }
	                }
	            }
        }catch (SQLException sqle) {
            LOGGER.error("Unable able to close the Result set : {}",sqle);
            throw sqle;
        }
        return resultList;
    }
	
	private void deleteRolePermission(final String role,final String tenantCode){
		LOGGER.debug("Query to delete existing permission role mapping :" + DEL_EXISTING_PERM_ROLE_MAP);
        jdbcTemplate.execute(DEL_EXISTING_PERM_ROLE_MAP, new PreparedStatementCallback<Integer>() {
            @Override
            public Integer doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                try {
                	ps.setString(1, role);
                	ps.setString(2, tenantCode);
                    return ps.executeUpdate();
                } catch (SQLException sqle) {
                    LOGGER.error("Error occured while deleting from perm_role_mapping : {}",sqle);
                    throw sqle;
                }
            }
        });
	}
	
	private void insertRolePermission(final String id, final String privilegeId){
		LOGGER.debug("Query to delete existing permission role mapping :" + INSERT_PERM_ROLE_MAP);
        jdbcTemplate.execute(INSERT_PERM_ROLE_MAP, new PreparedStatementCallback<Integer>() {
            @Override
            public Integer doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                try {
                	ps.setString(1, id);
                	ps.setString(2, privilegeId);
                    return ps.executeUpdate();
                } catch (SQLException sqle) {
                    LOGGER.error("Error occured while deleting from perm_role_mapping : {}",sqle);
                    throw sqle;
                }
            }
        });
	}

    
}
