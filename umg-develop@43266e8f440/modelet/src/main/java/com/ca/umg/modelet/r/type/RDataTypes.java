/*
 * RDataTypes.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.r.type;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;

@SuppressWarnings("PMD")
public enum RDataTypes {
    R_CHARACTER("character"), R_LOGICAL("logical"), R_COMPLEX("complex"), R_INTEGER("integer"), R_NUMERIC("numeric"), R_VECTOR(
            "vector"), R_LIST(
                    "list"), R_MATRIX("matrix"), R_DATA_FRAME("data.frame"), R_RAW("raw"), R_ARRAY("array"), R_FACTOR("factor");

    private String name;

    /**
     * Creates a new RDataTypes object.
     *
     * @param name
     *            - R name of the data type
     **/
    private RDataTypes(String name) {
        this.name = name;
    }

    /**
     * Returns R name of data type
     *
     * @return String
     **/
    public String getName() {
        return name;
    }

    /**
     * Gets R Data Type Enum from R name
     *
     * @param strName
     *            eg: "data.frame"
     *
     * @return Enum RDataType
     *
     * @throws BusinessException
     *             - if String contains an unsupported type
     **/
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public static RDataTypes getTypeEnumFromName(String strName) throws BusinessException {
        RDataTypes rdt = null;

        switch (strName) {
        case "string":
        case "character":
        case "date":
            rdt = RDataTypes.R_CHARACTER;
            break;

        case "double":
        case "numeric":
        case "bigdecimal":
        case "biginteger":
        case "long":
            rdt = RDataTypes.R_NUMERIC;
            break;

        case "boolean":
        case "logical":
            rdt = RDataTypes.R_LOGICAL;
            break;

        case "complex":
            rdt = RDataTypes.R_COMPLEX;
            break;

        case "integer":
            rdt = RDataTypes.R_INTEGER;
            break;

        case "vector":
            rdt = RDataTypes.R_VECTOR;
            break;
        case "object":
        case "list":
            rdt = RDataTypes.R_LIST;
            break;

        case "matrix":
            rdt = RDataTypes.R_MATRIX;
            break;

        case "data.frame":
            rdt = RDataTypes.R_DATA_FRAME;
            break;

        case "array":
            rdt = RDataTypes.R_ARRAY;
            break;

        case "raw":
            rdt = RDataTypes.R_RAW;
            break;

        case "factor":
            rdt = RDataTypes.R_FACTOR;
            break;

        default:
            throw new BusinessException(ModeletExceptionCodes.MOBE000001, new String[] { "R", strName });
        }

        return rdt;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public static String getJavaNameFromTypeEnum(RDataTypes rdt) throws BusinessException {

        String retVal = null;

        switch (rdt.getName()) {
        case "character":
            retVal = "string";
            break;

        case "numeric":
            retVal = "double";
            break;

        case "logical":
            retVal = "boolean";
            break;

        // case "complex":
        // retVal = "object";
        // break;

        case "integer":
            retVal = "integer";
            break;

        // case "vector":
        // retVal = "object";
        // break;

        case "list":
            retVal = "object";
            break;

        case "matrix":
            retVal = "object";
            break;

        case "data.frame":
            retVal = "object";
            break;

        case "array":
            retVal = "object";
            break;

        case "factor":
            retVal = "object";
            break;

        // case "raw":
        // rdt = RDataTypes.R_RAW;
        // break;

        default:
            throw new BusinessException(ModeletExceptionCodes.MOBE000001, new String[] { "R", rdt.getName() });
        }

        return retVal;
    }
}
