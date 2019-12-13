package com.ca.umg.business.dbauth.bo;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static com.ca.umg.business.dbauth.util.PasswordUtil.isNewPasswordSameAsCurrentPassword;
import static com.ca.umg.business.dbauth.util.PasswordUtil.isPasswordMatchedWithEncoded;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.ChangePasswordDetail;
import com.ca.umg.business.dbauth.UMGUser;
import com.ca.umg.business.dbauth.dao.UMGUserDao;

@SuppressWarnings("PMD")
@Named
public class UMGUserBOImpl implements UMGUserBO {

	public static final String USER_NOT_FOUND = "Please review your user name and password. In case of further issues, please contact system administrator";
	public static final String CURRENT_PASSWORD_IS_VALID = "Incorrect current password";
	public static final String NEW_PASS_SAME_AS_CURRENT_PASSWORD = "New password is same as current password";
	public static final String PASSWORD_CHANGE_SUCCESS = "Password changed successfully";

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

	@Inject
	private UMGUserDao dao;

	@Override
	public UMGUser findUser(final String username, final String tenantCode) throws BusinessException {
		getRequestContext().setAdminAware(true);
		try {
			return dao.findUser(username, tenantCode);
		} finally {
			getRequestContext().setAdminAware(false);
		}
	}

	@Override
	public String changePassword(final ChangePasswordDetail changePasswordDetail) throws BusinessException {
		final UMGUser user = findUser(changePasswordDetail.getUserName(), changePasswordDetail.getTenantCode());

		String status;
		if (!isUserFound(user)) {
			status = USER_NOT_FOUND;
		} else if (!isCurrentPasswordValid(changePasswordDetail.getCurrentPassword(), user)) {
			status = CURRENT_PASSWORD_IS_VALID;
		} else if (isNewPasswordSameAsCurrentPassword(changePasswordDetail.getNewPassword(), user.getPassword(), encoder)) {
			status = NEW_PASS_SAME_AS_CURRENT_PASSWORD;
		} else {
			try {
				getRequestContext().setAdminAware(true);
				dao.updatePassword(changePasswordDetail.getUserName(), changePasswordDetail.getTenantCode(),
						encoder.encode(changePasswordDetail.getNewPassword()));
				status = PASSWORD_CHANGE_SUCCESS;
			} catch (BusinessException be) {
				throw be;
			} finally {
				getRequestContext().setAdminAware(false);
			}
		}

		return status;
	}

	private boolean isUserFound(final UMGUser user) {
		boolean userFound = false;
		if (user != null) {
			userFound = true;
		}

		return userFound;
	}

	private boolean isCurrentPasswordValid(final String currentPassword, final UMGUser user) {
		return isPasswordMatchedWithEncoded(currentPassword, user.getPassword(), encoder);
	}
}