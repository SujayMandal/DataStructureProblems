package com.fa.dp.business.week0.report.delegate;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.filter.bo.DPProcessParamsBO;
import com.fa.dp.business.sop.week0.bo.DPSopWeek0ParamBO;
import com.fa.dp.business.week0.report.info.DPWeek0QAReportRespInfo;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.business.week0.report.operation.FetchWeek0ReportOperation;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.util.DateConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Named
public class Week0QAReportDelegateImpl implements Week0QAReportDelegate {

	private Map<String, FetchWeek0ReportOperation> week0ReportOperationMap;

	@Inject
	private DPProcessParamsBO dpProcessParamsBO;

	@Inject
	private DPSopWeek0ParamBO dpSopWeek0ParamBO;

	@PostConstruct
	private void init() {
		week0ReportOperationMap = new HashMap<>();

		week0ReportOperationMap
				.put(DPAConstants.OCCUPIED, (startDate, endDate, clientCode) -> dpSopWeek0ParamBO.fetchWeek0Report(startDate, endDate, clientCode));
		week0ReportOperationMap
				.put(DPAConstants.VACANT, (startDate, endDate, clientCode) -> dpProcessParamsBO.fetchWeek0Report(startDate, endDate, clientCode));

		week0ReportOperationMap = Collections.unmodifiableMap(week0ReportOperationMap);
	}

	@Override
	public DPWeek0QAReportRespInfo fetchWeek0Repots(String startDate, String endDate, String occupancy, List<String> clientCode)
			throws SystemException {

		DPWeek0QAReportRespInfo qaReportInfo = new DPWeek0QAReportRespInfo();
		Long parsedStartDate = null;
		Long parsedEndDate = null;
		try {
			parsedStartDate = DateConversionUtil.EST_DATE_TIME_FORMATTER_QA_REPORT.parseDateTime(startDate).getMillis();
		} catch (DateTimeParseException e) {
			log.error("startDate Date parsing exception. {}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPSOPWK0008, e.getMessage());
		}
		try {
			parsedEndDate = DateConversionUtil.EST_DATE_TIME_FORMATTER_QA_REPORT.parseDateTime(endDate).getMillis() + (86400 * 1000 - 1);
		} catch (DateTimeParseException e) {
			log.error("endDate Date parsing exception. {}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPSOPWK0009, e.getMessage());
		}

		FetchWeek0ReportOperation reportOperation = week0ReportOperationMap.get(occupancy);

		if (ObjectUtils.isEmpty(reportOperation)) {
			log.error("occupancy is not correct. occupancy : {}", occupancy);
			SystemException.newSystemException(CoreExceptionCodes.DPSOPWK0010);
		}

		List<DPWeek0ReportInfo> reports = reportOperation.apply(parsedStartDate, parsedEndDate, clientCode);
        log.debug("Parameters of week 0 startDate : {} , endDate : {} ,clientCode : {}",parsedStartDate,parsedEndDate,clientCode.toString());

		long size = reports.stream().filter(a -> NumberUtils.isParsable(a.getPctAV())).count();

		DPWeek0ReportInfo minimumReportInfoData = reports.stream().filter(a -> NumberUtils.isParsable(a.getPctAV()))
				.min(Comparator.comparing(a -> Double.parseDouble(a.getPctAV()))).orElse(null);

		DPWeek0ReportInfo maximumReportInfoData = reports.stream().filter(a -> NumberUtils.isParsable(a.getPctAV()))
				.max(Comparator.comparing(a -> Double.parseDouble(a.getPctAV()))).orElseGet(() -> null);

		if(minimumReportInfoData != null) {
			qaReportInfo.setMinimumPctAv(Double.parseDouble(minimumReportInfoData.getPctAV()));
		}
		if(maximumReportInfoData != null) {
			qaReportInfo.setMaximumPctAv(Double.parseDouble(maximumReportInfoData.getPctAV()));
		}

		double median = reports.stream().filter(a -> NumberUtils.isParsable(a.getPctAV())).mapToDouble(a -> Double.parseDouble(a.getPctAV())).sorted()
				.skip((size - 1) / 2).limit(2 - size % 2).average().orElse(Double.NaN);

		qaReportInfo.setMedianPctAv(median);
		qaReportInfo.setPropertyCount(reports.size());
		qaReportInfo.setVoilationCount(Math.toIntExact(
				reports.stream().filter(a -> StringUtils.equalsIgnoreCase(Boolean.FALSE.toString(), a.getWithinBusinessRules())).count()));
		qaReportInfo.setMissingReportCount(0);

		qaReportInfo.setWeek0Reports(reports);

		return qaReportInfo;
	}

}
