/*
 * Version.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.entity;

import java.io.Serializable;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.Property;

/**
 * 
 **/
public class Version
	implements Serializable
{
	private static final long serialVersionUID = 8659292572661625722L;
	private String            id;
	@Property
	private String            name;
	@Property
	private Integer           majorVersion;
	@Property
	private Integer           minorVersion;
	private boolean allowNull;
	
	private String status;
	
	private String modelType;

	/**
	 * DOCUMENT ME!
	 *
	 * @return the name
	 **/
	public String getName()
	{
		return name;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param name the name to set
	 **/
	public void setName(String name)
	{
		this.name                              = name;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the majorVersion
	 **/
	public Integer getMajorVersion()
	{
		return majorVersion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param majorVersion the majorVersion to set
	 **/
	public void setMajorVersion(Integer majorVersion)
	{
		this.majorVersion = majorVersion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the minorVersion
	 **/
	public Integer getMinorVersion()
	{
		return minorVersion;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param minorVersion the minorVersion to set
	 **/
	public void setMinorVersion(Integer minorVersion)
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
	 * @return the id
	 **/
	public String getId()
	{
		return id;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param id the id to set
	 **/
	public void setId(String id)
	{
		this.id = id;
	}

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
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

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
    
}
