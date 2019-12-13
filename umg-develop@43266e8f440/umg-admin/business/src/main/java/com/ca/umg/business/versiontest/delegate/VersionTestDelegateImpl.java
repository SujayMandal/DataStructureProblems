package com.ca.umg.business.versiontest.delegate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.helper.MappingHelper;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.mapping.info.VersionTestContainer;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype;
import com.ca.umg.business.mid.extraction.info.PartialMapping;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.tenant.info.TenantIORequest;
import com.ca.umg.business.tenant.info.TenantReqResHeader;
import com.ca.umg.business.tenant.info.TenantReqResInfo;
import com.ca.umg.business.transaction.bo.TransactionBO;
import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.util.TransactionUtil;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.TenantParamInfo;
import com.ca.umg.business.version.info.VersionAPIContainer;
import com.ca.umg.business.version.info.VersionAPIInfo;
import com.ca.umg.business.versiontest.bo.TenantInput;

/**
 * @author basanaga
 * 
 */

@Named
@SuppressWarnings({"unchecked", "PMD.CyclomaticComplexity"})
public class VersionTestDelegateImpl extends AbstractDelegate implements VersionTestDelegate {

    @Inject
    private TransactionBO transactionBO;

    @Inject
    private VersionBO versionBO;

    @Inject
    private MappingBO mappingBO;

    @Inject
    private MappingHelper mappingHelper;

    @Inject
    private MappingDelegate mappingDelegate;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private UmgFileProxy umgFileProxy;

    private static final String SERVICER_DATA = "ServicerData";
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionTestDelegateImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.testbed.delegate.TestBedDelegate#getVersionTestContainer(java.lang.String)
     */
    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardTransactionRerun())")
    public VersionTestContainer getVersionTestContainer(String txnId) throws SystemException, BusinessException {
        Transaction transaction = transactionBO.getTransactionByTxnId(txnId);
        TransactionDocument txnDocument = transactionBO.getTxnDocumentByTxnId(txnId);
        VersionTestContainer versionTestContainer = null;
        TenantInput tenantInputJSON = null;

        if (transaction != null && txnDocument != null) {
            Version version = versionBO.findByNameAndVersion(transaction.getTenantModelName(), transaction.getMajorVersion(),
                    transaction.getMinorVersion());
            if (version != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    tenantInputJSON = ConversionUtil.convertJson(mapper.writeValueAsString(txnDocument.getTenantInput()),
                            TenantInput.class);
                } catch (SystemException | IOException exception) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000091,
                            new Object[] { transaction.getId() });
                }
                versionTestContainer = getVersionTestContainer(version, tenantInputJSON.getData());
                if (txnDocument.getRunAsOfDate() != null && versionTestContainer != null) {
                    versionTestContainer.setAsOnDate(AdminUtil.getDateFormatMillisForEst(txnDocument.getRunAsOfDate(),
                            BusinessConstants.UMG_EST_DATE_FORMAT));
                }
                versionTestContainer.setHasModelOpValidation(transaction.isOpValidation());
                versionTestContainer.setHasAcceptableValuesValidation(transaction.isAcceptValuesValidation());
                versionTestContainer.setPayloadStorage(txnDocument.getPayloadStorage());
                
                
            } else {
                BusinessException.raiseBusinessException(
                        BusinessExceptionCodes.BSE000087,
                        new Object[] { transaction.getTenantModelName(), transaction.getMajorVersion(),
                                transaction.getMinorVersion() });

            }
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000092, new Object[] { txnId });
        }

        return versionTestContainer;

    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageExportVersionAPI())")
    public VersionAPIContainer getVersionAPI(String versionId) throws SystemException, BusinessException {
        VersionAPIContainer versionAPIContainer = new VersionAPIContainer();
        Version version = versionBO.getVersionDetails(versionId);
        if (version != null) {
            try {
                versionAPIContainer.setTenantInputSchema(getTenantIOInfo(BusinessConstants.INPUT, version));
                versionAPIContainer.setTenantInputSchemaName(getName(version, BusinessConstants.INPUT));
                versionAPIContainer.setTenantOutputSchema(getTenantIOInfo(BusinessConstants.OUTPUT, version));
                versionAPIContainer.setTenantOutputSchemaName(getName(version, BusinessConstants.OUTPUT));
                versionAPIContainer.setSampleTenantInputJson(getSampleTenantInput(version.getMapping().getName(),
                        version.getName(), version.getMajorVersion(), version.getMinorVersion(), 
                        StringUtils.EMPTY, Boolean.FALSE,Boolean.TRUE,Boolean.TRUE,Boolean.FALSE));
            } catch (SystemException exception) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000091, new Object[] { versionId });
            }
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000068, new Object[] { versionId });
        }
        return versionAPIContainer;
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.versiontest.delegate.VersionTestDelegate#getSampleTenantInput(java.lang.String, java.lang.String, int, int, java.lang.String)
     */
    @Override
    public byte[] getSampleTenantInput(String tidName, String modelName, int majorVersion, int minorVersion, 
 String runAsDate,
            Boolean isTestForVerCreation, Boolean isOpValidation, Boolean isAcceptableValuesValidation,Boolean saveRLogs)
            throws SystemException, BusinessException {
        List<TidIoDefinition> definitions = mappingDelegate.getTidIoDefinitions(tidName, false);
        String rtJson = mappingDelegate.createRuntimeInputJson(definitions, modelName, majorVersion, minorVersion, 
 runAsDate,
                isTestForVerCreation, isOpValidation, isAcceptableValuesValidation,saveRLogs);
        return ConversionUtil.convertToFormattedJsonStringByteArray(rtJson.getBytes());
    }

    private String getName(Version version, String type) {
        return version.getName() + BusinessConstants.HYPHEN + version.getMajorVersion() + BusinessConstants.DOT
                + version.getMinorVersion() + BusinessConstants.HYPHEN + type;
    }

    private byte[] getTenantIOInfo(String type, Version version) throws SystemException, BusinessException {
        PartialMapping<TidParamInfo> partialIOTidParams = null;
        TenantIORequest tenantInputListInfo = new TenantIORequest();
        Mapping mapping = version.getMapping();
        byte[] tenantIOInfoData = null;
        if (BusinessConstants.INPUT.equalsIgnoreCase(type)) {
            MappingInput inputMapping = mappingBO.findInputByMapping(mapping);
            partialIOTidParams = getPartials(inputMapping.getTenantInterfaceDefn());
        } else if (BusinessConstants.OUTPUT.equalsIgnoreCase(type)) {
            MappingOutput outputMapping = mappingBO.findOutputByMapping(mapping);
            if (outputMapping != null) {
                partialIOTidParams = getPartials(outputMapping.getTenantInterfaceDefn());
            }
        }
        if (partialIOTidParams != null) {
            // clearDefaultValues(partialIOTidParams.getPartials());
            mappingHelper.removeTidParamsSkippedToTenant(partialIOTidParams.getPartials());
            tenantInputListInfo.setTenantReqResHeader(new TenantReqResHeader());
            List<TenantReqResInfo> tenantIOInfoList = convertToList(partialIOTidParams.getPartials(), TenantReqResInfo.class);
            tenantInputListInfo.setTenantReqResInfo(tenantIOInfoList);
            String tidIOJsonSchema = ConversionUtil.convertToJsonString(tenantInputListInfo);
            tenantIOInfoData = ConversionUtil.convertToFormattedJsonStringByteArray(tidIOJsonSchema.getBytes());
        }
        if (tenantIOInfoData == null) {
            String msg = "There is no " + type + " mapping found. Hence there is no output schema generated";
            tenantIOInfoData = msg.getBytes();
        }
        return tenantIOInfoData;
    }

    // private void clearDefaultValues(List<TidParamInfo> partials) {
    // for (TidParamInfo tidParamInfo : partials) {
    // if (tidParamInfo.getChildren() != null && tidParamInfo.getChildren().size() > 0) {
    // clearDefaultValues(tidParamInfo.getChildren());
    // } else {
    // if (tidParamInfo.getDatatype().getProperties() != null &&
    // tidParamInfo.getDatatype().getProperties().containsKey(BusinessConstants.DEFAULT_VALUE)) {
    // tidParamInfo.getDatatype().getProperties().put(BusinessConstants.DEFAULT_VALUE, BusinessConstants.EMPTY_STRING);
    // }
    // }
    // }
    // }

    private PartialMapping<TidParamInfo> getPartials(byte[] tidData) throws SystemException {
        return ConversionUtil.convertJson(tidData, PartialMapping.class, TidParamInfo.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.testbed.delegate.TestBedDelegate#getVersionTestContainerFromFile(byte[])
     */
    @Override
    public VersionTestContainer getVersionTestContainerFromFile(byte[] tenantInput) throws SystemException, BusinessException {
        TenantInput tenantInputJSON = null;
        Version version = null;
        try {
            tenantInputJSON = ConversionUtil.convertJson(tenantInput, TenantInput.class);
        } catch (SystemException exception) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000089, new Object[] {});
        }
        VersionTestContainer versionTestContainer = null;
        Object tenantModelName = null;
        Object majorVersion =null;
        Object minorVersion = null;
        Object addOnValidation = null;
        Map<String, Object> headerMap = null;
        if(tenantInputJSON != null) {
            headerMap = tenantInputJSON.getHeader();
        
            tenantModelName = headerMap.get(BusinessConstants.MODEL_NAME);
            majorVersion = headerMap.get(BusinessConstants.MAJOR_VERSION);
            minorVersion = headerMap.get(BusinessConstants.MINOR_VERSION);
            addOnValidation = headerMap.get(FrameworkConstant.ADD_ON_VALIDATION);
        }
        try {
            version = versionBO.findByNameAndVersion(String.valueOf(tenantModelName),
                    Integer.parseInt(String.valueOf(majorVersion)), Integer.parseInt(String.valueOf(minorVersion)));

        } catch (NumberFormatException e) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000087, new Object[] { tenantModelName,
                    majorVersion, minorVersion });
        }
        if (version != null) {
            if (tenantInputJSON == null) {
                tenantInputJSON = new TenantInput();
            }
            versionTestContainer = getVersionTestContainer(version, tenantInputJSON.getData());
            if (versionTestContainer != null && headerMap != null) {

                final Object oDate = headerMap.get(BusinessConstants.DATE);
                String date = null;
                if (oDate != null) {
                    if (oDate instanceof String) {
                        date = (String) oDate;
                    } else {
                        LOGGER.error("Error while converting request date");
                        throw new BusinessException(BusinessExceptionCodes.BSE000024, new Object[] { oDate,
                                BusinessConstants.UMG_EST_DATE_FORMAT });// NOPMD
                    }
                }

                if (!StringUtils.isEmpty(date)) {
                    try {
                        DateTimeFormatter format = ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC();
                        DateTime dateTime = format.parseDateTime((String) date);
                        versionTestContainer.setAsOnDate(AdminUtil.getDateFormatMillisForEst(dateTime.getMillis(),
                                BusinessConstants.UMG_EST_DATE_FORMAT));

                    } catch (UnsupportedOperationException | IllegalArgumentException e) // NOPMD
                    {
                        LOGGER.error("Error while converting request date", e);
                        throw new BusinessException(BusinessExceptionCodes.BSE000024, new Object[] { date, BusinessConstants.UMG_EST_DATE_FORMAT });// NOPMD
                    }
                } else {
                    versionTestContainer.setAsOnDate(AdminUtil.getDateFormatMillisForEst(new Date().getTime(),
                            BusinessConstants.UMG_EST_DATE_FORMAT));
                }
                getOpValidation(versionTestContainer, addOnValidation);
            }
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000087, new Object[] { tenantModelName,
                    majorVersion, minorVersion });

        }
        return versionTestContainer;

    }

    private void getOpValidation(VersionTestContainer versionTestContainer, Object addOnValidation) {
        if (addOnValidation != null) {
            List<String> addOnValidationList = (List<String>) addOnValidation;
            if (CollectionUtils.isNotEmpty(addOnValidationList) && addOnValidationList.contains(FrameworkConstant.MODEL_OUTPUT)) {
                versionTestContainer.setHasModelOpValidation(Boolean.TRUE);
            }
        }
    }

    /**
     * This method used to get Test Bed related information based on the tenant input
     * 
     * @param transaction
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    private VersionTestContainer getVersionTestContainer(Version version, Map<String, Object> tanantInpuTdata)
            throws BusinessException, SystemException {
        List<TidIoDefinition> tidIoDefinitions = null;
        PartialMapping<TidParamInfo> partialTidParams;
        List<TidParamInfo> tidParamInfos;
        MappingInput mappingInput = null;
        List<String> paramNames = new ArrayList<String>();
        VersionTestContainer versionTestContainer = new VersionTestContainer();
        Map<String, Object> updatedTenantInputData = new HashMap<String, Object>();

        setTidNameAndVersionDetails(version, versionTestContainer);
        Mapping mapping = version.getMapping();

        if (mapping != null) {
            versionTestContainer.setTidName(mapping.getName());
            mappingInput = mappingBO.findInputByMapping(mapping);
            if (mappingInput != null) {
                partialTidParams = ConversionUtil.convertJson(mappingInput.getTenantInterfaceDefn(), PartialMapping.class,
                        TidParamInfo.class);
                tidParamInfos = partialTidParams.getPartials();
                mappingHelper.removeTidParamsSkippedToTenant(tidParamInfos);
                tidIoDefinitions = new ArrayList<TidIoDefinition>();
                setTenantInputData(tanantInpuTdata, updatedTenantInputData);
                if (tidParamInfos != null) {
                    for (TidParamInfo tidParamInfo : tidParamInfos) {
                        setTidParamInfo(tidParamInfo, updatedTenantInputData);
                    }
                    List<String> defaultValuesList = new ArrayList<String>();
                    mappingHelper
                            .getIoDefinitionsFromTxnDashboard(tidParamInfos, tidIoDefinitions, defaultValuesList, paramNames);
                    versionTestContainer.setDefaultValuesList(defaultValuesList);
                    setAdditionalParams(paramNames, versionTestContainer, updatedTenantInputData);
                }
            } else {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000048, new Object[] { version.getName() });
            }

        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000048, new Object[] { version.getName() });

        }
        versionTestContainer.setTidIoDefinitions(tidIoDefinitions);
        return versionTestContainer;
    }

    /**
     * This method is used to set The Additional params in tenant input nad tehre is no matching field in TID
     * 
     * @param paramNames
     * @param versionTestContainer
     * @param tanantInpuTdata
     */
    private void setAdditionalParams(List<String> paramNames, VersionTestContainer versionTestContainer,
            Map<String, Object> tanantInpuTdata) {

        List<String> additionalparamNames = new ArrayList<String>();

        for (Map.Entry<String, Object> tenantInputEntry : tanantInpuTdata.entrySet()) {
            if (!paramNames.contains(tenantInputEntry.getKey()) && !tenantInputEntry.getKey().equals(SERVICER_DATA)) {
                additionalparamNames.add(tenantInputEntry.getKey());
            }

        }
        versionTestContainer.setAdditionalPropsList(additionalparamNames);

    }

    /**
     * This method used to set Tid name and Version details to VersionTestConatiner
     * 
     * @param version
     * @param versionTestContainer
     */
    private void setTidNameAndVersionDetails(Version version, VersionTestContainer versionTestContainer) {
        versionTestContainer.setModelName(version.getName());
        versionTestContainer.setMajorVersion(version.getMajorVersion());
        versionTestContainer.setMinorVersion(version.getMinorVersion());
        versionTestContainer.setVersionId(version.getId());
    }

    /**
     * This method used to set All Tid Param Infos for the TID to run Test Bed
     * 
     * @param tidParamInfo
     * @param tanantInpuTdata
     */
    private void setTidParamInfo(TidParamInfo tidParamInfo, Map<String, Object> tanantInpuTdata) {
        if (StringUtils.equalsIgnoreCase(tidParamInfo.getDatatype().getType(), Datatype.OBJECT.getDatatype())) {
            setTidParamInfo(tidParamInfo.getChildren(), tanantInpuTdata);
        } else {
            if (tanantInpuTdata.get(tidParamInfo.getFlatenedName()) != null) {
                tidParamInfo.setValue(tanantInpuTdata.get(tidParamInfo.getFlatenedName()).toString());

            }

        }

    }

    /**
     * @param tidParamInfoList
     * @param tanantInpuTdata
     */
    private void setTidParamInfo(List<TidParamInfo> tidParamInfoList, Map<String, Object> tanantInpuTdata) {
        for (TidParamInfo tidParamInfo : tidParamInfoList) {
            setTidParamInfo(tidParamInfo, tanantInpuTdata);
        }
    }

    /**
     * This method used for setting the values for the params
     * 
     * @param tanantInpuTdata
     * @param updatedTenantInputData
     */
    private void setTenantInputData(Map<String, Object> tanantInpuTdata, Map<String, Object> updatedTenantInputData) {
        Set<Map.Entry<String, Object>> tenantInputDatSet = tanantInpuTdata.entrySet();
        for (Map.Entry<String, Object> tenantInputEntry : tenantInputDatSet) {
            if (tenantInputEntry.getValue() instanceof Map) {
                setDataForTenantInputMap((Map<String, Object>) tenantInputEntry.getValue(), tenantInputEntry.getKey(),
                        updatedTenantInputData);

            } else {
                // UMG-3128: adding double quotes to array of Strings: START#
                List<Object> stringListwithDoubleQuotes = new ArrayList<>();
                AdminUtil.getStringArrayWithDoubleQuotes(tenantInputEntry.getValue(), stringListwithDoubleQuotes);
                // UMG-3128: adding double quotes to array of Strings: END#
                if (!CollectionUtils.isEmpty(stringListwithDoubleQuotes)) {

                    updatedTenantInputData.put(tenantInputEntry.getKey(),
                            stringListwithDoubleQuotes);
                } else {
                    updatedTenantInputData.put(tenantInputEntry.getKey(),
                            tenantInputEntry.getValue());
                }

            }

        }

    }

    /**
     * 
     * @param valueMap
     * @param key
     * @param updatedTenantInputData
     */
    private void setDataForTenantInputMap(Map<String, Object> valueMap, String key, Map<String, Object> updatedTenantInputData) {
        Set<Map.Entry<String, Object>> tenantInputDatSet = valueMap.entrySet();
        for (Map.Entry<String, Object> tenantInputEntry : tenantInputDatSet) {
            if (tenantInputEntry.getValue() instanceof Map) {
                setDataForTenantInputMap((Map<String, Object>) tenantInputEntry.getValue(), key + BusinessConstants.SLASH
                        + tenantInputEntry.getKey(), updatedTenantInputData);
            } else {
                // UMG-3128: adding double quotes to array of Strings: START#
                List<Object> stringListwithDoubleQuotes = new ArrayList<Object>();
                AdminUtil.getStringArrayWithDoubleQuotes(tenantInputEntry.getValue(), stringListwithDoubleQuotes);
                // UMG-3128: adding double quotes to array of Strings: END#
                if (!CollectionUtils.isEmpty(stringListwithDoubleQuotes)) {
                    updatedTenantInputData.put(key + BusinessConstants.SLASH + tenantInputEntry.getKey(),
                            stringListwithDoubleQuotes);
                } else {
                    updatedTenantInputData.put(key + BusinessConstants.SLASH + tenantInputEntry.getKey(),
                            tenantInputEntry.getValue());
                }

            }

        }

    }

    @Override
    public VersionAPIInfo getVersionDetails(String name, Integer majorVersion, Integer minorVersion) throws BusinessException,
            SystemException {
        List<Version> versions = versionBO.searchVersions(name, majorVersion, minorVersion);
        VersionAPIInfo versionAPIInfo = null;
        if (CollectionUtils.isNotEmpty(versions)) {
            versionAPIInfo = buildVersionAPIDetails(versions).get(0);
        }
        return versionAPIInfo;
    }

    @Override
    public List<VersionAPIInfo> getVersionDetails(String name) throws BusinessException, SystemException {
        List<Version> versions = versionBO.searchVersions(name, null, null);
        List<VersionAPIInfo> versionAPIInfos = null;
        if (CollectionUtils.isNotEmpty(versions)) {
            versionAPIInfos = buildVersionAPIDetails(versions);
        }
        return versionAPIInfos;
    }

    private List<VersionAPIInfo> buildVersionAPIDetails(List<Version> versions) throws SystemException, BusinessException {
        PartialMapping<TidParamInfo> partialMapping = null;
        VersionAPIInfo versionAPIInfo = null;
        Mapping mapping = null;
        MappingInput mappingInput = null;
        MappingOutput mappingOutput = null;
        Map<String, Object> header = null;
        List<VersionAPIInfo> versionAPIInfos = new ArrayList<VersionAPIInfo>();
        for (Version version : versions) {
            versionAPIInfo = new VersionAPIInfo();

            header = new HashMap<String, Object>();

            header.put("modelName", version.getName());
            header.put("majorVersion", version.getMajorVersion());
            header.put("minorVersion", version.getMinorVersion());
            header.put("date", "");
            header.put("transactionId", "");

            versionAPIInfo.setHeader(header);

            mapping = version.getMapping();
            mappingInput = mappingBO.findInputByMapping(mapping);
            mappingOutput = mappingBO.findOutputByMapping(mapping);

            if (mappingInput != null) {
                partialMapping = getPartials(mappingInput.getTenantInterfaceDefn());
                versionAPIInfo.setTidInputParams(convertToList(partialMapping.getPartials(), TenantParamInfo.class));
            }

            if (mappingOutput != null) {
                partialMapping = getPartials(mappingOutput.getTenantInterfaceDefn());
                versionAPIInfo.setTidOutputParams(convertToList(partialMapping.getPartials(), TenantParamInfo.class));
            }
            versionAPIInfos.add(versionAPIInfo);
        }
        return versionAPIInfos;
    }

    @Override
    public List<VersionAPIInfo> getAllVersionDetails() throws BusinessException, SystemException {
        List<Version> versions = versionBO.searchVersions(null, null, null);
        List<VersionAPIInfo> versionAPIInfos = null;
        if (CollectionUtils.isNotEmpty(versions)) {
            versionAPIInfos = buildVersionAPIDetails(versions);
        }
        return versionAPIInfos;
    }

    @Override
    public String createZip(List<Map<String, Object>> jsonList, boolean downloadSingleFile) throws BusinessException,
            SystemException {
        ZipOutputStream zos = null;
        FileOutputStream fos = null;
        String fileName = null;
        StringBuffer sanPath = null;
        Map<String, Object> jsonAsBatch = null;
        try {
            sanPath = new StringBuffer(AdminUtil.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider
                    .getParameter(SystemConstants.SAN_BASE))))
                    .append(File.separator).append("exceltozip");
            if (createDirectory(sanPath.toString())) {
                fileName = UUID.randomUUID().toString();
                sanPath.append(File.separator).append(fileName).append(".zip");

                File file = new File(sanPath.toString());
                fos = new FileOutputStream(file);
                zos = new ZipOutputStream(fos);
                if (downloadSingleFile) {
                    jsonAsBatch = new HashMap<>();
                    jsonAsBatch.put("requestCount", jsonList.size());
                    jsonAsBatch.put("requests", jsonList);
                    String tenantRequestJSON = ConversionUtil.convertToJsonString(jsonAsBatch);
                    byte[] formattedJSON = ConversionUtil.convertToFormattedJsonStringByteArray(tenantRequestJSON.getBytes());
                    TransactionUtil.addToZipFile("batch_input", formattedJSON, zos);
                } else {
                    int i = 0;
                    for (Object object : jsonList) {
                        i++;
                        String tenantRequestJSON = ConversionUtil.convertToJsonString(object);
                        byte[] formattedJSON = ConversionUtil.convertToFormattedJsonStringByteArray(tenantRequestJSON.getBytes());
                        TransactionUtil.addToZipFile("request_" + i, formattedJSON, zos);
                    }
                }
                zos.finish();

            }
        } catch (IOException ie) {
            SystemException.newSystemException("", new Object[] { ie.getMessage() });

        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ie) {
                SystemException.newSystemException("", new Object[] { ie.getMessage() });
            }

        }
        return fileName;
    }

    @Override
    public void getZipFile(ZipOutputStream zos, String fileName) throws BusinessException, SystemException {
        StringBuffer sanPath = null;
        ZipFile zipFile = null;
        Enumeration<? extends ZipEntry> entries = null;
        try {
            sanPath = new StringBuffer(AdminUtil.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider
                    .getParameter(SystemConstants.SAN_BASE))))
                    .append(File.separator).append("exceltozip");
            sanPath.append(File.separator).append(fileName).append(".zip");
            zipFile = new ZipFile(new File(sanPath.toString()));
            entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InputStream stream = zipFile.getInputStream(entry);
                zos.putNextEntry(entry);
                zos.write(IOUtils.toByteArray(stream));
                zos.closeEntry();
                if (stream != null) {
                    stream.close();
                }
            }

        } catch (IOException e) {
            SystemException.newSystemException("", new Object[] { e.getMessage() });
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    SystemException.newSystemException("", new Object[] { e.getMessage() });
                }

            }
        }
    }

    private boolean createDirectory(String exceltojsonpath) {
        boolean directoryAvailable = false;
        File file = new File(exceltojsonpath);
        if (!file.exists()) {
            directoryAvailable = file.mkdir();
        } else {
            directoryAvailable = true;
        }
        return directoryAvailable;
    }
}
