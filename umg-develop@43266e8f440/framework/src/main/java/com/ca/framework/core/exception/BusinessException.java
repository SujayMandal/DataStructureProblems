package com.ca.framework.core.exception;

public class BusinessException extends BaseException {

    public BusinessException(String code, Object[] arguements) {
        super(code, arguements);
    }

    private static final long serialVersionUID = -6468861372130310288L;

    public static BusinessException raiseBusinessException(String code, Object[] arguements) throws BusinessException {
        throw new BusinessException(code, arguements);
    }
    
    public BusinessException(String code, Object[] arguements, Throwable cause) {
        super(code, arguements, cause);
    }

    public static BusinessException newBusinessException(String code, Object[] arguements) throws BusinessException {
        throw new BusinessException(code, arguements);
    }

    public static BusinessException newBusinessException(String code, Object[] arguements, Throwable cause) throws BusinessException {
        throw new BusinessException(code, arguements, cause);
    }
}
