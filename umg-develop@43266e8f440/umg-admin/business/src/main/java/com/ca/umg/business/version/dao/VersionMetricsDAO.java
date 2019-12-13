package com.ca.umg.business.version.dao;

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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionMetricResponseInfo;

@Repository
public class VersionMetricsDAO {

	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private static final String API = "api";
/*	private static final String TEST = "testBed";
*/	private static final String TOTAL_TRANSACTIONS = "totalTransactions";
	private static final String METRICS_INFO = "metricsInfo";
	private static final String END_TO_END = "End To End UMG Time";
	private static final String MODEL = "Model Execution Time";
	private static final String MODELET = "Modelet Execution Time";
	private static final String ME2 = "Modelet Wait Time";
	private static final String RUNTIME = "Run Time";
	private static final String IS_TEST = "IS_TEST";
	
	/*private static final String AND = " AND ";
	private static final String RESULT_ALIAS = " ) RESULT";
	private static final String AS_90PERC_FROM = " AS 90PERC FROM (";
	private static final String MANDATORY_CLAUSE = "VERSION_NAME = :VERSION_NAME AND STATUS = 'Success' AND IS_TEST = :IS_TEST";
	private static final String ADD_VERSION_SPECIFIC_CLAUSE = "MAJOR_VERSION = :MAJOR_VERSION AND MINOR_VERSION = :MINOR_VERSION AND TENANT_ID= :TENANT_ID";*/
	private static final String SET_GRP_CONCAT_SIZE = "SET SESSION group_concat_max_len = 1000000";
	
	/*private static final String GET_TIMES_FOR_VERSION = "SELECT RUNTIME_CALL_END - RUNTIME_CALL_START AS FULLCYCLETIME, "
			+ "MODEL_EXECUTION_TIME AS MODEL, MODELET_EXECUTION_TIME - MODEL_EXECUTION_TIME AS MODELET, ME2_WAITING_TIME AS ME2, "
			+ "((RUNTIME_CALL_END - RUNTIME_CALL_START) - MODELET_EXECUTION_TIME - ME2_WAITING_TIME) AS RUNTIME FROM UMG_RUNTIME_TRANSACTION WHERE "
			+ MANDATORY_CLAUSE;*/
	
	/*private static final String GET_MIN_MAX_AVG_FOR_VERSION = "SELECT MIN(FULLCYCLETIME) AS FULLCYC_MIN, MAX(FULLCYCLETIME) AS FULLCYC_MAX, "
			+ "ROUND(AVG(FULLCYCLETIME)) AS FULLCYC_AVG,   MIN(MODEL) AS MODEL_MIN, MAX(MODEL) AS MODEL_MAX, ROUND(AVG(MODEL)) AS MODEL_AVG,   "
			+ "MIN(MODELET) AS MODELET_MIN, MAX(MODELET) AS MODELET_MAX, ROUND(AVG(MODELET)) AS MODELET_AVG, MIN(ME2) AS ME2_MIN, MAX(ME2) AS ME2_MAX, "
			+ "ROUND(AVG(ME2)) AS ME2_AVG, MIN(RUNTIME) AS RUNTIME_MIN, MAX(RUNTIME) AS RUNTIME_MAX, ROUND(AVG(RUNTIME)) AS RUNTIME_AVG FROM "
			+ "(" + GET_TIMES_FOR_VERSION + " AND " + ADD_VERSION_SPECIFIC_CLAUSE + ") UMGRUNTIME";*/
	
	/*private static final String GET_TXN_COUNT_FOR_VERSION = "SELECT COUNT(*) AS TXN_COUNT FROM UMG_RUNTIME_TRANSACTION WHERE " + MANDATORY_CLAUSE + " AND " + ADD_VERSION_SPECIFIC_CLAUSE;
	
	private static final String ORDER_BY_MODEL = " ORDER BY MODEL ";
	private static final String ORDER_BY_MODELET = " ORDER BY MODELET ";
	private static final String ORDER_BY_RUNTIME = " ORDER BY RUNTIME ";
	private static final String ORDER_BY_ME2 = " ORDER BY ME2 ";
	private static final String ORDER_BY_FULLCYCLE = " ORDER BY FULLCYCLE ";*/
	
	private static final String GET_VERSION_METRIC_QUERY = 	"SELECT count(*) AS TXN_COUNT,MIN(RUNTIME_CALL_END - RUNTIME_CALL_START) AS FULLCYC_MIN,"+ 
		    " MAX(RUNTIME_CALL_END - RUNTIME_CALL_START) AS FULLCYC_MAX,ROUND(AVG(RUNTIME_CALL_END - RUNTIME_CALL_START)) AS FULLCYC_AVG,MIN(MODEL_EXECUTION_TIME) AS MODEL_MIN,MAX(MODEL_EXECUTION_TIME) AS MODEL_MAX,"+ 
			" ROUND(AVG(MODEL_EXECUTION_TIME)) AS MODEL_AVG,MIN(MODELET_EXECUTION_TIME - MODEL_EXECUTION_TIME) AS MODELET_MIN,MAX(MODELET_EXECUTION_TIME - MODEL_EXECUTION_TIME) AS MODELET_MAX,ROUND(AVG(MODELET_EXECUTION_TIME - MODEL_EXECUTION_TIME)) AS MODELET_AVG,MIN(ME2_WAITING_TIME) AS ME2_MIN,"+ 
			" MAX(ME2_WAITING_TIME) AS ME2_MAX,ROUND(AVG(ME2_WAITING_TIME)) AS ME2_AVG,MIN(((RUNTIME_CALL_END - RUNTIME_CALL_START) - MODELET_EXECUTION_TIME - ME2_WAITING_TIME)) AS RUNTIME_MIN,"+ 
			" MAX(((RUNTIME_CALL_END - RUNTIME_CALL_START) - MODELET_EXECUTION_TIME - ME2_WAITING_TIME)) AS RUNTIME_MAX,ROUND(AVG(((RUNTIME_CALL_END - RUNTIME_CALL_START) - MODELET_EXECUTION_TIME - ME2_WAITING_TIME))) AS RUNTIME_AVG,"+
			" SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(MODEL_EXECUTION_TIME ORDER BY MODEL_EXECUTION_TIME ASC), ',', 90/100 * COUNT(*)), ',', -1) AS MODEL,"+
			" SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(((RUNTIME_CALL_END - RUNTIME_CALL_START) - MODELET_EXECUTION_TIME - ME2_WAITING_TIME) ORDER BY ((RUNTIME_CALL_END - RUNTIME_CALL_START) - MODELET_EXECUTION_TIME - ME2_WAITING_TIME) ASC), ',', 90/100 * COUNT(*)), ',', -1) AS RUNTIME,"+
			" SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT((RUNTIME_CALL_END - RUNTIME_CALL_START) ORDER BY (RUNTIME_CALL_END - RUNTIME_CALL_START) ASC), ',', 90/100 * COUNT(*)), ',', -1) AS FULLCYCLE,"+
			" SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT((MODELET_EXECUTION_TIME - MODEL_EXECUTION_TIME) ORDER BY (MODELET_EXECUTION_TIME - MODEL_EXECUTION_TIME) ASC), ',', 90/100 * COUNT(*)), ',', -1) AS MODELET,"+
			" SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(ME2_WAITING_TIME ORDER BY ME2_WAITING_TIME ASC), ',', 90/100 * COUNT(*)), ',', -1) AS ME2"+
			" FROM (SELECT RUNTIME_CALL_END,RUNTIME_CALL_START,MODEL_EXECUTION_TIME,MODELET_EXECUTION_TIME,ME2_WAITING_TIME"+ 
			" FROM UMG_RUNTIME_TRANSACTION WHERE VERSION_NAME = :VERSION_NAME AND STATUS = 'Success' AND IS_TEST = :IS_TEST AND MAJOR_VERSION = :MAJOR_VERSION AND MINOR_VERSION = :MINOR_VERSION AND TENANT_ID = :TENANT_ID ) RESULT";
	
	
	/*private static final String GET_MODEL_TIME = "SELECT MODEL_EXECUTION_TIME AS MODEL FROM UMG_RUNTIME_TRANSACTION WHERE " + MANDATORY_CLAUSE;
	
	private static final String GET_MODELET_TIME = "SELECT MODELET_EXECUTION_TIME - MODEL_EXECUTION_TIME AS MODELET FROM UMG_RUNTIME_TRANSACTION WHERE "
			+ MANDATORY_CLAUSE;
	
	private static final String GET_RUNTIME_TIME = "SELECT ((RUNTIME_CALL_END - RUNTIME_CALL_START) - MODELET_EXECUTION_TIME - ME2_WAITING_TIME) AS RUNTIME"
			+ " FROM UMG_RUNTIME_TRANSACTION WHERE " + MANDATORY_CLAUSE;
	
	private static final String GET_ME2_TIME = "SELECT ME2_WAITING_TIME AS ME2 FROM UMG_RUNTIME_TRANSACTION WHERE " + MANDATORY_CLAUSE;
	
	private static final String GET_FULLCYCLE_TIME = "SELECT RUNTIME_CALL_END - RUNTIME_CALL_START AS FULLCYCLE FROM UMG_RUNTIME_TRANSACTION WHERE "
			+ MANDATORY_CLAUSE;*/
	
	/*private static final String VERSION_SPECIFIC_ORDERED_MODEL_TIME = GET_MODEL_TIME + AND + ADD_VERSION_SPECIFIC_CLAUSE + ORDER_BY_MODEL; //NOPMD
	
	private static final String VERSION_SPECIFIC_ORDERED_ME2_TIME = GET_ME2_TIME + AND + ADD_VERSION_SPECIFIC_CLAUSE + ORDER_BY_ME2; //NOPMD
	
	private static final String VERSION_SPECIFIC_ORDERED_MODELET_TIME = GET_MODELET_TIME + AND + ADD_VERSION_SPECIFIC_CLAUSE + ORDER_BY_MODELET; //NOPMD
	
	private static final String VERSION_SPECIFIC_ORDERED_RUNTIME_TIME = GET_RUNTIME_TIME + AND + ADD_VERSION_SPECIFIC_CLAUSE + ORDER_BY_RUNTIME; //NOPMD
	
	private static final String VERSION_SPECIFIC_ORDERED_FULLCYCLE_TIME = GET_FULLCYCLE_TIME + AND + ADD_VERSION_SPECIFIC_CLAUSE + ORDER_BY_FULLCYCLE; //NOPMD
*/	
	/*private static final String GET_90PERC_MODEL = "SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(MODEL SEPARATOR ','), ',', 90/100 * COUNT(*)), ',', -1)"
			+ AS_90PERC_FROM + VERSION_SPECIFIC_ORDERED_MODEL_TIME + RESULT_ALIAS;
	
	private static final String GET_90PERC_MODELET = "SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(MODELET SEPARATOR ','), ',', 90/100 * COUNT(*)), ',', -1)"
			+ AS_90PERC_FROM + VERSION_SPECIFIC_ORDERED_MODELET_TIME + RESULT_ALIAS;
	
	private static final String GET_90PERC_ME2 = "SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(ME2 SEPARATOR ','), ',', 90/100 * COUNT(*)), ',', -1)"
			+ AS_90PERC_FROM + VERSION_SPECIFIC_ORDERED_ME2_TIME + RESULT_ALIAS;
	
	private static final String GET_90PERC_RUNTIME = "SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(RUNTIME SEPARATOR ','), ',', 90/100 * COUNT(*)), ',', -1)"
			+ AS_90PERC_FROM + VERSION_SPECIFIC_ORDERED_RUNTIME_TIME + RESULT_ALIAS;
	
	private static final String GET_90PERC_FULLCYCLE = "SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(FULLCYCLE SEPARATOR ','), ',', 90/100 * COUNT(*)), ',', -1)"
			+ AS_90PERC_FROM + VERSION_SPECIFIC_ORDERED_FULLCYCLE_TIME + RESULT_ALIAS;*/
	
	/*
	 * Added for future extension for model level.
	 * 
	private static final String ADD_DATE_RANGE_CLAUSE = "CREATED_ON BETWEEN :FROMDATE AND :TODATE";
	
	private static final String GET_ORDERED_MODEL_TIME = GET_MODEL_TIME + ORDER_BY_MODEL;
	
	private static final String GET_ORDERED_ME2_TIME = GET_ME2_TIME + ORDER_BY_ME2;
	
	private static final String GET_ORDERED_MODELET_TIME = GET_MODELET_TIME + ORDER_BY_MODELET;
	
	private static final String GET_ORDERED_RUNTIME_TIME = GET_RUNTIME_TIME + ORDER_BY_RUNTIME;
	
	private static final String GET_ORDERED_FULLCYCLE_TIME = GET_FULLCYCLE_TIME + ORDER_BY_FULLCYCLE;
	
		private static final String GET_MIN_MAX_AVG_FOR_MODEL = "SELECT MIN(FULLCYCLETIME) AS FULLCYC_MIN, MAX(FULLCYCLETIME) AS FULLCYC_MAX, "
			+ "AVG(FULLCYCLETIME) AS FULLCYC_AVG,   MIN(MODEL) AS MODEL_MIN, MAX(MODEL) AS MODEL_MAX, AVG(MODEL) AS MODEL_AVG, "
			+ "MIN(MODELET) AS MODELET_MIN, MAX(MODELET) AS MODELET_MAX, AVG(MODELET) AS MODELET_AVG,   MIN(ME2) AS ME2_MIN, MAX(ME2) AS ME2_MAX, "
			+ "AVG(ME2) AS ME2_AVG, MIN(RUNTIME) AS RUNTIME_MIN, MAX(RUNTIME) AS RUNTIME_MAX, AVG(RUNTIME) AS RUNTIME_AVG,  TOTALTXN AS TOTALTXN FROM "
			+ "(" + GET_TIMES_FOR_VERSION +" ) UMGRUNTIME";
			
	
	private static final String GET_MIN_MAX_AVG_FOR_VERSION_WITH_RANGE = "SELECT MIN(FULLCYCLETIME) AS FULLCYC_MIN, MAX(FULLCYCLETIME) AS FULLCYC_MAX, "
			+ "AVG(FULLCYCLETIME) AS FULLCYC_AVG,   MIN(MODEL) AS MODEL_MIN, MAX(MODEL) AS MODEL_MAX, AVG(MODEL) AS MODEL_AVG,   "
			+ "MIN(MODELET) AS MODELET_MIN, MAX(MODELET) AS MODELET_MAX, AVG(MODELET) AS MODELET_AVG, MIN(ME2) AS ME2_MIN, MAX(ME2) AS ME2_MAX, "
			+ "AVG(ME2) AS ME2_AVG, MIN(RUNTIME) AS RUNTIME_MIN, MAX(RUNTIME) AS RUNTIME_MAX, AVG(RUNTIME) AS RUNTIME_AVG, TOTALTXN AS TOTALTXN FROM "
			+ "(" + GET_TIMES_FOR_VERSION + " AND " + ADD_VERSION_SPECIFIC_CLAUSE + " AND " + ADD_DATE_RANGE_CLAUSE + ") UMGRUNTIME";			
	 
	 */

	/**
	 * Initializes JDBC template with data source.
	 */
	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public Map<String, Object> getVersionMetricsDetails(VersionMetricRequestInfo verReqInfo) throws BusinessException, SystemException {
		Map<String, Object> versionMetricsMap = new HashMap<String, Object>();
		Map<String, Object> apiMetricsMap = null;
		Map params = populateParams(verReqInfo);
		try {
			jdbcTemplate.execute(SET_GRP_CONCAT_SIZE, new VersionPreparedStatementCallback());
			if(StringUtils.equalsIgnoreCase(verReqInfo.getIsTest().toString(), "0")) {
				apiMetricsMap = populateVersionMetrics(params);
			}else {
				apiMetricsMap = populateVersionMetrics(params);
			}
		} catch (Exception excp) { //NOPMD
			BusinessException.newBusinessException(BusinessExceptionCodes.BSE000132, new Object[]{}, excp);
		}
		versionMetricsMap.put(API, apiMetricsMap);
		return versionMetricsMap;
	}

	private Map<String, Object> populateVersionMetrics(Map params) {
		Map<String, Object> metricsMap = jdbcTemplate.query(GET_VERSION_METRIC_QUERY, params, new VersionMetricResponseMapper());
		/*metricsMap.put(TOTAL_TRANSACTIONS, jdbcTemplate.queryForInt(GET_TXN_COUNT_FOR_VERSION, params));
		Integer model90Perc = jdbcTemplate.query(GET_90PERC_MODEL, params, new PercentileMapper());
		Integer modelet90Perc = jdbcTemplate.query(GET_90PERC_MODELET, params, new PercentileMapper());
		Integer me290Perc = jdbcTemplate.query(GET_90PERC_ME2, params, new PercentileMapper());
		Integer rt90Perc = jdbcTemplate.query(GET_90PERC_RUNTIME, params, new PercentileMapper());
		Integer fullCycle90Perc = jdbcTemplate.query(GET_90PERC_FULLCYCLE, params, new PercentileMapper());
		populate90Perc(model90Perc, modelet90Perc, me290Perc, rt90Perc, fullCycle90Perc, metricsMap);*/
		return metricsMap;
	}

	private Map populateParams(VersionMetricRequestInfo verReqInfo) {
		Map params = new HashMap();
		params.put("VERSION_NAME", verReqInfo.getVersionName());
		params.put("MAJOR_VERSION", verReqInfo.getMajorVersion());
		params.put("MINOR_VERSION", verReqInfo.getMinorVersion());
		params.put("TENANT_ID", RequestContext.getRequestContext().getTenantCode());
		params.put(IS_TEST, verReqInfo.getIsTest());

		return params;
	}
	
	/*private void populate90Perc(Integer model90Perc, Integer modelet90Perc, Integer me290Perc, Integer rt90Perc, 
			Integer fullCycle90Perc, Map<String, Object> metricsMap) {
		List metricsRespInfoList = (List) metricsMap.get(METRICS_INFO);
		for (Object object : metricsRespInfoList) {
			VersionMetricResponseInfo metricInfo = (VersionMetricResponseInfo) object;
			if (metricInfo.getStage().equalsIgnoreCase(RUNTIME)) {
				metricInfo.setPercentileTransaction(rt90Perc);
			} else if (metricInfo.getStage().equalsIgnoreCase(MODEL)) {
				metricInfo.setPercentileTransaction(model90Perc);
			} else if (metricInfo.getStage().equalsIgnoreCase(MODELET)) {
				metricInfo.setPercentileTransaction(modelet90Perc);
			} else if (metricInfo.getStage().equalsIgnoreCase(ME2)) {
				metricInfo.setPercentileTransaction(me290Perc);
			} else if (metricInfo.getStage().equalsIgnoreCase(END_TO_END)) {
				metricInfo.setPercentileTransaction(fullCycle90Perc);
			}
		}
	}*/
	
	class VersionPreparedStatementCallback implements PreparedStatementCallback {
		  public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
		      return preparedStatement.executeQuery();
		  }
	}

	class PercentileMapper implements ResultSetExtractor<Integer> {
		@Override
		public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
			rs.next();
			return rs.getInt("90PERC");
		}
	}
	
	class VersionMetricResponseMapper implements ResultSetExtractor<Map<String, Object>> {

		@Override
		public Map<String, Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
			rs.next();
			Map<String, Object> metricsMap = new HashMap<String, Object>();
			List<VersionMetricResponseInfo> responseList = new ArrayList();
			VersionMetricResponseInfo metricResponse = new VersionMetricResponseInfo();
			metricResponse.setStage(RUNTIME);
			metricResponse.setMaxTime(rs.getInt("RUNTIME_MAX"));
			metricResponse.setMinTime(rs.getInt("RUNTIME_MIN"));
			metricResponse.setMeanTime(rs.getInt("RUNTIME_AVG"));
			metricResponse.setPercentileTransaction(rs.getInt("RUNTIME"));
			responseList.add(metricResponse);
			metricResponse = new VersionMetricResponseInfo();
			metricResponse.setStage(ME2);
			metricResponse.setMaxTime(rs.getInt("ME2_MAX"));
			metricResponse.setMinTime(rs.getInt("ME2_MIN"));
			metricResponse.setMeanTime(rs.getInt("ME2_AVG"));
			metricResponse.setPercentileTransaction(rs.getInt("ME2"));
			responseList.add(metricResponse);
			metricResponse = new VersionMetricResponseInfo();
			metricResponse.setStage(MODELET);
			metricResponse.setMaxTime(rs.getInt("MODELET_MAX"));
			metricResponse.setMinTime(rs.getInt("MODELET_MIN"));
			metricResponse.setMeanTime(rs.getInt("MODELET_AVG"));
			metricResponse.setPercentileTransaction(rs.getInt("MODELET"));
			responseList.add(metricResponse);
			metricResponse = new VersionMetricResponseInfo();
			metricResponse.setStage(MODEL);
			metricResponse.setMaxTime(rs.getInt("MODEL_MAX"));
			metricResponse.setMinTime(rs.getInt("MODEL_MIN"));
			metricResponse.setMeanTime(rs.getInt("MODEL_AVG"));
			metricResponse.setPercentileTransaction(rs.getInt("MODEL"));
			responseList.add(metricResponse);
			metricResponse = new VersionMetricResponseInfo();
			metricResponse.setStage(END_TO_END);
			metricResponse.setMaxTime(rs.getInt("FULLCYC_MAX"));
			metricResponse.setMinTime(rs.getInt("FULLCYC_MIN"));
			metricResponse.setMeanTime(rs.getInt("FULLCYC_AVG"));
			metricResponse.setPercentileTransaction(rs.getInt("FULLCYCLE"));
			responseList.add(metricResponse);
			metricsMap.put(TOTAL_TRANSACTIONS,rs.getInt("TXN_COUNT"));
			metricsMap.put(METRICS_INFO, responseList);
			return metricsMap;
		}
	}

}
