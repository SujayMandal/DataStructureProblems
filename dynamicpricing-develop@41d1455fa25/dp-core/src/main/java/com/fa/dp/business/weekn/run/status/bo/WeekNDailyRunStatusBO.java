package com.fa.dp.business.weekn.run.status.bo;

import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.exception.SystemException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WeekNDailyRunStatusBO {
	/**
	 * get last run date from qa run status
	 * @return
	 * @throws SystemException
	 */
	LocalDate getLastRunDate() throws SystemException;

	/**
	 * fetch hubzu response between start date and current date
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws SystemException
	 */
	HubzuDBResponse fetchQaReportHubzuResponse(LocalDate startDate, LocalDate endDate, Boolean sopStatus) throws SystemException;

	/**
	 * fetch previous listing data
	 * @param sellerPropertyIds
	 * @return
	 * @throws SystemException
	 */
	List<WeekNDailyQAReportInfo> fetchPreviousListingDataBySellerrPropertyId(List<String> sellerPropertyIds) throws SystemException;

	/**
	 * save weekn qa report into database
	 * @param weeknQAReportList
	 * @param weeknRunStatusInfo
	 * @throws SystemException
	 */
	void saveWeekNQaReport(List<WeekNDailyQAReportInfo> weeknQAReportList, WeekNDailyRunStatusInfo weeknRunStatusInfo) throws SystemException;
	/**
	 * save weekn run status
	 * @param weekNDailyRunStatusInfo
	 * @return
	 * @throws SystemException
	 */
	WeekNDailyRunStatusInfo saveWeekNRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatusInfo) throws SystemException;
	/**
	 * Fetch migrated hubzu response for given asset numbers
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
}
