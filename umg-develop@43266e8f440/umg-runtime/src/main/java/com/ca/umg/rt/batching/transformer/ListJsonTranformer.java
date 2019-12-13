/**
 * 
 */
package com.ca.umg.rt.batching.transformer;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

/**
 * @author chandrsa
 * 
 */
public class ListJsonTranformer extends AbstractTransformer {

    private static final String ERROR = "_Error.";
    private static final Logger LOGGER = LoggerFactory.getLogger(ListJsonTranformer.class);
    private UmgFileProxy umgFileProxy;
    private SystemParameterProvider systemParameterProvider;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.integration.transformer.AbstractTransformer#doTransform(org.springframework.integration.Message)
     */
    @Override
    protected Object doTransform(Message<?> message) throws SystemException {
        Message<?> transformedMessage = null;
        String fileName = null;
        try {
            MessageHeaders messageHeaders = message.getHeaders();
            fileName = (String) messageHeaders.get(FileHeaders.FILENAME);

            if ((Boolean) messageHeaders.get(RuntimeConstants.ERROR)){
                 StringBuffer responsefileName = new StringBuffer(FilenameUtils.getBaseName(fileName)).append(ERROR)
                         .append(FilenameUtils.getExtension(fileName));
                transformedMessage = getMessageBuilder(message).setHeader(FileHeaders.FILENAME,
                        responsefileName.toString())
                         .build();
            } else {
                transformedMessage = getMessageBuilder(message).build();
            }
         
        } catch (Exception exception) {// NOPMD
            LOGGER.error(exception.getMessage(), exception);
            SystemException.newSystemException("CODE", new Object[] {}, exception);
    }
        return transformedMessage;
    }

    private MessageBuilder<?> getMessageBuilder(Message<?> message) throws SystemException {
        return MessageBuilder.withPayload(((KeyValuePair<Object, ?>) message.getPayload()).getValue())
                .copyHeaders(message.getHeaders())
                .setHeader(RuntimeConstants.SAN_PATH,
                        umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)));
    }

    public UmgFileProxy getUmgFileProxy() {
        return umgFileProxy;
    }

    public void setUmgFileProxy(UmgFileProxy umgFileProxy) {
        this.umgFileProxy = umgFileProxy;
    }

    public SystemParameterProvider getSystemParameterProvider() {
        return systemParameterProvider;
    }

    public void setSystemParameterProvider(SystemParameterProvider systemParameterProvider) {
        this.systemParameterProvider = systemParameterProvider;
    }

}
