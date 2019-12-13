package com.fa.dp.business.db.client;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.info.Response;
import com.fa.dp.business.info.SSPMIInfo;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.rr.rtng.constant.HubzuDBConstant;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validator.dao.DPWeekNIntgAuditDao;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNIntgAudit;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.NumberUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Named
public class HubzuDBClient {

	@Inject
	private CacheManager cacheManager;

	@Inject
	@Named(value = "hubzuDataSource")
	private DataSource dataSource;

	@Value("${WEEKN_CONCURRENT_DBCALL_INITIAL_QUERY_POOL_SIZE}")
	private int concurrentWeekNDbCallInitialQueryPoolSize;

	@Value("${WEEKN_INITIAL_QUERY_IN_CLAUSE_COUNT}")
	private int initialQueryInClauseCount;

	@Inject
	private DPWeekNIntgAuditDao dpWeekNIntgAuditDao;

	@Inject
	private RRMigration rRMigration;

	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedJdbcTemplate;

	private ExecutorService executorService;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentWeekNDbCallInitialQueryPoolSize);
	}

	@PreDestroy
	public void destroy() {
		if(executorService != null) {
			executorService.shutdown();
		}
	}

	public HubzuDBResponse fetchQaHubzuData(DPProcessWeekNParamInfo dpProcessWeekNParamInfo, String hubzuQuery) {
		HubzuDBResponse hubzuRes = null;
		List<String> assetList = new ArrayList<>();
		assetList.add(dpProcessWeekNParamInfo.getPropTemp());
		if(dpProcessWeekNParamInfo.getOldAssetNumber() != null) {
			List<String> oldAssetNumbers = new ArrayList<String>();
			oldAssetNumbers.add(dpProcessWeekNParamInfo.getOldAssetNumber());
			String rrMigrationLoanNumQuery = (String) cacheManager
					.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY);
			String oldPropTemp = rRMigration.getPropTemps(rrMigrationLoanNumQuery, oldAssetNumbers).get(0);
			if(oldPropTemp != null) {
				assetList.add(oldPropTemp);
			}
		}
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("idList", assetList);
		hubzuQuery = StringUtils.replace(hubzuQuery, DPAConstants.LIST_END_DATE_DT_NN_CONDITION, DPAConstants.REPLACED_LIST_END_DATE_CONDITION);
		parameters.addValue("endDateCondition", dpProcessWeekNParamInfo.getMostRecentListEndDate());
		try {
			hubzuRes = namedJdbcTemplate.execute(hubzuQuery, parameters, ps -> {
				try {
					log.info("Inside doInPreparedStatement.");
					return createHubzuInfo(ps.executeQuery(), dpProcessWeekNParamInfo, IntegrationType.HUBZU_RA_INTEGRATION.getIntegrationType());
				} catch (SQLException sqle) {
					log.error("Error while Executing Hubzu query SQLException: {}", sqle);
					HubzuDBResponse hubzuRes1 = new HubzuDBResponse();
					hubzuRes1.setErrorMsg("Error while Executing Hubzu query SQLException");
					hubzuRes1.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
					return hubzuRes1;
				}
			});
		} catch (DataAccessException dae) {
			hubzuRes = prepareHubzuException(dae);
		} catch (Exception e) {
			hubzuRes = new HubzuDBResponse();
			hubzuRes.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
			hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		}
		return hubzuRes;
	}

	private HubzuDBResponse createHubzuInfo(final ResultSet rs, DPProcessWeekNParamInfo dpInfo, String integrationType) {
		log.info("Enter HubzuDBClient :: method createHubzuInfo");
		HubzuDBResponse hubzuRes = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfos = new ArrayList<>();
		Boolean recordFound = false;
		try {
			if(rs != null) {
				try {
					while (rs.next()) {
						recordFound = true;
						if(rs.getRow() > 0) {
							HubzuInfo hubzuInfo = convertToHubzuInfo(rs);
							hubzuInfos.add(hubzuInfo);
						}
					}
					if(recordFound) {
						hubzuRes.setHubzuInfos(hubzuInfos);
					} else {
						hubzuRes = new HubzuDBResponse();
						hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						hubzuRes.setErrorMsg(
								"No Record Found In Hubzu DB for " + (IntegrationType.HUBZU_RECENT_STATUS_INTEGRATION.getIntegrationType()
										.equalsIgnoreCase(integrationType) ?
												"Date In Between " + dpInfo.getListEndDateDtNnstart() + " And " + dpInfo.getListEndDateDtNnend() :
													"Loan Number :" + dpInfo.getAssetNumber()));
					}

				} catch (SQLException sqle) {
					log.error(sqle.getMessage(), sqle);
					hubzuRes = new HubzuDBResponse();
					hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
					hubzuRes.setErrorMsg("Error while setting Properties in HubzuDBResponse");
				} finally {
					if(rs != null) {
						rs.close();
					}
				}
			}

		} catch (SQLException sqle) {
			log.error("Error while closing the Result set", sqle);
			hubzuRes = new HubzuDBResponse();
			hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
			hubzuRes.setErrorMsg("Not able to close the Result set");
			/*
			 * String errorDetail =
			 * dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
			 * IntegrationType.HUBZU_INTEGRATION.getIntegrationType(),
			 * dpInfo.getErrorDetail(), sqle);
			 * dpInfo.setErrorDetail(errorDetail);
			 * dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.
			 * getValue());
			 */
		}
		log.info("Exit HubzuDBClient :: method createHubzuInfo");
		return hubzuRes;
	}

	private HubzuDBResponse prepareHubzuException(DataAccessException dae) {
		HubzuDBResponse hubzuResponse;
		log.error("Error while Executing Hubzu query DataAccessException: {}", dae);
		hubzuResponse = new HubzuDBResponse();
		hubzuResponse.setErrorMsg("Error while Executing Hubzu query DataAccessException");
		hubzuResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		return hubzuResponse;
	}

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

	public HubzuDBResponse fetchHubzuData(DPProcessWeekNParamInfo dPProcessParamInfo, Map<String, String> hubzuQuery, Boolean isFetchProcess)
			throws SystemException {
		HubzuDBResponse hubzuRes = null;
		log.info("Enter HubzuDBClient :: method fetchHubzuData");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);
		long startTime = 0;
		if(StringUtils.equalsIgnoreCase(integrationType, IntegrationType.HUBZU_RECENT_STATUS_INTEGRATION.getIntegrationType()) && CollectionUtils
				.isNotEmpty(dPProcessParamInfo.getNaLoanNumber()) && dPProcessParamInfo.getNaLoanNumber().size() > 1) {
			// executing loanNumber in clouse query so replacing the ? with
			// number of loanNumber in List with
			// ?,?,?,?
			selectQuery = selectQuery.replaceAll("\\?", StringUtils.repeat("?,", dPProcessParamInfo.getNaLoanNumber().size() - 1) + "?");
		}
		try {

			/*
			 * dpInfo.setStartTime(
			 * BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(
			 * System.currentTimeMillis())));
			 */
			List<String> assetList = new ArrayList<>();
			assetList.add(dPProcessParamInfo.getPropTemp());
			if(dPProcessParamInfo.getOldAssetNumber() != null) {
				List<String> oldAssetNumbers = new ArrayList<String>();
				oldAssetNumbers.add(dPProcessParamInfo.getOldAssetNumber());
				String rrMigrationLoanNumQuery = (String) cacheManager
						.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY);
				String oldPropTemp = rRMigration.getPropTemps(rrMigrationLoanNumQuery, oldAssetNumbers).get(0);
				if(oldPropTemp != null) {
					assetList.add(oldPropTemp);
				}
			}
			// Replaced LIST_END_DATE_DT_NN_CONDITION for end date check
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			SimpleDateFormat yyyyMMdd = new SimpleDateFormat(DateConversionUtil.DATE_DD_MMM_YY);
			SimpleDateFormat parseFormat = new SimpleDateFormat(RAClientConstants.DATE_FORMAT);
			String listEndDate = null;
			if(isFetchProcess) {
				if(StringUtils.isNotEmpty(dPProcessParamInfo.getMostRecentListEndDate())) {
					listEndDate = yyyyMMdd.format(parseFormat.parse(dPProcessParamInfo.getMostRecentListEndDate())).toUpperCase();
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
			log.info("Query to fetch Hubzu Data : {},  for Hubzu Integration Type : {}", selectQuery, integrationType);
			parameters.addValue("idList", assetList);
			startTime = System.currentTimeMillis();
			if(StringUtils.equalsIgnoreCase(integrationType, IntegrationType.HUBZU_RA_INTEGRATION.getIntegrationType())) {
				hubzuRes = namedJdbcTemplate.execute(selectQuery, parameters, ps -> {
					try {
						log.info("Inside doInPreparedStatement.");
						return createHubzuInfo(ps.executeQuery(), dPProcessParamInfo, integrationType);
					} catch (SQLException sqle) {
						log.error("Error while Executing Hubzu query SQLException: {}", sqle);
						HubzuDBResponse hubzuRes1 = new HubzuDBResponse();
						hubzuRes1.setErrorMsg("Error while Executing Hubzu query SQLException");
						hubzuRes1.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						return hubzuRes1;
					}
				});
			} else {
				hubzuRes = jdbcTemplate.execute(selectQuery, new PreparedStatementCallback<HubzuDBResponse>() {
					@Override
					public HubzuDBResponse doInPreparedStatement(final PreparedStatement ps) throws SQLException {
						try {
							log.info("Inside doInPreparedStatement.");
							// set loan Number to only those query which required it
							// For HUBZU_INITIAL_INTEGRATION wee need to fetch
							// records data in between date
							if(StringUtils.equalsIgnoreCase(integrationType, IntegrationType.HUBZU_INITIAL_INTEGRATION.getIntegrationType())) {
								log.info("from date used is : " + dPProcessParamInfo.getListEndDateDtNnstart());
								log.info("to date used is : " + dPProcessParamInfo.getListEndDateDtNnend());
								ps.setString(1, dPProcessParamInfo.getListEndDateDtNnstart());
								ps.setString(2, dPProcessParamInfo.getListEndDateDtNnend());
							}
							// for Recent status we need to fetch the date for
							// multiple loan number
							else if(StringUtils
									.equalsIgnoreCase(integrationType, IntegrationType.HUBZU_RECENT_STATUS_INTEGRATION.getIntegrationType())) {
								int index1 = 1;
								int index2 = dPProcessParamInfo.getNaLoanNumber().size() + 1;
								for (String loanNum : dPProcessParamInfo.getNaLoanNumber()) {
									ps.setString(index1++, loanNum);
									ps.setString(index2++, loanNum);
								}
							} else if(StringUtils.equalsIgnoreCase(integrationType, IntegrationType.HUBZU_RA_INTEGRATION.getIntegrationType())) {
								ps.setString(1, dPProcessParamInfo.getRbidPropIdVcPk());
							}
							// for other Hubzu integration except
							// we need to set loan number
							else {
								ps.setString(1, dPProcessParamInfo.getAssetNumber());
								// ps.setLong(1,Long.valueOf(dPProcessParamInfo.getAssetNumber()));
							}

							return createHubzuInfo(ps.executeQuery(), dPProcessParamInfo, integrationType);
						} catch (SQLException sqle) {
							log.error("Error while Executing Hubzu query SQLException: {}", sqle);
							HubzuDBResponse hubzuRes = new HubzuDBResponse();
							hubzuRes.setErrorMsg("Error while Executing Hubzu query SQLException");
							hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
							return hubzuRes;
						}
					}
				});
			}

			if(!TransactionStatus.FAIL.getTranStatus().equals(hubzuRes.getTransactionStatus())) {
				hubzuRes.setTransactionStatus(TransactionStatus.SUCCESS.getTranStatus());
			}

		} catch (DataAccessException dae) {
			hubzuRes = prepareHubzuException(dae);
		} catch (Exception e) {
			/*
			 * String errorDetail =
			 * dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
			 * IntegrationType.HUBZU_INTEGRATION.getIntegrationType(),
			 * dpInfo.getErrorDetail(), e); dpInfo.setErrorDetail(errorDetail);
			 * dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.
			 * getValue());
			 */
			log.error("Error while Executing Hubzu query. Exception : {}" , e);
			hubzuRes = new HubzuDBResponse();
			hubzuRes.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
			hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} finally {
			/*
			 * dpInfo.setEndTime(BigInteger.valueOf(DateConversionUtil.
			 * getMillisFromUtcToEst(System.currentTimeMillis())));
			 */
			insertHubzuResponse(hubzuRes, dPProcessParamInfo, integrationType, isFetchProcess, startTime);
		}
		log.info("Exit HubzuDBClient :: method fetchHubzuData");
		return hubzuRes;
	}

	public void insertHubzuResponse(Response response, DPProcessWeekNParamInfo dpInfo, String integrationType, Boolean isFetchProcess,
			long startTime) {
		log.debug("save the Hubzu DB Response audit data");
		try {
			if(!StringUtils.equalsIgnoreCase(integrationType, IntegrationType.HUBZU_INITIAL_INTEGRATION.getIntegrationType()) && !isFetchProcess) {
				CompletableFuture.runAsync(() -> {
					DPWeekNIntgAudit dpWeekNIntgAudit = new DPWeekNIntgAudit();
					dpWeekNIntgAudit.setEventType(integrationType);
					dpWeekNIntgAudit.setStatus(response.getTransactionStatus());
					dpWeekNIntgAudit.setErrorDescription(response.getErrorMsg());

					dpWeekNIntgAudit.setStartTime(startTime);
					dpWeekNIntgAudit.setEndTime(System.currentTimeMillis());

					DPProcessWeekNParam dpProcessParam = new DPProcessWeekNParam();
					dpProcessParam.setId(dpInfo.getId());
					dpWeekNIntgAudit.setDpProcessWeekNParam(dpProcessParam);
					dpWeekNIntgAuditDao.save(dpWeekNIntgAudit);
				});
			}

		} catch (DataAccessException e) {
			log.error("Exception while saving the Hubzu DB Response audit data: {}", e);
		}
		log.debug("Exit HubzuDBClient : insertHubzuResponse method");
	}

	public HubzuDBResponse fetchAllRowsOfInitialQueryOutput(HubzuDBResponse topRowsHubzuResponse, Map<String, String> hubzuQuery,
			Boolean isFetchProcess, Map<String, String> migrationNewPropToPropMap, String listEndDateDtNnEnd) throws SystemException {
		final HubzuDBResponse hubzuResponseAllRows = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfosAllRows = new ArrayList<>();
		hubzuResponseAllRows.setHubzuInfos(hubzuInfosAllRows);
		log.info("Enter HubzuDBClient :: method fetchHubzuData");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);

		try {
			log.info("Query to fetch all rows for initial query Hubzu Data :" + selectQuery + " for Hubzu Integration Type :" + integrationType);

			List<List<HubzuInfo>> splitListHubzuInfos = ListUtils.partition(topRowsHubzuResponse.getHubzuInfos(), initialQueryInClauseCount);
			List<Future<HubzuDBResponse>> futureList = new ArrayList<>();
			for (List<HubzuInfo> subHubzuInfoList : splitListHubzuInfos) {
				Future<HubzuDBResponse> hubzuRespFuture = executorService
						.submit(fetchAllRowsForPropIds(selectQuery, integrationType, subHubzuInfoList, migrationNewPropToPropMap,
								listEndDateDtNnEnd));
				futureList.add(hubzuRespFuture);
			}

			for (Future<HubzuDBResponse> hubzuDBResponseFuture : futureList) {
				HubzuDBResponse intermediateHubzuRes = hubzuDBResponseFuture.get();
				if(!TransactionStatus.FAIL.getTranStatus().equals(intermediateHubzuRes.getTransactionStatus())) {
					hubzuResponseAllRows.setTransactionStatus(TransactionStatus.SUCCESS.getTranStatus());
					hubzuResponseAllRows.getHubzuInfos().addAll(intermediateHubzuRes.getHubzuInfos());
				}
			}
		} catch (DataAccessException dae) {
			log.error("Error while Executing Hubzu query DataAccessException: {}", dae);
			hubzuResponseAllRows.setErrorMsg("Error while Executing Hubzu query DataAccessException");
			hubzuResponseAllRows.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : {}", e);
			hubzuResponseAllRows.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
			hubzuResponseAllRows.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		}
		log.info("Exit HubzuDBClient :: method fetchHubzuData");
		return hubzuResponseAllRows;
	}

	private Callable<HubzuDBResponse> fetchAllRowsForPropIds(final String selectQuery, String integrationType, final List<HubzuInfo> subHubzuInfoList,
			final Map<String, String> migrationNewPropToPropMap, String listEndDateDtNnEnd) {
		return new Callable<HubzuDBResponse>() {
			@Override
			public HubzuDBResponse call() throws Exception {
				DPProcessWeekNParamInfo dPProcessParamInfo = new DPProcessWeekNParamInfo();
				List<String> rbidFKList = new ArrayList<>();
				// including old loan for new loan numbers
				subHubzuInfoList.stream().forEach(hbzData -> {
					if(StringUtils.startsWith(hbzData.getRbidPropIdVcFk(), DPAConstants.PHH_ACNT_ID)) {
						String substr = StringUtils.substring(hbzData.getRbidPropIdVcFk(), 3);
						rbidFKList.add(DPAConstants.PHH_ACNT_ID + substr);
						if(migrationNewPropToPropMap.containsKey(substr)) {
							log.error("Including old propTemp " + migrationNewPropToPropMap.get(substr) + " for new propTemp : " + substr);
							rbidFKList.add(DPAConstants.PHH_ACNT_ID + migrationNewPropToPropMap.get(substr));
						}
					} else {
						String substr = StringUtils.substring(hbzData.getRbidPropIdVcFk(), 3);
						rbidFKList.add(DPAConstants.NRZ_ACNT_ID + substr);
						rbidFKList.add(DPAConstants.OCN_ACNT_ID + substr);

						if(migrationNewPropToPropMap.containsKey(substr)) {
							log.error("Including old propTemp " + migrationNewPropToPropMap.get(substr) + " for new propTemp : " + substr);
							rbidFKList.add(DPAConstants.NRZ_ACNT_ID + migrationNewPropToPropMap.get(substr));
							rbidFKList.add(DPAConstants.OCN_ACNT_ID + migrationNewPropToPropMap.get(substr));
						}
					}
				});
				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("idList", rbidFKList);
				parameters.addValue("endDate", listEndDateDtNnEnd);
				HubzuDBResponse intermediateHubzuRes = namedJdbcTemplate
						.execute(selectQuery, parameters, new PreparedStatementCallback<HubzuDBResponse>() {
							@Override
							public HubzuDBResponse doInPreparedStatement(final PreparedStatement ps) {
								try {
									log.info("Inside doInPreparedStatement.");
									return createHubzuInfo(ps.executeQuery(), dPProcessParamInfo, integrationType);
								} catch (SQLException sqle) {
									log.error("Error while Executing Hubzu query SQLException: {}", sqle);
									HubzuDBResponse hubzuRes = new HubzuDBResponse();
									hubzuRes.setErrorMsg("Error while Executing Hubzu query SQLException");
									hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
									return hubzuRes;
								}
							}
						});
				return intermediateHubzuRes;
			}
		};

	}

	public HubzuDBResponse fetchAllRowsOfPriorRecommendation(List<DPProcessWeekNParam> priorRecommendedEntries, Map<String, String> hubzuQuery)
			throws SystemException {
		final HubzuDBResponse hubzuResponseAllRows = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfosAllRows = new ArrayList<>();
		hubzuResponseAllRows.setHubzuInfos(hubzuInfosAllRows);
		log.info("Enter HubzuDBClient :: method fetchHubzuData");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);

		try {
			log.info("Query to fetch all rows for prior recommendation Data :" + selectQuery + " for Hubzu Integration Type :" + integrationType);

			List<List<DPProcessWeekNParam>> splitListPriorRecommendedEntries = ListUtils
					.partition(priorRecommendedEntries, initialQueryInClauseCount);
			List<Future<HubzuDBResponse>> futureList = new ArrayList<>();
			for (List<DPProcessWeekNParam> subListPriorRecommendedEntries : splitListPriorRecommendedEntries) {
				Future<HubzuDBResponse> hubzuRespFuture = executorService
						.submit(fetchAllRowsForPriorRecommendationPropIds(selectQuery, integrationType, subListPriorRecommendedEntries));
				futureList.add(hubzuRespFuture);
			}

			for (Future<HubzuDBResponse> hubzuDBResponseFuture : futureList) {
				HubzuDBResponse intermediateHubzuRes = hubzuDBResponseFuture.get();
				if(!TransactionStatus.FAIL.getTranStatus().equals(intermediateHubzuRes.getTransactionStatus())) {
					hubzuResponseAllRows.setTransactionStatus(TransactionStatus.SUCCESS.getTranStatus());
					hubzuResponseAllRows.getHubzuInfos().addAll(intermediateHubzuRes.getHubzuInfos());
				}
			}
		} catch (DataAccessException dae) {
			log.error("Error while Executing Hubzu query DataAccessException: {}", dae);
			hubzuResponseAllRows.setErrorMsg("Error while Executing Hubzu query DataAccessException");
			hubzuResponseAllRows.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : {}", e);
			hubzuResponseAllRows.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
			hubzuResponseAllRows.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		}
		log.info("Exit HubzuDBClient :: method fetchHubzuData");
		return hubzuResponseAllRows;
	}

	private Callable<HubzuDBResponse> fetchAllRowsForPriorRecommendationPropIds(final String selectQuery, String integrationType,
			final List<DPProcessWeekNParam> subListPriorRecommendedEntries) {
		return new Callable<HubzuDBResponse>() {
			@Override
			public HubzuDBResponse call() throws Exception {
				DPProcessWeekNParamInfo dPProcessParamInfo = new DPProcessWeekNParamInfo();
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
				HubzuDBResponse intermediateHubzuRes = namedJdbcTemplate
						.execute(selectQuery, parameters, new PreparedStatementCallback<HubzuDBResponse>() {
							@Override
							public HubzuDBResponse doInPreparedStatement(final PreparedStatement ps) {
								try {
									log.info("Inside doInPreparedStatement.");
									return createHubzuInfo(ps.executeQuery(), dPProcessParamInfo, integrationType);
								} catch (SQLException sqle) {
									log.error("Error while Executing Hubzu query SQLException: {}", sqle);
									HubzuDBResponse hubzuRes = new HubzuDBResponse();
									hubzuRes.setErrorMsg("Error while Executing Hubzu query SQLException");
									hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
									return hubzuRes;
								}
							}
						});
				return intermediateHubzuRes;
			}
		};

	}

	public HubzuDBResponse fetchAllRowsOfSearch(String propTemp, String oldAssetNumber, String oldPropTemp, Map<String, String> hubzuQuery)
			throws SystemException {
		HubzuDBResponse hubzuResponseAllRows = new HubzuDBResponse();
		log.info("Enter HubzuDBClient :: method fetchAllRowsOfSearch");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);
		String rrMigrationLoanNumQuery = (String) cacheManager
				.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_WHERE_OLD_RR_LOAN_NULL_QUERY);

		try {
			log.info("Query to fetch all rows for search query Hubzu Data :" + selectQuery + " for Hubzu Integration Type :" + integrationType);
			DPProcessWeekNParamInfo dPProcessParamInfo = new DPProcessWeekNParamInfo();
			List<String> rbidFKList = new ArrayList<>();
			propTemp = StringUtils.remove(propTemp, "-");
			rbidFKList.add(DPAConstants.NRZ_ACNT_ID + propTemp);
			rbidFKList.add(DPAConstants.OCN_ACNT_ID + propTemp);
			rbidFKList.add(DPAConstants.PHH_ACNT_ID + propTemp);

			if(oldAssetNumber != null) {
				List<String> oldAssetNumbers = new ArrayList<String>();
				oldAssetNumbers.add(oldAssetNumber);
				oldPropTemp = rRMigration.getPropTemps(rrMigrationLoanNumQuery, oldAssetNumbers).get(0);
				//dp-384
				if(oldPropTemp != null) {
					oldPropTemp = StringUtils.remove(oldPropTemp, "-");
					rbidFKList.add(DPAConstants.NRZ_ACNT_ID + oldPropTemp);
					rbidFKList.add(DPAConstants.OCN_ACNT_ID + oldPropTemp);
					rbidFKList.add(DPAConstants.PHH_ACNT_ID + propTemp);
				}
			}

			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("idList", rbidFKList);
			hubzuResponseAllRows = namedJdbcTemplate.execute(selectQuery, parameters, new PreparedStatementCallback<HubzuDBResponse>() {
				@Override
				public HubzuDBResponse doInPreparedStatement(final PreparedStatement ps) {
					try {
						log.info("Inside doInPreparedStatement.");
						return createHubzuInfo(ps.executeQuery(), dPProcessParamInfo, integrationType);
					} catch (SQLException sqle) {
						log.error("Error while Executing Hubzu query SQLException: {}", sqle);
						HubzuDBResponse hubzuRes = new HubzuDBResponse();
						hubzuRes.setErrorMsg("Error while Executing Hubzu query SQLException");
						hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						return hubzuRes;
					}
				}
			});
		} catch (DataAccessException dae) {
			log.error("Error while Executing Hubzu query DataAccessException: {}", dae);
			hubzuResponseAllRows.setErrorMsg("Error while Executing Hubzu query DataAccessException");
			hubzuResponseAllRows.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : {}", e);
			hubzuResponseAllRows.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
			hubzuResponseAllRows.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		}
		log.info("Exit HubzuDBClient :: method fetchHubzuData");
		return hubzuResponseAllRows;
	}

	public Callable<List<SSPMIInfo>> fetchClientIdsForPropIds(String selectQuery, String integrationType, List<String> subAssetList) {
		return new Callable<List<SSPMIInfo>>() {

			@Override
			public List<SSPMIInfo> call() throws Exception {
				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("idList", subAssetList);
				List<SSPMIInfo> ssPmiInfos = namedJdbcTemplate.execute(selectQuery, parameters, new PreparedStatementCallback<List<SSPMIInfo>>() {

					@Override
					public List<SSPMIInfo> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
						log.info("Inside doInPreparedStatement.");
						return createSSPmiInfos(ps.executeQuery());
					}
				});
				return ssPmiInfos;
			}
		};
	}

	private List<SSPMIInfo> createSSPmiInfos(ResultSet rs) {
		log.info("Enter RRDBClient :: method createSSPMIInfo");
		List<SSPMIInfo> ssPmiInfos = new ArrayList<>();
		if(rs != null) {
			try {
				while (rs.next()) {
					SSPMIInfo ssPmiInfo = new SSPMIInfo();
					ssPmiInfo.setAssetNumber(rs.getString("SELR_PROP_ID_VC_NN"));
					ssPmiInfo.setClientCode(rs.getString("CLNT_CODE_VC"));
					ssPmiInfos.add(ssPmiInfo);
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		return ssPmiInfos;
	}

	// Hubzu query for WeekN to check Active Listing for Success and Under Review Listings
	public HubzuDBResponse fetchSuccessReviewQueryOutput(DPProcessWeekNParamInfo dPProcessParamInfo, Map<String, String> hubzuQuery,
			Boolean isFetchProcess) throws SystemException {
		HubzuDBResponse hubzuRes = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfosAllRows = new ArrayList<>();
		hubzuRes.setHubzuInfos(hubzuInfosAllRows);
		List<String> assetList = new ArrayList<>();
		log.info("Enter HubzuDBClient :: method fetchHubzuData");
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);
		long startTime = 0;
		try {

			log.info("Query to fetch Hubzu Data :" + selectQuery + " for Hubzu Integration Type :" + integrationType);
			/*
			 * dpInfo.setStartTime(
			 * BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(
			 * System.currentTimeMillis())));
			 */

			//dp-331
			assetList.add(dPProcessParamInfo.getPropTemp());
			if(dPProcessParamInfo.getOldAssetNumber() != null) {
				List<String> oldAssetNumbers = new ArrayList<String>();
				oldAssetNumbers.add(dPProcessParamInfo.getOldAssetNumber());
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
			hubzuRes = namedJdbcTemplate.execute(selectQuery, parameters, new PreparedStatementCallback<HubzuDBResponse>() {
				@Override
				public HubzuDBResponse doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					try {
						log.info("Inside doInPreparedStatement.");
						return createHubzuInfo(ps.executeQuery(), dPProcessParamInfo, integrationType);
					} catch (SQLException sqle) {
						log.error("Error while Executing Hubzu query SQLException: {}", sqle);
						HubzuDBResponse hubzuRes = new HubzuDBResponse();
						hubzuRes.setErrorMsg("Error while Executing Hubzu query SQLException");
						hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						return hubzuRes;
					}
				}
			});
			if(!TransactionStatus.FAIL.getTranStatus().equals(hubzuRes.getTransactionStatus())) {
				hubzuRes.setTransactionStatus(TransactionStatus.SUCCESS.getTranStatus());
			}

		} catch (DataAccessException dae) {
			hubzuRes = prepareHubzuException(dae);
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : {}", e);
			hubzuRes = new HubzuDBResponse();
			hubzuRes.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
			hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} finally {
			/*
			 * dpInfo.setEndTime(BigInteger.valueOf(DateConversionUtil.
			 * getMillisFromUtcToEst(System.currentTimeMillis())));
			 */
			insertHubzuResponse(hubzuRes, dPProcessParamInfo, integrationType, isFetchProcess, startTime);
		}
		log.info("Exit HubzuDBClient :: method fetchHubzuData");
		return hubzuRes;
	}

	// Success and Under Review for SOP week N

	// Hubzu query for WeekN to check Active Listing for Success and Under Review Listings
	public HubzuDBResponse fetchInitialSuccessReviewQueryOutput(DPProcessWeekNParamInfo dPProcessParamInfo, Map<String, String> hubzuQuery)
			throws SystemException {
		log.info("Enter HubzuDBClient :: method fetchInitialSuccessReviewQueryOutput");
		HubzuDBResponse hubzuRes = new HubzuDBResponse();
		List<String> assetList = new ArrayList<>();
		String selectQuery = hubzuQuery.get(RAClientConstants.HUBZU_QUERY);
		String integrationType = hubzuQuery.get(RAClientConstants.HUBZU_INTEGRATION_TYPE);
		try {

			log.info("Query to fetch Hubzu Data :" + selectQuery + " for Hubzu Integration Type :" + integrationType);
			/*
			 * dpInfo.setStartTime(
			 * BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(
			 * System.currentTimeMillis())));
			 */

			//dp-331
			assetList.add(dPProcessParamInfo.getPropTemp());
			if(dPProcessParamInfo.getOldAssetNumber() != null) {
				List<String> oldAssetNumbers = new ArrayList<String>();
				oldAssetNumbers.add(dPProcessParamInfo.getOldAssetNumber());
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

			hubzuRes = namedJdbcTemplate.execute(selectQuery, parameters, new PreparedStatementCallback<HubzuDBResponse>() {
				@Override
				public HubzuDBResponse doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					try {
						log.info("Inside doInPreparedStatement.");
						return createHubzuInfo(ps.executeQuery(), dPProcessParamInfo, integrationType);
					} catch (SQLException sqle) {
						log.error("Error while Executing Hubzu query SQLException: {}", sqle);
						HubzuDBResponse hubzuRes = new HubzuDBResponse();
						hubzuRes.setErrorMsg("Error while Executing Hubzu query SQLException");
						hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						return hubzuRes;
					}
				}
			});
			if(!TransactionStatus.FAIL.getTranStatus().equals(hubzuRes.getTransactionStatus())) {
				hubzuRes.setTransactionStatus(TransactionStatus.SUCCESS.getTranStatus());
			}

		} catch (DataAccessException dae) {
			hubzuRes = prepareHubzuException(dae);
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : {}", e);
			hubzuRes = new HubzuDBResponse();
			hubzuRes.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
			hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} finally {
			/*
			 * dpInfo.setEndTime(BigInteger.valueOf(DateConversionUtil.
			 * getMillisFromUtcToEst(System.currentTimeMillis())));
             insertHubzuResponse(hubzuRes, dPProcessParamInfo, integrationType, isFetchProcess);*/
		}
		log.info("Exit HubzuDBClient :: method fetchHubzuData");
		return hubzuRes;
	}

	public HubzuDBResponse fetchQaReportHubzuResponse(String startDate, String endDate, Boolean sopStatus) {
		log.debug("fetchQaReportHubzuResponse start.");
		HubzuDBResponse hubzuResponse;
		String hubzuQuery = (String) cacheManager.getAppParamValue(
				sopStatus ? AppParameterConstant.SOP_HUBZU_DAILY_QA_REPORT: AppParameterConstant.HUBZU_DAILY_QA_REPORT);
		log.debug("hubzuQuery : {}", hubzuQuery);
		log.debug("startDate : {}, endDate : {}", startDate, endDate);
		try {
			hubzuResponse = jdbcTemplate.execute(hubzuQuery, (PreparedStatementCallback<HubzuDBResponse>) ps -> {
				try {
					log.info("Inside doInPreparedStatement.");
					ps.setString(1, startDate);
					ps.setString(2, endDate);
					return createQaReportHubzuInfo(ps.executeQuery());
				} catch (SQLException sqle) {
					log.error("Error while Executing Hubzu query SQLException: {}", sqle);
					HubzuDBResponse hubzuRes = new HubzuDBResponse();
					hubzuRes.setErrorMsg("Error while Executing Hubzu query SQLException");
					hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
					return hubzuRes;
				}
			});
		} catch (DataAccessException dae) {
			hubzuResponse = prepareHubzuException(dae);
		} catch (Exception e) {
			log.error("Error while Executing Hubzu query. Exception : {}", e);
			hubzuResponse = new HubzuDBResponse();
			hubzuResponse.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
			hubzuResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		}
		log.debug("fetchQaReportHubzuResponse end.");

		return hubzuResponse;
	}

	private HubzuDBResponse createQaReportHubzuInfo(ResultSet resultSet) throws SQLException {
		HubzuDBResponse hubzuResponse = new HubzuDBResponse();
		List<HubzuInfo> hubzuInfos = new ArrayList<>();
		while (resultSet.next()) {
			if(resultSet.getRow() > 0) {
				HubzuInfo hubzuInfo = convertToHubzuInfo(resultSet);
				hubzuInfos.add(hubzuInfo);
			}
		}
		hubzuResponse.setHubzuInfos(hubzuInfos);
		return hubzuResponse;
	}

	public List<HubzuInfo> getMigratedHubzuResponse(List<String> assetNumberList, Map<String, String> migrationNewPropToPropMap, Boolean sopStatus) {
		String allRowsHubzuQuery = (String) cacheManager.getAppParamValue(
				sopStatus ? AppParameterConstant.SOP_HUBZU_DAILY_QA_REPORT_ALL_ROWS: AppParameterConstant.HUBZU_DAILY_QA_REPORT_ALL_ROWS);
		List<HubzuInfo> hubzuInfoList = new ArrayList<>();

		List<String> rbidFKList = new ArrayList<>();
		assetNumberList.stream().forEach(c -> {
			String substr = StringUtils.substring(c, 3);
			rbidFKList.add(c);
			if(migrationNewPropToPropMap.containsKey(substr)) {
				log.error("Including old propTemp " + migrationNewPropToPropMap.get(substr) + " for new propTemp : " + substr);
				rbidFKList.add(StringUtils.startsWith(c, DPAConstants.PHH_ACNT_ID)? DPAConstants.PHH_ACNT_ID :
					StringUtils.startsWith(c, DPAConstants.OCN_ACNT_ID)? DPAConstants.OCN_ACNT_ID : DPAConstants.NRZ_ACNT_ID 
							+ migrationNewPropToPropMap.get(substr));
			}
		});

		log.info("rbidFKList for hubzu : {}", rbidFKList);

		List<List<String>> splitListProps = ListUtils
				.partition(rbidFKList.stream().distinct().collect(Collectors.toList()), initialQueryInClauseCount);

		splitListProps.forEach(props -> {
			HubzuDBResponse hubzuResponse;
			try {
				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("idList", props);

				hubzuResponse = namedJdbcTemplate.execute(allRowsHubzuQuery, parameters, ps -> {
					try {
						log.info("Inside doInPreparedStatement.");
						return createQaReportHubzuInfo(ps.executeQuery());
					} catch (SQLException sqle) {
						log.error("Error while Executing Hubzu query SQLException: {}", sqle);
						HubzuDBResponse hubzuRes = new HubzuDBResponse();
						hubzuRes.setErrorMsg("Error while Executing Hubzu query SQLException");
						hubzuRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						return hubzuRes;
					}
				});
			} catch (DataAccessException dae) {
				hubzuResponse = prepareHubzuException(dae);
			} catch (Exception e) {
				log.error("Error while Executing Hubzu query. Exception : {}", e);
				hubzuResponse = new HubzuDBResponse();
				hubzuResponse.setErrorMsg("Error while Executing Hubzu query. Exception : " + e.getLocalizedMessage());
				hubzuResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
			}
			if(hubzuResponse != null && CollectionUtils.isNotEmpty(hubzuResponse.getHubzuInfos())) {
				hubzuInfoList.addAll(hubzuResponse.getHubzuInfos());
			}
		});

		return hubzuInfoList;
	}
}
