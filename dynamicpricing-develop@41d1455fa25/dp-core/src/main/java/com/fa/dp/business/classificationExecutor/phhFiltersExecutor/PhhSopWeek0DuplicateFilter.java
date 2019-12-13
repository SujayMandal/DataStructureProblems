package com.fa.dp.business.classificationExecutor.phhFiltersExecutor;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.task.sop.week0.AbstractSopWeek0AssetValueFilter;
import com.fa.dp.business.task.sop.week0.AbstractSopWeek0DuplicateFilter;
import com.fa.dp.core.exception.SystemException;

import javax.inject.Named;

@Named
@CommandDescription(name = "phhSopWeek0DuplicateFilter")
public class PhhSopWeek0DuplicateFilter extends AbstractSopWeek0DuplicateFilter {

	/**
	 * This method executes the process for filtering the loans.
	 *
	 * @param data has all information embedded that the excel file has provided.
	 * @throws SystemException when system cannot complete the requested step.
	 */
	@Override
	public void execute(Object data) throws SystemException {
		executeSopWeek0DuplicateFilter(data, DPAConstants.PHH_SOP_DUPLICATE_FILTER);
	}
}
