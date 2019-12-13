package com.fa.dp.business.util;

/**
 * @author yogeshku
 * DPFileProcess status enum
 *
 */
public enum DPFileProcessStatus {
	
	UPLOADED("UPLOADED"),
	IN_PROGRESS("IN-PROGRESS"),
	SUCCESSFUL("SUCCESSFUL"),
	PARTIAL("PARTIAL"),
	FAILED("FAILED"),
	ERROR("ERROR"),
	DATA_LOAD("DATA_LOAD");
	
	/**
	 * status of the file
	 */
	private final String fileStatus;

	/**
	 * @param fileStatus
	 * set the constant value
	 */
	DPFileProcessStatus(String fileStatus) {
      this.fileStatus = fileStatus;
    }

	public String getFileStatus() {
		return fileStatus;
	}
 
}
