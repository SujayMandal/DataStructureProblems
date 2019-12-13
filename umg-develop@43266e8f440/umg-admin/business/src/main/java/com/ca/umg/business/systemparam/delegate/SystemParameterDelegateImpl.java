package com.ca.umg.business.systemparam.delegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.systemparam.bo.SystemParameterBO;
import com.ca.umg.business.systemparam.entity.SystemParameter;
import com.ca.umg.business.systemparam.info.SystemParameterInfo;

@Named
public class SystemParameterDelegateImpl extends AbstractDelegate implements
		SystemParameterDelegate {
	@Inject
	private SystemParameterBO systemParameterBO;

	@Transactional(rollbackFor = { Exception.class })
	public void saveParameter(SystemParameterInfo systemParameterInfo)
			throws BusinessException, SystemException {
		SystemParameter systemParameter = convert(systemParameterInfo,
				SystemParameter.class);
		systemParameterBO.saveParameter(systemParameter);
	}
	
	@PreAuthorize("hasRole(T(com.ca.umg.business.constants.BusinessConstants).ROLE_SUPER_ADMIN)")
	public List<SystemParameterInfo> getAllSystemParameterList()
			throws BusinessException, SystemException {
		return convertToList(systemParameterBO.getSysParameters(),
				SystemParameterInfo.class);
	}
}
