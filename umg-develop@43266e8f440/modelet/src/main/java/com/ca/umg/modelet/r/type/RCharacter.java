/*
 * RCharacter.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.rosuda.JRI.REXP;

import com.ca.framework.core.ioreduce.BooleanValueEnum;
import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.DataType;
import com.ca.umg.modelet.common.FieldInfo;

/**
 * JSON representation:
 * "payload":[
 *      {
 *        "fieldName":"inPrimitive",
 *        "sequence":1,
 *        "dataType":"character",
 *        "collection":false,
 *        "value":"this is a test"
 *     }]
 * 
 *  R representation:
 *  		"this is a test"
 **/
public class RCharacter
extends AbstractRType
{
	private String strValue;

	/**
	 * Creates a new RCharacter object from String.
	 *
	 * @param strValue
	 **/
	public RCharacter(final String strValue)
	{
		super();
        String tempStrValue = strValue;

		// UMG-9723 Start, replace \\ with empty to avoid model crash.
		if (StringUtils.contains(tempStrValue, "\\")) {
			tempStrValue = StringUtils.replaceChars(tempStrValue, "\\", " ");
		}
		//UMG-9723 Ends

		if (StringUtils.contains(tempStrValue, "\"")) {
			tempStrValue = StringUtils.replacePattern(tempStrValue, "\"", "\\\\\"");
		}

        if (tempStrValue != null && !tempStrValue.equals("NA") && !tempStrValue.equals("NA_character_")) {
        	this.strValue = tempStrValue;
        } else {
        	this.strValue = null;
        }
	}

	/**
	 * Creates a new RCharacter object from REXP output - for unmarshalling from REngine to Java.
	 *
	 * @param output - REXP
	 **/
	public RCharacter(final REXP output)
	{
		super();

		// if NA is returned, treat it as null
        if (output != null && output.asString() != null && !output.asString().equals("NA") && !output.asString().equals("NA_character_")) {
            strValue = output.asString();
        }
	}

	/**
	 * Returns native representation of R character data type - "<string>"
	 *
	 * @return String
	 **/
	@Override
	public String toNative()
	{
        String nativeString = "NA_character_";
        if (strValue != null) {
            nativeString = "\"" + strValue + "\"";
        }
        return nativeString;
	}

	/**
	 * returns String object
	 *
	 * @return String
	 **/

	/**
	 * Indicates if this is a primitive type
	 *
	 * @return true
	 **/
	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	/**
	 * Returns enum for RDataTypes
	 *
	 * @return "character"
	 **/
	@Override
	public RDataTypes getRDataType()
	{
		return RDataTypes.R_CHARACTER;
	}

	@Override
	public Object getPrimitive()	{
		return strValue;
	}

	@Override
	public FieldInfo toUmgType(final String name, final String sequence)	{
		FieldInfo fi = new FieldInfo();
		fi.setCollection(false);
		fi.setDataType(DataType.STRING.getUmgType());
		fi.setModelParameterName(name);
        fi.setNativeDataType(RDataTypes.R_CHARACTER.getName());
		fi.setSequence(sequence);
		fi.setValue(strValue);
		return fi;
	}

	@Override
	public String toString() {
		return strValue;
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
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(RDataTypes.R_CHARACTER.getName()).getIntValue());
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(DataType.STRING.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), strValue);	

		return newElement;
	}
}
