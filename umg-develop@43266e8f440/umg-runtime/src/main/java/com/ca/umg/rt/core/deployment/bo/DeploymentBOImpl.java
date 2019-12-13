/*
 * DeploymentBOImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.deployment.bo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.ca.framework.core.bo.AbstractBusinessObject;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.util.MessageContainer;
import com.ca.umg.rt.core.deployment.dao.DeploymentDAO;
import com.ca.umg.rt.core.deployment.info.DeploymentDescriptor;
import com.ca.umg.rt.core.deployment.info.DeploymentStatusInfo;
import com.ca.umg.rt.core.deployment.info.TestStatusInfo;
import com.ca.umg.rt.core.flow.entity.Version;
import com.ca.umg.rt.core.flow.entity.VersionMapping;
import com.ca.umg.rt.core.flow.entity.VersionModelLibrary;
import com.ca.umg.rt.core.flow.entity.VersionQuery;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.container.ContainerManager;
import com.ca.umg.rt.flows.container.DeploymentStatus;
import com.ca.umg.rt.flows.container.TestGateway;
import com.ca.umg.rt.flows.generator.FlowGenerator;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.generator.QueryMetaData;
import com.ca.umg.rt.repository.IntegrationFlow;
import com.ca.umg.rt.repository.IntegrationRepository;
import com.ca.umg.rt.util.JsonDataUtil;
import com.ca.umg.rt.util.MessageVariables;

/**
 * 
 **/
@Component
public class DeploymentBOImpl extends AbstractBusinessObject implements DeploymentBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentBOImpl.class);
    private static final String STATUS_DEPLOYED = "deployed";
    private static final String STATUS_UNDEPLOYED = "undeployed";
    private static final String STATUS_ERROR = "error";
    private static final String VERSION_STATUS_TESTED = "TESTED";
    private static final String VERSION_STATUS_PUBLISHED = "PUBLISHED";
    private static final String VERSION_STATUS_SAVED = "SAVED";
    private static final String VERSION_STATUS_DELETED = "DELETED";
    private static final String VERSION_STATUS_DEACTIVATED = "DEACTIVATED";
    private static final String VERSION_PENDING_APPROVAL = "PENDING APPROVAL";

    @Inject
    private DeploymentDAO deploymentDAO;

    @Inject
    @Qualifier("flowContainerManager")
    private ContainerManager containerManager;

    @Inject
    @Qualifier("batchFlowContainerManager")
    private ContainerManager batchContainerManager;

    @Inject
    @Qualifier("databaseIntegrationRepository")
    private IntegrationRepository integrationRepository;
    @Inject
    private RModelDAO rModelDAO;

    /**  */
    private static final long serialVersionUID = -5912873246761907671L;

    /**
     * DOCUMENT ME!
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    @Override
    public DeploymentStatusInfo deploy(DeploymentDescriptor deploymentDescriptor) throws SystemException, BusinessException {
        return deploy(deploymentDescriptor, false);
    }

    private DeploymentStatusInfo deploy(DeploymentDescriptor deploymentDescriptor, boolean isTest)
            throws SystemException, BusinessException {
        List<IntegrationFlow> integrationFlowList = getIntegrationFlow(deploymentDescriptor);
        DeploymentStatusInfo deploymentStatusInfo = new DeploymentStatusInfo();
        if (CollectionUtils.isEmpty(integrationFlowList)) {
            deploymentStatusInfo.setStatus(STATUS_ERROR);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000101);
            deploymentStatusInfo.setErrorMessage(
                    MessageContainer.getMessage(RuntimeExceptionCode.RSE000101, new Object[] { deploymentDescriptor.getName(),
                            deploymentDescriptor.getMajorVersion(), deploymentDescriptor.getMinorVersion() }));
            return deploymentStatusInfo;
        }
        String versionStatus = integrationFlowList.get(0).getFlowMetadata().getStatus();
        // setting the saveOrTested value to true only if the version status is saved or tested or pending approval
        boolean saveOrTested = false;
        if (StringUtils.equalsIgnoreCase(VERSION_STATUS_TESTED, versionStatus)
                || StringUtils.equalsIgnoreCase(VERSION_STATUS_SAVED, versionStatus)
                || StringUtils.equalsIgnoreCase(VERSION_PENDING_APPROVAL, versionStatus)) {
            saveOrTested = true;
        }

        // Deleted versions cannot be deployed or tested. However SAVED versions cannot be deployed but can be tested.
        if (versionStatus.equals(VERSION_STATUS_DELETED) || (versionStatus.equals(VERSION_STATUS_SAVED) && !isTest)) {
            deploymentStatusInfo.setStatus(STATUS_ERROR);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000102);
            deploymentStatusInfo
                    .setErrorMessage(MessageContainer.getMessage(RuntimeExceptionCode.RSE000102,
                            new Object[] { deploymentDescriptor.getName(), deploymentDescriptor.getMajorVersion(),
                                    deploymentDescriptor.getMinorVersion(),
                                    integrationFlowList.get(0).getFlowMetadata().getStatus() }));
            return deploymentStatusInfo;
        }

        DeploymentStatus status = new DeploymentStatus();
        // If SAVED or TESTED versions are being tested again, we need to deploy the flow. However while testing PUBLISHED,
        // DEACTIVATED versions the deployment need not happen.
        if (saveOrTested || (versionStatus.equals(VERSION_STATUS_DEACTIVATED) && isTest)) {
            status = containerManager.deployflow(integrationFlowList.get(0) , isTest );
            // setting the flag to true for undeployment of flow for versions with status saved or tested or pending approval
            deploymentStatusInfo.setUndeploymentReq(true && isTest);
        } else if (versionStatus.equals(VERSION_STATUS_PUBLISHED)) {
            status = containerManager.deployflow(integrationFlowList.get(0) , isTest);
            // After testing a PUBLISHED version, it should not be un-deployed hence the
            // deploymentStatusInfo.setUndeploymentReq is false by default
        } else {
            // No deployment required. So setting the status as true
            status.setSuccess(true);
        }

        if (!status.isSuccess()) {
            deploymentStatusInfo.setStatus(STATUS_ERROR);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000103);
            deploymentStatusInfo.setErrorMessage(status.getMessage());
            return deploymentStatusInfo;
        }
        deploymentStatusInfo.setStatus(STATUS_DEPLOYED);

        return deploymentStatusInfo;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    @Override
    public DeploymentStatusInfo undeploy(DeploymentDescriptor deploymentDescriptor , boolean isTest) throws SystemException, BusinessException {
        List<IntegrationFlow> integrationFlowList = getIntegrationFlow(deploymentDescriptor);
        DeploymentStatusInfo deploymentStatusInfo = new DeploymentStatusInfo();
        if (CollectionUtils.isEmpty(integrationFlowList)) {
            deploymentStatusInfo.setStatus(STATUS_ERROR);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000101);
            deploymentStatusInfo.setErrorMessage(
                    MessageContainer.getMessage(RuntimeExceptionCode.RSE000101, new Object[] { deploymentDescriptor.getName(),
                            deploymentDescriptor.getMajorVersion(), deploymentDescriptor.getMinorVersion() }));
            return deploymentStatusInfo;
        }

        /*
         * String versionStatus = VERSION_STATUS_PUBLISHED; if
         * (!versionStatus.equals(integrationFlowList.get(0).getFlowMetadata().getStatus())) {
         * deploymentStatusInfo.setStatus(STATUS_ERROR); deploymentStatusInfo.setError(true);
         * deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000301);
         * deploymentStatusInfo.setErrorMessage(String.format(MESSAGE_INVALID_STATUS, deploymentDescriptor.getName(),
         * deploymentDescriptor.getMajorVersion(), deploymentDescriptor.getMinorVersion(), integrationFlowList.get(0)
         * .getFlowMetadata().getStatus())); return deploymentStatusInfo; }
         */

        DeploymentStatus status = containerManager.unDeployflow(integrationFlowList.get(0) , isTest);

        if (!status.isSuccess()) {
            deploymentStatusInfo.setStatus(STATUS_ERROR);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000104);
            deploymentStatusInfo.setErrorMessage(status.getMessage());
            return deploymentStatusInfo;
        }
        deploymentStatusInfo.setStatus(STATUS_UNDEPLOYED);

        return deploymentStatusInfo;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * @throws BusinessException
     * @throws SystemException
     **/
    private List<IntegrationFlow> getIntegrationFlow(DeploymentDescriptor deploymentDescriptor)
            throws SystemException, BusinessException {
        List<IntegrationFlow> integrations = new ArrayList<IntegrationFlow>();
        Resource resource = null;
        IntegrationFlow integrationFlow = null;
        List<FlowMetaData> flowMetaDatas = gatherVersionData(deploymentDescriptor.getName(),
                deploymentDescriptor.getMajorVersion(), deploymentDescriptor.getMinorVersion());

        if (CollectionUtils.isNotEmpty(flowMetaDatas)) {
            for (FlowMetaData flowMetaData : flowMetaDatas) {
                integrationFlow = new IntegrationFlow();
                integrationFlow.setFlowMetadata(flowMetaData);
                integrationFlow.setFlowName(flowMetaData.getModelName());
                integrationFlow.setDescription(flowMetaData.toString());

                String xml = FlowGenerator.generate(flowMetaData);
                LOGGER.debug(xml);
                resource = new ByteArrayResource(xml.getBytes(), flowMetaData.toString());
                integrationFlow.setResource(resource);

                integrations.add(integrationFlow);
            }
        }
        return integrations;
    }

    @SuppressWarnings("unchecked")
    public TestStatusInfo executeTestFlow(ModelRequest modelRequest, Map<String, Object> requestBody)
            throws SystemException, BusinessException {
        DeploymentDescriptor deploymentDescriptor = new DeploymentDescriptor();
        deploymentDescriptor.setName(modelRequest.getHeader().getModelName());
        deploymentDescriptor.setMajorVersion(modelRequest.getHeader().getMajorVersion());
        deploymentDescriptor.setMinorVersion(modelRequest.getHeader().getMinorVersion());
        DeploymentStatusInfo deploymentStatusInfo = deploy(deploymentDescriptor, true);
        TestStatusInfo testStatusInfo = new TestStatusInfo();
        if (!deploymentStatusInfo.isError()) {
            TestGateway testGateway = containerManager.getTestHandler(modelRequest);
            Map<String, Object> response;
            try {
                response = testGateway.executeFlow(requestBody);
                testStatusInfo.setResponse(response);
                Map<String, Object> header = (Map<String, Object>) response.get(MessageVariables.HEADER);
                if (header.get(MessageVariables.SUCCESS).equals(false)) {
                    testStatusInfo.setError(true);
                    testStatusInfo.setErrorCode((String) header.get(MessageVariables.ERROR_CODE));
                    testStatusInfo.setErrorMessage((String) header.get(MessageVariables.ERROR_MESSAGE));
                } else {
                    testStatusInfo.setError(false);
                }
            } catch (Exception e) { // NOPMD
                LOGGER.error("Exception occured while executing flow.", e);
                throw e;
            } finally {
                // Undeploy any other version apart from PUBLISHED after test completion.
                if (deploymentStatusInfo.isUndeploymentReq()) {
                    undeploy(deploymentDescriptor , true);
                }
            }
        } else {
            testStatusInfo.setError(true);
            testStatusInfo.setErrorCode(deploymentStatusInfo.getErrorCode());
            testStatusInfo.setErrorMessage(String.format("Deployment failed, %s, %s", deploymentStatusInfo.getErrorCode(),
                    deploymentStatusInfo.getErrorMessage()));
        }
        return testStatusInfo;
    }

    @Override
    public DeploymentStatusInfo deployBatch() throws SystemException, BusinessException {
        // TODO check for batch enabled of this tenant.
        DeploymentStatusInfo statusInfo = null;
        DeploymentStatus status = null;
        boolean tenantBatchEnabled = true;
        IntegrationFlow batchIntegrationFlow = null;
        if (tenantBatchEnabled) {
            batchIntegrationFlow = integrationRepository.loadBatchIntegrationFlow();
            statusInfo = new DeploymentStatusInfo();
            if (batchIntegrationFlow != null && batchIntegrationFlow.getResource() != null) {
                status = batchContainerManager.deployflow(batchIntegrationFlow , false);
                if (status.isSuccess()) {
                    statusInfo.setError(false);
                    statusInfo.setStatus(STATUS_DEPLOYED);
                } else {
                    statusInfo.setError(true);
                    statusInfo.setStatus(STATUS_ERROR);
                    statusInfo.setErrorMessage(status.getMessage());
                }
            } else {
                statusInfo.setError(true);
                statusInfo.setStatus(STATUS_ERROR);
                statusInfo.setErrorMessage("NO BATCH INTEGRATION FLOW DEFINED");
            }
        } else {
            // TODO Saurabh- Set error codes and message from resources
            statusInfo = new DeploymentStatusInfo();
            statusInfo.setError(true);
            statusInfo.setErrorCode("TENANT NOT BATCH ENABLED");
            statusInfo.setErrorMessage("TENANT NOT BATCH ENABLED");
            statusInfo.setStatus(STATUS_ERROR);
        }
        return statusInfo;
    }

    @Override
    public DeploymentStatusInfo undeployBatch() throws SystemException, BusinessException {
        DeploymentStatusInfo statusInfo = new DeploymentStatusInfo();
        IntegrationFlow batchIntegrationFlow = new IntegrationFlow();
        batchIntegrationFlow.setFlowName(RequestContext.getRequestContext().getTenantCode());
        DeploymentStatus status = batchContainerManager.unDeployflow(batchIntegrationFlow , false);
        if (status.isSuccess()) {
            statusInfo.setError(false);
            statusInfo.setErrorMessage(status.getMessage());
        } else {
            statusInfo.setError(true);
            statusInfo.setErrorCode(RuntimeExceptionCode.RSE000503);
            statusInfo.setErrorMessage(
                    StringUtils.isNotBlank(status.getMessage()) ? status.getMessage() : "BATCH UNDEPLOYMENT FAILED");
        }
        return statusInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.rt.core.deployment.bo.DeploymentBO#gatherVersionData(java.lang.String, int, int)
     */
    @Override
    public List<FlowMetaData> gatherVersionData(String versionName, int majorVersion, int minorVersion)
            throws SystemException, BusinessException {
        List<FlowMetaData> flowMetaDatas = null;
        FlowMetaData flowMetaData = null;
        List<Version> versions = null;
        Map<Version, List<VersionQuery>> versionQueryMap = null;
        Map<Version, VersionMapping> versionMappingList = null;
        Map<Version, VersionModelLibrary> versionModelLibraryList = null;
        if (StringUtils.isNotBlank(versionName) && majorVersion > 0) {
            versions = deploymentDAO.getVersion(versionName, majorVersion, minorVersion);
            versionQueryMap = deploymentDAO.getVersionQuery(versionName, majorVersion, minorVersion);
            versionMappingList = deploymentDAO.getVersionMapping(versionName, majorVersion, minorVersion);
            versionModelLibraryList = deploymentDAO.getVersionModelLibrary(versionName, majorVersion, minorVersion);

            if (CollectionUtils.isNotEmpty(versions)) {
                flowMetaDatas = new ArrayList<>();
                for (Version version : versions) {
                    flowMetaData = new FlowMetaData();
                    flowMetaData.setMajorVersion(version.getMajorVersion());
                    flowMetaData.setMinorVersion(version.getMinorVersion());
                    flowMetaData.setModelName(version.getName());
                    flowMetaData.setStatus(version.getStatus());
                    flowMetaData.setModelType(version.getModelType());
                    VersionMapping versionMapping = versionMappingList.get(version);

                    if (versionMapping != null) {
                        flowMetaData.getMappingMetaData().setMappingInput(removeMappingsForExposedToTntParams(versionMapping));
                        flowMetaData.getMappingMetaData().setMappingOutput(versionMapping.getOutput().getMapping());
                        flowMetaData.getMappingMetaData().setModelIoData(versionMapping.getModelIoData());
                        flowMetaData.getMappingMetaData().setTenantInputDefinition(versionMapping.getInput().getTid());
                        flowMetaData.getMappingMetaData().setTenantOutputDefinition(versionMapping.getOutput().getTid());
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
                    flowMetaDatas.add(flowMetaData);
                }
            }
        }
        return flowMetaDatas;
    }
    
    @Override
    public int getMaxMinorVersion(String modelName, int majorVersion)
            throws SystemException, BusinessException {
    	return deploymentDAO.getMaxMinorVersion(modelName, majorVersion).get(0);  	
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
                        && (Boolean) tidItem.get("exposedToTenant")) {
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
            LOGGER.error("DeploymentBOImpl:removeMappingsForExposedToTntParams :: Error while converting Map to Json data", e);
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
            LOGGER.error("DeploymentBOImpl:getMapRepForByteArr :: Error while converting Byte array to Map", e);
            throw new SystemException(RuntimeExceptionCode.RSE000821, new Object[] { e.getMessage() }, e);
        }

        return mappingData;
    }

    @Override
    public DeploymentStatusInfo deployWrapper(String wrapperType) throws SystemException, BusinessException {
        DeploymentStatus status = null;
        DeploymentStatusInfo statusInfo = new DeploymentStatusInfo();
        if (StringUtils.isNotBlank(wrapperType)) {
            status = batchContainerManager.deployWrapper(wrapperType);
            if (status.isSuccess()) {
                statusInfo.setError(false);
                statusInfo.setStatus(STATUS_DEPLOYED);
                statusInfo.setErrorMessage(status.getMessage());
            } else {
                statusInfo.setError(true);
                statusInfo.setStatus(STATUS_ERROR);
                statusInfo.setErrorMessage(status.getMessage());
            }
        } else {
            LOGGER.error("No wrapper type mentioned for deployment");
            statusInfo.setError(true);
            statusInfo.setStatus(STATUS_ERROR);
            statusInfo.setErrorMessage("No wrapper type mentioned for deployment");
        }
        return statusInfo;
    }

    @Override
    public DeploymentStatusInfo unDeployWrapper(String wrapperType) throws SystemException, BusinessException {
        DeploymentStatus status = null;
        DeploymentStatusInfo statusInfo = new DeploymentStatusInfo();
        if (StringUtils.isNotBlank(wrapperType)) {
            status = batchContainerManager.unDeployWrapper(wrapperType);
            if (status.isSuccess()) {
                statusInfo.setError(false);
                statusInfo.setStatus(STATUS_UNDEPLOYED);
                statusInfo.setErrorMessage(status.getMessage());
            } else {
                statusInfo.setError(true);
                statusInfo.setStatus(STATUS_ERROR);
                statusInfo.setErrorMessage(status.getMessage());
            }
        } else {
            LOGGER.error("No wrapper type mentioned for un-deployment");
            statusInfo.setError(true);
            statusInfo.setStatus(STATUS_ERROR);
            statusInfo.setErrorMessage("No wrapper type mentioned for un-deployment");
        }
        return statusInfo;
    }

    @Override
    public List<SupportPackage> getSupportPackages(String versionName, int majorVersion, int minorVersion,
            final String tenantCode) throws SystemException, BusinessException {
        return rModelDAO.getSupportPackageList(versionName, majorVersion, minorVersion, tenantCode);
    }

    @Override
    public String getModelPackageName(final String versionName, final int majorVersion, final int minorVersion,
            final String tenantCode) throws SystemException, BusinessException {
        final Map<String, String> result = rModelDAO.getModelPackageName(versionName, majorVersion, minorVersion, tenantCode);
        return result != null ? result.get("PACKAGE_NAME") : null;
    }

    @Override
    public VersionExecInfo getExecutionLanguageDeatils(final String versionName, final int majorVersion,
            final int minorVersion) throws SystemException, BusinessException {
        return deploymentDAO.getExecutionLanguage(versionName, majorVersion, minorVersion);
    }

    @Override
    public Map<String,String> getExecutionEnvtVersion(String versionName, int majorVersion, int minorVersion)
            throws SystemException, BusinessException {
        return deploymentDAO.getExecutionEnvironment(versionName, majorVersion, minorVersion);
    }
}
