/**
 * 
 */
package com.ca.umg.business.dashboard.dao;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.dashboard.info.ModelUsageInfo;
import com.ca.umg.business.dashboard.info.ModelVersionStatus;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.mongo.dao.MongoTransactionDAO;
import com.ca.umg.business.util.AdminUtil;
import com.mongodb.Cursor;
import com.mongodb.DBObject;

/**
 * @author kamathan
 *
 */
@Repository
@SuppressWarnings("PMD")
public class DashBoardDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(DashBoardDAO.class);

	private static final String COUNT_UNIQUE_VERSIONS = "SELECT COUNT(DISTINCT NAME) FROM UMG_VERSION WHERE TENANT_ID = ? ";

	private static final String COUNT_VERSIONS_USING_STATUS = "SELECT COUNT(NAME) FROM UMG_VERSION WHERE TENANT_ID = ? AND UPPER(STATUS) = ? ";

	private static final String COUNT_VERSION_BY_DAY = "SELECT COUNT(VERSION_NAME) FROM UMG_RUNTIME_TRANSACTION WHERE TENANT_ID = ? AND RUN_AS_OF_DATE >= ? AND IS_TEST = 0";

	private static final String COUNT_ACTIVE_LOOKUP_DATA = "SELECT COUNT(CONTAINER_NAME) FROM SYNDICATED_DATA WHERE VALID_FROM <= ? AND VALID_TO >= ? ";

	private static final String COUNT_EXPIRING_LOOKUP_DATA = "SELECT COUNT(VERSION_NAME) FROM SYNDICATED_DATA WHERE VALID_FROM <= ? AND VALID_TO >= ? AND VALID_TO <= ? ";

	private static final String PUBLISHED_TRANSACTION_COUNT = "SELECT DISTINCT r.VERSION_NAME AS VERSION_NAME, "
			+ "COUNT(CASE WHEN UPPER(r.STATUS)='SUCCESS' THEN r.STATUS END) AS SUCCESS_COUNT, "
			+ "COUNT(CASE WHEN UPPER(r.STATUS)='ERROR' THEN r.STATUS END) AS FAILURE_COUNT "
			+ "FROM UMG_RUNTIME_TRANSACTION r " + "WHERE r.IS_TEST = 0 "
			+ "AND r.TENANT_ID = ? AND r.RUN_AS_OF_DATE BETWEEN ? AND ? " + "GROUP BY r.VERSION_NAME";

	private static final String DATE_PATTERN = "yyyy,MM,dd,HH,mm";

	/*
	 * private static final String TRANSACTION_COUNT_PER_DAY = "SELECT " +
	 * "YEAR(STR_TO_DATE(DATE_FORMAT(FROM_UNIXTIME(CREATED_ON/1000),'%m-%d-%Y'), '%m-%d-%Y')) AS D_year, "
	 * +
	 * "MONTH(STR_TO_DATE(DATE_FORMAT(FROM_UNIXTIME(CREATED_ON/1000),'%m-%d-%Y'), '%m-%d-%Y')) AS D_moonth, "
	 * +
	 * "DAY(STR_TO_DATE(DATE_FORMAT(FROM_UNIXTIME(CREATED_ON/1000),'%m-%d-%Y'), '%m-%d-%Y')) AS D_daay, "
	 * + "COUNT(VERSION_NAME) AS COUNT, VERSION_NAME AS VERSION_NAME " +
	 * "FROM UMG_RUNTIME_TRANSACTION " +
	 * "WHERE DATE_FORMAT(FROM_UNIXTIME(CREATED_ON/1000),'%Y-%m-%d') >= DATE_ADD(CURDATE(), INTERVAL -90 DAY) "
	 * + "GROUP BY D_year,D_moonth, D_daay, VERSION_NAME " +
	 * "ORDER BY D_daay , D_moonth, D_year ";
	 */

	private static final String TRANSACTION_COUNT_LAST_90_DAYS = "SELECT "
			+ "MONTHNAME(STR_TO_DATE(DATE_FORMAT(FROM_UNIXTIME(r.RUN_AS_OF_DATE/1000),'%m-%d-%Y'), '%m-%d-%Y')) AS MONTH_NAME, "
			+ "count(VERSION_NAME) AS COUNT, VERSION_NAME AS VERSION_NAME " + "FROM UMG_RUNTIME_TRANSACTION r  "
			+ "WHERE DATE_FORMAT(FROM_UNIXTIME(r.RUN_AS_OF_DATE/1000),'%Y-%m-%d') >= DATE_ADD(CURDATE(), INTERVAL -90 DAY) "
			+ "AND r.IS_TEST = 0 AND r.TENANT_ID = ? " + "GROUP BY MONTH_NAME, VERSION_NAME " + "ORDER BY MONTH_NAME ";

	private static final String TOP_100_FAILURE_TXN = "SELECT ID,CLIENT_TRANSACTION_ID,TRANSACTION_MODE,VERSION_NAME,MAJOR_VERSION,MINOR_VERSION,RUN_AS_OF_DATE,ERROR_CODE,CAST(r.ERROR_DESCRIPTION AS CHAR) AS ERROR_DESCRIPTION FROM UMG_RUNTIME_TRANSACTION r "
			+ "WHERE r.IS_TEST = 0 AND r.STATUS = 'Error' AND r.TENANT_ID IN(#) AND r.RUN_AS_OF_DATE BETWEEN ? AND ? ORDER BY r.RUN_AS_OF_DATE DESC LIMIT TXLIMIT";

	private static final String RA_USAGE_TREND_DATA = "SELECT COUNT(*) AS TOTAL_COUNT,COUNT(CASE WHEN UPPER(r.STATUS)='SUCCESS' THEN r.STATUS END) AS SUCCESS_COUNT, COUNT(CASE WHEN UPPER(r.STATUS)='ERROR' THEN r.STATUS END) AS FAILURE_COUNT, "
			+"STR_TO_DATE(DATE_FORMAT(FROM_UNIXTIME(r.RUN_AS_OF_DATE/1000),DATEFORMAT),DATEFORMAT) AS RUN_DATE,VERSION_NAME FROM UMG_RUNTIME_TRANSACTION r WHERE r.IS_TEST = 0 AND r.TENANT_ID IN(#) AND r.RUN_AS_OF_DATE BETWEEN ? AND ? GROUP BY RUN_DATE,VERSION_NAME";
	
	private static final String RA_USAGE_TREND_DATA_INIT ="SELECT COUNT(*) AS TOTAL_COUNT,TENANT_ID,COUNT(CASE WHEN UPPER(r.STATUS)='SUCCESS' THEN r.STATUS END) AS SUCCESS_COUNT, COUNT(CASE WHEN UPPER(r.STATUS)='ERROR' THEN r.STATUS END) AS FAILURE_COUNT, "
	+"STR_TO_DATE(DATE_FORMAT(FROM_UNIXTIME(r.RUN_AS_OF_DATE/1000),'DATEFORMAT'),'DATEFORMAT') AS RUN_DATE,VERSION_NAME FROM UMG_RUNTIME_TRANSACTION r WHERE r.IS_TEST = 0 AND r.TENANT_ID IN(#) AND r.RUN_AS_OF_DATE BETWEEN ? AND ? GROUP BY RUN_DATE,VERSION_NAME,TENANT_ID";
	
	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@Inject
	private MongoTransactionDAO mongoTransactionDAO;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * Returns unique version count
	 * 
	 * @return
	 */
	public Long countUniqueVersions() throws BusinessException, SystemException {
		LOGGER.info("Getting the unique version count DashBoardDAO::countUniqueVersions : " + COUNT_UNIQUE_VERSIONS);
		Long uniqueVersionCount = jdbcTemplate.queryForObject(COUNT_UNIQUE_VERSIONS,
				new Object[] { RequestContext.getRequestContext().getTenantCode() }, Long.class);
		return uniqueVersionCount;
	}

	/**
	 * Returns published version count
	 * 
	 * @return
	 */
	public Long countVersionsUsingStatus(String status) {
		LOGGER.info("Getting the published version count DashBoardDAO::countVersionsUsingStatus : "
				+ COUNT_VERSIONS_USING_STATUS);
		Long versionStatusCount = jdbcTemplate.queryForObject(COUNT_VERSIONS_USING_STATUS,
				new Object[] { RequestContext.getRequestContext().getTenantCode(), status }, Long.class);
		return versionStatusCount;
	}

	/**
	 * Returns version count for day
	 * 
	 * @return
	 * @throws SystemException
	 */
	public Long countVersionsByDay(Long millisForDays) throws SystemException {
		/*
		 * LOGGER.
		 * info("Getting the published version count DashBoardDAO::countVersionsByDay : "
		 * +COUNT_VERSION_BY_DAY); Long versionByDayCount =
		 * jdbcTemplate.queryForObject(COUNT_VERSION_BY_DAY, new Object[]
		 * {RequestContext.getRequestContext().getTenantCode(), millisForDays},
		 * Long.class);
		 */
		Long versionByDayCount = mongoTransactionDAO.fetchTransactionInDays(millisForDays);
		return versionByDayCount;
	}

	/**
	 * Returns active lookup data count
	 * 
	 * @return
	 */
	public Long countActiveLookUpData(Long millisForCurrentTime) {
		LOGGER.info("Getting the Active LookUp Data count DashBoardDAO::countActiveLookUpData : "
				+ COUNT_ACTIVE_LOOKUP_DATA);
		getRequestContext().setAdminAware(true);
		Long activeLookUpDataCount = jdbcTemplate.queryForObject(COUNT_ACTIVE_LOOKUP_DATA,
				new Object[] { millisForCurrentTime, millisForCurrentTime }, Long.class);
		getRequestContext().setAdminAware(false);
		return activeLookUpDataCount;
	}

	/**
	 * Returns expiring lookup data count
	 * 
	 * @return
	 */
	public Long countExpiringLookUpData(Long millisForCurrentTime, Long millisForDays) {
		LOGGER.info("Getting the Expiring LookUp Data count DashBoardDAO::countExpiringLookUpData : "
				+ COUNT_EXPIRING_LOOKUP_DATA);
		getRequestContext().setAdminAware(true);
		Long expiringLookUpDataCount = jdbcTemplate.queryForObject(COUNT_EXPIRING_LOOKUP_DATA,
				new Object[] { millisForCurrentTime, millisForCurrentTime, millisForDays }, Long.class);
		getRequestContext().setAdminAware(false);
		return expiringLookUpDataCount;
	}

	/**
	 * gets the success and failure count for published/prod transactions
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, Long> getScsFailCntForTransactions(final Long runAsOfDateFrom, final Long runAsOfDateTo,
			final String[] tenants) throws SystemException, InterruptedException, ExecutionException {

		final Map<String, Long> aggregateResult = new ConcurrentHashMap<>();
		// TODO if number of request increase change the cache pool to fixed pool
		ExecutorService exService = Executors.newCachedThreadPool();
		for (final String tenantCode : tenants) {
			Future<Cursor> future = exService.submit(new Callable<Cursor>() {
				@Override
				public Cursor call() throws Exception {
					return mongoTransactionDAO.getSuccessFailTransactionCount(runAsOfDateFrom, runAsOfDateTo,
							tenantCode);
				}
			});
			Cursor cursor = future.get();
			// Aggregate all the result in map for multiple tenanats
			if (cursor != null) {
				while (cursor.hasNext()) {
					DBObject obj = cursor.next();
					String key = (String) obj.get(BusinessConstants.GROUP_ID);

					if (aggregateResult.containsKey(key)) {
						aggregateResult.put(key, (Long.valueOf((Integer) obj.get(BusinessConstants.TRANSACTION_COUNT)))
								+ aggregateResult.get(key));
					} else {
						aggregateResult.put(key, Long.valueOf((Integer) obj.get(key)));
					}

				}
			}
		}

		if (exService.isTerminated()) {
			exService.shutdown();
		}
		// get the max three element from map and sum up others

		Map<String, Long> successFailCount = new HashMap(); /*getMaxTenTxnAndOtherTxn(aggregateResult);*/

		return successFailCount;
	}

	private void getMaxTenTxnAndOtherTxn(Map<String,Object> agregatedData,Map<String, Long> unsortMap,Map<String,Object> modelTrendData) {
		LOGGER.info("Entering getMaxTenTxnAndOtherTxn");

		List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(unsortMap.entrySet());

		Map<String,Object> topTenModelData = new HashMap<>();
		
		
		Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<String, Long> result = new HashMap<String, Long>();
		int count = BusinessConstants.NUMBER_ZERO;
		for (Map.Entry<String, Long> entry : list) {
			String key =  entry.getKey();
		    Long value =entry.getValue();
			if (count < BusinessConstants.NUMBER_TEN) {
				LOGGER.info("Count {} Key {} Val {}",count,key,value);
				LOGGER.info("Model Trend {} ",modelTrendData.get(key));

				topTenModelData.put(key, modelTrendData.get(key));
				result.put(key, value);
				result.put(BusinessConstants.OTHERS, BusinessConstants.NUMBER_ZERO_LONG);
			} else {
				result.put(BusinessConstants.OTHERS, result.get(BusinessConstants.OTHERS) + value);
			}
			count++;
		}
		agregatedData.put(BusinessConstants.USAGE_MATRICS, result);
		agregatedData.put(BusinessConstants.MODEL_USAGE_TRENDLINE, topTenModelData);

	}

	/**
	 * gets the status metrics count for published/prod transactions
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, Object> getStatusMetricsTransactions(final Long runAsOfDateFrom, final Long runAsOfDateTo,
			final String[] tenants) throws SystemException, InterruptedException, ExecutionException {

		final Map<String, Object> aggregateResult = new HashMap<>();
		// TODO if number of request increase change the cache pool to fixed pool
		ExecutorService exService = Executors.newCachedThreadPool();
		List<Future<Cursor>> aggregateResponse = new ArrayList<>();
		for (final String tenantCode : tenants) {
			Future<Cursor> future = exService.submit(new Callable<Cursor>() {
				@Override
				public Cursor call() throws Exception {
					return mongoTransactionDAO.getStatusMetricsTransactionCount(runAsOfDateFrom, runAsOfDateTo,
							tenantCode);
				}
			});
			aggregateResponse.add(future);
		}

		for (Future<Cursor> future : aggregateResponse) {
			Cursor cursor = future.get();
			// Aggregate all the result in map for multiple tenanats
			if (cursor != null && cursor.hasNext()) {
				DBObject obj = cursor.next();
				for (String key : obj.keySet()) {
					if (!key.equals("_id")) {
						if (aggregateResult.containsKey(key)) {
							aggregateResult.put(key, (Long.valueOf((Integer) obj.get(key)))
									+ Long.valueOf((Integer) aggregateResult.get(key)));
						} else {
							aggregateResult.put(key, obj.get(key));
						}
					}
				}

			}
		}

		if (exService.isTerminated()) {
			exService.shutdown();
		}

		return aggregateResult;
	}

	/**
	 * gets the usage dynamics for published/prod transactions
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public Map<String, Object> getUsageDynamicsDetails(final Long runAsOfDateFrom, final Long runAsOfDateTo,
			String[] tenantCodes) throws SystemException, InterruptedException, ExecutionException {

		List<ModelVersionStatus> transactionSucFailCnt = new ArrayList<>();
		final Map<String, Long> aggregateResult = new HashMap<>();

		/*// Status Metrics Attribute
		Long modelFailure = BusinessConstants.NUMBER_ZERO_LONG;
		Long otherFailure = BusinessConstants.NUMBER_ZERO_LONG;
		Long inputFailure = BusinessConstants.NUMBER_ZERO_LONG;
		Long prodSuccess = BusinessConstants.NUMBER_ZERO_LONG;
		Long outputFailure = BusinessConstants.NUMBER_ZERO_LONG;*/

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Map<String, Object>> tntTxnMetrics = new HashMap<>();

		//Login Tnt param
		Map<String, Object> loginTntData = new HashMap<String, Object>();
		Map<String, Long> loginTntUsageMetrics = null;
		List<ModelVersionStatus> loginUsageDynamics = new ArrayList<>();


		// Note - We implemented this story assuming that two tenents can't publish the
		// model with same modelname and same version
		// TODO if number of request increase change the cache pool to fixed pool
		ExecutorService exService = Executors.newCachedThreadPool();
		Map<String, Future<Cursor>> aggregateResponse = new HashMap<>();
		for (final String tenantCode : tenantCodes) {
			Future<Cursor> future = exService.submit(new Callable<Cursor>() {
				@Override
				public Cursor call() throws Exception {
					return mongoTransactionDAO.getUsageDynamicsDetails(runAsOfDateFrom, runAsOfDateTo, tenantCode);
				}
			});
			aggregateResponse.put(tenantCode, future);
		}

		// prepare aggregated result
		for (Entry<String, Future<Cursor>> future : aggregateResponse.entrySet()) {
			// Tenant Transaction Metrics

			Long tntSuccsCount = BusinessConstants.NUMBER_ZERO_LONG;
			Long inptvalfail = BusinessConstants.NUMBER_ZERO_LONG;
			Long outptValFail = BusinessConstants.NUMBER_ZERO_LONG;
			Long modlFail = BusinessConstants.NUMBER_ZERO_LONG;
			Long techFail = BusinessConstants.NUMBER_ZERO_LONG;

			Map<String, Object> tntMetricsVal = new HashMap<String, Object>();
			

			Cursor cursor = future.getValue().get();
			if (cursor != null) {
				while (cursor.hasNext()) {
					DBObject obj = cursor.next();
					ModelVersionStatus modelVersionStatus = new ModelVersionStatus();

					DBObject groupEle = (DBObject) obj.get(BusinessConstants.GROUP_ID);
					String key = (String) groupEle.get(BusinessConstants.MODEL_NAME);
					Long modelFailCount = Long.valueOf(obj.get(BusinessConstants.MODEL_FAILURES).toString());
					Long successCount = Long.valueOf(obj.get(BusinessConstants.SUCCESS).toString());
					Long inputValCount = Long.valueOf(obj.get(BusinessConstants.INPUT_VALIDATION_FAILURES).toString());
					Long outputValCount = Long
							.valueOf(obj.get(BusinessConstants.OUTPUT_VALIDATION_FAILURES).toString());

					modelVersionStatus.setModelName(key);
					modelVersionStatus.setModelVersion(groupEle.get(BusinessConstants.MAJOR_VERSION) + "."
							+ groupEle.get(BusinessConstants.MINOR_VERSION));
					Long totalRcrds = Long.valueOf(obj.get(BusinessConstants.TOTAL).toString());
					modelVersionStatus.setCount(totalRcrds);
					modelVersionStatus.setSuccessCount(successCount);
					
					
					List<Long> modelResTime = (List<Long>) ((List<?>) obj.get(BusinessConstants.MODEL_RESPONSE_TIME));
					Collections.sort(modelResTime);
					Long prefix = (modelResTime.get((int) Math.round((modelResTime.size() - 1) * (0.9))))
							/ BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
					Long sufix = (modelResTime.get((int) Math.round((modelResTime.size() - 1) * (0.9))))
							% BusinessConstants.LONG_NUMBER_ONE_THOUSAND;

					modelVersionStatus.setModelResponseTime(prefix + BusinessConstants.DOT + sufix);

					List<Long> endToEndTime = (List<Long>) ((List<?>) obj.get(BusinessConstants.END_TO_END_TIME));
					Collections.sort(endToEndTime);

					Long endprefix = (endToEndTime.get((int) Math.round((endToEndTime.size() - 1) * (0.9))))
							/ BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
					Long endsufix = (endToEndTime.get((int) Math.round((endToEndTime.size() - 1) * (0.9))))
							% BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
					modelVersionStatus.setEndToEndTime(endprefix + BusinessConstants.DOT + endsufix);
					Long modeletUtilization = Long.valueOf(obj.get(BusinessConstants.MODEL_UTILISATION).toString());
					Long utlprefix = modeletUtilization / BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
					Long ultsufix = modeletUtilization % BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
					modelVersionStatus.setModelUtilization(utlprefix + BusinessConstants.DOT + ultsufix);

					modelVersionStatus.setInputValidationFailure(inputValCount);

					modelVersionStatus.setOutputValidationFailure(outputValCount);

					modelVersionStatus.setFailureCount(modelFailCount);

					transactionSucFailCnt.add(modelVersionStatus);

					// set the total count for specific model

					if (aggregateResult.containsKey(key)) {
						aggregateResult.put(key, totalRcrds + aggregateResult.get(key));
					} else {
						aggregateResult.put(key, totalRcrds);
					}

					// set status Metrics for the perticular model name
					/*modelFailure += modelFailCount;
					otherFailure += Long.valueOf(obj.get(BusinessConstants.OTHER_FAILURES).toString());
					inputFailure += inputValCount;
					prodSuccess += successCount;
					outputFailure += outputValCount;*/

					// set tnt txn metrics
					modlFail += modelFailCount;
					techFail += Long.valueOf(obj.get(BusinessConstants.OTHER_FAILURES).toString());
					inptvalfail += inputValCount;
					tntSuccsCount += successCount;
					outptValFail += outputValCount;

				}
				tntMetricsVal.put(BusinessConstants.OTHER_FAILURES, techFail);
				tntMetricsVal.put(BusinessConstants.MODEL_FAILURES, modlFail);
				tntMetricsVal.put(BusinessConstants.INPUT_VALIDATION_FAILURES, inptvalfail);
				tntMetricsVal.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, outptValFail);
				tntMetricsVal.put(BusinessConstants.SUCCESS, tntSuccsCount);
				tntTxnMetrics.put(future.getKey(), tntMetricsVal);
				
				if(StringUtils.equalsIgnoreCase(RequestContext.getRequestContext().TENANT_CODE, future.getKey())) {
/*				 loginTntUsageMetrics = getMaxTenTxnAndOtherTxn(aggregateResult);
*/				 Collections.copy(loginUsageDynamics, transactionSucFailCnt);
				}
				
			}
		}

		// get the top three total count models
/*		Map<String, Long> usageMetrics = getMaxTenTxnAndOtherTxn(aggregateResult);
*/
		// Prepare Status Metrics
		/*Map<String, Long> statusMetrics = new HashMap<>();
		statusMetrics.put(BusinessConstants.OTHER_FAILURES, otherFailure);
		statusMetrics.put(BusinessConstants.MODEL_FAILURES, modelFailure);
		statusMetrics.put(BusinessConstants.INPUT_VALIDATION_FAILURES, inputFailure);
		statusMetrics.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, outputFailure);
		statusMetrics.put(BusinessConstants.SUCCESS, prodSuccess);*/

		result.put(BusinessConstants.USAGE_DYNAMICS, transactionSucFailCnt);
/*		result.put(BusinessConstants.USAGE_MATRICS, usageMetrics);
*//*		result.put(BusinessConstants.STATUS_MATRICS, statusMetrics);
*/		result.put(BusinessConstants.TENANT_TXN_MATRICS, tntTxnMetrics);
        loginTntData.put(BusinessConstants.USAGE_DYNAMICS, loginUsageDynamics);
        loginTntData.put(BusinessConstants.USAGE_MATRICS, loginTntUsageMetrics);
        result.put(BusinessConstants.LOGIN_TENANT,loginTntData);

		return result;
	}

	
	/**
	 * gets the usage dynamics for published/prod transactions
	 * @param string 
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getInitUsageDynamicsDetails(TransactionFilter transactionFilter,Map<String,Object> groupByAndData) throws SystemException, InterruptedException, ExecutionException {

		final Long runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
		final Long runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
		String[] tenantCodes = transactionFilter.getTenantNames();
		final String dateFormat = (String) groupByAndData.get(BusinessConstants.GROUP_ID);
		final String selectionType = transactionFilter.getSelectionType();
		
		final Map<String, Long> aggregateResult = new HashMap<>();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Map<String, Object>> tntTxnMetrics = new HashMap<>();
		Map<String, Object> raTrendData = new HashMap<String, Object>((Map<String, Object>)groupByAndData.get(BusinessConstants.RA_USAGE_TRENDLINE));
		Map<String, Object> modelTrendData = new HashMap<String, Object>();

		String currentTenant = RequestContext.getRequestContext().getTenantCode();
		
		// Note - We implemented this story assuming that two tenents can't publish the
		// model with same modelname and same version
		// TODO if number of request increase change the cache pool to fixed pool
		ExecutorService exService = Executors.newCachedThreadPool();
		Map<String, Future<Cursor>> aggregateResponse = new HashMap<>();
		for (final String tenantCode : tenantCodes) {
			Future<Cursor> future = exService.submit(new Callable<Cursor>() {
				@Override
				public Cursor call() throws Exception {
					return mongoTransactionDAO.getUsageDynamicsDetails(runAsOfDateFrom, runAsOfDateTo, tenantCode,dateFormat,selectionType);
				}
			});
			aggregateResponse.put(tenantCode, future);
		}
		
		
		
		
		//Usage Pattern

		// prepare aggregated result
		for (Entry<String, Future<Cursor>> future : aggregateResponse.entrySet()) {
			
			// Tenant Transaction Metrics
			Long tntSuccsCount = BusinessConstants.NUMBER_ZERO_LONG;
			Long inptvalfail = BusinessConstants.NUMBER_ZERO_LONG;
			Long outptValFail = BusinessConstants.NUMBER_ZERO_LONG;
			Long modlFail = BusinessConstants.NUMBER_ZERO_LONG;
			Long techFail = BusinessConstants.NUMBER_ZERO_LONG;

			Map<String, Object> tntMetricsVal = new HashMap<String, Object>();
			
			
			Cursor cursor = future.getValue().get();
			if (cursor != null) {
				while (cursor.hasNext()) {
					String exeTenant = future.getKey();
					
					DBObject obj = cursor.next();
					DBObject groupEle = (DBObject) obj.get(BusinessConstants.GROUP_ID);
					String key = (String) groupEle.get(BusinessConstants.MODEL_NAME);
					Long modelFailCount = Long.valueOf(obj.get(BusinessConstants.MODEL_FAILURES).toString());
					Long successCount = Long.valueOf(obj.get(BusinessConstants.SUCCESS).toString());
					Long inputValCount = Long.valueOf(obj.get(BusinessConstants.INPUT_VALIDATION_FAILURES).toString());
					Long outputValCount = Long.valueOf(obj.get(BusinessConstants.OUTPUT_VALIDATION_FAILURES).toString());
					Long totalRcrds = Long.valueOf(obj.get(BusinessConstants.TOTAL).toString());
					
					Integer year ;
					Integer month ;
					Integer day ;
					Integer hour ;
					String trendDataKey = "";
					if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.YEAR)) {
						year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE;
					}else if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.MONTH)){
						year = Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month = Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE;
					}
                    else if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.DAY)){
                    	year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month = Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						day = Integer.valueOf(groupEle.get(BusinessConstants.DAY).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+day;
					}
                    else {
                    	year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month =Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						day =Integer.valueOf(groupEle.get(BusinessConstants.DAY).toString());
						hour =Integer.valueOf(groupEle.get(BusinessConstants.HOUR).toString());
/*						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+day+BusinessConstants.HYPHEN+hour;
*/						trendDataKey = AdminUtil.getDateFormatForEst(new DateTime(year, month, day, hour,00,DateTimeZone.UTC),BusinessConstants.UMG_EST_FORMAT);                    
                    }
					

					//RA USAGE TREND DATA REQUIRED FOR ALL TENANTS
					//success and fail count in particular time interval 
					
					if(raTrendData.containsKey(trendDataKey)) {
						Map<String,Long> map = (Map<String, Long>) raTrendData.get(trendDataKey);
						map.put(BusinessConstants.SUCCESS, map.get(BusinessConstants.SUCCESS)+successCount);
						map.put(BusinessConstants.FAILURE_COUNT, map.get(BusinessConstants.FAILURE_COUNT)+(totalRcrds-successCount));
					}else {
						LOGGER.info("key {} not available in raTrendInit data",trendDataKey);
						Map<String, Long> raData = new HashMap<String, Long>();
						raData.put(BusinessConstants.SUCCESS, successCount);
						raData.put(BusinessConstants.FAILURE_COUNT, totalRcrds-successCount);
						raTrendData.put(trendDataKey, raData);
					}

					//this data is only for login tenant by default for dashboard screen
					if(StringUtils.equalsIgnoreCase(currentTenant.trim(),exeTenant.trim())) {
						LOGGER.info("Current Tenant {} Key {}",currentTenant,exeTenant);

						//MODEL USAGE TREND DATA
						//total count in particular time interval
						if(modelTrendData.containsKey(key)) {
							Map<String,Object> modelmap = (Map<String, Object>)modelTrendData.get(key);
							if(modelmap.containsKey(trendDataKey)) {
								modelmap.put(trendDataKey,(Long)modelmap.get(trendDataKey)+totalRcrds);
							}else {
								LOGGER.info("Model Trend Data not Present key {} model {}",trendDataKey,key);
								modelmap.put(trendDataKey,totalRcrds);
							}
					}else {
						Map<String, Long> modelData = new HashMap<String, Long>((Map<String, Long>)groupByAndData.get(BusinessConstants.MODEL_USAGE_TRENDLINE));
						//Trend data key is the particular time having total txn
						modelData.put(trendDataKey, totalRcrds);
						//Unique Model at the particular time having total rcrd
						LOGGER.info("key1 {} Val1 {}",trendDataKey,totalRcrds);
						modelTrendData.put(key, modelData);
					}
					
					
					// set the total count for specific model pie chart
					if (aggregateResult.containsKey(key)) {
						aggregateResult.put(key, totalRcrds + aggregateResult.get(key));
					} else {
						aggregateResult.put(key, totalRcrds);
					}
					}
					// set tnt txn metrics For all tenants
					modlFail += modelFailCount;
					techFail += Long.valueOf(obj.get(BusinessConstants.OTHER_FAILURES).toString());
					inptvalfail += inputValCount;
					tntSuccsCount += successCount;
					outptValFail += outputValCount;

				}
				// set tnt txn metrics For all tenants
				tntMetricsVal.put(BusinessConstants.OTHER_FAILURES, techFail);
				tntMetricsVal.put(BusinessConstants.MODEL_FAILURES, modlFail);
				tntMetricsVal.put(BusinessConstants.INPUT_VALIDATION_FAILURES, inptvalfail);
				tntMetricsVal.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, outptValFail);
				tntMetricsVal.put(BusinessConstants.SUCCESS, tntSuccsCount);
				tntTxnMetrics.put(future.getKey(), tntMetricsVal);
				
			}
		}
		
		

		// get the top ten total count models
		getMaxTenTxnAndOtherTxn(result,aggregateResult,modelTrendData);


	    result.put(BusinessConstants.TENANT_TXN_MATRICS, tntTxnMetrics);
		result.put(BusinessConstants.RA_USAGE_TRENDLINE, raTrendData);


		return result;
	}

	/**
	 * gets the usage dynamics for published/prod transactions
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getUsageDynamicsGrid(TransactionFilter transactionFilter) throws SystemException, InterruptedException, ExecutionException {

		final Long runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
		final Long runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
		String[] tenantCodes = transactionFilter.getSelectedTnt();
/*		final String selectionType = transactionFilter.getSelectionType();
*/		
		List<Object> usagePattern = new ArrayList<>();
		
		

		// Note - We implemented this story assuming that two tenents can't publish the
		// model with same modelname and same version
		// TODO if number of request increase change the cache pool to fixed pool
		ExecutorService exService = Executors.newCachedThreadPool();
		Map<String, Future<Cursor>> aggregateResponse = new HashMap<>();
		for (final String tenantCode : tenantCodes) {
			Future<Cursor> future = exService.submit(new Callable<Cursor>() {
				@Override
				public Cursor call() throws Exception {
					return mongoTransactionDAO.getUsageDynamicsGrid(runAsOfDateFrom, runAsOfDateTo, tenantCode);
				}
			});
			aggregateResponse.put(tenantCode, future);
		}
		
		
		
		
		//Usage Pattern

		// prepare aggregated result
		for (Entry<String, Future<Cursor>> future : aggregateResponse.entrySet()) {
			
			Cursor cursor = future.getValue().get();
			if (cursor != null) {
				while (cursor.hasNext()) {
					DBObject obj = cursor.next();
					DBObject groupEle = (DBObject) obj.get(BusinessConstants.GROUP_ID);
					String key = (String) groupEle.get(BusinessConstants.MODEL_NAME);
					Long modelFailCount = Long.valueOf(obj.get(BusinessConstants.MODEL_FAILURES).toString());
					Long successCount = Long.valueOf(obj.get(BusinessConstants.SUCCESS).toString());
					Long inputValCount = Long.valueOf(obj.get(BusinessConstants.INPUT_VALIDATION_FAILURES).toString());
					Long outputValCount = Long.valueOf(obj.get(BusinessConstants.OUTPUT_VALIDATION_FAILURES).toString());
					Long otherFailures = Long.valueOf(obj.get(BusinessConstants.OTHER_FAILURES).toString());
					Long totalRcrds = Long.valueOf(obj.get(BusinessConstants.TOTAL).toString());
					Long modelResponseTime =Math.round(Double.valueOf(obj.get(BusinessConstants.MODEL_RESPONSE_TIME).toString()));
					Long endToendTime =Math.round(Double.valueOf(obj.get(BusinessConstants.END_TO_END_TIME).toString()));

				Long modeletUtilization = Long.valueOf(obj.get(BusinessConstants.MODEL_UTILISATION).toString());
				Long utlprefix = modeletUtilization / BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
				Long ultsufix = modeletUtilization % BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
				
				Long e2eprefix = endToendTime / BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
				Long e2esufix = endToendTime % BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
				
				Long modelResprefix = modelResponseTime / BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
				Long modelRespsufix = modelResponseTime % BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
				
				
				String modelVersion = groupEle.get(BusinessConstants.MAJOR_VERSION) + BusinessConstants.DOT
							+ groupEle.get(BusinessConstants.MINOR_VERSION);
					
				
						Map<String, Object> usagePatternData = new HashMap<String, Object>();
						
						usagePatternData.put(BusinessConstants.MODEL_NAME, key);
						usagePatternData.put(BusinessConstants.MODEL_VERSION, modelVersion);
						usagePatternData.put(BusinessConstants.TOTAL, totalRcrds);
						usagePatternData.put(BusinessConstants.SUCCESS, successCount);
						usagePatternData.put(BusinessConstants.MODEL_FAILURES, modelFailCount);
						usagePatternData.put(BusinessConstants.INPUT_VALIDATION_FAILURES, inputValCount);
						usagePatternData.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, outputValCount);
						usagePatternData.put(BusinessConstants.OTHER_FAILURES, otherFailures);
						usagePatternData.put(BusinessConstants.MODEL_RESPONSE_TIME, modelResprefix+BusinessConstants.DOT+modelRespsufix);
						usagePatternData.put(BusinessConstants.END_TO_END_TIME, e2eprefix+BusinessConstants.DOT+e2esufix);
						usagePatternData.put(BusinessConstants.MODEL_UTILISATION, utlprefix+BusinessConstants.DOT+ultsufix);
						usagePattern.add(usagePatternData);	
                    }
				
			}
		}
		
		return usagePattern;
	}
	
	/**
	 * gets the usage dynamics for published/prod transactions
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSelectedUsageDynamicsDetails(TransactionFilter transactionFilter,Map<String,Object> groupByAndData) throws SystemException, InterruptedException, ExecutionException {

		final Long runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
		final Long runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
		String[] tenantCodes = transactionFilter.getTenantNames();
		final String dateFormat = (String) groupByAndData.get(BusinessConstants.GROUP_ID);
		final String selectionType = transactionFilter.getSelectionType();
		
		final Map<String, Long> aggregateResult = new HashMap<>();
		Map<String, Object> result = new HashMap<String, Object>();
	
		Map<String, Object> modelTrendData = new HashMap<String, Object>();
		
		

		// Note - We implemented this story assuming that two tenents can't publish the
		// model with same modelname and same version
		// TODO if number of request increase change the cache pool to fixed pool
		ExecutorService exService = Executors.newCachedThreadPool();
		Map<String, Future<Cursor>> aggregateResponse = new HashMap<>();
		for (final String tenantCode : tenantCodes) {
			Future<Cursor> future = exService.submit(new Callable<Cursor>() {
				@Override
				public Cursor call() throws Exception {
					return mongoTransactionDAO.getSelectedUsageDynamicsDetails(runAsOfDateFrom, runAsOfDateTo, tenantCode,dateFormat,selectionType);
				}
			});
			aggregateResponse.put(tenantCode, future);
		}
		
		
		
		
		//Usage Pattern

		// prepare aggregated result
		for (Entry<String, Future<Cursor>> future : aggregateResponse.entrySet()) {
			
			Cursor cursor = future.getValue().get();
			if (cursor != null) {
				while (cursor.hasNext()) {
					DBObject obj = cursor.next();
					DBObject groupEle = (DBObject) obj.get(BusinessConstants.GROUP_ID);
					String key = (String) groupEle.get(BusinessConstants.MODEL_NAME);
					Long totalRcrds = Long.valueOf(obj.get(BusinessConstants.TOTAL).toString());
					Integer year ;
					Integer month ;
					Integer day ;
					Integer hour ;
					String trendDataKey = "";
					if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.YEAR)) {
						year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE;
					}else if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.MONTH)){
						year = Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month = Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE;
					}
                    else if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.DAY)){
                    	year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month = Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						day = Integer.valueOf(groupEle.get(BusinessConstants.DAY).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+day;
					}
                    else {
                    	year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month =Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						day =Integer.valueOf(groupEle.get(BusinessConstants.DAY).toString());
						hour =Integer.valueOf(groupEle.get(BusinessConstants.HOUR).toString());
/*						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+day+BusinessConstants.HYPHEN+hour;
*/						trendDataKey = AdminUtil.getDateFormatForEst(new DateTime(year, month, day, hour,00,DateTimeZone.UTC),BusinessConstants.UMG_EST_FORMAT);                    
                    }
					
			//MODEL USAGE TREND DATA
					//total count in particular time interval
					if(modelTrendData.containsKey(key)) {
						Map<String,Object> modelmap = (Map<String, Object>)modelTrendData.get(key);
						if(modelmap.containsKey(trendDataKey)) {
							modelmap.put(trendDataKey,(Long)modelmap.get(trendDataKey)+totalRcrds);
						}else {
							LOGGER.info("Model Trend data init key not available {}",trendDataKey);
							modelmap.put(trendDataKey,totalRcrds);
						}
				}else {
					Map<String, Long> modelData = new HashMap<String, Long>((Map<String, Long>)groupByAndData.get(BusinessConstants.MODEL_USAGE_TRENDLINE));
					//Trend data key is the particular time having total txn
					modelData.put(trendDataKey, totalRcrds);
					//Unique Model at the particular time having total rcrd
					modelTrendData.put(key, modelData);
				}
					
					
					// set the total count for specific model
					if (aggregateResult.containsKey(key)) {
						aggregateResult.put(key, totalRcrds + aggregateResult.get(key));
					} else {
						aggregateResult.put(key, totalRcrds);
					}
					
				}
				
			}
		}
		
		// get the top ten total count models
		getMaxTenTxnAndOtherTxn(result,aggregateResult,modelTrendData);

		return result;
	}
	
	/**
	 * gets the usage dynamics for published/prod transactions
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllTntUsageDynamicsDetails(TransactionFilter transactionFilter,Map<String,Object> groupByAndData) throws SystemException, InterruptedException, ExecutionException {

		final Long runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
		final Long runAsOfDateTo = transactionFilter.getRunAsOfDateTo();
		String[] tenantCodes = transactionFilter.getTenantNames();
		final String dateFormat = (String) groupByAndData.get(BusinessConstants.GROUP_ID);
		final String selectionType = transactionFilter.getSelectionType();
		
		
/*		final Map<String, Long> aggregateResult = new HashMap<>();
*/		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Map<String, Object>> tntTxnMetrics = new HashMap<>();
		Map<String, Object> raTrendData = new HashMap<String, Object>((Map<String, Object>)groupByAndData.get(BusinessConstants.RA_USAGE_TRENDLINE));
		/*Map<String, Object> modelTrendData = new HashMap<String, Object>();
		Map<String, Object> modelData = new HashMap<String, Object>();
		Map<String, Object> usagePattern = new HashMap<String, Object>();
		Map<String, Object> usagePatternData = new HashMap<String, Object>();
		List<Long> endToendTime = new ArrayList<>();
		List<Long> modelResponseTime = new ArrayList<>();
		
		Long totalCountForMode = BusinessConstants.NUMBER_ZERO_LONG;*/

		// Note - We implemented this story assuming that two tenents can't publish the
		// model with same modelname and same version
		// TODO if number of request increase change the cache pool to fixed pool
		ExecutorService exService = Executors.newCachedThreadPool();
		Map<String, Future<Cursor>> aggregateResponse = new HashMap<>();
		for (final String tenantCode : tenantCodes) {
			Future<Cursor> future = exService.submit(new Callable<Cursor>() {
				@Override
				public Cursor call() throws Exception {
					return mongoTransactionDAO.getAllTntUsageDynamicsDetails(runAsOfDateFrom, runAsOfDateTo, tenantCode,dateFormat,selectionType);
				}
			});
			aggregateResponse.put(tenantCode, future);
		}
		
		
		
		
		//Usage Pattern

		// prepare aggregated result
		for (Entry<String, Future<Cursor>> future : aggregateResponse.entrySet()) {
			
			// Tenant Transaction Metrics
			Long tntSuccsCount = BusinessConstants.NUMBER_ZERO_LONG;
			Long inptvalfail = BusinessConstants.NUMBER_ZERO_LONG;
			Long outptValFail = BusinessConstants.NUMBER_ZERO_LONG;
			Long modlFail = BusinessConstants.NUMBER_ZERO_LONG;
			Long techFail = BusinessConstants.NUMBER_ZERO_LONG;

			Map<String, Object> tntMetricsVal = new HashMap<String, Object>();
			
			
			Cursor cursor = future.getValue().get();
			if (cursor != null) {
				while (cursor.hasNext()) {
					DBObject obj = cursor.next();
					DBObject groupEle = (DBObject) obj.get(BusinessConstants.GROUP_ID);
/*					String key = (String) groupEle.get(BusinessConstants.MODEL_NAME);
*/					Long modelFailCount = Long.valueOf(obj.get(BusinessConstants.MODEL_FAILURES).toString());
					Long successCount = Long.valueOf(obj.get(BusinessConstants.SUCCESS).toString());
					Long inputValCount = Long.valueOf(obj.get(BusinessConstants.INPUT_VALIDATION_FAILURES).toString());
					Long outputValCount = Long.valueOf(obj.get(BusinessConstants.OUTPUT_VALIDATION_FAILURES).toString());
					Long totalRcrds = Long.valueOf(obj.get(BusinessConstants.TOTAL).toString());
					/*List<Long> modelResTime = (List<Long>) ((List<?>) obj.get(BusinessConstants.MODEL_RESPONSE_TIME));
					List<Long> endToEndRespTime = (List<Long>) ((List<?>) obj.get(BusinessConstants.END_TO_END_TIME));
					Long modeletUtilization = Long.valueOf(obj.get(BusinessConstants.MODEL_UTILISATION).toString());

					
					String modelVersion = groupEle.get(BusinessConstants.MAJOR_VERSION) + BusinessConstants.DOT
							+ groupEle.get(BusinessConstants.MINOR_VERSION);
					*/
					Integer year ;
					Integer month ;
					Integer day ;
					Integer hour ;
					String trendDataKey = "";
					if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.YEAR)) {
						year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE;
					}else if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.MONTH)){
						year = Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month = Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+BusinessConstants.NUMBER_ONE;
					}
                    else if(StringUtils.equalsIgnoreCase(dateFormat, BusinessConstants.DAY)){
                    	year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month = Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						day = Integer.valueOf(groupEle.get(BusinessConstants.DAY).toString());
						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+day;
					}
                    else {
                    	year =Integer.valueOf(groupEle.get(BusinessConstants.YEAR).toString());
						month =Integer.valueOf(groupEle.get(BusinessConstants.MONTH).toString());
						day =Integer.valueOf(groupEle.get(BusinessConstants.DAY).toString());
						hour =Integer.valueOf(groupEle.get(BusinessConstants.HOUR).toString());
/*						trendDataKey=year+BusinessConstants.HYPHEN+month+BusinessConstants.HYPHEN+day+BusinessConstants.HYPHEN+hour;
*/						trendDataKey = AdminUtil.getDateFormatForEst(new DateTime(year, month, day, hour,00,DateTimeZone.UTC),BusinessConstants.UMG_EST_FORMAT);                    
                    }
					

					//RA USAGE TREND DATA REQUIRED FOR ALL TENANTS
					//success and fail count in particular time interval 
					
					if(raTrendData.containsKey(trendDataKey)) {
						Map<String,Long> map = (Map<String, Long>) raTrendData.get(trendDataKey);
						map.put(BusinessConstants.SUCCESS, map.get(BusinessConstants.SUCCESS)+successCount);
						map.put(BusinessConstants.FAILURE_COUNT, map.get(BusinessConstants.FAILURE_COUNT)+(totalRcrds-successCount));
					}else {
						LOGGER.info("RA Trend Data not available in Ra trend Init for key {}",trendDataKey);
						Map<String, Long> raData = new HashMap<String, Long>();
						raData.put(BusinessConstants.SUCCESS, successCount);
						raData.put(BusinessConstants.FAILURE_COUNT, totalRcrds-successCount);
						raTrendData.put(trendDataKey, raData);
					}

					
					
					
					
					
					/*//this data is only for login tenant by default for dashboard screen
					if(StringUtils.equalsIgnoreCase(RequestContext.getRequestContext().TENANT_CODE, future.getKey())) {
						//MODEL USAGE TREND DATA
						//total count in particular time interval
						if(modelTrendData.containsKey(key)) {
							Map<String,Object> modelmap = (Map<String, Object>)modelTrendData.get(key);
							modelmap.put(trendDataKey,(Long)modelmap.get(trendDataKey)+totalRcrds);
					}else {
						//Trend data key is the particular time having total txn
						modelData.put(trendDataKey, totalRcrds);
						//Unique Model at the particular time having total rcrd
						modelTrendData.put(key, modelData);
					}
					
					//TENANT TXN METRICS

					
					
					if(usagePattern.containsKey(key+modelVersion)){
						usagePatternData.put(BusinessConstants.TOTAL, (Long)usagePatternData.get(BusinessConstants.TOTAL)+totalRcrds);
						usagePatternData.put(BusinessConstants.SUCCESS, (Long)usagePatternData.get(BusinessConstants.SUCCESS)+successCount);
						usagePatternData.put(BusinessConstants.MODEL_FAILURES, (Long)usagePatternData.get(BusinessConstants.MODEL_FAILURES)+modelFailCount);
						usagePatternData.put(BusinessConstants.INPUT_VALIDATION_FAILURES, (Long)usagePatternData.get(BusinessConstants.INPUT_VALIDATION_FAILURES)+inputValCount);
						usagePatternData.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, (Long)usagePatternData.get(BusinessConstants.OUTPUT_VALIDATION_FAILURES)+outputValCount);
						usagePatternData.put(BusinessConstants.MODEL_RESPONSE_TIME, ((List<Long>)usagePatternData.get(BusinessConstants.MODEL_RESPONSE_TIME)).addAll(modelResTime));
						usagePatternData.put(BusinessConstants.END_TO_END_TIME, ((List<Long>)usagePatternData.get(BusinessConstants.END_TO_END_TIME)).addAll(endToEndRespTime));
						usagePatternData.put(BusinessConstants.MODEL_UTILISATION, (Long)usagePatternData.get(BusinessConstants.MODEL_UTILISATION)+modeletUtilization);
					}else {
						usagePatternData.put(BusinessConstants.MODEL_NAME, key);
						usagePatternData.put(BusinessConstants.MODEL_VERSION, modelVersion);
						usagePatternData.put(BusinessConstants.TOTAL, totalRcrds);
						usagePatternData.put(BusinessConstants.SUCCESS, successCount);
						usagePatternData.put(BusinessConstants.MODEL_FAILURES, modelFailCount);
						usagePatternData.put(BusinessConstants.INPUT_VALIDATION_FAILURES, inputValCount);
						usagePatternData.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, outputValCount);
						usagePatternData.put(BusinessConstants.MODEL_RESPONSE_TIME, modelResponseTime.addAll(modelResTime));
						usagePatternData.put(BusinessConstants.END_TO_END_TIME, endToendTime.addAll(endToEndRespTime));
						usagePatternData.put(BusinessConstants.MODEL_UTILISATION, modeletUtilization);
						usagePattern.put(key+modelVersion, usagePatternData);	
					}
					// set the total count for specific model
					if (aggregateResult.containsKey(key)) {
						aggregateResult.put(key, totalRcrds + aggregateResult.get(key));
					} else {
						aggregateResult.put(key, totalRcrds);
					}
					totalCountForMode+=totalRcrds;
					}*/
					// set tnt txn metrics For all tenants
					modlFail += modelFailCount;
					techFail += Long.valueOf(obj.get(BusinessConstants.OTHER_FAILURES).toString());
					inptvalfail += inputValCount;
					tntSuccsCount += successCount;
					outptValFail += outputValCount;

				}
				// set tnt txn metrics For all tenants
				tntMetricsVal.put(BusinessConstants.OTHER_FAILURES, techFail);
				tntMetricsVal.put(BusinessConstants.MODEL_FAILURES, modlFail);
				tntMetricsVal.put(BusinessConstants.INPUT_VALIDATION_FAILURES, inptvalfail);
				tntMetricsVal.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, outptValFail);
				tntMetricsVal.put(BusinessConstants.SUCCESS, tntSuccsCount);
				tntTxnMetrics.put(future.getKey(), tntMetricsVal);
				/*
				if(StringUtils.equalsIgnoreCase(RequestContext.getRequestContext().TENANT_CODE, future.getKey())) {
				 loginTntUsageMetrics = getMaxTenTxnAndOtherTxn(aggregateResult);
				 Collections.copy(loginUsageDynamics, transactionSucFailCnt);
				}
				*/
			}
		}
		
		/*
		for(Entry<String, Object> entry:usagePattern.entrySet()) {
			Map<String,Object> map = (Map<String, Object>) entry.getValue();
			
			//90% MODEL RESPONSE TIME
			List<Long> modelResTime =(List<Long>) map.get(BusinessConstants.MODEL_RESPONSE_TIME);
			Collections.sort(modelResTime);
			Long prefix = (modelResTime.get((int) Math.round((modelResTime.size() - 1) * (0.9))))
					/ BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
			Long sufix = (modelResTime.get((int) Math.round((modelResTime.size() - 1) * (0.9))))
					% BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
			
			map.put(BusinessConstants.MODEL_RESPONSE_TIME, prefix+BusinessConstants.DOT+sufix);
			
			//90% END TO END RESPONSE TIME
			List<Long> endToEndTime =(List<Long>) map.get(BusinessConstants.END_TO_END_TIME);

			Collections.sort(endToEndTime);

			Long endprefix = (endToEndTime.get((int) Math.round((endToEndTime.size() - 1) * (0.9))))
					/ BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
			Long endsufix = (endToEndTime.get((int) Math.round((endToEndTime.size() - 1) * (0.9))))
					% BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
			
			map.put(BusinessConstants.END_TO_END_TIME, endprefix+BusinessConstants.DOT+endsufix);
			
			//MODEL UTILIZATION TIME
			Long modeletUtilization = (Long) map.get(BusinessConstants.MODEL_UTILISATION);
			Long utlprefix = modeletUtilization / BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
			Long ultsufix = modeletUtilization % BusinessConstants.LONG_NUMBER_ONE_THOUSAND;
			map.put(BusinessConstants.MODEL_UTILISATION,utlprefix+BusinessConstants.DOT+ultsufix);
		}
		
		

		// get the top ten total count models
		getMaxTenTxnAndOtherTxn(result,aggregateResult,modelTrendData,totalCountForMode);
*/

/*		result.put(BusinessConstants.USAGE_DYNAMICS, usagePattern);
*//*     	result.put(BusinessConstants.USAGE_MATRICS, usageMetrics);
*/		result.put(BusinessConstants.TENANT_TXN_MATRICS, tntTxnMetrics);
		result.put(BusinessConstants.RA_USAGE_TRENDLINE, raTrendData);
/*		result.put(BusinessConstants.MODEL_USAGE_TRENDLINE, modelTrendData);
*/


        /*loginTntData.put(BusinessConstants.USAGE_DYNAMICS, loginUsageDynamics);
        loginTntData.put(BusinessConstants.USAGE_MATRICS, loginTntUsageMetrics);
        result.put(BusinessConstants.LOGIN_TENANT,loginTntData);*/

		return result;
	}
	
	
	
	
	
	
	
	
	class TopHundredTranRowMapper implements RowMapper<ModelVersionStatus> {
		@Override
		public ModelVersionStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
			ModelVersionStatus modelVerStatus = new ModelVersionStatus();
			modelVerStatus.setTransactionId(rs.getString("ID"));
			modelVerStatus.setClientTransactionId(rs.getString("CLIENT_TRANSACTION_ID"));
			modelVerStatus.setModelName(rs.getString("VERSION_NAME"));
			modelVerStatus.setTransactionMode(rs.getString("TRANSACTION_MODE"));
			modelVerStatus.setModelVersion(
					rs.getString("MAJOR_VERSION") + BusinessConstants.DOT + rs.getString("MINOR_VERSION"));
			modelVerStatus.setErrorCode(rs.getString("ERROR_CODE"));
			modelVerStatus.setRunDate(AdminUtil.getDateFormatMillisForEst(Long.valueOf(rs.getString("RUN_AS_OF_DATE")), BusinessConstants.UMG_UTC_DATE_FORMAT));
			modelVerStatus.setErrorDescription(rs.getString("ERROR_DESCRIPTION"));
			return modelVerStatus;

		}

	}

	private class RAUsageInItTrendData implements ResultSetExtractor<Map<String, Object>> {

		public Map<String, Object> extractData(ResultSet rs) throws SQLException{
			 Map<String, Object> usageTrendDataMap = new HashMap<>();
			 List<ModelVersionStatus> loginTenant = new ArrayList<>();
			 List<ModelVersionStatus> allTenant = new ArrayList<>();
			while (rs.next()) {
				ModelVersionStatus modelVerStatus = new ModelVersionStatus();
				modelVerStatus.setModelName(rs.getString("VERSION_NAME"));
				modelVerStatus.setSuccessCount(Long.valueOf(rs.getString("SUCCESS_COUNT")));
				modelVerStatus.setFailureCount(Long.valueOf(rs.getString("FAILURE_COUNT")));
				modelVerStatus.setRunDate(rs.getString("RUN_DATE"));
				modelVerStatus.setCount(Long.valueOf(rs.getString("TOTAL_COUNT")));
				allTenant.add(modelVerStatus);
				if (StringUtils.equalsIgnoreCase(rs.getString("TENANT_ID"),
						RequestContext.getRequestContext().getTenantCode())) {
					loginTenant.add(modelVerStatus);
				}

			}
			usageTrendDataMap.put(BusinessConstants.ALL_TENANT, allTenant);
			usageTrendDataMap.put(BusinessConstants.LOGIN_TENANT, loginTenant);

			return usageTrendDataMap;

		}

	}
	
	private class RAUsageTrendData implements ResultSetExtractor<Map<String, List<ModelVersionStatus>>> {

		public Map<String, List<ModelVersionStatus>> extractData(ResultSet rs) throws SQLException{
			 Map<String, List<ModelVersionStatus>> usageTrendDataMap = new HashMap<>();
			 List<ModelVersionStatus> loginTenant = new ArrayList<>();
			 List<ModelVersionStatus> allTenant = new ArrayList<>();
			while (rs.next()) {
				ModelVersionStatus modelVerStatus = new ModelVersionStatus();
				modelVerStatus.setModelName(rs.getString("VERSION_NAME"));
				modelVerStatus.setSuccessCount(Long.valueOf(rs.getString("SUCCESS_COUNT")));
				modelVerStatus.setFailureCount(Long.valueOf(rs.getString("FAILURE_COUNT")));
				modelVerStatus.setRunDate(rs.getString("RUN_DATE"));
				modelVerStatus.setCount(Long.valueOf(rs.getString("TOTAL_COUNT")));
				allTenant.add(modelVerStatus);
			}
			usageTrendDataMap.put(BusinessConstants.ALL_TENANT, allTenant);
			usageTrendDataMap.put(BusinessConstants.LOGIN_TENANT, loginTenant);

			return usageTrendDataMap;

		}

	}

	/**
	 * gets the count for last 90 days
	 */
	public Cursor getTransactionsCnt() throws SystemException {
		/*
		 * LOGGER.info("Data Query for getVersionForTidCopy is :" +
		 * TRANSACTION_COUNT_LAST_90_DAYS); List<ModelUsageInfo> transactionCnt =
		 * jdbcTemplate.query(TRANSACTION_COUNT_LAST_90_DAYS, new Object[]
		 * {RequestContext.getRequestContext().getTenantCode()},new
		 * TranCountRowMapper());
		 */
		Long currentTime = System.currentTimeMillis();
		Long daysInMillis = TimeUnit.DAYS.toMillis(90);
		Cursor aggregationOutput = mongoTransactionDAO.fetchTransactionCount(currentTime - daysInMillis);
		return aggregationOutput;
	}

	public List<ModelVersionStatus> getFailTxn(Long runAsOfDateFrom, Long runAsOfDateTo, String[] tenantList, String limit)
			throws SystemException {

		// prepare the string of tenant name
		StringBuilder param = new StringBuilder();
		List<Object> queryParam = new ArrayList<>();
		for (String tnt : tenantList) {
			param.append("?,");
			queryParam.add(tnt.trim());
		}
		queryParam.add(runAsOfDateFrom);
		queryParam.add(runAsOfDateTo);

		param.deleteCharAt(param.length() - 1);

		String query = TOP_100_FAILURE_TXN.replace("#", param.toString()).replace("TXLIMIT", limit);
		List<ModelVersionStatus> failTransaction = jdbcTemplate.query(query,queryParam.toArray(),
				new TopHundredTranRowMapper());

		return failTransaction;
	}

	public Map<String, Object> getRaUsageTrendData(Long runAsOfDateFrom, Long runAsOfDateTo, String[] tenantList, String groupBy)
	{

		// prepare the string of tenant name
		StringBuilder param = new StringBuilder();
		List<Object> queryParam = new ArrayList<>();
		for (String tnt : tenantList) {
			param.append("?,");
			queryParam.add(tnt.trim());
		}
		queryParam.add(runAsOfDateFrom);
		queryParam.add(runAsOfDateTo);
		
		param.deleteCharAt(param.length() - 1);

		String query = RA_USAGE_TREND_DATA_INIT.replace("#", param.toString()).replaceAll("DATEFORMAT", groupBy);
		LOGGER.info("RA USAGE TREND DATA QUERY "+query);
		long startTime =  System.currentTimeMillis();
		Map<String, Object> failTransaction = jdbcTemplate.query(query, queryParam.toArray(), new RAUsageInItTrendData());
        LOGGER.info("Time taken to fetch the Trend data from mysql {}",System.currentTimeMillis()-startTime);
		return failTransaction;
	}

	public Map<String, Object> getRaUsageInItTrendData(Long runAsOfDateFrom, Long runAsOfDateTo, String[] tenantList)
	{

		// prepare the string of tenant name
		StringBuilder param = new StringBuilder();
		List<Object> queryParam = new ArrayList<>();
		for (String tnt : tenantList) {
			param.append("?,");
			queryParam.add(tnt.trim());
		}
		queryParam.add(runAsOfDateFrom);
		queryParam.add(runAsOfDateTo);

		param.deleteCharAt(param.length() - 1);
		Map<String, Object> raUsageTrendData = jdbcTemplate.query(
				RA_USAGE_TREND_DATA_INIT.replace("#", param.toString()), queryParam.toArray(), new RAUsageInItTrendData());

		return raUsageTrendData;
	}
	class TranCountRowMapper implements RowMapper<ModelUsageInfo> {
		@Override
		public ModelUsageInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			ModelUsageInfo modelUsageInfo = new ModelUsageInfo();
			modelUsageInfo.setModelName(rs.getString("VERSION_NAME"));
			modelUsageInfo.setTransactionCount(rs.getLong("COUNT"));
			modelUsageInfo.setInterval(rs.getString("MONTH_NAME"));
			return modelUsageInfo;

		}

	}

}
