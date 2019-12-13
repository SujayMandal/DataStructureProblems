/*
 * PayloadToMapTransformer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.transformer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.MapCopyUtil;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.StopWatchMetrics;

/**
 * Extracts request body in to a {@link Map}
 **/
public class PayloadToMapTransformer extends AbstractMetricTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadToMapTransformer.class);

    /**
     * Converts incoming message to a map. Incoming message is treated as {@link Map<String,Object>}
     *
     * @param message
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws SystemException
     * @throws BusinessException
     **/
    @SuppressWarnings("unchecked")
    @Override
    protected Object doTransform(final Message<?> message) throws SystemException, BusinessException {
        Map<String, Long> metricMap = null;
        StopWatchMetrics stopWatchMetrics = new StopWatchMetrics(allowMetrics());
        stopWatchMetrics.createCheckPointAndStart(this.getClass().getSimpleName() + " transformation time: ");
        long startTime = System.currentTimeMillis();
        LOGGER.debug("Payload to map transformation started for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));
        try {
            Map<String, Object> payloadData = new LinkedHashMap<>();
            Map<String, Object> payload = new LinkedHashMap<String, Object>((Map<String, Object>) message.getPayload());
            payloadData.put(MessageVariables.TENANT_REQUEST, MapCopyUtil.deepCopy(payload));

            Map<String, Object> reqData = new LinkedHashMap<>((Map) payload.get("data"));
            payloadData.put(MessageVariables.REQUEST, MapCopyUtil.deepCopy(reqData));
            LOGGER.debug("Payload to map transformation starated for message with id {}",
                    message.getHeaders().get(MessageVariables.MESSAGE_ID));
            stopWatchMetrics.stopLastCheckPoint();
            MessageBuilder messageBuilder = MessageBuilder.withPayload(payloadData).copyHeaders(message.getHeaders());
            if (allowMetrics()) {
                metricMap = new LinkedHashMap<>();
                metricMap.putAll(stopWatchMetrics.getMetrics());
                messageBuilder.setHeader(MessageVariables.METRICS, metricMap);
            }
            Object obj = messageBuilder.build();
            LOGGER.debug("PayloadToMapTransformer.doTransform : " + (System.currentTimeMillis() - startTime));
            return obj;
        } catch (Exception e) {// NOPMD
            LOGGER.error("ERROR in PayloadToMapTransformer::doTransform", e);
            throw new BusinessException(RuntimeExceptionCode.RVE000700, new Object[] {});// NOPMD
        }
    }

}
