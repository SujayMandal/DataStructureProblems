/*
 * ArrayRowMapper.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * **/
public class ArrayRowMapper
	implements RowMapper<Object>
{
	/**
	 * DOCUMENT ME!
	 *
	 * @param rs DOCUMENT ME!
	 * @param rowNum DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws SQLException DOCUMENT ME!
	 **/
	@Override
	public Object mapRow(ResultSet rs,
	                     int       rowNum)
	  throws SQLException
	{
		int count = rs.getMetaData().getColumnCount();

		if (count > 1)//NOPMD
		{
			List<Object> row = new LinkedList<Object>();

			for (int columnIndex = 1; columnIndex <= count; columnIndex++)
			{
				Object obj = getObjectValue(rs, columnIndex);
				row.add(obj);
			}

			return row;
		}
		else
		{
			return getObjectValue(rs, 1);
		}
	}
	
	private Object getObjectValue(final ResultSet rs, final int columnIndex) throws SQLException{
		Object value = rs.getObject(columnIndex);
		if (value instanceof BigDecimal) {
			value = ((BigDecimal) value).doubleValue();
		}
		return value;
	}
}
