package com.ca.umg.business.dbauth.bo;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.ChangePasswordDetail;
import com.ca.umg.business.dbauth.UMGUser;

public interface UMGUserBO {
	public UMGUser findUser(final String username, final String tenantCode) throws BusinessException;

	public String changePassword(final ChangePasswordDetail changePasswordDetail) throws BusinessException;
}