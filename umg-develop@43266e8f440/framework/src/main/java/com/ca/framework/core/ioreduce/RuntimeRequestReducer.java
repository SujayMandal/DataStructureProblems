/*
 * MappingTransformer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.framework.core.ioreduce;


import static com.ca.framework.core.ioreduce.BooleanValueEnum.getBooleanValueEnum;
import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.MapUtil;
import com.mchange.v1.util.MapUtils;

@SuppressWarnings({ "PMD"})
public class RuntimeRequestReducer {

    
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
			parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(row.get(FieldInfoEnum.DATA_TYPE.getName()).toString()).getIntValue());
			parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(row.get(FieldInfoEnum.NATIVE_DATA_TYPE.getName()).toString()).getIntValue());
			parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), getBooleanValueEnum(row.get(FieldInfoEnum.COLLECTION.getName()).toString()).getIntValue());
//			parameterList.set(FieldInfoEnum.VALUE.getIndex(), row.get(FieldInfoEnum.VALUE.getName()));
			parameterList.set(FieldInfoEnum.P.getIndex(),  null);

			
//			parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), row.get(FieldInfoEnum.FIELD_NAME.getName()));
//			parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), row.get(FieldInfoEnum.SEQUENCE.getName()));
//			parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), row.get(FieldInfoEnum.DATA_TYPE.getName()));
//			parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), row.get(FieldInfoEnum.NATIVE_DATA_TYPE.getName()));
//			parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), row.get(FieldInfoEnum.COLLECTION.getName()));
//			parameterList.set(FieldInfoEnum.VALUE.getIndex(), row.get(FieldInfoEnum.VALUE.getName()));
			
//			parameterList.add(row.get("sequence"));
//			parameterList.add(row.get("dataType"));
//			parameterList.add(row.get("nativeDataType"));
//			parameterList.add(row.get("collection"));
			
			reducedRow.put("p", parameterList);
		} else {
			reducedRow.put("p", row.get("p"));
		}
		
//		return row;
		return reducedRow;
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
        		for (final Map<String, Object> element : modelRequestBodyList) {
        			final List<Object> parameterList = createParameterList(element);
        			final Map<String, Object> newElement = new LinkedHashMap<String, Object>();
        			newElement.put("p", parameterList);
        			newModelRequestBody.add(newElement);
        			final Object value = element.get("value");
        			
        			if (value instanceof List && !isValuePrimitive((List<Object>) value)) {
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), convert(value));
        			} else if (value instanceof Map) {
        				final Map<String, Object> valueMap = new LinkedHashMap<String, Object> ();
        				valueMap.put("p", convert(value));
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), valueMap);
        				newModelRequestBody.add(valueMap);
        			} else {
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), value);
        			}
        		}
    		} else if (modelRequestBody instanceof Map) {
    			
    		}
    	}
    	
    	return newModelRequestBody;
    }
    
    private static List<Object> convert1(final Object modelRequestBody) {
    	List<Object> newModelRequestBody = new ArrayList<Object>();
    	
    	if (modelRequestBody != null) {
    		if (modelRequestBody instanceof List) {
    			List<Map<String, Object>> modelRequestBodyList = (List<Map<String, Object>>) modelRequestBody;
        		for (final Map<String, Object> element : modelRequestBodyList) {
        			final List<Object> parameterList = createParameterList(element);
        			newModelRequestBody.add(parameterList);
        			final Object value = element.get("value");
        			
        			if (value instanceof List) {
        				newModelRequestBody.add(parameterList.set(FieldInfoEnum.VALUE.getIndex(), convert(value)));
        			} else if (value instanceof Map) {
        				final Map<String, Object> valueMap = new LinkedHashMap<String, Object> ();
        				valueMap.put("p", "");
        				newModelRequestBody.add(parameterList.set(FieldInfoEnum.VALUE.getIndex(), convert(value)));
        			} else {
        				parameterList.set(FieldInfoEnum.VALUE.getIndex(), value);
        			}
        		}
    		} else if (modelRequestBody instanceof Map) {
    			
    		}
    	}
    	
    	return newModelRequestBody;
    }
    
    private static List<Object> createParameterList(final Map<String, Object> row) {
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}
		
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), row.get(FieldInfoEnum.FIELD_NAME.getName()));
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), row.get(FieldInfoEnum.SEQUENCE.getName()));
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(row.get(FieldInfoEnum.DATA_TYPE.getName()).toString()).getIntValue());
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(row.get(FieldInfoEnum.NATIVE_DATA_TYPE.getName()).toString()).getIntValue());
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), getBooleanValueEnum(row.get(FieldInfoEnum.COLLECTION.getName()).toString()).getIntValue());
//		parameterList.set(FieldInfoEnum.VALUE.getIndex(), row.get(FieldInfoEnum.VALUE.getName()));
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);

		return parameterList;
    }
    
    private static boolean isValuePrimitive(final List<Object> value) {
    	boolean flag = false;
    	
    	if (value != null && value.size() > 0) {
    		final Object firstElement = value.get(0);
    		if (firstElement instanceof Double ||
    				firstElement instanceof String ||
    				firstElement instanceof Integer ||
    				firstElement instanceof Boolean ) {
    			flag = true;
    		}
    	}
    	
    	return flag;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static final String R_LANGUAGE = "R"; 

    public static final String MATLAB_LANGUAGE = "Matlab";

    private static final String NATIVE_DATATYPE = "nativeDataType";
    
    private static final String FACTOR = "factor";
    
    public static final int INT_ZERO = 0;
    
    public static final int INT_ONE = 1;
    
    public static List<Map<String, Object>> createObject(List<Map<String, Object>> parameters, Object currentContext, boolean allowNull,
            String language) throws SystemException {
        List<Map<String, Object>> modelRequestBody = new ArrayList<Map<String, Object>>();
        JXPathContext context = JXPathContext.newContext(currentContext);

        for (Map<String, Object> item : parameters) {
            Map<String, Object> row = null;// NOPMD         
            
            String name = item.get(FrameworkConstant.API_NAME)!=null ? (String) item.get(FrameworkConstant.API_NAME) : (String) item.get(FrameworkConstant.NAME);          
            String modelParamName = item.get(FrameworkConstant.MODEL_PARAM_NAME)!=null ? (String)item.get(FrameworkConstant.MODEL_PARAM_NAME) : name ;
        
            Map<String, Object> dataTypeObject = (Map<String, Object>) item.get("datatype");
            String dataType = (String) dataTypeObject.get("type");// NOPMD
            boolean isArray = (boolean) dataTypeObject.get("array");
            Object value = currentContext != null ? context.getValue(name) : null;

            if (StringUtils.equalsIgnoreCase(language, MATLAB_LANGUAGE)
                    && !StringUtils.equalsIgnoreCase(dataType, "object") && isArray && !(value instanceof List)) {
                value = Arrays.asList(value);
            }

            if (value != null && StringUtils.equalsIgnoreCase(dataType, "double")) {
                if (value instanceof Integer) {
                    value = new Double(((Integer) value).doubleValue());
                } else if (value instanceof BigDecimal) {
                    value = new Double(((BigDecimal) value).doubleValue());
                } else if (value instanceof List) {
                    value = convertToDouble((List) value);
                }
            }

            Boolean mandatory = (Boolean) item.get("mandatory");
            String nativeDataType = null;
            if (mandatory && value == null) {
                throw new SystemException("RVE000216", new Object[] { name });
            }
            if (!mandatory && value == null && !allowNull) {
                continue;
            } else {
                Integer sequence = (Integer) item.get("sequence");
                nativeDataType = (String) item.get(NATIVE_DATATYPE);
                row = new LinkedHashMap<String, Object>();
                row.put("modelParameterName", modelParamName);
                row.put("sequence", sequence);
                row.put("dataType", dataType);
                if (nativeDataType != null) {
                    row.put(NATIVE_DATATYPE, nativeDataType);
                }
            }
            if (value != null) {
                if (!(StringUtils.equalsIgnoreCase(nativeDataType, "matrix") || StringUtils.equalsIgnoreCase(nativeDataType,
                        FACTOR)) && (value instanceof List || value.getClass().isArray())) {
                    row.put("collection", true);
                } else {
                    row.put("collection", false);
                }
            } else {
                row.put("collection", false);

            }

            if ((item.get("children") != null) && dataType.equals("object")) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) item.get("children");
                if (value instanceof List) {
                    List<Object> valueList = new ArrayList();
                    for (Object element : (List) value) {
                        if (element instanceof Map) {
                            value = createObject(children, element, allowNull, language);
                            Object obj = value;
                            valueList.add(obj);
                        } else {
                            value = createObject(children, value, allowNull, language);
                            break;
                        }

                    }
                    if (valueList.size() > 0) {
                        value = valueList;
                    }

                } else {
                    value = createObject(children, value, allowNull, language);
                }
            }

            if (row != null) {
                if (value instanceof List) {
                    if (allowNull) {
                        row.put("value", value);
                        row = reduceModelInputField(row);
                        setValue(row, value);
                        modelRequestBody.add(row);
                    } else {
                        List<Object> tempList = new ArrayList<Object>();
                        checkforMultiDimensional(tempList, value);
                        if (tempList.size() > INT_ZERO) {
                            row.put("value", value);
                            row = reduceModelInputField(row);
                            setValue(row, value);
                            modelRequestBody.add(row);
                        }
                    }
                    if (DataTypeValueEnum.OBJECT.getIntValue() == (Integer) getValueFromReduceModelInput(row, "dataType") || 
                    		(getValueFromReduceModelInput(row, NATIVE_DATATYPE) != null && (
                    				NativeDataTypeValueEnum.MATRIX.getIntValue() == (Integer) getValueFromReduceModelInput(row, NATIVE_DATATYPE)) || 
                    				NativeDataTypeValueEnum.FACTOR.getIntValue() == (Integer) getValueFromReduceModelInput(row, NATIVE_DATATYPE))
                            && language.equals(R_LANGUAGE)) {
                        row = dataRow(row);
                    }
                } else {
                	Object nativeDataTypeValue = getValueFromReduceModelInput(row, NATIVE_DATATYPE) ;
                	boolean isFactor = false;
                	if (language.equals(R_LANGUAGE) && nativeDataTypeValue instanceof Integer) {
                		isFactor = NativeDataTypeValueEnum.FACTOR.getIntValue() == (Integer) nativeDataTypeValue;
                	} else if (language.equals(R_LANGUAGE) && nativeDataTypeValue instanceof String) {
                		isFactor = StringUtils.equals(FACTOR, (String) nativeDataTypeValue);
                	}
                	
                    if (isFactor) {
                        List<Object> list = new ArrayList();
                        list.add(value);
                        row.put("value", list);
                        row = dataRow(row);
                        row = reduceModelInputField(row);
                        setValue(row, list);
                        modelRequestBody.add(row);
                    } else {
                        row.put("value", value);
                        row = reduceModelInputField(row);
                        setValue(row, value);
                        modelRequestBody.add(row);
                    }

                }

            }

        }

        return modelRequestBody;
    }
    
    private static List convertToDouble(List list) {
        List returnList = new ArrayList();
        for (Object object : list) {
            if (object instanceof List) {
                returnList.add(convertToDouble((List) object));
            } else {
                if (object instanceof Integer) {
                    returnList.add(new Double(((Integer) object).doubleValue()));
                } else if (object instanceof BigDecimal) {
                    returnList.add(new Double(((BigDecimal) object).doubleValue()));
                } else {
                    returnList.add(object);
                }
            }
        }
        return returnList;
    }
    
    private static List<Object> checkforMultiDimensional(List<Object> tempList, Object value) {
        for (Object obj : (List) value) {
            if (obj instanceof List) {
                if (((List) obj).size() > INT_ZERO) {
                    checkforMultiDimensional(tempList, obj);
                }
            } else {
                if (obj != null) {
                    tempList.add(obj);
                    break;
                }
            }
        }

        return tempList;
    }
    
    private static Map<String, Object> dataRow(Map<String, Object> row) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> dataRow = new LinkedHashMap<>();
        dataRow.put("modelParameterName", "data");
        dataRow.put("sequence", INT_ONE);
        
        Object nativeDataTypeValue = getValueFromReduceModelInput(row, NATIVE_DATATYPE) ;
        boolean isMatrix = false;
        if (nativeDataTypeValue instanceof Integer) {
        	isMatrix = NativeDataTypeValueEnum.MATRIX.getIntValue() == (Integer) nativeDataTypeValue;
    	} else if (nativeDataTypeValue instanceof String) {
    		isMatrix = StringUtils.equalsIgnoreCase("matrix", (String) nativeDataTypeValue);
    	}
        
        boolean isFactor = false;
    	if (nativeDataTypeValue instanceof Integer) {
    		isFactor = NativeDataTypeValueEnum.FACTOR.getIntValue() == (Integer) nativeDataTypeValue;
    	} else if (nativeDataTypeValue instanceof String) {
    		isFactor = StringUtils.equalsIgnoreCase(FACTOR, (String) nativeDataTypeValue);
    	}
        
        if (isMatrix|| isFactor) {
            dataRow.put("dataType", getValueFromReduceModelInput(row, "dataType"));
            putFieldInRow("dataType", "object", row); //row.put("dataType", "object");
        } else {
            dataRow.put("dataType", "object");
        }

        dataRow.put(NATIVE_DATATYPE, getValueFromReduceModelInput(row, NATIVE_DATATYPE));
        dataRow.put("collection", getValueFromReduceModelInput(row, "collection"));
        dataRow.put("value", getValueFromReduceModelInput(row, "value"));
        dataRow = reduceModelInputField(dataRow);
        setValue(dataRow, getValueFromReduceModelInput(row, "value"));
        list.add(dataRow);
        putFieldInRow("value", list, row); //row.put("value", list);
        return row;
    }
    
    public static void setValue(final Map<String, Object> row, final Object value) {
		if (row.containsKey("p")) {
			final List<Object> parameterList = (List<Object>) row.get("p");
			parameterList.set(FieldInfoEnum.VALUE.getIndex(), value);
		}
	}
    
    /**
     * DOCUMENT ME!
     *
     * @param parameters
     *            DOCUMENT ME!
     * @param currentContext
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws SystemException
     **/
    public static List<Map<String, Object>> createObjectForExcelOutput(List<Map<String, Object>> parameters, boolean allowNull,
            String language) throws SystemException {
        List<Map<String, Object>> modelRequestBody = new ArrayList<Map<String, Object>>();       

        for (Map<String, Object> item : parameters) {
            Map<String, Object> row = null;// NOPMD
            String name = item.get("apiName")!=null ? (String)item.get("apiName") :  (String)item.get("name");            
            String modelParamName = item.get("modelParamName")!=null? (String)item.get("modelParamName"):(String)item.get("name");           
            
            Map<String, Object> dataTypeObject = (Map<String, Object>) item.get("datatype");
            String dataType = (String) dataTypeObject.get("type");// NOPMD
            boolean isArray = (boolean) dataTypeObject.get("array");         
            Map<String, Object> properties =  (Map<String, Object>) dataTypeObject.get("properties");
            int fractionDigits = 0;
            if(!properties.isEmpty()){
            	 String fractionDigitsStr = (String) properties.get("fractionDigits");
            	 if(fractionDigitsStr != null){
            	 fractionDigits  = Integer.parseInt(fractionDigitsStr);
            	 }
            }
            Boolean mandatory = (Boolean) item.get("mandatory");
            String nativeDataType = null;
           
            Integer sequence = (Integer) item.get("sequence");
            nativeDataType = (String) item.get(NATIVE_DATATYPE);
            row = new LinkedHashMap<String, Object>();
            row.put("apiName", name);
            row.put("modelParameterName", modelParamName);
            row.put("sequence", sequence);
            row.put("dataType", dataType);
            if(fractionDigits > 0 ){
            row.put("precession", fractionDigits);
            }
            if (nativeDataType != null) {
                row.put(NATIVE_DATATYPE, nativeDataType);
            }            
            if (isArray) {               
                row.put("collection", true);
            }else{
                row.put("collection", false);           	
            }

            if ((item.get("children") != null) && "object".equals(dataType)) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) item.get("children");
                for(Map<String, Object> child : children){
                	row.put("value" ,createObjectForExcelOutput(children,  allowNull, language));              	
                }
             }
            
        modelRequestBody.add(row);
           
        }

        return modelRequestBody;
    }

}
