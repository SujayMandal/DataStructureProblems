
package com.ca.umg.modelet.common;

import java.io.IOException;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ExceptionResolver {
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    private ExceptionResolver() {}

    public static void resolveException(final String jsonResponse) throws SystemException, BusinessException {
        if (jsonResponse.contains("exceptionType")) {
            ExceptionResponse exceptionResponse = null;
            try {
                exceptionResponse = objectMapper.readValue(jsonResponse, ExceptionResponse.class);
            } catch (JsonParseException | JsonMappingException e) {
                SystemException.newSystemException("", new String[]{""}, e);
            } catch (IOException ioe) {
                SystemException.newSystemException("", new String[]{""}, ioe);
            }
            switch (exceptionResponse.getExceptionType()) {
            case "SystemException":
                SystemException.newSystemException(exceptionResponse.getErrorCode(), exceptionResponse.getArguments());
                break;
            case "BusinessException":
                BusinessException.raiseBusinessException(exceptionResponse.getErrorCode(), exceptionResponse.getArguments());
                break;
           default:
                break;
            }
        }

    }

}
