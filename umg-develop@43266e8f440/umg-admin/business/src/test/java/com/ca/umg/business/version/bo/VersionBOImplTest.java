package com.ca.umg.business.version.bo;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.dao.MappingDAO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.VersionAbstractTest;
import com.ca.umg.business.version.audit.VersionAuditFactory;
import com.ca.umg.business.version.dao.VersionContainerDAO;
import com.ca.umg.business.version.dao.VersionDAO;
import com.ca.umg.business.version.dao.VersionMetricsDAO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionStatus;

@Ignore
// TODO fix ignored test cases
public class VersionBOImplTest {

    @InjectMocks
    private VersionBOImpl classUnderTest = new VersionBOImpl();

    @Mock
    private VersionDAO mockVersionDAO;
    
    @Mock
    private VersionMetricsDAO mockVersionMetricsDAO;

    @Mock

    private MappingDAO mockMappingDAO;

    @Mock
    private VersionAuditFactory versionAuditFactory;

    @Mock
    private VersionContainerDAO versionContainerDAO;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void createFirstMajorVersion() throws BusinessException, SystemException {
        when(mockVersionDAO.getMaxMajorVersionForTenantModelName("some name")).thenReturn(null);
        Version version = new Version();
        version.setName("some name");

        classUnderTest.create(version);

        assertThat(version.getStatus(), is(VersionStatus.SAVED.getVersionStatus()));
        assertThat(version.getMajorVersion(), is(1));
        assertThat(version.getMinorVersion(), is(0));
        verify(mockVersionDAO).save(version);
    }

    @Test
    public void createMajorVersion_WithDeletedVersion() throws BusinessException, SystemException {
        when(mockVersionDAO.getMaxMajorVersionForTenantModelName("some name")).thenReturn(2);
        List<Version> versionList = new ArrayList<Version>();
        Version deletedVersion = new Version();
        deletedVersion.setName("some name");
        deletedVersion.setMajorVersion(3);
        versionList.add(deletedVersion);
        when(versionAuditFactory.getAllDeletedRevisions(Version.class, deletedVersion)).thenReturn(versionList);
        Version version = new Version();
        version.setName("some name");

        classUnderTest.create(version);

        assertThat(version.getStatus(), is(VersionStatus.SAVED.getVersionStatus()));
        assertThat(version.getMajorVersion(), is(3));
        assertThat(version.getMinorVersion(), is(0));
        verify(mockVersionDAO).save(version);
    }
    
    @Test
    public void createMajorVersion() throws BusinessException, SystemException {
        when(mockVersionDAO.getMaxMajorVersionForTenantModelName("some name")).thenReturn(2);
        List<Version> versionList = new ArrayList<Version>();
        Version version = new Version();
        version.setName("some name");

        when(versionAuditFactory.getAllDeletedRevisions(Version.class, version)).thenReturn(versionList);

        classUnderTest.create(version);

        assertThat(version.getStatus(), is(VersionStatus.SAVED.getVersionStatus()));
        assertThat(version.getMajorVersion(), is(3));
        assertThat(version.getMinorVersion(), is(0));
        verify(mockVersionDAO).save(version);
    }
    
    @Test
    public void deleteVersion() throws BusinessException, SystemException {
        Version version = new Version();
        List<Map<String, Object>> verList = new ArrayList<>();
        Map<String, Object> mapTest = new HashMap<String, Object>();
        mapTest.put("test", "value");
        verList.add(mapTest);
        when(mockVersionDAO.findOne("1")).thenReturn(version);
        when(versionContainerDAO.getVersionNameList(any(String.class))).thenReturn(verList);
        classUnderTest.delete("1");
    }
    
    @Test(expected = BusinessException.class)  
    public void deleteNonExistingVersion() throws BusinessException, SystemException {
        when(mockVersionDAO.findOne("1")).thenReturn(null);
        classUnderTest.delete("1");
    }
    
    @Test
    public void testMajorVersionCompare() {
        Version ver1 = new Version();
        ver1.setMajorVersion(1);
        ver1.setMinorVersion(0);
        Version ver2 = new Version();
        ver2.setMajorVersion(2);
        ver2.setMinorVersion(0);
        Version ver3 = new Version();
        ver3.setMajorVersion(3);
        ver3.setMinorVersion(0);
        List<Version> versionList = new ArrayList<Version>();
        versionList.add(ver3);
        versionList.add(ver2);
        versionList.add(ver1);
        assertThat(versionList.get(0).getMajorVersion(), is(3));
        Collections.sort(versionList, VersionBOImpl.VERSION_LATEST_LAST_ORDER);
        assertThat(versionList.get(0).getMajorVersion(), is(1));
    }
    
    @Test
    public void testMinorVersionCompare() {
        Version ver1 = new Version();
        ver1.setMajorVersion(1);
        ver1.setMinorVersion(1);
        Version ver2 = new Version();
        ver2.setMajorVersion(1);
        ver2.setMinorVersion(2);
        Version ver3 = new Version();
        ver3.setMajorVersion(1);
        ver3.setMinorVersion(3);
        List<Version> versionList = new ArrayList<Version>();
        versionList.add(ver3);
        versionList.add(ver2);
        versionList.add(ver1);
        assertThat(versionList.get(0).getMinorVersion(), is(3));
        Collections.sort(versionList, VersionBOImpl.VERSION_LATEST_LAST_ORDER);
        assertThat(versionList.get(0).getMinorVersion(), is(1));
    }

    @Test
    public void createFirstMinorVersion() throws BusinessException, SystemException {
        when(mockVersionDAO.getMaxMinorVersionForGivenMajorVersionAndTenantModelName(2, "some name")).thenReturn(null);
        Version version = new Version();
        version.setName("some name");
        version.setMajorVersion(2);

        classUnderTest.create(version);

        assertThat(version.getStatus(), is(VersionStatus.SAVED.getVersionStatus()));
        assertThat(version.getMajorVersion(), is(2));
        assertThat(version.getMinorVersion(), is(1));
        verify(mockVersionDAO).save(version);
    }

    @Test
    public void createMinorVersion() throws BusinessException, SystemException {
        when(mockVersionDAO.getMaxMinorVersionForGivenMajorVersionAndTenantModelName(3, "some name")).thenReturn(5);
        Version version = new Version();
        version.setName("some name");
        version.setMajorVersion(3);

        classUnderTest.create(version);

        assertThat(version.getStatus(), is(VersionStatus.SAVED.getVersionStatus()));
        assertThat(version.getMajorVersion(), is(3));
        assertThat(version.getMinorVersion(), is(6));
        verify(mockVersionDAO).save(version);
    }

    @Test
    public void getTenantModeldescriptionWhenVersionNotCreated() throws BusinessException, SystemException {
        when(mockVersionDAO.getTenantModeldescriptions("some name")).thenReturn(null);

        assertThat(classUnderTest.getTenantModeldescription("some name"), is(""));
    }

    @Test
    public void getTenantModeldescriptionWhenVersionIsPresent() throws BusinessException, SystemException {
        when(mockVersionDAO.getTenantModeldescriptions("some name")).thenReturn(asList("desc1"));

        assertThat(classUnderTest.getTenantModeldescription("some name"), is("desc1"));
    }

    @Test
    public void testFindNotDeletedVersions() throws BusinessException, SystemException {
        List<String> listOfNames = classUnderTest.findNotDeletedVersions("dd196f8d-cae2-47a3-af15-9f31fa19e4ad");
        Assert.assertNull(listOfNames);
    }

    @Test
    public void testGetAllUmgVersionsOnModelLibraryId() throws BusinessException, SystemException {
        List<Version> versionList = new ArrayList<Version>();
        Version version = new Version();
        version.setName("UmgVersion");
        version.setMajorVersion(2);
        version.setMinorVersion(1);
        versionList.add(version);
        when(mockVersionDAO.findVersionsOnModelLibAndStatus("libraryId", VersionStatus.DELETED.name())).thenReturn(versionList);
        List<String> resultList = classUnderTest.getAllUmgVersionsOnModelLibraryId("libraryId");
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals("UmgVersion-2.1", resultList.get(0));
    }

    @Test
    public void testGetAllUmgVersionsOnModelLibraryIdForNull() throws BusinessException, SystemException {
        when(mockVersionDAO.findVersionsOnModelLibAndStatus("libraryId", VersionStatus.DELETED.name())).thenReturn(null);
        Assert.assertNull(classUnderTest.getAllUmgVersionsOnModelLibraryId("libraryId"));
    }

    private void setVersionList(int n) {
        Order modelLibraryOrder = new Order(Sort.Direction.ASC, "modelLibrary.name");
        Order modelOrder = new Order(Sort.Direction.ASC, "mapping.model.name");
        Order majorVerOrder = new Order(Sort.Direction.DESC, "majorVersion");
        Order minorVerOrder = new Order(Sort.Direction.DESC, "minorVersion");
        Sort sort = new Sort(modelLibraryOrder, modelOrder, majorVerOrder, minorVerOrder);

        List<Version> versionList = new ArrayList<>();
        Version version = null;
        for (int i = 0; i < n; i++) {
            version = this.createVersion(i);
            versionList.add(version);
        }
        when(mockVersionDAO.findAll(sort)).thenReturn(versionList);
    }

    private Version createVersion(int i) {
        Mapping mapping = new Mapping();
        ModelLibrary modelLibrary = new ModelLibrary();
        Version version = new Version();
        version.setId("05a68810-e6f9-11e3-a68a-82687f4fc15" + (i % 10));
        version.setDeactivatedBy(null);
        version.setDeactivatedOn(null);
        version.setDescription("Version " + i + " Descripion");
        version.setMajorVersion(i);
        version.setMapping(mapping);
        version.setMinorVersion(0);
        version.setModelLibrary(modelLibrary);
        version.setName("version" + i);
        version.setPublishedBy(null);
        version.setPublishedOn(null);
        version.setStatus(i % 2 == 0 ? "active" : "inactive");
        version.setTenantId("tenant1");
        version.setVersionDescription("Version " + i + " Descripion");
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
    public void getAllVersionsTest() {
        try {
            // create number of version list as required.
            this.setVersionList(5);
            List<Version> versionList1 = classUnderTest.getAllVersions();
            Assert.assertNotNull(versionList1);
            for (Version version : versionList1) {
                Assert.assertNotNull(version);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void getAllVersionsWithArgumentsTest() {
        VersionInfo versionInfo = this.getVersionInfo();

        List<Version> versionList = new ArrayList<>();
        Version version1 = null;
        for (int i = 0; i < 5; i++) {
            version1 = this.createVersion(i);
            versionList.add(version1);
        }
        Page<Version> pageVersion = new PageImpl<>(versionList);
        Pageable pageRequest = getPagingInformation(versionInfo, "column1");
        when(mockVersionDAO.findByLibraryNameAndModelNameAndSearchTextInNameOrDescription("lib1", "model", "any", pageRequest))
                .thenReturn(pageVersion);
        when(mockVersionDAO.findByLibraryNameAndModelNameAndSearchTextInNameOrDescription("lib1", "model", "", pageRequest))
                .thenReturn(pageVersion);
        when(mockVersionDAO.findByLibraryNameAndModelNameAndSearchTextInNameOrDescription("lib1", "model", "", pageRequest))
                .thenReturn(pageVersion);

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
    public void getVersionDetailsTest() {
        Version version = this.createVersion(1);
        when(mockVersionDAO.findOne("05a68810-e6f9-11e3-a68a-82687f4fc151")).thenReturn(version);
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
    public void getAllLibrariesTest() {
        VersionInfo versionInfo = this.getVersionInfo();
        List<String> content = new ArrayList<>();
        content.add("page1");
        content.add("page2");
        content.add("page3");
        Page<String> pageString = new PageImpl<>(content);
        Pageable pageRequest = getPagingInformation(versionInfo, "UPPER(modelLibrary.name)");

        when(mockVersionDAO.findAllLibraries(AdminUtil.getLikePattern(versionInfo.getSearchString()), pageRequest)).thenReturn(
                pageString);

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
    public void getAllModelsForLibraryTest() {
        VersionInfo versionInfo = this.getVersionInfo();
        versionInfo.setSortColumn("UPPER(mapping.model.name)");
        List<String> content = new ArrayList<>();
        content.add("page1");
        content.add("page2");
        content.add("page3");
        Page<String> pageString = new PageImpl<>(content);
        Pageable pageRequest = getPagingInformation(versionInfo, "UPPER(mapping.model.name)");
        when(mockVersionDAO.findAllModels("lib1", pageRequest)).thenReturn(pageString);
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
    public void findAllMappingsTest() {
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
        for (int i = 1; i <= 5; i++) {
            mapping = new Mapping();
            mapping.setDescription("mapping description " + i);
            mapping.setModel(model);
            mapping.setModelIO(modelIO);
            mapping.setName("mapping" + i);
            mapping.setTenantId("tenant1");
            mappings.add(mapping);
        }

        when(mockMappingDAO.findByModel(model)).thenReturn(mappings);

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
    public void getAllTenantModelNamesTest() {
        List<String> content = new ArrayList<>();
        content.add("page1");
        content.add("page2");
        content.add("page3");
        when(mockVersionDAO.getAllTenantModelNames()).thenReturn(content);

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
    public void findByNameAndMappingNameAndModelLibraryUmgNameTest() {
        Version version = this.createVersion(1);
        when(mockVersionDAO.findByNameAndMappingNameAndModelLibraryUmgName("ocwin", "mapping1", "lib1")).thenReturn(version);

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
    public void getAllMajorVersionsTest() {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(2);
        integerList.add(3);

        when(mockVersionDAO.getMajorVersionsForTenantModelName("ocwin")).thenReturn(integerList);

        try {
            List<Integer> resultList = classUnderTest.getAllMajorVersions("ocwin");
            Assert.assertNotNull(resultList);
            for (Integer integer : resultList) {
                Assert.assertNotNull(integer);
            }

            resultList = classUnderTest.getAllMajorVersions("");
            Assert.assertNotNull(resultList);
            Assert.assertTrue(resultList.size() == 0);

        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void getModelNamesForLibraryNameAndCharsInNameOrDescriptionTest() {
        List<String> modelList = new ArrayList<>();
        modelList.add("model1");
        modelList.add("model2");
        modelList.add("model3");
        Sort sort = new Sort(Direction.ASC, "UPPER(ver.mapping.model.name)");
        when(mockVersionDAO.getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "lib", sort)).thenReturn(modelList);

        try {
            List<String> resultModelList = classUnderTest.getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "lib",
                    false);
            Assert.assertNotNull(resultModelList);
            for (String string : resultModelList) {
                Assert.assertNotNull(string);
            }

            resultModelList = classUnderTest.getModelNamesForLibraryNameAndCharsInNameOrDescription("lib1", "", false);
            Assert.assertNotNull(resultModelList);
            Assert.assertTrue(resultModelList.size() == 0);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetVersionStatus() throws BusinessException, SystemException {
        List<String> verList = new ArrayList<String>();
        Version version = this.createVersion(31);
        verList.add(version.getName());
        when(
                mockVersionDAO.getVersionStatus("test", VersionStatus.SAVED.getVersionStatus(),
                        VersionStatus.TESTED.getVersionStatus())).thenReturn(verList);

        List<String> result = classUnderTest.getVersionStatus("test");
        Assert.assertNotNull(result);
    }

    @Test
    public void testSearchVersions() throws BusinessException, SystemException {
        when(mockVersionDAO.findAll(any(Specification.class))).thenReturn(getMockVersions(1));

        List<Version> versions = classUnderTest.searchVersions("version-1", null, null);
        assertNotNull(versions);
        assertTrue(versions.size() == 1);

        versions = classUnderTest.searchVersions("version-1", 1, 0);
        assertNotNull(versions);
        assertTrue(versions.size() == 1);
    }

    private List<Version> getMockVersions(int size) {
        List<Version> versions = new ArrayList<Version>();
        for (int i = 0; i < size; i++) {
            Version version = new Version();
            version.setId(Integer.toString(i));
            version.setName("verison-" + i);
            versions.add(version);
        }
        return versions;
    }
    

    @Test
    public void testFindAllVersionName() {
        List<Version> verListResult = null;
        VersionAbstractTest versionAbstractTest = new VersionAbstractTest();
        Model model = versionAbstractTest.buildModel("createModelPubDeacTest1", "ModlePubDeacTest1", "docNamePubDeacTest",
                "ioNamePubDeacTest", "text/xml", "sampleIoPubDeacTest");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        ModelLibrary modelLibrary = versionAbstractTest.buildModelLibrary("DummyLibPubDeacTest1",
                "Dummy Library Description Pub Deac test", "DummyLibPubDeacTest", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d41", "SHA256", "Matlab-7.16");
        Mapping mapping = versionAbstractTest.buildMapping("MappingPubDeacTest1", model, "localhost", "DescriptionPubDeacTest",
                "mappigIOPubDeacTest");
        Version version = versionAbstractTest.buildVersion("VPubDeacTest11", "VPubDeacTest11", 1, 0,
                VersionStatus.DEACTIVATED.getVersionStatus(), mapping, modelLibrary, "this is version description Pub Deac test");
        version.setCreatedBy("TEST");

        List<Version> verList = new ArrayList<Version>();
        verList.add(version);
        Pageable pageable = new PageRequest(0, 5);

        Page<Version> pagedVersion = new PageImpl<Version>(verList, pageable, 1);

        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setPage(0);
        searchOptions.setPageSize(5);
        searchOptions.setSearchText("TEST");

        when((mockVersionDAO.findAll(any(Specification.class), any(Pageable.class)))).thenReturn(pagedVersion);
       
        try{
            verListResult = classUnderTest.findAllVersionName(searchOptions);
        } catch (BusinessException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            e.printStackTrace();
        }
        assertNotNull(verListResult);

    }

    @Test
    public void testFindAllversionByVersionName() {
        Page<Version> verListResult = null;
        VersionAbstractTest versionAbstractTest = new VersionAbstractTest();
        Model model = versionAbstractTest.buildModel("createModelPubDeacTest1", "ModlePubDeacTest1", "docNamePubDeacTest",
                "ioNamePubDeacTest", "text/xml", "sampleIoPubDeacTest");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        ModelLibrary modelLibrary = versionAbstractTest.buildModelLibrary("DummyLibPubDeacTest1",
                "Dummy Library Description Pub Deac test", "DummyLibPubDeacTest", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d41", "SHA256", "Matlab-7.16");
        Mapping mapping = versionAbstractTest.buildMapping("MappingPubDeacTest1", model, "localhost", "DescriptionPubDeacTest",
                "mappigIOPubDeacTest");
        Version version = versionAbstractTest.buildVersion("VersionName", "VPubDeacTest11", 1, 0,
                VersionStatus.DEACTIVATED.getVersionStatus(), mapping, modelLibrary, "this is version description Pub Deac test");
        version.setCreatedBy("TEST");

        List<Version> verList = new ArrayList<Version>();
        verList.add(version);
        Pageable pageable = new PageRequest(1, 5);

        Page<Version> pagedVersion = new PageImpl<Version>(verList, pageable, 1);

        when((mockVersionDAO.findAll(any(Specification.class), any(Pageable.class)))).thenReturn(pagedVersion);

        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setPage(0);
        searchOptions.setPageSize(5);
        try{
            verListResult = classUnderTest.findAllversionByVersionName("VersionName", searchOptions);
        } catch (BusinessException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            e.printStackTrace();
        }
        assertNotNull(verListResult);

    }

    // @Test
    // public void testGetVersionDetails() throws BusinessException, SystemException {
    // List<Map<String, Object>> versionDetails = populateVersionDetails();
    // when(versionContainerDAO.getExisitingVersionDetails("MATLAB", ModelType.ONLINE)).thenReturn(versionDetails);
    // List<VersionInfo> versionInfoList = classUnderTest.getVersionDetailsForLanguage("MATLAB", ModelType.ONLINE);
    // assertNotNull(versionInfoList);
    // Assert.assertEquals("testVersion", versionInfoList.get(0).getName());
    // }
    
    @Test
    public void testSearchLibraries() throws BusinessException, SystemException {
        SearchOptions searchOptions = new SearchOptions();
        List<Map<String, Object>> versionDetails = populateVersionDetails();
        when(versionContainerDAO.searchLibraries(searchOptions,"MATLAB")).thenReturn(versionDetails);
        List<VersionInfo> versionInfoList = classUnderTest.searchLibraries(searchOptions,"MATLAB");
        assertNotNull(versionInfoList);
        Assert.assertEquals("testjar.jar", versionInfoList.get(0).getModelLibrary().getJarName());
    }

    // @Test
    // public void testSearchIoDefns() throws BusinessException, SystemException {
    // SearchOptions searchOptions = new SearchOptions();
    // List<Map<String, Object>> versionDetails = populateVersionDetails();
    // when(versionContainerDAO.searchIoDefns(searchOptions,"MATLAB")).thenReturn(versionDetails);
    // List<VersionInfo> versionInfoList = classUnderTest.searchIoDefns(searchOptions,"MATLAB");
    // assertNotNull(versionInfoList);
    // Assert.assertEquals("testIoDefn.xlsx", versionInfoList.get(0).getMapping().getModel().getIoDefinitionName());
    // }

    private List<Map<String, Object>> populateVersionDetails() {
        List<Map<String, Object>> verDetails = new ArrayList<>();
        Map<String, Object> verDetailsMap = new HashMap<>();
        verDetailsMap.put("VERSION_NAME", "testVersion");
        verDetailsMap.put("MAJOR_VERSION", 1);
        verDetailsMap.put("MINOR_VERSION", 2);
        verDetailsMap.put("STATUS", "TESTED");
        verDetailsMap.put("CREATED_BY", "SYSTEM");
        verDetailsMap.put("CREATED_DATE", 1425050868047L);
        verDetailsMap.put("JAR_NAME", "testjar.jar");
        verDetailsMap.put("MODEL_LIBRARY_ID", "09000-7823gkajsh-82379048");
        verDetailsMap.put("IO_DEFINITION_NAME", "testIoDefn.xlsx");
        verDetailsMap.put("MODEL_ID", "09000-7823gkajsh-823798596");
        verDetails.add(verDetailsMap);
        return verDetails;
    }

    @Test
    public void testVersionMetrics() throws BusinessException, SystemException {
        Map dummyMetricMap = new HashMap();
        dummyMetricMap.put("a", 1);
        dummyMetricMap.put("b", 2);
        when(mockVersionMetricsDAO.getVersionMetricsDetails(any(VersionMetricRequestInfo.class))).thenReturn(dummyMetricMap);
        VersionMetricRequestInfo dummyReq = new VersionMetricRequestInfo();
        Map responseMap = classUnderTest.getVersionMetrics(dummyReq);
        assertTrue(responseMap.equals(dummyMetricMap));
    }
    
}
