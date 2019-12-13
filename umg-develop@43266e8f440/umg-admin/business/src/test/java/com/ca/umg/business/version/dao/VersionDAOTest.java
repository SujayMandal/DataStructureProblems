package com.ca.umg.business.version.dao;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.version.VersionAbstractTest;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@Ignore 
// TODO fix ignored test cases
public class VersionDAOTest extends VersionAbstractTest {

    private RequestContext requestContext;

    @Before
    public void setup() {
        requestContext = getLocalhostTenantContext();
    }

    @Test
    public void saveTest() {
        Model model = buildModel("createModel1", "Modle1", "docName", "ioName", "text/xml", "sampleIo");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLib", "Dummy Library Description", "DummyLib", "DummyJarIO.jar",
                "MATLAB", "INTERNAL", "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256",
                "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("Mapping1", model, requestContext.getTenantCode(), "Description", "mappigIO");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("V1", "V1", 1, 0, "SAVED", mapping, modelLibrary, "this is version description");
        getVersionDAO().saveAndFlush(version);
        List<Version> allVersions = getVersionDAO().findAll();
        assertNotNull(allVersions);
        assertThat(1, is(allVersions.size()));
        assertThat("V1", is(allVersions.get(0).getName()));

        Pageable pageable = new PageRequest(0, 5);
        Page<String> page = getVersionDAO().findAllLibraries("%dum%", pageable);
        assertNotNull(page);
        assertTrue(page.getSize() > 0);
        deleteTestData(model, modelLibrary, mapping, version);
    }

    public void deleteTestData(Model model, ModelLibrary modelLibrary, Mapping mapping, Version version) {
        getVersionDAO().delete(version);
        getVersionDAO().flush();
        getMappingDAO().delete(mapping);
        getMappingDAO().flush();
        getModelDAO().delete(model);
        getModelDAO().flush();
        getModelLibraryDAO().delete(modelLibrary);
        getModelLibraryDAO().flush();
    }

    @Test
    public void testFindNotDeletedVersions() {
        Model model = buildModel("createModelDelTest", "ModleDelTest", "docNameDelTest", "ioNameDelTest", "text/xml",
                "sampleIoDelTest");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLibDelTest", "Dummy Library Description Del Test", "DummyLibDelTest",
                "DummyJarIO.jar", "MATLAB", "INTERNAL", "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4",
                "SHA256", "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("MappingDelTest", model, requestContext.getTenantCode(), "DescriptionDelTest",
                "mappigIODelTest");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("VDelTest1", "VDelTest1", 1, 0, VersionStatus.SAVED.getVersionStatus(), mapping,
                modelLibrary, "this is version description Del test1");
        getVersionDAO().saveAndFlush(version);
        version = buildVersion("VDelTest2", "VDelTest2", 2, 0, VersionStatus.TESTED.getVersionStatus(), mapping, modelLibrary,
                "this is version description Del test2");
        getVersionDAO().saveAndFlush(version);
        version = buildVersion("VDelTest3", "VDelTest3", 3, 0, VersionStatus.DEACTIVATED.getVersionStatus(), mapping,
                modelLibrary, "this is version description Del test3");
        getVersionDAO().saveAndFlush(version);
        version = buildVersion("VDelTest4", "VDelTest4", 4, 0, VersionStatus.PUBLISHED.getVersionStatus(), mapping, modelLibrary,
                "this is version description Del test4");
        getVersionDAO().saveAndFlush(version);
        version = buildVersion("VDelTest5", "VDelTest5", 5, 0, VersionStatus.DELETED.getVersionStatus(), mapping, modelLibrary,
                "this is version description Del test5");
        getVersionDAO().saveAndFlush(version);

        List<Version> verList = getVersionDAO().findNotDeletedVersions("MappingDelTest", VersionStatus.DELETED.name());

        assertNotNull(verList);
        assertThat(4, is(verList.size()));

        // deleting the created test data
        verList.add(version);
        deleteTestDataList(model, modelLibrary, mapping, verList);
    }

    @Test
    public void testFailFindNotDeletedVersions() {
        Model model = buildModel("createModelDelTest1", "ModleDelTest1", "docNameDelTest1", "ioNameDelTest1", "text/xml",
                "sampleIoDelTest1");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLibDelTest1", "Dummy Library Description Del Test1",
                "DummyLibDelTest1", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("MappingDelTest1", model, requestContext.getTenantCode(), "DescriptionDelTest1",
                "mappigIODelTest1");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("VDelTest6", "VDelTest6", 6, 0, VersionStatus.DELETED.getVersionStatus(), mapping,
                modelLibrary, "this is version description Del test6");
        getVersionDAO().saveAndFlush(version);

        List<Version> verList = getVersionDAO().findNotDeletedVersions("MappingDelTest1",
                VersionStatus.DELETED.getVersionStatus());

        assertThat(0, is(verList.size()));

        // deleting the created test data
        deleteTestData(model, modelLibrary, mapping, version);
    }

    @Test
    public void testFindByMappingName() {
        Model model = buildModel("createModelPubDeacTest", "ModlePubDeacTest", "docNamePubDeacTest", "ioNamePubDeacTest",
                "text/xml", "sampleIoPubDeacTest");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLibPubDeacTest", "Dummy Library Description Pub Deac test",
                "DummyLibPubDeacTest", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("MappingPubDeacTest", model, requestContext.getTenantCode(), "DescriptionPubDeacTest",
                "mappigIOPubDeacTest");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("VPubDeacTest1", "VPubDeacTest1", 1, 0, VersionStatus.DEACTIVATED.getVersionStatus(),
                mapping, modelLibrary, "this is version description Pub Deac test");
        getVersionDAO().saveAndFlush(version);
        version = buildVersion("VPubDeacTest2", "VPubDeacTest2", 2, 0, VersionStatus.PUBLISHED.getVersionStatus(), mapping,
                modelLibrary, "this is version description Pub Deac Test1");
        getVersionDAO().saveAndFlush(version);

        List<Version> verList = getVersionDAO().findByMappingName("MappingPubDeacTest",
                VersionStatus.PUBLISHED.getVersionStatus(), VersionStatus.DEACTIVATED.getVersionStatus());

        assertNotNull(verList);
        assertThat(2, is(verList.size()));

        // deleting the created test data
        deleteTestDataList(model, modelLibrary, mapping, verList);
    }

    @Test
    public void testFailFindByMappingName() {
        Model model = buildModel("createModelPubDeacTest1", "ModlePubDeacTest1", "docNamePubDeacTest1", "ioNamePubDeacTes1t",
                "text/xml", "sampleIoPubDeacTest1");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLibPubDeacTest1", "Dummy Library Description Pub Deac test1",
                "DummyLibPubDeacTest", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("MappingPubDeacTest1", model, requestContext.getTenantCode(), "DescriptionPubDeacTest1",
                "mappigIOPubDeacTest1");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("VPubDeacTest3", "VPubDeacTest3", 3, 0, VersionStatus.SAVED.getVersionStatus(), mapping,
                modelLibrary, "this is version description Pub Deac test 3");
        getVersionDAO().saveAndFlush(version);

        List<Version> verList = getVersionDAO().findByMappingName("MappingPubDeacTest1",
                VersionStatus.PUBLISHED.getVersionStatus(), VersionStatus.DEACTIVATED.getVersionStatus());

        assertThat(0, is(verList.size()));

        // deleting the created test data
        deleteTestData(model, modelLibrary, mapping, version);
    }

    public void deleteTestDataList(Model model, ModelLibrary modelLibrary, Mapping mapping, List<Version> versionList) {
        for (Version version : versionList) {
            getVersionDAO().delete(version);
            getVersionDAO().flush();
        }
        getMappingDAO().delete(mapping);
        getMappingDAO().flush();
        getModelDAO().delete(model);
        getModelDAO().flush();
        getModelLibraryDAO().delete(modelLibrary);
        getModelLibraryDAO().flush();
    }

    @Test
    public void testGetTestedVersions() {
        Model model = buildModel("createModelTestVerTest", "ModleTestVerTest", "docNameTestVerTest", "ioNameTestVerTest",
                "text/xml", "sampleIoTestVerTest");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLibTestVerTest", "Dummy Library Description Test Ver test",
                "DummyLibTestVerTest", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("MappingTestVerTest", model, requestContext.getTenantCode(), "DescriptionTestVerTest",
                "mappigIOTestVerTest");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("VTestVerTest1", "VTestVerTest1", 1, 0, VersionStatus.TESTED.getVersionStatus(), mapping,
                modelLibrary, "this is version description Test Ver test");
        getVersionDAO().saveAndFlush(version);

        List<String> verList = getVersionDAO().getTestedVersions("MappingTestVerTest", VersionStatus.TESTED.getVersionStatus());

        assertNotNull(verList);
        assertThat(1, is(verList.size()));

        // deleting the created test data
        deleteTestData(model, modelLibrary, mapping, version);
    }

    @Test
    public void testGetTestedVersionsFailure() {
        Model model = buildModel("createModelTestVerTest1", "ModleTestVerTest1", "docNameTestVerTest1", "ioNameTestVerTest1",
                "text/xml", "sampleIoTestVerTest1");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLibTestVerTest1", "Dummy Library Description Test Ver test",
                "DummyLibTestVerTest1", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("MappingTestVerTest1", model, requestContext.getTenantCode(), "DescriptionTestVerTest1",
                "mappigIOTestVerTest1");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("VTestVerTest2", "VTestVerTest2", 2, 0, VersionStatus.SAVED.getVersionStatus(), mapping,
                modelLibrary, "this is version description Test Ver test 2");
        getVersionDAO().saveAndFlush(version);

        List<String> verList = getVersionDAO().getTestedVersions("MappingTestVerTest1", VersionStatus.TESTED.getVersionStatus());

        assertThat(0, is(verList.size()));

        // deleting the created test data
        deleteTestData(model, modelLibrary, mapping, version);
    }

    @Test
    public void testUpdateVersion() {
        Model model = buildModel("createModel1", "Modle1", "docName", "ioName", "text/xml", "sampleIo");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLib", "Dummy Library Description", "DummyLib", "DummyJarIO.jar",
                "MATLAB", "INTERNAL", "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256",
                "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("Mapping1", model, requestContext.getTenantCode(), "Description", "mappigIO");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("V1", "V1", 1, 0, "SAVED", mapping, modelLibrary, "this is version description");
        version = getVersionDAO().saveAndFlush(version);
        assertNotNull(version.getId());
        assertEquals("this is version description", version.getVersionDescription());
        version.setVersionDescription("New Description");
        getVersionDAO().saveAndFlush(version);
        Version updatedVersion = getVersionDAO().findOne(version.getId());
        assertEquals("New Description", updatedVersion.getVersionDescription());
        deleteTestData(model, modelLibrary, mapping, version);
    }
    
    @Test
    public void testgetVersionStatus() {
        Model model = buildModel("createModelTestVerTest21", "ModleTestVerTest21", "docNameTestVerTest21", "ioNameTestVerTest21",
                "text/xml", "sampleIoTestVerTest21");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLibTestVerTest21", "Dummy Library Description Test Ver test21",
                "DummyLibTestVerTest21", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("MappingTestVerTest21", model, requestContext.getTenantCode(), "DescriptionTestVerTest21",
                "mappigIOTestVerTest21");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("VTestVerTest21", "VTestVerTest21", 21, 0, VersionStatus.SAVED.getVersionStatus(),
                mapping, modelLibrary, "this is version description Test Ver test 21");
        getVersionDAO().saveAndFlush(version);

        List<String> verList = getVersionDAO().getVersionStatus("MappingTestVerTest21", VersionStatus.SAVED.getVersionStatus(),
                VersionStatus.TESTED.getVersionStatus());

        assertThat(1, is(verList.size()));

        // deleting the created test data
        deleteTestData(model, modelLibrary, mapping, version);
    }
    
    @Test
    public void testFailgetVersionStatus() {
        Model model = buildModel("createModelTestVerTestFl21", "ModleTestVerTestFl21", "docNameTestVerTestFl21", "ioNameTestVerTestFl21",
                "text/xml", "sampleIoTestVerTestFl21");
        model.getModelDefinition().setModel(model);
        model.setUmgName(model.getName());
        getModelDAO().saveAndFlush(model);
        ModelLibrary modelLibrary = buildModelLibrary("DummyLibTestVerTestFl21", "Dummy Library Description Test Ver testFl21",
                "DummyLibTestVerTestFl21", "DummyJarIO.jar", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        getModelLibraryDAO().saveAndFlush(modelLibrary);
        Mapping mapping = buildMapping("MappingTestVerTestFl21", model, requestContext.getTenantCode(), "DescriptionTestVerTestFl21",
                "mappigIOTestVerTest21");
        getMappingDAO().saveAndFlush(mapping);
        Version version = buildVersion("VTestVerTestFl21", "VTestVerTestFl21", 21, 0, VersionStatus.SAVED.getVersionStatus(),
                mapping, modelLibrary, "this is version description Test Ver test 21");
        getVersionDAO().saveAndFlush(version);

        List<String> verList = getVersionDAO().getVersionStatus("MappingTestVerTestDummy", VersionStatus.SAVED.getVersionStatus(),
                VersionStatus.TESTED.getVersionStatus());

        assertThat(0, is(verList.size()));

        // deleting the created test data
        deleteTestData(model, modelLibrary, mapping, version);
    }
    
    
	@Test
	public void testFindAllversionByVersionName(){
		Model model = buildModel("createModelPubDeacTest1", "ModlePubDeacTest1", "docNamePubDeacTest", "ioNamePubDeacTest", "text/xml", "sampleIoPubDeacTest");
		model.getModelDefinition().setModel(model);
		model.setUmgName(model.getName());
		getModelDAO().saveAndFlush(model);
		ModelLibrary modelLibrary = buildModelLibrary("DummyLibPubDeacTest1", "Dummy Library Description Pub Deac test", "DummyLibPubDeacTest", "DummyJarIO.jar", "MATLAB", "INTERNAL", 
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d41", "SHA256", "Matlab-7.16");
		getModelLibraryDAO().saveAndFlush(modelLibrary);
		Mapping mapping = buildMapping("MappingPubDeacTest1", model, requestContext.getTenantCode(), "DescriptionPubDeacTest", "mappigIOPubDeacTest");
		getMappingDAO().saveAndFlush(mapping);
		Version version = buildVersion("VPubDeacTest11", "VPubDeacTest11", 1, 0, VersionStatus.DEACTIVATED.getVersionStatus(), mapping, modelLibrary, "this is version description Pub Deac test");
		getVersionDAO().saveAndFlush(version);
		

        Pageable pageable = new PageRequest(0, 5);
        Page<Version> verList = getVersionDAO().findAllversionByVersionName("VPubDeacTest11", pageable);

        assertNotNull(verList);

		// deleting the created test data
		deleteTestData(model, modelLibrary, mapping, version);
	}
}
