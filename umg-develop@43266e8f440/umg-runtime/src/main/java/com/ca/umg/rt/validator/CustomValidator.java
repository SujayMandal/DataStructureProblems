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
import java.util.ArrayList;
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

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
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
 * Validates data.frame object send by the client for R language. Validations are if the array lengths in data.frame are different it throws the validations
 **/
public class CustomValidator
	extends AbstractMetricTransformer
{
	private static final Logger LOGGER        = LoggerFactory.getLogger(CustomValidator.class);
	private CacheRegistry       cacheRegistry;
    private static final String NATIVE_DATATYPE = "nativeDataType";
    private  static final String ISTEST = "isTest";
	private DeploymentBO deploymentBO;	
	@SuppressWarnings("unchecked")
	@Override
	@Timed
	protected Object doTransform(final Message<?> message)
	  throws BusinessException,
	           SystemException
	{
        long startedTime = System.currentTimeMillis();
        LOGGER.error("Array validation Started at :"+startedTime);
		StopWatchMetrics stopWatch = new StopWatchMetrics(allowMetrics());
        stopWatch.createCheckPointAndStart("CustomValidator total transformation time: ");
        LOGGER.debug("Request validation started for message with id {}",
		             message.getHeaders().get(MessageVariables.MESSAGE_ID));
        Map<String, Object> result            = new LinkedHashMap<String, Object>();
        FlowMetaData flowMetadata = null;
        Map<String, Object> payload = null;
    
		try
		{
            flowMetadata = getFlowMetadata(message);	
	
            payload = (Map<String, Object>) message.getPayload();
			Map<String, Object>       data                      = (Map<String, Object>)payload.get("request");
            stopWatch.createCheckPointAndStart(this.getClass().getSimpleName() + " validate data: ");           
            Map<String, Object> modelInterfaceDefinition = getModelInterfaceDefinition(flowMetadata);            
            List<Map<String, Object>> midInput = (List<Map<String, Object>>) modelInterfaceDefinition
                    .get(MessageVariables.MID_INPUT);   
            List<String> childArrayMessages = new ArrayList<String>();
            validateObject(midInput, data, result, null, childArrayMessages,flowMetadata.getModelLibrary().getLanguage(),null);  
            stopWatch.stopLastCheckPoint();
			payload.put(MessageVariables.VALIDATIONS, result);
            if (result.size() > RuntimeConstants.INT_ZERO) {
                payload.put("environment", flowMetadata.getModelLibrary().getLanguage().toUpperCase());
            }
			LOGGER.debug("Request validation completed for message with id {}",
			             message.getHeaders().get(MessageVariables.MESSAGE_ID));
		}
		catch (Exception e)//NOPMD
		{
			LOGGER.error("ERROR in CustomValidator::doTransform", e);
			if(flowMetadata!=null && payload!=null) {
			    payload.put("environment", flowMetadata.getModelLibrary().getLanguage().toUpperCase());
			}
			throw new SystemException(RuntimeExceptionCode.RSE000801, new Object[] { e.getMessage() },e);
		}

		if (!result.isEmpty())
		{
			throw new BusinessException(RuntimeExceptionCode.RVE000702,
			                            new Object[] { result.size() });
		}
        stopWatch.stopLastCheckPoint();
        addMetrics(message, stopWatch); 
        LOGGER.error("Array validation Ended at :"+System.currentTimeMillis());
        LOGGER.error("Time Taken for Array Input Validation is :"+(System.currentTimeMillis()-startedTime));
		return message;
	}
	

	/**
	 * Validates an object represented by currentContext parameter. Assumes that
	 * currentContext is an object and its properties are described by "parameters". This method
	 * validates current object and if find any of the parameter is an another object then
	 * recursively invoke same method to perform validations. This method is a recursive method.
	 *
	 * @param parameters {@link List} of definitions describing properties of an object.
	 * @param currentObject Object under validation.
	 * @param resultObject Validation results for each data points.
	 **/
	@SuppressWarnings("unchecked")
	private void validateObject(List<Map<String, Object>> parameters,
	                            Object                    currentObject,
 Map<String, Object> resultObject,
            String parentName,List<String> childArrayMessages,String language,String nativeDatatype)
	{
		JXPathContext currentContext = JXPathContext.newContext(currentObject);
		JXPathContext resultContext  = JXPathContext.newContext(resultObject);
		
        resultContext.setFactory(new ObjectBuilderFactory());

        int firstElementSize = RuntimeConstants.INT_MINUS_ONE;
        Boolean isArraySizeError = Boolean.FALSE;
     
		for (Map<String, Object> parameter : parameters)
		{
			
			Map<String, Object> row            = new LinkedHashMap<String, Object>();			
			String              name           = parameter.get("apiName") != null ? (String)parameter.get("apiName") :  (String)parameter.get("name");
			Boolean             mandatory      = (Boolean)parameter.get("mandatory");
			Map<String, Object> dataTypeObject = (Map<String, Object>)parameter.get("datatype");
			String              dataType       = (String)dataTypeObject.get("type");
			Boolean             isArray        = (Boolean)dataTypeObject.get("array");
			row.put("dataType", dataType);
			row.put("mandatory", mandatory);

			Object       value             = currentContext.getValue(name);
			
          
            if (parentName != null && isArray && value != null && StringUtils.equals("data.frame",nativeDatatype)) {
                int elementSize = ((List) value).size();
                if (firstElementSize == RuntimeConstants.INT_MINUS_ONE) {
                        firstElementSize = ((List) value).size();
                } else if (elementSize != firstElementSize) {                 
                        isArraySizeError = Boolean.TRUE;
                 }
                    childArrayMessages.add(parentName + "." + name + "-" + ((List) value).size());
            }
			if ((value != null) && (parameter.get("children") != null) &&
				    dataType.equals("object")){
				List<Map<String, Object>> children        = (List<Map<String, Object>>)parameter.get("children");
				Map<String, Object>       childValidation = new LinkedHashMap<String, Object>();	
				List<String>     arrayMessages = new ArrayList<String>();	
				if(StringUtils.equalsIgnoreCase(language, RuntimeConstants.R_LANGUAGE) ){
					if(isArray && value instanceof List) {
						List valueList = (List) value;
	                    for (Object valueObj : valueList) {
	                    	validateObject(children, valueObj, childValidation, name,arrayMessages,language,(String)parameter.get(NATIVE_DATATYPE));
	                    }
					} else {
						validateObject(children, value, childValidation, name,arrayMessages,language,(String)parameter.get(NATIVE_DATATYPE));
					}
				}
                if (CollectionUtils.isNotEmpty(arrayMessages)) {                	
                    childValidation.put("validation",
                            "Bad input. Please refer to RA API documentation and resubmit. Array length should be constant across all parameters within object : "
                                    + name + ". Following are the  arrayLengths in current input. " + arrayMessages.toString());
                }

                if (!childValidation.isEmpty()) {
                    resultContext.setValue(name, childValidation);
                }            
                
           }
		}
		 if (!isArraySizeError) {	            
	            childArrayMessages.clear();
	     }
      

	}


	

	/**
	 * Retrieve {@link FlowMetaData} object from cache.
	 *
	 * @param message {@link Message} Used to get flow information like name,version etc.
	 *
	 * @return {@link FlowMetaData}
	 * @throws BusinessException 
	 * @throws SystemException 
	 **/
    private FlowMetaData getFlowMetadata(final Message<?> message) throws SystemException, BusinessException {
        Map<Object, Object> containerMap = cacheRegistry.getMap(message.getHeaders().get(
                EnvironmentVariables.FLOW_CONTAINER_NAME, String.class));

        VersionInfo versionInfo = new VersionInfo(message.getHeaders().get(EnvironmentVariables.MODEL_NAME, String.class),
                message.getHeaders().get(EnvironmentVariables.MAJOR_VERSION, Integer.class), message.getHeaders().get(
                        EnvironmentVariables.MINOR_VERSION, Integer.class));

        FlowMetaData metadata = (FlowMetaData) containerMap.get(versionInfo);
        if (message.getHeaders().containsKey(ISTEST ) && message.getHeaders().get(ISTEST , Integer.class) == 1 && !isFlowDataInCache(metadata)) {
            //LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            //containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format(" FOR VESRION  TEST :: %s", versionInfo.toString()));
        }
        
        if ((!isFlowDataInCache(metadata) && ! message.getHeaders().containsKey(ISTEST )) ||  (message.getHeaders().containsKey(ISTEST ) && message.getHeaders().get(ISTEST , Integer.class) != 1 && !isFlowDataInCache(metadata) ))  {
            LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format("CACHE DATA REFRESHED FOR VESRION :: %s", versionInfo.toString()));
        }
        /*if(!isFlowDataInCache(metadata)){
            LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s",versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format("CACHE DATA REFRESHED FOR VESRION :: %s",versionInfo.toString()));
        }*/
        return metadata;
    }
    
    private FlowMetaData refreshFlowData(VersionInfo versionInfo) throws SystemException, BusinessException {
        FlowMetaData flowMetaData = null;
        List<FlowMetaData> flowMetaDatas = deploymentBO.gatherVersionData(versionInfo.getModelName(),
                versionInfo.getMajorVersion(), versionInfo.getMinorVersion());
        if(CollectionUtils.isNotEmpty(flowMetaDatas)){
            flowMetaData = flowMetaDatas.get(RuntimeConstants.INT_ZERO);
        }
        return flowMetaData;
    }
    
    private boolean isFlowDataInCache(FlowMetaData metadata) {
        return metadata != null && metadata.getMappingMetaData() != null
                && metadata.getMappingMetaData().getTenantInputDefinition() != null;
    }

	/**
	 * {@link CacheRegistry} is used to cache {@link FlowMetaData} which contains complete
	 * information about an integration flow.
	 *
	 * @return {@link CacheRegistry}
	 **/
	public CacheRegistry getCacheRegistry()
	{
		return cacheRegistry;
	}

	/**
	 * Set property cacheRegistry of type {@link CacheRegistry}. Used for property
	 * injection.
	 *
	 * @param cacheRegistry {@link CacheRegistry}
	 **/
	public void setCacheRegistry(CacheRegistry cacheRegistry)
	{
		this.cacheRegistry = cacheRegistry;
	}

    public DeploymentBO getDeploymentBO() {
        return deploymentBO;
    }

    public void setDeploymentBO(DeploymentBO deploymentBO) {
        this.deploymentBO = deploymentBO;
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
 
}
