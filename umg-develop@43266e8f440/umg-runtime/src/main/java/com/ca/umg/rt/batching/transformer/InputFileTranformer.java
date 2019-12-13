/**
 * 
 */
package com.ca.umg.rt.batching.transformer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;

/**
 * @author chandrsa
 * 
 */
public class InputFileTranformer extends AbstractTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListJsonTranformer.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.integration.transformer.AbstractTransformer#doTransform(org.springframework.integration.Message)
     */
    @Override
    protected Object doTransform(Message<?> message) throws SystemException {
        KeyValuePair<Object, List<?>> payLoad = (KeyValuePair<Object, List<?>>) message.getPayload();
        Message<?> transformedMessage = null;
        try {
            transformedMessage = MessageBuilder.withPayload(payLoad.getKey()).copyHeaders(message.getHeaders())
                    .build();
        } catch (Exception exception) {// NOPMD
            LOGGER.error(exception.getMessage(), exception);
            SystemException.newSystemException("CODE", new Object[]{}, exception);
        }
        return transformedMessage;
    }


}
