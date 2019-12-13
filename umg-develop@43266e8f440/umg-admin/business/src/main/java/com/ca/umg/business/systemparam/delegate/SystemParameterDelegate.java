package com.ca.umg.business.systemparam.delegate;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.systemparam.info.SystemParameterInfo;

public interface SystemParameterDelegate {
	void saveParameter(SystemParameterInfo systemParameterInfo)
			throws BusinessException, SystemException;

	List<SystemParameterInfo> getAllSystemParameterList()
			throws BusinessException, SystemException;
}
