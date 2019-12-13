/*
 * RNumeric.java
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

/**
 * JSON representation:
 * 
 * "payload":[
      {
         "fieldName":"inPrimitive",
         "sequence":1,
         "dataType":"numeric",
         "collection":false,
         "value":-4.674
      }
   ]

   R representation:
   		<numeric>
 * **/
public class RNumeric
extends AbstractRType
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RNumeric.class);
    private Long longValue;
	private Double dblValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;

	/**
	 * Creates a new RNumeric object.
	 *
	 * @param dblValue
	 **/
	public RNumeric(final Double dblValue)
	{
		super();
		this.dblValue = dblValue;
	}

    public RNumeric(final Long longValue)
    {
        super();
        this.longValue = longValue;
    }

    public RNumeric(final BigInteger bigIntegerValue)
    {
        super();
        this.bigIntegerValue = bigIntegerValue;
    }

    public RNumeric(final BigDecimal bigDecimalValue)
    {
        super();
        this.bigDecimalValue = bigDecimalValue;
    }

	/**
	 * Creates a new RNumeric object.
	 *
	 * @param output - REXP returned from REngine.eval
	 **/
	public RNumeric(final REXP output)
	{
		super();
		LOGGER.debug("In Rnumeric : "+output);
        if (Double.isNaN(output.asDouble())) {
            dblValue = null;//NOPMD
        } else {
			dblValue = output.asDouble();
        }
		
	}

	/**
	 * Returns string R representation
	 *
	 * @return String
	 **/
	@Override
	public String toNative()
	{
        String nativeNumeric = "NA_real_";
		if (dblValue != null){
            nativeNumeric = dblValue.toString();
		} else if(bigIntegerValue != null) {
            nativeNumeric = bigIntegerValue.toString();
        } else if(bigDecimalValue != null) {
            nativeNumeric = bigDecimalValue.toPlainString();
        } else if(longValue != null) {
            nativeNumeric = longValue.toString();
        }
        return nativeNumeric;
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
	 * Returns "numeric"
	 *
	 * @return "numeric"
	 **/
	@Override
	public RDataTypes getRDataType()
	{
		return RDataTypes.R_NUMERIC;
	}

	/**
	 * Returns double
	 *
	 * @return Double
	 **/
	@Override
	public Object getPrimitive()	{
        Object primitiveVal = null;
        if (dblValue != null){
            primitiveVal = dblValue;
        } else if(bigIntegerValue != null) {
            primitiveVal = bigIntegerValue;
        } else if(bigDecimalValue != null) {
            primitiveVal = bigDecimalValue;
        } else if(longValue != null) {
            primitiveVal = longValue;
        }
		return primitiveVal;
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
        fi.setNativeDataType(RDataTypes.R_NUMERIC.getName());
		fi.setSequence(sequence);
        if (dblValue != null){
            fi.setValue(dblValue);
            fi.setDataType(DataType.DOUBLE.getUmgType());
        } else if(bigIntegerValue != null) {
            fi.setDataType(DataType.BIGINTEGER.getUmgType());
            fi.setValue(bigIntegerValue);
        } else if(bigDecimalValue != null) {
            fi.setDataType(DataType.BIGDECIMAL.getUmgType());
            fi.setValue(bigDecimalValue);
        } else if(longValue != null) {
            fi.setDataType(DataType.LONG.getUmgType());
            fi.setValue(longValue);
        }
		return fi;
	}

	@Override
	public String toString() {
        String response = null;
        if(dblValue != null) {
            response = Double.toString(dblValue);
        } else if(bigIntegerValue != null) {
            response = bigIntegerValue.toString();
        } else if(bigDecimalValue != null) {
            response = bigDecimalValue.toPlainString();
        } else {
            response = longValue.toString();
        }
        return response;
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
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(RDataTypes.R_NUMERIC.getName()).getIntValue());
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(DataType.DOUBLE.getUmgType()).getIntValue());
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
        if(dblValue != null) {
            parameterList.set(FieldInfoEnum.VALUE.getIndex(), dblValue);
        } else if(bigIntegerValue != null) {
            parameterList.set(FieldInfoEnum.VALUE.getIndex(), bigIntegerValue);
        } else if(bigDecimalValue != null) {
            parameterList.set(FieldInfoEnum.VALUE.getIndex(), bigDecimalValue);
        } else {
            parameterList.set(FieldInfoEnum.VALUE.getIndex(), longValue);
        }
		return newElement;
	}
}
