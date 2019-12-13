package com.ca.umg.business.mapping.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;

public interface MappingBO {
    List<Mapping> listAll() throws SystemException, BusinessException;

    Mapping find(String identifier) throws SystemException, BusinessException;

    Mapping create(Mapping mapping) throws SystemException, BusinessException;

    Mapping findByName(String tidName) throws SystemException, BusinessException;

    MappingInput createMappingInput(MappingInput mappingInput) throws SystemException, BusinessException;

    MappingOutput createMappingOutput(MappingOutput mappingOutput) throws SystemException, BusinessException;

    MappingInput findInputByMapping(Mapping mapping) throws SystemException, BusinessException;

    MappingOutput findOutputByMapping(Mapping mapping) throws SystemException, BusinessException;

    boolean deleteTidMapping(String tidName) throws SystemException, BusinessException;

    /**
     * gets the status of the mapping
     * 
     * @param tidName
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    String getMappingStatus(String tidName) throws SystemException, BusinessException;

    /**
     * Get list of mapping names.
     * 
     * @param modelName
     * @return list of mapping names.
     * @throws SystemException
     * @throws BusinessException
     */
    List<String> getListOfMappingNames(String modelName) throws SystemException, BusinessException;

    /**
     * find mappings by model name.
     * 
     * @param modelName
     * @return list of mappings
     * @throws SystemException
     * @throws BusinessException
     */
    List<Mapping> findByModelName(String modelName) throws SystemException, BusinessException;

    /**
     * find mappings by model name and having status as finalized
     * 
     * @param modelName
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    public List<Mapping> findFinalizedMappings(String modelName) throws SystemException, BusinessException;

    /**
     * Get list of mapping names based upon the modelId.
     * 
     * @param modelId
     * @return list of mapping names.
     * @throws SystemException
     * @throws BusinessException
     */
    List<String> getListOfMappingNamesById(String modelId) throws SystemException, BusinessException;

    /**
     * This method will return List of MappingsCopyInfo
     * 
     * @return
     */
    public List<MappingsCopyInfo> getAllTidsForCopy() throws BusinessException;

    /**
     * Returns the mapping output for the given mapping name
     * 
     * @param mappingName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    MappingOutput getMappingOutputByMappingName(String mappingName) throws BusinessException, SystemException;

    /**
     * This method will retrieve all major version names with search criteria
     */
    public List<Mapping> findAllMappings(SearchOptions searchOptions) throws BusinessException, SystemException;

}
