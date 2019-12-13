/**
 * 
 */
package com.ca.umg.business.version.data.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.task.executor.CustomTaskExecutor;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.version.dao.VersionContainerDAO;
import com.ca.umg.business.version.data.builder.task.ModelNameBuilderTask;
import com.ca.umg.business.version.data.builder.task.VersionBuilderTask;

/**
 * @author kamathan
 *
 */
@Named
public class VersionDataBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionDataBuilder.class);

    @Inject
    private VersionContainerDAO versionContainerDAO;

    @Inject
    private CustomTaskExecutor taskExecutor;

    /**
     * Returns list of tenants defined in the system.
     * 
     * @return
     */
    public List<String> getAllTenants() {
        return versionContainerDAO.getAllTenants();
    }

    /**
     * This method would build map of version name and corresponding description for all the tenants.
     * 
     * @param tenants
     * @return
     * @throws SystemException
     */
    public Map<String, Map<String, String>> buildVersionContainer(List<String> tenants) throws SystemException {
        Map<String, Map<String, String>> allVersionMap = new HashMap<String, Map<String, String>>();

        List<Callable<Map<String, Map<String, String>>>> tasks = new ArrayList<Callable<Map<String, Map<String, String>>>>();

        for (String tenantCode : tenants) {
            VersionBuilderTask versionBuilderTask = new VersionBuilderTask(tenantCode, versionContainerDAO);
            tasks.add(versionBuilderTask);
            LOGGER.info("Created build version container task for tenant {}.", tenantCode);
        }

        List<Future<Map<String, Map<String, String>>>> futureResult = taskExecutor.runTask(tasks);

        for (Future<Map<String, Map<String, String>>> future : futureResult) {
            try {
                allVersionMap.putAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Exception occured while building all versions.", e);
                SystemException.newSystemException(BusinessExceptionCodes.BSE000601, new Object[] {});
            }
        }
        return allVersionMap;
    }

    public Map<String, Set<String>> buildModelNameContainer(Map<String, String> tenants) throws SystemException {
        Map<String, Set<String>> allModelNameMap = new HashMap<String, Set<String>>();

        List<Callable<Map<String, Set<String>>>> tasks = new ArrayList<Callable<Map<String, Set<String>>>>();

        Set<String> tenantCodes = tenants.keySet();
        
        for (String tenantCode : tenantCodes) {
            ModelNameBuilderTask versionBuilderTask = new ModelNameBuilderTask(tenantCode, tenants.get(tenantCode), versionContainerDAO);
            tasks.add(versionBuilderTask);
            LOGGER.info("Created build model name container task for tenant {}.", tenantCode);
        }

        List<Future<Map<String, Set<String>>>> futureResult = taskExecutor.runTask(tasks);

        for (Future<Map<String, Set<String>>> future : futureResult) {
            try {
                allModelNameMap.putAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000601, new Object[] {});
            }
        }
        return allModelNameMap;
    }  
    
    public Map<String, String> getTenantNames() {
        return versionContainerDAO.getTenantNames();
    }
    
    public Map<String, Map<String, Map<String, Map<String, Set<String>>>>> buildModelNameContainerWithEnv(
            final Map<String, String> tenants) throws SystemException {
        final Map<String, Map<String, Map<String, Map<String, Set<String>>>>> allModelNameMap = new HashMap<String, Map<String, Map<String, Map<String, Set<String>>>>>();

        final Set<String> tenantCodes = tenants.keySet();
        for (String tenantCode : tenantCodes) {
            setRequestContext(tenantCode);
            try {
                Map<String, Map<String, Map<String, Set<String>>>> modelNamesByEnv = versionContainerDAO
                        .getAllModelVersionsWithEnv();
                allModelNameMap.put(tenantCode, modelNamesByEnv);
            } finally {
                destroyRequestContext();
            }            
        }

        return allModelNameMap;
    }  
    
    private void setRequestContext(final String tenantCode) {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        new RequestContext(properties);
    }

    protected void destroyRequestContext() {
        RequestContext.getRequestContext().destroy();
    }
}