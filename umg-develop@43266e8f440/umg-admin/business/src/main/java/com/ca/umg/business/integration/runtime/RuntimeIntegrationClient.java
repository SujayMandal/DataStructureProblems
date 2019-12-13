/**
 *
 */
package com.ca.umg.business.integration.runtime;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.ModeletPoolingResponse;
import com.ca.pool.model.PoolAllocationInfo;
import com.ca.pool.model.PoolRequestInfo;
import com.ca.umg.business.integration.info.RuntimeResponse;
import com.ca.umg.business.integration.info.TestStatusInfo;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author kamathan
 *
 */
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public interface RuntimeIntegrationClient {

    /**
     *
     * @param versionInfo
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    RuntimeResponse deploy(VersionInfo versionInfo, String tenantUrl, String authToken) throws BusinessException, SystemException;

    /**
     *
     * @param versionInfo
     * @throws BusinessException
     * @throws SystemException
     */
    RuntimeResponse unDeploy(VersionInfo versionInfo, String tenantUrl, String authToken)
            throws BusinessException, SystemException;

    /**
     * Method to test a UMG version. Here we post a input Json to the runtime instance and collect the response for the same.
     *
     * @param inputJson
     * @param tenantUrl
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    TestStatusInfo versionTest(String inputJson, String tenantUrl, String authToken) throws BusinessException, SystemException;

    /**
     * @param tenantUrl
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    RuntimeResponse deployBatch(String tenantUrl, String deployUrl, String authToken, String tenantCode)
            throws BusinessException, SystemException;

    /**
     * @param tenantUrl
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    RuntimeResponse unDeployBatch(String tenantUrl, String undeployUrl, String authToken, String tenantCode)
            throws BusinessException, SystemException;

    ModeletPoolingResponse refreshModeletAllocation(String tenantUrl, String undeployUrl, String authToken,
            final PoolRequestInfo requestInfo) throws BusinessException, SystemException;

    ModeletPoolingResponse switchModelet(String tenantUrl, String undeployUrl, String authToken,
            ModeletClientInfo modeletClientInfo) throws BusinessException, SystemException;

    ModeletPoolingResponse fetchModeletResult(String tenantUrl, String undeployUrl, String authToken,
            ModeletClientInfo modeletClientInfo) throws BusinessException, SystemException;

    ModeletPoolingResponse allocateModelets(String tenantUrl, String refreshUrl, String authToken,
            List<PoolAllocationInfo> requestInfo) throws BusinessException, SystemException;

    ModeletPoolingResponse restartModelets(String tenantUrl, String authToken,
                                         List<ModeletClientInfo> modeletClientInfoList) throws BusinessException, SystemException;

    public ModeletPoolingResponse downloadModeletLogs(String tenantUrl, String authToken,
                                                                 ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException;

}
