/*
 * DeploymentDescriptor.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.deployment.info;

import java.io.Serializable;

/**
 * 
 **/
public class DeploymentDescriptor
	implements Serializable
{
	/**  */
	private static final long serialVersionUID = 7853894894608466407L;
	private String name;
	private int    minorVersion;
	private int    majorVersion;

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
	 * @return the minorVersion
	 **/
	public int getMinorVersion()
	{
		return minorVersion;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param minorVersion the minorVersion to set
	 **/
	public void setMinorVersion(int minorVersion)
	{
		this.minorVersion = minorVersion;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the majorVersion
	 **/
	public int getMajorVersion()
	{
		return majorVersion;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param majorVersion the majorVersion to set
	 **/
	public void setMajorVersion(int majorVersion)
	{
		this.majorVersion = majorVersion;
	}
}
