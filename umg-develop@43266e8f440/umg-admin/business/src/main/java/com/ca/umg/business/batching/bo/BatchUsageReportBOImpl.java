package com.ca.umg.business.batching.bo;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.batching.dao.BatchUsageReportDAO;
import com.ca.umg.business.batching.report.usage.BatchUsageReportFilter;

@Named
public class BatchUsageReportBOImpl implements BatchUsageReportBO {
	
	@Inject
	private BatchUsageReportDAO batchUsageReportDAO;
	
	@Override
	public SqlRowSet loadBatchTransactionsRowSet(BatchUsageReportFilter filter) throws SystemException, BusinessException {
		return batchUsageReportDAO.loadBatchTransactionsRowSet(filter);
	}
	
	@Override
	public SqlRowSet loadAllBatchTransactionsRowSet(BatchUsageReportFilter filter) throws SystemException, BusinessException {
		return batchUsageReportDAO.loadAllBatchTransactionsRowSet(filter);
	}

}
