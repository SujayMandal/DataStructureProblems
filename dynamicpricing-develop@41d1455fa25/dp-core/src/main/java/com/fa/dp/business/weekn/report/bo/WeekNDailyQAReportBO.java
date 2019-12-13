package com.fa.dp.business.weekn.report.bo;

import com.fa.dp.business.weekn.report.entity.SOPWeekNDailyQAReport;
import com.fa.dp.business.weekn.report.entity.WeekNDailyQAReport;
import com.fa.dp.core.exception.SystemException;

import java.time.LocalDate;
import java.util.List;

public interface WeekNDailyQAReportBO {

	/**
	 * Fetch listings from QA table for given date range
	 * @param startDateMillis
	 * @param endDateMillis
	 * @param client
	 * @return
	 */
	List<WeekNDailyQAReport> fetchListingsForGivenDateRange(LocalDate startDateMillis, LocalDate endDateMillis, List<String> client) throws SystemException;

	List<SOPWeekNDailyQAReport> fetchListingsForGivenDateRangeSOP(LocalDate startDateMillis, LocalDate endDateMillis, List<String> client) throws SystemException;
}
