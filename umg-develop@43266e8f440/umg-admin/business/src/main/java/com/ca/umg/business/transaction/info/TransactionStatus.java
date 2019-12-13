package com.ca.umg.business.transaction.info;

import static java.util.Locale.getDefault;

@SuppressWarnings("PMD")
public enum TransactionStatus {

	SUCCESS("success", "Success"),
	ERROR("error", "Failure"),
	FAILED("failed", "Failure"),
	FAILURE("failure", "Failure"),
	OTHER("any other status", "Failure"),
	ALL("All", "All");

	private final String status;
	private final String reportStatus;

	private TransactionStatus(final String status, final String reportStatus) {
		this.status = status;
		this.reportStatus = reportStatus;
	}

	public static TransactionStatus valuOf(final String status) {
		TransactionStatus ts = OTHER;
		if (status != null) {
			for (TransactionStatus transactionStatus : values()) {
				if (transactionStatus.getStatus().equals(status.toLowerCase(getDefault()))) {
					ts = transactionStatus;
					break;
				}
			}
		}

		return ts;
	}

	public String getStatus() {
		return status;
	}

	public String getReportStatus() {
		return reportStatus;
	}
}