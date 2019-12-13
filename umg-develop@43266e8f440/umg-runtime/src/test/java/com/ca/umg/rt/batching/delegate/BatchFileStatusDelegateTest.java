package com.ca.umg.rt.batching.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
import com.ca.umg.rt.batching.bo.BatchFileStatusBOImpl;
import com.ca.umg.rt.batching.data.BatchFileStatusInput;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BatchFileStatusDelegateTest {

    @Inject
    private BatchFileStatusBOImpl batchFileStatusBOImpl;

    @Inject
    private BatchFileStatusDelegate batchFileStatusDelegate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetBatchFileStatus() throws BusinessException {
        when(batchFileStatusBOImpl.getBatcheStatusByFileNameAndStartTime(Mockito.anyString(), Mockito.anyLong())).thenReturn(
                getBatchStatusInfo());
        List<Map<String, Object>> batchStatusInfos = batchFileStatusDelegate.getBatchFileStatus(getBatchStatusInput());
        assertNotNull(batchStatusInfos);
        assertEquals(10, batchStatusInfos.size());
    }

    private List<Map<String,Object>> getBatchStatusInfo() {
        List<Map<String,Object>> batchStatusInfos = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 10; i++) {
            Map<String,Object> batchStatusInfo = new HashMap<String,Object>();
            batchStatusInfo.put("Batch status", "INPROGRESS");
            batchStatusInfos.add(batchStatusInfo);
        }
        return batchStatusInfos;

    }

    private BatchFileStatusInput getBatchStatusInput() {
        BatchFileStatusInput batchFileStatusInput = new BatchFileStatusInput();
        batchFileStatusInput.setFileName("aqmk.txt");
        batchFileStatusInput.setDate("20-Jun-2010");
        return batchFileStatusInput;

    }

    private BatchFileStatusInput getBatchStatusInputWithWrongDate() {
        BatchFileStatusInput batchFileStatusInput = new BatchFileStatusInput();
        batchFileStatusInput.setFileName("aqmk.txt");
        batchFileStatusInput.setDate("20-10-2010");
        return batchFileStatusInput;

    }
}
