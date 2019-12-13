package com.ca.umg.business.version.command.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mid.extraction.MidExtractor;
import com.ca.umg.business.mid.extraction.xml.MidXmlExtractor;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.delegate.ModelDelegateImpl;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.error.impl.ErrorControllerImpl;
import com.ca.umg.business.version.info.VersionInfo;

public class ValidateModelIOXmlTest {

    @InjectMocks
    private ValidateModelIOXml validateModelIOXml = new ValidateModelIOXml();

    @Mock
    public ModelDelegate modelDelegate = new ModelDelegateImpl();

    @Spy
    public ErrorController errorController = new ErrorControllerImpl();

    @Spy
    public MidExtractor midExtractor = new MidXmlExtractor();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() throws IOException, BusinessException, SystemException {
        VersionInfo versionInfo = createVersionInfo();
        validateModelIOXml.execute(versionInfo);
        Assert.assertTrue(errorController.getErrors().size() == BusinessConstants.NUMBER_ZERO);
        Assert.assertTrue(errorController.canContinueExecution());
    }

    @Test
    public void testExecuteWithExce() throws IOException, BusinessException, SystemException {
        VersionInfo versionInfo = createVersionInfo();
        validateModelIOXml.execute(versionInfo.getModelLibrary());
        Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
        Assert.assertTrue(!errorController.canContinueExecution());

    }

    @Test
    public void testExecuteWithGlobalExce() throws IOException, BusinessException, SystemException {
        VersionInfo versionInfo = createVersionInfo();
            Mockito.doThrow(Exception.class).when(modelDelegate).validateModelXml(Mockito.any(ByteArrayInputStream.class));
            validateModelIOXml.execute(versionInfo);
        Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
        Assert.assertTrue(!errorController.canContinueExecution());
    }

    @Test
    public void testExecuteWithOutId() throws IOException, BusinessException, SystemException {
        VersionInfo versionInfo = new VersionInfo();
        ModelInfo modelInfo = new ModelInfo();
        MappingInfo mappingInfo = new MappingInfo();
        mappingInfo.setModel(modelInfo);
        versionInfo.setMapping(mappingInfo);
        validateModelIOXml.execute(versionInfo);
        Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
        Assert.assertTrue(!errorController.canContinueExecution());
        VersionInfo versionInfo1 = new VersionInfo();
        validateModelIOXml.execute(versionInfo1);
        Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
        Assert.assertTrue(!errorController.canContinueExecution());

    }

    @Test
    public void testExecuteWithId() throws IOException, BusinessException, SystemException {
        VersionInfo versionInfo = createVersionInfoWithOutxml();
        validateModelIOXml.execute(versionInfo);
        Assert.assertTrue(errorController.getErrors().size() == BusinessConstants.NUMBER_ZERO);
        Assert.assertTrue(errorController.canContinueExecution());

    }

    @Test
    public void testRollback() throws IOException, BusinessException, SystemException {
        VersionInfo versionInfo = createVersionInfoWithOutxml();
        validateModelIOXml.rollback(versionInfo);
        Assert.assertTrue(Boolean.TRUE);

    }

    @Test
    public void testisCreate() throws IOException, BusinessException, SystemException {
        validateModelIOXml.isCreated();
        Assert.assertTrue(Boolean.TRUE);

    }

    private VersionInfo createVersionInfo() throws IOException {
    	InputStream data = null;
    	try {
    		VersionInfo versionInfo = new VersionInfo();
    		data = new FileInputStream(new File(
                    "./src/test/resources/com/ca/umg/business/mapping/dao/UMG_MATLAB_IO_SYMANTIC.xml"));

            ModelInfo modelInfo = new ModelInfo();
            modelInfo.setName("AQMK");
            modelInfo.setDescription("AQMK");
            modelInfo.setDocumentationName("UMG-MATLAB-IO_1.XML");
            modelInfo.setIoDefinitionName("UMG-MATLAB-IO_1.XML_IODEF");
            ModelArtifact modelArtifact = new ModelArtifact();
            modelArtifact.setName("AQMKName");
            modelArtifact.setDataArray(IOUtils.toByteArray(data));
            modelInfo.setXml(modelArtifact);
            MappingInfo mappingInfo = new MappingInfo();
            mappingInfo.setModel(modelInfo);
            versionInfo.setMapping(mappingInfo);
            return versionInfo;
		} finally {
			IOUtils.closeQuietly(data);
		}
        
    }

    private VersionInfo createVersionInfoWithOutxml() throws IOException {
        VersionInfo versionInfo = new VersionInfo();
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setId("AQMK");
        MappingInfo mappingInfo = new MappingInfo();
        mappingInfo.setModel(modelInfo);
        versionInfo.setMapping(mappingInfo);
        return versionInfo;
    }

}
