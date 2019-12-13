package com.ca.umg.business.systemparam.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.systemparam.dao.SystemParameterDAO;
import com.ca.umg.business.systemparam.entity.SystemParameter;

@Named
public class SystemParameterBOImpl implements SystemParameterBO {
	@Inject
	private CacheRegistry cacheRegistry;
	@Inject
	private SystemParameterDAO systemParameterDAO;

	public List<SystemParameter> getSysParameters() {
		RequestContext requestContext = RequestContext.getRequestContext();
		requestContext.setAdminAware(true);
		List<SystemParameter> listSysParams = systemParameterDAO.findAll();
		requestContext.setAdminAware(false);
		return listSysParams;
	}

	public void saveParameter(SystemParameter systemParameter) {
		systemParameterDAO.save(systemParameter);
		cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).put(
				systemParameter.getSysKey(), systemParameter.getSysValue());
	}

}
