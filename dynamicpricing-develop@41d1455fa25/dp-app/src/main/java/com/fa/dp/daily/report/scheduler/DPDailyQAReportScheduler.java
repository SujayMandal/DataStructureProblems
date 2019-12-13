package com.fa.dp.daily.report.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTimeUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.weekn.report.delegate.WeekNDailyQAReportDelegate;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.delegate.WeekNDailyRunStatusDelegate;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.qa.constant.DPQaReportConstant;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppType;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.core.util.RAClientUtil;

@Named
@Slf4j
public class DPDailyQAReportScheduler {

	@Inject
	private WeekNDailyRunStatusDelegate weekNDailyRunStatusDelegate;

	@Inject
	private WeekNDailyQAReportDelegate weekNDailyQAReportDelegate;

	@Inject
	private RRMigration rRMigration;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Value("${qa.daily.report.enable}")
	private Boolean dailyReportEnable;


	/**
	 * Find property that gets listed after our last run time.
	 * We are checking for timely reduction when due.
	 */

	@Scheduled(cron = "${qa.daily.report.delay.cron}")
	public void populateDailyQAReport() {

		boolean reportStatus = Boolean
				.parseBoolean(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_QA_REPORT_SCHEDULE_STATUS));

		if (dailyReportEnable && reportStatus) {
			MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());

			String trace = null;
			WeekNDailyRunStatusInfo dailyRunStatusInfo = null;
			List<String> failedLoanNumbers = new ArrayList<>();

			final List<WeekNDailyQAReportInfo> validQaReportListAfterReduction;

			try {
				//process daily run between last run date and current date

				long startTime = DateTimeUtils.currentTimeMillis();

				LocalDate lastRunDate = weekNDailyRunStatusDelegate.getLastRunDate();

				HubzuDBResponse hubzuinfo;
				if (Objects.isNull(lastRunDate)) {
					//make last run date as 1 day before for 1st time
					lastRunDate = LocalDate.now().minusDays(1);
				}

				log.info("Last run date is : {}", lastRunDate);

				//Fetch hubzu query for date range
				//Fetch all properties from Hubzu WHERE
				//List Start Date > last run date
				//list status != ( Cancelled / Disapproved)
				//SOP status != N
				LocalDate weeknLastRunDate = weekNDailyRunStatusDelegate.getLatestWekNRunDate();
				log.info("weeknLastRunDate : {}", weeknLastRunDate);
				Boolean sopStatus = false;
				hubzuinfo = weekNDailyRunStatusDelegate.fetchQaReportHubzuResponse(lastRunDate, weeknLastRunDate, sopStatus);

				//perform migration on hubzu loans
				List<HubzuInfo> migratedResponse = migrationQuery(hubzuinfo, sopStatus);

				Map<String, Integer> validListingCount = new HashMap<>();

				Map<String, List<HubzuInfo>> hubzuDataSelrIdMap = new HashMap<>();
				Map<String, List<HubzuInfo>> hubzuNonMigratedDataSelrIdMap = new HashMap<>();

				hubzuinfo.getHubzuInfos().stream().forEach(info -> {
					if (!hubzuNonMigratedDataSelrIdMap.containsKey(info.getSelrPropIdVcNn())) {
						hubzuNonMigratedDataSelrIdMap.put(info.getSelrPropIdVcNn(), new ArrayList<>());
					}
					String key = StringUtils
							.join(new Object[] { info.getSelrPropIdVcNn(), info.getListEndDateDtNn() }, RAClientConstants.CHAR_HYPHEN);
					hubzuNonMigratedDataSelrIdMap.get(info.getSelrPropIdVcNn()).add(info);
					validListingCount.put(key, 0);
					hubzuDataSelrIdMap.put(key, new ArrayList<>());
				});

				//Count valid listings
				//after removing Cancelled / Disapproved and SOP status = Y
				//Only listing after the most recent non duplicate record in week 0; old and new property ids combined
				log.debug("hubzu info list count : {}", CollectionUtils.isNotEmpty(migratedResponse) ? migratedResponse.size() : 0);
				if (CollectionUtils.isNotEmpty(migratedResponse)) {
					migratedResponse.forEach(migratedData -> {
						List<HubzuInfo> hubzuNonMigrated = hubzuNonMigratedDataSelrIdMap.get(migratedData.getSelrPropIdVcNn());

						if (CollectionUtils.isNotEmpty(hubzuNonMigrated)) {
							hubzuNonMigrated.forEach(hubzuData -> {

								log.debug("hubzu data : listEndDateDtNn : {}, assignmentDate : {}", hubzuData.getListEndDateDtNn(),
										hubzuData.getAssignmentDate());
								log.debug("migrated data : listEndDateDtNn : {}, listStrtDateDtNn : {}", migratedData.getListEndDateDtNn(),
										migratedData.getListStrtDateDtNn());

								if (hubzuData != null && migratedData.getListEndDateDtNn() != null && hubzuData.getListEndDateDtNn() != null
										&& migratedData.getListEndDateDtNn().compareTo(hubzuData.getListEndDateDtNn()) <= 0
										&& migratedData.getListStrtDateDtNn() != null && (hubzuData.getAssignmentDate() == null
										|| migratedData.getListStrtDateDtNn().compareTo(hubzuData.getAssignmentDate()) >= 0)) {
									String key = StringUtils.join(new Object[] { migratedData.getSelrPropIdVcNn(), hubzuData.getListEndDateDtNn() },
											RAClientConstants.CHAR_HYPHEN);
									if (!validListingCount.containsKey(key)) {
										validListingCount.put(key, 1);
									} else {
										validListingCount.put(key, validListingCount.get(key) + 1);
									}
									if (!hubzuDataSelrIdMap.containsKey(key)) {
										hubzuDataSelrIdMap.put(key, new ArrayList<>());
									}
									hubzuDataSelrIdMap.get(key).add(migratedData);
								}
							});
						}
					});
				}

				log.debug("listing count data : {}", validListingCount.toString());

				hubzuDataSelrIdMap.forEach((k, v) -> {
					v.sort(Comparator.comparing(HubzuInfo::getListStrtDateDtNn).reversed());
					v.forEach(c -> {
						c.setCurrentListStrtDate(DateConversionUtil.US_DATE_TIME_FORMATTER.print(c.getListStrtDateDtNn().getTime()));
						c.setCurrentListEndDate(DateConversionUtil.US_DATE_TIME_FORMATTER.print(c.getListEndDateDtNn().getTime()));
					});
				});

				final List<HubzuInfo> validHubzuInfos = new ArrayList<>();
				final List<HubzuInfo> inValidHubzuInfos = new ArrayList<>();

				//Discard loans where count of valid listings is greater than 2 & even
				hubzuinfo.getHubzuInfos().forEach(info -> {
					String key = StringUtils
							.join(new Object[] { info.getSelrPropIdVcNn(), info.getListEndDateDtNn() }, RAClientConstants.CHAR_HYPHEN);
					Integer count = validListingCount.get(key);
					if (count == null || count == 0 || count % 2 == 1) {
						inValidHubzuInfos.add(info);
					} else {
						validHubzuInfos.add(info);
					}
				});
				log.debug("valid listing count is : {}. invalid listing count is : {}", validHubzuInfos.size(), inValidHubzuInfos.size());

				List<WeekNDailyQAReportInfo> qaReportList = new ArrayList<>();

				validHubzuInfos.forEach(info -> {
					WeekNDailyQAReportInfo qaReport = new WeekNDailyQAReportInfo();
					qaReport.setSelrPropIdVcNn(info.getSelrPropIdVcNn());
					qaReport.setRbidPropIdVcPk(info.getRbidPropIdVcPk());
					qaReport.setOldPropId(info.getOldPropId());
					qaReport.setOldLoanNumber(info.getOldLoanNumber());
					qaReport.setReoPropSttsVc(info.getReoPropSttsVc());
					qaReport.setPropSoldDateDt(info.getPropSoldDateDt());
					qaReport.setPropSttsIdVcFk(info.getPropSttsIdVcFk());
					qaReport.setRbidPropListIdVcPk(info.getRbidPropListIdVcPk());
					qaReport.setListTypeIdVcFk(info.getListTypeIdVcFk());
					//qaReport.setCurrentListStartDate(DateConversionUtil.US_DATE_TIME_FORMATTER.print(info.getListStrtDateDtNn().getTime()));
					//qaReport.setCurrentListEndDate(DateConversionUtil.US_DATE_TIME_FORMATTER.print(info.getListEndDateDtNn().getTime()));

					qaReport.setCurrentListStartDate(
							LocalDate.parse(DateConversionUtil.US_SIMPLE_DATE_TIME_FORMATTER.format(info.getListStrtDateDtNn())));
					qaReport.setCurrentListEndDate(
							LocalDate.parse(DateConversionUtil.US_SIMPLE_DATE_TIME_FORMATTER.format(info.getListEndDateDtNn())));

					qaReport.setListPriceNt(info.getListPrceNt());
					qaReport.setListSttsDtlsVc(info.getListSttsDtlsVc());
					qaReport.setOccpncySttsAtLstCreatn(info.getOccpncySttsAtLstCreatn());
					qaReport.setClassification(DPAConstants.ACCNT_ID_CLASSIFICATION_MAP.get(info.getSelrAcntIdVcFk()));

					qaReportList.add(qaReport);

				});

				//populatre previous listing record
				List<WeekNDailyQAReportInfo> qaReportListWithPreviousListingData = weekNDailyRunStatusDelegate
						.populatePreviousListingData(qaReportList, hubzuDataSelrIdMap);

				//For remaining loans, check if a reduction was given (delivery date in local Week N DB)
				//between previous List End date and current List Start Date

				//timely reduction - successful
				//if not check for the reason in Audit table
				//	valid reason - successful
				//		SS, PMI, PR State : (OCN, NRZ and PHH)
				//		Week 0 not run, Benchmarked, Past 12 cycles : (OCN and PHH)
				//	No Valid reason - Exception
				validQaReportListAfterReduction = weekNDailyRunStatusDelegate.checkReduction(qaReportListWithPreviousListingData, sopStatus);

				//Rule voilation logic
				validQaReportListAfterReduction.forEach(data -> {
					data.setRuleViolation(Boolean.FALSE.toString());
					if (StringUtils.equals(data.getClassification(), DPProcessParamAttributes.NRZ.getValue()) && NumberUtils
							.isParsable(data.getPctPriceChangeFrmLastList())
							&& Math.abs(org.springframework.util.NumberUtils.parseNumber(data.getPctPriceChangeFrmLastList(), Double.class))
							< 0.005) {
						data.setRuleViolation(Boolean.TRUE.toString());
					}
				});

				final List<String> successLoanNumbers = validQaReportListAfterReduction.stream().filter(info -> Boolean.TRUE.equals(info.getStatus()))
						.map(a -> a.getSelrPropIdVcNn()).collect(Collectors.toList());

				failedLoanNumbers = validQaReportListAfterReduction.stream().filter(info -> !Boolean.TRUE.equals(info.getStatus()))
						.map(a -> a.getSelrPropIdVcNn()).collect(Collectors.toList());

				//Store data into DB
				dailyRunStatusInfo = saveWeeknQAReport(startTime, failedLoanNumbers.size(), validQaReportListAfterReduction, lastRunDate,
						weeknLastRunDate);

			} catch (Exception e) {
				log.error("QA Daily report failed {}", e);
				trace = ExceptionUtils.getStackTrace(e);
			}

			try {
				weekNDailyRunStatusDelegate.notifyDailyRunStatus(dailyRunStatusInfo, trace, failedLoanNumbers);
			} catch (SystemException e) {
				log.error("Problem in sending mail for daily qa report {}", e);
			}
		}
	}

	/**
	 * Save weekn qa report
	 *
	 * @param startTime
	 * @param failureCount
	 * @param validQaReportListAfterReduction
	 * @param lastRunDate
	 * @param weeknLastRunDate
	 */
	private WeekNDailyRunStatusInfo saveWeeknQAReport(long startTime, int failureCount, List<WeekNDailyQAReportInfo> validQaReportListAfterReduction,
			LocalDate lastRunDate, LocalDate weeknLastRunDate) throws SystemException {
		WeekNDailyRunStatusInfo runStatusInfo = new WeekNDailyRunStatusInfo();
		runStatusInfo.setStartTime(startTime);
		runStatusInfo.setEndTime(DateTimeUtils.currentTimeMillis());
		runStatusInfo.setTotalRecord(validQaReportListAfterReduction.size());
		runStatusInfo.setSuccessCount(validQaReportListAfterReduction.size() - failureCount);
		runStatusInfo.setFailCount(failureCount);
		runStatusInfo.setLastRunDate(LocalDateTime.now(ZoneId.of(RAClientUtil.EST_TIME_ZONE)));

		/*runStatusInfo.setLastRunDate(LocalDateTime
				.from(DateConversionUtil.LOCAL_DATE_TIME_FORMATTER_RUN_DATE.withZone(ZoneId.of(RAClientUtil.EST_TIME_ZONE)).parse(weeknLastRunDate)));*/
		runStatusInfo.setFetchStartDate(lastRunDate);
		runStatusInfo.setFetchEndDate(weeknLastRunDate);
		runStatusInfo.setReportType(DPQaReportConstant.QA_REPORT);

		try {
			weekNDailyRunStatusDelegate.saveWeekNQaReport(validQaReportListAfterReduction, runStatusInfo);
		} catch (SystemException e) {
			log.error("qa report task : weekn qa report save failed. {}", e);
			throw e;
		}

		return runStatusInfo;
	}

	/**
	 * Migration logic for hubzu response
	 *
	 * @param hubzuinfo
	 * @return
	 */
	private List<HubzuInfo> migrationQuery(HubzuDBResponse hubzuinfo, Boolean sopStatus) {
		final Map<String, String> migrationNewPropToPropMap = new HashMap<>();
		final Map<String, String> migrationPropToLoanMap = new HashMap<>();

		List<String> topAssetsFromHbz = hubzuinfo.getHubzuInfos().stream().map(hbzData -> hbzData.getSelrPropIdVcNn()).collect(Collectors.toList());
		List<String> topAssetsRbidFromHbz = hubzuinfo.getHubzuInfos().stream().map(hbzData -> hbzData.getRbidPropIdVcFk())
				.collect(Collectors.toList());

		rRMigration.getMigrationMaps(migrationNewPropToPropMap, migrationPropToLoanMap, topAssetsFromHbz);

		final Map<String, String> migrationPropToNewPropMap = migrationNewPropToPropMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		log.info("migrationNewPropToPropMap : {}", migrationNewPropToPropMap.toString());
		log.info("migrationPropToLoanMap : {}", migrationPropToLoanMap.toString());

		weekNDailyQAReportDelegate.prepareAssignmentDate(hubzuinfo, migrationPropToLoanMap);

		//migrationPropToNewPropMap key is old loan and value is new loan

		//New prop temp
		//String newProp = migrationPropToNewPropMap.get(topAssetsFromHbz.get(0));

		//**********
		//String newLoanNumber = migrationPropToLoanMap.get(newProp);

		/*
		 * fetch loan from hubzu and get list end date and selrPropId
		 * get assignment date from week0
		 *
		 * call hubzu where list start date >= assignment date and list end date <= (list end date from 1st step) and
		 * selrpropid = (selrPropId from 1st step)
		 *
		 * */

		Map<String, String> oldAssetToNewAssetNumberMap = new HashMap<>();

		/*hubzuinfo.getHubzuInfos().stream().filter(info -> migrationPropToNewPropMap.containsKey(info.getSelrPropIdVcNn())).forEach(
				info -> oldAssetToNewAssetNumberMapp
						.put(info.getSelrPropIdVcNn(), migrationPropToLoanMap.get(migrationPropToNewPropMap.get(info.getSelrPropIdVcNn()))));*/

		hubzuinfo.getHubzuInfos().stream().filter(info -> migrationNewPropToPropMap.containsKey(info.getSelrPropIdVcNn())).forEach(
				info -> oldAssetToNewAssetNumberMap
				.put(migrationPropToLoanMap.get(migrationNewPropToPropMap.get(info.getSelrPropIdVcNn())), info.getSelrPropIdVcNn()));

		Map<String, String> newAssetToOldAssetNumberMap = oldAssetToNewAssetNumberMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		hubzuinfo.getHubzuInfos().stream().forEach(a -> {
			a.setOldPropId(migrationNewPropToPropMap.get(a.getSelrPropIdVcNn()));
			a.setOldLoanNumber(newAssetToOldAssetNumberMap.get(a.getSelrPropIdVcNn()));
		});

		//call 2nd hubzu query with new migrated loan
		List<String> assetNumberWithMigratedAssetNumber = new ArrayList<>();
		assetNumberWithMigratedAssetNumber.addAll(topAssetsRbidFromHbz);
		assetNumberWithMigratedAssetNumber.addAll(oldAssetToNewAssetNumberMap.values());

		List<HubzuInfo> migratedData = null;
		try {
			migratedData = weekNDailyRunStatusDelegate.getMigratedHubzuResponse(assetNumberWithMigratedAssetNumber, migrationNewPropToPropMap, sopStatus);
			log.info("Migration selrPropID : {}", CollectionUtils.isNotEmpty(migratedData) ?
					migratedData.stream().map(a -> a.getSelrPropIdVcNn()).collect(Collectors.toList()).toString() :
						RAClientConstants.CHAR_EMPTY);
		} catch (SystemException e) {
			log.error("Migrated hubzu query failed in daily qa report.");
		}

		if (migratedData != null) {
			migratedData.stream().forEach(a -> {
				a.setOldPropId(migrationNewPropToPropMap.get(a.getSelrPropIdVcNn()));
				a.setOldLoanNumber(newAssetToOldAssetNumberMap.get(a.getSelrPropIdVcNn()));
			});
			migratedData.stream().filter(info -> migrationPropToNewPropMap.containsKey(info.getSelrPropIdVcNn()))
			.forEach(info -> info.setSelrPropIdVcNn(migrationPropToNewPropMap.get(info.getSelrPropIdVcNn())));
		}

		hubzuinfo.getHubzuInfos().stream().filter(info -> migrationPropToNewPropMap.containsKey(info.getSelrPropIdVcNn()))
		.forEach(info -> info.setSelrPropIdVcNn(migrationPropToNewPropMap.get(info.getSelrPropIdVcNn())));

		log.info("Migration selrPropID after merging: {}", CollectionUtils.isNotEmpty(migratedData) ?
				migratedData.stream().map(a -> a.getSelrPropIdVcNn()).collect(Collectors.toList()).toString() :
					RAClientConstants.CHAR_EMPTY);

		return migratedData;

		/*hubzuinfo.getHubzuInfos().stream().filter(hubzuInfo -> migrationPropToNewPropMap.containsKey(hubzuInfo.getSelrPropIdVcNn()))
				.forEach(hubzuInfo -> hubzuInfo.setSelrPropIdVcNn(migrationPropToNewPropMap.get(hubzuInfo.getSelrPropIdVcNn())));*/
	}

}