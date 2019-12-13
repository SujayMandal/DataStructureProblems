/**
 *
 */
package com.ca.umg.business.integration.runtime;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.ModeletPoolingResponse;
import com.ca.pool.model.PoolAllocationInfo;
import com.ca.pool.model.PoolRequestInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.integration.info.RuntimeRequest;
import com.ca.umg.business.integration.info.RuntimeResponse;
import com.ca.umg.business.integration.info.TestStatusInfo;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author kamathan
 *
 */
@Named
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public class DefaultRuntimeIntegrationClient implements RuntimeIntegrationClient {

    private static final String BASIC = "Basic ";

    private static final String AUTHORIZATION = "Authorization";

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRuntimeIntegrationClient.class);
    public static final String SENDING_REQUEST_TO_URL_MESAGE = "Sending request to url :";

    @Inject
    @Named("umgRestTemplate")
    private RestTemplate restTemplate;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    private static final String AUTH_TOKEN = "authToken";

    /*
     * (non-Javadoc)
     *
     * @see com.ca.umg.business.integration.runtime.RuntimeIntegrationClient#deploy(com.ca.umg.business.version.info.VersionInfo)
     */
    @Override
    public RuntimeResponse deploy(VersionInfo versionInfo, String tenantUrl, String authToken)
            throws BusinessException, SystemException {
        StringBuffer finalUrl = null;
        RuntimeResponse runtimeResponse = null;
        String deployUrl = getRuntimeProperty(BusinessConstants.VERSION_DEPLOY_URL);
        try {
            if (StringUtils.isNotBlank(deployUrl)) {
                finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append(deployUrl);

                RuntimeRequest runtimeRequest = buildRequest(versionInfo);
                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTHORIZATION, BASIC + getRuntimeCredentials());
                headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
                HttpEntity<RuntimeRequest> request = new HttpEntity<RuntimeRequest>(runtimeRequest, headers);
                runtimeResponse = restTemplate.postForObject(finalUrl.toString(), request, RuntimeResponse.class);

                if (runtimeResponse == null || runtimeResponse.isError()) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000071,
                            new Object[]{versionInfo.getName(), versionInfo.getMajorVersion(), versionInfo.getMinorVersion(),
                                    runtimeResponse != null ? runtimeResponse.getErrorMessage() : "Unknown runtime error"});
                }

            } else {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000070,
                        new Object[]{RequestContext.getRequestContext().getTenantCode()});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079, new Object[]{"deploying", exp.getMessage()});
        }
        return runtimeResponse;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ca.umg.business.integration.runtime.RuntimeIntegrationClient#unDeploy(com.ca.umg.business.version.info.VersionInfo)
     */
    @Override
    public RuntimeResponse unDeploy(VersionInfo versionInfo, String tenantUrl, String authToken)
            throws BusinessException, SystemException {
        RuntimeResponse runtimeResponse = null;
        StringBuffer finalUrl = null;
        String deployUrl = getRuntimeProperty(BusinessConstants.VERSION_UNDEPLOY_URL);
        try {
            if (StringUtils.isNotBlank(deployUrl)) {
                finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append(deployUrl);
                RuntimeRequest runtimeRequest = buildRequest(versionInfo);
                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTHORIZATION, BASIC + getRuntimeCredentials());
                headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
                HttpEntity<RuntimeRequest> request = new HttpEntity<RuntimeRequest>(runtimeRequest, headers);
                runtimeResponse = restTemplate.postForObject(finalUrl.toString(), request, RuntimeResponse.class);
                if (runtimeResponse == null || runtimeResponse.isError()) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000072,
                            new Object[]{versionInfo.getName(), versionInfo.getMajorVersion(), versionInfo.getMinorVersion()});
                }
            } else {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000070,
                        new Object[]{RequestContext.getRequestContext().getTenantCode()});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079,
                    new Object[]{"undeploying", exp.getMessage()});
        }
        return runtimeResponse;
    }

    private String getRuntimeProperty(String property) {
        return systemParameterProvider.getParameter(property);
    }

    // TODO temparory fix for authentication, discuss with Anish/Kumar for credentials lookup
    private String getRuntimeCredentials() {
        String username = getRuntimeProperty("umg-runtime-username");
        String pwd = getRuntimeProperty("umg-runtime-pwd");
        String plainCreds = username + ":" + pwd;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);
    }

    private String buildBaseUrl(String tenantUrl) throws BusinessException, SystemException {
        StringBuffer urlBuilder = new StringBuffer(tenantUrl);
        String runtimeContextPath = getRuntimeProperty(BusinessConstants.RUNTIME_CONTEXT);
        if (StringUtils.isBlank(runtimeContextPath)) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000070,
                    new Object[]{RequestContext.getRequestContext().getTenantCode()});
        }
        urlBuilder.append(runtimeContextPath);
        return urlBuilder.toString();
    }

    private RuntimeRequest buildRequest(VersionInfo versionInfo) {
        RuntimeRequest runtimeRequest = new RuntimeRequest();
        runtimeRequest.setName(versionInfo.getName());
        runtimeRequest.setMajorVersion(versionInfo.getMajorVersion());
        runtimeRequest.setMinorVersion(versionInfo.getMinorVersion());
        return runtimeRequest;
    }

    @Override
    public TestStatusInfo versionTest(String inputJson, String tenantUrl, String authToken)
            throws BusinessException, SystemException {
        TestStatusInfo response = new TestStatusInfo();
        StringBuffer tenantTestUrl = null;
        HttpEntity<String> request = null;
        String testUrl = getRuntimeProperty(BusinessConstants.VERSION_TEST_URL);
        try {
            if (StringUtils.isNotBlank(testUrl) && StringUtils.isNotBlank(tenantUrl)) {
                tenantTestUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append(testUrl);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add(AUTHORIZATION, BASIC + getRuntimeCredentials());
                headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
                request = new HttpEntity<String>(inputJson, headers);
                response = restTemplate.postForObject(tenantTestUrl.toString(), request, TestStatusInfo.class);
            }
        } catch (RestClientException exp) {
            LOGGER.error("RestClientException  while accessing runtime :", exp);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079, new Object[]{"testing", exp.getMessage()});
        } catch (Exception exp) { // NOPMD
            LOGGER.error("Exception while accessing  runtime :", exp);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000136, new Object[]{"testing", exp.getMessage()});
        }
        return response;
    }

    private String getAuthTokenForTenant(String authToken) throws BusinessException, SystemException {
        return RequestContext.getRequestContext().getTenantCode() + "." + authToken;
    }

    private String getAuthTokenForRequestedTenant(String tenantCode, String authToken) throws BusinessException, SystemException {
        return tenantCode + "." + authToken;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ca.umg.business.integration.runtime.RuntimeIntegrationClient#deployBatch()
     */
    @Override
    public RuntimeResponse deployBatch(String tenantUrl, String deployUrl, String authToken, String tenantCode)
            throws BusinessException, SystemException {
        HttpEntity<RuntimeResponse> runtimeResponseEntity = null;
        RuntimeResponse runtimeResponse = null;
        StringBuffer finalUrl = null;
        try {
            if (StringUtils.isNotBlank(deployUrl)) {
                finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append(deployUrl);

                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTH_TOKEN, getAuthTokenForRequestedTenant(tenantCode, authToken));
                HttpEntity<String> request = new HttpEntity<String>(headers);
                runtimeResponseEntity = restTemplate.exchange(finalUrl.toString(), HttpMethod.GET, request,
                        RuntimeResponse.class);
                runtimeResponse = runtimeResponseEntity.getBody();

                if (runtimeResponse != null && runtimeResponse.isError()) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000105,
                            new Object[]{runtimeResponse.getErrorMessage()});
                } else if (runtimeResponse == null) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000103, new Object[]{});
                }

            } else {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000070,
                        new Object[]{RequestContext.getRequestContext().getTenantCode()});
            }
        } catch (RestClientException exp) {
            LOGGER.error("RestException while accessing  runtime :" + exp.getMessage());
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079, new Object[]{"deploying", exp.getMessage()});
        } catch (Exception exp) { // NOPMD
            LOGGER.error("Exception while accessing  runtime :" + exp.getMessage());
            SystemException.newSystemException(BusinessExceptionCodes.BSE000136, new Object[]{"testing", exp.getMessage()});
        }
        return runtimeResponse;

    }

    @Override
    public RuntimeResponse unDeployBatch(String tenantUrl, String undeployUrl, String authToken, String tenantCode)
            throws BusinessException, SystemException {
        RuntimeResponse runtimeResponse = null;
        StringBuffer finalUrl = null;
        HttpEntity<RuntimeResponse> runtimeResponseEntity = null;
        try {
            if (StringUtils.isNotBlank(undeployUrl)) {
                finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append(undeployUrl);

                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTH_TOKEN, getAuthTokenForRequestedTenant(tenantCode, authToken));
                HttpEntity<String> request = new HttpEntity<String>(headers);
                runtimeResponseEntity = restTemplate.exchange(finalUrl.toString(), HttpMethod.GET, request,
                        RuntimeResponse.class);
                runtimeResponse = runtimeResponseEntity.getBody();
                if (runtimeResponse != null && runtimeResponse.isError()) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000106,
                            new Object[]{runtimeResponse.getErrorMessage()});
                } else if (runtimeResponse == null) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000104, new Object[]{});

                }

            } else {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000070,
                        new Object[]{RequestContext.getRequestContext().getTenantCode()});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079,
                    new Object[]{"undeploying", exp.getMessage()});
        }
        return runtimeResponse;

    }

    @Override
    public ModeletPoolingResponse refreshModeletAllocation(final String tenantUrl, final String refreshUrl,
                                                           final String authToken, final PoolRequestInfo requestInfo) throws BusinessException, SystemException {
        ModeletPoolingResponse runtimeResponse = null;
        StringBuffer url = null;
        try {
            if (StringUtils.isNotBlank(refreshUrl)) {
                url = new StringBuffer(buildBaseUrl(tenantUrl)).append(refreshUrl);
                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
                HttpEntity<PoolRequestInfo> request = new HttpEntity<PoolRequestInfo>(requestInfo, headers);
                LOGGER.error(SENDING_REQUEST_TO_URL_MESAGE + url);
                runtimeResponse = restTemplate.postForObject(url.toString(), request, ModeletPoolingResponse.class);
                if (runtimeResponse != null && runtimeResponse.isError()) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000202,
                            new Object[]{runtimeResponse.getErrorMessage()});
                } else if (runtimeResponse == null) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000202,
                            new Object[]{"runtimeResponse is null"});
                }
            } else {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000070,
                        new Object[]{RequestContext.getRequestContext().getTenantCode()});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079,
                    new Object[]{"refreshModeletAllocation", exp.getMessage()});
        }

        return runtimeResponse;
    }

    @Override
    public ModeletPoolingResponse switchModelet(final String tenantUrl, final String refreshUrl, final String authToken,
                                                final ModeletClientInfo modeletClientInfo) throws BusinessException, SystemException {
        ModeletPoolingResponse runtimeResponse = null;
        StringBuffer finalUrl = null;
        try {
            finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append(refreshUrl + BusinessConstants.SLASH);
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(ConversionUtil.convertToJsonString(modeletClientInfo), headers);
            LOGGER.error(SENDING_REQUEST_TO_URL_MESAGE + finalUrl);
            runtimeResponse = restTemplate.postForObject(finalUrl.toString(), request, ModeletPoolingResponse.class);
            LOGGER.error("Runtime response status is :" + runtimeResponse.getStatus());
            if (runtimeResponse != null && runtimeResponse.isError()) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000201,
                        new Object[]{runtimeResponse.getErrorMessage()});
            } else if (runtimeResponse == null) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000104, new Object[]{});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079,
                    new Object[]{"Swicth Modelet failed", exp.getMessage()});
        }

        return runtimeResponse;
    }

    @Override
    public ModeletPoolingResponse fetchModeletResult(String tenantUrl, String refreshUrl, String authToken, ModeletClientInfo modeletClientInfo)
            throws BusinessException, SystemException {
        ModeletPoolingResponse runtimeResponse = null;
        StringBuffer finalUrl = null;
        try {
            finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append(refreshUrl + BusinessConstants.SLASH);
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(ConversionUtil.convertToJsonString(modeletClientInfo), headers);
            LOGGER.error(SENDING_REQUEST_TO_URL_MESAGE + finalUrl);
            runtimeResponse = restTemplate.postForObject(finalUrl.toString(), request, ModeletPoolingResponse.class);
            LOGGER.error("Runtime response status is :" + runtimeResponse.getStatus());
            if (runtimeResponse != null && runtimeResponse.isError()) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000203,
                        new Object[]{runtimeResponse.getErrorMessage()});
            } else if (runtimeResponse == null) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000203, new Object[]{});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079,
                    new Object[]{"Fetch Modelet command result failed", exp.getMessage()});
        }

        return runtimeResponse;
    }

    @Override
    public ModeletPoolingResponse allocateModelets(final String tenantUrl, final String refreshUrl, final String authToken,
                                                   final List<PoolAllocationInfo> requestInfo) throws BusinessException, SystemException {
        ModeletPoolingResponse runtimeResponse = null;
        StringBuffer finalUrl = null;
        try {
            if (StringUtils.isNotBlank(refreshUrl)) {
                finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append(refreshUrl);
                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
                HttpEntity<String> request = new HttpEntity<String>(ConversionUtil.convertToJsonString(requestInfo), headers);
                LOGGER.error(SENDING_REQUEST_TO_URL_MESAGE + finalUrl);
                runtimeResponse = restTemplate.postForObject(finalUrl.toString(), request, ModeletPoolingResponse.class);
                if (runtimeResponse != null && runtimeResponse.isError()) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000202,
                            new Object[]{runtimeResponse.getErrorMessage()});
                } else if (runtimeResponse == null) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000202,
                            new Object[]{"runtimeResponse is null"});
                }
            } else {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000070,
                        new Object[]{RequestContext.getRequestContext().getTenantCode()});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079,
                    new Object[]{"refreshModeletAllocation", exp.getMessage()});
        }

        return runtimeResponse;
    }

    @Override
    public ModeletPoolingResponse restartModelets(String tenantUrl, String authToken, List<ModeletClientInfo> modeletClientInfoList) throws BusinessException, SystemException {
        ModeletPoolingResponse runtimeResponse = null;
        StringBuffer finalUrl = null;
        try {
            finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append("/modeletPooling/restartModelets" + BusinessConstants.SLASH);
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(ConversionUtil.convertToJsonString(modeletClientInfoList), headers);
            LOGGER.error(SENDING_REQUEST_TO_URL_MESAGE + finalUrl);
            runtimeResponse = restTemplate.postForObject(finalUrl.toString(), request, ModeletPoolingResponse.class);
            LOGGER.error("Runtime response status is :" + runtimeResponse.getStatus());
            if (runtimeResponse != null && runtimeResponse.isError()) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000205,
                        new Object[]{runtimeResponse.getErrorMessage()});
            } else if (runtimeResponse == null) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000205, new Object[]{});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079, new Object[]{"Modelet restarting failed", exp.getMessage()});
        }
        return runtimeResponse;
    }

    @Override
    public ModeletPoolingResponse downloadModeletLogs(String tenantUrl, String authToken, ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException {
        ModeletPoolingResponse runtimeResponse = null;
        StringBuffer finalUrl = null;
        try {
            finalUrl = new StringBuffer(buildBaseUrl(tenantUrl)).append("/modeletPooling/downloadModeletLog" + BusinessConstants.SLASH);
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTH_TOKEN, getAuthTokenForTenant(authToken));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(ConversionUtil.convertToJsonString(modeletClientInfo), headers);
            LOGGER.error(SENDING_REQUEST_TO_URL_MESAGE + finalUrl);
            runtimeResponse = restTemplate.postForObject(finalUrl.toString(), request, ModeletPoolingResponse.class);
            LOGGER.error("Runtime response : " + runtimeResponse.getStatus());
            if (runtimeResponse != null && runtimeResponse.isError()) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000204,
                        new Object[]{runtimeResponse.getErrorMessage()});
            } else if (runtimeResponse == null) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000204, new Object[]{});
            }
        } catch (RestClientException exp) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000079,
                    new Object[]{"Fetch Modelet command result failed", exp.getMessage()});
        }
        return runtimeResponse;
    }
}
