/*
 * RConverter.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * 
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.converter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rosuda.REngine.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.converter.Converter;
import com.ca.umg.modelet.lang.type.DataType;
import com.ca.umg.modelet.r.type.AbstractRType;

/**
 * This class marshals incoming JSON to R Data Type and unmarshals the returned R Data Types to FieldInfo objects
 **/

public class RConverter implements Converter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RConverter.class);
    
    @Override
    public Object marshall(final ModelRequestInfo modelRequestInfo) throws BusinessException {
        LOGGER.info("Started model request conversion for model {}.", modelRequestInfo.getHeaderInfo().getModelLibraryName());

        final List<FieldInfo> payLoad = modelRequestInfo.getPayload();
        List<DataType> inputList = new ArrayList<DataType>();

        final boolean stringsAsFactors = getStringsAsFactorsValue(modelRequestInfo);

        if (payLoad != null) {
            for (FieldInfo objInput : payLoad) {
                if (objInput == null || objInput.getValue() == null) {
                    inputList.add(null);
                } else {
                    DataType dt = AbstractRType.createRDataTypeFromObject(objInput, stringsAsFactors);
                    inputList.add(dt);
                }
            }

            LOGGER.info("Model request conversion completed for model {}.",
                    modelRequestInfo.getHeaderInfo().getModelLibraryName());
        }

        return inputList;
    }

    @Override
    public Object unmarshall(final Object response) throws BusinessException {
        LOGGER.info("Response received for unmarshalling {}.", response);
        Object output;
        	output = response;
        List<FieldInfo> fieldInfos = new ArrayList<>();
        FieldInfo value = null;
        if (output != null) {
        	if(output instanceof REXP) {
        		value = AbstractRType.createRDataTypeFromREXP((REXP) output).toUmgType(null, "1");
        	} else {
        		value = AbstractRType.createRDataTypeFromREXP((org.rosuda.JRI.REXP) output).toUmgType(null, "1");
        	}
            fieldInfos.add(value);
            // TODO:MS - look at return type in XSD and do any conversions here
            // (or throw an exception if you cannot convert)
        } else {
            throw new BusinessException(ErrorCodes.ME0819, new Object [] {});
        }
        return fieldInfos;
    }

    private boolean getStringsAsFactorsValue(final ModelRequestInfo modelRequestInfo) {
        return modelRequestInfo.getHeaderInfo().isStringsAsFactors();
    }

    @Override
    public Object unmarshall(final Object response, final boolean reduceModelSize) throws BusinessException {
        LOGGER.info("Response received for unmarshalling {}.", response);
        final List<Map<String, Object>> newModelRequestBody = new ArrayList<Map<String, Object>>();
        Map<String, Object> value = null;

        if (response != null) {
        	if(response instanceof REXP) {
        		value = AbstractRType.createRDataTypeFromREXP((REXP) response).toUmgType1(null, "1");
        	} else {
        		value = AbstractRType.createRDataTypeFromREXP((org.rosuda.JRI.REXP) response).toUmgType1(null, "1");
        	}
        }
        newModelRequestBody.add(value);
        return newModelRequestBody;
    }

}