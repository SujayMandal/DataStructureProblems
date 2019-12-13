/*
 * PayloadToMapTransformer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.transformer;

import java.net.ConnectException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.handler.ReplyRequiredException;
import org.springframework.integration.message.ErrorMessage;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.web.client.HttpClientErrorException;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;

/**
 * 
 **/
public class JdbcValidateTransformer
	extends AbstractTransformer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcValidateTransformer.class);

	/**
	 * DOCUMENT ME!
	 *
	 * @param message DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	protected Object doTransform(final Message<?> message)
	{
        long startTime = System.currentTimeMillis();//NOPMD
		LOGGER.debug("Start JdbcValidateTransormer::doTransform");
		Map<String, Object> responseMessage = null;
		if (message instanceof ErrorMessage)
		{
		    responseMessage = analyzeError(message);
		}else{
		    responseMessage = createResponse(true, null,
                    null, message.getPayload());
		}
		LOGGER.debug("End JdbcValidateTransormer::doTransform");
		Object obj = MessageBuilder.withPayload(responseMessage).copyHeaders(message.getHeaders()).build();
        LOGGER.debug("JdbcValidateTransformer.doTransform : " + (System.currentTimeMillis() - startTime));
        return obj;
	}

    public Map<String, Object> analyzeError(final Message<?> message) {
        ErrorMessage        errorMessage    = (ErrorMessage)message;
		Throwable           th              = searchCause(errorMessage.getPayload());
		Map<String, Object> responseMessage = null;

		if (th instanceof ReplyRequiredException)
		{
			SystemException systemException = new SystemException(RuntimeExceptionCode.RSE000401,
			                                                      new Object[] { th.getMessage() },
			                                                      th);
			responseMessage = createResponse(false, systemException.getCode(),
			                                 systemException.getLocalizedMessage(), null);
		}
		else
		{
			SystemException systemException = new SystemException(RuntimeExceptionCode.RSE000401,
			                                                      new Object[] { th.getMessage() },
			                                                      th);
			responseMessage = createResponse(false, systemException.getCode(),
			                                 systemException.getLocalizedMessage(), null);
		}
        return responseMessage;
    }

	/**
	 * DOCUMENT ME!
	 *
	 * @param status DOCUMENT ME!
	 * @param errorCode DOCUMENT ME!
	 * @param errorMessage DOCUMENT ME!
	 * @param result DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	private Map<String, Object> createResponse(boolean status,
	                                           String  errorCode,
	                                           String  errorMessage,
	                                           Object  result)
	{
		Map<String, Object> jdbcResult = new LinkedHashMap<String, Object>();
		jdbcResult.put(MessageVariables.SUCCESS, status);
		jdbcResult.put(MessageVariables.ERROR_CODE, errorCode);
		jdbcResult.put(MessageVariables.ERROR_MESSAGE, errorMessage);
		jdbcResult.put(MessageVariables.RESULT, result);
		return jdbcResult;
	}

	/**
	 * Helper method to search for know exception in this error handler. This helper
	 * specifically search for {@link MessageRejectedException), {@link HttpClientErrorException}
	 * and {@link ConnectException} to identify if exception occurred due to ME2 unavailability or
	 * model let unavailability. {@link MessageRejectedException} occurs when ME2 response message
	 * contains "success" value as false, meaning ME2 could not connect to any modelet. {@link
	 * HttpClientErrorException} occurs when ME2 itself is not available. {@link
	 * java.net.ConnectException} occurs when ME2 is not started.
	 *
	 * @param exception Root cause {@link Throwable}
	 *
	 * @return Specific cause {@link Throwable}
	 **/
	private Throwable searchCause(Throwable exception)
	{
		if (exception instanceof ReplyRequiredException)
		{
			return exception;
		}
		else
		{
			if (exception.getCause() != null)
			{
				Throwable cause = searchCause(exception.getCause());

				if (cause instanceof ReplyRequiredException)
				{
					return cause;
				}
			}

			return exception;
		}
	}
}
