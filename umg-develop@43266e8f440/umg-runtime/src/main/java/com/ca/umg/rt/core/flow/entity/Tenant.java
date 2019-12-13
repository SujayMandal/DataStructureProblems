/*
 * Tenant.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.entity;

import java.util.List;

/**
 * 
 * **/
public class Tenant
{
	private String             name;
	private String             description;
	private String             code;
	private String 			   authCode;
	private List<TenantConfig> configList;

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the name
	 **/
	public String getName()
	{
		return name;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param name the name to set
	 **/
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the description
	 **/
	public String getDescription()
	{
		return description;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param description the description to set
	 **/
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the code
	 **/
	public String getCode()
	{
		return code;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param code the code to set
	 **/
	public void setCode(String code)
	{
		this.code = code;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the configList
	 **/
	public List<TenantConfig> getConfigList()
	{
		return configList;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param configList the configList to set
	 **/
	public void setConfigList(List<TenantConfig> configList)
	{
		this.configList = configList;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
}
