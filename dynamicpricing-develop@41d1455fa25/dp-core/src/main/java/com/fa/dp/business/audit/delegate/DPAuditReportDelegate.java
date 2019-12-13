package com.fa.dp.business.audit.delegate;

import java.util.List;
import java.util.Map;

import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.permanent.exclusion.report.info.DPPermanentExclusionReportInfo;
import com.fa.dp.core.exception.SystemException;

/**
 * @author misprakh
 */
public interface DPAuditReportDelegate {

    /**
     * @param   setOfAuditOddEntries
     * @param   selectedDateMillis
     * @return void
     * @throws SystemException
     */
    void createWeekNAuditEntries(Map<String, String> setOfAuditOddEntries, Long selectedDateMillis,
                                 Map<String, String> migrationNewPropToPropMap, Map<String, String> migrationPropToLoanMap) throws SystemException;

	/**
	 * @param   failedEntries
	 * @return void
	 * @throws SystemException
	 */
	void createFailedEntriesInAudit(List<DPProcessWeekNParamInfo> failedEntries, Long selectedDateMillis) throws SystemException;
	
	/**
	 * @param   recommendedEntries
	 * @return void
	 * @throws SystemException
	 */
	void createRecommendedEntriesInAudit(List<DPProcessWeekNParamInfo> recommendedEntries, Long selectedDateMillis) throws SystemException;
	
	/**
	 * @param   successUnderReviewEntries
	 * @return void
	 * @throws SystemException
	 */
	void createSucessfulUnderreviewEntriesInAudit(List<DPProcessWeekNParamInfo> successUnderReviewEntries) throws SystemException;

	/**
	 * @param priorRecommendedEntries
	 * @param selectedDateMillis
	 * @throws SystemException
	 */
	void createPriorRecommendedEntriesInAudit(List<DPProcessWeekNParamInfo> priorRecommendedEntries, Long selectedDateMillis) throws SystemException;

	/**
	 * Retieve permanent exclusion reports
	 * @param clasifications
	 * @return
	 * @throws SystemException
	 */
	List<DPPermanentExclusionReportInfo> findPermanentExclusionList(List<String> clasifications) throws SystemException;
}
