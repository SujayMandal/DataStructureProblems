/**
 * 
 */
package com.fa.dp.core.exception;

public class SystemException extends BaseException {

	private static final long serialVersionUID = -7940911742198911437L;

	public SystemException(String code, Object... arguements) {
		super(code, arguements);
	}

	public SystemException(String code, Throwable cause, Object... arguements) {
		super(cause, code, arguements);
	}

	public static SystemException newSystemException(String code, Object... arguements) throws SystemException {
		throw new SystemException(code, arguements);
	}

	public static SystemException newSystemException(String code, Throwable cause, Object... arguements)
			throws SystemException {
		throw new SystemException(code, arguements, cause);
	}
}
