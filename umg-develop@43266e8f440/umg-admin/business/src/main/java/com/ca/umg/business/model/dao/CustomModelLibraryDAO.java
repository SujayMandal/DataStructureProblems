/**
 * 
 */
package com.ca.umg.business.model.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.util.AdminUtil;

/**
 * @author nigampra
 *
 */
@Repository
@SuppressWarnings("PMD")
public class CustomModelLibraryDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomModelLibraryDAO.class);
	
	private static final String UNIQUE_LIBRARY_SELECT_QUERY = "SELECT DISTINCT(M.NAME) AS MODEL_LIBRARY_NAME from MODEL_LIBRARY M WHERE M.NAME IN";
	private static final String UNIQUE_LIBRARY_COUNT_QUERY = "SELECT COUNT(DISTINCT(M.NAME)) from MODEL_LIBRARY M WHERE M.NAME IN";
	private static final String CREATED_ON_COLUMN = "N.CREATED_ON ";
    private static final String R_MODEL_PACKAGES = "SELECT * FROM MODEL_LIBRARY M WHERE M.MODEL_EXEC_ENV_NAME = ? and M.ID NOT IN (SELECT DISTINCT(V.MODEL_LIBRARY_ID) FROM UMG_VERSION V) LIMIT 100";
    // private static final String UPDATE_MANIFEST_FILE = "UPDATE MODEL_LIBRARY M SET M.R_MANIFEST_FILE_NAME = ? WHERE ID = ?";


	@Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public PageRecord<ModelLibraryInfo> getUniqueModelLibraries(SearchOptions searchOptions) throws BusinessException, SystemException{
    	PageRecord<ModelLibraryInfo> pageRecord = new PageRecord<>();
    	List<ModelLibraryInfo> libraries = null;
    	String dataQuery = UNIQUE_LIBRARY_SELECT_QUERY + buildDataQuery(searchOptions);
    	LOGGER.info("Data Query is :" + dataQuery);
        libraries = jdbcTemplate.query(dataQuery, new ModelLibraryNameWrapper());
    	String dataCountQuery = UNIQUE_LIBRARY_COUNT_QUERY + buildDataCountQuery(searchOptions);
    	LOGGER.info("Data Count Query is :" + dataCountQuery);
    	Integer filteredRecords = jdbcTemplate.queryForObject(dataCountQuery, Integer.class);
    	pageRecord.setContent(libraries);
    	pageRecord.setTotalElements(filteredRecords);
    	return pageRecord;
    }
    
    private String buildDataQuery(SearchOptions searchOptions) throws BusinessException, SystemException {
    	String searchText = searchOptions.getSearchText().toLowerCase();
    	StringBuffer sq = new StringBuffer();
    	sq.append(" ( SELECT N.NAME FROM MODEL_LIBRARY N WHERE ( lower(N.NAME) LIKE '%").append(searchText).append("%' OR lower(N.UMG_NAME) LIKE '%").append(searchText).append("%' OR lower(N.DESCRIPTION) LIKE '%").append(searchText).append("%' OR lower(N.EXECUTION_TYPE) LIKE '%").append(searchText).append("%' OR lower(N.EXECUTION_LANGUAGE) LIKE '%").append(searchText).append("%' OR lower(N.JAR_NAME) LIKE '%").append(searchText).append("%' OR lower(N.CREATED_BY) LIKE '%").append(searchText).append("%')");
    	Long fromDate = null;
		Long tillDate = null;
		String sortDirection = "ASC";
		if(searchOptions.isDescending()){
			sortDirection = "DESC";
		}
		int fromIndex = (searchOptions.getPage() - 1)*searchOptions.getPageSize();
		int records = searchOptions.getPageSize();
		
		if (searchOptions.getFromDate() != null && !searchOptions.getFromDate().isEmpty()) {
			fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(),
					BusinessConstants.LIST_SEARCH_DATE_FORMAT);
			sq.append(' ').append(BusinessConstants.AND).append(' ').append(CREATED_ON_COLUMN).append(BusinessConstants.GREATER_THAN_EQUAL_TO).append(' ').append(fromDate);
		}
		if (searchOptions.getToDate() != null && !searchOptions.getToDate().isEmpty()) {
			tillDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(),
					BusinessConstants.LIST_SEARCH_DATE_FORMAT);
			sq.append(' ').append(BusinessConstants.AND).append(' ').append(CREATED_ON_COLUMN).append(BusinessConstants.LESS_THAN_EQUAL_TO).append(' ').append(tillDate);
		}
		
		sq.append(" ) ORDER BY lower(MODEL_LIBRARY_NAME) ").append(sortDirection).append(" LIMIT ").append(fromIndex).append(", ").append(records);
    	return sq.toString();
    }
    
    private String buildDataCountQuery(SearchOptions searchOptions) throws BusinessException, SystemException {
    	String searchText = searchOptions.getSearchText().toLowerCase();
    	StringBuffer sq = new StringBuffer();
    	sq.append(" ( SELECT N.NAME FROM MODEL_LIBRARY N WHERE ( lower(N.NAME) LIKE '%").append(searchText).append("%' OR lower(N.UMG_NAME) LIKE '%").append(searchText).append("%' OR lower(N.DESCRIPTION) LIKE '%").append(searchText).append("%' OR lower(N.EXECUTION_TYPE) LIKE '%").append(searchText).append("%' OR lower(N.EXECUTION_LANGUAGE) LIKE '%").append(searchText).append("%' OR lower(N.JAR_NAME) LIKE '%").append(searchText).append("%' OR lower(N.CREATED_BY) LIKE '%").append(searchText).append("%')");
    	Long fromDate = null;
		Long tillDate = null;
		
		if (searchOptions.getFromDate() != null && !searchOptions.getFromDate().isEmpty()) {
			fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(),
					BusinessConstants.LIST_SEARCH_DATE_FORMAT);
			sq.append(' ').append(BusinessConstants.AND).append(' ').append(CREATED_ON_COLUMN).append(BusinessConstants.GREATER_THAN_EQUAL_TO).append(' ').append(fromDate);
		}
		if (searchOptions.getToDate() != null && !searchOptions.getToDate().isEmpty()) {
			tillDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(),
					BusinessConstants.LIST_SEARCH_DATE_FORMAT);
			sq.append(' ').append(BusinessConstants.AND).append(' ').append(CREATED_ON_COLUMN).append(BusinessConstants.LESS_THAN_EQUAL_TO).append(' ').append(tillDate);
		}
		sq.append(" )");
    	return sq.toString();
    }
    
    public class ModelLibraryNameWrapper implements ParameterizedRowMapper<ModelLibraryInfo> {
    	public ModelLibraryInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
    		ModelLibraryInfo libraryInfo = new ModelLibraryInfo();
    		libraryInfo.setName(rs.getString("MODEL_LIBRARY_NAME"));
    		return libraryInfo;
    	}
    }

    public List<ModelLibraryInfo> getNewModelPackages(String modelExecEnvName) throws BusinessException, SystemException {
        List<ModelLibraryInfo> modelLibrtaries = null;
        LOGGER.debug("query to get R newly added model languages is :" + R_MODEL_PACKAGES);
        try {
            modelLibrtaries = jdbcTemplate.query(R_MODEL_PACKAGES, new ModelLibraryRowWrapper(), modelExecEnvName);
        } catch (EmptyResultDataAccessException dae) {
            modelLibrtaries = Collections.EMPTY_LIST;
        } catch (DataAccessException dae) {
            throw new BusinessException(BusinessExceptionCodes.BSE000133, new Object[] {});
        }
        return modelLibrtaries;
    }

    public class ModelLibraryRowWrapper implements ParameterizedRowMapper<ModelLibraryInfo> {
        public ModelLibraryInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ModelLibraryInfo libraryInfo = new ModelLibraryInfo();
            libraryInfo.setName(rs.getString("NAME"));
            libraryInfo.setId(rs.getString("ID"));
            libraryInfo.setUmgName(rs.getString("UMG_NAME"));
            libraryInfo.setCreatedBy(rs.getString("CREATED_BY"));
            libraryInfo.setCreatedDate(new DateTime(rs.getLong("CREATED_ON")));
            libraryInfo.setLastModifiedBy(rs.getString("LAST_UPDATED_BY"));
            Long lastUpdatedDate = rs.getLong("LAST_UPDATED_ON");
            libraryInfo.setJarName(rs.getString("JAR_NAME"));
            if (lastUpdatedDate != null) {
                libraryInfo.setLastModifiedDate(new DateTime(lastUpdatedDate));
            }
            return libraryInfo;
        }
    }

    /*
     * public boolean updateManifestFile(String modelLibId, String manifestFileName) throws BusinessException { Boolean
     * isManifestFielUpdated = Boolean.TRUE; LOGGER.debug("query to get R newly added model languages is :" + R_MODEL_PACKAGES);
     * try { int i = jdbcTemplate.update(UPDATE_MANIFEST_FILE, new Object[] { manifestFileName, modelLibId }); if (i <= 0) {
     * isManifestFielUpdated = Boolean.FALSE; }
     * 
     * } catch (DataAccessException dae) { LOGGER.error("error while updating manifest file into the model library with id :" +
     * modelLibId + ". Exception is :" + dae.getMessage()); throw new BusinessException(BusinessExceptionCodes.BSE000133, new
     * Object[] {}); } return isManifestFielUpdated; }
     */
}
