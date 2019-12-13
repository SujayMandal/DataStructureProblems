package com.ca.umg.business.version.bo;

import static com.ca.umg.business.version.entity.EmailApprovalEnum.PORTAL_APPROVAL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.dao.MappingDAO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.audit.VersionAuditFactory;
import com.ca.umg.business.version.dao.VersionDAO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionStatus;

import junit.framework.Assert;

public class VersionBOTest {

    @InjectMocks
    private VersionBO classUnderTest = new VersionBOImpl();

    @Mock
    private VersionDAO versionDAOMock;

    @Mock
    private MappingDAO mappingDAOMock;
    
    @Mock
    private VersionAuditFactory VersionAuditFactory;
    
    Mapping mapping = null;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testgetAllUmgVersionsOnModelLibraryId() throws BusinessException, SystemException {
        List<Version> versionsList = createDummyVersions(3);
        when(versionDAOMock.findVersionsOnModelLibAndStatus("modelLibraryId1", VersionStatus.DELETED.name())).thenReturn(
                versionsList);
        List<String> resultVersionList = classUnderTest.getAllUmgVersionsOnModelLibraryId("modelLibraryId1");
        Assert.assertEquals(3, resultVersionList.size());
    }

    @Test
    public void testgetAllUmgVersionsOnModelLibraryIdForNull() throws BusinessException, SystemException {
        when(versionDAOMock.findVersionsOnModelLibAndStatus("modelLibraryId1", VersionStatus.DELETED.name())).thenReturn(null);
        List<String> resultVersionList = classUnderTest.getAllUmgVersionsOnModelLibraryId("modelLibraryId1");
        Assert.assertNull(resultVersionList);
    }

    @Test
    public void testgetAllUmgVersionsOnModelLibraryIdForEmpty() throws BusinessException, SystemException {
        when(versionDAOMock.findVersionsOnModelLibAndStatus("modelLibraryId1", VersionStatus.DELETED.name())).thenReturn(
                new ArrayList<Version>());
        List<String> resultVersionList = classUnderTest.getAllUmgVersionsOnModelLibraryId("modelLibraryId1");
        Assert.assertNull(resultVersionList);
    }

    @Test
    public void testFindByMappingName() throws BusinessException, SystemException {
        List<Version> versionsList = createDummyVersions(4);
        when(versionDAOMock.findByMappingName("tidName", VersionStatus.PUBLISHED.name(), VersionStatus.DEACTIVATED.name()))
                .thenReturn(versionsList);
        List<Version> versionList = classUnderTest.findByMappingName("tidName");
        Assert.assertEquals(4, versionList.size());
    }

    @Test
    public void testFindByMappingNameForNull() throws BusinessException, SystemException {
        when(versionDAOMock.findByMappingName("tidName", VersionStatus.PUBLISHED.name(), VersionStatus.DEACTIVATED.name()))
                .thenReturn(null);
        List<Version> versionList = classUnderTest.findByMappingName("tidName");
        Assert.assertNull(versionList);
    }

    @Test
    public void testFindNotDeletedVersions() throws BusinessException, SystemException {
        List<Version> versionsList = createDummyVersions(4);
        when(versionDAOMock.findNotDeletedVersions("tidName", VersionStatus.DELETED.name())).thenReturn(versionsList);
        List<String> versionList = classUnderTest.findNotDeletedVersions("tidName");
        Assert.assertEquals(4, versionList.size());
    }

    @Test
    public void testFindNotDeletedVersionsForNull() throws BusinessException, SystemException {
        when(versionDAOMock.findNotDeletedVersions("tidName", VersionStatus.DELETED.name())).thenReturn(null);
        List<String> versionList = classUnderTest.findNotDeletedVersions("tidName");
        Assert.assertNull(versionList);
    }

    private List<Version> createDummyVersions(int count) {
        List<Version> versionsList = new ArrayList<Version>();
        for (int i = 1; i <= count; i++) {
            Version version = new Version();
            version.setName("VersionName" + i);
            version.setMajorVersion(i);
            version.setMinorVersion(i + i);
            versionsList.add(version);
        }
        return versionsList;
    }

    private Version createVersion(String id, String name, int major, int minor, String status, String versionDesc) {
        Version version = new Version();
        version.setId(id);
        version.setName(name);
        version.setMajorVersion(major);
        version.setMinorVersion(minor);
        version.setStatus(status);
        version.setVersionDescription(versionDesc);
        return version;
    }
    
    private Version createVersion(String id, String name, int major, int minor, String status, Mapping testMapping) {
        Version version = new Version();
        version.setId(id);
        version.setName(name);
        version.setMajorVersion(major);
        version.setMinorVersion(minor);
        version.setStatus(status);
        version.setMapping(testMapping);
        return version;
    }
    
    private Mapping createMapping (String tidTestedVersionName) {
        mapping = new Mapping();
        mapping.setName(tidTestedVersionName);
    
        Model model = new Model();
        model.setName("Model1x");
        model.setDescription("model 1x");
        model.setDocumentationName("DOC1x");
        model.setIoDefinitionName("iio file1");
        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setModel(model);
        modelDefinition.setType("text/xml");
        modelDefinition.setIoDefinition("sample".getBytes());
        model.setModelDefinition(modelDefinition);
        model.setUmgName("umg-model-1");
    
        mapping.setModel(model);
        mapping.setModelIO("Sample MID Json".getBytes());
        return mapping;
    }

    @Test
    public void testMarkVersionAsPublished() throws BusinessException, SystemException {
        Version version = createVersion("1", "Version1", 1, 0, VersionStatus.TESTED.getVersionStatus(), "");
        when(versionDAOMock.save(any(Version.class))).thenReturn(version);
        String user = "SYSTEM";
        version = classUnderTest.publishVersion(version, user, PORTAL_APPROVAL.getValue());
        assertEquals(VersionStatus.PUBLISHED.getVersionStatus(), version.getStatus());
        verify(versionDAOMock, times(1)).save(version);
    }

    @Test
    public void testMarkVersionAsPublishedFailure() {
        Version version = createVersion("1", "Version1", 1, 0, VersionStatus.SAVED.getVersionStatus(), "");
        when(versionDAOMock.save(any(Version.class))).thenReturn(version);
        String user = "SYSTEM";
        try {
            classUnderTest.publishVersion(version, user, PORTAL_APPROVAL.getValue());
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000065, e.getCode());
        }
    }

    @Test
    public void testMarkVersionAsDeactivated() throws BusinessException, SystemException {
        Version version = createVersion("1", "Version1", 1, 0, VersionStatus.PUBLISHED.getVersionStatus(), "");
        when(versionDAOMock.save(any(Version.class))).thenReturn(version);
        String user = "SYSTEM";
        version = classUnderTest.markVersionAsDeactivated(version, user);
        assertEquals(VersionStatus.DEACTIVATED.getVersionStatus(), version.getStatus());
        verify(versionDAOMock, times(1)).save(version);
    }

    @Test
    public void testMarkVersionAsDeactivatedFailure() throws BusinessException, SystemException {
        Version version = createVersion("1", "Version1", 1, 0, VersionStatus.SAVED.getVersionStatus(), "");
        String user = "SYSTEM";
        try {
            classUnderTest.markVersionAsDeactivated(version, user);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000066, e.getCode());
        }
    }
    
    @Test
    public void testMarkVersionAsSaved() throws BusinessException, SystemException {
        Version version = createVersion("2", "Version2", 2, 0, VersionStatus.TESTED.getVersionStatus(), "");
        when(versionDAOMock.save(any(Version.class))).thenReturn(version);
        when(versionDAOMock.findOne(any(String.class))).thenReturn(version);
        String id = "2";
        classUnderTest.markVersionAsSaved(id);
        assertEquals(VersionStatus.SAVED.getVersionStatus(), version.getStatus());
        verify(versionDAOMock, times(1)).save(version);
    }
    
    @Test
    public void testMarkVersionAsSavedFailure() throws BusinessException, SystemException {
        Version version = createVersion("3", "Version3", 3, 0, VersionStatus.SAVED.getVersionStatus(), "");
        when(versionDAOMock.save(any(Version.class))).thenReturn(version);
        when(versionDAOMock.findOne(any(String.class))).thenReturn(version);
        String id = "2";
        try {
            classUnderTest.markVersionAsSaved(id);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000066, e.getCode());
        }
    }
    
    @Test
    public void testGetTestedVersions() throws BusinessException, SystemException {
        String tidName = "tidTestedVersionName";
//        Mapping testMapping = createMapping(tidName);
        Version version = createVersion("4", "Version4", 4, 0, VersionStatus.SAVED.getVersionStatus(), "");
        List<String> verList = new ArrayList<>();
        verList.add(version.getId());
        when(versionDAOMock.getTestedVersions(any(String.class),any(String.class))).thenReturn(verList);
        List<String> result = classUnderTest.getTestedVersions(tidName);
        assertEquals(1, result.size());
        assertEquals("4", result.get(0));
        verify(versionDAOMock, times(1)).getTestedVersions(tidName,VersionStatus.TESTED.getVersionStatus());
    }
    
    @Test
    public void testGetTestedVersionsNull() throws BusinessException, SystemException {
        String tidName = "tidTestedVersionName";
        Mapping testMapping = createMapping(tidName);
        Version version = createVersion("5", "Version5", 5, 0, VersionStatus.SAVED.getVersionStatus(),testMapping);
        List<String> verList = new ArrayList<>();
        verList.add(version.getId());
        when(versionDAOMock.getTestedVersions(any(String.class),any(String.class))).thenReturn(null);
        List<String> result = classUnderTest.getTestedVersions(tidName);
        Assert.assertNull(result);
        verify(versionDAOMock, times(1)).getTestedVersions(tidName,VersionStatus.TESTED.getVersionStatus());
    }
    
    @Test
    public void testVersionUpdate() throws BusinessException, SystemException {
        Version version = createVersion("1", "Version1", 1, 0, VersionStatus.TESTED.getVersionStatus(), "Create Desc");
        when(versionDAOMock.save(any(Version.class))).thenReturn(version);
        Version createdVersion = classUnderTest.create(version);
        assertEquals("Create Desc", createdVersion.getVersionDescription());
        verify(versionDAOMock, times(1)).save(version);
        when(versionDAOMock.findOne(any(String.class))).thenReturn(version);
        when(versionDAOMock.save(any(Version.class))).thenReturn(version);
        version.setVersionDescription("Updated Desc");
        classUnderTest.update(version);
        assertEquals("Updated Desc", versionDAOMock.findOne(version.getId()).getVersionDescription());
        verify(versionDAOMock, times(2)).findOne(version.getId());
        verify(versionDAOMock, times(2)).save(version);
    }

    private void setVersionList(int n) {
        Order modelLibraryOrder = new Order(Sort.Direction.ASC, "modelLibrary.name");
        Order modelOrder = new Order(Sort.Direction.ASC, "mapping.model.name");
        Order majorVerOrder = new Order(Sort.Direction.DESC, "majorVersion");
        Order minorVerOrder = new Order(Sort.Direction.DESC, "minorVersion");
        Sort sort = new Sort(modelLibraryOrder, modelOrder, majorVerOrder, minorVerOrder);
        
        List<Version> versionList = new ArrayList<>();
        Version version = null;
        for (int i=0;i< n;i++) {
            version = this.createVersion(i);
            versionList.add(version);
        }
        when(versionDAOMock.findAll(sort)).thenReturn(versionList);
    }

    private Version createVersion(int i) {
        Mapping mapping = new Mapping();
        ModelLibrary modelLibrary = new ModelLibrary();
        Version version = new Version();
        version.setId("05a68810-e6f9-11e3-a68a-82687f4fc15"+(i%10));
        version.setDeactivatedBy(null);
        version.setDeactivatedOn(null);
        version.setDescription("Version "+i+" Descripion");
        version.setMajorVersion(i);
        version.setMapping(mapping);
        version.setMinorVersion(0);
        version.setModelLibrary(modelLibrary);
        version.setName("version"+i);
        version.setPublishedBy(null);
        version.setPublishedOn(null);
        version.setStatus(i%2==0?"active":"inactive");
        version.setTenantId("tenant1");
        version.setVersionDescription("Version "+i+" Descripion");
        return version;
    }
    
    private Pageable getPagingInformation(PagingInfo pagingInfo, String sortColumn) {
        Direction direction = pagingInfo.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC;
        String newSortColumn = sortColumn;
        Order[] sortOrders = null;
        Order order = null;
        Order majorVerOrder = null;
        Order minorVerOrder = null;
        if (StringUtils.isBlank(newSortColumn)) {
            majorVerOrder = new Order(Sort.Direction.DESC, "majorVersion");
            minorVerOrder = new Order(Sort.Direction.DESC, "minorVersion");
            order = new Order(direction, "name").ignoreCase();
            sortOrders = new Order[] { order, majorVerOrder, minorVerOrder };
        } else {
            order = new Order(direction, newSortColumn).ignoreCase();
            sortOrders = new Order[] { order };
        }

        Sort sort = new Sort(sortOrders);
        return new PageRequest(pagingInfo.getPage() == 0 ? 0 : pagingInfo.getPage() - 1, pagingInfo.getPageSize(), sort);
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
    
    @Test
    public void getAllVersionsTest(){
        try {
            //create number of version list as required.
            this.setVersionList(5);
            List<Version>  versionList1 = classUnderTest.getAllVersions();
            Assert.assertNotNull(versionList1);
            for (Version version : versionList1) {
                Assert.assertNotNull(version);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void getAllVersionsWithArgumentsTest(){
        VersionInfo versionInfo = this.getVersionInfo();
        
        List<Version> versionList = new ArrayList<>();
        Version version1 = null;
        for (int i=0;i< 5;i++) {
            version1 = this.createVersion(i);
            versionList.add(version1);
        }
        Page<Version> pageVersion = new PageImpl<>(versionList);
        Pageable pageRequest = getPagingInformation(versionInfo,"column1");
        when(versionDAOMock.findByLibraryNameAndModelNameAndSearchTextInNameOrDescription("lib1", "model", "any", pageRequest)).thenReturn(pageVersion);
        when(versionDAOMock.findByLibraryNameAndModelNameAndSearchTextInNameOrDescription("lib1", "model", "", pageRequest)).thenReturn(pageVersion);
        when(versionDAOMock.findByLibraryNameAndModelNameAndSearchTextInNameOrDescription("lib1", "model", "", pageRequest)).thenReturn(pageVersion);
        
        try {
            pageVersion = classUnderTest.getAllVersions("lib1", "model", versionInfo);
            Assert.assertNotNull(pageVersion);
            for (Version version : pageVersion) {
                Assert.assertNotNull(version);
            }
            
            versionInfo.setSearchString("");
            pageVersion = classUnderTest.getAllVersions("lib1", "model", versionInfo);
            Assert.assertNotNull(pageVersion);
            for (Version version : pageVersion) {
                Assert.assertNotNull(version);
            }
            
            versionInfo.setSortColumn(null);
            pageVersion = classUnderTest.getAllVersions("lib1", "model", versionInfo);
            Assert.assertNull(pageVersion);
            
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    
    
    @Test
    public void getVersionDetailsTest(){
        Version version = this.createVersion(1);
        when(versionDAOMock.findOne("05a68810-e6f9-11e3-a68a-82687f4fc151")).thenReturn(version);
        try {
            version = classUnderTest.getVersionDetails("");
            Assert.assertNull(version);
            version = classUnderTest.getVersionDetails("05a68810-e6f9-11e3-a68a-82687f4fc151");
            Assert.assertNotNull(version);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void getAllLibrariesTest(){
        VersionInfo  versionInfo = this.getVersionInfo();
        List<String> content = new ArrayList<>();
        content.add("page1");
        content.add("page2");
        content.add("page3");
        Page<String> pageString = new PageImpl<>(content);
        Pageable pageRequest = getPagingInformation(versionInfo, "UPPER(modelLibrary.name)");
        
        when(versionDAOMock.findAllLibraries(AdminUtil.getLikePattern(versionInfo.getSearchString()), pageRequest)).thenReturn(pageString);
        
        try {
            Page<String> pageStringResults = classUnderTest.getAllLibraries(versionInfo);
            Assert.assertNotNull(pageStringResults);
            for (String string : pageStringResults) {
                Assert.assertNotNull(string);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }


    @Test
    public void getAllModelsForLibraryTest(){
        VersionInfo  versionInfo = this.getVersionInfo();
        versionInfo.setSortColumn("UPPER(mapping.model.name)");
        List<String> content = new ArrayList<>();
        content.add("page1");
        content.add("page2");
        content.add("page3");
        Page<String> pageString = new PageImpl<>(content);
        Pageable pageRequest = getPagingInformation(versionInfo, "UPPER(mapping.model.name)");
        when(versionDAOMock.findAllModels("lib1",pageRequest)).thenReturn(pageString);
        try {
            Page<String> pageString1 = classUnderTest.getAllModelsForLibrary("lib1", versionInfo);
            Assert.assertNotNull(pageString1);
            for (String string : pageString1) {
                Assert.assertNotNull(string);
            }
            
            pageString1 = classUnderTest.getAllModelsForLibrary(null, versionInfo);
            Assert.assertNull(pageString1);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void findAllMappingsTest(){
        Model model = new Model();
        model.setDescription("model1");
        model.setDocumentationName("modelDocumented");
        model.setIoDefinitionName("modelIOName");
        model.setModelDefinition(null);
        model.setName("model1");
        model.setTenantId("ocwin");
        model.setUmgName("modelumg1");
        List<Mapping> mappings = new ArrayList<>();
        byte[] modelIO = new byte[10];
        Mapping mapping = null;
        for(int i=1;i<=5;i++){
            mapping = new Mapping();
            mapping.setDescription("mapping description "+i);
            mapping.setModel(model);
            mapping.setModelIO(modelIO);
            mapping.setName("mapping"+i);
            mapping.setTenantId("tenant1");
            mappings.add(mapping);
        }
        
        when(mappingDAOMock.findByModel(model)).thenReturn(mappings);
        
        try {
            KeyValuePair<Boolean, List<Mapping>> keyValuePair = classUnderTest.findAllMappings(model);
            Assert.assertNotNull(keyValuePair);
            
            List<Mapping> resultMappings = keyValuePair.getValue();
            for (Mapping resultMapping : resultMappings) {
                Assert.assertNotNull(resultMapping);
            }
            
            keyValuePair = classUnderTest.findAllMappings(null);
            Assert.assertNotNull(keyValuePair);
            resultMappings = keyValuePair.getValue();
            Assert.assertNull(resultMappings);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void getAllTenantModelNamesTest(){
        List<String> content = new ArrayList<>();
        content.add("page1");
        content.add("page2");
        content.add("page3");
        when(versionDAOMock.getAllTenantModelNames()).thenReturn(content);
        
        try {
            List<String> tenanatModelNames = classUnderTest.getAllTenantModelNames();
            Assert.assertNotNull(tenanatModelNames);
            for (String string : tenanatModelNames) {
                Assert.assertNotNull(string);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void findByNameAndMappingNameAndModelLibraryUmgNameTest(){
        Version version = this.createVersion(1);
        when(versionDAOMock.findByNameAndMappingNameAndModelLibraryUmgName("ocwin", "mapping1", "lib1")).thenReturn(version);
        
        try {
            Version v1 = classUnderTest.findByNameAndMappingNameAndModelLibraryUmgName("ocwin", "mapping1", "lib1");
            Assert.assertNotNull(v1);
            v1 = classUnderTest.findByNameAndMappingNameAndModelLibraryUmgName("ocwin", "", "lib1");
            Assert.assertNull(v1);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void getAllMajorVersionsTest(){
        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(2);
        integerList.add(3);
        
        when(versionDAOMock.getMajorVersionsForTenantModelName("ocwin")).thenReturn(integerList);
        
        try {
            List<Integer> resultList = classUnderTest.getAllMajorVersions("ocwin");
            Assert.assertNotNull(resultList);
            for (Integer integer : resultList) {
                Assert.assertNotNull(integer);
            }
            
            resultList = classUnderTest.getAllMajorVersions("");
            Assert.assertNotNull(resultList);
            Assert.assertTrue(resultList.size()==0);
            
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void getModelNamesForLibraryNameAndCharsInNameOrDescriptionTest(){
        List<String> modelList = new ArrayList<>();
        modelList.add("model1");
        modelList.add("model2");
        modelList.add("model3");
        Sort sort = new Sort(Direction.ASC, "UPPER(ver.mapping.model.name)");
        when(versionDAOMock.getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "lib", sort)).thenReturn(modelList);
        
        try {
            List<String> resultModelList = classUnderTest.getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "lib", false);
            Assert.assertNotNull(resultModelList);
            for (String string : resultModelList) {
                Assert.assertNotNull(string);
            }
            
            resultModelList = classUnderTest.getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "", false);
            Assert.assertNotNull(resultModelList);
            Assert.assertTrue(resultModelList.size()==0);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void markVersionAsTestedTest(){
        Version version = this.createVersion(1);
        when(versionDAOMock.save(version)).thenReturn(version);
        try {
            Version resultVersion= classUnderTest.markVersionAsTested(version);
            Assert.assertNotNull(resultVersion);
            
            version.setId(null);
            version.setStatus(VersionStatus.SAVED.getVersionStatus());
            
            resultVersion= classUnderTest.markVersionAsTested(version);
            Assert.assertNotNull(resultVersion);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }
    
    
    
    @Test
    public void testFindVersionWithTidNameAndStatusPublishedORDeactivated() throws BusinessException, SystemException {
        String tidName = "tidTestedVersionName1";
        Version version = createVersion("4", "Version4", 4, 0, VersionStatus.PUBLISHED.getVersionStatus(), "");
        List<Version> verList = new ArrayList<>();
        verList.add(version);
        when(versionDAOMock.findByMappingName(tidName,VersionStatus.PUBLISHED.getVersionStatus(),VersionStatus.DEACTIVATED.getVersionStatus())).thenReturn(verList);
        List<Version> result = classUnderTest.findVersionWithTidNameAndStatusPublishedORDeactivated(tidName);
        assertEquals(1, result.size());
        assertEquals(VersionStatus.PUBLISHED.getVersionStatus(), result.get(0).getStatus() );
        verify(versionDAOMock, times(1)).findByMappingName(tidName,VersionStatus.PUBLISHED.getVersionStatus(),VersionStatus.DEACTIVATED.getVersionStatus());
    }
    
    
}