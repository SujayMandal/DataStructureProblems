package com.ca.framework.core.exception;

public class SystemException extends BaseException {

    private static final long serialVersionUID = -1021921174012133745L;

    public SystemException(String code, Object[] arguements) {
        super(code, arguements);
    }

    public SystemException(String code, Object[] arguements, Throwable cause) {
        super(code, arguements, cause);
    }

    public static SystemException newSystemException(String code, Object[] arguements) throws SystemException {
        throw new SystemException(code, arguements);
    }

    public static SystemException newSystemException(String code, Object[] arguements, Throwable cause) throws SystemException {
        throw new SystemException(code, arguements, cause);
    }
}
