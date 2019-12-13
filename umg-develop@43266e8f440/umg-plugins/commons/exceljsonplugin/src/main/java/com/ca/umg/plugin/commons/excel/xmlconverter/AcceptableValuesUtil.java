package com.ca.umg.plugin.commons.excel.xmlconverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author basanaga
 *
 * This class used to validate the default value against acceptable values
 *
 */
public class AcceptableValuesUtil {	
	/**
	 * This method used to validate the default value against acceptable values
	 * 
	 * @param defaultValue
	 * @param acceptableValues
	 * @param datatype
	 * @return
	 */
	public static boolean isDefaultValinAcceptValues(Object defaultValue,Object[] acceptableValues,String datatype) {
		boolean isPresent = false;
		if(acceptableValues!=null){	
			for(Object acceptableValue : acceptableValues){
				if(StringUtils.equalsIgnoreCase(datatype,DataTypeMapUtil.DATATYPE_INTEGER)){
					Number  acceptableValNum = (Number)acceptableValue;
					if(defaultValue.equals(acceptableValNum.intValue())){
						isPresent = Boolean.TRUE;	
						break;
					}					
				}else if(StringUtils.equalsIgnoreCase(datatype,DataTypeMapUtil.DATATYPE_BIGINTEGER)){
					Object defValObj = defaultValue;
					if(defaultValue instanceof Integer){
						defValObj = BigInteger.valueOf(((Integer)defaultValue).intValue());					
					} else if(defaultValue instanceof Long){
						defValObj = BigInteger.valueOf(((Long)defaultValue).longValue());					
					} else if(acceptableValue instanceof Number ) {		
						defValObj = BigInteger.valueOf( ((Number) defaultValue).longValue());
					}
					Object acceptaValObj = acceptableValue;
					if(acceptableValue instanceof Integer){
						acceptaValObj = BigInteger.valueOf(((Integer)acceptableValue).intValue());					
					} else if(acceptableValue instanceof Long){
						acceptaValObj = BigInteger.valueOf(((Long)acceptableValue).longValue());					
					} else if(acceptableValue instanceof Number ) {		
						acceptaValObj = BigInteger.valueOf( ((Number) acceptableValue).longValue());
					}					
					if(defValObj.equals(acceptaValObj)){
						isPresent = Boolean.TRUE;			
						break;
					}		
				}
				else if(StringUtils.equalsIgnoreCase(datatype,DataTypeMapUtil.DATATYPE_LONG)){
					Object defValObj = defaultValue;
					if(defaultValue instanceof Integer){
						defValObj = Long.valueOf(((Integer)defaultValue).intValue());					
					}else if(defaultValue instanceof Number){						
						defValObj =((Number)defaultValue).longValue();
					}	
					Object acceptaValObj = acceptableValue;
					if(acceptableValue instanceof Integer){
						acceptaValObj = Long.valueOf(((Integer)acceptableValue).intValue());					
					}else if(acceptableValue instanceof Number){						
						acceptaValObj =(((Number)acceptableValue)).longValue();
					}	
					if(defValObj.equals(acceptaValObj)){
						isPresent = Boolean.TRUE;			
						break;
					}	
				}
				else if(StringUtils.equalsIgnoreCase(datatype,DataTypeMapUtil.DATATYPE_DOUBLE)){
					Object defValObj = defaultValue;
					if(defaultValue instanceof Integer){
						defValObj = Double.valueOf(((Integer)defaultValue).intValue());					
					}else if(defaultValue instanceof Long){
						defValObj = Double.valueOf(((Long)defaultValue).longValue());					
					} else if(defaultValue instanceof Number){					
						defValObj = (((Number)defaultValue)).doubleValue();
					}	
					Object acceptaValObj = acceptableValue;
					if(acceptableValue instanceof Integer){
						acceptaValObj = Double.valueOf(((Integer)acceptableValue).intValue());					
					}else if(acceptableValue instanceof Long){
						acceptaValObj = Double.valueOf(((Long)acceptableValue).longValue());					
					} else if(acceptableValue instanceof Number){						
						acceptaValObj = ((Number)acceptableValue).doubleValue();
					}
					if(defValObj.equals(acceptaValObj)){
						isPresent = Boolean.TRUE;	
						break;
					}					
				}
				else if(StringUtils.equalsIgnoreCase(datatype,DataTypeMapUtil.DATATYPE_BIGDECIMAL)){					
					Object defValObj = defaultValue;
					if(defaultValue instanceof Integer){
						defValObj = BigDecimal.valueOf(((Integer)defaultValue).intValue());					
					}else if(defaultValue instanceof Long){
						defValObj = BigDecimal.valueOf(((Long)defaultValue).longValue());					
					}else if(defaultValue instanceof Double){
						defValObj = BigDecimal.valueOf(((Double)defaultValue).doubleValue());					
					}	
					
					Object acceptaValObj = acceptableValue;
					if(acceptaValObj instanceof Integer){
						acceptaValObj = BigDecimal.valueOf(((Integer)acceptableValue).intValue());					
					}else if(acceptaValObj instanceof Long){
						acceptaValObj = BigDecimal.valueOf(((Long)acceptableValue).longValue());					
					}else if(acceptaValObj instanceof Double){
						acceptaValObj = BigDecimal.valueOf(((Double)acceptableValue).doubleValue());					
					}	
					if(defValObj.equals(acceptaValObj)){
						isPresent = Boolean.TRUE;	
						break;
					}				
				
				}
				else if(StringUtils.equalsIgnoreCase(datatype,DataTypeMapUtil.DATATYPE_STRING)){	
					String val = (String) acceptableValue;
					String defaultVal = ((String) defaultValue);
					if (defaultVal.contains("\"")
							&& StringUtils.equalsIgnoreCase(defaultVal.substring(1, defaultVal.length() - 1), val)) {
						isPresent = Boolean.TRUE;
						break;
					} else if (StringUtils.equalsIgnoreCase(defaultVal, val)) {
						isPresent = Boolean.TRUE;
						break;
					}				
										
				}else if(StringUtils.equalsIgnoreCase(datatype,DataTypeMapUtil.DATATYPE_BOOLEAN)){	
					Boolean isTrue = (Boolean)acceptableValue;
					if(defaultValue.equals(isTrue.booleanValue())){
						isPresent = Boolean.TRUE;	
						break;
					}					
				}
			}
		}
		return isPresent;		
	 
	}
	
	/**
	 * This method used to validate multi dimensional default value against acceptable values
	 * 
	 * @param defaultValue
	 * @param datatype
	 * @param name
	 * @param sheetName
	 * @param convertedArray
	 * @param errorList
	 * @param arrayVal
	 * @param errorMsg
	 */
	public static void acceptValueCheckForArray(String defaultValue, String datatype, String name, String sheetName,
			Object[] convertedArray, List<String> errorList, Object[] arrayVal,String errorMsg) {
		for(Object obj  :arrayVal){
			if(obj!=null){
				if(obj.getClass().isArray()){
					acceptValueCheckForArray(defaultValue,datatype,name,sheetName,convertedArray,errorList,(Object[])obj,errorMsg);		
				
				}else {
					if(convertedArray!=null && !AcceptableValuesUtil.isDefaultValinAcceptValues(obj, convertedArray,datatype)){
						errorList.add(String.format(errorMsg,name,sheetName,obj,Arrays.asList(convertedArray)));
					}	
				}
		}
		}
	}
	
	/**
	 * This method used to validate the multidimensional default value array against acceptable values
	 * 
	 * @param value
	 * @param dataType
	 * @param validationMessage
	 * @param convertedArray
	 * @param error
	 * @return
	 */
	public static void acceptableValueCheckForArray(Object value, String dataType, StringBuilder validationMessage,
			Object[] convertedArray) {
		
		if(value instanceof List){
        	List<Object> valueList =(List<Object>) value;
        	for(Object obj : valueList){    
        		if(obj instanceof List){
        			acceptableValueCheckForArray(obj,dataType,validationMessage, convertedArray);
        		
        		}else{
        		  if(obj!=null && convertedArray!=null && !AcceptableValuesUtil.isDefaultValinAcceptValues(obj, convertedArray, dataType)){  
        			  validationMessage.append(String.format("Actual value is not from the acceptable_values defined. Actual value received is %s and acceptable values are %s",obj,Arrays.asList(convertedArray)));        	        	     	
        	        }   
        		}
        	}      	
        }	
	}
	
}