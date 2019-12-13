/**
 * 
 */
package com.ca.umg.business.plugin.delegate;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.model.bo.ModelBO;
import com.ca.umg.business.plugin.bo.PluginBO;

/**
 * @author raddibas
 * 
 */
@Named
public class PluginDelegateImpl implements PluginDelegate {

	@Inject
	private PluginBO pluginBO;
	
	@Inject
	private ModelBO modelBO;
	/*
	 * gets the plugins mapped to tenant
	 * 
	 * @see
	 * com.ca.umg.business.plugin.delegate.PluginDelegate#getPluginsMappedForTenant
	 * (java.lang.String)
	 */
	@Override
	public Map<String, Boolean> getPluginsMappedForTenant()
			throws SystemException, BusinessException {

		Map<String, Boolean> pluginsMapForTenant = pluginBO
				.getPluginsMappedForTenant(RequestContext.getRequestContext()
						.getTenantCode(),
						SystemConstants.SYSTEM_KEY_TYPE_PLUGIN);

		return pluginsMapForTenant;
	}

	@Override
	public byte[] getModelTemplate(final String modelName, final ModelType modelType) throws SystemException,
			BusinessException {
		return modelBO.getModelTemplate(modelName, modelType);
    }
}
