package com.ca.umg.rt.core.runtime.info;

import org.joda.time.DateTime;

public class ContainerInfo {
    private String name;
    private int modelCount;
    private DateTime startTime;
    private int timeTaken;
    private String status;
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
     * @return the modelCount
     */
    public int getModelCount() {
        return modelCount;
    }
    /**
     * @param modelCount the modelCount to set
     */
    public void setModelCount(int modelCount) {
        this.modelCount = modelCount;
    }
    /**
     * @return the startTime
     */
    public DateTime getStartTime() {
        return startTime;
    }
    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }
    /**
     * @return the timeTaken
     */
    public int getTimeTaken() {
        return timeTaken;
    }
    /**
     * @param timeTaken the timeTaken to set
     */
    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
}
