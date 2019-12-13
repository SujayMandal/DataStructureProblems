/*
 * RComplex.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.r.type;

import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;
import static com.ca.umg.modelet.lang.type.LangTypeConstants.R_COMPLEX_IMAGINARY;
import static com.ca.umg.modelet.lang.type.LangTypeConstants.R_COMPLEX_REAL;
import static java.lang.String.valueOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.ioreduce.BooleanValueEnum;
import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.lang.type.LangTypeConstants;

/**
 * JSON representation:
 * 	HashMap : Required Keys - real, imaginary, rDataType
 * 
 * "payload":[
 * 		{   "fieldName":"inPrimitive",
 * 			"sequence":1,
 * 			"dataType":"complex",
 * 			"collection":false,
 * 			"value": {
 * 					"real":10.3,
 * 					"imaginary":-4.34,
 * 					"rDataType":"complex"
 * 			}
 * 		}
 * 
 * R representation:
 * 	Usage: complex(length.out = 0, real = numeric(), imaginary = numeric(), modulus = 1, argument = 0)
 * 
 * 	Currently supported: real, imaginary. All other params default
 * 
 *  Eg: complex(real=5, imaginary=-29.3)
 * 
 **/
@SuppressWarnings("PMD")
public class RComplex
extends AbstractRType
{
	private static final Logger LOGGER       = LoggerFactory.getLogger(RComplex.class);
	private final double              dblReal;
	private final double              dblImaginary;

	/**
	 * Creates a new RComplex object.
	 *
	 * @param dblReal - real component of Complex number
	 * @param dblImaginary - imaginary component of Complex number
	 **/
	public RComplex(final double dblReal,
			final double dblImaginary)
	{
		super();
		this.dblReal                         = dblReal;
		this.dblImaginary                    = dblImaginary;
	}

	/**
	 * Creates a new RComplex object from plain object representation.
	 *
	 * @param hmValue - HashMap
	 *
	 * @throws BusinessException
	 *  - if given hmValue map contains wrong rDataType
	 *  - if given hmValue map is null
	 *  - if either real or imaginary keys return null objects
	 *  - if either real or imaginary keys return malformed numbers
	 * 
	 **/
	public RComplex(final Map<String, Object> hmValue)
			throws BusinessException
			{
		super();

		if (hmValue != null)
		{
			Object oType = hmValue.get(LangTypeConstants.R_DATA_TYPE);

			if (oType instanceof String &&
					oType.equals(getRDataType().getName()))
			{
				Object oReal      = hmValue.get(LangTypeConstants.R_COMPLEX_REAL);
				Object oImaginary = hmValue.get(LangTypeConstants.R_COMPLEX_IMAGINARY);

				if (oReal == null && oImaginary == null)
				{
				    LOGGER.debug("In RComplex oReal & oImaginary are null");
					throw new BusinessException(ModeletExceptionCodes.MOBE000002,
							new String[]
									{
							getClass().getName(),
							LangTypeConstants.R_COMPLEX_REAL + "/" +
									LangTypeConstants.R_COMPLEX_IMAGINARY
									});
				}
				else
				{
					try
					{
						dblReal      = Double.valueOf(oReal.toString());
						dblImaginary = Double.valueOf(oImaginary.toString());
					}
					catch (NumberFormatException nfe)
					{
						LOGGER.debug("In RComplex Real or double number format exception");
						throw new BusinessException(ModeletExceptionCodes.MOBE000002,
								new String[]
										{
								getClass().getName(),
								LangTypeConstants.R_COMPLEX_REAL + "/" +
										LangTypeConstants.R_COMPLEX_IMAGINARY
										},nfe );
					}
				}
			}
			else
			{
			    LOGGER.debug("In RComplex oReal & oImaginary are null");
				throw new BusinessException(ModeletExceptionCodes.MOBE000002,
						new String[] {getClass().getName(), LangTypeConstants.R_DATA_TYPE });
			}
		}
		else
		{
		    LOGGER.debug("In RComplex hmvalue is null");
			throw new BusinessException(ModeletExceptionCodes.MOBE000002,
					new String[] { getClass().getName(), "HashMap" });
		}
			}

	/**
	 * Returns String representation of native R Complex object
	 * @return String
	 **/
	@Override
	public String toNative()
	{
		return getRDataType().getName() + "(real=" + dblReal + ",imaginary=" + dblImaginary + ")";
	}

	/**
	 * Returns plain object representation
	 *
	 * @return HashMap
	 **/

	/**
	 * Returns true
	 *
	 * @return true as this is a primitive type
	 **/
	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	/**
	 * Returns "complex"
	 *
	 * @return "complex"
	 **/
	@Override
	public RDataTypes getRDataType()
	{
		return RDataTypes.R_COMPLEX;
	}

	@Override
	public FieldInfo toUmgType(final String name, final String sequence) {
		final FieldInfo fi = new FieldInfo();
		fi.setCollection(false);
		fi.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
		fi.setModelParameterName(name);
		fi.setSequence(sequence);
		fi.setNativeDataType(getRDataType().getName());

		final ArrayList<Object> alValues = new ArrayList<Object>();

		FieldInfo f = new FieldInfo();
		f.setCollection(false);
		f.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
		f.setModelParameterName(LangTypeConstants.R_COMPLEX_REAL);
		f.setSequence("1");
		f.setValue(valueOf(dblReal));

		alValues.add(f);

		f = new FieldInfo();
		f.setCollection(false);
		f.setDataType(com.ca.umg.modelet.common.DataType.STRING.getUmgType());
		f.setModelParameterName(LangTypeConstants.R_COMPLEX_IMAGINARY);
		f.setSequence("2");
		f.setValue(valueOf(dblImaginary));

		alValues.add(f);
		fi.setValue(alValues.toArray());
		return fi;
	}

	@Override
	public Object getPrimitive() {
		// TODO Auto-generated method stub
		return null;
	}

	public RComplex(final List<Object> hmValue) throws BusinessException {
		super();

		if (hmValue != null) {
			HashMap<String, Object> attribute;
			Object oReal = null;
			Object oImaginary = null;

			for (final Object objAttribute : hmValue) {
				attribute = (HashMap<String, Object>) objAttribute;
				if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_COMPLEX_REAL)) {
					oReal = attribute.get("value");
				} else if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_COMPLEX_IMAGINARY)) {
					oImaginary = attribute.get("value");
				}
			}

			if (oReal == null && oImaginary == null) {
			    LOGGER.debug("In RComplex List<Object> oReal and oImaginary are null");
				throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), R_COMPLEX_REAL + "/" + R_COMPLEX_IMAGINARY });
			} else {
				try {
					dblReal = Double.valueOf(oReal.toString());
					dblImaginary = Double.valueOf(oImaginary.toString());
				} catch (NumberFormatException nfe) {
					LOGGER.debug("In RComplex List<Object>Real or double number format exception");
					throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
					        R_COMPLEX_REAL + "/" + R_COMPLEX_IMAGINARY }, nfe);
				}
			}
		} else {
		    LOGGER.debug("In RComplex List<Object> hmValue null");
			throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), LangTypeConstants.R_DATA_TYPE });
		}
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public Map<String, Object> toUmgType1(final String name, final String sequence) {
		
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), name);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), sequence);
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(getRDataType().getName()).getIntValue());
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);

		final ArrayList<Object> alValues = new ArrayList<Object>();
		alValues.add(createRealPart());
		alValues.add(createComplexPart());

		parameterList.set(FieldInfoEnum.VALUE.getIndex(), alValues.toArray());	
		
		return newElement;		
	}
	
	private Map<String, Object> createRealPart() {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_COMPLEX_REAL);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "1");
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), valueOf(dblReal));	

		return newElement;
	}

	private Map<String, Object> createComplexPart() {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_COMPLEX_IMAGINARY);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "2");
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.STRING.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), valueOf(dblImaginary));	

		return newElement;
	}

}
