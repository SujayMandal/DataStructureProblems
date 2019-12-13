package com.fa.dp.business.db.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.fa.dp.business.info.Response;
import com.fa.dp.business.info.StageFiveDBResponse;
import com.fa.dp.business.info.StageFiveInfo;
import com.fa.dp.business.rr.rtng.constant.StageFiveDBConstant;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validator.dao.DPWeekNIntgAuditDao;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNIntgAudit;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.util.AppParameterConstant;

@Slf4j
@Named
public class StageFiveDBClient {

	private static final String HYPHEN = "-";
	private static final String DEFAULT_ASISMIDMKTVAL = null;
	private static final String DEFAULT_APPRTYP = "M";
	private static final String DEFAULT_CONDITIONCDE = "AVERAGE";
	private static final String DEFAULT_LIVINGAREA = "0";
	private static final String DEFAULT_TOTREPAIRAMT = "0";

	@Inject
	private CacheManager cacheManager;

	@Inject
	@Named(value = "stage5DataSource")
	private DataSource dataSource;

	@Value("${SCHEMA_NAME}")
	private String schemaName;

	@Value("${INPUT_PARAMS}")
	private String inputParam;

	@Inject
	private DPWeekNIntgAuditDao dpWeekNIntgAuditDao;

	private JdbcTemplate jdbcTemplate;

	private SimpleJdbcCall simpleJdbcCall;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		simpleJdbcCall = new SimpleJdbcCall(dataSource);
	}

	public StageFiveDBResponse fetchStageFiveData(DPProcessWeekNParamInfo dPProcessParamInfo, Boolean isFetchProcess) {
		StageFiveDBResponse stageFiveRes = null;
		log.info("Enter StageFiveDBClient :: method fetchStageFiveData");
		long startTime = 0;
		try {
			String selectQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_STAGE5_QUERY);
			log.debug("Query to fetch Stage5 Data :" + selectQuery);
			/*dpInfo.setStartTime(
					BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis())));*/
			startTime = System.currentTimeMillis();
			stageFiveRes = jdbcTemplate.execute(selectQuery, new PreparedStatementCallback<StageFiveDBResponse>() {
				@Override
				public StageFiveDBResponse doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					try {
						log.info("Inside doInPreparedStatement.");
						if (StringUtils.isNotBlank(dPProcessParamInfo.getAssetNumber())) {
							if (StringUtils.contains(dPProcessParamInfo.getAssetNumber(), HYPHEN)) {
								String[] splitStr = StringUtils.split(dPProcessParamInfo.getAssetNumber(), HYPHEN);
								ps.setString(1, splitStr[0]);
								ps.setInt(2, Integer.valueOf(splitStr[1]));
							} else {
								ps.setString(1, dPProcessParamInfo.getAssetNumber());
								ps.setInt(2, 1);
							}
						}
						return createStageFiveInfo(ps.executeQuery(), dPProcessParamInfo);
					} catch (SQLException sqle) {
						log.error(sqle.getLocalizedMessage(), sqle);
						StageFiveDBResponse stageFiveRes = new StageFiveDBResponse();
						stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						return stageFiveRes;
					}
				}
			});
			if (!TransactionStatus.FAIL.getTranStatus().equals(stageFiveRes.getTransactionStatus())) {
				stageFiveRes.setTransactionStatus(TransactionStatus.SUCCESS.getTranStatus());
			}

		} catch (DataAccessException dae) {
			log.error(dae.getLocalizedMessage(), dae);
		/*	String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
					IntegrationType.STAGE5_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), dae);*/
			/*dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
			stageFiveRes = new StageFiveDBResponse();
			stageFiveRes.setErrorMsg("Error while Executing Stage5 query");
			stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} catch (Exception e) {
			/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
					IntegrationType.STAGE5_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), e);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
			log.info(e.getLocalizedMessage(), e);
			stageFiveRes = new StageFiveDBResponse();
			stageFiveRes.setErrorMsg("Error while Executing Stage5 query");
			stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} finally {
			/*			dpInfo.setEndTime(BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis())));
			 */
			insertStageFiveResponse(stageFiveRes, dPProcessParamInfo, isFetchProcess,startTime);
		}
		log.info("Exit StageFiveDBClient :: method fetchStageFiveData");
		return stageFiveRes;
	}

	private StageFiveDBResponse createStageFiveInfo(final ResultSet rs, DPProcessWeekNParamInfo dpInfo) {
		log.info("Enter StageFiveDBClient :: method createStageFiveInfo");
		StageFiveDBResponse stageFiveRes = new StageFiveDBResponse();
		List<StageFiveInfo> stageFiveInfos = new ArrayList<>();
		Boolean recordFound = false;
		try {
			if (rs != null) {
				try {
					while (rs.next()) {
						recordFound = true;
						if (rs.getRow() > 0) {
							StageFiveInfo stageFiveInfo = new StageFiveInfo();
							stageFiveInfo.setLoanNumber(rs.getString(StageFiveDBConstant.LOANNUMBER));
							stageFiveInfo.setApprTyp(rs.getString(StageFiveDBConstant.APPRTYP));
							stageFiveInfo.setAsIsLowMktVal(rs.getString(StageFiveDBConstant.ASISLOWMKTVAL));
							stageFiveInfo.setAsIsMidMktVal(rs.getString(StageFiveDBConstant.ASISMIDMKTVAL));
							stageFiveInfo.setConditionCde(rs.getString(StageFiveDBConstant.CONDITIONCDE));
							stageFiveInfo.setLivingArea(rs.getString(StageFiveDBConstant.LIVINGAREA));
							stageFiveInfo.setReViewDt(rs.getString(StageFiveDBConstant.REVIEWDT));
							stageFiveInfo.setTotRepairAmt(rs.getString(StageFiveDBConstant.TOTREPAIRAMT));
							stageFiveInfos.add(stageFiveInfo);
						}
					}
					if (!recordFound) {
						StageFiveInfo stageFiveInfo = new StageFiveInfo();
						stageFiveInfo.setApprTyp(DEFAULT_APPRTYP);
						stageFiveInfo.setAsIsMidMktVal(DEFAULT_ASISMIDMKTVAL);
						stageFiveInfo.setConditionCde(DEFAULT_CONDITIONCDE);
						stageFiveInfo.setLivingArea(DEFAULT_LIVINGAREA);
						stageFiveInfo.setTotRepairAmt(DEFAULT_TOTREPAIRAMT);
						stageFiveInfos.add(stageFiveInfo);
						/*stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						stageFiveRes
								.setErrorMsg("No Record Found In Stage5 DB for Loan Number :" + dpInfo.getAssetNumber());*/
						/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
								IntegrationType.STAGE5_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), null);
						dpInfo.setErrorDetail(errorDetail);
						dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
					}
					stageFiveRes.setStageFiveInfos(stageFiveInfos);
				} catch (SQLException sqle) {
					log.error(sqle.getMessage());
					stageFiveRes = new StageFiveDBResponse();
					stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
					stageFiveRes.setErrorMsg("Error while setting Properties in StageFiveDBResponse");
					/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
							IntegrationType.STAGE5_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), sqle);
					dpInfo.setErrorDetail(errorDetail);
					dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			}

		} catch (SQLException sqle) {
			log.debug("Not able to close the Result set");
			stageFiveRes = new StageFiveDBResponse();
			stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
			stageFiveRes.setErrorMsg("Not able to close the Result set");
			/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
					IntegrationType.STAGE5_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), sqle);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
		}
		log.info("Exit StageFiveDBClient :: method createStageFiveInfo");
		return stageFiveRes;
	}

	/*
	 * Create SOP weekn Stage 5 Info
	 */
	private StageFiveDBResponse createSOPWeekNStageFiveInfo(final ResultSet rs, DPSopWeekNParamInfo dpInfo) {
		log.info("Enter StageFiveDBClient :: method createStageFiveInfo");
		StageFiveDBResponse stageFiveRes = new StageFiveDBResponse();
		List<StageFiveInfo> stageFiveInfos = new ArrayList<>();
		Boolean recordFound = false;
		try {
			if (rs != null) {
				try {
					while (rs.next()) {
						recordFound = true;
						if (rs.getRow() > 0) {
							StageFiveInfo stageFiveInfo = new StageFiveInfo();
							stageFiveInfo.setLoanNumber(rs.getString(StageFiveDBConstant.LOANNUMBER));
							stageFiveInfo.setApprTyp(rs.getString(StageFiveDBConstant.APPRTYP));
							stageFiveInfo.setAsIsLowMktVal(rs.getString(StageFiveDBConstant.ASISLOWMKTVAL));
							stageFiveInfo.setAsIsMidMktVal(rs.getString(StageFiveDBConstant.ASISMIDMKTVAL));
							stageFiveInfo.setConditionCde(rs.getString(StageFiveDBConstant.CONDITIONCDE));
							stageFiveInfo.setLivingArea(rs.getString(StageFiveDBConstant.LIVINGAREA));
							stageFiveInfo.setReViewDt(rs.getString(StageFiveDBConstant.REVIEWDT));
							stageFiveInfo.setTotRepairAmt(rs.getString(StageFiveDBConstant.TOTREPAIRAMT));
							stageFiveInfos.add(stageFiveInfo);
						}
					}
					if (recordFound) {
						stageFiveRes.setStageFiveInfos(stageFiveInfos);
					} else {
						stageFiveRes = new StageFiveDBResponse();
						stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						stageFiveRes.setErrorMsg("No Record Found In Stage5 DB for Loan Number :" + dpInfo.getAssetNumber());
						/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
								IntegrationType.STAGE5_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), null);
						dpInfo.setErrorDetail(errorDetail);
						dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
					}

				} catch (SQLException sqle) {
					log.error(sqle.getMessage());
					stageFiveRes = new StageFiveDBResponse();
					stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
					stageFiveRes.setErrorMsg("Error while setting Properties in StageFiveDBResponse");
					/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
							IntegrationType.STAGE5_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), sqle);
					dpInfo.setErrorDetail(errorDetail);
					dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			}

		} catch (SQLException sqle) {
			log.debug("Not able to close the Result set");
			stageFiveRes = new StageFiveDBResponse();
			stageFiveRes.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
			stageFiveRes.setErrorMsg("Not able to close the Result set");
			/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(),
					IntegrationType.STAGE5_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), sqle);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
		}
		log.info("Exit StageFiveDBClient :: method createStageFiveInfo");
		return stageFiveRes;
	}

	public void insertStageFiveResponse(Response response, DPProcessWeekNParamInfo dpInfo, Boolean isFetchProcess, long startTime) {
		log.debug("save the Stage5 Response audit data");
		try {
			if (!isFetchProcess) {
				CompletableFuture.runAsync(() -> {
				DPWeekNIntgAudit dpWeekNIntgAudit = new DPWeekNIntgAudit();
				dpWeekNIntgAudit.setEventType(IntegrationType.STAGE5_INTEGRATION.getIntegrationType());
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
			log.error(e.getLocalizedMessage(), e);
		}

	}

	/*
	 * Executing stored procedure to get data from Stage 5 DB
	 */
	public List<DPProcessWeekNParamInfo> getStage5FromStoredProcedure(List<DPProcessWeekNParamInfo> columnEntries) throws SystemException {
		final Map<String, String> inputParams = new HashMap<>();
		List<String> loanNumbers = new ArrayList<>();
		StageFiveInfo stageFiveInfo = null;
		String finalInput = "'";
		log.info("Enter StageFiveDBClient :: method fetchStageFiveData");
		String storedProcdure = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_STAGE5_QUERY);
		log.debug("Stored Procedure To Get Data From  Stage5 :" + storedProcdure);

		// Preparing Stored Procdure with required parameters
		simpleJdbcCall.withSchemaName(schemaName).withProcedureName(storedProcdure)
				//.addDeclaredParameter( new SqlParameter(StageFiveDBConstant.INPUT_PARAMS, Types.VARCHAR));
				.withoutProcedureColumnMetaDataAccess().useInParameterNames(inputParam)
				.declareParameters(new SqlParameter(StageFiveDBConstant.INPUT_PARAMS, Types.LONGNVARCHAR));

		// Preparing Input Params for Stored Procdure
		columnEntries.forEach(nParamInfo -> {
			if (StringUtils.isNotBlank(nParamInfo.getAssetNumber())) {

				// Hyphen Check with New Loan
				if (StringUtils.contains(nParamInfo.getPropTemp(), HYPHEN)) {
					String[] splitStr = StringUtils.split(nParamInfo.getPropTemp(), HYPHEN);
					loanNumbers.add(nParamInfo.getAssetNumber() + "," + splitStr[1]);
				} else
					loanNumbers.add(nParamInfo.getAssetNumber() + ",0" + 1);
			}
		});

		// Creating String of all loan numbers
		if (!loanNumbers.isEmpty()) {
			String firstRecord = loanNumbers.get(0);
			for (String str : loanNumbers)
				finalInput = finalInput + str + ";";
			finalInput = finalInput + firstRecord + ";'";
		}
		log.info("Input params for Stored Procedure :" + finalInput);

		//Input Prepared and finally calling Stored Procedure
		SqlParameterSource in = new MapSqlParameterSource().addValue(StageFiveDBConstant.INPUT_PARAMS, finalInput);
		Map<String, Object> stage5ResultSet = simpleJdbcCall.execute(in);

		// Preparing Results retrieved from stored procedure
		log.info(
				"Enter StageFiveDBClient :: Results retrieved from Stage 5 Stored Procedure : " + stage5ResultSet.get(StageFiveDBConstant.DB_RESULT));

		List<StageFiveInfo> stageFiveInfos = new ArrayList<>();
		int length = (((List) ((LinkedHashMap) stage5ResultSet).get(StageFiveDBConstant.DB_RESULT))).size();

		if (length > 0 && null != stage5ResultSet.get(StageFiveDBConstant.DB_RESULT)) {
			for (int i = 0; i < length; i++) {
				Map stage5Results = ((Map) (((List) ((LinkedHashMap) stage5ResultSet).get(StageFiveDBConstant.DB_RESULT))).get(i));
				if (String.valueOf(stage5Results.get(StageFiveDBConstant.APPRTYP)).matches("[AaMm]")) {
					stageFiveInfo = new StageFiveInfo();
					stageFiveInfo.setLoanNumber(String.valueOf(stage5Results.get(StageFiveDBConstant.LOANNUMBER)));
					stageFiveInfo.setReViewDt(String.valueOf(stage5Results.get(StageFiveDBConstant.REVIEWDT)));
					stageFiveInfo.setApprTyp(stage5Results.get(StageFiveDBConstant.APPRTYP) != null ?
							String.valueOf(stage5Results.get(StageFiveDBConstant.APPRTYP)) :
							DEFAULT_APPRTYP);
					stageFiveInfo.setAsIsLowMktVal(stage5Results.get(StageFiveDBConstant.ASISLOWMKTVAL) != null ?
							String.valueOf(stage5Results.get(StageFiveDBConstant.ASISLOWMKTVAL)) :
							DEFAULT_ASISMIDMKTVAL);
					stageFiveInfo.setAsIsMidMktVal(stage5Results.get(StageFiveDBConstant.ASISMIDMKTVAL) != null ?
							String.valueOf(stage5Results.get(StageFiveDBConstant.ASISMIDMKTVAL)) :
							DEFAULT_ASISMIDMKTVAL);
					stageFiveInfo.setConditionCde(stage5Results.get(StageFiveDBConstant.CONDITIONCDE) != null ?
							String.valueOf(stage5Results.get(StageFiveDBConstant.CONDITIONCDE)) :
							DEFAULT_CONDITIONCDE);
					stageFiveInfo.setLivingArea(stage5Results.get(StageFiveDBConstant.LIVINGAREA) != null ?
							String.valueOf(stage5Results.get(StageFiveDBConstant.LIVINGAREA)) :
							DEFAULT_LIVINGAREA);
					stageFiveInfo.setTotRepairAmt(stage5Results.get(StageFiveDBConstant.TOTREPAIRAMT) != null ?
							String.valueOf(stage5Results.get(StageFiveDBConstant.TOTREPAIRAMT)) :
							DEFAULT_TOTREPAIRAMT);
					stageFiveInfos.add(stageFiveInfo);
				}
			}
		}

		Map<String, List<StageFiveInfo>> stageFiveGroupedMap = stageFiveInfos.stream().collect(Collectors.groupingBy(StageFiveInfo::getLoanNumber));

		columnEntries.stream().forEach(it -> {
			StageFiveDBResponse stageFiveRes = null;
			List<StageFiveInfo> recordsNotInStage5DbList = null;
			if (stageFiveGroupedMap.containsKey(it.getAssetNumber())) {
				stageFiveRes = new StageFiveDBResponse();
				stageFiveRes.setStageFiveInfos(stageFiveGroupedMap.get(it.getAssetNumber()));
				it.setStageFiveDBResponse(stageFiveRes);
			} else if (stageFiveGroupedMap.containsKey(it.getOldAssetNumber())) {
				stageFiveRes = new StageFiveDBResponse();
				stageFiveRes.setStageFiveInfos(stageFiveGroupedMap.get(it.getOldAssetNumber()));
				it.setStageFiveDBResponse(stageFiveRes);
			} else {
				StageFiveInfo recordsNotinStage5Db = new StageFiveInfo();
				recordsNotInStage5DbList = new ArrayList<>();
				recordsNotinStage5Db.setLoanNumber(it.getAssetNumber());
				recordsNotinStage5Db.setApprTyp(DEFAULT_APPRTYP);
				recordsNotinStage5Db.setAsIsMidMktVal(DEFAULT_ASISMIDMKTVAL);
				recordsNotinStage5Db.setConditionCde(DEFAULT_CONDITIONCDE);
				recordsNotinStage5Db.setLivingArea(DEFAULT_LIVINGAREA);
				recordsNotinStage5Db.setTotRepairAmt(DEFAULT_TOTREPAIRAMT);
				recordsNotInStage5DbList.add(recordsNotinStage5Db);
				stageFiveRes = new StageFiveDBResponse();
				stageFiveRes.setStageFiveInfos(recordsNotInStage5DbList);
				it.setStageFiveDBResponse(stageFiveRes);
			}
		});

		log.info("Exit StageFiveDBClient :: method fetchStageFiveData");
		return columnEntries;
	}
	
	public List<DPSopWeekNParamInfo> getStage5SOPFromStoredProcedure(List<DPSopWeekNParamInfo> columnEntries) throws SystemException {
		final Map<String, String> inputParams = new HashMap<>();
		List<String> loanNumbers = new ArrayList<>();
		StageFiveInfo stageFiveInfo = null;
		String finalInput = "'";
		log.info("Enter StageFiveDBClient :: method fetchStageFiveData");
		String storedProcdure = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_STAGE5_QUERY);
		log.debug("Stored Procedure To Get Data From  Stage5 :" + storedProcdure);

		// Preparing Stored Procdure with required parameters
		simpleJdbcCall.withSchemaName(schemaName).withProcedureName(storedProcdure)
				//.addDeclaredParameter( new SqlParameter(StageFiveDBConstant.INPUT_PARAMS, Types.VARCHAR));
				.withoutProcedureColumnMetaDataAccess().useInParameterNames(inputParam)
				.declareParameters(new SqlParameter(StageFiveDBConstant.INPUT_PARAMS, Types.LONGNVARCHAR));

		// Preparing Input Params for Stored Procdure
		columnEntries.forEach(nParamInfo -> {
			if (StringUtils.isNotBlank(nParamInfo.getAssetNumber())) {

				// Hyphen Check with New Loan
				if (StringUtils.contains(nParamInfo.getPropTemp(), HYPHEN)) {
					String[] splitStr = StringUtils.split(nParamInfo.getPropTemp(), HYPHEN);
					loanNumbers.add(nParamInfo.getAssetNumber() + "," + splitStr[1]);
				} else
					loanNumbers.add(nParamInfo.getAssetNumber() + ",0" + 1);
			}
		});

		// Creating String of all loan numbers
		if (!loanNumbers.isEmpty()) {
			String firstRecord = loanNumbers.get(0);
			for (String str : loanNumbers)
				finalInput = finalInput + str + ";";
			finalInput = finalInput + firstRecord + ";'";
		}
		log.info("Input params for Stored Procedure :" + finalInput);

		//Input Prepared and finally calling Stored Procedure
		SqlParameterSource in = new MapSqlParameterSource().addValue(StageFiveDBConstant.INPUT_PARAMS, finalInput);
		Map<String, Object> stage5ResultSet = simpleJdbcCall.execute(in);

		// Preparing Results retrieved from stored procedure
		log.info(
				"Enter StageFiveDBClient :: Results retrieved from Stage 5 Stored Procedure : " + stage5ResultSet.get(StageFiveDBConstant.DB_RESULT));

		List<StageFiveInfo> stageFiveInfos = new ArrayList<>();
		int length = (((List) ((LinkedHashMap) stage5ResultSet).get(StageFiveDBConstant.DB_RESULT))).size();

		if (length > 0 && null != stage5ResultSet.get(StageFiveDBConstant.DB_RESULT)) {
			for (int i = 0; i < length; i++) {
				Map stage5Results = ((Map) (((List) ((LinkedHashMap) stage5ResultSet).get(StageFiveDBConstant.DB_RESULT))).get(i));
				stageFiveInfo = new StageFiveInfo();
				stageFiveInfo.setLoanNumber(String.valueOf(stage5Results.get(StageFiveDBConstant.LOANNUMBER)));
				stageFiveInfo.setReViewDt(String.valueOf(stage5Results.get(StageFiveDBConstant.REVIEWDT)));
				stageFiveInfo.setApprTyp(stage5Results.get(StageFiveDBConstant.APPRTYP) != null ?
						String.valueOf(stage5Results.get(StageFiveDBConstant.APPRTYP)) :
							DEFAULT_APPRTYP);
				stageFiveInfo.setAsIsLowMktVal(stage5Results.get(StageFiveDBConstant.ASISLOWMKTVAL) != null ?
						String.valueOf(stage5Results.get(StageFiveDBConstant.ASISLOWMKTVAL)) :
							DEFAULT_ASISMIDMKTVAL);
				stageFiveInfo.setAsIsMidMktVal(stage5Results.get(StageFiveDBConstant.ASISMIDMKTVAL) != null ?
						String.valueOf(stage5Results.get(StageFiveDBConstant.ASISMIDMKTVAL)) :
							DEFAULT_ASISMIDMKTVAL);
				stageFiveInfo.setConditionCde(stage5Results.get(StageFiveDBConstant.CONDITIONCDE) != null ?
						String.valueOf(stage5Results.get(StageFiveDBConstant.CONDITIONCDE)) :
							DEFAULT_CONDITIONCDE);
				stageFiveInfo.setLivingArea(stage5Results.get(StageFiveDBConstant.LIVINGAREA) != null ?
						String.valueOf(stage5Results.get(StageFiveDBConstant.LIVINGAREA)) :
							DEFAULT_LIVINGAREA);
				stageFiveInfo.setTotRepairAmt(stage5Results.get(StageFiveDBConstant.TOTREPAIRAMT) != null ?
						String.valueOf(stage5Results.get(StageFiveDBConstant.TOTREPAIRAMT)) :
							DEFAULT_TOTREPAIRAMT);
				stageFiveInfos.add(stageFiveInfo);
			}
		}

		Map<String, List<StageFiveInfo>> stageFiveGroupedMap = stageFiveInfos.stream().collect(Collectors.groupingBy(StageFiveInfo::getLoanNumber));

		columnEntries.stream().forEach(it -> {
			StageFiveDBResponse stageFiveRes = null;
			List<StageFiveInfo> recordsNotInStage5DbList = null;
			if (stageFiveGroupedMap.containsKey(it.getAssetNumber())) {
				stageFiveRes = new StageFiveDBResponse();
				stageFiveRes.setStageFiveInfos(stageFiveGroupedMap.get(it.getAssetNumber()));
				it.setStageFiveDBResponse(stageFiveRes);
			} else if (stageFiveGroupedMap.containsKey(it.getOldAssetNumber())) {
				stageFiveRes = new StageFiveDBResponse();
				stageFiveRes.setStageFiveInfos(stageFiveGroupedMap.get(it.getOldAssetNumber()));
				it.setStageFiveDBResponse(stageFiveRes);
			} else {
				StageFiveInfo recordsNotinStage5Db = new StageFiveInfo();
				recordsNotInStage5DbList = new ArrayList<>();
				recordsNotinStage5Db.setLoanNumber(it.getAssetNumber());
				recordsNotinStage5Db.setApprTyp(DEFAULT_APPRTYP);
				recordsNotinStage5Db.setAsIsMidMktVal(DEFAULT_ASISMIDMKTVAL);
				recordsNotinStage5Db.setConditionCde(DEFAULT_CONDITIONCDE);
				recordsNotinStage5Db.setLivingArea(DEFAULT_LIVINGAREA);
				recordsNotinStage5Db.setTotRepairAmt(DEFAULT_TOTREPAIRAMT);
				recordsNotInStage5DbList.add(recordsNotinStage5Db);
				stageFiveRes = new StageFiveDBResponse();
				stageFiveRes.setStageFiveInfos(recordsNotInStage5DbList);
				it.setStageFiveDBResponse(stageFiveRes);
			}
		});

		log.info("Exit StageFiveDBClient :: method fetchStageFiveData");
		return columnEntries;
	}

}