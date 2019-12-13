package com.ca.umg.rt.response;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ModelLanguages;
import com.ca.umg.modelet.r.type.RDataTypes;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.transformer.ModelResponseUtil;
import com.ca.umg.rt.transformer.ModelResponseValidatorUtil;
import com.ca.umg.rt.util.Datatype;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.validator.DataTypes;

public abstract class AbstractModelResponseBuilder {

	public static final String SEQUENCE = "sequence";
	
    public static final String MODEL_PARAMETER_NAME = "modelParameterName";
    
    private static final String ONE = "1";

    private static final String TWO = "2";
    
    public static final String NATIVE_DATATYPE = "nativeDataType";
    
    public static final String CHILDREN = "children";
    
    public static final String NATIVE_DATATYPE_DATA_FRAME = "data.frame";

    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelResponseBuilder.class);

	public Map<String, Object> getFieldInfo(List<Map<String, Object>> modelResponsePayload, boolean isList,
			Integer sequence) {
		Map<String, Object> fieldInfo = null;
		if (modelResponsePayload != null) {
			if (isList) {
				fieldInfo = modelResponsePayload.get(sequence - 1);
			} else {
				fieldInfo = getFieldInfoPropertyValue1(sequence, modelResponsePayload);
			}
		}
		return fieldInfo;
	}

	public Map<String, Object> getFieldInfoPropertyValue1(Integer sequence,
			List<Map<String, Object>> modelResponsePayload) {
		Map<String, Object> fieldValue = null; 
		if (CollectionUtils.isNotEmpty(modelResponsePayload)) {
			for (Map<String, Object> field : modelResponsePayload) {
				if (sequence != null && field != null && sequence.equals(Integer.valueOf((String) field.get(SEQUENCE)))) {
					fieldValue = field;
					break;
				}
			}
		}
		return fieldValue;
	}

	
	

    public void generateOutputError(Object dataValue, String errorMessage, List<String> errorMessages) {
        errorMessages.add(errorMessage + "Value received is " + dataValue);
    }
    
    public Map<String, Object> getFieldInfoPropertyValue(String mappingFieldName,
            List<Map<String, Object>> modelResponsePayload) {
        Map<String, Object> fieldValue = null;
        if (CollectionUtils.isNotEmpty(modelResponsePayload)) {
            for (Map<String, Object> field : modelResponsePayload) {
                if (StringUtils.equals(mappingFieldName, (String) field.get(MODEL_PARAMETER_NAME))) {
                    fieldValue = field;
                    break;
                }
            }
        }
        return fieldValue;
    }
    
    public Object getPrimitiveValue(Object value, Boolean isArray, String dataType, StringBuffer errorMessages,
            String flatenedName, Boolean modelOpValidation, String modellingEnvironment,Integer fractionDigits) {
        Object returnValue = null;
        if (modelOpValidation) {         
            if (value != null && value instanceof List && !isArray && CollectionUtils.isNotEmpty(((List<Object>) value))) {// NOPMD
                returnValue = convertValue(((List<Object>) value).get(0), dataType, isArray, errorMessages, flatenedName, modellingEnvironment,fractionDigits);
            } else if (value != null && !isArray && !(value instanceof List && CollectionUtils.isEmpty((List) (value)))) {
                returnValue = convertValue(value, dataType, isArray, errorMessages, flatenedName, modellingEnvironment,fractionDigits);
            }
        } else {          
            if (value != null && value instanceof List && !isArray && CollectionUtils.isNotEmpty(((List<Object>) value))) {// NOPMD
                returnValue = ((List<Object>) value).get(0);
            } else if (value != null && !isArray && !(value instanceof List && CollectionUtils.isEmpty((List) (value)))) {
                returnValue = value;
            }
        }
        return returnValue;
    }
    
    public Object getPrimitiveValueForExcel(Object value, Boolean isArray, String dataType, StringBuffer errorMessages,
            String flatenedName, Boolean modelOpValidation,Integer fractionDigits) {
        Object returnValue = null;
        if (modelOpValidation) {
           if (value != null) {
                returnValue = convertValue(value, dataType, Boolean.FALSE, errorMessages, flatenedName, ModelLanguages.EXCEL.toString(),fractionDigits);
            }
        } else {         
            if (value != null) {
                returnValue = value;
            }
        }
        return returnValue;
    }

   
    @SuppressWarnings("PMD")
    public Object getPrimitiveValueForR(Object value, Boolean isArray, String dataType, StringBuffer errorMessages,
            String flatenedName, Boolean modelOpValidation, List<String> dimLenErrorMessage, String length,Integer fractionDigits) {
        Object returnValue = null;
        if (modelOpValidation) {            
            if (value != null && value instanceof List && !isArray && CollectionUtils.isNotEmpty(((List<Object>) value))) {// NOPMD
                if (((List<Object>) value).size() > 1) {
                    dimLenErrorMessage.add(flatenedName
                            + " not defined as array in output definition but received array in output. Value received is "
                            + ((List<Object>) value).get(0) + ".");
                } else if (RuntimeConstants.DATATYPE_STRING.equalsIgnoreCase(dataType.trim())) {
                    if (!StringUtils.isBlank(length) && Integer.parseInt(length) > 0 && value.toString().replace("\"", "").trim().length() > Integer.parseInt(length)) {                   
                            dimLenErrorMessage.add("Defined length of " + flatenedName + " is " + Integer.parseInt(length)
                                    + " in output definition but received length is "
                                    + value.toString().replace("\"", "").trim().length() + ".");                       
                    }
                }
                returnValue = convertValue(((List<Object>) value).get(0), dataType, isArray, errorMessages, flatenedName, ModelLanguages.R.toString(),fractionDigits);
            } else if (value != null && !isArray && !(value instanceof List && CollectionUtils.isEmpty((List) (value)))) { // NOPMD
                if (RuntimeConstants.DATATYPE_STRING.equalsIgnoreCase(dataType.trim())) {
                    if (!StringUtils.isBlank(length) && Integer.parseInt(length) > RuntimeConstants.INT_ONE) {
                        if (value.toString().replace("\"", "").trim().length() > Integer.parseInt(length)) {
                            dimLenErrorMessage.add("Defined length of " + flatenedName + " is " + Integer.parseInt(length)
                                    + " in output definition but received length is "
                                    + value.toString().replace("\"", "").trim().length() + ".");
                        }
                    }
                }
                returnValue = convertValue(value, dataType, isArray, errorMessages, flatenedName, ModelLanguages.R.toString(),fractionDigits);
            }
        } else {            
            if (value != null && value instanceof List && !isArray && CollectionUtils.isNotEmpty(((List<Object>) value))) {// NOPMD
                returnValue = ModelResponseValidatorUtil.convertValueWithOutValidation(((List<Object>) value).get(0), dataType,
                        isArray);
            } else if (value != null && !isArray && !(value instanceof List && CollectionUtils.isEmpty((List) (value)))) {
                returnValue = ModelResponseValidatorUtil.convertValueWithOutValidation(value, dataType, isArray);
            }
        }
        return returnValue;
    }
    


    

    @SuppressWarnings("PMD")
    private Object convertValue(Object value, String datatype, boolean isArray, StringBuffer errorMessages, String flattendName, String modellingEnvironment,Integer fractionDigits) {
        Object returnValue = null;

        if (value instanceof List) {
            LOGGER.error("primitive as List :" + value);
            returnValue = value;
        } else if (isArray) {
            LOGGER.error("primitive as array :" + value);
            returnValue = value;
        } else {
            if (value != null) {
                String className = value == null ? null
                        : value.getClass().getName().equals("java.util.LinkedHashMap") ? "java.lang.String"
                                : value.getClass().getName();
                switch (Datatype.valueOf(StringUtils.upperCase(datatype))) {
                case INTEGER:
                	if (value instanceof Integer) {
                        returnValue = value;
                        // Following code is required only for Rserve, since it does not support integers
                    //} else if(StringUtils.equalsIgnoreCase(ModelLanguages.R.toString(), modellingEnvironment) && (value instanceof Integer || value instanceof Double)){ 
                    //	returnValue = value;
                    } else {
                        errorMessages.append(String.format(ModelResponseUtil.ERRORMESSAGE, DataTypes.INTEGER,
                                flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                className));
                    }
                    break;
                case LONG:
                    if (value instanceof Integer || value instanceof Long) {
                        returnValue = value;
                    } else {
                        errorMessages.append(String.format(ModelResponseUtil.ERRORMESSAGE, DataTypes.LONG,
                                flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                className));
                    }
                    break;
                case BIGINTEGER:
                    if (value instanceof Long || value instanceof Integer || value instanceof BigInteger) {
                        returnValue = value;
                    } else {
                        errorMessages.append(String.format(ModelResponseUtil.ERRORMESSAGE, DataTypes.BIGINTEGER,
                                flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                className));

                    }
                    break;
                case BIGDECIMAL:
                    if ((value instanceof Double || value instanceof Integer || value instanceof Long
                            || value instanceof BigDecimal || value instanceof BigInteger)) {
                    	if(fractionDigits != null && fractionDigits>0 && StringUtils.length(StringUtils.substringAfter(String.valueOf(value), ".")) > fractionDigits) {
                   		 errorMessages.append(String.format(ModelResponseUtil.PRECISION_ERRORMESSAGE, fractionDigits,
                                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                    StringUtils.length(StringUtils.substringAfter(String.valueOf(value), "."))));
                   	}else {
                   		returnValue = value;
                   	}
                    } else {
                        errorMessages.append(String.format(ModelResponseUtil.ERRORMESSAGE, DataTypes.BIGDECIMAL,
                                flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                className));

                    }
                    break;
                case STRING:
                    if (value instanceof Map) {
                        returnValue = ((Map) value).get(MessageVariables.VALUE);
                        if (returnValue instanceof List && CollectionUtils.isNotEmpty((List) returnValue)) {
                            returnValue = ((List) returnValue).get(0);
                        } else {
                            returnValue = null;// NOPMD

                        }
                        if (returnValue != null && !(returnValue instanceof String)) {
                            errorMessages.append(String.format(ModelResponseUtil.ERRORMESSAGE, DataTypes.STRING,
                                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                    className));

                        }
                    } else if (value instanceof String) {
                        returnValue = value;
                    } else {
                        errorMessages.append(String.format(ModelResponseUtil.ERRORMESSAGE, DataTypes.STRING,
                                flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                className));

                    }
                    break;
                case DOUBLE:
                    if ((value instanceof Integer || value instanceof Double)) {

                    	if(fractionDigits != null && fractionDigits>0 && StringUtils.length(StringUtils.substringAfter(String.valueOf(value), ".")) > fractionDigits) {
                    		 errorMessages.append(String.format(ModelResponseUtil.PRECISION_ERRORMESSAGE, fractionDigits,
                                     flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                     StringUtils.length(StringUtils.substringAfter(String.valueOf(value), "."))));
                    	}else {
                    		returnValue = value;
                    	}
                    
                        
                    } else {
                        errorMessages.append(String.format(ModelResponseUtil.ERRORMESSAGE, DataTypes.DOUBLE,
                                flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                className));

                    }
                    break;
                case BOOLEAN:
                    if ((value instanceof Boolean)) {
                        returnValue = value;
                    } else {
                        errorMessages.append(String.format(ModelResponseUtil.ERRORMESSAGE, DataTypes.BOOLEAN,
                                flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, RuntimeConstants.CHAR_DOT),
                                className));

                    }
                    break;
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
            } else {
                returnValue = null;
            }
        }
        return returnValue;
    }
    


    @SuppressWarnings("PMD")
    public static List convertToPrimitive(List list, String nativeDataType, String datatype, List dimensions, String parentPath,
            String fieldName, String modelOpDataType, String seqPath, List<Map<String, Object>> errorPath,
            List<String> errorMessages, String modelEnv,Integer fractionDigits) {
        List returnList = new ArrayList();
        int k = 0;
        for (Object object : list) {
            StringBuilder sb = new StringBuilder();
            try {
                if (object instanceof List) {
                    returnList.add(
                            convertToPrimitive((List) object, nativeDataType, datatype, dimensions, parentPath + "[" + k + "]/",
                                    fieldName, modelOpDataType, seqPath + "[" + k + "].", errorPath, errorMessages, modelEnv,fractionDigits));
                } else {
                    if (object == null) {
                        returnList.add(object);
                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_DOUBLE, datatype)) {
                        ModelResponseUtil.dataTypeDouble(returnList, object, sb, parentPath + "[" + k + "]",fractionDigits);
                    } else if ((StringUtils.equals(RuntimeConstants.DATATYPE_STRING, datatype)
                            || StringUtils.equals(RuntimeConstants.DATATYPE_STRING, datatype))) {
                        ModelResponseUtil.dataTypeString(returnList, object, sb, parentPath + "[" + k + "]");
                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_INTEGER, datatype)) {
                        ModelResponseUtil.dataTypeInteger(returnList, object, sb, parentPath + "[" + k + "]", modelEnv);
                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_LONG, datatype)) {
                        ModelResponseUtil.dataTypeLong(returnList, object, sb, parentPath + "[" + k + "]");
                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_BIGINTEGER, datatype)) {
                        ModelResponseUtil.dataTypeBigInteger(returnList, object, sb, parentPath + "[" + k + "]");
                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_BIGDECIMAL, datatype)) {
                        ModelResponseUtil.dataTypeBigDecimal(returnList, object, sb, parentPath + "[" + k + "]",fractionDigits);
                    } else if (StringUtils.equals(RuntimeConstants.DATATYPE_BOOLEAN, datatype)) {
                        ModelResponseUtil.dataTypeBoolean(returnList, object, sb, parentPath + "[" + k + "]");

                    }
                    if (sb.length() > RuntimeConstants.INT_ZERO) {
                        generateOutputError1(object, sb.toString(), errorMessages);
                    }
                }
                k++;
            } catch (Exception e) {// NOPMD
                LOGGER.error("Exception occured in convertToPrimitive for field " + fieldName, e);
                ModelResponseValidatorUtil.generateErrorJson(seqPath + "[" + k + "]", errorPath, datatype,
                        parentPath + "[" + k + "]", object, modelOpDataType, nativeDataType, fieldName, dimensions, null);
            }
        }
        return returnList;
    }
    
    public Map<String, Object> getFieldInfo1(List<Map<String, Object>> modelResponsePayload, boolean isList, Integer sequence,
            int loopCount, int divideFactor) {
        Map<String, Object> fieldInfo = null;
        if (modelResponsePayload != null) {
            if (isList) {
                int factor = (loopCount * divideFactor) + sequence;
                fieldInfo = modelResponsePayload.get(factor - 1);
            } else {
                fieldInfo = getFieldInfoPropertyValue2(sequence, modelResponsePayload, loopCount, divideFactor);
            }
        }
        return fieldInfo;
    }

    private Map<String, Object> getFieldInfoPropertyValue2(Integer sequence, List<Map<String, Object>> modelResponsePayload,
            int loopCount, int divideFactor) {
        Map<String, Object> fieldValue = null;
        if (CollectionUtils.isNotEmpty(modelResponsePayload)) {
            int factor = (loopCount * divideFactor) + sequence;
            Map<String, Object> field = modelResponsePayload.get(factor - 1);
            fieldValue = field;
        }

        return fieldValue;
    }
    
    @SuppressWarnings("PMD")
    public Object getPrimitiveValueForR(Object value, Boolean isArray, String dataType, StringBuffer errorMessages,
            String flatenedName, Boolean modelOpValidation,Integer fractionDigits) {
        Object returnValue = null;
        if (modelOpValidation) {            
            if (value != null && value instanceof List && !isArray && CollectionUtils.isNotEmpty(((List<Object>) value))) {// NOPMD
                returnValue = convertValue(((List<Object>) value).get(0), dataType, isArray, errorMessages, flatenedName, ModelLanguages.R.toString(),fractionDigits);
            } else if (value != null && !isArray && !(value instanceof List && CollectionUtils.isEmpty((List) (value)))) {
                returnValue = convertValue(value, dataType, isArray, errorMessages, flatenedName, ModelLanguages.R.toString(),fractionDigits);
            }
        } else {            
            if (value != null && value instanceof List && !isArray && CollectionUtils.isNotEmpty(((List<Object>) value))) {// NOPMD
                returnValue = ModelResponseValidatorUtil.convertValueWithOutValidation(((List<Object>) value).get(0), dataType,
                        isArray);
            } else if (value != null && !isArray && !(value instanceof List && CollectionUtils.isEmpty((List) (value)))) {
                returnValue = ModelResponseValidatorUtil.convertValueWithOutValidation(value, dataType, isArray);
            }
        }
        return returnValue;
    }

    
    /*
     * This methods converts column wise response of data frame row wise response
     * 
     * @param fieldInfo
     */
    public void transposeModelResponse(Map<String, Object> fieldInfo) {
        List<Map> columnWiseResponse = (List<Map>) fieldInfo.get(MessageVariables.VALUE);

        List<List<Map>> rowWiseResponse = new LinkedList<List<Map>>();
        Map<Integer, List<Map>> sequenceObjMap = new TreeMap<Integer, List<Map>>();

        int responseSize = columnWiseResponse.size();
        for (int i = 0; i < responseSize; i++) {
            List values = null;
            if (columnWiseResponse.get(i).get(MessageVariables.VALUE) instanceof List) {
                values = (List) columnWiseResponse.get(i).get(MessageVariables.VALUE);
            } else {
                Object value = columnWiseResponse.get(i).get(MessageVariables.VALUE);
                values = new ArrayList<>();
                values.add(value);
            }

            Map paramValue = null;
            int valuesSize = values.size();

            for (int j = 0; j < valuesSize; j++) {

                paramValue = new LinkedHashMap<>(columnWiseResponse.get(i));

                if (!StringUtils.equalsIgnoreCase(MessageVariables.COLUMN_NAMES,
                        (CharSequence) paramValue.get(MessageVariables.FIELD_NAME))) {

                    paramValue.put(MessageVariables.COLLECTION, false);
                    paramValue.put(MessageVariables.VALUE, values.get(j));

                    switch (Datatype.valueOf(StringUtils.upperCase((String) paramValue.get(MessageVariables.DATA_TYPE)))) {
                    case STRING:
                    case DATE:
                    case DATETIME:
                        paramValue.put(MessageVariables.NATIVE_DATA_TYPE, RDataTypes.R_CHARACTER.getName());
                        break;
                    case BOOLEAN:
                        paramValue.put(MessageVariables.NATIVE_DATA_TYPE, RDataTypes.R_LOGICAL.getName());
                        break;
                    case INTEGER:
                        paramValue.put(MessageVariables.NATIVE_DATA_TYPE, RDataTypes.R_INTEGER.getName());
                        break;
                    case DOUBLE:
                        paramValue.put(MessageVariables.NATIVE_DATA_TYPE, RDataTypes.R_NUMERIC.getName());
                        break;
                    case BIGDECIMAL:
                    case BIGINTEGER:
                    case LONG:
                        paramValue.put(MessageVariables.NATIVE_DATA_TYPE, RDataTypes.R_NUMERIC.getName());
                        break;

                    default:
                        break;
                    }

                    int key = j + 1;
                    if (!sequenceObjMap.containsKey(key)) {
                        sequenceObjMap.put(key, new LinkedList<Map>());
                    }
                    sequenceObjMap.get(key).add(paramValue);
                }
            }
        }

        for (Entry<Integer, List<Map>> entry : sequenceObjMap.entrySet()) {
            rowWiseResponse.add(entry.getValue());
        }

        fieldInfo.put(MessageVariables.VALUE, rowWiseResponse);
    }


    @SuppressWarnings("PMD")
    public void validateLength(String inputDimensions, String name, List<String> errorMessages, Object inputDefaultValue,
            String dataType, String inputLength) {

        String dimensions = StringUtils.trimToEmpty(inputDimensions.replace('[', ' ').replace(']', ' '));
        String length = StringUtils.trimToEmpty(inputLength);
        if (StringUtils.equalsIgnoreCase(dataType, DataTypes.OBJECT) && StringUtils.equalsIgnoreCase(dimensions, ONE)) {
            List defaultValue = (List) inputDefaultValue;
            if (CollectionUtils.isNotEmpty(defaultValue) && Integer.parseInt(length) > 0
                    && defaultValue.size() > Integer.parseInt(length)) {
                errorMessages.add("Incorrect array length received in tenant input for " + name + " . Expected length "
                        + Integer.parseInt(length) + "; Received length " + defaultValue.size() + ".");
            }
        } else {
            String defaultValue = StringUtils.trimToEmpty(inputDefaultValue.toString());
            if (dimensions.equalsIgnoreCase(ONE)) {
                if (!StringUtils.isBlank(length) && (Integer.parseInt(length) > 0)
                        && (defaultValue.split(",").length > Integer.parseInt(length))) {
                    errorMessages.add("Incorrect array length received in tenant output for " + name + " . Expected length "
                            + Integer.parseInt(length) + "; Received length " + defaultValue.split(",").length + ".");
                }
            } else if (dimensions.equalsIgnoreCase(TWO)) {
                Boolean xDimensionMismatch = false;
                Boolean yDimensionMismatch = false;
                String[] splitedValue = defaultValue.replaceAll(" ", "").split("],\\[");
                String[] splitedLength = length.split(",");
                int i = 0;
                if ((splitedLength.length == 2) && (Integer.parseInt(splitedLength[0].trim()) > 0)
                        && (splitedValue.length > Integer.parseInt(splitedLength[0].trim()))) {
                    xDimensionMismatch = true;
                }
                for (i = 0; i < splitedValue.length; i++) {
                    if (splitedValue[i].split(",").length != Integer.parseInt(splitedLength[1].trim())) {
                        yDimensionMismatch = true;
                        break;
                    }
                }
                if (xDimensionMismatch && yDimensionMismatch) {
                    errorMessages.add("Row and Column length mismatch for " + name
                            + " in output. Defined row length in IO definition " + splitedLength[0] + ", actual row length "
                            + "received " + splitedValue.length
                            + ". Actual row length must be equal <= to defined length. Defined column length in IO definition "
                            + splitedLength[1] + ", actual " + "column length received " + splitedValue[i].split(",").length
                            + ". Actual column length must be equal to defined length.");
                } else if (xDimensionMismatch) {
                    errorMessages.add("Row length mismatch for " + name + " in output. Defined row length in IO definition "
                            + splitedLength[0] + ", actual row length received " + splitedValue.length
                            + ". Actual length must be equal <= to defined length.");
                } else if (yDimensionMismatch) {
                    errorMessages.add("Column length mismatch for " + name + " in output. Defined column length in IO definition "
                            + splitedLength[1] + ", actual column length received " + splitedValue[i].split(",").length
                            + ". Actual length must be equal to defined length.");
                }
            } else if (dataType.trim().equalsIgnoreCase("string")) {
                if (!StringUtils.isBlank(length) && Integer.parseInt(length) > 0) {
                    if (defaultValue.replace("\"", "").trim().length() > Integer.parseInt(length)) {
                        errorMessages.add("Defined length of " + name + " is " + Integer.parseInt(length)
                                + " in output definition but received length is " + defaultValue.replace("\"", "").trim().length()
                                + ".");
                    }
                }
            }
        }

    }
    

    

    @SuppressWarnings("PMD")
    public void validateDimensions(String inputDimensions, String name, List<String> errorMessages, String inputDefaultValue) {

        String dimensions = StringUtils.trimToEmpty(inputDimensions.replace('[', ' ').replace(']', ' '));
        String defaultValue = StringUtils.trimToEmpty(inputDefaultValue);
        if (StringUtils.isBlank(dimensions)) {
            if (StringUtils.isNotBlank(defaultValue) && defaultValue.indexOf('[') != -1) {
                errorMessages
                        .add(name + " not defined as array in output definition but received array in output. Value received is "
                                + defaultValue + ".");
            }
        } else if (dimensions.equalsIgnoreCase(ONE)) {
            if (StringUtils.isNotBlank(defaultValue) && (defaultValue.charAt(0) != '[' || defaultValue.indexOf('[', 1) != -1)) {
                errorMessages.add(
                        name + " defined as 1 dimensional array in output definition but received data in incorrect format in output. Value received is "
                                + defaultValue + ".");
            }
        } else if (dimensions.equalsIgnoreCase(TWO)) {
            if (StringUtils.isNotBlank(defaultValue)
                    && (defaultValue.charAt(0) != '[' || defaultValue.charAt(1) != '[' || defaultValue.charAt(2) == '[')) {
                errorMessages.add(
                        name + " defined as 2 dimensional array in output definition but received data in incorrect format in output. Value received is "
                                + defaultValue + ".");
            }
        }
    }    
  


    private static void generateOutputError1(Object dataValue, String errorMessage, List<String> errorMessages) {
        errorMessages.add(errorMessage + "Value received is " + dataValue);
    }
    
    public abstract Map<String, Object> createObject(List<Map<String, Object>> parameters,
			List<Map<String, Object>> modelResponsePayload, boolean isList, List<String> errorMessages,
			Boolean modelOpValidation) throws SystemException ;
    
	public abstract Map<String, Object> createObject(List<Map<String, Object>> parameters,
			List<Map<String, Object>> modelResponsePayload, boolean isList, String seqPath,
            List<Map<String, Object>> errorPath, List<String> errorMessages, Boolean modelOpValidation,
            Boolean hasWrapperOverRList)
					throws SystemException ;
	
	public abstract List<Map<String, Object>> createObjectForRList(List<Map<String, Object>> parameters,
			List<Map<String, Object>> modelResponsePayload, boolean isList, String seqPath,
			List<Map<String, Object>> errorPath, List<String> errorMessages, Boolean modelOpValidation)
					throws SystemException ;

}