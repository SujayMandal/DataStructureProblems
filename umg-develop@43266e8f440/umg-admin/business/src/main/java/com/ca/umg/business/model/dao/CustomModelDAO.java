/**
 * 
 */
package com.ca.umg.business.model.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.common.info.ResponseWrapper;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.util.AdminUtil;
import com.hazelcast.util.StringUtil;

/**
 * @author nigampra
 *
 */
@Repository
@SuppressWarnings("PMD")
public class CustomModelDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomModelDAO.class);
	
	private static final String UNIQUE_MODEL_SELECT_QUERY = "SELECT DISTINCT(M.NAME) AS MODEL_NAME from MODEL M WHERE M.NAME IN";
	private static final String UNIQUE_MODEL_COUNT_QUERY = "SELECT COUNT(DISTINCT(M.NAME)) AS MODEL_NAME from MODEL M WHERE M.NAME IN";
	private static final String CREATED_ON_COLUMN = "N.CREATED_ON ";

	@Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;
    
    /**
     * Initializes JDBC template with data source.
     */
    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public PageRecord<ModelInfo> getUniqueModelInfos(SearchOptions searchOptions) throws BusinessException, SystemException{
    	PageRecord<ModelInfo> pageRecord = new PageRecord<>();
    	List<ModelInfo> models = null;
    	String dataQuery = UNIQUE_MODEL_SELECT_QUERY + buildDataQuery(searchOptions);
    	LOGGER.info("Data Query is :" + dataQuery);
    	models = jdbcTemplate.query(dataQuery, new ModelInfoWrapper());
    	String dataCountQuery = UNIQUE_MODEL_COUNT_QUERY + buildDataCountQuery(searchOptions);
    	LOGGER.info("Data Count Query is :" + dataCountQuery);
    	Integer filteredRecords = jdbcTemplate.queryForObject(dataCountQuery, Integer.class);
    	pageRecord.setContent(models);
    	pageRecord.setTotalElements(filteredRecords);
    	
    	return pageRecord;
    }
    
    private String buildDataQuery(SearchOptions searchOptions) throws BusinessException, SystemException {
    	String searchText = searchOptions.getSearchText().toLowerCase();
    	StringBuffer sq = new StringBuffer();
    	sq.append(" ( SELECT N.NAME FROM MODEL N WHERE ( lower(N.NAME) LIKE '%").append(searchText).append("%' OR lower(N.UMG_NAME) LIKE '%").append(searchText).append("%' OR lower(N.DESCRIPTION) LIKE '%").append(searchText).append("%' OR lower(N.IO_DEFINITION_NAME) LIKE '%").append(searchText).append("%' OR lower(N.DOC_NAME) LIKE '%").append(searchText).append("%' OR lower(N.CREATED_BY) LIKE '%").append(searchText).append("%')");
    	Long fromDate = null;
		Long tillDate = null;
		String sortDirection = "ASC";
		if(searchOptions.isDescending()){
			sortDirection = "DESC";
		}
		int fromIndex = (searchOptions.getPage() - 1)*searchOptions.getPageSize();
		int toIndex = searchOptions.getPageSize();
		
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
		
		sq.append(" ) ORDER BY lower(MODEL_NAME) ").append(sortDirection).append(" LIMIT ").append(fromIndex).append(", ").append(toIndex);
    	return sq.toString();
    }
    
    private String buildDataCountQuery(SearchOptions searchOptions) throws BusinessException, SystemException {
    	String searchText = searchOptions.getSearchText().toLowerCase();
    	StringBuffer sq = new StringBuffer();
    	sq.append(" ( SELECT N.NAME FROM MODEL N WHERE ( lower(N.NAME) LIKE '%").append(searchText).append("%' OR lower(N.UMG_NAME) LIKE '%").append(searchText).append("%' OR lower(N.DESCRIPTION) LIKE '%").append(searchText).append("%' OR lower(N.IO_DEFINITION_NAME) LIKE '%").append(searchText).append("%' OR lower(N.DOC_NAME) LIKE '%").append(searchText).append("%' OR lower(N.CREATED_BY) LIKE '%").append(searchText).append("%')");
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
    
    public class ModelInfoWrapper implements ParameterizedRowMapper<ModelInfo> {
    	public ModelInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
    		ModelInfo modelInfo = new ModelInfo();
    		modelInfo.setName(rs.getString("MODEL_NAME"));
    		return modelInfo;
    	}
    }
    
    
    
    
    public ResponseWrapper<List<ModelInfo>> findAllModelForMapping(SearchOptions searchOptions) throws BusinessException, SystemException{
    	ResponseWrapper<List<ModelInfo>> pageRecord = new ResponseWrapper<List<ModelInfo>>();
    	List<ModelInfo> modelList = null;
    	StringBuffer whereCondition = new StringBuffer(buildQueryForModelMapping(searchOptions));
    	
    	whereCondition.append(" ORDER BY UPPER(map.name) ");
    	whereCondition.append(searchOptions.isDescending()?" DESC " : " ASC ");
    	
    	int fromIndex = searchOptions.getPage()==0? 0 : (searchOptions.getPage()-1)*searchOptions.getPageSize();
		int toIndex = searchOptions.getPageSize();
    	whereCondition.append(" LIMIT ").append(fromIndex).append(", ").append(toIndex);
    	
    	String dataQuery = "SELECT DISTINCT(m.name) AS MODEL_NAME FROM model m JOIN  mapping map ON m.id = map.model_id " + whereCondition;
    	
    	LOGGER.info("Data Query is :" + dataQuery);
    	modelList = jdbcTemplate.query(dataQuery, new ModelInfoWrapper());
    	String dataCountQuery = "SELECT COUNT(DISTINCT(m.name))AS total FROM model m JOIN  mapping map ON m.id =map.model_id " + buildQueryForModelMapping(searchOptions);
    	LOGGER.info("Data Count Query is :" + dataCountQuery);
    	Integer filteredRecords = jdbcTemplate.queryForObject(dataCountQuery, Integer.class);
    	pageRecord.setResponse(modelList);
    	PagingInfo pagingInfo=searchOptions;
    	pagingInfo.setTotalElements(new Long(filteredRecords));
    	pageRecord.setPagingInfo(pagingInfo);
    	return pageRecord;
    }
    
    
	private String buildQueryForModelMapping(SearchOptions searchOptions) throws BusinessException, SystemException{
		StringBuffer query = new StringBuffer(" WHERE 1=1 ");
		String searchText = searchOptions.getSearchText();
		// setting start and end date to Long format
		if(!StringUtil.isNullOrEmpty(searchOptions.getFromDate())){
			Long fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
			query.append("AND map.created_on >=" + fromDate);
		}
		if(!StringUtil.isNullOrEmpty(searchOptions.getToDate())){
			Long toDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
			query.append("AND map.created_on <=" + toDate);
		}
		
		if(!StringUtil.isNullOrEmpty(searchText)){
			query.append(" AND UPPER(map.name) like %" + searchText.toUpperCase() + "%");
			query.append(" OR UPPER(map.description) like %" + searchText.toUpperCase() + "%");
			query.append(" OR UPPER(map.status)=" + searchText.toUpperCase());
			query.append(" OR UPPER(map.created_by)=" + searchText.toUpperCase());
		}
		return query.toString();
	}
    
    
    

}
