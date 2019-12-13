/*
 * AbstractRType.java
 * Author Name: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.r.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.rosuda.REngine.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.lang.type.DataType;

/**
 * Abstract Class implementing DataType with specific customization for R Data Types
 **/
@SuppressWarnings("PMD")
public abstract class AbstractRType implements DataType {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRType.class);

    private static final int DIM_ONE_D_ARRAY = 1;
    private static final int DIM_TWO_D_ARRAY = 2;

    /**
     * Abstract method for interface method DataType.toNative
     * 
     * @param <T>
     * 
     * @return String
     **/
    @Override
    public abstract <T> T toNative();

    /**
     * Abstract method for interface method DataType.toJava
     * 
     * @return Object
     **/
    @Override
    public abstract FieldInfo toUmgType(String name, String sequence);

    @Override
    public abstract Object getPrimitive();

    /**
     * Abstract method for interface method DataType.isPrimitive
     * 
     * @return boolean
     **/
    @Override
    public abstract boolean isPrimitive();

    /**
     * Abstract method for returning correct enumeration type associated with this wrapper R data type
     * 
     * @return RDataType enum
     **/
    public abstract RDataTypes getRDataType();

    /**
     * Static method to return AbstractRType from REXP - used for unmarshalling data coming from REngine
     * 
     * @param output
     *            - REXP returned from REngine.eval
     * 
     * @return AbstractRType - wrapper data type for REXP
     * 
     * @throws BusinessException
     **/

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    public static AbstractRType createRDataTypeFromREXP(final REXP output) throws BusinessException {
        // REXP is usually returned as null if there is an error in the native R
        // method evaluation
        LOGGER.debug("Entered createRDataTypeFromREXP output is : " + output);
        if (output == null) {
            throw new BusinessException(ModeletExceptionCodes.MOBE000004, null);
        }

        AbstractRType objType = null;

        // Selecting correct wrapper data type based on REXP type and Xt
        // (extended type) attributes
        if (output.isFactor()) {
            objType = new RFactor(output);
        } else if (output.isNumeric()) {
            LOGGER.debug("Rtype is REALSXP : Numeric");
            if (output.isVector()) {
                LOGGER.debug("In REALSXP - XT_ARRAY_DOUBLE : " + output);
                if (output.dim() == null) {
                    LOGGER.debug("In REALSXP - XT_ARRAY_DOUBLE - vector1D : " + output);
                    // Vector (1D array) of doubles
                    objType = new RVector(output, RDataTypes.R_NUMERIC);
                } else {
                    LOGGER.debug("In REALSXP - XT_ARRAY_DOUBLE - matrix2D : " + output);
                    // Matrix (2D array) of doubles - Assuming if asMatrix
                    // returns not null, then it is a well formed matrix
                    objType = new RMatrix(output, RDataTypes.R_NUMERIC);
                }
            }
        } else if (output.isInteger()) {
            LOGGER.debug("Rtype is INTSXP : Integer");
            if (output.isVector()) {
                LOGGER.debug("In REALSXP - XT_ARRAY_INT : " + output);
                if (output.dim() == null) {
                    LOGGER.debug("In REALSXP - XT_ARRAY_INT - vector1D : " + output);
                    // Vector (1D array) of doubles
                    objType = new RVector(output, RDataTypes.R_INTEGER);
                } else {
                    LOGGER.debug("In REALSXP - XT_ARRAY_INT - matrix2D : " + output);
                    // Matrix (2D array) of doubles - Assuming if asMatrix
                    // returns not null, then it is a well formed matrix
                    objType = new RMatrix(output, RDataTypes.R_INTEGER);
                }
            }
        } else if (output.isString()) {
            // LOGGER.debug("Rtype is STRSXP : " + output.rtype);
            LOGGER.debug("Rtype is STRSXP : String");
            if (output.isVector()) {
                LOGGER.debug("In STRSXP XT_ARRAY_STR : " + output);
                if (output.dim() == null) {
                    objType = new RVector(output, RDataTypes.R_CHARACTER);
                } else if (output.dim().length == 2) {
                    objType = new RMatrix(output, RDataTypes.R_CHARACTER);
                } else {
                    objType = new RArray(output, RDataTypes.R_CHARACTER);
                }
            }
        } else if (output.isLogical()) {
            // LOGGER.debug("Rtype is LGLSXP : " + output.rtype);
            LOGGER.debug("Rtype is LGLSXP : Logical");
            if (output.isVector()) {
                LOGGER.debug("In STRSXP XT_ARRAY_STR : " + output);
                if (output.dim() == null) {
                    objType = new RVector(output, RDataTypes.R_LOGICAL);
                } else if (output.dim().length == 2) {
                    objType = new RMatrix(output, RDataTypes.R_LOGICAL);
                } else {
                    objType = new RArray(output, RDataTypes.R_CHARACTER);
                }
            }
        } else if (output.isComplex()) {
            // LOGGER.debug("Rtype is CPLXSXP : " + output.rtype);
            LOGGER.debug("Rtype is CPLXSXP : complex");
            // Return type complex not supported yet
            throw new BusinessException(ModeletExceptionCodes.MOBE000003, new String[] { "R", RDataTypes.R_COMPLEX.getName() });
        } // else if (output.rtype == REXP.RAWSXP) {
        else if (output.isRaw()) {
            // LOGGER.debug("Rtype is RAWSXP : " + output.rtype);
            LOGGER.debug("Rtype is RAWSXP : raw");
            // Return type complex not supported yet
            throw new BusinessException(ModeletExceptionCodes.MOBE000003, new String[] { "R", RDataTypes.R_RAW.getName() });
        } // else if (output.getType() == REXP.XT_VECTOR) {
        else if (output.isVector()) {
            // LOGGER.debug("Rtype is XT_VECTOR : " + output.rtype);
            LOGGER.debug("Rtype is XT_VECTOR : Vector");
            // Assuming that a data frame without row names is considered a list
            if (output.getAttribute("row.names") != null) {
                LOGGER.debug("Rtype is XT_VECTOR - RDataFrame : " + output);
                objType = new RDataFrame(output);
            } else {
                LOGGER.debug("Rtype is XT_VECTOR - RList : " + output);
                objType = new RList(output);
            }
        }

        return objType;
    }

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    public static AbstractRType createRDataTypeFromREXP(final org.rosuda.JRI.REXP output) throws BusinessException {
        // REXP is usually returned as null if there is an error in the native R
        // method evaluation
        LOGGER.debug("Entered createRDataTypeFromREXP output is : " + output);
        if (output == null) {
            throw new BusinessException(ModeletExceptionCodes.MOBE000004, null);
        }

        AbstractRType objType = null;

        // Selecting correct wrapper data type based on REXP type and Xt
        // (extended type) attributes
        if (output.rtype == org.rosuda.JRI.REXP.REALSXP) {
            LOGGER.debug("Rtype is REALSXP : " + output.rtype);
            if (output.getType() == org.rosuda.JRI.REXP.XT_DOUBLE) {
                LOGGER.debug("In REALSXP - XT_DOUBLE : " + output);
                // Primitive double
                objType = new RNumeric(output);
            }

            else if (output.getType() == org.rosuda.JRI.REXP.XT_ARRAY_DOUBLE) {
                LOGGER.debug("In REALSXP - XT_ARRAY_DOUBLE : " + output);
                if (output.asMatrix() == null) {
                    LOGGER.debug("In REALSXP - XT_ARRAY_DOUBLE - vector1D : " + output);
                    // Vector (1D array) of doubles
                    objType = new RVector(output, RDataTypes.R_NUMERIC);
                } else {
                    LOGGER.debug("In REALSXP - XT_ARRAY_DOUBLE - matrix2D : " + output);
                    // Matrix (2D array) of doubles - Assuming if asMatrix
                    // returns not null, then it is a well formed matrix
                    objType = new RMatrix(output, RDataTypes.R_NUMERIC);
                }
            }
        } else if (output.rtype == org.rosuda.JRI.REXP.INTSXP) {
            LOGGER.debug("Rtype is INTSXP : " + output.rtype);
            if (output.getType() == org.rosuda.JRI.REXP.XT_INT) {
                LOGGER.debug("In INTSXP - XT_INT : " + output);
                // Primitive integer
                objType = new RInteger(output);
            } else if (output.getType() == org.rosuda.JRI.REXP.XT_ARRAY_INT) {
                LOGGER.debug("In INTSXP - XT_ARRAY_INT : " + output);
                if (output.asMatrix() == null) {
                    LOGGER.debug("In INTSXP - XT_ARRAY_INT - vector : " + output);
                    // Vector (1D array) of integers
                    objType = new RVector(output, RDataTypes.R_INTEGER);
                } else {
                    LOGGER.debug("In INTSXP - XT_ARRAY_INT - matrix : " + output);
                    // Matrix (2D array) of integers - Assuming if asMatrix
                    // returns not null, then it is a well formed matrix
                    objType = new RMatrix(output, RDataTypes.R_INTEGER);
                }
            } else if (output.getType() == org.rosuda.JRI.REXP.XT_FACTOR) {
                LOGGER.debug("In INTSXP XT_FACTOR : " + output.getType());
                objType = new RFactor(output);
            }
        } else if (output.rtype == org.rosuda.JRI.REXP.STRSXP) {
            LOGGER.debug("Rtype is STRSXP : " + output.rtype);
            if (output.getType() == org.rosuda.JRI.REXP.XT_STR) {
                LOGGER.debug("In STRSXP XT_STR : " + output);
                // Primitive String
                objType = new RCharacter(output);
            } else if (output.getType() == org.rosuda.JRI.REXP.XT_ARRAY_STR) {
                LOGGER.debug("In STRSXP XT_ARRAY_STR : " + output);
                // Type will be determined by number of dimensions in array
                org.rosuda.JRI.REXP dim = output.getAttribute("dim");

                if (dim != null && dim.getType() == org.rosuda.JRI.REXP.XT_ARRAY_INT) {
                    LOGGER.debug("In STRSXP - XT_ARRAY_STR - XT_ARRAY_INT : " + output);
                    // Indicates n-D array
                    int[] ds = dim.asIntArray();

                    if (ds == null || ds.length == DIM_ONE_D_ARRAY) {
                        LOGGER.debug("In STRSXP - XT_ARRAY_STR - XT_ARRAY_INT - DIM_ONE_D_ARRAY vector: " + output);
                        // Vector (1D array) of String - just checking, but in
                        // 1D vector dim is always null and will go to outer
                        // else
                        objType = new RVector(output, RDataTypes.R_CHARACTER);
                    }

                    else if (ds.length == DIM_TWO_D_ARRAY) {
                        LOGGER.debug("In STRSXP - XT_ARRAY_STR - XT_ARRAY_INT - DIM_TWO_D_ARRAY matrix: " + output);
                        // Matrix (2D array) of String
                        objType = new RMatrix(output, RDataTypes.R_CHARACTER);
                    }

                    else {
                        LOGGER.debug("In STRSXP - XT_ARRAY_STR - XT_ARRAY_INT - rArray: " + output);
                        // nD Array of String - not supported yet
                        objType = new RArray(output, RDataTypes.R_CHARACTER);
                    }
                } else {
                    LOGGER.debug("In STRSXP - XT_ARRAY_STR - XT_ARRAY_INT - rVector: " + output);
                    // Vector (1D array) of String
                    objType = new RVector(output, RDataTypes.R_CHARACTER);
                }
            }
        } else if (output.rtype == org.rosuda.JRI.REXP.LGLSXP) {
            LOGGER.debug("Rtype is LGLSXP : " + output.rtype);
            if (output.getType() == org.rosuda.JRI.REXP.XT_BOOL) {
                LOGGER.debug("In LGLSXP - XT_BOOL : " + output);
                // Primitive boolean
                objType = new RLogical(output);
            }

            else if (output.getType() == org.rosuda.JRI.REXP.XT_ARRAY_BOOL_INT) {
                LOGGER.debug("In LGLSXP - XT_ARRAY_BOOL_INT : " + output);

                // Type will be determined by number of dimensions in array
                org.rosuda.JRI.REXP dim = output.getAttribute("dim");

                if (dim != null && dim.getType() == org.rosuda.JRI.REXP.XT_ARRAY_INT) {
                    LOGGER.debug("In LGLSXP - XT_ARRAY_BOOL_INT - XT_ARRAY_INT : " + output);
                    // Indicates n-D Array
                    int[] ds = dim.asIntArray();

                    if (ds == null || ds.length == DIM_ONE_D_ARRAY) {
                        LOGGER.debug("In LGLSXP - XT_ARRAY_BOOL_INT - DIM_ONE_D_ARRAY - Rvector : " + output);
                        // Vector (1D array) of booleans - just checking, but in
                        // 1D vector dim is always null and will go to outer
                        // else
                        objType = new RVector(output, RDataTypes.R_LOGICAL);
                    }

                    else if (ds.length == DIM_TWO_D_ARRAY) {
                        LOGGER.debug("In LGLSXP - XT_ARRAY_BOOL_INT - DIM_TWO_D_ARRAY - Rmatrix : " + output);
                        // Matrix (2D array) of booleans
                        objType = new RMatrix(output, RDataTypes.R_LOGICAL);
                    }

                    else {
                        LOGGER.debug("In LGLSXP - XT_ARRAY_BOOL_INT - RArray : " + output);
                        // nD Array of booleans - not supported yet
                        objType = new RArray(output, RDataTypes.R_CHARACTER);
                    }
                } else {
                    LOGGER.debug("In LGLSXP - XT_ARRAY_BOOL_INT - RVector : " + output);
                    // Vector (1D array) of booleans
                    objType = new RVector(output, RDataTypes.R_LOGICAL);
                }
            }
        } else if (output.rtype == org.rosuda.JRI.REXP.CPLXSXP) {
            LOGGER.debug("Rtype is CPLXSXP : " + output.rtype);
            // Return type complex not supported yet
            throw new BusinessException(ModeletExceptionCodes.MOBE000003, new String[] { "R", RDataTypes.R_COMPLEX.getName() });
        } else if (output.rtype == org.rosuda.JRI.REXP.RAWSXP) {
            LOGGER.debug("Rtype is RAWSXP : " + output.rtype);
            // Return type complex not supported yet
            throw new BusinessException(ModeletExceptionCodes.MOBE000003, new String[] { "R", RDataTypes.R_RAW.getName() });
        } else if (output.getType() == org.rosuda.JRI.REXP.XT_VECTOR) {
            LOGGER.debug("Rtype is XT_VECTOR : " + output.rtype);
            // Assuming that a data frame without row names is considered a list
            if (output.getAttribute("row.names") != null) {
                LOGGER.debug("Rtype is XT_VECTOR - RDataFrame : " + output);
                objType = new RDataFrame(output);
            } else {
                LOGGER.debug("Rtype is XT_VECTOR - RList : " + output);
                objType = new RList(output);
            }
        }

        return objType;
    }

    // public static AbstractRType createRDataTypeFromObject(Object obj)
    // throws BusinessException {
    // return createRDataTypeFromObject(obj, null);
    // }

    /**
     * Static method to return AbstractRType from plain Object representation - used for marshalling data coming from input JSON
     * 
     * @param fieldInfo
     *            - Plain object representation of wrapper R Data type coming in JSON
     * 
     * @return AbstractRType of wrapper data type
     * 
     * @throws BusinessException
     **/

    public static AbstractRType createRDataTypeFromObject(final FieldInfo fieldInfo) throws BusinessException {
        return createRDataTypeFromObject(fieldInfo, true);
    }

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "all" })
    public static AbstractRType createRDataTypeFromObject(final FieldInfo fieldInfo, final boolean stringsAsFactors)
            throws BusinessException {
        AbstractRType objType = null;

        if (fieldInfo != null) {
            String errorMessage = "FieldName:" + fieldInfo.getModelParameterName() + " ";
            String nativeDataType = fieldInfo.getNativeDataType();

            if (fieldInfo.isCollection()) {
                if (nativeDataType.equalsIgnoreCase("data.frame")) {
                    // objType = new RVector((List<Object>)
                    // fieldInfo.getValue(), RDataTypes.R_DATA_FRAME);
                    objType = new RDataFrame((List<Object>) fieldInfo.getValue(), stringsAsFactors);
                } else if (StringUtils.equalsIgnoreCase(nativeDataType, "list")) {
                    objType = new RList((List<Object>) fieldInfo.getValue(), stringsAsFactors);
                } else {
                    objType = new RVector((List<Object>) fieldInfo.getValue(),
                            RDataTypes.getTypeEnumFromName(fieldInfo.getDataType()));
                }
            } else if (StringUtils.equalsIgnoreCase(fieldInfo.getDataType(),
                    (com.ca.umg.modelet.common.DataType.INTEGER.getUmgType()))) {
                objType = new RInteger((Integer) fieldInfo.getValue());
            } else if (StringUtils.equalsIgnoreCase(fieldInfo.getDataType(),
                    (com.ca.umg.modelet.common.DataType.LONG.getUmgType()))) {
                if (fieldInfo.getValue() instanceof Integer) {
                    objType = new RNumeric(new Long((Integer) fieldInfo.getValue()));
                } else {
                    objType = new RNumeric((Long) fieldInfo.getValue());
                }
            } else if (StringUtils.equalsIgnoreCase(fieldInfo.getDataType(),
                    (com.ca.umg.modelet.common.DataType.BIGDECIMAL.getUmgType()))) {
                if (fieldInfo.getValue() instanceof BigInteger) {
                    objType = new RNumeric((BigInteger) fieldInfo.getValue());
                } else if (fieldInfo.getValue() instanceof BigDecimal) {
                    objType = new RNumeric((BigDecimal) fieldInfo.getValue());
                } else if (fieldInfo.getValue() instanceof Double) {
                    objType = new RNumeric((Double) fieldInfo.getValue());
                } else if (fieldInfo.getValue() instanceof Integer) {
                    objType = new RNumeric(((Integer) fieldInfo.getValue()).doubleValue());
                } else if (fieldInfo.getValue() instanceof Long) {
                    objType = new RNumeric(((Long) fieldInfo.getValue()).doubleValue());
                }
            } else if (StringUtils.equalsIgnoreCase(fieldInfo.getDataType(),
                    (com.ca.umg.modelet.common.DataType.BIGINTEGER.getUmgType()))) {
                if (fieldInfo.getValue() instanceof BigInteger) {
                    objType = new RNumeric((BigInteger) fieldInfo.getValue());
                } else if (fieldInfo.getValue() instanceof Long || fieldInfo.getValue() instanceof Integer) {
                    objType = new RNumeric(new BigInteger((fieldInfo.getValue()).toString()));
                }
            } else if (StringUtils.equalsIgnoreCase(fieldInfo.getDataType(),
                    com.ca.umg.modelet.common.DataType.STRING.getUmgType())
                    || StringUtils.equalsIgnoreCase(fieldInfo.getDataType(), com.ca.umg.modelet.common.DataType.DATE.getUmgType())
                    || StringUtils.equalsIgnoreCase(fieldInfo.getDataType(),
                            com.ca.umg.modelet.common.DataType.CHARACTER.getUmgType())) {
                objType = new RCharacter((String) fieldInfo.getValue());
            } else if (StringUtils.equalsIgnoreCase(fieldInfo.getDataType(),
                    com.ca.umg.modelet.common.DataType.DOUBLE.getUmgType())) {
                // objType = new
                // RNumeric(Double.valueOf(fieldInfo.getValue().toString()));
                if (fieldInfo.getValue() instanceof BigInteger) {
                    objType = new RNumeric((BigInteger) fieldInfo.getValue());
                } else if (fieldInfo.getValue() instanceof BigDecimal) {
                    objType = new RNumeric((BigDecimal) fieldInfo.getValue());
                } else {
                    objType = new RNumeric((Double) fieldInfo.getValue());
                }
            } else if (StringUtils.equalsIgnoreCase(fieldInfo.getDataType(),
                    com.ca.umg.modelet.common.DataType.BOOLEAN.getUmgType())) {
                objType = new RLogical(fieldInfo.getValue());
            } else if (fieldInfo.getValue() instanceof ArrayList<?>) {
                ArrayList<Object> map = (ArrayList<Object>) fieldInfo.getValue();

                // if incoming item is List, check the nativeDataType key
                if (map != null && nativeDataType != null) {

                    if (StringUtils.equalsIgnoreCase(nativeDataType, RDataTypes.R_COMPLEX.getName())) {
                        objType = new RComplex(map);
                    } else if (StringUtils.equalsIgnoreCase(nativeDataType, RDataTypes.R_LIST.getName())) {
                        objType = new RList(map, stringsAsFactors);
                    } else if (StringUtils.equalsIgnoreCase(nativeDataType, RDataTypes.R_MATRIX.getName())) {
                        objType = new RMatrix(map, stringsAsFactors);
                    } else if (StringUtils.equalsIgnoreCase(nativeDataType, RDataTypes.R_ARRAY.getName())) {
                        objType = new RArray(map, stringsAsFactors);
                    } else if (StringUtils.equalsIgnoreCase(nativeDataType, RDataTypes.R_DATA_FRAME.getName())) {
                        objType = new RDataFrame(map, stringsAsFactors);
                    } else if (StringUtils.equalsIgnoreCase(nativeDataType, RDataTypes.R_FACTOR.getName())) {
                        objType = new RFactor(map);
                    } else {
                        // unknown string in rDataType
                        throw new BusinessException(ModeletExceptionCodes.MOBE000001, new String[] { "R", nativeDataType });
                    }
                } else {
                    // Map is null or rDataType is missing
                    errorMessage += "Value is null or Missing R Data Type";
                    throw new BusinessException(ModeletExceptionCodes.MOBE000001, new String[] { "R", errorMessage });
                }
            } else {
                // unknown plain Object representation
                errorMessage += fieldInfo.getValue().getClass().toString();
                throw new BusinessException(ModeletExceptionCodes.MOBE000001, new String[] { "R", errorMessage });
            }
        }

        return objType;
    }

    public Map<String, Object> toUmgType1(String name, String sequence) {
        throw new IllegalArgumentException("we need to implement toUmgType1 method");
    }
}
