/*
 * FlowMetaData.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 *
 * Author : KR Kumar
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.generator;

import java.util.HashMap;
import java.util.Map;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.Property;

import com.ca.umg.rt.core.flow.entity.ModelLibrary;

/**
 * 
 **/
public class FlowMetaData extends HashMap<Integer, MetaData> implements MetaData {
    /**  */
    private static final long serialVersionUID = -8522548348445689327L;
    @Property
    private String modelName;
    @Property
    private int majorVersion;
    @Property
    private int minorVersion;
    private long startDate;
    private long endDate;
    private boolean allowNull;
    private String modelType;
    private String status;
    private MappingMetaData mappingMetaData = new MappingMetaData();
    private ModelLibrary modelLibrary = new ModelLibrary();
    private Map<String, String> paramDatatypeMap = new HashMap<String, String>();
    private String exeEnv;

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public Map<String, String> getParamDatatypeMap() {
        return paramDatatypeMap;
    }

    public void setParamDatatypeMap(Map<String, String> paramDatatypeMap) {
        this.paramDatatypeMap = paramDatatypeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public String getModelName() {
        return modelName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param modelName
     *            DOCUMENT ME!
     **/
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * DOCUMENT ME!
     *
     * @param majorVersion
     *            DOCUMENT ME!
     **/
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * DOCUMENT ME!
     *
     * @param minorVersion
     *            DOCUMENT ME!
     **/
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public long getStartDate() {
        return startDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param startDate
     *            DOCUMENT ME!
     **/
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public long getEndDate() {
        return endDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param endDate
     *            DOCUMENT ME!
     **/
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public MappingMetaData getMappingMetaData() {
        return mappingMetaData;
    }

    public void setMappingMetaData(MappingMetaData mappingMetaData) {
        this.mappingMetaData = mappingMetaData;
    }

    public ModelLibrary getModelLibrary() {
        return modelLibrary;
    }

    public void setModelLibrary(ModelLibrary modelLibrary) {
        this.modelLibrary = modelLibrary;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }

    public String getExeEnv() {
        return exeEnv;
    }

    public void setExeEnv(String exeEnv) {
        this.exeEnv = exeEnv;
    }

}
