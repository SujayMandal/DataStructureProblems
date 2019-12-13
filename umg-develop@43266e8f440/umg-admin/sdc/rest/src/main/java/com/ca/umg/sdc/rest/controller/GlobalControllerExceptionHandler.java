package com.ca.umg.sdc.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.exception.RESTExceptionCodes;
import com.ca.umg.sdc.rest.utils.RestResponse;


@ControllerAdvice
public class GlobalControllerExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody 
    public RestResponse<Exception> handleConflict(HttpServletRequest req,Exception ex) {
        LOGGER.error("Global Error message :", ex);
		RestResponse<Exception> errorResponse = new RestResponse<Exception>();
		errorResponse.setErrorCode(RESTExceptionCodes.RSE0000100);
		errorResponse.setError(true);
        errorResponse.setMessage(RestConstants.GLOBAL_ERROR_MESSAG);
		return errorResponse;
    }
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody 
    public RestResponse<Exception> handleConflict1(HttpServletRequest req,Exception ex) {
        LOGGER.error("Global Error message for access denied:", ex);
        RestResponse<Exception> errorResponse = new RestResponse<Exception>();
        errorResponse.setErrorCode(RESTExceptionCodes.RSE0000401);
        errorResponse.setError(true);
        errorResponse.setMessage(RestConstants.GLOBAL_ERROR_MESSAG);
        return errorResponse;
    }
}
