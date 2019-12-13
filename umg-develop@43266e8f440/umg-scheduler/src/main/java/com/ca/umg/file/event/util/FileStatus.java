package com.ca.umg.file.event.util;

public enum FileStatus {

    ACK("ACK"),

    POSTED("POSTED");

    private String status;

    private FileStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
