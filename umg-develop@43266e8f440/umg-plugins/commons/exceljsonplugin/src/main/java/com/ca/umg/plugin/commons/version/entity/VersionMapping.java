/*
 * VersionMapping.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.plugin.commons.version.entity;

/**
 * 
 **/
public class VersionMapping
{
	private Version     version;
	private String      name;
	private byte[]      modelIoData;
	private MappingData input;
	private MappingData output;

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
	 * @return the input
	 **/
	public MappingData getInput()
	{
		return input;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param input the input to set
	 **/
	public void setInput(MappingData input)
	{
		this.input = input;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the output
	 **/
	public MappingData getOutput()
	{
		return output;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param output the output to set
	 **/
	public void setOutput(MappingData output)
	{
		this.output = output;
	}

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
		this.name = name;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the modelIoData
	 **/
	public byte[] getModelIoData()
	{
		return modelIoData;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param modelIoData the modelIoData to set
	 **/
	public void setModelIoData(byte[] modelIoData)
	{
		this.modelIoData = modelIoData;
	}
}
