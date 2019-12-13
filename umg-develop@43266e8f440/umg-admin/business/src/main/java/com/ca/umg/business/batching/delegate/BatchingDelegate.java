/**
 * 
 */
package com.ca.umg.business.batching.delegate;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.batching.dao.BatchDashboardFilter;
import com.ca.umg.business.batching.dao.BatchTransactionInfoWrapper;
import com.ca.umg.business.batching.entity.BatchTransaction;

/**
 * @author kabiju
 * 
 */
public interface BatchingDelegate {

    /**
     * The method would execute a batch.
     * 
     * @param jsonList
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    String executeBatchAsync(String tenantUrl, String fileName, InputStream excelInputStream) throws SystemException,
            BusinessException;

    /**
     * 
     * Invalidates a batch
     * 
     * @param batchId
     * 
     */

    void invalidateBatch(String batchFileName) throws SystemException, BusinessException;

    /**
     * It will return byte array of the given file name, from relative san location.
     * 
     * @param filename
     * @return byte[]
     * */
    byte[] getBatchInputFileContent(String batchId) throws SystemException, BusinessException;

    /**
     * It will return byte array of the given file name, from relative san location.
     * 
     * @param filename
     * @return byte[]
     * */
    public byte[] getBatchFileContent(BatchTransaction batchTran, String fileType) throws SystemException, BusinessException;

    /**
     * It will return byte array of the given file name, from relative san location.
     * 
     * @param filename
     * @return byte[]
     * */
    byte[] getBatchOutputFileContent(String batchId) throws SystemException, BusinessException;

    /**
     * 
     * Retrieve the BatchTransaction object corresponding to the input batchId
     * 
     * @param batchId
     * @return BatchTransaction
     */
    public BatchTransaction getBatch(String batchId);
    
    /**
     * This method will save the Excel file into the tenant SAN location
     */
    void saveExcelFile(MultipartFile excelFile, String fileName) throws SystemException, BusinessException;
    
    public BatchTransactionInfoWrapper getPagedBatchData(final BatchDashboardFilter filter) throws SystemException, BusinessException;
    
    public String formSearchResultMessage(final BatchDashboardFilter filter, final long totalCount, final long returnCount);
    
    public boolean isEmptySearch(final BatchDashboardFilter filter);
    
    public String terminateBatch(final String batchId);
    void saveBulkFile(MultipartFile jsonFile, String fileName) throws SystemException, BusinessException;
    
}
