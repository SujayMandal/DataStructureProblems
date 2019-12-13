package com.ca.umg.rt.transformer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.util.Datatype;
import com.ca.umg.rt.util.MessageVariables;

public final class ModelResponseValidatorUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelResponseValidatorUtil.class);
	
	private ModelResponseValidatorUtil(){
		
	}
	
	 public static Object convertValueWithOutValidation(Object value, String datatype, boolean isArray) {
	        Object returnValue = null;

	        if (value instanceof List) {
	            // TODO handle datatypes
	            returnValue = value;
	        } else if (isArray) {
	            // TODO handle datatypes
	            returnValue = value;
	        } else {
	            switch (Datatype.valueOf(StringUtils.upperCase(datatype))) {
	                case INTEGER:
	                    if (value instanceof Integer) {
	                        returnValue = value;
	                    } else if(value instanceof BigDecimal) {
	                        returnValue = value != null ? ((BigDecimal) value).intValueExact() : null;
	                    } else {
	                        returnValue = value != null ? ((Double) value).intValue() : null;
	                    }
	                    break;
	                case LONG:
	                    if (value instanceof Integer || value instanceof Long) {
	                        returnValue = value;
	                    } else {
	                        returnValue = value != null ? ((Double) value).longValue() : null;
	                    }
	                    break;
	                case BIGINTEGER:
	                    if (value instanceof Double){
	                        returnValue = value != null ? new BigDecimal((Double) value).toBigInteger() : null;
	                    } else if(value instanceof BigDecimal) {
	                        returnValue = value != null ? ((BigDecimal) value).toBigIntegerExact() : null;
	                    } else {
	                        returnValue = value;
	                    }
	                    break;
	                case BIGDECIMAL:
	                case STRING:
	                case DOUBLE:
	                default:
	                    if (value instanceof Map) {
	                        returnValue = ((Map) value).get(MessageVariables.VALUE);
	                        if (returnValue instanceof List && CollectionUtils.isNotEmpty((List) returnValue)) {
	                            returnValue = ((List) returnValue).get(0);
	                        } else {
	                            returnValue = null;// NOPMD

	                        }
	                    } else {
	                        returnValue = value;
	                    }
	                    break;
	            }
	        }
	        return returnValue;
	    }
	 
	    @SuppressWarnings("PMD.UseObjectForClearerAPI")
	    public static List convertToPrimitiveWithoutValidation(List list, String nativeDataType, String datatype, List dimensions, String parentPath,
	            String fieldName, String modelOpDataType, String seqPath, List<Map<String, Object>> errorPath) {
	        List returnList = new ArrayList();
	        int k = 0;
	        for (Object object : list) {
	            try {
	                if (object instanceof List) {
	                    returnList.add(convertToPrimitiveWithoutValidation((List) object, nativeDataType, datatype, dimensions,
	                            parentPath + "[" + k + "]/", fieldName, modelOpDataType, seqPath + "[" + k + "].", errorPath));
	                } else {
	                    if (object == null) {
	                        returnList.add(object);
	                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_DOUBLE, datatype)) {
	                        dataTypeDouble(returnList, object);
	                    } else if (((StringUtils.equals(RuntimeConstants.DATATYPE_STRING, datatype) || StringUtils.equals(RuntimeConstants.DATATYPE_DATE, datatype)))
	                            && object instanceof String) {
	                        returnList.add(String.valueOf(object));
	                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_INTEGER, datatype)) {
	                        dataTypeInteger(returnList, object);
	                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_LONG, datatype)) {
	                        dataTypeLong(returnList, object);
	                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_BIGINTEGER, datatype)) {
	                        dataTypeBigInteger(returnList, object);
	                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_BIGDECIMAL, datatype)) {
	                        dataTypeBigDecimal(returnList, object);
	                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_BOOLEAN, datatype) && object instanceof Boolean) {
	                        returnList.add((Boolean) object);
	                    }
	                }
	                k++;
	            } catch (Exception e) {// NOPMD
	                LOGGER.error("Exception occured in convertToPrimitive for field " + fieldName, e);
	                generateErrorJson(seqPath + "[" + k + "]", errorPath, datatype, parentPath + "[" + k + "]", (String) object,
	                        modelOpDataType, nativeDataType, fieldName, dimensions,null);
	            }
	        }
	        return returnList;
	    }

	    private static void dataTypeDouble(List returnList, Object object) {
	        if(object instanceof Double) {
	            returnList.add(object);
	        } else if(object instanceof Long) {
	            returnList.add(new Double((Long) object));
	        } else if(object instanceof Integer) {
	            returnList.add(new Double((Integer) object));
	        } else if(object instanceof BigInteger) {
	            returnList.add(new BigDecimal((BigInteger) object));
	        } else if(object instanceof BigDecimal) {
	            returnList.add(object);
	        }
	    }

	    private static void dataTypeInteger(List returnList, Object object) {
	        if(object instanceof Double) {
	            returnList.add(((Double)object).intValue());
	        } else if(object instanceof Integer) {
	            returnList.add(object);
	        } else if(object instanceof BigInteger) {
	            returnList.add(((BigInteger) object).intValue());
	        } else if(object instanceof BigDecimal) {
	            returnList.add(((BigDecimal) object).intValue());
	        } else if(object instanceof Long) {
	            returnList.add(((Long) object).intValue());
	        }
	    }

	    private static void dataTypeLong(List returnList, Object object) {
	        if(object instanceof Double) {
	            returnList.add(((Double)object).longValue());
	        } else if(object instanceof Integer) {
	            returnList.add(Long.valueOf((Integer)object));
	        } else if(object instanceof BigInteger) {
	            returnList.add(((BigInteger) object).longValue());
	        } else if(object instanceof BigDecimal) {
	            returnList.add(((BigDecimal) object).longValue());
	        } else if(object instanceof Long) {
	            returnList.add(object);
	        }
	    }

	    private static void dataTypeBigInteger(List returnList, Object object) {
	        if(object instanceof Double) {
	            returnList.add(new BigDecimal((Double)object).toBigInteger());
	        } else if(object instanceof Integer) {
	            returnList.add(object);
	        } else if(object instanceof BigInteger) {
	            returnList.add(object);
	        } else if(object instanceof BigDecimal) {
	            returnList.add(((BigDecimal) object).toBigInteger());
	        } else if(object instanceof Long) {
	            returnList.add(object);
	        }
	    }

	    private static void dataTypeBigDecimal(List returnList, Object object) {
	        returnList.add(object);
	    }
	    
	    @SuppressWarnings({ "PMD.ExcessiveParameterList", "PMD.UseObjectForClearerAPI" })
	    public static void generateErrorJson(String seqPath, List<Map<String, Object>> errorPath, String dataType,
	            String flatenedName, Object dataValue, String modelOpDataType, String nativeDataType, String fieldName,
	            List dimensions, String errorMessage) {
	        Map<String, Object> errorJson = new LinkedHashMap<String, Object>();
	        errorJson.put(MessageVariables.FLATENED_NAME, flatenedName);
	        errorJson.put(MessageVariables.DIMENSIONS, dimensions == null ? null : dimensions);
	        errorJson.put(MessageVariables.MODELOUTPUT_FIELDNAME, fieldName);
	        errorJson.put(MessageVariables.MODELOUTPUT_VALUE, dataValue);
	        errorJson.put(MessageVariables.EXPECTED_DATATYPE, dataType);
	        errorJson.put(MessageVariables.MODELOUTPUT_DATATYPE, modelOpDataType);
	        errorJson.put(MessageVariables.MODELOUTPUT_NATIVEDATATYPE, nativeDataType);
	        errorJson.put(MessageVariables.MODELOUTPUT_SEQUENCEPATH, seqPath);
	        errorJson.put(MessageVariables.MODELOUTPUT_SEQUENCEPATH, seqPath);
	        if (errorMessage != null) {
	            errorJson.put(MessageVariables.ERROR_MESSAGE, errorMessage);
	        }

	        ((List<Map<String, Object>>) errorPath).add(errorJson);
	    }

}
