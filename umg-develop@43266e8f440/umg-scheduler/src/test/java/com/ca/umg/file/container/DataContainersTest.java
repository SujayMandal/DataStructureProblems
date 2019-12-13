package com.ca.umg.file.container;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.file.event.info.FileStatusInfo;
import com.ca.umg.file.event.util.FileStatus;

@Ignore
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class DataContainersTest {

    private static String TNT_CODE = "TNT_CODE";

    private static final String MODEL_DETAIL = "model1-1";

    @Inject
    private DataContainers dataContainers;

    @Before
    public void setUp() throws Exception {
        Map<String, Map<String, List<FileStatusInfo>>> requestFilesMap = dataContainers.getRequestFilesMap();
        FileStatusInfo fileStatusInfo = getFileStatusInfo();
        List<FileStatusInfo> fileStatusInfos = new ArrayList<FileStatusInfo>();
        fileStatusInfos.add(fileStatusInfo);
        Map<String, List<FileStatusInfo>> modelDetailMap = new HashMap<String, List<FileStatusInfo>>();
        modelDetailMap.put(MODEL_DETAIL, fileStatusInfos);
        requestFilesMap.put(TNT_CODE, modelDetailMap);
    }

    private FileStatusInfo getFileStatusInfo() {
        FileStatusInfo fileStatusInfo = new FileStatusInfo();
        fileStatusInfo.setTenantCode(TNT_CODE);
        fileStatusInfo.setFilePath("/sanpath");
        fileStatusInfo.setName("file_name");
        fileStatusInfo.setStatus(FileStatus.ACK.getStatus());
        return fileStatusInfo;
    }

    @Test
    public void testGetRequestFilesMap() {
        assertNotNull(dataContainers.getRequestFilesMap());
    }

    @Test
    public void testGetRequestFilesMapClone() {
        assertNotNull(dataContainers.getRequestFilesMapClone());
    }

    @Test
    public void testUpdateRequestFilesMap() {
        dataContainers.updateRequestFilesMap(TNT_CODE, MODEL_DETAIL, FileStatus.ACK.getStatus(), getFileStatusInfo());
        List<FileStatusInfo> fileStatusInfos = dataContainers.getRequestFilesMapClone().get(TNT_CODE).get(MODEL_DETAIL);
        assertNotNull(fileStatusInfos);
        assertEquals(1, fileStatusInfos.size());
        assertEquals(FileStatus.ACK.getStatus(), fileStatusInfos.get(0).getStatus());

        dataContainers.updateRequestFilesMap(TNT_CODE, MODEL_DETAIL, FileStatus.POSTED.getStatus(), getFileStatusInfo());

        fileStatusInfos = dataContainers.getRequestFilesMapClone().get(TNT_CODE).get(MODEL_DETAIL);
        assertNotNull(fileStatusInfos);
        assertEquals(0, fileStatusInfos.size());

    }

}
