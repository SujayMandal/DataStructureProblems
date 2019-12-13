package com.ca.umg.sdc.rest.controller;

import static com.ca.framework.core.constants.FrameworkConstant.TENANT_URL_MAP;
import static com.ca.umg.business.version.delegate.VersionDelegate.SUCCESS_MESSAGE;
import static com.ca.umg.business.version.entity.EmailApprovalEnum.EMAIL_APPROVAL;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MAJOR_VERSION;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MINOR_VERSION;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MODEL_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.PUBLISHER_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TRANSACTION_ID;
import static com.ca.umg.notification.util.NotificationUtil.getEncryptedIdForTenantMatch;
import static com.ca.umg.notification.util.NotificationUtil.getEncryptedIdFromURL;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.encryption.EncryptionUtil;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.TenantData;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.accessprivilege.delegate.SwitchTenantDelegate;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.info.ModelViewInfo;
import com.ca.umg.business.version.info.SwitchTenantUrlInfo;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionStatus;
import com.ca.umg.notification.NotificationConstants;
import com.ca.umg.notification.notify.NotificationTriggerDelegate;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.hazelcast.core.IMap;

@Controller
@RequestMapping("/model")
public class RAModelApprovalController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RAModelApprovalController.class);

	@Inject
	private VersionDelegate versionDelegate;

	@Inject
	private TenantDelegate tenantDelegate;

	@Inject
	private NotificationTriggerDelegate notificationDelegate; 

	@Inject
	private CacheRegistry cacheRegistry;
	
	@Inject
    private SwitchTenantDelegate switchTenantDelegate;	
	
	@RequestMapping(value = { "/checkTenancy/**" }, method = RequestMethod.GET)
	@ResponseBody
	public SwitchTenantUrlInfo tenantMatch(HttpServletRequest request, HttpServletResponse servletresponse){
		LOGGER.info("Called approval model rest API from URL");
		SwitchTenantUrlInfo switchTenantUrlInfo = new SwitchTenantUrlInfo();
		LOGGER.info("URL is : {}", ((HttpServletRequest)request).getRequestURL().toString());
		final String id = getEncryptedIdForTenantMatch(((HttpServletRequest)request).getRequestURL().toString());
		final String[] decryptedIdAndEmail = EncryptionUtil.decryptToken(id).split(NotificationConstants.ID_FIELD_SEPERATOR);
		switchTenantUrlInfo.setCurrentTenantCode(RequestContext.getRequestContext().getTenantCode());
		switchTenantUrlInfo.setSwitchToTenantCode(decryptedIdAndEmail[2]);
		if(!decryptedIdAndEmail[2].equalsIgnoreCase(RequestContext.getRequestContext().getTenantCode())){
			switchTenantUrlInfo.setTenantMismatchFlag(true);
		}
		else
		{
			switchTenantUrlInfo.setTenantMismatchFlag(false);
		}
		return switchTenantUrlInfo;
	}

	@RequestMapping(value = { "/approval/**" }, method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<ModelViewInfo> approvedPublishVersion(HttpServletRequest request, HttpServletResponse servletresponse) {
		LOGGER.info("Called approval model rest API from URL");
		final RestResponse<ModelViewInfo> response = new RestResponse<ModelViewInfo>();
		Boolean switchTenantFlag = false;
		String switchTenantInfo = "";
		VersionInfo  versionMessageInfo = null;
		ModelViewInfo modelViewInfo = new ModelViewInfo();
		String message = VersionDelegate.FAILED_MESSAGE;
		try {

			/*LOGGER.info("URL is : {}", request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString());*/
			LOGGER.info("URL is : {}", ((HttpServletRequest)request).getRequestURL().toString());
			/*final String id = getEncryptedIdFromURL(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString());*/
			final String id = getEncryptedIdFromURL(((HttpServletRequest)request).getRequestURL().toString());
			LOGGER.info("Encripted Id : {}", id);
			final String[] decryptedIdAndEmail = EncryptionUtil.decryptToken(id).split(NotificationConstants.ID_FIELD_SEPERATOR);
			//final  String user = decryptedIdAndEmail[0];
			String user = SecurityContextHolder.getContext().getAuthentication().getName();
			LOGGER.info("Approval User is : {}", user);
			
			final String versionId = decryptedIdAndEmail[1];
			LOGGER.info("Version Id is : {}", versionId);
			LOGGER.info("Tenant Code is : {}", decryptedIdAndEmail[2]);
			
			//call some func and set a flag for single user multi tenant case
			if(!decryptedIdAndEmail[2].equalsIgnoreCase(RequestContext.getRequestContext().getTenantCode())) {
				switchTenantInfo = switchTenantResponse(decryptedIdAndEmail[2], request);
				if(switchTenantInfo.equals(VersionDelegate.UNAUTHORISED_SWITCH)){
					switchTenantFlag = false;
					LOGGER.info("Not authorised to publish model for this tenant.");
					response.setError(true);
					response.setMessage(VersionDelegate.UNAUTHORISED_APPROVAL);
				}
				else if(switchTenantInfo.equals(VersionDelegate.TENANT_SWITCH_SQL_EXCEPTION)){
					switchTenantFlag = false;
					LOGGER.info("sql exception in switching tenant.");
					response.setError(true);
					response.setMessage(VersionDelegate.TENANT_SWITCH_EXCEPTION_MSG);
				}
				else{
					switchTenantFlag = true;
				}
				modelViewInfo.setSwitchTenantFlag(switchTenantFlag);
				response.setResponse(modelViewInfo);
			} else {
					versionMessageInfo = versionDelegate.findOneversion(versionId);
					buildGeneralNotificationInfo(versionMessageInfo,decryptedIdAndEmail[2] , modelViewInfo);

					if (versionMessageInfo.getStatus().equalsIgnoreCase(VersionStatus.PUBLISHED.getVersionStatus())) {
						buildAlReadyPublishedNotificationInfo(versionMessageInfo, VersionDelegate.VERSION_ALREADY_PUBLISHED, modelViewInfo);
					} else {
						final String tenantUrl = VersionControllerHelper.getTenantBaseUrl(tenantDelegate);                       
                        final String authToken =VersionControllerHelper.getTenantAuthToken(cacheRegistry);
						final VersionInfo versionInfo = versionDelegate.publishVersion(versionId, user, tenantUrl, authToken, EMAIL_APPROVAL.getValue());
						final Map<String, String> versionInfoMap = new HashMap<>();
						versionInfoMap.put(MODEL_NAME.getHeaderName(), versionInfo.getName());
						versionInfoMap.put(MINOR_VERSION.getHeaderName(), String.valueOf(versionInfo.getMinorVersion()));
						versionInfoMap.put(MAJOR_VERSION.getHeaderName(), String.valueOf(versionInfo.getMajorVersion()));
						versionInfoMap.put(TRANSACTION_ID.getHeaderName(), versionInfo.getUmgTransactionId());            
						versionInfoMap.put(PUBLISHER_NAME.getHeaderName(), user);

						notificationDelegate.notifyModelPublishSuccess(versionInfoMap, false);

						message = SUCCESS_MESSAGE;
						buildSuccessNotificationInfo(versionInfo, message,user, modelViewInfo);
					}	
					response.setError(false);
				
				response.setResponse(modelViewInfo);			
			}
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
			response.setResponse(modelViewInfo);
			//notificationMessageInfo.setNotificationStatus(message);
		}
		return response;
	}

	private void buildSuccessNotificationInfo(final VersionInfo versionInfo ,final String message,final String user,final ModelViewInfo notificationMessage) {
		notificationMessage.setStatus(VersionStatus.PUBLISHED.getVersionStatus());
		notificationMessage.setPublishedBy(user);
		notificationMessage.setPublishedOn(versionInfo.getPublishedOn());
		notificationMessage.setPublishedDateTime(AdminUtil.getDateFormatMillisForEst(notificationMessage.getPublishedOn().getMillis(), null));
		notificationMessage.setNotificationStatus(message);
		notificationMessage.setResponseSuccessFailureMsg(false);
	}

	private void buildGeneralNotificationInfo(final VersionInfo versionMessageInfo,final String tenantCode ,final ModelViewInfo notificationMessage) {
		notificationMessage.setName(versionMessageInfo.getName());
		notificationMessage.setMajorVersion(versionMessageInfo.getMajorVersion());
		notificationMessage.setMinorVersion(versionMessageInfo.getMinorVersion());
		notificationMessage.setCreatedOn(versionMessageInfo.getCreatedDate());
		notificationMessage.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(versionMessageInfo.getCreatedDate().getMillis(), null));
		notificationMessage.setCreatedBy(versionMessageInfo.getCreatedBy());
		notificationMessage.setTenantCode(tenantCode);
		notificationMessage.setTenantName(getTenantName(tenantCode));
		notificationMessage.setStatus(versionMessageInfo.getStatus());
		notificationMessage.setNotificationStatus(VersionDelegate.FAILED_MESSAGE);
		notificationMessage.setDescription(versionMessageInfo.getDescription());
		notificationMessage.setResponseSuccessFailureMsg(true);

	}
	private void buildAlReadyPublishedNotificationInfo(final VersionInfo versionMessageInfo,final String message ,final ModelViewInfo notificationMessage) {
		notificationMessage.setNotificationStatus(message);
		notificationMessage.setPublishedBy(versionMessageInfo.getPublishedBy());
		notificationMessage.setPublishedOn(versionMessageInfo.getPublishedOn());
		notificationMessage.setPublishedDateTime(AdminUtil.getDateFormatMillisForEst(versionMessageInfo.getPublishedOn().getMillis(), null));
		notificationMessage.setResponseSuccessFailureMsg(false);
	}

	private String getTenantName(final String tenantCode) {
		final IMap<String, TenantData> map = cacheRegistry.getMap(TENANT_URL_MAP);
		return map.get(tenantCode) != null ? map.get(tenantCode).getTenantName() : tenantCode;
	}
	
	private String switchTenantResponse(String tenantCode, HttpServletRequest request) {
		String switchFlag = "";
		try {
			switchTenantDelegate.switchAndSetTenant(tenantCode, request);
            switchFlag = VersionDelegate.SWITCH_TENANT;
        }catch(UsernameNotFoundException ex){
        	LOGGER.error("Error occured while switchinhg tenant due authorisation failure: {}",ex);
        	switchFlag = VersionDelegate.UNAUTHORISED_SWITCH;
        }
		catch (Exception ex) { //NOPMD
            LOGGER.error("Error occured while switching tenant due to sql exception : {}",ex);
            switchFlag = VersionDelegate.TENANT_SWITCH_SQL_EXCEPTION;
        }
		return switchFlag;
	}
}