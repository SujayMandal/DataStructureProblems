package com.ca.umg.modelet.runtime.impl;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.umg.modelet.constants.ErrorCodes.ME0020;
import static java.lang.Long.valueOf;
import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.MatlabModel;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.converter.Converter;
import com.ca.umg.modelet.converter.impl.MatlabConverter;
import com.ca.umg.modelet.runtime.ModeletRuntime;
import com.ca.umg.modelet.runtime.RuntimeProcess;
import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWCellArray;
import com.mathworks.toolbox.javabuilder.MWCharArray;
import com.mathworks.toolbox.javabuilder.MWStructArray;

public class MatlabRuntimeProcess implements RuntimeProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatlabRuntimeProcess.class);

    private Converter converter;

    private ModeletRuntime matlabRuntime;

    public MatlabRuntimeProcess(final ModeletRuntime matlabRuntime) {
        this.matlabRuntime = matlabRuntime;
        converter = new MatlabConverter();
    }

    @SuppressWarnings("rawtypes")
    public ModelResponseInfo execute(final Object input, SystemInfo systemInfo) throws BusinessException, SystemException {
        final List<Object> outputList = new ArrayList<Object>();
        List inputList = null;
        try {
            final ModelRequestInfo modelRequestInfo = (ModelRequestInfo) input;
            HeaderInfo headerInfo = modelRequestInfo.getHeaderInfo();
            LOGGER.info("Received request to execute model {}.", modelRequestInfo.getHeaderInfo().getModelLibraryVersionName());
            buildOutput(outputList, modelRequestInfo.getHeaderInfo().getResponseSize());
            // final MatlabModel model = (MatlabModel) matlabRuntime.getModel(modelRequestInfo.getHeaderInfo().getModelName());
            LOGGER.info("fetch instance of model initiated");
            final MatlabModel model = (MatlabModel) matlabRuntime.getModel(headerInfo);
            LOGGER.info("fetch instance of model successful");
            LOGGER.info("execution of instance initiated");
            inputList = (List) converter.marshall(modelRequestInfo);
            final long start = currentTimeMillis();
            model.executeModel(outputList, inputList);
            final long end = currentTimeMillis();
            LOGGER.info("execution of instance successful");
            final ModelResponseInfo modelResponseInfo = new ModelResponseInfo();
            LOGGER.info("initiated unmarshall of message");
            final Object respPayload = converter.unmarshall(outputList);
            LOGGER.info("unmarshall of message successful");
            modelResponseInfo.setPayload(respPayload);
            modelResponseInfo.setModelName(modelRequestInfo.getHeaderInfo().getModelName());
            final Long modelExecutionTime = valueOf(end - start);
            modelResponseInfo.setModelExecutionTime(modelExecutionTime);
            LOGGER.info("Model Exution Time:" + modelExecutionTime);
            LOGGER.info("Executed model {} successfully.", modelRequestInfo.getHeaderInfo().getModelName());
            return modelResponseInfo;
        } finally {

            if (inputList != null) {
                releaseMemory(inputList);
            }
            if (outputList != null) {
                releaseMemory(outputList);
            }
        }
    }

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    public void releaseMemory(@SuppressWarnings("rawtypes") List list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                if (list.get(i) instanceof MWStructArray) {
                    MWStructArray.disposeArray((MWStructArray) list.get(i));
                    ((MWStructArray) list.get(i)).dispose();
                } else if (list.get(i) instanceof MWCellArray) {
                    MWStructArray.disposeArray((MWCellArray) list.get(i));
                    ((MWCellArray) list.get(i)).dispose();
                } else if (list.get(i) instanceof MWCharArray) {
                    MWStructArray.disposeArray((MWCharArray) list.get(i));
                    ((MWCharArray) list.get(i)).dispose();
                } else if (list.get(i) instanceof MWArray) {
                    MWStructArray.disposeArray((MWArray) list.get(i));
                    ((MWArray) list.get(i)).dispose();
                }
            }
        }
        list = null; // NOPMD
    }

    public void buildOutput(final List<Object> outputList, final int responseSize) {
        for (int i = 0; i < responseSize; i++) {
            outputList.add(new Object());
        }
    }

    public void setConverter(final Converter converter) {
        this.converter = converter;
    }

    public void setMatlabRuntime(final ModeletRuntime matlabRuntime) {
        this.matlabRuntime = matlabRuntime;
    }

    public ModelResponseInfo loadModel(Object input) throws BusinessException, SystemException {
        throw newBusinessException(ME0020, new String[] { "Load of Model is not supported for Matlab Runtime" });
    }

    public ModelResponseInfo unloadModel(Object input) throws BusinessException, SystemException {
        throw newBusinessException(ME0020, new String[] { "Unload of Model is not supported for Matlab Runtime" });
    }

    public ModelResponseInfo getLoadedModels(Object input) throws BusinessException, SystemException {
        throw newBusinessException(ME0020, new String[] { "Getting Loaded Models is not supported for Matlab Runtime" });
    }

    @Override
    public void stopRServeProcess() {
        LOGGER.error("Nothing to destroy here !!!!!!!!!!");

    }

    @Override
    public void startRServeProcess() throws SystemException {
        // TODO Auto-generated method stub

    }
}
