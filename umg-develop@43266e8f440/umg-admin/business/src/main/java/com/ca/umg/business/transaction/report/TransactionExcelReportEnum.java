package com.ca.umg.business.transaction.report;

public enum TransactionExcelReportEnum {

	TENANT_TRANSACTION_ID(0, "Tenant Transaction ID", "CLIENT_TRANSACTION_ID", true, true, 25 * 256),
	TENANT_ID(1, "Tenant ID", "TENANT_ID", true, true, 15 * 256),
	TRANSACTION_TYPE(2, "Transaction Type", null, true, false, 15 * 256),
	BATCH_ID(3, "Batch ID", "BATCH_ID", true, true, 35 * 256),
	MODEL(4, "Model", "VERSION_NAME", true, true, 30 * 256),
	MODEL_VERSION(5, "Model Version", null, true, false, 13 * 256),
	ENVIRONMENT(6, "Environment", "EXECUTION_LANGUAGE", true, true, 13 * 256),
	RUN_DATE_TIME(7, "Date Time", "RUN_AS_OF_DATE", true, true, 20 * 256),
	PROCESSING_STATUS(8, "Processing Status", "STATUS", true, true, 16 * 256),
	REASON(9, "Reason", "STATUS", true, true, 16 * 256),
	PROCESSING_TIME(10, "Processing Time", null, true, false, 15 * 256),
	UMG_TRANSACTION_ID(11, "UMG Transaction ID", "ID", true, true, 35 * 256),
	MAJOR_VERSION(-1, "Major Version", "MAJOR_VERSION", false, true, 1),
	MINOR_VERSION(-1, "Minor Version", "MINOR_VERSION", false, true, 1),
	RUNTIME_CALL_START(-1, "Runtime Call Start", "RUNTIME_CALL_START", false, true, 1),
	RUNTIME_CALL_END(-1, "Runtime Call End", "RUNTIME_CALL_END", false, true, 1),
	LIBRARY_NAME(-1, "Library Name", "LIBRARY_NAME", false, true, 1),
	IS_TEST(-1, "Is Test", "IS_TEST", false, true, 1),
	ERROR_CODE(-1, "Error Code", "ERROR_CODE", false, true, 1),
	ERROR_DESCRIPTION(-1, "Error Description", "ERROR_DESCRIPTION", false, true, 1);
	
	private final int columnIndex;
	private final String excelHeaderName;
	private final String dbColumnName;
	private final boolean reportField;
	private final int cellWidth;
	private final boolean dbField;
	
	private TransactionExcelReportEnum(final int columnIndex, final String excelHeaderName, final String dbColumnName, final boolean reportField, 
			final boolean dbField, final int cellWidth) {
		this.columnIndex = columnIndex;
		this.excelHeaderName = excelHeaderName;
		this.dbColumnName = dbColumnName;
		this.reportField = reportField;
		this.cellWidth = cellWidth;
		this.dbField = dbField;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public String getExcelHeaderName() {
		return excelHeaderName;
	}

	public String getDbColumnName() {
		return dbColumnName;
	}

	public boolean isReportField() {
		return reportField;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public boolean isDbField() {
		return dbField;
	}
}