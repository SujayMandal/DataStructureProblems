/*
 * Query.java
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
public class Query
{
	private String  name;
	private Integer sequence;
	private String  sql;
	private boolean multipleRow;
	private boolean array;

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
	 * @return the sequence
	 **/
	public Integer getSequence()
	{
		return sequence;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param sequence the sequence to set
	 **/
	public void setSequence(Integer sequence)
	{
		this.sequence = sequence;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the sql
	 **/
	public String getSql()
	{
		return sql;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param sql the sql to set
	 **/
	public void setSql(String sql)
	{
		this.sql = sql;
	}

    /**
     * @return the isMultipleRow
     */
    public boolean isMultipleRow() {
        return multipleRow;
    }

    /**
     * @param isMultipleRow the isMultipleRow to set
     */
    public void setMultipleRow(boolean multipleRow) {
        this.multipleRow = multipleRow;
    }

    /**
     * @return the isArray
     */
    public boolean isArray() {
        return array;
    }

    /**
     * @param isArray the isArray to set
     */
    public void setArray(boolean array) {
        this.array = array;
    }
}
