package com.ca.umg.business.version.delegate;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.ResponseWrapper;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.TestBedOutputInfo;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.MappingVersionInfo;
import com.ca.umg.business.version.info.VersionHierarchyInfo;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionStatus;
import com.ca.umg.business.version.info.VersionSummaryInfo;

@SuppressWarnings({ "PMD.UseObjectForClearerAPI", "PMD.ExcessivePublicCount", "PMD.TooManyMethods" })
public interface VersionDelegate {

    public static final String SUCCESS_MESSAGE = "Thank you for clicking on the approval link.<br> Model has been automatically published post your approval.<br> Please find model details below";

    public static final String VERSION_ALREADY_PUBLISHED = "Thank you for clicking on the approval link.<br> However, model has been already approved and published.<br> Please find model details below";

    public static final String FAILED_MESSAGE = "Thank you for clicking on the approval link.<br> Unfortunately, due to technical challenges we could not process your approval. Please retry by clicking on the approval link after 30 minutes.<br> In case issue persists, please contact REALAnalytics product support REALAnalyticsSupport@altisource.com";

    public static final String SENIND_EMAIL_APPROVAL_FAILED = "Email could not be sent. Please retry after sometime or contact RA Support for assistance";

    public static final String UNAUTHORISED_APPROVAL = "Thank you for clicking on the approval link.<br>Unfortunately you do not have the authority to approve model publish for the said tenant.<br>Please contact REALAnalyticsSupport@altisource.com to avail appropriate tenant access.";

    public static final String UNAUTHORISED_SWITCH = "UNAUTHORISED SWITCH";

    public static final String TENANT_SWITCH_SQL_EXCEPTION = "TENANT SWITCH SQL EXCEPTION";

    public static final String TENANT_SWITCH_EXCEPTION_MSG = "Thank you for clicking on the approval link.<br> Unfortunately, due to technical challenges we could not switch tenant.<br> Please retry by clicking on the approval link after 30 minutes.<br> In case issue persists, please contact REALAnalytics product support REALAnalyticsSupport@altisource.com";

    public static final String SWITCH_TENANT = "SWITCH TENANT";

    /**
     * @param tidName
     * @return Returns the list of versions for defined TID Name having status as published or deactivated.
     * @throws BusinessException
     * @throws SystemException
     */
    public KeyValuePair<Boolean, List<String>> getTidMappingStatus(String tidName) throws BusinessException, SystemException;

    /**
     * get the version status for tid name
     * 
     * @param tidName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public Boolean getVersionStatus(String tidName) throws BusinessException, SystemException;

    List<VersionHierarchyInfo> getAllVersions() throws BusinessException, SystemException;

    void update(VersionInfo versionInfo) throws BusinessException, SystemException;

    // single model publishing

    /**
     * get all the version names for auto search in selecting exisitng modelapi
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getAllVersionNames() throws BusinessException, SystemException;

    /**
     * gets the version description for selected version
     * 
     * @param versionName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public String getVersionDescription(String versionName) throws BusinessException, SystemException;

    /**
     * gets the major verssion for passedd version name
     * 
     * @param versionName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<Integer> getMajorVersions(String versionName) throws BusinessException, SystemException;

    /**
     * get the all version deatails pick exisitng
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<VersionInfo> getVersionDetails(String executionLanguage, ModelType modelType, boolean isforReport)
            throws BusinessException, SystemException;

    /**
     * get the version details for the search in modelapi pick existing model library
     * 
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<VersionInfo> searchLibrary(SearchOptions searchOptions, String executionLanguage)
            throws BusinessException, SystemException;

    /**
     * get the model library details
     * 
     * @param searchOptions
     * @param envId
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<MediateModelLibraryInfo> searchNewLibrary(SearchOptions searchOptions, String environmentId)
            throws BusinessException, SystemException;

    /**
     * get the version details for the search in modelapi pick existing model
     * 
     * @param searchOptions
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<VersionInfo> searchIoDefns(SearchOptions searchOptions, String executionLanguage, final ModelType modelType)
            throws BusinessException, SystemException;

    /**
     * gets the version object for passed id
     * 
     * @param versionId
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public VersionInfo getVersionById(String versionId) throws BusinessException, SystemException;

    /**
     * Returns the page of model library names used in UMG versioning.
     * 
     * @param versionInfo
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    PageRecord<String> getAllLibraries(VersionInfo versionInfo) throws BusinessException, SystemException;

    /**
     * Returns the page of records for model names used in UMG versioning.
     * 
     * @param libraryName
     * @param versionInfo
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    PageRecord<String> getAllModelsForLibrary(String libraryName, VersionInfo versionInfo)
            throws BusinessException, SystemException;

    /**
     * Returns the page of records of existing UMG versions
     * 
     * @param libraryName
     * @param modelName
     * @param versionInfo
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    PageRecord<VersionInfo> getAllVersions(String libraryName, String modelName, VersionInfo versionInfo)
            throws BusinessException, SystemException;

    /**
     * API to create a new UMG version
     * 
     * @param version
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public VersionInfo create(VersionInfo version) throws BusinessException, SystemException;

    /**
     * Get all library names.
     * 
     * @return all library names
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getAllLibraryNames() throws BusinessException, SystemException;

    /**
     * Get all library records for the library name.
     * 
     * @param libraryName
     * @return all records for the library name.
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getAllLibraryRecords(String libraryName) throws BusinessException, SystemException;

    /**
     * Get all model names
     * 
     * @return all model names
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getAllModelNames() throws BusinessException, SystemException;

    /**
     * Get all TID version for a model name
     * 
     * @param modelName
     * @return all TID version names
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getAllTidVersionNames(String modelName) throws BusinessException, SystemException;

    /**
     * Get mappings based on modelName
     * 
     * @param modelName
     * @return mappings
     * @throws BusinessException
     * @throws SystemException
     */
    public List<MappingInfo> getTidMappings(String modelName) throws BusinessException, SystemException;

    /**
     * This method will return list of version name along with the major version and minor version for the particular
     * modelLibraryId
     * 
     * @param modelLibraryId
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getUmgVersionsOnModelLibraryId(String modelLibraryId) throws BusinessException, SystemException;

    /**
     * Get all UmgVersions for Tid Name not in deleted state
     * 
     * @param tidName
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getNotDeletedVersions(String tidName) throws BusinessException, SystemException;

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
     * Summary for the version.
     * 
     * @param tenantModelName
     * @return Version summary
     * @throws BusinessException
     * @throws SystemException
     */
    public VersionSummaryInfo getVersionSummary(String tenantModelName) throws BusinessException, SystemException;

    /**
     * Marks the given UMG version as published.
     * 
     * @param emailApproval
     *            TODO
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     */
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    VersionInfo publishVersion(String identifier, String user, String tenantUrl, String authToken, int emailApproval)
            throws BusinessException, SystemException;

    /**
     * marks the version as published on click from publish button from UI
     * 
     * @param identifier
     * @param user
     * @param tenantUrl
     * @param authToken
     * @param emailApproval
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    VersionInfo publishVersionFromUI(String identifier, String user, String tenantUrl, String authToken, int emailApproval)
            throws BusinessException, SystemException;

    /**
     * Marks the given UMG version as deactivated.
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     */
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    VersionInfo deactivateVersion(String identifier, String user, String tenantUrl, String authToken)
            throws BusinessException, SystemException;

    /**
     * 
     * @param libraryName
     * @return
     */
    List<MappingInfo> listAllLibraryRecNameDescs(String libraryName) throws BusinessException, SystemException;

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
    List<String> getModelNamesForLibraryNameAndCharsInNameOrDescription(String libraryName, String searchStr,
            boolean isDescending) throws BusinessException, SystemException;

    /**
     * This method would initiate the test for a version.
     * 
     * @param payloadJson
     *            carries the runtime input data
     * @param tenantUrl
     * @return
     * @throws BusinessException
     * @throws SystemException
     */

    TestBedOutputInfo versionTest(String payloadJson, String tenantUrl, String authToken, String versionId)
            throws BusinessException, SystemException;

    Version markAsTested(String versionId) throws BusinessException, SystemException;

    void delete(String id) throws BusinessException, SystemException;

    void deleteVersion(String id) throws BusinessException, SystemException;

    /**
     * 
     * Returns a POI workbook object that represents the excel input template for a mapping
     * 
     * @param mvi
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public Workbook exportExcel(MappingVersionInfo mvi) throws BusinessException, SystemException;

    /**
     * This method will get all UmgVersions for Tid Name and with status:published,deactivated
     */
    public List<Version> findVersionWithTidNameAndStatusPublishedORDeactivated(String tidName)
            throws BusinessException, SystemException;

    /**
     * This method will retrieve all major version names with search criteria and return paginated data back with page info
     */
    public ResponseWrapper<List<String>> findAllVersionName(SearchOptions searchOptions)
            throws BusinessException, SystemException;

    /**
     * This method will retrieve all major&minor version and return paginated data back with page info
     */
    public PageRecord<VersionInfo> findAllversionByVersionName(String modelName, SearchOptions searchOptions)
            throws BusinessException, SystemException;

    public Map<String, Object> getVersionMetrics(VersionMetricRequestInfo versionReq) throws BusinessException, SystemException;

    public VersionExecInfo getVersionExecutionEnvInfo(String name, Integer majorVersion, Integer minorVersion)
            throws SystemException;

    public List<SupportPackage> getSupportPackagesForVersion(String name, Integer majorVersion, Integer minorVersion)
            throws SystemException;

    public long getVersionCountByName(String versionName) throws SystemException;

    long getVrsnCountByNameAddArtifact(String versionName) throws SystemException;

    public VersionInfo searchVersionByName(String name) throws SystemException;

    public List<String> getAllModelsbyEnvironment(String executionEnvironment) throws SystemException;

    /**
     * This method will retrieve all major&minor version and return the list for the tenant
     */
    List<VersionInfo> findAllVersions(SearchOptions searchOptions) throws BusinessException, SystemException;

    /**
     * @param searchOptions
     * @param executionLanguage
     * @return List of versionInfos
     * @throws BusinessException
     * @throws SystemException
     */
    List<VersionInfo> searchReports(SearchOptions searchOptions, String executionLanguage)
            throws BusinessException, SystemException;

    /**
     * Marks the given UMG version as approval pending.
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     */
    public VersionInfo updateModelApprovalStatus(final String identifier, final String user, final VersionStatus status)
            throws BusinessException, SystemException;

    boolean isVersionPublished(String versionID) throws BusinessException, SystemException;

    public VersionInfo findOneversion(String versionID) throws BusinessException, SystemException;

    public VersionInfo getVersionDetails(String name, int majorVersion, int minorVersion)
            throws BusinessException, SystemException;
}
