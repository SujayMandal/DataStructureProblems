package com.fa.dp.business.db.client;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.NumberUtils;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.rr.rtng.constant.HubzuDBConstant;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

/**
 * @author misprakh
 */
@Slf4j
@Named
public class SopHubzuDBClient {

	@Value("${SOPWEEKN_CONCURRENT_DBCALL_POOL_SIZE}")
	private int concurrentSopWeekNDbCallPoolSize;

	@Value("${SOP_WEEKN_HUBZU_QUERY_IN_CLAUSE_COUNT}")
	private int initialQueryInClauseCount;

	@Inject
	private CacheManager cacheManager;

	@Inject
	@Named(value = "hubzuDataSource")
	private DataSource dataSource;

	@Inject
	private RRMigration rRMigration;

	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedJdbcTemplate;

	private ExecutorService executorService;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentSopWeekNDbCallPoolSize);
	}

	@PreDestroy
	public void destroy() {
		if(executorService != null) {
			executorService.shutdown();
		}
	}

	/**
	 * @param topRowsHubzuResponse
	 * @param hubzuQuery
	 * @param isFetchProcess
	 * @param migrationNewPropToPropMap
	 * @param listEndDateDtNnEnd
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	public HubzuDBResponse retrieveHubzuAllRowsSOPWeekN(HubzuDBResponse topRowsHubzuResponse, Map<String, String> hubzuQuery, Boolean isFetchProcess,
			Map<String, String> migrationNewPropToPropMap, String listEndDateDtNnEnd) throws SystemException {
		final HubzuDBResponse hubzuResponseAllRows = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfosAllRows = new ArrayList<>();
		hubzuResponseAllRows.setHubzuInfos(hubzuInfosAllRows);
		log.info("Enter HubzuDBClient :: method retrieveHubzuAllRowsSOPWeekN");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);
		log.info("SOP WeekN - Query to fetch all rows from hubzu: {} for Hubzu Integration Type : {}", selectQuery, integrationType);
		List<List<HubzuInfo>> splitListHubzuInfos = ListUtils.partition(topRowsHubzuResponse.getHubzuInfos(), initialQueryInClauseCount);
		List<Future<HubzuDBResponse>> futureList = new ArrayList<>();
		for (List<HubzuInfo> subHubzuInfoList : splitListHubzuInfos) {
			Future<HubzuDBResponse> hubzuRespFuture = executorService
					.submit(retrieveHubzuAllRowsInSOPWeekNForPropIds(selectQuery, integrationType, subHubzuInfoList, migrationNewPropToPropMap,
							listEndDateDtNnEnd));
			futureList.add(hubzuRespFuture);
		}
		for (Future<HubzuDBResponse> hubzuDBResponseFuture : futureList) {
			HubzuDBResponse intermediateHubzuRes = null;
			try {
				intermediateHubzuRes = hubzuDBResponseFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("There is an error while getting response from hubzu table.{}", e);
				SystemException.newSystemException(CoreExceptionCodes.DPSOPWKN015, e.getMessage());
			}
			hubzuResponseAllRows.getHubzuInfos().addAll(intermediateHubzuRes.getHubzuInfos());
		}
		log.info("Exit HubzuDBClient :: method retrieveHubzuAllRowsSOPWeekN");
		return hubzuResponseAllRows;
	}

	/**
	 * @param dpSopWeekNParamInfo
	 * @param hubzuQuery
	 * @param isFetchProcess
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	public HubzuDBResponse fetchSOPWeekNHubzuData(DPSopWeekNParamInfo dpSopWeekNParamInfo, Map<String, String> hubzuQuery, Boolean isFetchProcess)
			throws SystemException {
		HubzuDBResponse hubzuRes = null;
		log.info("Enter HubzuDBClient :: method fetchSOPWeekNHubzuData");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);
		long startTime = 0;
		//		try {
		List<String> assetList = new ArrayList<>();
		assetList.add(dpSopWeekNParamInfo.getPropTemp());
		if(dpSopWeekNParamInfo.getOldAssetNumber() != null) {
			List<String> oldAssetNumbers = new ArrayList<String>();
			oldAssetNumbers.add(dpSopWeekNParamInfo.getOldAssetNumber());
			String rrMigrationLoanNumQuery = (String) cacheManager
					.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY);
			String oldPropTemp = rRMigration.getPropTemps(rrMigrationLoanNumQuery, oldAssetNumbers).get(0);
			log.info("Old prop temp from RR migration query: {}", oldPropTemp);
			if(oldPropTemp != null)
				assetList.add(oldPropTemp);
		}
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		log.info("Query to fetch Hubzu records is: {},  for integration type : {}", selectQuery, integrationType);
		parameters.addValue("idList", assetList);
		startTime = System.currentTimeMillis();
		try {
			if(StringUtils.equalsIgnoreCase(integrationType, IntegrationType.HUBZU_SOP_INTEGRATION.getIntegrationType())) {
				SimpleDateFormat yyyyMMdd = new SimpleDateFormat(DateConversionUtil.DATE_DD_MMM_YY);
				SimpleDateFormat parseFormat = new SimpleDateFormat(RAClientConstants.DATE_FORMAT);
				String listEndDate = null;
				if(isFetchProcess) {
					if(StringUtils.isNotEmpty(dpSopWeekNParamInfo.getMostRecentListEndDate())) {
						listEndDate = yyyyMMdd.format(parseFormat.parse(dpSopWeekNParamInfo.getMostRecentListEndDate())).toUpperCase();
						log.info("List end date {} for loan  number {}", listEndDate, assetList);
						selectQuery = StringUtils
								.replace(selectQuery, DPAConstants.LIST_END_DATE_DT_NN_CONDITION, DPAConstants.REPLACED_LIST_END_DATE_CONDITION);
						parameters.addValue("endDateCondition", listEndDate);
					} else {
						selectQuery = StringUtils.replace(selectQuery, DPAConstants.LIST_END_DATE_DT_NN_CONDITION, "");
					}
				} else {
					listEndDate = DateConversionUtil.getCurrentEstDate().toString(DateConversionUtil.DATE_DD_MMM_YY);
					log.info("List end date {} for loan  number {}", listEndDate, assetList);
					selectQuery = StringUtils.replace(selectQuery, DPAConstants.LIST_END_DATE_DT_NN_CONDITION,
							DPAConstants.REPLACED_LIST_END_DATE_CONDITION_WITHOUT_EQUALS);
					parameters.addValue("endDateCondition", listEndDate);
				}
				hubzuRes = namedJdbcTemplate.execute(selectQuery, parameters, (PreparedStatementCallback<HubzuDBResponse>) ps -> {
					log.info("retrieving loans from  {} to {}  ", dpSopWeekNParamInfo.getListStrtDateDtNn(), dpSopWeekNParamInfo.getListEndDateDtNn());
					return createSopWeekNHubzuInfo(ps.executeQuery(), dpSopWeekNParamInfo, integrationType);
				});
			} else {
				hubzuRes = jdbcTemplate.execute(selectQuery, (PreparedStatementCallback<HubzuDBResponse>) ps -> {
					log.info("retrieving loans from  {} to {}  ", dpSopWeekNParamInfo.getListStrtDateDtNn(), dpSopWeekNParamInfo.getListEndDateDtNn());
					if(StringUtils.equalsIgnoreCase(integrationType, IntegrationType.SOP_WEEKN_HUBZU_TOP_ROWS_INTEGRATION.getIntegrationType())) {
						ps.setString(1, dpSopWeekNParamInfo.getListStrtDateDtNn());
						ps.setString(2, dpSopWeekNParamInfo.getListEndDateDtNn());
					}
					return createSopWeekNHubzuInfo(ps.executeQuery(), dpSopWeekNParamInfo, integrationType);
				});
			}
			log.info("Time taken for executing hubzu query is : {}ms", (System.currentTimeMillis() - startTime));
		} catch (DataAccessException dae) {
			log.error("Error while Executing Hubzu query DataAccessException", dae);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN022, dae.getMessage());
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : ", e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN023, e.getMessage());
		}
		log.info("Exit HubzuDBClient :: method fetchSOPWeekNHubzuData");
		return hubzuRes;
	}

	/**
	 * @param priorRecommendedEntries
	 * @param hubzuQuery
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	public HubzuDBResponse sopWeekNHubzuAllRecordsForPriorRecommendation(List<DPSopWeekNParam> priorRecommendedEntries,
			Map<String, String> hubzuQuery) throws SystemException {
		final HubzuDBResponse hubzuResponseAllRows = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfosAllRows = new ArrayList<>();
		hubzuResponseAllRows.setHubzuInfos(hubzuInfosAllRows);
		log.info("Enter HubzuDBClient :: method sopWeekNHubzuAllRecordsForPriorRecommendation");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);

		log.info("Query to fetch all rows from Hubzu for prior recommended records : {}, with Integration Type :", selectQuery, integrationType);
		List<List<DPSopWeekNParam>> splitListPriorRecommendedEntries = ListUtils.partition(priorRecommendedEntries, initialQueryInClauseCount);
		List<Future<HubzuDBResponse>> futureList = new ArrayList<>();
		/*splitListPriorRecommendedEntries.parallelStream().forEach(sublist -> {
			HubzuDBResponse intermediateHubzuRes = sopWeeknPriorRecommendationForPropIds(selectQuery, integrationType, sublist);
			hubzuResponseAllRows.getHubzuInfos().addAll(intermediateHubzuRes.getHubzuInfos());
		});*/

		for (List<DPSopWeekNParam> subListPriorRecommendedEntries : splitListPriorRecommendedEntries) {
			Future<HubzuDBResponse> hubzuRespFuture = executorService
					.submit(sopWeeknPriorRecommendationForPropIds(selectQuery, integrationType, subListPriorRecommendedEntries));
			futureList.add(hubzuRespFuture);
		}

		for (Future<HubzuDBResponse> hubzuDBResponseFuture : futureList) {
			try {
				HubzuDBResponse intermediateHubzuRes = hubzuDBResponseFuture.get();
				hubzuResponseAllRows.getHubzuInfos().addAll(intermediateHubzuRes.getHubzuInfos());
			} catch (InterruptedException | ExecutionException e) {
				log.error("There is error while getting response from hubzu table.{}", e);
				SystemException.newSystemException(CoreExceptionCodes.DPSOPWKN015, e.getMessage());
			}
		}
		log.info("Exit HubzuDBClient :: method fetchHubzuData");
		return hubzuResponseAllRows;
	}

	/**
	 * @param sopWeekNParamInfo
	 * @param hubzuQuery
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	public HubzuDBResponse getTopHubzuSuccessUnderReviewQueryOutput(DPSopWeekNParamInfo sopWeekNParamInfo, Map<String, String> hubzuQuery)
			throws SystemException {
		log.info("Enter HubzuDBClient :: method getTopHubzuSuccessUnderReviewQueryOutput");
		HubzuDBResponse hubzuRes = new HubzuDBResponse();
		List<String> assetList = new ArrayList<>();
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);
		try {
			log.info("Query to fetch Hubzu Data :{} for Hubzu Integration Type :{}", selectQuery, integrationType);
			//dp-331
			assetList.add(sopWeekNParamInfo.getPropTemp());
			if(sopWeekNParamInfo.getOldAssetNumber() != null) {
				List<String> oldAssetNumbers = new ArrayList<String>();
				oldAssetNumbers.add(sopWeekNParamInfo.getOldAssetNumber());
				String rrMigrationLoanNumQuery = (String) cacheManager
						.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY);
				List<String> props = rRMigration.getPropTemps(rrMigrationLoanNumQuery, oldAssetNumbers);
				if(props != null && props.size() > 0) {
					String oldPropTemp = rRMigration.getPropTemps(rrMigrationLoanNumQuery, oldAssetNumbers).get(0);
					if(oldPropTemp != null) {
						assetList.add(oldPropTemp);
					}
				}
			}
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("idList", assetList);
			hubzuRes = namedJdbcTemplate.execute(selectQuery, parameters, ps -> createSopWeekNHubzuInfo(ps.executeQuery(), sopWeekNParamInfo, integrationType));

		} catch (DataAccessException dae) {
			log.error("Error while Executing Hubzu query DataAccessException", dae);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN020, dae.getMessage());
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : ", e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN017, e.getMessage());
		}
		log.info("Exit HubzuDBClient :: method getTopHubzuSuccessUnderReviewQueryOutput");
		return hubzuRes;
	}

	/**
	 * @param sopWeekNParamInfo
	 * @param hubzuQuery
	 * @param isFetchProcess
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	public HubzuDBResponse getAllHubzuSuccessUnderReviewQueryOutput(DPSopWeekNParamInfo sopWeekNParamInfo, Map<String, String> hubzuQuery,
			Boolean isFetchProcess) throws SystemException {
		HubzuDBResponse hubzuRes = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfosAllRows = new ArrayList<>();
		hubzuRes.setHubzuInfos(hubzuInfosAllRows);
		List<String> assetList = new ArrayList<>();
		log.info("Enter HubzuDBClient :: method getAllHubzuSuccessUnderReviewQueryOutput");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);
		long startTime = 0;
		try {
			log.info("Query to fetch Hubzu Data :{} for Hubzu Integration Type :", selectQuery, integrationType);
			//dp-331
			assetList.add(sopWeekNParamInfo.getPropTemp());
			if(sopWeekNParamInfo.getOldAssetNumber() != null) {
				List<String> oldAssetNumbers = new ArrayList<String>();
				oldAssetNumbers.add(sopWeekNParamInfo.getOldAssetNumber());
				String rrMigrationLoanNumQuery = (String) cacheManager
						.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY);
				String oldPropTemp = rRMigration.getPropTemps(rrMigrationLoanNumQuery, oldAssetNumbers).get(0);
				if(oldPropTemp != null) {
					assetList.add(oldPropTemp);
				}
			}

			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("idList", assetList);
			startTime = System.currentTimeMillis();
			hubzuRes = namedJdbcTemplate.execute(selectQuery, parameters, ps -> createSopWeekNHubzuInfo(ps.executeQuery(), sopWeekNParamInfo, integrationType));
			log.info("Time taken for success and under review query for all records : {}ms", (DateTime.now().getMillis() - startTime));
		} catch (DataAccessException dae) {
			log.error("Error while Executing Hubzu query DataAccessException", dae);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN020, dae.getMessage());
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : ", e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN021, e.getMessage());
		}
		log.info("Exit HubzuDBClient :: method getAllHubzuSuccessUnderReviewQueryOutput");
		return hubzuRes;
	}

	/**
	 * @param selectQuery
	 * @param integrationType
	 * @param subListPriorRecommendedEntries
	 *
	 * @return
	 */
	private Callable<HubzuDBResponse> sopWeeknPriorRecommendationForPropIds(final String selectQuery, String integrationType,
			final List<DPSopWeekNParam> subListPriorRecommendedEntries) {
		return () -> {
			DPSopWeekNParamInfo sopWeekNParamInfo = new DPSopWeekNParamInfo();
			List<String> rbidFKList = new ArrayList<>();
			// including old loan for new loan numbers
			subListPriorRecommendedEntries.stream().forEach(priorRecommendedEntry -> {
				String propTemp = priorRecommendedEntry.getPropTemp();
				rbidFKList.add((StringUtils.equalsIgnoreCase(priorRecommendedEntry.getClassification(), DPAConstants.NRZ) ?
						DPAConstants.NRZ_ACNT_ID :
						StringUtils.equalsIgnoreCase(priorRecommendedEntry.getClassification(), DPAConstants.OCN) ?
								DPAConstants.OCN_ACNT_ID :
								DPAConstants.PHH_ACNT_ID) + propTemp);
			});
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("idList", rbidFKList);

			HubzuDBResponse intermediateHubzuRes = namedJdbcTemplate.execute(selectQuery, parameters, ps -> {
				log.info("Inside createSopWeekNHubzuInfo.");
				return createSopWeekNHubzuInfo(ps.executeQuery(), sopWeekNParamInfo, integrationType);

			});
			return intermediateHubzuRes;
		};
	}

	/**
	 * @param rs
	 * @param dpSopWeekNParamInfo
	 * @param integrationType
	 *
	 * @return
	 *
	 * @throws SQLException
	 */
	private HubzuDBResponse createSopWeekNHubzuInfo(final ResultSet rs, DPSopWeekNParamInfo dpSopWeekNParamInfo, String integrationType)
			throws SQLException {
		log.info("Enter HubzuDBClient :: method createSopWeekNHubzuInfo");
		HubzuDBResponse hubzuRes = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfos = new ArrayList<>();
		while (rs.next()) {
			if(rs.getRow() > 0) {
				HubzuInfo hubzuInfo = convertToHubzuInfo(rs);
				hubzuInfos.add(hubzuInfo);
			}
		}
		hubzuRes.setHubzuInfos(hubzuInfos);
		log.info("Exit HubzuDBClient :: method createSopWeekNHubzuInfo");
		return hubzuRes;
	}

	/**
	 * @param rs
	 *
	 * @return
	 *
	 * @throws SQLException
	 */
	private HubzuInfo convertToHubzuInfo(final ResultSet rs) throws SQLException {
		HubzuInfo hubzuInfo = new HubzuInfo();
		ResultSetMetaData metaData = rs.getMetaData();
		int count = metaData.getColumnCount(); // number of column
		List<String> fetchColumns = new ArrayList<>(count);

		for (int i = 1; i <= count; i++) {
			fetchColumns.add(metaData.getColumnLabel(i));
		}

		hubzuInfo.setSelrPropIdVcNn(
				fetchColumns.contains(HubzuDBConstant.SELR_PROP_ID_VC_NN) ? rs.getString(HubzuDBConstant.SELR_PROP_ID_VC_NN) : null);
		hubzuInfo.setClntCodeVc(fetchColumns.contains(HubzuDBConstant.CLNT_CODE_VC) ? rs.getString(HubzuDBConstant.CLNT_CODE_VC) : null);
		hubzuInfo.setIsSpclHndlPropVc(
				fetchColumns.contains(HubzuDBConstant.IS_SPCL_HNDL_PROP_VC) ? rs.getString(HubzuDBConstant.IS_SPCL_HNDL_PROP_VC) : null);
		hubzuInfo.setSelrAcntIdVcFk(
				fetchColumns.contains(HubzuDBConstant.SELR_ACNT_ID_VC_FK) ? rs.getString(HubzuDBConstant.SELR_ACNT_ID_VC_FK) : null);
		hubzuInfo.setRbidPropIdVcPk(
				fetchColumns.contains(HubzuDBConstant.RBID_PROP_ID_VC_PK) ? rs.getString(HubzuDBConstant.RBID_PROP_ID_VC_PK) : null);
		hubzuInfo.setAddress(fetchColumns.contains(HubzuDBConstant.ADDRESS) ? rs.getString(HubzuDBConstant.ADDRESS) : null);
		hubzuInfo.setPropCityVcFk(fetchColumns.contains(HubzuDBConstant.PROP_CITY_VC_FK) ? rs.getString(HubzuDBConstant.PROP_CITY_VC_FK) : null);
		hubzuInfo.setPropStatIdVcFk(
				fetchColumns.contains(HubzuDBConstant.PROP_STAT_ID_VC_FK) ? rs.getString(HubzuDBConstant.PROP_STAT_ID_VC_FK) : null);
		hubzuInfo.setPropZipVcFk(fetchColumns.contains(HubzuDBConstant.PROP_ZIP_VC_FK) ? rs.getString(HubzuDBConstant.PROP_ZIP_VC_FK) : null);
		hubzuInfo.setPropCntyVc(fetchColumns.contains(HubzuDBConstant.PROP_CNTY_VC) ? rs.getString(HubzuDBConstant.PROP_CNTY_VC) : null);
		hubzuInfo.setPropSubTypeIdVcFk(
				fetchColumns.contains(HubzuDBConstant.PROP_SUB_TYPE_ID_VC_FK) ? rs.getString(HubzuDBConstant.PROP_SUB_TYPE_ID_VC_FK) : null);
		hubzuInfo
				.setAreaSqurFeetNm(fetchColumns.contains(HubzuDBConstant.AREA_SQUR_FEET_NM) ? rs.getString(HubzuDBConstant.AREA_SQUR_FEET_NM) : null);
		hubzuInfo.setLotSizeVc(fetchColumns.contains(HubzuDBConstant.LOT_SIZE_VC) ? rs.getString(HubzuDBConstant.LOT_SIZE_VC) : null);
		hubzuInfo.setBdrmCntNt(fetchColumns.contains(HubzuDBConstant.BDRM_CNT_NT) ? rs.getString(HubzuDBConstant.BDRM_CNT_NT) : null);
		hubzuInfo.setBtrmCntNm(fetchColumns.contains(HubzuDBConstant.BTRM_CNT_NM) ? rs.getString(HubzuDBConstant.BTRM_CNT_NM) : null);
		hubzuInfo.setTotlRoomCntNm(fetchColumns.contains(HubzuDBConstant.TOTL_ROOM_CNT_NM) ? rs.getString(HubzuDBConstant.TOTL_ROOM_CNT_NM) : null);
		hubzuInfo.setBuldDateDt(fetchColumns.contains(HubzuDBConstant.BULD_DATE_DT) ? rs.getString(HubzuDBConstant.BULD_DATE_DT) : null);
		hubzuInfo.setReprValuNt(fetchColumns.contains(HubzuDBConstant.REPR_VALU_NT) ? rs.getString(HubzuDBConstant.REPR_VALU_NT) : null);
		hubzuInfo.setReoPropSttsVc(fetchColumns.contains(HubzuDBConstant.REO_PROP_STTS_VC) ? rs.getString(HubzuDBConstant.REO_PROP_STTS_VC) : null);
		hubzuInfo.setReoDateDt(fetchColumns.contains(HubzuDBConstant.REO_DATE_DT) ? rs.getString(HubzuDBConstant.REO_DATE_DT) : null);
		hubzuInfo
				.setPropSoldDateDt(fetchColumns.contains(HubzuDBConstant.PROP_SOLD_DATE_DT) ? rs.getString(HubzuDBConstant.PROP_SOLD_DATE_DT) : null);
		hubzuInfo.setPropSttsIdVcFk(
				fetchColumns.contains(HubzuDBConstant.PROP_STTS_ID_VC_FK) ? rs.getString(HubzuDBConstant.PROP_STTS_ID_VC_FK) : null);
		hubzuInfo.setRbidPropListIdVcPk(
				fetchColumns.contains(HubzuDBConstant.RBID_PROP_LIST_ID_VC_PK) ? rs.getString(HubzuDBConstant.RBID_PROP_LIST_ID_VC_PK) : null);
		hubzuInfo.setListTypeIdVcFk(
				fetchColumns.contains(HubzuDBConstant.LIST_TYPE_ID_VC_FK) ? rs.getString(HubzuDBConstant.LIST_TYPE_ID_VC_FK) : null);
		hubzuInfo.setRbidPropIdVcFk(
				fetchColumns.contains(HubzuDBConstant.RBID_PROP_ID_VC_FK) ? rs.getString(HubzuDBConstant.RBID_PROP_ID_VC_FK) : null);
		hubzuInfo.setListAtmpNumbNtNn(
				fetchColumns.contains(HubzuDBConstant.LIST_ATMP_NUMB_NT_NN) ? rs.getString(HubzuDBConstant.LIST_ATMP_NUMB_NT_NN) : null);
		hubzuInfo.setCurrentListStrtDate(
				fetchColumns.contains(HubzuDBConstant.CURRENT_LIST_STRT_DATE) ? rs.getString(HubzuDBConstant.CURRENT_LIST_STRT_DATE) : null);
		hubzuInfo.setCurrentListEndDate(
				fetchColumns.contains(HubzuDBConstant.CURRENT_LIST_END_DATE) ? rs.getString(HubzuDBConstant.CURRENT_LIST_END_DATE) : null);
		hubzuInfo
				.setListSttsDtlsVc(fetchColumns.contains(HubzuDBConstant.LIST_STTS_DTLS_VC) ? rs.getString(HubzuDBConstant.LIST_STTS_DTLS_VC) : null);
		hubzuInfo.setPropertySold(fetchColumns.contains(HubzuDBConstant.PROPERTY_SOLD) ? rs.getString(HubzuDBConstant.PROPERTY_SOLD) : null);
		hubzuInfo.setActvAutoBid(fetchColumns.contains(HubzuDBConstant.ACTV_AUTO_BID) ? rs.getString(HubzuDBConstant.ACTV_AUTO_BID) : null);
		hubzuInfo.setCurrentRsrvPrceNt(
				fetchColumns.contains(HubzuDBConstant.CURRENT_RSRV_PRCE_NT) ? rs.getString(HubzuDBConstant.CURRENT_RSRV_PRCE_NT) : null);
		hubzuInfo.setCurrentListPrceNt(
				fetchColumns.contains(HubzuDBConstant.CURRENT_LIST_PRCE_NT) ? rs.getString(HubzuDBConstant.CURRENT_LIST_PRCE_NT) : null);
		hubzuInfo.setHgstBidAmntNt(fetchColumns.contains(HubzuDBConstant.HGST_BID_AMNT_NT) ? rs.getString(HubzuDBConstant.HGST_BID_AMNT_NT) : null);
		hubzuInfo.setMinmBidAmntNt(fetchColumns.contains(HubzuDBConstant.MINM_BID_AMNT_NT) ? rs.getString(HubzuDBConstant.MINM_BID_AMNT_NT) : null);
		hubzuInfo.setOccpncySttsAtLstCreatn(
				fetchColumns.contains(HubzuDBConstant.OCCPNCY_STTS_AT_LST_CREATN) ? rs.getString(HubzuDBConstant.OCCPNCY_STTS_AT_LST_CREATN) : null);
		hubzuInfo.setSopProgramStatus(
				fetchColumns.contains(HubzuDBConstant.SOP_PROGRAM_STATUS) ? rs.getString(HubzuDBConstant.SOP_PROGRAM_STATUS) : null);
		hubzuInfo.setIsStatHotVc(fetchColumns.contains(HubzuDBConstant.IS_STAT_HOT_VC) ? rs.getString(HubzuDBConstant.IS_STAT_HOT_VC) : null);
		hubzuInfo.setBuyItNowPrceNt(
				fetchColumns.contains(HubzuDBConstant.BUY_IT_NOW_PRCE_NT) ? rs.getString(HubzuDBConstant.BUY_IT_NOW_PRCE_NT) : null);
		hubzuInfo.setRsrvPrceMetVc(fetchColumns.contains(HubzuDBConstant.RSRV_PRCE_MET_VC) ? rs.getString(HubzuDBConstant.RSRV_PRCE_MET_VC) : null);
		hubzuInfo.setFallOutResnVc(fetchColumns.contains(HubzuDBConstant.FALL_OUT_RESN_VC) ? rs.getString(HubzuDBConstant.FALL_OUT_RESN_VC) : null);
		hubzuInfo.setFallOutDateDt(fetchColumns.contains(HubzuDBConstant.FALL_OUT_DATE_DT) ? rs.getString(HubzuDBConstant.FALL_OUT_DATE_DT) : null);
		hubzuInfo.setFinancialConsideredIndicator(fetchColumns.contains(HubzuDBConstant.FINANCIAL_CONSIDERED_INDICATOR) ?
				rs.getString(HubzuDBConstant.FINANCIAL_CONSIDERED_INDICATOR) :
				null);
		hubzuInfo.setCashOnlyIndicator(
				fetchColumns.contains(HubzuDBConstant.CASH_ONLY_INDICATOR) ? rs.getString(HubzuDBConstant.CASH_ONLY_INDICATOR) : null);
		hubzuInfo.setPropBiddingNumbids(
				fetchColumns.contains(HubzuDBConstant.PROP_BIDDING_NUMBIDS) ? rs.getString(HubzuDBConstant.PROP_BIDDING_NUMBIDS) : null);
		hubzuInfo.setPropBiddingDistinctBidders(fetchColumns.contains(HubzuDBConstant.PROP_BIDDING_DISTINCT_BIDDERS) ?
				rs.getString(HubzuDBConstant.PROP_BIDDING_DISTINCT_BIDDERS) :
				null);
		hubzuInfo.setPropBiddingMaxBid(
				fetchColumns.contains(HubzuDBConstant.PROP_BIDDING_MAX_BID) ? rs.getString(HubzuDBConstant.PROP_BIDDING_MAX_BID) : null);
		hubzuInfo.setPropBiddingMinBid(
				fetchColumns.contains(HubzuDBConstant.PROP_BIDDING_MIN_BID) ? rs.getString(HubzuDBConstant.PROP_BIDDING_MIN_BID) : null);
		hubzuInfo.setTotalNoViews(fetchColumns.contains(HubzuDBConstant.TOTAL_NO_VIEWS) ? rs.getString(HubzuDBConstant.TOTAL_NO_VIEWS) : null);
		hubzuInfo.setPropBiddingDstnctWtchlst(fetchColumns.contains(HubzuDBConstant.PROP_BIDDING_DSTNCT_WTCHLST) ?
				rs.getString(HubzuDBConstant.PROP_BIDDING_DSTNCT_WTCHLST) :
				null);
		hubzuInfo.setListStrtDateDtNn(
				fetchColumns.contains(HubzuDBConstant.LIST_STRT_DATE_DT_NN) ? rs.getDate(HubzuDBConstant.LIST_STRT_DATE_DT_NN) : null);
		hubzuInfo.setListEndDateDtNn(
				fetchColumns.contains(HubzuDBConstant.LIST_END_DATE_DT_NN) ? rs.getDate(HubzuDBConstant.LIST_END_DATE_DT_NN) : null);

		if(fetchColumns.contains(HubzuDBConstant.LIST_PRCE_NT) && org.apache.commons.lang3.math.NumberUtils
				.isDigits(rs.getString(HubzuDBConstant.LIST_PRCE_NT))) {
			hubzuInfo.setListPrceNt(NumberUtils.parseNumber(rs.getString(HubzuDBConstant.LIST_PRCE_NT), Long.class));
		}

		//hubzuInfo.setListPrceNt(fetchColumns.contains(HubzuDBConstant.LIST_PRCE_NT) ? Long.valueOf(rs.getString(HubzuDBConstant.LIST_PRCE_NT)) : null);
		hubzuInfo.setAutoRLSTVc(fetchColumns.contains(HubzuDBConstant.AUTO_RLST_VC) ? rs.getString(HubzuDBConstant.AUTO_RLST_VC) : null);

		return hubzuInfo;
	}

	/**
	 * @param selectQuery
	 * @param integrationType
	 * @param subHubzuInfoList
	 * @param migrationNewPropToPropMap
	 * @param listEndDateDtNnEnd
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	private Callable<HubzuDBResponse> retrieveHubzuAllRowsInSOPWeekNForPropIds(final String selectQuery, String integrationType,
			final List<HubzuInfo> subHubzuInfoList, final Map<String, String> migrationNewPropToPropMap, String listEndDateDtNnEnd)
			throws SystemException {
		try {
			return () -> {
				DPSopWeekNParamInfo sopWeekNParamInfo = new DPSopWeekNParamInfo();
				List<String> listOfRbidFk = new ArrayList<>();
				// including old loan for new loan numbers
				subHubzuInfoList.stream().forEach(hbzData -> {
					if(StringUtils.startsWith(hbzData.getRbidPropIdVcFk(), DPAConstants.PHH_ACNT_ID)) {
						String substr = StringUtils.substring(hbzData.getRbidPropIdVcFk(), 3);
						listOfRbidFk.add(DPAConstants.PHH_ACNT_ID + substr);
						if(migrationNewPropToPropMap.containsKey(substr)) {
							log.error("Including old propTemp " + migrationNewPropToPropMap.get(substr) + " for new propTemp : " + substr);
							listOfRbidFk.add(DPAConstants.PHH_ACNT_ID + migrationNewPropToPropMap.get(substr));
						}
					} else {
						String substr = StringUtils.substring(hbzData.getRbidPropIdVcFk(), 3);
						listOfRbidFk.add(DPAConstants.NRZ_ACNT_ID + substr);
						listOfRbidFk.add(DPAConstants.OCN_ACNT_ID + substr);
						if(migrationNewPropToPropMap.containsKey(substr)) {
							log.error("Including old propTemp " + migrationNewPropToPropMap.get(substr) + " for new propTemp : " + substr);
							listOfRbidFk.add(DPAConstants.NRZ_ACNT_ID + migrationNewPropToPropMap.get(substr));
							listOfRbidFk.add(DPAConstants.OCN_ACNT_ID + migrationNewPropToPropMap.get(substr));
						}
					}
				});
				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("idList", listOfRbidFk);
				parameters.addValue("endDate", listEndDateDtNnEnd);
				return  namedJdbcTemplate.execute(selectQuery, parameters, ps -> createSopWeekNHubzuInfo(ps.executeQuery(), sopWeekNParamInfo, integrationType));
			};
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : ", e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN023, e.getMessage());
		}
	}
	
}
