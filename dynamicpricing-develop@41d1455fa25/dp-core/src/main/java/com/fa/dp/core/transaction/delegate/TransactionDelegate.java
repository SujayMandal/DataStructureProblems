package com.fa.dp.core.transaction.delegate;

import java.util.List;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.transaction.info.TransactionInfo;

public interface TransactionDelegate {

    /**
     * 
     * @param modelName
     * @param majorVersion
     * @param minorVersion
     * @param status
     * @param transactionId
     * @param fromDate
     * @param toDate
     * @return
     * @throws SystemException
     */
    public List<TransactionInfo> getAllTransactions(
            String modelName, String modelVersion, String status, String transactionId,
            String fromDate, String toDate) throws SystemException;

    public void saveTransaction(TransactionInfo transactionInfo) throws SystemException;

    public List<TransactionInfo> getTransactionDetails(String modelName);

    public List<TransactionInfo>getTransactionDetails(String modelName, String fromDate, String toDate)throws
            SystemException;

}
