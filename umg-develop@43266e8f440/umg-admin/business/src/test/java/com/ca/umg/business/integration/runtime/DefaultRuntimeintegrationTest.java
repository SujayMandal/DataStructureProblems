/**
 * 
 */
package com.ca.umg.business.integration.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.MessageContainer;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.integration.info.RuntimeResponse;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionStatus;

/**
 * @author kamathan
 *
 */
public class DefaultRuntimeintegrationTest {

    @Mock
    @Named("umgRestTemplate")
    private RestTemplate restTemplate;

    @InjectMocks
    private MessageContainer messageContainer = new MessageContainer();

    @Mock
    private ApplicationContext applicationContext = mock(ApplicationContext.class);

    @InjectMocks
    private RuntimeIntegrationClient runtimeIntegrationClient = new DefaultRuntimeIntegrationClient();

    @Mock
    private SystemParameterProvider systemParameterProvider;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Properties properties = new Properties();
        InputStream inputStream =null;
        try {
            URL filePath = this.getClass().getResource("/umg.properties");
            inputStream = new FileInputStream(new File(filePath.getFile()));
            properties.load(inputStream);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        finally {
        	IOUtils.closeQuietly(inputStream);
		}

        messageContainer.setApplicationContext(applicationContext);
        when(applicationContext.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("message");

        // set request context
        properties.put(RequestContext.TENANT_CODE, "Tenant 1");
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            System.setProperty(key, properties.getProperty(key));
        }
        new RequestContext(properties);
        when(systemParameterProvider.getParameter(eq("version-deploy-api"))).thenReturn("/api/deployment/deploy");
        when(systemParameterProvider.getParameter(eq("version-undeploy-api"))).thenReturn("/api/deployment/undeploy");
        when(systemParameterProvider.getParameter(eq("umg-runtime-context"))).thenReturn("/umg-runtime");
    }

    @Test
    public void testDeploy() throws BusinessException, SystemException {
        VersionInfo versionInfo = buildVersionInfo("Version1", 1, 0, "version desc", VersionStatus.TESTED.getVersionStatus());
        String tenantUrl = "http://localhost:8080";

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(RuntimeResponse.class))).thenReturn(
                buildMockRuntimeResponse(false, "success", null, null, 100l));
                       
        RuntimeResponse response = runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        assertNotNull(response);
        assertFalse(response.isError());
    }

    @Test
    public void testDeployError() {
        VersionInfo versionInfo = buildVersionInfo("Version1", 1, 0, "version desc", VersionStatus.TESTED.getVersionStatus());
        String tenantUrl = "http://localhost:8080";

        // runtime returns error
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(RuntimeResponse.class))).thenReturn(
                buildMockRuntimeResponse(true, "FAILURE", "125", "dsds", 154));

        try {
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000071, e.getCode());
        }

        // runtime returns null response
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(RuntimeResponse.class))).thenReturn(null);

        try {
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000071, e.getCode());
        }

        // rest client throws exception
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(RuntimeResponse.class))).thenThrow(
                new RestClientException("rest cleint exception"));

        try {
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000079, e.getCode());
        }
        // runtime context is not defined
        System.clearProperty(BusinessConstants.RUNTIME_CONTEXT);
        when(systemParameterProvider.getParameter(eq("umg-runtime-context"))).thenReturn(null);
        try {
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000070, e.getCode());
        }

        // deploy url is not defined
        System.clearProperty(BusinessConstants.VERSION_DEPLOY_URL);

        try {
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000070, e.getCode());
        }
    }

    @Test
    public void testUnDeployError() {
        VersionInfo versionInfo = buildVersionInfo("Version1", 1, 0, "version desc", VersionStatus.PUBLISHED.getVersionStatus());
        String tenantUrl = "http://localhost:8080";

        // runtime returns error
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(RuntimeResponse.class))).thenReturn(
                buildMockRuntimeResponse(true, "FAILURE", "125", "dsds", 154));

        try {
            runtimeIntegrationClient.unDeploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000072, e.getCode());
        }

        // runtime returns null response
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(RuntimeResponse.class))).thenReturn(null);

        try {
            runtimeIntegrationClient.unDeploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000072, e.getCode());
        }

        // rest client throws exception
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(RuntimeResponse.class))).thenThrow(
                new RestClientException("rest cleint exception"));

        try {
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000079, e.getCode());
        }
        // runtime context is not defined
        System.clearProperty(BusinessConstants.RUNTIME_CONTEXT);
        when(systemParameterProvider.getParameter(eq("umg-runtime-context"))).thenReturn(null);
        try {
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000070, e.getCode());
        }

        // deploy url is not defined
        System.clearProperty(BusinessConstants.VERSION_DEPLOY_URL);

        try {
            runtimeIntegrationClient.deploy(versionInfo, tenantUrl, null);
        } catch (BusinessException | SystemException e) {
            assertEquals(BusinessExceptionCodes.BSE000070, e.getCode());
        }
    }

    @Test
    public void testUndeploy() throws BusinessException, SystemException {
        VersionInfo versionInfo = buildVersionInfo("Version1", 1, 0, "version desc", VersionStatus.TESTED.getVersionStatus());
        String tenantUrl = "http://localhost:8080";

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(RuntimeResponse.class))).thenReturn(
                buildMockRuntimeResponse(false, "success", null, null, 100l));
       

        RuntimeResponse response = runtimeIntegrationClient.unDeploy(versionInfo, tenantUrl, null);
        assertNotNull(response);
        assertFalse(response.isError());
    }

    private RuntimeResponse buildMockRuntimeResponse(boolean error, String status, String errorCode, String errorMessage,
            long timeTaken) {
        RuntimeResponse runtimeResponse = new RuntimeResponse();
        runtimeResponse.setError(error);
        runtimeResponse.setStatus(status);
        runtimeResponse.setErrorCode(errorCode);
        runtimeResponse.setErrorMessage(errorMessage);
        runtimeResponse.setTimeTaken(timeTaken);
        return runtimeResponse;
    }

    private VersionInfo buildVersionInfo(String name, Integer majorVersion, Integer minorVersion, String description,
            String status) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setName(name);
        versionInfo.setMajorVersion(majorVersion);
        versionInfo.setMinorVersion(minorVersion);
        versionInfo.setDescription(description);
        versionInfo.setStatus(status);
        return versionInfo;
    }
}
