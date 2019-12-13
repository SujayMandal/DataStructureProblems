package com.ca.umg.rt.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ModelLanguages;
import com.ca.umg.modelet.r.type.RDataTypes;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.transformer.ModelResponseValidatorUtil;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.validator.DataTypes;

@Named
@SuppressWarnings({ "PMD.NPathComplexity", "PMD.ExcessiveClassLength" })
public class RModelResponseBuilder extends AbstractModelResponseBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RModelResponseBuilder.class);

    @Override
    /**
     * DOCUMENT ME!
     *
     * @param parameters
     *            DOCUMENT ME!
     * @param modelResponsePayload
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws SystemException
     **/
    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.AvoidReassigningParameters", "PMD" })
    public Map<String, Object> createObject(List<Map<String, Object>> parameters, List<Map<String, Object>> modelResponsePayload,
            boolean isList, String seqPath, List<Map<String, Object>> errorPath, List<String> errorMessages,
            Boolean modelOpValidation, Boolean hasWrapperOverRlist) throws SystemException {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        JXPathContext context = JXPathContext.newContext(response);
        for (Map<String, Object> item : parameters) {
            String name = item.get("apiName") != null ? (String) item.get("apiName") : (String) item.get("name");
            Integer sequence = (Integer) item.get(SEQUENCE);
            Map<String, Object> dataTypeObject = (Map<String, Object>) item.get("datatype");
            String dataType = (String) dataTypeObject.get("type");
            String flatenedName = (String) item.get("flatenedName");
            Boolean isArray = (Boolean) dataTypeObject.get("array");
            String fractionPrecision = (String)((Map<String, Object>)dataTypeObject.get("properties")).get("fractionDigits");
            Integer fractionDigits = Integer.valueOf(fractionPrecision!=null?fractionPrecision:"0");
            String dataValue = null;
            String modelOpSequence = null;
            String modelOpDataType = null;
            String nativeDataType = (String) item.get(RuntimeConstants.NATIVE_DATATYPE);
            String fieldName = null;
            Map<String, Object> fieldInfo = null;
            String length = "";
            String dimensionsString = "";

            List dimensions = null;
            if (isArray) {
                dimensions = ((List) ((Map) dataTypeObject.get(RuntimeConstants.PROPERTIES)).get("dimensions"));
            }

            if (dimensions != null) {
                dimensionsString = dimensions.toString();
            }

            if (dataTypeObject != null) {
                Map<String, Object> propertiesObject = (Map<String, Object>) dataTypeObject.get(RuntimeConstants.PROPERTIES);
                if (propertiesObject != null) {
                    length = (String) propertiesObject.get(RuntimeConstants.LENGTH) == null ? ""
                            : (String) propertiesObject.get(RuntimeConstants.LENGTH);
                }
            }

            try {
                fieldInfo = getFieldInfo(modelResponsePayload, isList, sequence);
            } catch (Exception e) {// NOPMD
                LOGGER.error("Exception occured for field " + flatenedName, e);
                if (e instanceof ClassCastException) { // NOPMD
                    StringBuffer errorMessage = new StringBuffer();
                    errorMessage.append(String.format("Expected datatype %s for %s but received value is %s.", dataType,
                            flatenedName, modelResponsePayload));
                    generateOutputError(modelResponsePayload, errorMessage.toString(), errorMessages);
                } else {
                    ModelResponseValidatorUtil.generateErrorJson(
                            StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                    : seqPath + FrameworkConstant.DOT + modelOpSequence,
                            errorPath, dataType, flatenedName, modelResponsePayload, modelOpDataType, nativeDataType, fieldName,
                            dimensions, null);
                }
            }
            if (fieldInfo != null) {
                dataValue = fieldInfo.get(MessageVariables.VALUE) != null
                        ? (String) fieldInfo.get(MessageVariables.VALUE).toString() : null;
                modelOpSequence = StringUtils.EMPTY + fieldInfo.get("sequence");
                modelOpDataType = (String) fieldInfo.get("dataType");
                nativeDataType = (String) fieldInfo.get(RuntimeConstants.NATIVE_DATATYPE);
                fieldName = (String) fieldInfo.get(MODEL_PARAMETER_NAME);

                if (isArray && StringUtils.equalsIgnoreCase(nativeDataType, RDataTypes.R_DATA_FRAME.getName())) {
                    transposeModelResponse(fieldInfo);
                }
            }
            if (fieldInfo == null) {
                try {
                    if (item.get("mandatory").toString().equalsIgnoreCase("false")) {
                        context.setValue(name, null);
                    } else {
                    	if(!modelOpValidation){
                        	context.setValue(name, fieldInfo);
                        	}
                        errorMessages.add("Mandatory field in response is missing for field " + name);
                    }
                } catch (Exception e) {// NOPMD
                    LOGGER.error("Exception occured for field " + flatenedName, e);
                    ModelResponseValidatorUtil.generateErrorJson(
                            StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                    : seqPath + FrameworkConstant.DOT + modelOpSequence,
                            errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType, fieldName, dimensions,
                            null);
                }
            } else {
                if (isArray) {
                    if (dataType.equals(DataTypes.OBJECT)) {
                        if (StringUtils.equalsIgnoreCase(RDataTypes.R_LIST.getName(), nativeDataType)) {
                            List results = new LinkedList();
                            List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                            Object result = null;
                            if (hasWrapperOverRlist) {
                                // RList array of object contains a wrapper, here the model response would contain multiple lists
                                // i.e multiple instances of object,we need to loop and map the object instances
                                for (Map<String, Object> map : modelResponsePayload) {
                                    List<Map<String, Object>> value = (List<Map<String, Object>>) map.get("value");
                                    result = createObject(children, value, isList, seqPath, errorPath, errorMessages,
                                            modelOpValidation, false);
                                    results.add(result);
                                }
                            } else {
                                // RList is defined as array of object without container, here the the model response contain
                                // parent objects data hence we need to match the sequence number of the object and get the object
                                // data, The value attribute of the object would contain object instances which we would need to
                                // map
                                Map<String, Object> res = getObjectInstances(sequence, modelResponsePayload);
                                List<Map<String, Object>> value2 = (List<Map<String, Object>>) res.get("value");
                                for (Map<String, Object> map : value2) {
                                    result = createObject(children, (List<Map<String, Object>>) map.get("value"), isList, seqPath,
                                            errorPath, errorMessages, modelOpValidation, false);
                                    results.add(result);
                                }
                            }
                            if (((results instanceof List) && (((List) results).size() == 1) && (((List) results).get(0) == null))
                                    || results == null) {
                                if (item.get("mandatory").toString().equalsIgnoreCase("true")) {
                                	if(!modelOpValidation){
                                    	context.setValue(name, results);
                                    	}
                                    errorMessages.add("Mandatory field in response is missing for field " + name);
                                } else if(results instanceof List) {
                                    context.setValue(name, results);
                                } else {
                                	context.setValue(name, null);
                                }
                            } else {
                                context.setValue(name, results);
                            }
                        } else {
                            try {
                                List<Object> result = new ArrayList<>();
                                List<List<Map<String, Object>>> value = (List<List<Map<String, Object>>>) fieldInfo
                                        .get(MessageVariables.VALUE);
                                List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                                if (CollectionUtils.isNotEmpty(children)) {

                                    if (isArray
                                            && StringUtils.equalsIgnoreCase(nativeDataType, RDataTypes.R_DATA_FRAME.getName())) {
                                        Object[] resObj = (Object[]) mapMultiDimensionalArray1(value, dimensions, children,
                                                (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                        : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                errorPath, errorMessages, modelOpValidation);

                                        if (resObj != null && resObj.length > 0) {
                                            for (Object res : resObj) {
                                                result.add(res);
                                            }
                                        }

                                    } else {
                                        result.add(mapMultiDimensionalArray1(value, dimensions, children,
                                                (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                        : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                errorPath, errorMessages, modelOpValidation));
                                    }
                                }
                                if (((result instanceof List) && (((List) result).size() == 1)
                                        && (((List) result).get(0) == null)) || result == null) {
                                    if (item.get("mandatory").toString().equalsIgnoreCase("true")) {
                                    	if(!modelOpValidation){
                                        	context.setValue(name, result);
                                        	}
                                        errorMessages.add("Mandatory field in response is missing for field " + name);
                                    } else if(result instanceof List) {
                                        context.setValue(name, result);
                                    } else {
                                    	context.setValue(name, null);
                                    }
                                } else {
                                    context.setValue(name, result);
                                }
                            } catch (Exception e) {// NOPMD
                                LOGGER.error("Exception occured for field " + flatenedName, e);
                                ModelResponseValidatorUtil.generateErrorJson(
                                        StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                        errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType, fieldName,
                                        dimensions, null);
                            }
                        }
                    } else {
                        // PROD_ISSUE fix # start
                        if (fieldInfo.get(NATIVE_DATATYPE) != null
                                && StringUtils.equals((String) fieldInfo.get(NATIVE_DATATYPE), "factor")
                                && fieldInfo.get(MessageVariables.VALUE) instanceof List) {// This code has written to handle
                                                                                           // Fatcor without lables map
                            List<Map<String, Object>> factorData = (List<Map<String, Object>>) fieldInfo
                                    .get(MessageVariables.VALUE);
                            for (Map<String, Object> data : factorData) {
                                if (StringUtils.equals((String) data.get(MODEL_PARAMETER_NAME), RuntimeConstants.DATA)
                                        && modelOpValidation) {
                                    Object result = data.get(MessageVariables.VALUE) instanceof List
                                            ? convertToPrimitive((List) data.get(MessageVariables.VALUE), nativeDataType,
                                                    dataType, dimensions, flatenedName, fieldName, modelOpDataType,
                                                    (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                            : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                    errorPath, errorMessages, ModelLanguages.R.getLanguage(),fractionDigits)
                                            : data.get(MessageVariables.VALUE);
                                    if (result != null && modelOpValidation) {
                                        validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                        validateLength(dimensionsString, flatenedName, errorMessages, result, dataType, length);
                                    }
                                    if (((result instanceof List) && (((List) result).size() == 1)
                                            && (((List) result).get(0) == null)) || result == null) {
                                        if (item.get("mandatory").toString().equalsIgnoreCase("true")) {
                                        	if(!modelOpValidation){
                                            	context.setValue(name, result);
                                            	}
                                            errorMessages.add("Mandatory field in response is missing for field " + name);
                                        } else if(result instanceof List) {
                                            context.setValue(name, result);
                                        } else {
                                        	context.setValue(name, null);
                                        }
                                    } else {
                                        context.setValue(name, result);
                                    }
                                    break;
                                } else if (StringUtils.equals((String) data.get(MODEL_PARAMETER_NAME), RuntimeConstants.DATA)) {
                                    Object result = data.get(MessageVariables.VALUE) instanceof List
                                            ? ModelResponseValidatorUtil.convertToPrimitiveWithoutValidation(
                                                    (List) data.get(MessageVariables.VALUE), nativeDataType, dataType, dimensions,
                                                    flatenedName, fieldName, modelOpDataType,
                                                    (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                            : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                    errorPath)
                                            : data.get(MessageVariables.VALUE);
                                    if (result != null && modelOpValidation) {
                                        validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                        validateLength(dimensionsString, flatenedName, errorMessages, result.toString(), dataType,
                                                length);
                                    }
                                    if (((result instanceof List) && (((List) result).size() == 1)
                                            && (((List) result).get(0) == null)) || result == null) {
                                        if (item.get("mandatory").toString().equalsIgnoreCase("true")) {
                                        	if(!modelOpValidation){
                                            	context.setValue(name, result);
                                            	}
                                            errorMessages.add("Mandatory field in response is missing for field " + name);
                                        } else if(result instanceof List) {
                                            context.setValue(name, result);
                                        } else {
                                        	context.setValue(name, null);
                                        }
                                    } else {
                                        context.setValue(name, result);
                                    }
                                    break;

                                }
                            }
                        } else {
                            if (fieldInfo.get(MessageVariables.VALUE) instanceof List) {
                                Object result = null;
                                if (modelOpValidation) {
                                    result = fieldInfo.get(MessageVariables.VALUE) instanceof List
                                            ? convertToPrimitive((List) fieldInfo.get(MessageVariables.VALUE), nativeDataType,
                                                    dataType, dimensions, flatenedName, fieldName, modelOpDataType,
                                                    (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                            : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                    errorPath, errorMessages, ModelLanguages.R.getLanguage(),fractionDigits)
                                            : fieldInfo.get(MessageVariables.VALUE);
                                } else {
                                    result = fieldInfo.get(MessageVariables.VALUE) instanceof List
                                            ? ModelResponseValidatorUtil.convertToPrimitiveWithoutValidation(
                                                    (List) fieldInfo.get(MessageVariables.VALUE), nativeDataType, dataType,
                                                    dimensions, flatenedName, fieldName, modelOpDataType,
                                                    (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                            : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                    errorPath)
                                            : fieldInfo.get(MessageVariables.VALUE);

                                }
                                if (result != null && modelOpValidation) {
                                    validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                    validateLength(dimensionsString, flatenedName, errorMessages, result.toString(), dataType,
                                            length);
                                }
                                if (((result instanceof List) && (((List) result).size() == 1)
                                        && (((List) result).get(0) == null)) || result == null) {
                                    if (item.get("mandatory").toString().equalsIgnoreCase("true")) {
                                    	if(!modelOpValidation){
                                        	context.setValue(name, result);
                                        	}
                                        errorMessages.add("Mandatory field in response is missing for field " + name);
                                    } else if(result instanceof List) {
                                        context.setValue(name, result);
                                    } else {
                                    	context.setValue(name, null);
                                    }
                                } else {
                                    context.setValue(name, result);
                                }
                            } else {
                                List<Object> primitiveToArr = new ArrayList<Object>();
                                primitiveToArr.add(fieldInfo.get(MessageVariables.VALUE));
                                if (!dataType.equals(DataTypes.OBJECT) && modelOpValidation) {
                                    validateDimensions(dimensionsString, flatenedName, errorMessages, primitiveToArr.toString());
                                    validateLength(dimensionsString, flatenedName, errorMessages, primitiveToArr.toString(),
                                            dataType, length);
                                }
                                if (((primitiveToArr instanceof List) && (((List) primitiveToArr).size() == 1)
                                        && (((List) primitiveToArr).get(0) == null)) || primitiveToArr == null) {
                                    if (item.get("mandatory").toString().equalsIgnoreCase("true")) {
                                    	if(!modelOpValidation){
                                    	context.setValue(name, primitiveToArr);
                                    	}
                                        errorMessages.add("Mandatory field in response is missing for field " + name);
                                    } else if(primitiveToArr instanceof List) {
                                        context.setValue(name, primitiveToArr);
                                    } else {
                                    	context.setValue(name, null);
                                    }
                                } else {
                                    context.setValue(name, primitiveToArr);
                                }
                            }
                        }
                        // PROD_ISSUE fix # end
                    }
                } else if (dataType.equals(DataTypes.OBJECT)) {
                    List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                    Object fieldValue = fieldInfo.get(MessageVariables.VALUE);
                    if (CollectionUtils.isNotEmpty((List<Object>) fieldValue)) {
                        if ((((List<Object>) fieldValue).size() == 1) && ((List<Object>) fieldValue).get(0) == null) {
                            if (item.get("mandatory").toString().equalsIgnoreCase("true")) {
                            	if(!modelOpValidation){
                                	context.setValue(name, fieldValue);
                                	}
                                errorMessages.add("Mandatory field in response is missing for field " + name);
                            } else if(fieldValue instanceof List) {
                                context.setValue(name, fieldValue);
                            } else {
                            	context.setValue(name, null);
                            }
                        } else {
                            if (((List<Object>) fieldValue).get(0) instanceof List) {
                                context.setValue(name,
                                        createObject(children, ((List<List<Map<String, Object>>>) fieldValue).get(0), false,
                                                (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                        : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                errorPath, errorMessages, modelOpValidation, false));

                            } else {

                                // If RList at the output side defined as array of object inside a container then the model
                                // response would contain multiple lists and size of child elements defined under container would
                                // be 1. Just to make sure RList is defined as array of object with container we are validating
                                // the child element native datatype and array property
                                if (StringUtils.equalsIgnoreCase(RDataTypes.R_LIST.getName(), nativeDataType)
                                        && children.size() == 1
                                        && StringUtils.equalsIgnoreCase(RDataTypes.R_LIST.getName(),
                                                (String) children.get(0).get("nativeDataType"))
                                        && ((Map) children.get(0).get("datatype")) != null
                                        && (Boolean) ((Map) children.get(0).get("datatype")).get("array")) {

                                    List<Map<String, Object>> vals = (List<Map<String, Object>>) fieldValue;

                                    if (vals != null && vals.get(0).get("value") instanceof List
                                            && vals.get(0).get("value") != null
                                            && vals.get(0)
                                                    .get("value") instanceof Map
                                            && StringUtils
                                                    .equalsIgnoreCase(
                                                            (String) ((Map<String, Object>) ((List) vals.get(0).get("value"))
                                                                    .get(0)).get("nativeDataType"),
                                                            RDataTypes.R_LIST.getName())) {
                                        context.setValue(name,
                                                createObject(children, (List<Map<String, Object>>) fieldValue, false,
                                                        (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                                : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                        errorPath, errorMessages, modelOpValidation, false));
                                    } else {
                                        context.setValue(name,
                                                createObject(children, (List<Map<String, Object>>) fieldValue, false,
                                                        (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                                : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                        errorPath, errorMessages, modelOpValidation, true));
                                    }
                                } else {
                                    // if R list is defined as normal object or array of object without container then the model
                                    // response would contain complete response and we need to fetch the objects based on the
                                    // sequence number.
                                    context.setValue(name,
                                            createObject(children, (List<Map<String, Object>>) fieldValue, false,
                                                    (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                            : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                    errorPath, errorMessages, modelOpValidation, false));
                                }
                            }
                        }
                    } else {
                        if (item.get("mandatory").toString().equalsIgnoreCase("true")) {
                        	if(!modelOpValidation){
                            	context.setValue(name, fieldValue);
                            	}
                            errorMessages.add("Mandatory field in response is missing for field " + name);
                        } else if(fieldValue instanceof List) {
                            context.setValue(name, fieldValue);
                        } else {
                        	context.setValue(name, null);
                        }
                    }
                }

                else {
                    try {
                        StringBuffer errorMessage = new StringBuffer();
                        Object result = getPrimitiveValueForR(fieldInfo.get(MessageVariables.VALUE), isArray, dataType,
                                errorMessage, flatenedName, modelOpValidation, errorMessages, StringUtils.trimToEmpty(length),fractionDigits);
                        /*
                         * if (result != null && modelOpValidation) { validateDimensions(dimensionsString, flatenedName,
                         * errorMessages, result.toString()); validateLength(dimensionsString, flatenedName, errorMessages,
                         * result.toString(), dataType, length); }
                         */
                        if (result == null) {
                            if (item.get("mandatory").toString().equalsIgnoreCase("false")) {
                                context.setValue(name, null);
                            } else {
                            	if(!modelOpValidation){
                                	context.setValue(name, result);
                                	}
                                errorMessages.add("Mandatory field in response is missing for field " + name);
                            }
                        } else {
                            context.setValue(name, result);
                        }
                        if (errorMessage.length() > RuntimeConstants.INT_ZERO) {
                            generateOutputError(fieldInfo.get(MessageVariables.VALUE), errorMessage.toString(), errorMessages);

                        }
                    } catch (Exception e) {// NOPMD
                        LOGGER.error("Exception occured in createObjectForR for field " + flatenedName, e);
                        ModelResponseValidatorUtil.generateErrorJson(
                                StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                        : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType, fieldName,
                                dimensions, null);
                    }
                }
            }
        }
        return response;
    }

    /**
     * Returns objects instances for RList defined with container
     * 
     * @param sequence
     * @param modelResponse
     * @return
     */
    private Map<String, Object> getObjectInstances(Integer sequence, List<Map<String, Object>> modelResponse) {
        Map<String, Object> result = null;
        for (Map<String, Object> map : modelResponse) {
            if (map != null && map.get("sequence") != null && Integer.parseInt((String) map.get("sequence")) == sequence) {
                result = map;
            }
        }
        return result;
    }

    @SuppressWarnings({ "PMD" })
    private Object mapMultiDimensionalArray1(Object value, List dimensions, List<Map<String, Object>> parameters, String seqPath,
            List<Map<String, Object>> errorPath, List<String> errorMessages, Boolean modelOpValidation) throws SystemException {
        if (value instanceof Map) {
            final List<Map<String, Object>> list = new ArrayList<>();
            final Map<String, Object> mapValue = (Map<String, Object>) value;
            list.add(mapValue);

            List<Map<String, Object>> matchedParameters = new ArrayList<>();
            Object oSequence = mapValue.get(SEQUENCE);
            Integer iSequence = null;
            if (oSequence != null) {
                iSequence = Integer.parseInt(oSequence.toString());
            }
            for (Map<String, Object> item : parameters) {
                Object parameterSequnce = item.get(SEQUENCE);
                if (parameterSequnce != null) {
                    Integer iParameterSequnce = (Integer) parameterSequnce;
                    if (iParameterSequnce.equals(iSequence)) {
                        matchedParameters.add(item);
                    }
                }
            }

            int zeroSize = 0;
            if (matchedParameters.size() > zeroSize) {
                return createObject(matchedParameters, list, false, seqPath + RuntimeConstants.CHAR_DOT + iSequence, errorPath,
                        errorMessages, modelOpValidation, false);
            } else {
                return createObject(parameters, list, false, seqPath + RuntimeConstants.CHAR_DOT + iSequence, errorPath,
                        errorMessages, modelOpValidation, false);
            }
        }

        List<Object> req = (List<Object>) value;
        Object[] response = null;
        if (CollectionUtils.isNotEmpty(req) && CollectionUtils.isNotEmpty(dimensions)) {
            if (((List<Object>) req).get(0) instanceof List) {
                response = new Object[req.size()];
                for (int i = 0; i < req.size(); i++) {
                    response[i] = mapMultiDimensionalArray1(req.get(i), dimensions.subList(0, dimensions.size() - 1), parameters,
                            seqPath + "[" + i + "]", errorPath, errorMessages, modelOpValidation);
                }
            } else {
                return createObject(parameters, (List<Map<String, Object>>) value, false, seqPath + "[0]", errorPath,
                        errorMessages, modelOpValidation, false);
            }

        } else {
            return createObject(parameters, (List<Map<String, Object>>) value, false, seqPath + "[0]", errorPath, errorMessages,
                    modelOpValidation, false);
        }
        return response;
    }

    @Override
    public Map<String, Object> createObject(List<Map<String, Object>> parameters, List<Map<String, Object>> modelResponsePayload,
            boolean isList, List<String> errorMessages, Boolean modelOpValidation) {
        LOGGER.error("Not Appalicable");
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.AvoidReassigningParameters", "PMD.NcssMethodCount" })
    public List<Map<String, Object>> createObjectForRList(List<Map<String, Object>> parameters,
            List<Map<String, Object>> modelResponsePayload, boolean isList, String seqPath, List<Map<String, Object>> errorPath,
            List<String> errorMessages, Boolean modelOpValidation) throws SystemException {
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
        boolean isDfObject = false;
        Map<String, Object> dfOutput = new HashMap<String, Object>();
        for (int i = 0; i < modelResponsePayload.size(); i++) {
            if (StringUtils.equalsIgnoreCase((String) modelResponsePayload.get(i).get(NATIVE_DATATYPE),
                    NATIVE_DATATYPE_DATA_FRAME)) {
                isDfObject = true;
                break;
            }
        }
        if (isDfObject) {
            for (int i = RuntimeConstants.INT_ONE; i < modelResponsePayload.size(); i++) {
                List<Map<String, Object>> modelResponsePayload1 = new ArrayList<Map<String, Object>>();
                List<Map<String, Object>> parameters1 = new ArrayList<Map<String, Object>>();
                parameters1.add(parameters.get(i));
                modelResponsePayload1.add(modelResponsePayload.get(i));
                Map<String, Object> dfOutput1 = createObject(parameters1, modelResponsePayload1, isList, seqPath, errorPath,
                        errorMessages, modelOpValidation, false);
                if (dfOutput1 != null) {
                    dfOutput.putAll(dfOutput1);
                }
            }
        }
        if (dfOutput.size() > RuntimeConstants.INT_ONE) {
            Map<String, Object> res1 = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : dfOutput.entrySet()) {
                if (entry.getValue() instanceof List<?>) {
                    List<?> list = (List<?>) entry.getValue();
                    if (CollectionUtils.isNotEmpty(list)) {
                        Object obj = list.get(0);
                        if (obj instanceof Object && !(obj instanceof Number) && !(obj instanceof String)) {
                            // List<Map<String, Object>> res = (List<Map<String,
                            // Object>>)entry.getValue();
                            res1.put(entry.getKey(), obj);
                        } else {
                            res1.put(entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    res1.put(entry.getKey(), entry.getValue());
                }
            }
            response.add(res1);
            return response;
            // modelResponsePayload.add((Map<String, Object>)
            // dfOutput.get("sampleVector"));
        }

        int divideFactor;
        Map<String, Object> response1 = new HashMap<String, Object>();
        JXPathContext context = JXPathContext.newContext(response1);
        if (modelResponsePayload.size() >= parameters.size()
                && !(modelResponsePayload.get(RuntimeConstants.INT_ONE).get("value") instanceof List)) {
            divideFactor = modelResponsePayload.size() / parameters.size();
            // int sequenceIndcator = 0;

            for (int loopCount = 0; loopCount < divideFactor; loopCount++) {
                Map<String, Object> response2 = new HashMap<String, Object>();
                JXPathContext context1 = JXPathContext.newContext(response2);
                int sequenceIndcator = 0;
                for (Map<String, Object> item : parameters) {

                    /*
                     * if(sequenceIndcator > 0 && sequenceIndcator < item.size()){ loopCount = loopCount + sequenceIndcator;
                     * if(sequenceIndcator == (parameters.size() -1)){ sequenceIndcator = 0; } }
                     */
                    sequenceIndcator++;
                    String name = (String) item.get("name");
                    Integer sequence = (Integer) item.get(SEQUENCE);
                    Map<String, Object> dataTypeObject = (Map<String, Object>) item.get("datatype");
                    String dataType = (String) dataTypeObject.get("type");
                    String flatenedName = (String) item.get("flatenedName");
                    Boolean isArray = (Boolean) dataTypeObject.get("array");
                    String fractionPrecision = (String)((Map<String, Object>)dataTypeObject.get("properties")).get("fractionDigits");
                    Integer fractionDigits = Integer.valueOf(fractionPrecision!=null?fractionPrecision:"0");
                    String dataValue = null;
                    String modelOpSequence = null;
                    String modelOpDataType = null;
                    String nativeDataType = null;
                    String fieldName = null;
                    Map<String, Object> fieldInfo = null;
                    String length = "";
                    String dimensionsString = "";
                    List dimensions = null;
                    if (isArray) {
                        dimensions = ((List) ((Map) dataTypeObject.get(RuntimeConstants.PROPERTIES)).get("dimensions"));
                    }

                    if (dimensions != null) {
                        dimensionsString = dimensions.toString();
                    }

                    if (dataTypeObject != null) {
                        Map<String, Object> propertiesObject = (Map<String, Object>) dataTypeObject.get("properties");
                        if (propertiesObject != null) {
                            length = (String) propertiesObject.get(RuntimeConstants.LENGTH) == null ? ""
                                    : (String) propertiesObject.get(RuntimeConstants.LENGTH);
                        }
                    }

                    try {
                        fieldInfo = getFieldInfo1(modelResponsePayload, isList, sequence, loopCount, parameters.size());
                    } catch (Exception e) {// NOPMD
                        LOGGER.error("Exception occured for field " + flatenedName, e);

                        if (e instanceof ClassCastException) { // NOPMD
                            StringBuffer errorMessage = new StringBuffer();
                            errorMessage.append(String.format("Expected datatype %s for %s but received value is %s.", dataType,
                                    flatenedName, modelResponsePayload));
                            generateOutputError(modelResponsePayload, errorMessage.toString(), errorMessages);
                        } else {
                            ModelResponseValidatorUtil.generateErrorJson(
                                    StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                            : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                    errorPath, dataType, flatenedName, modelResponsePayload, modelOpDataType, nativeDataType,
                                    fieldName, dimensions, null);
                        }

                    }
                    if (fieldInfo != null) {
                        dataValue = fieldInfo.get(MessageVariables.VALUE) != null
                                ? (String) fieldInfo.get(MessageVariables.VALUE).toString() : null;
                        modelOpSequence = StringUtils.EMPTY + fieldInfo.get("sequence");
                        modelOpDataType = (String) fieldInfo.get("dataType");
                        nativeDataType = (String) fieldInfo.get("nativeDataType");
                        fieldName = (String) fieldInfo.get(MODEL_PARAMETER_NAME);
                    }
                    if (fieldInfo == null) {
                        try {
                            context.setValue(name, null);
                        } catch (Exception e) {// NOPMD
                            LOGGER.error("Exception occured for field " + flatenedName, e);
                            ModelResponseValidatorUtil.generateErrorJson(
                                    StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                            : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                    errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType, fieldName,
                                    dimensions, null);
                        }
                    } else {
                        if (isArray) {
                            if (dataType.equals(DataTypes.OBJECT)) {
                                try {
                                    List<Object> result = new ArrayList<>();
                                    List<List<Map<String, Object>>> value = (List<List<Map<String, Object>>>) fieldInfo
                                            .get(MessageVariables.VALUE);
                                    List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                                    if (CollectionUtils.isNotEmpty(children)) {
                                        result.add(mapMultiDimensionalArray2(value, dimensions, children,
                                                (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                        : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                errorPath, errorMessages, modelOpValidation));
                                    }
                                    context.setValue(name, result);
                                } catch (Exception e) {// NOPMD
                                    LOGGER.error("Exception occured for field " + flatenedName, e);
                                    ModelResponseValidatorUtil.generateErrorJson(
                                            StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                    : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                            errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType,
                                            fieldName, dimensions, null);
                                }

                            } else {
                                // PROD_ISSUE fix # start
                                if (fieldInfo.get(NATIVE_DATATYPE) != null
                                        && StringUtils.equals((String) fieldInfo.get(NATIVE_DATATYPE), "factor")
                                        && fieldInfo.get(MessageVariables.VALUE) instanceof List) {
                                    List<Map<String, Object>> factorData = (List<Map<String, Object>>) fieldInfo
                                            .get(MessageVariables.VALUE);
                                    for (Map<String, Object> data : factorData) {
                                        if (StringUtils.equals((String) data.get(MODEL_PARAMETER_NAME), RuntimeConstants.DATA)
                                                && FrameworkConstant.MODEL_OUTPUT.equals(modelOpValidation)) {
                                            Object result = data.get(MessageVariables.VALUE) instanceof List
                                                    ? convertToPrimitive((List) data.get(MessageVariables.VALUE), nativeDataType,
                                                            dataType, dimensions, flatenedName, fieldName, modelOpDataType,
                                                            (String) (StringUtils.equals(seqPath, StringUtils.EMPTY)
                                                                    ? modelOpSequence
                                                                    : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                            errorPath, errorMessages, ModelLanguages.R.getLanguage(),fractionDigits)
                                                    : data.get(MessageVariables.VALUE);
                                            if (result != null) {
                                                validateDimensions(dimensionsString, flatenedName, errorMessages,
                                                        result.toString());
                                                validateLength(dimensionsString, flatenedName, errorMessages, result, dataType,
                                                        length);
                                            }
                                            context.setValue(name, result);
                                            break;
                                        } else if (StringUtils.equals((String) data.get(MODEL_PARAMETER_NAME),
                                                RuntimeConstants.DATA)) {
                                            Object result = data.get(MessageVariables.VALUE) instanceof List
                                                    ? ModelResponseValidatorUtil.convertToPrimitiveWithoutValidation(
                                                            (List) data.get(MessageVariables.VALUE), nativeDataType, dataType,
                                                            dimensions, flatenedName, fieldName, modelOpDataType,
                                                            (String) (StringUtils.equals(seqPath, StringUtils.EMPTY)
                                                                    ? modelOpSequence
                                                                    : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                            errorPath)
                                                    : data.get(MessageVariables.VALUE);
                                            if (result != null) {
                                                validateDimensions(dimensionsString, flatenedName, errorMessages,
                                                        result.toString());
                                                validateLength(dimensionsString, flatenedName, errorMessages, result.toString(),
                                                        dataType, length);
                                            }
                                            context.setValue(name, result);
                                            break;

                                        }
                                    }
                                } else {
                                    if (fieldInfo.get(MessageVariables.VALUE) instanceof List) {
                                        Object result = fieldInfo.get(MessageVariables.VALUE) instanceof List
                                                ? convertToPrimitive((List) fieldInfo.get(MessageVariables.VALUE), nativeDataType,
                                                        dataType, dimensions, flatenedName, fieldName, modelOpDataType,
                                                        (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                                : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                        errorPath, errorMessages, ModelLanguages.R.getLanguage(),fractionDigits)
                                                : fieldInfo.get(MessageVariables.VALUE);
                                        if (result != null) {
                                            validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                            validateLength(dimensionsString, flatenedName, errorMessages, result.toString(),
                                                    dataType, length);
                                        }
                                        context1.setValue(name, result);
                                        response.add(response2);
                                    } else {
                                        List<Object> primitiveToArr = new ArrayList<Object>();
                                        primitiveToArr.add(fieldInfo.get(MessageVariables.VALUE));
                                        if (!dataType.equals(DataTypes.OBJECT)) {
                                            validateDimensions(dimensionsString, flatenedName, errorMessages,
                                                    primitiveToArr.toString());
                                            validateLength(dimensionsString, flatenedName, errorMessages,
                                                    primitiveToArr.toString(), dataType, length);
                                        }
                                        context.setValue(name, primitiveToArr);

                                    }
                                }
                                // PROD_ISSUE fix # end
                            }
                        } else if (dataType.equals(DataTypes.OBJECT)) {
                            List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                            Object fieldValue = fieldInfo.get(MessageVariables.VALUE);
                            if (CollectionUtils.isNotEmpty((List<Object>) fieldValue)) {
                                if (((List<Object>) fieldValue).get(0) instanceof List) {
                                    context.setValue(name,
                                            createObjectForRList(children, ((List<List<Map<String, Object>>>) fieldValue).get(0),
                                                    false,
                                                    (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                            : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                    errorPath, errorMessages, modelOpValidation));

                                } else {
                                    context.setValue(name,
                                            createObjectForRList(children, (List<Map<String, Object>>) fieldValue, false,
                                                    (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                            : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                    errorPath, errorMessages, modelOpValidation));
                                }
                            } else {
                                context.setValue(name,
                                        createObjectForRList(children, null, false,
                                                (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                        : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                errorPath, errorMessages, modelOpValidation));
                            }
                        } else {
                            try {
                                StringBuffer errorMessage = new StringBuffer();
                                Object result = getPrimitiveValueForR(fieldInfo.get(MessageVariables.VALUE), isArray, dataType,
                                        errorMessage, flatenedName, modelOpValidation,fractionDigits);
                                if (result != null) {
                                    validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                    validateLength(dimensionsString, flatenedName, errorMessages, result.toString(), dataType,
                                            length);
                                }
                                context1.setValue(name, result);
                                if (sequenceIndcator == parameters.size()) {
                                    response.add(response2);
                                }
                                if (errorMessage.length() > RuntimeConstants.INT_ZERO) {
                                    generateOutputError(fieldInfo.get(MessageVariables.VALUE), errorMessage.toString(),
                                            errorMessages);

                                }
                            } catch (Exception e) {// NOPMD
                                LOGGER.error("Exception occured in createObjectForR for field " + flatenedName, e);
                                ModelResponseValidatorUtil.generateErrorJson(
                                        StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                        errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType, fieldName,
                                        dimensions, null);
                            }
                        }
                    }
                }
            }
        }

        else {

            for (Map<String, Object> item : parameters) {
                String name = (String) item.get("name");
                Integer sequence = (Integer) item.get(SEQUENCE);
                Map<String, Object> dataTypeObject = (Map<String, Object>) item.get("datatype");
                String dataType = (String) dataTypeObject.get("type");
                String flatenedName = (String) item.get("flatenedName");
                Boolean isArray = (Boolean) dataTypeObject.get("array");
                String fractionPrecision = (String)((Map<String, Object>)dataTypeObject.get("properties")).get("fractionDigits");
                Integer fractionDigits = Integer.valueOf(fractionPrecision!=null?fractionPrecision:"0");
                String dataValue = null;
                String modelOpSequence = null;
                String modelOpDataType = null;
                String nativeDataType = null;
                String fieldName = null;
                Map<String, Object> fieldInfo = null;
                String length = "";
                String dimensionsString = "";
                List dimensions = null;
                if (isArray) {
                    dimensions = ((List) ((Map) dataTypeObject.get(RuntimeConstants.PROPERTIES)).get("dimensions"));
                }

                if (dimensions != null) {
                    dimensionsString = dimensions.toString();
                }

                if (dataTypeObject != null) {
                    Map<String, Object> propertiesObject = (Map<String, Object>) dataTypeObject.get(RuntimeConstants.PROPERTIES);
                    if (propertiesObject != null) {
                        length = (String) propertiesObject.get("length") == null ? "" : (String) propertiesObject.get("length");
                    }
                }

                try {
                    fieldInfo = getFieldInfo(modelResponsePayload, isList, sequence);
                } catch (Exception e) {// NOPMD
                    LOGGER.error("Exception occured for field " + flatenedName, e);
                    if (e instanceof ClassCastException) { // NOPMD
                        StringBuffer errorMessage = new StringBuffer();
                        errorMessage.append(String.format("Expected datatype %s for %s but received value is %s.", dataType,
                                flatenedName, modelResponsePayload));
                        generateOutputError(modelResponsePayload, errorMessage.toString(), errorMessages);
                    } else {
                        ModelResponseValidatorUtil.generateErrorJson(
                                StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                        : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                errorPath, dataType, flatenedName, modelResponsePayload, modelOpDataType, nativeDataType,
                                fieldName, dimensions, null);
                    }
                }
                if (fieldInfo != null) {
                    dataValue = fieldInfo.get(MessageVariables.VALUE) != null
                            ? (String) fieldInfo.get(MessageVariables.VALUE).toString() : null;
                    modelOpSequence = StringUtils.EMPTY + fieldInfo.get("sequence");
                    modelOpDataType = (String) fieldInfo.get("dataType");
                    nativeDataType = (String) fieldInfo.get(RuntimeConstants.NATIVE_DATATYPE);
                    fieldName = (String) fieldInfo.get(MODEL_PARAMETER_NAME);
                }
                if (fieldInfo == null) {
                    try {
                        context.setValue(name, null);
                    } catch (Exception e) {// NOPMD
                        LOGGER.error("Exception occured for field " + flatenedName, e);
                        ModelResponseValidatorUtil.generateErrorJson(
                                StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                        : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType, fieldName,
                                dimensions, null);
                    }
                } else {
                    if (isArray) {
                        if (dataType.equals(DataTypes.OBJECT)) {
                            try {
                                List<Object> result = new ArrayList<>();
                                List<List<Map<String, Object>>> value = (List<List<Map<String, Object>>>) fieldInfo
                                        .get(MessageVariables.VALUE);
                                List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                                if (CollectionUtils.isNotEmpty(children)) {
                                    result.add(mapMultiDimensionalArray2(value, dimensions, children,
                                            (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                    : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                            errorPath, errorMessages, modelOpValidation));
                                }
                                context.setValue(name, result);
                                response.add(response1);
                            } catch (Exception e) {// NOPMD
                                LOGGER.error("Exception occured for field " + flatenedName, e);
                                ModelResponseValidatorUtil.generateErrorJson(
                                        StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                        errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType, fieldName,
                                        dimensions, null);
                            }

                        } else {
                            // PROD_ISSUE fix # start
                            if (fieldInfo.get(NATIVE_DATATYPE) != null
                                    && StringUtils.equals((String) fieldInfo.get(NATIVE_DATATYPE), "factor")
                                    && fieldInfo.get(MessageVariables.VALUE) instanceof List) {// This
                                                                                               // code
                                                                                               // has
                                                                                               // written
                                                                                               // to
                                                                                               // handle
                                                                                               // Fatcor
                                                                                               // without
                                                                                               // lables
                                                                                               // map
                                List<Map<String, Object>> factorData = (List<Map<String, Object>>) fieldInfo
                                        .get(MessageVariables.VALUE);
                                for (Map<String, Object> data : factorData) {
                                    if (StringUtils.equals((String) data.get(MODEL_PARAMETER_NAME), RuntimeConstants.DATA)
                                            && FrameworkConstant.MODEL_OUTPUT.equals(modelOpValidation)) {
                                        Object result = data.get(MessageVariables.VALUE) instanceof List
                                                ? convertToPrimitive((List) data.get(MessageVariables.VALUE), nativeDataType,
                                                        dataType, dimensions, flatenedName, fieldName, modelOpDataType,
                                                        (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                                : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                        errorPath, errorMessages, ModelLanguages.R.getLanguage(),fractionDigits)
                                                : data.get(MessageVariables.VALUE);
                                        if (result != null) {
                                            validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                            validateLength(dimensionsString, flatenedName, errorMessages, result.toString(),
                                                    dataType, length);
                                        }
                                        context.setValue(name, result);
                                        response.add(response1);
                                        break;
                                    } else if (StringUtils.equals((String) data.get(MODEL_PARAMETER_NAME),
                                            RuntimeConstants.DATA)) {
                                        Object result = data.get(MessageVariables.VALUE) instanceof List
                                                ? ModelResponseValidatorUtil.convertToPrimitiveWithoutValidation(
                                                        (List) data.get(MessageVariables.VALUE), nativeDataType, dataType,
                                                        dimensions, flatenedName, fieldName, modelOpDataType,
                                                        (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                                : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                        errorPath)
                                                : data.get(MessageVariables.VALUE);
                                        if (result != null) {
                                            validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                            validateLength(dimensionsString, flatenedName, errorMessages, result.toString(),
                                                    dataType, length);
                                        }
                                        context.setValue(name, result);
                                        response.add(response1);
                                        break;

                                    }
                                }
                            } else {
                                if (fieldInfo.get(MessageVariables.VALUE) instanceof List) {
                                    Object result = fieldInfo.get(MessageVariables.VALUE) instanceof List
                                            ? convertToPrimitive((List) fieldInfo.get(MessageVariables.VALUE), nativeDataType,
                                                    dataType, dimensions, flatenedName, fieldName, modelOpDataType,
                                                    (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                            : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                    errorPath, errorMessages, ModelLanguages.R.getLanguage(),fractionDigits)
                                            : fieldInfo.get(MessageVariables.VALUE);
                                    if (result != null) {
                                        validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                        validateLength(dimensionsString, flatenedName, errorMessages, result.toString(), dataType,
                                                length);
                                    }
                                    context.setValue(name, result);
                                    response.add(response1);
                                } else {
                                    List<Object> primitiveToArr = new ArrayList<Object>();
                                    primitiveToArr.add(fieldInfo.get(MessageVariables.VALUE));
                                    if (!dataType.equals(DataTypes.OBJECT)) {
                                        validateDimensions(dimensionsString, flatenedName, errorMessages,
                                                primitiveToArr.toString());
                                        validateLength(dimensionsString, flatenedName, errorMessages, primitiveToArr.toString(),
                                                dataType, length);
                                    }
                                    context.setValue(name, primitiveToArr);
                                    response.add(response1);
                                }
                            }
                            // PROD_ISSUE fix # end
                        }
                    } else if (dataType.equals(DataTypes.OBJECT)) {
                        List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                        Object fieldValue = fieldInfo.get(MessageVariables.VALUE);
                        if (CollectionUtils.isNotEmpty((List<Object>) fieldValue)) {
                            if (((List<Object>) fieldValue).get(0) instanceof List) {
                                context.setValue(name,
                                        createObjectForRList(children, ((List<List<Map<String, Object>>>) fieldValue).get(0),
                                                false,
                                                (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                        : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                errorPath, errorMessages, modelOpValidation));
                                response.add(response1);

                            } else {
                                context.setValue(name,
                                        createObjectForRList(children, (List<Map<String, Object>>) fieldValue, false,
                                                (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                        : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                                errorPath, errorMessages, modelOpValidation));
                                response.add(response1);
                            }
                        } else {
                            context.setValue(name,
                                    createObjectForRList(children, null, false,
                                            (String) (StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                                    : seqPath + FrameworkConstant.DOT + modelOpSequence),
                                            errorPath, errorMessages, modelOpValidation));
                            response.add(response1);
                        }

                    } else {
                        try {
                            StringBuffer errorMessage = new StringBuffer();
                            Object result = getPrimitiveValueForR(fieldInfo.get(MessageVariables.VALUE), isArray, dataType,
                                    errorMessage, flatenedName, modelOpValidation,fractionDigits);
                            if (result != null) {
                                validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                                validateLength(dimensionsString, flatenedName, errorMessages, result.toString(), dataType,
                                        length);
                            }
                            context.setValue(name, result);
                            if (errorMessage.length() > RuntimeConstants.INT_ZERO) {
                                generateOutputError(fieldInfo.get(MessageVariables.VALUE), errorMessage.toString(),
                                        errorMessages);

                            }
                        } catch (Exception e) {// NOPMD
                            LOGGER.error("Exception occured in createObjectForR for field " + flatenedName, e);
                            ModelResponseValidatorUtil.generateErrorJson(
                                    StringUtils.equals(seqPath, StringUtils.EMPTY) ? modelOpSequence
                                            : seqPath + FrameworkConstant.DOT + modelOpSequence,
                                    errorPath, dataType, flatenedName, dataValue, modelOpDataType, nativeDataType, fieldName,
                                    dimensions, null);
                        }
                    }
                }
            }
        }
        return response;
    }

    private Object mapMultiDimensionalArray2(Object value, List dimensions, List<Map<String, Object>> parameters, String seqPath,
            List<Map<String, Object>> errorPath, List<String> errorMessages, Boolean modelOpValidation) throws SystemException {
        if (value instanceof Map) {
            final List<Map<String, Object>> list = new ArrayList<>();
            final Map<String, Object> mapValue = (Map<String, Object>) value;
            list.add(mapValue);

            List<Map<String, Object>> matchedParameters = new ArrayList<>();
            Object oSequence = mapValue.get(SEQUENCE);
            Integer iSequence = null;
            if (oSequence != null) {
                iSequence = Integer.parseInt(oSequence.toString());
            }
            for (Map<String, Object> item : parameters) {
                Object parameterSequnce = item.get(SEQUENCE);
                if (parameterSequnce != null) {
                    Integer iParameterSequnce = (Integer) parameterSequnce;
                    if (iParameterSequnce.equals(iSequence)) {
                        matchedParameters.add(item);
                    }
                }
            }

            int zeroSize = 0;
            if (matchedParameters.size() > zeroSize) {
                return createObjectForRList(matchedParameters, list, false, seqPath + RuntimeConstants.CHAR_DOT + iSequence,
                        errorPath, errorMessages, modelOpValidation);
            } else {
                return createObjectForRList(parameters, list, false, seqPath + RuntimeConstants.CHAR_DOT + iSequence, errorPath,
                        errorMessages, modelOpValidation);
            }
        }

        List<Object> req = (List<Object>) value;
        Object[] response = null;
        if (CollectionUtils.isNotEmpty(req) && CollectionUtils.isNotEmpty(dimensions)) {
            if (((List<Object>) req).get(0) instanceof List) {
                response = new Object[req.size()];
                for (int i = 0; i < req.size(); i++) {
                    response[i] = mapMultiDimensionalArray2(req.get(i), dimensions.subList(0, dimensions.size() - 1), parameters,
                            seqPath + "[" + i + "]", errorPath, errorMessages, modelOpValidation);
                }
            } else {

                return createObjectForRList(parameters, (List<Map<String, Object>>) value, false, seqPath + "[0]", errorPath,
                        errorMessages, modelOpValidation);
            }

        } else {
            return createObjectForRList(parameters, (List<Map<String, Object>>) value, false, seqPath + "[0]", errorPath,
                    errorMessages, modelOpValidation);
        }
        return response;
    }

}