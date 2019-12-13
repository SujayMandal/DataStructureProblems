package com.ca.umg.business.dashboard.delegate;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.dashboard.info.ModelUsagePattern;
import com.ca.umg.business.dashboard.info.ModelVersionStatus;
import com.ca.umg.business.transaction.info.TransactionFilter;

public interface DashBoardDelegate {

	List<ModelVersionStatus> getVersionStats() throws BusinessException, SystemException;
	
	Long getTransactionsCountForDay (Integer day) throws BusinessException, SystemException;
	
	Map<String,Long> getScsFailCntForTransactions (TransactionFilter transactionFilter) 
			throws BusinessException, SystemException;
	
	List<ModelUsagePattern> getTransactionsCnt() throws BusinessException, SystemException ;
	
	List<String> getUniqueModelNames() throws BusinessException, SystemException;

	Long getActiveLookupData()throws BusinessException, SystemException;

	Long getExpiringLookupData()throws BusinessException, SystemException;
	
	Map<String, Object> getStatusMetrics (TransactionFilter transactionFilter) 
			throws BusinessException, SystemException;
	
	Map<String, Object> getUsageDynamics (TransactionFilter transactionFilter) 
			throws BusinessException, SystemException;
	
	List<ModelVersionStatus> getErrorTxnList(TransactionFilter transactionFilter) 
			throws BusinessException, SystemException;

	Map<String, Object> getUsageTrendLineData(TransactionFilter transactionFilter)
			throws BusinessException, SystemException;

	List<Object> getUsageDynamicsGrid(TransactionFilter transactionFilter) throws BusinessException, SystemException;
	
	
}
