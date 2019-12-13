package com.ca.umg.business.tenant.report.usage;

import com.ca.umg.business.common.info.PagingInfo;

@SuppressWarnings("PMD")
public class UsageReportPageInfo extends PagingInfo {

	private static final long serialVersionUID = 1L;

	private boolean resetDatesAtUI;

	private String startDate;

	private String endDate;

	public boolean isResetDatesAtUI() {
		return resetDatesAtUI;
	}

	public void setResetDatesAtUI(final boolean resetDatesAtUI) {
		this.resetDatesAtUI = resetDatesAtUI;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}
}