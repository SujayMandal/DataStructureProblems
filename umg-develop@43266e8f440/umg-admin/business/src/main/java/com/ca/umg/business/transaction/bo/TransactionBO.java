package com.ca.umg.business.transaction.bo;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;

public interface TransactionBO {

    public Page<Transaction> listAll(TransactionFilter transactionFilter) throws BusinessException, SystemException;


    List<String> findAllLibraries() throws BusinessException, SystemException;

    List<String> findAllTenantModelNames() throws BusinessException, SystemException;

    Transaction getTransactionByTxnId(String txnId) throws BusinessException, SystemException;

    Long getMaxRunAsOfDate() throws BusinessException, SystemException;
    
    /**
     * @param txnId
     * @return
     */
    public TransactionDocument getTxnDocumentByTxnId(String txnId) throws SystemException;
}
