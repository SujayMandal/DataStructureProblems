/*
 * RestResponse.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.web.rest.response;

import java.io.Serializable;

/**
 * Wrapper object for REST requests. Wrap actual response with meta data information like if
 * response is success or not and error message if any.
 *
 * @param <T> DOCUMENT ME!
 **/
public class RestResponse<T>
	implements Serializable
{
	private static final long serialVersionUID = 618151937793694961L;
	private boolean           error;
	private String            errorCode;
	private String            message;
	private T                 response;

	/**
	 * UMG error code.
	 *
	 * @return DOCUMENT ME!
	 **/
	public String getErrorCode()
	{
		return errorCode;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param errorCode DOCUMENT ME!
	 **/
	public void setErrorCode(String errorCode)
	{
		this.errorCode                         = errorCode;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public boolean isError()
	{
		return error;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param error DOCUMENT ME!
	 **/
	public void setError(boolean error)
	{
		this.error = error;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public String getMessage()
	{
		return message;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param message DOCUMENT ME!
	 **/
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public T getResponse()
	{
		return response;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param response DOCUMENT ME!
	 **/
	public void setResponse(T response)
	{
		this.response = response;
	}
}
