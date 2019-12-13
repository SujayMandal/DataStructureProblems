package com.ca.umg.business.tenant.bo;

import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.framework.event.TenantBulkPollingEvent;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mappingnotification.dao.MappingNotificationDao;
import com.ca.umg.business.mappingnotification.dao.NotificationDataDao;
import com.ca.umg.business.tenant.dao.SystemKeyDAO;
import com.ca.umg.business.tenant.dao.TenantConfigDAO;
import com.ca.umg.business.tenant.dao.TenantDAO;
import com.ca.umg.business.tenant.entity.Address;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.hazelcast.core.ITopic;

/**
 * @author basanaga Junit test case for TenantBOImpl class
 * 
 */
public class TenantBOImplTest {

    @InjectMocks
    TenantBO tenantBO = new TenantBOImpl();

    @Mock
    private TenantDAO tenantDAOMock;

    @Mock
    private SystemKeyDAO systemKeyDAOMock;

    @Mock
    private TenantConfigDAO tenantConfigDAOMock;

    @Mock
    private SystemParameterProvider systemParameterProvider;

    @Mock
    private RequestContext requestContextMock;
    
    @Mock
    private UmgFileProxy umgFileProxy;
    
    @Mock 
    private CacheRegistry cacheRegistry;
    
    @Mock
    private NotificationDataDao notificationDao;
    
    @Mock
    private MappingNotificationDao mappingNotificationDao;
    
    @Mock
    private ITopic<Object> iTopic;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testListAll() throws BusinessException, SystemException {
        List<Tenant> tenantList = new ArrayList<Tenant>();
        Tenant tenant = new Tenant();
        tenant.setId("tenantId");
        tenantList.add(tenant);
        Mockito.when(tenantDAOMock.findAll()).thenReturn(tenantList);
        List<Tenant> resultList = tenantBO.listAll();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
    }

    @Test
    public void testSave() throws SystemException, BusinessException {
        Tenant tenant = getTenant();
        SystemKey systemKey = new SystemKey();
        systemKey.setKey("tenantConfigKey");
        systemKey.setType("tenantConfigType");
        Mockito.when(systemKeyDAOMock.findByKeyAndType("tenantConfigKey", "tenantConfigType")).thenReturn(systemKey);
        
        Mockito.when(tenantDAOMock.save(tenant)).thenReturn(tenant);
        
        Mockito.when(notificationDao.getSuperAdminToAddresses()).thenReturn("superadmin@admin.com");
        Tenant resultTenant = tenantBO.save(tenant);
        assertNotNull(resultTenant);
        assertEquals("tenantId", resultTenant.getId());
        assertNotNull(resultTenant.getAddresses());
        assertNotNull(resultTenant.getTenantConfigs());

    }

    @Test
    public void testGetTenantConfigDetails() throws BusinessException, SystemException {
        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setId("tenantConfigId");
        Mockito.when(tenantConfigDAOMock.findByTenantCodeAndSystemKeyKeyAndSystemKeyType("tenantCode", "key", "type"))
                .thenReturn(tenantConfig);
        TenantConfig resultTenantConfig = tenantBO.getTenantConfigDetails("tenantCode", "key", "type");
        assertNotNull(resultTenantConfig);
        assertEquals("tenantConfigId", resultTenantConfig.getId());
    }

    @Test
    public void testGetTenant() throws BusinessException, SystemException {
        Mockito.when(tenantDAOMock.findByCode("localhost")).thenReturn(getTenant());
        Tenant tenant = tenantBO.getTenant("localhost");
        assertNotNull(tenant);
        assertEquals("localhost", tenant.getCode());
    }

    @Test
    public void testUpdate() throws BusinessException, SystemException {
        Tenant existingTenant = getTenant();
        Tenant newTenant = getUpdatedTenant();
        Mockito.when(systemKeyDAOMock.findByKeyAndType("USER", "DATABASE")).thenReturn(getDbUserSystemKey());
        Mockito.when(systemKeyDAOMock.findByKeyAndType("URL", "TENANT")).thenReturn(getUrlTenantSystemKey());
        Mockito.when(tenantDAOMock.save(newTenant)).thenReturn(newTenant);
        Mockito.when(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)).thenReturn("/home/umgadmin/umg-tomcat/SAN_UMG");
        Mockito.when(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))).thenReturn("/home/umgadmin/umg-tomcat/SAN_UMG");
        Mockito.when(cacheRegistry.getTopic(TenantBulkPollingEvent.TENANT_BULK_POLLING_DISABLE_EVENT)).thenReturn(iTopic);
        Tenant tenant = tenantBO.update(newTenant, existingTenant);

        assertNotNull(tenant);
        assertEquals("localhost", tenant.getCode());
    }


    @Test
    public void testGetSystemKeys() throws SystemException {
        List<SystemKey> systemKeyList = new ArrayList<SystemKey>();
        systemKeyList.add(getDbUserSystemKey());
        systemKeyList.add(getBatchSystemKey());
        systemKeyList.add(getUrlTenantSystemKey());
        Mockito.when(systemKeyDAOMock.findAll()).thenReturn(systemKeyList);
        List<SystemKey> systemKeys = tenantBO.getSystemKeys();
        assertNotNull(systemKeys);
        assertEquals(3, systemKeys.size());
    }

    // Test cases for Exception handling : #START

    @Test
    public void testUpdateWithInvalidTenantType() {
        Tenant existingTenant = getTenant();
        existingTenant.setTenantType("both");
        Tenant newTenant = getUpdatedTenant();
        Mockito.when(systemKeyDAOMock.findByKeyAndType("USER", "DATABASE")).thenReturn(getDbUserSystemKey());
        Mockito.when(systemKeyDAOMock.findByKeyAndType("URL", "TENANT")).thenReturn(getUrlTenantSystemKey());
        newTenant.setTenantType("single");
        try {
            tenantBO.update(newTenant, existingTenant);
            fail("does not throwd the exception");
        } catch (SystemException | BusinessException e) {
            assertTrue(e.getCode()!=null);
        }

    }

    @Test
    public void testUpdateWithInvalidSysKeys() throws SystemException {
        Tenant existingTenant = getTenant();
        Tenant newTenant = getUpdatedTenant();
        Mockito.when(systemKeyDAOMock.findByKeyAndType("USER1", "DATABASE")).thenReturn(getDbUserSystemKey());
        Mockito.when(systemKeyDAOMock.findByKeyAndType("URL1", "TENANT")).thenReturn(getUrlTenantSystemKey());
        Mockito.when(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))).thenReturn("/home/umgadmin/umg-tomcat/SAN_UMG");
        try {
            tenantBO.update(newTenant, existingTenant);
            fail("does not throwd the exception");
        } catch (SystemException | BusinessException e) {
            assertTrue(e.getCode()!=null);
        }

    }

    // Test cases for Exception handling : #END

    private Tenant getTenant() {
        Tenant tenant = new Tenant();
        tenant.setId("tenantId");
        tenant.setCode("localhost");

        Set<TenantConfig> tenantConfigSet = new HashSet<TenantConfig>();
        Set<Address> addresses = new HashSet<Address>();

        TenantConfig tenantConfig = new TenantConfig();

        tenant.setTenantConfigs(tenantConfigSet);

        SystemKey systemKey = new SystemKey();
        systemKey.setKey("tenantConfigKey");
        systemKey.setType("tenantConfigType");

        tenantConfig.setSystemKey(systemKey);

        tenantConfigSet.add(tenantConfig);

        Address address = new Address();
        address.setId("addressId");
        addresses.add(address);
        tenant.setAddresses(addresses);
        return tenant;
    }

    private Tenant getUpdatedTenant() {

        Tenant newTenant = new Tenant();
        newTenant.setId("tenantId");
        newTenant.setCode("localhost");
        newTenant.setTenantType("both");

        Set<TenantConfig> tenantConfigSet = new HashSet<TenantConfig>();

        TenantConfig dbUsertenantConfig = new TenantConfig();
        dbUsertenantConfig.setSystemKey(getDbUserSystemKey());
        dbUsertenantConfig.setValue("admin");
        tenantConfigSet.add(dbUsertenantConfig);

        /*
         * TenantConfig batchTenantConfig = new TenantConfig(); batchTenantConfig.setSystemKey(getBatchSystemKey());
         * batchTenantConfig.setValue("true"); tenantConfigSet.add(batchTenantConfig);
         */

        TenantConfig urlTenantTenantConfig = new TenantConfig();
        urlTenantTenantConfig.setSystemKey(getUrlTenantSystemKey());
        urlTenantTenantConfig.setValue("http://localhost:8081");
        tenantConfigSet.add(urlTenantTenantConfig);

        newTenant.setTenantConfigs(tenantConfigSet);

        Address address = new Address();
        address.setId("addressId");

        Set<Address> addresses = new HashSet<Address>();
        addresses.add(address);
        newTenant.setAddresses(addresses);
        return newTenant;
    }

    private SystemKey getDbUserSystemKey() {
        SystemKey systemKey = new SystemKey();
        systemKey.setKey("USER");
        systemKey.setType("DATABASE");
        return systemKey;

    }

    private SystemKey getBatchSystemKey() {
        SystemKey systemKey = new SystemKey();
        systemKey.setKey("BATCH_ENABLED");
        systemKey.setType("TENANT");
        return systemKey;

    }

    private SystemKey getUrlTenantSystemKey() {
        SystemKey systemKey = new SystemKey();
        systemKey.setKey("URL");
        systemKey.setType("TENANT");
        return systemKey;

    }
}
