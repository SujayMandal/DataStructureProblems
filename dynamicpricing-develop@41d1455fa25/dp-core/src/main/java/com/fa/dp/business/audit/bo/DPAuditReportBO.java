package com.fa.dp.business.audit.bo;

import java.time.LocalDate;
import java.util.List;

import com.fa.dp.business.audit.entity.DPWeekNAuditReports;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

/**
 * @author misprakh
 */
public interface DPAuditReportBO {
    /**
     * @param dpWeekNAuditReports
     * @return void
     * @throws SystemException
     */
    void saveAuditReport(List<DPWeekNAuditReports> dpWeekNAuditReports) throws SystemException;
    
    /**
     * @param priorRecommendation
     * @return boolean
     */
    boolean updatePriorRecommendationAuditReport(DPProcessWeekNParamInfo priorRecommendation,Long selectedDateMillis);
    
    /**
     * @param exclusionEntry
     * @return boolean
     */
    boolean updateExclusionEntriesAuditReport(DPWeekNAuditReports exclusionEntry);

    /**
     * Calculate exclusion reason from audit for given asset number and after given list end date
     * @param assetNumber
     * @param currentListEndDate
     * @return
     */
    DPWeekNAuditReports findExclusionReason(String assetNumber, LocalDate currentListEndDate);

    /**
     * Fetch audit report for given classifications
     * @param clasificationList
     * @return
     */
    List<DPWeekNAuditReports> findPermanentExclusionList(List<String> clasificationList);
}
