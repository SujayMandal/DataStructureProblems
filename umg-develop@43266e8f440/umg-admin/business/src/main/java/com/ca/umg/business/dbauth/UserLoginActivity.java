package com.ca.umg.business.dbauth;


public enum UserLoginActivity {

	LOGIN_SUCCESS("Login Success"), //
	LOGIN_FAILED("Login Failed"), //
	LOGOUT_SUCESSS("Logout Success"), //
	PASSWORD_CHANGE_SUCESSS("Password Change Success"), //
	PASSWORD_CHANGE_FAILED("Password Change Failed");

	private final String activity;

	private UserLoginActivity(final String activity) {
		this.activity = activity;
	}

	public String getActivity() {
		return activity;
	}

	public static UserLoginActivity getUserLoginActivity(final String activityValue) {
		UserLoginActivity matchedActivity = null;
		for (UserLoginActivity a : values()) {
			if (a.getActivity().equals(activityValue)) {
				matchedActivity = a;
			}
		}

		return matchedActivity;
	}
}