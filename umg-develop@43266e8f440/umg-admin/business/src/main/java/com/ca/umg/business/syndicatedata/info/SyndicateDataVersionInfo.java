/*
 * SyndicateDataVersionInfo.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.info;

import java.util.List;

import com.ca.framework.core.info.BaseInfo;

/**
 * Syndicate Data Version Information.
 *
 * @author mandavak
 **/
public class SyndicateDataVersionInfo extends BaseInfo {
    private static final long serialVersionUID = -5661661155812532820L;
    private List<SyndicateDataInfo> versions;
    private List<SyndicateDataColumnInfo> metaData;
    private List<SyndicateDataKeyInfo> keys;

    /**
     * @return the versions
     */
    public List<SyndicateDataInfo> getVersions() {
        return versions;
    }

    /**
     * @param versions
     *            the versions to set
     */
    public void setVersions(List<SyndicateDataInfo> versions) {
        this.versions = versions;
    }

    /**
     * @return the metaData
     */
    public List<SyndicateDataColumnInfo> getMetaData() {
        return metaData;
    }

    /**
     * @param metaData
     *            the metaData to set
     */
    public void setMetaData(List<SyndicateDataColumnInfo> metaData) {
        this.metaData = metaData;
    }

    /**
     * @return the keys
     */
    public List<SyndicateDataKeyInfo> getKeys() {
        return keys;
    }

    /**
     * @param keys
     *            the keys to set
     */
    public void setKeys(List<SyndicateDataKeyInfo> keys) {
        this.keys = keys;
    }
}
