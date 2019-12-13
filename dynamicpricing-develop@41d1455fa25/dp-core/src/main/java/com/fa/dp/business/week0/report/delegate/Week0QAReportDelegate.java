package com.fa.dp.business.week0.report.delegate;

import com.fa.dp.business.week0.report.info.DPWeek0QAReportRespInfo;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.core.exception.SystemException;

import java.util.List;

public interface Week0QAReportDelegate {
	/**
	 * Fetch and return week0 report by assignment date between start date and end date, client code.
	 * It fetches data from week0 params or sop week0 params based on occupancy.
	 * @param startDate
	 * @param endDate
	 * @param occupancy
	 * @param clientCode
	 * @return
	 * @throws SystemException
	 */
	DPWeek0QAReportRespInfo fetchWeek0Repots(String startDate, String endDate, String occupancy, List<String> clientCode) throws SystemException;
}
