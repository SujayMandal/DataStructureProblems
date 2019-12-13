/**
 * 
 */
package com.ca.umg.business.execution.bo;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.dao.ModelExecutionPackageDAO;
import com.ca.umg.business.execution.entity.ModelExecutionPackage;
import com.ca.umg.business.model.info.ModelExecutionPackageInfo;
import com.ca.umg.business.model.specification.ModelExecutionPackageSpecification;

/**
 * @author nigampra
 *
 */

@Service
public class ModelExecutionPackageBOImpl implements ModelExecutionPackageBO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelExecutionPackageBOImpl.class);
	
	@Inject
    private ModelExecutionPackageDAO executionPackageDAO;
	
	@Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private SystemParameterProvider systemParameterProvider;

	/* (non-Javadoc)
	 * @see com.ca.umg.business.execution.bo.ModelExecutionPackageBO#createModelExecutionPackage(com.ca.umg.business.execution.entity.ModelExecutionPackage)
	 */
	@Override
	public ModelExecutionPackage createModelExecutionPackage(
			ModelExecutionPackage modelExecutionPackage)
			throws SystemException, BusinessException {
        return executionPackageDAO.saveAndFlush(modelExecutionPackage);

	}
	
	@Override
	public boolean packageNameExist(String packageName,String modelExecEnvName) throws SystemException,
			BusinessException {
		return executionPackageDAO.findByPackageNameAndModelExecEnvName(packageName,modelExecEnvName) != null ? true : false; 
	}


	@Override
	public boolean isBasePackage(String packageFolder)
			throws SystemException, BusinessException {
		boolean isBasePkg = false;
		List<ModelExecutionPackage> modelExecutionPackages =  executionPackageDAO.findByPackageFolderAndPackageType(packageFolder,BusinessConstants.BASE_PACKAGE_TYPE);
		if(CollectionUtils.isNotEmpty(modelExecutionPackages)){
			isBasePkg = true;
		}
		return isBasePkg;
	}

    @Override
    public List<String> getPackageFoldersByEnv(String modelExecEnvName)
            throws SystemException, BusinessException {
        return executionPackageDAO.findAllPackageFoldersByModelExecEnvName(modelExecEnvName,
                BusinessConstants.ADDON_PACKAGE_TYPE);
    }

    @Override
    public Page<ModelExecutionPackage> getModelExecPkgByEnvAndPkgFolder(String environmentName,
            String packageFolder, ModelExecutionPackageInfo modelExePackageInfo) throws BusinessException, SystemException {
        Pageable pageRequest = getPagingInformation(modelExePackageInfo, modelExePackageInfo.getSortColumn());
        Specification<ModelExecutionPackage> withPackageFolder = ModelExecutionPackageSpecification
                .withPackageFolder(packageFolder);
        Specification<ModelExecutionPackage> withEnvironment = ModelExecutionPackageSpecification
                .withEnvironmentName(environmentName);
        Specification<ModelExecutionPackage> withPackageName = ModelExecutionPackageSpecification
                .withPackageName(modelExePackageInfo.getSearchString());
        Specification<ModelExecutionPackage> withCreatedBy = ModelExecutionPackageSpecification
                .withCreatedBy(modelExePackageInfo.getSearchString());
        return executionPackageDAO.findAll(where(withEnvironment).and(withPackageFolder).and(where(withPackageName).or(where(withCreatedBy))), pageRequest);

    }
    
    @Override
    public Page<ModelExecutionPackage> getAllModelExecPkg(String environmentName,
			ModelExecutionPackageInfo modelExePackageInfo)
			throws BusinessException, SystemException {
    	Pageable pageRequest = getPagingInformation(modelExePackageInfo, modelExePackageInfo.getSortColumn());
    	Specification<ModelExecutionPackage> withPackageType = ModelExecutionPackageSpecification
                .withPackageType(BusinessConstants.ADDON_PACKAGE_TYPE);
    	Specification<ModelExecutionPackage> withEnvironment = ModelExecutionPackageSpecification
                .withEnvironmentName(environmentName);
        Specification<ModelExecutionPackage> withPackageName = ModelExecutionPackageSpecification
                .withPackageName(modelExePackageInfo.getSearchString());
        Specification<ModelExecutionPackage> withCreatedBy = ModelExecutionPackageSpecification
                .withCreatedBy(modelExePackageInfo.getSearchString());
        return executionPackageDAO.findAll(where(withEnvironment).and(withPackageType).and(where(withPackageName).or(where(withCreatedBy))), pageRequest);
	}


    /**
     * Prepares paging request
     * 
     * @param pagingInfo
     * @param sortColumn
     * @return
     */
    private Pageable getPagingInformation(PagingInfo pagingInfo, String sortColumn) {
        Direction direction = pagingInfo.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC;
        String newSortColumn = sortColumn;
        Order[] sortOrders = null;
        Order order = null;
        Order supportPackageDate = null;
        if (StringUtils.isBlank(newSortColumn)) {
            supportPackageDate = new Order(Sort.Direction.ASC, BusinessConstants.CREATED_DATE);
            sortOrders = new Order[] { supportPackageDate };
        } else {
            order = new Order(direction, newSortColumn).ignoreCase();
            sortOrders = new Order[] { order };
        }
        Sort sort = new Sort(sortOrders);
        return new PageRequest(pagingInfo.getPage() == 0 ? 0 : pagingInfo.getPage() - 1, pagingInfo.getPageSize(), sort);
    }

    @Override
    public ModelExecutionPackage findById(String id) {
        return executionPackageDAO.findById(id);
    }

	@Override
	public String getExecutionPackageAbsolutePath(
			ModelExecutionPackageInfo modelExePackageInfo)
			throws SystemException {
		String sanBasePath = umgFileProxy.getSanPath(systemParameterProvider
				.getParameter(SystemConstants.SAN_BASE));
		StringBuffer absolutePath = new StringBuffer(sanBasePath);
		absolutePath
				.append(File.separatorChar)
				.append(BusinessConstants.SUPPORT_PACKAGE_PARENT_FOLDER)
				.append(File.separatorChar)
				.append(modelExePackageInfo.getModelExecEnvName().split(
						BusinessConstants.HYPHEN)[0])
				.append(File.separatorChar)
				.append(modelExePackageInfo.getModelExecEnvName().split(
						BusinessConstants.HYPHEN)[1])
				.append(File.separatorChar)
				.append(modelExePackageInfo.getExecEnv())
				.append(File.separatorChar)
				.append(modelExePackageInfo.getPackageFolder())
				.append(File.separatorChar)
				.append(modelExePackageInfo.getPackageName());
		return absolutePath.toString();
	}

	@Override
	public byte[] getModelExecutionPackage(
			ModelExecutionPackageInfo modelExePackageInfo)
			throws SystemException {
		InputStream inputStream = null;
		byte[] packageContent = null;
		String executionPackagePath = getExecutionPackageAbsolutePath(modelExePackageInfo);
		File executionPackage = new File(executionPackagePath);
		if (executionPackage.isFile()) {
			try {
				inputStream = new FileInputStream(executionPackage);
				packageContent = IOUtils.toByteArray(inputStream);
			} catch (IOException exp) {
				SystemException.newSystemException(
						BusinessExceptionCodes.BSE000010, new Object[] {
								"An error occurred while reading file %s.",
								executionPackagePath });
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						LOGGER.error(
								"Exception occured while closing Input Stream",
								e);
					}
				}
			}
		} else {
			SystemException.newSystemException(
					BusinessExceptionCodes.BSE000010,
					new Object[] { String.format("File %s not found.",
							executionPackagePath) });
		}
		return packageContent;
	}

	@Override
	public boolean isPackageAvailable(final ModelExecutionPackageInfo modelExecutionPackageInfo)
			throws SystemException, BusinessException {
		return executionPackageDAO.findByPackageFolderAndPackageVersionAndExecEnvAndModelExecEnvName(modelExecutionPackageInfo.getPackageFolder(), modelExecutionPackageInfo.getPackageVersion(),modelExecutionPackageInfo.getExecEnv(),modelExecutionPackageInfo.getModelExecEnvName()) != null ? true: false;
	}

	

}
