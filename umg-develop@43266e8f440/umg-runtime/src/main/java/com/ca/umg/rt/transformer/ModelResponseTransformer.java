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

import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getMe2WaitingTime;
import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getModelExecutionTime;
import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getModeletExecution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.object.size.util.ObjectSizeCalculator;
import com.ca.umg.me2.bo.ModelExecutorBOImpl;
import com.ca.umg.me2.util.ModelExecResponse;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.response.ModelResponseFactory;
import com.ca.umg.rt.util.MessageVariables;
import com.codahale.metrics.annotation.Timed;

/**
 * 
 **/
@SuppressWarnings("PMD")
public class ModelResponseTransformer extends AbstractTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelResponseTransformer.class);

    private CacheRegistry cacheRegistry;

    private DeploymentBO deploymentBO;

    private SystemParameterProvider systemParameterProvider;

    private ModelResponseFactory responseFactory;

    private static final String MODEL_EXC_PATTERN = "MOSE0000";
    
    private static final String STORE_RLOG = "storeRLogs";

    /**
     * DOCUMENT ME!
     *
     * @param message
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws Exception
     **/
    @SuppressWarnings("unchecked")
    @Override
    @Timed
    public Object doTransform(final Message<?> message) throws SystemException, BusinessException {
        LOGGER.debug("Model response data transformation starated for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));
        long startTime = System.currentTimeMillis();// NOPMD

        Map<String, Object> payload = (Map<String, Object>) message.getPayload();

        Map<String, Object> tenantReqHeader = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST_HEADER);
        ModelResponseUtil.setAddOnValidation(payload, cacheRegistry);
        boolean storeRLogs = false;
        if(tenantReqHeader != null && tenantReqHeader.containsKey(STORE_RLOG)){
        	Object valueOfrLog = tenantReqHeader.get(STORE_RLOG);
        	if(valueOfrLog instanceof Boolean){
        	storeRLogs = (boolean) tenantReqHeader.get(STORE_RLOG);
        	} else {
        		if(valueOfrLog instanceof String){
        			if(StringUtils.endsWithIgnoreCase(valueOfrLog.toString(), "true")){
        				storeRLogs = true;
        			}
        		}
        	}
        }
        Boolean modelOpValidation = Boolean.FALSE;
        Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        TenantInfo tenantInfo = tenantMap.get(RequestContext.getRequestContext().getTenantCode());
        Map<String, String> tenantConfigsMap = tenantInfo.getTenantConfigsMap();

        if ((tenantReqHeader != null && tenantReqHeader.get(FrameworkConstant.ADD_ON_VALIDATION) != null)
                || tenantConfigsMap.get(FrameworkConstant.MODELOUTPUT_VALIDATION) != null) {
            List<String> addOnValidations = (List<String>) tenantReqHeader.get(FrameworkConstant.ADD_ON_VALIDATION);
            if (Boolean.valueOf(tenantConfigsMap.get(FrameworkConstant.MODELOUTPUT_VALIDATION))
                    || (CollectionUtils.isNotEmpty(addOnValidations)
                            && addOnValidations.contains(FrameworkConstant.MODEL_OUTPUT))) {
                modelOpValidation = Boolean.TRUE;// As we have only one, getting with index 0
            }

        }
        payload.remove(MessageVariables.MODEL_REQUEST_STRING);
        ModelExecResponse<Map<String, Object>> modelExecResponse = (ModelExecResponse) payload.get(MessageVariables.ME2_RESPONSE);
        
        ObjectSizeCalculator.getObjectDeepSize(modelExecResponse, tenantReqHeader.get("transactionId").toString(), "Response received from ME2 in runtime");
        
        Map<String, Object> result = modelExecResponse.getResponse();
        Map<String, Object> modelResponseHeader = (Map<String, Object>) result.get(MessageVariables.RESPONSE_HEADER_INFO);

        List<String> addOnValidation = ModelResponseUtil.setAddOnValidation(payload, cacheRegistry);
        modelResponseHeader.put("addOnValidation", addOnValidation);
        modelResponseHeader.put(STORE_RLOG , storeRLogs);
        
        FlowMetaData flowMetadata = getFlowMetadata(message);
        tenantReqHeader.put(EnvironmentVariables.MODEL_CHECKSUM, flowMetadata.getModelLibrary().getChecksum());
        Map<String, Object> modelInterfaceDefinition = getModelInterfaceDefinition(flowMetadata);
        List<Map<String, Object>> midOutput = (List<Map<String, Object>>) modelInterfaceDefinition
                .get(MessageVariables.MID_OUTPUT);
        
        boolean error = Boolean.valueOf((String) modelResponseHeader.get(MessageVariables.ERROR));
        if (error) {
            String errorMsg = (String) modelResponseHeader.get(MessageVariables.ERROR_MESSAGE);
            String errorCode = (String) modelResponseHeader.get(MessageVariables.ERROR_CODE);
            if (!StringUtils.isBlank(errorCode)) {
                errorMsg = "";
            }

            if (StringUtils.equalsIgnoreCase(ErrorCodes.ME0819, errorCode)) {
                throw new SystemException(RuntimeExceptionCode.RSE000819, new Object[] {});
            } else if (StringUtils.equalsIgnoreCase(ErrorCodes.ME00043, errorCode)) {
                throw new SystemException(RuntimeExceptionCode.RMV000702,
                        new Object[] { modelResponseHeader.get(MessageVariables.ERROR_MESSAGE) });
            } else if (StringUtils.contains(errorCode, MODEL_EXC_PATTERN)) {
                throw new SystemException(ModelExecutorBOImpl.errorCodeMap.get(StringUtils.trim(errorCode)),
                        new Object[] { modelResponseHeader.get(MessageVariables.ERROR_CODE), errorMsg });
            } else {
                throw new SystemException(RuntimeExceptionCode.RSE000804,
                        new Object[] { modelResponseHeader.get(MessageVariables.ERROR_CODE), errorMsg });
            }

        }
     

        if (midOutput == null) {
            throw new SystemException(RuntimeExceptionCode.RSE000806, new Object[] {});
        }

        int i = 0;
        List<Map<String, Object>> modelResponsePayload = (List<Map<String, Object>>) result.get(MessageVariables.PAYLOAD);
        LOGGER.debug("Model response Payload is {}", modelResponsePayload);

        if (CollectionUtils.isEmpty(modelResponsePayload) && (midOutput.size() > i)) {
            throw new SystemException(RuntimeExceptionCode.RSE000804, new Object[] { midOutput.size() });
        }
        List<Map<String, Object>> errorPath = new ArrayList<Map<String, Object>>();
        List<String> errorMessages = new ArrayList<String>();
        Object response = responseFactory.getModelResponse(flowMetadata.getModelLibrary().getLanguage(), midOutput,
                modelResponsePayload, true, StringUtils.EMPTY, errorPath, errorMessages, modelOpValidation);
        long startTime1 = System.currentTimeMillis();

        ObjectSizeCalculator.getObjectDeepSize(response, tenantReqHeader.get("transactionId").toString(), "Response transformed in runtime");
        
        payload.put(MessageVariables.MODEL_RESPONSE_TRANSLATED, response);

        LOGGER.error("Resposne 1 : {}", System.currentTimeMillis() - startTime1);
        if (errorPath != null && !errorPath.isEmpty()) {
            modelResponseHeader.put(MessageVariables.ERROR_MESSAGE, errorPath);
            SystemException.newSystemException(RuntimeExceptionCode.RSE000816, new Object[] {});

        }
        if (addOnValidation != null && addOnValidation.contains(FrameworkConstant.MODEL_OUTPUT) && CollectionUtils.isNotEmpty(errorMessages)) {
            throw new SystemException(RuntimeExceptionCode.RMV000702, new Object[] { errorMessages });

        }
        LOGGER.debug("Model response data transformation completed for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));

        Object obj = addExecutionTimeHeaders(message, payload);
        LOGGER.debug("ModelResponseTransformer.doTransform : {}", System.currentTimeMillis() - startTime);
        return obj;
    }

    private Message<?> addExecutionTimeHeaders(final Message<?> message, final Map<String, Object> payload) {
        Message<?> modifiedMessage = MessageBuilder.fromMessage(message)
                .setHeaderIfAbsent(MessageVariables.MODEL_EXECUTION_TIME, getModelExecutionTime(payload))
                .setHeaderIfAbsent(MessageVariables.MODELET_EXECUTION_TIME, getModeletExecution(payload))
                .setHeaderIfAbsent(MessageVariables.ME2_WAITING_TIME, getMe2WaitingTime(payload)).build();

        return modifiedMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param flowMetadata
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public Map<String, Object> getModelInterfaceDefinition(FlowMetaData flowMetadata) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappingData = null;

        try {
            mappingData = mapper.readValue(flowMetadata.getMappingMetaData().getModelIoData(),
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException e) {
            LOGGER.error("Error while converting input mapping data to json object");
            LOGGER.debug("Error stack trace : ", e);
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
    public FlowMetaData getFlowMetadata(final Message<?> message) throws SystemException, BusinessException {
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
                && metadata.getMappingMetaData().getModelIoData() != null;
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

    public void setResponseFactory(ModelResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    public void setSystemParameterProvider(final SystemParameterProvider systemParameterProvider) {
        this.systemParameterProvider = systemParameterProvider;
    }

    public SystemParameterProvider getSystemParameterProvider() {
        return systemParameterProvider;
    }

}
