package com.ca.umg.business.batching.delegate;

import static org.slf4j.LoggerFactory.getLogger;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.batching.bo.BatchUsageReportBO;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.batching.report.usage.BatchUsageReportFilter;
import com.ca.umg.business.util.AdminUtil;
@Named
public class BatchUsageReportDelegateImpl implements BatchUsageReportDelegate {
	
	@Inject
	private BatchUsageReportBO batchUsageReportBO;
	
	private static final Logger LOGGER = getLogger(BatchUsageReportDelegateImpl.class.getName());

	// returns selected transaction
	@Override
	public SqlRowSet loadBatchTransactionsRowSetByTransactionList(BatchUsageReportFilter filter)
			throws SystemException, BusinessException {
		return batchUsageReportBO.loadBatchTransactionsRowSet(filter);
	}

	// returns all transactions based on filters
	@Override
	public SqlRowSet loadAllBatchTransactionsRowSet(BatchUsageReportFilter filter)
			throws SystemException, BusinessException {
		// TODO Auto-generated method stub
		checkValidFromToDates(filter);
		return batchUsageReportBO.loadAllBatchTransactionsRowSet(filter);
	}
	
	private void checkValidFromToDates(final BatchUsageReportFilter filter) throws BusinessException {
		filter.setFromDate(AdminUtil.getMillisFromEstToUtc(filter.getFromDateToString(), null));
		filter.setToDate(AdminUtil.getMillisFromEstToUtc(filter.getToDateToString(), null));

		if (filter.getFromDate() != null && filter.getToDate() != null && filter.getFromDate() > filter.getToDate()) {
			LOGGER.error("BSE000076 : The Batch Transaction Run Dates Range is invalid");
			BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000076, new Object[] {});
		} else if (filter.getToDate() != null && filter.getFromDate() == null) {
			LOGGER.error("BSE000078 : Batch RunDateFrom missing. Please specify the RunDateFrom");
			BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000078, new Object[] {});
		}
	}

}
