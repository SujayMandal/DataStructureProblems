/*
 * RMatrix.java
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
import com.ca.umg.modelet.lang.type.DataType;
import com.ca.umg.modelet.lang.type.LangTypeConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
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
 * JSON representation: HashMap - Required - data, type, rDataType Optional - names
 * 
 * Eg: "payload":[ { "fieldName":"inMatrix", "sequence":1, "dataType":"matrix", "collection":false, "value": { "data": [ [1, -34,
 * 23], [4, null, 8], [4, 5, null], [-3, 45, 10]
 * 
 * 
 * ], "names":[ ["row1", "row2", "row3", null], ["col1", null, "col3"] ], "type":"integer", "rDataType":"matrix" } } ]
 * 
 * R representation: Usage matrix(data = NA, nrow = 1, ncol = 1, byrow = FALSE, dimnames = NULL)
 * 
 * Currently supported: data, nrow, ncol, dimnames. byrow defaults to false
 * 
 * Eg: matrix( c(1,4,4,-3,-34,NA,5,45,23,8,NA,10), nrow=4, ncol=3, dimnames=list( c("row1","row2","row3",NA), c("col1",NA,"col3")
 * ) )
 * 
 * **/
@SuppressWarnings("PMD")
public class RMatrix extends AbstractRType {
    private static final Logger LOGGER = LoggerFactory.getLogger(RMatrix.class);
    private DataType[][] matrix;
    private int rows;
    private int cols;
    private List<String> rowNames;
    private List<String> colNames;
    private RDataTypes elementDataType;
    private boolean stringsAsFactors;

    /**
     * Creates a new RMatrix object.
     *
     * @param objMatrix
     *            - 2D object array
     * @param oNames
     *            - List of column names
     * @param elementType
     *            - enum indicating data type of elements
     *
     * @throws BusinessException
     **/
    public RMatrix(final Object[][] objMatrix, final List<Object> oNames, final RDataTypes elementType, final boolean stringsAsFactors) throws BusinessException {
        super();
        this.stringsAsFactors = stringsAsFactors;
        elementDataType = elementType;
        createMatrixFrom2DArray(objMatrix, oNames);
    }

    /**
     * Initialize RMatrix from 2D Object array and optional List<List<String>> for row and col names
     *
     * @param objMatrix
     *            (required)
     * @param oNames
     *            (can be null)
     *
     * @throws BusinessException
     *             - if the incoming object[][] is not a perfect matrix or empty or malformed - The Object matrix has elements of
     *             data type other than type specified - if dimension names are not null and of type of than String - if dimension
     *             names are not a 2D array of number of rows = 2 (i.e. one array for each dimension) - if number of row / column
     *             names are not equal to Object matrix
     * 
     **/

    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    private void createMatrixFrom2DArray(final Object[][] objMatrix, final List<Object> oNames) throws BusinessException {
        if (objMatrix != null) {
            // this.matrix = matrix;
            rows = objMatrix.length;
            cols = -1;

            // checking the matrix
            for (Object[] element : objMatrix) {
                if (cols == -1) {
                    cols = element.length;
                } else {
                    // the incoming array should be a perfect matrix, if not, throw a BusinessException
                    if (cols != element.length) {
                        LOGGER.debug("In RMatrix incoming array not a perfect matrix");
                        throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                                "Object[][]" });
                    }
                }
            }

            if (cols == -1 || objMatrix.length == 0) {
                LOGGER.debug("In RMatrix objmatrix length zero");
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "Object[][]" });
            }
        } else {
            LOGGER.debug("In RMatrix objmatrix null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "Object[][]" });
        }

        // objMatrix has checked out, rows and cols have been set
        if (objMatrix instanceof AbstractRType[][]) {
            matrix = (DataType[][]) objMatrix;
        } else {
            matrix = new DataType[rows][cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    // MS: preserve nulls without type checking - already inited to null, no need to reassign
                    // if (objMatrix[i][j] == null)
                    // {
                    // matrix[i][j] = null;
                    // }
                    if (objMatrix[i][j] != null) {
                        if (objMatrix[i][j] instanceof AbstractRType) {
                            matrix[i][j] = (AbstractRType) objMatrix[i][j];
                            continue;
                        }
                        // TODO:MS ----????
                        AbstractRType element = AbstractRType.createRDataTypeFromObject(new FieldInfo(objMatrix[i][j]), stringsAsFactors);

                        if (element != null && element.getRDataType() == elementDataType) {
                            matrix[i][j] = element;
                        } else {
                            // MS: if this is a mix of RNumerics and RIntegers, it comes here because few items are returned as
                            // RIntegers which is not equal to "numeric" vector type.
                            // For this one case only, convert any RIntegers to RNumerics if the vector type is numeric.
                            // No other datatypes are auto converted, for everything else, throw an exception
                            if (element instanceof RInteger && elementDataType == RDataTypes.R_NUMERIC) {

                              //  int intValue = ((Integer) element.getPrimitive()).intValue();
                                element = new RNumeric((Double) element.getPrimitive()); // checked cast so I know what I'm doing
                                matrix[i][j] = element;
                            } 
                            else if (element.getPrimitive() instanceof Number) {

                                //  int intValue = ((Integer) element.getPrimitive()).intValue();
                                  element = new RNumeric((Double) element.getPrimitive()); // checked cast so I know what I'm doing
                                  matrix[i][j] = element;
                              } 
                            
                            else {
                                LOGGER.debug("In RMatrix element not integer");
                                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { "RVector",
                                        LangTypeConstants.R_VECTOR_TYPE });
                            }
                        }
                    }
                }
            }
        }

        // if dimension names are not null then consider this else just ignore
        if (oNames != null) {
            if (oNames instanceof List<?> && oNames.size() == 2) {
                List<Object> objNames = oNames;

                // names should have 2 non-null elements which are lists of size rows and cols respectively
                if (objNames.get(0) instanceof List<?> && ((List<Object>) objNames.get(0)).size() == rows
                        && objNames.get(1) instanceof List<?> && ((List<Object>) objNames.get(1)).size() == cols) {
                    List<Object> objRows = (List<Object>) objNames.get(0);
                    List<Object> objCols = (List<Object>) objNames.get(1);

                    if (!(objRows.get(0) instanceof String) || !(objCols.get(0) instanceof String)) {
                        LOGGER.debug("In RMatrix objRows not string");
                        throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                                LangTypeConstants.R_MATRIX_NAMES });
                    }

                    // at this point we have established that objRows and objCols are the right size and filled with right type or
                    // nulls
                    rowNames = new ArrayList<String>();
                    colNames = new ArrayList<String>();

                    for (Object o : objRows) {
                        rowNames.add((String) o);
                    }

                    for (Object o : objCols) {
                        colNames.add((String) o);
                    }
                } else {
                    LOGGER.debug("In RMatrix rows cols size not list or null");
                    throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                            LangTypeConstants.R_MATRIX_NAMES });
                }
            } else {
                LOGGER.debug("In RMatrix rows cols not present");
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                        LangTypeConstants.R_MATRIX_NAMES });
            }
        }
    }

    /**
     * Creates a new RMatrix object.
     *
     * @param output
     *            - REXP
     * @param elementType
     *            data type of components
     *
     * @throws BusinessException
     *             - For numeric/integer components - if REXP cannot be returned as Matrix via JRI asMatrix - For string / boolean
     *             components - if dimensions are more than 2 (rexp.getAttribute("dim")) - For components other than
     *             numeric/integer/logical/character -
     * 
     **/
    public RMatrix(final REXP output, final RDataTypes elementType) throws BusinessException {
        super();

        LOGGER.debug("Entered RMatrix elementType {} for output is {}  : ",elementType,output);
        elementDataType = elementType;

        switch (elementType) {
        case R_NUMERIC:
        case R_INTEGER:

            createNumericMatrix(output);
            break;

        case R_CHARACTER:

            createCharacterMatrix(output);
            break;

        case R_LOGICAL:

            createLogicalMatrix(output);
            break;

        default:
            throw new BusinessException(ModeletExceptionCodes.MOBE000003, new String[] { getClass().getName(),
                    elementType.getName() });
        }
    }
    
    public RMatrix(final org.rosuda.JRI.REXP output, final RDataTypes elementType) throws BusinessException {
        super();

        LOGGER.debug("Entered RMatrix elementType {} for output is {}  : ",elementType,output);
        elementDataType = elementType;

        switch (elementType) {
        case R_NUMERIC:
        case R_INTEGER:

            createNumericMatrix(output);
            break;

        case R_CHARACTER:

            createCharacterMatrix(output);
            break;

        case R_LOGICAL:

            createLogicalMatrix(output);
            break;

        default:
            throw new BusinessException(ModeletExceptionCodes.MOBE000003, new String[] { getClass().getName(),
                    elementType.getName() });
        }
    }

    // MS:Utility functions for above method
    private void createNumericMatrix(final REXP output) throws BusinessException {
        if (output.dim() == null) {
            LOGGER.debug("In RMatrix output asMatrix is null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" });
        }

        double[][] twoDArray;
        try {
            twoDArray = output.asDoubleMatrix();
        } catch (REXPMismatchException e) {
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" });
        }
        if(StringUtils.equalsIgnoreCase(elementDataType.getName(),RDataTypes.R_INTEGER.getName())) {
	        Integer[][] twoDWrapper = null;
	        for (int i = 0; i < twoDArray.length; i++) {
	            if (twoDWrapper == null) {
	                twoDWrapper = new Integer[twoDArray.length][twoDArray[i].length];
	            }
	            for(int j=0; j<twoDArray[i].length; j++) {
	            	twoDWrapper[i][j] = (int)twoDArray[i][j];
	            }
	        }
	        createMatrixFrom2DArray(twoDWrapper, null);
        } else {
            Double[][] twoDWrapper = null;
            for (int i = 0; i < twoDArray.length; i++) {
                if (twoDWrapper == null) {
                    twoDWrapper = new Double[twoDArray.length][twoDArray[i].length];
                }
                twoDWrapper[i] = ArrayUtils.toObject(twoDArray[i]);
            }
            createMatrixFrom2DArray(twoDWrapper, null);
        }

    }
    
    private void createNumericMatrix(final org.rosuda.JRI.REXP output) throws BusinessException {
        if (output.asMatrix() == null) {
            LOGGER.debug("In RMatrix output asMatrix is null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" });
        }
        
        double[][] twoDArray = output.asMatrix();
        if(StringUtils.equalsIgnoreCase(elementDataType.getName(),RDataTypes.R_INTEGER.getName())) {
	        Integer[][] twoDWrapper = null;
	        for (int i = 0; i < twoDArray.length; i++) {
	            if (twoDWrapper == null) {
	                twoDWrapper = new Integer[twoDArray.length][twoDArray[i].length];
	            }
	            for(int j=0; j<twoDArray[i].length; j++) {
	            	twoDWrapper[i][j] = (int)twoDArray[i][j];
	            }
	        }
	        createMatrixFrom2DArray(twoDWrapper, null);
        } else {
            Double[][] twoDWrapper = null;
            for (int i = 0; i < twoDArray.length; i++) {
                if (twoDWrapper == null) {
                    twoDWrapper = new Double[twoDArray.length][twoDArray[i].length];
                }
                twoDWrapper[i] = ArrayUtils.toObject(twoDArray[i]);
            }
            createMatrixFrom2DArray(twoDWrapper, null);
        }

    }

    private void createCharacterMatrix(final REXP output) throws BusinessException {

        try {
            REXP dim = output.getAttribute("dim");

            if (dim == null || dim.length() != 2) {
                LOGGER.debug("In RMatrix createCharacterMatrix dim not equal to XT_ARRAY_INT and size 2");
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[]{getClass().getName(), "REXP"});
            }

            int[] ds = dim.asIntegers();
            int nrow = ds[0];
            int ncol = ds[1];
            String[] strArray = output.asStrings();

            // get attribute here - byrow (not coming from JRI). Currently assuming default: byrow=false, i.e. fill it over rows first
            // then column
            createMatrixFrom2DArray(fillMatrixFromArray(strArray, nrow, ncol, false), null);

            REXP dimnames = output.getAttribute("dimnames");
            if (dimnames != null) {
                org.rosuda.REngine.RList jriList = dimnames.asList();
                // at(index) does not throw Array Index OOB exception, just returns null for missing elements
                if (jriList != null && jriList.at(0) != null && jriList.at(1) != null) {
                    String[] rows = jriList.at(0).asStrings();
                    if (rows != null && rows.length == nrow) {
                        rowNames = new ArrayList<String>();
                        for (String row : rows) {
                            rowNames.add(row);
                        }
                    }
                    String[] cols = jriList.at(1).asStrings();
                    if (cols != null && cols.length == ncol) {
                        colNames = new ArrayList<String>();
                        for (String col : cols) {
                            colNames.add(col);
                        }
                    }
                }
            }
        } catch(REXPMismatchException e) {
            LOGGER.debug("In RMatrix createCharacterMatrix dim not equal to XT_ARRAY_INT and size 2");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[]{getClass().getName(), "REXP"});
        }
    }
    
    private void createCharacterMatrix(final org.rosuda.JRI.REXP output) throws BusinessException {
    	org.rosuda.JRI.REXP dim = output.getAttribute("dim");

        if (dim == null || dim.getType() != org.rosuda.JRI.REXP.XT_ARRAY_INT || dim.asIntArray() == null || dim.asIntArray().length != 2) {
            LOGGER.debug("In RMatrix createCharacterMatrix dim not equal to XT_ARRAY_INT and size 2");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" });
        }

        int[] ds = dim.asIntArray();
        int nrow = ds[0];
        int ncol = ds[1];
        String[] strArray = output.asStringArray();

        // get attribute here - byrow (not coming from JRI). Currently assuming default: byrow=false, i.e. fill it over rows first
        // then column
        createMatrixFrom2DArray(fillMatrixFromArray(strArray, nrow, ncol, false), null);

        org.rosuda.JRI.REXP dimnames = output.getAttribute("dimnames");
        if (dimnames != null) {
            org.rosuda.JRI.RList jriList = dimnames.asList();
            // at(index) does not throw Array Index OOB exception, just returns null for missing elements
            if (jriList != null && jriList.at(0) != null && jriList.at(1) != null) {
                String[] rows = jriList.at(0).asStringArray();
                if (rows != null && rows.length == nrow) {
                    rowNames = new ArrayList<String>();
                    for (String row : rows) {
                        rowNames.add(row);
                    }
                }
                String[] cols = jriList.at(1).asStringArray();
                if (cols != null && cols.length == ncol) {
                    colNames = new ArrayList<String>();
                    for (String col : cols) {
                        colNames.add(col);
                    }
                }
            }
        }
    }

    @SuppressWarnings("PMD.NPathComplexity")
    private void createLogicalMatrix(final REXP output) throws BusinessException {
        try {
            REXP dimb = output.getAttribute("dim");

            if (dimb == null || dimb.asIntegers().length != 2) {
                LOGGER.debug("In RMatrix createLogicalMatrix dim not equal to XT_ARRAY_INT and size 2");
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[]{getClass().getName(), "REXP"});
            }

            int[] dsb = dimb.asIntegers();
            int nrowb = dsb[0];
            int ncolb = dsb[1];
            int[] intBArray = output.asIntegers();
            Boolean[] blnArray = new Boolean[intBArray.length];

            for (int i = 0; i < intBArray.length; i++) {

                if (intBArray[i] == 0 || intBArray[i] == 1) {
                    blnArray[i] = intBArray[i] == 1 ? true : false;
                }
            }

            // get attribute here - byrow.Not coming from JRI. Currently assuming default: byrow=false, i.e. fill it over rows first
            // then column

            createMatrixFrom2DArray(fillMatrixFromArray(blnArray, nrowb, ncolb, false), null);

            REXP dimnamesbln = output.getAttribute("dimnames");
            if (dimnamesbln != null) {
                org.rosuda.REngine.RList jriList = dimnamesbln.asList();
                // at(index) does not throw Array Index OOB exception, just returns null for missing elements
                if (jriList != null && jriList.at(0) != null && jriList.at(1) != null) {
                    String[] rows = jriList.at(0).asStrings();
                    if (rows != null && rows.length == nrowb) {
                        rowNames = new ArrayList<String>();
                        for (String row : rows) {
                            rowNames.add(row);
                        }
                    }
                    String[] cols = jriList.at(1).asStrings();
                    if (cols != null && cols.length == ncolb) {
                        colNames = new ArrayList<String>();
                        for (String col : cols) {
                            colNames.add(col);
                        }
                    }
                }
            }
        } catch(REXPMismatchException e) {
            LOGGER.debug("In RMatrix createCharacterMatrix dim not equal to XT_ARRAY_INT and size 2");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[]{getClass().getName(), "REXP"});
        }

    }
    
    @SuppressWarnings("PMD.NPathComplexity")
    private void createLogicalMatrix(final org.rosuda.JRI.REXP output) throws BusinessException {
    	org.rosuda.JRI.REXP dimb = output.getAttribute("dim");

        if (dimb == null || dimb.getType() != org.rosuda.JRI.REXP.XT_ARRAY_INT || dimb.asIntArray() == null || dimb.asIntArray().length != 2) {
            LOGGER.debug("In RMatrix createLogicalMatrix dim not equal to XT_ARRAY_INT and size 2");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" });
        }

        int[] dsb = dimb.asIntArray();
        int nrowb = dsb[0];
        int ncolb = dsb[1];
        int[] intBArray = output.asIntArray();
        Boolean[] blnArray = new Boolean[intBArray.length];

        for (int i = 0; i < intBArray.length; i++) {

            if (intBArray[i] == 0 || intBArray[i] == 1) {
                blnArray[i] = intBArray[i] == 1 ? true : false;
            }
        }

        // get attribute here - byrow.Not coming from JRI. Currently assuming default: byrow=false, i.e. fill it over rows first
        // then column

        createMatrixFrom2DArray(fillMatrixFromArray(blnArray, nrowb, ncolb, false), null);

        org.rosuda.JRI.REXP dimnamesbln = output.getAttribute("dimnames");
        if (dimnamesbln != null) {
            org.rosuda.JRI.RList jriList = dimnamesbln.asList();
            // at(index) does not throw Array Index OOB exception, just returns null for missing elements
            if (jriList != null && jriList.at(0) != null && jriList.at(1) != null) {
                String[] rows = jriList.at(0).asStringArray();
                if (rows != null && rows.length == nrowb) {
                    rowNames = new ArrayList<String>();
                    for (String row : rows) {
                        rowNames.add(row);
                    }
                }
                String[] cols = jriList.at(1).asStringArray();
                if (cols != null && cols.length == ncolb) {
                    colNames = new ArrayList<String>();
                    for (String col : cols) {
                        colNames.add(col);
                    }
                }
            }
        }

    }

    // MS:End utility functions

    /**
     * Given an Object 1D array, fills it into a 2D array with specified rows and cols, by row or by column Currently R default is
     * byrow=false
     *
     * @param objArray
     *            1D object array
     * @param nrow
     *            - number of rows
     * @param ncol
     *            number of cols
     * @param byRow
     *            - if false, fills per column, each row. If true, fills per row, each column
     *
     * @return 2D Object array
     **/
    // MS: Skipping PMD check because we are reshaping an array not just copying from one to another
    private Object[][] fillMatrixFromArray(final Object[] objArray, final int nrow, final int ncol, final boolean byRow) {
        Object[][] objMatrix = new Object[nrow][ncol];
        int index = 0;

        if (!byRow) {
            // default
            for (int j = 0; j < ncol; j++) // NOPMD
            {
                for (int i = 0; i < nrow; i++) // NOPMD
                {
                    objMatrix[i][j] = objArray[index];
                    index++;
                }
            }
        } else {
            for (int i = 0; i < nrow; i++) // NOPMD
            {
                for (int j = 0; j < ncol; j++) // NOPMD
                {
                    objMatrix[i][j] = objArray[index];
                    index++;
                }
            }
        }

        return objMatrix;
    }

    /**
     * Creates a new RMatrix object.
     *
     * @param hmValue
     *            - HashMap
     *
     * @throws BusinessException
     *             - if type is null or unsupported - if data is null or not list of objects or has no rows or columns - if data
     *             is not a perfect matrix - if data has components of a different data type than specified type - if row and
     *             column names are not null and not strings - if row and column name arrays are not of correct lengths - if names
     *             element has more than 2 sub arrays - if data is null or rDataType is null or unsupported - if hashmap is null
     * 
     **/
    public RMatrix(final Map<String, Object> hmValue, final boolean stringsAsFactors) throws BusinessException {
        super();
        this.stringsAsFactors = stringsAsFactors;
        if (hmValue != null) {
            Object oObjType = hmValue.get(LangTypeConstants.R_MATRIX_TYPE);

            if (!(oObjType instanceof String)) {
                LOGGER.debug("In RMatrix oObjType not string");
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                        LangTypeConstants.R_MATRIX_TYPE });
            } else {
                elementDataType = RDataTypes.getTypeEnumFromName((String) oObjType);
            }

            Object oType = hmValue.get(LangTypeConstants.R_DATA_TYPE);
            Object oData = hmValue.get(LangTypeConstants.R_MATRIX_DATA);
            Object oNames = hmValue.get(LangTypeConstants.R_MATRIX_NAMES);

            if (oData instanceof List<?> && oType instanceof String && oType.equals(getRDataType().getName())) {
                List<Object> objs = (ArrayList<Object>) oData;

                rows = objs.size();
                cols = -1;

                for (int i = 0; i < objs.size(); i++) {
                    if (!(objs.get(i) instanceof List<?>)) {
                        LOGGER.debug("In RMatrix oObjs not list");
                        throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                                LangTypeConstants.R_MATRIX_DATA });
                    }

                    List<Object> subList = (List<Object>) objs.get(i);

                    if (cols == -1) {
                        cols = subList.size();
                    } else {
                        // the incoming array should be a perfect matrix, if not, throw a BusinessException
                        if (cols != subList.size()) {
                            LOGGER.debug("In RMatrix cols size not equal");
                            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                                    LangTypeConstants.R_MATRIX_DATA });
                        }
                    }
                }

                if (rows == 0 || cols == -1) {
                    LOGGER.debug("In RMatrix cols equals -1 and rows zero ");
                    throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                            LangTypeConstants.R_MATRIX_DATA });
                }

                matrix = new DataType[rows][cols];

                for (int i = 0; i < rows; i++) {
                    List<Object> subList = (List<Object>) objs.get(i);

                    for (int j = 0; j < cols; j++) {

                        if (subList.get(j) != null) {
                            // TODO:MS ----????
                            AbstractRType element =  AbstractRType.createRDataTypeFromObject((FieldInfo) subList.get(j));
                           if (element!= null){
                            if (element.getRDataType() == elementDataType) {
                                // MS: preserve nulls without type checking
                                matrix[i][j] = element;
                            } else {
                                // MS: if this is a mix of RNumerics and RIntegers, it comes here because few items are returned
                                // as RIntegers which is not equal to "numeric" vector type.
                                // For this one case only, convert any RIntegers to RNumerics if the vector type is numeric.
                                // No other datatypes are auto converted, for everything else, throw an exception
                                if (element instanceof RInteger && ((String) oObjType).equals(RDataTypes.R_NUMERIC.getName())) {
                                  //  int intValue = ((Integer) element.getPrimitive()).intValue();
                                    element = new RNumeric((Double) element.getPrimitive()); // checked cast so I know what I'm doing
                                    matrix[i][j] = element;
                                } else {
                                    LOGGER.debug("In RMatrix element not integer ");
                                    throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] {
                                            getClass().getName(), LangTypeConstants.R_MATRIX_DATA });
                                }
                            }
                           }
                        }
                    }
                }

                // if dimension names are not null then consider this else just ignore
                if (oNames != null) {
                    if (oNames instanceof List<?> && ((List<Object>) oNames).size() == 2) {
                        List<Object> objNames = (List<Object>) oNames;

                        // names should have 2 non-null elements which are lists of size rows and cols respectively
                        if (objNames.get(0) instanceof List<?> && ((List<Object>) objNames.get(0)).size() == rows
                                && objNames.get(1) instanceof List<?> && ((List<Object>) objNames.get(1)).size() == cols) {
                            List<Object> objRows = (List<Object>) objNames.get(0);

                            for (int i = 0; i < rows; i++) {
                                // suppress PMD as this null check is required. If object is null we DONT want to raise exception
                                if (objRows.get(i) != null && !(objRows.get(i) instanceof String)) // NOPMD
                                {
                                    LOGGER.debug("In RMatrix objrows not string ");
                                    throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] {
                                            getClass().getName(), LangTypeConstants.R_MATRIX_NAMES });
                                }

                            }

                            List<Object> objCols = (List<Object>) objNames.get(1);

                            for (int i = 0; i < cols; i++) {
                                // suppress PMD as this null check is required. If object is null we DONT want to raise exception
                                if (objCols.get(i) != null && !(objCols.get(i) instanceof String)) // NOPMD
                                {
                                    LOGGER.debug("In RMatrix objcols not string ");
                                    throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] {
                                            getClass().getName(), LangTypeConstants.R_MATRIX_NAMES });
                                }

                            }

                            // at this point we have established that objRows and objCols are the right size and filled with right
                            // type or nulls
                            rowNames = new ArrayList<String>();
                            colNames = new ArrayList<String>();

                            for (Object o : objRows) {
                                rowNames.add((String) o);
                            }

                            for (Object o : objCols) {
                                colNames.add((String) o);
                            }
                        } else {
                            LOGGER.debug("In RMatrix rows and cols not list or size not matching ");
                            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                                    LangTypeConstants.R_MATRIX_NAMES });
                        }
                    } else {
                        LOGGER.debug("In RMatrix onames not list or size not 2 ");
                        throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                                LangTypeConstants.R_MATRIX_NAMES });
                    }
                }
            } else {
                LOGGER.debug("In RMatrix odata not list or instance of string ");
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                        LangTypeConstants.R_MATRIX_DATA + "/" + LangTypeConstants.R_DATA_TYPE });
            }
        } else {
            LOGGER.debug("In RMatrix hmvalue stringasfactors null ");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "HashMap" });
        }
    }

    /**
     * Returns R string representation (always represents in byrow=false format)
     *
     * @return String
     **/
    @Override
    public String toNative() {
        String strRet = null;

        // as matrix() in R creates a 1x1 array with element NA, we dont allow a Rmatrix with rows and cols 0, as this does not
        // translate to matrix(), so we will not check here

        // MS: by default, it sends data to R in byrow=false i.e it sends data by column (to be consistent with R defaults)
        StringBuffer sb = new StringBuffer(getRDataType().getName());
        String s = "(c(";
        sb.append(s);

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                if (matrix[i][j] == null) {
                    String na = "NA";
                    sb.append(na);
                } else {
                    sb.append(matrix[i][j].toNative());
                }

                if (i < rows - 1 || j < cols - 1) {
                    String comma = ",";
                    sb.append(comma);
                }
            }
        }

        String tmp = "),nrow=" + rows + ",ncol=" + cols;
        sb.append(tmp);

        if (rowNames != null && !rowNames.isEmpty() || colNames != null && !colNames.isEmpty()) {
            try {
                String dimnames = ",dimnames=";
                sb.append(dimnames);

                RVector vector = null;
                List<RVector> vecList = new ArrayList<RVector>();

                List<Object> objRows = new ArrayList<Object>();
                List<Object> objCols = new ArrayList<Object>();

                objRows.addAll(rowNames);
                objCols.addAll(colNames);

                vector = new RVector(objRows, RDataTypes.R_CHARACTER);
                vecList.add(vector);

                vector = new RVector(objCols, RDataTypes.R_CHARACTER);
                vecList.add(vector);

                RList list = new RList(vecList, null, stringsAsFactors);
                sb.append(list.toNative());
            } catch (BusinessException be) {
                LOGGER.debug("This is never going to happen because we have checked and created the dimNames");
            }
        }

        String bracket = ")";
        sb.append(bracket);

        strRet = sb.toString();

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
     * Returns "matrix"
     *
     * @return "matrix"
     **/
    @Override
    public RDataTypes getRDataType() {
        return RDataTypes.R_MATRIX;
    }

    /**
     * Plain object representation
     *
     * @return HashMap
     **/
    // @Override
    // public Object toJava()
    // {
    // HashMap<String, Object> hmValues = new HashMap<String, Object>();
    // hmValues.put(LangTypeConstants.R_DATA_TYPE, getRDataType().getName());
    // hmValues.put(LangTypeConstants.R_MATRIX_TYPE, elementDataType.getName());
    //
    // Object[][] objs = new Object[rows][cols];
    //
    // for (int i = 0; i < rows; i++)
    // {
    // for (int j = 0; j < cols; j++)
    // {
    // if (matrix[i][j] != null) {
    // objs[i][j] = matrix[i][j].toJava();
    // }
    // }
    // }
    //
    // hmValues.put(LangTypeConstants.R_MATRIX_DATA, objs);
    //
    // if (rowNames != null || colNames != null)
    // {
    // List<Object> lstNames = new ArrayList<Object>();
    // lstNames.add(rowNames);
    // lstNames.add(colNames);
    //
    // hmValues.put(LangTypeConstants.R_MATRIX_NAMES, lstNames);
    // }
    //
    // return hmValues;
    // }

    @Override
    public FieldInfo toUmgType(final String name, final String sequence) {
        final FieldInfo fi = new FieldInfo();
        fi.setCollection(true);
        fi.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
        fi.setModelParameterName(name);
        fi.setSequence(sequence);
        fi.setNativeDataType(getRDataType().getName());

        final List<Object> objs = new ArrayList<Object>();

        for (int i = 0; i < rows; i++) {
            final List<Object> subObjs = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                subObjs.add(matrix[i][j].getPrimitive());
            }
            objs.add(subObjs);
        }

        final ArrayList<Object> alValues = new ArrayList<Object>();

        FieldInfo f = new FieldInfo();
        f.setCollection(true);
        f.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
        f.setModelParameterName(LangTypeConstants.R_MATRIX_DATA);
        f.setValue(objs.toArray());
        f.setSequence(sequence);

        alValues.add(f);

        if (CollectionUtils.isNotEmpty(rowNames)) {
            f = new FieldInfo();
            f.setCollection(false);
            f.setDataType(com.ca.umg.modelet.common.DataType.STRING.getUmgType());
            f.setModelParameterName(LangTypeConstants.R_MATRIX_ROW_NAMES);
            f.setValue(rowNames.toArray());

            alValues.add(f);
        }

        if (CollectionUtils.isNotEmpty(colNames)) {
            f = new FieldInfo();
            f.setCollection(false);
            f.setDataType(com.ca.umg.modelet.common.DataType.STRING.getUmgType());
            f.setModelParameterName(LangTypeConstants.R_MATRIX_COL_NAMES);
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

    public RMatrix(final List<Object> hmValue, final boolean stringsAsFactors) throws BusinessException {
        super();
        this.stringsAsFactors = stringsAsFactors;
        if (hmValue != null) {

            HashMap<String, Object> attribute;
            Object[][] objMatrix = null;
            List<Object> oNames = null;
            List<Object> rowNames;
            List<Object> colNames;

            for (final Object objAttribute : hmValue) {
                attribute = (HashMap<String, Object>) objAttribute;
                if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_MATRIX_DATA)) {
                    elementDataType = RDataTypes.getTypeEnumFromName(attribute.get("dataType").toString());
                    objMatrix = marshallData((List<Object>) attribute.get("value"));
                } else if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_MATRIX_ROW_NAMES)) {
                    rowNames = marshallRowNames((List<Object>) attribute.get("value"));
                    if (rowNames != null) {
                        if (oNames == null) {
                            oNames = new ArrayList<>(2);
                            oNames.add(0, null);
                            oNames.add(1, null);
                        }

                        oNames.set(0, rowNames);
                    }
                } else if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_MATRIX_COL_NAMES)) {
                    colNames = marshallColNames((List<Object>) attribute.get("value"));
                    if (colNames != null) {
                        if (oNames == null) {
                            oNames = new ArrayList<>(2);
                            oNames.add(0, null);
                            oNames.add(1, null);
                        }

                        oNames.set(1, colNames);
                    }
                }
            }

            createMatrixFrom2DArray(objMatrix, oNames);
        } else {
            LOGGER.debug("In RMatrix list<Object> hmValue stringasfactors null ");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(),
                    "FieldInfo value" });
        }
    }

    private Object[][] marshallData(final List<Object> hmValue) throws BusinessException {
        final int row = hmValue.size();
        final int col = ((List<?>) hmValue.get(0)).size();
        final Object[][] objMatrix = new Object[row][col];

        int r = 0;
        int c = 0;
        for (Object first : hmValue) {
            c = 0;
            final List secondValue = (List) first;
            for (Object value : secondValue) {
                objMatrix[r][c] = createRDataTypeFromObject(new FieldInfo(value), stringsAsFactors);
                c++;
            }
            r++;
        }

        return objMatrix;
    }

    private List<Object> marshallRowNames(final List<Object> hmValue) throws BusinessException {
        return convertValueToList(hmValue);
    }

    private List<Object> marshallColNames(final List<Object> hmValue) throws BusinessException {
        return convertValueToList(hmValue);
    }

    private List<Object> convertValueToList(final List<Object> hmValue) throws BusinessException {
        if (hmValue == null) {
            return null;
        } else {
            final List<Object> names = new ArrayList<Object>();

            for (Object value : hmValue) {
                names.add(value);
            }

            return names;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public Map<String, Object> toUmgType1(final String name, final String sequence) {
        final List<Object> objs = new ArrayList<Object>();

        for (int i = 0; i < rows; i++) {
            final List<Object> subObjs = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                subObjs.add(matrix[i][j].getPrimitive());
            }
            objs.add(subObjs);
        }

        final ArrayList<Object> alValues = new ArrayList<Object>();

        alValues.add(createData(objs, sequence));

        if (CollectionUtils.isNotEmpty(rowNames)) {            
            alValues.add(createDimRowNames());
        }

        if (CollectionUtils.isNotEmpty(colNames)) {
            alValues.add(createDimColNames());
        }

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
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.TRUE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), alValues.toArray());	

        return newElement;
    }
    
    private Map<String, Object> createData(final List<Object> objs, final String sequence) {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_MATRIX_DATA);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), sequence);
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.TRUE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), objs.toArray());	

		return newElement;
	}
	
	private Map<String, Object> createDimRowNames() {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);

		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_MATRIX_ROW_NAMES);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "2");
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.STRING.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), rowNames.toArray());	

		return newElement;
	}
	
	private Map<String, Object> createDimColNames() {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_MATRIX_COL_NAMES);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "3");
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.STRING.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), colNames.toArray());	

		return newElement;
	}

}