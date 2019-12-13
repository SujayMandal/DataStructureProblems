/**
 * 
 */
package com.ca.umg.business.plugin.delegate;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.plugin.bo.PluginBO;

/**
 * @author raddibas
 * 
 */
public class PluginDelegateImplTest {

	@InjectMocks
	PluginDelegate pluginDelegate = new PluginDelegateImpl();

	@Mock
	PluginBO pluginBO;
	
	@Before
    public void setUp() {
        initMocks(this);
    }

	@Test
	public void testGetPluginsMappedForTenant() throws SystemException,
			BusinessException {
		Map<String, Boolean> pluginsMapForTenant = new HashMap<>();
		pluginsMapForTenant.put("EXCEL", true);
		pluginsMapForTenant.put("PDF", false);

		Properties properties = new Properties();
		properties.setProperty("TENANT_CODE", "TENANT_CODE");
		RequestContext rc = new RequestContext(properties);

		Mockito.when(
				pluginBO.getPluginsMappedForTenant(rc.getRequestContext()
						.getTenantCode(),
						SystemConstants.SYSTEM_KEY_TYPE_PLUGIN)).thenReturn(
				pluginsMapForTenant);
		Map<String, Boolean> resultPluginsMapForTenant = pluginDelegate
				.getPluginsMappedForTenant();
		assertEquals(2, resultPluginsMapForTenant.size());
		rc.destroy();
	}
}
