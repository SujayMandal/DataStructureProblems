/*
 * LangTypeConstants.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.lang.type;

/**
 * The following strings are keys used in the plain Object (HashMap) representations of Native wrapper data types
 * **/

public final class LangTypeConstants
{
	// Required field indicating data type of element being represented
	// Allowed values: "numeric", "integer", "logical", "character","complex", "list", "data.frame", "vector", "matrix", "array")
	public static final String R_DATA_TYPE = "rDataType";

	//workaround for PMD check, avoiding multiple duplicate literals
	private static final String DATA = "data";

	// Keys for Complex R Data Type
	// Required Real component of the Complex number
	public static final String R_COMPLEX_REAL      = "real";
	// Required Imaginary component of the Complex number
	public static final String R_COMPLEX_IMAGINARY = "imaginary";

	// Keys for List / Named List R Data Type
	// Required List of Objects representing data of List
	public static final String R_LIST_DATA  = DATA;
	// Optional List of names for each component of named List
	public static final String R_LIST_NAMES = "names";

	// Keys for Data Frame R Data Type
	// Required List of Objects representing data of Data Frame
	public static final String R_DATA_FRAME_DATA      = DATA;
	// Optional List of row names of Data Frame
	public static final String R_DATA_FRAME_ROW_NAMES = "rownames";
	// Optional List of column names of Data Frame
	public static final String R_DATA_FRAME_COL_NAMES = "colnames";


	// Keys for Vector R Data Type
	// Required List of Objects representing data of Vector
	public static final String R_VECTOR_DATA = DATA;
	// Required field indicating data type of elements in vector
	// Allowed values: "numeric", "integer", "logical", "character"
	public static final String R_VECTOR_TYPE = "type";


	// Keys for Matrix R Data Type
	// Required List of Objects representing data of Matrix
	public static final String R_MATRIX_DATA  = DATA;
	// Required field indicating data type of elements in Matrix
	// Allowed values: "numeric", "integer", "logical", "character"
	public static final String R_MATRIX_TYPE  = "type";
	// Optional 2D array with 2 rows - index 0 for list of row names, index 1 for list of column names
	public static final String R_MATRIX_NAMES = "names";
	public static final String R_MATRIX_ROW_NAMES = "rownames";
	public static final String R_MATRIX_COL_NAMES = "colnames";

	// Keys for Array (N-dimensional) R Data Type
	// Required List of Objects representing data of Array
	public static final String R_ARRAY_DATA  = DATA;
	// Optional 2D array with 1 row per dimension - each row contains list of names for each element in that dimension
	public static final String R_ARRAY_NAMES = "names";

	public static final String R_ARRAY_ROW_NAMES = "rownames";
	public static final String R_ARRAY_COL_NAMES = "colnames";


	// Keys for Factor R Data Type
	// Required List of Objects representing data of Factor
	public static final String R_FACTOR_DATA  = DATA;
	// Optional label array
	public static final String R_FACTOR_LABELS = "labels";

	private LangTypeConstants() { }
}
