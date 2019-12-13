/*
 * ModelBOImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2012 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.model.bo;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.bo.AbstractBusinessObject;
import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.CheckSumUtil;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.constants.EncodingTypes;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.dao.ModelExecutionEnvironmentDAO;
import com.ca.umg.business.execution.dao.ModelExecutionPackageDAO;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.execution.entity.ModelExecutionPackage;
import com.ca.umg.business.model.dao.CustomModelDAO;
import com.ca.umg.business.model.dao.CustomModelLibraryDAO;
import com.ca.umg.business.model.dao.ModelDAO;
import com.ca.umg.business.model.dao.ModelLibraryDAO;
import com.ca.umg.business.model.dao.ModelLibraryExecPackageMappingDAO;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.model.specification.ModelLibrarySpecification;
import com.ca.umg.business.model.specification.ModelSpecification;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.util.ResourceLoader;
import com.ca.umg.business.util.XmlValidator;
import com.ca.umg.business.version.dao.VersionContainerDAO;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 **/
@Named
public class ModelBOImpl extends AbstractBusinessObject implements ModelBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelBOImpl.class);

    private static final long serialVersionUID = 1L;

    public static final String UMG_MODEL_SCHEMA = "classpath:/schema/matlab/UMG-MODEL-IO.XSD";
    
    public static final String UMG_MODEL_SCHEMA_OLD = "classpath:/schema/matlab/UMG-MATLAB-IO.XSD";

    public static final String MATLAB_LANG = "MATLAB";

    public static final String R_LANG = "R";

    @Inject
    private ModelDAO modelDAO;
    
    @Inject
    private CustomModelDAO customModelDAO;

    @Inject
    private ModelLibraryDAO modelLibraryDAO;
    
    @Inject
    private CustomModelLibraryDAO customModelLibraryDAO;
    
    @Inject
    private SystemParameterProvider systemParameterProvider;
    
    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private ModelExecutionEnvironmentDAO modelExecutionEnvironmentDAO;

    @Inject
    private ModelExecutionPackageDAO modelExecutionPackageDAO;

    @Inject
    private ModelLibraryExecPackageMappingDAO modelLibExecPackageMapDAO;
    
    @Inject
    private VersionContainerDAO versionContainerDAO;
    
    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws BusinessException
     *             DOCUMENT ME!
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    @Override
    public List<Model> listAll() throws BusinessException, SystemException {
        return modelDAO.findAll(new Sort(Sort.Direction.DESC, BusinessConstants.CREATED_BY));
    }
    
    @Override
    public PageRecord<ModelInfo> listUniqueFilteredModels(SearchOptions searchOptions)
            throws BusinessException, SystemException {
        return customModelDAO.getUniqueModelInfos(searchOptions);
    }
    
    
    @Override
    public List<Model> listAllFilteredModels(SearchOptions searchOptions,
            String modelName) throws BusinessException, SystemException {
        Long fromDate = null;
        Long tillDate = null;
        Sort sortingCriteria = null;
        String searchText = searchOptions.getSearchText();
        
        if (searchOptions.getFromDate() != null && !searchOptions.getFromDate().isEmpty()) {
            fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(),
                    BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        if (searchOptions.getToDate() != null && !searchOptions.getToDate().isEmpty()) {
            tillDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(),
                    BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        
        if(searchOptions.isDescending()){
            sortingCriteria = new Sort(Sort.Direction.DESC, searchOptions.getSortColumn());
        }else{
            sortingCriteria = new Sort(Sort.Direction.ASC, searchOptions.getSortColumn());
        }
        
        Specification<Model> withModelName = ModelSpecification.withModelName(modelName);
        Specification<Model> withUMGNameLike = ModelSpecification.withUMGNameLike(searchText);
        Specification<Model> withIODefinitionNameLike = ModelSpecification.withIODefinitionNameLike(searchText);
        Specification<Model> withDocumentationNameLike = ModelSpecification.withDocumentationNameLike(searchText);
        Specification<Model> withCreatedByLike = ModelSpecification.withCreatedByLike(searchText);
        Specification<Model> withCreatedDateFrom = ModelSpecification.withCreatedDateFrom(fromDate);
        Specification<Model> withCreatedDateTill = ModelSpecification.withCreatedDateTill(tillDate);
        
        return modelDAO.findAll(where(withModelName).and(withCreatedDateFrom).and(withCreatedDateTill).and(where(withUMGNameLike).or(withIODefinitionNameLike).or(withDocumentationNameLike).or(withCreatedByLike)), sortingCriteria);
    
    }
    
    @Override
    public PageRecord<ModelLibraryInfo> listUniqueFilteredLibraries(SearchOptions searchOptions)
            throws BusinessException, SystemException {
        return customModelLibraryDAO.getUniqueModelLibraries(searchOptions);
    }
    
    @Override
    public List<ModelLibrary> listAllFilteredLibraries(SearchOptions searchOptions,
            String modelLibName)
            throws BusinessException,
            SystemException {
        Long fromDate = null;
        Long tillDate = null;
        Sort sortingCriteria = null;
        String searchText = searchOptions.getSearchText();
        
        if (searchOptions.getFromDate() != null && !searchOptions.getFromDate().isEmpty()) {
            fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(),
                    BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        if (searchOptions.getToDate() != null && !searchOptions.getToDate().isEmpty()) {
            tillDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(),
                    BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        
        if(searchOptions.isDescending()){
            sortingCriteria = new Sort(Sort.Direction.DESC, searchOptions.getSortColumn());
        }else{
            sortingCriteria = new Sort(Sort.Direction.ASC, searchOptions.getSortColumn());
        }
        
        Specification<ModelLibrary> withLibraryName = ModelLibrarySpecification.withLibraryName(modelLibName);
        Specification<ModelLibrary> withCreateFrom = ModelLibrarySpecification.withCreatedDateFrom(fromDate);
        Specification<ModelLibrary> withCreateTill = ModelLibrarySpecification.withCreatedDateTill(tillDate);
        Specification<ModelLibrary> withUmgNameLike = ModelLibrarySpecification.withUMGNameLike(searchText);
        Specification<ModelLibrary> withDescriptionLike = ModelLibrarySpecification.withDescriptionLike(searchText);
        Specification<ModelLibrary> withExecutionType = ModelLibrarySpecification.withExecutionType(searchText);
        Specification<ModelLibrary> withExecutionLanguage = ModelLibrarySpecification.withExecutionLanguage(searchText);
        Specification<ModelLibrary> withPackageNameLike = ModelLibrarySpecification.withPackageNameLike(searchText);
        Specification<ModelLibrary> createdBy = ModelLibrarySpecification.withCreatedBy(searchText);

        return modelLibraryDAO.findAll(
                where(withLibraryName).and(withCreateFrom).and(withCreateTill)
                        .and(where(withUmgNameLike).or(withDescriptionLike).or(withExecutionType).or(withExecutionLanguage).or(withPackageNameLike).or(createdBy)),
                        sortingCriteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.bo.ModelBO#createModel(com.ca.umg.business. model.entity.Model)
     */
    @Override
    public Model createModel(Model model) throws BusinessException, SystemException {
        LOGGER.info("Creating model {}.", model.getName());
        Model tempModel = model;
        tempModel.setUmgName(AdminUtil.generateUmgName(model.getName() + "-MID"));
        if (modelDAO.findByUmgName(model.getUmgName()) != null) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000035, new Object[] { BusinessConstants.MODEL,
                    model.getUmgName() });
        }
        tempModel.getModelDefinition().setModel(model);
        validate(tempModel);
        tempModel = modelDAO.save(tempModel);
        LOGGER.info("Created model {} successfully.", model.getName());
        return tempModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.bo.ModelBO#deleteModel(com.ca.umg.business. model.entity.Model)
     */
    @Override
    @Transactional
    public void deleteModel(Model model) throws BusinessException, SystemException {
        LOGGER.info("Deleting model {}.", model);
        try {
            modelDAO.delete(model);
        } catch (DataAccessException dae) {
            LOGGER.error("An error occurred while deleting model {} details. {} ", model.getName(), model);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000032, new Object[] { dae.getMessage() }, dae);
        }
        LOGGER.info("Deleted model {} successfully.", model);
    }

    @Override
    public void validateModelXml(InputStream modelXml) throws SystemException, BusinessException {
    	byte[] bytes = null;
		try {
			bytes = IOUtils.toByteArray(modelXml);
		}  catch (IOException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000006, new Object[] { e.getLocalizedMessage() }, e);
        }
    	InputStream backupStream  = new ByteArrayInputStream(bytes);
    	InputStream modelXmlStream  = new ByteArrayInputStream(bytes);
        InputStream xsd = ResourceLoader.getResource(UMG_MODEL_SCHEMA);
        InputStream xsdOld = ResourceLoader.getResource(UMG_MODEL_SCHEMA_OLD);
        boolean isValid = XmlValidator.validate(xsd, modelXmlStream);
        if(!isValid){
        	isValid = XmlValidator.validate(xsdOld, backupStream);
        }
        if (!isValid) {
            throw new BusinessException(BusinessExceptionCodes.BSE000007, new Object[] { "invalid" });
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.bo.ModelBO#getModelDetails(java.lang.String)
     */
    @Override
    public Model getModelDetails(String identifier) throws BusinessException, SystemException {
        return modelDAO.findOne(identifier);
    }

    @Override
    public Model getModelByName(String modelDerievedName) throws BusinessException, SystemException {
        return modelDAO.findByUmgName(modelDerievedName);
    }

    @Override
    public List<ModelLibrary> findAllModelLibraries() throws SystemException, BusinessException {
        return modelLibraryDAO.findAll(new Sort(Sort.Direction.DESC, BusinessConstants.CREATED_BY));
    }

    @Override
    public ModelLibrary createModelLibrary(ModelLibrary library) throws SystemException, BusinessException {
        library.setUmgName(AdminUtil.generateUmgName(library.getName()));
        if (modelLibraryDAO.findByUmgName(library.getUmgName()) != null) {
            throw new BusinessException(BusinessExceptionCodes.BSE000035, new Object[] { BusinessConstants.MODEL_LIBRARY,
                    library.getUmgName() });
        }
        ModelLibrary modelLibrary = null;
        validate(library);
        modelLibrary = modelLibraryDAO.save(library);
        return modelLibrary;
    }

    @Override
    public ModelLibrary findModelLibrary(String modelLibraryId) throws SystemException, BusinessException {
        return modelLibraryDAO.findOne(modelLibraryId);
    }

    @Override
    public void deleteModelLibrary(ModelLibrary library) throws SystemException, BusinessException {
        LOGGER.info("Deleting model library {}.", library);
        modelLibraryDAO.delete(library);
        LOGGER.info("Deleted model library {} successfully.", library);
    }

    @Override
    public void checkJarAvailability(byte[] modelLibJar, String encodingType, String jarName) throws SystemException,
            BusinessException {
        String generatedChecksum = CheckSumUtil.getCheckSumValue(modelLibJar, encodingType);
        List<ModelLibrary> modelLibraries = modelLibraryDAO.findByJarNameContainingIgnoreCaseAndChecksumOrderByCreatedDateDesc(
                jarName, generatedChecksum);
        if (CollectionUtils.isNotEmpty(modelLibraries)) {

            String versionNumber = versionContainerDAO.getVersionNumberForModelLibId(modelLibraries.get(0).getId());
            throw new BusinessException(BusinessExceptionCodes.BSE000093,
                    new Object[] { jarName, modelLibraries.get(0).getName(), versionNumber });
        }
    }

    @Override
    public void validateCheckSum(byte[] modelLibJar, final String checksum, String encodingType) throws SystemException,
            BusinessException {
        boolean isValid = false;
        switch (EncodingTypes.getType(encodingType)) {
        case MD5:
            isValid = CheckSumUtil.validateCheckSumByMD5(modelLibJar, checksum);
            break;
        case SHA1:
            isValid = CheckSumUtil.validateCheckSumBySHA1(modelLibJar, checksum);
            break;
        case SHA384:
            isValid = CheckSumUtil.validateCheckSumBySHA384(modelLibJar, checksum);
            break;
        case SHA512:
            isValid = CheckSumUtil.validateCheckSumBySHA512(modelLibJar, checksum);
            break;
        default:
            isValid = CheckSumUtil.validateCheckSumBySHA256(modelLibJar, checksum);
            break;
        }
        if (!isValid) {
            throw new BusinessException(BusinessExceptionCodes.BSE000059, new Object[] { "checksum invalid" });
        }
    }

    @Override
    public List<String> getAllLibraryNames() throws BusinessException, SystemException {
        return modelLibraryDAO.getAllLibraryNames();
    }

    @Override
    public List<String> getListOfDerivedModelLibraryNames(String libraryName) throws BusinessException, SystemException {
        return modelLibraryDAO.getListOfDerivedModelLibraryNames(libraryName);
    }

    @Override
    public List<String> getAllModelNames() throws BusinessException, SystemException {
        return modelDAO.getAllModelNames();
    }

    @Override
    public ModelLibrary findByUmgName(String umgName) throws BusinessException, SystemException {
        return modelLibraryDAO.findByUmgName(umgName);
    }

    @Override
    public List<ModelLibrary> findMappingInfoByLibraryNamName(String libraryName) throws BusinessException, SystemException {
        return modelLibraryDAO.findByName(libraryName);
    }

    @Override
    public List<ModelLibrary> searchModelLibraryByJarAndChecksum(String jarName, String checkSum) throws BusinessException,
            SystemException {
        return modelLibraryDAO.findByJarNameContainingIgnoreCaseAndChecksumOrderByCreatedDateDesc(jarName, checkSum);
    }

    @Override
    public byte[] getModelTemplate(String modelName, final ModelType modelType) throws BusinessException, SystemException {
        byte[] modelTemplateContent = null;
        InputStream inputStream = null;
        if(modelName != null){
            try {
                String modelTemplatePath;
                if (modelType == ModelType.BULK) {
                    modelTemplatePath = "io-definition/" + modelName + "_Bulk_Template.xlsx";
                } else {
                    modelTemplatePath = "io-definition/" + modelName + "_Template.xlsx";
                }
                inputStream =  ModelBOImpl.class.getClassLoader().getResourceAsStream(modelTemplatePath);
                modelTemplateContent = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }finally {
            	IOUtils.closeQuietly(inputStream);
			}
        }
        return modelTemplateContent;
    }

    @Override
    public byte[] getModelExcel(Model model) throws SystemException {
        InputStream inputStream = null;
        byte[] excelContent = null;
        StringBuffer filePathBfr = new StringBuffer(AdminUtil.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider
                .getParameter(SystemConstants.SAN_BASE))));
        filePathBfr.append(File.separatorChar).append(BusinessConstants.MODEL)
        .append(File.separatorChar)
        .append(model.getName())
        .append(File.separatorChar)
        .append(model.getUmgName())
        .append(File.separatorChar)
        .append(BusinessConstants.EXCEL_IO_DEFINITION)
        .append(File.separatorChar)
        .append(model.getIoDefExcelName());
        
        File modelExcelFile = new File(filePathBfr.toString());
        
        if(modelExcelFile.isFile()){
            try {
                inputStream = new FileInputStream(modelExcelFile);
                excelContent = IOUtils.toByteArray(inputStream);
            } catch (FileNotFoundException e) {
                 SystemException.newSystemException(BusinessExceptionCodes.BSE000010,
                            new Object[] { String.format("File %s not found.", filePathBfr.toString()) });
            } catch (IOException exp) {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000010, new Object[] {
                        "An error occurred while reading file %s.", filePathBfr.toString() });
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("Exception occured while closing Input Stream", e);
                    }
                }
            }
        }
        return excelContent;
    }

    @Override
    public ModelExecutionEnvironment getModelExecutionEnvironment(String execLanguage, String environmentVersion) {
        return modelExecutionEnvironmentDAO.findByExecutionEnvironmentAndEnvironmentVersion(execLanguage, environmentVersion);
    }

    @Override
    @SuppressWarnings("PMD")
    public ModelExecutionPackage getModelExecutionPackage(String modelExecEnvName,
            String packageFolder, String packageVersion,String execEnv) {
        return modelExecutionPackageDAO.findByModelExecEnvNameAndPackageFolderAndPackageVersionAndExecEnv(modelExecEnvName, packageFolder,
                packageVersion,execEnv);

    }

    @Override
    public ModelExecutionPackage getBaseExecutionPackage(String modelExecEnvName, String packageFolder) {
        ModelExecutionPackage baseModelExecPackage = null;
        List<ModelExecutionPackage> baseExecutionPackages = modelExecutionPackageDAO
                .findByModelExecEnvNameAndPackageFolderAndPackageType(modelExecEnvName, packageFolder,
                        BusinessConstants.BASE);
        if (CollectionUtils.isNotEmpty(baseExecutionPackages)) {
            baseModelExecPackage = baseExecutionPackages.get(0);

        }
        return baseModelExecPackage;
    }

    @Override
    public void deleteSupportPackages(ModelLibrary modelLibrary) {
        modelLibExecPackageMapDAO.deleteByModelLibrary(modelLibrary);
    }

    @Override
    public List<ModelLibraryInfo> getModelPackageLibraries(String modelExecEnvName) throws BusinessException, SystemException {
        return customModelLibraryDAO.getNewModelPackages(modelExecEnvName);
    }
   
}
