/**
 * 
 */
package com.ca.umg.rt.flows.version;

import java.io.Serializable;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.Property;

/**
 * @author chandrsa
 *
 */
final public class BatchVersionInfo implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private static final String APPENDER = "-batch";
    
    @Property
    private final String tenantCode;
    
    public BatchVersionInfo(String tenantCode) {
        super();
        this.tenantCode = tenantCode + APPENDER;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    @Override
    public int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return Pojomatic.equals(this, obj);
    }
}
