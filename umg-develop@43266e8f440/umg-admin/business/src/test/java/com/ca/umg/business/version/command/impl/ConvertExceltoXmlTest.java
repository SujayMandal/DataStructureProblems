package com.ca.umg.business.version.command.impl;

import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.MessageContainer;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.error.impl.ErrorControllerImpl;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.plugin.commons.excel.xmlconverter.ExceltoXmlConverter;
import com.ca.umg.plugin.commons.excel.xmlconverter.MatlabModelExcelReader;
import com.ca.umg.plugin.commons.excel.xmlconverter.ModelExcelReader;

import junit.framework.Assert;

public class ConvertExceltoXmlTest {

    @InjectMocks
    private ConvertExceltoXml convertExceltoXml = new ConvertExceltoXml();

    @Mock
    public ModelExcelReader excelReader = new MatlabModelExcelReader();

    @Mock
    public ExceltoXmlConverter exceltoXmlConverter = new ExceltoXmlConverter();

    private final String inputExcelFile_sucess = "Matlab_HAMPV6_Ver_0.2_Success.xlsx";

    @Spy
    public ErrorController errorController = new ErrorControllerImpl();

    @Mock
    private MessageContainer messageContainer = new MessageContainer();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() throws BusinessException, SystemException, IOException {
        VersionInfo versionInfo = createVersionInfo();

        doNothing().when(exceltoXmlConverter).validateExcel(versionInfo.getMapping().getModel().getExcel().getDataArray(),
                versionInfo.getMapping().getModel().getExcel().getDataArray());
        Mockito.when(
                exceltoXmlConverter.excelConvertToXml(versionInfo.getMapping().getModel().getExcel().getDataArray(),
                        new HashMap<String, Object>(), "R", ModelType.ONLINE)).thenReturn(
                versionInfo.getMapping().getModel().getExcel().getDataArray());
        convertExceltoXml.execute(versionInfo);
        Assert.assertTrue(errorController.getErrors().size() == 0);
    }


    private VersionInfo createVersionInfo() throws IOException {
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
        MappingInfo mappingInfo = new MappingInfo();
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setExcel(buildArtifacts());
        modelInfo.setIoDefExcelName("Matlab_HAMPV6_Ver_0.2_Success.xlsx");
        mappingInfo.setModel(modelInfo);
        versionInfo.setMapping(mappingInfo);
        return versionInfo;
    }

    private ModelArtifact buildArtifacts() throws IOException {
    	InputStream inputStream= null;
    	ModelArtifact modelArtifact = null;
    	try {
    		 modelArtifact = new ModelArtifact();
    	     inputStream = ConvertExceltoXmlTest.class.getClassLoader().getResourceAsStream(
    	                inputExcelFile_sucess);
    	            modelArtifact.setDataArray(IOUtils.toByteArray(inputStream));
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
       
        return modelArtifact;
    }

    @Test
    public void tesRollback() throws BusinessException, SystemException, IOException {
        convertExceltoXml.rollback(createVersionInfo());
    }

    @Test
    public void tesIsCreated() throws BusinessException, SystemException, IOException {
        convertExceltoXml.isCreated();
    }

}
