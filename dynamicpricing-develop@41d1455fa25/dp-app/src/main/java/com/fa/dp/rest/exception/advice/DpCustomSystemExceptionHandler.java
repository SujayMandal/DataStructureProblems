package com.fa.dp.rest.exception.advice;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.business.BusinessException;
import com.fa.dp.rest.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom Exception handler class for System and business exception.
 */
@ControllerAdvice
@Slf4j
public class DpCustomSystemExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Handle business exception like validation or any business logic failure
	 * @param se
	 * @param request
	 *
	 * @return
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<RestResponse> handleBusinessException(BusinessException se, WebRequest request) {

		RestResponse response = new RestResponse();
		response.setErrorCode(se.getCode());
		response.setMessage(se.getLocalizedMessage());
		response.setSuccess(false);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Handle system exception
	 * @param se
	 * @param request
	 *
	 * @return
	 */
	@ExceptionHandler(SystemException.class)
	public ResponseEntity<RestResponse> handleSystemException(SystemException se, WebRequest request) {

		RestResponse response = new RestResponse();
		response.setErrorCode(se.getCode());
		response.setMessage(se.getLocalizedMessage());
		response.setSuccess(false);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
