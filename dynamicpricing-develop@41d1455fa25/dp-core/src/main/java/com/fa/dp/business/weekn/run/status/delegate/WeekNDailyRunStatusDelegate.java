package com.fa.dp.business.weekn.run.status.delegate;

import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.exception.SystemException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WeekNDailyRunStatusDelegate {

	/**
	 * Calculate last run date from daily run status
	 * @return
	 * @throws SystemException
	 */
	LocalDate getLastRunDate() throws SystemException;

	/**
	 * Fetch hubzu response between start date and current date.
	 * Fetch all properties from Hubzu WHERE List Start Date between last run date and weekn last run date
	 * list status != ( Cancelled / Disapproved)
	 * SOP status != N
	 *
	 *
	 * @param lastRunDate
	 * @param weeknLastRunDate
	 * @return
	 * @throws SystemException
	 */
	HubzuDBResponse fetchQaReportHubzuResponse(LocalDate lastRunDate, LocalDate weeknLastRunDate, Boolean sopStatus) throws SystemException;

	/**
	 * check in weekn db if reduction was given for qa report
	 * @param weekNDailyQAReportInfoList
	 * @return
	 */
	List<WeekNDailyQAReportInfo> checkReduction(List<WeekNDailyQAReportInfo> weekNDailyQAReportInfoList, Boolean sopStatus);

	/**
	 * populates previous listing data
	 * @param WeekNDailyQAReportList
	 * @param hubzuDataSelrIdMap
	 * @return
	 */
	List<WeekNDailyQAReportInfo> populatePreviousListingData(List<WeekNDailyQAReportInfo> WeekNDailyQAReportList,
			Map<String, List<HubzuInfo>> hubzuDataSelrIdMap);

	/**
	 * save weekn qa report into database
	 * @param weeknQAReportList
	 * @param runStatusInfo
	 * @throws SystemException
	 */
	void saveWeekNQaReport(List<WeekNDailyQAReportInfo> weeknQAReportList, WeekNDailyRunStatusInfo runStatusInfo) throws SystemException;
	/**
	 * get migrated hubzu response for asset numbers
	 * @param assetNumberList
	 * @param migrationNewPropToPropMap
	 * @return
	 * @throws SystemException
	 */
	List<HubzuInfo> getMigratedHubzuResponse(List<String> assetNumberList, Map<String, String> migrationNewPropToPropMap, Boolean sopStatus) throws SystemException;

	/**
	 * Send mail for success or failure of daily qa report
	 * @param weekNDailyRunStatus
	 * @param exceptionTrace
	 * @param failedLoanNumbers
	 * @throws SystemException
	 */
	void notifyDailyRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatus, String exceptionTrace, List<String> failedLoanNumbers)
			throws SystemException;

	/**
	 * Fetch latest weekn run date
	 * @return
	 */
	LocalDate getLatestWekNRunDate();

}
