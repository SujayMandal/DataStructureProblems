/*
 * FlowBOImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.bo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.flow.dao.FlowDAO;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.core.flow.entity.Version;
import com.ca.umg.rt.core.flow.entity.VersionMapping;
import com.ca.umg.rt.core.flow.entity.VersionModelLibrary;
import com.ca.umg.rt.core.flow.entity.VersionQuery;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.generator.FlowGenerator;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.generator.QueryMetaData;
import com.ca.umg.rt.flows.version.BatchVersionInfo;
import com.ca.umg.rt.repository.IntegrationFlow;
import com.ca.umg.rt.util.JsonDataUtil;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.RuntimeConfigurationUtil;

/**
 * 
 **/
@Named
public class FlowBOImpl implements FlowBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowBOImpl.class);
    @Inject
    private FlowDAO flowDAO;

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    @Override
    public List<Tenant> getAllTenants() {
        return flowDAO.getAllTenants();
    }

    @Override
    public List<Version> getAllVersionsForTenant() {
        return flowDAO.getAllVersions();
    }

    /**
     * Create {@link List} of {@link IntegrationFlow} from database.
     * 
     * @return {@link List<IntegrationFlow>}
     * @throws BusinessException
     * @throws SystemException
     **/
    @Override
    public List<IntegrationFlow> getAllIntegrationFlows() throws SystemException, BusinessException {
        LOGGER.debug("Read all integration flows from database");
        List<IntegrationFlow> integrations = new ArrayList<IntegrationFlow>();
        List<Version> versions = flowDAO.getAllVersions();
        Map<Version, List<VersionQuery>> versionQueryMap = flowDAO.getAllVersionQuery();
        Map<Version, VersionMapping> versionMappingList = flowDAO.getAllVersionMapping();
        Map<Version, VersionModelLibrary> versionModelLibraryList = flowDAO.getAllVersionModelLibrary();

        Resource resource = null;
        IntegrationFlow integrationFlow = null;
        FlowMetaData flowMetaData = null;

        for (Version version : versions) {
            integrationFlow = new IntegrationFlow();
            flowMetaData = new FlowMetaData();
            flowMetaData.setMajorVersion(version.getMajorVersion());
            flowMetaData.setMinorVersion(version.getMinorVersion());
            flowMetaData.setModelName(version.getName());
            flowMetaData.setModelType(version.getModelType());

            VersionMapping versionMapping = versionMappingList.get(version);
            if (versionMapping != null) {
                flowMetaData.getMappingMetaData().setMappingInput(removeMappingsForExposedToTntParams(versionMapping));
                flowMetaData.getMappingMetaData().setMappingOutput(versionMapping.getOutput().getMapping());
                flowMetaData.getMappingMetaData().setTenantInputDefinition(versionMapping.getInput().getTid());
                flowMetaData.getMappingMetaData().setTenantOutputDefinition(versionMapping.getOutput().getTid());
                flowMetaData.getMappingMetaData().setModelIoData(versionMapping.getModelIoData());
                flowMetaData.setAllowNull(versionMapping.getVersion().isAllowNull());

            }

            List<VersionQuery> versionQueryList = versionQueryMap.get(version);

            if (!CollectionUtils.isEmpty(versionQueryList)) {
                for (VersionQuery versionQuery : versionQueryList) {
                    QueryMetaData queryMetaData = new QueryMetaData();
                    queryMetaData.setId("enricher--" + versionQuery.getQuery().getName());
                    queryMetaData.setJdbcQueryId("jdbc--" + versionQuery.getQuery().getName());
                    queryMetaData.setQueryResponseName(versionQuery.getQuery().getName());
                    queryMetaData.setSql(versionQuery.getQuery().getSql());
                    queryMetaData.setMaxRowsPerPoll(versionQuery.getQuery().isMultipleRow());
                    queryMetaData.setRowMapperCondition(versionQuery.getQuery().isArray());
                    flowMetaData.put(versionQuery.getQuery().getSequence(), queryMetaData);
                }
            }

            VersionModelLibrary versionModelLibrary = versionModelLibraryList.get(version);
            if (versionModelLibrary != null) {
                flowMetaData.setModelLibrary(versionModelLibrary.getModelLibrary());
            }

            integrationFlow.setFlowMetadata(flowMetaData);
            integrationFlow.setFlowName(version.getName());
            integrationFlow.setDescription(version.toString());

            String xml = FlowGenerator.generate(flowMetaData);
            LOGGER.debug(xml);
            resource = new ByteArrayResource(xml.getBytes(), version.toString());
            integrationFlow.setResource(resource);

            integrations.add(integrationFlow);
        }

        return integrations;
    }

    /*
     * private Map<String, Object> getTenantOutputDefn(FlowMetaData flowMetadata) { ObjectMapper mapper = new ObjectMapper();
     * Map<String, Object> mappingData = null; if (flowMetadata.getMappingMetaData().getMappingOutput() != null) { try {
     * mappingData = mapper.readValue(flowMetadata.getMappingMetaData().getTenantOutputDefinition(), new
     * TypeReference<HashMap<String, Object>>() { }); } catch (IOException e) { LOGGER.error(
     * "Error while converting input mapping data to json object", e); } }
     * 
     * return mappingData; }
     */
    private byte[] removeMappingsForExposedToTntParams(VersionMapping versionMapping) throws SystemException {
        byte[] output = null;
        Map<String, Object> mappingData = getMapRepForByteArr(versionMapping.getInput().getMapping());
        Map<String, Object> tenantData = getMapRepForByteArr(versionMapping.getInput().getTid());
        List<Map<String, String>> mapList = (List<Map<String, String>>) mappingData.get(MessageVariables.PARTIALS);
        List<Map<String, Object>> tidList = (List<Map<String, Object>>) tenantData.get(MessageVariables.PARTIALS);

        Iterator maptItr = mapList.iterator();
        while (maptItr.hasNext()) {
            // for (Map<String, String> mapItem : mapList) {
            Map<String, Object> mapItem = (Map<String, Object>) maptItr.next();
            String mappingParam = (String) mapItem.get(MessageVariables.MAPPING_IN);
            for (Map<String, Object> tidItem : tidList) {
                if (StringUtils.equals(mappingParam, (String) tidItem.get("flatenedName"))
                        && tidItem.get("exposedToTenant") != null && (Boolean) tidItem.get("exposedToTenant")) {
                    maptItr.remove();
                    break;
                }
            }
        }
        mappingData.put(MessageVariables.PARTIALS, mapList);
        String outputStr = null;
        try {
            outputStr = JsonDataUtil.convertToJsonString(mappingData);
            if (outputStr != null) {
                output = outputStr.getBytes();
            }
        } catch (IOException e) {
            LOGGER.error("FlowBOImpl:removeMappingsForExposedToTntParams :: Error while converting Map to Json data", e);
            throw new SystemException(RuntimeExceptionCode.RSE000822, new Object[] { e.getMessage() }, e);
        }
        return output;
    }

    private Map<String, Object> getMapRepForByteArr(byte[] input) throws SystemException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappingData = null;

        try {
            mappingData = mapper.readValue(input, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            LOGGER.error("FlowBOImpl:getMapRepForByteArr :: Error while converting Byte array to Map");
            throw new SystemException(RuntimeExceptionCode.RSE000821, new Object[] { e.getMessage() }, e);
        }

        return mappingData;
    }

    @Override
    public IntegrationFlow loadBatchIntegrationFlow() throws SystemException, BusinessException {
        Map<String, String> batchDestinations = null;
        String tenantSanBatch = null;
        IntegrationFlow integrationFlow = null;
        BatchVersionInfo version = null;
        Resource resource = null;
        String batchFlowXml = null;
        String sanPath = umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE));
        String tenantSan = RuntimeConfigurationUtil.getSanBasePath(sanPath);
        if (StringUtils.isNotBlank(tenantSan)) {
            integrationFlow = new IntegrationFlow();
            integrationFlow.setFlowName(RequestContext.getRequestContext().getTenantCode());
            version = new BatchVersionInfo(integrationFlow.getFlowName());
            batchDestinations = new HashMap<String, String>();
            tenantSanBatch = new StringBuffer(tenantSan).append(File.separatorChar).append(RuntimeConstants.BATCH_FOLDER)
                    .toString();
            batchDestinations.put(RuntimeConstants.BATCH_INPUT, new StringBuffer(tenantSanBatch).append(File.separatorChar)
                    .append(RuntimeConstants.BATCH_INPUT).toString());
            batchDestinations.put(RuntimeConstants.BATCH_OUTPUT, new StringBuffer(tenantSanBatch).append(File.separatorChar)
                    .append(RuntimeConstants.BATCH_OUTPUT).toString());
            batchDestinations.put(RuntimeConstants.BATCH_INPROGRESS, new StringBuffer(tenantSanBatch).append(File.separatorChar)
                    .append(RuntimeConstants.BATCH_INPROGRESS).toString());
            batchDestinations.put(RuntimeConstants.BATCH_ARCHIVE, new StringBuffer(tenantSanBatch).append(File.separatorChar)
                    .append(RuntimeConstants.BATCH_ARCHIVE).toString());
            batchDestinations.put(RuntimeConstants.TENANT_CODE, integrationFlow.getFlowName());
            batchDestinations.put(RuntimeConstants.BATCH_FOLDER, RuntimeConstants.BATCH_FOLDER);
            batchDestinations.put(RuntimeConstants.BATCH_OUTPUT, RuntimeConstants.BATCH_OUTPUT);
            batchDestinations.put(RuntimeConstants.BATCH_INPROGRESS, RuntimeConstants.BATCH_INPROGRESS);
            batchDestinations.put(RuntimeConstants.BATCH_ARCHIVE, RuntimeConstants.BATCH_ARCHIVE);
            batchFlowXml = FlowGenerator.generateBatchFlow(batchDestinations);
            LOGGER.debug(batchFlowXml);
            resource = new ByteArrayResource(batchFlowXml.getBytes(), version.toString());
            integrationFlow.setResource(resource);
        }
        return integrationFlow;
    }

    @Override
    public List<Tenant> loadBatchEnabledTenants() throws SystemException, BusinessException {
        return flowDAO.getAllBatchEnabledTenants();
    }

    @Override
    public IntegrationFlow loadWrapperDetail(String wrapperType) throws SystemException, BusinessException {
        Map<String, String> wrapperDetails = null;
        IntegrationFlow integrationFlow = null;
        String wrapperFlowXml = null;
        String wrapper = null;
        Resource resource = null;
        StringBuffer wrapperTemplate = null;
        StringBuffer wrapperSystemKey = null;
        String tenantSan = RuntimeConfigurationUtil.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider
                .getParameter(SystemConstants.SAN_BASE)));
        if (StringUtils.isNotBlank(wrapperType)) {
            wrapper = StringUtils.upperCase(wrapperType, Locale.getDefault());
            wrapperSystemKey = new StringBuffer(wrapper).append(RuntimeConstants.BATCH_WRAPPER_KEY);
            RequestContext.getRequestContext().setAdminAware(true);
            wrapperDetails = flowDAO.loadWrapperDetail(wrapperSystemKey.toString());
            RequestContext.getRequestContext().setAdminAware(false);
            if (MapUtils.isNotEmpty(wrapperDetails)) {
                wrapperTemplate = new StringBuffer(RuntimeConstants.BATCH_PARENT_PATH).append(File.separator).append(wrapper)
                        .append(File.separator).append(wrapperType).append(RuntimeConstants.WRAPPER_APPENDER);

                wrapperDetails.put(RuntimeConstants.TENANT_CODE, RequestContext.getRequestContext().getTenantCode());
                wrapperDetails.put(RuntimeConstants.WRAPPER_TYPE, wrapper);// Always UpperCase
                wrapperDetails.put(RuntimeConstants.SAN_PATH, tenantSan);
                wrapperDetails.put(RuntimeConstants.BATCH_FOLDER_CONST, RuntimeConstants.BATCH_FOLDER);
                wrapperDetails.put(RuntimeConstants.FILE_SEPERATOR, File.separator);

                // TODO move these to DB / System Properties START
                wrapperDetails.put("FTP_LOCAL_SCAN_FOLDER", "FTPScanLocation");
                /*
                 * new StringBuffer(tenantSan).append(File.separator).append(RuntimeConstants.BATCH_FOLDER)
                 * .append(File.separator).append("FTPScanLocation").toString());
                 */
                wrapperDetails.put("FTP_POLL_INTRVL", "100000");// 100 Sec
                wrapperDetails.put("FTP_SESSION_POOL_SIZE", "10");
                wrapperDetails.put("FTP_SESSION_CONN_TIMEOUT", "100000");// 100 Sec
                // TODO move these to DB / System Properties END

                integrationFlow = new IntegrationFlow();
                integrationFlow.setFlowName(wrapperType);
                wrapperFlowXml = FlowGenerator.generateFlow(wrapperDetails, wrapperTemplate.toString());
                LOGGER.debug(wrapperFlowXml);
                resource = new ByteArrayResource(wrapperFlowXml.getBytes(), wrapperType);
                integrationFlow.setResource(resource);
            }
        }
        return integrationFlow;
    }

    @Override
    public List<String> getAllEnabledWrappers() throws SystemException, BusinessException {
        return flowDAO.getAllEnabledWrappers();
    }
}
