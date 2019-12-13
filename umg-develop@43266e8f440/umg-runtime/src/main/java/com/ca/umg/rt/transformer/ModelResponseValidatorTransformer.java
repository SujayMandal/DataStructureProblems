/*
 * MappingTransformer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.transformer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;
import com.codahale.metrics.annotation.Timed;

/**
 * Maps MID to TID
 **/
public class ModelResponseValidatorTransformer extends AbstractTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelResponseValidatorTransformer.class);

    /**
     * Does transformation of data from model call result to tenant format
     *
     * @param message
     *            {@link Message}
     *
     * @return {@link Message}
     *
     * @throws SystemException
     **/
    @SuppressWarnings("unchecked")
    @Override
    @Timed
    protected Object doTransform(final Message<?> message) throws SystemException {
        LOGGER.debug("ME2 response data valiation starated for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));
        long startTime = System.currentTimeMillis();//NOPMD
        Map<String, Object> payload = (Map<String, Object>) message.getPayload();
        Boolean success = (Boolean) payload.get(MessageVariables.SUCCESS);
        if (!success) {
            throw new SystemException(RuntimeExceptionCode.RSE000809, new Object[] { payload.get(MessageVariables.ERROR_CODE),
                    payload.get(MessageVariables.ME2_ERROR_MESSAGE) });
        }
        LOGGER.debug("ME2 response data valiation completed for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));
        LOGGER.debug("ModelResponseValidatorTransformer.doTransform : " + (System.currentTimeMillis() - startTime));
        return message;
    }    
}
