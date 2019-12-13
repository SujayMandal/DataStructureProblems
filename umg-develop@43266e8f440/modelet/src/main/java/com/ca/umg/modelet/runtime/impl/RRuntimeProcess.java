/*
 * RRuntimeProcess.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * 
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.runtime.impl;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.umg.modelet.constants.ErrorCodes.ME0020;
import static com.ca.umg.modelet.exception.ModeletExceptionCodes.MOSE000001;
import static java.lang.Long.valueOf;
import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.common.RModel;
import com.ca.umg.modelet.common.RServeModel;
import com.ca.umg.modelet.common.ResponseHeaderInfo;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.converter.Converter;
import com.ca.umg.modelet.converter.impl.RConverter;
import com.ca.umg.modelet.lang.type.DataType;
import com.ca.umg.modelet.runtime.ModeletRuntime;
import com.ca.umg.modelet.runtime.RuntimeProcess;

@SuppressWarnings("PMD")
public class RRuntimeProcess implements RuntimeProcess {
    private static final Logger LOGGER = LoggerFactory.getLogger(RRuntimeProcess.class);

    public Converter converter;

    private final ModeletRuntime rRuntime;

    public RRuntimeProcess(final ModeletRuntime rRuntime) {
        this.rRuntime = rRuntime;
        converter = new RConverter();
    }

    @Override
    public ModelResponseInfo execute(final Object input, SystemInfo systemInfo) throws BusinessException, SystemException {
        final ModelRequestInfo modelRequestInfo = (ModelRequestInfo) input;
        final HeaderInfo headerInfo = modelRequestInfo.getHeaderInfo();
        LOGGER.info("Received request to execute model {}.", modelRequestInfo.getHeaderInfo().getModelPackageName());

        final long modelGetTime = System.currentTimeMillis();
        MDC.put("tID", headerInfo.getTransactionCriteria().getUmgTransactionId());
        @SuppressWarnings("unchecked")
        final long marshallStart = System.currentTimeMillis();
        List<DataType> inputList = (List<DataType>) converter.marshall(modelRequestInfo);
        LOGGER.debug("Marshalling time: " + (System.currentTimeMillis() - marshallStart));

        final long start = currentTimeMillis();
        /* changes for UMG-5015 */
        final Map<String, Object> output;
        final Object model;
        Object result = null;
        String command = null;
        String rlog = null;
        if(systemInfo.getrMode() != null && StringUtils.equalsIgnoreCase(SystemConstants.R_SERVE, systemInfo.getrMode())) {
        	model = (RServeModel) rRuntime.getModel(headerInfo);
        	output = ((RServeModel) model).executeModel(inputList,systemInfo,headerInfo);
        	if (output != null) {
                result = (REXP) output.get("result");
                command = (String) output.get("command");
                rlog = (String) output.get("rText");
            }
        } else {
        	model = (RModel) rRuntime.getModel(headerInfo);
        	output = ((RModel) model).executeModel(inputList, headerInfo);
        	if (output != null) {
                result = (org.rosuda.JRI.REXP) output.get("result");
                command = (String) output.get("command");
                rlog = (String) output.get("rText");
            }
        }
        inputList = null;// NOPMD
        final long end = currentTimeMillis();
        LOGGER.info("execution of instance successful");

        final ModelResponseInfo modelResponseInfo = new ModelResponseInfo();

        LOGGER.info("initiated unmarshall of message");
        final long unmarshallStart = System.currentTimeMillis();
        if(result instanceof REXP){
        	if (((REXP) result).inherits("try-error")) {
        		String errorMessage = null;
        		try {
        			errorMessage = ((REXP) result).asString();
        		} catch (REXPMismatchException e) {
        			errorMessage = "";
        			LOGGER.error("Could not convert error message to String");
        		}
        		errorMessage = errorMessage.concat("-ExecutionCommandUsed-" + command);
        		SystemException.newSystemException(MOSE000001, new String[] { errorMessage });
        	}
        }

        Object respPayload;
        if (modelRequestInfo.getHeaderInfo().isModelSizeReduction()) {
            respPayload = converter.unmarshall(result, modelRequestInfo.getHeaderInfo().isModelSizeReduction());
        } else {
            respPayload = converter.unmarshall(result);
        }

        modelResponseInfo.setPayload(respPayload);
        /* changes for UMG-5015 */
        ResponseHeaderInfo responseHeader = new ResponseHeaderInfo();
        responseHeader.setExecutionCommand(command);
        if (output.get("result") != null) {
            responseHeader.setExecutionResponse(output.get("result").toString());
        }
        if (rlog != null) {
            responseHeader.setExecutionLogs(rlog);
        }
        modelResponseInfo.setResponseHeaderInfo(responseHeader);
        LOGGER.info("Unmarshalling time: " + (System.currentTimeMillis() - unmarshallStart));

        /*
         * final ObjectMapper mapper = new ObjectMapper(); try { if (modelRequestInfo.getHeaderInfo().isModelSizeReduction()) {
         * LOGGER.info("Model size reduction is intiated for this request"); LOGGER.info("Response payload before converting : {}"
         * , mapper.writeValueAsString(respPayload)); List<Map<String, Object>> newRespPayload =
         * ModeletResponseReducer.convert(respPayload); modelResponseInfo.setPayload(newRespPayload); LOGGER.info(
         * "Response payload after converting : {} ", mapper.writeValueAsString(newRespPayload)); } else { LOGGER.info(
         * "Model size reduction is NOT intiated for this request"); modelResponseInfo.setPayload(respPayload); } } catch (final
         * JsonProcessingException e) { LOGGER.error("Exception occured.", e); throw new
         * BusinessException(ModeletExceptionCodes.MOBE000005, new Object[] {e.getLocalizedMessage()}); } catch (final IOException
         * ioe) { LOGGER.error("Exception occured.", ioe); throw new BusinessException(ModeletExceptionCodes.MOBE000005, new
         * Object[] {ioe.getLocalizedMessage()}); }
         */

        modelResponseInfo.setModelName(modelRequestInfo.getHeaderInfo().getModelName());
        final Long modelExecutionTime = valueOf(end - start);
        modelResponseInfo.setModelExecutionTime(modelExecutionTime);
        LOGGER.info("Executed model {} successfully. Model Exution Time: {}", modelRequestInfo.getHeaderInfo().getModelName(), + modelExecutionTime);
        return modelResponseInfo;
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(final Converter converter) {
        this.converter = converter;
    }

    public ModelResponseInfo loadModel(Object input) throws BusinessException, SystemException {
        final long start = currentTimeMillis();

        final ModelRequestInfo modelRequestInfo = (ModelRequestInfo) input;

        final HeaderInfo headerInfo = modelRequestInfo.getHeaderInfo();
        LOGGER.info("Received request to load model {}, and it's libraries.",
                modelRequestInfo.getHeaderInfo().getModelPackageName());
        if(rRuntime instanceof RserveRuntime) {
        	((RserveRuntime) rRuntime).clearModeletKeyMap();
        } 
        else {
        	((RRuntime) rRuntime).clearModeletKeyMap();
        } 
        rRuntime.getModel(headerInfo);
        LOGGER.info("Loading of model {} , {} is completed",modelRequestInfo.getHeaderInfo().getModelName(), modelRequestInfo.getHeaderInfo().getVersion());
        final long end = currentTimeMillis();

        final ModelResponseInfo modelResponseInfo = new ModelResponseInfo();

        modelResponseInfo.setPayload(createPayloadForLoadRequest());
        modelResponseInfo.setModelName(modelRequestInfo.getHeaderInfo().getModelName());

        LOGGER.info("Loaded model {} successfully.", modelRequestInfo.getHeaderInfo().getModelName());

        final Long modelLoadTime = valueOf(end - start);
        modelResponseInfo.setModelExecutionTime(modelLoadTime);
        LOGGER.info("Model Load Time:" + modelLoadTime);

        return modelResponseInfo;
    }

    @Override
    public void stopRServeProcess() {
        LOGGER.error("Stopping rserve");
        rRuntime.destroyConnection();
        rRuntime.destroyRuntime();
        LOGGER.error("Stopped rserve successfully");
    }

    public ModelResponseInfo unloadModel(Object input) throws BusinessException, SystemException {
        throw newBusinessException(ME0020, new String[] { "Unload of Model is not supported for R Runtime" });
    }

    public ModelResponseInfo getLoadedModels(Object input) throws BusinessException, SystemException {
        throw newBusinessException(ME0020, new String[] { "Unload of Model is not supported for R Runtime" });
    }

    private Object createPayloadForLoadRequest() {
        final List<FieldInfo> fieldInfos = new ArrayList<>();
        final FieldInfo value = new FieldInfo();
        value.setCollection(false);
        value.setModelParameterName("Load Model Request");
        value.setSequence("1");
        value.setValue("Success");
        fieldInfos.add(value);
        return fieldInfos;
    }

    @Override
    public void startRServeProcess() throws SystemException {
        rRuntime.initializeRuntime();

    }
}