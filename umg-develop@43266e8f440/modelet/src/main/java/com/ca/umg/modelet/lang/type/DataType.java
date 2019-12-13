/*
 * DataType.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.lang.type;

import java.util.Map;

import com.ca.umg.modelet.common.FieldInfo;

/**
 * Interface method for all Wrapper Native data type classes
 * **/
public interface DataType
{
	    /**
     * Currently implemented by R which requires String representation of incoming data types In the future can be extended to
     * Object return type for other languages
     * 
     * @param <T>
     *
     * @return Native String representation of the wrapper data type
     **/
    public <T extends Object> T toNative();

	/**
	 * Converts Native data type wrapper to a raw Object format (HashMaps etc)
	 * in order to return them in the outgoing JSON
	 *
	 * @return Plain Object representation of wrapper data type
	 **/
	public FieldInfo toUmgType(String name, String sequence);

	/**
	 * Relevant for primitives only, returns null for non primitives
	 * @return Plain Object representation of wrapper data type
	 **/
	public Object getPrimitive();

	/**
	 * Set to TRUE if this native data type is a primitive, FALSE if not
	 *
	 * @return boolean indicating if this data type is a primitive or complex data type
	 **/
	public boolean isPrimitive();

    public Map<String, Object> toUmgType1(String name, String sequence);
}
