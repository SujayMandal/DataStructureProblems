package com.ca.umg.rt.core.deployment.bo;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.umg.rt.core.deployment.info.DeploymentDescriptor;
import com.ca.umg.rt.core.deployment.info.DeploymentStatusInfo;
import com.ca.umg.rt.core.deployment.info.TestStatusInfo;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.flows.generator.FlowMetaData;

public interface DeploymentBO {

    /**
     * DOCUMENT ME!
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    DeploymentStatusInfo deploy(DeploymentDescriptor deploymentDescriptor) throws SystemException, BusinessException;

    /**
     * DOCUMENT ME!
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    DeploymentStatusInfo undeploy(DeploymentDescriptor deploymentDescriptor , boolean isTest) throws SystemException, BusinessException;

    TestStatusInfo executeTestFlow(ModelRequest modelRequest, Map<String, Object> requestBody)
            throws SystemException, BusinessException;

    /**
     * The method would deploy the batch execution flow for the tenant. Requires no input from tenant as the configurations will
     * be read from tenant-configuration table.
     * 
     * @return {@link DeploymentStatusInfo} with the status of deployment.
     * @throws SystemException
     * @throws BusinessException
     */
    DeploymentStatusInfo deployBatch() throws SystemException, BusinessException;

    /**
     * The method would un-deploy the batch execution flow for the tenant. Requires no input from tenant as the configurations
     * will be read from tenant-configuration table.
     * 
     * @return {@link DeploymentStatusInfo} with the status of un-deployment.
     * @throws SystemException
     * @throws BusinessException
     */
    DeploymentStatusInfo undeployBatch() throws SystemException, BusinessException;

    /**
     * Gathers all flow related data for a given version name, major and minor version.
     * 
     * @param versionName
     * @param majorVersion
     * @param minorVersion
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    List<FlowMetaData> gatherVersionData(String versionName, int majorVersion, int minorVersion)
            throws SystemException, BusinessException;
    
    int getMaxMinorVersion(String modelName, int majorVersion)
            throws SystemException, BusinessException;

    DeploymentStatusInfo deployWrapper(String wrapperType) throws SystemException, BusinessException;

    DeploymentStatusInfo unDeployWrapper(String wrapperType) throws SystemException, BusinessException;

    List<SupportPackage> getSupportPackages(String versionName, int majorVersion, int minorVersion, final String tenantCode)
            throws SystemException, BusinessException;

    public String getModelPackageName(final String versionName, final int majorVersion, final int minorVersion,
            final String tenantCode) throws SystemException, BusinessException;

    public VersionExecInfo getExecutionLanguageDeatils(final String versionName, final int majorVersion,
            final int minorVersion) throws SystemException, BusinessException;

    public Map<String,String> getExecutionEnvtVersion(final String versionName, final int majorVersion, final int minorVersion)
            throws SystemException, BusinessException;

}
