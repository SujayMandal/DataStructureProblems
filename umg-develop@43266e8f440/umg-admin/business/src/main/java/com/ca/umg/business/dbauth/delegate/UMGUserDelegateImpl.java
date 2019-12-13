package com.ca.umg.business.dbauth.delegate;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.ChangePasswordDetail;
import com.ca.umg.business.dbauth.UMGUser;
import com.ca.umg.business.dbauth.bo.UMGUserBO;

@Named
public class UMGUserDelegateImpl implements UMGUserDelegate {

	@Inject
	private UMGUserBO umgUserBO;

	@Override
	public UMGUser findUser(final String username, final String tenantCode) throws BusinessException {
		return umgUserBO.findUser(username, tenantCode);
	}

	@Override
	public String changePassword(final ChangePasswordDetail changePasswordDetail) throws BusinessException {
		return umgUserBO.changePassword(changePasswordDetail);
	}
}