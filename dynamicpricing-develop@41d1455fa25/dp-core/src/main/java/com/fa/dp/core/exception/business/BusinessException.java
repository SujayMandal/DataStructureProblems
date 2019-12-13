package com.fa.dp.core.exception.business;

import com.fa.dp.core.exception.BaseException;

public class BusinessException extends BaseException {

	private static final long serialVersionUID = 9078358673137365969L;

	public BusinessException(String code, Object... arguements) {
		super(code, arguements);
	}

	public BusinessException(String code, Throwable cause, Object... arguements) {
		super(cause, code, arguements);
	}

	public static BusinessException newBusinessException(String code, Object... arguements) throws BusinessException {
		throw new BusinessException(code, arguements);
	}

	public static BusinessException newBusinessException(String code, Throwable cause, Object... arguements) throws BusinessException {
		throw new BusinessException(code, cause, arguements);
	}
}
