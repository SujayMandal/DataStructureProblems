
/**
 * 
 */
package com.ca.umg.business.version.dao;

import static com.ca.umg.business.util.AdminUtil.getLikePattern;
import static java.util.Locale.getDefault;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.pool.util.PoolCriteriaUtil;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author kamathan
 *
 */
@Repository
@SuppressWarnings("PMD")
public class VersionContainerDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionContainerDAO.class);

    private static final String ESCAPE_FOR_SINGLE_QUOTE = "\\\\'";

    private static final String SINGLE_QUOTE_REXP = "[']";

    private static final String SINGLE_QUOTE = "'";

    private static final String ESCAPE_FOR_PERCENTAGE = "\\\\%";

    private static final String PERCENTAGE_REXP = "[%]";

    private static final String PERCENTAGE = "%";

    private static final String ESCAPE_FOR_UNDER_SCORE = "\\\\_";

    private static final String UNDER_SCORE_REXP = "[_]";

    private static final String UNDER_SCORE = "_";

    private static final String TENANT_ID = "TENANT_ID";

    private static final String MAJOR_VERSION = "MAJOR_VERSION";

    private static final String MINOR_VERSION = "MINOR_VERSION";

    private static final String VERSION_NAME = "VERSION_NAME";

    private static final String FETCH_ALL_TENANT = "SELECT CODE FROM TENANT";

    private static final String FETCH_UNIQUE_VERSIONS = "SELECT DISTINCT V.NAME AS VERSION_NAME, V.DESCRIPTION AS DESCRIPTION FROM UMG_VERSION V WHERE TENANT_ID = ? ORDER BY V.NAME ";

    private static final String FETCH_VERSION_NAMES = "SELECT V.NAME AS VERSION_NAME FROM UMG_VERSION V WHERE LOWER(V.NAME) ";

    private static final String FETCH_VERSION_COUNT = "SELECT COUNT(V.NAME) AS VERSION_NAME_COUNT FROM UMG_VERSION V WHERE LOWER(V.NAME) = ? AND LOWER(V.TENANT_ID) = ? ";

    private static final String FETCH_ALL_TENANT_NAMES = "SELECT NAME, CODE FROM TENANT";

    private static final String FETCH_UNIQUE_MODEL_NAMES = "SELECT NAME, MAJOR_VERSION, MINOR_VERSION from UMG_VERSION WHERE TENANT_ID = ? ORDER BY NAME, MAJOR_VERSION, MINOR_VERSION";
    
    private static final String FETCH_UNIQUE_MODEL_NAMES_AND_ENV = "SELECT uv.NAME, uv.MAJOR_VERSION, uv.MINOR_VERSION,uv.MODEL_TYPE, ml.EXECUTION_LANGUAGE, ml.EXECUTION_ENVIRONMENT from UMG_VERSION uv JOIN MODEL_LIBRARY ml ON uv.MODEL_LIBRARY_ID = ml.ID and uv.TENANT_ID = ml.TENANT_ID  WHERE uv.TENANT_ID = ? ORDER BY NAME, MAJOR_VERSION, MINOR_VERSION";

    private static final String FETCH_UNIQUE_MODEL_LIBRARIES = "SELECT DISTINCT ML.NAME AS LIBRARY_NAME, ML.DESCRIPTION AS DESCRIPTION FROM MODEL_LIBRARY ML ORDER BY ML.NAME";

    private static final String FETCH_UNIQUE_MODEL_DEFNS = "SELECT DISTINCT M.NAME AS MODEL_DEFN_NAME, M.DESCRIPTION AS DESCRIPTION FROM MODEL M ORDER BY M.NAME";

    private static final String FETCH_EXISTING_VERSN_DTLS = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION AS MAJOR_VERSION,"
            + " V.MINOR_VERSION AS MINOR_VERSION, V.STATUS AS STATUS, V.CREATED_BY AS CREATED_BY,"
            + " V.CREATED_ON AS CREATED_DATE, ML.JAR_NAME AS JAR_NAME, ML.R_MANIFEST_FILE_NAME AS R_MANIFEST_FILE_NAME, V.MODEL_LIBRARY_ID AS MODEL_LIBRARY_ID,"
            + " M.IO_DEFINITION_NAME AS IO_DEFINITION_NAME, M.IO_DEF_EXCEL_NAME IO_DEF_EXCEL_NAME, M.ID AS MODEL_ID,ML.CHECKSUM_VALUE AS CHECK_SUM, V.MODEL_TYPE,ML.EXECUTION_ENVIRONMENT AS EXECENV, ML.MODEL_EXEC_ENV_NAME"
            + " FROM UMG_VERSION V, MODEL_LIBRARY ML, MODEL M, MAPPING MP"
            + " WHERE V.MODEL_LIBRARY_ID = ML.ID AND V.MAPPING_ID = MP.ID AND MP.MODEL_ID = M.ID AND ML.MODEL_EXEC_ENV_NAME LIKE ? AND V.TENANT_ID=?"
            + " ORDER BY V.CREATED_ON DESC LIMIT 100";
    
    private static final String FETCH_EXISTING_VERSN_DTLS_SPECIFIC = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION AS MAJOR_VERSION,"
            + " V.MINOR_VERSION AS MINOR_VERSION, V.STATUS AS STATUS, V.CREATED_BY AS CREATED_BY,"
            + " V.CREATED_ON AS CREATED_DATE, ML.JAR_NAME AS JAR_NAME, ML.R_MANIFEST_FILE_NAME AS R_MANIFEST_FILE_NAME, V.MODEL_LIBRARY_ID AS MODEL_LIBRARY_ID,"
            + " M.IO_DEFINITION_NAME AS IO_DEFINITION_NAME, M.IO_DEF_EXCEL_NAME IO_DEF_EXCEL_NAME, M.ID AS MODEL_ID,ML.CHECKSUM_VALUE AS CHECK_SUM, V.MODEL_TYPE, ML.EXECUTION_ENVIRONMENT AS EXECENV,ML.MODEL_EXEC_ENV_NAME"
            + " FROM UMG_VERSION V, MODEL_LIBRARY ML, MODEL M, MAPPING MP"
            + " WHERE V.MODEL_LIBRARY_ID = ML.ID AND V.MAPPING_ID = MP.ID AND MP.MODEL_ID = M.ID AND ML.MODEL_EXEC_ENV_NAME LIKE ? AND V.TENANT_ID=? AND V.MODEL_TYPE=?"
            + " ORDER BY V.CREATED_ON DESC LIMIT 100";

    private static final String SEARCH_LIBRARIES = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION AS MAJOR_VERSION,"
            + " V.MINOR_VERSION AS MINOR_VERSION, V.STATUS AS STATUS, V.CREATED_BY AS CREATED_BY,"
            + " V.CREATED_ON AS CREATED_DATE, ML.JAR_NAME AS JAR_NAME, ML.R_MANIFEST_FILE_NAME AS R_MANIFEST_FILE_NAME, V.MODEL_LIBRARY_ID AS MODEL_LIBRARY_ID, ML.CHECKSUM_VALUE AS CHECK_SUM, V.MODEL_TYPE AS MODEL_TYPE,ML.EXECUTION_ENVIRONMENT AS EXECENV"
            + " FROM UMG_VERSION V, MODEL_LIBRARY ML"
            + " WHERE V.MODEL_LIBRARY_ID = ML.ID AND UPPER(ML.EXECUTION_LANGUAGE) = ? AND V.TENANT_ID=? AND ";

    private static final String SEARCH_NEW_LIBRARIES = "SELECT * FROM MEDIATE_MODEL_LIBRARY M WHERE M.MODEL_EXEC_ENV_NAME = ? AND";

    private static final String SEARCH_IODEFNS = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION AS MAJOR_VERSION,"
            + " V.MINOR_VERSION AS MINOR_VERSION, V.STATUS AS STATUS, V.CREATED_BY AS CREATED_BY,"
            + " V.CREATED_ON AS CREATED_DATE,"
            + " M.IO_DEFINITION_NAME AS IO_DEFINITION_NAME,  M.IO_DEF_EXCEL_NAME IO_DEF_EXCEL_NAME,M.ID AS MODEL_ID, V.MODEL_TYPE AS MODEL_TYPE"
            + " FROM UMG_VERSION V, MODEL M, MAPPING MP, MODEL_LIBRARY ML"
            + " WHERE V.MAPPING_ID = MP.ID AND MP.MODEL_ID = M.ID AND V.MODEL_LIBRARY_ID = ML.ID AND UPPER(ML.EXECUTION_LANGUAGE) = ? AND V.TENANT_ID=? AND V.MODEL_TYPE=? AND ";

    private static final String FETCH_VERSION_NO_FOR_MODEL_LIBRARY = "SELECT V.MAJOR_VERSION AS MAJOR_VERSION,V.MINOR_VERSION AS MINOR_VERSION"
            + " FROM UMG_VERSION V WHERE V.MODEL_LIBRARY_ID = ? ORDER BY V.CREATED_ON DESC";

    private static final String GET_VERSION_DATA_FOR_TIDCOPY = "SELECT V.NAME AS VERSION_NAME,V.MAJOR_VERSION AS MAJOR_VERSION,V.MINOR_VERSION AS MINOR_VERSION,MAP.NAME AS TID_NAME,V.STATUS AS STATUS FROM UMG_VERSION V,MAPPING MAP,MODEL M WHERE V.MAPPING_ID=MAP.ID and MAP.MODEL_ID=M.ID and (V.STATUS='PUBLISHED' OR V.STATUS ='TESTED')";

    private static final String GET_EXEC_ENVT_VERSION = "SELECT me.ENVIRONMENT_VERSION AS ENVIRONMENT_VERSION, me.EXECUTION_ENVIRONMENT AS EXECUTION_LANGUAGE , ml.EXECUTION_ENVIRONMENT as EXECUTION_ENVIRONMENT FROM MODEL_LIBRARY ml "
            + "join UMG_VERSION uv ON ml.ID = uv.MODEL_LIBRARY_ID "
            + "join umg_admin.MODEL_EXECUTION_ENVIRONMENTS me ON ml.MODEL_EXEC_ENV_NAME = me.NAME "
            + "WHERE uv.NAME=? AND uv.MAJOR_VERSION=? AND uv.MINOR_VERSION=? AND uv.TENANT_ID=?";

    public static final String GET_SUPPORT_PACKAGES = "SELECT uv.NAME AS VERSION_NAME,uv.MAJOR_VERSION,uv.MINOR_VERSION, mep.PACKAGE_FOLDER, mep.PACKAGE_NAME, mep.PACKAGE_TYPE, mep.PACKAGE_VERSION, mep.COMPILED_OS, "
            + "mee.EXECUTION_ENVIRONMENT, mee.ENVIRONMENT_VERSION, maping.EXEC_SEQUENCE "
            + "FROM MODEL_LIBRARY ml join UMG_VERSION uv ON ml.ID = uv.MODEL_LIBRARY_ID "
            + "join MODEL_LIB_EXEC_PKG_MAPPING maping ON maping.MODEL_LIBRARY_ID = ml.ID "
            + "join umg_admin.MODEL_EXEC_PACKAGES mep ON mep.ID = maping.MODEL_EXEC_PKG_ID "
            + "join umg_admin.MODEL_EXECUTION_ENVIRONMENTS mee ON mep.MODEL_EXEC_ENV_NAME = mee.NAME "
            + "WHERE lower(uv.NAME)=? AND uv.MAJOR_VERSION=? AND uv.MINOR_VERSION=? AND lower(uv.TENANT_ID)=?";
    
    public static final String ALL_VERSION_DETAILS = "SELECT V.ID AS ID,V.NAME AS NAME ,V.DESCRIPTION AS DESCRIPTION,V.MAJOR_VERSION AS MAJOR_VERSION,V.MINOR_VERSION AS MINOR_VERSION,V.STATUS AS STATUS,V.LAST_UPDATED_BY LAST_UPDATED_BY,V.LAST_UPDATED_ON AS LAST_UPDATED_ON, M.NAME AS MAPPING_NAME,"+
    " ML.MODEL_EXEC_ENV_NAME AS MODEL_ENV_NAME, V.MODEL_TYPE AS MODEL_TYPE " 
            + "FROM UMG_VERSION V,MAPPING M,MODEL_LIBRARY ML WHERE V.TENANT_ID=? AND V.MAPPING_ID=M.ID AND ML.ID=V.MODEL_LIBRARY_ID";
    
    private static final String FETCH_EXISTING_MODEL_REPORT_DTLS = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION AS MAJOR_VERSION, V.MINOR_VERSION AS MINOR_VERSION,"+
 "V.STATUS AS STATUS, V.CREATED_BY AS CREATED_BY,V.CREATED_ON AS CREATED_DATE, V.MODEL_TYPE,MRT.TEMPLATE_FILE_NAME AS TEMPLATE_NAME,MRT.REPORT_TYPE,MRT.REPORT_ENGINE,MRT.ID AS REPORT_ID,MRT.TENANT_ID AS TENANT_ID,MRT.MAJOR_VERSION AS REPORT_VERSION,ML.MODEL_EXEC_ENV_NAME "
            + "FROM UMG_VERSION V, MODEL_LIBRARY ML, MODEL M, MAPPING MP,MODEL_REPORT_TEMPLATE MRT,MODEL_REPORT_STATUS MRS "
            + "WHERE V.MODEL_LIBRARY_ID = ML.ID AND V.MAPPING_ID = MP.ID AND MP.MODEL_ID = M.ID AND ML.MODEL_EXEC_ENV_NAME LIKE ? AND V.TENANT_ID = ? "
            + "AND MRT.UMG_VERSION_ID=V.ID AND MRT.IS_ACTIVE=1 AND  MRS.EXECUTION_STATUS='SUCCESS' "
            + "AND MRT.ID=MRS.REPORT_TEMPLATE_ID AND (V.STATUS='TESTED' OR V.STATUS='PUBLISHED') GROUP BY V.NAME,MRT.TEMPLATE_FILE_NAME,V.MAJOR_VERSION,V.MINOR_VERSION ORDER BY lower(V.CREATED_ON) DESC LIMIT 50";

    private static final String FETCH_EXISTING_MODEL_REPORT_SPEC = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION AS MAJOR_VERSION, V.MINOR_VERSION AS MINOR_VERSION,"
            + "V.STATUS AS STATUS, V.CREATED_BY AS CREATED_BY,V.CREATED_ON AS CREATED_DATE, V.MODEL_TYPE,MRT.TEMPLATE_FILE_NAME AS TEMPLATE_NAME,MRT.REPORT_TYPE,MRT.REPORT_ENGINE,MRT.ID AS REPORT_ID,MRT.TENANT_ID AS TENANT_ID,MRT.MAJOR_VERSION AS REPORT_VERSION,ML.MODEL_EXEC_ENV_NAME "
            + "FROM UMG_VERSION V, MODEL_LIBRARY ML, MODEL M, MAPPING MP,MODEL_REPORT_TEMPLATE MRT,MODEL_REPORT_STATUS MRS "
            + "WHERE V.MODEL_LIBRARY_ID = ML.ID AND V.MAPPING_ID = MP.ID AND MP.MODEL_ID = M.ID AND ML.MODEL_EXEC_ENV_NAME LIKE ? AND V.TENANT_ID = ? "
            + "AND MRT.UMG_VERSION_ID=V.ID AND MRT.IS_ACTIVE=1 AND  MRS.EXECUTION_STATUS='SUCCESS' "
            + "AND MRT.ID=MRS.REPORT_TEMPLATE_ID AND V.MODEL_TYPE=? AND (V.STATUS='TESTED' OR V.STATUS='PUBLISHED') GROUP BY V.NAME,MRT.TEMPLATE_FILE_NAME,V.MAJOR_VERSION,V.MINOR_VERSION ORDER BY lower(V.CREATED_ON) DESC LIMIT 50";

    private static final String SEARCH_EXISTING_MODEL_REPORT_DTLS = "SELECT V.NAME AS VERSION_NAME, V.MAJOR_VERSION AS MAJOR_VERSION, V.MINOR_VERSION AS MINOR_VERSION,"+
            "V.STATUS AS STATUS, V.CREATED_BY AS CREATED_BY,V.CREATED_ON AS CREATED_DATE, V.MODEL_TYPE,MRT.TEMPLATE_FILE_NAME AS TEMPLATE_NAME,MRT.REPORT_TYPE,MRT.REPORT_ENGINE,MRT.ID AS REPORT_ID,MRT.TENANT_ID AS TENANT_ID,MRT.MAJOR_VERSION AS REPORT_VERSION "+
                    "FROM UMG_VERSION V, MODEL_LIBRARY ML, MODEL M, MAPPING MP,MODEL_REPORT_TEMPLATE MRT,MODEL_REPORT_STATUS MRS "+
            "WHERE V.MODEL_LIBRARY_ID = ML.ID AND V.MAPPING_ID = MP.ID AND MP.MODEL_ID = M.ID AND ML.EXECUTION_LANGUAGE = ? AND V.TENANT_ID=? "+
 "AND MRT.UMG_VERSION_ID=V.ID AND MRT.IS_ACTIVE=1 AND  MRS.EXECUTION_STATUS='SUCCESS' AND MRT.ID=MRS.REPORT_TEMPLATE_ID AND (V.STATUS='TESTED' OR V.STATUS='PUBLISHED') AND";
    
    private  static final String GET_SUPPORT_PACKAGES_BY_MODEL_LIB= "SELECT * FROM MODEL_LIB_EXEC_PKG_MAPPING WHERE MODEL_LIBRARY_ID= ? AND TENANT_ID = ? ";
    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    @Inject
    private SystemParameterProvider parameterProvider;

    @Inject
    private CacheRegistry cacheRegistry;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Returns tenant code of all the tenants present in the system
     * 
     * @return
     */
    public List<String> getAllTenants() {
        return jdbcTemplate.queryForList(FETCH_ALL_TENANT, String.class);
    }

    /**
     * Returns version name and description defined in the system.
     * 
     * @return
     */
    public Map<String, String> getAllUniqueVersions() {
        Map<String, String> versionMap = new HashMap<String, String>();

        List<Map<String, Object>> listOfVersions = jdbcTemplate.queryForList(FETCH_UNIQUE_VERSIONS, RequestContext
                .getRequestContext().getTenantCode());

        if (CollectionUtils.isNotEmpty(listOfVersions)) {
            LOGGER.info("Found {} versions for tenant {}.", listOfVersions.size(), RequestContext.getRequestContext()
                    .getTenantCode());
            for (Map<String, Object> row : listOfVersions) {
                versionMap.put((String) row.get("VERSION_NAME"), (String) row.get("DESCRIPTION"));
            }
        }
        return versionMap;
    }

    /**
     * Returns model library name and description defined in the system.
     * 
     * @return
     */
    public Map<String, String> getAllUniqueLibraries() {
        Map<String, String> libraryMap = new HashMap<String, String>();
        List<Map<String, Object>> listOfLibraries = jdbcTemplate.queryForList(FETCH_UNIQUE_MODEL_LIBRARIES);
        if (CollectionUtils.isNotEmpty(listOfLibraries)) {
            LOGGER.info("Found {} libraries for tenant {}.", listOfLibraries.size(), RequestContext.getRequestContext()
                    .getTenantCode());
            for (Map<String, Object> row : listOfLibraries) {
                libraryMap.put((String) row.get("LIBRARY_NAME"), (String) row.get("DESCRIPTION"));
            }
        }
        return libraryMap;
    }

    /**
     * Returns model definition name and description defined in the system.
     * 
     * @return
     */
    public Map<String, String> getAllUniqueModelDefn() {
        Map<String, String> modelDefnMap = new HashMap<String, String>();
        List<Map<String, Object>> listOfModelDefns = jdbcTemplate.queryForList(FETCH_UNIQUE_MODEL_DEFNS);
        if (CollectionUtils.isNotEmpty(listOfModelDefns)) {
            LOGGER.info("Found {} model definitions for tenant {}.", listOfModelDefns.size(), RequestContext.getRequestContext()
                    .getTenantCode());
            for (Map<String, Object> row : listOfModelDefns) {
                modelDefnMap.put((String) row.get("MODEL_DEFN_NAME"), (String) row.get("DESCRIPTION"));
            }
        }
        return modelDefnMap;
    }

    /**
     * returns the version details using the query FETCH_EXISTING_VERSN_DTLS
     * 
     * @return
     */
    public List<Map<String, Object>> getExisitingVersionDetails(final String executionEnvironmentName, final ModelType modelType) {
        final String tenantCode = RequestContext.getRequestContext().getTenantCode();
        List<Map<String, Object>> result;
        if (modelType == ModelType.ALL) {
            result = jdbcTemplate.queryForList(FETCH_EXISTING_VERSN_DTLS, executionEnvironmentName.substring(0,executionEnvironmentName.indexOf("-") + 1) + "%", tenantCode);
        } else {
            result = jdbcTemplate.queryForList(FETCH_EXISTING_VERSN_DTLS_SPECIFIC, executionEnvironmentName.substring(0,executionEnvironmentName.indexOf("-") + 1) + "%", tenantCode,
                    modelType.getType());
        }

        return result;
    }

    /**
     * returns the version details by doing a 'like' search for search string in versionname, createdby, status and jarname
     * 
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<Map<String, Object>> searchLibraries(SearchOptions searchOptions, String executionLanguage)
            throws BusinessException, SystemException {
        String searchText = getFormatedString(searchOptions.getSearchText());
        String dataQuery = SEARCH_LIBRARIES + "( lower(ML.JAR_NAME) LIKE '" + getLikePattern(searchText) + "'"
                + buildQuery(searchOptions);
        LOGGER.info("Data Query for searchLibraries is :" + dataQuery);
        return jdbcTemplate.queryForList(dataQuery, executionLanguage.toUpperCase(),RequestContext.getRequestContext().getTenantCode());
    }

    /**
     * returns the version details by doing a 'like' search for search string in versionname, createdby, status and jarname
     * 
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<Map<String, Object>> searchNewLibraries(SearchOptions searchOptions, String environmentId)
            throws BusinessException, SystemException {
        String dataQuery = SEARCH_NEW_LIBRARIES + buildRestQuery(searchOptions);
        LOGGER.info("Data Query to search New Libraries is :" + dataQuery);
        return jdbcTemplate.queryForList(dataQuery, environmentId);
    }

    /**
     * returns the version details by doing a 'like' search for search string in versionname, createdby, status and IoDefnname
     * 
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<Map<String, Object>> searchIoDefns(SearchOptions searchOptions, String executionLanguage, final ModelType modelType)
            throws BusinessException, SystemException {
        String searchText = getFormatedString(searchOptions.getSearchText());
        String dataQuery = SEARCH_IODEFNS + "( lower(M.IO_DEFINITION_NAME) LIKE '" + getLikePattern(searchText)
                + "' OR lower(M.IO_DEF_EXCEL_NAME) LIKE '" + getLikePattern(searchText) + "'" + buildQuery(searchOptions);
        LOGGER.info("Data Query for searchIoDefns is :" + dataQuery);
        return jdbcTemplate.queryForList(dataQuery, executionLanguage.toUpperCase(),RequestContext.getRequestContext().getTenantCode(), modelType.getType());
    }

    public List<Map<String, Object>> getVersionNameList(String verName) throws BusinessException, SystemException {
        String dataQuery = FETCH_VERSION_NAMES + "LIKE '%" + StringUtils.lowerCase(verName) + "%'";
        LOGGER.info("Data Query for getVersionNameList is :" + dataQuery);
        return jdbcTemplate.queryForList(dataQuery);
    }

    private String buildRestQuery(SearchOptions searchOptions) throws BusinessException, SystemException {
        String searchText = getFormatedString(searchOptions.getSearchText());
        StringBuffer sq = new StringBuffer(" (LOWER(M.TAR_NAME) LIKE '");
        sq.append(getLikePattern(searchText)).append("' OR LOWER(M.CREATED_BY) LIKE '").append(getLikePattern(searchText))
                .append("')");
        String sortDirection = "ASC";
        if (searchOptions.isDescending()) {
            sortDirection = "DESC";
        }
        int noOfRecords = searchOptions.getPageSize();
        sq.append(" ORDER BY LOWER(M.CREATED_ON) ").append(sortDirection).append(" LIMIT ").append(noOfRecords);
        return sq.toString();
    }

    private String buildQuery(SearchOptions searchOptions) throws BusinessException, SystemException {
        String searchText = getFormatedString(searchOptions.getSearchText());
        StringBuffer sq = new StringBuffer(" OR lower(V.NAME) LIKE '");
        sq.append(getLikePattern(searchText)).append("' OR lower(V.STATUS) LIKE '").append(getLikePattern(searchText))
                .append("' OR lower(V.CREATED_BY) LIKE '").append(getLikePattern(searchText)).append("'");
        String sortDirection = "ASC";
        if (searchOptions.isDescending()) {
            sortDirection = "DESC";
        }
        int noOfRecords = searchOptions.getPageSize();
        sq.append(" ) ORDER BY lower(V.CREATED_ON) ").append(sortDirection).append(" LIMIT ").append(noOfRecords);
        return sq.toString();
    }
    private String buildReportQuery(SearchOptions searchOptions) throws BusinessException, SystemException {
        String searchText = getFormatedString(searchOptions.getSearchText());
        StringBuffer sq = new StringBuffer(" OR lower(V.NAME) LIKE '");
        sq.append(getLikePattern(searchText)).append("' OR lower(V.STATUS) LIKE '").append(getLikePattern(searchText))
                .append("' OR lower(V.CREATED_BY) LIKE '").append(getLikePattern(searchText)).append("'");
        String sortDirection = "ASC";
        if (searchOptions.isDescending()) {
            sortDirection = "DESC";
        }
        int noOfRecords = searchOptions.getPageSize();
        sq.append(" ) GROUP BY V.NAME,MRT.TEMPLATE_FILE_NAME,V.MAJOR_VERSION,V.MINOR_VERSION ORDER BY lower(V.CREATED_ON) ").append(sortDirection).append(" LIMIT ").append(noOfRecords);
        return sq.toString();
    }

    private String getFormatedString(final String value) {
        String formattedValue = value;
        if (StringUtils.isNotBlank(value)) {
            if (value.indexOf(SINGLE_QUOTE) > -1) {
                formattedValue = value.replaceAll(SINGLE_QUOTE_REXP, ESCAPE_FOR_SINGLE_QUOTE);
            }

            if (value.indexOf(PERCENTAGE) > -1) {
                formattedValue = value.replaceAll(PERCENTAGE_REXP, ESCAPE_FOR_PERCENTAGE);
            }

            if (value.indexOf(UNDER_SCORE) > -1) {
                formattedValue = value.replaceAll(UNDER_SCORE_REXP, ESCAPE_FOR_UNDER_SCORE);
            }
        }
        return StringUtils.lowerCase(formattedValue, getDefault());
    }

    /**
     * 
     * @return all versions which are tested and published
     * @throws BusinessException
     */
    public List<MappingsCopyInfo> getDataForTidCopy() throws BusinessException {
        LOGGER.debug("Data Query for getVersionForTidCopy is :" + GET_VERSION_DATA_FOR_TIDCOPY);
        List<MappingsCopyInfo> tidCopyInfos = null;
        try {
            tidCopyInfos = jdbcTemplate.query(GET_VERSION_DATA_FOR_TIDCOPY, new TidCopyRowMapper());
        } catch (EmptyResultDataAccessException dae) {
            tidCopyInfos = Collections.EMPTY_LIST;
        } catch (DataAccessException dae) {
            throw new BusinessException(BusinessExceptionCodes.BSE000133, new Object[] {});
        }
        return tidCopyInfos;
    }

    class TidCopyRowMapper implements ParameterizedRowMapper<MappingsCopyInfo> {

        @Override
        public MappingsCopyInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            MappingsCopyInfo tidCopyInfo = new MappingsCopyInfo();
            tidCopyInfo.setVersion(rs.getString("VERSION_NAME"));
            String versionNo = rs.getInt("MAJOR_VERSION") + "." + rs.getInt("MINOR_VERSION");
            tidCopyInfo.setVersionNo(versionNo);
            tidCopyInfo.setTidName(rs.getString("TID_NAME"));
            return tidCopyInfo;

        }

    }

    /**
     * gets the latest version number which has used the passed model library id
     * 
     * @param modelLibId
     * @return
     */
    public String getVersionNumberForModelLibId(String modelLibId) {
        String versionNumber = null;
        List<Map<String, Object>> listOfVersions = jdbcTemplate.queryForList(FETCH_VERSION_NO_FOR_MODEL_LIBRARY, modelLibId);

        if (CollectionUtils.isNotEmpty(listOfVersions)) {
            LOGGER.info("Found {} versions for tenant {}.", listOfVersions.size(), RequestContext.getRequestContext()
                    .getTenantCode());
            Map<String, Object> latestVersion = listOfVersions.get(0);
            versionNumber = latestVersion.get("MAJOR_VERSION") + BusinessConstants.DOT + latestVersion.get("MINOR_VERSION");
        }
        return versionNumber;
    }

    public VersionExecInfo getExecutionEnvtVersion(final String modelName, final Integer majorVersion,
            final Integer minorVersion) {
        List result = jdbcTemplate.queryForList(
                StringUtils.replace(GET_EXEC_ENVT_VERSION, BusinessConstants.UMG_ADMIN,
                        parameterProvider.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)), modelName, majorVersion, minorVersion,
                RequestContext.getRequestContext().getTenantCode());

        VersionExecInfo versionExecInfo = null;
        if (CollectionUtils.isNotEmpty(result)) {
            Map<String, String> excEnvMap = (Map<String, String>) result.get(0);
            versionExecInfo = new VersionExecInfo();
            versionExecInfo.setExecEnv(excEnvMap.get(SystemConstants.EXECUTION_ENVIRONMENT));
            versionExecInfo.setExecLanguage(StringUtils.upperCase(excEnvMap.get(SystemConstants.EXECUTION_LANGUAGE)));
            versionExecInfo.setExecLangVer(excEnvMap.get(SystemConstants.ENVIRONMENT_VERSION));        
        }
        return versionExecInfo;
    }

    public List<SupportPackage> getVersionSupportPackage(final String modelName, final Integer majorVersion,
            final Integer minorVersion) {
        final List<SupportPackage> supportPkgList = new ArrayList<SupportPackage>();

        List result = jdbcTemplate.queryForList(
                StringUtils.replace(GET_SUPPORT_PACKAGES, BusinessConstants.UMG_ADMIN,
                        parameterProvider.getParameter(SystemConstants.UMG_ADMIN_SCHEMA)), modelName, majorVersion, minorVersion,
                RequestContext.getRequestContext().getTenantCode());

        if (CollectionUtils.isNotEmpty(result)) {
            for (Object object : result) {
                Map<String, Object> rs = (Map<String, Object>) object;
                SupportPackage supportPkg = new SupportPackage();
                supportPkg.setVersionName((String) rs.get("VERSION_NAME"));
                supportPkg.setMajorVersion(String.valueOf(rs.get("MAJOR_VERSION")));
                supportPkg.setMinorVersion(String.valueOf(rs.get("MINOR_VERSION")));
                supportPkg.setPackageFolder((String) rs.get("PACKAGE_FOLDER"));
                supportPkg.setPackageName((String) rs.get("PACKAGE_NAME"));
                supportPkg.setPackageType((String) rs.get("PACKAGE_TYPE"));
                supportPkg.setPackageVersion((String) rs.get("PACKAGE_VERSION"));
                supportPkg.setCompiledOs((String) rs.get("COMPILED_OS"));
                supportPkg.setExecEnv((String) rs.get("EXECUTION_ENVIRONMENT"));
                supportPkg.setEnvVersion((String) rs.get("ENVIRONMENT_VERSION"));
                supportPkg.setHierarchy((Integer) rs.get("EXEC_SEQUENCE"));
                supportPkgList.add(supportPkg);
            }
        }
        return supportPkgList;

    }

    public long getVersionCountByName(String verName) throws SystemException {
        List<Map<String, Object>> dataQuery = jdbcTemplate.queryForList(FETCH_VERSION_COUNT, StringUtils.lowerCase(verName),
                RequestContext.getRequestContext().getTenantCode());
        LOGGER.info("Data Query for getVersionCountByName is :" + dataQuery);
        List<Map<String, Object>> result = dataQuery;
        Long count = new Long(0);
        if (CollectionUtils.isNotEmpty(result)) {
            count = (Long) result.get(0).get("VERSION_NAME_COUNT");
        }
        return count;
    }

    public Set<String> getAllModelVersions() {
        Set<String> modelNames = new TreeSet<String>();

        List<Map<String, Object>> listOfVersions = jdbcTemplate.queryForList(FETCH_UNIQUE_MODEL_NAMES, RequestContext
                .getRequestContext().getTenantCode());

        if (CollectionUtils.isNotEmpty(listOfVersions)) {
            LOGGER.info("Found {} models for tenant {}.", listOfVersions.size(), RequestContext.getRequestContext()
                    .getTenantCode());
            for (Map<String, Object> row : listOfVersions) {
                modelNames.add(PoolCriteriaUtil.getModelNameWithVersion((String) row.get("NAME"),
                        (Integer) row.get("MAJOR_VERSION"), (Integer) row.get("MINOR_VERSION")));
            }
        }

        return modelNames;
    }

    public Map<String, String> getTenantNames() {
        Map<String, TenantInfo> result = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        Map<String, String> tenantByCode = new HashMap<>();

        if (MapUtils.isNotEmpty(result)) {
            LOGGER.info("Found {} tenants.", result.size());
            for (Entry<String, TenantInfo> resultEntry : result.entrySet()) {
                tenantByCode.put(resultEntry.getKey(), resultEntry.getValue().getName());
            }
        }

        return tenantByCode;
    }
    
    /**
     * @return
     */
    public List<VersionInfo> getVersionDetails(SearchOptions searchOptions)throws BusinessException,SystemException {

        final List<VersionInfo> versionList = new ArrayList<VersionInfo>();

        StringBuffer query = new StringBuffer(ALL_VERSION_DETAILS);
        Long fromDate = null;
         Long toDate = null;
        String searchText = null;

        List<Object> params = new ArrayList<Object>();
        ;
        params.add(RequestContext.getRequestContext().getTenantCode());

        if (null != searchOptions) {
            if (searchOptions.getFromDate() != null && !searchOptions.getFromDate().isEmpty()) {
                 fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
                 query.append(" AND V.LAST_UPDATED_ON > ? ");
                 params.add(fromDate);
             }
             if (searchOptions.getToDate() != null && !searchOptions.getToDate().isEmpty()) {
                 toDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
                 query.append(" AND V.LAST_UPDATED_ON <= ? ");
                 params.add(toDate);
             }
             if(searchOptions.getSearchText()!= null){
                searchText = searchOptions.getSearchText();
                query.append(" AND( lower(V.NAME) LIKE '").append(getLikePattern(searchText))
                        .append("' OR lower(V.DESCRIPTION) LIKE '").append(getLikePattern(searchText));
                query.append("' OR lower(V.LAST_UPDATED_BY) LIKE '").append(getLikePattern(searchText));
                query.append("' OR lower(V.STATUS) LIKE '").append(getLikePattern(searchText)).append("')");
             }
             query.append(" ORDER BY V.LAST_UPDATED_ON DESC ");
        }

        LOGGER.info("Generated Query:", query.toString());

        List<Map<String, Object>> result = jdbcTemplate.queryForList(
                query.toString(),
                         params.toArray());

         if (CollectionUtils.isNotEmpty(result)) {
             for (Object object : result) {
                 Map<String, Object> rs = (Map<String, Object>) object;
                 VersionInfo version = new VersionInfo();
                 MappingInfo mapping = new MappingInfo();
                 ModelLibraryInfo modelLibInfo = new ModelLibraryInfo();
                 version.setId((String) rs.get("ID"));
                 version.setName((String) rs.get("NAME"));
                 version.setDescription((String) rs.get("DESCRIPTION"));
                 version.setMajorVersion((Integer) rs.get("MAJOR_VERSION"));
                 version.setMinorVersion((Integer) rs.get("MINOR_VERSION"));
                 version.setStatus((String) rs.get("STATUS"));
                 version.setLastModifiedDateTime(AdminUtil.getDateFormatMillisForEst((Long) rs.get("LAST_UPDATED_ON"), null));
                 version.setLastModifiedBy((String) rs.get("LAST_UPDATED_BY"));
                 version.setModelType((String) rs.get("MODEL_TYPE"));
                 modelLibInfo.setModelExecEnvName((String) rs.get("MODEL_ENV_NAME"));
                 version.setModelLibrary(modelLibInfo);                 
                 mapping.setName((String) rs.get("MAPPING_NAME"));
                 version.setMapping(mapping);
                 versionList.add(version);
                 
                 
             }
         }
         return versionList;
    }
    
    public Map<String, Map<String, Map<String, Set<String>>>> getAllModelVersionsWithEnv() {
        Map<String, Set<String>> modelsByType = new HashMap<String, Set<String>>();
        Map<String, Map<String, Set<String>>> modelsByExcEnv = new HashMap<String, Map<String, Set<String>>>();
        final Map<String, Map<String, Map<String, Set<String>>>> modelsByExcLan = new HashMap<String, Map<String, Map<String, Set<String>>>>();
        Set<String> modelNames = null;
          String modelNameAny = " " ;
        List<Map<String, Object>> listOfVersions = jdbcTemplate.queryForList(FETCH_UNIQUE_MODEL_NAMES_AND_ENV, RequestContext
                .getRequestContext().getTenantCode());

        if (CollectionUtils.isNotEmpty(listOfVersions)) {
            LOGGER.info("Found {} models for tenant {}.", listOfVersions.size(), RequestContext.getRequestContext().getTenantCode());
            for (Map<String, Object> row : listOfVersions) {

                final String modelName = PoolCriteriaUtil.getModelNameWithVersion((String) row.get("NAME"),
                        (Integer) row.get("MAJOR_VERSION"), (Integer) row.get("MINOR_VERSION"));
                final String modelNameAnyTest = (String) row.get("NAME");
                final String excEnv = (String) row.get("EXECUTION_ENVIRONMENT");
                final String excLng = (String) row.get("EXECUTION_LANGUAGE");
                final String modelType = (String) row.get("MODEL_TYPE");

                if (modelsByExcLan.containsKey(excLng)) {
                    modelsByExcEnv = modelsByExcLan.get(excLng);
                } else {
                    modelsByExcEnv = new HashMap<String, Map<String, Set<String>>>();
                }

                if (modelsByExcEnv.containsKey(excEnv)) {
                    modelsByType = modelsByExcLan.get(excLng).get(excEnv);
                } else {
                    modelsByType = new HashMap<String, Set<String>>();
                }

                if (modelsByType.containsKey(modelType)) {
                    modelNames = modelsByExcLan.get(excLng).get(excEnv).get(modelType);
                } else {
                    modelNames = new TreeSet<String>();
                }

                if (!(modelNameAny.equals(modelNameAnyTest))) {
                    modelNameAny = modelNameAnyTest;
                    modelNames.add(modelNameAnyTest + "_Any");
                }
                modelNames.add(modelName);
                modelsByType.put(modelType, modelNames);
                modelsByExcEnv.put(excEnv, modelsByType);
                modelsByExcLan.put(excLng, modelsByExcEnv);
            }
        }

        return modelsByExcLan;
    }
    /**
     * returns the model report details using the query FETCH_EXISTING_MODEL_REPORT_DTLS
     * 
     * @return
     */
    public List<Map<String, Object>> getExisitingReportDetails(final String executionEnvironmentName, final ModelType modelType) {
        final String tenantCode = RequestContext.getRequestContext().getTenantCode();
        List<Map<String, Object>> result;
        if (modelType == ModelType.ALL) {
            result = jdbcTemplate.queryForList(FETCH_EXISTING_MODEL_REPORT_DTLS,
                    executionEnvironmentName.substring(0, executionEnvironmentName.indexOf("-") + 1) + "%", tenantCode);
        } else {
            result = jdbcTemplate.queryForList(FETCH_EXISTING_MODEL_REPORT_SPEC,
                    executionEnvironmentName.substring(0, executionEnvironmentName.indexOf("-") + 1) + "%", tenantCode,
                    modelType.getType());
        }
        
        return result;
    }
    

    /**
     * returns the version details by doing a 'like' search for search string in versionname, createdby, status and jarname
     * 
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<Map<String, Object>> searchReports(SearchOptions searchOptions, String executionLanguage)
            throws BusinessException, SystemException {
        String searchText = getFormatedString(searchOptions.getSearchText());
        String dataQuery = SEARCH_EXISTING_MODEL_REPORT_DTLS + "( lower(MRT.TEMPLATE_FILE_NAME) LIKE '" + getLikePattern(searchText) + "'"
                + buildReportQuery(searchOptions);
        LOGGER.info("Data Query for searchLibraries is :" + dataQuery);
        return jdbcTemplate.queryForList(dataQuery, executionLanguage.toUpperCase(),RequestContext.getRequestContext().getTenantCode());
    }
    
    /**
     * returns the model report details using the query FETCH_EXISTING_MODEL_REPORT_DTLS
     * 
     * @return
     */
    public List<Map<String, Object>> getSupportPackages(final String modelLibraryId) {
        final String tenantCode = RequestContext.getRequestContext().getTenantCode();
        List<Map<String, Object>>  result = jdbcTemplate.queryForList(GET_SUPPORT_PACKAGES_BY_MODEL_LIB, modelLibraryId, tenantCode); 
        if(result!=null){
            LOGGER.error("Support packages are :" + result);
        }
        return result;
    }

}