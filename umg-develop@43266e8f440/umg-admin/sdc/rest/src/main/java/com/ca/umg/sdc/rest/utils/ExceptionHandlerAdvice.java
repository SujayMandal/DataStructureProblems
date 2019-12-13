package com.ca.umg.sdc.rest.utils;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public RestResponse<Object> handleException(Throwable throwable) {
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setError(true);
        restResponse.setErrorCode("RUNTIME UNDEFINED");
        restResponse.setMessage(throwable.getLocalizedMessage());
        return restResponse;
    }

}
