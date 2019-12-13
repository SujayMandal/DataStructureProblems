/**
 * 
 */
package com.ca.umg.business.version.command.error;

/**
 * The immutable error object.
 * 
 * @author chandrsa
 *
 */
public class Error {

    private final String errorMsg;
    private final String step;
    private final String code;

    public Error(String errorMsg, String step, String code) {
        this.errorMsg = errorMsg;
        this.step = step;
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getStep() {
        return step;
    }

    public String getCode() {
        return code;
    }
}
