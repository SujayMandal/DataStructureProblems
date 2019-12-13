package com.ca.umg.business.batching.dao;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.batching.report.usage.BatchUsageReportFilter;

public interface BatchUsageReportDAO {
	
	/**
	 * gets data for selected batch transactions
	 * @param filter
	 * @return
	 * @throws BusinessException
	 */
	public SqlRowSet loadBatchTransactionsRowSet(final BatchUsageReportFilter filter) throws BusinessException ;
	
	/**
	 * gets all data for filtered batch transactions
	 * @param filter
	 * @return
	 * @throws BusinessException
	 */
	public SqlRowSet loadAllBatchTransactionsRowSet(final BatchUsageReportFilter filter) throws BusinessException ;

}
