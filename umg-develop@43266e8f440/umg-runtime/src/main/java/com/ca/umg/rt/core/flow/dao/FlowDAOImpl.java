/*
 * FlowDaoImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.dao;

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

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ca.framework.core.encryption.EncryptionUtil;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.core.flow.constants.FlowQueryConstants;
import com.ca.umg.rt.core.flow.entity.MappingData;
import com.ca.umg.rt.core.flow.entity.ModelLibrary;
import com.ca.umg.rt.core.flow.entity.Query;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.core.flow.entity.TenantConfig;
import com.ca.umg.rt.core.flow.entity.Version;
import com.ca.umg.rt.core.flow.entity.VersionMapping;
import com.ca.umg.rt.core.flow.entity.VersionModelLibrary;
import com.ca.umg.rt.core.flow.entity.VersionQuery;

/**
 * 
 **/
@Named
public class FlowDAOImpl implements FlowDAO {
    private static final String TENANT_ID = "TENANT_ID";
    private static final String NAME = "NAME";
    private static final String MAJOR_VERSION = "MAJOR_VERSION";
    private static final String MINOR_VERSION = "MINOR_VERSION";
    private static final String VERSION_NAME = "VERSION_NAME";
    private static final String ALLOW_NULL = "ALLOW_NULL";
    private static final String VERSION_ID = "VERSION_ID";
    private static final String MODEL_TYPE = "MODEL_TYPE";
    private static final String ARRAY = "ARRAY";
    private static final String PRIMITIVE = "PRIMITIVE";
    private static final String SINGLE_DIM_ARRAY = "SINGLE_DIM_ARRAY";
    private static final String TENANT_CODE = "TENANT_CODE";
    private static final String WRAPPER_TYPE = "WRAPPER_TYPE";
    private static final String SYSTEM_KEY = "SYSTEM_KEY";
    private static final String CONFIG_VALUE = "CONFIG_VALUE";

    private NamedParameterJdbcTemplate jdbcTemplate;
    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    /**
     * Initilize {@link JdbcTemplate} with {@link DataSource} instance.
     **/
    @PostConstruct
    public void initializeTemplate() {
        setJdbcTemplate(new NamedParameterJdbcTemplate(dataSource));
    }

    /**
     * DOCUMENT ME!
     * 
     * @return
     **/
    public Map<Version, VersionMapping> getAllVersionMapping() {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());

        final Map<Version, VersionMapping> versions = new HashMap<Version, VersionMapping>();
        jdbcTemplate.query(FlowQueryConstants.GET_ALL_VERSION_MAPPING, valueMap, new RowMapper<VersionMapping>() {
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
     * @return DOCUMENT ME!
     **/
    public Map<Version, List<VersionQuery>> getAllVersionQuery() {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());

        final Map<Version, List<VersionQuery>> versions = new HashMap<Version, List<VersionQuery>>();
        jdbcTemplate.query(FlowQueryConstants.GET_ALL_VERSION_QUERY, valueMap, new RowMapper<VersionQuery>() {
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

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    public Map<Version, VersionModelLibrary> getAllVersionModelLibrary() {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());

        final Map<Version, VersionModelLibrary> versions = new HashMap<Version, VersionModelLibrary>();
        jdbcTemplate.query(FlowQueryConstants.GET_ALL_VERSION_LIBRARY, valueMap, new RowMapper<VersionModelLibrary>() {
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
     * Get the instance of {@link JdbcTemplate}
     * 
     * @return An instance of {@link JdbcTemplate}
     **/
    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param jdbcTemplate
     *            An instance of {@link JdbcTemplate}
     **/
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return the {@link DataSource} associated with {@link FlowDAOImpl} instance.
     **/
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param dataSource
     *            the {@link DataSource} to set
     **/
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    @Override
    public List<Tenant> getAllTenants() {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();

        final Map<String, Tenant> tenants = new HashMap<String, Tenant>();
        getTenants(FlowQueryConstants.GET_ALL_ACTIVE_TENANT, valueMap, tenants);
        getTenants(FlowQueryConstants.GET_ALL_PENDING_TENANT, valueMap, tenants);
        return new ArrayList<Tenant>(tenants.values());
    }

    private void getTenants(String query, MapSqlParameterSource valueMap, final Map<String, Tenant> tenants) {
        jdbcTemplate.query(query, valueMap, new RowMapper<Tenant>() {
            @Override
            public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                String tenantName = rs.getString(NAME);
                Tenant tenant = (Tenant) tenants.get(tenantName);

                if (tenant == null) {
                    tenant = new Tenant();
                    tenant.setName(rs.getString(NAME));
                    tenant.setCode(rs.getString("CODE"));
                    tenant.setDescription(rs.getString("DESCRIPTION"));
                    tenant.setAuthCode(EncryptionUtil.decryptToken(rs.getString("AUTH_CODE")));
                    tenant.setConfigList(new ArrayList<TenantConfig>());
                    tenants.put(tenant.getName(), tenant);
                }

                TenantConfig tenantConfig = new TenantConfig();
                tenantConfig.setKey(rs.getString("SYSTEM_KEY"));
                tenantConfig.setKeyType(rs.getString("KEY_TYPE"));
                tenantConfig.setValue(rs.getString("CONFIG_VALUE"));
                tenant.getConfigList().add(tenantConfig);
                return tenant;
            }
        });
    }

    /**
     * Get all versions available in the system. Queries UMG_VERSION table and retrieve all entries available.
     * 
     * @return {@link List} of {@link Version}
     **/
    @Override
    public List<Version> getAllVersions() {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        return jdbcTemplate.query(FlowQueryConstants.GET_ALL_VERSION, valueMap, new RowMapper<Version>() {
            @Override
            public Version mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Version version = new Version();
                version.setId(rs.getString(VERSION_ID));
                version.setName(rs.getString(VERSION_NAME));
                version.setMajorVersion(rs.getInt(MAJOR_VERSION));
                version.setMinorVersion(rs.getInt(MINOR_VERSION));
                version.setModelType(rs.getString(MODEL_TYPE));
                return version;
            }
        });
    }

    @Override
    public List<Tenant> getAllBatchEnabledTenants() {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();

        final Map<String, Tenant> tenants = new HashMap<String, Tenant>();
        jdbcTemplate.query(FlowQueryConstants.GET_ALL_BATCH_ENABLED_TENANTS, valueMap, new RowMapper<Tenant>() {
            @Override
            public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                String tenantName = rs.getString(NAME);
                Tenant tenant = (Tenant) tenants.get(tenantName);

                if (tenant == null) {
                    tenant = new Tenant();
                    tenant.setDescription(rs.getString("DESCRIPTION"));
                    tenant.setCode(rs.getString("CODE"));
                    tenant.setName(rs.getString(NAME));

                    tenant.setConfigList(new ArrayList<TenantConfig>());
                    tenants.put(tenant.getName(), tenant);
                }

                TenantConfig tenantConfig = new TenantConfig();
                tenantConfig.setValue(rs.getString(CONFIG_VALUE));
                tenantConfig.setKey(rs.getString(SYSTEM_KEY));
                tenantConfig.setKeyType(rs.getString("KEY_TYPE"));
                tenant.getConfigList().add(tenantConfig);
                return tenant;
            }
        });
        return new ArrayList<Tenant>(tenants.values());
    }

    @Override
    public Map<String, String> loadWrapperDetail(String wrapperType) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_CODE, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(WRAPPER_TYPE, wrapperType);
        final Map<String, String> tenantConfigs = new HashMap<String, String>();
        jdbcTemplate.query(FlowQueryConstants.GET_BATCH_ENABLED_CONFIGS, valueMap, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                String systemKey = rs.getString(SYSTEM_KEY);
                tenantConfigs.put(rs.getString(SYSTEM_KEY), rs.getString(CONFIG_VALUE));
                return systemKey;
            }
        });
        return tenantConfigs;
    }

    @Override
    public List<String> getAllEnabledWrappers() {
        final List<String> wrappers = new ArrayList<String>();
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(TENANT_CODE, RequestContext.getRequestContext().getTenantCode());
        jdbcTemplate.query(FlowQueryConstants.GET_ALL_ENABLED_WRAPPERS, valueMap, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                String systemKey = rs.getString(SYSTEM_KEY);
                wrappers.add(systemKey);
                return systemKey;
            }
        });
        return wrappers;
    }
}
