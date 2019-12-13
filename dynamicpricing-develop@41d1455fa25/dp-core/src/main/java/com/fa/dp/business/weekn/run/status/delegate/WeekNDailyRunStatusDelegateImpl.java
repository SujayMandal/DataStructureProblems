package com.fa.dp.business.weekn.run.status.delegate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fa.dp.business.audit.bo.DPAuditReportBO;
import com.fa.dp.business.audit.entity.DPWeekNAuditReports;
import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.bo.WeekNDailyRunStatusBO;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Named
@Slf4j
public class WeekNDailyRunStatusDelegateImpl implements WeekNDailyRunStatusDelegate {

	@Inject
	private WeekNDailyRunStatusBO weekNDailyRunStatusBO;

	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private DPAuditReportBO dpAuditReportBO;

	@Inject
	@Named("dpCommandMaster")
	private CommandMaster commandMaster;

	@Override
	public LocalDate getLastRunDate() throws SystemException {
		return weekNDailyRunStatusBO.getLastRunDate();
	}
	@Override
	public HubzuDBResponse fetchQaReportHubzuResponse(LocalDate lastRunDate, LocalDate weeknLastRunDate, Boolean sopStatus) throws SystemException {
		return weekNDailyRunStatusBO.fetchQaReportHubzuResponse(lastRunDate, weeknLastRunDate, sopStatus);
	}

	@Override
	public List<WeekNDailyQAReportInfo> checkReduction(List<WeekNDailyQAReportInfo> weekNDailyQAReportInfoList, Boolean sopStatus) {
		log.debug("checkReduction start.");
		List<WeekNDailyQAReportInfo> qaReportInfoList = new ArrayList<>();

		log.info("Reduction : weekn daily qa report info size : {}", weekNDailyQAReportInfoList.size());

		List<WeekNDailyQAReportInfo> nonReductionQAReportInfoList = new ArrayList<>();

		weekNDailyQAReportInfoList.forEach(c -> {
			DPProcessWeekNParamInfo data = dpProcessWeekNParamsBO
					.checkReduction(c.getSelrPropIdVcNn(), c.getOldLoanNumber(), c.getCurrentListEndDate());

			if (Objects.nonNull(data)) {
				//c.setActualListCycle();
				c.setWeeknRecommendedListPriceReduction(data.getLpDollarAdjustmentRec() != null ? data.getLpDollarAdjustmentRec().toString() : null);
				if(data.getDeliveryDate() == null) {
					data.setDeliveryDate(data.getLastModifiedDate().getMillis());
				}
				c.setWeeknRecommendedDate(data.getDeliveryDate() != null ? data.getDeliveryDate().toString() : null);
				c.setWeeknExclusionReason(data.getExclusionReason());

				String pctPriceChangeFrmLastList;
				if(data.getLpDollarAdjustmentRec() != null && c.getPreviousListPrice() != null) {
					pctPriceChangeFrmLastList = String.valueOf(data.getLpDollarAdjustmentRec().divide(new BigDecimal(c.getPreviousListPrice()), 5, RoundingMode.CEILING));
				} else {
					pctPriceChangeFrmLastList = "0";
				}

				c.setPctPriceChangeFrmLastList(pctPriceChangeFrmLastList);
				//c.setRuleViolation();
				//c.setWeeknMissingreport(Boolean.TRUE.toString());
				c.setClassification(data.getClassification());
			} else {
				log.error("No record found in weekn db for sele prop id : {}, current list end date :{}", c.getSelrPropIdVcNn(),
						c.getCurrentListEndDate());
			}

			boolean reduction = (data != null && data.getLpDollarAdjustmentRec() != null
					&& data.getLpDollarAdjustmentRec().compareTo(new BigDecimal("0")) < 0) ? true : false;
			log.info("Reduction : {}", reduction);
			if (reduction) {
				c.setStatus(true);
				c.setWeeknMissingreport(Boolean.FALSE.toString());
				c.setWeeknExclusionReason(null);
				qaReportInfoList.add(c);
			} else {
				//use hubzu command to find exclusion reason
				nonReductionQAReportInfoList.add(c);
				//findExclusionReason(c, data, c.getCurrentListEndDate());
				//c.setStatus(false);
			}
		});

		prepareReductionTask(nonReductionQAReportInfoList, sopStatus);

		qaReportInfoList.addAll(nonReductionQAReportInfoList);

		log.debug("checkReduction end.");
		return qaReportInfoList;
	}

	private void prepareReductionTask(List<WeekNDailyQAReportInfo> nonReductionQAReportInfoList, Boolean sopStatus) {
		DPProcessWeekNParamEntryInfo paramEntryInfo = new DPProcessWeekNParamEntryInfo();
		List<DPProcessWeekNParamInfo> columnEntries = new ArrayList<>();

		nonReductionQAReportInfoList.stream().forEach(info -> {
			DPProcessWeekNParamInfo paramInfo = new DPProcessWeekNParamInfo();
			paramInfo.setAssetNumber(info.getSelrPropIdVcNn());
			paramInfo.setClassification(info.getClassification());
			paramInfo.setPropTemp(info.getSelrPropIdVcNn());
			paramInfo.setOldAssetNumber(info.getOldLoanNumber());
			paramInfo.setMostRecentListEndDate(info.getCurrentListEndDate().format(DateTimeFormatter.ofPattern(DateConversionUtil.DATE_DD_MMM_YY)));
			columnEntries.add(paramInfo);
		});

		paramEntryInfo.setColumnEntries(columnEntries);
		commandMaster.filterQaReport(paramEntryInfo, sopStatus);

		Map<String, String> exclusionMap = new HashMap<>();
		Map<String, Boolean> exclusionBooleanMap = new HashMap<>();
		paramEntryInfo.getColumnEntries().stream().forEach(a -> {
			exclusionMap.put(a.getPropTemp(), a.getExclusionReason());
			if (StringUtils.equalsAny(a.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
					DPProcessParamAttributes.PHH.getValue()) && StringUtils
					.equalsAny(a.getExclusionReason(), DPProcessFilterParams.PMI.getValue(), DPProcessFilterParams.SPECIAL_SERVICE.getValue(),
							DPProcessFilterParams.STATE_LAW.getValue().replace("#", "State :PR"))) {
				exclusionBooleanMap.put(a.getPropTemp(), Boolean.TRUE);
			} else if (StringUtils.equalsAny(a.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.PHH.getValue())
					&& StringUtils.equalsAny(a.getExclusionReason(), DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue(),
					DPProcessFilterParams.ASSIGNMENT_BENCHMARK.getValue(), DPProcessFilterParams.PAST_12_CYCLES_EXCLUSION.getValue())) {
				exclusionBooleanMap.put(a.getPropTemp(), Boolean.TRUE);
			} else {
				exclusionBooleanMap.put(a.getPropTemp(), Boolean.FALSE);
			}
		});

		log.info("exclusionReasonMap value : {}", exclusionMap);
		log.info("exclusionBooleanMap value : {}", exclusionBooleanMap);

		nonReductionQAReportInfoList.stream().forEach(c -> {
			//if(StringUtils.isBlank(exclusionMap.get(c.getSelrPropIdVcNn()))) {
			if (BooleanUtils.isTrue(exclusionBooleanMap.get(c.getSelrPropIdVcNn()))) {
				c.setStatus(true);
				c.setWeeknExclusionReason(exclusionMap.get(c.getSelrPropIdVcNn()));
				c.setWeeknMissingreport(Boolean.FALSE.toString());
			} else {
				c.setStatus(false);
				c.setWeeknMissingreport(Boolean.TRUE.toString());
				/*if (StringUtils.isEmpty(c.getWeeknExclusionReason())) {
					c.setWeeknExclusionReason(exclusionMap.get(c.getSelrPropIdVcNn()));
				}*/
			}
		});
	}

	private void findExclusionReason(WeekNDailyQAReportInfo c, DPProcessWeekNParamInfo data, LocalDate currentListEndDate) {

		/*if (StringUtils
				.equalsAny(data.getExclusionReason(), DPProcessFilterParams.SPECIAL_SERVICE.getValue(), DPProcessFilterParams.PMI.getValue())) {
			c.setStatus(true);
			c.setWeeknExclusionReason(null);
			c.setWeeknMissingreport(Boolean.FALSE.toString());
		} else if (StringUtils.equalsAny(data.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.PHH.getValue())
				&& StringUtils.equalsAny(data.getExclusionReason(), DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue(),
				DPProcessFilterParams.ASSIGNMENT_BENCHMARK.getValue(), DPProcessFilterParams.PAST_12_CYCLES_EXCLUSION.getValue())) {

		}*/

		DPWeekNAuditReports auditReport = dpAuditReportBO
				.findExclusionReason((data != null) ? data.getPropTemp() : c.getSelrPropIdVcNn(), currentListEndDate);
		log.info("Audit report for asset number :{} and current list end date date : {} is : {}",
				(data != null) ? data.getPropTemp() : c.getSelrPropIdVcNn(), currentListEndDate, auditReport);
		if (auditReport != null) {
			if (StringUtils
					.equalsAny(auditReport.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
							DPProcessParamAttributes.PHH.getValue()) && StringUtils
					.equalsAny(auditReport.getAction(), DPProcessFilterParams.PMI.getValue(), DPProcessFilterParams.SPECIAL_SERVICE.getValue(),
							DPProcessFilterParams.STATE_LAW.getValue().replace("#", "State :PR"))) {
				c.setStatus(true);
				c.setWeeknExclusionReason(null);
				c.setWeeknMissingreport(Boolean.FALSE.toString());
			} else if (StringUtils
					.equalsAny(auditReport.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.PHH.getValue())
					&& StringUtils.equalsAny(auditReport.getAction(), DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue(),
					DPProcessFilterParams.ASSIGNMENT_BENCHMARK.getValue(), DPProcessFilterParams.PAST_12_CYCLES_EXCLUSION.getValue())) {
				c.setStatus(true);
				c.setWeeknExclusionReason(null);
				c.setWeeknMissingreport(Boolean.FALSE.toString());
			} else {
				c.setStatus(false);
				c.setWeeknMissingreport(Boolean.TRUE.toString());
				if (StringUtils.isEmpty(c.getWeeknExclusionReason())) {
					c.setWeeknExclusionReason(auditReport.getAction());
				}
			}
		} else {
			c.setStatus(false);
			c.setWeeknMissingreport(Boolean.TRUE.toString());
		}
	}

	@Override
	public List<WeekNDailyQAReportInfo> populatePreviousListingData(List<WeekNDailyQAReportInfo> weekNDailyQAReportList,
			Map<String, List<HubzuInfo>> hubzuDataSelrIdMap) {
		log.debug("populatePreviousListingData start.");
		/*List<String> sellerPropertyIds = weekNDailyQAReportList.stream().map(a -> a.getSelrPropIdVcNn()).collect(Collectors.toList());

		List<WeekNDailyQAReportInfo> previousListReport = weekNDailyRunStatusBO.fetchPreviousListingDataBySellerrPropertyId(sellerPropertyIds);
		Map<String, List<WeekNDailyQAReportInfo>> qaReportMap = new HashMap<>();

		previousListReport.forEach(c -> {
			if (!qaReportMap.containsKey(c.getSelrPropIdVcNn())) {
				qaReportMap.put(c.getSelrPropIdVcNn(), new ArrayList<>());
			}
			qaReportMap.get(c.getSelrPropIdVcNn()).add(c);
		});*/

		weekNDailyQAReportList.forEach(c -> {
			String key = StringUtils.join(new Object[] { c.getSelrPropIdVcNn(), c.getCurrentListEndDate() }, RAClientConstants.CHAR_HYPHEN);
			if (hubzuDataSelrIdMap.containsKey(key) && hubzuDataSelrIdMap.get(key).size() >= 2) {
				if (hubzuDataSelrIdMap.get(key).get(1).getCurrentListStrtDate() != null) {
					/*c.setPreviousListStartDate(DateConversionUtil.US_DATE_TIME_FORMATTER_JAVA
							.parse(hubzuDataSelrIdMap.get(c.getSelrPropIdVcNn()).get(1).getCurrentListStrtDate()));*/
					c.setPreviousListStartDate(LocalDate.parse(hubzuDataSelrIdMap.get(key).get(1).getCurrentListStrtDate()));
				}
			}
			if (hubzuDataSelrIdMap.containsKey(key) && hubzuDataSelrIdMap.get(key).size() >= 2) {
				if (hubzuDataSelrIdMap.get(key).get(1).getCurrentListEndDate() != null) {
					/*c.setPreviousListEndDate(DateConversionUtil.US_DATE_TIME_FORMATTER
							.parseLocalDate(hubzuDataSelrIdMap.get(c.getSelrPropIdVcNn()).get(1).getCurrentListEndDate()));*/
					c.setPreviousListEndDate(LocalDate.parse(hubzuDataSelrIdMap.get(key).get(1).getCurrentListEndDate()));
				}
			}
			if (hubzuDataSelrIdMap.containsKey(key) && hubzuDataSelrIdMap.get(key).size() >= 2) {
				if (hubzuDataSelrIdMap.get(key).get(1).getListPrceNt() != null) {
					c.setPreviousListPrice(hubzuDataSelrIdMap.get(key).get(1).getListPrceNt());
				}
			}

			if (hubzuDataSelrIdMap.containsKey(key)) {
				c.setActualListCycle(String.valueOf(hubzuDataSelrIdMap.get(key).size()));
			} else {
				c.setActualListCycle("0");
			}
		});
		log.debug("populatePreviousListingData end.");
		return weekNDailyQAReportList;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void saveWeekNQaReport(List<WeekNDailyQAReportInfo> weeknQAReportList, WeekNDailyRunStatusInfo runStatusInfo) throws SystemException {
		WeekNDailyRunStatusInfo weeknRunStatusInfo = weekNDailyRunStatusBO.saveWeekNRunStatus(runStatusInfo);
		weekNDailyRunStatusBO.saveWeekNQaReport(weeknQAReportList, weeknRunStatusInfo);
	}

	@Override
	public List<HubzuInfo> getMigratedHubzuResponse(List<String> assetNumberList, Map<String, String> migrationNewPropToPropMap, Boolean sopStatus)
			throws SystemException {
		return weekNDailyRunStatusBO.getMigratedHubzuResponse(assetNumberList, migrationNewPropToPropMap, sopStatus);
	}

	@Override
	public void notifyDailyRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatus, String exceptionTrace, List<String> failedLoanNumbers)
			throws SystemException {
		weekNDailyRunStatusBO.notifyDailyRunStatus(weekNDailyRunStatus, exceptionTrace, failedLoanNumbers);
	}

	@Override
	public LocalDate getLatestWekNRunDate() {
		return dpFileProcessBO.getLatestWekNRunDate();
	}
}
