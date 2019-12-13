/*
 * SyndicateDataContainerInfo.java
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

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.validation.UserConfirmation;

/**
 * Syndicate Data Container Information.
 * 
 * @author mandavak
 **/
public class SyndicateDataContainerInfo extends BaseInfo {// NOPMD
    private static final long serialVersionUID = -7510033536003889157L;

    // no spaces and special characters
    @Pattern(regexp = "^[a-zA-Z0-9_]{1,50}$", message = "Container name cannot contain special charaters and spaces")
    @NotEmpty(message = "Container name cannot be empty")
    @Size(max = BusinessConstants.NUMBER_FIFTY, message = "Container name can be maximum 50 characters")
    private String containerName;

    @NotEmpty(message = "Container description cannot be empty")
    @Size(max = BusinessConstants.NUMBER_TWO_HUNDRED, message = "Container description can be maximum 200 characters")
    private String description;

    @NotEmpty(message = "Version description cannot be empty")
    @Size(max = BusinessConstants.NUMBER_TWO_HUNDRED, message = "Version description can be maximum 200 characters")
    private String versionDescription;

    @NotEmpty(message = "Version name cannot be empty")
    @Size(max = BusinessConstants.NUMBER_FIFTY, message = "Version name can be maximum 50 characters")
    private String versionName;

    private Long validFrom;

    private Long validTo;

    @NotEmpty(message = "Valid from cannot be empty")
    private String validFromString;

    private String validToString;

    // TODO: object not created scenario
    @NotEmpty(message = "Column definition cannot be empty")
    @Valid
    private List<SyndicateDataColumnInfo> metaData;

    // no duplicates
    // key name mandatory, one column mandatory
    private List<SyndicateDataKeyInfo> keyDefinitions;

    // validations against column definition, row count
    // TODO: test
    @NotEmpty(message = "Syndicate data cannot be empty")
    private List<Map<String, String>> syndicateVersionData;

    // TODO: zero testing
    @NotNull(message = "Row count cannot be empty")
    // max 10 digits
    @Digits(integer = BusinessConstants.NUMBER_TEN, fraction = BusinessConstants.NUMBER_ZERO, message = "Row count can be maximum 10 digits")
    private Long totalRows;

    private MultipartFile csvFile;

    private UserConfirmation action;

    private Long versionId;
    
    private String oldValidFromStr;

    private String oldValidToStr;

    private Long oldValidFrom;

    private Long oldValidTo;

    /**
     * 
     * @return the containerName
     **/
    public String getContainerName() {
        return containerName;
    }

    /**
     * 
     * @param containerName
     *            the containerName to set
     **/
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * 
     * @return the description
     **/
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *            the description to set
     **/
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return the validFrom
     **/
    public Long getValidFrom() {
        return validFrom;
    }

    /**
     * 
     * @param validFrom
     *            the validFrom to set
     **/
    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * 
     * @return the validTo
     **/
    public Long getValidTo() {
        return validTo;
    }

    /**
     * 
     * @param validTo
     *            the validTo to set
     **/
    public void setValidTo(Long validTo) {
        this.validTo = validTo;
    }

    /**
     * 
     * @return the metaData
     **/
    public List<SyndicateDataColumnInfo> getMetaData() {
        return metaData;
    }

    /**
     * 
     * @param metaData
     *            the metaData to set
     **/
    public void setMetaData(List<SyndicateDataColumnInfo> metaData) {
        this.metaData = metaData;
    }

    public Long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Long totalRows) {
        this.totalRows = totalRows;
    }

    public List<Map<String, String>> getSyndicateVersionData() {
        return syndicateVersionData;
    }

    public void setSyndicateVersionData(List<Map<String, String>> syndicateVersionData) {
        this.syndicateVersionData = syndicateVersionData;
    }

    public List<SyndicateDataKeyInfo> getKeyDefinitions() {
        return keyDefinitions;
    }

    public void setKeyDefinitions(List<SyndicateDataKeyInfo> keyDefinitions) {
        this.keyDefinitions = keyDefinitions;
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

    public MultipartFile getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(MultipartFile csvFile) {
        this.csvFile = csvFile;
    }

    public UserConfirmation getAction() {
        return action;
    }

    public void setAction(UserConfirmation action) {
        this.action = action;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getOldValidFromStr() {
        return oldValidFromStr;
    }

    public void setOldValidFromStr(String oldValidFromStr) {
        this.oldValidFromStr = oldValidFromStr;
    }

    public String getOldValidToStr() {
        return oldValidToStr;
    }

    public void setOldValidToStr(String oldValidToStr) {
        this.oldValidToStr = oldValidToStr;
    }

    public Long getOldValidFrom() {
        return oldValidFrom;
    }

    public void setOldValidFrom(Long oldValidFrom) {
        this.oldValidFrom = oldValidFrom;
    }

    public Long getOldValidTo() {
        return oldValidTo;
    }

    public void setOldValidTo(Long oldValidTo) {
        this.oldValidTo = oldValidTo;
    }


}
