package com.fa.dp.business.weekn.run.status.bo;

import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.exception.SystemException;

import java.time.LocalDate;
import java.util.List;

public interface SOPWeekNDailyRunStatusBO {
    /**
     * get last run date from qa run status for SOP WeekN
     * @return
     * @throws SystemException
     */
    LocalDate getLastRunDateForSOP() throws SystemException;


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
     * save weekn SOP run status
     * @param weekNDailyRunStatusInfo
     * @return
     * @throws SystemException
     */
    WeekNDailyRunStatusInfo saveWeekNSOPRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatusInfo) throws SystemException;

    /**
     * save weekn SOP qa report into database
     * @param weeknQAReportList
     * @param weeknRunStatusInfo
     * @throws SystemException
     */
    void saveWeekNSOPQaReport(List<WeekNDailyQAReportInfo> weeknQAReportList, WeekNDailyRunStatusInfo weeknRunStatusInfo) throws SystemException;


}
