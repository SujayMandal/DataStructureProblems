/**
 * 
 */
package com.ca.umg.me2.util;

/**
 * @author kamathan
 *
 */
public final class ModelExecConstants {

    public static final int NUMBER_ZERO = 0;

    public static final int NUMBER_THREE = 3;
    /**
     * time to wait for the request to fetch the next available modelet client from registry
     */
    
    public static final String TIME_OUT = "timeout";
    /**
     * number of retry attempts if error occurs while executing model, defaults to 3 retry attempts
     */

    public static final String RETRY_COUNT = "retryCount";

    private ModelExecConstants() {
    }

}
