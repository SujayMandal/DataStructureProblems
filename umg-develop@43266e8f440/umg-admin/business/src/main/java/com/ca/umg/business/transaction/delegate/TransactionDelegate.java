package com.ca.umg.business.transaction.delegate;

import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.info.TransactionVersionInfo;
import com.ca.umg.business.transaction.info.TransactionWrapper;
import com.ca.umg.business.transaction.info.TransactionWrapperForApi;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.version.entity.Version;

public interface TransactionDelegate {

	public TransactionWrapper listAll(TransactionFilter transactionFilter) throws BusinessException, SystemException;

	public TransactionWrapper searchDefaultTransactions(Integer pageSize) throws BusinessException, SystemException;
	
	public TransactionWrapper searchTransactions(TransactionFilter transactionFilter, 
	        AdvanceTransactionFilter advanceTransactionFilter) throws BusinessException, SystemException;

	/**
	 * Searches the transactions for exposed RA Api
	 * @param transactionFilter
	 * @param advanceTransactionFilter
	 * @param includeTenantIO
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public TransactionWrapperForApi searchTransactionsForRaApi(TransactionFilter transactionFilter, 
            AdvanceTransactionFilter advanceTransactionFilter, TransactionFilterForApi transactionFilterForApi) throws BusinessException, SystemException;
	
	public SqlRowSet loadTransactionsRowSet(final TransactionFilter filter, final String tenantId) throws SystemException,
	BusinessException;

	public List<String> getOperatorList () throws BusinessException, SystemException;

	public TransactionDocument getTxnDocument(String txnId) throws SystemException;
	
	public TransactionDocument getTntIoDocuments(String txnId) throws SystemException;
	
	public TransactionDocument getModelIoDocuments(String txnId) throws SystemException;
	
	/**
	 * for io josn download from tran dashboard
	 * @param txnId
	 * @return
	 * @throws SystemException
	 */
	public TransactionDocument getTntModelIoDocuments(String txnId) throws SystemException;

	public TransactionVersionInfo getTransactionVersionInfo(final String versionName, final String fullVersion) throws BusinessException, SystemException;
	
	public Version getVersionInfo(final String versionName, final String fullVersion) throws BusinessException, SystemException;
	
	public byte[] getBulkModelOuput(final String txnId) throws SystemException;
	
	public byte[] getBulkModelErrorOuput(final String txnId) throws SystemException;
}
