/*
 * VersionModelLibrary.java
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
public class VersionModelLibrary
{
	private Version      version;
	private ModelLibrary modelLibrary;

	/**
	 * DOCUMENT ME!
	 *
	 * @return the version
	 **/
	public Version getVersion()
	{
		return version;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param version the version to set
	 **/
	public void setVersion(Version version)
	{
		this.version = version;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the modelLibrary
	 **/
	public ModelLibrary getModelLibrary()
	{
		return modelLibrary;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param modelLibrary the modelLibrary to set
	 **/
	public void setModelLibrary(ModelLibrary modelLibrary)
	{
		this.modelLibrary = modelLibrary;
	}
}
