package com.ca.umg.rt.core.flow.dao;

import com.ca.umg.rt.core.flow.entity.TransactionLog;

public interface TransactionLogDAO {
    /**
     * inserts a transaction
     * @param transactionLog
     * @return
     */
    public int insertTransactionLog(TransactionLog transactionLog);
    /**
     * inserts the transaction to mysql used in flow-template.xml
     * @param transactionLog
     * @return
     */
    public int insertTxnFlowLog(TransactionLog transactionLog);
    
    public int remove(String transactionId);

    public int checkTransactionExists(String umgTransactionId);

    public int insertTransactionRequest(TransactionLog transactionLog);

    public int insertTransactionResponse(TransactionLog transactionLog);

    public int updateTransactionResponse(TransactionLog transactionLog);
    
    public int updateModelExecEnvName(TransactionLog transactionLog);

}
