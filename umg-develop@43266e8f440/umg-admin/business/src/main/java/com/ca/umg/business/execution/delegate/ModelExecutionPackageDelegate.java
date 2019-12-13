/**
 * 
 */
package com.ca.umg.business.execution.delegate;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelExecutionPackageInfo;

/**
 * @author nigampra
 * 
 */
public interface ModelExecutionPackageDelegate {

    ModelExecutionPackageInfo createModelExecutionPackage(final ModelExecutionPackageInfo modelExecutionPackageInfo)
            throws SystemException, BusinessException;

    ModelExecutionPackageInfo buildModelExecutionPackageInfo(final String executionEnvironment,
            final ModelArtifact executionPackage) throws SystemException, BusinessException;
    
    boolean packageNameExist(final String packageName,final String modelExecEnvName) throws SystemException, BusinessException;

    boolean isPackageAvailable(final ModelExecutionPackageInfo modelExecutionPackageInfo) throws SystemException, BusinessException;

    boolean isBasePackage(final String packageFolder) throws SystemException, BusinessException;
    
    ModelExecutionPackageInfo getModelExecutionPackageInfo(final String packageId) throws SystemException, BusinessException;
    

    List<String> getPackageFoldersByEnvironment(final String modelExecEnvId) throws SystemException, BusinessException;

    public PageRecord<ModelExecutionPackageInfo> getModelExecPkgByEnvAndName(final String modelExecEnvId,
            final String packageFolder, final ModelExecutionPackageInfo modelExecutionPackageInfo) throws BusinessException,
            SystemException;
    
    byte[] getModelExecutionPackage(ModelExecutionPackageInfo modelExecutionPackageInfo) throws SystemException;
}
