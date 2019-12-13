package com.ca.umg.business.dbauth;

public enum UMGUserStatus {

	FIRST_TIME_LOGIN(0), //
	ACTIVE(1), //
	DEACTIVE(2), //
	LOCKED(3), //
	LOGICALLY_DELETED(4), //
	UNKNOWN(-1);

	private final int code;

	private UMGUserStatus(final int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static UMGUserStatus getUMGUserStatus(final int code) {
		UMGUserStatus status;
		switch (code) {
			case 0:
				status = FIRST_TIME_LOGIN;
				break;
			case 1:
				status = ACTIVE;
				break;
			case 2:
				status = DEACTIVE;
				break;
			case 3:
				status = LOCKED;
				break;
			case 4:
				status = LOGICALLY_DELETED;
				break;
			default:
				status = UNKNOWN;
				break;
		}

		return status;
	}
}