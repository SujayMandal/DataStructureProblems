/**
 * 
 */
package com.ca.umg.modelet.runtime.impl;

import static java.lang.Long.valueOf;
import static java.lang.System.currentTimeMillis;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.ExcelModel;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.converter.Converter;
import com.ca.umg.modelet.converter.impl.ExcelConverter;
import com.ca.umg.modelet.runtime.RuntimeProcess;

/**
 * @author kamathan
 *
 */
public class ExcelRuntimeProcess implements RuntimeProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelRuntimeProcess.class);

    private final Converter converter;

    private final ExcelRuntime excelRuntime;

    public ExcelRuntimeProcess(ExcelRuntime excelRuntime) {
        this.excelRuntime = excelRuntime;
        this.converter = new ExcelConverter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.runtime.RuntimeProcess#execute(java.lang.Object)
     */
    @Override
    public ModelResponseInfo execute(Object input, SystemInfo systemInfo) throws BusinessException, SystemException {
        final ModelRequestInfo excelRequestInfo = (ModelRequestInfo) input;
        HeaderInfo headerInfo = excelRequestInfo.getHeaderInfo();
        // marshal input
        Map<String, FieldInfo> excelModelInput = (Map<String, FieldInfo>) converter.marshall(excelRequestInfo);

        ExcelModel excelModel = excelRuntime.getModel(headerInfo);
        LOGGER.debug("Retrieved Excel model for request execution.");

        List<FieldInfo> excelModelOutputFields = excelRequestInfo.getOutput();
        // execute model
        final long start = currentTimeMillis();
        List<FieldInfo> excelModelResponse = excelModel.executeModel(excelModelInput, excelModelOutputFields);
        final long end = currentTimeMillis();
        LOGGER.debug("Execution model execution completed in {} ms.", end - start);
        // unmarshal the response
        ModelResponseInfo modelResponseInfo = (ModelResponseInfo) converter.unmarshall(excelModelResponse);
        modelResponseInfo.setModelName(headerInfo.getModelName());

        final Long modelExecutionTime = valueOf(end - start);
        modelResponseInfo.setModelExecutionTime(modelExecutionTime);
        LOGGER.info("Model Exution Time:" + modelExecutionTime);

        return modelResponseInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.runtime.RuntimeProcess#loadModel(java.lang.Object)
     */
    @Override
    public ModelResponseInfo loadModel(Object input) throws BusinessException, SystemException {
        throw BusinessException.newBusinessException(ErrorCodes.ME0020,
                new Object[] { "Excel model does not support Load Model." });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.runtime.RuntimeProcess#unloadModel(java.lang.Object)
     */
    @Override
    public ModelResponseInfo unloadModel(Object input) throws BusinessException, SystemException {
        throw BusinessException.newBusinessException(ErrorCodes.ME0020,
                new Object[] { "Excel model does not support Unload Model." });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.runtime.RuntimeProcess#getLoadedModels(java.lang.Object)
     */
    @Override
    public ModelResponseInfo getLoadedModels(Object input) throws BusinessException, SystemException {
        throw BusinessException.newBusinessException(ErrorCodes.ME0020,
                new Object[] { "Excel model does not Get Loaded Models." });
    }

    @Override
    public void stopRServeProcess() {
        LOGGER.error("Nothing to destroy here !!!!!!!!!!!");

    }

    @Override
    public void startRServeProcess() throws SystemException {
        // TODO Auto-generated method stub

    }
}
