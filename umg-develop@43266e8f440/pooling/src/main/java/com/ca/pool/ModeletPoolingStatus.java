package com.ca.pool;

public enum ModeletPoolingStatus {

	IN_PROGRESS("Inproress"),
	DONE("Done");
	
	private final String status;
	
	private ModeletPoolingStatus(final String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public static boolean isInprogress(final String status) {
		if (IN_PROGRESS.getStatus().equalsIgnoreCase(status)) {
			return true;
		} else {
			return false;
		}
	}
}
