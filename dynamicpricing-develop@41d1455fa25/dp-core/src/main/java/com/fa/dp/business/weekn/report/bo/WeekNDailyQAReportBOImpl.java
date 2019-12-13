package com.fa.dp.business.weekn.report.bo;

import com.fa.dp.business.weekn.report.dao.SOPWeekNDailyQAReportDao;
import com.fa.dp.business.weekn.report.dao.WeekNDailyQAReportDao;
import com.fa.dp.business.weekn.report.entity.SOPWeekNDailyQAReport;
import com.fa.dp.business.weekn.report.entity.WeekNDailyQAReport;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.List;

@Named
@Slf4j
public class WeekNDailyQAReportBOImpl implements WeekNDailyQAReportBO {

	@Inject
	private WeekNDailyQAReportDao weekNDailyQAReportDao;

	@Inject
	private SOPWeekNDailyQAReportDao sopWeekNDailyQAReportDao;


	/**
	 * Fetch listings from QA table for given date range
	 *
	 * @param startDateMillis
	 * @param endDateMillis
	 * @param client
	 * @return
	 */
	@Override
	public List<WeekNDailyQAReport> fetchListingsForGivenDateRange(LocalDate startDateMillis, LocalDate endDateMillis, List<String> client) {
		return weekNDailyQAReportDao.findAllByStartTimeAndEndTime(startDateMillis, endDateMillis, client);
	}

	/**
	 * Fetch listings from QA table for given date range
	 *
	 * @param startDateMillis
	 * @param endDateMillis
	 * @param client
	 * @return
	 */
	@Override
	public List<SOPWeekNDailyQAReport> fetchListingsForGivenDateRangeSOP(LocalDate startDateMillis, LocalDate endDateMillis, List<String> client) {
		return sopWeekNDailyQAReportDao.findAllByStartTimeAndEndTimeForSOP(startDateMillis, endDateMillis, client);
	}


}
