/*
 * MappingTransformer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.transformer;

import static java.lang.Boolean.valueOf;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.ioreduce.RuntimeRequestReducer;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.object.size.util.ObjectSizeCalculator;
import com.ca.pool.TransactionMode;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.pool.model.RequestMode;
import com.ca.pool.model.RequestType;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.message.MessageConstants;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.container.StaticDataContainer;
import com.ca.umg.rt.validator.DataTypes;
import com.codahale.metrics.annotation.Timed;

/**
 * 
 **/
@SuppressWarnings({ "PMD", "unchecked" })
public class ModelRequestTransformer extends AbstractTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRequestTransformer.class);
    private static final String FACTOR = "factor";
    private static final String NATIVE_DATATYPE = "nativeDataType";
    private static final String STRINGS_AS_FACTORS = "STRINGS_AS_FACTORS";

    private CacheRegistry cacheRegistry;
    private DeploymentBO deploymentBO;
    private StaticDataContainer staticDataContainer;
    private SystemParameterProvider systemParameterProvider;
    private boolean stringAsFactors;
    public static final String STORE_RLOGS = "storeRLogs";
    public static final String VALUE_TRUE ="true";
    /**
     * DOCUMENT ME!
     *
     * @param message
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SystemException
     **/
    @Override
    @Timed
    public Object doTransform(final Message<?> message) throws SystemException {
        LOGGER.debug("Model data transformation starated for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));
        long startTime = System.currentTimeMillis();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FlowMetaData flowMetadata = getFlowMetadata(message);
            Map<String, Object> modelInterfaceDefinition = getModelInterfaceDefinition(flowMetadata);
            Map<String, Object> midMetadata = (Map<String, Object>) modelInterfaceDefinition.get(MessageVariables.METADATA);
            List<Map<String, Object>> midInput = (List<Map<String, Object>>) modelInterfaceDefinition
                    .get(MessageVariables.MID_INPUT);
            List<Map<String, Object>> midOutput = (List<Map<String, Object>>) modelInterfaceDefinition
                    .get(MessageVariables.MID_OUTPUT);
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            Map<String, Object> result = (Map<String, Object>) payload.get(MessageVariables.RESULT);
            Map<String, Object> tenantRequest = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST);
            Map<String, Object> tenantRequestHeader = (Map<String, Object>) tenantRequest.get(MessageVariables.HEADER);
            Map<String, Object> payloadTenantRequestHeader = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST_HEADER);
            
            tenantRequestHeader.put(EnvironmentVariables.MODEL_CHECKSUM, flowMetadata.getModelLibrary().getChecksum());
            payloadTenantRequestHeader.put(EnvironmentVariables.MODEL_CHECKSUM, flowMetadata.getModelLibrary().getChecksum());
            
            // Create model request header
            Map<String, Object> headerInfo = new LinkedHashMap<String, Object>();
            boolean rLogValue = false;
            if(tenantRequestHeader.containsKey(STORE_RLOGS)){
            	Object rlog = tenantRequestHeader.get(STORE_RLOGS);
            	if(rlog instanceof Boolean){
            		rLogValue = (boolean)tenantRequestHeader.get(STORE_RLOGS);
            	} else {
                     if(rlog != null && rlog instanceof String){
                    	if(StringUtils.equalsIgnoreCase(rlog.toString(), VALUE_TRUE)) {
                    		rLogValue = true;
                    	}
                     }
            	}
            } else{
            	 //Map<String, Object> requestHeader  = (Map<String, Object>)payload.get(MessageVariables.TENANT_REQUEST_HEADER);
            	// requestHeader.put(STORE_RLOGS, rLogValue);
            }
            Boolean isVersionCreationTest = false;;
            if (tenantRequestHeader != null && tenantRequestHeader.containsKey(MessageVariables.VERSION_CREATION_TEST)) {
            	  isVersionCreationTest = (Boolean) tenantRequestHeader.get(MessageVariables.VERSION_CREATION_TEST);
            	if( isVersionCreationTest){
            		headerInfo.put(STORE_RLOGS, true);
            	} 
            }
            
           if(!isVersionCreationTest){
        	   headerInfo.put(STORE_RLOGS, rLogValue);   
           }
            headerInfo.put(EnvironmentVariables.MODEL_LIBRARY_NAME, flowMetadata.getModelLibrary().getName());
            headerInfo.put(EnvironmentVariables.MODEL_LIBRARY_VERSION_NAME, flowMetadata.getModelLibrary().getUmgName());
            headerInfo.put("jarName", flowMetadata.getModelLibrary().getJarName());
            headerInfo.put("modelClass", midMetadata.get("model-class"));
            headerInfo.put("modelMethod", midMetadata.get("model-method"));
            headerInfo.put("tenantCode", message.getHeaders().get("tenantCode"));
            headerInfo.put("engine", flowMetadata.getModelLibrary().getLanguage());
            headerInfo.put("responseSize", midOutput.size());
            headerInfo.put("transactionCriteria", setTransactionCriteria(message, flowMetadata));
            headerInfo.put(EnvironmentVariables.MODEL_CHECKSUM, flowMetadata.getModelLibrary().getChecksum());

            boolean modelSizeReduction = requireModelSizeReduction();
            headerInfo.put("modelSizeReduction", modelSizeReduction);
            
            List<String> addOnValidation =ModelResponseUtil.setAddOnValidation(payload, cacheRegistry);
            
            if(CollectionUtils.isNotEmpty(addOnValidation)){	
    			((TransactionCriteria)headerInfo.get("transactionCriteria")).setAddOnValidation(addOnValidation);           	
    		}

            if (RuntimeConstants.R_LANGUAGE.equals(flowMetadata.getModelLibrary().getLanguage())) {
                List<SupportPackage> supportPackage = getSupportPackages(message);
                headerInfo.put("libraries", supportPackage);
                headerInfo.put("modelPackageName", getModelPackageName(message));
                String sStringAsFactors = systemParameterProvider.getParameter(STRINGS_AS_FACTORS);
                if (sStringAsFactors != null) {
                    stringAsFactors = valueOf(sStringAsFactors);
                } else {
                    stringAsFactors = true;
                }
                headerInfo.put("stringsAsFactors", stringAsFactors);
            }

            List<Map<String, Object>> modelRequestBody;
            // Create model reqeust body

            if (modelSizeReduction) {
                // modelRequestBody = createObject(midInput, result, flowMetadata.isAllowNull(),
                // flowMetadata.getModelLibrary().getLanguage());
                // modelRequestBody = convert(modelRequestBody);
                modelRequestBody = RuntimeRequestReducer.createObject(midInput, result, flowMetadata.isAllowNull(),
                        flowMetadata.getModelLibrary().getLanguage());
            } else {
                modelRequestBody = createObject(midInput, result, flowMetadata.isAllowNull(),
                        flowMetadata.getModelLibrary().getLanguage());
            }
           
            // Create model request
            Map<String, Object> modelRequest = new LinkedHashMap<String, Object>();
            modelRequest.put("headerInfo", headerInfo);
            modelRequest.put("payload", modelRequestBody);
            if(flowMetadata.getModelLibrary().getLanguage().startsWith(ExecutionLanguage.EXCEL.getValue())){
	            List<Map<String, Object>>   output = RuntimeRequestReducer.createObjectForExcelOutput(midOutput, flowMetadata.isAllowNull(),
	                    flowMetadata.getModelLibrary().getLanguage());
	            modelRequest.put("output", output);
            }
            ObjectSizeCalculator.getObjectDeepSize(modelRequest, payloadTenantRequestHeader.get("transactionId").toString(), "Request transformed in runtime");
            payload.put(MessageVariables.MODEL_REQUEST, modelRequest);
            payload.put(MessageVariables.TRANSACTION_CRITERIA, headerInfo.get("transactionCriteria"));
            payload.put(MessageVariables.MODEL_REQUEST_STRING, objectMapper.writeValueAsString(modelRequest));

            // Map req = new LinkedHashMap<String, Object>();
            // Map<String, Object> hi = new LinkedHashMap<String, Object>();
            // hi.put(EnvironmentVariables.MODEL_LIBRARY_VERSION_NAME, flowMetadata.getModelLibrary().getUmgName());
            // req.put("headerInfo", hi);
            // payload.put("modelRequest", modelRequest);
            // payload.put("headerInfo", headerInfo);
            // long start = System.currentTimeMillis();
            // payload.put("txCriteria", mapper.writer().writeValueAsString(headerInfo.get("transactionCriteria")));
            // FileWriter fileWriter = new FileWriter(System.getProperty("sanpath") + File.separator + "ModelInputSample.txt");

            // fileWriter.write(mapper.writer().writeValueAsString(modelRequest)); fileWriter.flush(); fileWriter.close();

            // LOGGER.error("ModelInputSample.txt write time: " + (System.currentTimeMillis() - start));
            LOGGER.debug("Model data transformation completed for message with id {}",
                    message.getHeaders().get(MessageVariables.MESSAGE_ID));
        } catch (SystemException systemException) {
            LOGGER.error("Error in MID validation", systemException);
            throw systemException;// NOPMD
        } catch (Exception e)// NOPMD
        {
            throw new SystemException(RuntimeExceptionCode.RSE000803, new Object[] {}, e);
        }
        LOGGER.debug("ModelRequestTransformer.doTransform : " + (System.currentTimeMillis() - startTime));
        return message;
    }

	

    /**
     * DOCUMENT ME!
     *
     * @param parameters
     *            DOCUMENT ME!
     * @param currentContext
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws SystemException
     **/
    public List<Map<String, Object>> createObject(List<Map<String, Object>> parameters, Object currentContext, boolean allowNull,
            String language) throws SystemException {
        List<Map<String, Object>> modelRequestBody = new ArrayList<Map<String, Object>>();
        JXPathContext context = JXPathContext.newContext(currentContext);

        for (Map<String, Object> item : parameters) {
            Map<String, Object> row = null;// NOPMD
            String name = item.get("apiName")!=null ? (String)item.get("apiName") :  (String)item.get("name");            
            String modelParamName = item.get("modelParamName")!=null? (String)item.get("modelParamName"):(String)item.get("name");           
            
            Map<String, Object> dataTypeObject = (Map<String, Object>) item.get("datatype");
            String dataType = (String) dataTypeObject.get("type");// NOPMD
            boolean isArray = (boolean) dataTypeObject.get("array");
            Object value = currentContext != null ? context.getValue(name) : null;

            if (StringUtils.equalsIgnoreCase(language, RuntimeConstants.MATLAB_LANGUAGE)
                    && !StringUtils.equalsIgnoreCase(dataType, "object") && isArray && !(value instanceof List)) {
                value = Arrays.asList(value);
            }
            if (value != null && StringUtils.equalsIgnoreCase(dataType, "double")) {
                if (value instanceof Integer) {
                    value = new Double(((Integer) value).doubleValue());
                } else if (value instanceof BigDecimal) {
                    value = new Double(((BigDecimal) value).doubleValue());
                } else if (value instanceof List) {
                    value = convertToDouble((List) value);
                }
            }

            Boolean mandatory = (Boolean) item.get("mandatory");
            String nativeDataType = null;
            if (mandatory && value == null) {
                throw new SystemException(RuntimeExceptionCode.RVE000216, new Object[] { name });
            }
            if (!mandatory && value == null && !allowNull) {
                continue;
            } else {
                Integer sequence = (Integer) item.get("sequence");
                nativeDataType = (String) item.get(NATIVE_DATATYPE);
                row = new LinkedHashMap<String, Object>();
                row.put("apiName", name);
                row.put("modelParameterName", modelParamName);
                row.put("sequence", sequence);
                row.put("dataType", dataType);
                if (nativeDataType != null) {
                    row.put(NATIVE_DATATYPE, nativeDataType);
                }
            }
            if (value != null) {
                if (!(StringUtils.equalsIgnoreCase(nativeDataType, "matrix")
                        || StringUtils.equalsIgnoreCase(nativeDataType, FACTOR))
                        && (value instanceof List || value.getClass().isArray())) {
                    row.put("collection", true);
                } else {
                    row.put("collection", false);
                }
            } else {
                row.put("collection", false);

            }

            if ((item.get("children") != null) && dataType.equals("object")) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) item.get("children");
                if (value instanceof List) {
                    List<Object> valueList = new ArrayList();
                    for (Object element : (List) value) {
                        if (element instanceof Map) {
                            value = createObject(children, element, allowNull, language);
                            Object obj = value; 
                            valueList.add(obj);
                        } else {
                            value = createObject(children, value, allowNull, language);
                            break;
                        }
                    }
                    if (valueList.size() > 0) {
                        value = valueList;
                    }

                } else {
                    value = createObject(children, value, allowNull, language);
                }
            }

            if (row != null) {
                if (value instanceof List) {
                    if (allowNull) {
                        row.put("value", value);
                        // row = reduceModelInputField(row);
                        modelRequestBody.add(row);
                    } else {
                        List<Object> tempList = new ArrayList<Object>();
                        checkforMultiDimensional(tempList, value);
                        if (tempList.size() > RuntimeConstants.INT_ZERO) {
                            row.put("value", value);
                            // row = reduceModelInputField(row);
                            modelRequestBody.add(row);
                        }
                    }
                    if ((StringUtils.equals("object", (String) row.get("dataType")) || (row.get(NATIVE_DATATYPE) != null
                            && (StringUtils.equals(("matrix"), (String) row.get(NATIVE_DATATYPE))
                                    || StringUtils.equals((FACTOR), (String) row.get(NATIVE_DATATYPE)))))
                            && language.equals(RuntimeConstants.R_LANGUAGE)) {
                        row = dataRow(row);
                    }
                } else {
                    if (StringUtils.equals(FACTOR, (String) row.get(NATIVE_DATATYPE))
                            && language.equals(RuntimeConstants.R_LANGUAGE)) {
                        List<Object> list = new ArrayList();
                        list.add(value);
                        row.put("value", list);
                        row = dataRow(row);
                        // row = reduceModelInputField(row);
                        modelRequestBody.add(row);
                    } else if (!isArray && value != null && StringUtils.equalsIgnoreCase(dataType, DataTypes.STRING)) {
                        row.put("value", (Object) (value.toString().replace("\"", "")));
                        row.put("value", (Object) (value.toString().replace("\\\\" , "\\")));
                        row.put("value", (Object) (value.toString().replace("\\" , "\\\\")));
                        modelRequestBody.add(row);
                    } else {
                        row.put("value", value);
                        // row = reduceModelInputField(row);
                        modelRequestBody.add(row);
                    }

                }

            }

        }

        return modelRequestBody;
    }

    private List convertToDouble(List list) {
        List returnList = new ArrayList();
        for (Object object : list) {
            if (object instanceof List) {
                returnList.add(convertToDouble((List) object));
            } else {
                if (object instanceof Integer) {
                    returnList.add(new Double(((Integer) object).doubleValue()));
                } else if (object instanceof BigDecimal) {
                    returnList.add(new Double(((BigDecimal) object).doubleValue()));
                } else {
                    returnList.add(object);
                }
            }
        }
        return returnList;
    }

    private Map<String, Object> dataRow(Map<String, Object> row) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> dataRow = new LinkedHashMap<>();
        dataRow.put("modelParameterName", "data");
        dataRow.put("sequence", RuntimeConstants.INT_ONE);
        if (StringUtils.equalsIgnoreCase((String) row.get(NATIVE_DATATYPE), "matrix")
                || StringUtils.equalsIgnoreCase((String) row.get(NATIVE_DATATYPE), "factor")) {
            dataRow.put("dataType", row.get("dataType"));
            row.put("dataType", "object");
        } else {
            dataRow.put("dataType", "object");
        }

        dataRow.put(NATIVE_DATATYPE, row.get(NATIVE_DATATYPE));
        dataRow.put("collection", row.get("collection"));
        dataRow.put("value", row.get("value"));
        list.add(dataRow);
        row.put("value", list);
        return row;

    }

    private List<Object> checkforMultiDimensional(List<Object> tempList, Object value) {
        for (Object obj : (List) value) {
            if (obj instanceof List) {
                if (((List) obj).size() > RuntimeConstants.INT_ZERO) {
                    checkforMultiDimensional(tempList, obj);
                }
            } else {
                if (obj != null) {
                    tempList.add(obj);
                    break;
                }
            }
        }

        return tempList;
    }

    /**
     * DOCUMENT ME!
     *
     * @param flowMetadata
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    private Map<String, Object> getModelInterfaceDefinition(FlowMetaData flowMetadata) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappingData = null;

        try {
            mappingData = mapper.readValue(flowMetadata.getMappingMetaData().getModelIoData(),
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException e) {
            LOGGER.error("Error while converting input mapping data to json object");
        }

        return mappingData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param message
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws BusinessException
     * @throws SystemException
     **/
    private FlowMetaData getFlowMetadata(final Message<?> message) throws SystemException, BusinessException {
        Map<Object, Object> containerMap = cacheRegistry
                .getMap(message.getHeaders().get(EnvironmentVariables.FLOW_CONTAINER_NAME, String.class));

        VersionInfo versionInfo = new VersionInfo(message.getHeaders().get(EnvironmentVariables.MODEL_NAME, String.class),
                message.getHeaders().get(EnvironmentVariables.MAJOR_VERSION, Integer.class),
                message.getHeaders().get(EnvironmentVariables.MINOR_VERSION, Integer.class));
        FlowMetaData metadata = (FlowMetaData) containerMap.get(versionInfo);
        if (message.getHeaders().containsKey("isTest" ) && message.getHeaders().get("isTest" , Integer.class) == 1 && !isFlowDataInCache(metadata)) {
            //LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            //containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format(" FOR VESRION  TEST :: %s", versionInfo.toString()));
        }
        
        if ((!isFlowDataInCache(metadata) && ! message.getHeaders().containsKey("isTest" )) ||  (message.getHeaders().containsKey("isTest" ) && message.getHeaders().get("isTest" , Integer.class) != 1 && !isFlowDataInCache(metadata) ))  {            LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format("CACHE DATA REFRESHED FOR VESRION :: %s", versionInfo.toString()));
        }
       /* if (!isFlowDataInCache(metadata)) {
            LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format("CACHE DATA REFRESHED FOR VESRION :: %s", versionInfo.toString()));
        }*/
        return metadata;
    }

    private FlowMetaData refreshFlowData(VersionInfo versionInfo) throws SystemException, BusinessException {
        FlowMetaData flowMetaData = null;
        List<FlowMetaData> flowMetaDatas = deploymentBO.gatherVersionData(versionInfo.getModelName(),
                versionInfo.getMajorVersion(), versionInfo.getMinorVersion());
        if (CollectionUtils.isNotEmpty(flowMetaDatas)) {
            flowMetaData = flowMetaDatas.get(RuntimeConstants.INT_ZERO);
        }
        return flowMetaData;
    }

    private boolean isFlowDataInCache(FlowMetaData metadata) {
        return metadata != null && metadata.getMappingMetaData() != null
                && metadata.getMappingMetaData().getModelIoData() != null;
    }

    private List<SupportPackage> getSupportPackages(final Message<?> message) throws SystemException, BusinessException {
        String versionName = message.getHeaders().get(EnvironmentVariables.MODEL_NAME, String.class);
        int majorVersion = message.getHeaders().get(EnvironmentVariables.MAJOR_VERSION, Integer.class);
        int minorVersion = message.getHeaders().get(EnvironmentVariables.MINOR_VERSION, Integer.class);
        final String tenantCode = message.getHeaders().get("tenantCode", String.class);
        String versionKey = StringUtils.join(versionName, RuntimeConstants.CHAR_HYPHEN, majorVersion,
                RuntimeConstants.CHAR_HYPHEN, minorVersion);
        Map<String, List<SupportPackage>> allSupportPackages = staticDataContainer.getAllSupportPackages().get(tenantCode);

        List<SupportPackage> supportPackages = null;
        if (allSupportPackages != null && allSupportPackages.containsKey(versionKey)) {
            supportPackages = allSupportPackages.get(versionKey);
        } else if (allSupportPackages != null) {
            // read support packages from db in case cache is not updated
            supportPackages = deploymentBO.getSupportPackages(versionName, majorVersion, minorVersion, tenantCode);
            allSupportPackages.put(versionKey, supportPackages);
        }
        return supportPackages;
    }

    private String getModelPackageName(final Message<?> message) throws SystemException, BusinessException {
        String versionName = message.getHeaders().get(EnvironmentVariables.MODEL_NAME, String.class);
        int majorVersion = message.getHeaders().get(EnvironmentVariables.MAJOR_VERSION, Integer.class);
        int minorVersion = message.getHeaders().get(EnvironmentVariables.MINOR_VERSION, Integer.class);
        final String tenantCode = message.getHeaders().get("tenantCode", String.class);
        String versionKey = StringUtils.join(versionName, RuntimeConstants.CHAR_HYPHEN, majorVersion,
                RuntimeConstants.CHAR_HYPHEN, minorVersion);
        Map<String, String> allPackageNames = staticDataContainer.getAllPackageNames().get(tenantCode);

        String modelPackagename = null;

        if (allPackageNames != null && allPackageNames.containsKey(versionKey)) {
            modelPackagename = allPackageNames.get(versionKey);
        } else if (allPackageNames != null) {
            // read model package name from db incase cache is not updated
            modelPackagename = deploymentBO.getModelPackageName(versionName, majorVersion, minorVersion, tenantCode);
        }
        return modelPackagename;
    }

    private TransactionCriteria setTransactionCriteria(final Message<?> message, FlowMetaData flowMetadata)
            throws SystemException, BusinessException {
        String versionName = message.getHeaders().get(EnvironmentVariables.MODEL_NAME, String.class);
        int majorVersion = message.getHeaders().get(EnvironmentVariables.MAJOR_VERSION, Integer.class);
        int minorVersion = message.getHeaders().get(EnvironmentVariables.MINOR_VERSION, Integer.class);
        String dateUsed = message.getHeaders().get(MessageVariables.DATE_USED, String.class);
        // this isTestTransaction variable can be '1' if request is a test(eg:- testbed/testurl) transaction
        // or '0' if request is for a published transaction
        int isTestTransaction = message.getHeaders().get(RequestType.TEST.toString().toLowerCase(), Integer.class);

        String modelVersionAsString = Integer.toString(majorVersion) + RuntimeConstants.CHAR_DOT + Integer.toString(minorVersion);

        VersionExecInfo versionExecInfo = null;

        final String tenantCode = message.getHeaders().get("tenantCode", String.class);
        String versionKey = StringUtils.join(versionName, RuntimeConstants.CHAR_HYPHEN, majorVersion,
                RuntimeConstants.CHAR_HYPHEN, minorVersion);

        Map<String, VersionExecInfo> tenantVersionExcEnvmap = staticDataContainer.getAllVersionExecEnvMap()
                .get(tenantCode);

        LOGGER.debug("Version key to search in tenantVersionMap: " + versionKey);
        if (tenantVersionExcEnvmap != null && tenantVersionExcEnvmap.containsKey(versionKey)) {
        	versionExecInfo = tenantVersionExcEnvmap.get(versionKey);
            LOGGER.debug("EnvVersion returned from map is : " + versionExecInfo);
        } else if (tenantVersionExcEnvmap != null) {
            LOGGER.debug("TenantVersionMap does not have entry for versionKey : " + versionKey);
            // read version execution environment from database incase the cache does not contain the data
            versionExecInfo = deploymentBO.getExecutionLanguageDeatils(versionName, majorVersion, minorVersion);
            tenantVersionExcEnvmap.put(versionKey, versionExecInfo);
            LOGGER.debug("EnvVersion returned from db is : " + versionExecInfo);
        }

        Map<String, Object> payload = (Map<String, Object>) message.getPayload();
        Map<String, Object> tenantRequest = (Map<String, Object>) payload.get(MessageConstants.TENANT_REQUEST);
        Map<String, Object> tenantRequestHdr = (Map<String, Object>) tenantRequest.get("header");
        String transactionType = (String) tenantRequestHdr.get(MessageVariables.TRANSACTION_TYPE);
        // added this to fix umg-4251 to set versionCreationTest flag to true
        // if it is test transaction during version creation else the flag will be false
        Boolean isVersionCreationTest = Boolean.FALSE;
        if (tenantRequestHdr.get(MessageVariables.VERSION_CREATION_TEST) != null) {
            isVersionCreationTest = (Boolean) tenantRequestHdr.get(MessageVariables.VERSION_CREATION_TEST);
            tenantRequestHdr.remove(MessageVariables.VERSION_CREATION_TEST);
        }

        TransactionCriteria transactionCriteria = new TransactionCriteria();
        transactionCriteria.setExecutionEnvironment(StringUtils.upperCase(flowMetadata.getModelLibrary().getExcEnv()));
        transactionCriteria.setModelName(versionName);
        transactionCriteria.setModelVersion(modelVersionAsString);
        transactionCriteria.setTenantCode((String) message.getHeaders().get(EnvironmentVariables.TENANT_CODE));
        transactionCriteria.setExecutionLanguage(flowMetadata.getModelLibrary().getLanguage().toUpperCase());
        String version = ((String)(((List)cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS).get(flowMetadata.getModelLibrary().getLanguage().toUpperCase())).get(0)));
        transactionCriteria.setExecutionLanguageVersion(version.substring(version.indexOf(RuntimeConstants.CHAR_HYPHEN)+1));//TODO need to remove      
        transactionCriteria.setTransactionRequestType(
                isTestTransaction == RuntimeConstants.INT_ONE ? RequestType.TEST.toString().toLowerCase()
                        : StringUtils.isBlank(transactionType) ? RequestType.PROD.toString().toLowerCase()
                                : RequestType.TEST.toString().toLowerCase());
        String transactionId = message.getHeaders().get(MessageVariables.TRANSACTION_ID, String.class);
        transactionCriteria.setClientTransactionId(transactionId);
        transactionCriteria.setUmgTransactionId((String) message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID));
        transactionCriteria.setIsVersionCreationTest(isVersionCreationTest);
        transactionCriteria.setRunAsData(dateUsed);
        if (StringUtils.isNotBlank((String) tenantRequestHdr.get(MessageVariables.BATCH_ID))) {
            transactionCriteria.setTransactionRequestMode(RequestMode.BATCH.getMode());
            transactionCriteria.setBatchId((String) tenantRequestHdr.get(MessageVariables.BATCH_ID));
        } else if (StringUtils.isNotBlank((String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER)) || StringUtils
                .equals((String) tenantRequestHdr.get(MessageVariables.TRAN_MODE), TransactionMode.BULK.getMode())) {
            transactionCriteria.setTransactionRequestMode(RequestMode.BULK.getMode());
            transactionCriteria.setBatchId((String) message.getHeaders().get(MessageVariables.BATCH_ID));
        } else {
            transactionCriteria.setTransactionRequestMode(RequestMode.ONLINE.toString());
        }
        if (tenantRequestHdr.get(FrameworkConstant.ADD_ON_VALIDATION) != null) {
            transactionCriteria.setAddOnValidation((List<String>) tenantRequestHdr.get(FrameworkConstant.ADD_ON_VALIDATION));
        }
        // set channel information
        String channel = null;
        if (isTestTransaction == 1) {
            channel = MessageVariables.ChannelType.HTTP.getChannel();
        } else {
            channel = message.getHeaders().get(MessageVariables.CHANNEL, String.class);
        }
        transactionCriteria.setTransactionRequestChannel(channel);

        return transactionCriteria;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    /**
     * DOCUMENT ME!
     *
     * @param cacheRegistry
     *            DOCUMENT ME!
     **/
    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public DeploymentBO getDeploymentBO() {
        return deploymentBO;
    }

    public void setDeploymentBO(DeploymentBO deploymentBO) {
        this.deploymentBO = deploymentBO;
    }

    public StaticDataContainer getStaticDataContainer() {
        return staticDataContainer;
    }

    public void setStaticDataContainer(StaticDataContainer staticDataContainer) {
        this.staticDataContainer = staticDataContainer;
    }

    public void setSystemParameterProvider(final SystemParameterProvider systemParameterProvider) {
        this.systemParameterProvider = systemParameterProvider;
    }

    public SystemParameterProvider getSystemParameterProvider() {
        return systemParameterProvider;
    }

    private boolean requireModelSizeReduction() {
        boolean bModelSizeReduction = false;
        final String sModelSizeReduction = systemParameterProvider
                .getParameter(SystemParameterProvider.REQUIRE_MODEL_SIZE_REDUCTION);
        if (sModelSizeReduction != null) {
            bModelSizeReduction = valueOf(sModelSizeReduction);
        }

        return bModelSizeReduction;
    }
}
