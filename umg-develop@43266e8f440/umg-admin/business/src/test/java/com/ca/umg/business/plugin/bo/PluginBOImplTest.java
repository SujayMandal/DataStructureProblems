/**
 * 
 */
package com.ca.umg.business.plugin.bo;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.tenant.dao.SystemKeyDAO;
import com.ca.umg.business.tenant.dao.TenantConfigDAO;

/**
 * @author raddibas
 *
 */
public class PluginBOImplTest {
	
	@InjectMocks
	PluginBO pluginBO = new PluginBOImpl();
	
	@Mock
    private SystemKeyDAO systemKeyDAOMock;

    @Mock
    private TenantConfigDAO tenantConfigDAOMock;
    
    @Before
    public void setUp() {
        initMocks(this);
    }
    
    @Test
	public void testGetPluginsMappedForTenant() throws SystemException, BusinessException {
		List<String> allPluginList = new ArrayList<>();
		allPluginList.add("EXCEL");
		allPluginList.add("PDF");

		List<String> tenantPluginList = new ArrayList<>();
		tenantPluginList.add("EXCEL");
		
		Properties properties = new Properties();
		properties.setProperty("TENANT_CODE", "TENANT_CODE");
		RequestContext requestContext = new RequestContext(properties);
		
		Mockito.when(systemKeyDAOMock.findByKeyType("PLUGIN")).thenReturn(
				allPluginList);
		Mockito.when(
				tenantConfigDAOMock.findPluginsForTenantCodeAndSystemKeyType(
						"tenantCode", "PLUGIN","true")).thenReturn(tenantPluginList);
		
		Map<String, Boolean> pluginsMapForTenant = pluginBO.getPluginsMappedForTenant("tenantCode", "PLUGIN");
		assertEquals(2,pluginsMapForTenant.size());
		assertEquals(true, pluginsMapForTenant.get("EXCEL"));
		assertEquals(false, pluginsMapForTenant.get("PDF"));
		requestContext.destroy();
	}

}
