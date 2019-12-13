/*
 * VersionInfo.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.version;

import java.io.Serializable;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

/**
 * 
 **/
public class VersionInfo
	implements Serializable
{
	private static final long serialVersionUID = 6492139047402088905L;
	@Property
	private String            modelName;
	@Property
	private int               majorVersion;
	@Property
	private int               minorVersion;

	private transient String  status;
	
	@Property(policy=PojomaticPolicy.TO_STRING)
	private String modelType;

	/**
	 * Creates a new VersionInfo object.
	 *
	 * @param modelName DOCUMENT ME!
	 **/
	public VersionInfo(String modelName)
	{
		this.modelName    = modelName;
		this.majorVersion = 0;
		this.minorVersion = 0;
	}

	/**
	 * Creates a new VersionInfo object.
	 *
	 * @param modelName DOCUMENT ME!
	 * @param majorVersion DOCUMENT ME!
	 * @param minorVersion DOCUMENT ME!
	 **/
	public VersionInfo(String modelName,
	                   int    majorVersion,
	                   int    minorVersion)
	{
		this.modelName    = modelName;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	
	public VersionInfo(String modelName,
            int    majorVersion,
            int    minorVersion, String modelType)
    {
        this.modelName    = modelName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.modelType = modelType;
    }

	public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    /**
	 * DOCUMENT ME!
	 *
	 * @return the modelName
	 **/
	public String getModelName()
	{
		return modelName;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param modelName the modelName to set
	 **/
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the majorVersion
	 **/
	public int getMajorVersion()
	{
		return majorVersion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param majorVersion the majorVersion to set
	 **/
	public void setMajorVersion(int majorVersion)
	{
		this.majorVersion = majorVersion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the minorVersion
	 **/
	public int getMinorVersion()
	{
		return minorVersion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param minorVersion the minorVersion to set
	 **/
	public void setMinorVersion(int minorVersion)
	{
		this.minorVersion = minorVersion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public final String toString()
	{
		return Pojomatic.toString(this);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param obj DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public final boolean equals(Object obj)
	{
		return Pojomatic.equals(this, obj);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public final int hashCode()
	{
		return Pojomatic.hashCode(this);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public String getVersionString()
	{
		return this.modelName + "-" + majorVersion + "." + minorVersion;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
