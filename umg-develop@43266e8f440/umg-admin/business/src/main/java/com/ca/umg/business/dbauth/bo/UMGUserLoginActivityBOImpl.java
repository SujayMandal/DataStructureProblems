package com.ca.umg.business.dbauth.bo;

import static com.ca.umg.business.dbauth.UserLoginActivity.LOGIN_FAILED;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.UserLoginAudit;
import com.ca.umg.business.dbauth.dao.UMGUserLoginActivityDao;

@Named
public class UMGUserLoginActivityBOImpl implements UMGUserLoginActivityBO {

	public static final int LOCK_COUNT_FOR_FAILURE = 3;

	@Inject
	private UMGUserLoginActivityDao dao;

	@Override
	public void logActivity(final UserLoginAudit loginAudit) throws BusinessException {
		dao.insertUserLoginActivity(loginAudit);
	}

	@Override
	public boolean isLoginConsecutivelyFailed(final String username, final String tenantCode) throws BusinessException {
		boolean lockUser = false;
		final List<UserLoginAudit> userActivies = dao.getTop3Activies(username, tenantCode);
		if (userActivies != null && userActivies.size() >= LOCK_COUNT_FOR_FAILURE) {
			lockUser = allThreeActiviesAreFailures(userActivies);
		}
		return lockUser;
	}

	private boolean allThreeActiviesAreFailures(final List<UserLoginAudit> userActivies) {
		boolean allAreFailures = false;
		for (UserLoginAudit activity : userActivies) {
			if (activity.getActivity() == LOGIN_FAILED) {
				allAreFailures = true;
			} else {
				allAreFailures = false;
				break;
			}
		}

		return allAreFailures;
	}

	@Override
	public void lockUser(final String username, final String tenantCode) throws BusinessException {
		dao.lockUser(username, tenantCode);
	}
}