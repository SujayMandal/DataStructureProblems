package com.ca.umg.business.version.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.AuthTokenInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.tenant.bo.TenantBO;
import com.ca.umg.business.tenant.delegate.AuthTokenDelegate;
import com.ca.umg.business.tenant.entity.AuthToken;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.version.data.builder.VersionDataBuilder;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;

public class VersionDataContainerTest {

    @InjectMocks
    VersionDataContainer versionDataContainer = new VersionDataContainer();

    @Mock
    private VersionDataBuilder versionDataBuilder;
    
    @Mock
    private CacheRegistry cacheRegistry;
    
    @Mock
    private ITopic<Object> topicObj;
    
    @Mock
    private TenantBO tenantBO;
    
    @Mock IMap<Object, Object> iMap;
    
    @Mock
    private AuthTokenDelegate authTokenDelegate;
    

    @Before
    public void setup() throws SystemException, BusinessException {
        MockitoAnnotations.initMocks(this);
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "localhost");
        new RequestContext(properties);
        
        
        // mock to fetch list of tenant codes
        Mockito.when(versionDataBuilder.getAllTenants()).thenReturn(returnDummyTenantCodes());

        // mock build version map
        Mockito.when(versionDataBuilder.buildVersionContainer(Mockito.anyListOf(String.class))).thenReturn(
                buildversionContainer());
        
        Mockito.when(cacheRegistry.getTopic(Mockito.any(String.class))).thenReturn(topicObj);
        
        Mockito.when(tenantBO.listAll()).thenReturn(buildTenantList());
        
        Mockito.when(cacheRegistry.getMap(FrameworkConstant.TENANT_MAP)).thenReturn(iMap);
        Mockito.when((TenantInfo)iMap.get("localhost")).thenReturn(buildTenantInfo());
        
        Mockito.when(authTokenDelegate.getActiveAuthToken("123")).thenReturn(buildAuthTokenInfo());
        versionDataContainer.buildContainer();
    }

    private Map<String, Map<String, String>> buildversionContainer() {
        Map<String, Map<String, String>> tenantSpecVersions = new HashMap<String, Map<String, String>>();
        Map<String, String> versionMap = new HashMap<String, String>();
        versionMap.put("version1", "version 1 desc");
        tenantSpecVersions.put("localhost", versionMap);
        return tenantSpecVersions;
    }

    private List<String> returnDummyTenantCodes() {
        List<String> tenantCodes = new ArrayList<String>();
        tenantCodes.add("localhost");
        return tenantCodes;
    }

    private List<Tenant> buildTenantList(){
    	List<Tenant> list = new ArrayList<Tenant>();
    	Tenant tenant = new Tenant();
    	tenant.setCode("localhost");
    	tenant.setTenantType("localhost");
    	tenant.setId("123");
    	AuthToken authToken = new AuthToken();
    	authToken.setTenant(tenant);
    	authToken.setAuthCode("Ffqt/JiOQNNTHs7camlIjIbYZgdVdPF77dv+EZdVAse4P3m9MIIDh4b7ujeh6+dj");
    	authToken.setStatus("Active");
    	Set<AuthToken> authTokenSet = new HashSet<AuthToken>();
    	authTokenSet.add(authToken);
    	tenant.setAuthTokens(authTokenSet);
    	Set<TenantConfig> tConfigSet = new HashSet<TenantConfig>();
    	TenantConfig tConfig = new TenantConfig();
    	SystemKey key = new SystemKey();
    	key.setKey("aaaaa");
    	tConfig.setSystemKey(key);
    	tConfigSet.add(tConfig);
    	tenant.setTenantConfigs(tConfigSet);
    	list.add(tenant);
    	
    	return list;
    }
    
    private TenantInfo buildTenantInfo(){
    	Map<String,String> tenantConfigsMap = new HashMap<String, String>();
    	tenantConfigsMap.put(BusinessConstants.BULK_ENABLED, BusinessConstants.BULK_ENABLED);
    	TenantInfo info = new TenantInfo();
    	info.setCode("localhost");
    	info.setTenantConfigsMap(tenantConfigsMap);
    	return info;
    }
    
    private AuthTokenInfo buildAuthTokenInfo(){
    AuthTokenInfo authTokenInfo = new AuthTokenInfo();
    authTokenInfo.setAuthCode("Ffqt/JiOQNNTHs7camlIjIbYZgdVdPF77dv+EZdVAse4P3m9MIIDh4b7ujeh6+dj");
    authTokenInfo.setStatus("Active");
    return authTokenInfo;
    
    } 
    @Test
    public void testVersionContainer() throws SystemException {
        Set<String> versions = versionDataContainer.getVersionNameLike("version1");
        assertNotNull(versions);
        assertEquals(1, versions.size());
        assertNotNull(versionDataContainer.getVersionDescription("version1"));
        assertEquals("version 1 desc", versionDataContainer.getVersionDescription("version1"));
    }

}
