/*
 * TenantConfig.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.entity;

/**
 * 
 * **/
public class TenantConfig
{
	private String keyType;
	private String key;
	private String value;

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the keyType
	 **/
	public String getKeyType()
	{
		return keyType;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param keyType the keyType to set
	 **/
	public void setKeyType(String keyType)
	{
		this.keyType = keyType;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the key
	 **/
	public String getKey()
	{
		return key;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param key the key to set
	 **/
	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the value
	 **/
	public String getValue()
	{
		return value;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param value the value to set
	 **/
	public void setValue(String value)
	{
		this.value = value;
	}
}
