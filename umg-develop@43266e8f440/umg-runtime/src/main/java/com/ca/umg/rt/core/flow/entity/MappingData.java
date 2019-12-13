/*
 * MappingData.java
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
 **/
public class MappingData
{
	private byte[] mapping;
	private byte[] tid;

	/**
	 * DOCUMENT ME!
	 *
	 * @return the mapping
	 **/
	public byte[] getMapping()
	{
		return mapping;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param mapping the mapping to set
	 **/
	public void setMapping(byte[] mapping)
	{
		this.mapping = mapping;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the tid
	 **/
	public byte[] getTid()
	{
		return tid;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param tid the tid to set
	 **/
	public void setTid(byte[] tid)
	{
		this.tid = tid;
	}
}
