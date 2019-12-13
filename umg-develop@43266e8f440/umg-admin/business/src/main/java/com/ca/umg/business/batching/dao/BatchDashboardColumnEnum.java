package com.ca.umg.business.batching.dao;

import static java.util.Locale.getDefault;


public enum BatchDashboardColumnEnum {

	ID("ID", "ID", true), //
	BATCH_ID("Batch ID", "BATCH_ID", true), //
	BATCH_INPUT_FILE("Batch Input File", "BATCH_INPUT_FILE", true), //
	BATCH_OUTPUT_FILE("Batch Output File", "BATCH_OUTPUT_FILE", true), //
	IS_TEST("IS TEST", "IS_TEST", true), //
	STATUS("Status", "STATUS", true), //
	TOTAL_RECORDS("Total Records", "TOTAL_RECORDS", true), //
	SUCCESS_COUNT("Success Count", "SUCCESS_COUNT", true), //
	FAILED_COUNT("Failed Count", "FAIL_COUNT", true), //
	START_TIME("Start Time", "START_TIME", true), //
	END_TIME("End Time", "END_TIME", true), //
	CREATED_ON("Created On", "CREATED_ON", true), //
	TENANT_ID("Tenant Id", "TENANT_ID", true), //
	BATCH_EXEC_TIME("Batch Exec Time", "BATCH_EXEC_TIME", false), //
	TRANSACTION_MODE("Transaction Mode", "TRANSACTION_MODE", true),
	EXECUTION_ENVIRONMENT("Execution Environment", "EXECUTION_ENVIRONMENT", true), //
	MODELLING_ENVIRONMENT("Modelling Environment", "MODELLING_ENVIRONMENT", true), //
	NOT_PICKED_COUNT("Not Picked Count", "NOT_PICKED_COUNT", true); //
	
	private final String headerName;
	private final String dbColumnName;
	private final boolean dbField;
	
	private BatchDashboardColumnEnum(final String headerName, final String dbColumnName, final boolean dbField) {
		this.headerName = headerName;
		this.dbColumnName = dbColumnName;
		this.dbField = dbField;
	}

	public static BatchDashboardColumnEnum valueOfByHeaderName(final String headerName) {
		BatchDashboardColumnEnum columnEnum = null;
		final BatchDashboardColumnEnum values[] = values();
		for (BatchDashboardColumnEnum value : values) {
			if (value.getHeaderName().toLowerCase(getDefault()).equals(headerName.toLowerCase(getDefault()))) {
				columnEnum = value;
				break;
			}
		}

		return columnEnum;
	}
	
	public String getHeaderName() {
		return headerName;
	}

	public String getDbColumnName() {
		return dbColumnName;
	}

	public boolean isDbField() {
		return dbField;
	}
}