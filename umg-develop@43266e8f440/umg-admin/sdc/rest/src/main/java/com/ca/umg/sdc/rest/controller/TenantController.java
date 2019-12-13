/**
 * 
 */
package com.ca.umg.sdc.rest.controller;
import static com.ca.umg.notification.model.NotificationHeaderEnum.ACTIVE_FROM;
import static com.ca.umg.notification.model.NotificationHeaderEnum.ACTIVE_UNTIL;
import static com.ca.umg.notification.model.NotificationHeaderEnum.AUTH_TOKEN;
import static com.ca.umg.notification.model.NotificationHeaderEnum.BATCH_ENABLED;
import static com.ca.umg.notification.model.NotificationHeaderEnum.BULK_ENABLED;
import static com.ca.umg.notification.model.NotificationHeaderEnum.EMAIL_NOTIFICATION_ENABLED;
import static com.ca.umg.notification.model.NotificationHeaderEnum.ModelOutput_Validation;
import static com.ca.umg.notification.model.NotificationHeaderEnum.RESET_BY;
import static com.ca.umg.notification.model.NotificationHeaderEnum.RESET_REASON;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TENANT_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TENANT_ONBOARDED_BY;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TENANT_ONBOARDED_ON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantConfigInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.info.tenant.TenantUtil;
import com.ca.framework.event.StaticDataRefreshEvent;
import com.ca.framework.event.TenantDataRefreshEvent;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.notification.NotificationConstants;
import com.ca.umg.notification.model.NotificationHeaderEnum;
import com.ca.umg.notification.notify.NotificationTriggerDelegate;
import com.ca.umg.notification.util.NotificationUtil;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * @author kamathan
 * 
 */
@Controller
@RequestMapping("/tenant")
public class TenantController {
	
	private static final String TENANT_CODE = "code";

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantController.class);

    @Inject
    private TenantDelegate tenantDelegate;
    
    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private NotificationTriggerDelegate notificationDelegate;   
  

    @RequestMapping(value = "/listAll", method = GET)
    @ResponseBody
    public RestResponse<List<TenantInfo>> listAll() throws IOException {
        RestResponse<List<TenantInfo>> response = new RestResponse<List<TenantInfo>>();
        List<TenantInfo> tenantInfos = null;
        try {
            tenantInfos = tenantDelegate.listAll();
            response.setError(false);
            response.setMessage("Done");
            response.setResponse(tenantInfos);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/create", method = POST)
    @ResponseBody
    public RestResponse<TenantInfo> create(@RequestBody TenantInfo tenantInfo) {
        RestResponse<TenantInfo> response = new RestResponse<TenantInfo>();
        TenantInfo savedTenantInfo = null;
        long count = 0;
        try {
        	if (tenantDelegate.getTenantCountByNameOrCode(tenantInfo.getName(),tenantInfo.getCode()) == count) {
        		// TODO remove hardcoded boolean value
                savedTenantInfo = tenantDelegate.create(tenantInfo, false);
                updateCache (tenantInfo);
                response.setError(false);
                response.setMessage(String.format("Tenant %s saved successfully.", tenantInfo.getName()));
                response.setResponse(savedTenantInfo);
                sendTenantAddedNotification(savedTenantInfo);
        	} else {
        		response.setError(true);
                response.setMessage("Tenant Code or Tenant Name already exists");
        	}
        } catch (SystemException | BusinessException sysExc) {
            LOGGER.error(sysExc.getLocalizedMessage(), sysExc);
            response.setErrorCode(sysExc.getCode());
            response.setError(true);
            response.setMessage(sysExc.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/tenantDetails/{code}", method = GET)
    @ResponseBody
    public RestResponse<TenantInfo> getTenantDetails(@PathVariable(value = TENANT_CODE) String code ) {
        RestResponse<TenantInfo> response = new RestResponse<TenantInfo>();
        try {
            TenantInfo tenantInfo = tenantDelegate.getTenantWithAllSystemKeys(code);
            Set<TenantConfigInfo> tntConf = new HashSet<>();
        	for(TenantConfigInfo info:tenantInfo.getTenantConfigs()) {
        		if(!(info.getSystemKey().getKey().startsWith("FTP")||info.getSystemKey().getKey().startsWith("EXCEL"))) {
        			tntConf.add(info);
        		}
        	}
            response.setError(false);
            tenantInfo.setTenantConfigs(tntConf);
            response.setResponse(tenantInfo);
        } catch (SystemException | BusinessException exe) {
            LOGGER.error(exe.getLocalizedMessage(), exe);
            response.setErrorCode(exe.getCode());
            response.setError(true);
            response.setMessage(exe.getLocalizedMessage());
        }

        return response;
    }
    
    @RequestMapping(value = "/tenantDetails", method = GET)
    @ResponseBody
    public RestResponse<TenantInfo> getTenantDetails() {
        RestResponse<TenantInfo> response = new RestResponse<TenantInfo>();
        try {
            TenantInfo tenantInfo = tenantDelegate.getTenantWithAllSystemKeys();
            response.setError(false);
            response.setResponse(tenantInfo);
        } catch (SystemException | BusinessException exe) {
            LOGGER.error(exe.getLocalizedMessage(), exe);
            response.setErrorCode(exe.getCode());
            response.setError(true);
            response.setMessage(exe.getLocalizedMessage());
        }

        return response;
    }
    
    @RequestMapping(value = "/systemKeys", method = GET)
    @ResponseBody
    public RestResponse<List<SystemKey>> getAllSystemKeys() {
        RestResponse<List<SystemKey>> response = new RestResponse<List<SystemKey>>();
        try {
        	List<SystemKey> systemKeys = tenantDelegate.getAllSystemKeys();
        	SystemKey syskey = null;
        	List<SystemKey> sysyKey = new ArrayList<>();
        	for(SystemKey sys:systemKeys) {
        		if("defaultAutoCommit".equals(sys.getKey())) {
        			syskey = sys;	
        		}
        		else if(!(sys.getKey().startsWith("FTP")||sys.getKey().startsWith("EXCEL"))) {
        		sysyKey.add(sys);
        		}
        	   
        	}
        	sysyKey.add(syskey);
            response.setError(false);
            response.setResponse(sysyKey);
        } catch (SystemException | BusinessException exe) {
            LOGGER.error(exe.getLocalizedMessage(), exe);
            response.setErrorCode(exe.getCode());
            response.setError(true);
            response.setMessage(exe.getLocalizedMessage());
        }

        return response;
    }
    
    @RequestMapping(value = "/update", method = POST, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<TenantInfo> update(@RequestBody TenantInfo tenantInfo) {
        RestResponse<TenantInfo> response = new RestResponse<TenantInfo>();
        TenantInfo updatedTenantInfo = null;
        try {
            TenantInfo existingTenantInfo = tenantDelegate.getTenant(tenantInfo.getCode());
            if (existingTenantInfo != null) {
                updatedTenantInfo = tenantDelegate.update(tenantInfo);

            } else {
                throw BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000094,
                        new Object[] { tenantInfo.getCode() });
            }
            response.setError(false);
            response.setMessage(String.format("Tenant %s updated successfully.", tenantInfo.getCode()));
            response.setResponse(updatedTenantInfo);
        } catch (SystemException | BusinessException exe) {
            LOGGER.error(exe.getLocalizedMessage(), exe);
            response.setErrorCode(exe.getCode());
            response.setError(true);
            response.setMessage(exe.getLocalizedMessage());
        }

        return response;
    }

    @RequestMapping(value = "/batchDeploy/{code}", method = GET)
    public @ResponseBody RestResponse<String> batchDeploy(@PathVariable(value = TENANT_CODE) String code ) {
        RestResponse<String> response = new RestResponse<>();
        try {
            tenantDelegate.deployBatch(code);
            response.setError(false);
            response.setResponse("Batch Deployed Successfully.");
            response.setMessage("Batch Deployment is Successful");
        } catch (SystemException | BusinessException exe) {
            LOGGER.error(exe.getLocalizedMessage(), exe);
            response.setErrorCode(exe.getCode());
            response.setError(true);
            response.setMessage(exe.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/batchUndeploy/{code}", method = GET)
    public @ResponseBody RestResponse<String> undeployBatch(@PathVariable(value = TENANT_CODE) String code ) {
        RestResponse<String> response = new RestResponse<>();
        try {
            tenantDelegate.undeployBatch(code);
            response.setError(false);
            response.setResponse("Batch Undeployed Successfully.");
            response.setMessage("Batch Undeployment is Successful");
        } catch (SystemException | BusinessException exe) {
            LOGGER.error(exe.getLocalizedMessage(), exe);
            response.setErrorCode(exe.getCode());
            response.setError(true);
            response.setMessage(exe.getLocalizedMessage());
        }
        return response;
    }
    
    private void updateCache(TenantInfo tenant) {
    	StaticDataRefreshEvent<Tenant> event = new StaticDataRefreshEvent<Tenant>();
        event.setEvent(StaticDataRefreshEvent.REFRESH_ONLY_TENANT_EVENT);
        cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_ONLY_TENANT_EVENT).publish(event);
        TenantDataRefreshEvent configEvent = new TenantDataRefreshEvent();
        configEvent.setTenantCode(tenant.getCode());
        configEvent.setEvent(TenantDataRefreshEvent.REFRESH_TENANT_CONFIG_EVENT);
        cacheRegistry.getTopic(TenantDataRefreshEvent.REFRESH_TENANT_CONFIG_EVENT).publish(configEvent);
    }

    private void sendTenantAddedNotification(final TenantInfo tenantInfo) throws SystemException, BusinessException {    	
			final Map<String, String> notificationTenantInfoMap = new HashMap<>();		
	
			notificationTenantInfoMap.put(TENANT_NAME.getHeaderName(), tenantInfo.getName());
			notificationTenantInfoMap.put(NotificationHeaderEnum.TENANT_CODE.getHeaderName(), tenantInfo.getCode());
			notificationTenantInfoMap.put(TENANT_ONBOARDED_BY.getHeaderName(), tenantInfo.getCreatedBy());
			notificationTenantInfoMap.put(EMAIL_NOTIFICATION_ENABLED.getHeaderName(),String.valueOf(TenantUtil.isTenantConfigEnabled(tenantInfo,NotificationConstants.EMAIL_NOTIFICATIONS_ENABLED,cacheRegistry)));
			notificationTenantInfoMap.put(BATCH_ENABLED.getHeaderName(), String.valueOf(TenantUtil.isTenantConfigEnabled(tenantInfo,NotificationConstants.BATCH_ENABLED,cacheRegistry)));
			notificationTenantInfoMap.put(BULK_ENABLED.getHeaderName(), String.valueOf(TenantUtil.isTenantConfigEnabled(tenantInfo,NotificationConstants.BULK_ENABLED,cacheRegistry)));
			notificationTenantInfoMap.put(ModelOutput_Validation.getHeaderName(), String.valueOf(TenantUtil.isTenantConfigEnabled(tenantInfo,NotificationConstants.ModelOutput_Validation,cacheRegistry)));
			notificationTenantInfoMap.put(RESET_REASON.getHeaderName(), tenantInfo.getResetReason());
			notificationTenantInfoMap.put(FrameworkConstant.MODELOUTPUT_VALIDATION, String.valueOf(TenantUtil.isTenantConfigEnabled(tenantInfo,FrameworkConstant.MODELOUTPUT_VALIDATION,cacheRegistry)));
			if(tenantInfo.getCreatedDate() != null){
			notificationTenantInfoMap.put(TENANT_ONBOARDED_ON.getHeaderName(), tenantInfo.getCreatedDate().toString());
			}
			notificationTenantInfoMap.put(TENANT_ONBOARDED_BY.getHeaderName(), tenantInfo.getCreatedBy());
			notificationDelegate.notifyNewTenantAdded(notificationTenantInfoMap, false);    	
		

	}

    @RequestMapping(value = "/resendAuth/{code}", method = GET)
    @ResponseBody
    public RestResponse<String> resendAuth(@PathVariable(value = TENANT_CODE) String code ) {
        final RestResponse<String> response = new RestResponse<String>();
		final long activeFrom = System.currentTimeMillis();
		final long activeUntil = NotificationUtil.getOneYearFromDate(activeFrom);

        try {
        	TenantInfo tenantInfo = tenantDelegate.getTenant(code);
        	final Map<String, String> authTokenInfoMap = new HashMap<>();
    		authTokenInfoMap.put(TENANT_NAME.getHeaderName(), tenantInfo.getName());
    		authTokenInfoMap.put(NotificationHeaderEnum.TENANT_CODE.getHeaderName(), tenantInfo.getCode());
            authTokenInfoMap.put(AUTH_TOKEN.getHeaderName(), ((TenantInfo)cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).get(tenantInfo.getCode())).getActiveAuthToken());
    		authTokenInfoMap.put(ACTIVE_FROM.getHeaderName(), String.valueOf(activeFrom));
    		authTokenInfoMap.put(ACTIVE_UNTIL.getHeaderName(), String.valueOf(activeUntil));
    		authTokenInfoMap.put(RESET_REASON.getHeaderName(), tenantInfo.getResetReason());
    		authTokenInfoMap.put(RESET_BY.getHeaderName(), SecurityContextHolder.getContext().getAuthentication().getName());
        	notificationDelegate.notifyAuthTokenChange(authTokenInfoMap, false);
        	response.setMessage("Message Send Success");
        } catch (SystemException | BusinessException sysExc) {
            LOGGER.error(sysExc.getLocalizedMessage(), sysExc);
            response.setErrorCode(sysExc.getCode());
            response.setError(true);
            response.setMessage(sysExc.getLocalizedMessage());
        }
        
        return response;
    }

    
}
