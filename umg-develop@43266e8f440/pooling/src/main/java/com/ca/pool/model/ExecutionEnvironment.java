/**
 * 
 */
package com.ca.pool.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author kamathan
 *
 */
public enum ExecutionEnvironment implements Serializable {

    LINUX("Linux"),

    WINDOWS("Windows");

    private final String environment;

    private ExecutionEnvironment(final String environment) {
        this.environment = environment;
    }

    public String getEnvironment() {
        return environment;
    }
    
    @Override
	public String toString() {
	    return getEnvironment();
	}

    public static ExecutionEnvironment getEnvironment(final String name) {
    	ExecutionEnvironment e = null;
		if (StringUtils.equalsIgnoreCase(name, LINUX.getEnvironment())) {
			e = ExecutionEnvironment.LINUX;
		} else if (StringUtils.equalsIgnoreCase(name, WINDOWS.getEnvironment())) {
			e = ExecutionEnvironment.WINDOWS;
		}
		
		return e;
	}
}
