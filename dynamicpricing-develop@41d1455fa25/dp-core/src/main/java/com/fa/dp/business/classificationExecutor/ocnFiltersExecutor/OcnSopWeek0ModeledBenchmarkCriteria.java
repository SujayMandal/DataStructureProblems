package com.fa.dp.business.classificationExecutor.ocnFiltersExecutor;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.task.sop.week0.AbstractSopWeek0DuplicateFilter;
import com.fa.dp.business.task.sop.week0.AbstractSopWeek0ModeledBenchmarkCriteria;
import com.fa.dp.core.exception.SystemException;

import javax.inject.Named;

/**
 * @author misprakh
 */

@Named
@CommandDescription(name = "ocnSopWeek0ModeledBenchmarkCriteria")
public class OcnSopWeek0ModeledBenchmarkCriteria extends AbstractSopWeek0ModeledBenchmarkCriteria {
	/**
	 * This method executes the process for filtering the loans.
	 *
	 * @param data has all information embedded that the excel file has provided.
	 * @throws SystemException when system cannot complete the requested step.
	 */
	@Override
	public void execute(Object data) throws SystemException {
		executeSopWeek0ModeledBenchmarkCriteria(data, DPAConstants.OCN_SOP_WEEK0_MODELED_BENCHMARK_CRITERIA);
	}
}
