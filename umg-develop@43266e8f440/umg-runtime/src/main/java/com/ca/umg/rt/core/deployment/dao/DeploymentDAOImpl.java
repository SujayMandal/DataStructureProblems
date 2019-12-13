/*
 * DeploymentDAOImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.deployment.dao;

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.rt.core.deployment.constants.DeploymentQueryConstants;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.flow.entity.MappingData;
import com.ca.umg.rt.core.flow.entity.ModelLibrary;
import com.ca.umg.rt.core.flow.entity.Query;
import com.ca.umg.rt.core.flow.entity.Version;
import com.ca.umg.rt.core.flow.entity.VersionMapping;
import com.ca.umg.rt.core.flow.entity.VersionModelLibrary;
import com.ca.umg.rt.core.flow.entity.VersionQuery;
import com.ca.umg.rt.flows.container.EnvironmentVariables;

/**
 * 
 **/
@Repository
public class DeploymentDAOImpl implements DeploymentDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentDAOImpl.class);

    private static final String TENANT_ID = "TENANT_ID";
    private static final String NAME = "NAME";
    private static final String MAJOR_VERSION = "MAJOR_VERSION";
    private static final String MINOR_VERSION = "MINOR_VERSION";
    private static final String VERSION_NAME = "VERSION_NAME";
    private static final String VERSION_ID = "VERSION_ID";
    private static final String ALLOW_NULL = "ALLOW_NULL";
    private static final String STATUS = "STATUS";
    private static final String MODEL_TYPE = "MODEL_TYPE";
    private static final String ARRAY = "ARRAY";
    private static final String PRIMITIVE = "PRIMITIVE";
    private static final String SINGLE_DIM_ARRAY = "SINGLE_DIM_ARRAY";
    private static final String PUBLISHED = "PUBLISHED";

    private NamedParameterJdbcTemplate jdbcTemplate;
    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    @Inject
    private SystemParameterProvider sysParam;

    /**
     * DOCUMENT ME!
     **/
    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * DOCUMENT ME!
     *
     * @param modelName
     *            DOCUMENT ME!
     * @param majorVersion
     *            DOCUMENT ME!
     * @param minorVersion
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    @Override
    public List<Version> getVersion(String modelName, Integer majorVersion, Integer minorVersion) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        LOGGER.info("Finding versions for tenant {}.", RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(NAME, StringUtils.lowerCase(modelName));
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);
        return jdbcTemplate.query(DeploymentQueryConstants.GET_VERSION, valueMap, new RowMapper<Version>() {
            @Override
            public Version mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Version version = new Version();
                version.setId(rs.getString(VERSION_ID));
                version.setName(rs.getString(VERSION_NAME));
                version.setMajorVersion(rs.getInt(MAJOR_VERSION));
                version.setMinorVersion(rs.getInt(MINOR_VERSION));
                version.setStatus(rs.getString(STATUS));
                version.setModelType(rs.getString(MODEL_TYPE));
                return version;
            }
        });
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param modelName
     *            DOCUMENT ME!
     * @param majorVersion
     *            DOCUMENT ME!
     * @param minorVersion
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    @Override
    public List<Integer> getMaxMinorVersion(String modelName, Integer majorVersion) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(NAME, StringUtils.lowerCase(modelName));
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(STATUS, PUBLISHED);
        LOGGER.info("Finding max published minor version for model name {}, major version {} in tenant {}.", StringUtils.lowerCase(modelName), majorVersion, RequestContext.getRequestContext().getTenantCode());
        return jdbcTemplate.query(DeploymentQueryConstants.GET_MAX_MINOR_VERSION, valueMap, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
            	Integer minorVersion = rs.getInt(MINOR_VERSION);
            	return minorVersion;
            }
        });
    }

    /**
     * DOCUMENT ME!
     *
     * @param modelName
     *            DOCUMENT ME!
     * @param majorVersion
     *            DOCUMENT ME!
     * @param minorVersion
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    @Override
    public Map<Version, VersionMapping> getVersionMapping(String modelName, Integer majorVersion, Integer minorVersion) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(NAME, modelName);
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);

        final Map<Version, VersionMapping> versions = new HashMap<Version, VersionMapping>();
        jdbcTemplate.query(DeploymentQueryConstants.GET_VERSION_MAPPING, valueMap, new RowMapper<VersionMapping>() {
            @Override
            public VersionMapping mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Version version = new Version();
                version.setName(rs.getString(VERSION_NAME));
                version.setMajorVersion(rs.getInt(MAJOR_VERSION));
                version.setMinorVersion(rs.getInt(MINOR_VERSION));
                version.setAllowNull(rs.getBoolean(ALLOW_NULL));
                VersionMapping versionMapping = versions.get(version);

                if (versions.get(version) == null) {
                    versionMapping = new VersionMapping();
                    versions.put(version, versionMapping);
                }

                MappingData inputMappingData = new MappingData();
                inputMappingData.setMapping(rs.getBytes("INPUT_MAPPING_DATA"));
                inputMappingData.setTid(rs.getBytes("INPUT_TID"));

                MappingData outputMappingData = new MappingData();
                outputMappingData.setMapping(rs.getBytes("OUTPUT_MAPPING_DATA"));
                outputMappingData.setTid(rs.getBytes("OUTPUT_TID"));
                versionMapping.setInput(inputMappingData);
                versionMapping.setOutput(outputMappingData);
                versionMapping.setVersion(version);
                versionMapping.setModelIoData(rs.getBytes("MODEL_IO_DATA"));
                versionMapping.setName(rs.getString("MAPPING_NAME"));
                return versionMapping;
            }
        });
        return versions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param modelName
     *            DOCUMENT ME!
     * @param majorVersion
     *            DOCUMENT ME!
     * @param minorVersion
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    @Override
    public Map<Version, VersionModelLibrary> getVersionModelLibrary(String modelName, Integer majorVersion,
            Integer minorVersion) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(NAME, modelName);
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);

        final Map<Version, VersionModelLibrary> versions = new HashMap<Version, VersionModelLibrary>();
        jdbcTemplate.query(DeploymentQueryConstants.GET_VERSION_LIBRARY, valueMap, new RowMapper<VersionModelLibrary>() {
            @Override
            public VersionModelLibrary mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Version version = new Version();
                version.setName(rs.getString(VERSION_NAME));
                version.setMajorVersion(rs.getInt(MAJOR_VERSION));
                version.setMinorVersion(rs.getInt(MINOR_VERSION));

                VersionModelLibrary versionModelLibrary = versions.get(version);

                if (versions.get(version) == null) {
                    versionModelLibrary = new VersionModelLibrary();
                    versions.put(version, versionModelLibrary);
                }

                ModelLibrary modelLibrary = new ModelLibrary();
                modelLibrary.setName(rs.getString("MODEL_LIBRARY_NAME"));
                modelLibrary.setUmgName(rs.getString("MODEL_LIBRARY_UMG_NAME"));
                modelLibrary.setDescription(rs.getString("MODEL_LIBRARY_DESCRIPTION"));
                modelLibrary.setLanguage(rs.getString("EXECUTION_LANGUAGE"));
                modelLibrary.setType(rs.getString("EXECUTION_TYPE"));
                modelLibrary.setJarName(rs.getString("JAR_NAME"));
                modelLibrary.setExcEnv(rs.getString("EXECUTION_ENVIRONMENT"));
                modelLibrary.setChecksum(rs.getString("CHECKSUM"));
                versionModelLibrary.setVersion(version);
                versionModelLibrary.setModelLibrary(modelLibrary);
                return versionModelLibrary;
            }
        });
        return versions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param modelName
     *            DOCUMENT ME!
     * @param majorVersion
     *            DOCUMENT ME!
     * @param minorVersion
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    @Override
    public Map<Version, List<VersionQuery>> getVersionQuery(String modelName, Integer majorVersion, Integer minorVersion) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(NAME, modelName);
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);

        final Map<Version, List<VersionQuery>> versions = new HashMap<Version, List<VersionQuery>>();
        jdbcTemplate.query(DeploymentQueryConstants.GET_VERSION_QUERY, valueMap, new RowMapper<VersionQuery>() {
            @Override
            public VersionQuery mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Version version = new Version();
                version.setName(rs.getString(VERSION_NAME));
                version.setMajorVersion(rs.getInt(MAJOR_VERSION));
                version.setMinorVersion(rs.getInt(MINOR_VERSION));

                List<VersionQuery> versionQueryList = versions.get(version);

                if (versionQueryList == null) {
                    versionQueryList = new ArrayList<VersionQuery>();
                    versions.put(version, versionQueryList);
                }
                VersionQuery versionQuery = new VersionQuery();
                Query query = new Query();
                query.setName(rs.getString("QUERY_NAME"));
                query.setSequence(rs.getInt("EXEC_SEQUENCE"));
                query.setSql(rs.getString("QUERY_STRING"));
                query.setMultipleRow(rs.getString("ROW_TYPE").equals("MULTIPLEROW") ? true : false);
                String dataType = rs.getString("DATA_TYPE");
                if (ARRAY.equals(dataType) || PRIMITIVE.equals(dataType) || SINGLE_DIM_ARRAY.equals(dataType)) {
                    query.setArray(true);
                } else {
                    query.setArray(false);
                }
                versionQuery.setQuery(query);
                versionQuery.setVersion(version);
                versionQueryList.add(versionQuery);
                return versionQuery;
            }
        });
        return versions;
    }

    @Override
    public VersionExecInfo getExecutionLanguage(final String modelName, final Integer majorVersion,
            final Integer minorVersion) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(VERSION_NAME, StringUtils.lowerCase(modelName));
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);
        LOGGER.debug("Getting execution langauge for version details : " + modelName + " " + majorVersion + " " + minorVersion
                + " of tenant code : " + RequestContext.getRequestContext().getTenantCode());
        final List<VersionExecInfo> packageNameList = new ArrayList<VersionExecInfo>();
        LOGGER.debug("Getting envt version for version details query : " + DeploymentQueryConstants.GET_EXEC_LANGUAGE_VERSION
                .replace(RuntimeConstants.UMG_ADMIN, sysParam.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)));
        jdbcTemplate.query(
                DeploymentQueryConstants.GET_EXEC_LANGUAGE_VERSION.replace(RuntimeConstants.UMG_ADMIN,
                        sysParam.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)),
                valueMap, new RowMapper<VersionExecInfo>() {
                    @Override
                    public VersionExecInfo mapRow(ResultSet rs, int rowNum)
                            throws SQLException, DataAccessException {                   	
                        final VersionExecInfo versionExecInfo = new VersionExecInfo();
                        versionExecInfo.setExecEnv(rs.getString(SystemConstants.EXECUTION_ENVIRONMENT));
                        versionExecInfo.setExecLanguage(StringUtils.upperCase(rs.getString(SystemConstants.EXECUTION_LANGUAGE)));
                        versionExecInfo.setExecLangVer(rs.getString(SystemConstants.ENVIRONMENT_VERSION));
                        packageNameList.add(versionExecInfo);
                        return versionExecInfo;
                    }
                });

        if (CollectionUtils.isNotEmpty(packageNameList)) {
            return packageNameList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Map<String,String> getExecutionEnvironment(String modelName, Integer majorVersion, Integer minorVersion) throws SystemException {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(VERSION_NAME, StringUtils.lowerCase(modelName));
        valueMap.addValue(MAJOR_VERSION, majorVersion);
        valueMap.addValue(MINOR_VERSION, minorVersion);
        LOGGER.debug("Getting environment version for version details : " + modelName + " " + majorVersion + " " + minorVersion
                + " of tenant code : " + RequestContext.getRequestContext().getTenantCode());
        final Map<String,String> envList = new HashMap<String,String>();
        LOGGER.debug("Getting envt version for version details query : " + DeploymentQueryConstants.GET_EXEC_LANGUAGE_VERSION
                .replace(RuntimeConstants.UMG_ADMIN, sysParam.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)));

        jdbcTemplate.query(DeploymentQueryConstants.GET_EXECUTION_ENVIRONMENT.replace(RuntimeConstants.UMG_ADMIN,
                sysParam.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)), valueMap, new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        final String excEnv = rs.getString("EXC_ENV");
                        final String chkSum = rs.getString("CHECKSUM");
                        envList.put(EnvironmentVariables.MODEL_CHECKSUM,chkSum);
                        envList.put(EnvironmentVariables.EXE_ENV,excEnv);
                        return excEnv;
                    }
                });

        if (CollectionUtils.isNotEmpty(envList.keySet())) {
            return envList;
        } else {
            return null;
        }

    }
}
