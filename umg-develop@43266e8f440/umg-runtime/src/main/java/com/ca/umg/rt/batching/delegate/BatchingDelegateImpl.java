package com.ca.umg.rt.batching.delegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.batching.bo.BatchTransactionBO;
import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.batching.entity.BatchTransaction;

@SuppressWarnings("PMD")
@Named
public class BatchingDelegateImpl implements BatchingDelegate {
    
    @Inject
    private BatchTransactionBO batchTransactionBO;

    @Override
    public String createBatchEntry(String batchFileName, String tenantCode, Boolean isBulk) throws SystemException,
            BusinessException {
        return batchTransactionBO.createBatchEntry(batchFileName, tenantCode, isBulk);
    }
    
    @Override
    public String createBatch(String batchFileName, TransactionStatus batchStatus, Boolean isBulk, final int test, String user, String modelName, String majorVersion, String minorVersion, String timeStamp, String transactionId,boolean storeRLog) throws SystemException,
            BusinessException {
        return batchTransactionBO.createBatch(batchFileName,batchStatus, isBulk, test, user, modelName, majorVersion, minorVersion, timeStamp, transactionId,storeRLog);
    }
    
    @Override
    public BatchTransaction getBatchForFileName(String fileName) {
        return batchTransactionBO.getBatchForFileName(fileName);
    }

    @Override
    public BatchTransaction updateBatch(String batchId, int batchCount)
            throws SystemException, BusinessException {
        return batchTransactionBO.updateBatch(batchId, batchCount);
        
    }
    
    @Override
    public BatchTransaction updateNotPickedCount(String batchId, int notPickedCount)
            throws SystemException, BusinessException {
        return batchTransactionBO.updateNotPickedCount(batchId, notPickedCount);
        
    }
    
    @Override
    public BatchTransaction updateSuccessFailCount(String batchId, int successCount, int failCount)
            throws SystemException, BusinessException {
        return batchTransactionBO.updateSuccessFailCount(batchId, successCount, failCount);
        
    }

    @Override
    public BatchTransaction updateBatch(String batchId, int batchCount,
            int successCount, int failureCount, String status)
            throws SystemException, BusinessException {
        return batchTransactionBO.updateBatch(batchId, batchCount, successCount, failureCount, status);
    }
    
    @Override
    public BatchTransaction updateBatchStatusOnly(String batchId, String status)
            throws SystemException, BusinessException {
        return batchTransactionBO.updateBatchStatusOnly(batchId, status);
    }

    @Override
    public BatchTransaction getBatch(String batchId) {
        return batchTransactionBO.getBatch(batchId);
    }

    @Override
    public List<BatchTransaction> getAllBatches() {
        return batchTransactionBO.getAllBatches();
    }

    @Override
    public void addBatchTransactionMapping(BatchRuntimeTransactionMapping runtimeTransactionMapping, Boolean isBulk)
            throws SystemException, BusinessException {
        batchTransactionBO.addBatchTransactionMapping(runtimeTransactionMapping,isBulk);
    }
    
    @Override
    public void updateBatchTransactionMapping(BatchRuntimeTransactionMapping runtimeTransactionMapping)
            throws SystemException, BusinessException {
        batchTransactionBO.updateBatchTransactionMapping(runtimeTransactionMapping);
    }

    /* (non-Javadoc)
     * @see com.ca.umg.rt.batching.delegate.BatchingDelegate#updateBatchOutputFile(java.lang.String, java.lang.String)
     */
    @Override
    public void updateBatchOutputFile(String batchId, String outputFileName) {
        batchTransactionBO.updateBatchOutputFile(batchId, outputFileName);
    }

	@Override
	public byte[] getModelOutput(String tenantCode, String versionName, int majorVersion, int minorVersion){		
		return batchTransactionBO.getModelOutput(tenantCode,versionName,majorVersion,minorVersion);
	}
}