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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;

/**
 * 
 **/
public class ContentEnrichTransformer extends AbstractTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentEnrichTransformer.class);

    /**
     * Enrich message by converting date field present in the request message header. Validate date and covert to various formats
     * for further processing. If request header does not contain date field current {@link DateTime} is used.
     *
     * @param message
     *            {@link Message}
     *
     * @return {@link Message}
     *
     * @throws BusinessException
     *             Any error occur here is considered to be an error in request header date field.
     **/
    @SuppressWarnings("unchecked")
    @Override
    protected Object doTransform(final Message<?> message) throws BusinessException {
        long startTime = System.currentTimeMillis();// NOPMD
        Map<String, Object> payload = (Map<String, Object>) message.getPayload();
        Map<String, Map<String, Object>> tenantRequest = (Map<String, Map<String, Object>>) payload
                .get(MessageVariables.TENANT_REQUEST);
        payload.put("tenantRequestHeader", tenantRequest.get(MessageVariables.HEADER));
        Object date = tenantRequest.get(MessageVariables.HEADER).get(MessageVariables.DATE);
        DateTime dateTime = null;
        LOGGER.debug("Model request date validation starated for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));

        if (date == null) {
            dateTime = new DateTime(DateTimeZone.UTC);
        } else if (date instanceof DateTime) {
            dateTime = (DateTime) date;
        } else if (date instanceof String) {
            if (StringUtils.isEmpty((String) date)) {
                dateTime = new DateTime(DateTimeZone.UTC);
            } else {
                try {
                    DateTimeFormatter format = ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC();
                    dateTime = format.parseDateTime((String) date);
                } catch (UnsupportedOperationException | IllegalArgumentException e) // NOPMD
                {
                    LOGGER.error("An error occurred while converting request date", e);
                    throw new BusinessException(RuntimeExceptionCode.RVE000701, new Object[] { e.getMessage() });// NOPMD
                }
            }
        } else {
            throw new BusinessException(RuntimeExceptionCode.RVE000701, new Object[] {});
        }

        Map<String, Object> request = (Map<String, Object>) payload.get(MessageVariables.REQUEST);
        //need to remove this log
        LOGGER.error("dateTime is======"+dateTime);
        String dt = DateTimeFormat.forPattern("MMM-dd-yyyy HH:mm").print(dateTime);
        request.put(MessageVariables.TESTDATE_MILLIS, dateTime.getMillis());
        request.put(MessageVariables.TESTDATE, dt);
        LOGGER.debug("Model request date validation finished for message with id {}",
                message.getHeaders().get(MessageVariables.MESSAGE_ID));
        Object obj = MessageBuilder.withPayload(message.getPayload()).copyHeaders(message.getHeaders()).setHeaderIfAbsent(
                MessageVariables.DATE_USED, ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC().print(dateTime)).build();
        LOGGER.debug("ContentEnrichTransformer.doTransform : " + (System.currentTimeMillis() - startTime));
        return obj;
    }
}
