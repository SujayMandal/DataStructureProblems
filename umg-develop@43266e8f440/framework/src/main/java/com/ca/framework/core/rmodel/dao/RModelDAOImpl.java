package com.ca.framework.core.rmodel.dao;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.systemparameter.SystemParameterProvider;

public class RModelDAOImpl implements RModelDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(RModelDAOImpl.class);

    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    @Inject
    private SystemParameterProvider sysParam;

    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String TENANT_ID = "TENANT_ID";
    private static final String MAJOR_VERSION = "MAJOR_VERSION";
    private static final String MINOR_VERSION = "MINOR_VERSION";
    private static final String VERSION_NAME = "VERSION_NAME";
    public static final String UMG_ADMIN = "umg_admin";
    

    public static final String GET_SUPPORT_PACKAGES = "SELECT distinct mep.PACKAGE_FOLDER, mep.PACKAGE_NAME, mep.PACKAGE_TYPE, mep.PACKAGE_VERSION, mep.COMPILED_OS, "
            + "mee.EXECUTION_ENVIRONMENT, mee.ENVIRONMENT_VERSION, maping.EXEC_SEQUENCE "
            + "FROM MODEL_LIBRARY ml join UMG_VERSION uv ON ml.ID = uv.MODEL_LIBRARY_ID "
            + "join MODEL_LIB_EXEC_PKG_MAPPING maping ON maping.MODEL_LIBRARY_ID = ml.ID "
            + "join umg_admin.MODEL_EXEC_PACKAGES mep ON mep.ID = maping.MODEL_EXEC_PKG_ID "
            + "join umg_admin.MODEL_EXECUTION_ENVIRONMENTS mee ON mep.MODEL_EXEC_ENV_NAME = mee.NAME "
            + "WHERE lower(uv.NAME)=:VERSION_NAME AND uv.MAJOR_VERSION=:MAJOR_VERSION AND uv.MINOR_VERSION=:MINOR_VERSION AND lower(uv.TENANT_ID)=:TENANT_ID";

    public static final String GET_MODEL_PACKAGE_NAME = "SELECT PACKAGE_NAME, ml.JAR_NAME as JAR_NAME, ml.UMG_NAME as MODEL_LIBRARY_VERSION_NAME, ml.NAME as MODEL_NAME, uv.NAME as VERSION_NAME  FROM MODEL_LIBRARY ml join UMG_VERSION uv ON ml.ID = uv.MODEL_LIBRARY_ID "
            + "WHERE lower(uv.NAME)=:VERSION_NAME AND uv.MAJOR_VERSION=:MAJOR_VERSION AND uv.MINOR_VERSION=:MINOR_VERSION AND lower(uv.TENANT_ID)=:TENANT_ID";

    public static final String GET_ALL_SUPPORT_PACKAGES = "SELECT distinct uv.NAME as VERSION_NAME, uv.MAJOR_VERSION as MAJOR_VERSION, uv.MINOR_VERSION as MINOR_VERSION, mep.PACKAGE_FOLDER, mep.PACKAGE_NAME, mep.PACKAGE_TYPE, mep.PACKAGE_VERSION, mep.COMPILED_OS, "
            + "mee.EXECUTION_ENVIRONMENT, mee.ENVIRONMENT_VERSION, maping.EXEC_SEQUENCE "
            + "FROM MODEL_LIBRARY ml join UMG_VERSION uv ON ml.ID = uv.MODEL_LIBRARY_ID "
            + "join MODEL_LIB_EXEC_PKG_MAPPING maping ON maping.MODEL_LIBRARY_ID = ml.ID "
            + "join umg_admin.MODEL_EXEC_PACKAGES mep ON mep.ID = maping.MODEL_EXEC_PKG_ID "
            + "join umg_admin.MODEL_EXECUTION_ENVIRONMENTS mee ON mep.MODEL_EXEC_ENV_NAME = mee.NAME "
            + "WHERE uv.TENANT_ID=:TENANT_ID ORDER BY uv.NAME,uv.MAJOR_VERSION,uv.MINOR_VERSION";

    public static final String GET_ALL_MODEL_PACKAGE_NAMES = "SELECT CONCAT(uv.NAME,'-',uv.MAJOR_VERSION,'-',uv.MINOR_VERSION) AS VERSION_KEY,PACKAGE_NAME FROM MODEL_LIBRARY ml join UMG_VERSION uv ON ml.ID = uv.MODEL_LIBRARY_ID "
            + "WHERE uv.TENANT_ID=:TENANT_ID ORDER BY uv.NAME,uv.MAJOR_VERSION,uv.MINOR_VERSION";

    public static final String GET_ALL_EXEC_ENVT_VERSION_MAPPING = "SELECT CONCAT(uv.NAME,'-',uv.MAJOR_VERSION,'-',uv.MINOR_VERSION) AS VERSION_KEY,CONCAT(uv.NAME,'-',uv.MAJOR_VERSION) AS MAJOR_VERSION_KEY, me.ENVIRONMENT_VERSION AS ENVIRONMENT_VERSION,  me.EXECUTION_ENVIRONMENT AS EXECUTION_LANGUAGE ,ml.EXECUTION_ENVIRONMENT AS EXECUTION_ENVIRONMENT FROM MODEL_LIBRARY ml "
            + "join UMG_VERSION uv ON ml.ID = uv.MODEL_LIBRARY_ID "
            + "join umg_admin.MODEL_EXECUTION_ENVIRONMENTS me ON ml.MODEL_EXEC_ENV_NAME = me.NAME "
            + "WHERE uv.TENANT_ID=:TENANT_ID";
    public static final String GET_ENVIRONMENT_DETAILS = "SELECT me.ENVIRONMENT_VERSION AS ENVIRONMENT_VERSION,  me.EXECUTION_ENVIRONMENT AS EXECUTION_LANGUAGE ,ml.EXECUTION_ENVIRONMENT AS EXECUTION_ENVIRONMENT FROM MODEL_LIBRARY ml "
            + "join UMG_VERSION uv ON ml.ID = uv.MODEL_LIBRARY_ID "
            + "join umg_admin.MODEL_EXECUTION_ENVIRONMENTS me ON ml.MODEL_EXEC_ENV_NAME = me.NAME "
            + "WHERE uv.TENANT_ID=:TENANT_ID AND LOWER(uv.NAME)=:VERSION_NAME AND uv.MAJOR_VERSION=:MAJOR_VERSION AND uv.MINOR_VERSION=:MINOR_VERSION";
    
    public static final String GET_ACTIVE_VERSION = "SELECT ENVIRONMENT_VERSION FROM umg_admin.MODEL_EXECUTION_ENVIRONMENTS WHERE EXECUTION_ENVIRONMENT=:EXECUTION_ENVIRONMENT AND IS_ACTIVE='T'";
    
    public static final String GET_MAPPING_OUTPUT = "SELECT M.MODEL_IO_DATA AS OUTPUT FROM MAPPING M JOIN UMG_VERSION V ON V.MAPPING_ID=M.ID WHERE V.TENANT_ID=:TENANT_ID AND V.NAME=:VERSION_NAME AND V.MAJOR_VERSION=:MAJOR_VERSION AND V.MINOR_VERSION=:MINOR_VERSION";

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<SupportPackage> getSupportPackageList(String modelName, Integer majorVersion, Integer minorVersion,
            final String tenantCode) {
        getRequestContext().setAdminAware(false);
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, tenantCode.toLowerCase()); // RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(VERSION_NAME, modelName.toLowerCase());
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);
        final List<SupportPackage> supportPkgList = new ArrayList<SupportPackage>();
        LOGGER.error("Get Support Paclkage Query: {}", GET_SUPPORT_PACKAGES);
        LOGGER.error("Query Values, Tenant : {}, Model : {}, Major Version : {}, Minor Version : {}", tenantCode.toLowerCase(),
                modelName.toLowerCase(), majorVersion, minorVersion);
        jdbcTemplate.query(GET_SUPPORT_PACKAGES.replace(UMG_ADMIN, sysParam.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)),
                valueMap, new RowMapper<SupportPackage>() {
                    @Override
                    public SupportPackage mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        SupportPackage supportPkg = new SupportPackage();
                        supportPkg.setPackageFolder(rs.getString("PACKAGE_FOLDER"));
                        supportPkg.setPackageName(rs.getString("PACKAGE_NAME"));
                        supportPkg.setPackageType(rs.getString("PACKAGE_TYPE"));
                        supportPkg.setPackageVersion(rs.getString("PACKAGE_VERSION"));
                        supportPkg.setCompiledOs(rs.getString("COMPILED_OS"));
                        supportPkg.setExecEnv(rs.getString("EXECUTION_ENVIRONMENT"));
                        supportPkg.setEnvVersion(rs.getString("ENVIRONMENT_VERSION"));
                        supportPkg.setHierarchy(rs.getInt("EXEC_SEQUENCE"));

                        supportPkgList.add(supportPkg);
                        return supportPkg;
                    }
                });
        LOGGER.error("supportPkgList size is :"+supportPkgList.size()+" list is :"+supportPkgList);
        return supportPkgList;
    }

    @Override
    public Map<String, String> getModelPackageName(final String modelName, final Integer majorVersion, final Integer minorVersion,
            final String tenantCode) {
        getRequestContext().setAdminAware(false);
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, tenantCode.toLowerCase()); // RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(VERSION_NAME, modelName.toLowerCase());
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);

        final List<Map<String, String>> packageNameList = new ArrayList<Map<String, String>>();
        LOGGER.info("Get R Model Package Query: {}", GET_MODEL_PACKAGE_NAME);
        LOGGER.info("Query Values, Tenant : {}, Model : {}, Major Version : {}, Minor Version : {}", tenantCode.toLowerCase(),
                modelName.toLowerCase(), majorVersion, minorVersion);
        jdbcTemplate.query(GET_MODEL_PACKAGE_NAME, valueMap, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                final Map<String, String> result = new HashMap<String, String>();
                result.put("PACKAGE_NAME", rs.getString("PACKAGE_NAME"));
                result.put("JAR_NAME", rs.getString("JAR_NAME"));
                result.put("MODEL_LIBRARY_VERSION_NAME", rs.getString("MODEL_LIBRARY_VERSION_NAME"));
                result.put("MODEL_NAME", rs.getString("MODEL_NAME"));
                result.put("VERSION_NAME", rs.getString("VERSION_NAME"));
                packageNameList.add(result);
                return rs.getString("PACKAGE_NAME");
            }
        });

        return packageNameList.get(0);
    }

    public SystemParameterProvider getSysParam() {
        return sysParam;
    }

    public void setSysParam(SystemParameterProvider sysParam) {
        this.sysParam = sysParam;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.framework.core.rmodel.dao.RModelDAO#getAllSupportPackages(java.lang.String)
     */
    @Override
    public List<SupportPackage> getAllSupportPackages(String tenantCode) throws SystemException {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, tenantCode);
        final List<SupportPackage> supportPkgList = new ArrayList<SupportPackage>();
        jdbcTemplate.query(GET_ALL_SUPPORT_PACKAGES.replace(UMG_ADMIN, sysParam.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)),
                valueMap, new RowMapper<SupportPackage>() {
                    @Override
                    public SupportPackage mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        SupportPackage supportPackage = new SupportPackage();
                        supportPackage.setVersionName(rs.getString("VERSION_NAME"));
                        supportPackage.setMajorVersion(rs.getString("MAJOR_VERSION"));
                        supportPackage.setMinorVersion(rs.getString("MINOR_VERSION"));
                        supportPackage.setPackageFolder(rs.getString("PACKAGE_FOLDER"));
                        supportPackage.setPackageName(rs.getString("PACKAGE_NAME"));
                        supportPackage.setPackageType(rs.getString("PACKAGE_TYPE"));
                        supportPackage.setPackageVersion(rs.getString("PACKAGE_VERSION"));
                        supportPackage.setCompiledOs(rs.getString("COMPILED_OS"));
                        supportPackage.setExecEnv(rs.getString("EXECUTION_ENVIRONMENT"));
                        supportPackage.setEnvVersion(rs.getString("ENVIRONMENT_VERSION"));
                        supportPackage.setHierarchy(rs.getInt("EXEC_SEQUENCE"));
                        supportPkgList.add(supportPackage);
                        return supportPackage;
                    }
                });
        return supportPkgList;
    }

    @Override
    public Map<String, String> getAllModelPackageNames(String tenantCode) throws SystemException {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, tenantCode);
        final Map<String, String> allModelPkgNameMap = new HashMap<String, String>();

        jdbcTemplate.query(GET_ALL_MODEL_PACKAGE_NAMES, valueMap, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                allModelPkgNameMap.put(rs.getString(SystemConstants.VERSION_KEY), rs.getString("PACKAGE_NAME"));
                return rs.getString("PACKAGE_NAME");
            }
        });
        return allModelPkgNameMap;
    }

    @Override
    public Map<String, Map<String, VersionExecInfo>>  getAllVersionEnvironmentMap(
            String tenantCode) throws SystemException {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, tenantCode);

        final Map<String, VersionExecInfo> allVersionMap = new HashMap<String, VersionExecInfo>(); 
        final Map<String, VersionExecInfo> allModelMajorVersionMap = new HashMap<String, VersionExecInfo>(); 
        
        final Map<String, Map<String, VersionExecInfo>> allModelVersionMap = new HashMap<String, Map<String, VersionExecInfo>>();   

        jdbcTemplate.query(
                GET_ALL_EXEC_ENVT_VERSION_MAPPING.replace(UMG_ADMIN, sysParam.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)),
                valueMap, new RowMapper<VersionExecInfo>() {
                    @Override
                    public VersionExecInfo mapRow(ResultSet rs, int rowNum)
                            throws SQLException, DataAccessException {
                      VersionExecInfo versionExecInfo = new VersionExecInfo();
                      versionExecInfo.setExecLanguage(StringUtils.upperCase(rs.getString(SystemConstants.EXECUTION_LANGUAGE)));
                      versionExecInfo.setExecEnv(rs.getString(SystemConstants.EXECUTION_ENVIRONMENT));
                      versionExecInfo.setExecLangVer(rs.getString(SystemConstants.ENVIRONMENT_VERSION));               
                      allVersionMap.put(rs.getString(SystemConstants.VERSION_KEY), versionExecInfo);
                      allModelMajorVersionMap.put(rs.getString(SystemConstants.MAJOR_VERSION_KEY), versionExecInfo);
                      return versionExecInfo;
                    }
                });
        allModelVersionMap.put(SystemConstants.VERSION_KEY, allVersionMap);
        allModelVersionMap.put(SystemConstants.MAJOR_VERSION_KEY, allModelMajorVersionMap);
     
        return allModelVersionMap;
    }
    
    @Override
    public VersionExecInfo getEnvironmentDetails(String tenantCode,
            String versionName,String majorVer,String minorVer) throws SystemException {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, tenantCode);
        valueMap.addValue(VERSION_NAME, StringUtils.lowerCase(versionName));
        valueMap.addValue(MAJOR_VERSION, majorVer);
        valueMap.addValue(MINOR_VERSION, minorVer);
        final List<VersionExecInfo> infos = new ArrayList<VersionExecInfo>(); 
        VersionExecInfo execInfo = null;      

        jdbcTemplate.query(
        		GET_ENVIRONMENT_DETAILS,
                valueMap, new RowMapper<VersionExecInfo>() {
                    @Override
                    public VersionExecInfo mapRow(ResultSet rs, int rowNum)
                            throws SQLException, DataAccessException {
                      VersionExecInfo versionExecInfo = new VersionExecInfo();
                      versionExecInfo.setExecLanguage(StringUtils.upperCase(rs.getString(SystemConstants.EXECUTION_LANGUAGE)));
                      versionExecInfo.setExecEnv(rs.getString(SystemConstants.EXECUTION_ENVIRONMENT));
                      versionExecInfo.setExecLangVer(rs.getString(SystemConstants.ENVIRONMENT_VERSION));         
                      infos.add(versionExecInfo);              
                      return versionExecInfo;
                    }
                });
	     if(!infos.isEmpty()){
	    	 execInfo = infos.get(0);
	    	 MapSqlParameterSource activeVerValueMap = new MapSqlParameterSource();
		     activeVerValueMap.addValue("EXECUTION_ENVIRONMENT", execInfo.getExecLanguage());	     
		     List<Map<String,Object>> activeBVersions = jdbcTemplate.queryForList(
		    		 GET_ACTIVE_VERSION.replace(UMG_ADMIN, sysParam.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)),
		    		 activeVerValueMap);
		     if(activeBVersions.size()>0){
		    	 execInfo.setExecLangVer((String)activeBVersions.get(0).get("ENVIRONMENT_VERSION"));	
		     }
	     }	 
	     return execInfo;
    }

	@Override
	public byte[] getMappingOutput(String tenantCode, String versionName, int majorVersion,
			int minorVersion){		
		boolean adminAware = getRequestContext().isAdminAware();	
		LOGGER.error("adminAware is before setting tenant datasource:"+adminAware);
		byte[] mappingOutputArr = null;
	    getRequestContext().setAdminAware(false);
		MapSqlParameterSource valueMap = new MapSqlParameterSource();		
        valueMap.addValue(TENANT_ID, tenantCode);
        valueMap.addValue(VERSION_NAME, versionName);
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);
        mappingOutputArr =   jdbcTemplate.queryForObject(GET_MAPPING_OUTPUT, valueMap,byte[].class);
        getRequestContext().setAdminAware(adminAware);     
		LOGGER.error("adminAware is after coming from tenant datasource:"+adminAware);
        return mappingOutputArr;
	}
}