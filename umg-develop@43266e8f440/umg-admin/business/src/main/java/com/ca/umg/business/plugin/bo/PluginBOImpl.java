/**
 * 
 */
package com.ca.umg.business.plugin.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.tenant.dao.SystemKeyDAO;
import com.ca.umg.business.tenant.dao.TenantConfigDAO;

/**
 * @author raddibas
 * 
 */
@Named
public class PluginBOImpl implements PluginBO {

	@Inject
	private SystemKeyDAO systemKeyDAO;

	@Inject
	private TenantConfigDAO tenantConfigDAO;

	/*
	 * gets the plugins mapped to tenant
	 * 
	 * @see
	 * com.ca.umg.business.plugin.bo.PluginBo#getPluginsMappedForTenant(java
	 * .lang.String)
	 */
	@Override
	public Map<String, Boolean> getPluginsMappedForTenant(String tenantCode,
			String type) throws SystemException, BusinessException {

		Map<String, Boolean> pluginsMapForTenant = new HashMap<>();

		RequestContext requestContext = RequestContext.getRequestContext();
		requestContext.setAdminAware(true);
		
		//gets all the plugins
		List<String> allPluginList = systemKeyDAO.findByKeyType(type);
		if (allPluginList == null || allPluginList.isEmpty()) {
			 BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000100, new Object[] { });
		}
		//gets the plugins mapped for tenant
		String configValue = Boolean.toString(BusinessConstants.TRUE);
		List<String> tenantPluginList = tenantConfigDAO
				.findPluginsForTenantCodeAndSystemKeyType(tenantCode, type, configValue);
		if (tenantPluginList == null || tenantPluginList.isEmpty()) {
			 BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000101, new Object[] {tenantCode});
		}
		
		requestContext.setAdminAware(false);

		for (String pluginName : allPluginList) {
			if (tenantPluginList.contains(pluginName)) {
				pluginsMapForTenant.put(pluginName, true);
			} else {
				pluginsMapForTenant.put(pluginName, false);
			}
		}
		return pluginsMapForTenant;
	}

}
