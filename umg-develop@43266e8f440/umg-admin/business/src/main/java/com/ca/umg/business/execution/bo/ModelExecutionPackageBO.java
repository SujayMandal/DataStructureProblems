/**
 * 
 */
package com.ca.umg.business.execution.bo;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.execution.entity.ModelExecutionPackage;
import com.ca.umg.business.model.info.ModelExecutionPackageInfo;

/**
 * @author nigampra
 * 
 */
public interface ModelExecutionPackageBO {

	ModelExecutionPackage createModelExecutionPackage(
			final ModelExecutionPackage modelExecutionPackage)
			throws SystemException, BusinessException;
	
	boolean packageNameExist(final String packageName,final String modelExecEnvName) throws SystemException,
			BusinessException;

	boolean isPackageAvailable(final ModelExecutionPackageInfo modelExecutionPackageInfo) throws SystemException,
			BusinessException;

	boolean isBasePackage(final String packageFolder) throws SystemException,
			BusinessException;

    List<String> getPackageFoldersByEnv(final String modelExecEnvName)
			throws SystemException, BusinessException;

    Page<ModelExecutionPackage> getAllModelExecPkg(String environmentName,
			ModelExecutionPackageInfo modelExePackageInfo)
			throws BusinessException, SystemException;

    Page<ModelExecutionPackage> getModelExecPkgByEnvAndPkgFolder(String environmentName,
			String packageFolder, ModelExecutionPackageInfo modelExePackageInfo)
			throws BusinessException, SystemException;

	ModelExecutionPackage findById(String id);
	
	String getExecutionPackageAbsolutePath(ModelExecutionPackageInfo modelExePackageInfo) throws SystemException;
	
	byte[] getModelExecutionPackage(ModelExecutionPackageInfo modelExePackageInfo) throws SystemException;
}
