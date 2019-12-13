/*
 * ModelBO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.model.bo;

import java.io.InputStream;
import java.util.List;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.execution.entity.ModelExecutionPackage;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 */
public interface ModelBO {

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
    // TODO rename
    public List<Model> listAll() throws BusinessException, SystemException;
    
    /**
     * List only unique Models based on filters and page information.
     * 
     * @param SearchOptions
     * @return PageRecord<{@link ModelInfo}>
     * @throws BusinessException
     * @throws SystemException
     **/
    
    PageRecord<ModelInfo> listUniqueFilteredModels(SearchOptions searchOptions) throws BusinessException, SystemException;
    
    /**
     * List models based on filters and page information for given modelName.
     * 
     * @param SearchOptions
     * @param modelName
     * @return List<{@link Model}>
     * @throws BusinessException
     * @throws SystemException
     **/
    
    List<Model> listAllFilteredModels(SearchOptions searchOptions, String modelName) throws BusinessException, SystemException;
    
    /**
     * List only unique Models based on filters and page information.
     * 
     * @param SearchOptions
     * @return PageRecord<{@link ModelLibraryInfo}>
     * @throws BusinessException
     * @throws SystemException
     **/
    
    PageRecord<ModelLibraryInfo> listUniqueFilteredLibraries(SearchOptions searchOptions) throws BusinessException, SystemException;
    
    /**
     * List models based on filters and page information for given modelName.
     * 
     * @param SearchOptions
     * @param modelName
     * @return List<{@link ModelLibrary}>
     * @throws BusinessException
     * @throws SystemException
     **/
    
    List<ModelLibrary> listAllFilteredLibraries(SearchOptions searchOptions, String libraryName) throws BusinessException, SystemException;

    /**
     * Saves new model version.
     * 
     * @param model
     * @return
     * @throws BusinessException
     * @throws SystemException
     **/
    public Model createModel(Model model) throws BusinessException, SystemException;

    /**
     * Returns the model details of the model identified by the given identifier.
     * 
     * @param identifier
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public Model getModelDetails(String identifier) throws BusinessException, SystemException;
    
    
    byte[] getModelExcel(Model model) throws SystemException;
    

    /**
     * Deletes the model details.
     * 
     * @param model
     * @throws BusinessException
     * @throws SystemException
     */
    void deleteModel(Model model) throws BusinessException, SystemException;

    void validateModelXml(InputStream modelXml) throws SystemException, BusinessException;

    void checkJarAvailability(byte[] modelLibJar, String encodingType, String jarName) throws SystemException, BusinessException;

    void validateCheckSum(byte[] modelLibJar, String checksumVal, String encodingType) throws SystemException, BusinessException;

    List<ModelLibrary> findAllModelLibraries() throws SystemException, BusinessException;

    ModelLibrary createModelLibrary(ModelLibrary library) throws SystemException, BusinessException;

    ModelLibrary findModelLibrary(String modelLibraryId) throws SystemException, BusinessException;

    void deleteModelLibrary(ModelLibrary library) throws SystemException, BusinessException;

    Model getModelByName(String modelDerievedName) throws BusinessException, SystemException;

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

    /**
     * Get all model names.
     * 
     * @return model names
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> getAllModelNames() throws BusinessException, SystemException;

    /**
     * Get model library based on UMG name.
     * 
     * @param umgName
     * @return model library.
     * @throws BusinessException
     * @throws SystemException
     */
    ModelLibrary findByUmgName(String umgName) throws BusinessException, SystemException;

    /**
     * 
     * @param libraryName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<ModelLibrary> findMappingInfoByLibraryNamName(String libraryName) throws BusinessException, SystemException;

    List<ModelLibrary> searchModelLibraryByJarAndChecksum(String jarName, String checkSum) throws BusinessException,
            SystemException;
    
    byte[] getModelTemplate(String modelName, final ModelType modelType) throws BusinessException, SystemException;

    /**
     * This method used to get the modelExecutionenvironment based on execution language adn version
     * @param execLanguage
     * @param environmentVersion
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    ModelExecutionEnvironment getModelExecutionEnvironment(String execLanguage, String environmentVersion);

    /**
     * This method used to get the modelExecutionenvironment based on execution language adn version
     * 
     * @param execLanguage
     * @param environmentVersion
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    @SuppressWarnings("PMD")
    ModelExecutionPackage getModelExecutionPackage(String modelExecEnvName, String packageFolder,
            String packageVersion,String execEnv);
    /**
     * This method used to get the modelExecutionenvironment based on execution language adn version
     * 
     * @param execLanguage
     * @param environmentVersion
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    ModelExecutionPackage getBaseExecutionPackage(String modelExecEnvName, String packageFolder);

    /**
     * This method used to delete the support packages for the model library
     * 
     * @param modelLibrary
     */
    void deleteSupportPackages(ModelLibrary modelLibrary);

    /**
     * This method used to get the Model Packages which are added manually
     * 
     * @param modelLibrary
     */
    List<ModelLibraryInfo> getModelPackageLibraries(String modelExecEnvName) throws BusinessException, SystemException;
   
 
}
