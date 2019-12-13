package com.ca.umg.business.dbauth.dao;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.UMGUser;

public interface UMGUserDao {
	public UMGUser findUser(final String username, final String tenantCode) throws BusinessException;

	public void updatePassword(final String username, final String tenantCode, final String newPassword) throws BusinessException;
}
