package com.ca.umg.notification.notify;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationHeaders;

@Named
public class NotificationTriggerDelegateImpl implements NotificationTriggerDelegate {

	@Inject
	private NotificationTriggerBO bo;
	
	@Override
	public void notifyModelPublishSuccess(final Map<String,String> versionInfoMap, final boolean async) throws SystemException, BusinessException {
		bo.notifyModelPublishSuccess(versionInfoMap, async);
	}
	
	@Override
	public void modelAndSystemFailureMail(final Map<String, String> versionInfoMap, final String errorCode, final String errorMessage, final boolean async, List<Map<String, Object>> modeletClientInfo) throws SystemException, BusinessException {
		bo.notifyRuntimeFailure(versionInfoMap, errorCode, errorMessage, async, modeletClientInfo);
	}
	
	@Override
	public void sendModelApprovalEmail(Map<String, String> versionInfoMap, final List<NotificationAttachment> attachments, final boolean async) throws SystemException, BusinessException {
		bo.sendModelApprovalEmail(versionInfoMap, attachments, async);		
	}

	@Override
	public void notifyNewTenantAdded(Map<String, String> newTenentInfoMap,boolean async) throws SystemException, BusinessException {
		bo.notifyNewTenantAdded(newTenentInfoMap, async);		
	}

	@Override
	public void notifyAuthTokenChange(Map<String, String> authTokenInfoMap,boolean async) throws SystemException, BusinessException {
		bo.nofitySendAuthToken(authTokenInfoMap, async);		
	}
	
	@Override
	public void notifyExcessModelExecTime(NotificationHeaders headers, boolean async)
			throws SystemException, BusinessException {
		bo.nofityExcessModelExecTime(headers, async);
		
	}

	@Override
	public void notifyModeletStatusfinal(Map<String, Object> authTokenInfoMap,boolean async) throws SystemException, BusinessException {
		bo.notifyModeletStatusfinal(authTokenInfoMap, async);
		
	}
}