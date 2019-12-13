package com.ca.umg.business.transaction.info;

import org.joda.time.DateTime;

public class TransactionVersionInfo {

    private String umgLibraryName;

    private String umgModelName;

    private String umgTidName;

    private String status;

    private DateTime publishedOn;

    private String publishedBy;

    public String getUmgLibraryName() {
        return umgLibraryName;
    }

    public void setUmgLibraryName(String umgLibraryName) {
        this.umgLibraryName = umgLibraryName;
    }

    public String getUmgModelName() {
        return umgModelName;
    }

    public void setUmgModelName(String umgModelName) {
        this.umgModelName = umgModelName;
    }

    public String getUmgTidName() {
        return umgTidName;
    }

    public void setUmgTidName(String umgTidName) {
        this.umgTidName = umgTidName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DateTime getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(DateTime publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }
}
