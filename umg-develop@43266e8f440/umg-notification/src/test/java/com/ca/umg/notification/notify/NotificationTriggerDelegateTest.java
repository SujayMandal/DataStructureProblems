package com.ca.umg.notification.notify;

import static org.junit.Assert.*;

import java.util.Map;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

public class NotificationTriggerDelegateTest {
	@Inject
	private NotificationTriggerBO bo;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test(expected = NullPointerException.class)
	public void notifyModelPublishSuccessTest() throws SystemException,BusinessException {
		final Map<String, String> versionInfoMap = null;
		bo.notifyModelPublishSuccess(versionInfoMap, false);
	}

	@Ignore
	@Test(expected = NullPointerException.class)
	public void modelAndSystemFailureMailTest() throws SystemException,BusinessException {
		final Map<String, String> versionInfoMap = null;
		//bo.notifyRuntimeFailure(versionInfoMap, "", "", false);
	}

	@Test(expected = NullPointerException.class)
	public void sendModelApprovalEmailTest() throws SystemException,BusinessException {
		final Map<String, String> versionInfoMap = null;
		bo.sendModelApprovalEmail(versionInfoMap, null, false);
	}

}
