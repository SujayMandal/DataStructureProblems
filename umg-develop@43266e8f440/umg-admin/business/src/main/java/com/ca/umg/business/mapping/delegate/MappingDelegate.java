package com.ca.umg.business.mapping.delegate;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.ResponseWrapper;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mapping.info.MappingHierarchyInfo;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;
import com.ca.umg.business.mapping.info.QueryLaunchInfo;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.mid.extraction.info.TenantIODefinition;
import com.ca.umg.business.validation.ValidationError;

public interface MappingDelegate {
    List<MappingInfo> listAll() throws BusinessException, SystemException;

    MappingInfo find(String identifier) throws BusinessException, SystemException;

    // boolean deploy(MappingInfo mappingInfo) throws BusinessException,
    // SystemException;
    List<MappingHierarchyInfo> getMappingHierarchyInfos() throws BusinessException, SystemException;

    /**
     * This method would extract the mapping information first time till the same is not saved.
     * 
     * @param derievedModelNm
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    MappingDescriptor generateMapping(String derievedModelNm) throws BusinessException, SystemException;

    /**
     * This method would extract the mapping information and copy relevant fields from the provided reference TID. Only elements
     * that haven't changed are copied with their mapping
     * 
     * @param derievedModelNm
     *            , the model umgName to create the MID
     * @param tidNameToCopy
     *            , the reference TID to copy from.
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    MappingDescriptor generateMapping(String derievedModelNm, String derievedTidNm, String tidNameToCopy, String description)// NOPMD
            throws BusinessException, SystemException;// NOPMD

    MappingDescriptor readMapping(String derievedTidName) throws BusinessException, SystemException;

    boolean deleteMapping(String tidName) throws BusinessException, SystemException;

    KeyValuePair<String, List<ValidationError>> saveMappingDescription(MappingDescriptor mappingDescriptor,
            String derievedModelNm, String validate) throws BusinessException, SystemException;

    QueryLaunchInfo createInputMapForQuery(String type, String tidName) throws BusinessException, SystemException;

    Map<String, Boolean> isReferenced(List<String> tidParamNames, String tidName, String mappingType) throws SystemException,
            BusinessException;

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
     * Get mappings based on model name.
     * 
     * @param modelName
     * @return mappings
     * @throws SystemException
     * @throws BusinessException
     */
    List<MappingInfo> findByModelName(String modelName) throws SystemException, BusinessException;

    /**
     * Get mapping based on TID name.
     * 
     * @param tidName
     * @return mapping
     * @throws SystemException
     * @throws BusinessException
     */
    MappingInfo findByName(String tidName) throws SystemException, BusinessException;

    /**
     * This method will return the List of MappingsCopyInfo for TidCopy
     * 
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    List<MappingsCopyInfo> getTidListForCopy() throws SystemException, BusinessException;

    List<TidIoDefinition> getTidIoDefinitions(String tidName, boolean isTestBed) throws SystemException, BusinessException;
    
    List<TidIoDefinition> getTidIoDeFnsTestBed(String tidName, boolean isTestBed) throws SystemException, BusinessException;

    /**
     * Reads all input captured on the UI and prepares a Json payload for runtime testing.
     * 
     * @param definitions
     * @param modelName
     * @param majorVersion
     * @param minorVersion
     * @param date
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    String createRuntimeInputJson(List<TidIoDefinition> definitions, String modelName, int majorVersion, int minorVersion,
            String date, Boolean isTestForVerCreation,Boolean isOpValidation,Boolean isAcceptableValues,Boolean storeRLogs) throws SystemException, BusinessException;

    /**
     * gets the status of the mapping
     * 
     * @param tidName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    String getMappingStatus(String tidName) throws BusinessException, SystemException;

    MappingInfo updateMappingStatus(String identifier, String status) throws SystemException, BusinessException;
	/**
	 * This method will retrieve all mapping and return paginated data back with pageinfo
	 * It will be grouped based on model
	 */
    public ResponseWrapper<List<MappingHierarchyInfo>> findAllMappings(SearchOptions searchOptions) throws BusinessException, SystemException;

    public TenantIODefinition getTIDParams(String tidName) throws BusinessException, SystemException;

}
