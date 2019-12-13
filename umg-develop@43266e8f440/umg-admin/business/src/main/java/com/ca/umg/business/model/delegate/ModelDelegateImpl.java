/*

 * ModelDelegateImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.model.delegate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.constants.EncodingTypes;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.entity.ModelExecutionPackage;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.info.ModelMappingInfo;
import com.ca.umg.business.model.bo.ModelArtifactBO;
import com.ca.umg.business.model.bo.ModelBO;
import com.ca.umg.business.model.bo.ModelReportTemplateBO;
import com.ca.umg.business.model.dao.ModelLibraryExecPackageMappingDAO;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.entity.ModelLibraryExecPackageMapping;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryExecPackageMappingInfo;
import com.ca.umg.business.model.info.ModelLibraryHierarchyInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.report.ReportExceptionCodes;
import com.ca.umg.report.model.ModelReportTemplateDefinition;
import com.ca.umg.report.model.ModelReportTemplateInfo;

/**
 * 
 **/
@Component
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount" })
public class ModelDelegateImpl extends AbstractDelegate implements ModelDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelDelegateImpl.class);
    @Inject
    private ModelBO modelBO;
    @Inject
    private ModelArtifactBO modelArtifactBO;
    @Inject
    private VersionBO versionBO;
    @Inject
    private MappingBO mappingBO;

    @Inject
    private ModelReportTemplateBO modelReportTemplateBO;

    @Inject
    private ModelLibraryExecPackageMappingDAO modelLibExecPackageMapDAO;

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private SystemParameterProvider sysParam;

    /**
     * DOCUMENT ME!
     * 
     * @param modelInfo
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws BusinessException
     *             DOCUMENT ME!
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public ModelInfo createModel(ModelInfo modelInfo) throws BusinessException, SystemException {
        ModelArtifact documentationArtifact = null;
        byte[] data = AdminUtil.convertStreamToByteArray(modelInfo.getXml().getData());
        // validate model xml
        modelBO.validateModelXml(new ByteArrayInputStream(data));
        LOGGER.info("Model {} xml validated successfully.", modelInfo.getName());
        Model model = convert(modelInfo, Model.class);
        // set io definition to model definition
        model.getModelDefinition().setIoDefinition(data);
        // save model
        model = modelBO.createModel(model);
        // store jar and documentation to SAN location
        if (modelInfo.getDocumentation() != null) {
            documentationArtifact = modelInfo.getDocumentation();
            documentationArtifact.setModelName(model.getName());
            documentationArtifact.setUmgName(model.getUmgName());
            byte[] docDataArray = AdminUtil.convertStreamToByteArray(modelInfo.getDocumentation().getData());
            documentationArtifact.setDataArray(docDataArray);
            modelArtifactBO.storeArtifacts(new ModelArtifact[] { documentationArtifact });
        }
        // store excel definition in SAN location
        modelInfo.setUmgName(model.getUmgName());
        storeModelDefArtifacts(modelInfo);
        return convert(model, ModelInfo.class);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public ModelInfo createModelWithoutValidn(ModelInfo modelInfo) throws BusinessException, SystemException {
        byte[] xmlData = null;
        Model model = convert(modelInfo, Model.class);
        // set io definition to model definition
        xmlData = modelInfo.getXml().getDataArray();
        model.getModelDefinition().setIoDefinition(xmlData);
        // save model
        model = modelBO.createModel(model);
        return convert(model, ModelInfo.class);
    }

    @Override
    public void storeModelArtifacts(ModelInfo modelInfo) throws BusinessException, SystemException {
        ModelArtifact documentationArtifact = null;
        // store jar and documentation to SAN location
        if (modelInfo.getDocumentation() != null) {
            documentationArtifact = modelInfo.getDocumentation();
            documentationArtifact.setModelName(modelInfo.getName());
            documentationArtifact.setUmgName(modelInfo.getUmgName());
            modelArtifactBO.storeArtifacts(new ModelArtifact[] { documentationArtifact });
        }

    }

    /**
     * DOCUMENT ME!
     * 
     * @param identifier
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws BusinessException
     *             DOCUMENT ME!
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    @Override
    public ModelInfo getModelDetails(String identifier) throws BusinessException, SystemException {
        Model model = modelBO.getModelDetails(identifier);
        return convert(model, ModelInfo.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.delegate.ModelDelegate#deleteModel(java.lang .String)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(String identifier) throws BusinessException, SystemException {
        LOGGER.info("Received request to delete model.");
        ModelArtifact modelArtifact = null;
        Model model = modelBO.getModelDetails(identifier);
        if (model != null) {
            modelBO.deleteModel(model);
            // delete model artifact
            modelArtifact = new ModelArtifact();
            modelArtifact.setModelName(model.getName());
            modelArtifact.setUmgName(model.getUmgName());
            // delete model artifacts from san location
            modelArtifactBO.deleteModelArtifact(modelArtifact, false);
        }
    }

    @Override
    public PageRecord<ModelInfo> getUniqueModelInfos(SearchOptions searchOptions) throws BusinessException, SystemException {
        return modelBO.listUniqueFilteredModels(searchOptions);
    }

    @Override
    public List<ModelInfo> getAllFilteredModelInfos(SearchOptions searchOptions, String modelName)
            throws BusinessException, SystemException {
        List<Model> models = modelBO.listAllFilteredModels(searchOptions, modelName);
        return convertToList(models, ModelInfo.class);
    }

    @Override
    public PageRecord<ModelLibraryInfo> getUniqueModelLibraries(SearchOptions searchOptions)
            throws BusinessException, SystemException {
        return modelBO.listUniqueFilteredLibraries(searchOptions);
    }

    @Override
    public List<ModelLibraryInfo> getAllFilteredModelLibraries(SearchOptions searchOptions, String modelLibName)
            throws BusinessException, SystemException {
        List<ModelLibrary> libraries = modelBO.listAllFilteredLibraries(searchOptions, modelLibName);
        return convertToList(libraries, ModelLibraryInfo.class);
    }

    @Override
    public List<ModelLibraryHierarchyInfo> getModelLibraryHierarchyInfos() throws BusinessException, SystemException {
        List<ModelLibraryHierarchyInfo> modelLibHierarchyInfos = null;
        List<ModelLibrary> modelLibsList = modelBO.findAllModelLibraries();
        ModelLibraryHierarchyInfo modelLibTagInfo = null;
        ModelLibraryHierarchyInfo modelLibTag = null;
        Map<String, ModelLibraryHierarchyInfo> tagMap = null;
        String modelTag = null;
        if (CollectionUtils.isNotEmpty(modelLibsList)) {
            tagMap = new HashMap<String, ModelLibraryHierarchyInfo>();
            for (ModelLibrary modelLib : modelLibsList) {
                modelLibTag = new ModelLibraryHierarchyInfo();
                modelTag = modelLib.getName();
                modelLibTag.setName(modelLib.getUmgName());
                modelLibTag.setId(modelLib.getId());
                modelLibTag.setDescription(modelLib.getDescription());
                modelLibTag.setJarName(modelLib.getJarName());
                modelLibTag.setExecutionLanguage(modelLib.getExecutionLanguage());
                modelLibTag.setExecutionType(modelLib.getExecutionType());
                modelLibTag.setUmgName(modelLib.getUmgName());
                modelLibTag.setCreatedBy(modelLib.getCreatedBy());
                modelLibTag.setCreatedDate(AdminUtil.getDateFormatMillisForEst(modelLib.getCreatedDate().getMillis(), null));
                modelLibTag.setUpdatedBy(modelLib.getLastModifiedBy());
                modelLibTag.setUpdatedOn(AdminUtil.getDateFormatMillisForEst(modelLib.getLastModifiedDate().getMillis(), null));
                if (tagMap.containsKey(modelTag)) {
                    tagMap.get(modelTag).getChildren().add(modelLibTag);
                } else {
                    modelLibTagInfo = new ModelLibraryHierarchyInfo();
                    modelLibTagInfo.setName(modelTag);
                    modelLibTagInfo.getChildren().add(modelLibTag);
                    tagMap.put(modelTag, modelLibTagInfo);
                }
            }
            modelLibHierarchyInfos = new ArrayList<>(tagMap.values());
        }
        return modelLibHierarchyInfos;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param modelXml
     *            DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    public void validateModelXml(InputStream modelXml) throws SystemException, BusinessException {
        modelBO.validateModelXml(modelXml);
    }

    @Override
    public List<ModelLibraryInfo> findAllLibraries() throws SystemException, BusinessException {
        List<ModelLibrary> modelLibs = modelBO.findAllModelLibraries();
        return convertToList(modelLibs, ModelLibraryInfo.class);
    }

    /*
     * 
     * Stream pointer will reach the end of stream after it is read once. So, in the below method we re-create the input stream
     * from the byte array that was earlier read out of it, before sending it for save.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelLibraryInfo createModelLibrary(ModelLibraryInfo modelLibraryInfo) throws BusinessException, SystemException {
        ModelLibrary modelLibrary = getModelLibraryFromInfo(modelLibraryInfo);
        if (modelLibraryInfo.getJar() != null && modelLibrary != null) {
            byte[] data = AdminUtil.convertStreamToByteArray(modelLibraryInfo.getJar().getData());
            modelLibraryInfo.getJar().setData(new ByteArrayInputStream(data));
            modelLibraryInfo.getJar().setDataArray(data);
            modelBO.checkJarAvailability(data, modelLibrary.getEncodingType(), modelLibrary.getJarName());
            modelBO.validateCheckSum(data, modelLibrary.getChecksum(), modelLibrary.getEncodingType());
            modelLibrary = modelBO.createModelLibrary(modelLibrary);
            modelLibraryInfo.getJar().setModelName(modelLibrary.getName());
            modelLibraryInfo.getJar().setUmgName(modelLibrary.getUmgName());
            modelArtifactBO.storeModelJar(modelLibraryInfo.getJar());
        }
        return convert(modelLibrary, ModelLibraryInfo.class);
    }

    @Override
    public ModelLibraryInfo getModelLibraryDetails(String modelLibraryId) throws SystemException, BusinessException {
        ModelLibrary library = modelBO.findModelLibrary(modelLibraryId);
        return convert(library, ModelLibraryInfo.class);
    }

    @Override
    public void deleteModelLibrary(String modelLibraryId) throws SystemException, BusinessException {
        LOGGER.info("Received request to delete model library.");
        ModelArtifact modelArtifact = null;
        ModelLibrary modelLibrary = modelBO.findModelLibrary(modelLibraryId);
        if (modelLibrary != null) {
            modelLibExecPackageMapDAO.deleteByModelLibrary(modelLibrary);
            modelBO.deleteModelLibrary(modelLibrary);
            modelArtifact = new ModelArtifact();
            modelArtifact.setModelName(modelLibrary.getName());
            modelArtifact.setUmgName(modelLibrary.getUmgName());
            modelArtifactBO.deleteModelArtifact(modelArtifact, true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.delegate.ModelDelegate#getModelArtifacts(java.lang.String)
     */
    @Override
    public List<ModelArtifact> getModelArtifacts(String modelId) throws SystemException, BusinessException {
        return getModelArtifactsCommon(modelId);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageViewDownloadReleaseNotes())")
    public List<ModelArtifact> getModelArtifactsFromDownloadModelDoc(String modelId) throws SystemException, BusinessException {
        return getModelArtifactsCommon(modelId);
    }

    private List<ModelArtifact> getModelArtifactsCommon(String modelId) throws SystemException, BusinessException {
        List<ModelArtifact> modelArtifacts = null;
        Model model = modelBO.getModelDetails(modelId);
        if (model != null) {
            LOGGER.info("Retrieving model artifacts for model {}.", model.getUmgName());
            modelArtifacts = modelArtifactBO.fetchArtifacts(model.getName(), model.getUmgName(), false);
        }
        LOGGER.info("Retrived {} artifacts.", modelArtifacts == null ? 0 : modelArtifacts.size());
        return modelArtifacts;
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageViewDownloadIODef())")
    public Model getModelXML(String modelId) throws SystemException, BusinessException {
        return modelBO.getModelDetails(modelId);
    }

    @Override
    public List<ModelArtifact> getModelLibraryArtifacts(String modelLibraryId) throws SystemException, BusinessException {
        return getModelLibraryArtifactsCommonFunction(modelLibraryId);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageViewDownloadModelPackage())")
    public List<ModelArtifact> getModelLibraryArtifactsFromDownloadLibraryJar(String modelLibraryId)
            throws SystemException, BusinessException {
        return getModelLibraryArtifactsCommonFunction(modelLibraryId);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageViewDownloadManifest())")
    public List<ModelArtifact> getModelLibraryArtifactsFromDownloadModelLibManifest(String modelLibraryId)
            throws SystemException, BusinessException {
        return getModelLibraryArtifactsCommonFunction(modelLibraryId);
    }

    private List<ModelArtifact> getModelLibraryArtifactsCommonFunction(String modelLibraryId)
            throws SystemException, BusinessException {
        List<ModelArtifact> modelArtifacts = null;
        ModelLibrary modelLibrary = modelBO.findModelLibrary(modelLibraryId);
        if (modelLibrary != null) {
            LOGGER.info("Retrieving model library artifacts for libray {}.", modelLibrary.getUmgName());
            modelArtifacts = modelArtifactBO.fetchArtifacts(modelLibrary.getName(), modelLibrary.getUmgName(), true);
        }
        LOGGER.info("Retrived {} artifacts.", modelArtifacts == null ? 0 : modelArtifacts.size());
        return modelArtifacts;
    }

    /*
     * gets the list of all the model names
     * 
     * @see com.ca.umg.business.model.delegate.ModelDelegate#getAllModelNames()
     */

    @Override
    public List<String> getAllModelNames() throws SystemException, BusinessException {
        List<String> allModelNames = null;
        Set<String> modelNames = null;

        final List<Model> AllModelsList = modelBO.listAll();
        if (CollectionUtils.isNotEmpty(AllModelsList)) {
            modelNames = new HashSet<String>();
            for (Model model : AllModelsList) {
                modelNames.add(model.getName());
            }
            allModelNames = new ArrayList<String>(modelNames);
        }

        return allModelNames;
    }

    /*
     * @Override public void validateCheckSum(byte[] modelLibJar, String checksumVal) throws SystemException, BusinessException {
     * // TODO Auto-generated method stub modelBO.validateCheckSum(modelLibJar, checksumVal); }
     */

    @Override
    public List<String> getAllLibraryNames() throws BusinessException, SystemException {
        return modelBO.getAllLibraryNames();
    }

    @Override
    public List<String> getListOfDerivedModelLibraryNames(String libraryName) throws BusinessException, SystemException {
        return modelBO.getListOfDerivedModelLibraryNames(libraryName);
    }

    @Override
    public ModelMappingInfo getAllVersionNamesForModel(String identifier) throws SystemException, BusinessException {
        ModelMappingInfo mappingInfo = null;
        List<String> mappingNames = null;
        List<String> versionNames = null;
        List<String> individualMapping = null;
        if (StringUtils.isNotBlank(identifier)) {
            mappingNames = mappingBO.getListOfMappingNamesById(identifier);
            if (CollectionUtils.isNotEmpty(mappingNames)) {
                mappingInfo = new ModelMappingInfo();
                versionNames = new ArrayList<>();
                for (String mappingName : mappingNames) {
                    individualMapping = versionBO.findNotDeletedVersions(mappingName);
                    if (CollectionUtils.isNotEmpty(individualMapping)) {
                        versionNames.addAll(individualMapping);
                    }
                }
                mappingInfo.setMappingNameList(mappingNames);
                mappingInfo.setVersionNameList(versionNames);
            }
        }
        return mappingInfo;
    }

    @Override
    public ModelLibraryInfo findByUmgName(String umgName) throws BusinessException, SystemException {
        return convert(modelBO.findByUmgName(umgName), ModelLibraryInfo.class);
    }

    /**
     * 
     */
    @Override
    public List<ModelLibrary> findMappingInfoByLibraryNamName(String libraryName) throws BusinessException, SystemException {
        return modelBO.findMappingInfoByLibraryNamName(libraryName);
    }

    @Override
    public List<ModelLibraryInfo> searchModelLibraryByJarAndChecksum(String jarName, String checkSum)
            throws BusinessException, SystemException {
        List<ModelLibrary> modelLibraries = modelBO.searchModelLibraryByJarAndChecksum(jarName, checkSum);
        return convertToList(modelLibraries, ModelLibraryInfo.class);
    }

    @Override
    public void validateCheckSum(ModelLibraryInfo modelLibraryInfo) throws BusinessException, SystemException {
        ModelLibrary modelLibrary = getModelLibraryFromInfo(modelLibraryInfo);
        if (modelLibraryInfo.getJar() != null && modelLibrary != null && modelLibraryInfo.getId() != null
                && modelLibrary.getChecksum() != null) {
            modelBO.validateCheckSum(modelLibraryInfo.getJar().getDataArray(), modelLibrary.getChecksum(),
                    modelLibrary.getEncodingType());
        }
    }

    @Override
    public void checkJarAvailability(ModelLibraryInfo modelLibraryInfo) throws BusinessException, SystemException {
        ModelLibrary modelLibrary = getModelLibraryFromInfo(modelLibraryInfo);
        if (modelLibraryInfo.getJar() != null && modelLibrary != null) {
            modelBO.checkJarAvailability(modelLibraryInfo.getJar().getDataArray(), modelLibrary.getEncodingType(),
                    modelLibrary.getJarName());
        }
    }

    @Override
    public ModelLibraryInfo createModelLibraryWithOutValidation(ModelLibraryInfo modelLibraryInfo)
            throws BusinessException, SystemException {
        ModelLibrary modelLibrary = getModelLibraryFromInfo(modelLibraryInfo);
        if (modelLibrary != null) {
            modelLibrary = modelBO.createModelLibrary(modelLibrary);
        }
        return convert(modelLibrary, ModelLibraryInfo.class);
    }

    @Override
    public void storeModelLibraryArtifacts(ModelLibraryInfo modelLibraryInfo) throws BusinessException, SystemException {
        if (modelLibraryInfo.getJar() != null) {
            modelArtifactBO.storeModelJar(modelLibraryInfo.getJar());
        }
    }

    private ModelLibrary getModelLibraryFromInfo(ModelLibraryInfo modelLibraryInfo) {
        ModelLibrary modelLibrary = convert(modelLibraryInfo, ModelLibrary.class);
        if (StringUtils.isEmpty(modelLibrary.getEncodingType())) {
            modelLibrary.setEncodingType(EncodingTypes.SHA256.getName());
        }
        return modelLibrary;
    }

    @Override
    public byte[] getModelExcel(Model model) throws SystemException, BusinessException {
        return modelBO.getModelExcel(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.delegate.ModelDelegate#storeModelDefArtifacts(com.ca.umg.business.model.info.ModelInfo)
     */
    @Override
    public void storeModelDefArtifacts(ModelInfo modelInfo) throws SystemException, BusinessException {
        ModelArtifact definitionArtifact = null;
        // store IO Excel to SAN location
        if (modelInfo.getExcel() != null) {
            definitionArtifact = modelInfo.getExcel();
            definitionArtifact.setModelName(modelInfo.getName());
            definitionArtifact.setUmgName(modelInfo.getUmgName());
            modelArtifactBO.storeModelDefArtifacts(new ModelArtifact[] { definitionArtifact });
        }

    }

    @Override
    public void getModelAddonPackages(String modelExecEnvName, List<ModelLibraryExecPackageMappingInfo> supportPackageInfos,
            String execEnv, List<Error> errors) {
        for (ModelLibraryExecPackageMappingInfo modelLibExecPackageMapInfo : supportPackageInfos) {
            ModelExecutionPackage modelExecPackage = modelBO.getModelExecutionPackage(modelExecEnvName,
                    modelLibExecPackageMapInfo.getPackageFolder().trim(), modelLibExecPackageMapInfo.getPackageVersion().trim(),
                    execEnv);
            if (modelExecPackage == null) {
                modelExecPackage = modelBO.getBaseExecutionPackage(modelExecEnvName,
                        modelLibExecPackageMapInfo.getPackageFolder().trim());
                if (modelExecPackage != null) {
                    errors.add(new Error(
                            "Cannot use " + modelLibExecPackageMapInfo.getPackageFolder() + " with version "
                                    + modelLibExecPackageMapInfo.getPackageVersion() + ". Use "
                                    + modelLibExecPackageMapInfo.getPackageFolder() + " with version "
                                    + modelExecPackage.getPackageVersion() + " and resubmit manifest",
                            BusinessConstants.VALIDATE_R_MANIFEST_FILE, StringUtils.EMPTY));
                } else {
                    errors.add(new Error(
                            "Library " + modelLibExecPackageMapInfo.getPackageFolder() + " version "
                                    + modelLibExecPackageMapInfo.getPackageVersion() + " is not available in " + execEnv,
                            BusinessConstants.VALIDATE_R_MANIFEST_FILE, StringUtils.EMPTY));
                }
            } else {
                modelLibExecPackageMapInfo.setModelExecPackageId(modelExecPackage.getId());
            }
        }

    }

    @Override
    public List<ModelLibraryInfo> getModelPackageLibraries(String modelExecEnvName) throws BusinessException, SystemException {
        return modelBO.getModelPackageLibraries(modelExecEnvName);
    }

    @Override
    public void storeModelLibraryManifestFile(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException {
        if (modelLibraryInfo.getManifestFile() != null) {
            modelArtifactBO.storeModelJar(modelLibraryInfo.getManifestFile());
        }
    }

    @Override
    public void saveSupportModelExecPackages(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException {
        if (modelLibraryInfo != null) {
            List<ModelLibraryExecPackageMappingInfo> supportPackages = modelLibraryInfo.getSupportPackages();
            if (CollectionUtils.isNotEmpty(supportPackages)) {
                LOGGER.error("supportPackages size is :" + modelLibraryInfo.getSupportPackages().size());
                for (ModelLibraryExecPackageMappingInfo modelLibExecPackageMapInfo : supportPackages) {
                    modelLibExecPackageMapInfo.setModelLibrary(modelBO.findModelLibrary(modelLibraryInfo.getId()));
                    ModelLibraryExecPackageMapping modelLibraryExecPackageMapping = convert(modelLibExecPackageMapInfo,
                            ModelLibraryExecPackageMapping.class);
                    modelLibraryExecPackageMapping.setModelExecPackageId(modelLibExecPackageMapInfo.getModelExecPackageId());
                    LOGGER.error("Saving support package for package id: " + modelLibExecPackageMapInfo.getModelExecPackageId());
                    try {
                        modelLibExecPackageMapDAO.save(modelLibraryExecPackageMapping);
                    } catch (Exception ex) { // NOPMD
                        LOGGER.error("exception is ", ex);

                    }
                    LOGGER.error("Saved successfully for package id: " + modelLibExecPackageMapInfo.getModelExecPackageId());
                }
            }
        }
    }

    @Override
    public void deleteSupportModelExecPackages(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException {
        ModelLibrary modelLibrary = modelBO.findModelLibrary(modelLibraryInfo.getId());
        modelBO.deleteSupportPackages(modelLibrary);
        ModelArtifact modelArtifact = new ModelArtifact();
        modelArtifact.setModelName(modelLibrary.getName());
        modelArtifact.setUmgName(modelLibrary.getUmgName());
        modelArtifactBO.deleteModelArtifact(modelArtifact, true);
    }

    @SuppressWarnings("resource")
    @Override
    public void moveRmodelFromTemptoSan(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException {
        String sanPath = AdminUtil.getSanBasePath(umgFileProxy.getSanPath(sysParam.getParameter(SystemConstants.SAN_BASE)));
        String fileUploadPath = sanPath + File.separatorChar + sysParam.getParameter(SystemConstants.FILE_UPLOAD_TEMP_PATH)
                + File.separatorChar + modelLibraryInfo.getJarName();
        String modelLibPath = sanPath + File.separatorChar + BusinessConstants.MODEL_LIBRARY_PARENT_FOLDER + File.separatorChar
                + modelLibraryInfo.getName() + File.separatorChar + modelLibraryInfo.getUmgName();
        File outputFile = new File(modelLibPath);
        if (!outputFile.exists()) {
            outputFile.mkdirs();
        }
        try {
            FileChannel inputChannel = null;
            FileChannel outputChannel = null;
            File inputFile = new File(fileUploadPath);
            try {
                inputChannel = new FileInputStream(inputFile).getChannel();
                outputChannel = new FileOutputStream(new File(modelLibPath + File.separatorChar + modelLibraryInfo.getJarName()))
                        .getChannel();
                long startTime = System.currentTimeMillis();
                LOGGER.info("File moment started :" + startTime);
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                LOGGER.info("File moment finished in ms : " + (System.currentTimeMillis() - startTime));

            } finally {
                if (inputChannel != null) {
                    inputChannel.close();
                }
                if (outputChannel != null) {
                    outputChannel.close();
                }
            }

        } catch (IOException e) {
            LOGGER.error("error while removing the file from temp to sanpath. Exception is :", e);
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000142,
                    new Object[] { modelLibraryInfo.getJarName(), e.getMessage() });

        }

    }

    @Override
    public ModelReportTemplateInfo createModelReportTemplate(final ModelReportTemplateInfo reportTemplateInfo)
            throws SystemException {
        ModelReportTemplateDefinition savedEntity = null;
        if (reportTemplateInfo != null) {
            final ModelReportTemplateDefinition reportTemplateDefination = convert(reportTemplateInfo,
                    ModelReportTemplateDefinition.class);
            savedEntity = modelReportTemplateBO.createModelReportTemplate(reportTemplateDefination);
        }
        return convert(savedEntity, ModelReportTemplateInfo.class);
    }

    @Override
    public void deleteModelReportTemplate(String modelReportTemplateId) throws SystemException, BusinessException {
        LOGGER.info("Received request to delete Model Report Template. ModelReportTemplateId :" + modelReportTemplateId);
        ModelReportTemplateDefinition modelReportTemplateDefinition = modelReportTemplateBO
                .findModelReportTemplateDefinition(modelReportTemplateId);
        if (modelReportTemplateDefinition != null) {
            modelReportTemplateBO.deleteModelReportTemplateDefinition(modelReportTemplateDefinition);
        }
    }

    @Override
    public ModelReportTemplateDefinition getModelReportTemplate(final String versionId)
            throws SystemException, BusinessException {
        return modelReportTemplateBO.getModelReportTemplate(versionId);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageAddReportTemplate())")
    public ModelReportTemplateDefinition getModelRprtTemplateVerListScrn(final String versionId)
            throws SystemException, BusinessException {
        return modelReportTemplateBO.getModelReportTemplate(versionId);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageViewDownloadReportTemplate())")
    public ModelReportTemplateDefinition getModelReportTemplateFromDownloadReportTemplate(final String versionId)
            throws SystemException, BusinessException {
        return modelReportTemplateBO.getModelReportTemplate(versionId);
    }

    @Override
    public ModelReportTemplateInfo uploadModelReportTemplate(final ModelReportTemplateInfo reportTemplateInfo)
            throws SystemException, BusinessException {
        ModelReportTemplateDefinition savedEntity = null;
        if (reportTemplateInfo != null) {
            final ModelReportTemplateDefinition reportTemplateDefination = convert(reportTemplateInfo,
                    ModelReportTemplateDefinition.class);
            savedEntity = modelReportTemplateBO.uploadModelReportTemplate(reportTemplateDefination);
        }
        return convert(savedEntity, ModelReportTemplateInfo.class);
    }

    @Override
    public ModelReportTemplateDefinition getModelReportTemplateByTxnId(final String transactionId)
            throws SystemException, BusinessException {
        return modelReportTemplateBO.getModelReportTemplateByTxnId(transactionId);
    }

    @Override
    public ModelReportTemplateDefinition getModelReportTemplate(final String versionName, final String fullVersion)
            throws SystemException, BusinessException {
        return modelReportTemplateBO.getModelReportTemplate(versionName, fullVersion);
    }

    @Override
    public Boolean hasModelReportTemplate(final String versionId) throws SystemException, BusinessException {
        boolean flag = false;
        try {
            flag = hasModelReportTemplate(modelReportTemplateBO.getModelReportTemplate(versionId));
        } catch (SystemException | BusinessException e) {
            if (!ReportExceptionCodes.isReportTemplateNotAvlbCode(e.getCode())) {
                throw e;
            }
        }

        return flag;
    }

    @Override
    public Boolean hasModelReportTemplate(final String versionName, final String fullVersion)
            throws SystemException, BusinessException {
        boolean flag = false;
        try {
            flag = hasModelReportTemplate(modelReportTemplateBO.getModelReportTemplate(versionName, fullVersion));
        } catch (SystemException | BusinessException e) {
            if (!ReportExceptionCodes.isReportTemplateNotAvlbCode(e.getCode())) {
                throw e;
            }
        }

        return flag;
    }

    private Boolean hasModelReportTemplate(final ModelReportTemplateDefinition reportTemplate) {
        Boolean flag = Boolean.FALSE;
        if (reportTemplate != null) {
            flag = Boolean.TRUE;
        }

        return flag;
    }

    @Override
    public TransactionDocument getTransactionDocumentByTxnId(final String transactionId)
            throws SystemException, BusinessException {
        return modelReportTemplateBO.getTransactionDocumentByTxnId(transactionId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.delegate.ModelDelegate#getModelReportTemplateInfo(java.lang.String)
     */
    @Override
    public ModelReportTemplateInfo getModelReportTemplateInfo(final String templateId) throws SystemException, BusinessException {
        return convert(modelReportTemplateBO.findModelReportTemplateDefinition(templateId), ModelReportTemplateInfo.class);
    }

}
