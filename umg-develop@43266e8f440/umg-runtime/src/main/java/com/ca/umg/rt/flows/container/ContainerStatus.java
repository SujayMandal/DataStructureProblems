package com.ca.umg.rt.flows.container;

public enum ContainerStatus {
    ERROR("ERROR"),
    RUNNING("RUNNING"),
    REFRESH("REFRESH"),
    STOPPED("STOPPED");
    private final String status;       
    ContainerStatus(String status) {
        this.status = status;
    }
    public boolean equalsName(String otherStatus){
        return (otherStatus == null)? false:status.equals(otherStatus);
    }

    public String toString(){
       return status;
    }
}
