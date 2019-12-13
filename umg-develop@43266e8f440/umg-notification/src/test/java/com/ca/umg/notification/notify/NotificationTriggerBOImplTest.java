package com.ca.umg.notification.notify;

import static org.junit.Assert.*;

import java.util.Map;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.NotificationService;
import com.ca.umg.notification.dao.NotificationDao;

public class NotificationTriggerBOImplTest {
	/*@Inject
	private NotificationService notificationService;

	@Inject
	private CacheRegistry cacheRegistry;
	
	@Inject
	private NotificationDao mysqlDao;
	*/
	
	@Test(expected = NullPointerException.class)
	public void notifyModelPublishSuccessTest() throws SystemException, BusinessException {
		
		final Map<String, String> versionInfoMap = null;
		NotificationTriggerBO bo = new NotificationTriggerBOImpl();
		bo.notifyModelPublishSuccess(versionInfoMap, false);
		
		
	}
	
	@Ignore
	@Test(expected = NullPointerException.class)
	public void notifyRuntimeFailureTest() throws SystemException, BusinessException {
		
		final Map<String, String> versionInfoMap = null;
		NotificationTriggerBO bo = new NotificationTriggerBOImpl();
		//bo.notifyRuntimeFailure(versionInfoMap, "", "", false);
		
		
	}
	
	@Test(expected = NullPointerException.class)
	public void sendModelApprovalEmailTest() throws SystemException, BusinessException {
		
		final Map<String, String> versionInfoMap = null;
		NotificationTriggerBO bo = new NotificationTriggerBOImpl();
		bo.sendModelApprovalEmail(versionInfoMap, null, false);
		
		
	}
	
	@Test
	
	public void createdModelApprovalURLTest(){
		
	}

}
