package com.ca.umg.business.model.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.AbstractModelTest;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.util.ResourceLoader;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class ModelBOImplTest extends AbstractModelTest {

    @Inject
    private ModelBO modelBO;

    @Before
    public void setup() {
        getLocalhostTenantContext();
    }

    @Test
    public void testCreateModel() {
        try {
            Model model = buildModel("createModel1", "Modle1", "docName", "ioName", "text/xml", "sampleIo", true);
            model = modelBO.createModel(model);
            assertNotNull(model);
            modelBO.deleteModel(model);
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }
    }

    @Test
    public void testCreateModelLibrary() {
        try {
            ModelLibrary modelLib = buildModelLibrary("test501", "testing", "test501", "antlr-2.7.2", "MATLAB", "INTERNAL",
                    "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
            modelLib = modelBO.createModelLibrary(modelLib);
            assertNotNull(modelLib);
            modelBO.deleteModelLibrary(modelLib);
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetModelDetails() {
        try {
            Model model = createModel("Model1a1", "Modle11", "docName", "ioName", "text/xml", "sampleIo", true);
            assertNotNull(model);

            Model savedModel = modelBO.getModelDetails(model.getId());
            assertNotNull(savedModel);
            assertEquals(model, savedModel);
            modelBO.deleteModel(model);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetModelLibraryDetails() {
        try {
            ModelLibrary modelLib = createModelLibrary("test502", "testing", "test502", "antlr-2.7.2", "MATLAB", "INTERNAL",
                    "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
            assertNotNull(modelLib);
            ModelLibrary savedModelLib = modelBO.findModelLibrary(modelLib.getId());
            assertNotNull(savedModelLib);
            assertEquals(modelLib, savedModelLib);
            modelBO.deleteModelLibrary(savedModelLib);

        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testDeleteModel() {
        try {
            Model model = createModel("Modelas2", "Modle1", "docName", "ioName", "text/xml", "sampleIo", true);
            assertNotNull(model);

            modelBO.deleteModel(model);

            model = modelBO.getModelDetails(model.getId());
            assertNull(model);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testDeleteModelLibrary() {
        try {
            ModelLibrary modelLib = createModelLibrary("test503", "testing", "test503", "antlr-2.7.2", "MATLAB", "INTERNAL",
                    "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
            assertNotNull(modelLib);
            modelBO.deleteModelLibrary(modelLib);
            modelLib = modelBO.findModelLibrary(modelLib.getId());
            assertNull(modelLib);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }
   
   
    @Test
    public void testFindAllModelLibraries() {
        try {
            createModelLibraryList();
            List<ModelLibrary> modelLib = modelBO.findAllModelLibraries();
            assertNotNull(modelLib);
            deleteModelLibrary(modelLib);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }


    @Test
    public void testFindByName() {
        ModelLibrary modelLib = createModelLibrary("test502", "testing", "test502", "antlr-2.7.2", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        try {
            List<ModelLibrary> modelLibraryList = modelBO.findMappingInfoByLibraryNamName("test502");
            assertNotNull(modelLibraryList);
            for (ModelLibrary modelLibrary : modelLibraryList) {
                assertNotNull(modelLibrary);
            }
            modelBO.deleteModelLibrary(modelLib);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetModelByName() {
        try {
            Model model = createModel("Model4x", "model 4x", "DOC4x", "iio file4", "text/xml", "sample4", true);
            Model resultModel = modelBO.getModelByName(model.getUmgName());
            assertNotNull(resultModel);
            assertEquals("Model4x", resultModel.getName());
            modelBO.deleteModel(model);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    @Ignore
    public void testGetAllModelNames() {
        try {        
        	List<Model> allModels = modelBO.listAll();
        	deleteModels(allModels);
            Model model = createModel("Model5x", "model 5x", "DOC5x", "iio file5", "text/xml", "sample5", true);
            List<String> resultList = modelBO.getAllModelNames();
            assertNotNull(resultList);
            assertEquals("Model5x", resultList.get(0));
            modelBO.deleteModel(model);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetAllLibraryNames() {
        try {
            ModelLibrary modelLib = createModelLibrary("test504", "testing", "test504", "antlr-2.7.2", "MATLAB", "INTERNAL",
                    "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
            assertNotNull(modelLib);
            List<String> libList = modelBO.getAllLibraryNames();
            assertNotNull(libList);
            assertEquals("test504", libList.get(0));
            modelBO.deleteModelLibrary(modelLib);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testFindByUmgName() {
        try {
            ModelLibrary modelLib = createModelLibrary("test505", "testing", "test505", "antlr-2.7.2", "MATLAB", "INTERNAL",
                    "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
            assertNotNull(modelLib);
            ModelLibrary resultmodelLibrary = modelBO.findByUmgName(modelLib.getUmgName());
            assertNotNull(resultmodelLibrary);
            assertEquals("test505", resultmodelLibrary.getName());
            modelBO.deleteModelLibrary(modelLib);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetListOfDerivedModelLibraryNames() {
        try {
            ModelLibrary modelLib = createModelLibrary("test506", "testing", "test506", "antlr-2.7.2", "MATLAB", "INTERNAL",
                    "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
            assertNotNull(modelLib);
            List<String> libList = modelBO.getListOfDerivedModelLibraryNames(modelLib.getName());
            assertNotNull(libList);
            assertEquals("test506", libList.get(0));
            modelBO.deleteModelLibrary(modelLib);
        } catch (BusinessException | SystemException exp) {
            fail(exp.getMessage());
        }
    }

    @Test(expected = BusinessException.class)
    public void testCheckJarAvailability() throws SystemException, BusinessException {
        try {
            createModelLibrary("test506", "testing", "test506", "antlr-2.7.2", "MATLAB", "INTERNAL",
                    "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
            InputStream inputStream = ResourceLoader.getResource("classpath:/jarFile/antlr-2.7.2.jar");
            byte[] testJar = FileCopyUtils.copyToByteArray(inputStream);
            modelBO.checkJarAvailability(testJar, "SHA256", "antlr-2.7.2");
        } catch (IOException exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testValidateCheckSum() {
        try {
            String checkSum = "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4";
            InputStream inputStream = ResourceLoader.getResource("classpath:/jarFile/antlr-2.7.2.jar");
            byte[] testJar = FileCopyUtils.copyToByteArray(inputStream);
            modelBO.validateCheckSum(testJar, checkSum, "SHA256");
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testValidateModelXml() {
        try {
            InputStream inputStream = ResourceLoader.getResource("com/ca/umg/business/model/bo/computeAQMKNPV.xml");
            modelBO.validateModelXml(inputStream);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
}