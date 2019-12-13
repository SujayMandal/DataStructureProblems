package com.ca.umg.file.processor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.pool.model.PoolStatus;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.file.container.DataContainers;
import com.ca.umg.file.event.info.FileStatusInfo;
import com.ca.umg.file.event.util.FileStatus;
import com.ca.umg.file.rt.RuntimeClient;

@Ignore
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FileRequestProcessorTest {

    @Inject
    private FileRequestProcessor fileRequestProcessor;

    @Inject
    private DataContainers dataContainers;

    @Inject
    private RuntimeClient runtimeClient;

    private static final String TNT_CODE = "TNT_CODE";

    private static final String MDL_NAME = "MDL_NAME";

    private static final String MDL_VERSION = "MDL_VERSION";

    @Before
    public void setUp() throws Exception {
        Map<String, Map<String, List<FileStatusInfo>>> tenantRequestFilesMap = new HashMap<String, Map<String, List<FileStatusInfo>>>();
        Map<String, List<FileStatusInfo>> modelReqMap = new HashMap<String, List<FileStatusInfo>>();
        List<FileStatusInfo> fileStatusInfos = new LinkedList<FileStatusInfo>();
        FileStatusInfo fileStatusInfo = new FileStatusInfo();
        fileStatusInfo.setFilePath("dummyFilePath");
        fileStatusInfo.setName("dummyFilename");
        fileStatusInfo.setTenantCode(TNT_CODE);
        fileStatusInfo.setAckTime(DateTime.now().getMillis());
        fileStatusInfo.setStatus(FileStatus.ACK.getStatus());
        fileStatusInfos.add(fileStatusInfo);
        modelReqMap.put(MDL_NAME + FrameworkConstant.HYPHEN + MDL_VERSION, fileStatusInfos);
        tenantRequestFilesMap.put(TNT_CODE, modelReqMap);

        when(dataContainers.getRequestFilesMapClone()).thenReturn(tenantRequestFilesMap);
        doNothing().when(runtimeClient).executeRuntimeRequest(any(FileStatusInfo.class), any(TransactionCriteria.class),
                any(Map.class));

        doNothing().when(dataContainers).updateRequestFilesMap(eq(TNT_CODE), eq(MDL_NAME + MDL_VERSION), anyString(),
                any(FileStatusInfo.class));

        PoolStatus poolStatus = new PoolStatus();
        poolStatus.setAvailablemodelets(1);
        poolStatus.setPoolname("Pool1");
        when(runtimeClient.getProbablePoolAndCount(any(TransactionCriteria.class))).thenReturn(poolStatus);
    }

    @Test
    public void testProcessAllFiles() {

        fileRequestProcessor.processAllFiles();

        verify(dataContainers, times(1)).getRequestFilesMapClone();
        verify(runtimeClient, times(1)).getProbablePoolAndCount(any(TransactionCriteria.class));
        verify(dataContainers, times(1)).updateRequestFilesMap(eq(TNT_CODE),
                eq(MDL_NAME + FrameworkConstant.HYPHEN + MDL_VERSION), anyString(), any(FileStatusInfo.class));
    }

    @Test
    public void testProcessFileByModel() {

        fileRequestProcessor.processFileByModel(TNT_CODE, MDL_NAME, MDL_VERSION);
    }

}
