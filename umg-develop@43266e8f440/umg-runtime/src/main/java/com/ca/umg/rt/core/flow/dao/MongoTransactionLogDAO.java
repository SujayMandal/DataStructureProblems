package com.ca.umg.rt.core.flow.dao;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.TransactionDocumentPayload;
import com.ca.umg.rt.util.TransactionPayload;

/**
 * interface to insert the transaction log to mongodb
 * @author raddibas
 *
 */
public interface MongoTransactionLogDAO {
	/**
	 * inserts the transaction into mongo db
	 * @param transactionDocumentPayload
	 */
	public void insertTransactionLogToMongo(final TransactionPayload transactionPayload,final TransactionDocumentPayload transactionDocumentPayload);

    public void upsertRequestTransactionLogToMongo(final TransactionPayload transactionPayload,final TransactionDocumentPayload transactionDocumentPayload) throws SystemException;

    public void upsertResponseTransactionLogToMongo(final TransactionPayload transactionPayload, final TransactionDocumentPayload transactionDocumentPayload) throws SystemException;
}
