package com.ca.umg.business.batching.bo;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.batching.report.usage.BatchUsageReportFilter;

public interface BatchUsageReportBO {
	
	/**
	 * gets data for selected batch transactions
	 * @param filter
	 * @return
	 * @throws SystemException
	 * @throws BusinessException
	 */
	public SqlRowSet loadBatchTransactionsRowSet(final BatchUsageReportFilter filter) throws SystemException, BusinessException;
	
	/**
	 * gets all data for filtered batch transactions
	 * @param filter
	 * @return
	 * @throws SystemException
	 * @throws BusinessException
	 */
	public SqlRowSet loadAllBatchTransactionsRowSet(final BatchUsageReportFilter filter) throws SystemException, BusinessException;

}
