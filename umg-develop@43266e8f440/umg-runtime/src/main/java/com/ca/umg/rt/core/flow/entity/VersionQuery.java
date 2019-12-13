/*
 * VersionQuery.java
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
public class VersionQuery
{
	private Version version;
	private Query   query;

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
	 * @return the query
	 **/
	public Query getQuery()
	{
		return query;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param query the query to set
	 **/
	public void setQuery(Query query)
	{
		this.query = query;
	}
}
