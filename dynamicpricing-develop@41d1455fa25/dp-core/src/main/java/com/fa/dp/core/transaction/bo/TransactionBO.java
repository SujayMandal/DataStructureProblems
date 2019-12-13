package com.fa.dp.core.transaction.bo;

import java.util.List;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.transaction.domain.Transaction;

public interface TransactionBO {

	public List<Transaction> findAllTransactions(
            String modelName, String modelVersion, String status,
            String transactionId, String fromDate, String toDate) throws SystemException;

	void save(Transaction transaction, byte[] request, byte[] response) throws SystemException;

	public List<Transaction> getTransactionDetails(String modelName);

	public List<Transaction> findTransactions(String modelName, String fromDate, String toDate) throws SystemException;

}
