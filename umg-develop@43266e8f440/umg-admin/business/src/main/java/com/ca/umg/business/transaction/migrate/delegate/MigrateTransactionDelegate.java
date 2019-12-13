package com.ca.umg.business.transaction.migrate.delegate;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.transaction.migrate.execution.StopMigrateTransaction;

public interface MigrateTransactionDelegate {
	
	/**
	 * method moves all the blobs from mysql (umg_runtime_transaction table) to mongo 
	 * @return the report of blob movement from mysql to mongo for all tenants 
	 */
	public List<Map<String,Object>> moveBlobsOfAllTransactions ();
	
	/**
	 * method to stop transaction migration by setting the stopMigration flag in {@link StopMigrateTransaction}
	 * @throws BusinessException
	 */
	public void stopTransactionMigration() throws BusinessException;

}
