package com.ca.umg.plugin.commons.excel.xmlconverter;

import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;

import java.util.HashMap;
import java.util.Map;

public final class DataTypeMapUtil {     

	public DataTypeMapUtil() {
		// TODO Auto-generated constructor stub
	}

	private static Map<String, Map<String,String>> dataTypeMap = new HashMap<String, Map<String,String>>();
	
	public static final String DATATYPE_MATRIX = "matrix";
	public static final String DATATYPE_VECTOR = "vector";
    public static final String DATATYPE_DATAFRAME = "data.frame";
	public static final String DATATYPE_FACTOR = "factor";
	public static final String DATATYPE_INTEGER = "integer";
    public static final String DATATYPE_LONG = "long";
    public static final String DATATYPE_BIGINTEGER = "biginteger";
    public static final String DATATYPE_BIGDECIMAL = "bigdecimal";
	public static final String DATATYPE_DOUBLE = "double";
	public static final String DATATYPE_STRING = "string";
	public static final String DATATYPE_BOOLEAN = "boolean";
	public static final String DATATYPE_DATE = "date";
	public static final String DATATYPE_DATETIME = "datetime";
	public static final String DATATYPE_LIST = "list";
	public static final String DATATYPE_NUMERIC = "numeric";
	public static final String DATATYPE_CHARACTER = "character";
	public static final String DATATYPE_LOGICAL = "logical";
	public static final String DATATYPE_OBJECT = "object";
	public static final String DATATYPE_MWNUMERICARRAY = "MWNumericArray";
	public static final String DATATYPE_MWCHARARRAY = "MWCharArray";
	public static final String DATATYPE_MWLOGICALARRAY = "MWLogicalArray";
	public static final String DATATYPE_MWSTRUCTARRAY = "MWStructArray";
	public static final String DATATYPE_CURRENCY = "currency";
	public static final String DATATYPE_PERCENTAGE = "percentage";
	
	static {
		final Map<String,String> RDataTypeMap = new HashMap<String, String>();
        RDataTypeMap.put("integer", "integer");
        RDataTypeMap.put("logical", "boolean");
        RDataTypeMap.put("character", "string, date");  
        RDataTypeMap.put("vector", "integer, double, string, boolean, biginteger, bigdecimal, long");
        RDataTypeMap.put("data.frame", "object");
		RDataTypeMap.put("factor", "string");
		RDataTypeMap.put("matrix", "integer, double, string, boolean, biginteger, bigdecimal, long");
        RDataTypeMap.put("list", "object");
        RDataTypeMap.put("numeric", "integer, double, biginteger, bigdecimal, long");
        // RDataTypeMap.put("character", "character");
        // RDataTypeMap.put("boolean", "logical");
		dataTypeMap.put(ExcelConstants.R_LANG, RDataTypeMap);
	}

	public static Map<String, Map<String, String>> getDataTypeMap() {
		return dataTypeMap;
	}

	public static void setDataTypeMap(Map<String, Map<String, String>> dataTypeMap) {
		DataTypeMapUtil.dataTypeMap = dataTypeMap;
	}
	
	
}
