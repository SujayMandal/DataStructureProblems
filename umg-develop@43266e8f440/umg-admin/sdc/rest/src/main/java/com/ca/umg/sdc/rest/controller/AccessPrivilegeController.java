package com.ca.umg.sdc.rest.controller;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.delegate.AccessPrivilegeDelegate;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/accessPrivilege")
public class AccessPrivilegeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchTenantController.class);
    
    @Inject
    private AccessPrivilegeDelegate accessPrivilegeDelegate;
    
    @RequestMapping(value = "/getRolesPrivilegesMap/{tenantCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse getRolesPrivilegesMap(@PathVariable("tenantCode") String tenantCode,HttpServletRequest request) throws BusinessException, SystemException {
        RestResponse response = new RestResponse();
        try {
        	response.setResponse(accessPrivilegeDelegate.getRolesPrivilegesMap(tenantCode));
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
        } catch (SystemException ex) {
            LOGGER.error("Error occured while switchinhg tenant",ex);
            response.setError(true);
            response.setErrorCode(ex.getCode());
            response.setMessage(ex.getLocalizedMessage());
        }
        return response;
    }
    
    @RequestMapping(value = { "/setRolesPrivilegesMap" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public RestResponse setRolesPrivilegesMap(@RequestParam(value = "tenantCode") String tenantCode , @RequestParam(value = "role") String role , @RequestParam(value = "privilegeList") List<String> privilegeList) throws BusinessException, SystemException, SQLException {
        RestResponse response = new RestResponse();
        try {
        	accessPrivilegeDelegate.setRolesPrivilegesMap(tenantCode,role,privilegeList);
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
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
