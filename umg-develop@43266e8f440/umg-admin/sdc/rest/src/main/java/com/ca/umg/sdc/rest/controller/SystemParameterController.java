package com.ca.umg.sdc.rest.controller;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.systemparam.delegate.SystemParameterDelegate;
import com.ca.umg.business.systemparam.info.SystemParameterInfo;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/sysParam")
public class SystemParameterController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SystemParameterController.class);

	@Inject
	private SystemParameterDelegate systemParameterDelegate;

	@RequestMapping(value = "/listAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<SystemParameterInfo>> getAllSystemParameters() {
		RestResponse<List<SystemParameterInfo>> response = new RestResponse<List<SystemParameterInfo>>();
		List<SystemParameterInfo> systemParameterInfos = null;
		try {
			systemParameterInfos = systemParameterDelegate
					.getAllSystemParameterList();
			response.setError(false);
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setResponse(systemParameterInfos);
		} catch (BusinessException | SystemException se) {
			LOGGER.error(se.getLocalizedMessage(), se);
			response.setError(true);
			response.setErrorCode(se.getCode());
			response.setMessage(se.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> createSystemParameter(
			@RequestBody SystemParameterInfo sysParam) {

		RestResponse<String> response = new RestResponse<>();
		RequestContext requestContext = RequestContext.getRequestContext();
		try {
			requestContext.setAdminAware(true);
			systemParameterDelegate.saveParameter(sysParam);
			response.setError(false);
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setResponse("Success");
		} catch (BusinessException | SystemException se) {
			LOGGER.error(se.getLocalizedMessage(), se);
			response.setError(true);
			response.setErrorCode(se.getCode());
			response.setMessage(se.getLocalizedMessage());
		} finally {
			requestContext.setAdminAware(false);
		}
		return response;

	}

}
