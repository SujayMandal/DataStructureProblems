package com.fa.dp.business.task.sop.week0.filters;

import com.fa.dp.core.exception.SystemException;

/**
 * @author misprakh
 */
public interface SopWeek0ModeledBenchmarkCriteria {
	/**
	 * This filter provide 80 and 20 criteria to process loans
	 *
	 * @param data
	 * @throws SystemException
	 */
	public void executeSopWeek0ModeledBenchmarkCriteria(Object data, String filterName) throws SystemException;
}
