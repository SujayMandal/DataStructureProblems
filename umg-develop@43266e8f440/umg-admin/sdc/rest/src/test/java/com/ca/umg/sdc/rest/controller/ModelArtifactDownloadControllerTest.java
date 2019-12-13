package com.ca.umg.sdc.rest.controller;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.poi.util.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;
import com.ca.umg.business.model.info.ModelArtifact;

/**
 * @author basanaga Junit test class for ModelArtifactDownloadController
 *
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ModelArtifactDownloadControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Inject
    private ModelArtifactDownloadController modelArtifactDownloadController;

    @Inject
    private ModelDelegate modelDelegate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(modelArtifactDownloadController).build();
    }

    @Test
    public final void testDownloadManifest() {
        try {
            when(modelDelegate.getModelLibraryArtifacts("123")).thenReturn(getModelLibArtifacts());
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelLibManifest/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testdownloadManifestFail() {
        try {
            when(modelDelegate.getModelLibraryArtifacts("1")).thenReturn(getModelLibArtifacts());
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelLibManifest/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertEquals(mockResponse.getContentAsString(), "The required manifest doesn't exist in the repository");
        } catch (Exception exp) {
            System.out.println(exp);

        }
    }

    @Test
    public final void testDownloadModellibJar() {
        try {
            when(modelDelegate.getModelLibraryArtifacts("123")).thenReturn(getModelLibArtifacts());
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadLibraryJar/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDownloadModellibJarFail() {
        try {
            when(modelDelegate.getModelLibraryArtifacts("123")).thenReturn(getModelArtifacts());
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadLibraryJar/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDownloadDoc() {
        try {
            when(modelDelegate.getModelArtifacts("123")).thenReturn(getModelArtifacts());
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelDoc/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDownloadDocFail() {
        try {
            when(modelDelegate.getModelArtifacts("123")).thenReturn(null);
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelDoc/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDownloadModelXml() {
        try {
            when(modelDelegate.getModelXML("123")).thenReturn(getModel());
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelXml/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDownloadModelXmlFail() {
        try {
            when(modelDelegate.getModelXML("123")).thenReturn(null);
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelXml/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDownloadModelExcel() {
        try {
            when(modelDelegate.getModelXML("123")).thenReturn(getModel());
            when(modelDelegate.getModelExcel(getModel())).thenReturn("simpleexcel".getBytes());
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelExcel/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDownloadModelExcelFail1() {
        try {
            when(modelDelegate.getModelXML("123")).thenReturn(null);
            when(modelDelegate.getModelExcel(getModel())).thenReturn("simpleexcel".getBytes());
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelExcel/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDownloadModelExcelFail2() {
        try {
            when(modelDelegate.getModelXML("123")).thenReturn(null);
            when(modelDelegate.getModelExcel(getModel())).thenReturn(null);
            MvcResult mvcResult = mockMvc.perform(get("/modelDownload/downloadModelExcel/{modelId}", "123")).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertThat(mockResponse, notNullValue());
            assertThat(mockResponse.getContentAsString(), notNullValue());
        } catch (Exception exp) {
            System.out.println(exp);
            fail(exp.getMessage());
        }
    }

    private List<ModelArtifact> getModelLibArtifacts() throws SystemException, IOException {
        List<ModelArtifact> modelArtifacts = new ArrayList();
        ModelArtifact modelArtifact = new ModelArtifact();
        modelArtifact.setModelName("model1_mdljrtst");
        modelArtifact.setUmgName(
                "model1_mdljrtst-" + new SimpleDateFormat("MM-dd-yyyy-HH-mm").format(Calendar.getInstance().getTime()));
        InputStream xsd = new FileInputStream("./src/test/resources/testdata/Rmanifest.csv");
        modelArtifact.setData(xsd);
        modelArtifact.setDataArray(IOUtils.toByteArray(xsd));
        modelArtifact.setName("Rmanifest.csv");
        ModelArtifact modelArtifact1 = new ModelArtifact();


        modelArtifact1.setModelName("model1_mdljrtst");
        modelArtifact1.setUmgName(
                "model1_mdljrtst-" + new SimpleDateFormat("MM-dd-yyyy-HH-mm").format(Calendar.getInstance().getTime()));
        InputStream xsd1 = new FileInputStream("./src/test/resources/testdata/computeAQMKNPV_38.jar");
        modelArtifact1.setData(xsd1);
        modelArtifact1.setDataArray(IOUtils.toByteArray(xsd));
        modelArtifact1.setName("computeAQMKNPV_38.jar");

        modelArtifacts.add(modelArtifact1);
        modelArtifacts.add(modelArtifact);
        return modelArtifacts;
    }

    private List<ModelArtifact> getModelArtifacts() throws SystemException, IOException {
        List<ModelArtifact> modelArtifacts = new ArrayList();
        ModelArtifact modelArtifact = new ModelArtifact();
        modelArtifact.setModelName("model1_mdljrtst");
        modelArtifact.setUmgName(
                "model1_mdljrtst-" + new SimpleDateFormat("MM-dd-yyyy-HH-mm").format(Calendar.getInstance().getTime()));
        InputStream xsd = new FileInputStream("./src/test/resources/testdata/Rmanifest.csv");
        modelArtifact.setData(xsd);
        modelArtifact.setDataArray(IOUtils.toByteArray(xsd));
        modelArtifact.setName("Rmanifest.csv");
        modelArtifacts.add(modelArtifact);
        return modelArtifacts;
    }

    private Model getModel() throws SystemException, IOException {
        Model model = new Model();
        final String modelName = "test model";
        model.setName(modelName);
        model.setDescription("test model create for junit testing");
        model.setId("1");
        model.setUmgName("test model");
        model.setIoDefinitionName("test.xml");
        model.setIoDefExcelName("test.xlsx");

        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setModel(model);
        modelDefinition.setType("text/xml");
        modelDefinition.setIoDefinition("sample".getBytes());

        model.setModelDefinition(modelDefinition);

        return model;
    }

}
