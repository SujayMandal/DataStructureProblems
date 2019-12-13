package com.ca.umg.business.transaction.migrate.dao;

import java.util.List;

import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;

public interface MigrateTransactionDAO {
	
	/**
	 * gets the data for passed transaction id 
	 * @param tranId
	 * @return {@link TransactionDocument} - object for fetched data
	 */
	public TransactionDocument getBlobsOfRuntmTransaction(String tranId);
	
	/**
	 * gets all the transaction ids from umg_runitme_transaction table
	 * @return list of transaction ids from umg_runitme_transaction
	 */
	public List<String> getAllRtmTranIds ();
	
	/**
	 * updates the blobs to null in umg_runitme_transaction table for given transaction id
	 * @param tranId
	 * @return true if updation of blobs to null succeeds else returns false 
	 */
	public Boolean updateBlobAsNull (String tranId); 

}
