/**
 * 
 */
package com.ca.umg.business.integration.info;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class RuntimeRequest implements Serializable {

    private static final long serialVersionUID = -5794973250571430025L;
    private String name;
    private int minorVersion;
    private int majorVersion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }
}
