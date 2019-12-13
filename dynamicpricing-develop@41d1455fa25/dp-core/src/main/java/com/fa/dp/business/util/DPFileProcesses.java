package com.fa.dp.business.util;

public enum DPFileProcesses {
	
	VACANT_WEEK0("Vacant Week 0"),
	VACANT_WEEKN("Vacant Week N");
	
	private final String process;

	/**
	 * @param process
	 */
	private DPFileProcesses(String process) {
		this.process = process;
	}

	/**
	 * @return the process
	 */
	public String getProcess() {
		return process;
	}

}
