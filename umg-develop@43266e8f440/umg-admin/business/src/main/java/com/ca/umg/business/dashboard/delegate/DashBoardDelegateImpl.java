package com.ca.umg.business.dashboard.delegate;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.dashboard.bo.DashBoardBO;
import com.ca.umg.business.dashboard.info.ModelUsagePattern;
import com.ca.umg.business.dashboard.info.ModelVersionStatus;
import com.ca.umg.business.transaction.info.TransactionFilter;

@Named
public class DashBoardDelegateImpl implements DashBoardDelegate {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DashBoardDelegateImpl.class);
	
	@Inject
	private DashBoardBO dashBoardBO;

	@Override
	public List<ModelVersionStatus> getVersionStats() throws BusinessException,
			SystemException {
		LOGGER.info("Getting the version statistics DashBoardDelegateImpl::getVersionStats");
		return dashBoardBO.getVersionStats();
	}

	@Override
	public Long getTransactionsCountForDay(Integer day) throws BusinessException,
			SystemException {
		return dashBoardBO.getTransactionsCountForDay(day);
	}
	
	@Override
	public Long getActiveLookupData() throws BusinessException,
			SystemException {
		return dashBoardBO.getActiveLookupData();
	}
	
	@Override
	public Long getExpiringLookupData() throws BusinessException,
			SystemException {
		return dashBoardBO.getExpiringLookupData();
	}
	
	@Override
	public Map<String,Long> getScsFailCntForTransactions (TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the version statistics DashBoardDelegateImpl::getScsFailCntForTransactions : "+transactionFilter);
		return dashBoardBO.getScsFailCntForTransactions(transactionFilter);
	}
	
	@Override
	public Map<String,Object> getStatusMetrics(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the status Metrics DashBoardDelegateImpl::getStatusMetrics : "+transactionFilter);
		return dashBoardBO.getstatusMetricsForTransactions(transactionFilter);
	}
	
	@Override
	public Map<String,Object> getUsageDynamics(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the usage Dynamics DashBoardDelegateImpl::getUsageDynamics : "+transactionFilter);
		Map<String, Object> result = dashBoardBO.getUsageDynamicsForTransactions(transactionFilter);
		result.put(BusinessConstants.DATE, transactionFilter.getExecutionGroup());
		result.put(BusinessConstants.GRID_COUNT, transactionFilter.getTotalElements());
		return result;
	}
	
	@Override
	public List<ModelVersionStatus> getErrorTxnList(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the Error Txn List DashBoardDelegateImpl::getErrorTxnList : "+transactionFilter);
		return dashBoardBO.getFailTxn(transactionFilter);
	}
	@Override
	public List<Object> getUsageDynamicsGrid(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the Error Txn List DashBoardDelegateImpl::getErrorTxnList : "+transactionFilter);
		return dashBoardBO.getUsageDynamicsGrid(transactionFilter);
	}
	
	@Override
	public Map<String, Object> getUsageTrendLineData(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException {
		LOGGER.info("Getting the Error Txn List DashBoardDelegateImpl::getUsageTrendLineData : "+transactionFilter);
		Map<String, Object> result = dashBoardBO.getRaUsageTrendData(transactionFilter);
		result.put(BusinessConstants.DATE, transactionFilter.getExecutionGroup());
		result.put(BusinessConstants.GRID_COUNT, transactionFilter.getTotalElements());
		return result;
	}
	
	@Override
	public List<ModelUsagePattern> getTransactionsCnt() throws BusinessException, SystemException {
		LOGGER.info("Getting the version statistics DashBoardDelegateImpl::getTransactionsCnt : ");
		return dashBoardBO.getTransactionsCnt();
	}

	@Override
	public List<String> getUniqueModelNames() throws BusinessException,
			SystemException {
		return dashBoardBO.getUniqueModelNames();
	}
}
