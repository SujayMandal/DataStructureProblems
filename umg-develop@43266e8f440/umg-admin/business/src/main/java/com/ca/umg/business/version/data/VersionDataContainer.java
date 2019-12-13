/**
 * 
 */
package com.ca.umg.business.version.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.AuthTokenInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.info.tenant.TenantUtil;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.tenant.bo.TenantBO;
import com.ca.umg.business.tenant.delegate.AuthTokenDelegate;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.version.data.builder.VersionDataBuilder;

/**
 * @author kamathan
 *
 */
@Named
public class VersionDataContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionDataContainer.class);

    /**
     * Holds version name and description for tenants defined in the system.
     * 
     e* The version container would hold the data in the following format.
     * 
     * Map(tenant code, Map(version name, version description))
     */
    private final Map<String, Map<String, String>> versionContainer = new HashMap<String, Map<String, String>>();
    
    private final Map<String, Set<String>> modelNameContainer = new HashMap<String, Set<String>>();
    private final Map<String, Map<String, Map<String, Map<String, Set<String>>>>> modelNamesByTenantAndEnv = new HashMap<String, Map<String, Map<String, Map<String, Set<String>>>>>();

   
    private Map<String, String> tenantNames = new HashMap<String, String>();
    
    @Inject
    private VersionDataBuilder versionDataBuilder;

    @Inject
    private TenantBO tenantBO;

    @Inject
    private CacheRegistry cacheRegistry;
    
    @Inject
    private AuthTokenDelegate authTokenDelegate;

    @PostConstruct
    public void buildContainer() throws SystemException, BusinessException {
        LOGGER.info("Started building tenant specific container maps.");
        List<String> tenants = versionDataBuilder.getAllTenants();
        LOGGER.info("Found {} tenants in the system.", CollectionUtils.isNotEmpty(tenants) ? tenants.size() : 0);
        if (CollectionUtils.isNotEmpty(tenants)) {
            populateTenants();
            populateTenantNames();
            populateVersionContainer(tenants);
            populateModelNameContainer(tenantNames);
        }
        LOGGER.info("Completed building tenant specific container maps.");
    }

    /**
     * update tenantNames map with new tenant
     * 
     * @param tenant
     * @throws SystemException
     */
    public void buildTenantContainer(Tenant tenant) throws SystemException {
        LOGGER.info("Updating TENANT_MAP in cacheRegistry.");
        cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).put(tenant.getCode(), getTenantInfo(tenant));
        LOGGER.info("Updated TENANT_MAP in cacheRegistry.");
        LOGGER.info("Started updating tenantNames map.");
        tenantNames.put(tenant.getCode(), tenant.getName());
        LOGGER.info("Completed updating tenantNames map.");
        LOGGER.info("Started updating VersionContainer map.");
        populateVersionContainer(tenantBO.getListOfTenantCodes());
        LOGGER.info("Completed updating VersionContainer map.");
    }
    
    /**
     * update tenantNames map by removing new tenant for roll-back
     * 
     * @param tenant
     * @throws SystemException
     */
    public void rollbackTenantContainer(Tenant tenant) throws SystemException {
        LOGGER.info("Updating TENANT_MAP in cacheRegistry for rollback.");
        cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).remove(tenant.getCode());
        LOGGER.info("Updated TENANT_MAP in cacheRegistry for rollback.");
        LOGGER.info("Started updating tenantNames map for rollback.");
        tenantNames.remove(tenant.getCode());
        LOGGER.info("Completed updating tenantNames map for rollback.");
        LOGGER.info("Started updating VersionContainer map for rollback.");
        populateVersionContainer(tenantBO.getListOfTenantCodes());
        LOGGER.info("Completed updating VersionContainer map for rollback.");
    }
    
    /**
     * Populates version container map.
     * 
     * @param tenants
     * @throws SystemException
     */
    private void populateVersionContainer(List<String> tenants) throws SystemException {
        LOGGER.info("Started building tenant specific version container.");
        Map<String, Map<String, String>> versionMap = versionDataBuilder.buildVersionContainer(tenants);
        if (MapUtils.isNotEmpty(versionMap)) {
            versionContainer.putAll(versionMap);
        }
        LOGGER.info("Finished building tenant specific version container.");
    }

    /**
     * This method would return the list of version names matched by the given characters
     * 
     * @param versionName
     * @return
     */
    public Set<String> getVersionNameLike(String versionName) {
        Set<String> filteredVersions = null;
        Map<String, String> tenantVersionMap = versionContainer.get(RequestContext.getRequestContext().getTenantCode());
        if (MapUtils.isNotEmpty(tenantVersionMap)) {
            TreeSet<String> allVersion = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            allVersion.addAll(tenantVersionMap.keySet());
            filteredVersions = allVersion.tailSet(versionName);
        }
        return filteredVersions;
    }

    public List<String> getListOfVersionNames() {
        List<String> versionNames = null;
        Map<String, String> tenantVersionMap = versionContainer.get(RequestContext.getRequestContext().getTenantCode());
        if (MapUtils.isNotEmpty(tenantVersionMap)) {
            versionNames = new ArrayList<>(tenantVersionMap.keySet());
        }
        return versionNames;
    }

    /**
     * Returns the description for the given version.
     * 
     * @param versionName
     * @return
     */
    public String getVersionDescription(String versionName) {
        String description = null;
        Map<String, String> tenantVersionMap = versionContainer.get(RequestContext.getRequestContext().getTenantCode());
        if (MapUtils.isNotEmpty(tenantVersionMap)) {
            description = tenantVersionMap.get(versionName);
        }
        return description;
    }

    /**
     * adds an entry for created version to versionContainer
     * 
     * @param versionName
     * @param description
     */
    public void addVersionToContainer(String tenantCode, String versionName, String description) {
        LOGGER.info("Adding version {} to cache.", versionName);
        Map<String, String> tenantVersionMap = versionContainer.get(tenantCode);
        if (tenantVersionMap == null) {
            tenantVersionMap = new HashMap<String, String>();
            versionContainer.put(tenantCode, tenantVersionMap);
        }
        tenantVersionMap.put(versionName, description);
    }

    /**
     * removes the entry for rollbacked version
     * 
     * @param versionName
     */
    public void removeVersionFromContainer(String tenantCode, String versionName) {
        LOGGER.info("Removing version {} from cache.", versionName);
        Map<String, String> tenantVersionMap = versionContainer.get(tenantCode);
        if (tenantVersionMap != null) {
            tenantVersionMap.remove(versionName);
        }
    }
    
    public void addModelNameToContainer(final String modelName, final Integer majorVersion, final Integer minorVersion) {
        Set<String> tenantModelNameSet = modelNameContainer.get(tenantNames.get(RequestContext.getRequestContext().getTenantCode()));
        if (tenantModelNameSet == null) {
            tenantModelNameSet = new HashSet<String>();
            modelNameContainer.put(tenantNames.get(RequestContext.getRequestContext().getTenantCode()), tenantModelNameSet);
        }
        tenantModelNameSet.add(modelName + "_" + majorVersion + "." + minorVersion);
    }
    
    public void removeModelNameFromContainer(final String modelName, final Integer majorVersion, final Integer minorVersion) {
        Set<String> tenantModelNameSet = modelNameContainer.get(tenantNames.get(RequestContext.getRequestContext()
                .getTenantCode()));
        if (tenantModelNameSet != null) {
            tenantModelNameSet.remove(modelName + "_" + majorVersion + "." + minorVersion);
        }
    }
    
    private void populateModelNameContainer(Map<String, String> tenants) throws SystemException {
        LOGGER.info("Started building tenant specific model name container.");
        Map<String, Set<String>> versionMap = versionDataBuilder.buildModelNameContainer(tenants);
        Map<String, Map<String, Map<String, Map<String, Set<String>>>>> versionMap1 = versionDataBuilder
                .buildModelNameContainerWithEnv(tenants);
        if (MapUtils.isNotEmpty(versionMap)) {
            modelNameContainer.putAll(versionMap);
            modelNamesByTenantAndEnv.putAll(versionMap1);
        }
        LOGGER.info("Finished building tenant specific model container.");
    }
    
    public Map<String, Set<String>> getModelsWithVersions() {
        return modelNameContainer;
    }
    
    public Map<String, Map<String, Map<String, Map<String, Set<String>>>>> getModelNamesByTenantAndEnv() throws SystemException {
        final Map<String, Map<String, Map<String, Map<String, Set<String>>>>> versionMap1 = versionDataBuilder
                .buildModelNameContainerWithEnv(tenantNames);
        final Map<String, Map<String, Map<String, Map<String, Set<String>>>>> modelNamesByTenantAndEnv = new HashMap<>();

        if (MapUtils.isNotEmpty(versionMap1)) {
            modelNamesByTenantAndEnv.putAll(versionMap1);
        }

        return modelNamesByTenantAndEnv;
    }
 
    private void populateTenantNames() {
        tenantNames = versionDataBuilder.getTenantNames();
        LOGGER.info("Tenant Names : {}", tenantNames.toString());
    }

    public Set<String> getTenantNames() {
        Set<String> tenantNameSet = new TreeSet<>();
        Set<String> tenantCodes = tenantNames.keySet();
        for (String tenantCode : tenantCodes) {
            tenantNameSet.add(tenantNames.get(tenantCode));
        }

        return tenantNameSet;
    }
    
    public Map<String , String > getTenantNameAndCode(){

        return tenantNames;
    }

    private void populateTenants() throws SystemException, BusinessException {
        LOGGER.info("Creating TENANT_MAP in cacheRegistry.");
        List<Tenant> tenants = tenantBO.listAll();
        Map<String, TenantInfo> tenantInfoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tenants)) {
            for (Tenant tenant : tenants) {
                tenantInfoMap.put(tenant.getCode(), getTenantInfo(tenant));
            }
        }
        cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).putAll(tenantInfoMap);
        LOGGER.info("Created TENANT_MAP in cacheRegistry.");
        if (MapUtils.isNotEmpty(tenantInfoMap)) {

            LOGGER.info("Starting event triggering for bulk poll.");
            Set<String> tenantSet = tenantInfoMap.keySet();
            for (String tenantCode : tenantSet) {
                TenantInfo tenantInfo = (TenantInfo) cacheRegistry.getMap(FrameworkConstant.TENANT_MAP)
                        .get(tenantCode);
                TenantUtil.initiateBulk(tenantCode, cacheRegistry,
                        Boolean.valueOf(tenantInfo.getTenantConfigsMap().get(BusinessConstants.BULK_ENABLED)));

            }
            

      }
      LOGGER.info("Finished event triggering for bulk poll.");
    }
    
    private TenantInfo getTenantInfo(Tenant tenant) throws SystemException {
        TenantInfo tenantInfo = new TenantInfo();
        Map<String, String> tenantConfigsMap = new HashMap<String, String>();
        for (TenantConfig tenantConfig : tenant.getTenantConfigs()) {
            tenantConfigsMap.put(tenantConfig.getSystemKey().getKey(), tenantConfig.getValue());
        }
        tenantInfo.setTenantConfigsMap(tenantConfigsMap);
        tenantInfo.setName(tenant.getName());
        try {
            AuthTokenInfo authTokenInfo = authTokenDelegate.getActiveAuthToken(tenant.getId());
            tenantInfo.setActiveAuthToken(authTokenInfo.getAuthCode());
        } catch (SystemException ex) {
            LOGGER.info("No Active authToken found for tenant {}", tenant.getCode());
        }
        return tenantInfo;
    }
    
    
}
