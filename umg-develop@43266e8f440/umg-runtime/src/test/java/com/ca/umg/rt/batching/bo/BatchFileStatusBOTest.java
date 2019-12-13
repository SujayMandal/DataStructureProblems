package com.ca.umg.rt.batching.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.batching.dao.BatchRuntimeTransactionMappingDAO;
import com.ca.umg.rt.batching.dao.BatchTransactionDAO;
import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.batching.entity.BatchTransaction;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BatchFileStatusBOTest {

    @Inject
    private BatchFileStatusBO batchFileStatusBOImpl;

    @Inject
    private BatchTransactionDAO batchTransactionDAO;

    @Inject
    private BatchRuntimeTransactionMappingDAO transactionMappingDAO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetBatchFileStatus() throws BusinessException, SystemException {
        when(batchTransactionDAO.findByBatchFileNameAndStartTime(Mockito.anyString(), Mockito.anyLong())).thenReturn(
                getBatchTransactions());
        when(transactionMappingDAO.findAllByBatchId("2")).thenReturn(getBatchTransactionMappings());
        List<Map<String, Object>> batchStatusInfos = batchFileStatusBOImpl.getBatcheStatusByFileNameAndStartTime("aqmk",
                143245678);
        assertNotNull(batchStatusInfos);
        assertEquals(4, batchStatusInfos.size());
        for (Map<String, Object> batchStatusInfo : batchStatusInfos) {
            if (StringUtils.equals((String) batchStatusInfo.get("Batch status"), "PROCESSED")) {
                assertEquals(batchStatusInfo.get("#Total requests"), (long) batchStatusInfo.get("#Requests succeeded")
                        + (long) batchStatusInfo.get("#Requests failed") + (long) batchStatusInfo.get("#Requests timed-out"));
                assertEquals(batchStatusInfo.get("Estimated time to complete"), "0.0 sec");
                assertEquals(batchStatusInfo.get("#Requests remaining"), 0l);
                assertEquals(batchStatusInfo.get("#Requests completed"), batchStatusInfo.get("#Total requests"));
            }

            if (StringUtils.equals((String) batchStatusInfo.get("Batch status"), "IN_PROGRESS")) {
                assertEquals(batchStatusInfo.get("#Total requests"), (long) batchStatusInfo.get("#Requests succeeded")
                        + (long) batchStatusInfo.get("#Requests failed") + (long) batchStatusInfo.get("#Requests timed-out")
                        + (long) batchStatusInfo.get("#Requests remaining"));

            }
        }

    }

    private List<BatchTransaction> getBatchTransactions() {
        List<BatchTransaction> batchTransactions = new ArrayList<BatchTransaction>();

        long startTime = 1421144537024l;
        long endTime = 14211446252164l;
        BatchTransaction batchTxn_processed = new BatchTransaction();
        batchTxn_processed.setId("1");
        batchTxn_processed.setBatchInputFileName("UI-aqmk_12312321321321.xls");
        batchTxn_processed.setBatchOutputFileName("UI-aqmk_12312321321321_Response.xls");
        batchTxn_processed.setStartTime(startTime);
        batchTxn_processed.setEndTime(endTime);
        batchTxn_processed.setFailCount(51l);
        batchTxn_processed.setSuccessCount(165l);
        batchTxn_processed.setTotalRecords(216l);
        batchTxn_processed.setStatus("PROCESSED");
        batchTransactions.add(batchTxn_processed);

        BatchTransaction batchTxn_inprogress = new BatchTransaction();
        batchTxn_inprogress.setId("2");
        batchTxn_inprogress.setBatchInputFileName("UI-aqmk_3123213213.xls");
        batchTxn_inprogress.setBatchOutputFileName("UI-aqmk_3123213213_Response.xls");
        batchTxn_inprogress.setStartTime(startTime);
        batchTxn_inprogress.setEndTime(endTime);
        batchTxn_inprogress.setFailCount(2l);
        batchTxn_inprogress.setSuccessCount(3l);
        batchTxn_inprogress.setTotalRecords(5l);
        batchTxn_inprogress.setStatus("IN_PROGRESS");
        batchTransactions.add(batchTxn_inprogress);

        BatchTransaction batchTxn_error = new BatchTransaction();
        batchTxn_error.setId("3");
        batchTxn_error.setBatchInputFileName("UI-aqmk_31232132134.xls");
        batchTxn_error.setBatchOutputFileName("UI-aqmk_31232132134_Response.xls");
        batchTxn_error.setStartTime(startTime);
        batchTxn_error.setEndTime(endTime);
        batchTxn_error.setFailCount(51l);
        batchTxn_error.setSuccessCount(165l);
        batchTxn_error.setTotalRecords(216l);
        batchTxn_error.setStatus("ERROR");
        batchTransactions.add(batchTxn_error);

        BatchTransaction batchTxn_invalid = new BatchTransaction();
        batchTxn_invalid.setId("4");
        batchTxn_invalid.setBatchInputFileName("UI-aqmk_31232132135.xls");
        batchTxn_invalid.setBatchOutputFileName("UI-aqmk_31232132135_Response.xls");
        batchTxn_invalid.setStartTime(startTime);
        batchTxn_invalid.setEndTime(endTime);
        batchTxn_invalid.setFailCount(51l);
        batchTxn_invalid.setSuccessCount(165l);
        batchTxn_invalid.setTotalRecords(216l);
        batchTxn_invalid.setStatus("INVALID");
        batchTransactions.add(batchTxn_invalid);

        return batchTransactions;

    }

    private List<BatchRuntimeTransactionMapping> getBatchTransactionMappings() {

        List<BatchRuntimeTransactionMapping> batchTxnMappings = new ArrayList<BatchRuntimeTransactionMapping>();

        BatchRuntimeTransactionMapping batchTxnMapping_success = new BatchRuntimeTransactionMapping();
        batchTxnMapping_success.setId("1");
        batchTxnMapping_success.setBatchId("2");
        batchTxnMapping_success.setStatus(RuntimeConstants.SUCCESS);
        batchTxnMappings.add(batchTxnMapping_success);

        BatchRuntimeTransactionMapping batchTxnMapping_failure = new BatchRuntimeTransactionMapping();
        batchTxnMapping_failure.setId("2");
        batchTxnMapping_failure.setBatchId("2");
        batchTxnMapping_failure.setStatus(RuntimeConstants.FAILURE);
        batchTxnMappings.add(batchTxnMapping_failure);

        BatchRuntimeTransactionMapping batchTxnMapping_timeout = new BatchRuntimeTransactionMapping();
        batchTxnMapping_timeout.setId("3");
        batchTxnMapping_timeout.setBatchId("2");
        batchTxnMapping_timeout.setStatus(RuntimeConstants.TIMED_OUT);
        batchTxnMappings.add(batchTxnMapping_timeout);

        return batchTxnMappings;

    }
}
