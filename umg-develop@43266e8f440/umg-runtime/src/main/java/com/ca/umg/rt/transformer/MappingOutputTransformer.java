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
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.pool.TransactionMode;
import com.ca.umg.me2.util.ModelExecResponse;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.util.MessageVariables;
import com.codahale.metrics.annotation.Timed;

/**
 * Maps MID to TID
 **/
public class MappingOutputTransformer extends AbstractTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingOutputTransformer.class);
    private CacheRegistry cacheRegistry;
    private DeploymentBO deploymentBO;
    private  static final String ISTEST = "isTest";
    /**
     * Does transformation of data from model call result to tenant format
     *
     * @param message
     *            {@link Message}
     *
     * @return {@link Message}
     *
     * @throws SystemException
     **/
    @SuppressWarnings("unchecked")
    @Override
    @Timed
    protected Object doTransform(final Message<?> message) throws SystemException {
        LOGGER.debug("Output data transformation starated for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));
        long startTime = System.currentTimeMillis();
        try {
            FlowMetaData flowMetadata = getFlowMetadata(message);
            Map<String, Object> mappingData = getOutputMapping(flowMetadata);
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            
            boolean isModelResponseIsMap = false;
            Map<String, Object> modelResponseTranslated = null;
            List<Map<String, Object>> modelResponseTranslatedForList = null;
            if(payload.get(MessageVariables.MODEL_RESPONSE_TRANSLATED) instanceof Map){
            	isModelResponseIsMap =  true;
            modelResponseTranslated = (Map<String, Object>) payload.get(MessageVariables.MODEL_RESPONSE_TRANSLATED);
            }
            if(payload.get(MessageVariables.MODEL_RESPONSE_TRANSLATED) instanceof List){
            	isModelResponseIsMap =  false;
            	modelResponseTranslatedForList = (List<Map<String, Object>>) payload.get(MessageVariables.MODEL_RESPONSE_TRANSLATED);
            }
            
            Map<String, Object> tenantRequestHeader = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST_HEADER);
            ModelResponseUtil.setAddOnValidation(payload, cacheRegistry);
            Integer tenantTranCount = tenantRequestHeader != null
                    ? (Integer) tenantRequestHeader.get(MessageVariables.TENANT_TRAN_COUNT) : null;
            /* changes for UMG-5015 */
            ModelExecResponse<Map<String, Object>> modelExecResponse = (ModelExecResponse) payload
                    .get(MessageVariables.ME2_RESPONSE);
            Map<String, Object> response = modelExecResponse.getResponse();
            Map<String, Object> modelResponseHeader = (Map<String, Object>) response.get(MessageVariables.RESPONSE_HEADER_INFO);

            setTenantResponseHeader(message, mappingData, payload, isModelResponseIsMap, modelResponseTranslated,
					modelResponseTranslatedForList, tenantRequestHeader, tenantTranCount);
            /* changes for UMG-5015 */
            setModelResponseHeader(tenantRequestHeader, modelResponseHeader);           
        } catch (Exception e) {// NOPMD
            LOGGER.error("An error occurred while mapping MID-TID output parameters.", e);
            throw new SystemException(RuntimeExceptionCode.RSE000805, new Object[] { e.getMessage() }, e);
        }
        LOGGER.error("MappingOutputTransformer.doTransform : " + (System.currentTimeMillis() - startTime));
        return message;
    }

	private void setTenantResponseHeader(final Message<?> message, Map<String, Object> mappingData,
			Map<String, Object> payload, boolean isModelResponseIsMap, Map<String, Object> modelResponseTranslated,
			List<Map<String, Object>> modelResponseTranslatedForList, Map<String, Object> tenantRequestHeader,
			Integer tenantTranCount) {
		if ((mappingData == null) || mappingData.isEmpty()) {
		    Map<String, Object> tenantResponse = new LinkedHashMap<String, Object>();
		    Map<String, Object> tenantResponseHeader = new LinkedHashMap<String, Object>();
		    tenantResponseHeader.put(MessageVariables.MODEL_NAME, message.getHeaders().get(MessageVariables.MODEL_NAME));
		    tenantResponseHeader.put(MessageVariables.MAJOR_VERSION,
		            message.getHeaders().get(MessageVariables.MAJOR_VERSION));
		    tenantResponseHeader.put(MessageVariables.MINOR_VERSION,
		            message.getHeaders().get(MessageVariables.MINOR_VERSION));
		    tenantResponseHeader.put(MessageVariables.DATE, message.getHeaders().get(MessageVariables.DATE_USED));
		    tenantResponseHeader.put(MessageVariables.TRANSACTION_ID,
		            message.getHeaders().get(MessageVariables.TRANSACTION_ID));
		    tenantResponseHeader.put(MessageVariables.UMG_TRANSACTION_ID,
		            message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID) != null
		                    ? message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID)
		                    : message.getHeaders().get(MessageVariables.MESSAGE_ID));
		    tenantResponseHeader.put(MessageVariables.SUCCESS, true);
		    setTenantResponse(message, tenantRequestHeader, tenantTranCount, tenantResponseHeader);
		    tenantResponse.put(MessageVariables.HEADER, tenantResponseHeader);
		    if(isModelResponseIsMap){
		        tenantResponse.put(MessageVariables.DATA, modelResponseTranslated);
		        }else{
		        	 tenantResponse.put(MessageVariables.DATA, modelResponseTranslatedForList);
		        }
		    payload.put(MessageVariables.TENANT_RESPONSE, tenantResponse);
		} else {
		    List<Map<String, String>> mapList = (List<Map<String, String>>) mappingData.get(MessageVariables.PARTIALS);
		    Map<String, Object> result = new LinkedHashMap<String, Object>();
		    JXPathContext context;
		    if(isModelResponseIsMap){
		    	context = JXPathContext.newContext(modelResponseTranslated);
		    } else{
		    	context = JXPathContext.newContext(modelResponseTranslatedForList);
		    }
		    JXPathContext resultContext = JXPathContext.newContext(result);
		    resultContext.setFactory(new ObjectBuilderFactory());
		    for (Map<String, String> item : mapList) {
		        String in = item.get(MessageVariables.MAPPING_IN);
		        String out = item.get(MessageVariables.MAPPING_OUT);
		        Object value = context.getValue(in);
		        resultContext.setValue(out, value);
		    }
		    Map<String, Object> tenantResponse = new LinkedHashMap<String, Object>();
		    Map<String, Object> tenantResponseHeader = new LinkedHashMap<String, Object>();
		    tenantResponseHeader.put(MessageVariables.MODEL_NAME, message.getHeaders().get(MessageVariables.MODEL_NAME));
		    tenantResponseHeader.put(MessageVariables.MAJOR_VERSION,
		            message.getHeaders().get(MessageVariables.MAJOR_VERSION));
		    tenantResponseHeader.put(MessageVariables.MINOR_VERSION,
		            message.getHeaders().get(MessageVariables.MINOR_VERSION));
		    tenantResponseHeader.put(MessageVariables.DATE, message.getHeaders().get(MessageVariables.DATE_USED));
		    tenantResponseHeader.put(MessageVariables.TRANSACTION_ID,
		            message.getHeaders().get(MessageVariables.TRANSACTION_ID));
		    tenantResponseHeader.put(MessageVariables.UMG_TRANSACTION_ID,
		            message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID) != null
		                    ? message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID)
		                    : message.getHeaders().get(MessageVariables.MESSAGE_ID));
		    tenantResponseHeader.put(MessageVariables.SUCCESS, true);
		    setTenantResponse(message, tenantRequestHeader, tenantTranCount, tenantResponseHeader);
		    tenantResponse.put(MessageVariables.HEADER, tenantResponseHeader);
		    tenantResponse.put(MessageVariables.DATA, result);
		    payload.put(MessageVariables.TENANT_RESPONSE, tenantResponse);
		}
	}

	private void setModelResponseHeader(Map<String, Object> tenantRequestHeader,
			Map<String, Object> modelResponseHeader) {
		Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
		TenantInfo tenantInfo = tenantMap.get(RequestContext.getRequestContext().getTenantCode());    
		Map<String,String> tenantConfigsMap = tenantInfo.getTenantConfigsMap();
		if(tenantRequestHeader.get(FrameworkConstant.ADD_ON_VALIDATION)!=null) {
			modelResponseHeader.put(FrameworkConstant.ADD_ON_VALIDATION, tenantRequestHeader.get(FrameworkConstant.ADD_ON_VALIDATION));                	
		}else if(Boolean.valueOf(tenantConfigsMap.get("ModelOutput_Validation"))){
			List<String> addonValidations = new ArrayList<String>();
			addonValidations.add(FrameworkConstant.MODEL_OUTPUT);
			modelResponseHeader.put(FrameworkConstant.ADD_ON_VALIDATION, addonValidations);            	
		}
		modelResponseHeader.remove(MessageVariables.EXECUTION_COMMAND);
		modelResponseHeader.remove(MessageVariables.EXECUTION_RESPONSE);
		modelResponseHeader.remove(MessageVariables.EXECUTION_LOGS);
	}

	private void setTenantResponse(final Message<?> message, Map<String, Object> tenantRequestHeader,
			Integer tenantTranCount, Map<String, Object> tenantResponseHeader) {
		if(tenantRequestHeader.get(FrameworkConstant.ADD_ON_VALIDATION)!=null) {
			tenantResponseHeader.put(FrameworkConstant.ADD_ON_VALIDATION, tenantRequestHeader.get(FrameworkConstant.ADD_ON_VALIDATION));                	
		}
		if (tenantTranCount != null) {
		    tenantResponseHeader.put(MessageVariables.TENANT_TRAN_COUNT, tenantTranCount);
		}
		setTntResponseHeader(message, tenantResponseHeader);
	}

    private void setTntResponseHeader(final Message<?> message, Map<String, Object> tenantResponseHeader) {
        Map<String, Object> payloadRequest = (Map<String, Object>) message.getPayload();
        if (payloadRequest != null) {
            /*
             * Map<String, Object> tenantRequest = (Map<String, Object>) payloadRequest.get("tenantRequestHeader"); if
             * (tenantRequest != null) {
             */
            Map<String, Object> tenantRequestHeader = (Map<String, Object>) payloadRequest.get("tenantRequestHeader");
            // UMG-4697
            if (tenantRequestHeader != null) {
            	Object payloadStorage = tenantRequestHeader.get(MessageVariables.PAYLOAD_STORAGE);
             	if(payloadStorage!=null){
             		tenantResponseHeader.put(MessageVariables.PAYLOAD_STORAGE,(Boolean)payloadStorage);                 		
             	}
            	Object tranModeFromReq = tenantRequestHeader.get(MessageVariables.TRAN_MODE);
            	String tranMode = tranModeFromReq!=null?(String)tranModeFromReq: message.getHeaders().get(MessageVariables.FILE_NAME_HEADER) != null?MessageVariables.TRAN_BULK:TransactionMode.ONLINE.getMode();
            	if(StringUtils.equals(tranMode,TransactionMode.ONLINE.getMode())  && tenantRequestHeader.get(MessageVariables.BATCH_ID)!=null){
            		tranMode = TransactionMode.BATCH.getMode(); 
            	}
                tenantResponseHeader.put(MessageVariables.TRAN_MODE,tranMode);
                tenantResponseHeader.put(MessageVariables.EXECUTION_GROUP,
                        StringUtils.isBlank((String) tenantRequestHeader.get(MessageVariables.EXECUTION_GROUP))
                                ? MessageVariables.DEFAULT_EXECUTION_GROUP
                                : tenantRequestHeader.get(MessageVariables.EXECUTION_GROUP));
            }
            if (tenantRequestHeader != null && tenantRequestHeader.get(MessageVariables.USER) != null) {
                tenantResponseHeader.put(MessageVariables.USER, tenantRequestHeader.get(MessageVariables.USER));
            }
            if (tenantRequestHeader != null && tenantRequestHeader.get(MessageVariables.STORE_RLOGS) != null) {
                tenantResponseHeader.put(MessageVariables.STORE_RLOGS, tenantRequestHeader.get(MessageVariables.STORE_RLOGS));
            }
            if (tenantRequestHeader != null && tenantRequestHeader.containsKey(MessageVariables.VERSION_CREATION_TEST)) {
            	Boolean isVersionCreationTest = (Boolean) tenantRequestHeader.get(MessageVariables.VERSION_CREATION_TEST);
            	if( isVersionCreationTest){
            		 tenantResponseHeader.put(MessageVariables.STORE_RLOGS, true);
            		 tenantRequestHeader.put(MessageVariables.STORE_RLOGS, true);
            	}
               
            }
            tenantResponseHeader.put(FrameworkConstant.ADD_ON_VALIDATION,ModelResponseUtil.setAddOnValidation(payloadRequest, cacheRegistry));
            // }
        }
    }

    /*
     * private void writeToFile(Map<String, Object> tenantResponse) { com.fasterxml.jackson.databind.ObjectMapper mapper = new
     * com.fasterxml.jackson.databind.ObjectMapper(); try { String fileName = "D:\\UMG\\Sample\\" + ((Map)tenantResponse.get("
     * header")).get("umgTransactionId"); //BufferedWriter bufferedWriter = new BufferedWriter(new
     * FileWriter("D:\\UMG\\Sample\\TenantResponse.txt")); BufferedWriter bufferedWriter = new BufferedWriter(new
     * FileWriter(fileName)); IOUtils.write(mapper.writeValueAsString(tenantResponse).getBytes(), bufferedWriter); } catch
     * (IOException e) { e.printStackTrace(); }
     * 
     * }
     */

    /**
     * DOCUMENT ME!
     *
     * @param flowMetadata
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    private Map<String, Object> getOutputMapping(FlowMetaData flowMetadata) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappingData = null;
        if (flowMetadata.getMappingMetaData().getMappingOutput() != null) {
            try {
                mappingData = mapper.readValue(flowMetadata.getMappingMetaData().getMappingOutput(),
                        new TypeReference<HashMap<String, Object>>() {
                        });
            } catch (IOException e) {
                LOGGER.error("Error while converting input mapping data to json object", e);
                LOGGER.debug("Exception stack trace is : ", e);
            }
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
        if (message.getHeaders().containsKey(ISTEST ) && message.getHeaders().get(ISTEST , Integer.class) == 1 && !isFlowDataInCache(metadata)) {
            //LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            //containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format(" FOR VESRION  TEST :: %s", versionInfo.toString()));
        }
        
        if ((!isFlowDataInCache(metadata) && ! message.getHeaders().containsKey(ISTEST)) ||  (message.getHeaders().containsKey(ISTEST ) && message.getHeaders().get(ISTEST , Integer.class) != 1 && !isFlowDataInCache(metadata) ))  {            LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
            metadata = refreshFlowData(versionInfo);
            containerMap.put(versionInfo, metadata);
            LOGGER.error(String.format("CACHE DATA REFRESHED FOR VESRION :: %s", versionInfo.toString()));
        }
        /*if (!isFlowDataInCache(metadata)) {
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
                && metadata.getMappingMetaData().getMappingOutput() != null;
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
    
    /* doTransform(final Message<?> message) method comment 
     * long start = System.currentTimeMillis(); // payload.put(MessageVariables.TENANT_RESPONSE, new HashMap<>());
     * //FileWriter fileWriter = new FileWriter(System.getProperty("sanpath") + File.separator +
     * "TenantOutputSample.txt"); FileWriter fileWriter = new FileWriter(System.getProperty("sanpath") +
     * File.separator + ((Map)tenantResponse.get("header")).get("umgTransactionId"));
     * com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
     * fileWriter.write(mapper.writer().writeValueAsString(tenantResponse)); fileWriter.flush(); fileWriter.close();
     * LOGGER.error("TenantOutputSample.txt write time: " + (System.currentTimeMillis() - start));
     */
}
