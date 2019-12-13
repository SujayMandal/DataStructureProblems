package com.ca.umg.business.version.info;

import java.io.Serializable;

public class VersionMetricResponseInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6630702117141564833L;
    
    private String stage;

    private Integer meanTime;
    
    private Integer minTime;
    
    private Integer maxTime;
    
    private Integer percentileTransaction;

    public Integer getMeanTime() {
        return meanTime;
    }

    public void setMeanTime(Integer meanTime) {
        this.meanTime = meanTime;
    }

    public Integer getMinTime() {
        return minTime;
    }

    public void setMinTime(Integer minTime) {
        this.minTime = minTime;
    }

    public Integer getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Integer maxTime) {
        this.maxTime = maxTime;
    }

    public Integer getPercentileTransaction() {
        return percentileTransaction;
    }

    public void setPercentileTransaction(Integer percentileTransaction) {
        this.percentileTransaction = percentileTransaction;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

}
