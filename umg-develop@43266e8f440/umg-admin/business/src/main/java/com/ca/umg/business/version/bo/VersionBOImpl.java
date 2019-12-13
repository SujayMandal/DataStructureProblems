
/**
 * 
 */
package com.ca.umg.business.version.bo;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

import com.ca.framework.core.bo.AbstractBusinessObject;
import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.dao.MappingDAO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.modelexecenvs.ModelExecEnvironmentProvider;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.dao.VersionContainerDAO;
import com.ca.umg.business.version.dao.VersionDAO;
import com.ca.umg.business.version.dao.VersionMetricsDAO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionStatus;
import com.ca.umg.business.version.specification.VersionListingSpecification;
import com.ca.umg.business.version.specification.VersionSpecification;
import com.ca.umg.report.model.ModelReportTemplateInfo;

/**
 * @author kamathan
 * 
 */
@Named
@SuppressWarnings("PMD.TooManyMethods")
public class VersionBOImpl extends AbstractBusinessObject implements VersionBO {

    private static final int EQUAL = 0;

    private static final long serialVersionUID = -3297209964380644980L;

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionBOImpl.class);
    
    private static final String MODEL_EXE_NAME = "MODEL_EXEC_ENV_NAME";

    @Inject
    private VersionDAO versionDAO;

    @Inject
    private VersionMetricsDAO versionMetricsDAO;

    @Inject
    private MappingDAO mappingDAO;

    @Inject
    private VersionContainerDAO versionContainerDAO;

    @Inject
    private ModelExecEnvironmentProvider modelExecEnvironmentProvider;

    public static final Comparator<Version> VERSION_LATEST_LAST_ORDER = new Comparator<Version>() {
        public int compare(Version ver1, Version ver2) {
            int res = Integer.compare(ver1.getMajorVersion(), ver2.getMajorVersion());
            if (EQUAL == res) {
                res = Integer.compare(ver1.getMinorVersion(), ver2.getMinorVersion());
            }
            return res;
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.bo.VersionBO#getAllVersions()
     */
    @Override
    public List<Version> getAllVersions() throws BusinessException, SystemException {
        Order modelLibraryOrder = new Order(Sort.Direction.ASC, "modelLibrary.name");
        Order modelOrder = new Order(Sort.Direction.ASC, "mapping.model.name");
        Order majorVerOrder = new Order(Sort.Direction.DESC, "majorVersion");
        Order minorVerOrder = new Order(Sort.Direction.DESC, "minorVersion");
        Sort sort = new Sort(modelLibraryOrder, modelOrder, majorVerOrder, minorVerOrder);
        return versionDAO.findAll(sort);
    }

    @Override
    public Version getVersionDetails(String identifier) throws BusinessException, SystemException {
        return versionDAO.findOne(identifier);
    }

    /**
     * Lists all model library names available in Versions
     * 
     * @param sortDirection
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    @Override
    public Page<String> getAllLibraries(VersionInfo versionInfo) throws BusinessException, SystemException {
        Pageable pageRequest = getPagingInformation(versionInfo, "UPPER(modelLibrary.name)");
        return versionDAO.findAllLibraries(AdminUtil.getLikePattern(versionInfo.getSearchString()), pageRequest);
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
        Order majorVerOrder = null;
        Order minorVerOrder = null;
        if (StringUtils.isBlank(newSortColumn)) {
            majorVerOrder = new Order(Sort.Direction.DESC, "majorVersion");
            minorVerOrder = new Order(Sort.Direction.DESC, "minorVersion");
            order = new Order(direction, "name").ignoreCase();
            sortOrders = new Order[] { order, majorVerOrder, minorVerOrder };
        } else {
            order = new Order(direction, newSortColumn).ignoreCase();
            sortOrders = new Order[] { order };
        }

        Sort sort = new Sort(sortOrders);
        return new PageRequest(pagingInfo.getPage() == 0 ? 0 : pagingInfo.getPage() - 1, pagingInfo.getPageSize(), sort);
    }

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
    @Override
    public Page<Version> getAllVersions(String libraryName, String modelName, VersionInfo versionInfo) throws BusinessException,
            SystemException {
        String sortColumn = null;
        String searchString = EMPTY;
        if (isNotEmpty(versionInfo.getSortColumn())) {
            sortColumn = versionInfo.getSortColumn();
        }
        if (isNotEmpty(versionInfo.getSearchString())) {
            searchString = versionInfo.getSearchString();
        }
        Pageable pageRequest = getPagingInformation(versionInfo, sortColumn);
        return versionDAO.findByLibraryNameAndModelNameAndSearchTextInNameOrDescription(libraryName, modelName,
                searchString.toLowerCase(Locale.getDefault()), pageRequest);
    }

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
    @Override
    public Page<String> getAllModelsForLibrary(String libraryName, VersionInfo versionInfo) throws BusinessException,
            SystemException {
        Pageable pageRequest = getPagingInformation(versionInfo, "UPPER(mapping.model.name)");
        return versionDAO.findAllModels(libraryName, pageRequest);
    }

    /**
     * Returns MODEL_EXECUTION_ENVIRONMENTS map from cache
     * 
     * @param
     * @return Set
     * @throws SystemException
     */
    @Override
    public Map<String, List<String>> getEnvironments() throws SystemException {
        return modelExecEnvironmentProvider.getExecutionEnvironmentMap();
    }

    @Override
    public Version create(Version version) throws BusinessException, SystemException {
        Version newVersion = version;
        try {
            if (isCreateMajorVersion(newVersion)) {
                newVersion.setMajorVersion(getCurrentMaxMajorVersion(newVersion) + 1);
                newVersion.setMinorVersion(0);
            } else {
                newVersion.setMinorVersion(getCurrentMaxMinorVersion(newVersion) + 1);
            }
            newVersion.setStatus(VersionStatus.SAVED.getVersionStatus());
            newVersion = versionDAO.save(version);
        } catch (DataAccessException e) {
            LOGGER.error("Unable to create new UMG Version", e);
            throw new SystemException(BusinessExceptionCodes.BSE000061, new Object[] { version.getModelLibrary().getUmgName(),
                    version.getMapping().getName() }, e);
        }
        return newVersion;
    }

    @Override
    public void update(Version version) throws BusinessException, SystemException {
        try {
            Version savedVersion = versionDAO.findOne(version.getId());
            savedVersion.setVersionDescription(version.getVersionDescription());
            versionDAO.save(savedVersion);
        } catch (DataAccessException e) {
            LOGGER.error("Unable to update UMG Version", e);
            throw new SystemException(BusinessExceptionCodes.BSE000064, new Object[] { version.getModelLibrary().getName(),
                    version.getMapping().getName() }, e);
        }
    }

    private Integer getCurrentMaxMinorVersion(Version version) {
        Integer currentMaxMinorVersion = versionDAO.getMaxMinorVersionForGivenMajorVersionAndTenantModelName(
                version.getMajorVersion(), version.getName());
        return currentMaxMinorVersion == null ? 0 : currentMaxMinorVersion;
    }

    private Integer getCurrentMaxMajorVersion(Version version) {
        Integer currentMaxMajorVersion = versionDAO.getMaxMajorVersionForTenantModelName(version.getName());
        return currentMaxMajorVersion == null ? 0 : currentMaxMajorVersion;
    }

    private boolean isCreateMajorVersion(Version version) {
        return version.getMajorVersion() == null || version.getMajorVersion() == 0;
    }

    /*
     * (non-Javadoc) Returns the list of versions for defined TID Name having status as published or deactivated.
     * 
     * @see com.ca.umg.business.version.bo.VersionBO#findByMappingName(java.lang.String)
     */
    @Override
    public List<Version> findByMappingName(String tidName) throws BusinessException, SystemException {
        String published = VersionStatus.PUBLISHED.getVersionStatus();
        String deactivated = VersionStatus.DEACTIVATED.getVersionStatus();
        return versionDAO.findByMappingName(tidName, published, deactivated);
    }

    @Override
    public List<String> getVersionStatus(String tidName) throws BusinessException, SystemException {
        String saved = VersionStatus.SAVED.getVersionStatus();
        String tested = VersionStatus.TESTED.getVersionStatus();
        return versionDAO.getVersionStatus(tidName, saved, tested);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.bo.VersionBO#markVersionAsPublished(com.ca.umg.business.version.entity.Version)
     */
    @Override
    public Version publishVersion(Version version, String user, int emailApproval) throws BusinessException, SystemException {
        Version tempVersion = version;
        if (StringUtils.equals(VersionStatus.PENDING_APPROVAL.getVersionStatus(), tempVersion.getStatus()) ||
                StringUtils.equals(VersionStatus.TESTED.getVersionStatus(), tempVersion.getStatus())) {
            tempVersion.setPublishedBy(user);
            tempVersion.setStatus(VersionStatus.PUBLISHED.getVersionStatus());
            tempVersion.setPublishedOn(DateTime.now());
            tempVersion.setEmailApproval(emailApproval);
            tempVersion = versionDAO.save(version);
            LOGGER.info("Published version {} successfully.", tempVersion.getName());
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000065, new Object[] {});
        }
        return tempVersion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.bo.VersionBO#markVersionAsDeactivated(com.ca.umg.business.version.entity.Version)
     */
    @Override
    public Version markVersionAsDeactivated(Version version, String user) throws BusinessException, SystemException {
        Version tempVersion = version;
        if (StringUtils.equals(VersionStatus.PUBLISHED.getVersionStatus(), tempVersion.getStatus())) {
            tempVersion.setDeactivatedBy(user);
            tempVersion.setStatus(VersionStatus.DEACTIVATED.getVersionStatus());
            tempVersion.setDeactivatedOn(DateTime.now());
            tempVersion = versionDAO.save(version);
            LOGGER.info("Deactivated version {} successfully.", tempVersion.getName());
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000066, new Object[] {});
        }
        return tempVersion;
    }

    @Override
    public KeyValuePair<Boolean, List<Mapping>> findAllMappings(Model model) throws BusinessException, SystemException {
        List<Mapping> mappings = mappingDAO.findByModel(model);
        return CollectionUtils.isNotEmpty(mappings) ? new KeyValuePair<Boolean, List<Mapping>>(Boolean.TRUE, mappings)
                : new KeyValuePair<Boolean, List<Mapping>>(Boolean.FALSE, null);
    }

    @Override
    public List<Integer> getAllMajorVersions(String tenantModelName) throws BusinessException, SystemException {
        return versionDAO.getMajorVersionsForTenantModelName(tenantModelName);
    }

    @Override
    public Version findByNameAndMappingNameAndModelLibraryUmgName(String name, String mappingName, String libraryUmgName)
            throws BusinessException, SystemException {
        return versionDAO.findByNameAndMappingNameAndModelLibraryUmgName(name, mappingName, libraryUmgName);
    }

    @Override
    public List<String> getAllUmgVersionsOnModelLibraryId(String modelLibraryId) throws BusinessException, SystemException {

        List<String> versionsList = null;
        List<Version> versionList = versionDAO.findVersionsOnModelLibAndStatus(modelLibraryId, VersionStatus.DELETED.name());
        if (versionList != null && !versionList.isEmpty()) {
            versionsList = new ArrayList<String>();
            for (Version version : versionList) {
                StringBuffer versionInfo = new StringBuffer();
                versionInfo.append(version.getName()).append(BusinessConstants.CHAR_HYPHEN).append(version.getMajorVersion())
                        .append(BusinessConstants.DOT).append(version.getMinorVersion());
                versionsList.add(versionInfo.toString());
            }
        }
        return versionsList;
    }

    @Override
    public List<String> findNotDeletedVersions(String tidName) throws BusinessException, SystemException {
        List<Version> result = null;
        List<String> listOfUmgVer = null;
        StringBuffer verData = null;
        String deleted = null;
        try {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tidName)) {
                deleted = VersionStatus.DELETED.getVersionStatus();
                result = versionDAO.findNotDeletedVersions(tidName, deleted);
                if (result != null && !result.isEmpty()) {
                    listOfUmgVer = new ArrayList<String>();
                    for (Version version : result) {
                        verData = new StringBuffer();
                        verData.append(version.getName());
                        verData.append(BusinessConstants.CHAR_HYPHEN);
                        verData.append(version.getMajorVersion().toString());
                        verData.append(BusinessConstants.DOT);
                        verData.append(version.getMinorVersion().toString());
                        listOfUmgVer.add(verData.toString());
                    }
                }
            }
        } catch (DataAccessException exception) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000049, new Object[] { "getNotDelVerForTid", tidName },
                    exception);
        }
        return listOfUmgVer;
    }

    @Override
    public Version findByNameAndVersion(String versionName, Integer majorVersion, Integer minorVersion) throws BusinessException,
            SystemException {
        return versionDAO.findByNameAndMajorVersionAndMinorVersion(StringUtils.lowerCase(versionName), majorVersion, minorVersion);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.bo.VersionBO#markVersionAsSaved(com.ca.umg.business.version.entity.Version)
     */
    @Override
    public void markVersionAsSaved(String id) throws BusinessException, SystemException {
        Version version = versionDAO.findOne(id);
        if (version != null && StringUtils.equals(VersionStatus.TESTED.getVersionStatus(), version.getStatus())) {
            version.setStatus(VersionStatus.SAVED.getVersionStatus());
            versionDAO.save(version);
            LOGGER.info("Saved version {} status successfully.", version);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000066, new Object[] {});
        }
    }

    @Override
    public List<String> getTestedVersions(String tidName) throws BusinessException, SystemException {
        List<String> result = null;
        String tested = null;
        try {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tidName)) {
                tested = VersionStatus.TESTED.getVersionStatus();
                result = versionDAO.getTestedVersions(tidName, tested);
            }
        } catch (DataAccessException exception) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000049, new Object[] { "getTestedVersions", tidName },
                    exception);
        }
        return result;
    }

    @Override
    public List<String> getAllTenantModelNames() throws BusinessException, SystemException {
        return versionDAO.getAllTenantModelNames();
    }

    @Override
    public String getTenantModeldescription(String tenantModelName) throws BusinessException, SystemException {
        List<String> descriptionList = versionDAO.getTenantModeldescriptions(tenantModelName);
        return isEmpty(descriptionList) ? EMPTY : descriptionList.get(0);
    }

    @Override
    public List<String> getModelNamesForLibraryNameAndCharsInNameOrDescription(String libraryName, String searchStr,
            boolean isDescending) throws BusinessException, SystemException {
        Sort sort = new Sort(isDescending ? Direction.DESC : Direction.ASC, "UPPER(ver.mapping.model.name)");
        return versionDAO.getModelNamesForLibraryNameAndCharsInNameOrDescription(libraryName,
                searchStr.toLowerCase(Locale.getDefault()), sort);
    }

    @Override
    public Version markVersionAsTested(Version version) throws BusinessException, SystemException {
        Version tempVersion = version;
        if (StringUtils.equals(VersionStatus.SAVED.getVersionStatus(), tempVersion.getStatus())
                || StringUtils.equals(VersionStatus.TESTED.getVersionStatus(), tempVersion.getStatus())) {
            tempVersion.setStatus(VersionStatus.TESTED.getVersionStatus());
            tempVersion = versionDAO.save(version);
            LOGGER.info("Tested version {} successfully. Status updated to TESTED", tempVersion.getName());
        } else {
            LOGGER.info("Tested version {} successfully. No status update affected!", tempVersion.getName());
        }
        return tempVersion;
    }

    @Override
    public void delete(String id) throws BusinessException, SystemException {
        Version version = versionDAO.findOne(id);
        if (version != null) {
            LOGGER.info("Version " + version.getName() + " found for deleting");
            versionDAO.delete(id);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000068, new Object[] {});
        }
    }

    @Override
    public List<Version> searchVersions(String name, Integer majorVersion, Integer minorVersion) throws BusinessException,
            SystemException {
        Specification<Version> withName = VersionSpecification.withName(name);
        Specification<Version> withMinorVersion = VersionSpecification.withMinorVersion(minorVersion);
        Specification<Version> withMajorVersion = VersionSpecification.withMajorVersion(majorVersion);
        Specification<Version> withStatusPublished = VersionSpecification.withStatus(VersionStatus.PUBLISHED.getVersionStatus());
        return versionDAO.findAll(where(withName).and(withMajorVersion).and(withMinorVersion).and(withStatusPublished));
    }

    @Override
    public List<Version> findVersionWithTidNameAndStatusPublishedORDeactivated(String tidName) throws BusinessException,
            SystemException {
        String published = VersionStatus.PUBLISHED.getVersionStatus();
        String deactivated = VersionStatus.DEACTIVATED.getVersionStatus();
        return versionDAO.findByMappingName(tidName, published, deactivated);
    }

    /**
     * This method will retrieve all major version names with search criteria and return paginated data back with page info
     * UMG-1755
     */
    @Override
    public List<Version> findAllVersionName(SearchOptions searchOptions) throws BusinessException, SystemException {
        PagingInfo pagingInfo = searchOptions;
        Long fromDate = null;
        Long toDate = null;
        if (pagingInfo == null) {
            pagingInfo = new PagingInfo();
        }
        // since the first level major version name are displayed all the time
        pagingInfo.setSortColumn("name");

        // Specification<Version> minorVersionSpec = VersionSpecification.withMinorVersion(0);
        Specification<Version> statusSpec = VersionSpecification.withStatus(searchOptions.getSearchText());
        Specification<Version> containerNameSpec = VersionSpecification.withContainerNameLike(searchOptions.getSearchText());
        Specification<Version> versionDescr = VersionSpecification.withVersionDescriptionLike(searchOptions.getSearchText());
        Specification<Version> createdBy = VersionListingSpecification.withVersionCreatedBy(searchOptions.getSearchText());
        // setting start and end date to Long format
        if (searchOptions.getFromDate() != null && !searchOptions.getFromDate().isEmpty()) {
            fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        if (searchOptions.getToDate() != null && !searchOptions.getToDate().isEmpty()) {
            toDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        Specification<Version> createFromSpec = VersionListingSpecification.withCreatedDateFrom(fromDate);
        Specification<Version> createToSpec = VersionListingSpecification.withCreatedDateTo(toDate);

        Specification<Version> modelNameSpec = VersionSpecification.withModelNameLike(searchOptions.getSearchText());
        Specification<Version> mappingNameSpec = VersionSpecification.withTIDNameLike(searchOptions.getSearchText());
        Specification<Version> mappingDescrSpec = VersionListingSpecification.withTIDDescriptionLike(searchOptions
                .getSearchText());

        Specification<Version> libraryNameSpec = VersionListingSpecification.withLibraryNameLike(searchOptions.getSearchText());

        return versionDAO.findAll(where(createFromSpec).and(createToSpec).and(
                where(containerNameSpec).or(versionDescr).or(createdBy).or(statusSpec).or(mappingNameSpec).or(mappingDescrSpec)
                        .or(modelNameSpec).or(libraryNameSpec)));
    }

    /**
     * This method will retrieve all major&minor version and return paginated data back with page info
     */
    /*
     * @Override public Page<Version> findAllversionByVersionName(String versionName, VersionInfo versionInfo) throws
     * BusinessException, SystemException{ String sortColumn = null; if(isNotEmpty(versionInfo.getSortColumn())){ sortColumn =
     * versionInfo.getSortColumn(); } Pageable pageRequest = getPagingInformation(versionInfo, sortColumn); return
     * versionDAO.findAllversionByVersionName(versionName, pageRequest); }
     */

    @Override
    public Page<Version> findAllversionByVersionName(String versionName, SearchOptions searchOptions) throws BusinessException,
            SystemException {
        PagingInfo pagingInfo = searchOptions;
        Long fromDate = null;
        Long toDate = null;
        if (pagingInfo == null) {
            pagingInfo = new PagingInfo();
        }
        Specification<Version> versionNameSpec = VersionSpecification.withName(versionName);
        Specification<Version> statusSpec = VersionSpecification.withStatus(searchOptions.getSearchText());
        Specification<Version> containerNameSpec = VersionSpecification.withContainerNameLike(searchOptions.getSearchText());
        Specification<Version> versionDescr = VersionSpecification.withVersionDescriptionLike(searchOptions.getSearchText());
        Specification<Version> createdBy = VersionListingSpecification.withVersionCreatedBy(searchOptions.getSearchText());
        // setting start and end date to Long format
        if (searchOptions.getFromDate() != null && !searchOptions.getFromDate().isEmpty()) {
            fromDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getFromDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        if (searchOptions.getToDate() != null && !searchOptions.getToDate().isEmpty()) {
            toDate = AdminUtil.getMillisFromEstToUtc(searchOptions.getToDate(), BusinessConstants.LIST_SEARCH_DATE_FORMAT);
        }
        Specification<Version> createFromSpec = VersionListingSpecification.withCreatedDateFrom(fromDate);
        Specification<Version> createToSpec = VersionListingSpecification.withCreatedDateTo(toDate);

        Specification<Version> modelNameSpec = VersionSpecification.withModelNameLike(searchOptions.getSearchText());
        Specification<Version> mappingNameSpec = VersionSpecification.withTIDNameLike(searchOptions.getSearchText());
        Specification<Version> mappingDescrSpec = VersionListingSpecification.withTIDDescriptionLike(searchOptions
                .getSearchText());

        Specification<Version> libraryNameSpec = VersionListingSpecification.withLibraryNameLike(searchOptions.getSearchText());

        Pageable pageRequest = getPagingInformation(searchOptions, "");

        return versionDAO.findAll(
                where(versionNameSpec)
                        .and(createFromSpec)
                        .and(createToSpec)
                        .and(where(containerNameSpec).or(versionDescr).or(createdBy).or(statusSpec).or(mappingNameSpec)
                                .or(mappingDescrSpec).or(modelNameSpec).or(libraryNameSpec)), pageRequest);

    }

    @Override
    public List<VersionInfo> getVersionDetailsForLanguage(final String executionEnvironment, final ModelType modelType) throws BusinessException, SystemException {
        List<VersionInfo> versionInfos = null;
        List<Map<String, Object>> versionDetails = versionContainerDAO.getExisitingVersionDetails(executionEnvironment, modelType);
        if (CollectionUtils.isNotEmpty(versionDetails)) {
            versionInfos = new ArrayList<>();
            for (Map<String, Object> row : versionDetails) {
                versionInfos.add(populateVersionInfo(row));
            }
        }
        return versionInfos;
    }

    @Override
    public List<VersionInfo> searchLibraries(SearchOptions searchOptions, String executionLanguage) throws BusinessException,
            SystemException {
        List<VersionInfo> versionInfos = null;
        List<Map<String, Object>> versionDetails = versionContainerDAO.searchLibraries(searchOptions, executionLanguage);
        if (CollectionUtils.isNotEmpty(versionDetails)) {
            versionInfos = new ArrayList<>();
            for (Map<String, Object> row : versionDetails) {
                versionInfos.add(populateVersionInfo(row));
            }
        }
        return versionInfos;
    }

    @Override
    public List<MediateModelLibraryInfo> searchNewLibraries(SearchOptions searchOptions, String environmentId)
            throws BusinessException, SystemException {
        List<MediateModelLibraryInfo> mediateModelLibraryInfos = null;
        List<Map<String, Object>> modelLibraryDetails = versionContainerDAO.searchNewLibraries(searchOptions, environmentId);
        if (CollectionUtils.isNotEmpty(modelLibraryDetails)) {
            mediateModelLibraryInfos = new ArrayList<>();
            for (Map<String, Object> row : modelLibraryDetails) {
                mediateModelLibraryInfos.add(populateMediateModelLibraryInfo(row));
            }
        }
        return mediateModelLibraryInfos;
    }

    @Override
    public List<VersionInfo> searchIoDefns(SearchOptions searchOptions, String executionLanguage, final ModelType modelType) throws BusinessException,
            SystemException {
        List<VersionInfo> versionInfos = new ArrayList<>();
        List<Map<String, Object>> versionDetails = versionContainerDAO.searchIoDefns(searchOptions, executionLanguage, modelType);
        if (CollectionUtils.isNotEmpty(versionDetails)) {
            for (Map<String, Object> row : versionDetails) {
                versionInfos.add(populateVersionInfo(row));
            }
        }
        return versionInfos;
    }

    private MediateModelLibraryInfo populateMediateModelLibraryInfo(Map<String, Object> row) {
        MediateModelLibraryInfo mediateModelLibraryInfo = new MediateModelLibraryInfo();
        mediateModelLibraryInfo.setId(row.get("ID").toString());
        mediateModelLibraryInfo.setTarName(row.get("TAR_NAME").toString());
        mediateModelLibraryInfo.setChecksum(row.get("CHECKSUM_VALUE").toString());
        mediateModelLibraryInfo.setEncodingType(row.get("CHECKSUM_TYPE").toString());
        mediateModelLibraryInfo.setCreatedBy(row.get("CREATED_BY").toString());
        mediateModelLibraryInfo.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(
                new DateTime(row.get("CREATED_ON")).getMillis(), null));
        mediateModelLibraryInfo.setModelExecEnvName(row.get("MODEL_EXEC_ENV_NAME").toString());
        return mediateModelLibraryInfo;
    }

    /**
     * populates and returns {@link VersionInfo} by reading the query result
     * 
     * @param row
     * @return
     */
    private VersionInfo populateVersionInfo(Map<String, Object> row) {// NO PMD
        VersionInfo versionInfo = null;
        ModelLibraryInfo modelLibraryInfo = null;
        MappingInfo mappingInfo = null;
        ModelInfo modelInfo = null;
        versionInfo = new VersionInfo();
        versionInfo.setName(row.get("VERSION_NAME").toString());
        versionInfo.setMajorVersion((Integer) row.get("MAJOR_VERSION"));
        versionInfo.setMinorVersion((Integer) row.get("MINOR_VERSION"));
        versionInfo.setStatus(row.get("STATUS").toString());
        versionInfo.setCreatedBy(row.get("CREATED_BY").toString());
        versionInfo.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst((Long) row.get("CREATED_DATE"), null));
        versionInfo.setModelType(row.get("MODEL_TYPE").toString());
        if(row.containsKey(MODEL_EXE_NAME) && row.get(MODEL_EXE_NAME) != null){
        versionInfo.setExecutionLanguage(row.get(MODEL_EXE_NAME).toString());
        }
        if (row.get("JAR_NAME") != null) {
            modelLibraryInfo = new ModelLibraryInfo();
            modelLibraryInfo.setJarName(row.get("JAR_NAME").toString());
            modelLibraryInfo.setId(row.get("MODEL_LIBRARY_ID").toString());
            modelLibraryInfo.setExecEnv(row.get("EXECENV").toString());
            Object manifestFilename=row.get("R_MANIFEST_FILE_NAME");
            if(manifestFilename!=null){
                modelLibraryInfo.setRmanifestFileName(manifestFilename.toString());
            }           
            modelLibraryInfo.setChecksum(row.get("CHECK_SUM") != null ? row.get("CHECK_SUM").toString() : null);
            versionInfo.setModelLibrary(modelLibraryInfo);
        }
        if (row.get("IO_DEF_EXCEL_NAME") != null && isNotEmpty((String) row.get("IO_DEF_EXCEL_NAME"))) {
            mappingInfo = new MappingInfo();
            modelInfo = new ModelInfo();
            modelInfo.setIoDefinitionName(row.get("IO_DEF_EXCEL_NAME").toString());
            modelInfo.setId(row.get("MODEL_ID").toString());
            mappingInfo.setModel(modelInfo);
            versionInfo.setMapping(mappingInfo);
        } else if (row.get("IO_DEFINITION_NAME") != null) {
            mappingInfo = new MappingInfo();
            modelInfo = new ModelInfo();
            modelInfo.setIoDefinitionName(row.get("IO_DEFINITION_NAME").toString());
            modelInfo.setId(row.get("MODEL_ID").toString());
            mappingInfo.setModel(modelInfo);
            versionInfo.setMapping(mappingInfo);
        }
        return versionInfo;
    }
    
    /**
     * populates report info and returns {@link VersionInfo} by reading the query result
     * 
     * @param row
     * @return
     */
    private ModelReportTemplateInfo populateModelReportInfo(Map<String, Object> row) {
        ModelReportTemplateInfo modelReportTemplateInfo = new ModelReportTemplateInfo();
        modelReportTemplateInfo.setName(row.get("TEMPLATE_NAME").toString());  
        modelReportTemplateInfo.setReportType(row.get("REPORT_TYPE").toString());
        modelReportTemplateInfo.setReportEngine(row.get("REPORT_ENGINE").toString());
        modelReportTemplateInfo.setId(row.get("REPORT_ID").toString());
        modelReportTemplateInfo.setTenantId(row.get("TENANT_ID").toString());
        modelReportTemplateInfo.setReportVersion(Integer.parseInt(row.get("MAJOR_VERSION").toString()));      
        return modelReportTemplateInfo;
        
    }

    @Override
    public Map<String, Object> getVersionMetrics(VersionMetricRequestInfo versionReq) throws BusinessException, SystemException {
        return versionMetricsDAO.getVersionMetricsDetails(versionReq);
    }

    @Override
    public Version searchVersionByName(String name) throws SystemException {
        Sort sort = new Sort(Direction.ASC, "createdDate");
        Pageable pageable = new PageRequest(0, 1, sort);
        Page<Version> page = versionDAO.findAllversionByVersionName(name, pageable);
        return page.getContent() == null ? null : page.getContent().get(0);
    }
    
    /**
     * This method will retrieve all major version names with search criteria and return paginated data back with page info
     * 
     */
    @Override
    public List<VersionInfo> findAllVersions(SearchOptions searchOptions) throws BusinessException, SystemException {
       
        return versionContainerDAO.getVersionDetails(searchOptions);
    }

    @Override
    public List<String> getAllTenantNamesByEnv(String executionEnvironment) throws SystemException {
        return versionDAO.getAllTenantModelNamesByEnv(executionEnvironment);
    }
    
    @Override
    public List<VersionInfo> getModelReportDetailsForLanguage(final String executionEnvironment, final ModelType modelType) throws BusinessException, SystemException {
        List<VersionInfo> versionInfos = null;
        List<Map<String, Object>> versionDetails = versionContainerDAO.getExisitingReportDetails(executionEnvironment, modelType);
        if (CollectionUtils.isNotEmpty(versionDetails)) {
            versionInfos = new ArrayList<>();
            for (Map<String, Object> row : versionDetails) {
                VersionInfo versionInfo = populateVersionInfo(row);                
                versionInfo.setReportTemplateInfo(populateModelReportInfo(row));
                versionInfos.add(versionInfo);
            }
        }
        return versionInfos;
    }
    
    @Override
    public List<VersionInfo> searchReports(final SearchOptions searchOptions, final String executionEnvironment) throws BusinessException, SystemException {
        List<VersionInfo> versionInfos = null;
        List<Map<String, Object>> versionDetails = versionContainerDAO.searchReports(searchOptions, executionEnvironment);
        if (CollectionUtils.isNotEmpty(versionDetails)) {
            versionInfos = new ArrayList<>();
            for (Map<String, Object> row : versionDetails) {
                VersionInfo versionInfo = populateVersionInfo(row);                
                versionInfo.setReportTemplateInfo(populateModelReportInfo(row));
                versionInfos.add(versionInfo);
            }
        }
        return versionInfos;
    }
    
    @Override
    public Version updateModelApprovalStatus(final Version version, final String user, final VersionStatus status)
            throws BusinessException, SystemException {
        Version tempVersion = version;

        if (status == VersionStatus.PENDING_APPROVAL) {
            tempVersion.setRequestedBy(user);
            tempVersion.setRequestedOn(DateTime.now());
        } else {
            tempVersion.setRequestedBy(null);
            tempVersion.setRequestedOn(null);
        }

        tempVersion.setStatus(status.getVersionStatus());
        tempVersion = versionDAO.save(version);
        LOGGER.info("Version is marked as Pedning Approval successfully. Version name is {}, status is {}", tempVersion.getName(), status.getVersionStatus());
        return tempVersion;
    }

    @Override
    public Version findOneVersion(String versionId) throws BusinessException, SystemException {
        // Version version = versionDAO.findOne(versionId);
        return versionDAO.findOne(versionId);
    }
}