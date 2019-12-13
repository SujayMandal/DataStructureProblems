package com.fa.dp.business.task.sop.weekn.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.slf4j.MDC;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNAssignmentFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Slf4j
@Named
public abstract class AbstractSOPWeekNAssignmentFilter implements Command, SOPWeekNAssignmentFilter {

	@Inject
	private DPSopProcessBO dpSopProcessBO;

	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;

	@Override
	public void executeAssignmentFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPSopWeekNParamInfo> failEntries = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())) {
			for (DPSopWeekNParamInfo paramEntry : paramEntryInfo.getColumnEntries()) {
				MDC.put(RAClientConstants.LOAN_NUMBER, paramEntry.getAssetNumber());
				DPSopWeek0Param dpSopWeek0Param = dpSopProcessBO.findInSOPWeek0ForAssetNumber(paramEntry.getAssetNumber());
				DateTime assignmentDate;
				if (Objects.isNull(dpSopWeek0Param)) {
					log.info("There is no SOP Week0 entry for loan no: " + paramEntry.getAssetNumber());
				} else {
					log.info("SOP Week0 entry for loan: " + paramEntry.getAssetNumber() + " Assignment : '" + dpSopWeek0Param.getAssignment()
							+ "' Assignment Date : " + dpSopWeek0Param.getAssignmentDate());
				}

				if (dpSopWeek0Param != null && dpSopWeek0Param.getAssignmentDate() != null) {
					DateTimeParser[] dateParsers = { DateTimeFormat.forPattern("MM/dd/yyyy").getParser(),
							DateTimeFormat.forPattern("M/d/yy").getParser(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser() };
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, dateParsers).toFormatter();
					assignmentDate = DateConversionUtil.getEstDate(dpSopWeek0Param.getAssignmentDate());
					List<HubzuInfo> updatedHubzuInfoList = new ArrayList<>();
					for (HubzuInfo hubzuInfo : paramEntry.getHubzuDBResponse().getHubzuInfos()) {
						if (hubzuInfo.getCurrentListStrtDate() == null)
							continue;
						DateTime listStrtDt = DateConversionUtil.getEstDate(formatter.parseDateTime(hubzuInfo.getCurrentListStrtDate()).getMillis());
						if (listStrtDt.isBefore(assignmentDate))
							continue;
						else
							updatedHubzuInfoList.add(hubzuInfo);
					}
					paramEntry.getHubzuDBResponse().setHubzuInfos(updatedHubzuInfoList);
					//if all listings are before assignment date, put that columnEntry in assignment filter with a different exclusion reason
					if (updatedHubzuInfoList.isEmpty()) {
						paramEntry.setExclusionReason(DPProcessFilterParams.ASSIGNMENT_DATE_EXCLUSION.getValue());
						paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
						paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						failEntries.add(paramEntry);
						continue;
					}
				}

				if (dpSopWeek0Param == null || StringUtils
						.equalsIgnoreCase(dpSopWeek0Param.getAssignment(), DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue()) || !StringUtils
						.equalsIgnoreCase(dpSopWeek0Param.getEligible(), DPProcessParamAttributes.ELIGIBLE.getValue())) {
					paramEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
					paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					failEntries.add(paramEntry);
				} else if (StringUtils
						.equalsIgnoreCase(dpSopWeek0Param.getAssignment(), DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
					paramEntry.setExclusionReason(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
					paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					failEntries.add(paramEntry);
				} else if (StringUtils.equalsIgnoreCase(dpSopWeek0Param.getAssignment(), DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue())) {
					successEntries.add(paramEntry);
				} else {
					//For any other assignment statuses
					log.debug("No Success or Failure condition Matches for given LoanNumber=" + paramEntry.getAssetNumber() + " have Assignment="
							+ dpSopWeek0Param.getAssignment() + " and classification=" + dpSopWeek0Param.getClassification());
					paramEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
					paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					failEntries.add(paramEntry);
				}
			}
			if(!paramEntryInfo.isFetchProcess()) {
				paramEntryInfo.setColumnEntries(successEntries);
				sopWeekNParamDelegate.saveSopWeekNParamInfoList(failEntries);
			}
		}
	}
}

