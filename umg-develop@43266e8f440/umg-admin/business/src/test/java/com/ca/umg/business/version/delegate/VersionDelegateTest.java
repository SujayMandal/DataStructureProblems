package com.ca.umg.business.version.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.integration.info.RuntimeResponse;
import com.ca.umg.business.integration.info.TestStatusInfo;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.TestBedOutputInfo;
import com.ca.umg.business.model.bo.ModelBO;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.validation.CreateVersionValidator;
import com.ca.umg.business.validation.UpdateVersionValidator;
import com.ca.umg.business.version.VersionAbstractTest;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.dao.VersionDAO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionHierarchyInfo;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionStatus;
import com.ca.umg.business.version.info.VersionSummaryInfo;

public class VersionDelegateTest extends VersionAbstractTest {

    @InjectMocks
    private VersionDelegate classUnderTest = new VersionDelegateImpl();

    @Mock
    private VersionBO versionBO;

    @Mock
    private VersionDAO mockVersionDAO;

    @Mock
    private ModelBO modelBO;

    @Spy
    private ConfigurableMapper mapper = new UMGConfigurableMapper();

    @InjectMocks
    private VersionDelegate versionDelegate = new VersionDelegateImpl();

    @Mock
    private ModelDelegate modelDelegate;

    @Spy
    @InjectMocks
    private VersionDelegateHelper versionDelegateHelper = new VersionDelegateHelper();

    @Mock
    private RuntimeIntegrationClient runtimeIntegrationClient;

    @Mock
    private UpdateVersionValidator updateVersionValidator;

    private Mapping mapping;

    private ModelLibrary modelLibrary;

    @Mock
    private MappingDelegate mappingDelegate;
    
    @Mock
    private CreateVersionValidator createVersionValidator;
    
    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void getAllVersionsTest() throws BusinessException, SystemException {
        List<Version> versions = buildVersions();
        when(versionBO.getAllVersions()).thenReturn(versions);
        List<VersionHierarchyInfo> hierarchyInfos = classUnderTest.getAllVersions();
        assertThat(1, Is.is(hierarchyInfos.size()));
    }

    @Test
    public void testGetUmgVersionsOnModelLibraryId() throws BusinessException, SystemException {
        List<String> versionStringList = new ArrayList<String>();
        versionStringList.add("Version1");
        versionStringList.add("Version2");
        versionStringList.add("Version3");
        when(versionBO.getAllUmgVersionsOnModelLibraryId(Mockito.anyString())).thenReturn(versionStringList);
        List<String> resultVersionList = classUnderTest.getUmgVersionsOnModelLibraryId("ModLibId1");
        Assert.assertEquals(3, resultVersionList.size());
    }

    @Test
    public void testGetUmgVersionsOnModelLibraryIdForNull() throws BusinessException, SystemException {
        when(versionBO.getAllUmgVersionsOnModelLibraryId(Mockito.anyString())).thenReturn(null);
        List<String> resultVersionList = classUnderTest.getUmgVersionsOnModelLibraryId("ModLibId1");
        Assert.assertNull(resultVersionList);
    }

    private Version createVersion(int i) {
        Version version = new Version();
        ModelLibrary modelLibrary = new ModelLibrary();
        modelLibrary.setName("LIB_" + i / 5);
        Model model = new Model();
        model.setName("MODEL_" + i / 3);
        Mapping mapping = new Mapping();
        mapping.setModel(model);
        version.setMapping(mapping);
        version.setModelLibrary(modelLibrary);
        version.setMajorVersion(i % 3);
        version.setMinorVersion(0);
        version.setCreatedDate(new DateTime());
        version.setLastModifiedDate(new DateTime());
        return version;
    }

    private VersionInfo getVersionInfo() {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setDeactivatedBy(null);
        versionInfo.setDeactivatedOn(null);
        versionInfo.setDescending(true);
        versionInfo.setDescription("versioninfo1");
        versionInfo.setMajorVersion(1);
        versionInfo.setMapping(null);
        versionInfo.setMinorVersion(0);
        versionInfo.setModelLibrary(null);
        versionInfo.setName("version1");
        versionInfo.setPage(1);
        versionInfo.setPageSize(100);
        versionInfo.setPublishedBy(null);
        versionInfo.setPublishedOn(null);
        versionInfo.setSearchString("any");
        versionInfo.setSortColumn("column1");
        versionInfo.setStatus("active");
        versionInfo.setVersionDescription("versioninfo1 description");
        return versionInfo;
    }

    private List<Version> buildVersions() {
        Model model = new Model();
        model.setDescription("model description");
        model.setDocumentationName("doc1");
        modelLibrary = super.buildModelLibrary("modelLib1", "Model Library", "UMG_NAME", "JARNAME", "EN", "JARTYPE",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        mapping = super.buildMapping("mapping1", model, "tenant1", "tenant description", "mappigIO");
        List<Version> versions = new ArrayList<>();
        Version version1 = buildVersion("V0", "V0", 1, 0, VersionStatus.PUBLISHED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v0");
        Version version2 = buildVersion("V2", "V2", 2, 0, VersionStatus.DEACTIVATED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v2");
        versions.add(version1);
        versions.add(version2);
        return versions;
    }

    @Test
    public void getAllLibrariesTest() {
        List<String> content = new ArrayList<>();
        content.add("page1");
        content.add("page2");
        content.add("page3");
        Page<String> pageContent = new PageImpl<>(content);
        VersionInfo versionInfo = this.getVersionInfo();
        PageRecord<String> pageRecord = null;
        try {
            when(versionBO.getAllLibraries(versionInfo)).thenReturn(pageContent);
            pageRecord = classUnderTest.getAllLibraries(versionInfo);
            Assert.assertNotNull(pageRecord);
            for (String string : pageContent) {
                Assert.assertNotNull(string);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void getAllModelsForLibraryTest() {
        List<String> content = new ArrayList<>();
        content.add("page1");
        content.add("page2");
        content.add("page3");
        Page<String> pageContent = new PageImpl<>(content);
        VersionInfo versionInfo = this.getVersionInfo();
        PageRecord<String> pageRecord = null;
        try {
            when(versionBO.getAllModelsForLibrary("", versionInfo)).thenReturn(pageContent);
            pageRecord = classUnderTest.getAllModelsForLibrary("", versionInfo);
            Assert.assertNotNull(pageRecord);
            for (String string : pageContent) {
                Assert.assertNotNull(string);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void getAllVersionsWithArugmentsTest() {
        VersionInfo versionInfo = this.getVersionInfo();
        List<Version> contentVersion = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            contentVersion.add(this.createVersion(i));
        }
        Page<Version> page = new PageImpl<>(contentVersion);

        try {
            when(versionBO.getAllVersions("lib1", "model1", versionInfo)).thenReturn(page);

            PageRecord<VersionInfo> pageRecordVersionInfo = classUnderTest.getAllVersions("lib1", "model1", versionInfo);
            Assert.assertNotNull(pageRecordVersionInfo);

            List<VersionInfo> listVersionInfo = pageRecordVersionInfo.getContent();
            Assert.assertNotNull(listVersionInfo);
            for (VersionInfo versionInfo2 : listVersionInfo) {
                Assert.assertNotNull(versionInfo2);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }

    }

    @Test
    public void testDeactivateVersionFailure() throws BusinessException, SystemException {
        String user = "SYSTEM";
        VersionInfo versionInfo = null;
        Version version = buildVersion("version1", "dsds", 1, 0, VersionStatus.PUBLISHED.getVersionStatus(), null, null,
                "version 1");
        version.setId("1");
        try {
            when(versionBO.getVersionDetails("1")).thenReturn(null);
            versionInfo = versionDelegate.deactivateVersion("1", user, "http://localhost:8080/", null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000068, e.getCode());
        }

        try {
            when(versionBO.getVersionDetails("1")).thenReturn(version);
            version.setStatus(VersionStatus.DEACTIVATED.getVersionStatus());
            when(versionBO.publishVersion(any(Version.class), anyString(),  Matchers.anyInt())).thenReturn(version);
            when(runtimeIntegrationClient.unDeploy(any(VersionInfo.class), anyString(), anyString())).thenThrow(
                    new SystemException(BusinessExceptionCodes.BSE000070, new Object[] {}));
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000070, e.getCode());
        }
    }

    @Test
    public void testPublishVersion() throws BusinessException, SystemException {
        String user = "SYSTEM";
        Version version = buildVersion("version1", "dsds", 1, 0, VersionStatus.SAVED.getVersionStatus(), null, null, "version 1");
        version.setId("1");

        when(versionBO.getVersionDetails("1")).thenReturn(version);
        version.setStatus(VersionStatus.PUBLISHED.getVersionStatus());
        when(versionBO.publishVersion(any(Version.class), anyString(),  Matchers.anyInt())).thenReturn(version);
        when(runtimeIntegrationClient.deploy(any(VersionInfo.class), anyString(), anyString())).thenReturn(new RuntimeResponse());

        VersionInfo versionInfo = versionDelegate.publishVersion("1", user, "http://localhost:8080/", null, 0);
        assertNotNull(versionInfo);
        assertEquals(VersionStatus.PUBLISHED.getVersionStatus(), versionInfo.getStatus());
        verify(versionBO, times(1)).getVersionDetails("1");
        verify(versionBO, times(1)).publishVersion(version, user, 0);
        verify(runtimeIntegrationClient, times(1)).deploy(versionInfo, "http://localhost:8080/", null);
    }

    @Test
    public void testPublishVersionFailure() {
        String user = "SYSTEM";
        VersionInfo versionInfo = null;
        Version version = buildVersion("version1", "dsds", 1, 0, VersionStatus.SAVED.getVersionStatus(), null, null, "version 1");
        version.setId("1");
        try {
            when(versionBO.getVersionDetails("1")).thenReturn(null);
            versionInfo = versionDelegate.publishVersion("1", user, "http://localhost:8080/", null, 0);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000068, e.getCode());
        }

        try {
            when(versionBO.getVersionDetails("1")).thenReturn(version);
            version.setStatus(VersionStatus.PUBLISHED.getVersionStatus());
            when(versionBO.publishVersion(any(Version.class), anyString(), Matchers.anyInt())).thenReturn(version);
            when(runtimeIntegrationClient.deploy(any(VersionInfo.class), anyString(), anyString())).thenThrow(
                    new SystemException(BusinessExceptionCodes.BSE000070, new Object[] {}));
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000070, e.getCode());
        }
    }

    @Test
    public void getModelNamesForLibraryNameAndCharsInNameOrDescriptionTest() {
        List<String> value = new ArrayList<>();
        value.add("lib1");
        value.add("lib2");
        value.add("lib3");
        value.add("lib4");
        try {
            when(versionBO.getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "ser1", false)).thenReturn(value);

            List<String> resultList = classUnderTest
                    .getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "ser1", false);
            Assert.assertNotNull(resultList);
            Assert.assertTrue(resultList.size() > 0);

            resultList = classUnderTest.getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "ser1", true);
            Assert.assertNotNull(resultList);
            Assert.assertTrue(resultList.size() == 0);

        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void getAllLibraryNamesTest() {
        List<String> value = new ArrayList<>();
        value.add("lib1");
        value.add("lib2");
        value.add("lib3");
        value.add("lib4");
        try {
            when(modelDelegate.getAllLibraryNames()).thenReturn(value);

            List<String> resultString = classUnderTest.getAllLibraryNames();
            Assert.assertNotNull(resultString);
            for (String string : resultString) {
                Assert.assertNotNull(string);
                Assert.assertTrue(string.contains("lib"));
            }

        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void listAllLibraryRecNameDescsTest() {
        List<ModelLibrary> listModelLibrary = new ArrayList<>();
        ModelLibrary modelLibrary = new ModelLibrary();
        modelLibrary.setDescription("modelLibrary1");
        modelLibrary.setExecutionLanguage("EN");
        modelLibrary.setJarName("myjar");
        modelLibrary.setName("TestLibrary");
        modelLibrary.setTenantId("Ocwin");
        modelLibrary.setUmgName("OCWIN_UMG");
        listModelLibrary.add(modelLibrary);
        try {
            when(modelDelegate.findMappingInfoByLibraryNamName("lib1")).thenReturn(listModelLibrary);

            List<MappingInfo> listmappingInfo = classUnderTest.listAllLibraryRecNameDescs("lib1");
            Assert.assertNotNull(listmappingInfo);
            for (MappingInfo mappingInfo : listmappingInfo) {
                Assert.assertNotNull(mappingInfo);
                Assert.assertTrue(mappingInfo.getUmgName().equals("OCWIN_UMG"));
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void versionTestTest(){
        TestStatusInfo testStatusInfo = new TestStatusInfo();
        try {
            when(runtimeIntegrationClient.versionTest("{}", "v1", null)).thenReturn(testStatusInfo);
            TestBedOutputInfo testBedOutputInfo = classUnderTest.versionTest("{}", "http://localhost:8090/testVersion",null, "v1");
            Assert.assertNotNull(testBedOutputInfo);
            Assert.assertNull(testBedOutputInfo.getOutputJson());
            Assert.assertNull(testBedOutputInfo.getOutputData());
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        
        try {
            when(runtimeIntegrationClient.versionTest("", "", null)).thenReturn(testStatusInfo);
            TestBedOutputInfo testBedOutputInfo = classUnderTest.versionTest("", "", null, "");
            Assert.assertNotNull(testBedOutputInfo);
        } catch (BusinessException | SystemException e) {
            assertEquals(FrameworkExceptionCodes.BSE000009, e.getCode());
        }
        
        Map<String,Object> response =  new HashMap<>();
        response.put("error", "errorValue");
        testStatusInfo = new TestStatusInfo();
        testStatusInfo.setResponse(response);
        try {
            when(runtimeIntegrationClient.versionTest("{}", "http://davxotdrnq02:6161", null)).thenReturn(testStatusInfo);
            TestBedOutputInfo testBedOutputInfo = classUnderTest.versionTest("{}", "http://davxotdrnq02:6161", null, "v1");
            Assert.assertNotNull(testBedOutputInfo);
            Assert.assertNotNull(testBedOutputInfo.getOutputJson());
            Assert.assertNull(testBedOutputInfo.getOutputData());
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        
        testStatusInfo = new TestStatusInfo();
        testStatusInfo.setErrorMessage("testError");
        testStatusInfo.setErrorCode("error1");
        try {
            when(runtimeIntegrationClient.versionTest("{}", "http://davxotdrnq02:6161", null)).thenReturn(testStatusInfo);
            TestBedOutputInfo testBedOutputInfo = classUnderTest.versionTest("{}", "http://davxotdrnq02:6161", null, "v1");
            Assert.assertNotNull(testBedOutputInfo);
            Assert.assertNotNull(testBedOutputInfo.getOutputJson());
            Assert.assertNull(testBedOutputInfo.getOutputData());
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void markAsTestedTest(){
        Version version = this.createVersion(1);
        try {
            when(versionBO.getVersionDetails("MODEL_1")).thenReturn(version);
            when(versionBO.markVersionAsTested(version)).thenReturn(version);
            Version resultVersion = classUnderTest.markAsTested("MODEL_1");
            Assert.assertNotNull(resultVersion);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        
    }

    @Test
    public void getAllLibraryRecordsTest(){
        List<String> stringList = new ArrayList<>();
        try {
            when(modelDelegate.getListOfDerivedModelLibraryNames("lib1")).thenReturn(stringList);
            List<String> resultList = classUnderTest.getAllLibraryRecords("lib1");
            Assert.assertNotNull(resultList);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void getVersionSummaryTest(){
        String tenantModelName = "tenant1";
        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(2);
        try {
            when(versionBO.getTenantModeldescription(tenantModelName)).thenReturn("HelloTest");
            when(versionBO.getAllMajorVersions(tenantModelName)).thenReturn(integerList);
            VersionSummaryInfo resultSummaryInfo = classUnderTest.getVersionSummary(tenantModelName);
            Assert.assertNotNull(resultSummaryInfo);
            Assert.assertEquals(resultSummaryInfo.getDescription(), "HelloTest");
            Assert.assertTrue(CollectionUtils.isEqualCollection(integerList, resultSummaryInfo.getMajorVersions()));
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void getAllTenantModelNamesTest(){
        List<String> tenantModelNames = new ArrayList<String>();
        tenantModelNames.add("tenantModelName1");
        tenantModelNames.add("tenantModelName2");
        tenantModelNames.add("tenantModelName3");
        
        try {
            when(versionBO.getAllTenantModelNames()).thenReturn(tenantModelNames);
            List<String> resultTenantModelNames = classUnderTest.getAllTenantModelNames();
            Assert.assertNotNull(resultTenantModelNames);
            Assert.assertTrue(CollectionUtils.isEqualCollection(resultTenantModelNames, tenantModelNames));
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void getAllModelNamesTest(){
        List<String> modelNames = new ArrayList<>();
        modelNames.add("model1");
        modelNames.add("model2");
        modelNames.add("model3");
        modelNames.add("model4");
        modelNames.add("model5");
        try {
            when(modelDelegate.getAllModelNames()).thenReturn(modelNames);
            
            List<String> resultModelNames = classUnderTest.getAllModelNames();
            Assert.assertNotNull(resultModelNames);
            Assert.assertTrue(CollectionUtils.isEqualCollection(resultModelNames, modelNames));
        } catch (SystemException | BusinessException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void getAllTidVersionNamesTest(){
        List<String> modelNames = new ArrayList<>();
        modelNames.add("model1");
        modelNames.add("model2");
        modelNames.add("model3");
        modelNames.add("model4");
        modelNames.add("model5");
        
        try {
            when(mappingDelegate.getListOfMappingNames("model1")).thenReturn(modelNames);
            
            List<String> resultModelNames = classUnderTest.getAllTidVersionNames("model1");
            Assert.assertNotNull(resultModelNames);
            Assert.assertTrue(CollectionUtils.isEqualCollection(resultModelNames, modelNames));
            assertThat(5, Is.is(resultModelNames.size()));
            
            //negative test case
            resultModelNames = classUnderTest.getAllTidVersionNames("");
            Assert.assertNotNull(resultModelNames);
            assertThat(0, Is.is(resultModelNames.size()));
        } catch (SystemException | BusinessException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void getTidMappingsTest(){
        List<MappingInfo> mappingInfos = new ArrayList<>();
        MappingInfo mappingInfo = new MappingInfo();
        mappingInfo.setDescription("mapping description");
        mappingInfo.setActive(true);
        mappingInfo.setMappingData("map1");
        mappingInfo.setModel(null);
        mappingInfo.setModelName("modelName1");
        mappingInfo.setName("mappingInfo");
        mappingInfo.setUmgName("umg_ver1");
        mappingInfo.setVersion(1);
        mappingInfos.add(mappingInfo);
        
        try {
            when(mappingDelegate.findByModelName("map1")).thenReturn(mappingInfos);
            
            List<MappingInfo> resultMappingInfos = classUnderTest.getTidMappings("map1");
            Assert.assertNotNull(resultMappingInfos);
            Assert.assertTrue(CollectionUtils.isEqualCollection(mappingInfos, resultMappingInfos));
        } catch (SystemException | BusinessException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void testGetVersionMetrics() throws BusinessException, SystemException {
    	Map dummyMetricMap = new HashMap();
    	dummyMetricMap.put("a", 1);
    	dummyMetricMap.put("b", 2);
    	VersionMetricRequestInfo req = new VersionMetricRequestInfo();
    	req.setVersionName("TestVersion");
    	req.setMajorVersion(1);
    	req.setMinorVersion(0);
    	when(versionBO.getVersionMetrics(req)).thenReturn(dummyMetricMap);
    	Map respMap = classUnderTest.getVersionMetrics(req);
    	assertTrue(respMap.equals(dummyMetricMap));
    }
    
    @Test (expected=BusinessException.class)
    public void testGetVersionMetricsForException() throws BusinessException, SystemException {
    	VersionMetricRequestInfo req = new VersionMetricRequestInfo();
    	Map respMap = classUnderTest.getVersionMetrics(req);
    }
}