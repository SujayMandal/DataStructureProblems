/**
 * 
 */
package com.ca.umg.rt.util.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.TenantData;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.task.executor.CustomTaskExecutor;
import com.ca.umg.rt.core.flow.dao.FlowDAO;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.core.flow.entity.TenantConfig;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.container.task.LoadPackageNamesTask;
import com.ca.umg.rt.util.container.task.LoadSupportPackagesTask;
import com.ca.umg.rt.util.container.task.LoadVersionToEnvironmentMappingTask;

/**
 * Holds static data maps
 * 
 * @author kamathan
 *
 */
@Named
public class StaticDataContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticDataContainer.class);

    @Inject
    private FlowDAO flowDAO;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private RModelDAO rModelDAO;

    @Inject
    private CustomTaskExecutor taskExecutor;

    /**
     * Holds <tenant code>, <tenant url> values
     */
    private final Map<String, TenantData> tenantUrlMap = new HashMap<String, TenantData>();

    /**
     * Holds <tenant code>, <tenant> details
     */
    private final Map<String, Tenant> tenantMap = new HashMap<String, Tenant>();

    private final Map<String, Map<String, List<SupportPackage>>> allSupportPackages = new HashMap<String, Map<String, List<SupportPackage>>>();

    private final Map<String, Map<String, String>> allPackageNames = new HashMap<String, Map<String, String>>();

    private final Map<String, Map<String, VersionExecInfo>> allVersionExecEnvMap = new HashMap<String, Map<String, VersionExecInfo>>();

    private final Map<String, Map<String, VersionExecInfo>> allModelEnvMap = new HashMap<String, Map<String, VersionExecInfo>>();

    public void loadTenant() {
        try {
            loadTenantData();
        } catch (SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    @PostConstruct
    public void loadData() throws SystemException {
        boolean isAdminAware = isAdminAware();
        setAdminAware(true);
        List<Tenant> tenants = flowDAO.getAllTenants();
        setAdminAware(isAdminAware);
        loadAllTenants(tenants);
        loadSupportPackages(tenants);
        loadPackageNames(tenants);
        loadVersionToExecEnvMapping(tenants);
    }

    public void loadTenantData() throws SystemException {
        boolean isAdminAware = isAdminAware();
        setAdminAware(true);
        List<Tenant> tenants = flowDAO.getAllTenants();
        setAdminAware(isAdminAware);
        loadAllTenants(tenants);
    }

    public void loadVersionToExecEnvMapping(List<Tenant> tenants) throws SystemException {
        if (CollectionUtils.isNotEmpty(tenants)) {
            List<Callable<Map<String, Map<String, Map<String, VersionExecInfo>>>>> tasks = new ArrayList<Callable<Map<String, Map<String, Map<String, VersionExecInfo>>>>>();
            for (Tenant tenant : tenants) {
                LoadVersionToEnvironmentMappingTask loadVersionEnvmapTask = new LoadVersionToEnvironmentMappingTask(
                        tenant.getCode(), rModelDAO);
                tasks.add(loadVersionEnvmapTask);
            }
            List<Future<Map<String, Map<String, Map<String, VersionExecInfo>>>>> futureResult = taskExecutor
                    .runTask(tasks);
            for (Future<Map<String, Map<String, Map<String, VersionExecInfo>>>> future : futureResult) {
                try {
                    Map<String, Map<String, Map<String, VersionExecInfo>>> futureMap = future
                            .get();                    
                   Set<Entry<String,Map<String,Map<String, VersionExecInfo>>>> entrySet =  futureMap.entrySet();
                   for(Entry<String,Map<String,Map<String, VersionExecInfo>>> set : entrySet){
                	   Map<String,Map<String, VersionExecInfo>> value =   set.getValue();
                	   if(value.containsKey(SystemConstants.VERSION_KEY)){
                		   allVersionExecEnvMap.put(set.getKey(), value.get(SystemConstants.VERSION_KEY));   
                	   }
                	   if(value.containsKey(SystemConstants.MAJOR_VERSION_KEY)){
                		   allModelEnvMap.put(set.getKey(), value.get(SystemConstants.MAJOR_VERSION_KEY));   
                	   }
                   }
                    Map<String, Map<String, VersionExecInfo>> majorVersionEnvMap = cacheRegistry
                            .getMap(FrameworkConstant.MAJOR_VERSION_ENV_MAP);
                    majorVersionEnvMap.putAll(allModelEnvMap);
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("An error occurred while loading version execution environment map.", e);
                    SystemException.newSystemException(RuntimeExceptionCode.RSE000010, new Object[] { e.getMessage() });
                }
            }
        }
    }

    public void loadSupportPackages(List<Tenant> tenants) throws SystemException {
        if (CollectionUtils.isNotEmpty(tenants)) {
            List<Callable<Map<String, Map<String, List<SupportPackage>>>>> tasks = new ArrayList<Callable<Map<String, Map<String, List<SupportPackage>>>>>();
            for (Tenant tenant : tenants) {
                LoadSupportPackagesTask loadSupportPackagesTask = new LoadSupportPackagesTask(tenant.getCode(), rModelDAO);
                tasks.add(loadSupportPackagesTask);
            }
            List<Future<Map<String, Map<String, List<SupportPackage>>>>> futureResult = taskExecutor.runTask(tasks);
            for (Future<Map<String, Map<String, List<SupportPackage>>>> future : futureResult) {
                try {
                    allSupportPackages.putAll(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("An error occurred while loading support packages.", e);
                    SystemException.newSystemException(RuntimeExceptionCode.RSE000010, new Object[] { e.getMessage() });
                }
            }
        }
    }

    public void loadPackageNames(List<Tenant> tenants) throws SystemException {
        if (CollectionUtils.isNotEmpty(tenants)) {
            List<Callable<Map<String, Map<String, String>>>> tasks = new ArrayList<Callable<Map<String, Map<String, String>>>>();
            for (Tenant tenant : tenants) {
                LoadPackageNamesTask loadPackageNamesTask = new LoadPackageNamesTask(tenant.getCode(), rModelDAO);
                tasks.add(loadPackageNamesTask);
            }
            List<Future<Map<String, Map<String, String>>>> futureResult = taskExecutor.runTask(tasks);
            for (Future<Map<String, Map<String, String>>> future : futureResult) {
                try {
                    allPackageNames.putAll(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("An error occurred while loading package names.", e);
                    SystemException.newSystemException(RuntimeExceptionCode.RSE000010, new Object[] { e.getMessage() });
                }
            }
        }
    }

    public void addPackageName(String tenantCode, String versionKey, String packageName) {
        Map<String, String> tenantPackageNamesMap = allPackageNames.get(tenantCode);
        if (tenantPackageNamesMap == null) {
            tenantPackageNamesMap = new HashMap<String, String>();
            allPackageNames.put(tenantCode, tenantPackageNamesMap);
        }
        tenantPackageNamesMap.put(versionKey, packageName);
    }

    public void removePackageName(String tenantCode, String versionKey) {
        Map<String, String> tenantPackageNamesMap = allPackageNames.get(tenantCode);
        if (MapUtils.isNotEmpty(tenantPackageNamesMap)) {
            tenantPackageNamesMap.remove(versionKey);
        }
    }

    public void addSupportPackage(String tenantCode, String versionKey, List<SupportPackage> supportPackages) {
        LOGGER.error("Adding new support package for version key {} to cache.", versionKey);
        Map<String, List<SupportPackage>> tenantSupportPackages = this.allSupportPackages.get(tenantCode);
        if (tenantSupportPackages == null) {
            tenantSupportPackages = new HashMap<String, List<SupportPackage>>();
            allSupportPackages.put(tenantCode, tenantSupportPackages);
        }

        if (CollectionUtils.isNotEmpty(supportPackages)) {
            if (tenantSupportPackages.containsKey(versionKey)) {
                for (SupportPackage supportPackage : supportPackages) {
                    if (!tenantSupportPackages.get(versionKey).contains(supportPackage)) {
                        tenantSupportPackages.get(versionKey).addAll(supportPackages);
                    }
                }
            } else {
                tenantSupportPackages.put(versionKey, supportPackages);
            }
        }
        if(tenantSupportPackages.get(versionKey)!=null){
        	LOGGER.error("support Packages length for version key "+versionKey+" is "+tenantSupportPackages.get(versionKey).size());
        	
        }else{
            LOGGER.error("No support Packages");
        	
        }

        
        LOGGER.error("Added new support package for version key {} to cache.", versionKey);
    }

    public void removeSupportPackage(String tenantCode, String versionKey) {
        LOGGER.info("Removing support package for version key {} from cache.", versionKey);
        Map<String, List<SupportPackage>> tenantSupportPackages = this.allSupportPackages.get(tenantCode);
        if (MapUtils.isNotEmpty(tenantSupportPackages)) {
            tenantSupportPackages.remove(versionKey);
        }
        LOGGER.info("Remove new support package {} to cache from cache.", versionKey);
    }

    public void addVersionEnvironmentMapping(String tenantCode, String versionKey, VersionExecInfo versionEnv) {
        LOGGER.info("Adding verion key - environment to cache for version key {}.", versionKey);
        Map<String, VersionExecInfo> tenantVersionExecMap = allVersionExecEnvMap.get(tenantCode);
        if (tenantVersionExecMap == null) {
            tenantVersionExecMap = new HashMap<String, VersionExecInfo>();
            allVersionExecEnvMap.put(tenantCode, tenantVersionExecMap);
        }
        tenantVersionExecMap.put(versionKey, versionEnv);
        addDistributedMajorVersionMap(tenantCode, versionKey, versionEnv);
        LOGGER.info("Added verion key - environment to cache for version key {}.", versionKey);
    }

    private void addDistributedMajorVersionMap(String tenantCode, String versionKey, VersionExecInfo versionEnv) {
        Map<String, Map<String, String>> majorVersionEnvMap = cacheRegistry.getMap(FrameworkConstant.MAJOR_VERSION_ENV_MAP);
        Map<String, String> majorVersionMap = majorVersionEnvMap.get(tenantCode);
        if (majorVersionMap == null) {
            majorVersionMap = new HashMap<String, String>();
            majorVersionEnvMap.put(tenantCode, majorVersionMap);
        }
        String tempVersionKey = StringUtils.substringBeforeLast(versionKey, "-");
        majorVersionMap.put(tempVersionKey, versionEnv.getExecLanguage());
    }

    public void removeVersionEnvironmentMapping(String tenantCode, String versionKey) {
        LOGGER.info("Removing verion key - environment to cache for version key {}.", versionKey);
        Map<String, VersionExecInfo> tenantVersionExecMap = allVersionExecEnvMap.get(tenantCode);
        if (MapUtils.isNotEmpty(tenantVersionExecMap)) {
            tenantVersionExecMap.remove(versionKey);
        }
        removeDistributedMajorVersionMap(tenantCode, versionKey);
        LOGGER.info("Removed verion key - environment from cache for version key {}.", versionKey);
    }

    private void removeDistributedMajorVersionMap(String tenantCode, String versionKey) {
        Map<String, Map<String, String>> majorVersionEnvMap = cacheRegistry.getMap(FrameworkConstant.MAJOR_VERSION_ENV_MAP);
        Map<String, String> majorVersionMap = majorVersionEnvMap.get(tenantCode);
        if (MapUtils.isNotEmpty(majorVersionMap)) {
            String tempVersionKey = StringUtils.substringBeforeLast(versionKey, "-");
            majorVersionMap.remove(tempVersionKey);
        }
    }

    /**
     * This method would load all the tenant records into the singleton map & create tenantMap in hazelcast if not available
     */
    private void loadAllTenants(List<Tenant> tenants) {
        boolean mapNotCreated = false;
        Map<String, TenantInfo> tenantInfoMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        if (MapUtils.isEmpty(tenantInfoMap)) {
            LOGGER.info("Creating TENANT_MAP in cacheRegistry.");
            mapNotCreated = true;
        }
        if (CollectionUtils.isNotEmpty(tenants)) {
            for (Tenant tenant : tenants) {
                if (mapNotCreated) {
                    tenantInfoMap.put(tenant.getCode(), getTenantInfo(tenant));
                }
                tenantMap.put(tenant.getCode(), tenant);
                List<TenantConfig> tenantConfigs = tenant.getConfigList();
                loadTenantUrlMap(tenant, tenantConfigs);
            }
        }
        if (mapNotCreated) {
            cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).putAll(tenantInfoMap);
            LOGGER.info("Created TENANT_MAP in cacheRegistry.");
            mapNotCreated = false;
        }
    }

    private boolean isAdminAware() {
        boolean isAdminAware = false;
        if (RequestContext.getRequestContext() != null) {
            isAdminAware = RequestContext.getRequestContext().isAdminAware();
        }
        return isAdminAware;
    }

    private void setAdminAware(boolean adminAware) {
        if (RequestContext.getRequestContext() != null) {
            RequestContext.getRequestContext().setAdminAware(adminAware);
        }
    }

    /**
     * This method would update the tenant url singleton map
     * 
     * @param tenant
     * @param tenantConfigs
     */
    private void loadTenantUrlMap(Tenant tenant, List<TenantConfig> tenantConfigs) {
        TenantData tenantData = new TenantData();
        tenantData.setTenantCode(tenant.getCode());
        tenantData.setTenantName(tenant.getName());
        tenantData.setAuthToken(tenant.getAuthCode());
        if (CollectionUtils.isNotEmpty(tenantConfigs)) {
            for (TenantConfig tenantConfig : tenantConfigs) {
                if (StringUtils.equalsIgnoreCase(SystemConstants.SYSTEM_KEY_TYPE_TENANT, tenantConfig.getKeyType())
                        && StringUtils.equalsIgnoreCase(SystemConstants.SYSTEM_KEY_TENANT_URL, tenantConfig.getKey())) {
                    tenantData.setRuntimeBaseUrl(tenantConfig.getValue());
                    tenantUrlMap.put(tenant.getCode(), tenantData);
                }
            }
        }
        cacheRegistry.getMap(FrameworkConstant.TENANT_URL_MAP).putAll(tenantUrlMap);
    }

    /**
     * Returns the copy of the tenant url map
     * 
     * @return
     */
    public Map<String, TenantData> getTenantUrlMap() {
        return tenantUrlMap;
    }

    /**
     * Returns the copy of the tenant map
     * 
     * @return
     */
    public Map<String, Tenant> getTenantMap() {
        return tenantMap;
    }

    public Map<String, Map<String, List<SupportPackage>>> getAllSupportPackages() {
        return allSupportPackages;
    }

    public Map<String, Map<String, String>> getAllPackageNames() {
        return allPackageNames;
    }

    public Map<String, Map<String, VersionExecInfo>> getAllVersionExecEnvMap() {
        return allVersionExecEnvMap;
    }

    private TenantInfo getTenantInfo(Tenant tenant) {
        TenantInfo tenantInfo = new TenantInfo();
        Map<String, String> tenantConfigsMap = new HashMap<String, String>();
        for (TenantConfig tenantConfig : tenant.getConfigList()) {
            tenantConfigsMap.put(tenantConfig.getKey(), tenantConfig.getValue());
        }
        tenantInfo.setTenantConfigsMap(tenantConfigsMap);
        tenantInfo.setName(tenant.getName());
        tenantInfo.setActiveAuthToken(tenant.getAuthCode());
        return tenantInfo;
    }
    
    public String getActiveAuthToken(String tenantCode)
    {
    	String authToken=null;
    	  Map<String, TenantInfo> tenantInfoMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
          if (MapUtils.isNotEmpty(tenantInfoMap)) {
        	  authToken=  tenantInfoMap.get(tenantCode).getActiveAuthToken();
          }
      return authToken;    
    }

}
