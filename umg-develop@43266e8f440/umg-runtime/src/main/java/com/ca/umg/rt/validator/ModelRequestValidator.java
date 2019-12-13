/*
 * MappingTransformer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.validator;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.plugin.commons.excel.xmlconverter.AcceptableValuesUtil;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.custom.serializers.DoubleSerializerModule;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.transformer.AbstractMetricTransformer;
import com.ca.umg.rt.transformer.ObjectBuilderFactory;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.StopWatchMetrics;
import com.codahale.metrics.annotation.Timed;

/**
 * Validates request send by the client. Validations are mostly mandatory check and data type compatibility checks. Result of
 * validations added to {@link Message} with key validations. If there are any validation errors then request is immediately
 * rejected. client will be notified with error code RSE000801. Error message will be a {@link String} representation of all
 * validation results
 **/
@SuppressWarnings("PMD")
public class ModelRequestValidator extends AbstractMetricTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRequestValidator.class);
    private CacheRegistry cacheRegistry;
    private DeploymentBO deploymentBO;

    private static final String ONE = "1";

    private static final String TWO = "2";
    /**
     * Does transformation of {@link Message} by validating client request and adding result with key "validations"
     *
     * @param message
     *            message Message} containing client request with key "tenantRequest".
     *
     * @return {@link Message} validation results are added by transformer with key "validations".
     *
     * @throws BusinessException
     *             Captures business validation errors.
     * @throws SystemException
     *             Captures system level exceptions.
     **/

    @Override
    @Timed
    protected Object doTransform(final Message<?> message) throws BusinessException, SystemException {
        StopWatchMetrics stopWatch = new StopWatchMetrics(allowMetrics());
        stopWatch.createCheckPointAndStart("ModelRequestValidator total transformation time: ");
        LOGGER.debug("Request validation started for message with id {}", message.getHeaders().get(MessageVariables.MESSAGE_ID));
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        FlowMetaData flowMetadata = null;
        Map<String, Object> payload = null;
        try {
            flowMetadata = getFlowMetadata(message);
            Map<String, Object> tenantInterfaceDefinition = getTenantInterfaceDefinition(flowMetadata);
            List<Map<String, Object>> partials = (List<Map<String, Object>>) tenantInterfaceDefinition
                    .get(MessageVariables.PARTIALS);
            removeTidParamsExposedToTenant(partials);
            payload = (Map<String, Object>) message.getPayload();
            Map<String, Object> data = (Map<String, Object>) payload.get("request");            
          
            Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
            TenantInfo tenantInfo = tenantMap.get(RequestContext.getRequestContext().getTenantCode());
            Map<String, String> tenantConfigsMap = tenantInfo.getTenantConfigsMap();            
            Map<String, Object> tenantReqHeader = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST_HEADER);  
            boolean isAcceptableValuePresent = false;
            if ((tenantReqHeader != null && tenantReqHeader.get(FrameworkConstant.ADD_ON_VALIDATION) != null)
                    || tenantConfigsMap.get(FrameworkConstant.ACCEPTABLE_VALUES) != null) {
                List<String> addOnValidations = (List<String>) tenantReqHeader.get(FrameworkConstant.ADD_ON_VALIDATION);
                if (Boolean.valueOf(tenantConfigsMap.get(FrameworkConstant.ACCEPTABLE_VALUES)) || (CollectionUtils.isNotEmpty(addOnValidations)
                        && addOnValidations.contains(FrameworkConstant.ACCEPTABLE_VALUES))) {
                	isAcceptableValuePresent = true;
                }
            }
            stopWatch.createCheckPointAndStart(this.getClass().getSimpleName() + " validate data: ");
            validateObject(partials, data, result,isAcceptableValuePresent);
            stopWatch.stopLastCheckPoint();
            payload.put(MessageVariables.VALIDATIONS, result);
            if (result.size() > RuntimeConstants.INT_ZERO) {
                payload.put("environment", flowMetadata.getModelLibrary().getLanguage().toUpperCase());
            }
            LOGGER.debug("Request validation completed for message with id {}",
                    message.getHeaders().get(MessageVariables.MESSAGE_ID));
        } catch (Exception e)// NOPMD
        {
            LOGGER.error("ERROR in ModelRequestValidator::doTransform", e);
            if (flowMetadata != null && payload != null) {
                payload.put("environment", flowMetadata.getModelLibrary().getLanguage().toUpperCase());
            }
            throw new SystemException(RuntimeExceptionCode.RSE000801, new Object[] { e.getMessage() }, e);
        }

        if (!result.isEmpty()) {
            throw new BusinessException(RuntimeExceptionCode.RVE000702, new Object[] { result.size() });
        }
        stopWatch.stopLastCheckPoint();
        addMetrics(message, stopWatch);
        return message;
    }

    /**
     * removes the parameters who has do not exposed to tenant as true
     * 
     * @param partials
     */
    private void removeTidParamsExposedToTenant(List<Map<String, Object>> partials) { // NOPMD
        if (partials != null) {
            Iterator itr = partials.iterator();
            while (itr.hasNext()) {
                Map<String, Object> map = (Map<String, Object>) itr.next();
                Iterator entries = map.entrySet().iterator();
                boolean exposedToTenant = false;
                boolean hasChildren = false;
                Object children = null;
                while (entries.hasNext()) {
                    Entry thisEntry = (Entry) entries.next();
                    Object key = thisEntry.getKey();
                    // Object value = thisEntry.getValue();
                    // ...
                    if (RuntimeConstants.EXPOSED_TO_TENANT.equals(key) && (Boolean) thisEntry.getValue()) {
                        exposedToTenant = true;
                    } else if (RuntimeConstants.CHILDREN.equals(key) && thisEntry.getValue() != null) {
                        hasChildren = true;
                        children = thisEntry.getValue();
                    }
                }
                if (exposedToTenant) {
                    itr.remove();
                } else if (hasChildren) {
                    removeTidParamsExposedToTenant((List<Map<String, Object>>) children);
                }
            }
        }
    }

    /**
     * Validates an object represented by currentContext parameter. Assumes that currentContext is an object and its properties
     * are described by "parameters". This method validates current object and if find any of the parameter is an another object
     * then recursively invoke same method to perform validations. This method is a recursive method.
     *
     * @param parameters
     *            {@link List} of definitions describing properties of an object.
     * @param currentObject
     *            Object under validation.
     * @param resultObject
     *            Validation results for each data points.
     **/
    private void validateObject(List<Map<String, Object>> parameters, Object currentObject, Map<String, Object> resultObject,boolean isAccpetableValuePresent) {
        JXPathContext currentContext = JXPathContext.newContext(currentObject);
        JXPathContext resultContext = JXPathContext.newContext(resultObject);
        resultContext.setFactory(new ObjectBuilderFactory());

        for (Map<String, Object> parameter : parameters) {
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            String name = null;
            
            if( parameter.get("apiName")!=null){
            	name= (String) parameter.get("apiName");
            }else{
            	name= (String) parameter.get("name");
            }
            
            String flatenedName = (String) parameter.get("flatenedName");
            Boolean mandatory = (Boolean) parameter.get("mandatory");
            Map<String, Object> dataTypeObject = (Map<String, Object>) parameter.get("datatype");
            String dataType = (String) dataTypeObject.get("type");
            Boolean isArray = (Boolean) dataTypeObject.get("array");
            row.put("dataType", dataType);
            row.put("mandatory", mandatory);
            row.put("fullName", flatenedName);
            Object value = currentContext.getValue(name);
            StringBuilder validationMessage = new StringBuilder(50);
            Object[] convertedArray = null;
            if(isAccpetableValuePresent){
            	if(parameter.get(RuntimeConstants.ACCEPTABLE_VALUE_ARR) instanceof List){
            		convertedArray = ((List<Object>) parameter.get(RuntimeConstants.ACCEPTABLE_VALUE_ARR)).toArray(); 
            	}            	
            }
         
            
            if(isAccpetableValuePresent){
            	if(parameter.get(RuntimeConstants.ACCEPTABLE_VALUE_ARR) instanceof List){
            		List<Object> list = (List<Object>) parameter.get(RuntimeConstants.ACCEPTABLE_VALUE_ARR); 
            	}            	      	
            }
            boolean error = validate(value, dataType, mandatory, isArray, validationMessage, dataTypeObject, row, convertedArray, name);
           
            if ((value != null) && (parameter.get(RuntimeConstants.CHILDREN) != null) && dataType.equals(DataTypes.OBJECT)) {

                if (isArray && value instanceof List) {
                    List valueList = (List) value;
                    for (Object valueObj : valueList) {
                        validateChildParams(resultContext, parameter, name, valueObj,isAccpetableValuePresent);
                    }
                } else {
                    validateChildParams(resultContext, parameter, name, value,isAccpetableValuePresent);
                }
            }
            if (error) {
                row.put("validation", validationMessage.toString());
                resultContext.setValue(name, row);
            }
            
        }
    }

    private void validateChildParams(JXPathContext resultContext, Map<String, Object> parameter, String name, Object value,boolean isAccpetableValuePresent) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) parameter.get(RuntimeConstants.CHILDREN);
        Map<String, Object> childValidation = new LinkedHashMap<String, Object>();
        validateObject(children, value, childValidation,isAccpetableValuePresent);

        if (!childValidation.isEmpty()) {
            resultContext.setValue(name, childValidation);
        }
    }

    /**
     * Validates data.
     *
     * @param value
     *            Object under validation
     * @param dataType
     *            data type of the object
     * @param mandatory
     *            is object value is mandatory
     * @param isArray
     *            is this object has to be an array
     * @param validationMessage
     *            validation message.
     * @param dataTypeObject
     *            data type related properties.
     *
     * @return DOCUMENT ME!
     **/

    private boolean validate(Object value, String dataType, boolean mandatory, boolean isArray, StringBuilder validationMessage,
            Map<String, Object> dataTypeObject, Map<String, Object> row,Object[] convertedArray, String apiName) {
        String dimensions = "";
        String length = "";

        if (dataTypeObject != null) {
            Map<String, Object> propertiesObject = (Map<String, Object>) dataTypeObject.get("properties");
            if (propertiesObject != null) {
                length = (String) propertiesObject.get("length") == null ? "" : (String) propertiesObject.get("length");
                dimensions = propertiesObject.get("dimensions") == null ? "" : propertiesObject.get("dimensions").toString();
            }
        }

        if (mandatory && (value == null)) {
            validationMessage.append("Field mandatory, but found empty/null value\n");
            return true;
        }
        if (mandatory && value instanceof String && StringUtils.isEmpty((String) value)) {
            validationMessage.append("Field mandatory, but found empty value\n");
            return true;
        }

        Boolean errorFlag = false;

        if (value != null) {

            if (StringUtils.trimToEmpty(dataType).equalsIgnoreCase(DataTypes.OBJECT)) {
                errorFlag = errorFlag || validateDimensionsForObject(dimensions, (String) row.get("fullName"), validationMessage,
                        value);
            } else {
                errorFlag = errorFlag
                        || validateDimensions(dimensions, (String) row.get("fullName"), validationMessage, value , dataType);
            }

            errorFlag = errorFlag
                    || validateLength(dimensions, (String) row.get("fullName"), validationMessage, value, dataType, length);
        }

        if (isArray) {
            errorFlag = errorFlag || validateArray(value, dataType, isArray, mandatory, validationMessage, dataTypeObject,convertedArray, apiName);
        } else {
            errorFlag = errorFlag || validateType(value, dataType, isArray, validationMessage, dataTypeObject,convertedArray, apiName);
        }

        return errorFlag;
    }

    public boolean validateDimensions(String inputDimensions, String name, StringBuilder validationMessage,
            Object inputDefaultValue,String dataType) {
    	com.fasterxml.jackson.databind.ObjectMapper mapper  = new com.fasterxml.jackson.databind.ObjectMapper();
		mapper.registerModule(new DoubleSerializerModule());
        String dimensions = StringUtils.trimToEmpty(inputDimensions.replace('[', ' ').replace(']', ' '));
        String defaultValue = StringUtils.trimToEmpty(inputDefaultValue.toString());
       try {
        if (StringUtils.isBlank(dimensions)) {
        	if(StringUtils.isNotBlank(defaultValue) && !dataType.equalsIgnoreCase("string") && defaultValue.indexOf('[') != -1){
                validationMessage
                        .append(name + " not defined as array in input definition but received array in input. Value received is "
                                + mapper.writeValueAsString(inputDefaultValue).toString() + ".");
                return true;
            }
        	if(StringUtils.isNotBlank(defaultValue) && dataType.equalsIgnoreCase("string") && defaultValue.indexOf('[') == 0 && !(inputDefaultValue instanceof String)){
                validationMessage
                        .append(name + " not defined as array in input definition but received array in input. Value received is "
                                + mapper.writeValueAsString(inputDefaultValue).toString() + ".");
                return true;
            }
        } else if (dimensions.equalsIgnoreCase(ONE)) {
            if (StringUtils.isNotBlank(defaultValue) && (defaultValue.charAt(0) != '[')) {
                validationMessage.append(
                        name + " defined as 1 dimensional array in input definition but received data in incorrect format in input. Value received is "
                                + mapper.writeValueAsString(inputDefaultValue).toString() + ".");
                return true;
            }
        } else if (dimensions.equalsIgnoreCase(TWO)) {
            if (StringUtils.isNotBlank(defaultValue)
                    && (defaultValue.charAt(0) != '[' || defaultValue.charAt(1) != '[' || defaultValue.charAt(2) == '[')) {
                validationMessage.append(
                        name + " defined as 2 dimensional array in input definition but received data in incorrect format in input. Value received is "
                                +  mapper.writeValueAsString(inputDefaultValue).toString() + ".");
                return true;
            }
        }
       } catch (Exception e ){
    	   LOGGER.error(e.getLocalizedMessage());
    	   LOGGER.debug("Exception : ",e);
       }

        return false;
    }

    public boolean validateDimensionsForObject(String inputDimensions, String name, StringBuilder validationMessage,
            Object inputDefaultValue) {
    	com.fasterxml.jackson.databind.ObjectMapper mapper  = new com.fasterxml.jackson.databind.ObjectMapper();
		mapper.registerModule(new DoubleSerializerModule());
        String dimensions = StringUtils.trimToEmpty(inputDimensions.replace('[', ' ').replace(']', ' '));
        String defaultValue = StringUtils.trimToEmpty(inputDefaultValue.toString());
      try {
        if (StringUtils.isBlank(dimensions)) {
            if (StringUtils.isNotBlank(defaultValue) && (defaultValue.charAt(0) == '[' && !(inputDefaultValue instanceof String))) {
            	validationMessage
                        .append(name + " not defined as array in input definition but received array in input. Value received is "
                                + mapper.writeValueAsString(inputDefaultValue).toString() + ".");
                return true;
            }
        } else if (dimensions.equalsIgnoreCase(ONE)) {
            if (StringUtils.isNotBlank(defaultValue) && (defaultValue.charAt(0) != '[')) {
                validationMessage.append(
                        name + " defined as 1 dimensional array in input definition but received data in incorrect format in input. Value received is "
                                + mapper.writeValueAsString(inputDefaultValue).toString() + ".");
                return true;
            }
        } else if (dimensions.equalsIgnoreCase(TWO)) {
            if (StringUtils.isNotBlank(defaultValue)
                    && (defaultValue.charAt(0) != '[' || defaultValue.charAt(1) != '[' || defaultValue.charAt(2) == '[')) {
                validationMessage.append(
                        name + " defined as 2 dimensional array in input definition but received data in incorrect format in input. Value received is "
                                + mapper.writeValueAsString(inputDefaultValue).toString() + ".");
                return true;
            }
        } 
        }catch(Exception e){
        	LOGGER.error(e.getLocalizedMessage());
        }
      

        return false;
    }

    public boolean validateLength(String inputDimensions, String name, StringBuilder validationMessage, Object inputDefaultValue,
            String dataType, String inputLength) {

        String dimensions = StringUtils.trimToEmpty(inputDimensions.replace('[', ' ').replace(']', ' '));
        String length = StringUtils.trimToEmpty(inputLength);

        if (StringUtils.equalsIgnoreCase(dataType, DataTypes.OBJECT) && StringUtils.equalsIgnoreCase(dimensions, ONE)) {
            List defaultValue = (List) inputDefaultValue;
            if (Integer.parseInt(length) > 0 && CollectionUtils.isNotEmpty(defaultValue) && defaultValue.size() > Integer.parseInt(length)) {
                validationMessage.append("Incorrect array length received in tenant input for " + name + " . Expected length "
                        + Integer.parseInt(length) + "; Received length " + defaultValue.size() + ".");
                return true;
            }
        } else {
            String defaultValue = StringUtils.trimToEmpty(inputDefaultValue.toString());

            if (dimensions.equalsIgnoreCase(ONE)) {
                if (!StringUtils.isBlank(length) && (Integer.parseInt(length) > 0)
                        && (defaultValue.split(",").length > Integer.parseInt(length))) {
                    validationMessage.append("Incorrect array length received in tenant input for " + name + " . Expected length "
                            + Integer.parseInt(length) + "; Received length " + defaultValue.split(",").length + ".");
                    return true;
                }
            } else if (dimensions.equalsIgnoreCase(TWO)) {
                Boolean xDimensionMismatch = false;
                Boolean yDimensionMismatch = false;
                String[] splitedValue = defaultValue.replaceAll(" ", "").split("],\\[");
                String[] splitedLength = length.split(",");
                int i = 0;
                if ((splitedLength.length == 2) && (Integer.parseInt(splitedLength[0].trim()) > 0)
                        && (splitedValue.length > Integer.parseInt(splitedLength[0].trim()))) {
                    xDimensionMismatch = true;
                }
                for (i = 0; i < splitedValue.length; i++) {
                    if (splitedValue[i].split(",").length != Integer.parseInt(splitedLength[1].trim())) {
                        yDimensionMismatch = true;
                        break;
                    }
                }
                if (xDimensionMismatch && yDimensionMismatch) {
                    validationMessage.append("Row and Column length mismatch for " + name
                            + " in input. Defined row length in IO definition " + splitedLength[0] + ", actual row length "
                            + "received " + splitedValue.length
                            + ". Actual row length must be equal <= to defined length. Defined column length in IO definition "
                            + splitedLength[1] + ", actual " + "column length received " + splitedValue[i].split(",").length
                            + ". Actual column length must be equal to defined length.");
                    return true;
                } else if (xDimensionMismatch) {
                    validationMessage.append("Row length mismatch for " + name + " in input. Defined row length in IO definition "
                            + splitedLength[0] + ", actual row length received " + splitedValue.length
                            + ". Actual length must be equal <= to defined length.");
                    return true;
                } else if (yDimensionMismatch) {
                    validationMessage
                            .append("Column length mismatch for " + name + " in input. Defined column length in IO definition "
                                    + splitedLength[1] + ", actual column length received " + splitedValue[i].split(",").length
                                    + ". Actual length must be equal to defined length.");
                    return true;
                }
            } else if (DataTypes.STRING.equalsIgnoreCase(dataType.trim())) {
                if (!StringUtils.isBlank(length) && Integer.parseInt(length) > 0) {
                    if (defaultValue.replace("\"", "").trim().length() > Integer.parseInt(length)) {
                        validationMessage.append("Defined length of " + name + " is " + Integer.parseInt(length)
                                + " in input definition but received length is " + defaultValue.replace("\"", "").trim().length()
                                + ".");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Validates type and associated check for a value.
     *
     * @param value
     *            DOCUMENT ME!
     * @param dataType
     *            DOCUMENT ME!
     * @param isArray
     *            DOCUMENT ME!
     * @param validationMessage
     *            DOCUMENT ME!
     * @param dataTypeObject
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public boolean validateType(Object value, String dataType, boolean isArray, StringBuilder validationMessage,
            Map<String, Object> dataTypeObject,Object[] convertedArray, String apiName) {
        boolean error = false;

        if ((value != null) && !isArray && !StringUtils.isEmpty(dataType)) {
            String errorMessage = validateType(value, dataType, dataTypeObject, apiName);
            if (!StringUtils.isEmpty(errorMessage)) {
                error = true;
                validationMessage.append(errorMessage);
            }
        }
        if(value!=null && convertedArray!=null && !error && !AcceptableValuesUtil.isDefaultValinAcceptValues(value, convertedArray, dataType)){  
        	validationMessage.append(String.format("Actual value is not from the acceptable_values defined. Actual value received is %s and acceptable values are %s",value,Arrays.asList(convertedArray)));
        	error = true;        	
        }
        

        return error;
    }

    /**
     * Validates an array value if value is not null
     *
     * @param value
     *            DOCUMENT ME!
     * @param isArray
     *            DOCUMENT ME!
     * @param validationMessage
     *            DOCUMENT ME!
     * @param dataTypeObject
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public boolean  validateArray(Object value, String dataType, boolean isArray, boolean mandatory, StringBuilder validationMessage,
            Map<String, Object> dataTypeObject,Object[] convertedArray, String apiName) {
        boolean error = false;

        if ((value != null) && isArray) {
            String errorMessage = null;
            if (mandatory) {
                errorMessage = validateType(value, DataTypes.ARRAY, dataTypeObject, apiName);
            } else {
                if (value instanceof List && !((List) value).isEmpty()) {
                    errorMessage = validateType(value, DataTypes.ARRAY, dataTypeObject, apiName);
                }
            }
            if (!StringUtils.isEmpty(errorMessage)) {
                error = true;
                validationMessage.append(errorMessage);
            }
        }
        if(!error){
        	AcceptableValuesUtil.acceptableValueCheckForArray(value, dataType, validationMessage, convertedArray); 
        	   if (!StringUtils.isEmpty(validationMessage)) {
                   error = true;                  
               }
        }
        
        return error;

        
    }
	

    /**
     * Validate type of an object
     *
     * @param value
     *            DOCUMENT ME!
     * @param dataType
     *            DOCUMENT ME!
     * @param dataTypeObject
     *
     * @return {@link String} Validation message.
     **/
    private String validateType(Object value, String dataType, Map<String, Object> dataTypeObject, String apiName) {
        TypeValidator typeValidator = TypeValidatorRegistry.getTypeValidator(dataType);

        if (typeValidator != null) {
            return typeValidator.validate(value, dataTypeObject, apiName);
        }

        return null;
    }

    /**
     * Retrieve Tenant Interface Definition associated with the current flow.
     *
     * @param flowMetadata
     *            {@link FlowMetaData}
     *
     * @return {@link Map} Tenant interface definition meta data.
     **/
    private Map<String, Object> getTenantInterfaceDefinition(FlowMetaData flowMetadata) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> tid = null;

        try {
            tid = mapper.readValue(flowMetadata.getMappingMetaData().getTenantInputDefinition(),
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException e) {
            LOGGER.error("Invalid tenant interface definition found in cache for flow with name {}", flowMetadata.getModelName());
            LOGGER.debug("Ecxception", e);
        }

        return tid;
    }

    /**
     * Retrieve {@link FlowMetaData} object from cache.
     *
     * @param message
     *            {@link Message} Used to get flow information like name,version etc.
     *
     * @return {@link FlowMetaData}
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
        
        if ((!isFlowDataInCache(metadata) && ! message.getHeaders().containsKey("isTest" )) ||  (message.getHeaders().containsKey("isTest" ) && message.getHeaders().get("isTest" , Integer.class) != 1 && !isFlowDataInCache(metadata) ))  {            
            LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format("CACHE DATA REFRESHED FOR VESRION :: %s", versionInfo.toString()));
        }
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
                && metadata.getMappingMetaData().getTenantInputDefinition() != null;
    }

    /**
     * {@link CacheRegistry} is used to cache {@link FlowMetaData} which contains complete information about an integration flow.
     *
     * @return {@link CacheRegistry}
     **/
    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    /**
     * Set property cacheRegistry of type {@link CacheRegistry}. Used for property injection.
     *
     * @param cacheRegistry
     *            {@link CacheRegistry}
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
}