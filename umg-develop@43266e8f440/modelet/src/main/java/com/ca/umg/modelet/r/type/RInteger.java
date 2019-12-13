/*
 * RInteger.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.r.type;

import com.ca.framework.core.ioreduce.BooleanValueEnum;
import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.DataType;
import com.ca.umg.modelet.common.FieldInfo;
import org.rosuda.JRI.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

/**
 * JSON Representation:
 * 	"payload":[
      {
         "fieldName":"inPrimitive",
         "sequence":1,
         "dataType":"integer",
         "collection":false,
         "value":6
      }

   R representation:
   		<intg>
 * **/
public class RInteger
extends AbstractRType
{
	private Integer intValue;

	private static final Logger LOGGER = LoggerFactory.getLogger(RMatrix.class);

	/**
	 * Creates a new RInteger object.
	 *
	 * @param intValue
	 **/
	public RInteger(final Integer intValue)
	{
		super();
		this.intValue = intValue;
	}

    /*public RInteger(final Long longValue)
    {
        super();
        isIntType = Boolean.FALSE;
        this.longValue = longValue;
    }*/

	/**
	 * Creates a new RInteger object.
	 *
	 * @param output - REXP
	 **/
	public RInteger(final REXP output)
	{
		super();
		LOGGER.debug("Entered RInteger output is {}  : ",output);
        if (output.asInt() == Integer.MIN_VALUE) {
            intValue = null;//NOPMD
        } else {
			intValue = output.asInt();
		}
	}

	/**
	 * Return string representation
	 *
	 * @return String
	 **/
	@Override
	public String toNative()
	{
        String nativeValue = "NA_integer_";
        if (intValue != null) {
            nativeValue = intValue.toString();
        }
        return nativeValue;
	}

	/**
	 * Returns plain Object representation
	 *
	 * @return Integer
	 **/
	@Override
	public Object getPrimitive()	{
        return intValue;
	}

	/**
	 * Returns FieldInfo representation
	 *
	 * @return FieldInfo
	 **/
	@Override
	public FieldInfo toUmgType(final String name, final String sequence)	{
		FieldInfo fi = new FieldInfo();
		fi.setCollection(false);
		fi.setModelParameterName(name);
        fi.setDataType(DataType.INTEGER.getUmgType());
        fi.setNativeDataType(RDataTypes.R_INTEGER.getName());
		fi.setSequence(sequence);
        if(intValue != null) {
            fi.setValue(intValue);
        }
		return fi;
	}

	/**
	 * Returns true
	 *
	 * @return true
	 **/
	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	/**
	 * Returns "integer"
	 *
	 * @return "integer"
	 **/
	@Override
	public RDataTypes getRDataType()
	{
		return RDataTypes.R_INTEGER;
	}

	@Override
	public String toString() {
		return Integer.toString(intValue);
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
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(RDataTypes.R_INTEGER.getName()).getIntValue());
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(DataType.INTEGER.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
        if(intValue != null) {
            parameterList.set(FieldInfoEnum.VALUE.getIndex(), intValue);
        }
		return newElement;
	}
}
