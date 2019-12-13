package com.ca.umg.business.version.command.impl;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Properties;

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
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.MessageContainer;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.delegate.ModelDelegateImpl;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.error.impl.ErrorControllerImpl;
import com.ca.umg.business.version.info.VersionInfo;

public class CreateModelLibraryTest {

    @InjectMocks
    private CreateModelLibrary createModelLibrary = new CreateModelLibrary();

    @Mock
    public ModelDelegate modelDelegate = new ModelDelegateImpl();

    @Spy
    public ErrorController errorController = new ErrorControllerImpl();

    @Mock
    private MessageContainer messageContainer = new MessageContainer();
    

    @Spy
    public RequestContext reqContext = null;


    @Before
    public void setUp() {
        Properties p = new Properties();
        p.setProperty("TENANT_CODE", "localhost");
        reqContext = new RequestContext(p);
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testExecute() throws BusinessException, SystemException {
        VersionInfo versionInfo = createVersionInfo();
        Mockito.when(modelDelegate.createModelLibraryWithOutValidation(Mockito.any(ModelLibraryInfo.class))).thenReturn(
                versionInfo.getModelLibrary());
        createModelLibrary.execute(versionInfo);
        Assert.assertTrue(errorController.getErrors().size() == BusinessConstants.NUMBER_ZERO);
        Assert.assertTrue(errorController.canContinueExecution());

    }


    @Test
    public void testExecuteWithException() throws SystemException {

        try {
            VersionInfo versionInfo = createVersionInfo();
            Mockito.when(modelDelegate.createModelLibraryWithOutValidation(Mockito.any(ModelLibraryInfo.class))).thenReturn(
                    versionInfo.getModelLibrary());
            createModelLibrary.execute(versionInfo.getModelLibrary());
            Assert.assertTrue(errorController.getErrors().size() >= BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(!errorController.canContinueExecution());


        } catch (BusinessException | SystemException e) {
            System.out.println(e);
            errorController.getErrors();

        }

    }

    @Test
    public void testExecuteWithId() {
        try {

            VersionInfo versionInfo = new VersionInfo();
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            versionInfo.setModelLibrary(modelLibraryInfo);
            createModelLibrary.execute(versionInfo);
            Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(!errorController.canContinueExecution());

        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

    }

    @Test
    public void testExecuteWithErrors() {
        try {
            VersionInfo versionInfo = new VersionInfo();
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            versionInfo.setModelLibrary(modelLibraryInfo);
            createModelLibrary.execute(versionInfo);
            Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(!errorController.canContinueExecution());
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

    }

    @Test
    public void testRollback() {
        try {
            VersionInfo versionInfo = new VersionInfo();
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            modelLibraryInfo.setId("czxvcxx");
            versionInfo.setModelLibrary(modelLibraryInfo);
            Mockito.doNothing().when(modelDelegate).deleteModelLibrary(modelLibraryInfo.getId());
            createModelLibrary.rollback(versionInfo);
            Assert.assertTrue(errorController.getErrors().size() == BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(errorController.canContinueExecution());
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

    }

    @Test
    public void testRollbackWithExce() {
        try {

            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            createModelLibrary.rollback(modelLibraryInfo);
            Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(!errorController.canContinueExecution());
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

    }

    @Test
    public void testIsCreate() {
        try {
            VersionInfo versionInfo = new VersionInfo();
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            versionInfo.setModelLibrary(modelLibraryInfo);
            createModelLibrary.isCreated();
            Assert.assertTrue(errorController.getErrors().size()  == BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(errorController.canContinueExecution());
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

    }


    private VersionInfo createVersionInfo() {
        VersionInfo versionInfo = new VersionInfo();
        ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
        modelLibraryInfo.setDescription("Test Library Description");
        modelLibraryInfo.setExecutionLanguage("MATLAB");
        modelLibraryInfo.setExecutionType("INTERNAL");
        modelLibraryInfo.setJarName("antlr-2.7.2.jar");
        modelLibraryInfo.setName("TEST" + Math.random());
        modelLibraryInfo.setChecksum("2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4");
        InputStream stream = this.getClass().getResourceAsStream("classpath:/jarFile/antlr-2.7.2.jar");
        ModelArtifact modelLibJar = new ModelArtifact();
        modelLibJar.setData(stream);
        modelLibJar.setName("antlr-2.7.2.jar");
        modelLibraryInfo.setJar(modelLibJar);
        versionInfo.setModelLibrary(modelLibraryInfo);
        return versionInfo;
    }

}
