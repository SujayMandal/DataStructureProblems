package com.ca.umg.rt.metrics.reporter.entity;

import java.util.Date;

public class CoreEntity {

    protected String name;
    protected Date timestamp;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        if (timestamp != null) {
            return (Date) timestamp.clone();
        }
        return null;
    }

    public void setTimestamp(final Date timestamp) {
        if (timestamp != null) {
            this.timestamp = (Date) timestamp.clone();
        }
    }
}
