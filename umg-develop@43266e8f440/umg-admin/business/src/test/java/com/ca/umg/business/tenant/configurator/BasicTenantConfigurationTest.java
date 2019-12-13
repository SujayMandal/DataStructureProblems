package com.ca.umg.business.tenant.configurator;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.ca.framework.core.db.persistance.TenantRoutingDataSource;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.entity.Tenant;

public class BasicTenantConfigurationTest {

    @InjectMocks
    BasicTenantConfiguration basicTenantConfiguration = new BasicTenantConfiguration();

    @Mock
    private TenantRoutingDataSource tenantRoutingDatasourceMock;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testProvisionSchema() throws SystemException {
        Tenant tenant = new Tenant();
        tenant.setCode("code");
        tenant.setName("name");
        tenant.setTenantType("tenantType");
        tenant.setDescription("description");

        tenantRoutingDatasourceMock.provisionNewSchemaToTenant("code");
        Mockito.verify(tenantRoutingDatasourceMock).provisionNewSchemaToTenant("code");
        basicTenantConfiguration.provisionSchema(tenant);
    }

    //
    // @Test
    // public void testProvisionSchemaException() throws SystemException{
    // Tenant tenant=new Tenant();
    // tenant.setCode("code");
    // tenant.setName("name");
    // tenant.setTenantType("tenantType");
    // tenant.setDescription("description");
    //
    // // Mockito.doThrow(new BeansException("test") {
    // // }).when(tenantRoutingDatasourceMock.provisionNewSchemaToTenant("code"))
    // // Mockito.when(tenantRoutingDatasourceMock.provisionNewSchemaToTenant("code"))
    // Mockito.verify(tenantRoutingDatasourceMock).provisionNewSchemaToTenant("code");
    // basicTenantConfiguration.provisionSchema(tenant);
    // }

    @Test
    public void testInitializeTenantDatasource() throws SystemException {
        Tenant tenant = new Tenant();
        tenant.setCode("code");
        tenant.setName("name");
        tenant.setTenantType("tenantType");
        tenant.setDescription("description");

        tenantRoutingDatasourceMock.initializeDatasourceForTenant("code");
        Mockito.verify(tenantRoutingDatasourceMock).initializeDatasourceForTenant("code");
        basicTenantConfiguration.initializeTenantDatasource(tenant);

    }

}
