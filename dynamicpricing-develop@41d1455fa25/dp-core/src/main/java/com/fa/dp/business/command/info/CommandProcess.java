/**
 *
 */
package com.fa.dp.business.command.info;

/**
 * @author mandasuj
 */
public enum CommandProcess {

	WEEK0("WEEK0"),
	WEEKN("WEEKN"),
	WEEK0_OCN("WEEK0_OCN"),
	WEEK0_PHH("WEEK0_PHH"),
	WEEK0_NRZ("WEEK0_NRZ"),
	WEEKN_OCN("WEEKN_OCN"),
	WEEKN_NRZ("WEEKN_NRZ"),
	WEEKN_PHH("WEEKN_PHH"),
	QA_REPORT("QA_REPORT"),
	SOP_QA_REPORT("SOP_QA_REPORT"),
	SOP_WEEK0_PHH("SOP_WEEK0_PHH"),
	SOP_WEEK0_NRZ("SOP_WEEK0_NRZ"),
	SOP_WEEK0_OCN("SOP_WEEK0_OCN"),
	SOP_WEEKN_PHH("SOP_WEEKN_PHH"),
	SOP_WEEKN_NRZ("SOP_WEEKN_NRZ"),
	SOP_WEEKN_OCN("SOP_WEEKN_OCN"),
	SOP_WEEKN("SOP_WEEKN");

	private String commmandProcess;

	private CommandProcess(String commmandProcess) {
		this.commmandProcess = commmandProcess;
	}

	public String getCommmandProcess() {
		return commmandProcess;
	}

	public void setCommmandProcess(String commmandProcess) {
		this.commmandProcess = commmandProcess;
	}
}
