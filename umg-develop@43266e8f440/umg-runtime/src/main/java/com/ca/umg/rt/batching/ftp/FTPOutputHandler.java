/**
 * 
 */
package com.ca.umg.rt.batching.ftp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.util.RuntimeBatchUtil;

/**
 * @author chandrsa
 *
 */
public class FTPOutputHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FTPOutputHandler.class);
    
    private final static String APPENDER = "-remote-";
    
    private CacheRegistry cacheRegistry;
    /**
     * Tenant Name
     */
    private String name;

    private String transportType;

    /**
     * @return
     */
    public Map<Object, Object> getCache() {
        return cacheRegistry.getMap(this.getName() + APPENDER + this.getTransportType());
    }

    @ServiceActivator
    public void setFTPHeaderInfo(Message<?> message) {
    	String originalFileName = null;
    	if (!(Boolean) message.getHeaders().get("ACCEPT")) {
    		originalFileName = (String) message.getHeaders().get("file_name");
    		LOGGER.error(String.format("No unlock required for file %s in FTP as this was uploaded from UI. "
    				+ "Tenant Name : %s", originalFileName, message.getHeaders().get(RuntimeConstants.TENANT_CODE)));
    	} else {
    		LOGGER.error("Tenant Code " + message.getHeaders().get(RuntimeConstants.TENANT_CODE) + " SUCCESS " + message.getHeaders().get("SUCCESS"));
    		originalFileName = RuntimeBatchUtil.getOutputFileName((String) message.getHeaders().get("file_name"));
    		getCache().remove(originalFileName);
    		LOGGER.error(String.format("%s Unlocked after processing. Tenant Name : %s", originalFileName,
    				message.getHeaders().get(RuntimeConstants.TENANT_CODE)));
    	}
    }

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
}
