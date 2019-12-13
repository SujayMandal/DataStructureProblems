package com.ca.umg.business.version.data.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.task.executor.CustomTaskExecutor;
import com.ca.umg.business.version.dao.VersionContainerDAO;

public class VersionDataBuilderTest {

    @InjectMocks
    private VersionDataBuilder versionDataBuilder = new VersionDataBuilder();

    @Mock
    private VersionContainerDAO versionContainerDAO;

    @Mock
    private CustomTaskExecutor taskExecutor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        taskExecutor.initialize();
        // mock get all tenants
        Mockito.when(versionContainerDAO.getAllTenants()).thenReturn(returnTenants());

        // mock get all
        Mockito.when(versionContainerDAO.getAllUniqueVersions()).thenReturn(returnAllVersionMap());

    }

    private Map<String, String> returnAllVersionMap() {
        Map<String, String> versionMap = new HashMap<String, String>();
        versionMap.put("version1", "version desc");
        return versionMap;
    }

    private List<String> returnTenants() {
        List<String> tenants = new ArrayList<String>();
        tenants.add("localhost");
        return tenants;
    }

    @Test
    public void testGetAllTenants() {
        List<String> tenants = versionDataBuilder.getAllTenants();
        assertNotNull(tenants);
        assertEquals(1, tenants.size());
    }

    @Test
    public void testBuildVersionContainer() throws SystemException {
        Mockito.when(taskExecutor.runTask(Mockito.anyList())).thenReturn(getFutureObjs("localhost", "version1", "version1 desc"));

        Map<String, Map<String, String>> versionMap = versionDataBuilder
                .buildVersionContainer(versionDataBuilder.getAllTenants());
        assertNotNull(versionMap);
        assertEquals(1, versionMap.size());
    }

    private List getFutureObjs(final String tenantCode, final String key, final String value) {
        List<Future<Map<String, Map<String, String>>>> futures = new ArrayList<Future<Map<String, Map<String, String>>>>();
        final Map<String, Map<String, String>> tenantMap = new HashMap<String, Map<String, String>>();

        Future<Map<String, Map<String, String>>> future = new Future<Map<String, Map<String, String>>>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public Map<String, Map<String, String>> get() throws InterruptedException, ExecutionException {
                Map<String, String> dataMap = new HashMap<String, String>();
                dataMap.put(key, value);
                tenantMap.put(key, dataMap);
                return tenantMap;
            }

            @Override
            public Map<String, Map<String, String>> get(long timeout, TimeUnit unit) throws InterruptedException,
                    ExecutionException, TimeoutException {
                return null;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }
        };
        futures.add(future);
        return futures;
    }

}
