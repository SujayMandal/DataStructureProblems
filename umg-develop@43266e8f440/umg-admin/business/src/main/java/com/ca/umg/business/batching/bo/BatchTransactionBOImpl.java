package com.ca.umg.business.batching.bo;

import static com.ca.framework.core.batch.TransactionStatus.IN_EXECUTION;
import static com.ca.framework.core.batch.TransactionStatus.QUEUED;
import static com.ca.umg.business.batching.delegate.BatchingDelegateImpl.MAX_DISPLAY_RECORDS_SIZE;
import static com.ca.umg.business.batching.execution.BatchExecuterPool.putTerminatedBatchIntoCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.batching.dao.BatchDashboardFilter;
import com.ca.umg.business.batching.dao.BatchRuntimeTransactionMappingDAO;
import com.ca.umg.business.batching.dao.BatchTransactionDAO;
import com.ca.umg.business.batching.dao.BatchTransactionJdbcDAO;
import com.ca.umg.business.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.business.batching.entity.BatchTransaction;
import com.ca.umg.business.batching.execution.BatchExecuterPool;
import com.ca.umg.business.batching.info.BatchTransactionInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.plugin.commons.excel.model.ExcelData;

@Service
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class BatchTransactionBOImpl implements BatchTransactionBO {

    private static final String FILE = "File ";

	private static final String EXCEPTION_IS = ". Exception is :";

	private static final String EXCEPTION_OUTPUT_FILE = "Exception while getting output file ";

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchTransactionBOImpl.class);

    @Inject
    private BatchTransactionDAO batchTransactionDAO;

    @Inject
    private BatchTransactionJdbcDAO batchTransactionJdbcDAO;

    @Inject
    private BatchRuntimeTransactionMappingDAO transactionMappingDAO;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private SystemParameterProvider systemParameterProvider;
    
    public static final String BATCH_TERMINATED_FAILED = "Batch Terminated Failed";
    
    public static final String BATCH_TERMINATED_SUCCSS = "Batch Terminated Successfully";
    
    public static final String SYSTEM = "System";
    
    public static final long BATCH_TERMINATE_STATUS_CHECK = 2000L;
    
    @Override
    public String createBatch(String batchFileName) {

        BatchTransaction bt = new BatchTransaction();
        bt.setBatchInputFile(batchFileName);
        bt.setTest(true);
        bt.setUser(SYSTEM);
        bt.setStatus(TransactionStatus.QUEUED.getStatus());
        bt.setStartTime(System.currentTimeMillis());
        bt.setTransactionMode(ModelType.BATCH.getType());
        return batchTransactionDAO.save(bt).getId();
    }

    @Override
    public BatchTransaction getBatch(String id) {
        return batchTransactionDAO.findOne(id);
    }

    @Override
    public BatchTransaction updateBatch(String batchId, int batchCount, ExcelData excelData) throws SystemException,
            BusinessException {
        BatchTransaction bt = batchTransactionDAO.findOne(batchId);
        if (null != bt) {
            String modelName = null;
            String majorVersion = null;
            String minorVersion = null;
            if (excelData != null && excelData.getHeaderDetails() != null) {
                modelName = String.valueOf(excelData.getHeaderDetails().get("modelName"));
                majorVersion = String.valueOf(excelData.getHeaderDetails().get("majorVersion"));
                minorVersion = String.valueOf(excelData.getHeaderDetails().get("minorVersion"));
            }
            bt.setModelName(modelName);
            if (!StringUtils.isBlank(majorVersion)) {
                bt.setModelVersion(Double.parseDouble(majorVersion + "." + (minorVersion == null ? "0" : minorVersion)));
            }
            bt.setTotalRecords(Long.valueOf(Integer.toString(batchCount)));
            batchTransactionDAO.save(bt);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000093, new Object[] { batchId });
        }
        return bt;
    }

    @Override
    public BatchTransaction updateBatch(String batchId, int batchCount, int successCount, int failureCount,
            String status, String outputFileName) throws SystemException, BusinessException {
        BatchTransaction bt = batchTransactionDAO.findOne(batchId);
        if (null != bt) {
            bt.setTotalRecords(Long.valueOf(Integer.toString(batchCount)));
            bt.setSuccessCount(Long.valueOf(successCount));
            bt.setFailCount(Long.valueOf(failureCount));
            if (StringUtils.isNotBlank(status)) {
                bt.setStatus(status);
                bt.setEndTime(System.currentTimeMillis());
            }
            bt.setBatchOutputFile(outputFileName);
            batchTransactionDAO.save(bt);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000093, new Object[] { batchId });
        }
        return bt;
    }

    @Override
    public BatchTransaction updateBatch(String batchId, ExcelData excelData, int batchCount, int successCount, int failureCount,
            String status, String outputFileName) throws SystemException, BusinessException {
        String modelName = null;
        String majorVersion = null;
        String minorVersion = null;
        String versionString = null;
        Double version = null;
        BatchTransaction batchTransaction = batchTransactionDAO.findOne(batchId);
        if (excelData != null && excelData.getHeaderDetails() != null) {
            modelName = String.valueOf(excelData.getHeaderDetails().get("modelName"));
            majorVersion = String.valueOf(excelData.getHeaderDetails().get("majorVersion"));
            minorVersion = String.valueOf(excelData.getHeaderDetails().get("minorVersion"));
            versionString = majorVersion == null ? null : majorVersion + "." + (minorVersion == null ? "0" : minorVersion);
            if (StringUtils.isNotBlank(versionString)) {
                version = Double.parseDouble(versionString);
            }
        }
        if (batchTransaction != null) {
            batchTransaction.setTotalRecords(Long.valueOf(Integer.toString(batchCount)));
            batchTransaction.setSuccessCount(Long.valueOf(successCount));
            batchTransaction.setFailCount(Long.valueOf(failureCount));
            batchTransaction.setModelName(modelName);
            batchTransaction.setModelVersion(version);
            if (StringUtils.isNotBlank(status)) {
                batchTransaction.setStatus(status);
                batchTransaction.setEndTime(System.currentTimeMillis());
            }
            batchTransaction.setBatchOutputFile(outputFileName);
            batchTransactionDAO.save(batchTransaction);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000093, new Object[] { batchId });
        }
        return batchTransaction;
    }


    @Override
    public void addBatchTransactionMappings(String batchId, List<String> transactionIds) throws SystemException,
            BusinessException {
        BatchRuntimeTransactionMapping brtm;
        for (String transactionId : transactionIds) {
            if (!StringUtils.equalsIgnoreCase(transactionId, "failure")) {
                brtm = new BatchRuntimeTransactionMapping();
                brtm.setBatchTransaction(batchId);
                brtm.setTransaction(transactionId);
                transactionMappingDAO.save(brtm);
            }
        }
    }

    @Override
    public void addBatchTransactionMapping(BatchRuntimeTransactionMapping transaction) throws SystemException, BusinessException {
        transactionMappingDAO.save(transaction);
    }

    @Override
    public void addBatchTransactionMapping(String batchId, String transactionId) throws SystemException, BusinessException {
        BatchRuntimeTransactionMapping brtm = new BatchRuntimeTransactionMapping();
        brtm.setBatchTransaction(batchId);
        brtm.setTransaction(transactionId);
        transactionMappingDAO.save(brtm);
    }

    @Override
    public void invalidateBatch(String batchId) throws SystemException, BusinessException {
        LOGGER.info("Invalidating batch ID: {}", batchId);
        String batchFileName = batchTransactionDAO.findOne(batchId).getBatchInputFile();
        LOGGER.info("Batch file corresponding to batchID {} is {}", batchId, batchFileName);
        markBatchAsInvalid(batchId);
        if (StringUtils.isNotEmpty(batchFileName)) {
            moveBatchFiletoInputFolder(batchFileName);
            evictBatchFileNameFromCache(batchFileName);
        }
    }

    private void markBatchAsInvalid(String batchId) {
        BatchTransaction bt = batchTransactionDAO.findOne(batchId);
        bt.setStatus(BusinessConstants.INVALID);
        batchTransactionDAO.save(bt);
    }

    private void moveBatchFiletoInputFolder(String batchFileName) throws BusinessException, SystemException {
        String sanBase = umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE));
        String sanBasePath = AdminUtil.getSanBasePath(sanBase);
        StringBuffer batchFilePath = new StringBuffer(sanBasePath);
        batchFilePath.append(File.separatorChar).append(BusinessConstants.BATCH_FILE).append(File.separatorChar)
                .append(BusinessConstants.INPROGRESS_FOLDER).append(File.separatorChar).append(batchFileName);
        StringBuffer inputFolderPath = new StringBuffer(sanBasePath);
        inputFolderPath.append(File.separatorChar).append(BusinessConstants.BATCH_FILE).append(File.separatorChar)
                .append(BusinessConstants.INPUT_FOLDER);
        LOGGER.info("Moving batch file {} to directory {}", batchFilePath, inputFolderPath);
        File batchFile = new File(batchFilePath.toString());
        File inputFolder = new File(inputFolderPath.toString());
        try {
            FileUtils.copyFileToDirectory(batchFile, inputFolder);
            batchFile.delete();
            LOGGER.info("Moved batch file {} to directory {}", batchFilePath, inputFolderPath);
        } catch (IOException e) {
            LOGGER.error("Error in moving batch file {} to directory {}", batchFilePath, inputFolderPath);
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000107, new Object[0]);
        }

    }

    private void evictBatchFileNameFromCache(String batchFileName) {
        LOGGER.info("Evicting batch file {} from cache", batchFileName);
        Map<Object, Object> map = getCache();
        if (null != map.remove(batchFileName)) {
            LOGGER.info("Evicted batch file {} from cache", batchFileName);
        }
    }

    private Map<Object, Object> getCache() {
        return cacheRegistry.getMap(RequestContext.getRequestContext().getTenantCode() + BusinessConstants.BATCH_APPENDER);
    }

    @Override
    public byte[] getBatchInputFileContent(String batchId) throws SystemException, BusinessException {
    	 BatchTransaction batch = batchTransactionDAO.findOne(batchId);
          
        String inputFilePath = getBatchFileAbsolutPath(batch,BusinessConstants.BATCH_IP);
        
        byte[] content  = null;
        try{
        	content =  getFileContent(inputFilePath);
        } catch (SystemException exp) { // NOPMD
			if (exp.getCode().equals(BusinessExceptionCodes.BSE000010)) {
				LOGGER.error(FILE+ inputFilePath +" is not present in output_archive folder" );
				try {
					inputFilePath = getBatchFileAbsolutPath(batch,BusinessConstants.BATCH_IP);
					content = getFileContent(inputFilePath);
				} catch (SystemException exception) {					
					if (exception.getCode().equals(BusinessExceptionCodes.BSE000010)) {
						LOGGER.error(FILE+ inputFilePath +" is not present in output folder" );
						content = 	AdminUtil.createTempExc(batch.getBatchOutputFile());					
					} else {
						LOGGER.error("Exception while getting Input file."+ inputFilePath+ EXCEPTION_IS,exception);
						throw exception;
					}

				}
			} else {
				LOGGER.error("Exception while getting Input file."+ inputFilePath+ ". Exception is ",exp);
				throw exp;
			}
        }

        return content;
    }

    @Override
    public byte[] getBatchOutputFileContent(String batchId) throws SystemException, BusinessException {
    return  getBatchOutputFilePath(batchId);
       
    }
    @SuppressWarnings({"PMD.CyclomaticComplexity"})
    @Override
    public byte[] getBatchFileContent(BatchTransaction batchTran, String fileType) throws SystemException, BusinessException {
    	byte[] fileContent = null;
        String filePath = null;
        if (fileType.equals(BusinessConstants.BATCH_IP)) {
        	try{
        		filePath = getBatchFileAbsolutPath(batchTran,BusinessConstants.BATCH_IP);
        		fileContent = getFileContent(filePath);
        	}catch (SystemException exp) { // NOPMD
				if (exp.getCode().equals(BusinessExceptionCodes.BSE000010)) {
					LOGGER.error(FILE+ filePath+ " not present in input folder");				
						if (exp.getCode().equals(BusinessExceptionCodes.BSE000010)) {
							LOGGER.error(FILE+ filePath+ " not present in input folder");
							fileContent = 	AdminUtil.createTempExc(batchTran.getBatchInputFile());
						} else {
							LOGGER.error(EXCEPTION_OUTPUT_FILE+filePath+EXCEPTION_IS,exp);
							throw exp;
						}

					
				} else {
					LOGGER.error(EXCEPTION_OUTPUT_FILE+filePath+EXCEPTION_IS,exp);
					throw exp;
				}

			}

		} else {
			try {
				filePath = getBatchFileAbsolutPath(batchTran,BusinessConstants.OUTPUT_ARCHIVE_FOLDER);
				fileContent = getFileContent(filePath);
			} catch (SystemException exp) { // NOPMD
				if (exp.getCode().equals(BusinessExceptionCodes.BSE000010)) {
					LOGGER.error(FILE+ filePath+ " not present in output_archive folder");
					try {
						filePath = getBatchFileAbsolutPath(batchTran,BusinessConstants.OUTPUT_FOLDER);
						fileContent = getFileContent(filePath);
					} catch (SystemException exception) {
						if (exception.getCode().equals(BusinessExceptionCodes.BSE000010)) {
							LOGGER.error(FILE+ filePath+ " not present in output folder");
							fileContent = 	AdminUtil.createTempExc(batchTran.getBatchOutputFile());
						} else {
							LOGGER.error(EXCEPTION_OUTPUT_FILE+filePath+EXCEPTION_IS,exception);
							throw exception;
						}

					}
				} else {
					LOGGER.error(EXCEPTION_OUTPUT_FILE+filePath+EXCEPTION_IS,exp);
					throw exp;
				}

			}

		}

        if (filePath == null) {
            LOGGER.error("FilePath not found for batch id : " + batchTran.getId());
            SystemException.newSystemException(BusinessExceptionCodes.BSE000010,
                    new Object[] { String.format("FilePath not found for batch id %s ", batchTran.getId()) });
        }
       
        
        return fileContent;
    }

    @Override
    public String getBatchInputFilePath(String batchId) throws SystemException {
        String finalPath = null;
        BatchTransaction batch = batchTransactionDAO.findOne(batchId);
        finalPath = getBatchFileAbsolutPath(batch,BusinessConstants.BATCH_IP);
        return finalPath;
    }
    private byte[] getBatchOutputFilePath(String batchId) throws SystemException {
        BatchTransaction batch = batchTransactionDAO.findOne(batchId);
        String filePath = getBatchFileAbsolutPath(batch,BusinessConstants.OUTPUT_ARCHIVE_FOLDER);
        byte[] content  = null;
        try{
        	content =  getFileContent(filePath);
        } catch (SystemException exp) { // NOPMD
			if (exp.getCode().equals(BusinessExceptionCodes.BSE000010)) {
				LOGGER.error(FILE+ filePath +" is not present in output_archive folder" );
				try {
					filePath = getBatchFileAbsolutPath(batch,BusinessConstants.OUTPUT_FOLDER);
					content = getFileContent(filePath);
				} catch (SystemException exception) {					
					if (exception.getCode().equals(BusinessExceptionCodes.BSE000010)) {
						LOGGER.error(FILE+ filePath +" is not present in output folder" );
						content = 	AdminUtil.createTempExc(batch.getBatchOutputFile());					
					} else {
						LOGGER.error(EXCEPTION_OUTPUT_FILE+ filePath+ ". Exception is ",exception);
						throw exception;
					}

				}
			} else {
				LOGGER.error(EXCEPTION_OUTPUT_FILE+ filePath+ ". Exception is ",exp);
				throw exp;
			}
        }

        return content;
    }

    private String getBatchFileAbsolutPath(BatchTransaction batch,String folderName) throws SystemException {
        String finalPath = null;
        String sanBase = umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE));
        StringBuffer tenantSanBase = new StringBuffer(AdminUtil.getSanBasePath(sanBase));
        StringBuffer batchFolder = new StringBuffer(tenantSanBase).append(File.separator).append(BusinessConstants.BATCH_FILE);
        if (batch.isTest()) {
            batchFolder.append(File.separator).append(BusinessConstants.BATCH_TEST);
        }
        StringBuffer folderPath = new StringBuffer(batchFolder).append(File.separator);
        if(BusinessConstants.BATCH_IP.equals(folderName)){ 
            folderPath = folderPath.append(BusinessConstants.ARCHIEVE_FOLDER);
            finalPath = new StringBuffer(folderPath).append(File.separator).append(batch.getBatchInputFile()).toString();
        }else {
            folderPath = folderPath.append(folderName);
            finalPath = new StringBuffer(folderPath).append(File.separator).append(batch.getBatchOutputFile()).toString();
        }
     
        return finalPath;
    }

    private byte[] getFileContent(String absoluteFilePath) throws SystemException {
        InputStream inputStream = null;
        byte[] fileData = null;

        File file = new File(absoluteFilePath);
        if (file.exists() && file.isFile()) {
            try {
                inputStream = new FileInputStream(file);
                fileData = AdminUtil.convertStreamToByteArray(inputStream);
            } catch (FileNotFoundException exp) {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000010,
                        new Object[] { String.format("File %s not found.", absoluteFilePath) });
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("Exception occured while closing Input Stream", e);
                    }
                }
            }
        } else {
            LOGGER.error("BatchTransactionBOImpl:getFileContent :: File not found in path : " + absoluteFilePath);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000010,
                    new Object[] { String.format("File %s not found.", absoluteFilePath) });
        }

        return fileData;
    }
    
	

    public List<BatchTransactionInfo> getPagedBatchData(BatchDashboardFilter filter, final boolean isEmptySearch)
            throws BusinessException {
        final long count = batchTransactionJdbcDAO.getBatchTransactionCount(filter);
        filter.setMatchedTransactionCount(count);
        List<BatchTransactionInfo> result = new ArrayList<>();

        if (count <= MAX_DISPLAY_RECORDS_SIZE || isEmptySearch) {
            result = batchTransactionJdbcDAO.getPagedBatchData(filter);
        }

        return result;
    }

    public Map<String, Long> getBatchStatusCount(final String batchIds, final String status) throws SystemException {
        return batchTransactionJdbcDAO.getTxnIdsFromBatchId(batchIds, status);
    }

    @Override
    public String terminateBatch(final String batchId) {
        LOGGER.info("Terminating batch ID: {}", batchId);
        putTerminatedBatchIntoCache(cacheRegistry, batchId);

        BatchTransaction terminatedBatchTransaction = null;
        String status = BATCH_TERMINATED_FAILED;
        while (true) {
            try {
                terminatedBatchTransaction = batchTransactionDAO.findOne(batchId);
                if (terminatedBatchTransaction.getStatus() != null
                        && !(terminatedBatchTransaction.getStatus().equalsIgnoreCase(QUEUED.getStatus()) || terminatedBatchTransaction
                                .getStatus().equalsIgnoreCase(IN_EXECUTION.getStatus()))) {
                    status = BATCH_TERMINATED_SUCCSS;
                    BatchExecuterPool.removeTerminatedBatchFromCache(cacheRegistry, batchId);
                    break;
                }
                Thread.sleep(BATCH_TERMINATE_STATUS_CHECK);
            } catch (InterruptedException e) {
                LOGGER.error("Error while terminating batch,  Batch id is : {}", batchId);
                LOGGER.error(e.getLocalizedMessage());
            }
        }

        return status;
    }

    @Override
    public String createBulk(BatchTransaction bt) {
        return batchTransactionDAO.save(bt).getId();
    }
}