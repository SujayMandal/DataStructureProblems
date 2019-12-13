package com.ca.umg.business.version.info;

import java.io.Serializable;
import java.util.List;

public class VersionSummaryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;

    private List<Integer> majorVersions;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getMajorVersions() {
        return majorVersions;
    }

    public void setMajorVersions(List<Integer> majorVersions) {
        this.majorVersions = majorVersions;
    }

}
