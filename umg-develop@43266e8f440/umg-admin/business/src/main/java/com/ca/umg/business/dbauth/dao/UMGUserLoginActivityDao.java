package com.ca.umg.business.dbauth.dao;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.UserLoginAudit;

public interface UMGUserLoginActivityDao {

	public void insertUserLoginActivity(final UserLoginAudit loginAudit) throws BusinessException;

	public List<UserLoginAudit> getTop3Activies(final String username, final String tenantCode) throws BusinessException;

	public void lockUser(final String username, final String tenantCode) throws BusinessException;
}
