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

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.ioreduce.BooleanValueEnum;
import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.lang.type.LangTypeConstants;
import org.apache.commons.lang.StringUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

/**
 * JSON representation: HashMap : Required Keys - real, imaginary, rDataType
 * 
 * "payload":[ { "fieldName":"inPrimitive", "sequence":1, "dataType":"complex",
 * "collection":false, "value": { "real":10.3, "imaginary":-4.34,
 * "rDataType":"complex" } }
 * 
 * R representation: Usage: complex(length.out = 0, real = numeric(), imaginary
 * = numeric(), modulus = 1, argument = 0)
 * 
 * Currently supported: real, imaginary. All other params default
 * 
 * Eg: complex(real=5, imaginary=-29.3)
 * 
 **/
@SuppressWarnings("PMD")
public class RFactor extends AbstractRType {
	private static final Logger LOGGER = LoggerFactory.getLogger(RFactor.class);
	private RVector vData;
	private RVector vLabels;

	/**
	 * Creates a new RComplex object.
	 *
	 * @param dblReal
	 *            - real component of Complex number
	 * @param dblImaginary
	 *            - imaginary component of Complex number
	 **/
	// public RFactor(List<Object> lstData,
	// List<String> lstLabels) throws BusinessException
	// {
	// super();
	// if (lstData == null || lstData.size() == 0) {
	// throw new BusinessException(ModeletExceptionCodes.MOBE000002,
	// new String[]
	// {
	// RDataTypes.R_FACTOR.getName(),
	// LangTypeConstants.R_FACTOR_DATA
	// });
	// }
	// this.lstData = lstData;
	// this.lstLabels = lstLabels;
	// }

	/**
	 * Creates a new RComplex object from plain object representation.
	 *
	 * @param hmValue
	 *            - HashMap
	 *
	 * @throws BusinessException
	 *             - if given hmValue map contains wrong rDataType - if given
	 *             hmValue map is null - if either real or imaginary keys return
	 *             null objects - if either real or imaginary keys return
	 *             malformed numbers
	 * 
	 **/
	public RFactor(List<Object> hmValue) throws BusinessException {
		super();

		if (hmValue != null) {
			Object oData = null; // hmValue.get(LangTypeConstants.R_DATA_TYPE);
			Object oLabels = null;
			String dataType = null;

			for (Object objAttribute : hmValue) {
				HashMap<String, Object> attribute = (HashMap<String, Object>) objAttribute;
				if (attribute.get("modelParameterName").equals(
						LangTypeConstants.R_FACTOR_DATA)) {
					oData = attribute.get("value");
					dataType = attribute.get("dataType").toString();
				} else if (attribute.get("modelParameterName").equals(
						LangTypeConstants.R_FACTOR_LABELS)) {
					oLabels = attribute.get("value");
				}
			}

			if (oData instanceof List<?>) {

				List<Object> objs = (List<Object>) oData;

				vData = new RVector(objs,
						RDataTypes.getTypeEnumFromName(dataType));

				if (oLabels != null) {
					if (oLabels instanceof List<?>) {
						if (((List) oLabels).size() > 0) {
							vLabels = new RVector((List<Object>) oLabels,
									RDataTypes.R_CHARACTER);
						}
					} else {
						LOGGER.debug("In RFactor oLabels is null");
						throw new BusinessException(
								ModeletExceptionCodes.MOBE000002, new String[] {
										getClass().getName(),
										LangTypeConstants.R_FACTOR_LABELS });
					}
				}
			} else {
				LOGGER.debug("In RFactor odata not instance of list");
				throw new BusinessException(ModeletExceptionCodes.MOBE000002,
						new String[] { getClass().getName(),
								LangTypeConstants.R_FACTOR_DATA });
			}

		} else {
			LOGGER.debug("In RFactor hmvalue is null");
			throw new BusinessException(ModeletExceptionCodes.MOBE000002,
					new String[] { getClass().getName(), "FieldInfo value" });
		}
	}

	public RFactor(REXP output) {
		try {
			org.rosuda.REngine.RFactor factor = output.asFactor();
			// MS: bad way of doing this but JRI leaves us no choice

			// not checking for this because JRI always sends these attributes
			// if (stringRep.indexOf("levels=") != -1 &&
			// stringRep.indexOf("ids=") != -1) {

			List<Object> lstValues = new ArrayList<Object>();
			List<Object> lstLevels = new ArrayList<Object>();

			int[] idsArray = factor.asIntegers();
			String[] levelsArray = factor.levels();
			for (int i = 0; i < idsArray.length; i++) {
				// 2147483647 represents null in R, hence we are setting NA
				// as vector element if we encounter this value.
				if (idsArray[i] == -2147483648) {
					lstValues.add("NA");
				} else {
					lstValues.add(levelsArray[idsArray[i]-1]);
				}

			}

			for (int i = 0; i < levelsArray.length; i++) {
				lstLevels.add(levelsArray[i]);
			}

			try {
				this.vData = new RVector(lstValues, RDataTypes.R_CHARACTER);
				this.vLabels = new RVector(lstLevels, RDataTypes.R_CHARACTER);
			} catch (BusinessException be) {
				LOGGER.debug("Wrong datatype in vector - this should NEVER happen in this case so we can safely log and ignore");
			}

			// }
		} catch (REXPMismatchException e) {
			LOGGER.error("Type mismatch", e);
		}

	}

	public RFactor(org.rosuda.JRI.REXP output) {
		org.rosuda.JRI.RFactor factor = output.asFactor();
		// MS: bad way of doing this but JRI leaves us no choice
		String stringRep = factor.toString();

		// not checking for this because JRI always sends these attributes
		// if (stringRep.indexOf("levels=") != -1 && stringRep.indexOf("ids=")
		// != -1) {

		int levelPos = stringRep.indexOf("levels=");
		int idsPos = stringRep.indexOf("ids=");

		int endOfLevelsPos = stringRep.indexOf(")", levelPos);
		int endOfIdsPos = stringRep.indexOf(")", idsPos);

		String levels = stringRep.substring(levelPos + 8, endOfLevelsPos);
		String ids = stringRep.substring(idsPos + 5, endOfIdsPos);

		String[] levelsArray = levels.split(",");
		String[] idsArray = ids.split(",");
		List<Object> lstValues = new ArrayList<Object>();
		List<Object> lstLevels = new ArrayList<Object>();

		if (StringUtils.isNotEmpty(levels)) {
			for (int i = 0; i < levelsArray.length; i++) {
				levelsArray[i] = levelsArray[i].replaceAll("\"", "");
			}

			for (int i = 0; i < idsArray.length; i++) {
				// 2147483647 represents null in R, hence we are setting NA as
				// vector element if we encounter this value.
				if (Integer.parseInt(idsArray[i]) == 2147483647) {
					lstValues.add("NA");
				} else {
					lstValues.add(levelsArray[Integer.parseInt(idsArray[i])]);
				}
			}

			for (int i = 0; i < levelsArray.length; i++) {
				lstLevels.add(levelsArray[i]);
			}
		}
		try {
			this.vData = new RVector(lstValues, RDataTypes.R_CHARACTER);
			this.vLabels = new RVector(lstLevels, RDataTypes.R_CHARACTER);
		} catch (BusinessException be) {
			LOGGER.debug("Wrong datatype in vector - this should NEVER happen in this case so we can safely log and ignore");
		}

		// }

	}

	/**
	 * Returns String representation of native R Complex object
	 * 
	 * @return String
	 **/
	@Override
	public String toNative() {
		StringBuffer s = new StringBuffer(getRDataType().getName() + "( "
				+ vData.toNative());

		if (vLabels != null) {
			s.append(", labels=" + vLabels.toNative());

		}

		s.append(")");

		return s.toString();
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
	public boolean isPrimitive() {
		return false;
	}

	/**
	 * Returns "complex"
	 *
	 * @return "complex"
	 **/
	@Override
	public RDataTypes getRDataType() {
		return RDataTypes.R_FACTOR;
	}

	@Override
	public FieldInfo toUmgType(String name, String sequence) {

		FieldInfo fi = new FieldInfo();
		fi.setCollection(false);
		fi.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
		fi.setModelParameterName(name);
		fi.setSequence(sequence);
		fi.setNativeDataType(getRDataType().getName());

		ArrayList<Object> alValues = new ArrayList<Object>();
		alValues.add(vData.toUmgType(LangTypeConstants.R_FACTOR_DATA, "1"));

		if (vLabels != null) {
			alValues.add(vLabels.toUmgType(LangTypeConstants.R_FACTOR_LABELS,
					"2"));
		}
		fi.setValue(alValues.toArray());

		return fi;

	}

	@Override
	public Object getPrimitive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> toUmgType1(String name, String sequence) {

		final Map<String, Object> newElement = new HashMap<String, Object>();

		final List<Object> parameterList = new ArrayList<Object>(
				FieldInfoEnum.values().length);

		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);

		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), name);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), sequence);
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(),
				BooleanValueEnum.TRUE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(), null);

		parameterList.set(
				FieldInfoEnum.DATA_TYPE.getIndex(),
				getDataTypeValueEnum(
						com.ca.umg.modelet.common.DataType.OBJECT.getUmgType())
						.getIntValue());
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(),
				getNativeDataTypeValueEnum(getRDataType().getName())
						.getIntValue());

		ArrayList<Object> alValues = new ArrayList<Object>();
		alValues.add(vData.toUmgType1(LangTypeConstants.R_FACTOR_DATA, "1"));

		if (vLabels != null) {
			alValues.add(vLabels.toUmgType1(LangTypeConstants.R_FACTOR_LABELS,
					"2"));
		}

		parameterList.set(FieldInfoEnum.VALUE.getIndex(), alValues.toArray());

		return newElement;
	}
}
