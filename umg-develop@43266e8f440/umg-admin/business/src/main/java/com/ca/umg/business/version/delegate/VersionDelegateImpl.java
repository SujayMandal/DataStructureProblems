package com.ca.umg.business.version.delegate;

import static com.ca.umg.notification.model.NotificationEventNameEnum.MODEL_PUBLISH_APPROVAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.common.info.ResponseWrapper;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.integration.info.TestStatusInfo;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.business.mapping.dao.MappingInputDAO;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.TestBedOutputInfo;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype;
import com.ca.umg.business.mid.extraction.info.PartialMapping;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.tenant.report.model.util.JsonToExcelConverterUtil2;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.validation.CreateVersionValidator;
import com.ca.umg.business.validation.UpdateVersionValidator;
import com.ca.umg.business.validation.ValidationError;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.dao.VersionContainerDAO;
import com.ca.umg.business.version.data.VersionDataContainer;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.MappingVersionInfo;
import com.ca.umg.business.version.info.VersionHierarchyInfo;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionStatus;
import com.ca.umg.business.version.info.VersionSummaryInfo;
import com.ca.umg.notification.dao.NotificationDao;
import com.ca.umg.plugin.commons.excel.reader.ReadHeaderSheet;

@Named
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount", "PMD.UseObjectForClearerAPI" })
public class VersionDelegateImpl extends AbstractDelegate implements VersionDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionDelegateImpl.class);

    private static final String ERROR_MESSAGE_SEPARATOR = " \n ";

    private static final String PIPE = "|";

    private static final String OBJECT = Datatype.OBJECT.getDatatype();

    private static final String STRING = Datatype.STRING.getDatatype();

    private static final String OBJECT_ARRAY = Datatype.OBJECT.getDatatype() + "|ARRAY";

    @Inject
    private VersionBO versionBO;

    @Inject
    private VersionDelegateHelper versionDelegateHelper;

    @Inject
    private ModelDelegate modelDelegate;

    @Inject
    private CreateVersionValidator createVersionValidator;

    @Inject
    private UpdateVersionValidator updateVersionValidator;

    @Inject
    private MappingDelegate mappingDelegate;

    @Inject
    private RuntimeIntegrationClient runtimeIntegrationClient;

    @Inject
    private MappingInputDAO mappingInputDAO;

    @Inject
    private VersionDataContainer versionDataContainer;

    @Inject
    private VersionContainerDAO versionContainerDAO;

    @Inject
    private NotificationDao mysqlDao;

    @Override
    public List<VersionHierarchyInfo> getAllVersions() throws BusinessException, SystemException {
        List<VersionHierarchyInfo> hierarchyInfos = null;
        List<Version> versions = versionBO.getAllVersions();
        if (CollectionUtils.isNotEmpty(versions)) {
            hierarchyInfos = versionDelegateHelper.createVersionHierarchy(versions);
        }
        return hierarchyInfos;
    }

    // single model publishing
    @Override
    public List<String> getAllVersionNames() throws BusinessException, SystemException {
        return versionDataContainer.getListOfVersionNames();
    }

    @Override
    public String getVersionDescription(String versionName) throws BusinessException, SystemException {
        return versionDataContainer.getVersionDescription(versionName);
    }

    @Override
    public Map<String, List<String>> getEnvironments() throws SystemException {
        return versionBO.getEnvironments();
    }

    @Override
    public List<Integer> getMajorVersions(String versionName) throws BusinessException, SystemException {
        return versionBO.getAllMajorVersions(versionName);
    }

    @Override
    public List<VersionInfo> getVersionDetails(final String executionLanguage, final ModelType modelType,
            final boolean isforReport) throws BusinessException, SystemException {
        List<VersionInfo> versionDetails = null;
        if (isforReport) {
            versionDetails = versionBO.getModelReportDetailsForLanguage(executionLanguage, modelType);

        } else {
            versionDetails = versionBO.getVersionDetailsForLanguage(executionLanguage, modelType);

        }
        return versionDetails;
    }

    @Override
    public List<VersionInfo> searchLibrary(SearchOptions searchOptions, String executionLanguage)
            throws BusinessException, SystemException {
        return versionBO.searchLibraries(searchOptions, executionLanguage);
    }

    @Override
    public List<VersionInfo> searchIoDefns(SearchOptions searchOptions, String executionLanguage, final ModelType modelType)
            throws BusinessException, SystemException {
        return versionBO.searchIoDefns(searchOptions, executionLanguage, modelType);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageView())")
    public VersionInfo getVersionById(String versionId) throws BusinessException, SystemException {
        Version ver = versionBO.getVersionDetails(versionId);
        VersionInfo versionInfo = convert(ver, VersionInfo.class);

        final String toAddress = mysqlDao.getToAddresses(MODEL_PUBLISH_APPROVAL.getName(),
                RequestContext.getRequestContext().getTenantCode());

        if (StringUtils.isNotBlank(toAddress)) {
            versionInfo.setApprover(toAddress);
        }

        versionInfo.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(ver.getCreatedDate().getMillis(), null));

        if (ver.getDeactivatedOn() != null) {
            versionInfo.setDeactivatedDateTime(AdminUtil.getDateFormatMillisForEst(ver.getDeactivatedOn().getMillis(), null));
        }

        if (ver.getPublishedOn() != null) {
            versionInfo.setPublishedDateTime(AdminUtil.getDateFormatMillisForEst(ver.getPublishedOn().getMillis(), null));
        }

        if (ver.getRequestedOn() != null) {
            versionInfo.setRequestedDateTime(AdminUtil.getDateFormatMillisForEst(ver.getRequestedOn().getMillis(), null));
        }

        return versionInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * Returns the list of versions for defined TID Name having status as published or deactivated.
     * 
     * @see com.ca.umg.business.version.delegate.VersionDelegate#getTidMapStatusInUmgVer(java.lang.String)
     */
    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageUpdateMapping())")
    public KeyValuePair<Boolean, List<String>> getTidMappingStatus(String tidName) throws BusinessException, SystemException {
        KeyValuePair<Boolean, List<String>> listOfUmgVer = new KeyValuePair<>();
        try {
            List<Version> result = versionBO.findByMappingName(tidName);
            if (result != null && !result.isEmpty()) {
                listOfUmgVer.setKey(Boolean.TRUE);
                List<String> verList = new ArrayList<>();
                for (Version version : result) {
                    verList.add(version.getMajorVersion().toString() + "." + version.getMinorVersion().toString());
                }
                listOfUmgVer.setValue(verList);
            } else {
                listOfUmgVer.setKey(Boolean.FALSE);
            }

        } catch (DataAccessException exception) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000049,
                    new Object[] { "gettingStatusForMappingInUmgVer", tidName }, exception);
        }
        return listOfUmgVer;
    }

    @Override
    public Boolean getVersionStatus(String tidName) throws BusinessException, SystemException {
        Boolean retResult = Boolean.FALSE;
        try {
            List<String> result = versionBO.getVersionStatus(tidName);
            if (result != null && !result.isEmpty()) {
                retResult = Boolean.TRUE;
            } else {
                retResult = Boolean.FALSE;
            }

        } catch (DataAccessException exception) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000049,
                    new Object[] { "gettingStatusForMappingInUmgVer", tidName }, exception);
        }
        return retResult;
    }

    @Override
    public PageRecord<String> getAllLibraries(VersionInfo versionInfo) throws BusinessException, SystemException {
        Page<String> page = versionBO.getAllLibraries(versionInfo);
        return buildPageRecord(page);
    }

    private <T> PageRecord<T> buildPageRecord(Page<T> page) {
        PageRecord<T> pageRecord = new PageRecord<T>();
        pageRecord.setNumber(page.getNumber() + 1);
        pageRecord.setContent(page.getContent());
        pageRecord.setNumberOfElements(page.getNumberOfElements());
        pageRecord.setSize(page.getSize());
        pageRecord.setTotalElements(page.getTotalElements());
        pageRecord.setTotalPages(page.getTotalPages());
        pageRecord.setLastPage(page.isLast());
        pageRecord.setFirstPage(page.isFirst());
        return pageRecord;
    }

    @Override
    public PageRecord<String> getAllModelsForLibrary(String libraryName, VersionInfo versionInfo)
            throws BusinessException, SystemException {
        Page<String> page = versionBO.getAllModelsForLibrary(libraryName, versionInfo);
        return buildPageRecord(page);
    }

    @Override
    public PageRecord<VersionInfo> getAllVersions(String libraryName, String modelName, VersionInfo versionInfo)
            throws BusinessException, SystemException {
        List<VersionInfo> versionInfoList = null;
        Page<Version> page = versionBO.getAllVersions(libraryName, modelName, versionInfo);
        // TODO move it to common or try if convert works
        PageRecord<VersionInfo> pageRecord = new PageRecord<VersionInfo>();
        List<VersionInfo> versionList = convertToList(page.getContent(), VersionInfo.class);
        if (CollectionUtils.isNotEmpty(versionList)) {
            versionInfoList = new ArrayList<>();
            for (VersionInfo version : versionList) {
                version.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(version.getCreatedDate().getMillis(), null));
                version.setLastModifiedDateTime(
                        AdminUtil.getDateFormatMillisForEst(version.getLastModifiedDate().getMillis(), null));
                versionInfoList.add(version);
            }
        }
        pageRecord.setContent(versionInfoList);
        pageRecord.setNumber(page.getNumber());
        pageRecord.setNumberOfElements(page.getNumberOfElements());
        pageRecord.setSize(page.getSize());
        pageRecord.setTotalElements(page.getTotalElements());
        pageRecord.setTotalPages(page.getTotalPages());
        return pageRecord;
    }

    @Override
    public VersionInfo create(VersionInfo versionInfo) throws BusinessException, SystemException {
        validateData(versionInfo);
        versionInfo.setMapping(getMappingDetails(versionInfo.getMapping().getName()));
        versionInfo.setModelLibrary(getModelLibraryInfo(versionInfo.getModelLibrary().getUmgName()));
        Version version = convert(versionInfo, Version.class);
        version = versionBO.create(version);
        return convert(version, VersionInfo.class);
    }

    private MappingInfo getMappingDetails(String mappingName) throws BusinessException, SystemException {
        MappingInfo mappingInfo = mappingDelegate.findByName(mappingName);
        if (mappingInfo == null) {
            LOGGER.error("Could not find mapping with name {}.", mappingName);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000048, new Object[] { mappingName });
        }
        LOGGER.debug("Mapping id for mapping name {} is {}.", mappingName, mappingInfo.getId());
        return mappingInfo;
    }

    private ModelLibraryInfo getModelLibraryInfo(String libraryRecord) throws BusinessException, SystemException {
        ModelLibraryInfo modelLibraryInfo = modelDelegate.findByUmgName(libraryRecord);
        if (modelLibraryInfo == null) {
            LOGGER.error("Could not find model library with name {}.", libraryRecord);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000090, new Object[] { libraryRecord });
        }
        LOGGER.debug("Model library id for library record name {} is {}.", libraryRecord, modelLibraryInfo.getId());
        return modelLibraryInfo;
    }

    @Override
    public void update(VersionInfo versionInfo) throws BusinessException, SystemException {
        validateUpdatedData(versionInfo);
        Version version = convert(versionInfo, Version.class);
        versionBO.update(version);
    }

    private void validateData(VersionInfo version) throws BusinessException, SystemException {
        reportError(createVersionValidator.validate(version));
    }

    private void validateUpdatedData(VersionInfo version) throws BusinessException, SystemException {
        reportError(updateVersionValidator.validate(version));
    }

    public static void reportError(List<ValidationError> errors) throws BusinessException {
        if (CollectionUtils.isNotEmpty(errors)) {
            StringBuilder errorMessages = new StringBuilder();
            for (ValidationError validationError : errors) {
                errorMessages.append(validationError.getMessage()).append(ERROR_MESSAGE_SEPARATOR);
            }
            throw new BusinessException(BusinessExceptionCodes.BSE000060, new String[] { errorMessages.toString() });
        }
    }

    @Override
    public List<String> getAllLibraryNames() throws BusinessException, SystemException {
        return modelDelegate.getAllLibraryNames();
    }

    @Override
    public List<String> getAllLibraryRecords(String libraryName) throws BusinessException, SystemException {
        return modelDelegate.getListOfDerivedModelLibraryNames(libraryName);
    }

    @Override
    public List<String> getAllModelNames() throws BusinessException, SystemException {
        return modelDelegate.getAllModelNames();
    }

    @Override
    public List<String> getAllTidVersionNames(String modelName) throws BusinessException, SystemException {
        return mappingDelegate.getListOfMappingNames(modelName);
    }

    @Override
    public List<MappingInfo> getTidMappings(String modelName) throws BusinessException, SystemException {
        return mappingDelegate.findByModelName(modelName);
    }

    @Override
    public List<String> getNotDeletedVersions(String tidName) throws BusinessException, SystemException {
        return versionBO.findNotDeletedVersions(tidName);
    }

    @Override
    public List<String> getUmgVersionsOnModelLibraryId(String modelLibraryId) throws BusinessException, SystemException {
        return versionBO.getAllUmgVersionsOnModelLibraryId(modelLibraryId);
    }

    @Override
    public List<String> getAllTenantModelNames() throws BusinessException, SystemException {
        return versionBO.getAllTenantModelNames();
    }

    @Override
    public VersionSummaryInfo getVersionSummary(String tenantModelName) throws BusinessException, SystemException {
        VersionSummaryInfo version = new VersionSummaryInfo();
        version.setDescription(versionBO.getTenantModeldescription(tenantModelName));
        version.setMajorVersions(versionBO.getAllMajorVersions(tenantModelName));
        return version;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VersionInfo publishVersion(String identifier, String user, String tenantUrl, String authToken, int emailApproval)
            throws BusinessException, SystemException {
        return versionPublish(identifier, user, tenantUrl, authToken, emailApproval);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole(@accessPrivilege.getModelManagePublish())")
    public VersionInfo publishVersionFromUI(String identifier, String user, String tenantUrl, String authToken, int emailApproval)
            throws BusinessException, SystemException {
        return versionPublish(identifier, user, tenantUrl, authToken, emailApproval);
    }

    public VersionInfo versionPublish(String identifier, String user, String tenantUrl, String authToken, int emailApproval)
            throws BusinessException, SystemException {
        VersionInfo versionInfo = null;
        Version version = versionBO.getVersionDetails(identifier);
        if (version != null) {
            version = versionBO.publishVersion(version, user, emailApproval);
            versionInfo = convert(version, VersionInfo.class);
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, authToken);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000068, new Object[] {});
        }
        return versionInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)

    @PreAuthorize("hasRole(@accessPrivilege.getModelManageDeactivate())")
    public VersionInfo deactivateVersion(String identifier, String user, String tenantUrl, String authToken)
            throws BusinessException, SystemException {
        VersionInfo versionInfo = null;
        Version version = versionBO.getVersionDetails(identifier);
        if (version != null) {
            version = versionBO.markVersionAsDeactivated(version, user);
            versionInfo = convert(version, VersionInfo.class);
            runtimeIntegrationClient.unDeploy(versionInfo, tenantUrl, authToken);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000068, new Object[] {});
        }
        return versionInfo;
    }

    @Override
    public List<MappingInfo> listAllLibraryRecNameDescs(String libraryName) throws BusinessException, SystemException {
        List<ModelLibrary> listModelLibrary = modelDelegate.findMappingInfoByLibraryNamName(libraryName);
        return convertToList(listModelLibrary, MappingInfo.class);
    }

    @Override
    public List<String> getModelNamesForLibraryNameAndCharsInNameOrDescription(String libraryName, String searchStr,
            boolean isDescending) throws BusinessException, SystemException {
        return versionBO.getModelNamesForLibraryNameAndCharsInNameOrDescription(libraryName, searchStr, isDescending);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.delegate.VersionDelegate#versionTest(java.lang.String, java.lang.String)
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestBedOutputInfo versionTest(String payloadJsonWithExtraQuotes, String tenantUrl, String authToken, String versionId)
            throws BusinessException, SystemException {
        String payloadJson = null;
        TestStatusInfo runtimeResponse = null;
        TestBedOutputInfo response = null;
        if (StringUtils.isNotBlank(payloadJsonWithExtraQuotes) && StringUtils.isNotBlank(versionId)) {
            payloadJson = StringUtils.replace(payloadJsonWithExtraQuotes, "\"\\\"", "\"");
            payloadJson = StringUtils.replace(payloadJson, "\\\"\"", "\"");
            LOGGER.error(String.format("Test input for version id %s is as :: %s", versionId, payloadJson));
            runtimeResponse = runtimeIntegrationClient.versionTest(payloadJson, tenantUrl, authToken);
            response = new TestBedOutputInfo();
            if (runtimeResponse != null) {
                response.setError(runtimeResponse.isError());
                response.setErrorCode(runtimeResponse.getErrorCode());
                response.setErrorMessage(runtimeResponse.getErrorMessage());
                response.setStatus(runtimeResponse.getStatus());
                response.setTimeTaken(runtimeResponse.getTimeTaken());
                LOGGER.error(String.format(
                        "Response recieved for version id :: %s, with error flag as :: %s,error code is  :: %s,errorMessage is :: %s",
                        versionId, runtimeResponse.isError(), runtimeResponse.getErrorCode(), runtimeResponse.getErrorMessage()));
                response.setOutputJson(getRuntimeResponseAsJson(runtimeResponse));
                if (!runtimeResponse.isError()) {
                    markAsTested(versionId);
                }
            }
        } else {
            BusinessException.raiseBusinessException(FrameworkExceptionCodes.BSE000009, new Object[] {});
        }
        return response;
    }

    private String getRuntimeResponseAsJson(TestStatusInfo runtimeResponse) throws SystemException {
        String json = null;
        Map<String, String> responseError = null;
        if (runtimeResponse.getResponse() != null) {
            json = ConversionUtil.convertToJsonString(runtimeResponse.getResponse());
        } else if (runtimeResponse.getErrorMessage() != null) {
            responseError = new HashMap<String, String>();
            responseError.put("Error Message", runtimeResponse.getErrorMessage());
            responseError.put("Error Code", runtimeResponse.getErrorCode());
            json = ConversionUtil.convertToJsonString(responseError);
        }
        LOGGER.error(json);
        return json;
    }

    @Override
    public Version markAsTested(String versionId) throws BusinessException, SystemException {
        Version version = null;
        if (StringUtils.isNotBlank(versionId)) {
            version = versionBO.getVersionDetails(versionId);
            version = versionBO.markVersionAsTested(version);
        }
        return version;
    }

    @Override
    public void delete(String id) throws BusinessException, SystemException {
        versionBO.delete(id);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageDelete())")
    public void deleteVersion(String id) throws BusinessException, SystemException {
        versionBO.delete(id);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageExcelDownload())")
    public Workbook exportExcel(MappingVersionInfo mvi) throws BusinessException, SystemException {
        String tidName = mvi.getTidName();
        String versionName = mvi.getVersionName();
        String majorVersion = mvi.getMajorVersion();
        String minorVersion = mvi.getMinorVersion();
        MappingInfo mappingInfo = mappingDelegate.findByName(tidName);
        Mapping mapping = convert(mappingInfo, Mapping.class);
        MappingInput mappingInput = mappingInputDAO.findByMapping(mapping);
        PartialMapping<TidParamInfo> partialTidParams = ConversionUtil.convertJson(mappingInput.getTenantInterfaceDefn(),
                PartialMapping.class, TidParamInfo.class);
        List<TidParamInfo> tidParamInfos = partialTidParams.getPartials();
        Workbook wb = new HSSFWorkbook();
        Sheet dataSheet = wb.createSheet("Data");
        Sheet headerSheet = wb.createSheet("Header");
        JsonToExcelConverterUtil2.buildHeaderSheet(headerSheet);
        Row headerRow = headerSheet.getRow(0);
        if (headerRow.getCell(10) != null) {
        	Cell transactionIdCell = headerRow.getCell(10);
        	headerRow.removeCell(transactionIdCell);
        	if(headerRow.getCell(11) != null){
        		Cell transactionStoreRLogs = headerRow.getCell(11);
        		headerRow.removeCell(transactionStoreRLogs);
        		Cell cellstoreRLogs = headerRow.createCell(10);
        		cellstoreRLogs.setCellType(Cell.CELL_TYPE_BOOLEAN);		
        		cellstoreRLogs.setCellValue(ReadHeaderSheet.STORE_RLOGS);
        	}
        }
        setHeaderDetails(headerSheet, versionName, majorVersion, minorVersion);
        Row row = dataSheet.createRow(0);
        row.createCell(0).setCellValue("transactionId|STRING");
        int cellCount = 1;
        for (TidParamInfo tidParamInfo : tidParamInfos) {
            if (tidParamInfo.getApiName() == null) {
                tidParamInfo.setApiName(tidParamInfo.getName());
            }
            cellCount = buildExcel(tidParamInfo, row, cellCount);
        }
        return wb;
    }

    private int buildExcel(TidParamInfo tidParamInfo, Row row, int cellCount) {
        int cllcnt = cellCount;
        if (newSheetRequired(tidParamInfo)) {
            row.createCell(cllcnt).setCellValue(tidParamInfo.getApiName() + PIPE + tidParamInfo.getDataTypeStr());
            Row childRow = row.getSheet().getWorkbook().createSheet(tidParamInfo.getApiName()).createRow(0);
            childRow.createCell(0).setCellValue("ID" + PIPE + STRING);
            int childCellCount = 1;
            for (TidParamInfo tidParamInf : tidParamInfo.getChildren()) {
                if (tidParamInf.getApiName() == null) {
                    tidParamInf.setApiName(tidParamInf.getName());
                }
                childCellCount = buildExcel(tidParamInf, childRow, childCellCount);
            }
        } else {
            row.createCell(cllcnt).setCellValue(tidParamInfo.getApiName() + PIPE + tidParamInfo.getDataTypeStr());
        }
        cllcnt++;
        return cllcnt;
    }

    private boolean newSheetRequired(TidParamInfo tidParamInfo) {
        boolean newSheet = false;
        String tidParamDataType = tidParamInfo.getDataTypeStr();
        if (StringUtils.equalsIgnoreCase(tidParamDataType, OBJECT) || StringUtils.contains(tidParamDataType, OBJECT_ARRAY)) {
            newSheet = true;
        }
        return newSheet;
    }

    private void setHeaderDetails(Sheet sheet, String versionName, String majorVersion, String minorVersion) {
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0);
        dataRow.createCell(1).setCellValue(versionName);
        dataRow.createCell(2).setCellValue(Integer.parseInt(majorVersion));
        dataRow.createCell(3).setCellValue(Integer.parseInt(minorVersion));
    }

    @Override
    public List<Version> findVersionWithTidNameAndStatusPublishedORDeactivated(String tidName)
            throws BusinessException, SystemException {
        return versionBO.findVersionWithTidNameAndStatusPublishedORDeactivated(tidName);
    }

    /**
     * This method will retrieve all major version names with search criteria and return paginated data back with page info
     */
    @Override
    public ResponseWrapper<List<String>> findAllVersionName(SearchOptions searchOptions)
            throws BusinessException, SystemException {
        ResponseWrapper<List<String>> response = new ResponseWrapper<List<String>>();
        List<Version> verList = versionBO.findAllVersionName(searchOptions);

        Set<String> verSet = new LinkedHashSet<String>();

        for (Version var : verList) {
            verSet.add(var.getName());
        }

        List<String> versionNameList = new ArrayList<String>();
        versionNameList.addAll(verSet);

        Collections.sort(versionNameList, String.CASE_INSENSITIVE_ORDER);

        if (searchOptions.isDescending()) {
            Collections.reverse(versionNameList);
        }

        response.setPagingInfo(PagingInfo.setPagingForList(versionNameList, searchOptions));
        List pagedversionNameList = PagingInfo.getPagedList(versionNameList, response.getPagingInfo());
        response.setResponse(pagedversionNameList);
        return response;
    }

    /**
     * This method will retrieve all major&minor version and return paginated data back with page info
     */
    @Override
    public PageRecord<VersionInfo> findAllversionByVersionName(String versionName, SearchOptions searchOptions)
            throws BusinessException, SystemException {
        List<VersionInfo> versionInfoList = null;
        Page<Version> page = versionBO.findAllversionByVersionName(versionName, searchOptions);
        // TODO move it to common or try if convert works
        PageRecord<VersionInfo> pageRecord = new PageRecord<VersionInfo>();
        List<VersionInfo> versionList = convertToList(page.getContent(), VersionInfo.class);
        if (CollectionUtils.isNotEmpty(versionList)) {
            versionInfoList = new ArrayList<>();
            for (VersionInfo version : versionList) {
                version.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(version.getCreatedDate().getMillis(), null));
                version.setLastModifiedDateTime(
                        AdminUtil.getDateFormatMillisForEst(version.getLastModifiedDate().getMillis(), null));
                versionInfoList.add(version);
            }
        }
        pageRecord.setContent(versionInfoList);
        pageRecord.setNumber(page.getNumber());
        pageRecord.setNumberOfElements(page.getNumberOfElements());
        pageRecord.setSize(page.getSize());
        pageRecord.setTotalElements(page.getTotalElements());
        pageRecord.setTotalPages(page.getTotalPages());
        return pageRecord;
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageVersionMetric())")
    public Map<String, Object> getVersionMetrics(VersionMetricRequestInfo versionReq) throws BusinessException, SystemException {
        if (StringUtils.isEmpty(versionReq.getVersionName())) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000131, new Object[] {});
        }
        return versionBO.getVersionMetrics(versionReq);
    }

    @Override
    public List<MediateModelLibraryInfo> searchNewLibrary(SearchOptions searchOptions, String environmentId)
            throws BusinessException, SystemException {
        return versionBO.searchNewLibraries(searchOptions, environmentId);
    }

    @Override
    public VersionExecInfo getVersionExecutionEnvInfo(String name, Integer majorVersion, Integer minorVersion)
            throws SystemException {
        return versionContainerDAO.getExecutionEnvtVersion(name, majorVersion, minorVersion);

    }

    @Override
    public List<SupportPackage> getSupportPackagesForVersion(String name, Integer majorVersion, Integer minorVersion)
            throws SystemException {
        List<SupportPackage> supportPackages = versionContainerDAO.getVersionSupportPackage(name, majorVersion, minorVersion);
        for (SupportPackage sp : supportPackages) {
            LOGGER.error("supportapckage" + sp.getPackageFolder());
        }

        return supportPackages;
    }

    @Override
    public long getVersionCountByName(String versionName) throws SystemException {
        return versionContainerDAO.getVersionCountByName(versionName);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelAdd())")
    public long getVrsnCountByNameAddArtifact(String versionName) throws SystemException {
        return versionContainerDAO.getVersionCountByName(versionName);
    }

    @Override
    public VersionInfo searchVersionByName(String name) throws SystemException {
        Version version = versionBO.searchVersionByName(name);
        return convert(version, VersionInfo.class);
    }

    @Override
    public List<String> getAllModelsbyEnvironment(String executionEnvironment) throws SystemException {
        return versionBO.getAllTenantNamesByEnv(executionEnvironment);
    }

    /**
     * This method will retrieve all major&minor version and return paginated data back with page info
     */
    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManage())")
    public List<VersionInfo> findAllVersions(SearchOptions searchOptions) throws BusinessException, SystemException {
        List<VersionInfo> versionInfoList = versionBO.findAllVersions(searchOptions);
        // TODO move it to common or try if convert works

        if (versionInfoList != null) {
            for (VersionInfo vi : versionInfoList) {
                vi.setHasReportTemplate(modelDelegate.hasModelReportTemplate(vi.getId()));
            }
        }

        return versionInfoList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.delegate.VersionDelegate#searchReport(com.ca.umg.business.common.info.SearchOptions,
     * java.lang.String)
     */
    @Override
    public List<VersionInfo> searchReports(final SearchOptions searchOptions, final String executionLanguage)
            throws BusinessException, SystemException {
        return versionBO.searchReports(searchOptions, executionLanguage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageEmailPublishApproval())")
    public VersionInfo updateModelApprovalStatus(final String identifier, final String user, final VersionStatus status)
            throws BusinessException, SystemException {
        VersionInfo versionInfo = null;
        Version version = versionBO.getVersionDetails(identifier);
        if (version != null) {
            version = versionBO.updateModelApprovalStatus(version, user, status);
            versionInfo = convert(version, VersionInfo.class);
            // runtimeIntegrationClient.deploy(versionInfo, tenantUrl, authToken);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000068, new Object[] {});
        }
        return versionInfo;
    }

    @Override
    public boolean isVersionPublished(String versionID) throws BusinessException, SystemException {
        boolean published = false;
        final Version version = versionBO.findOneVersion(versionID);

        if (version != null && version.getStatus().equalsIgnoreCase(VersionStatus.PUBLISHED.getVersionStatus())) {
            published = true;
        }

        return published;
    }

    @Override
    public VersionInfo findOneversion(String versionID) throws BusinessException, SystemException {
        return convert(versionBO.findOneVersion(versionID), VersionInfo.class);
    }

    @Override
    public VersionInfo getVersionDetails(String name, int majorVersion, int minorVersion)
            throws BusinessException, SystemException {
        Version version = versionBO.findByNameAndVersion(StringUtils.lowerCase(name), majorVersion, minorVersion);
        return convert(version, VersionInfo.class);
    }
}