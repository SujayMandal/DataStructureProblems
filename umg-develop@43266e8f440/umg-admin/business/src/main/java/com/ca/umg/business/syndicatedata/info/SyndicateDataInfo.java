/*
 * SyndicateDataInfo.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.info;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.info.BaseInfo;

/**
 * Syndicate Data Information.
 *
 * @author mandavak
 **/
public class SyndicateDataInfo extends BaseInfo {
    private static final long serialVersionUID = -5661661155812532820L;
    private String versionId;
    private String description;
    private Long validFrom;
    private Long validTo;
    private Long rowCount;
    private String containerName;
    private List<Map<String, String>> data;
    private String versionName;
    private String versionDescription;
    private String validFromString;
    private String validToString;
    /**
     * 
     *
     *
     * @return the versionId
     **/
    public String getVersionId() {
        return versionId;
    }

    /**
     * 
     *
     *
     * @param versionId
     *            the versionId to set
     **/
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * 
     *
     *
     * @return the description
     **/
    public String getDescription() {
        return description;
    }

    /**
     * 
     *
     *
     * @param description
     *            the description to set
     **/
    public void setDescription(String description) {
        this.description = description;
    }

    
    public Long getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Long validFrom) {
		this.validFrom = validFrom;
	}

	public Long getValidTo() {
		return validTo;
	}

	public void setValidTo(Long validTo) {
		this.validTo = validTo;
	}

	/**
     * 
     *
     *
     * @return the rowCount
     **/
    public Long getRowCount() {
        return rowCount;
    }

    /**
     * 
     *
     *
     * @param rowCount
     *            the rowCount to set
     **/
    public void setRowCount(Long rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 
     *
     *
     * @return the containerName
     **/
    public String getContainerName() {
        return containerName;
    }

    /**
     * 
     *
     *
     * @param containerName
     *            the containerName to set
     **/
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * 
     *
     *
     * @return the data
     **/
    public List<Map<String, String>> getData() {
        return data;
    }

    /**
     * 
     *
     *
     * @param data
     *            the data to set
     **/
    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getValidFromString() {
        return validFromString;
    }

    public void setValidFromString(String validFromString) {
        this.validFromString = validFromString;
    }

    public String getValidToString() {
        return validToString;
    }

    public void setValidToString(String validToString) {
        this.validToString = validToString;
    }


}
