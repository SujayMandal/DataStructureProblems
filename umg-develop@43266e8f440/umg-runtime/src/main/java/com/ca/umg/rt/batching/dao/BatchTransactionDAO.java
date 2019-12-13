package com.ca.umg.rt.batching.dao;

import java.util.List;

import com.ca.umg.rt.batching.entity.BatchTransaction;

public interface BatchTransactionDAO {

    List<BatchTransaction> findByBatchFileName(String batchFileName);
    
    /**
     * gets the batch object for filename
     * @param batchFileName
     * @return
     */
    public BatchTransaction findByFileNameForBulk(String batchFileName);

    BatchTransaction save(BatchTransaction batchTransaction, final int test, final boolean newEntry);

    List<BatchTransaction> findAll();

    BatchTransaction findOne(String batchId);

    void updateBatch(String batchId, int totalCount);

    BatchTransaction updateStatus(String batchId, int batchCount, int successCount, int failureCount, String status);
    
    void updateBatchOutputFile(String batchId, String outputFileName);

    List<BatchTransaction> findByBatchFileNameAndStartTime(String batchInputFile, long startTime);
    
    void updateEnvAndModelEnvs(String batchId, String execEnv,String modellingEnv);

	BatchTransaction updateNotPickedCount(String batchId, int notPickedCount);

	BatchTransaction updateSuccessFailCount(String batchId, int successCount,
			int failCount);

	BatchTransaction updateStatusOnly(String batchId, String status);
}
