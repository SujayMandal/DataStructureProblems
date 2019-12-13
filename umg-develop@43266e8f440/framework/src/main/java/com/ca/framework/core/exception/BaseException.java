/****
Product Name : Universal Model Gateway
Version      : 0.1 alpha
Author       :
Date Created :




 *****/
package com.ca.framework.core.exception;

import com.ca.framework.core.util.MessageContainer;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 */
public abstract class BaseException extends Exception {
    private static final long serialVersionUID = -7363238402125561767L;
    private String code = null;
    private Object[] arguements = null;

    public BaseException(String code, Object[] arguements) {
        this.code = code;
        this.arguements = arguements != null ? arguements.clone() : null;
    }

    public BaseException(String code, Object[] arguements, Throwable cause) {
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
