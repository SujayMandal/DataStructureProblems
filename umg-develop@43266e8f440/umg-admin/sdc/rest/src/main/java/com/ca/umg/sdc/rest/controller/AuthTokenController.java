package com.ca.umg.sdc.rest.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.AuthTokenInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.tenant.delegate.AuthTokenDelegate;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * @author basanaga
 *
 */
@Controller
@RequestMapping("/authToken")
public class AuthTokenController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenController.class);
    @Inject
    private AuthTokenDelegate  authTokenDelegate;
    
    /**
     * This method used to get the all authtokens for the particular tenant
     * @param tenantId
     * @return
     */
    @RequestMapping(value = "/listAll/{tenantId}", method = GET)
    @ResponseBody
    public RestResponse<List<AuthTokenInfo>> listAll(@PathVariable String tenantId )  {
        RestResponse<List<AuthTokenInfo>> response = new RestResponse<List<AuthTokenInfo>>();
        List<AuthTokenInfo> tenantInfos = null;
        Boolean adminAware= AdminUtil.getActualAdminAware();
        try {        	
            AdminUtil.setAdminAwareTrue();
            tenantInfos = authTokenDelegate.listAll(tenantId);
            response.setError(false);
            response.setMessage("Done");
            response.setResponse(tenantInfos);
        } catch (SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        } catch (Exception e) {// NOPMD
            LOGGER.error(e.getMessage(), e);         
            response.setError(true);
            response.setMessage(e.getMessage());
        }
        finally {
	            RequestContext.getRequestContext().setAdminAware(adminAware);
	     }
        return response;
    }   
    /**
     * This method used to create authtoken
     * @param tenantId
     * @return
     */
    @RequestMapping(value = "/createAuthToken/{tenantId}", method = GET)
    @ResponseBody
    public RestResponse<List<AuthTokenInfo>> createAuthToken(@PathVariable String tenantId) {
        RestResponse<List<AuthTokenInfo>> response = new RestResponse<List<AuthTokenInfo>>();
        List<AuthTokenInfo> tenantInfos = null;
        Boolean adminAware= AdminUtil.getActualAdminAware();
        try {
            AdminUtil.setAdminAwareTrue();
            tenantInfos = authTokenDelegate.createNewAuthToken(tenantId);
            response.setError(false);
            response.setMessage("Done");
            response.setResponse(tenantInfos);
        } catch (SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }catch (Exception e) {// NOPMD
            LOGGER.error(e.getMessage(), e);         
            response.setError(true);
            response.setMessage(e.getMessage());
        }
        finally {
            RequestContext.getRequestContext().setAdminAware(adminAware);
        }
        return response;
    }
    
    /**
     * This method used to activate authtoken
     * @param tenantId
     * @param authTokenId
     * @param comment
     * @return
     */
    @RequestMapping(value = "/activateAuthToken/{tenantId}/{authTokenId}/{comment}", method = GET)
    @ResponseBody
    public RestResponse<List<AuthTokenInfo>> activateAuthToken(@PathVariable String tenantId, @PathVariable String authTokenId,
            @PathVariable String comment) {
        RestResponse<List<AuthTokenInfo>> response = new RestResponse<List<AuthTokenInfo>>();
        List<AuthTokenInfo> tenantInfos = null;
        Boolean adminAware= AdminUtil.getActualAdminAware();
        try {
            AdminUtil.setAdminAwareTrue();
            tenantInfos = (List<AuthTokenInfo>) authTokenDelegate.activateAuthToken(tenantId, authTokenId, comment);
            response.setError(false);
            response.setMessage("Done");
            response.setResponse(tenantInfos);

        } catch (SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }catch (Exception e) {// NOPMD
            LOGGER.error(e.getMessage(), e);         
            response.setError(true);
            response.setMessage(e.getMessage());
        }
        finally {
            RequestContext.getRequestContext().setAdminAware(adminAware);
        }
        return response;
    }



}
