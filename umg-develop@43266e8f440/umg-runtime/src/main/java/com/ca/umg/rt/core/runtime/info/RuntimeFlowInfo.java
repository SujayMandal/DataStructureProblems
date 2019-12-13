package com.ca.umg.rt.core.runtime.info;

import org.joda.time.DateTime;

public class RuntimeFlowInfo {
    private String name;
    private int majorVersion;
    private int minorVersion;
    private DateTime publishedDate;
    private DateTime deactivatedDate;
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the majorVerion
     */
    public int getMajorVersion() {
        return majorVersion;
    }
    /**
     * @param majorVerion the majorVerion to set
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }
    /**
     * @return the minorVersion
     */
    public int getMinorVersion() {
        return minorVersion;
    }
    /**
     * @param minorVersion the minorVersion to set
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }
    public DateTime getDeactivatedDate() {
        return deactivatedDate;
    }
    public void setDeactivatedDate(DateTime deactivatedDate) {
        this.deactivatedDate = deactivatedDate;
    }
    public DateTime getPublishedDate() {
        return publishedDate;
    }
    public void setPublishedDate(DateTime publishedDate) {
        this.publishedDate = publishedDate;
    }
    
}
