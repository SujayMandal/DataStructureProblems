/*
 * ModelDelegate.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.model.delegate;

import java.io.InputStream;
import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.info.ModelMappingInfo;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryExecPackageMappingInfo;
import com.ca.umg.business.model.info.ModelLibraryHierarchyInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.report.model.ModelReportTemplateDefinition;
import com.ca.umg.report.model.ModelReportTemplateInfo;

/**
 * DOCUMENT ME!
 **/
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount" })
public interface ModelDelegate {

    /**
     * Returns paged records for filtered models
     * 
     * @param SearchOptions
     * @return PageRecord<{@link ModelInfo}>
     * @throws BusinessException
     * @throws SystemException
     **/

    PageRecord<ModelInfo> getUniqueModelInfos(SearchOptions searchOptions) throws BusinessException, SystemException;

    /**
     * @param SearchOptions
     * @param modelName
     * @return List<{@link ModelInfo}>
     * @throws BusinessException
     * @throws SystemException
     **/
    List<ModelInfo> getAllFilteredModelInfos(SearchOptions searchOptions, String modelName)
            throws BusinessException, SystemException;

    /**
     * Returns paged records for filtered models
     * 
     * @param SearchOptions
     * @return PageRecord<{@link ModelInfo}>
     * @throws BusinessException
     * @throws SystemException
     **/

    PageRecord<ModelLibraryInfo> getUniqueModelLibraries(SearchOptions searchOptions) throws BusinessException, SystemException;

    /**
     * @param SearchOptions
     * @param modelName
     * @return List<{@link ModelInfo}>
     * @throws BusinessException
     * @throws SystemException
     **/
    List<ModelLibraryInfo> getAllFilteredModelLibraries(SearchOptions searchOptions, String modelLibName)
            throws BusinessException, SystemException;

    /**
     * Returns model for the given identifier.
     * 
     * @param identifier
     *            model id
     * 
     * @return {@link ModelInfo}
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    ModelInfo getModelDetails(String identifier) throws BusinessException, SystemException;

    /**
     * Saves the given model information.
     * 
     * The model artifacts such as documentations are stored to the SAN location.
     * 
     * @param modelInfo
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    ModelInfo createModel(ModelInfo modelInfo) throws BusinessException, SystemException;

    // TODO remove above method of create model
    ModelInfo createModelWithoutValidn(ModelInfo modelInfo) throws BusinessException, SystemException;

    void deleteModel(String identifier) throws BusinessException, SystemException;

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
    void validateModelXml(InputStream modelXml) throws SystemException, BusinessException;

    /**
     * Validates the model library checksum for jar
     * 
     * @param ModelLibraryInfo
     * @return
     * @return Boolean
     * @throws BusinessException
     * @throws SystemException
     */
    // void validateCheckSum(byte[] modelLibJar, String checksumVal) throws SystemException, BusinessException;

    /**
     * Return the list of all the created libraries.
     * 
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    List<ModelLibraryInfo> findAllLibraries() throws SystemException, BusinessException;

    /**
     * Return the list of all the created libraries in a hierarchy format.
     * 
     * @return List - list of all the model library hierarchy.
     * @throws BusinessException
     * @throws SystemException
     */
    List<ModelLibraryHierarchyInfo> getModelLibraryHierarchyInfos() throws BusinessException, SystemException;

    /**
     * Saves the given model library information.
     * 
     * The model artifacts jar are stored to the SAN location.
     * 
     * @param modelLibraryInfo
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     * 
     **/
    ModelLibraryInfo createModelLibrary(ModelLibraryInfo modelLibraryInfo) throws BusinessException, SystemException;

    /**
     * This method used to validate the checksum
     * 
     * @param modelLibraryInfo
     * @throws BusinessException
     * @throws SystemException
     * 
     */
    void validateCheckSum(ModelLibraryInfo modelLibraryInfo) throws BusinessException, SystemException;

    /**
     * This method used to validate duplicate model name
     * 
     * @param modelLibraryInfo
     * @throws BusinessException
     * @throws SystemException
     * 
     */
    void checkJarAvailability(ModelLibraryInfo modelLibraryInfo) throws BusinessException, SystemException;

    /**
     * This method used to store the jar in san and create the model library record in db
     * 
     * @param modelLibraryInfo
     * @return
     * @throws BusinessException
     * @throws SystemException
     * 
     */
    ModelLibraryInfo createModelLibraryWithOutValidation(ModelLibraryInfo modelLibraryInfo)
            throws BusinessException, SystemException;

    /**
     * @param modelLibraryId
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    ModelLibraryInfo getModelLibraryDetails(String modelLibraryId) throws SystemException, BusinessException;

    /**
     * @param library
     * @throws SystemException
     * @throws BusinessException
     */
    void deleteModelLibrary(String modelLibraryId) throws SystemException, BusinessException;

    /**
     * Fetches model artifact from the san location.
     * 
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    List<ModelArtifact> getModelArtifacts(String modelId) throws SystemException, BusinessException;

    List<ModelArtifact> getModelArtifactsFromDownloadModelDoc(String modelId) throws SystemException, BusinessException;

    /**
     * Get all the model names.
     * 
     * @return model names
     * @throws SystemException
     * @throws BusinessException
     */
    List<String> getAllModelNames() throws SystemException, BusinessException;

    /**
     * Get all library names.
     * 
     * @return all library names.
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> getAllLibraryNames() throws BusinessException, SystemException;

    /**
     * Get derived model library names.
     * 
     * @param libraryName
     * @return list of derived model library names.
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> getListOfDerivedModelLibraryNames(String libraryName) throws BusinessException, SystemException;

    ModelMappingInfo getAllVersionNamesForModel(String identifier) throws SystemException, BusinessException;

    /**
     * Get model library based on UMG name.
     * 
     * @param umgName
     * @return model library.
     * @throws BusinessException
     * @throws SystemException
     */
    ModelLibraryInfo findByUmgName(String umgName) throws BusinessException, SystemException;

    /**
     * 
     * @param libraryName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<ModelLibrary> findMappingInfoByLibraryNamName(String libraryName) throws BusinessException, SystemException;

    /**
     * Method returns the model for the modelId
     * 
     * @param modelId
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    Model getModelXML(String modelId) throws SystemException, BusinessException;

    byte[] getModelExcel(Model model) throws SystemException, BusinessException;

    /**
     * This method Returns List of artifact which are belongs to the Model Library Id
     * 
     * @param modelLibraryId
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    List<ModelArtifact> getModelLibraryArtifacts(String modelLibraryId) throws SystemException, BusinessException;

    List<ModelArtifact> getModelLibraryArtifactsFromDownloadLibraryJar(String modelLibraryId)
            throws SystemException, BusinessException;

    List<ModelArtifact> getModelLibraryArtifactsFromDownloadModelLibManifest(String modelLibraryId)
            throws SystemException, BusinessException;

    List<ModelLibraryInfo> searchModelLibraryByJarAndChecksum(String jarName, String checksum)
            throws BusinessException, SystemException;

    void storeModelLibraryArtifacts(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException;

    void storeModelArtifacts(ModelInfo modelInfo) throws SystemException, BusinessException;

    /**
     * @param modelInfo
     * @throws SystemException
     * @throws BusinessException
     *             This method is used to store the excel definition file
     */
    void storeModelDefArtifacts(ModelInfo modelInfo) throws SystemException, BusinessException;

    /**
     * This method used to get addon packages based on the model environment,language and version
     * 
     * @param modelEnvId
     * @param environment
     * @param version
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    void getModelAddonPackages(String modelExecEnvName, List<ModelLibraryExecPackageMappingInfo> supportPackageInfos,
            String execEnv, List<Error> errors);

    /**
     * This method used to get the model packages which are added manually
     * 
     * @param modelEnvId
     * @param environment
     * @param version
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    List<ModelLibraryInfo> getModelPackageLibraries(String modelExecEnvId) throws BusinessException, SystemException;

    /**
     * 
     * This method used to store manifest file
     * 
     * @param modelLibraryInfo
     * @throws SystemException
     * @throws BusinessException
     */
    void storeModelLibraryManifestFile(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException;

    void saveSupportModelExecPackages(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException;

    void deleteSupportModelExecPackages(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException;

    void moveRmodelFromTemptoSan(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException;

    public ModelReportTemplateInfo createModelReportTemplate(final ModelReportTemplateInfo reportTemplateInfo)
            throws SystemException;

    public void deleteModelReportTemplate(final String modelReportTemplateId) throws SystemException, BusinessException;

    public ModelReportTemplateDefinition getModelReportTemplate(final String versionId) throws SystemException, BusinessException;

    public ModelReportTemplateDefinition getModelReportTemplateFromDownloadReportTemplate(final String versionId)
            throws SystemException, BusinessException;

    public ModelReportTemplateDefinition getModelRprtTemplateVerListScrn(final String versionId)
            throws SystemException, BusinessException;

    public ModelReportTemplateInfo uploadModelReportTemplate(final ModelReportTemplateInfo reportTemplateInfo)
            throws SystemException, BusinessException;

    public ModelReportTemplateDefinition getModelReportTemplateByTxnId(final String transactionId)
            throws SystemException, BusinessException;

    public ModelReportTemplateDefinition getModelReportTemplate(final String versionName, final String fullVersion)
            throws SystemException, BusinessException;

    public Boolean hasModelReportTemplate(final String versionId) throws SystemException, BusinessException;

    public Boolean hasModelReportTemplate(final String versionName, final String fullVersion)
            throws SystemException, BusinessException;

    public TransactionDocument getTransactionDocumentByTxnId(final String transactionId)
            throws SystemException, BusinessException;

    /**
     * This method used to get the report template info based on template id
     * 
     * @param templateId
     * @return ModelReportTemplateInfo
     * @throws SystemException
     * @throws BusinessException
     */
    ModelReportTemplateInfo getModelReportTemplateInfo(String templateId) throws SystemException, BusinessException;

}
