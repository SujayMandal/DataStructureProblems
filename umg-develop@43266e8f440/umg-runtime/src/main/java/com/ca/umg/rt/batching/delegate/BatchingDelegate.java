/**
 * 
 */
package com.ca.umg.rt.batching.delegate;

import java.util.List;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.batching.entity.BatchTransaction;

/**
 * @author kabiju
 * 
 */
@SuppressWarnings("PMD")
public interface BatchingDelegate {

	/**
     * 
     * Create a new batch entry for the input batch file.
     * 
     * @param batchFileName
     * @return batchId
     * @throws SystemException
     * @throws BusinessException
     */
    String createBatchEntry(String batchFileName, String tenantCode, Boolean isBulk) throws SystemException, BusinessException;
	
    /**
     * 
     * Create a new batch for the input batch file.
     * 
     * @param batchFileName
     * @return batchId
     * @throws SystemException
     * @throws BusinessException
     */
    String createBatch(String batchFileName, TransactionStatus batchStatus, Boolean isBulk, final int test, String user, String modelName, String majorVersion, String minorVersion, String timeStamp, String transactionId , boolean storeRLog) throws SystemException, BusinessException;

    /**
     * 
     * Update an existing batch with the number of records present in the batch
     * 
     * @param batchId
     * @param batchCount
     * @return BatchTransaction entity
     * @throws SystemException
     * @throws BusinessException
     */
    BatchTransaction updateBatch(String batchId, int batchCount) throws SystemException, BusinessException;
    
    /**
     * 
     * Update an existing batch with the number of not picked transaction Count in the batch
     * 
     * @param batchId
     * @param notPickedCount
     * @return BatchTransaction entity
     * @throws SystemException
     * @throws BusinessException
     */
    BatchTransaction updateNotPickedCount(String batchId, int notPickedCount) throws SystemException, BusinessException;
    
    /**
     * 
     * Update an existing batch with the success and fail count after completion of each transaction
     * 
     * @param batchId
     * @param successCount
     * @param failureCount
     * @return BatchTransaction entity
     * @throws SystemException
     * @throws BusinessException
     */
    BatchTransaction updateSuccessFailCount(String batchId, int successCount, int failureCount) throws SystemException, BusinessException;

    /**
     * 
     * Update an existing batch with counts (batch, success & failure) and its status
     * 
     * 
     * @param batchId
     * @param batchCount
     * @param successCount
     * @param failureCount
     * @param status
     * @return BatchTransaction
     * @throws SystemException
     * @throws BusinessException
     */
    BatchTransaction updateBatch(String batchId, int batchCount, int successCount, int failureCount, String status)
            throws SystemException, BusinessException;

    /**
     * 
     * Retrieve the BatchTransaction object corresponding to the input batchId
     * 
     * @param batchId
     * @return BatchTransaction
     */
    BatchTransaction getBatch(String batchId);
    
    /**
     * gets the batch object for filename
     * @param fileName
     * @return
     */
    public BatchTransaction getBatchForFileName(String fileName);

    /**
     * Retrieve all batches
     * 
     * @return List<BatchTransaction>
     */
    List<BatchTransaction> getAllBatches();

    /**
     * Adds an entry in the BatchTxnRuntimeTxn mapping table for the input transactionId-batchId pair
     * 
     * @param runtimeTransactionMapping
     * @throws SystemException
     * @throws BusinessException
     */
    void addBatchTransactionMapping(BatchRuntimeTransactionMapping runtimeTransactionMapping, Boolean isBulk) throws SystemException,
            BusinessException;

    /**
     * This method would update the output file name for the given batchId.
     * 
     * @param batchId
     * @param outputFileName
     */
    void updateBatchOutputFile(String batchId, String outputFileName);
    
    byte[] getModelOutput(final String tenantCode,final String versionName,final int majorVersion,final int minorVersion);

	void updateBatchTransactionMapping(BatchRuntimeTransactionMapping runtimeTransactionMapping) throws SystemException, 
			BusinessException;

	BatchTransaction updateBatchStatusOnly(String batchId, String status) throws SystemException, BusinessException;

}
