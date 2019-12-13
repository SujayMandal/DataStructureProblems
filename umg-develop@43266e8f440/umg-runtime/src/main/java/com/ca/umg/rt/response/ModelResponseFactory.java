package com.ca.umg.rt.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.ioreduce.ME2ResponseExpander;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ModelLanguages;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;

/**
 * @author basanaga This class having common code cross all language
 *
 *         Factory to get model response builder based on language
 */
@Named
public class ModelResponseFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelResponseFactory.class);

	private SystemParameterProvider systemParameterProvider;

	public Object getModelResponse(String language, List<Map<String, Object>> midOutput,
			List<Map<String, Object>> modelResponsePayload, boolean isList, String seqPath,
			List<Map<String, Object>> errorPath, List<String> errorMessages, Boolean modelOpValidation)
					throws SystemException {

		AbstractModelResponseBuilder responseBuilder;
		Object response = null;

		switch (ModelLanguages.valueOf(StringUtils.toUpperEnglish(language))) {
		case MATLAB:
			responseBuilder = new MatlabModelResponseBuilder();
			response = responseBuilder.createObject(midOutput, modelResponsePayload, isList, errorMessages,
					modelOpValidation);
			break;
		case R:
			responseBuilder = new RModelResponseBuilder();

			List<Map<String, Object>> modelResponseWithOutData = new ArrayList<>();
			if (!requireModelSizeReduction()) {

				removeData(modelResponsePayload, modelResponseWithOutData);
                // Map<String, Object> checkArray = midOutput.get(0);
                // Map<String, Object> dataTypeMap = (Map<String, Object>) checkArray.get("datatype");
                // boolean isArray = Boolean.valueOf((boolean) dataTypeMap.get("array"));
                // if ((modelResponsePayload.get(0).get(RuntimeConstants.NATIVE_DATATYPE).toString()
                // .equals(RuntimeConstants.NATIVE_DATATYPE_LIST)) && isArray) {
                //
                // response = responseBuilder.createObjectForRList(midOutput, modelResponsePayload, isList, seqPath,
                // errorPath, errorMessages, modelOpValidation);
                // } else {

					response = responseBuilder.createObject(midOutput, modelResponseWithOutData, true, "", errorPath,
                        errorMessages, modelOpValidation, false);

                // }
			} else {
				long startTime1 = System.currentTimeMillis();
				ME2ResponseExpander.removeData2(modelResponsePayload, modelResponseWithOutData);
				response = ME2ResponseExpander.createObject2(midOutput, modelResponseWithOutData, true);
				LOGGER.error("Resposne 2 : {}", System.currentTimeMillis() - startTime1);
			}

			break;
		case EXCEL:
			responseBuilder = new ExcelModelResponseBuilder();
			response = responseBuilder.createObject(midOutput, modelResponsePayload, isList, errorMessages,
					modelOpValidation);
			break;			
		default :				
			throw new SystemException(RuntimeExceptionCode.RSE000834, new Object[] {"No Execution Language defined"});		
			
		}

		return response;

	}

	private boolean requireModelSizeReduction() {
		long startTime = System.currentTimeMillis();
		boolean bModelSizeReduction = false;
		final String sModelSizeReduction = systemParameterProvider
				.getParameter(SystemParameterProvider.REQUIRE_MODEL_SIZE_REDUCTION);
		if (sModelSizeReduction != null) {
			bModelSizeReduction = Boolean.valueOf(sModelSizeReduction);
		}
		LOGGER.error("Requeired model Size Reduction : {}", System.currentTimeMillis() - startTime);
		return bModelSizeReduction;
	}

	private void removeData(List<Map<String, Object>> modelResponsePayload,
			List<Map<String, Object>> modelResponseWithOutData) {
		for (Map<String, Object> data : modelResponsePayload) {
			Map<String, Object> newFieldInfo = new LinkedHashMap<>();
			newFieldInfo.put(RuntimeConstants.MODEL_PARAMETER_NAME, data.get(RuntimeConstants.MODEL_PARAMETER_NAME));
			newFieldInfo.put(RuntimeConstants.SEQUENCE, data.get(RuntimeConstants.SEQUENCE));
			newFieldInfo.put(RuntimeConstants.DATATYPE, data.get(RuntimeConstants.DATATYPE));
			newFieldInfo.put(RuntimeConstants.COLLECTION, data.get(RuntimeConstants.COLLECTION));
			if (data.get(MessageVariables.VALUE) instanceof List) {
				List<Object> obj = new ArrayList();
				removeDataFromList((List) data.get(MessageVariables.VALUE), obj);
				newFieldInfo.put(MessageVariables.VALUE, obj);
			} else {
				newFieldInfo.put(MessageVariables.VALUE, data.get(MessageVariables.VALUE));

			}
			newFieldInfo.put(RuntimeConstants.NATIVE_DATATYPE, data.get(RuntimeConstants.NATIVE_DATATYPE));
			modelResponseWithOutData.add(newFieldInfo);
		}

	}
	
	  private List<Object> removeDataFromList(List<Object> withData, List<Object> withoutData) {
	        for (Object obj : withData) {
	            if (obj instanceof Map) {
	                Map<String, Object> newFieldInfo = new LinkedHashMap<>();
	                Map<String, Object> fieldInfoMap = (Map<String, Object>) obj;
	                if (fieldInfoMap.get(RuntimeConstants.MODEL_PARAMETER_NAME) != null && fieldInfoMap.get(RuntimeConstants.MODEL_PARAMETER_NAME).equals("data")
	                        && fieldInfoMap.get(RuntimeConstants.DATATYPE).equals("object")
	                        && fieldInfoMap.get(MessageVariables.VALUE) instanceof List) {
	                    List<Object> list = (List) fieldInfoMap.get(MessageVariables.VALUE);
	                    removeDataFromList(list, withoutData);
	                } else if (fieldInfoMap.get(MessageVariables.VALUE) instanceof List) {
	                    newFieldInfo.put(RuntimeConstants.MODEL_PARAMETER_NAME, fieldInfoMap.get(RuntimeConstants.MODEL_PARAMETER_NAME));
	                    newFieldInfo.put(RuntimeConstants.SEQUENCE, fieldInfoMap.get(RuntimeConstants.SEQUENCE));
	                    newFieldInfo.put(RuntimeConstants.DATATYPE, fieldInfoMap.get(RuntimeConstants.DATATYPE));
	                    newFieldInfo.put(RuntimeConstants.COLLECTION, fieldInfoMap.get(RuntimeConstants.COLLECTION));
	                    newFieldInfo.put(RuntimeConstants.NATIVE_DATATYPE, fieldInfoMap.get(RuntimeConstants.NATIVE_DATATYPE));
	                    List<Object> emptyData = new ArrayList<Object>();
	                    emptyData = removeDataFromList((List) fieldInfoMap.get(MessageVariables.VALUE), emptyData);
	                    newFieldInfo.put(MessageVariables.VALUE, emptyData);
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

	public void setSystemParameterProvider(SystemParameterProvider systemParameterProvider) {
		this.systemParameterProvider = systemParameterProvider;
	}
}
