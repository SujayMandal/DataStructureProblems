package com.ca.exception;

import java.util.ResourceBundle;

/**
 * Created by repvenk on 9/21/2016.
 */
public class RAClientException extends Exception {

    private final static ResourceBundle labels = ResourceBundle.getBundle("errorcodes");

    public RAClientException() {
        super();
    }

    public RAClientException(String code) {
        super(code);
    }

    public RAClientException(String code, Throwable cause) {
        super(code, cause);
    }

    @Override
    public String getLocalizedMessage() {
        return labels.getString(getMessage());
    }
}
