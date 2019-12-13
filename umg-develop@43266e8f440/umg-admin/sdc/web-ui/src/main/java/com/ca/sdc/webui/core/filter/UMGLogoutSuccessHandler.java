package com.ca.sdc.webui.core.filter;

import static com.ca.sdc.webui.core.filter.TenantResolutionFilter.getClientsIPAddr;
import static com.ca.umg.business.dbauth.UserLoginActivity.LOGOUT_SUCESSS;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000515;
import static java.lang.System.currentTimeMillis;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.UMGUserDetails;
import com.ca.umg.business.dbauth.UserLoginAudit;
import com.ca.umg.business.dbauth.bo.UMGUserLoginActivityBO;

@SuppressWarnings("PMD")
public class UMGLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

	@Inject
	private UMGUserLoginActivityBO userLoginActivityBO;

	@Override
	public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException,
	ServletException {
		super.onLogoutSuccess(request, response, authentication);
		final String ipAddress = getClientsIPAddr(request);
		final UserLoginAudit loginAudit;
		if(authentication != null){
			loginAudit = createUserLoginAudit((UMGUserDetails) authentication.getPrincipal(), ipAddress);
			try {
				logoutSucessActivity(loginAudit);
			} catch (BusinessException be) {
				throw new ServletException(be.getLocalizedMessage());
			}
		}
	}

	private UserLoginAudit createUserLoginAudit(final UMGUserDetails userDetails, final String ipAddress) {
		final UserLoginAudit loginAudit = new UserLoginAudit();
		loginAudit.setUsername(userDetails.getUsername());
		loginAudit.setTenantCode(userDetails.getTenantCode());
		loginAudit.setAccessTime(currentTimeMillis());
		loginAudit.setIpAddress(ipAddress);
		loginAudit.setReasonCode(BSE0000515);
		return loginAudit;
	}

	private void logoutSucessActivity(final UserLoginAudit loginAudit) throws BusinessException {
		loginAudit.setActivity(LOGOUT_SUCESSS);
		userLoginActivityBO.logActivity(loginAudit);
	}
}