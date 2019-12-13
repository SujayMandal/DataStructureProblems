package com.ca.umg.business.version.command.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
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
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.delegate.ModelDelegateImpl;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.error.impl.ErrorControllerImpl;
import com.ca.umg.business.version.info.VersionInfo;

import junit.framework.Assert;

/**
 * Test case for ValidateRManifestFile
 * 
 * @author basanaga
 * 
 * 
 */
public class ValidateRManifestFileTest {

    @InjectMocks
    private ValidateRManifestFile validateRManifestFile = new ValidateRManifestFile();

    @Mock
    public ModelDelegate modelDelegate = new ModelDelegateImpl();

    @Spy
    public ErrorController errorController = new ErrorControllerImpl();

    @Mock
    private MessageContainer messageContainer = new MessageContainer();

    private static final String R_MANIFEST_FILE__WITH_ERRORS = "R-ManifestWithWrongHierarchy.csv";
    private static final String R_MANIFEST_CORRECT_FILE = "R-Manifest-correct.csv";



    @Before
    public void setUp() {
    	  Properties properties = new Properties();
          properties.put(RequestContext.TENANT_CODE, "localhost");
          new RequestContext(properties);
          MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteWithErrors() throws BusinessException, SystemException, IOException {
        VersionInfo versionInfo = createVersionInfo(R_MANIFEST_FILE__WITH_ERRORS);
        validateRManifestFile.execute(versionInfo);
        Assert.assertTrue(errorController.getErrors().size() > 0);
    }

    @Test
    public void testExecute() throws BusinessException, SystemException, IOException {
        VersionInfo versionInfo = createVersionInfo(R_MANIFEST_CORRECT_FILE);
        validateRManifestFile.execute(versionInfo);
        Assert.assertTrue(versionInfo.getModelLibrary().getSupportPackages().size() > 1);
        Assert.assertTrue(errorController.getErrors().size() == 0);
    }

    @Test
    public void testExecuteWithException() throws BusinessException, SystemException, IOException {
        VersionInfo versionInfo = createVersionInfo(R_MANIFEST_CORRECT_FILE);
        validateRManifestFile.execute(versionInfo.getModelLibrary());
        Assert.assertFalse(errorController.canContinueExecution());

    }

    @Test
    public void testExecuteWithGlobalException() throws BusinessException, SystemException, IOException {
        Mockito.doThrow(new RuntimeException()).when(modelDelegate)
                .getModelAddonPackages(Mockito.any(String.class), Mockito.anyList(), Mockito.any(String.class),Mockito.anyList());
        validateRManifestFile.execute(createVersionInfo(R_MANIFEST_CORRECT_FILE));
        Assert.assertFalse(errorController.canContinueExecution());

    }

    private VersionInfo createVersionInfo(String manifestFileName) throws IOException {
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
        modelLibraryInfo.setManifestFile(buildArtifacts(manifestFileName));
        versionInfo.setModelLibrary(modelLibraryInfo);

        return versionInfo;
    }

    private ModelArtifact buildArtifacts(String manifestFileName) throws IOException {
    	InputStream inputStream = null;
    	 ModelArtifact modelArtifact = null;
    	try {
    		modelArtifact = new ModelArtifact();
    	     inputStream = ConvertExceltoXmlTest.class.getClassLoader().getResourceAsStream(
    	                manifestFileName);
    	        modelArtifact.setDataArray(IOUtils.toByteArray(inputStream));
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
       
        return modelArtifact;
    }

    @Test
    public void testRollback() throws BusinessException, SystemException, IOException {
        validateRManifestFile.rollback(createVersionInfo(R_MANIFEST_CORRECT_FILE));

    }

    @Test
    public void testIsCreated() throws BusinessException, SystemException, IOException {
        Assert.assertTrue(validateRManifestFile.isCreated());

    }

}
