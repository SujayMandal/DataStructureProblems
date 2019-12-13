/**
 * 
 */
package com.ca.umg.rt.util;

import java.io.File;

import com.ca.framework.core.requestcontext.RequestContext;

/**
 * @author chandrsa
 *
 */
public final class RuntimeConfigurationUtil {
    
    private RuntimeConfigurationUtil() {
    }

    /**
     * returns the base model directory path
     * 
     * @return
     */
    public static String getSanBasePath(String sanBasePath) {
        // read san location from config file
        //String sanBase = System.getProperty(SystemConstants.SAN_BASE);
        // read tenant code from request context
        String tenantCode = RequestContext.getRequestContext().getTenantCode();
        // prepare base model directory path and return it
        return new StringBuffer(sanBasePath).append(File.separatorChar).append(tenantCode).toString();
    } 
}
