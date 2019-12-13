package com.ca.umg.business.model.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.info.ModelMappingInfo;
import com.ca.umg.business.model.AbstractModelTest;
import com.ca.umg.business.model.bo.ModelArtifactBO;
import com.ca.umg.business.model.bo.ModelBO;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryHierarchyInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.util.ResourceLoader;
import com.ca.umg.business.version.entity.Version;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
//TODO fix ignored test cases
public class ModelDelegateTest extends AbstractModelTest {

    @InjectMocks
    @Inject
    private ModelDelegate modelDelegate;

    @InjectMocks
    @Inject
    private ModelArtifactBO modelArtifactBO;

    @Inject
    private ModelBO modelBO;

    private RequestContext requestContext;

    @Before
    public void init() {
        requestContext = getLocalhostTenantContext();
    }

    @Test
    public void testCreateModelLibrary() {
        try {        	
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            modelLibraryInfo.setDescription("Test Library Description");
            modelLibraryInfo.setExecutionLanguage("MATLAB");
            modelLibraryInfo.setExecutionType("INTERNAL");
            modelLibraryInfo.setJarName("antlr-2.7.2.jar");
            modelLibraryInfo.setName("TEST" + Math.random());
            modelLibraryInfo.setChecksum("2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4");
            // modelLibraryInfo.setEncodingType("SHA256");
            InputStream stream = ResourceLoader.getResource("classpath:/jarFile/antlr-2.7.2.jar");
            ModelArtifact modelLibJar = new ModelArtifact();
            modelLibJar.setData(stream);
            modelLibJar.setName("antlr-2.7.2.jar");
            modelLibraryInfo.setJar(modelLibJar);
            modelLibraryInfo.setModelExecEnvName("Matlab-7.16");
            modelLibraryInfo = modelDelegate.createModelLibrary(modelLibraryInfo);
            assertNotNull(modelLibraryInfo.getId());
            assertNotNull(modelLibraryInfo.getUmgName());
            modelDelegate.deleteModelLibrary(modelLibraryInfo.getId());
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }
    }

    @Test
    public void testDeleteModelLibrary() {
        try {
            // doNothing().when(modelArtifactBO).deleteModelArtifact(any(ModelArtifact.class), anyBoolean());
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            modelLibraryInfo.setDescription("Test Library Description");
            modelLibraryInfo.setExecutionLanguage("MATLAB");
            modelLibraryInfo.setExecutionType("INTERNAL");
            modelLibraryInfo.setJarName("antlr-2.7.2.jar");
            modelLibraryInfo.setName("TEST" + Math.random());
            modelLibraryInfo.setChecksum("2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4");
            modelLibraryInfo.setEncodingType("SHA256");
            InputStream stream = ResourceLoader.getResource("classpath:/jarFile/antlr-2.7.2.jar");
            ModelArtifact modelLibJar = new ModelArtifact();
            modelLibJar.setData(stream);
            modelLibJar.setName("antlr-2.7.2.jar");
            modelLibraryInfo.setJar(modelLibJar);
            modelLibraryInfo.setModelExecEnvName("Matlab-7.16");
            modelLibraryInfo = modelDelegate.createModelLibrary(modelLibraryInfo);
            assertNotNull(modelLibraryInfo.getId());
            assertNotNull(modelLibraryInfo.getUmgName());

            modelDelegate.deleteModelLibrary(modelLibraryInfo.getId());

            modelLibraryInfo = modelDelegate.getModelLibraryDetails(modelLibraryInfo.getId());
            assertNull(modelLibraryInfo);
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }
    }

    @Test
    public void testModel() {
        ModelArtifact documentation = null;
        ModelArtifact xml = null;
        try {
            doNothing().when(modelArtifactBO).storeArtifacts(any(ModelArtifact[].class));
            doNothing().when(modelArtifactBO).deleteModelArtifact(any(ModelArtifact.class), anyBoolean());

            List<ModelArtifact> modelArtifactsDummy = new ArrayList<ModelArtifact>();
            when(modelArtifactBO.fetchArtifacts(any(String.class), any(String.class), anyBoolean())).thenReturn(
                    modelArtifactsDummy);

            documentation = buildModelArtifact("UMG-MATLAB-IO_1.XML", "UMG-MATLAB-IO_1.XML");
            xml = buildModelArtifact("UMG-MATLAB-IO_1.XML", "UMG-MATLAB-IO_1.XML");
            ModelInfo modelInfo = buildModelInfo("AQMK", "AQMK", "sample", "ioDefnName", "text/xml", documentation, xml);
            modelInfo = modelDelegate.createModel(modelInfo);

            // fetch model
            ModelInfo savedModelInfo = modelDelegate.getModelDetails(modelInfo.getId());
            assertNotNull(modelInfo);

            List<ModelArtifact> modelArtifacts = modelArtifactBO.fetchArtifacts(savedModelInfo.getName(),
                    savedModelInfo.getUmgName(), false);
            assertNotNull(modelArtifacts);
            verify(modelArtifactBO, times(1)).fetchArtifacts(savedModelInfo.getName(), savedModelInfo.getUmgName(), false);

            // delete model
            modelDelegate.deleteModel(modelInfo.getId());

            savedModelInfo = modelDelegate.getModelDetails(modelInfo.getId());
            assertNull(savedModelInfo);

        } catch (BusinessException | SystemException | FileNotFoundException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetModelLibraryHierarchyInfos() throws SystemException, BusinessException {
        try {
            createLModelLibs();
            List<ModelLibraryHierarchyInfo> modelHierarchyLists = modelDelegate.getModelLibraryHierarchyInfos();
            assertTrue(modelHierarchyLists.size() > 0);
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

        List<ModelLibraryInfo> resultList = modelDelegate.findAllLibraries();
        for (ModelLibraryInfo modelLibInfo : resultList) {
            modelDelegate.deleteModelLibrary(modelLibInfo.getId());
        }
    }

    @Test
    public void testfindAllLibraries() throws SystemException, BusinessException {
        List<ModelLibraryInfo> modelHierarchyLists = null;
        try {
            createLModelLibs();
            modelHierarchyLists = modelDelegate.findAllLibraries();
            assertTrue(modelHierarchyLists.size() > 0);
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }
        if(modelHierarchyLists != null) {
	        for (ModelLibraryInfo modelLibInfo : modelHierarchyLists) {
	            modelDelegate.deleteModelLibrary(modelLibInfo.getId());
	        }
        }
    }

    public void createLModelLibs() throws SystemException, BusinessException {
        ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
        modelLibraryInfo.setDescription("Dummy Library Description ");
        modelLibraryInfo.setExecutionLanguage("MATLAB");
        modelLibraryInfo.setExecutionType("INTERNAL");
        modelLibraryInfo.setJarName("antlr-2.7.2.jar");
        modelLibraryInfo.setName("antlr-2.7.2");
        modelLibraryInfo.setChecksum("2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4");
        modelLibraryInfo.setEncodingType("SHA256");
        InputStream stream = ResourceLoader.getResource("classpath:/jarFile/antlr-2.7.2.jar");
        ModelArtifact modelLibJar = new ModelArtifact();
        modelLibJar.setData(stream);
        modelLibJar.setName("antlr-2.7.2.jar");
        modelLibraryInfo.setJar(modelLibJar);
        modelLibraryInfo.setModelExecEnvName("Matlab-7.16");
        modelLibraryInfo.setModelExecEnvName("Matlab-7.16");
        try {
            modelLibraryInfo = modelDelegate.createModelLibrary(modelLibraryInfo);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
        }
        assertNotNull(modelLibraryInfo.getId());
        assertNotNull(modelLibraryInfo.getUmgName());
    }    
    @Ignore
    public void findMappingInfoByLibraryNamName() throws SystemException, BusinessException {
        String nameArray[] = new String[5];
        for (int i = 0; i < 5; i++) {
            try {
                ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
                modelLibraryInfo.setDescription("Library Description" + i);
                modelLibraryInfo.setExecutionLanguage("DMMYLAB");
                modelLibraryInfo.setExecutionType("INTERNAL");
                modelLibraryInfo.setJarName("antlr-2.7.2" + i + ".jar");
                nameArray[i] = "antlr-2.7.2" + Math.random() * i + Math.random();
                modelLibraryInfo.setName(nameArray[i]);
                modelLibraryInfo.setChecksum("2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4");
                modelLibraryInfo.setEncodingType("SHA256");
                InputStream stream = ResourceLoader.getResource("classpath:/jarFile/antlr-2.7.2.jar");
                ModelArtifact modelLibJar = new ModelArtifact();
                modelLibJar.setData(stream);
                modelLibJar.setName("antlr-2.7.2.jar");
                modelLibraryInfo.setJar(modelLibJar);
                modelLibraryInfo.setModelExecEnvName("Matlab-7.16");
                modelLibraryInfo = modelDelegate.createModelLibrary(modelLibraryInfo);
                assertNotNull(modelLibraryInfo.getId());
                assertNotNull(modelLibraryInfo.getUmgName());
            } catch (BusinessException | SystemException e) {
                e.printStackTrace();
            }
        }

        try {
            List<ModelLibrary> ModelLibraryList = modelDelegate.findMappingInfoByLibraryNamName(nameArray[3]);
            assertNotNull(ModelLibraryList);
            for (ModelLibrary modelLibrary : ModelLibraryList) {
                assertNotNull(modelLibrary);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }

        try {
            List<ModelLibrary> ModelLibraryList = modelDelegate.findMappingInfoByLibraryNamName(null);
            assertNotNull(ModelLibraryList);
            for (ModelLibrary modelLibrary : ModelLibraryList) {
                assertNotNull(modelLibrary);
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        List<ModelLibraryInfo> resultList = modelDelegate.findAllLibraries();
        for (ModelLibraryInfo modelLibInfo : resultList) {
            modelDelegate.deleteModelLibrary(modelLibInfo.getId());
        }
    }

    @Test
    public void testGetModelArtifacts() throws FileNotFoundException, BusinessException, SystemException {
        ModelArtifact modelArtifact = buildModelArtifact("UMG-MATLAB-IO_1.XML", "UMG-MATLAB-IO_1.XML");
        modelArtifact.setName("testModelArtifact");
        List<ModelArtifact> modelArtifactList = new ArrayList<ModelArtifact>();
        modelArtifactList.add(modelArtifact);
        Model model = createModel("Model11x", "model 11x", "DOC11x", "iio file11", "text/xml", "sample", true);

        when(modelArtifactBO.fetchArtifacts(any(String.class), any(String.class), anyBoolean())).thenReturn(modelArtifactList);
        List<ModelArtifact> result = modelDelegate.getModelArtifacts(model.getId());
        assertNotNull(result);
        assertEquals("testModelArtifact", result.get(0).getName());
        getModelDAO().delete(model);
    }

    @Test
    @Ignore
    public void testgetAllModelNames() throws SystemException, BusinessException {
        createModels();
        List<String> modelList = modelDelegate.getAllModelNames();
        assertNotNull(modelList);
        assertEquals(2, modelList.size());
        List<Model> AllModelsList = modelBO.listAll();
        deleteModels(AllModelsList);
    }

    @Test
    public void testgetModelLibraryArtifacts() throws SystemException, BusinessException, FileNotFoundException {
        ModelLibrary modelLibrary = createModelLibrary("test50511", "testing11", "test50511", "antlr-2.7.2", "MATLAB",
 "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        ModelArtifact modelArtifact = buildModelArtifact("UMG-MATLAB-IO_1.XML", "UMG-MATLAB-IO_1.XML");
        modelArtifact.setName("testModelArtifact1");
        List<ModelArtifact> modelArtifactList = new ArrayList<ModelArtifact>();
        modelArtifactList.add(modelArtifact);
        when(modelArtifactBO.fetchArtifacts(any(String.class), any(String.class), anyBoolean())).thenReturn(modelArtifactList);
        List<ModelArtifact> resultModelArtifact = modelDelegate.getModelLibraryArtifacts(modelLibrary.getId());
        assertNotNull(resultModelArtifact);
        assertEquals("testModelArtifact1", resultModelArtifact.get(0).getName());
        getModelLibraryDAO().delete(modelLibrary);
    }

    @Test
    public void testGetAllLibraryNames() throws BusinessException, SystemException {
        ModelLibrary modelLibrary = createModelLibrary("test50512", "testing12", "test50512", "antlr-2.7.2", "MATLAB",
 "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        List<String> resultStringList = modelDelegate.getAllLibraryNames();
        assertNotNull(resultStringList);
        assertEquals("test50512", resultStringList.get(0));
        getModelLibraryDAO().delete(modelLibrary);
    }

    @Test
    public void testGetListOfDerivedModelLibraryNames() throws BusinessException, SystemException {
        ModelLibrary modelLibrary = createModelLibrary("test50513", "testing13", "test50513", "antlr-2.7.2", "MATLAB",
 "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        List<String> resultStringList = modelDelegate.getListOfDerivedModelLibraryNames(modelLibrary.getName());
        assertNotNull(resultStringList);
        assertEquals("test50513", resultStringList.get(0));
        getModelLibraryDAO().delete(modelLibrary);
    }

    @Test
    public void testFindByUmgName() throws SystemException, BusinessException {
        ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
        modelLibraryInfo.setDescription("Test Library Description");
        modelLibraryInfo.setExecutionLanguage("MATLAB");
        modelLibraryInfo.setExecutionType("INTERNAL");
        modelLibraryInfo.setJarName("antlr-2.7.2.jar");
        modelLibraryInfo.setName("TEST555");
        modelLibraryInfo.setChecksum("2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4");
        modelLibraryInfo.setEncodingType("SHA256");
        InputStream stream = ResourceLoader.getResource("classpath:/jarFile/antlr-2.7.2.jar");
        ModelArtifact modelLibJar = new ModelArtifact();
        modelLibJar.setData(stream);
        modelLibJar.setName("antlr-2.7.2.jar");
        modelLibraryInfo.setJar(modelLibJar);
        modelLibraryInfo.setModelExecEnvName("Matlab-7.16");
        modelLibraryInfo = modelDelegate.createModelLibrary(modelLibraryInfo);

        ModelLibraryInfo result = modelDelegate.findByUmgName(modelLibraryInfo.getUmgName());
        assertNotNull(result);
        assertEquals("TEST555", result.getName());
        modelDelegate.deleteModelLibrary(modelLibraryInfo.getId());
    }

    @Test
    public void testGetModelXml() throws SystemException, BusinessException {
        Model model = createModel("Model101x", "model 111x", "DOC111x", "iio file11", "text/xml", "sample", true);
        Model result = modelDelegate.getModelXML(model.getId());
        assertNotNull(result);
        assertEquals("Model101x", result.getName());
        getModelDAO().delete(model);
    }

    @Test
    public void testSearchModelLibraryByJarAndChecksum() throws BusinessException, SystemException {
        ModelLibrary modelLibrary = createModelLibrary("test50514", "testing14", "test50514", "antlr-2.7.2", "MATLAB",
 "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");

        List<ModelLibraryInfo> resultList = modelDelegate.searchModelLibraryByJarAndChecksum(modelLibrary.getJarName(),
                modelLibrary.getChecksum());
        assertNotNull(resultList);
        assertEquals("test50514", resultList.get(0).getName());
        getModelLibraryDAO().delete(modelLibrary);
    }

    @Test
    public void testGetAllVersionNamesForModel() throws SystemException, BusinessException {
        Model model = createModel("Model121x", "model 121x", "DOC121x", "iio file121", "text/xml", "sample", true);
        ModelLibrary modelLib = createModelLibrary("testVerStaChange1", "testingVerStaChange1", "testVerStaChange1",
                "antlr-2.7.2", "MATLAB", "INTERNAL", "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256",
                "Matlab-7.16");
        Mapping mapping = createMapping("TIDIOMAPPINGDeltest-121", model, "tenantCode5", "tid for model_121", "Sample MID Json");
        Version version = createVersion(mapping, modelLib);
        ModelMappingInfo result = modelDelegate.getAllVersionNamesForModel(model.getId());
        assertNotNull(result);
        assertEquals("TIDIOMAPPINGDeltest-121", result.getMappingNameList().get(0));
        assertEquals("verStat11-11.11", result.getVersionNameList().get(0));
        // deleting all the created data
        getVersionDAO().delete(version);
        getMappingDAO().delete(mapping);
        getModelDAO().delete(model);
        getModelLibraryDAO().delete(modelLib);
    }

    @Test
    public void testCreateModelLibraryWithoutValidn() {
        try {
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            modelLibraryInfo.setDescription("Test Library Description");
            modelLibraryInfo.setExecutionLanguage("MATLAB");
            modelLibraryInfo.setExecutionType("INTERNAL");
            modelLibraryInfo.setJarName("antlr-2.7.2.jar");
            modelLibraryInfo.setName("TEST" + Math.random());
            modelLibraryInfo.setChecksum("2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4");
            // modelLibraryInfo.setEncodingType("SHA256");
            InputStream stream = ResourceLoader.getResource("classpath:/jarFile/antlr-2.7.2.jar");
            ModelArtifact modelLibJar = new ModelArtifact();
            modelLibJar.setData(stream);
            modelLibJar.setName("antlr-2.7.2.jar");
            modelLibraryInfo.setJar(modelLibJar);
            modelLibraryInfo.setModelExecEnvName("Matlab-7.16");
            modelLibraryInfo = modelDelegate.createModelLibraryWithOutValidation(modelLibraryInfo);
            assertNotNull(modelLibraryInfo.getId());
            assertNotNull(modelLibraryInfo.getUmgName());
            modelDelegate.deleteModelLibrary(modelLibraryInfo.getId());
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }
    }

    @Test
    public void testStoreModelDefArtifacts() {
        ModelArtifact documentation;
        try {
            documentation = buildModelArtifact("UMG-MATLAB-IO_1.XML", "UMG-MATLAB-IO_1.XML");

        ModelArtifact xml = buildModelArtifact("UMG-MATLAB-IO_1.XML", "UMG-MATLAB-IO_1.XML");
            doNothing().when(modelArtifactBO).storeModelDefArtifacts(any(ModelArtifact[].class));
        ModelInfo modelInfo = buildModelInfo("AQMK", "AQMK", "sample", "ioDefnName", "text/xml", documentation, xml);
        modelDelegate.storeModelDefArtifacts(modelInfo);
        } catch (SystemException | BusinessException | FileNotFoundException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


}
