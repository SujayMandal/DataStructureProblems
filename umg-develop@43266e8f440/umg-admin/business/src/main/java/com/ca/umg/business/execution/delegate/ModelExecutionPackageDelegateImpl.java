/**
 * 
 */
package com.ca.umg.business.execution.delegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.bo.ModelExecutionEnvironmentBO;
import com.ca.umg.business.execution.bo.ModelExecutionPackageBO;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.execution.entity.ModelExecutionPackage;
import com.ca.umg.business.model.bo.ModelArtifactBO;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.model.info.ModelExecutionPackageInfo;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.util.ZipUtil;

/**
 * @author nigampra
 * 
 */

@Component
public class ModelExecutionPackageDelegateImpl extends AbstractDelegate implements ModelExecutionPackageDelegate {

    @Inject
    private ModelExecutionPackageBO executionPackageBO;

    @Inject
    private ModelExecutionEnvironmentBO executionEnvironmentBO;

    @Inject
    private ModelArtifactBO modelArtifactBO;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.execution.delegate.ModelExecutionPackageDelegate# createModelExecutionPackage
     * (com.ca.umg.business.model.info.ModelExecutionPackageInfo)
     */
    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public ModelExecutionPackageInfo createModelExecutionPackage(ModelExecutionPackageInfo modelExecutionPackageInfo)
            throws SystemException, BusinessException {

        String executionEnv = null;
        String envVersion = null;

        if (modelExecutionPackageInfo.getModelExecEnvName() != null) {
            executionEnv = modelExecutionPackageInfo.getModelExecEnvName().split(BusinessConstants.HYPHEN)[0];
            envVersion = modelExecutionPackageInfo.getModelExecEnvName().split(BusinessConstants.HYPHEN)[1];
        }
        ModelExecutionEnvironment modelExecutionEnvironment = executionEnvironmentBO.getModelExecutionEnvironment(executionEnv,
                envVersion);

        if (modelExecutionEnvironment == null) {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000001, new Object[] { "Environment not found." });
        }
        modelExecutionPackageInfo.setExecEnv(modelExecutionPackageInfo.getPackageName().endsWith(".zip")?SystemConstants.WINDOWS_OS:SystemConstants.LINUX_OS);

        ModelExecutionPackage modelExecutionPackage = convert(modelExecutionPackageInfo, ModelExecutionPackage.class);

        modelExecutionPackage.setModelExecEnvName(modelExecutionEnvironment.getName());

        if (modelExecutionPackageInfo.getExecutionPackage() != null && modelExecutionPackage != null) {

            validatemodelExecPackage(modelExecutionPackageInfo);
            
            if (!isPackageAvailable(modelExecutionPackageInfo)) {            	
                modelExecutionPackage = executionPackageBO.createModelExecutionPackage(modelExecutionPackage);
                modelExecutionPackageInfo.getExecutionPackage().setModelName(modelExecutionPackageInfo.getPackageFolder());
                modelArtifactBO.storeSupportPackage(modelExecutionPackageInfo.getExecutionPackage(), executionEnv, envVersion,
                        modelExecutionPackageInfo.getExecEnv());
            } else {
                BusinessException.newBusinessException(BusinessExceptionCodes.BSE000001,
                        new Object[] { "Package with same version already exists." });
            }
        }
        return convert(executionPackageBO.createModelExecutionPackage(modelExecutionPackage), ModelExecutionPackageInfo.class);
    }

	private void validatemodelExecPackage(ModelExecutionPackageInfo modelExecutionPackageInfo)
			throws SystemException, BusinessException {
		if (isBasePackage(modelExecutionPackageInfo.getPackageFolder())) {
		    BusinessException.newBusinessException(BusinessExceptionCodes.BSE000001,
		            new Object[] { "Can't upload base package." });
		}
		
		if(packageNameExist(modelExecutionPackageInfo.getPackageName(),modelExecutionPackageInfo.getModelExecEnvName())){
		    BusinessException.newBusinessException(BusinessExceptionCodes.BSE000001,
		            new Object[] { "Package with same name already exist." });
		}
	}

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getSupportLibAdd())")
    public ModelExecutionPackageInfo buildModelExecutionPackageInfo(String executionEnvironment,
            ModelArtifact executionPackage) throws SystemException, BusinessException {
        ModelExecutionPackageInfo modelExecutionPackageInfo = new ModelExecutionPackageInfo();

        ModelExecutionEnvironment executionEnv = executionEnvironmentBO.getModelExecutionEnvironment(executionEnvironment,
                executionEnvironmentBO.getActiveRVersion(executionEnvironment));
        ModelExecutionEnvironmentInfo executionEnvInfo = convert(executionEnv, ModelExecutionEnvironmentInfo.class);
        modelExecutionPackageInfo.setModelExecEnvName(executionEnvInfo.getName());

        Properties tarDescription;
        if (executionPackage.getName().endsWith(".zip")) {
            tarDescription = ZipUtil.getZipDescription(executionPackage.getData());
            modelExecutionPackageInfo.setExecEnv(SystemConstants.WINDOWS_OS);
        } else {
            tarDescription = ZipUtil.getTarDescription(executionPackage.getData());
            modelExecutionPackageInfo.setExecEnv(SystemConstants.LINUX_OS);
        }        

        if (tarDescription.get("Package") != null) {
            modelExecutionPackageInfo.setPackageFolder((String) tarDescription.get("Package"));
        } else {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000656, new Object[] { "Package" });
        }

        if (tarDescription.get("Version") != null) {
            modelExecutionPackageInfo.setPackageVersion((String) tarDescription.get("Version"));
        } else {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000656, new Object[] { "Version" });
        }

        modelExecutionPackageInfo.setPackageName(executionPackage.getName());
        modelExecutionPackageInfo.setPackageType(BusinessConstants.ADDON_PACKAGE_TYPE);
        modelExecutionPackageInfo.setCompiledOs("Linux");
        modelExecutionPackageInfo.setExecutionPackage(executionPackage);

        return modelExecutionPackageInfo;
    }
    
    @Override
    public boolean packageNameExist(String packageName,String modelExecEnvName) throws SystemException, BusinessException {
        return executionPackageBO.packageNameExist(packageName,modelExecEnvName);
    }

    @Override
    public boolean isPackageAvailable(final ModelExecutionPackageInfo modelExecutionPackageInfo) throws SystemException, BusinessException {
        return executionPackageBO.isPackageAvailable(modelExecutionPackageInfo);
    }

    @Override
    public boolean isBasePackage(String packageFolder) throws SystemException, BusinessException {
        return executionPackageBO.isBasePackage(packageFolder);
    }

    @Override
    public List<String> getPackageFoldersByEnvironment(String modelExecEnvId) throws SystemException, BusinessException {
        return executionPackageBO
                .getPackageFoldersByEnv(executionEnvironmentBO.getModelExecutionEnvByName(modelExecEnvId).getName());

    }

    @Override
    @Transactional
    public PageRecord<ModelExecutionPackageInfo> getModelExecPkgByEnvAndName(String modelExeEnvName, String packageFolder,
            ModelExecutionPackageInfo modelPackageInfo) throws BusinessException, SystemException {
        Page<ModelExecutionPackage> page = null;

        if (packageFolder.equalsIgnoreCase("ALL")) {
            page = executionPackageBO.getAllModelExecPkg(modelExeEnvName, modelPackageInfo);
        } else {
            page = executionPackageBO.getModelExecPkgByEnvAndPkgFolder(modelExeEnvName, packageFolder, modelPackageInfo);
        }
        List<ModelExecutionPackageInfo> modelPackageInfos = getModelExcecutionPackageInfos(page);
        return preparePageRecord(page, modelPackageInfos);
    }

    private List<ModelExecutionPackageInfo> getModelExcecutionPackageInfos(Page<ModelExecutionPackage> page) {
        List<ModelExecutionPackageInfo> modelPackageInfos = convertToList(page.getContent(), ModelExecutionPackageInfo.class);
        List<ModelExecutionPackageInfo> modelExecuPkgList = null;
        if (CollectionUtils.isNotEmpty(modelPackageInfos)) {
            modelExecuPkgList = new ArrayList<>();
            for (ModelExecutionPackageInfo modelExecutionPackageInfo : modelPackageInfos) {
                modelExecutionPackageInfo.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(modelExecutionPackageInfo
                        .getCreatedDate().getMillis(), null));
                modelExecutionPackageInfo.setLastModifiedDateTime(AdminUtil.getDateFormatMillisForEst(modelExecutionPackageInfo
                        .getLastModifiedDate().getMillis(), null));
                modelExecuPkgList.add(modelExecutionPackageInfo);
            }
        }
        return modelExecuPkgList;
    }

    private <T, E> PageRecord<T> preparePageRecord(Page<E> page, List<T> infos) {
        PageRecord<T> pageRecord = new PageRecord<>();
        pageRecord.setContent(infos);
        pageRecord.setNumber(page.getNumber());
        pageRecord.setNumberOfElements(page.getNumberOfElements());
        pageRecord.setSize(page.getSize());
        pageRecord.setTotalElements(page.getTotalElements());
        pageRecord.setTotalPages(page.getTotalPages());
        return pageRecord;
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getSupportLibManageDownloadPackages())")
    public ModelExecutionPackageInfo getModelExecutionPackageInfo(String packageId) throws SystemException, BusinessException {
        return convert(executionPackageBO.findById(packageId), ModelExecutionPackageInfo.class);
    }

    @Override
    public byte[] getModelExecutionPackage(ModelExecutionPackageInfo modelExecutionPackageInfo) throws SystemException {
        return executionPackageBO.getModelExecutionPackage(modelExecutionPackageInfo);
    }



}
