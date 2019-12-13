package com.ca.umg.sdc.rest.controller;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static com.ca.umg.business.dbauth.bo.UMGUserBOImpl.PASSWORD_CHANGE_SUCCESS;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.ChangePasswordDetail;
import com.ca.umg.business.dbauth.delegate.UMGUserDelegate;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/dbAuth")
public class DBAuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DBAuthController.class);

	@Inject
	private UMGUserDelegate userDelegate;

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<String> changePassword(final HttpServletRequest request, @RequestBody final ChangePasswordDetail changePasswordDetail) {
		changePasswordDetail.setTenantCode(getRequestContext().getTenantCode());
		final RestResponse<String> response = new RestResponse<>();
		String status = null;
		try {
			status = userDelegate.changePassword(changePasswordDetail);
			LOGGER.debug("Password Change Status:" + status);
			response.setMessage(status);
			if (isPasswordChangeSuccess(status)) {
				response.setError(false);
			} else {
				response.setError(true);
			}
		} catch (BusinessException be) {
			LOGGER.debug("Password Change is failed");
			LOGGER.error(be.getLocalizedMessage(), be);
			response.setError(true);
			response.setErrorCode(be.getCode());
			response.setMessage(be.getLocalizedMessage());
		} finally {
			closeUserSession(status);
		}
		return response;
	}

	private void closeUserSession(final String status) {
		if (isPasswordChangeSuccess(status)) {
			LOGGER.debug("Closing user session as password is changed");
			getContext().getAuthentication().setAuthenticated(false);
			getContext().setAuthentication(null);
		}
	}

	private boolean isPasswordChangeSuccess(final String status) {
		return status != null && status.equals(PASSWORD_CHANGE_SUCCESS);
	}
}