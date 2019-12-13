package com.fa.dp.business.weekn.run.status.delegate;

import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.exception.SystemException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SOPWeekNDailyRunStatusDelegate {

    /**
     * Calculate last run date from daily run status for SOP WeekN
     * @return
     * @throws SystemException
     */
    LocalDate getLastRunDateForSOP() throws SystemException;

    /**
     * Fetch latest weekn run date
     * @return
     */
    LocalDate getLatestWekNRunDate();

    /**
     * check in weekn db if reduction was given for qa report
     * @param weekNDailyQAReportInfoList
     * @return
     */
    List<WeekNDailyQAReportInfo> checkReduction(List<WeekNDailyQAReportInfo> weekNDailyQAReportInfoList, Boolean sopStatus);

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
     * save weekn qa report into database
     * @param sopWeeknQAReportList
     * @param runStatusInfo
     * @throws SystemException
     */
    void saveWeekNSOPQaReport(List<WeekNDailyQAReportInfo> sopWeeknQAReportList, WeekNDailyRunStatusInfo runStatusInfo) throws SystemException;

}
