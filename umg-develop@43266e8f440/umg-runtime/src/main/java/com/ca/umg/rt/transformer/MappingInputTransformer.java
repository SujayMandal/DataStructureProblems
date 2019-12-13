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
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.util.MessageVariables;
import com.codahale.metrics.annotation.Timed;

/**
 * Handles TID to MID data transformation
 **/
public class MappingInputTransformer extends AbstractTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingInputTransformer.class);
    private CacheRegistry cacheRegistry;
    private DeploymentBO deploymentBO;
    private  static final String ISTEST = "isTest";

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
    @SuppressWarnings("unchecked")
    @Override
    @Timed
    protected Object doTransform(final Message<?> message) throws SystemException {
        LOGGER.debug("Input data transformation starated for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));
        long startTime = System.currentTimeMillis();
        try {
            FlowMetaData flowMetadata = getFlowMetadata(message);
            Map<String, Object> mappingData = getInputmapping(flowMetadata);
            List<Map<String, String>> mapList = (List<Map<String, String>>) mappingData.get(MessageVariables.PARTIALS);
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            Map<String, Object> request = (Map<String, Object>) payload.get(MessageVariables.REQUEST);
            JXPathContext context = JXPathContext.newContext(request);
            JXPathContext resultContext = JXPathContext.newContext(result);
            resultContext.setFactory(new ObjectBuilderFactory());

            // added to create a list of skip in tenant api tid params
            List<String> skipInTntApiList = getSkipInTntApiTidList(flowMetadata);

            Map<String, Object> priorityMappings = new HashMap<String, Object>();
            for (Map<String, String> item : mapList) {
                String in = item.get(MessageVariables.MAPPING_IN);
                String out = item.get(MessageVariables.MAPPING_OUT);
                Object value = getContextValue(in, context);

                // verifies if the parent objects are mapped before child mapping, if not stores the child mappings in a separate
                // map and merges it once the all the mapping is done.
                if (StringUtils.contains(out, RuntimeConstants.CHAR_SLASH) && !resultContext.iterate(out).hasNext()) {
                    if (priorityMappings.get(out) == null) {
                        priorityMappings.put(out, value);
                    }
                } else {
                    Object outValue = getContextValue(out, resultContext);
                    if (outValue == null) {
                        resultContext.setValue(out, value);
                    }
                    if (skipInTntApiList.contains(out)) {
                        resultContext.setValue(out, value);
                    }
                }
            }

            // merge child mappings,if any
            for (Entry<String, Object> entry : priorityMappings.entrySet()) {
                if (resultContext.iterate(entry.getKey()).hasNext()) {
                    resultContext.setValue(entry.getKey(), entry.getValue());
                } else {
                    resultContext.createPathAndSetValue(entry.getKey(), entry.getValue());
                }
            }
            payload.put(MessageVariables.RESULT, result);
        } catch (Exception e)// NOPMD
        {
            LOGGER.error("An error occurred while mapping TID - MID parameters.", e);
            throw new SystemException(RuntimeExceptionCode.RSE000802, new Object[] { e.getMessage() }, e);
        }

        LOGGER.debug("Input data transformation completed for message with id {} in {} ms.",
                message.getHeaders().get(MessageVariables.MESSAGE_ID), System.currentTimeMillis() - startTime);
        return message;
    }

    private Object getContextValue(String path, JXPathContext pathContext) {
        Object value = null;
        try {
            if (StringUtils.isNotBlank(path)) {
                value = pathContext.getValue(path);
            }
        } catch (Exception exp) {// NOPMD
            LOGGER.error("Parent mapping may not be available for path {}.", path);
            LOGGER.debug("Exception stack trace is : ", exp);
            // The exception occurs when the parent is null and we are taking out child data.
        }
        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param flowMetadata
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    private Map<String, Object> getInputmapping(FlowMetaData flowMetadata) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappingData = null;

        try {
            mappingData = mapper.readValue(flowMetadata.getMappingMetaData().getMappingInput(),
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException e) {
            LOGGER.error("Error while converting input mapping data to json object");
            LOGGER.debug("Exception stack trace is : ", e);
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
        
        if ((!isFlowDataInCache(metadata) && ! message.getHeaders().containsKey(ISTEST )) ||  (message.getHeaders().containsKey(ISTEST ) && message.getHeaders().get(ISTEST , Integer.class) != 1 && !isFlowDataInCache(metadata) ))  {            LOGGER.error(String.format("CACHE DATA MISSING FOR VESRION :: %s", versionInfo.toString()));
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
                && metadata.getMappingMetaData().getMappingInput() != null;
    }

    private Map<String, Object> getTenantInterfaceDefinition(FlowMetaData flowMetadata) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappingData = null;

        try {
            mappingData = mapper.readValue(flowMetadata.getMappingMetaData().getTenantInputDefinition(),
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException e) {
            LOGGER.error("Error while converting input mapping data to json object");
            LOGGER.debug("Exception stack trace is : ", e);
        }

        return mappingData;
    }

    private List<String> getSkipInTntApiTidList(FlowMetaData flowMetadata) {
        List<String> skipInTntApiList = new ArrayList<>();
        Map<String, Object> tenantData = getTenantInterfaceDefinition(flowMetadata);
        List<Map<String, Object>> tidList = (List<Map<String, Object>>) tenantData.get(MessageVariables.PARTIALS);
        for (Map<String, Object> tidItem : tidList) {
            if ((tidItem.get("children") != null)) {
                skipInTntApiList.addAll(getSkipInTntApiChildTidList(tidItem));
            } else {
                if (tidItem.get(MessageVariables.EXPOSED_TO_TNT) != null
                        && (Boolean) tidItem.get(MessageVariables.EXPOSED_TO_TNT)) {
                    skipInTntApiList.add((String) tidItem.get("flatenedName"));
                }
            }
        }
        return skipInTntApiList;
    }

    public List<String> getSkipInTntApiChildTidList(Map<String, Object> tidParent) {
        List<String> skipInTntApiList = new ArrayList<>();

        if (tidParent.get(MessageVariables.EXPOSED_TO_TNT) != null && (Boolean) tidParent.get(MessageVariables.EXPOSED_TO_TNT)) {
            skipInTntApiList.add((String) tidParent.get("flatenedName"));
        }
        List<Map<String, Object>> tidChildren = (List<Map<String, Object>>) tidParent.get("children");
        if (CollectionUtils.isNotEmpty(tidChildren)) {
            for (Map<String, Object> tidChild : tidChildren) {
                if ((tidChild.get("children") == null)) {
                    if (tidChild.get(MessageVariables.EXPOSED_TO_TNT) != null
                            && (Boolean) tidChild.get(MessageVariables.EXPOSED_TO_TNT)) {
                        skipInTntApiList.add((String) tidChild.get("flatenedName"));
                    }
                } else {
                    skipInTntApiList.addAll(getSkipInTntApiChildTidList(tidChild));
                }
            }
        }
        return skipInTntApiList;
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
}
