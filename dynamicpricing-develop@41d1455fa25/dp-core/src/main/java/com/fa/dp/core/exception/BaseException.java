/**
 * 
 */
package com.fa.dp.core.exception;

import com.fa.dp.localization.MessageContainer;

public class BaseException extends Exception {

	private static final long serialVersionUID = -5720057029451432819L;

	private String code = null;

	private Object[] arguements = null;

	public BaseException(String code, Object... arguements) {
		this.code = code;
		this.arguements = arguements != null ? arguements.clone() : null;
	}

	public BaseException(Throwable cause, String code, Object... arguements) {
		super(cause);
		this.code = code;
		this.arguements = arguements != null ? arguements.clone() : null;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getLocalizedMessage() {
		return MessageContainer.getMessage(code, arguements);
	}
}
