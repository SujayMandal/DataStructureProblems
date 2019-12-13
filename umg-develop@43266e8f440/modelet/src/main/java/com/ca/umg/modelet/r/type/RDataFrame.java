/*
 * RDataFrame.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rosuda.REngine.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.ioreduce.BooleanValueEnum;
import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.lang.type.DataType;
import com.ca.umg.modelet.lang.type.LangTypeConstants;

/**
 * JSON representation:
 * 
 * HashMap - Required keys - data, rDataType Optional keys - rowNames, colNames
 * 
 * Eg:
 * 
 * "payload": [ { "fieldName": "inDataFrame", "sequence": 1, "dataType": "object", "nativeDataType": "data.frame", "collection":
 * false, "value": [ { "fieldName": "data", "sequence": 1, "dataType": "object", "collection": false, "value": [ { "fieldName":
 * "int-array-data", "sequence": 1, "dataType": "integer", "collection": true, "value": [ 12, 3, 4 ] }, { "fieldName":
 * "num-array-data", "sequence": 2, "dataType": "double", "collection": true, "value": [ 2.3, -4.5, 4.7 ] }, { "fieldName":
 * "str-array-data", "sequence": 3, "dataType": "string", "collection": true, "value": [ "life", "is", "beautiful" ] }, {
 * "fieldName": "bool-array-data", "sequence": 4, "dataType": "boolean", "collection": true, "value": [ false, true, false ] }, {
 * "fieldName": "int-data", "sequence": 5, "dataType": "integer", "collection": false, "value": 15 }, { "fieldName": "str-data",
 * "sequence": 6, "dataType": "string", "collection": false, "value": "teststring" }, { "fieldName": "bool-data", "sequence": 7,
 * "dataType": "boolean", "collection": false, "value": true }, { "fieldName": "double-data", "sequence": 7, "dataType": "double",
 * "collection": false, "value": 67.6 } ] }, { "fieldName": "colnames", "sequence": 2, "dataType": "string", "collection": true,
 * "value": [ null, "col2", "col3", "col4", null, "col6", "col7", null ] }, { "fieldName": "rownames", "sequence": 3, "dataType":
 * "string", "collection": true, "value": [ "row1", "row2", "row3" ] } ] } ]
 * 
 * R representation:
 * 
 * Usage: data.frame(..., row.names = NULL, check.rows = FALSE, check.names = TRUE, stringsAsFactors = default.stringsAsFactors())
 * 
 * Currently supported params: ... , row.names. All others are default
 * 
 * Eg: data.frame( colNumeric = c(4,8.5,2.5), colInteger = c(1,3,5), colString = c("abc", "def", "efg"), colBoolean = c((TRUE),
 * (TRUE), (TRUE)), colComplex = c(complex(real=5,imaginary=7), complex(real=7, imaginary=-4), complex(real=0, imaginary=2)),
 * colSingleInt = 45, colSingleNum = 5.6, colSingleBoolean = (TRUE), colSingleString = "john", colSingleComplex = complex(real=4,
 * imaginary=8), colList = list (listCol1=c(1,2,3), listCol2=c((TRUE),(TRUE), (TRUE))), matrix(c(1,4,6,8,5,3), nrow=3, ncol=2),
 * colArray = array (1:6, c(1,3,2)), row.names = c("row1", "row2", "row3") )
 **/
@SuppressWarnings("PMD")
public class RDataFrame extends AbstractRType {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDataFrame.class);
    private List<DataType> list;
    private List<String> rowNames;
    private List<String> colNames;
    private boolean stringsAsFactors;
    private boolean rowWise;

    /**
     * Creates a new RDataFrame object.
     *
     * @param objs
     *            List of plain object representation of components
     * @param rowNames
     *            List of row names
     * @param colNames
     *            List of col names
     *
     * @throws BusinessException
     *             - all vectors in the non sanitized list should be same length - number of row names should match size of data
     *             frame (if list contains at least one vector) - row names should not be duplicated - row names should not be
     *             null - number of col names should be same as non sanitized list length
     * 
     **/
    // public RDataFrame(List<?> objs,
    // List<String> rowNames,
    // List<String> colNames)
    // throws BusinessException
    // {
    // super();
    // List<DataType> nonSanitizedList = null;
    //
    // if (objs != null && !objs.isEmpty() && objs.get(0) instanceof AbstractRType)
    // {
    // nonSanitizedList = (List<DataType>)objs;
    // }
    // else
    // {
    // nonSanitizedList = new ArrayList<DataType>();
    //
    // for (Object obj : objs)
    // {
    // nonSanitizedList.add(AbstractRType.createRDataTypeFromObject(obj));
    // }
    // }
    //
    // // Data.frame supports many data types - see above for R representation
    // // R autoconverts many data types like matrix and list to dataframe rows and columns based on length criteria.
    // // We do not check the goodness of a dataframe, we simply treat it like a list.
    // // We only check length for simple vectors not lists, matrix, and embedded dataframes
    // // If the dataframe is inconsistent, i.e contains a matrix that cannot fit into the given rows and cols for example,
    // // modelet does not flag this. The R engine will throw an error and it will appear as MOSE00001
    // // For ideal behavior in current implementation , we need at least 1 vector in the data frame
    //
    // int numRows = -1;
    //
    // for (DataType rType : nonSanitizedList)
    // {
    // if (rType.isPrimitive())
    // {
    // if (numRows == -1)
    // {
    // numRows = 1;
    // }
    // }
    // else if (rType instanceof RVector)
    // {
    // if (numRows == -1)
    // {
    // numRows = ((RVector)rType).size();
    // }
    // else if (numRows != ((RVector)rType).size())
    // {
    // throw new BusinessException(ModeletExceptionCodes.MOBE000002,
    // new String[]
    // {
    // getClass().getName(),
    // LangTypeConstants.R_DATA_FRAME_DATA
    // });
    // }
    // }
    // //else
    // //{
    // // for RList, RMatrix, RDataFrame, RArray
    // // just hope to God it works or else it will fail in REngine.eval
    // // otherwise add validations for all these types here
    // // do not set numRows in this case
    // // DISCLAIMER: a dataframe with only RMatrix, RList, RArray or combined with only primitives may behave unexpectedly!
    // // for ideal results, include vectors and primitives only
    // //}
    //
    // if (list == null)
    // {
    // list = new ArrayList<DataType>();
    // }
    //
    // // keeping non sanitized list separate because in the future we may support auto-converting matrix etc
    // // to vector form. Currently, nonSanitizedList is same as list, but in the future, list will be a pure
    // // vector list of length = numrows. Primitives will be repeated numRows times so we can check the
    // // goodness of dataframe and flag any errors here.
    // list.add(rType);
    // }
    //
    // //number of row names should match size of data frame (if list contains at least one vector)
    // if (rowNames != null && numRows > -1 && numRows != rowNames.size())
    // {
    // throw new BusinessException(ModeletExceptionCodes.MOBE000002,
    // new String[]
    // {
    // getClass().getName(), LangTypeConstants.R_DATA_FRAME_ROW_NAMES
    // });
    // }
    //
    // //rows should not be null
    // for (String s : rowNames)
    // {
    // if (s == null)
    // {
    // throw new BusinessException(ModeletExceptionCodes.MOBE000002,
    // new String[]
    // {
    // getClass().getName(),
    // "null " + LangTypeConstants.R_DATA_FRAME_ROW_NAMES
    // });
    // }
    // }
    //
    //
    // //rows should not be duplicated
    // List<String> dupRows = new ArrayList<String>();
    // for (String s : rowNames)
    // {
    // if (dupRows.contains(s))
    // {
    // throw new BusinessException(ModeletExceptionCodes.MOBE000002,
    // new String[]
    // {
    // getClass().getName(),
    // "duplicate " +
    // LangTypeConstants.R_DATA_FRAME_ROW_NAMES
    // });
    // }
    // else
    // {
    // dupRows.add(s);
    // }
    // }
    //
    //
    // this.rowNames = rowNames;
    // int numCols = nonSanitizedList.size();
    //
    // //number of column names should match nonSanitizedList length [note: NOT sanitized list length ]
    // if (colNames != null && numCols != colNames.size())
    // {
    // throw new BusinessException(ModeletExceptionCodes.MOBE000002,
    // new String[]
    // {
    // getClass().getName(), LangTypeConstants.R_DATA_FRAME_COL_NAMES
    // });
    // }
    //
    // this.colNames = colNames;
    // }

    /**
     * Creates a new RDataFrame object from REXP.
     *
     * @param output
     *            - REXP returned from REngine.eval
     *
     * @throws BusinessException
     *             - if REXP cannot be represented asList or asVector (at least one must work)
     **/
    public RDataFrame(final REXP output) throws BusinessException {
        super();
        LOGGER.debug("Entered RDataFrame output is {}  : ", output);
        try {
            rowNames = new ArrayList<String>();
            colNames = new ArrayList<String>();
            // rowNames.addAll(Arrays.asList(output.getAttribute("row.names").asStrings()));
            // colNames.addAll(Arrays.asList(output.getAttribute("names").asStrings()));
            list = new ArrayList<DataType>();
            AbstractRType subtype;
            for (int i = 0; i < output.asList().size(); i++) {
                subtype = AbstractRType.createRDataTypeFromREXP(output.asList().at(i));
                list.add(subtype);
            }
        } catch (Exception e) {
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(), "REXP" });
        }

        /*
         * final org.rosuda.JRI.RList jrilist = output.asList(); final org.rosuda.JRI.RVector jrivector = output.asVector();
         * 
         * if (jrilist == null && jrivector == null) { LOGGER.debug("In RDataFrame jrilist and jrivector is null"); throw new
         * BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(), "REXP" }); }
         * 
         * String[] keys = null;
         * 
         * if (jrilist != null) { keys = jrilist.keys(); }
         * 
         * list = new ArrayList<DataType>();
         * 
         * int length = 0; boolean isList = true;
         * 
         * if (keys == null || keys.length == 0) { if (jrivector != null) { length = jrivector.size(); } else { LOGGER.debug(
         * "In RDataFrame jrivector is null"); // jrilist is not null but not useful, in this case we need jrivector to have the
         * data throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(), "REXP" });
         * } isList = false; } else { length = keys.length; }
         * 
         * for (int x = 0; x < length; x++) { final REXP subout = isList ? jrilist.at(x) : jrivector.at(x);
         * 
         * if (isList) { if (colNames == null) { colNames = new ArrayList<String>(); }
         * 
         * colNames.add(keys[x]); }
         * 
         * final AbstractRType subtype = AbstractRType.createRDataTypeFromREXP(subout); list.add(subtype); }
         * 
         * if (output.getAttribute("row.names") != null) { final String[] rownameArray =
         * output.getAttribute("row.names").asStringArray(); rowNames = new ArrayList<String>();
         * 
         * // Assuming the dataframe returned from REngine is well formed and row names are filled with Strings if (rownameArray
         * != null) { for (final String element : rownameArray) { rowNames.add(element); } } }
         */
    }
    
    public RDataFrame(final org.rosuda.JRI.REXP output) throws BusinessException {
        super();
        LOGGER.debug("Entered RDataFrame output is {}  : ", output);
        final org.rosuda.JRI.RList jrilist = output.asList();
        final org.rosuda.JRI.RVector jrivector = output.asVector();

        if (jrilist == null && jrivector == null) {
            LOGGER.debug("In RDataFrame jrilist and jrivector is null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(), "REXP" });
        }

        String[] keys = null;

        if (jrilist != null) {
            keys = jrilist.keys();
        }

        list = new ArrayList<DataType>();

        int length = 0;
        boolean isList = true;

        if (keys == null || keys.length == 0) {
            if (jrivector != null) {
                length = jrivector.size();
            } else {
                LOGGER.debug("In RDataFrame jrivector is null");
                // jrilist is not null but not useful, in this case we need jrivector to have the data
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(), "REXP" });
            }
            isList = false;
        } else {
            length = keys.length;
        }
       if(length > 0 && keys != null){
        for (int x = 0; x < length; x++) {
            final org.rosuda.JRI.REXP subout = isList ? jrilist.at(x) : jrivector.at(x);

            if (isList) {
                if (colNames == null) {
                    colNames = new ArrayList<String>();
                }

                colNames.add(keys[x]);
            }

            final AbstractRType subtype = AbstractRType.createRDataTypeFromREXP(subout);
            list.add(subtype);
        }
       }

        if (output.getAttribute("row.names") != null) {
            final String[] rownameArray = output.getAttribute("row.names").asStringArray();
            rowNames = new ArrayList<String>();

            // Assuming the dataframe returned from REngine is well formed and row names are filled with Strings
            if (rownameArray != null) {
                for (final String element : rownameArray) {
                    rowNames.add(element);
                }
            }
        }
    }

    /**
     * Creates a new RDataFrame object from plain Object representation.
     *
     * @param hmValue
     *            - HashMap
     *
     * @throws BusinessException
     *             - all vectors in the non sanitized list should be same length - number of row names should match size of data
     *             frame (if list contains at least one vector) - row names should not be duplicated - row names should not be
     *             null - number of col names should be same as non sanitized list length - if data or rDataType is null or
     *             malformed - if map is null
     **/
    public RDataFrame(final List<Object> hmValue, final boolean stringsAsFactors) throws BusinessException {
        super();
        LOGGER.debug("In RDataFrame hmValue : stringsAsFactors" + hmValue);
        this.stringsAsFactors = stringsAsFactors;
        list = new ArrayList<DataType>();
        rowNames = new ArrayList<String>();
        colNames = new ArrayList<String>();

        if (hmValue != null) {
            Object oData = null; // hmValue.get(LangTypeConstants.R_DATA_FRAME_DATA);
            Object oRowNames = null; // hmValue.get(LangTypeConstants.R_DATA_FRAME_ROW_NAMES);
            Object oColNames = null; // hmValue.get(LangTypeConstants.R_DATA_FRAME_COL_NAMES);
            Object sequence = null;
            Object dataType = null;
            Object nativeDataType = null;
            Object collection = null;
            Object fieldName = null;

            for (final Object objAttribute : hmValue) {
                if (objAttribute instanceof FieldInfo) {
                    oData = hmValue;
                    break;
                } else if (objAttribute instanceof Map) {

                    final HashMap<String, Object> attribute = (HashMap<String, Object>) objAttribute;
                    if (attribute.get("modelParameterName").equals(LangTypeConstants.R_DATA_FRAME_DATA)) {
                        oData = attribute.get("value");

                        LOGGER.debug("In RDataFrame hmValue instance of map : value - " + oData);

                        final List<Object> parameterList = (ArrayList<Object>) attribute.get("p");

                        if (attribute.containsKey("sequence")) {
                            sequence = attribute.get("sequence");
                        } else {
                            sequence = parameterList.get(FieldInfoEnum.SEQUENCE.getIndex()).toString();
                        }

                        LOGGER.debug("In RDataFrame hmValue instance of map : sequence " + sequence);

                        if (attribute.containsKey("dataType")) {
                            dataType = attribute.get("dataType");
                        } else {
                            dataType = parameterList.get(FieldInfoEnum.DATA_TYPE.getIndex()).toString();
                        }
                        LOGGER.debug("In RDataFrame hmValue instance of map : dataType " + dataType);
                        if (attribute.containsKey("collection")) {
                            collection = attribute.get("collection");
                        } else {
                            collection = parameterList.get(FieldInfoEnum.COLLECTION.getIndex()).toString();
                        }

                        if (attribute.containsKey("nativeDataType")) {
                            nativeDataType = attribute.get("nativeDataType");
                        } else {
                            nativeDataType = parameterList.get(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex()).toString();
                        }

                        if (attribute.containsKey("modelParameterName")) {
                            fieldName = attribute.get("modelParameterName");
                        } else {
                            fieldName = parameterList.get(FieldInfoEnum.FIELD_NAME.getIndex()).toString();
                        }

                        // fieldName = attribute.get("fieldName");

                    } else if (attribute.get("modelParameterName").equals(LangTypeConstants.R_DATA_FRAME_ROW_NAMES)) {
                        oRowNames = attribute.get("value");
                    } else if (attribute.get("modelParameterName").equals(LangTypeConstants.R_DATA_FRAME_COL_NAMES)) {
                        oColNames = attribute.get("value");
                    }
                }
            }

            if (oData instanceof List<?>) {
                final List<Object> objs = (List<Object>) oData;

                for (final Object obj : objs) {

                    if (obj instanceof List) {
                        rowWise = true;
                        // array of object
                        List<Map> innerObjs = (List) obj;
                        FieldInfo fi = new FieldInfo();
                        if(dataType != null){
                        fi.setDataType(dataType.toString());
                        }
                        if(sequence !=  null  ){
                        fi.setSequence(sequence.toString());
                        }
                        if (collection != null) {
                            fi.setCollection(Boolean.valueOf(collection.toString()));
                        }
                        if(nativeDataType != null){
                        fi.setNativeDataType(nativeDataType.toString());
                        }
                        if(fieldName != null){
                        fi.setModelParameterName(fieldName.toString());
                        }

                        List innerList = new ArrayList();
                        for (Map innerObj : innerObjs) {
                            innerList.add(new FieldInfo(innerObj));
                        }

                        fi.setValue(innerList);

                        list.add(AbstractRType.createRDataTypeFromObject(fi, stringsAsFactors));
                    } else {
                        final FieldInfo fi = new FieldInfo(obj);
                        if (fi.getDataType() == null) {
                        	if(dataType != null){
                            fi.setDataType(dataType.toString());
                        	}
                        	if(sequence != null){
                            fi.setSequence(sequence.toString());
                        	}
                            if (collection != null) {
                                fi.setCollection(Boolean.valueOf(collection.toString()));
                            }
                            if(nativeDataType != null){
                            fi.setNativeDataType(nativeDataType.toString());
                            }
                            if(fieldName != null){
                            fi.setModelParameterName(fieldName.toString());
                            }

                            final Map<String, Object> map = new HashMap<String, Object>();
                            map.put("modelParameterName", "data");
                            map.put("value", fi.getValue());
                            final List<Object> listValue = new ArrayList<Object>();
                            listValue.add(map);
                            fi.setValue(listValue);
                        }
                        LOGGER.debug("In RDataFrame hmValue instance of list : fieldname " + fi.getModelParameterName()
                                + " sequence : " + fi.getSequence() + "value " + fi.getValue());
                        list.add(AbstractRType.createRDataTypeFromObject(fi, stringsAsFactors));
                    }
                }

                int numRows = -1;
                boolean hasPrimitive = false;
                boolean hasOtherDataType = false;

                Integer count = 1;
                for (final DataType rType : list) {
                    if (rType != null) {
                        LOGGER.debug("In RDataFrame datatype loop : " + count + " numRows set is " + numRows
                                + ((rType instanceof RVector) ? " vector size " + ((RVector) rType).size() : ""));
                        if (rType.isPrimitive()) {
                            hasPrimitive = true;
                        } else if (rType instanceof RVector) {
                            if (numRows == -1) {
                                numRows = ((RVector) rType).size();
                                LOGGER.debug("Number of rows set is : " + numRows);
                            } else if (numRows != ((RVector) rType).size()) {
                                LOGGER.debug("In RDataFrame datatype loop inside else if: " + count + " numRows set is " + numRows
                                        + " vector size " + ((RVector) rType).size());
                                LOGGER.debug("In RDataFrame numRows is not equal to rVector size ");
                                throw new BusinessException(ModeletExceptionCodes.MOBE000002,
                                        new String[] { this.getClass().getName(), LangTypeConstants.R_DATA_FRAME_DATA });
                            }
                        } else {
                            hasOtherDataType = true;
                        }
                    }
                    count++;
                    // else
                    // {
                    // for RList, RMatrix, RDataFrame, RArray
                    // just hope to God it works or else it will fail in REngine.eval
                    // otherwise put in validations for these types here
                    // do not set numRows in this case
                    // DISCLAIMER: a dataframe with only RMatrix, RList, RArray or combined with only primitives may behave
                    // unexpectedly!
                    // for ideal results, include vectors and primitives only
                    // }
                }

                if (hasPrimitive && numRows == -1)
                    numRows = 1;

                /*
                 * rowNames and colNames are optionals if (!(oRowNames instanceof List<?>)) { throw new
                 * BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(),
                 * LangTypeConstants.R_DATA_FRAME_ROW_NAMES }); }
                 * 
                 * if (!(oColNames instanceof List<?>)) { throw new BusinessException(ModeletExceptionCodes.MOBE000002, new
                 * String[] { this.getClass().getName(), LangTypeConstants.R_DATA_FRAME_COL_NAMES }); }
                 */

                if (oRowNames != null && !((List<Object>) oRowNames).isEmpty()) {
                    for (final Object o : (List<Object>) oRowNames) {
                        if (o == null) {
                            rowNames.add(null);
                        } else if (o instanceof String) {
                            rowNames.add((String) o);
                        } else {
                            LOGGER.debug("Received malformed item of type " + o.getClass().toString()
                                    + " in row names for RDataFrame, ignoring item");
                            rowNames.add(null);
                        }
                    }

                    // number of row names should match size of data frame (if list contains at least one vector)
                    if (numRows > -1 && !hasOtherDataType && rowNames.size() != numRows) {
                        LOGGER.debug("In RDataFrame number of row names not matching size of data frame ");
                        throw new BusinessException(ModeletExceptionCodes.MOBE000002,
                                new String[] { this.getClass().getName(), LangTypeConstants.R_DATA_FRAME_ROW_NAMES });
                    }

                    // rows should not be null
                    for (final String s : rowNames) {
                        if (s == null) {
                            LOGGER.debug("In RDataFrame rows should not be null ");
                            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] {
                                    this.getClass().getName(), "null " + LangTypeConstants.R_DATA_FRAME_ROW_NAMES });
                        }
                    }

                    final List<String> dupRows = new ArrayList<String>();

                    // rows should not be duplicated
                    for (final String s : rowNames) {
                        if (dupRows.contains(s)) {
                            LOGGER.debug("In RDataFrame rows should not be duplicated ");
                            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] {
                                    this.getClass().getName(), "duplicate " + LangTypeConstants.R_DATA_FRAME_ROW_NAMES });
                        } else {
                            dupRows.add(s);
                        }
                    }
                }

                if (oColNames != null && !((List<Object>) oColNames).isEmpty()) {
                    for (final Object o : (List<Object>) oColNames) {
                        if (o == null) {
                            colNames.add(null);
                        } else if (o instanceof String) {
                            colNames.add((String) o);
                        } else {
                            LOGGER.debug("Received malformed item of type " + o.getClass().toString()
                                    + " in col names for RDataFrame, ignoring item");
                            colNames.add(null);
                        }
                    }

                    // col names are completely optional so just ignore if there is nothing in the HashMap
                    if (colNames.size() != list.size()) {
                        LOGGER.debug(
                                "In RDataFrame col names are completely optional so just ignore if there is nothing in the HashMap ");
                        throw new BusinessException(ModeletExceptionCodes.MOBE000002,
                                new String[] { this.getClass().getName(), LangTypeConstants.R_DATA_FRAME_COL_NAMES });
                    }
                }

            } else {
                LOGGER.debug("In RDataFrame oData not instanceof List");
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(),
                        LangTypeConstants.R_DATA_FRAME_DATA + "/" + LangTypeConstants.R_DATA_TYPE });
            }
        } else {
            LOGGER.debug("In RDataFrame hmvalue is null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002,
                    new String[] { this.getClass().getName(), "FieldInfo value" });
        }

        if (rowWise || (!list.isEmpty() && list.size() > 1)) {

            boolean isAllElementsDataFrame = false;

            for (final DataType rType : list) {
                if (rType instanceof RDataFrame) {
                    isAllElementsDataFrame = true;
                } else {
                    isAllElementsDataFrame = false;
                    break;
                }
            }

            if (isAllElementsDataFrame) {
                final List<DataType> newDataFrameList = new ArrayList<>();
                final int size = list.size();

                // getMax Data Frame;
                final RDataFrame maxDataFrame = getMaxDataFrame(list);
                final int maxDataFrameSize = maxDataFrame.list.size();

                newDataFrameList.add(maxDataFrame);

                for (int i = 0; i < maxDataFrameSize; ++i) {
                    List<Object> vectorElements = null;
                    RDataTypes elementDataType = null;
                    for (int j = 0; j < size; j++) {
                        final RDataFrame listElement = (RDataFrame) list.get(j);
                        if (elementDataType == null && listElement.list.size() > i) {
                            elementDataType = ((AbstractRType) listElement.list.get(i)).getRDataType();
                        }

                        if (vectorElements == null) {
                            vectorElements = new ArrayList<>();
                        }

                        if (listElement.list.size() > i) {
                            vectorElements.add(listElement.list.get(i));
                        } else {
                            if (elementDataType != null) {
                                switch (elementDataType) {
                                case R_INTEGER:
                                    final Integer intValue = null;
                                    vectorElements.add(new RInteger(intValue));
                                    break;
                                case R_NUMERIC:
                                    final Double doubleValue = null;
                                    vectorElements.add(new RNumeric(doubleValue));
                                    break;
                                case R_LOGICAL:
                                    final Object booleanValue = null;
                                    vectorElements.add(new RLogical(booleanValue));
                                    break;
                                case R_CHARACTER:
                                    final String strValue = null;
                                    vectorElements.add(new RCharacter(strValue));
                                    break;
                                default:
                                    LOGGER.debug("In RDataFrame MOBE000001 error no datatype");
                                    throw new BusinessException(ModeletExceptionCodes.MOBE000001,
                                            new String[] { "R", elementDataType.getName() });
                                }
                            }
                        }
                    }

                    if (vectorElements != null && elementDataType != null) {
                        maxDataFrame.list.set(i, new RVector(vectorElements, elementDataType));
                    } else {
                        maxDataFrame.list.set(i, new RVector(vectorElements, elementDataType));
                    }

                }

                // list = rowWise ? ((RDataFrame) newDataFrameList.get(0)).list : newDataFrameList;
                list = rowWise ? ((RDataFrame) newDataFrameList.get(0)).list : newDataFrameList;

            }

        }
    }

    /**
     * Returns native representation of data frame
     *
     * @return String
     **/
    @Override
    public String toNative() {
        String strRet = null;
        if (list.isEmpty()) {
            strRet = getRDataType().getName() + "()";
        } else {
            final StringBuffer sb = new StringBuffer(getRDataType().getName());
            final String bracket = "(";
            sb.append(bracket);

            final int intSize = list.size();
            int index = 0;

            for (final DataType dt : list) {
                if (colNames != null && !colNames.isEmpty() && colNames.get(index) != null) {
                    final String s = "=";
                    sb.append(colNames.get(index));
                    sb.append(s);
                }

                if (dt == null) {
                    final String na = "NA";
                    sb.append(na);
                } else {
                    sb.append(dt.toNative());
                }

                if (index < intSize - 1) {
                    final String comma = ",";
                    sb.append(comma);
                }

                index++;
            }

            if (rowNames != null && !rowNames.isEmpty()) {
                try {
                    final String rown = ", row.names=";
                    sb.append(rown);

                    final List<Object> objNames = new ArrayList<Object>();

                    for (final String s : rowNames) {
                        objNames.add(s);
                    }

                    final RVector vec = new RVector(objNames, RDataTypes.R_CHARACTER);
                    sb.append(vec.toNative());
                } catch (final BusinessException be) {
                    // silently log if RVector constructor throws BusinessException
                    // this should never happen but if it does, completely skip row names and provide a good toNative string to
                    // REngine
                    LOGGER.debug(
                            "This should never happen because we are making sure the row names are correct when we create an RDataFrame. "
                                    + be.getLocalizedMessage());
                }
            }

            if (!stringsAsFactors) {
                sb.append(",stringsAsFactors=FALSE");
            }

            sb.append(")");

            strRet = sb.toString();

        }

        return strRet;
    }

    /**
     * Returns false
     *
     * @return false
     **/
    @Override
    public boolean isPrimitive() {
        return false;
    }

    /**
     * returns "data.frame"
     *
     * @return "data.frame"
     **/
    @Override
    public RDataTypes getRDataType() {
        return RDataTypes.R_DATA_FRAME;
    }

    /**
     * Returns plain object representation of RDataFrame
     *
     * @return HashMap
     **/

    // @Override
    // public Object toJava() {
    // HashMap<String, Object> hmValues = new HashMap<String, Object>();
    // hmValues.put(LangTypeConstants.R_DATA_TYPE, getRDataType().getName());
    // hmValues.put(LangTypeConstants.R_DATA_FRAME_ROW_NAMES, rowNames);
    // hmValues.put(LangTypeConstants.R_DATA_FRAME_COL_NAMES, colNames);
    //
    // List<Object> objs = new ArrayList<Object>();
    //
    // for (DataType type : list)
    // {
    // objs.add(type.toJava());
    // }
    // hmValues.put(LangTypeConstants.R_DATA_FRAME_DATA, objs);
    //
    // return hmValues;
    // }

    @Override
    public FieldInfo toUmgType(final String name, final String sequence) {
        // HashMap<String, Object> hmValues = new HashMap<String, Object>();
        // hmValues.put(LangTypeConstants.R_DATA_TYPE, getRDataType().getName());
        // hmValues.put(LangTypeConstants.R_DATA_FRAME_ROW_NAMES, rowNames);
        // hmValues.put(LangTypeConstants.R_DATA_FRAME_COL_NAMES, colNames);

        final FieldInfo fi = new FieldInfo();
        fi.setCollection(false);
        fi.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
        fi.setModelParameterName(name);
        fi.setSequence(sequence);
        fi.setNativeDataType(getRDataType().getName());

        final List<Object> objs = new ArrayList<Object>();

        int i = 1;
        for (final DataType type : list) {
            objs.add(type.toUmgType("column" + i, Integer.toString(i)));
            i++;

        }
        //
        // hmValues.put(LangTypeConstants.R_DATA_FRAME_DATA, objs);
        //
        // return hmValues;

        final ArrayList<Object> alValues = new ArrayList<Object>();

        FieldInfo f = new FieldInfo();
        f.setCollection(false);
        f.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
        f.setModelParameterName(LangTypeConstants.R_DATA_FRAME_DATA);
        f.setSequence("1");
        f.setValue(objs.toArray());

        alValues.add(f);

        if (rowNames != null && !rowNames.isEmpty()) {
            f = new FieldInfo();
            f.setCollection(false);
            f.setDataType(com.ca.umg.modelet.common.DataType.STRING.getUmgType());
            f.setModelParameterName(LangTypeConstants.R_DATA_FRAME_ROW_NAMES);
            f.setSequence("2");
            f.setValue(rowNames.toArray());

            alValues.add(f);
        }

        if (colNames != null && !colNames.isEmpty()) {

            f = new FieldInfo();
            f.setCollection(false);
            f.setDataType(com.ca.umg.modelet.common.DataType.STRING.getUmgType());
            f.setModelParameterName(LangTypeConstants.R_DATA_FRAME_COL_NAMES);
            f.setSequence("3");
            f.setValue(colNames.toArray());

            alValues.add(f);
        }

        fi.setValue(alValues.toArray());
        return fi;
    }

    @Override
    public Object getPrimitive() {
        // TODO Auto-generated method stub
        return null;
    }

    private RDataFrame getMaxDataFrame(final List<DataType> dataFrameList) {
        int maxSize = 0;
        RDataFrame maxDataFrame = null;
        for (final DataType dataFrame : dataFrameList) {
            if (maxDataFrame == null) {
                maxDataFrame = (RDataFrame) dataFrame;
            }
            int currrentSize = ((RDataFrame) dataFrame).list.size();
            if (maxSize <= currrentSize) {
                maxDataFrame = (RDataFrame) dataFrame;
                maxSize = currrentSize;
            }
        }

        return maxDataFrame;
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
        parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(),
                getNativeDataTypeValueEnum(getRDataType().getName()).getIntValue());
        parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(),
                getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());
        parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
        parameterList.set(FieldInfoEnum.P.getIndex(), null);

        final ArrayList<Object> alValues = new ArrayList<Object>();

        alValues.add(createOne());

        if (rowNames != null && !rowNames.isEmpty()) {
            alValues.add(createTwo());
        }

        if (colNames != null && !colNames.isEmpty()) {
            alValues.add(createThree());
        }

        parameterList.set(FieldInfoEnum.VALUE.getIndex(), alValues.toArray());

        return newElement;
    }

    private Map<String, Object> createOne() {
        final Map<String, Object> newElement = new HashMap<String, Object>();

        final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);

        final Object dummy = new Object();
        for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
            parameterList.add(dummy);
        }

        newElement.put("p", parameterList);

        parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_DATA_FRAME_DATA);
        parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "1");
        parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(),
                getNativeDataTypeValueEnum(getRDataType().getName()).getIntValue());
        parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(),
                getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());
        parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
        parameterList.set(FieldInfoEnum.P.getIndex(), null);

        final List<Object> objs = new ArrayList<Object>();

        int i = 1;
        for (final DataType type : list) {
            objs.add(type.toUmgType1("column" + i, Integer.toString(i)));
            i++;

        }

        parameterList.set(FieldInfoEnum.VALUE.getIndex(), objs.toArray());

        return newElement;
    }

    private Map<String, Object> createTwo() {
        final Map<String, Object> newElement = new HashMap<String, Object>();

        final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);

        final Object dummy = new Object();
        for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
            parameterList.add(dummy);
        }

        newElement.put("p", parameterList);

        parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_DATA_FRAME_ROW_NAMES);
        parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "2");
        parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(),
                getNativeDataTypeValueEnum(getRDataType().getName()).getIntValue());
        parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(),
                getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.STRING.getUmgType()).getIntValue());
        parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
        parameterList.set(FieldInfoEnum.P.getIndex(), null);
        parameterList.set(FieldInfoEnum.VALUE.getIndex(), rowNames.toArray());

        return newElement;
    }

    private Map<String, Object> createThree() {
        final Map<String, Object> newElement = new HashMap<String, Object>();

        final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);

        final Object dummy = new Object();
        for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
            parameterList.add(dummy);
        }

        newElement.put("p", parameterList);

        parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_DATA_FRAME_COL_NAMES);
        parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "3");
        parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(),
                getNativeDataTypeValueEnum(getRDataType().getName()).getIntValue());
        parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(),
                getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.STRING.getUmgType()).getIntValue());
        parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
        parameterList.set(FieldInfoEnum.P.getIndex(), null);
        parameterList.set(FieldInfoEnum.VALUE.getIndex(), colNames.toArray());

        return newElement;
    }
}
