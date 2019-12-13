/**
 * 
 */
package com.ca.umg.file.rt;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.TenantData;
import com.ca.pool.TransactionMode;
import com.ca.pool.model.PoolStatus;
import com.ca.pool.model.TransactionCriteria;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.exception.UmgSchedulerExceptionCodes;
import com.ca.umg.file.container.DataContainers;
import com.ca.umg.file.event.info.FileStatusInfo;
import com.ca.umg.file.event.util.FileStatus;
import com.ca.umg.util.UmgSchedulerConstants;

/**
 * This class is responsible for invoking umg-runtime application.
 * 
 * @author kamathan
 *
 */
@Named
public class RuntimeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeClient.class);

    @Inject
    private DataContainers dataContainers;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    @Named("umgRestTemplate")
    private RestTemplate restTemplate;

    @Value("${probable.pool.status.api}")
    private String probablePoolStatusApi;

    @Value("${runtime.api}")
    private String runtimeApi;

    /**
     * Returns the probable pool and modelet count for the given transaction criteria
     * 
     * @param criteria
     * @return
     */
    public PoolStatus getProbablePoolAndCount(TransactionCriteria criteria) {
        ResponseEntity<PoolStatus> poolStatus = null;
        try {
            Map<String, TenantData> tenantUrlMap = cacheRegistry.getMap(FrameworkConstant.TENANT_URL_MAP);
            TenantData tenantData = tenantUrlMap.get(criteria.getTenantCode());
            HttpHeaders headers = buildHttpRequestHeader(criteria);
            HttpEntity<TransactionCriteria> request = new HttpEntity<TransactionCriteria>(criteria, headers);

            if (tenantData == null) {
                LOGGER.error("Unable to find the tenant data from cache registry.");
                SystemException.newSystemException(UmgSchedulerExceptionCodes.USC0000004, new Object[] {});
            } else {
                poolStatus = restTemplate.postForEntity(getCompleteRntmUrlForPool(tenantData.getRuntimeBaseUrl()) + probablePoolStatusApi,
                        request, PoolStatus.class);
            }
        } catch (SystemException ex) {
            LOGGER.error("Unable to find the tenant data from cache registry.", ex);
        } catch (Exception e) {
            LOGGER.error("An error occurred while getting probable pool details for bulk request.",e);
        }
        return poolStatus != null ? poolStatus.getBody() : null;
    }

    /**
     * 
     * @param fileName
     * @param transactionCriteria
     * @param requestMap
     */
    public void executeRuntimeRequest(FileStatusInfo fileStatusInfo, TransactionCriteria transactionCriteria,
            Map<String, Object> requestMap) {

        try {  
        	if(requestMap.get(UmgSchedulerConstants.HEADER)!=null)
        		((Map<String, Object>)requestMap.get(UmgSchedulerConstants.HEADER)).put(UmgSchedulerConstants.TRAN_MODE, TransactionMode.BULK.getMode());
            LOGGER.info("Invoking runtime for bulk file {}.", fileStatusInfo.getName());
            final TenantData tenantData = getRuntimeUrl(transactionCriteria.getTenantCode());

            HttpHeaders headers = buildHttpRequestHeader(transactionCriteria);
            headers.add("authToken", getAuthTokenForTenant(transactionCriteria.getTenantCode(), tenantData.getAuthToken()));
            final HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(requestMap, headers);

            restTemplate.postForEntity(getCompleteRuntimeUrl(tenantData.getRuntimeBaseUrl()), request, Map.class);
        } catch (RestClientException restClientException) {
            // update status of the file to picked, so that it will get processed during during the next run
            dataContainers.updateRequestFilesMap(transactionCriteria.getTenantCode(),
                    transactionCriteria.getModelName() + UmgSchedulerConstants.CHAR_HYPHEN
                            + StringUtils.substringBefore(transactionCriteria.getModelVersion(), UmgSchedulerConstants.CHAR_DOT),
                    FileStatus.ACK.getStatus(), fileStatusInfo);

            LOGGER.error("Error occurred while invoking runtime for request file {}.", fileStatusInfo.getName(), restClientException);
        } catch (BusinessException | SystemException e) {
            // ignore runtime exception
            LOGGER.error("Request could not be processed correctly for file {}.", fileStatusInfo.getName(),e);
        }
    }

    private HttpHeaders buildHttpRequestHeader(TransactionCriteria transactionCriteria)
            throws BusinessException, SystemException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + getRuntimeCredentials());
        return headers;
    }

    private String getCompleteRuntimeUrl(String baseUrl) {
        String context = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get("umg-runtime-context");
        return baseUrl + context + runtimeApi;
    }
    
    private String getCompleteRntmUrlForPool (String baseUrl) {
        String context = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get("umg-runtime-context");
        return baseUrl + context;
    }

    private TenantData getRuntimeUrl(String tenantCode) {
        Map<String, TenantData> allTenantData = cacheRegistry.getMap(FrameworkConstant.TENANT_URL_MAP);
        return allTenantData.get(tenantCode);

    }

    private String getAuthTokenForTenant(String tenantCode, String authToken) throws BusinessException, SystemException {
        return tenantCode + "." + authToken;
    }

    private String getRuntimeCredentials() {
        String runtimeU = "admin";
        String runtimeP = "admin";
        String plainCreds = runtimeU + ":" + runtimeP;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);
    }
}
