/*
 * ObjectBuilderFactory.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.transformer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 * 
 **/
public class ObjectBuilderFactory
	extends AbstractFactory
{
	/**
	 * DOCUMENT ME!
	 *
	 * @param context DOCUMENT ME!
	 * @param pointer DOCUMENT ME!
	 * @param parent DOCUMENT ME!
	 * @param name DOCUMENT ME!
	 * @param index DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@SuppressWarnings({"unchecked",
		"rawtypes"
	})
	public boolean createObject(JXPathContext context,
	                            Pointer       pointer,
	                            Object        parent,
	                            String        name,
	                            int           index)
	{
		if (parent instanceof Map)
		{
			if ("payload".equals(name))
			{
				((Map)parent).put(name, new ArrayList<Object>());
			}
			else
			{
				((Map)parent).put(name, new LinkedHashMap<String, Object>());
			}

			return true;
		}

		if (parent instanceof List)
		{
			((List)parent).add(new LinkedHashMap<String, Object>());
			return true;
		}

		return false;
	}
}
