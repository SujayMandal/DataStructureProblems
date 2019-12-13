package com.ca.umg.rt.batching.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.rt.batching.dao.BatchRuntimeTransactionMappingDAO;
import com.ca.umg.rt.batching.dao.BatchTransactionDAO;
import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.batching.entity.BatchTransaction;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

/**
 * @author basanaga
 * 
 */
@Service
public class BatchFileStatusBOImpl implements BatchFileStatusBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFileStatusBOImpl.class);

    @Inject
    private BatchTransactionDAO batchTransactionDAO;

    @Inject
    private BatchRuntimeTransactionMappingDAO transactionMappingDAO;

    @Override
    public List<Map<String, Object>> getBatcheStatusByFileNameAndStartTime(String batchFileName, long submissionTime)
            throws BusinessException {
        LOGGER.info("Searching date is :" + submissionTime);
        List<Map<String, Object>> batchFileList = new ArrayList<Map<String, Object>>();
        List<BatchTransaction> batchTxnList = batchTransactionDAO.findByBatchFileNameAndStartTime(batchFileName, submissionTime);
        for (BatchTransaction batchTransaction : batchTxnList) {
            Map<String, Object> batchStatusInfo = new LinkedHashMap<String, Object>();
            long txnCount = 0;
            long successCount = 0;
            long failedCount = 0;
            long timedoutCount = 0;
            if (StringUtils.equalsIgnoreCase(batchTransaction.getStatus(), TransactionStatus.QUEUED.toString())) {
                List<BatchRuntimeTransactionMapping> batchTransactionMappings = transactionMappingDAO
                        .findAllByBatchId(batchTransaction.getId());
                for (BatchRuntimeTransactionMapping batchRuntimeTransactionMapping : batchTransactionMappings) {
                    if (StringUtils.equals(batchRuntimeTransactionMapping.getStatus(), RuntimeConstants.SUCCESS)) {
                        successCount = successCount + 1;
                    } else if (StringUtils.equals(batchRuntimeTransactionMapping.getStatus(), RuntimeConstants.FAILURE)) {
                        failedCount = failedCount + 1;
                    } else if (StringUtils.equals(batchRuntimeTransactionMapping.getStatus(), TransactionStatus.TIMEOUT.getStatus())) {
                        timedoutCount = timedoutCount + 1;
                    }

                    txnCount++;

                }
            } else {
                successCount = batchTransaction.getSuccessCount();
                failedCount = batchTransaction.getFailCount();
                txnCount = batchTransaction.getTotalRecords();
                // In batchHttpHandler - Check for timeout and update the count in batchLRU. Finally write the timeout count in
                // batch transaction table.
                // Also create the record in batchTransactionMapping table.
                // Alter the batch transaction table to store timeout count.

                // This process remains for IN-PROGRESS batches. For PROCESSED we would get the counts from batchTransaction
                // record.
            }
            if (batchTransaction.getBatchInputFileName() == null) {
                batchStatusInfo.put("Application internal file name", StringUtils.EMPTY);
            } else {
                batchStatusInfo.put("Application internal file name", batchTransaction.getBatchInputFileName());
            }
            if (batchTransaction.getBatchOutputFileName() == null) {
                if (StringUtils.equals(TransactionStatus.PROCESSED.toString(), batchTransaction.getStatus())) {
                    batchStatusInfo.put("Output file name", "Output file being populated with results");
                } else {
                    batchStatusInfo.put("Output file name", StringUtils.EMPTY);
                }
            } else {
                batchStatusInfo.put("Output file name ", batchTransaction.getBatchOutputFileName());
            }
            batchStatusInfo.put("Batch status", batchTransaction.getStatus());
            if (StringUtils.equals(RuntimeConstants.ERROR, batchTransaction.getStatus())) {
                batchStatusInfo.put("Error message", "Incorrect input file format OR Virus infected input file");
            }
            double elapsedTime = 0;
            double estimatedTime = 0;
            if (txnCount > RuntimeConstants.INT_ZERO) {
                if (StringUtils.equals(TransactionStatus.PROCESSED.toString(), batchTransaction.getStatus())) {
                    elapsedTime = batchTransaction.getEndTime() - batchTransaction.getStartTime();
                } else {
                    elapsedTime = System.currentTimeMillis() - batchTransaction.getStartTime();

                    if (txnCount > RuntimeConstants.INT_ZERO) {
                        estimatedTime = (elapsedTime / txnCount) * (batchTransaction.getTotalRecords() - txnCount);
                    }
                }
                batchStatusInfo.put("Elapsed time (post pickup for processing) / Time taken to complete",
                        setTimeinMinAndSec(elapsedTime));
                batchStatusInfo.put("Estimated time to complete", setTimeinMinAndSec(estimatedTime));
            }
            batchStatusInfo.put("#Total requests", batchTransaction.getTotalRecords());
            batchStatusInfo.put("#Requests completed", txnCount);
            batchStatusInfo.put("#Requests remaining", batchTransaction.getTotalRecords() - txnCount);
            batchStatusInfo.put("#Requests succeeded", successCount);
            batchStatusInfo.put("#Requests failed", failedCount);
            batchStatusInfo.put("#Requests timed-out", timedoutCount);
            batchFileList.add(batchStatusInfo);
        }

        return batchFileList;

    }

    private String setTimeinMinAndSec(double time) {
        double timeInSec = time * 0.001;
        if (timeInSec < RuntimeConstants.INT_SIXTY) {
            return Math.ceil(timeInSec) + " sec";
        } else {
            double minutes = (int) timeInSec / RuntimeConstants.INT_SIXTY;
            double sec = timeInSec % RuntimeConstants.INT_SIXTY;
            return Math.ceil(minutes) + " min " + Math.ceil(sec) + " sec";

        }

    }
}