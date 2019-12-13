package com.fa.dp.business.rr.aggregator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.rr.migration.dao.DPMigrationMapDao;
import com.fa.dp.business.rr.migration.entity.DPMigrationMap;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;

@Named
public class RRClassificationAggregator extends AbstractDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(RRClassificationAggregator.class);

	public static final String PMI_FLAG = "PMI_FLAG";
	public static final String INSURANCE_COMPANY_ID = "INSURANCE_COMPANY_ID";
	public static final String SPECIAL_SERVICING_FLAG = "SPECIAL_SERVICING_FLAG";
	public static final String NRZ_ACQUISITION_DT = "NRZ_ACQUISITION_DT";
	public static final String OLD_RR_LOAN_NUM = "OLD_RR_LOAN_NUM";
	public static final String PROP_TEMP = "PROP_TEMP";
	public static final String CLIENT_ID = "CLIENT_ID";
	public static final String LOAN_NUM = "LOAN_NUM";
	public static final String PHH_FLAG = "PHH_FLAG";

	@Inject
	@Named(value = "rrDataSource")
	private DataSource dataSource;

	@Inject
	private DPMigrationMapDao dpMigrationMapDao;

	@Inject
	private RRMigration rRMigration;

	@Inject
	private CommandDAO commandDAO;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;
	
	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;

	@Inject
	private CacheManager cacheManager;

	@Value("${WEEK0_CONCURRENT_DBCALL_POOL_SIZE}")
	private int concurrentWeek0DbCallPoolSize;

	private JdbcTemplate jdbcTemplate;

	private ExecutorService executorService;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentWeek0DbCallPoolSize);
	}

	@PreDestroy
	public void destroy() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	public void processTask(DPProcessParamEntryInfo dpProcessParamEntryInfo) throws SystemException {

		boolean reprocess = dpProcessParamEntryInfo.isReprocess();
		String rrMigrationQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_QUERY);
		Long startTime = System.currentTimeMillis();
		List<Future<KeyValue<String, DPProcessParamInfo>>> futureList = new ArrayList<>();

		for (DPProcessParamInfo obj : dpProcessParamEntryInfo.getColumnEntries()) {

			Future<KeyValue<String, DPProcessParamInfo>> keyValueFuture = executorService.submit(fetchWeek0MigrationInfo(rrMigrationQuery, obj));
			futureList.add(keyValueFuture);
		}

		for (Future<KeyValue<String, DPProcessParamInfo>> keyValueFuture : futureList) {
			try {
				KeyValue<String, DPProcessParamInfo> week0ParamInfoKeyValue = keyValueFuture.get();
				DPProcessParamInfo obj = week0ParamInfoKeyValue.getValue();
				if (obj != null && StringUtils.isNoneEmpty(obj.getAssetNumber(), obj.getPropTemp())) {
					DPMigrationMap dpMigrationMap = new DPMigrationMap();
					dpMigrationMap.setAssetNumber(obj.getAssetNumber());
					dpMigrationMap.setOldAssetNumber(obj.getOldAssetNumber());
					dpMigrationMap.setPropTemp(obj.getPropTemp());
					dpMigrationMapDao.deleteAll();
					dpMigrationMapDao.save(dpMigrationMap);
					rRMigration.retrospectUpdateMigrationInformation();
					dpMigrationMapDao.deleteAll();
				}

			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error("An error occurred while fetching rrMigration data ", e);
			}
		}
		LOGGER.info("Time taken for all Week0 RR Migration records : " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public void processQaReportRRTask(DPProcessWeekNParamEntryInfo weekNParamEntryInfo) {
		String rrClassificationQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_CLASSIFICATION_QUERY);
		List<Future<KeyValue<String, DPProcessWeekNParamInfo>>> futureList = new ArrayList<>();
		for (DPProcessWeekNParamInfo obj : weekNParamEntryInfo.getColumnEntries()) {
			Future<KeyValue<String, DPProcessWeekNParamInfo>> keyValueFuture = executorService
					.submit(fetchClassificationWeekNProperty(rrClassificationQuery, obj));
			futureList.add(keyValueFuture);
		}
		for (Future<KeyValue<String, DPProcessWeekNParamInfo>> keyValueFuture : futureList) {
			try {
				KeyValue<String, DPProcessWeekNParamInfo> weekNParamInfoKeyValue = keyValueFuture.get();
				String newClassification = weekNParamInfoKeyValue.getKey();
				DPProcessWeekNParamInfo dpProcessWeekNParamInfo = weekNParamInfoKeyValue.getValue();
				if (StringUtils.isNotBlank(newClassification)) {
					dpProcessWeekNParamInfo.setClassification(newClassification);
				}
			} catch (InterruptedException e) {
				LOGGER.error("Inturrupted in processQaReportRRTask {}", e);
			} catch (ExecutionException e) {
				LOGGER.error("Execution exception in processQaReportRRTask {}", e);
			}
		}
	}
	
	public void processSopQaReportRRTask(DPSopWeekNParamEntryInfo weekNParamEntryInfo) {
		String rrClassificationQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_CLASSIFICATION_QUERY);
		List<Future<KeyValue<String, DPSopWeekNParamInfo>>> futureList = new ArrayList<>();
		for (DPSopWeekNParamInfo obj : weekNParamEntryInfo.getColumnEntries()) {
			Future<KeyValue<String, DPSopWeekNParamInfo>> keyValueFuture = executorService
					.submit(fetchClassificationSOPWeekNProperty(rrClassificationQuery, obj));
			futureList.add(keyValueFuture);
		}
		for (Future<KeyValue<String, DPSopWeekNParamInfo>> keyValueFuture : futureList) {
			try {
				KeyValue<String, DPSopWeekNParamInfo> weekNParamInfoKeyValue = keyValueFuture.get();
				String newClassification = weekNParamInfoKeyValue.getKey();
				DPSopWeekNParamInfo dpProcessWeekNParamInfo = weekNParamInfoKeyValue.getValue();
				if (StringUtils.isNotBlank(newClassification)) {
					dpProcessWeekNParamInfo.setClassification(newClassification);
				}
			} catch (InterruptedException e) {
				LOGGER.error("Inturrupted in processSopQaReportRRTask {}", e);
			} catch (ExecutionException e) {
				LOGGER.error("Execution exception in processSopQaReportRRTask {}", e);
			}
		}
	}

	public void processWeekNTask(DPProcessWeekNParamEntryInfo weekNParamEntryInfo) throws SystemException {
		Long startTime = System.currentTimeMillis();
		boolean reprocess = weekNParamEntryInfo.isReprocess();
		List<DPProcessWeekNParamInfo> classifiedColumnEntries = new ArrayList<>();
		String rrClassificationQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_CLASSIFICATION_QUERY);
		List<Future<KeyValue<String, DPProcessWeekNParamInfo>>> futureList = new ArrayList<>();

		for (DPProcessWeekNParamInfo obj : weekNParamEntryInfo.getColumnEntries()) {
			Future<KeyValue<String, DPProcessWeekNParamInfo>> keyValueFuture = executorService
					.submit(fetchClassificationWeekNProperty(rrClassificationQuery, obj));
			futureList.add(keyValueFuture);
		}

		for (Future<KeyValue<String, DPProcessWeekNParamInfo>> keyValueFuture : futureList) {
			try {
				KeyValue<String, DPProcessWeekNParamInfo> weekNParamInfoKeyValue = keyValueFuture.get();
				String newClassification = weekNParamInfoKeyValue.getKey();
				DPProcessWeekNParamInfo dpProcessWeekNParamInfo = weekNParamInfoKeyValue.getValue();
				if (StringUtils.isNotBlank(newClassification)) {
					LOGGER.debug("Classification For LoanNumber " + dpProcessWeekNParamInfo.getAssetNumber() + " is " + newClassification);
					dpProcessWeekNParamInfo.setClassification(newClassification);
					classifiedColumnEntries.add(dpProcessWeekNParamInfo);
					if (reprocess) {
						dpProcessWeekNParamInfo.setAssignment(null);
						dpProcessWeekNParamInfo.setCommand(null);
						dpProcessWeekNParamInfo.setDeliveryDate(null);
						dpProcessWeekNParamInfo.setEligible(null);
						dpProcessWeekNParamInfo.setExclusionReason(null);
						dpProcessWeekNParamInfo.setHubzuDBResponse(null);
						dpProcessWeekNParamInfo.setRbidPropIdVcPk(null);
						dpProcessWeekNParamInfo.setSsPmiHubzuResponse(null);
						dpProcessWeekNParamInfo.setStageFiveDBResponse(null);
					}

					if (!reprocess && !weekNParamEntryInfo.isFetchProcess()) {
						dpProcessWeekNParamInfo.setSellerOccupiedProperty(null);
					}
				} else {
					List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN.getCommmandProcess(), DPAConstants.DATA_FETCH_FAILURE);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					dpProcessWeekNParamInfo.setCommand(commandInfo);
					dpProcessWeekNParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					dpProcessWeekNParamInfo.setDpWeekNProcessStatus(weekNParamEntryInfo.getDpWeeknProcessStatus());
					dpProcessWeekNParamInfo.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
					dpProcessWeekNParamInfo.setExclusionReason(DPProcessFilterParams.NOTES_RR_DB_FAIL.getValue());
					if (!reprocess && !weekNParamEntryInfo.isFetchProcess()) {
						dpProcessWeekNParamInfo.setClassification(null);
						dpProcessWeekNParamInfo.setSellerOccupiedProperty(null);
					}
					if (!weekNParamEntryInfo.isFetchProcess())
						dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(dpProcessWeekNParamInfo);
				}
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error("An error occurred while fetching rrClassification data ", e);
			}
		}
		LOGGER.info("Time taken for all RR classification records : " + (System.currentTimeMillis() - startTime) + "ms");
		if (!weekNParamEntryInfo.isFetchProcess())
			weekNParamEntryInfo.setColumnEntries(classifiedColumnEntries);
	}

	public void processSOPWeekNTask(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo) throws SystemException {
		Long startTime = System.currentTimeMillis();
		boolean reprocess = sopWeekNParamEntryInfo.isReprocess();
		List<DPSopWeekNParamInfo> classifiedColumnEntries = new ArrayList<>();
		String rrClassificationQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_CLASSIFICATION_QUERY);
		List<Future<KeyValue<String, DPSopWeekNParamInfo>>> futureList = new ArrayList<>();

		for (DPSopWeekNParamInfo obj : sopWeekNParamEntryInfo.getColumnEntries()) {
			Future<KeyValue<String, DPSopWeekNParamInfo>> keyValueFuture = executorService
					.submit(fetchClassificationSOPWeekNProperty(rrClassificationQuery, obj));
			futureList.add(keyValueFuture);
		}

		for (Future<KeyValue<String, DPSopWeekNParamInfo>> keyValueFuture : futureList) {
			try {
				KeyValue<String, DPSopWeekNParamInfo> weekNParamInfoKeyValue = keyValueFuture.get();
				String newClassification = weekNParamInfoKeyValue.getKey();
				DPSopWeekNParamInfo dpSopWeekNParamInfo = weekNParamInfoKeyValue.getValue();
				if (StringUtils.isNotBlank(newClassification)) {
					LOGGER.debug("Classification For LoanNumber " + dpSopWeekNParamInfo.getAssetNumber() + " is " + newClassification);
					dpSopWeekNParamInfo.setClassification(newClassification);
					classifiedColumnEntries.add(dpSopWeekNParamInfo);
					if (reprocess) {
						dpSopWeekNParamInfo.setAssignment(null);
						dpSopWeekNParamInfo.setFailedStepCommandName(null);
						dpSopWeekNParamInfo.setDeliveryDate(null);
						dpSopWeekNParamInfo.setEligible(null);
						dpSopWeekNParamInfo.setExclusionReason(null);
						dpSopWeekNParamInfo.setHubzuDBResponse(null);
						dpSopWeekNParamInfo.setRbidPropIdVcPk(null);
						dpSopWeekNParamInfo.setStageFiveDBResponse(null);
					}

					if (!reprocess && !sopWeekNParamEntryInfo.isFetchProcess()) {
						dpSopWeekNParamInfo.setSellerOccupiedProperty(null);
					}
				} else {
					dpSopWeekNParamInfo.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					dpSopWeekNParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					dpSopWeekNParamInfo.setSopWeekNProcessStatus(sopWeekNParamEntryInfo.getDpSopWeekNProcessStatus());
					dpSopWeekNParamInfo.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
					dpSopWeekNParamInfo.setExclusionReason(DPProcessFilterParams.NOTES_RR_DB_FAIL.getValue());
					if (!reprocess && !sopWeekNParamEntryInfo.isFetchProcess()) {
						dpSopWeekNParamInfo.setClassification(null);
						dpSopWeekNParamInfo.setSellerOccupiedProperty(null);
					}
					if (!sopWeekNParamEntryInfo.isFetchProcess())
						sopWeekNParamDelegate.saveSopWeekNParamInfo(dpSopWeekNParamInfo);
				}
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error("An error occurred while fetching rrClassification data {}", e);
				SystemException.newSystemException(CoreExceptionCodes.DPSOPWKN018, e.getMessage());
			}
		}
		LOGGER.info("Time taken for all RR classification records : " + (System.currentTimeMillis() - startTime) + "ms");
		if (!sopWeekNParamEntryInfo.isFetchProcess())
			sopWeekNParamEntryInfo.setColumnEntries(classifiedColumnEntries);
	}

	private Callable<KeyValue<String, DPProcessParamInfo>> fetchWeek0MigrationInfo(String rrMigrationQuery, DPProcessParamInfo obj) {
		return () -> {
			String oldAssetNumber = jdbcTemplate.execute(rrMigrationQuery, (PreparedStatementCallback<String>) ps -> {
				LOGGER.info("Inside doInPreparedStatement.");
				String oldAssetNumber1 = obj.getAssetNumber();
				try {
					ps.setString(1, obj.getAssetNumber());
					ps.setString(2, obj.getAssetNumber());
					ResultSet rs = ps.executeQuery();
					if (null != rs && rs.next()) {
						if (null != rs.getString(PROP_TEMP)) {
							obj.setPropTemp(StringUtils.trim(rs.getString(PROP_TEMP)));
						}
						if (null != rs.getString(OLD_RR_LOAN_NUM)) {
							obj.setOldAssetNumber(StringUtils.trim(rs.getString(OLD_RR_LOAN_NUM)));
						}
						if (null != rs.getString(LOAN_NUM)) {
							obj.setAssetNumber(StringUtils.trim(rs.getString(LOAN_NUM)));
						}
					}
				} catch (SQLException sqle) {
					LOGGER.info(sqle.getLocalizedMessage(), sqle);
					String errorDetail = dpFileProcessBO
							.saveDPProcessErrorDetail(obj.getId(), IntegrationType.RR_INTEGRATION.getIntegrationType(), obj.getErrorDetail(), sqle);
					obj.setErrorDetail(errorDetail);
				}
				return oldAssetNumber1;
			});
			return new KeyValue<>(oldAssetNumber, obj);
		};
	}

	private Callable<KeyValue<String, DPProcessWeekNParamInfo>> fetchClassificationWeekNProperty(final String rrClassificationQuery,
			final DPProcessWeekNParamInfo dpProcessWeekNParamInfo) {
		return () -> {
			String newClassification = jdbcTemplate.execute(rrClassificationQuery, (PreparedStatementCallback<String>) ps -> {
				LOGGER.info("Inside doInPreparedStatement.");
				String newClassification1 = null;
				try {
					ps.setString(1, dpProcessWeekNParamInfo.getPropTemp());
					ResultSet rs = ps.executeQuery();
					if (null != rs && rs.next()) {
						dpProcessWeekNParamInfo.setClientCode(null != rs.getString(CLIENT_ID) ? rs.getString(CLIENT_ID) : null);

						dpProcessWeekNParamInfo.setPmiCompanyInsuranceId(rs.getString(INSURANCE_COMPANY_ID));

						if (rs.getInt(PMI_FLAG) == 1)
							dpProcessWeekNParamInfo.setPrivateMortgageInsurance(RAClientConstants.YES);
						else
							dpProcessWeekNParamInfo.setPrivateMortgageInsurance(RAClientConstants.NO);

						if (StringUtils.equalsIgnoreCase(rs.getString(SPECIAL_SERVICING_FLAG), RAClientConstants.YES))
							dpProcessWeekNParamInfo.setSpecialServicingFlag(RAClientConstants.YES);
						else
							dpProcessWeekNParamInfo.setSpecialServicingFlag(RAClientConstants.NO);

						if (StringUtils.equalsIgnoreCase(rs.getString(PHH_FLAG), RAClientConstants.YES))
							newClassification1 = DPProcessParamAttributes.PHH.getValue();
						else if (null != rs.getDate(NRZ_ACQUISITION_DT))
							newClassification1 = DPProcessParamAttributes.NRZ.getValue();
						else
							newClassification1 = DPProcessParamAttributes.OCN.getValue();

						if (null != rs.getString(OLD_RR_LOAN_NUM) && !StringUtils
								.equalsIgnoreCase(StringUtils.trim(rs.getString(PROP_TEMP)), StringUtils.trim(rs.getString(OLD_RR_LOAN_NUM)))
								&& dpProcessWeekNParamInfo.getOldAssetNumber() == null) {
							dpProcessWeekNParamInfo.setOldAssetNumber(StringUtils.trim(rs.getString(OLD_RR_LOAN_NUM)));
						}
					}

				} catch (SQLException sqle) {
					LOGGER.info(sqle.getLocalizedMessage(), sqle);
				}
				return newClassification1;
			});

			return new KeyValue<>(newClassification, dpProcessWeekNParamInfo);
		};

	}

	private Callable<KeyValue<String, DPSopWeekNParamInfo>> fetchClassificationSOPWeekNProperty(final String rrClassificationQuery,
			final DPSopWeekNParamInfo dpSopWeekNParamInfo) {
		return () -> {
			String newClassification = jdbcTemplate.execute(rrClassificationQuery, (PreparedStatementCallback<String>) ps -> {
				LOGGER.info("Inside doInPreparedStatement.");
				String classification = null;
				try {
					ps.setString(1, dpSopWeekNParamInfo.getPropTemp());
					ResultSet rs = ps.executeQuery();
					if (null != rs && rs.next()) {
						dpSopWeekNParamInfo.setClientCode(null != rs.getString(CLIENT_ID) ? rs.getString(CLIENT_ID) : null);

						dpSopWeekNParamInfo.setPmiCompanyInsuranceId(rs.getString(INSURANCE_COMPANY_ID));

						if (rs.getInt(PMI_FLAG) == 1)
							dpSopWeekNParamInfo.setPrivateMortgageInsurance(RAClientConstants.YES);
						else
							dpSopWeekNParamInfo.setPrivateMortgageInsurance(RAClientConstants.NO);

						if (StringUtils.equalsIgnoreCase(rs.getString(SPECIAL_SERVICING_FLAG), RAClientConstants.YES))
							dpSopWeekNParamInfo.setSpecialServicingFlag(RAClientConstants.YES);
						else
							dpSopWeekNParamInfo.setSpecialServicingFlag(RAClientConstants.NO);

						if (StringUtils.equalsIgnoreCase(rs.getString(PHH_FLAG), RAClientConstants.YES))
							classification = DPProcessParamAttributes.PHH.getValue();
						else if (null != rs.getDate(NRZ_ACQUISITION_DT))
							classification = DPProcessParamAttributes.NRZ.getValue();
						else
							classification = DPProcessParamAttributes.OCN.getValue();

						if (null != rs.getString(OLD_RR_LOAN_NUM) && !StringUtils
								.equalsIgnoreCase(StringUtils.trim(rs.getString(PROP_TEMP)), StringUtils.trim(rs.getString(OLD_RR_LOAN_NUM)))
								&& dpSopWeekNParamInfo.getOldAssetNumber() == null) {
							dpSopWeekNParamInfo.setOldAssetNumber(StringUtils.trim(rs.getString(OLD_RR_LOAN_NUM)));
						}
					}

				} catch (SQLException sqle) {
					LOGGER.info("SQL exception {}", sqle);
				}
				return classification;
			});

			return new KeyValue<>(newClassification, dpSopWeekNParamInfo);
		};

	}
}