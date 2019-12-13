package com.ca.umg.business.transaction.migrate.bo;

import java.util.Map;

public interface MigrateTransactionBO {
	/**
	 * moves the blob from mysql table-umg_runtime_transaction to mongo 
	 * @return map having consisting the count of success/failed transaction and failed tranids and reason 
	 */
	public Map<String,Object> moveBlobsOfRuntmTransactions ();
}
