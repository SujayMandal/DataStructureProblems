package com.fa.dp.business.classificationExecutor.nrzFiltersExecutor;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.task.sop.week0.AbstractSopWeek0DuplicateFilter;
import com.fa.dp.core.exception.SystemException;

import javax.inject.Named;

/**
 * @author misprakh
 */

@Named
@CommandDescription(name = "nrzSopWeek0DuplicateFilter")
public class NrzSopWeek0DuplicateFilter extends AbstractSopWeek0DuplicateFilter {

	/**
	 * This method executes the process for filtering the loans.
	 *
	 * @param data has all information embedded that the excel file has provided.
	 * @throws SystemException when system cannot complete the requested step.
	 */
	@Override
	public void execute(Object data) throws SystemException {
		executeSopWeek0DuplicateFilter(data, DPAConstants.NRZ_SOP_DUPLICATE_FILTER);
	}
}
