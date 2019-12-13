package com.ca.umg.notification.notify;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationHeaders;

import java.util.List;
import java.util.Map;

public interface NotificationTriggerDelegate {
	
	public void notifyModelPublishSuccess(final Map<String, String> versionInfoMap, final boolean async) throws SystemException, BusinessException;

	public void modelAndSystemFailureMail(final Map<String,String> versionInfoMap, final String errorCode, final String errorMessage, final boolean async, List<Map<String, Object>> modeletClientInfo) throws SystemException, BusinessException;
    
	public void sendModelApprovalEmail(final Map<String, String> versionInfoMap, final List<NotificationAttachment> attachments, final boolean async) throws SystemException, BusinessException;
	
	public void notifyNewTenantAdded(final Map<String, String> newTenentInfoMap, final boolean async) throws SystemException, BusinessException;

	public void notifyAuthTokenChange(final Map<String, String> authTokenInfoMap, final boolean async) throws SystemException, BusinessException;
	
	public void notifyExcessModelExecTime(final NotificationHeaders headers, final boolean async) throws SystemException, BusinessException;
    
	public void notifyModeletStatusfinal (final Map<String, Object> authTokenInfoMap, final boolean async) throws SystemException, BusinessException;


}
