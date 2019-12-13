package com.ca.umg.business.syndicatedata.info;

import java.util.List;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.QueryLaunchInfo;

public class SyndicateDataQueryInfo extends BaseInfo {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private SyndicateDataQueryObjectInfo queryObject;

    private List<SyndicateDataQueryParameterInfo> inputParameters;

    private List<SyndicateDataQueryParameterInfo> outputParameters;

    private Integer execSequence;

    private MappingInfo mapping;

    private String mappingType;

    private String rowType;

    private String dataType;

    private QueryLaunchInfo queryLaunchInfo;

    private boolean mandatory;

    private String createdOn;

    private String updatedOn;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SyndicateDataQueryObjectInfo getQueryObject() {
        return queryObject;
    }

    public void setQueryObject(SyndicateDataQueryObjectInfo queryObject) {
        this.queryObject = queryObject;
    }

    public List<SyndicateDataQueryParameterInfo> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(List<SyndicateDataQueryParameterInfo> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public List<SyndicateDataQueryParameterInfo> getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(List<SyndicateDataQueryParameterInfo> outputParameters) {
        this.outputParameters = outputParameters;
    }

    public Integer getExecSequence() {
        return execSequence;
    }

    public void setExecSequence(Integer execSequence) {
        this.execSequence = execSequence;
    }

    public MappingInfo getMapping() {
        return mapping;
    }

    public void setMapping(MappingInfo mapping) {
        this.mapping = mapping;
    }

    public String getRowType() {
        return rowType;
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getMappingType() {
        return mappingType;
    }

    public void setMappingType(String mappingType) {
        this.mappingType = mappingType;
    }

    public QueryLaunchInfo getQueryLaunchInfo() {
        return queryLaunchInfo;
    }

    public void setQueryLaunchInfo(QueryLaunchInfo queryLaunchInfo) {
        this.queryLaunchInfo = queryLaunchInfo;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }


    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

}
