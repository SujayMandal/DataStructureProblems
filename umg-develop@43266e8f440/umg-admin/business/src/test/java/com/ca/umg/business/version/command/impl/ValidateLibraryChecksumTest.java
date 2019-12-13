package com.ca.umg.business.version.command.impl;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.delegate.ModelDelegateImpl;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.error.impl.ErrorControllerImpl;
import com.ca.umg.business.version.info.VersionInfo;


public class ValidateLibraryChecksumTest {

    @InjectMocks
    private ValidateLibraryChecksum validateLibraryChecksum = new ValidateLibraryChecksum();

    @Mock
    public ModelDelegate modelDelegate = new ModelDelegateImpl();

    @Spy
    public ErrorController errorController = new ErrorControllerImpl();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteWithJar() {
        try {
            VersionInfo versionInfo = createVersionInfo();
            validateLibraryChecksum.execute(versionInfo);
            Assert.assertTrue(errorController.getErrors().size() == BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(errorController.canContinueExecution());
            validateLibraryChecksum.execute(versionInfo.getModelLibrary());
            Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(!errorController.canContinueExecution());

        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

    }

    @Test
    public void testExecuteWithId() {
        try {
            VersionInfo versionInfo = new VersionInfo();
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            modelLibraryInfo.setId("czxvcxx");
            versionInfo.setModelLibrary(modelLibraryInfo);
            validateLibraryChecksum.execute(versionInfo);
            Assert.assertTrue(errorController.getErrors().size() == BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(errorController.canContinueExecution());
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
            validateLibraryChecksum.execute(versionInfo);
            Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(!errorController.canContinueExecution());
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

    }

    @Test
    public void testExecuteWithException() {
        try {
            VersionInfo versionInfo = new VersionInfo();
            ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
            versionInfo.setModelLibrary(modelLibraryInfo);
            modelLibraryInfo.setChecksum("dcds");
            InputStream stream = this.getClass().getResourceAsStream("classpath:/jarFile/antlr-2.7.2.jar");
            ModelArtifact modelLibJar = new ModelArtifact();
            modelLibJar.setData(stream);
            modelLibJar.setName("antlr-2.7.2.jar");
            modelLibraryInfo.setJar(modelLibJar);
            versionInfo.setModelLibrary(modelLibraryInfo);
            validateLibraryChecksum.execute(versionInfo.getModelLibrary());
            Assert.assertTrue(errorController.getErrors().size() > BusinessConstants.NUMBER_ZERO);
            Assert.assertTrue(!errorController.canContinueExecution());
        } catch (BusinessException | SystemException exp) {
            exp.printStackTrace();
            fail(exp.getMessage());
        }

    }

    @Test
    public void testRollback() {
        VersionInfo versionInfo = createVersionInfo();
        try {
            validateLibraryChecksum.rollback(versionInfo);
            Assert.assertTrue(Boolean.TRUE);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testIsCreated() {
        try {
            validateLibraryChecksum.isCreated();
            Assert.assertTrue(Boolean.TRUE);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
            fail(e.getMessage());
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
