/*
 * MappingTransformer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.ioconverter;


import static com.ca.framework.core.ioreduce.BooleanValueEnum.getBooleanValueEnum;
import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.FieldInfo;

@SuppressWarnings({ "PMD"})
public class ModeletResponseReducer {

    
    public static Map<String, Object> reduceModelInputField(final Map<String, Object> row) {
		final Map<String, Object> reducedRow = new LinkedHashMap<String, Object>();
		
//		reducedRow.put("fieldName", row.get("fieldName"));
//		reducedRow.put("value", row.get("value"));
		
		if (!row.containsKey("p")) {
			final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
			
			final Object dummy = new Object();
			for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
				parameterList.add(dummy);
			}
			
			parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), row.get(FieldInfoEnum.FIELD_NAME.getName()));
			parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), row.get(FieldInfoEnum.SEQUENCE.getName()));
			parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), row.get(FieldInfoEnum.DATA_TYPE.getName()));
			parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), row.get(FieldInfoEnum.NATIVE_DATA_TYPE.getName()));
			parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), row.get(FieldInfoEnum.COLLECTION.getName()));
			parameterList.set(FieldInfoEnum.VALUE.getIndex(), row.get(FieldInfoEnum.VALUE.getName()));
			
//			parameterList.add(row.get("sequence"));
//			parameterList.add(row.get("dataType"));
//			parameterList.add(row.get("nativeDataType"));
//			parameterList.add(row.get("collection"));
			
			reducedRow.put("p", parameterList);
		} else {
			reducedRow.put("p", row.get("p"));
		}
		
		return row;
//		return reducedRow;
	}
    
    public static Object getValueFromReduceModelInput(final Map<String, Object> row, final String field) {
    	Object fieldValue = null;
    	if(row.containsKey(field)) {
    		fieldValue = row.get(field);
    	} else {
    		final List<Object> parameterList = (ArrayList<Object>) row.get("p");
    		final FieldInfoEnum fieldInfoEnum = FieldInfoEnum.getFieldInfoEnum(field);
    		fieldValue = parameterList.get(fieldInfoEnum.getIndex());
//    		
//    		if (field.equals("sequence")) {
//    			fieldValue = parameterList.get(0);
//    		} else if (field.equals("dataType")) {
//    			fieldValue = parameterList.get(1);
//    		} else if (field.equals("nativeDataType")) {
//    			fieldValue = parameterList.get(2);
//    		} else if (field.equals("collection")) {
//    			fieldValue = parameterList.get(3);
//    		}
    	}
		
		return fieldValue;
	}
    
    public static void putFieldInRow(final String field, final Object value, final Map<String, Object> row) {
		if (row.containsKey("p")) {
			final List<Object> parameterList = (ArrayList<Object>) row.get("p");
    		final FieldInfoEnum fieldInfoEnum = FieldInfoEnum.getFieldInfoEnum(field);
    		parameterList.set(fieldInfoEnum.getIndex(), value);
		} else {
			row.put("value", value);
		}
    }
    
    
    
    
    
    
    
    
    
    public static List<Map<String, Object>> convert(final Object modelRequestBody) {
    	List<Map<String, Object>> newModelRequestBody = new ArrayList<Map<String, Object>>();
    	
    	if (modelRequestBody != null) {
    		if (modelRequestBody instanceof List) {
    			List<Map<String, Object>> modelRequestBodyList = (List<Map<String, Object>>) modelRequestBody;
        		for (final Object element : modelRequestBodyList) {
        			final List<Object> parameterList = createParameterList(element);
        			final Map<String, Object> newElement = new HashMap<String, Object>();
        			newElement.put("p", parameterList);
        			newModelRequestBody.add(newElement);
        			
        			final Object value = getElementValue(element);
        			
        			if (value instanceof List) {
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), convert(value));
        			} else if (value instanceof Map) {
        				final Map<String, Object> valueMap = new HashMap<String, Object> ();
        				valueMap.put("p", convert(value));
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), valueMap);
        				newModelRequestBody.add(valueMap);
        			} else if (value instanceof Object[] && !isValuePrimitive((Object[]) value)) {
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), convert(value));
        			} else {
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), value);
        			}
        		}
    		} else if (modelRequestBody instanceof Map) {
    			
    		} else if (modelRequestBody instanceof Object[]) {
    			Object[] modelRequestBodyList = (Object[]) modelRequestBody;
    			
        		for (final Object element : modelRequestBodyList) {
        			final List<Object> parameterList = createParameterList(element);
        			final Map<String, Object> newElement = new HashMap<String, Object>();
        			newElement.put("p", parameterList);
        			newModelRequestBody.add(newElement);
        			
        			final Object value = getElementValue(element);
        			
        			if (value instanceof List) {
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), convert(value));
        			} else if (value instanceof Map) {
        				final Map<String, Object> valueMap = new HashMap<String, Object> ();
        				valueMap.put("p", convert(value));
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), valueMap);
        				newModelRequestBody.add(valueMap);
        			} else if (value instanceof Object[]) {
        				if (isDoubleArray((Object[]) value)) {
            				parameterList.set(FieldInfoEnum.VALUE.getIndex(), getDoubleArray((Object[]) value));        					
        				} else if (isStringArray((Object[]) value)) {
        					parameterList.set(FieldInfoEnum.VALUE.getIndex(), getStringArray((Object[]) value)); 
        				} else if (isBooleanArray((Object[]) value)) {
        					parameterList.set(FieldInfoEnum.VALUE.getIndex(), getBooleanArray((Object[]) value)); 
        				}else {
        					parameterList.set(FieldInfoEnum.VALUE.getIndex(), convert(value));
        				}
        			} else {
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), value);
        			}    			
        		}
    		}
    	}
    	
    	return newModelRequestBody;
    }
    
    private static boolean isDoubleArray(final Object[] value) {
    	if (value != null && value.length > 0) {
    		Object valu1One = value[0];
    		if (valu1One instanceof Double) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private static boolean isStringArray(final Object[] value) {
    	if (value != null && value.length > 0) {
    		Object valu1One = value[0];
    		if (valu1One instanceof String) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private static boolean isBooleanArray(final Object[] value) {
    	if (value != null && value.length > 0) {
    		Object valu1One = value[0];
    		if (valu1One instanceof Boolean) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private static Double[] getDoubleArray(final Object[] value) {
		Double[] doubleArray = new Double[value.length];
		int i = 0;
		for (Object v : value) {
			if (v == null) {
				doubleArray[i] = null;
			} else {
				doubleArray[i] = (Double) v;
			}
			
			i++;
		}
		return doubleArray;
    }
    
    private static Boolean[] getBooleanArray(final Object[] value) {
		Boolean[] doubleArray = new Boolean[value.length];
		int i = 0;
		for (Object v : value) {
			if (v == null) {
				doubleArray[i] = null;
			} else {
				doubleArray[i] = Boolean.valueOf(v.toString());
			}
			
			i++;
		}
		return doubleArray;
    }
    
    private static String[] getStringArray(final Object[] value) {
		String[] doubleArray = new String[value.length];
		int i = 0;
		for (Object v : value) {
			if (v == null) {
				doubleArray[i] = null;
			} else {
				doubleArray[i] = v.toString();
			}
			
			i++;
		}
		return doubleArray;
    }
    
    private static List<Object> createParameterList(final Object row) {
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}
		
		if (row instanceof Map) {
			final Map<String, Object> rowMap = (Map<String, Object>) row;
			parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), rowMap.get(FieldInfoEnum.FIELD_NAME.getName()));
			parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), rowMap.get(FieldInfoEnum.SEQUENCE.getName()));
			parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(rowMap.get(FieldInfoEnum.DATA_TYPE.getName()).toString()).getIntValue());
			parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(rowMap.get(FieldInfoEnum.NATIVE_DATA_TYPE.getName()).toString()).getIntValue());
			parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), getBooleanValueEnum(rowMap.get(FieldInfoEnum.COLLECTION.getName()).toString()).getIntValue());
//			parameterList.set(FieldInfoEnum.VALUE.getIndex(), row.get(FieldInfoEnum.VALUE.getName()));	
			parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		} else {
			final FieldInfo fieldInfo = (FieldInfo) row;
			parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), fieldInfo.getModelParameterName());
			parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), fieldInfo.getSequence());
			parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(fieldInfo.getDataType()).getIntValue());
			if (fieldInfo.getNativeDataType() == null) {
				parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
			} else {
				parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(fieldInfo.getNativeDataType().toString()).getIntValue());
			}
			parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), getBooleanValueEnum(String.valueOf(fieldInfo.isCollection())).getIntValue());
//			parameterList.set(FieldInfoEnum.VALUE.getIndex(), row.get(FieldInfoEnum.VALUE.getName()));
			parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		}
		return parameterList;
    }
    
    private static Object getElementValue(final Object row) {
		if (row instanceof Map) {
			final Map<String, Object> rowMap = (Map<String, Object>) row;
			return rowMap.get(FieldInfoEnum.VALUE.getName());						
		} else {
			final FieldInfo fieldInfo = (FieldInfo) row;
			return fieldInfo.getValue();
		}
    }
	
	private static boolean isValuePrimitive(final Object[] value) {
    	boolean flag = false;
    	
    	if (value != null && value.length > 0) {
    		final Object firstElement = value[0];
    		if (firstElement instanceof Double ||
    				firstElement instanceof String ||
    				firstElement instanceof Integer ||
    				firstElement instanceof Boolean ) {
    			flag = true;
    		}
    	}
    	
    	return flag;
    }
    
}
