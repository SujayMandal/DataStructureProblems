package com.ca.umg.business.accessprivilege.delegate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UserDetails;

import com.altisource.iam.dto.RFUserDetails;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.bo.SwitchTenantBO;

@Named
public class SwitchTenantDelegateImpl implements SwitchTenantDelegate {
	
	
	@Inject
	private SwitchTenantBO switchTenantBO;

	@Override
	public void switchAndSetTenant(String tenantCode, HttpServletRequest request) throws SystemException {
		switchTenantBO.switchAndSetTenant(tenantCode, request);
	}

	@Override
	public UserDetails getUmgUserDetails(RFUserDetails rfUserDetails, String tenantCode) throws SystemException {
		return switchTenantBO.getUmgUserDetails(rfUserDetails, tenantCode);
	}

}
