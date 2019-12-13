package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.xmlconverter.AcceptableValuesUtil;
import com.ca.umg.plugin.commons.excel.xmlconverter.DataTypeMapUtil;

@Named
public class DatatypeValidator {
	private static final String DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION = "Default value of API Parameter <b>%s</b> in %s sheet must be from the acceptable_values defined. Default value is %s and acceptable values are %s";
	private static final Logger LOGGER = LoggerFactory.getLogger(DatatypeValidator.class);
	
	protected void validatePrimitiveValue(String sheetName, List<String> errorList, Cell defaultCellValue, String dataType,
			String columnName, String name,Object[] convertArray,String errorMsg) {		   
		switch (dataType.toLowerCase(Locale.ENGLISH)) {
		case DataTypeMapUtil.DATATYPE_INTEGER:
			validateIntegerValues(defaultCellValue, columnName, name, errorList, sheetName,errorMsg,convertArray);	
			break;
		case DataTypeMapUtil.DATATYPE_BIGINTEGER:
			validateBigIntegerValues(defaultCellValue, columnName, name, errorList, sheetName,errorMsg,convertArray);
			break;						
		case DataTypeMapUtil.DATATYPE_LONG:
			validateLongValues(defaultCellValue, columnName, name, errorList, sheetName,errorMsg,convertArray);
			break;
		case DataTypeMapUtil.DATATYPE_BOOLEAN:
			validateBooleanFlag(defaultCellValue, columnName, name, errorList, sheetName,errorMsg,convertArray);
			break;
		case DataTypeMapUtil.DATATYPE_STRING:
			validateStringValues(defaultCellValue, columnName, name, errorList, sheetName,errorMsg,convertArray);
			break;
		case DataTypeMapUtil.DATATYPE_DOUBLE:
			validateDoubleValues(defaultCellValue, columnName, name, errorList, sheetName,errorMsg,convertArray);
			break;
		case DataTypeMapUtil.DATATYPE_BIGDECIMAL:
			validateBigDecimalValues(defaultCellValue, columnName, name, errorList, sheetName,errorMsg,convertArray);
			break;

		}		
	}

	public void validateArrayValue(String sheetName, List<String> errorList, Cell dataTypeCellValue,
			String dataType, String columnName, String name, String defaultValue, Object[] convertedArray, String errorMsg) {				
		if (StringUtils.isNotBlank(defaultValue) && !isValidArray(defaultValue,dataType,columnName,name,sheetName,convertedArray,errorList,errorMsg)) {
			errorList.add("The array value set to column : " + columnName + " for variable name : <b>" + name
					+ "</b> in : " + sheetName + " sheet is not valid, Arrays should be in between square brackets, and should use comma separator. All elements in the array should be of "+dataType+" datatype.");
		}
	}	

	public static Boolean cellIsNullOrEmptyWithMsg(Cell cellValue, List<String> errorList, String sheetName,
			String name) {
		Boolean isEmptyOrNull = Boolean.FALSE;
		if (cellValue == null || StringUtils.isBlank(new DataFormatter().formatCellValue(cellValue))) {
			isEmptyOrNull = Boolean.TRUE;
			errorList.add("No value set to column : datatype for variable name : <b>" + name + "</b> in : " + sheetName + " sheet");
		}

		return isEmptyOrNull;
	}

	public static Boolean cellIsNullOrEmpty(Cell cellValue) {
		Boolean isEmptyOrNull = Boolean.FALSE;
		if (cellValue == null || StringUtils.isBlank(new DataFormatter().formatCellValue(cellValue))) {
			isEmptyOrNull = Boolean.TRUE;
		}

		return isEmptyOrNull;
	}

	public boolean isValidArray(String defaultValue,String datatype,String columnName,String name,String sheetName, Object[] convertedArray,List<String> errorList,String errorMsg) {
		boolean isValid = true;
		try {
			Context context = Context.enter();
			Scriptable scope = context.initStandardObjects();			
			Object result = context.evaluateString(scope, defaultValue, "<cmd>", 1, null);
			NativeArray arr = (NativeArray) result;
			Object[] array = new Object[(int) arr.getLength()];
			Object [] arrayVal = convertToArray(null,arr, array, datatype,columnName,name,sheetName,errorList,errorMsg);			
			if(convertedArray!=null && convertedArray.length>0){
				AcceptableValuesUtil.acceptValueCheckForArray(defaultValue, datatype, name, sheetName, convertedArray, errorList, arrayVal,DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION);				
			}
		} catch (EvaluatorException e) {
			LOGGER.debug("EvaluatorException : ",e);
			isValid = false;
		} catch (Exception e) {
			isValid = false;
			LOGGER.debug("Exception : ",e);
		}
		return isValid;
	}

	private void validateIntegerValues(Cell cellValue, String columnName, String name, List<String> errorList,
			String sheetName,String errorMsg,Object[] convertedArray) {
		String defaultValue = new DataFormatter().formatCellValue(cellValue);
		if (!isValidInteger(defaultValue)) {
			if(defaultValue.startsWith("\"") && defaultValue.endsWith("\"")){
				errorList.add(String.format(errorMsg, name, sheetName, "Integer", "integer and value must be between "+Integer.MIN_VALUE+" and "+Integer.MAX_VALUE , StringUtils.substringBetween(defaultValue, "\"") + " in Double Quotes"));
			} else {
				errorList.add(String.format(errorMsg, name, sheetName, "Integer", "integer and value must be between "+Integer.MIN_VALUE+" and "+Integer.MAX_VALUE , defaultValue));
			}
		}else{			
			if(convertedArray!=null && sheetName.equals(ExcelConstants.INPUTS) && !AcceptableValuesUtil.isDefaultValinAcceptValues( new Integer(defaultValue), convertedArray, DataTypeMapUtil.DATATYPE_INTEGER)){				
					errorList.add(String.format(DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION,name,sheetName,defaultValue,Arrays.asList(convertedArray)));			
			}
		}
		
	}
	
	private void validateBigIntegerValues(Cell cellValue, String columnName, String name, List<String> errorList,
			String sheetName,String errorMsg,Object[] convertedArray) {
		String defaultValue = new DataFormatter().formatCellValue(cellValue);
		if (!isValidBigInteger(defaultValue)) {
			if(defaultValue.startsWith("\"") && defaultValue.endsWith("\"")){
				errorList.add(String.format(errorMsg, name, sheetName, "Integer", "biginteger", StringUtils.substringBetween(defaultValue, "\"") + " in Double Quotes"));
			} else {
				errorList.add(String.format(errorMsg, name, sheetName, "Integer", "biginteger", defaultValue));
			}
		}else{
			if(convertedArray!=null && sheetName.equals(ExcelConstants.INPUTS) && !AcceptableValuesUtil.isDefaultValinAcceptValues(new BigInteger(defaultValue), convertedArray, DataTypeMapUtil.DATATYPE_BIGINTEGER)){				
					errorList.add(String.format(DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION,name,sheetName,defaultValue,Arrays.asList(convertedArray)));
				}
			}
		}
	
	private void validateLongValues(Cell cellValue, String columnName, String name, List<String> errorList,
			String sheetName,String errorMsg,Object[] convertedArray) {
		String defaultValue = new DataFormatter().formatCellValue(cellValue);
		if (!isValidLong(defaultValue)) {
			if(defaultValue.startsWith("\"") && defaultValue.endsWith("\"")){
				errorList.add(String.format(errorMsg, name, sheetName, "Integer", "long and value must be between"+Long.MIN_VALUE+" and "+Long.MAX_VALUE, StringUtils.substringBetween(defaultValue, "\"") + " in Double Quotes"));
			} else {
				errorList.add(String.format(errorMsg, name, sheetName, "Integer", "long and value must be between"+Long.MIN_VALUE+" and "+Long.MAX_VALUE, defaultValue));
			}
		}else{
			if(convertedArray!=null && sheetName.equals(ExcelConstants.INPUTS) && !AcceptableValuesUtil.isDefaultValinAcceptValues(new Long(defaultValue), convertedArray, DataTypeMapUtil.DATATYPE_LONG)){				
					errorList.add(String.format(DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION,name,sheetName,defaultValue,Arrays.asList(convertedArray)));
				}
		}
	}

	private void validateBooleanFlag(Cell cellValue, String columnName, String name, List<String> errorList,
			String sheetName,String errorMsg,Object[] convertedArray) {
		String defaultValue = new DataFormatter().formatCellValue(cellValue);
		if(!isValidBoolean(defaultValue)) {
			errorList.add(String.format(errorMsg, name, sheetName, "true or false", "boolean", cellValue));
		}else{
			if(convertedArray!=null && sheetName.equals(ExcelConstants.INPUTS) && !AcceptableValuesUtil.isDefaultValinAcceptValues(new Boolean(defaultValue), convertedArray, DataTypeMapUtil.DATATYPE_BOOLEAN)){				
					errorList.add(String.format(DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION,name,sheetName,defaultValue,Arrays.asList(convertedArray)));
				}
			}
	}

	public void validateForMandateAndSeq(Cell cellValue, String columnName, String name, List<String> errorList,
			String sheetName) {
		String cellVal = new DataFormatter().formatCellValue(cellValue);
		if(!(StringUtils.equalsIgnoreCase(cellVal, "true") || StringUtils.equalsIgnoreCase(cellVal, "false"))) {
			errorList.add(String.format(columnName + " not defined for API Parameter <b>" + name + "</b> in " + sheetName + " sheet."));
		}
	}

	private void validateStringValues(Cell cellValue, String columnName, String name, List<String> errorList,
			String sheetName,String errorMsg,Object[] convertedArray) {
		String defaultValue = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
		if (!(defaultValue.startsWith("\"") && defaultValue.endsWith("\""))) {
			errorList.add(String.format(errorMsg, name, sheetName, "in Double Quotes", "string",defaultValue));
		}else{
			if(convertedArray!=null &&  sheetName.equals(ExcelConstants.INPUTS) && !AcceptableValuesUtil.isDefaultValinAcceptValues( defaultValue, convertedArray, DataTypeMapUtil.DATATYPE_STRING)){				
					errorList.add(String.format(DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION,name,sheetName,defaultValue,Arrays.asList(convertedArray)));
			}
		}
	}

	private void validateDoubleValues(Cell cellValue, String columnName, String name, List<String> errorList,
			String sheetName,String errorMsg,Object[] convertedArray) {
		String defaultValue = new DataFormatter().formatCellValue(cellValue);
		if (!isValidDouble(defaultValue)) {
			if(defaultValue.startsWith("\"") && defaultValue.endsWith("\"")){
				errorList.add(String.format(errorMsg, name, sheetName, "Decimal or Integer", "double and value must be between"+Double.MIN_VALUE+" and "+Double.MAX_VALUE, StringUtils.substringBetween(defaultValue, "\"") + " in Double Quotes"));
			} else {
				errorList.add(String.format(errorMsg, name, sheetName, "Decimal or Integer", "double and value must be between"+Double.MIN_VALUE+" and "+Double.MAX_VALUE, defaultValue));
			}
		}else{
			if(convertedArray!=null && sheetName.equals(ExcelConstants.INPUTS) && !AcceptableValuesUtil.isDefaultValinAcceptValues(new Double(defaultValue), convertedArray, DataTypeMapUtil.DATATYPE_DOUBLE)){
				errorList.add(String.format(DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION,name,sheetName,defaultValue,Arrays.asList(convertedArray)));				
			}
			
		}
	}
	private void validateBigDecimalValues(Cell cellValue, String columnName, String name, List<String> errorList,
			String sheetName,String errorMsg,Object[] convertedArray) {
		String defaultValue = new DataFormatter().formatCellValue(cellValue);
		if (!isValidBigDecimal(defaultValue)) {
			if(defaultValue.startsWith("\"") && defaultValue.endsWith("\"")){
				errorList.add(String.format(errorMsg, name, sheetName, "Decimal or Integer", "bigdecimal",StringUtils.substringBetween(defaultValue, "\"") + " in Double Quotes"));
			} else {
				errorList.add(String.format(errorMsg, name, sheetName, "Decimal or Integer", "bigdecimal", defaultValue));
			}
		}else{
			if(convertedArray!=null && sheetName.equals(ExcelConstants.INPUTS) && !AcceptableValuesUtil.isDefaultValinAcceptValues(new BigDecimal(defaultValue), convertedArray,DataTypeMapUtil.DATATYPE_BIGDECIMAL)){
				errorList.add(String.format(DEFAULT_AND_ACCEPTABLE_VALUE_VALIDATION,name,sheetName,defaultValue,Arrays.asList(convertedArray)));				
			}
			
			
		}
	}

	public static boolean isValidInteger(String defaultValue) {
		boolean isValid = false;	
		if(StringUtils.isNoneBlank(defaultValue)){
			try {  
				Integer.parseInt(defaultValue);	
				isValid = true;				
			} catch (NumberFormatException e) {
				LOGGER.debug("NumberFormatException : ",e);		
			} 
		}
		return isValid;
	}
	

	public static boolean isValidBigInteger(String defaultValue) {
		boolean isValid = false;
		if(StringUtils.isNotBlank(defaultValue)){
			try {
				if(defaultValue.length() > 10 && ! defaultValue.contains(".")){
					Double.valueOf(defaultValue).longValue();
				} else {
					Integer.parseInt(defaultValue); 
					Long.parseLong(defaultValue);			
					new BigInteger(defaultValue);
				}
				isValid = true;
			} catch (NumberFormatException e) {
				LOGGER.debug("NumberFormatException : ",e);			
			}
		}
		return isValid;
	}



	public static boolean isValidLong(String defaultValue) {
		boolean isValid = false;
		if(StringUtils.isNotBlank(defaultValue)){
				try {
					if(defaultValue.length() > 10 && ! defaultValue.contains(".")){
						Double.valueOf(defaultValue).longValue();
					} else {
						Long.parseLong(defaultValue);
					}
					isValid = true;
				} catch (NumberFormatException e) {
					LOGGER.debug("NumberFormatException : ",e);			
				} 
		}
		return isValid;
	}

	public Boolean isValidBoolean(String cellVal) {
		Boolean isBoolean = Boolean.TRUE;
		if (!(StringUtils.equals(cellVal, "true") || StringUtils.equals(cellVal, "false"))) {
			isBoolean = Boolean.FALSE;
		}
		return isBoolean;
	}

	public static boolean isValidDouble(String defaultValue) {
		boolean isValid = false;
		if(StringUtils.isNotBlank(defaultValue)){
		try {
			if(defaultValue.length() > 10 && defaultValue.contains(".")){
					Double.valueOf(defaultValue).doubleValue();
					isValid= true;
				} else {
					if(!defaultValue.contains(".")){
						Integer.parseInt(defaultValue);
						isValid= true;
					}
					Double.parseDouble(defaultValue);
					isValid= true;
				} }catch (NumberFormatException e) {
					LOGGER.debug("NumberFormatException : ",e);				
				}
		}
		return isValid;
	}

	public static boolean isValidBigDecimal(String defaultValue) {
		boolean isValid = false;
		if(StringUtils.isNotBlank(defaultValue)){
			try {
				if(!defaultValue.contains(".")){
					if(defaultValue.length() > 10 ){
						Double.valueOf(defaultValue).longValue();
					} else {
						Integer.parseInt(defaultValue);
						Long.parseLong(defaultValue);			
						new BigInteger(defaultValue);
					} }else{
						if(defaultValue.length() > 10 ){
							Double.valueOf(defaultValue).doubleValue();
						} else {
							Double.parseDouble(defaultValue);
							new BigDecimal(defaultValue);
						}}
				
				isValid = true;
			} catch (NumberFormatException e) {
				LOGGER.debug("NumberFormatException : ",e);
			} 
		}
		return isValid;
	}

	private Object[] convertToArray(Integer parentIndex,NativeArray arr, Object[] array, String dataType,String columnName,String name,String sheetName,List<String> errorList,String errorMsg) {
		for (Object o : arr.getIds()) {
			int index = (Integer) o;
			if (arr.get(index, null) instanceof NativeArray) {
				NativeArray childarr = (NativeArray) arr.get(index, null);
				Object[] childArray = new Object[(int) childarr.getLength()];
				array[index] = convertToArray(index,childarr, childArray, dataType,columnName,name,sheetName,errorList,errorMsg);
			} else {
				if (StringUtils.equalsIgnoreCase(dataType, "DOUBLE")) {
					if(array[index] != null && (StringUtils.containsIgnoreCase(arr.get(index, null).toString() ,"E" ))){
						array[index]  = Double.valueOf(array[index].toString()).doubleValue();
					} else {
					     array[index] = arr.get(index, null);
					}
				} else if (StringUtils.equalsIgnoreCase(dataType, "INTEGER")) {
					if (arr.get(index, null) instanceof Double && ((Double)arr.get(index, null) == 1.0 || (Double)arr.get(index, null) == 0.0) ) {
						array[index] = new Double(Double.parseDouble(arr.get(index, null).toString())).intValue();
					} else {
						if(arr.get(index, null) != null && (StringUtils.containsIgnoreCase(arr.get(index, null).toString() ,"E" ))){
							array[index]  = Double.valueOf(arr.get(index, null).toString()).intValue();
						} else {
						array[index] = arr.get(index, null);
						}
					}
				} else if (StringUtils.equalsIgnoreCase(dataType, "BIGINTEGER") ) {
					if (arr.get(index, null) instanceof Double && ((Double)arr.get(index, null) == 1.0  || (Double)arr.get(index, null) == 0.0)) {                    	
						array[index] = new BigDecimal(Double.parseDouble(arr.get(index, null).toString())).toBigInteger();
					} else {
						if(arr.get(index, null) != null && (StringUtils.containsIgnoreCase(arr.get(index, null).toString() ,"E" ))){
							array[index]  = Double.valueOf(arr.get(index, null).toString()).longValue();
						} else {
						array[index] = arr.get(index, null);
						}
					}
				} else if(StringUtils.equalsIgnoreCase(dataType, "LONG") ){
					if (arr.get(index, null) instanceof Double && ((Double)arr.get(index, null) == 1.0 || (Double)arr.get(index, null) == 0.0 )) {                    	
						array[index] = new Double(Double.parseDouble(arr.get(index, null).toString())).longValue();
					} else {
						if(arr.get(index, null) != null && (StringUtils.containsIgnoreCase(arr.get(index, null).toString() ,"E" ))){
							array[index]  = Double.valueOf(arr.get(index, null).toString()).longValue();
						} else {
						array[index] = arr.get(index, null);
						}
					}
				} else {
					array[index] = arr.get(index, null);
				}
				if (StringUtils.equalsIgnoreCase(dataType, "DOUBLE") && array[index] !=null && !(array[index] instanceof Integer || array[index] instanceof Double)) {	
					if(parentIndex != null){
						errorList.add(String.format(errorMsg, name+"["+parentIndex+"]["+index+"]", sheetName, "Decimal or Integer", "double and value must be between"+Double.MIN_VALUE+" and "+Double.MAX_VALUE,array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));               		
					}else{
						errorList.add(String.format(errorMsg, name+"["+index+"]", sheetName, "Decimal or Integer", "double and value must be between"+Double.MIN_VALUE+" and "+Double.MAX_VALUE,array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));	                		
					}

				} else if (StringUtils.equalsIgnoreCase(dataType, "INTEGER") && array[index] !=null && !(array[index] instanceof Integer)) {
					if(parentIndex != null){
						errorList.add(String.format(errorMsg, name+"["+parentIndex+"]["+index+"]", sheetName, "Integer", "integer and value must be between "+Integer.MIN_VALUE+" and "+Integer.MAX_VALUE, array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));
					}else{
						errorList.add(String.format(errorMsg, name+"["+index+"]", sheetName, "Integer", "integer and value must be between "+Integer.MIN_VALUE+" and "+Integer.MAX_VALUE, array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));

					}
				}  else if (StringUtils.equalsIgnoreCase(dataType, "BIGINTEGER") && array[index] !=null && !(array[index] instanceof Integer || array[index] instanceof Long || array[index] instanceof BigInteger)) {	                	
					if(parentIndex != null){
						errorList.add(String.format(errorMsg, name+"["+parentIndex+"]["+index+"]", sheetName,  "Ingeter", "biginteger", array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));	                		
					}else{
						errorList.add(String.format(errorMsg, name+"["+index+"]", sheetName,  "Ingeter", "biginteger", array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));	                		
					}
				} else if(StringUtils.equalsIgnoreCase(dataType, "LONG") && array[index] !=null && !(array[index] instanceof Integer || array[index] instanceof Long)){
					if(parentIndex != null){
						errorList.add(String.format(errorMsg, name+"["+parentIndex+"]["+index+"]", sheetName,  "Ingeter", "long and value must be between "+Long.MIN_VALUE+" and "+Long.MAX_VALUE, array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));
					}else{
						errorList.add(String.format(errorMsg, name+"["+index+"]", sheetName,  "Ingeter", "long and value must be between "+Long.MIN_VALUE+" and "+Long.MAX_VALUE, array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));	            			
					}
				}else if(StringUtils.equalsIgnoreCase(dataType, "BIGDECIMAL") && array[index] !=null && !(array[index] instanceof Integer || array[index] instanceof Long || array[index] instanceof BigInteger || array[index] instanceof Double || array[index] instanceof BigDecimal  ) ){
					if(parentIndex != null){
						errorList.add(String.format(errorMsg, name+"["+parentIndex+"]["+index+"]", sheetName,  "Decimal or Integer", "bigdecimal", array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));
					}else{
						errorList.add(String.format(errorMsg, name+"["+index+"]", sheetName,  "Decimal or Integer", "bigdecimal", array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));

					}
				}else if(StringUtils.equalsIgnoreCase(dataType, "BOOLEAN") && array[index] !=null && !(array[index] instanceof Boolean) ){
					if(parentIndex != null){
						errorList.add(String.format(errorMsg, name+"["+parentIndex+"]["+index+"]", sheetName, "true or false", "boolean", array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));
					}else{
						errorList.add(String.format(errorMsg, name+"["+index+"]", sheetName, "true or false", "boolean", array[index] instanceof String ? array[index] + " in Double Quotes":array[index]));

					}
				}else if(StringUtils.equalsIgnoreCase(dataType, "STRING") && array[index] !=null && !(array[index] instanceof String)){	  
					if(parentIndex != null){
						errorList.add(String.format(errorMsg,  name+"["+parentIndex+"]["+index+"]", sheetName, "in Double Quotes", "string",array[index]));

					}else{
						errorList.add(String.format(errorMsg,  name+"["+index+"]", sheetName, "in Double Quotes", "string",array[index]));
					}
				}     
			}
		}
		return array;
	}

}