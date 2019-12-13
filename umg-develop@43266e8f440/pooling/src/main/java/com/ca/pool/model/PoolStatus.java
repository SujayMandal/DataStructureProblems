/**
 * 
 */
package com.ca.pool.model;

/**
 * @author kamathan
 *
 */
public class PoolStatus {

    private String poolname;

    private Integer availablemodelets;

    public String getPoolname() {
        return poolname;
    }

    public void setPoolname(String poolname) {
        this.poolname = poolname;
    }

    public Integer getAvailablemodelets() {
        return availablemodelets;
    }

    public void setAvailablemodelets(Integer availablemodelets) {
        this.availablemodelets = availablemodelets;
    }
    
    @Override
    public String toString () {
        return poolname+"-"+availablemodelets;
    }

}
