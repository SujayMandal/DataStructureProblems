package com.ca.umg.sdc.rest.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.delegate.SwitchTenantDelegate;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/switchTenant")
public class SwitchTenantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchTenantController.class);
    
    @Inject
    private SwitchTenantDelegate switchTenantDelegate;
    
    @RequestMapping(value = "/switch/{tenantCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse switchTenant(@PathVariable("tenantCode") String tenantCode, HttpServletRequest request) {
        RestResponse response = new RestResponse();
        try {
        	switchTenantDelegate.switchAndSetTenant(tenantCode, request);
            response.setError(false);
        } catch (SystemException ex) {
            LOGGER.error("Error occured while switchinhg tenant",ex);
            response.setError(true);
            response.setErrorCode(ex.getCode());
            response.setMessage(ex.getLocalizedMessage());
        }
        return response;
    }
}
