/*
 * ModelRequestHeader.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.endpoint.http;

import java.util.List;

import org.joda.time.DateTime;
 
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * 
 * **/
@SuppressWarnings({ "PMD.TooManyFields" })
public class Header
{
	private String   modelName;
	private Integer  majorVersion;
	private Integer  minorVersion;
	private DateTime date;
	private String   transactionId;
	private String batchId; 
	
	/**
	 * added this to fix umg-4251 to set versionCreationTest flag to true 
     *  if it is test transaction during version creation else the flag will be false 
	 */
	private Boolean versionCreationTest;
	//added to fix UMG-4500 Additional variables in Transaction header
	private String user;
	private String transactionType;
	
	//added for UMG-4697
	private String executionGroup;
	
	private String fileName;
	
	private Integer tenantTranCount;
	
	 @JsonProperty
	private List<String> addonValidation;	
	
	private String transactionMode;	
	
	private String channel;
	
	private boolean payloadStorage;	
	  
	private boolean storeRLogs;
	
	public Integer getTenantTranCount() {
		return tenantTranCount;
	}

	public void setTenantTranCount(Integer tenantTranCount) {
		this.tenantTranCount = tenantTranCount;
	}

	public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExecutionGroup() {
		return executionGroup;
	}

	public void setExecutionGroup(String executionGroup) {
		this.executionGroup = executionGroup;
	}

	public Boolean getVersionCreationTest() {
		return versionCreationTest;
	}

	public void setVersionCreationTest(Boolean versionCreationTest) {
		this.versionCreationTest = versionCreationTest;
	}

	//added to fix UMG-4500 Additional variables in Transaction header
	public String getUser() {
	    return user;
	}

	public void setUser(String user) {
	    this.user = user;
	}
	    
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the modelName
	 **/
	public String getModelName()
	{
		return modelName;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param modelName the modelName to set
	 **/
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the majorVersion
	 **/
	public Integer getMajorVersion()
	{
		return majorVersion;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param majorVersion the majorVersion to set
	 **/
	public void setMajorVersion(Integer majorVersion)
	{
		this.majorVersion = majorVersion;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the minorVersion
	 **/
	public Integer getMinorVersion()
	{
		return minorVersion;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param minorVersion the minorVersion to set
	 **/
	public void setMinorVersion(Integer minorVersion)
	{
		this.minorVersion = minorVersion;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the date
	 **/
	public DateTime getDate()
	{
		return date;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param date the date to set
	 **/
	public void setDate(DateTime date)
	{
		this.date = date;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the transactionId
	 **/
	public String getTransactionId()
	{
		return transactionId;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param transactionId the transactionId to set
	 **/
	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}
 
	
	/**
	 * @return list of addonValidations(1.ModelOutput)
	 * 
	 */
	public List<String> getAddonValidation() {
		return addonValidation;
	}

	/**
	 * @param addonValidations
	 * 
	 */
	public void setAddonValidation(List<String> addonValidation) {
		this.addonValidation = addonValidation;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}
	
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
  
	public boolean isPayloadStorage() {
		return payloadStorage;
	}

	public void setPayloadStorage(boolean payloadStorage) {
		this.payloadStorage = payloadStorage;
	}
  
	public boolean isStoreRLogs() {
		return storeRLogs;
	}

	public void setStoreRLogs(boolean storeRLogs) {
		this.storeRLogs = storeRLogs;
	}


}
