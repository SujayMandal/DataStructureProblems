package com.ca.umg.business.dbauth.util;

import static com.ca.umg.business.dbauth.UMGUserStatus.ACTIVE;
import static com.ca.umg.business.dbauth.UMGUserStatus.DEACTIVE;
import static com.ca.umg.business.dbauth.UMGUserStatus.LOCKED;
import static com.ca.umg.business.dbauth.UMGUserStatus.LOGICALLY_DELETED;
import static com.ca.umg.business.dbauth.UMGUserStatus.UNKNOWN;
import static com.ca.umg.business.dbauth.util.UMGUserUtil.isUserActive;
import static com.ca.umg.business.dbauth.util.UMGUserUtil.isUserDeactive;
import static com.ca.umg.business.dbauth.util.UMGUserUtil.isUserLocked;
import static com.ca.umg.business.dbauth.util.UMGUserUtil.isUserLogicallyDeleted;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.ca.umg.business.dbauth.UMGUser;

public class UMGUserUtilTest {

	private UMGUser user;

	@Before
	public void setup() {
		user = mock(UMGUser.class);
	}

	@Test
	public void testIsUserActive() {
		when(user.getEnabled()).thenReturn(ACTIVE.getCode());
		assertTrue(isUserActive(user));
	}

	@Test
	public void testIsUserDeactive() {
		when(user.getEnabled()).thenReturn(DEACTIVE.getCode());
		assertTrue(isUserDeactive(user));
	}

	@Test
	public void testIsUserLocked() {
		when(user.getEnabled()).thenReturn(LOCKED.getCode());
		assertTrue(isUserLocked(user));
	}

	@Test
	public void testIsUserLogicallyDeleted() {
		when(user.getEnabled()).thenReturn(LOGICALLY_DELETED.getCode());
		assertTrue(isUserLogicallyDeleted(user));
	}

	@Test
	public void testIsUserUnknoe() {
		when(user.getEnabled()).thenReturn(UNKNOWN.getCode());
		assertFalse(isUserActive(user));
		assertFalse(isUserDeactive(user));
		assertFalse(isUserLocked(user));
		assertFalse(isUserLogicallyDeleted(user));
	}

}
