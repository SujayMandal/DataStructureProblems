/**
 * 
 */
package com.ca.umg.business.execution.delegate;

import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.impl.ConfigurableMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.execution.bo.ModelExecutionEnvironmentBO;
import com.ca.umg.business.execution.bo.ModelExecutionPackageBO;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.model.bo.ModelArtifactBO;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelExecutionPackageInfo;

/**
 * @author nigampra
 *
 */
public class ModelExecutionPackageDelegateImplTest{
	
	@InjectMocks
	 private ModelExecutionPackageDelegateImpl mepDelegate;
	
	@Mock
	private ModelExecutionPackageBO executionPackageBO;
	
	@Spy
    ConfigurableMapper mapper = new UMGConfigurableMapper();

	@Mock
	private ModelExecutionEnvironmentBO executionEnvironmentBO;

	@Mock
	private ModelArtifactBO modelArtifactBO;
	
	@Mock
	ModelExecutionEnvironment executionEnv;
	 
	private static ModelExecutionPackageInfo mepiIN;
	 
	private final String rLib = "jarFile/ABCanalysis_1.0.1.tar.gz";
	 
	 ModelArtifact pkgArtifact = null;
	 
	 List<String> pkgNames = null;
	 
	 @Before
	 public void setup() {
	        MockitoAnnotations.initMocks(this);
	        initMocks();
	        pkgArtifact = getExecutionPackage();
	        pkgNames = new ArrayList<>();
	        pkgNames.add("ABCanalysis");
	        pkgNames.add("glmnet");
	        try {
				when(executionEnvironmentBO.getModelExecutionEnvironment("R", "3.1.2")).thenReturn(executionEnv);
            when(executionPackageBO.isPackageAvailable(mepiIN)).thenReturn(false);
				when(executionPackageBO.isBasePackage("ABCanalysis")).thenReturn(false);
				when(executionPackageBO.getPackageFoldersByEnv(null)).thenReturn(pkgNames);
			} catch (BusinessException | SystemException e) {
				e.printStackTrace();
			}
	 }
	 
	 private void initMocks(){
		 executionEnv = new ModelExecutionEnvironment();
		 executionEnv.setExecutionEnvironment("R");
		 executionEnv.setEnvironmentVersion("3.1.2");
        executionEnv.setName("R-3.1.2");
	 }
	 
	 @Test
	 public void testBuildModelExecutionPackageInfo() throws SystemException, BusinessException{
        ModelExecutionPackageInfo actualMepi = mepDelegate.buildModelExecutionPackageInfo("R", pkgArtifact);
		 Assert.assertNotNull(actualMepi);
		 Assert.assertEquals("R-3.1.2",actualMepi.getModelExecEnvName());
		 Assert.assertEquals("ABCanalysis",actualMepi.getPackageFolder());
		 Assert.assertEquals("1.0.1",actualMepi.getPackageVersion());
		 Assert.assertEquals("ABCanalysis_1.0.1.tar.gz",actualMepi.getPackageName());
		 Assert.assertEquals(BusinessConstants.ADDON_PACKAGE_TYPE,actualMepi.getPackageType());
	 }
	 
	 @Test
	 public void testIsPackageAvailable() throws SystemException, BusinessException{
        boolean actualResult = mepDelegate.isPackageAvailable(mepiIN);
		 Assert.assertFalse(actualResult);
	 }
	 
	 @Test
	 public void testIsBasePackage() throws SystemException, BusinessException{
		 boolean actualResult = mepDelegate.isBasePackage("ABCanalysis");
		 Assert.assertFalse(actualResult);
	 }
	 
    @Test
    public void testGetPackageFoldersByEnvironment() throws SystemException, BusinessException {
        Mockito.when(executionEnvironmentBO.getModelExecutionEnvByName("R-3.1.2")).thenReturn(executionEnv);
        List<String> actualList = mepDelegate.getPackageFoldersByEnvironment("R-3.1.2");
        Assert.assertEquals(0, actualList.size());
    }

	 @Test
	 public void testCreateModelExecutionPackage()throws SystemException, BusinessException{
        mepiIN = mepDelegate.buildModelExecutionPackageInfo("R", pkgArtifact);
		 ModelExecutionPackageInfo mepiOut = mepDelegate.createModelExecutionPackage(mepiIN);
		 Assert.assertNull(mepiOut);
	 }
	 
	 private ModelArtifact getExecutionPackage(){
		 InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(rLib);
		 ModelArtifact ma = new ModelArtifact();
		 ma.setData(inputStream);
		 ma.setModelName("ABCanalysis");
		 ma.setName("ABCanalysis_1.0.1.tar.gz");
		 return ma;
	 }
}
