package com.ca.umg.rt.batching.bo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.pool.TransactionMode;
import com.ca.umg.rt.batching.dao.BatchRuntimeTransactionMappingDAO;
import com.ca.umg.rt.batching.dao.BatchTransactionDAO;
import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.batching.entity.BatchTransaction;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.mysql.jdbc.StringUtils;

@SuppressWarnings("PMD")
@Service
public class BatchTransactionBOImpl implements BatchTransactionBO {

    private static final String JSON = ".json";

    @Inject
    private BatchTransactionDAO batchTransactionDAO;

    @Inject
    private RModelDAO rModelDAO;

    @Inject
    private BatchRuntimeTransactionMappingDAO transactionMappingDAO;

    @Inject
    private CacheRegistry cacheRegistry;

    @Override
    public String createBatchEntry(String batchFileName, String tenantCode, Boolean isBulk) throws SystemException {
        BatchTransaction bt = new BatchTransaction();
        bt.setStatus(TransactionStatus.QUEUED.getStatus());
        bt.setTenantId(tenantCode);
        bt.setId(UUID.randomUUID().toString());
        bt.setBatchInputFileName(batchFileName);
        bt.setStartTime(System.currentTimeMillis());
        bt.setCreatedDate(System.currentTimeMillis());
        bt.setCreatedBy(tenantCode);
        bt.setUser(null);
        bt.setModelName(null);
        bt.setStoreRlogs(Boolean.FALSE.toString());
        bt.setModelVersion(null);
        bt.setExecEnv(null);
        bt.setModellingEnv(null);
        if(isBulk){
        	bt.setTransactionMode(TransactionMode.BULK.getMode());
        } else {
        	bt.setTransactionMode(TransactionMode.BATCH.getMode());
        }
        return batchTransactionDAO.save(bt, RuntimeConstants.INT_ZERO, true).getId();
    }

    @Override
    public String createBatch(String batchFileName, TransactionStatus b, Boolean isBulk, final int test, String user,
            String modelName, String majorVersion, String minorVersion, String timeStamp, String transactionId, boolean storeRLog)
            throws SystemException {
    	boolean newEntry = false;
        BatchTransaction bt = new BatchTransaction();
        bt.setStatus((b != null) ? b.getStatus() : TransactionStatus.QUEUED.getStatus());
        bt.setTenantId(RequestContext.getRequestContext().getTenantCode());
        if (batchFileName == null) {
        	newEntry = true;
            bt.setId(UUID.randomUUID().toString());
            bt.setBatchInputFileName(
                    new StringBuffer().append(transactionId).append(PoolConstants.ENV_SEPERATOR).append(timeStamp)
                            .append(PoolConstants.ENV_SEPERATOR).append(RuntimeConstants.TENANT_INPUT).append(JSON).toString());
            bt.setBatchOutputFileName(
                    new StringBuffer().append(transactionId).append(PoolConstants.ENV_SEPERATOR).append(timeStamp)
                            .append(PoolConstants.ENV_SEPERATOR).append(RuntimeConstants.TENANT_OUTPUT).append(JSON).toString());
        } else {
        	Map<String, String> files = null;
        	while(MapUtils.isEmpty(files) || StringUtils.isNullOrEmpty(files.get(batchFileName))){
        		files = (Map<String, String>) cacheRegistry.getMap(isBulk ? FrameworkConstant.BULK_INPUT_FILES_MAP : FrameworkConstant.BATCH_INPUT_FILES_MAP)
        				.get(RequestContext.getRequestContext().getTenantCode());
        	}
            bt.setId(files.get(batchFileName));
            bt.setBatchInputFileName(batchFileName);
        }
        bt.setStartTime(System.currentTimeMillis());
        bt.setCreatedDate(System.currentTimeMillis());
        bt.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
        bt.setUser(user);
        bt.setModelName(modelName);
        if (storeRLog) {
            bt.setStoreRlogs("TRUE");
        } else {
            bt.setStoreRlogs("FALSE");
        }
        minorVersion = (minorVersion == null ? "0" : minorVersion);
        bt.setModelVersion(majorVersion == null ? null : (majorVersion + "." + minorVersion));
        VersionExecInfo versionExecInfo = rModelDAO.getEnvironmentDetails(RequestContext.getRequestContext().getTenantCode(),
                modelName, majorVersion, minorVersion);
        if (versionExecInfo != null) {
            bt.setExecEnv(versionExecInfo.getExecEnv());
            bt.setModellingEnv(
                    versionExecInfo.getExecLanguage() + RuntimeConstants.CHAR_HYPHEN + versionExecInfo.getExecLangVer());
        }
        if (isBulk) {
            bt.setTransactionMode(TransactionMode.BULK.getMode());
        } else {
            bt.setTransactionMode(TransactionMode.BATCH.getMode());
        }
        return batchTransactionDAO.save(bt, test, newEntry).getId();
    }

    @Override
    public BatchTransaction getBatch(String id) {
        return batchTransactionDAO.findOne(id);
    }

    @Override
    public BatchTransaction getBatchForFileName(String fileName) {
        return batchTransactionDAO.findByFileNameForBulk(fileName);
    }

    @Override
    public BatchTransaction updateBatch(String batchId, int batchCount) throws SystemException, BusinessException {
        BatchTransaction bt = batchTransactionDAO.findOne(batchId);
        if (null != bt) {
            bt.setTotalRecords(Long.valueOf(Integer.toString(batchCount)));
            batchTransactionDAO.updateBatch(batchId, batchCount);
        } else {
            BusinessException.raiseBusinessException(RuntimeExceptionCode.RSE000508, new Object[] { batchId });
        }
        return bt;
    }

    @Override
    public BatchTransaction updateNotPickedCount(String batchId, int notPickedCount) throws SystemException, BusinessException {
        BatchTransaction bt = batchTransactionDAO.findOne(batchId);
        if (null != bt) {
            bt.setNotPickedCount(Long.valueOf(Integer.toString(notPickedCount)));
            batchTransactionDAO.updateNotPickedCount(batchId, notPickedCount);
        } else {
            BusinessException.raiseBusinessException(RuntimeExceptionCode.RSE000508, new Object[] { batchId });
        }
        return bt;
    }

    @Override
    public BatchTransaction updateSuccessFailCount(String batchId, int successCount, int failCount)
            throws SystemException, BusinessException {
        BatchTransaction bt = batchTransactionDAO.findOne(batchId);
        if (null != bt) {
            bt.setSuccessCount(Long.valueOf(Integer.toString(successCount)));
            bt.setFailCount(Long.valueOf(Integer.toString(failCount)));
            batchTransactionDAO.updateSuccessFailCount(batchId, successCount, failCount);
        } else {
            BusinessException.raiseBusinessException(RuntimeExceptionCode.RSE000508, new Object[] { batchId });
        }
        return bt;
    }

    @Override
    public BatchTransaction updateBatch(String batchId, int batchCount, int successCount, int failureCount, String status)
            throws SystemException, BusinessException {
        BatchTransaction bt = batchTransactionDAO.findOne(batchId);
        if (null != bt) {
            batchTransactionDAO.updateStatus(batchId, batchCount, successCount, failureCount, status);
        } else {
            BusinessException.raiseBusinessException(RuntimeExceptionCode.RSE000508, new Object[] { batchId });
        }
        return bt;
    }

    @Override
    public BatchTransaction updateBatchStatusOnly(String batchId, String status) throws SystemException, BusinessException {
        BatchTransaction bt = batchTransactionDAO.findOne(batchId);
        if (null != bt) {
            batchTransactionDAO.updateStatusOnly(batchId, status);
        } else {
            BusinessException.raiseBusinessException(RuntimeExceptionCode.RSE000508, new Object[] { batchId });
        }
        return bt;
    }

    @Override
    public void addBatchTransactionMapping(BatchRuntimeTransactionMapping runtimeTransactionMapping, Boolean isBulk)
            throws SystemException, BusinessException {
        runtimeTransactionMapping.setId(UUID.randomUUID().toString());
        runtimeTransactionMapping.setTenantId(RequestContext.getRequestContext().getTenantCode());
        if (isBulk) {
            runtimeTransactionMapping.setCreatedBy(TransactionMode.BULK.getMode());
        } else {
            runtimeTransactionMapping.setCreatedBy(TransactionMode.BATCH.getMode());
        }
        runtimeTransactionMapping.setCreatedDate(System.currentTimeMillis());
        transactionMappingDAO.save(runtimeTransactionMapping);
    }

    @Override
    public void updateBatchTransactionMapping(BatchRuntimeTransactionMapping runtimeTransactionMapping)
            throws SystemException, BusinessException {
        runtimeTransactionMapping.setTenantId(RequestContext.getRequestContext().getTenantCode());
        runtimeTransactionMapping.setLastModifiedDate(System.currentTimeMillis());
        transactionMappingDAO.update(runtimeTransactionMapping);
    }

    @Override
    public List<BatchTransaction> getAllBatches() {
        return batchTransactionDAO.findAll();
    }

    @Override
    public void updateBatchOutputFile(String batchId, String outputFileName) {
        batchTransactionDAO.updateBatchOutputFile(batchId, outputFileName);
    }

    @Override
    public byte[] getModelOutput(String tenantCode, String versionName, int majorVersion, int minorVersion) {
        return rModelDAO.getMappingOutput(tenantCode, versionName, majorVersion, minorVersion);
    }
}