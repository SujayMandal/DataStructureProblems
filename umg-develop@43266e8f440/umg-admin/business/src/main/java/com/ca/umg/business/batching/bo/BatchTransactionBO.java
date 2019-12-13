package com.ca.umg.business.batching.bo;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.batching.dao.BatchDashboardFilter;
import com.ca.umg.business.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.business.batching.entity.BatchTransaction;
import com.ca.umg.business.batching.info.BatchTransactionInfo;
import com.ca.umg.plugin.commons.excel.model.ExcelData;

/**
 * 
 * API to create, read and update BATCH_TRANSACTION table.
 * 
 * Users of this API - Controller classes that read batch inputs from various
 * sources.
 * 
 * @author raghavni
 * 
 */
public interface BatchTransactionBO {

	/**
	 * 
	 * Create a new batch for the input batch file.
	 * 
	 * @param batchFileName
	 * @return batchId
	 * @throws SystemException
	 * @throws BusinessException
	 */
	public String createBatch(String batchFileName) throws SystemException,
			BusinessException;

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
	public BatchTransaction updateBatch(String batchId, int batchCount, ExcelData excelData)
			throws SystemException, BusinessException;

	/**
	 * 
	 * Update an existing batch with counts (batch, success & failure) and its
	 * status
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

	public BatchTransaction updateBatch(String batchId, int batchCount,
 int successCount, int failureCount, String status,
			String outputFileName) throws SystemException, BusinessException;
	/**
	 * 
	 * Update an existing batch with counts (batch, success & failure) and its
	 * status
	 * 
	 * 
	 * @param batchId
	 * @param excelData
	 * @param batchCount
	 * @param successCount
	 * @param failureCount
	 * @param status
	 * @return BatchTransaction
	 * @throws SystemException
	 * @throws BusinessException
	 */

	public BatchTransaction updateBatch(String batchId,ExcelData excelData, int batchCount,
 int successCount, int failureCount,
            String status,
			String outputFileName) throws SystemException, BusinessException;
	/**
	 * 
	 * Retrieve the BatchTransaction object corresponding to the input batchId
	 * 
	 * @param batchId
	 * @return BatchTransaction
	 */
	public BatchTransaction getBatch(String batchId);

	/**
	 * 
	 * Adds one entry in the BatchTxnRuntimeTxn mapping table for each item in
	 * the input list of transactions against the corresponding batchId
	 * 
	 * @param batchId
	 * @param transactionId
	 * @throws SystemException
	 * @throws BusinessException
	 */
	public void addBatchTransactionMappings(String batchId,
			List<String> transactionId) throws SystemException,
			BusinessException;

	/**
	 * 
	 * Adds one entry in the BatchTxnRuntimeTxn mapping table
	 */
	public void addBatchTransactionMapping(
			BatchRuntimeTransactionMapping transaction) throws SystemException,
			BusinessException;

	/**
	 * 
	 * Adds an entry in the BatchTxnRuntimeTxn mapping table for the input
	 * transactionId-batchId pair
	 * 
	 * @param batchId
	 * @param transactionId
	 * @throws SystemException
	 * @throws BusinessException
	 */
	public void addBatchTransactionMapping(String batchId, String transactionId)
			throws SystemException, BusinessException;

	/**
	 * 
	 * Invalidate a batch
	 * 
	 * @param batchId
	 * 
	 */

	public void invalidateBatch(String batchFileName) throws SystemException,
			BusinessException;

    /**
	 * It will return absolute path of input file used for Batch with given id
	 * */
    String getBatchInputFilePath(String batchId) throws SystemException;



	/**
	 * It will return byte array of the given file name, from relative san
	 * location.
	 * 
	 * @param batchId
	 *            : String
	 * @return byte[]
	 * */
	byte[] getBatchInputFileContent(String batchId) throws SystemException,
			BusinessException;

	/**
	 * It will return byte array of the given file name, from relative san
	 * location.
	 * 
	 * @param batchId
	 *            : String
	 * @return byte[]
	 * */
	byte[] getBatchOutputFileContent(String batchId) throws SystemException,
			BusinessException;
	
	/**
	 * It will return byte array of the given file name, from relative san
	 * location.
	 * 
	 * @param batchId
	 *            : String
	 * @return byte[]
	 * */
	public byte[] getBatchFileContent(BatchTransaction batchTran, String fileType) throws SystemException,
			BusinessException;

	public List<BatchTransactionInfo> getPagedBatchData(BatchDashboardFilter filter, final boolean isEmptySearch) throws BusinessException;
	
	public Map<String, Long> getBatchStatusCount(final String batchIds, final String status) throws SystemException;

	public String terminateBatch(final String batchId);
	
	public String createBulk(BatchTransaction bt);
}
