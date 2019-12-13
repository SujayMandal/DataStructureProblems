/**
 * 
 */
package com.ca.framework.core.common.model;

import java.io.Serializable;

/**
 * @author chandrsa
 *
 */
public class BaseResponse<T> implements Serializable{

	private static final long serialVersionUID = 1L;

	private T response;

	private boolean success = true;

	private String message;

	private String errorCode;

	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
