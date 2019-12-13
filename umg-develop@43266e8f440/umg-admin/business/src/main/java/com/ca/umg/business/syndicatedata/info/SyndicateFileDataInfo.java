/*
 * SyndicateFileDataInfo.java
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

/**
 * File Data information.
 * 
 * @author mandavak
 *
 */
public class SyndicateFileDataInfo {

    private List<Map<String, String>> data;

    /**
     * @return the data
     */
    public List<Map<String, String>> getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }
}
