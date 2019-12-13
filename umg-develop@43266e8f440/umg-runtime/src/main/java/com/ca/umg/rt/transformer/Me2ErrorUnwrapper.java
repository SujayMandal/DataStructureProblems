/*
 * ErrorUnWrapper.java
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
import org.springframework.integration.MessageChannel;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.message.ErrorMessage;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.web.client.HttpClientErrorException;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;

/**
 * Transformation component to translate thrown exception into meaning full error code and message.
 * This component is specifically used to handle exceptions thrown by ME2 http outbound gateway.
 **/
public class Me2ErrorUnwrapper
	extends AbstractTransformer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MappingInputTransformer.class);

	/**
	 * Transforms error message into a meaning full response containing error code and
	 * error message.
	 *
	 * @param message {@link ErrorMessage} containing thrown exception.
	 *
	 * @return Response {@link Message} containing error code and meaning full message translated from
	 *         thrown exception.
	 *
	 * @throws {@link SystemException}
	 **/
	@Override
	protected Object doTransform(Message<?> message)
	  throws SystemException
	{
		ErrorMessage errorMessage = (ErrorMessage)message;
		LOGGER.error("ME2 gateway failed reason is {}", errorMessage.getPayload().getMessage(),
		             errorMessage.getPayload());

		Throwable           th              = searchCause(errorMessage.getPayload());
		Map<String, Object> responseMessage = null;

		if (th instanceof HttpClientErrorException)
		{
			SystemException systemException = new SystemException(RuntimeExceptionCode.RSE000301,
			                                                      new Object[]
			                                                      {
			                                                          errorMessage.getPayload()
			                                                                      .getMessage(),
			                                                          th.getMessage()
			                                                      }, errorMessage.getPayload());
			responseMessage = createResponse(systemException.getCode(),
			                                 systemException.getLocalizedMessage());
		}
		else if (th instanceof ConnectException)
		{
            SystemException systemException = new SystemException(RuntimeExceptionCode.RSE000301,
			                                                      new Object[]
			                                                      {
			                                                          errorMessage.getPayload()
			                                                                      .getMessage(),
			                                                          th.getMessage()
			                                                      }, errorMessage.getPayload());
			responseMessage = createResponse(systemException.getCode(),
			                                 systemException.getLocalizedMessage());
		}
		else
		{
            SystemException systemException = new SystemException(RuntimeExceptionCode.RSE000302,
			                                                      new Object[]
			                                                      {
			                                                          errorMessage.getPayload()
			                                                                      .getMessage()
			                                                      }, errorMessage.getPayload());
			responseMessage = createResponse(systemException.getCode(),
			                                 systemException.getLocalizedMessage());
		}

		return MessageBuilder.withPayload(responseMessage)
		                     .copyHeadersIfAbsent(errorMessage.getHeaders())
		                     .setReplyChannel((MessageChannel)errorMessage.getHeaders()
		                                                                  .getReplyChannel()).build();
	}

	/**
	 * Create ME2 error response in case of network/connection issues.  This message will
	 * get rejected in next component and appropriate message will be conveyed to the user.
	 *
	 * @param errorCode DOCUMENT ME!
	 * @param errorMessage DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	private Map<String, Object> createResponse(String errorCode,
	                                           String errorMessage)
	{
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put(MessageVariables.SUCCESS, false);
		response.put(MessageVariables.ERROR_CODE, errorCode);
		response.put(MessageVariables.ME2_ERROR_MESSAGE, errorMessage);
		response.put(MessageVariables.ME2_RESPONSE, null);
		return response;
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
		if (exception instanceof ConnectException || exception instanceof HttpClientErrorException)
		{
			return exception;
		}
		else
		{
			if (exception.getCause() != null)
			{
				Throwable cause = searchCause(exception.getCause());

				if (cause instanceof MessageRejectedException || cause instanceof ConnectException ||
					    cause instanceof HttpClientErrorException)
				{
					return cause;
				}
			}

			return exception;
		}
	}
}
