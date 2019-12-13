/*
 * RLogical.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * 
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.r.type;

import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;
import static java.lang.Boolean.valueOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rosuda.JRI.RBool;
import org.rosuda.JRI.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.ioreduce.BooleanValueEnum;
import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.DataType;
import com.ca.umg.modelet.common.FieldInfo;

/**
 * JSON representation: "payload":[ { "fieldName":"inPrimitive", "sequence":1,
 * "dataType":"logical", "collection":false, "value":false } ]
 * 
 * R Representation: (TRUE) or (FALSE) or NA
 * 
 * **/

@SuppressWarnings("PMD")
public class RLogical
extends AbstractRType
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RLogical.class);
	private final Boolean blnValue;

	/**
	 * Creates a new RLogical object.
	 *
	 * @param blnValue
	 **/
	public RLogical(final Object blnValue)
	{
		super();
		if (blnValue != null) {
			this.blnValue = valueOf(blnValue.toString());
		} else {
			this.blnValue = null;
		}
	}

	/**
	 * Creates a new RLogical object.
	 *
	 * @param output REXP
	 **/
	public RLogical(final REXP output)
	{
		super();
		LOGGER.debug("Entered RLogical output is {}  : ",output);
		if (output != null) {
			final boolean value = output.asBool().isTRUE();
			if (value)
				blnValue = value;
			else	{
				if (output.asBool().isNA())
					blnValue = null;
				else
					blnValue = false;
			}
		} else {
			blnValue = null;
		}

	}

	/**
	 * Returns Native representation
	 *
	 * @return String
	 **/
	@Override
    public RBool toNative()
	{  
		if(blnValue!=null){		
			return new RBool(blnValue);
		}else{
		      return new RBool(2) ;
		}
		
		
	}

	/**
	 * Returns true
	 *
	 * @return True
	 **/
	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	/**
	 * Returns "logical"
	 *
	 * @return "logical"
	 **/
	@Override
	public RDataTypes getRDataType()
	{
		return RDataTypes.R_LOGICAL;
	}

	/**
	 * Returns boolean
	 *
	 * @return Boolean
	 **/
	@Override
	public Object getPrimitive()	{
		return blnValue;
	}

	/**
	 * Returns FieldInfo
	 *
	 * @return FieldInfo
	 **/
	@Override
	public FieldInfo toUmgType(final String name, final String sequence)	{
		final FieldInfo fi = new FieldInfo();
		fi.setCollection(false);
		fi.setDataType(DataType.BOOLEAN.getUmgType());
		fi.setModelParameterName(name);
        fi.setNativeDataType(RDataTypes.R_LOGICAL.getName());
		fi.setSequence(sequence);
		fi.setValue(blnValue);
		return fi;
	}

	@Override
	public String toString() {
		return getPrimitive().toString();
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public Map<String, Object> toUmgType1(final String name, final String sequence)	{
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), name);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), sequence);
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(RDataTypes.R_LOGICAL.getName()).getIntValue());
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(DataType.BOOLEAN.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), blnValue);	

		return newElement;
	}
}
