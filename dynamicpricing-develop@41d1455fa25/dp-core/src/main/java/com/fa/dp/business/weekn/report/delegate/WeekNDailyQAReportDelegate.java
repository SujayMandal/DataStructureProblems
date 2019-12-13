package com.fa.dp.business.weekn.report.delegate;

import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.core.exception.SystemException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WeekNDailyQAReportDelegate {
	void prepareAssignmentDate(HubzuDBResponse hubzuinfo, Map<String, String> migrationPropToLoanMap);


	/**
	 * get report data from QA table
	 *
	 * @param startDateMillis
	 * @param endDateMillis
	 * @param occupancy
	 * @param client
	 * @return
	 * @throws SystemException
	 */
	List<WeekNDailyQAReportInfo> getConsolidatedQAReports(LocalDate startDateMillis, LocalDate endDateMillis, String occupancy, List<String> client) throws SystemException;

}
