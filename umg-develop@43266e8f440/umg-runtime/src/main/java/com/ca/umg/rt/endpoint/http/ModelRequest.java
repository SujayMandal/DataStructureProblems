/*
 * ModelRequest.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.endpoint.http;

import java.util.Map;

/**
 * 
 * **/
public class ModelRequest
{
	private Header              header;
	private Map<Object, Object> data;

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the header
	 **/
	public Header getHeader()
	{
		return header;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param header the header to set
	 **/
	public void setHeader(Header header)
	{
		this.header = header;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the data
	 **/
	public Map<Object, Object> getData()
	{
		return data;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param data the data to set
	 **/
	public void setData(Map<Object, Object> data)
	{
		this.data = data;
	}	
}
