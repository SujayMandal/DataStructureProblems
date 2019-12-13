/**
 * 
 */
package com.ca.umg.business.execution.bo;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.execution.dao.ModelExecutionPackageDAO;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.execution.entity.ModelExecutionPackage;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.model.info.ModelExecutionPackageInfo;

/**
 * @author nigampra
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class ModelExecutionPackageBOImplTest {
	
	@InjectMocks
    private ModelExecutionPackageBO mepBO = new ModelExecutionPackageBOImpl();
	
	@Mock
	private UmgFileProxy umgFileProxy;

	@Mock
	private SystemParameterProvider systemParameterProvider;
	
	@Mock
    private ModelExecutionPackageDAO executionPackageDAO;
	
	private static ModelExecutionEnvironment rEnv;
	private static ModelExecutionEnvironmentInfo rEnvInfo;
	
	private ModelExecutionPackage mep1;
	private ModelExecutionPackage mep2;
	private ModelExecutionPackageInfo mepi1;

	@Before
    public void setup() {
        initMocks(this);
        rEnv = buildModelExecutionEnvironment("R", "3.1.2");
        rEnvInfo = buildModelExecutionEnvironmentInfo("R", "3.1.2");
        mep1 = buildModelExecutionPackage(rEnv, "ABCanalysis", "1.0.1");
        mep2 = buildModelExecutionPackage(rEnv, "glmnet", "1.9-8");
        mepi1 = buildModelExecutionPackageInfo(rEnvInfo, "ABCanalysis", "1.0.1");
    }
	
	@Test
	public void tesCreateModelExecutionPackage() throws SystemException, BusinessException{
		when(executionPackageDAO.saveAndFlush(mep1)).thenReturn(mep1);
		ModelExecutionPackage resultMEP = mepBO.createModelExecutionPackage(mep1);
		Assert.assertNotNull(resultMEP);
	    Assert.assertEquals("ABCanalysis", resultMEP.getPackageFolder());
	    Assert.assertEquals("1.0.1", resultMEP.getPackageVersion());
	}
	
	@Test
	public void testIsPackageAvailable() throws SystemException, BusinessException{
        when(
                executionPackageDAO.findByPackageFolderAndPackageVersionAndExecEnvAndModelExecEnvName("glmnet", "1.9-8",
                        rEnv.getExecutionEnvironment(), rEnv.getName())).thenReturn(mep2);
        boolean exist = mepBO.isPackageAvailable(mepi1);
		Assert.assertTrue(exist);
	}
	
	@Test
	public void testIsBasePackage() throws SystemException, BusinessException{
		List<ModelExecutionPackage> lmep = new ArrayList<>();
		lmep.add(mep1);
		when(executionPackageDAO.findByPackageFolderAndPackageType("glmnet",BusinessConstants.BASE_PACKAGE_TYPE)).thenReturn(lmep);
		boolean isBasePkg = mepBO.isBasePackage("glmnet");
		Assert.assertTrue(isBasePkg);
	}
	
	@Test
	public void testGetPackageFoldersByEnv() throws SystemException, BusinessException{
		List<String> pkgFolderNames = new ArrayList<>();
		pkgFolderNames.add("ABCanalysis");
		pkgFolderNames.add("glmnet");
        when(executionPackageDAO.findAllPackageFoldersByModelExecEnvName(rEnv.getName(), BusinessConstants.ADDON_PACKAGE_TYPE))
                .thenReturn(pkgFolderNames);
        List<String> pkgList = mepBO.getPackageFoldersByEnv(rEnv.getName());
		Assert.assertEquals(2, pkgList.size());
	}
	
	@Test
	public void testGetAllModelExecPkg() throws SystemException, BusinessException{
		Pageable pageRequest = getPagingInformation(mepi1, mepi1.getSortColumn());
		when(executionPackageDAO.findAll( null, pageRequest)).thenReturn(null);
        Page<ModelExecutionPackage> pmep = mepBO.getAllModelExecPkg(rEnv.getName(), mepi1);
		Assert.assertNull(pmep);
	}
	
	@Test
	public void testGetModelExecPkgByEnvAndPkgFolder() throws SystemException, BusinessException{
		Pageable pageRequest = getPagingInformation(mepi1, mepi1.getSortColumn());
		when(executionPackageDAO.findAll( null, pageRequest)).thenReturn(null);
        Page<ModelExecutionPackage> pmep = mepBO.getModelExecPkgByEnvAndPkgFolder(rEnv.getName(), "ABCanalysis", mepi1);
		Assert.assertNull(pmep);
	}
	
	@Test
	public void testGetExecutionPackageAbsolutePath() throws SystemException{
		when(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)).thenReturn("abc");
		when(umgFileProxy.getSanPath("abc")).thenReturn(this.getClass().getResource("/r_support_packages").getPath());
		String packagePath = mepBO.getExecutionPackageAbsolutePath(mepi1);
		Assert.assertTrue(packagePath.indexOf(BusinessConstants.SUPPORT_PACKAGE_PARENT_FOLDER) != -1);
		Assert.assertTrue(packagePath.indexOf("R") != -1);
		Assert.assertTrue(packagePath.indexOf(mepi1.getPackageName()) != -1);
	}
	
	@Test
	public void testGetModelExecutionPackage() throws SystemException{
		when(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)).thenReturn("abc");
		when(umgFileProxy.getSanPath("abc")).thenReturn(this.getClass().getResource("/r_support_packages").getPath());
		byte[] packageContent = mepBO.getModelExecutionPackage(mepi1);
		Assert.assertNotNull(packageContent);
	}
	
	private ModelExecutionPackageInfo buildModelExecutionPackageInfo(ModelExecutionEnvironmentInfo modelExecutionEnvironment, String packageFolder,String packageVersion){
		ModelExecutionPackageInfo mep = new ModelExecutionPackageInfo();
		mep.setCompiledOs("LINUX");
		mep.setModelExecEnvName(modelExecutionEnvironment.getName());
		mep.setPackageFolder(packageFolder);
		mep.setPackageName(packageFolder+"_"+packageVersion+".tar.gz");
		mep.setPackageType(BusinessConstants.ADDON_PACKAGE_TYPE);
		mep.setPackageVersion(packageVersion);
		mep.setSortColumn("createdDate");
		mep.setPageSize(50);
		mep.setPage(1);
		return mep;
	}
	
	private ModelExecutionPackage buildModelExecutionPackage(ModelExecutionEnvironment modelExecutionEnvironment, String packageFolder,String packageVersion){
		ModelExecutionPackage mep = new ModelExecutionPackage();
		mep.setCompiledOs("LINUX");
        mep.setModelExecEnvName(modelExecutionEnvironment.getName());
		mep.setPackageFolder(packageFolder);
		mep.setPackageName(packageFolder+"_"+packageVersion+".tar.gz");
		mep.setPackageType(BusinessConstants.ADDON_PACKAGE_TYPE);
		mep.setPackageVersion(packageVersion);
		return mep;
	}
	
	private ModelExecutionEnvironment buildModelExecutionEnvironment(String executionEnvironment, String environmentVersion){
		ModelExecutionEnvironment mee = new ModelExecutionEnvironment();
		mee.setExecutionEnvironment(executionEnvironment);
		mee.setEnvironmentVersion(environmentVersion);
		mee.setName(executionEnvironment+"-"+environmentVersion);
		return mee;
	}
	
	private ModelExecutionEnvironmentInfo buildModelExecutionEnvironmentInfo(String executionEnvironment, String environmentVersion){
		ModelExecutionEnvironmentInfo mee = new ModelExecutionEnvironmentInfo();
		mee.setExecutionEnvironment(executionEnvironment);
		mee.setEnvironmentVersion(environmentVersion);
		mee.setName(executionEnvironment+"-"+environmentVersion);
		return mee;
	}
	
	private Pageable getPagingInformation(PagingInfo pagingInfo, String sortColumn) {
        Direction direction = pagingInfo.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC;
        String newSortColumn = sortColumn;
        Order[] sortOrders = null;
        Order order = null;
        Order supportPackageDate = null;
        if (StringUtils.isBlank(newSortColumn)) {
            supportPackageDate = new Order(Sort.Direction.ASC, "createdDate");
            sortOrders = new Order[] { supportPackageDate };
        } else {
            order = new Order(direction, newSortColumn).ignoreCase();
            sortOrders = new Order[] { order };
        }
        Sort sort = new Sort(sortOrders);
        return new PageRequest(pagingInfo.getPage() == 0 ? 0 : pagingInfo.getPage() - 1, pagingInfo.getPageSize(), sort);
    }


}
