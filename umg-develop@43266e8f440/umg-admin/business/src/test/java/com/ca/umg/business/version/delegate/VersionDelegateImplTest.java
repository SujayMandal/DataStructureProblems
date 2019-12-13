package com.ca.umg.business.version.delegate;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.integration.info.RuntimeResponse;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.dao.MappingInputDAO;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.ModelDefinitionInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.version.VersionAbstractTest;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.entity.EmailApprovalEnum;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.MappingVersionInfo;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionStatus;

import junit.framework.Assert;
import ma.glasnost.orika.impl.ConfigurableMapper;

@RunWith(MockitoJUnitRunner.class)
public class VersionDelegateImplTest extends VersionAbstractTest {

    @InjectMocks
    private VersionDelegate versionDelegate = new VersionDelegateImpl();

    @Spy
    private ConfigurableMapper mapper = new UMGConfigurableMapper();

    @Mock
    private VersionBO versionBO;

    @Mock
    private MappingBO mappingBO;

    @Mock
    private RuntimeIntegrationClient runtimeIntegrationClient;
    
    @Mock
    private MappingDelegate mappingDelegate;
    
    @Mock
    private MappingInputDAO mappingInputDAO;

    private Mapping mapping;

    private ModelLibrary modelLibrary;
    
    private static final String MAPPING_TENANT_INPUT_DATA = "./src/test/resources/tid_mapping_input.txt";

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testGetTidMappingStatus() throws BusinessException, SystemException {
        when(versionBO.findByMappingName(Mockito.any(String.class))).thenReturn(buildVersions());
        KeyValuePair<Boolean, List<String>> listOfUmgVer = versionDelegate.getTidMappingStatus("Mapping0");
        assertTrue(listOfUmgVer.getKey());
        assertNotNull(listOfUmgVer);
        assertEquals(2, listOfUmgVer.getValue().size());
    }

    @Test
    public void testNullGetTidMappingStatus() throws BusinessException, SystemException {
        when(versionBO.findByMappingName(Mockito.any(String.class))).thenReturn(null);
        KeyValuePair<Boolean, List<String>> listOfUmgVer = versionDelegate.getTidMappingStatus("Mapping2");
        assertFalse(listOfUmgVer.getKey());
        assertNull(listOfUmgVer.getValue());
    }

    @Test
    public void testGetNotDeletedVersions() throws BusinessException, SystemException {
        when(versionBO.findNotDeletedVersions(Mockito.any(String.class))).thenReturn(buildDeleteVersions());
        List<String> listOfUmgVer = versionDelegate.getNotDeletedVersions("Mapping3");
        assertNotNull(listOfUmgVer);
        assertEquals(4, listOfUmgVer.size());
    }

    @Test
    public void testNullGetNotDeletedVersions() throws BusinessException, SystemException {
        when(versionBO.findNotDeletedVersions(Mockito.any(String.class))).thenReturn(null);
        List<String> listOfUmgVer = versionDelegate.getNotDeletedVersions("Mapping4");
        assertNull(listOfUmgVer);
    }

    @Test
    public void testPublishVersion() throws BusinessException, SystemException {
        String user = "SYSTEM";
        Version version = buildVersion("version1", "dsds", 1, 0, VersionStatus.SAVED.getVersionStatus(), null, null, "version 1");
        version.setId("1");

        when(versionBO.getVersionDetails("1")).thenReturn(version);
        version.setStatus(VersionStatus.PUBLISHED.getVersionStatus());
        when(versionBO.publishVersion(any(Version.class), anyString(), Matchers.anyInt())).thenReturn(version);
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
            when(versionBO.publishVersion(any(Version.class), anyString(), Matchers.anyInt())).thenReturn(version);
            when(runtimeIntegrationClient.unDeploy(any(VersionInfo.class), anyString(), anyString())).thenThrow(
                    new SystemException(BusinessExceptionCodes.BSE000070, new Object[] {}));
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000070, e.getCode());
        }
    }

    @Test
    public void testGetUmgVersionsOnModelLibraryId() throws BusinessException, SystemException {
        when(versionBO.getAllUmgVersionsOnModelLibraryId("libraryId")).thenReturn(asList("Version1-2.0"));
        List<String> versionList = versionDelegate.getUmgVersionsOnModelLibraryId("libraryId");
        Assert.assertEquals(1, versionList.size());
        Assert.assertEquals("Version1-2.0", versionList.get(0));
    }

    @Test
    public void testGetUmgVersionsOnModelLibraryIdForNull() throws BusinessException, SystemException {
        when(versionBO.getAllUmgVersionsOnModelLibraryId("libraryId")).thenReturn(null);
        List<String> versionList = versionDelegate.getUmgVersionsOnModelLibraryId("libraryId");
        Assert.assertNull(versionList);
    }

    @Test
    public void testGetUmgVersionsOnModelLibraryIdForException() throws BusinessException, SystemException {
        when(versionBO.getAllUmgVersionsOnModelLibraryId("libraryId")).thenReturn(asList("Version1-2.0"));
        List<String> versionList = versionDelegate.getUmgVersionsOnModelLibraryId("id");
        System.out.println(versionList.toString());
        // Assert.assertNull(versionList);
    }
    
    @Test
    public void testgetVersionStatus () throws BusinessException, SystemException {
        List<String> verList = new ArrayList();
        Version version21 = buildVersion("V21", "V21", 21, 0, VersionStatus.TESTED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v21");
        verList.add(version21.getName());
        when(versionBO.getVersionStatus("V21")).thenReturn(verList);
        Boolean result = versionDelegate.getVersionStatus("V21");
        Assert.assertTrue(result);
    }
    
    @Test
    public void testNullVersionStatus () throws BusinessException, SystemException {
        when(versionBO.getVersionStatus("V21")).thenReturn(null);
        Boolean result = versionDelegate.getVersionStatus("V21");
        Assert.assertFalse(result);
    }

    private List<Version> buildVersions() {
        List<Version> versions = new ArrayList<>();
        Version version1 = buildVersion("V0", "V0", 1, 0, VersionStatus.PUBLISHED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v0");
        Version version2 = buildVersion("V2", "V2", 2, 0, VersionStatus.DEACTIVATED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v2");
        versions.add(version1);
        versions.add(version2);
        return versions;
    }

    private List<String> buildDeleteVersions() {
        List<String> versions = new ArrayList<>();
        Version version1 = buildVersion("V5", "V5", 5, 0, VersionStatus.PUBLISHED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v5");
        Version version2 = buildVersion("V6", "V6", 6, 0, VersionStatus.DEACTIVATED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v6");
        Version version3 = buildVersion("V8", "V8", 8, 0, VersionStatus.SAVED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v8");
        Version version4 = buildVersion("V9", "V9", 9, 0, VersionStatus.TESTED.getVersionStatus(), mapping, modelLibrary,
                "this is version desc v9");
        versions.add(version1.getName() + BusinessConstants.CHAR_HYPHEN + version1.getMajorVersion() + BusinessConstants.DOT
                + version1.getMinorVersion());
        versions.add(version2.getName() + BusinessConstants.CHAR_HYPHEN + version2.getMajorVersion() + BusinessConstants.DOT
                + version2.getMinorVersion());
        versions.add(version3.getName() + BusinessConstants.CHAR_HYPHEN + version3.getMajorVersion() + BusinessConstants.DOT
                + version3.getMinorVersion());
        versions.add(version4.getName() + BusinessConstants.CHAR_HYPHEN + version4.getMajorVersion() + BusinessConstants.DOT
                + version4.getMinorVersion());
        return versions;
    }
    
    @Test
    public void testExportExcel() throws BusinessException, SystemException, IOException {
    	when(mappingDelegate.findByName("IMPORT_VERSION_MODEL-TID-2014-Nov-03-13-57")).thenReturn(buildMappingInfo());
    	when(mappingInputDAO.findByMapping(any(Mapping.class))).thenReturn(buildMappingInput());
    	Workbook wb = versionDelegate.exportExcel(buildMappingVersionInfo());
    	Assert.assertNotNull(wb);
    }
    
    private MappingVersionInfo buildMappingVersionInfo(){
    	MappingVersionInfo mvi = new MappingVersionInfo();
    	mvi.setTidName("IMPORT_VERSION_MODEL-TID-2014-Nov-03-13-57");
    	mvi.setVersionName("thyqwe");
    	mvi.setMajorVersion("1");
    	mvi.setMinorVersion("0");
    	return mvi;
    }
    
    private MappingInfo buildMappingInfo(){
    	MappingInfo mi = new MappingInfo();
    	mi.setName("IMPORT_VERSION_MODEL-TID-2014-Nov-03-13-57");
    	mi.setDescription("UMG_MIGRATION_VERSION_IMPORT_13");
    	mi.setMappingData("Mapping Data");
    	mi.setVersion(1);
    	mi.setActive(false);
    	mi.setModel(buildModelInfo());
    	mi.setStatus("FINALIZED");
    	return mi;
    }
    
    private ModelInfo buildModelInfo(){
    	ModelInfo mi = new ModelInfo();
    	mi.setName("IMPORT_VERSION_MODEL");
    	mi.setDescription("IMPORT_VERSION_MODEL");
    	mi.setUmgName("IMPORT_VERSION_MODEL-MID-2014-Nov-03-13-57");
    	mi.setIoDefinitionName("computeaqmknpv_mandatory-output_date_synd.xml");
    	mi.setDocumentationName("computeaqmknpv.zip");
    	mi.setAllowNull(true);
    	mi.setModelDefinition(buildModelDefinitionInfo());
    	return mi;
    }
    
    private ModelDefinitionInfo buildModelDefinitionInfo(){
    	ModelDefinitionInfo mdi = new ModelDefinitionInfo();
    	mdi.setType("text/xml");
    	return mdi;
    }
    
    private MappingInput buildMappingInput()  throws IOException{
    	MappingInput mi = new MappingInput();
    	mi.setMapping(buildMapping());
    	mi.setTenantInterfaceDefn(readFile(MAPPING_TENANT_INPUT_DATA));
    	return mi;
    }
    
    byte[] readFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.readAllBytes(path);
    }
    
    private Mapping buildMapping(){
    	Mapping mapping = new Mapping();
    	mapping.setName("IMPORT_VERSION_MODEL-TID-2014-Nov-03-13-57");
    	mapping.setDescription("UMG_MIGRATION_VERSION_IMPORT_13");
    	mapping.setStatus("FINALIZED");
    	return mapping;
    }

}
