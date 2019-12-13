/**
 * 
 */
package com.ca.umg.business.version.bo;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionStatus;

/**
 * @author kamathan
 *
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface VersionBO {

    /**
     * Returns list if all UMG versions
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<Version> getAllVersions() throws BusinessException, SystemException;

    Version getVersionDetails(String identifier) throws BusinessException, SystemException;

    /**
     * 
     * Returns a list of mappings, if any, that a model is involved in. List can be retrieved by getting the value for the 'true'
     * boolean object
     * 
     * 
     * @param modelInfo
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    // Map<Boolean, List<MappingInfo>> findAllMappings(ModelInfo modelInfo) throws BusinessException, SystemException;

    /**
     * Updates the status of the given UMG version as published.
     * 
     * @param version
     * @param emailApproval TODO
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    Version publishVersion(Version version, String user, int emailApproval) throws BusinessException, SystemException;

    /**
     * Updates the status of the given UMG version as Deactivated.
     * 
     * @param version
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    Version markVersionAsDeactivated(Version version, String user) throws BusinessException, SystemException;

    /**
     * Lists all model library names available in Versions
     * 
     * @param versionInfo
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    Page<String> getAllLibraries(VersionInfo versionInfo) throws BusinessException, SystemException;

    /**
     * Lists all model names for a given library name in Versions
     * 
     * @param libraryName
     * @param sortDirection
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    Page<String> getAllModelsForLibrary(String libraryName, VersionInfo versionInfo) throws BusinessException, SystemException;

    /**
     * Lists all Versions for given model name and library name in Versions
     * 
     * @param libraryName
     * @param modelName
     * @param sortDirection
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    Page<Version> getAllVersions(String libraryName, String modelName, VersionInfo versionInfo) throws BusinessException,
            SystemException;

    /**
     * 
     * Returns a list of mappings, if any, that a model is involved in. List can be retrieved by getting the value for the 'true'
     * boolean object
     * 
     * 
     * @param Model
     * @return KeyValuePair<Boolean,List<Mapping>>
     * @throws BusinessException
     * @throws SystemException
     */
    KeyValuePair<Boolean, List<Mapping>> findAllMappings(Model model) throws BusinessException, SystemException;

    /**
     * Create UMG version for a library record and a mapping name.
     * 
     * @param version
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    Version create(Version version) throws BusinessException, SystemException;

    /**
     * Get all major versions.
     * 
     * @param tenantModelName
     * @return all major versions.
     * @throws BusinessException
     * @throws SystemException
     */
    List<Integer> getAllMajorVersions(String tenantModelName) throws BusinessException, SystemException;

    /**
     * Get version by mapping name and model library UMG name.
     * 
     * @param libraryUmgName
     * @param mappingName
     * @return version if existing
     * @throws BusinessException
     * @throws SystemException
     */
    Version findByNameAndMappingNameAndModelLibraryUmgName(String name, String mappingName, String libraryUmgName)
            throws BusinessException, SystemException;

    /**
     * @param tidName
     * @return Returns the list of versions for defined TID Name having status as published or deactivated.
     * @throws BusinessException
     * @throws SystemException
     */
    List<Version> findByMappingName(String tidName) throws BusinessException, SystemException;

    /**
     * gets the version status for tid name
     * 
     * @param tidName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> getVersionStatus(String tidName) throws BusinessException, SystemException;

    /**
     * This method will return a Map which contains all the umg versions which are linked to the modelLibraryId
     * 
     * @param modelLibraryId
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> getAllUmgVersionsOnModelLibraryId(String modelLibraryId) throws BusinessException, SystemException;

    /**
     * Returns the list of versions for defined TID Name having status in not deleted state.
     * 
     * @param tidName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> findNotDeletedVersions(String tidName) throws BusinessException, SystemException;

    /**
     * Update the UMG Version.
     * 
     * @param version
     * @throws BusinessException
     * @throws SystemException
     */
    void update(Version version) throws BusinessException, SystemException;

    /**
     * 
     * @param name
     * @param majorVersion
     * @param minorVersion
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    Version findByNameAndVersion(String name, Integer majorVersion, Integer minorVersion) throws BusinessException,
            SystemException;

    /**
     * Get all tenant model names.
     * 
     * @return all tenant model names.
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getAllTenantModelNames() throws BusinessException, SystemException;

    /**
     * Returns MODEL_EXECUTION_ENVIRONMENTS map from cache
     * 
     * @param
     * @return Set
     * @throws SystemException
     */
    public Map<String, List<String>> getEnvironments() throws SystemException;

    /**
     * Get tenant model description for tenant model name.
     * 
     * @param tenantModelName
     * @return tenant model description.
     * @throws BusinessException
     * @throws SystemException
     */
    public String getTenantModeldescription(String tenantModelName) throws BusinessException, SystemException;

    /**
     * updates the status of the Versions to saved
     * 
     * @param tidName
     * @throws BusinessException
     * @throws SystemException
     */
    void markVersionAsSaved(String version) throws BusinessException, SystemException;

    /**
     * gets the list of versions having status as TESTED for given tid name
     * 
     * @param tidName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> getTestedVersions(String tidName) throws BusinessException, SystemException;

    /**
     * Get all model names for given library name and search characters present either in name or description.
     * 
     * @param libraryName
     * @param search
     *            string
     * @return model names
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> getModelNamesForLibraryNameAndCharsInNameOrDescription(String libraryName, String searchStr, boolean isDescending)
            throws BusinessException, SystemException;

    /**
     * updates the status of the Versions to TESTED
     * 
     * @param version
     *            to be updated.
     * @throws BusinessException
     * @throws SystemException
     */
    Version markVersionAsTested(Version version) throws BusinessException, SystemException;

    void delete(String id) throws BusinessException, SystemException;

    List<Version> searchVersions(String name, Integer majorVersion, Integer minorVersion) throws BusinessException,
            SystemException;

    /**
     * This method will get all UmgVersions for Tid Name and with status:published,deactivated
     */
    public List<Version> findVersionWithTidNameAndStatusPublishedORDeactivated(String tidName) throws BusinessException,
            SystemException;

    /**
     * This method will retrieve all major version names with search criteria and return paginated data back with page info
     */
    public List<Version> findAllVersionName(SearchOptions searchOptions) throws BusinessException, SystemException;

    /**
     * This method will retrieve all major&minor version and return paginated data back with page info
     */
    public Page<Version> findAllversionByVersionName(String modelName, SearchOptions searchOptions) throws BusinessException,
            SystemException;

    /**
     * this method returns all the versions(wiht IoDefn and jarName) for pick existing in single model publish
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<VersionInfo> getVersionDetailsForLanguage(final String executionEnvironment, final ModelType modelType) throws BusinessException, SystemException;

    /**
     * this method returns the versions(with jarName) for search string passed in {@link SearchOptions#getSearchText()}
     * 
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<VersionInfo> searchLibraries(SearchOptions searchOptions, String executionLanguage) throws BusinessException,
            SystemException;

    /**
     * get the model library details
     * 
     * @param searchOptions
     * @param envId
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<MediateModelLibraryInfo> searchNewLibraries(SearchOptions searchOptions, String environmentId)
            throws BusinessException, SystemException;

    /**
     * this method returns the versions(with iodefnName) for search string passed in {@link SearchOptions#getSearchText()}
     * 
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<VersionInfo> searchIoDefns(SearchOptions searchOptions, String executionLanguage, final ModelType modelType) throws BusinessException,
            SystemException;

    Map<String, Object> getVersionMetrics(VersionMetricRequestInfo versionReq) throws BusinessException, SystemException;

    Version searchVersionByName(String name) throws SystemException;

    List<String> getAllTenantNamesByEnv(String executionEnvironment) throws SystemException;

    /**
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<VersionInfo> findAllVersions(SearchOptions searchOptions) throws BusinessException, SystemException;
    
    /**
     * This method use to get the model reports for the given language
     * @param executionEnvironment
     * @param modelType
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<VersionInfo> getModelReportDetailsForLanguage(String executionEnvironment, ModelType modelType) throws BusinessException, SystemException;
    
    /**
     * @param searchOptions
     * @param executionEnvironment
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<VersionInfo> searchReports(SearchOptions searchOptions,String executionEnvironment) throws BusinessException, SystemException;
    /**
     * Updates the status of the given UMG version as approval pending.
     * 
     * @param version
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public Version updateModelApprovalStatus(final Version version, final String user, final VersionStatus status) throws BusinessException, SystemException;
    
    
    public Version findOneVersion(String versionId) throws BusinessException, SystemException;
}
