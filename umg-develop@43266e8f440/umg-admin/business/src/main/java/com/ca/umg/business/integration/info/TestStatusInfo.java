/**
 * 
 */
package com.ca.umg.business.integration.info;

import java.util.Map;

/**
 * @author chandrsa, devasia
 * 
 */
public class TestStatusInfo extends RuntimeResponse {

    private static final long serialVersionUID = 1L;
    private Map<String, Object> response;
    private boolean terminated;

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(Map<String, Object> response) {
        this.response = response;
    }
    
    public boolean isTerminated() {
    	return terminated;
    }
    
    public void setTerminated(final boolean terminated) {
    	this.terminated = terminated;
    }
}
