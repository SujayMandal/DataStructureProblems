package com.ca.umg.notification.notify;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationHeaders;

import java.util.List;
import java.util.Map;

public interface NotificationTriggerBO {

	public void notifyModelPublishSuccess(final Map<String, String> versionInfoMap, final boolean async) throws SystemException, BusinessException;
	
	public void notifyRuntimeFailure(final Map<String,String> versionInfoMap, final String errorCode, final String errorMessage, final boolean async, List<Map<String, Object>> modeletClientInfo) throws SystemException, BusinessException;

	public void sendModelApprovalEmail(final Map<String, String> versionInfoMap, final List<NotificationAttachment> attachments, final boolean async) throws SystemException, BusinessException;
	
	public void notifyNewTenantAdded(final Map<String, String> versionInfoMap, final boolean async) throws SystemException, BusinessException;

	public void nofitySendAuthToken(final Map<String, String> versionInfoMap, final boolean async) throws SystemException, BusinessException;

    public void notifyModeletRestart(final Map<String, String> modeletClientInfoMap, Map<String, String> info, boolean async) throws BusinessException, SystemException;
    
    public void nofityExcessModelExecTime(final NotificationHeaders headers, final boolean async) throws SystemException, BusinessException;
 
    public void notifyModeletStatusfinal(Map<String, Object> authTokenInfoMap,boolean async) throws SystemException, BusinessException;

}	
