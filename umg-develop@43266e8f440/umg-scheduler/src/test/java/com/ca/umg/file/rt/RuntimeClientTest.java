/**
 * 
 */
package com.ca.umg.file.rt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.HttpEntity;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.encryption.EncryptionUtil;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.TenantData;
import com.ca.pool.model.PoolStatus;
import com.ca.pool.model.TransactionCriteria;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.exception.UmgSchedulerExceptionCodes;
import com.ca.umg.file.container.DataContainers;
import com.ca.umg.file.event.info.FileStatusInfo;
import com.ca.umg.file.event.util.FileStatus;

/**
 * @author kamathan
 *
 */
@Ignore
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class RuntimeClientTest {

    @Inject
    private RuntimeClient runtimeClient;

    @Inject
    private DataContainers dataContainers;

    @Value("${default.model.env}")
    private String defaultModelEnv;

    @Value("${default.model.env.ver}")
    private String defaultModelEnvVersion;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    @Named("umgRestTemplate")
    private RestTemplate restTemplate;

    @Value("${probable.pool.status.api}")
    private String probablePoolStatusApi;

    private static final String TNT_CODE = "TNT_CODE";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Map<String, TenantData> tenantUrlMap = cacheRegistry.getMap(FrameworkConstant.TENANT_URL_MAP);

        TenantData tenantData = new TenantData();
        tenantData.setAuthToken(EncryptionUtil.encryptToken("adjbdsudbadad"));
        tenantData.setRuntimeBaseUrl("http://localhost:1999");
        tenantData.setTenantCode(TNT_CODE);

        tenantUrlMap.put(TNT_CODE, tenantData);

        Map<String, String> systemParamMap = cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER);
        systemParamMap.put("umg-runtime-context", "/runtime");
    }

    /**
     * Test method for
     * {@link com.ca.umg.file.rt.RuntimeClient#getProbablePoolAndCount(com.ca.pool.model.TransactionCriteria)}.
     */
    @Test
    public void testGetProbablePoolAndCount() {
        PoolStatus mockPoolStatus = new PoolStatus();
        mockPoolStatus.setAvailablemodelets(2);
        mockPoolStatus.setPoolname("P1");
        ResponseEntity<PoolStatus> poolStatusEntity = new ResponseEntity<>(mockPoolStatus, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(PoolStatus.class))).thenReturn(poolStatusEntity);

        PoolStatus poolStatus = runtimeClient.getProbablePoolAndCount(buildTransactionCriteria());
        assertNotNull(poolStatus);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(PoolStatus.class))).thenReturn(null);

        poolStatus = runtimeClient.getProbablePoolAndCount(buildTransactionCriteria());

        assertNull(poolStatus);
    }

    @Test
    public void testGetProbablePoolAndCountError() {
        doThrow(new RestClientException("Invalid resource.")).when(restTemplate).postForEntity(anyString(), any(HttpEntity.class),
                eq(PoolStatus.class));
        PoolStatus poolStatus = runtimeClient.getProbablePoolAndCount(buildTransactionCriteria());
        assertNull(poolStatus);
    }

    private TransactionCriteria buildTransactionCriteria() {
        TransactionCriteria transactionCriteria = new TransactionCriteria();
        transactionCriteria.setExecutionLanguage(defaultModelEnv);
        transactionCriteria.setExecutionLanguageVersion(defaultModelEnvVersion);
        transactionCriteria.setTenantCode(TNT_CODE);
        return transactionCriteria;
    }

    /**
     * Test method for
     * {@link com.ca.umg.file.rt.RuntimeClient#executeRuntimeRequest(com.ca.umg.file.event.info.FileStatusInfo, com.ca.pool.model.TransactionCriteria, java.util.Map)}
     * .
     */
    @Test
    public void testExecuteRuntimeRequest() {
        FileStatusInfo fileStatusInfo = new FileStatusInfo();
        fileStatusInfo.setAckTime(DateTime.now().getMillis());
        fileStatusInfo.setFilePath("/sanpath");
        fileStatusInfo.setName("dummy_file_name");
        fileStatusInfo.setPostedTime(DateTime.now().getMillis());
        fileStatusInfo.setStatus(FileStatus.ACK.getStatus());
        fileStatusInfo.setTenantCode(TNT_CODE);
        Map<String, Object> dummyResponseMap = new HashMap<String, Object>();
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<Map>(dummyResponseMap, HttpStatus.OK));

        Map<String, Object> requestMap = new HashMap<String, Object>();
        runtimeClient.executeRuntimeRequest(fileStatusInfo, buildTransactionCriteria(), requestMap);
    }

    @Test
    public void testExecuteRuntimeRequestError() {
        FileStatusInfo fileStatusInfo = new FileStatusInfo();
        fileStatusInfo.setAckTime(DateTime.now().getMillis());
        fileStatusInfo.setFilePath("/sanpath");
        fileStatusInfo.setName("dummy_file_name");
        fileStatusInfo.setPostedTime(DateTime.now().getMillis());
        fileStatusInfo.setStatus(FileStatus.ACK.getStatus());
        fileStatusInfo.setTenantCode(TNT_CODE);
        Map<String, Object> dummyResponseMap = new HashMap<String, Object>();
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<Map>(dummyResponseMap, HttpStatus.OK));

        doThrow(new RestClientException("invalid url")).when(restTemplate).postForEntity(anyString(), any(HttpEntity.class),
                eq(Map.class));

        doNothing().when(dataContainers).updateRequestFilesMap(eq(TNT_CODE), anyString(), anyString(), any(FileStatusInfo.class));

        Map<String, Object> requestMap = new HashMap<String, Object>();
        runtimeClient.executeRuntimeRequest(fileStatusInfo, buildTransactionCriteria(), requestMap);

        try {
            doThrow(BusinessException.raiseBusinessException(UmgSchedulerExceptionCodes.USC0000001, new Object[] {}))
                    .when(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Map.class));
            runtimeClient.executeRuntimeRequest(fileStatusInfo, buildTransactionCriteria(), requestMap);

            doThrow(SystemException.newSystemException(UmgSchedulerExceptionCodes.USC0000001, new Object[] {})).when(restTemplate)
                    .postForEntity(anyString(), any(HttpEntity.class), eq(Map.class));

            runtimeClient.executeRuntimeRequest(fileStatusInfo, buildTransactionCriteria(), requestMap);
        } catch (BusinessException | SystemException e) {
            assertEquals(UmgSchedulerExceptionCodes.USC0000001, e.getCode());
        }

    }

}
