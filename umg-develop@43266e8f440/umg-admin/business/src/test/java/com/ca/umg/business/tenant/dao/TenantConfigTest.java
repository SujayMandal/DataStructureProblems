/**
 * 
 */
package com.ca.umg.business.tenant.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.business.tenant.AbstractTenantTest;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;

/**
 * @author kamathan
 *
 */
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class TenantConfigTest extends AbstractTenantTest {

    @Test
    public void testTenantConfigs() {
        String type = "DATABASE";
        SystemKey driver = createSystemKey("driver", type);
        assertNotNull(driver);
        SystemKey url = createSystemKey("url", type);
        assertNotNull(url);
        SystemKey schema = createSystemKey("schema", type);
        assertNotNull(schema);
        SystemKey password = createSystemKey("password", type);
        assertNotNull(password);
        SystemKey username = createSystemKey("username", type);
        assertNotNull(username);

        Tenant tenant = new Tenant();
        tenant.setCode("OCN");
        tenant.setDescription("DESC");
        tenant.setName("Ocwen");
        tenant.setTenantType("Type");
        Set<TenantConfig> tenantConfigs = new HashSet<TenantConfig>();
        tenantConfigs.add(buildTenantConfig(tenant, driver, "org.hsqldb.jdbcDriver"));
        tenantConfigs.add(buildTenantConfig(tenant, url, "jdbc:hsqldb:mem:base"));
        tenantConfigs.add(buildTenantConfig(tenant, schema, "base"));
        tenantConfigs.add(buildTenantConfig(tenant, username, "SA"));
        tenantConfigs.add(buildTenantConfig(tenant, password, ""));
        tenant.setTenantConfigs(tenantConfigs);

        tenant = getTenantDAO().save(tenant);
        assertNotNull(tenant);
        assertNotNull(tenant.getId());
        assertNotNull(tenant.getTenantConfigs());
        assertEquals(5, tenant.getTenantConfigs().size());

        getTenantConfigDAO().deleteAll();
        getAddressDAO().deleteAll();
        getTenantDAO().deleteAll();
        getSystemKeyDAO().deleteAll();
    }
    
    @Test
	public void testFindPluginsForTenantCodeAndSystemKeyType() {

		String type = "PLUGIN";
		SystemKey excel = createSystemKey("EXCEL", type);
		assertNotNull(excel);

		Tenant tenant = new Tenant();
		tenant.setCode("Test_locahost");
		tenant.setDescription("DESC");
		tenant.setName("Ocwen");
		tenant.setTenantType("Type");
		Set<TenantConfig> tenantConfigs = new HashSet<TenantConfig>();
		tenantConfigs.add(buildTenantConfig(tenant, excel, "true"));
		tenant.setTenantConfigs(tenantConfigs);
		tenant = getTenantDAO().save(tenant);

		List<String> tenantPluginList = getTenantConfigDAO()
				.findPluginsForTenantCodeAndSystemKeyType("Test_locahost", type,"true");
		assertEquals(1, tenantPluginList.size());
		assertEquals("EXCEL", tenantPluginList.get(0));
		
		getTenantConfigDAO().deleteAll();
        getAddressDAO().deleteAll();
        getTenantDAO().deleteAll();
        getSystemKeyDAO().deleteAll();

	}

}
