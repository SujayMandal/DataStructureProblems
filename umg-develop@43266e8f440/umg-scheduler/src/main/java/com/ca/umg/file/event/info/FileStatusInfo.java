/**
 * 
 */
package com.ca.umg.file.event.info;

import java.io.Serializable;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

/**
 * @author kamathan
 *
 */
public class FileStatusInfo implements Serializable {

    private static final long serialVersionUID = 5783056813189027946L;

    @Property
    private String name;

    @Property(policy = PojomaticPolicy.TO_STRING)
    private String status;

    @Property(policy = PojomaticPolicy.TO_STRING)
    private long ackTime;

    @Property(policy = PojomaticPolicy.TO_STRING)
    private long postedTime;

    @Property
    private String filePath;

    @Property
    private String tenantCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getAckTime() {
        return ackTime;
    }

    public void setAckTime(long ackTime) {
        this.ackTime = ackTime;
    }

    public long getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(long postedTime) {
        this.postedTime = postedTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    /**
     * Don't override. Use Pojomatic annotations instead.
     */
    @Override
    public final String toString() {
        return Pojomatic.toString(this);
    }

    /*
     * Method is final because derived classes should use Pojomatic annotations.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj) {
        return Pojomatic.equals(this, obj);
    }

    /*
     * Method is final because derived classes should use Pojomatic annotations.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return Pojomatic.hashCode(this);
    }

}
