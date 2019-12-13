/**
 * 
 */
package com.ca.umg.rt.transformer;

import org.springframework.integration.Message;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.exception.SystemException;

/**
 * transformer to pass the message variable as is
 * 
 */
public class BulkFileTransformer extends AbstractTransformer {

    /*
     * transformer to pass the message variable as is
     */
    @Override
    protected Object doTransform(Message<?> message) throws SystemException {
        return message;
    }
}
