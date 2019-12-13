/**
 *
 */
package com.fa.dp.business.command.master;

import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.business.BusinessException;

/**
 * The master flow controller. Creates appropriate execution flows collating commands and hands over the responsibility of
 * execution to the command executor.
 *
 * @author mandasuj
 */
public interface CommandMaster {

	/**
	 * This method would take the responsibility of preparing Week0 input for filtering.
	 *
	 * @param dpProcessParamEntryInfo
	 * @throws SystemException
	 */
	void prepareWeek0(DPProcessParamEntryInfo dpProcessParamEntryInfo) throws SystemException;

	/**
	 * This method would take the responsibility of preparing SOP Week0 input for filtering.
	 *
	 * @param dpSopParamEntryInfo
	 * @return
	 * @throws SystemException
	 */
	void prepareSopWeek0(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException;

	/**
	 * This method would take the responsibility of preparing WeekN input for filtering.
	 *
	 * @param dpProcessWeekNParamEntryInfo
	 * @return
	 * @throws SystemException
	 */
	DPProcessWeekNParamEntryInfo prepareWeekN(DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException;

	/**
	 * Filter qa report task
	 *
	 * @param weeknParamEntryInfo
	 */
	void filterQaReport(DPProcessWeekNParamEntryInfo weeknParamEntryInfo, Boolean sopStatus);


	/**
	 * Filter qa report task
	 *
	 * @param sopWeeknParamEntryInfo
	 */
	void filterQaReportForSOP(DPSopWeekNParamEntryInfo sopWeeknParamEntryInfo, Boolean sopStatus);

	/**
	 * SOP WeekN filter preparation
	 * @param sopWeekNParamEntryInfo
	 *
	 * @throws SystemException
	 * @throws BusinessException
	 */
	void prepareSopWeekN(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo) throws SystemException, BusinessException;
}
