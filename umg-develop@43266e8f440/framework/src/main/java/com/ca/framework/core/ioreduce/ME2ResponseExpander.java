package com.ca.framework.core.ioreduce;

import static com.ca.framework.core.ioreduce.BooleanValueEnum.getBooleanValueEnum;
import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;

@SuppressWarnings("PMD")
public class ME2ResponseExpander {

    private static final Logger LOGGER = LoggerFactory.getLogger(ME2ResponseExpander.class);

    private static final String CHILDREN = "children";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> createObject2(List<Map<String, Object>> parameters,
            List<Map<String, Object>> modelResponsePayload, boolean isList) throws SystemException {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        JXPathContext context = JXPathContext.newContext(response);

        for (Map<String, Object> item : parameters) {
            String name = (String) item.get("name");
            Integer sequence = (Integer) item.get(FieldInfoEnum.SEQUENCE.getName());
            Map<String, Object> dataTypeObject = (Map<String, Object>) item.get("datatype");
            String dataType = (String) dataTypeObject.get("type");
            Boolean isArray = (Boolean) dataTypeObject.get("array");

            List dimensions = null;
            if (isArray) {
                dimensions = ((List) ((Map) dataTypeObject.get("properties")).get("dimensions"));
            }
            Map<String, Object> fieldInfo = null;
            if (modelResponsePayload != null) {
                if (isList) {
                    final List<Object> pValue = (List<Object>) ((Map) modelResponsePayload.get(sequence - 1))
                            .get(FieldInfoEnum.P.getName());
                    fieldInfo = new LinkedHashMap<String, Object>();
                    createMapFromPValue(pValue, fieldInfo);
                } else {
                    fieldInfo = getFieldInfoPropertyValue2(sequence, modelResponsePayload);
                }
            }

            if (fieldInfo == null) {
                context.setValue(name, null);
            } else {
                if (isArray) {
                    if (dataType.equals(DataTypes.OBJECT)) {
                        List<Object> result = new ArrayList<>();
                        List<List<Map<String, Object>>> value = (List<List<Map<String, Object>>>) fieldInfo
                                .get(FieldInfoEnum.VALUE.getName());
                        List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                        if (CollectionUtils.isNotEmpty(children)) {
                            result.add(mapMultiDimensionalArray1(value, dimensions, children));
                        }
                        context.setValue(name, result);

                    } else {
                        if (fieldInfo.get(FieldInfoEnum.NATIVE_DATA_TYPE.getName()) != null
                                && StringUtils.equals((String) fieldInfo.get(FieldInfoEnum.NATIVE_DATA_TYPE.getName()), "factor")
                                && fieldInfo.get(FieldInfoEnum.VALUE.getName()) instanceof List) {// This code has written to
                                                                                                  // handle
                            // Fatcor without lables map
                            List<Map<String, Object>> factorData = (List<Map<String, Object>>) fieldInfo
                                    .get(FieldInfoEnum.VALUE.getName());
                            for (Map<String, Object> data : factorData) {
                                Map<String, Object> factorFieldInfo = new LinkedHashMap<String, Object>();
                                createMapFromPValue((List<Object>) data.get(FieldInfoEnum.P.getName()), factorFieldInfo);
                                if (StringUtils.equals((String) factorFieldInfo.get("modelParameterName"), "data")) {
                                    context.setValue(name,
                                            data.get(FieldInfoEnum.VALUE.getName()) instanceof List
                                                    ? convertToPrimitive(
                                                            (List) factorFieldInfo.get(FieldInfoEnum.VALUE.getName()), dataType)
                                                    : factorFieldInfo.get(FieldInfoEnum.VALUE.getName()));
                                    break;
                                }
                            }
                        } else {
                            context.setValue(name,
                                    fieldInfo.get(FieldInfoEnum.VALUE.getName()) instanceof List
                                            ? convertToPrimitive((List) fieldInfo.get(FieldInfoEnum.VALUE.getName()), dataType)
                                            : fieldInfo.get(FieldInfoEnum.VALUE.getName()));
                        }
                    }
                } else if (dataType.equals(DataTypes.OBJECT)) {
                    List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                    Object fieldValue = fieldInfo.get(FieldInfoEnum.VALUE.getName());
                    if (CollectionUtils.isNotEmpty((List<Object>) fieldValue)) {
                        if (((List<Object>) fieldValue).get(0) instanceof List) {
                            context.setValue(name,
                                    createObject2(children, ((List<List<Map<String, Object>>>) fieldValue).get(0), false));

                        } else {
                            context.setValue(name, createObject2(children, (List<Map<String, Object>>) fieldValue, false));
                        }
                    } else {
                        context.setValue(name, createObject2(children, null, false));
                    }
                } else {
                    context.setValue(name, getPrimitiveValue2(fieldInfo.get(FieldInfoEnum.VALUE.getName()), isArray, dataType));
                }
            }
        }
        return response;
    }

    private static void createMapFromPValue(final List<Object> pValue, final Map<String, Object> map) {
        if (pValue != null) {
            Object fieldValue = pValue.get(FieldInfoEnum.FIELD_NAME.getIndex());
            if (fieldValue != null) {
                map.put(FieldInfoEnum.FIELD_NAME.getName(), (String) fieldValue);
            } else {
                map.put(FieldInfoEnum.FIELD_NAME.getName(), null);
            }

            fieldValue = pValue.get(FieldInfoEnum.COLLECTION.getIndex());
            if (fieldValue != null) {
                map.put(FieldInfoEnum.COLLECTION.getName(), getBooleanValueEnum(((Integer) fieldValue).intValue()).isBoolValue());
            } else {
                map.put(FieldInfoEnum.COLLECTION.getName(), null);
            }

            fieldValue = pValue.get(FieldInfoEnum.DATA_TYPE.getIndex());
            if (fieldValue != null) {
                map.put(FieldInfoEnum.DATA_TYPE.getName(), getDataTypeValueEnum(((Integer) fieldValue).intValue()).getStrValue());
            } else {
                map.put(FieldInfoEnum.DATA_TYPE.getName(), null);
            }

            fieldValue = pValue.get(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex());
            if (fieldValue != null) {
                map.put(FieldInfoEnum.NATIVE_DATA_TYPE.getName(),
                        getNativeDataTypeValueEnum(((Integer) fieldValue).intValue()).getStrValue());
            } else {
                map.put(FieldInfoEnum.NATIVE_DATA_TYPE.getName(), null);
            }

            fieldValue = pValue.get(FieldInfoEnum.SEQUENCE.getIndex());
            if (fieldValue != null) {
                map.put(FieldInfoEnum.SEQUENCE.getName(), (String) fieldValue);
            } else {
                map.put(FieldInfoEnum.SEQUENCE.getName(), null);
            }

            fieldValue = pValue.get(FieldInfoEnum.VALUE.getIndex());
            if (fieldValue != null) {
                map.put(FieldInfoEnum.VALUE.getName(), fieldValue);
            } else {
                map.put(FieldInfoEnum.VALUE.getName(), null);
            }
        }
    }

    private static Map<String, Object> getFieldInfoPropertyValue2(Integer sequence,
            List<Map<String, Object>> modelResponsePayload) {
        Map<String, Object> fieldValue = null;
        if (CollectionUtils.isNotEmpty(modelResponsePayload)) {
            for (Map<String, Object> field : modelResponsePayload) {
                final List<Object> pValue = (List<Object>) field.get("p");

                final Object objectSequenceFieldValue = pValue.get(FieldInfoEnum.SEQUENCE.getIndex());
                Integer integerSequenceFieldValue = null;
                if (objectSequenceFieldValue != null) {
                    integerSequenceFieldValue = Integer.valueOf((String) objectSequenceFieldValue);
                }

                if (sequence != null && integerSequenceFieldValue != null && sequence.equals(integerSequenceFieldValue)) {
                    fieldValue = new LinkedHashMap<String, Object>();
                    createMapFromPValue(pValue, fieldValue);
                    break;
                }
            }
        }

        return fieldValue;
    }

    private static Object getPrimitiveValue2(final Object value, final Boolean isArray, final String dataType) {
        Object returnValue = null;
        LOGGER.debug(String.format("Datatype :: %s", dataType));
        if (value != null && value instanceof List && !isArray && CollectionUtils.isNotEmpty(((List<Object>) value))) {// NOPMD
            returnValue = convertValue2(((List<Object>) value).get(0), dataType, isArray);
        } else if (value != null && !isArray && !(value instanceof List && CollectionUtils.isEmpty((List) (value)))) {
            returnValue = convertValue2(value, dataType, isArray);
        }
        return returnValue;
    }

    public static void removeData2(List<Map<String, Object>> modelResponsePayload,
            List<Map<String, Object>> modelResponseWithOutData) {
        for (Map<String, Object> data : modelResponsePayload) {
            Map<String, Object> newFieldInfo = new LinkedHashMap<>();
            List<Object> pValue = (List<Object>) data.get(FieldInfoEnum.P.getName());
            if (pValue != null && pValue.get(FieldInfoEnum.VALUE.getIndex()) instanceof List) {
                List<Object> obj = new ArrayList();
                removeDataFromList2((List) pValue.get(FieldInfoEnum.VALUE.getIndex()), obj);
                pValue.set(FieldInfoEnum.VALUE.getIndex(), obj);
                newFieldInfo.put(FieldInfoEnum.P.getName(), pValue);
            } else {
                newFieldInfo.put(FieldInfoEnum.P.getName(), pValue.get(FieldInfoEnum.VALUE.getIndex()));
            }
            modelResponseWithOutData.add(newFieldInfo);
        }
    }

    private static List<Object> removeDataFromList2(List<Object> withData, List<Object> withoutData) {
        for (Object obj : withData) {
            if (obj instanceof Map) {
                Map<String, Object> newFieldInfo = new LinkedHashMap<>();
                Map<String, Object> fieldInfoMap = (Map<String, Object>) obj;
                List<Object> pValue = (List<Object>) fieldInfoMap.get(FieldInfoEnum.P.getName());
                final Object fieldName = pValue.get(FieldInfoEnum.FIELD_NAME.getIndex());
                final String dataType = getDataTypeValueEnum(
                        Integer.valueOf(pValue.get(FieldInfoEnum.DATA_TYPE.getIndex()).toString())).getStrValue();
                final Object pValueValue = pValue.get(FieldInfoEnum.VALUE.getIndex());

                if (fieldName != null && fieldName.toString().equals("data") && dataType != null
                        && dataType.toString().equals("object") && pValueValue instanceof List) {
                    List<Object> list = (List) pValueValue;
                    removeDataFromList2(list, withoutData);
                } else if (pValue instanceof List) {
                    newFieldInfo.put(FieldInfoEnum.P.getName(), pValue);
                    // List<Object> emptyData = new ArrayList<Object>();
                    // emptyData = removeDataFromList2((List) pValueValue, emptyData);
                    withoutData.add(newFieldInfo);
                } else {
                    withoutData.add(obj);
                }

            } else {
                withoutData.add(obj);
            }
        }
        return withoutData;

    }

    private static Object convertValue2(Object value, String datatype, boolean isArray) {
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
                returnValue = value != null ? ((Double) value).intValue() : null;
                break;
            case STRING:
            case DOUBLE:
            default:
                if (value instanceof Map) {
                    returnValue = (List<Object>) ((Map) value).get(FieldInfoEnum.P.getName());
                    returnValue = ((List<Object>) returnValue).get(FieldInfoEnum.VALUE.getIndex());
                    // returnValue = ((Map) value).get("value");
                    if (returnValue instanceof List && CollectionUtils.isNotEmpty((List) returnValue)) {
                        returnValue = ((List) returnValue).get(0);
                    } else {
                        returnValue = null;// NOPMD

                    }
                } else if (value instanceof List) {
                    returnValue = ((List) value).get(FieldInfoEnum.VALUE.getIndex());
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

    private static List convertToPrimitive(List list, String datatype) {
        List returnList = new ArrayList();
        for (Object object : list) {
            if (object instanceof List) {
                returnList.add(convertToPrimitive((List) object, datatype));
            } else {
                if (object == null) {
                    returnList.add(object);
                } else if (StringUtils.equals("double", datatype) && object instanceof Double) {
                    returnList.add((Double) object);
                } else if (((StringUtils.equals("string", datatype) || StringUtils.equals("date", datatype)))
                        && object instanceof String) {
                    returnList.add(String.valueOf(object));
                } else if (StringUtils.equals("integer", datatype) && object instanceof Double) {
                    returnList.add(((Double) object).intValue());
                } else if (StringUtils.equals("integer", datatype) && object instanceof Integer) {
                    returnList.add((Integer) object);
                } else if (StringUtils.equals("boolean", datatype) && object instanceof Boolean) {
                    returnList.add((Boolean) object);
                }
            }
        }
        return returnList;
    }

    private static Object mapMultiDimensionalArray1(Object value, List dimensions, List<Map<String, Object>> parameters)
            throws SystemException {
        if (value instanceof Map) {
            final List<Map<String, Object>> list = new ArrayList<>();
            final Map<String, Object> mapValue = (Map<String, Object>) value;
            list.add(mapValue);

            List<Map<String, Object>> matchedParameters = new ArrayList<>();
            Object oSequence = mapValue.get(FieldInfoEnum.SEQUENCE.getName());
            Integer iSequence = null;
            if (oSequence != null) {
                iSequence = Integer.parseInt(oSequence.toString());
            }
            for (Map<String, Object> item : parameters) {
                Object parameterSequnce = item.get(FieldInfoEnum.SEQUENCE.getName());
                if (parameterSequnce != null) {
                    Integer iParameterSequnce = (Integer) parameterSequnce;
                    if (iParameterSequnce.equals(iSequence)) {
                        matchedParameters.add(item);
                    }
                }
            }

            int zeroSize = 0;
            if (matchedParameters.size() > zeroSize) {
                return createObject2(matchedParameters, list, false);
            } else {
                return createObject2(parameters, list, false);
            }
        }

        List<Object> req = (List<Object>) value;
        Object[] response = null;
        if (CollectionUtils.isNotEmpty(req) && CollectionUtils.isNotEmpty(dimensions)) {
            if (((List<Object>) req).get(0) instanceof List) {
                response = new Object[req.size()];
                for (int i = 0; i < req.size(); i++) {
                    response[i] = mapMultiDimensionalArray1(req.get(i), dimensions.subList(0, dimensions.size() - 1), parameters);
                }
            } else {
                return createObject2(parameters, (List<Map<String, Object>>) value, false);
            }

        } else {
            return createObject2(parameters, (List<Map<String, Object>>) value, false);
        }
        return response;
    }
}
