package com.ca.umg.business.dbauth.util;

import static com.ca.umg.business.dbauth.UMGUserStatus.ACTIVE;
import static com.ca.umg.business.dbauth.UMGUserStatus.DEACTIVE;
import static com.ca.umg.business.dbauth.UMGUserStatus.LOCKED;
import static com.ca.umg.business.dbauth.UMGUserStatus.LOGICALLY_DELETED;
import static com.ca.umg.business.dbauth.UMGUserStatus.getUMGUserStatus;

import com.ca.umg.business.dbauth.UMGUser;

public final class UMGUserUtil {

	private UMGUserUtil() {

	}

	public static boolean isUserActive(final UMGUser user) {
		return user != null && getUMGUserStatus(user.getEnabled()) == ACTIVE;
	}

	public static boolean isUserDeactive(final UMGUser user) {
		return user != null && getUMGUserStatus(user.getEnabled()) == DEACTIVE;
	}

	public static boolean isUserLocked(final UMGUser user) {
		return user != null && getUMGUserStatus(user.getEnabled()) == LOCKED;
	}

	public static boolean isUserLogicallyDeleted(final UMGUser user) {
		return user != null && getUMGUserStatus(user.getEnabled()) == LOGICALLY_DELETED;
	}
}