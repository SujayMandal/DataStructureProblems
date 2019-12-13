package com.ca.umg.rt.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ModelLanguages;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.validator.DataTypes;

public class ExcelModelResponseBuilder extends AbstractModelResponseBuilder {
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
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes", "PMD.AvoidReassigningParameters", "PMD.ExcessiveMethodLength" })
    public Map<String, Object> createObject(List<Map<String, Object>> parameters, List<Map<String, Object>> modelResponsePayload,
            boolean isList, List<String> errorMessages, Boolean modelOpValidation) throws SystemException {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        JXPathContext context = JXPathContext.newContext(response);

        for (Map<String, Object> item : parameters) {

            String name = item.get("modelParamName") != null ? (String) item.get("modelParamName") : (String) item.get("name");
            String apiName = item.get("apiName") != null ? (String) item.get("apiName") : (String) item.get("name");

            Integer sequence = (Integer) item.get(SEQUENCE);
            Map<String, Object> dataTypeObject = (Map<String, Object>) item.get("datatype");
            String dataType = (String) dataTypeObject.get("type");
            Boolean isArray = (Boolean) dataTypeObject.get("array");
            String fractionPrecision = (String)((Map<String, Object>)dataTypeObject.get(RuntimeConstants.PROPERTIES)).get("fractionDigits");
            Integer fractionDigits = Integer.valueOf(fractionPrecision!=null?fractionPrecision:"0");
            String flatenedName = (String) item.get("flatenedName");
            String length = "";
            String dimensionsString = "";

            List dimensions = null;
            if (isArray) {
                dimensions = ((List) ((Map) dataTypeObject.get("properties")).get("dimensions"));
            }

            if (dimensions != null) {
                dimensionsString = dimensions.toString();
            }

            if (dataTypeObject != null) {
                Map<String, Object> propertiesObject = (Map<String, Object>) dataTypeObject.get("properties");
                if (propertiesObject != null) {
                    length = (String) propertiesObject.get("length") == null ? "" : (String) propertiesObject.get("length");
                }
            }

            Map<String, Object> fieldInfo = null;
            if (modelResponsePayload != null) {
                if (isList) {
                    fieldInfo = modelResponsePayload.get(sequence - 1);
                } else {
                    fieldInfo = getFieldInfoPropertyValue(name, modelResponsePayload);
                }
            }

            if (fieldInfo == null) {
                context.setValue(apiName, null);
            } else {
                if (isArray) {
                    if (dataType.equals(DataTypes.OBJECT)) {
                        List<Object> result = new ArrayList<>();
                        List<List<Map<String, Object>>> value = (List<List<Map<String, Object>>>) fieldInfo
                                .get(MessageVariables.VALUE);
                        List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                        if (CollectionUtils.isNotEmpty(children)) {
                            result.add(mapMultiDimensionalArray(value, dimensions, children, errorMessages, modelOpValidation));
                        }
                        context.setValue(apiName, result);

                    } else {
                        List<Object> values = (List) fieldInfo.get(MessageVariables.VALUE);
                        List<Object> newValues = new ArrayList<Object>();

                        if (values != null && values.get(0) instanceof List) {
                            newValues = values;
                        } else {
                            for (Object value : values) {
                                StringBuffer errorMessage = new StringBuffer();
                                newValues.add(getPrimitiveValueForExcel(value, isArray, dataType, errorMessage,
                                        (String) item.get("flatenedName"), modelOpValidation,fractionDigits));
                                if (StringUtils.isNotEmpty(errorMessage)) {
                                    errorMessages.add(errorMessage.toString());
                                }
                            }
                        }

                        if (newValues != null && modelOpValidation) {
                            validateDimensions(dimensionsString, flatenedName, errorMessages, newValues.toString());
                            validateLength(dimensionsString, flatenedName, errorMessages, newValues, dataType, length);
                        }
                        context.setValue(apiName, newValues);
                    }
                } else if (dataType.equals(DataTypes.OBJECT)) {
                    List<Map<String, Object>> children = (List<Map<String, Object>>) item.get(CHILDREN);
                    Object fieldValue = fieldInfo.get(MessageVariables.VALUE);
                    if (CollectionUtils.isNotEmpty((List<Object>) fieldValue)) {
                        if (((List<Object>) fieldValue).get(0) instanceof List) {
                            context.setValue(apiName,
                                    createObject(children, ((List<List<Map<String, Object>>>) fieldValue).get(0), false,
                                            errorMessages, modelOpValidation));
                        } else {
                            context.setValue(apiName, createObject(children, (List<Map<String, Object>>) fieldValue, false,
                                    errorMessages, modelOpValidation));
                        }
                    } else {
                        context.setValue(apiName, createObject(children, null, false, errorMessages, modelOpValidation));
                    }
                } else {
                    StringBuffer errorMessage = new StringBuffer();
                    Object result = getPrimitiveValue(fieldInfo.get(MessageVariables.VALUE), isArray, dataType, errorMessage,
                            (String) item.get("flatenedName"), modelOpValidation, ModelLanguages.EXCEL.toString(),fractionDigits);
                    if (result != null && modelOpValidation) {
                        validateDimensions(dimensionsString, flatenedName, errorMessages, result.toString());
                        validateLength(dimensionsString, flatenedName, errorMessages, result.toString(), dataType, length);
                    }
                    context.setValue(apiName, result);
                    if (StringUtils.isNotEmpty(errorMessage)) {
                        generateOutputError(fieldInfo.get(MessageVariables.VALUE), errorMessage.toString(), errorMessages);
                    }
                }
            }
        }
        return response;
    }

    private Object mapMultiDimensionalArray(Object value, List dimensions, List<Map<String, Object>> parameters,
            List<String> errorMessages, Boolean modelOpValidation) throws SystemException {
        List<Object> req = (List<Object>) value;
        Object[] response = null;
        if (CollectionUtils.isNotEmpty(req) && CollectionUtils.isNotEmpty(dimensions)) {
            response = new Object[req.size()];
            for (int i = 0; i < req.size(); i++) {
                response[i] = mapMultiDimensionalArray(req.get(i), dimensions.subList(0, dimensions.size() - 1), parameters,
                        errorMessages, modelOpValidation);
            }
        } else {
            return createObject(parameters, (List<Map<String, Object>>) value, false, errorMessages, modelOpValidation);
        }
        return response;
    }


    @Override
    public List<Map<String, Object>> createObjectForRList(List<Map<String, Object>> parameters,
            List<Map<String, Object>> modelResponsePayload, boolean isList, String seqPath, List<Map<String, Object>> errorPath,
            List<String> errorMessages, Boolean modelOpValidation) throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> createObject(List<Map<String, Object>> parameters, List<Map<String, Object>> modelResponsePayload,
            boolean isList, String seqPath, List<Map<String, Object>> errorPath, List<String> errorMessages,
            Boolean modelOpValidation, Boolean hasWrapperOverRList) throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }
}
