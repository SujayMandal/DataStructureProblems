package com.ca.umg.business.dbauth.bo;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.UserLoginAudit;

public interface UMGUserLoginActivityBO {

	public void logActivity(final UserLoginAudit loginAudit) throws BusinessException;

	public boolean isLoginConsecutivelyFailed(final String username, final String tenantCode) throws BusinessException;

	public void lockUser(final String username, final String tenantCode) throws BusinessException;
}