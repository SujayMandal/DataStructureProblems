package com.ca.umg.business.tenant.report.usage;

import java.util.Locale;

@SuppressWarnings("PMD")
public enum ExecutionReportEnum {
	TENANT_TRANSACTION_ID(0, "Tenant Transaction ID", "CLIENT_TRANSACTION_ID", true, true, 35 * 256),
	TENANT_ID(1, "Tenant ID", "TENANT_ID", true, true, 15 * 256),
	TRANSACTION_MODE(2, "Transaction Mode", "TRANSACTION_MODE", true, true, 15 * 256),
	BATCH_ID(3, "Batch ID", "BATCH_ID", true, true, 35 * 256),
	MODEL(4, "Model", "VERSION_NAME", true, true, 30 * 256),
	MODEL_VERSION(5, "Model Version", null, true, false, 13 * 256),
	CREATED_ON(6, "Date Time", "CREATED_ON", true, true, 20 * 256),
	PROCESSING_STATUS(7, "Processing Status", "STATUS", true, true, 16 * 256),
	REASON(8, "Reason", "ERROR_DESCRIPTION", true, true, 16 * 256),
	PROCESSING_TIME(9,"Model Execution Time", "MODEL_EXECUTION_TIME", true, true, 15 * 256),
	UMG_TRANSACTION_ID(10, "UMG Transaction ID", "ID", true, true, 35 * 256),
	MAJOR_VERSION(-1, "Major Version", "MAJOR_VERSION", false, true, 1),
	MINOR_VERSION(-1, "Minor Version", "MINOR_VERSION", false, true, 1),
	RUNTIME_CALL_START(-1, "Runtime Call Start", "RUNTIME_CALL_START", false, true, 1),
	RUNTIME_CALL_END(-1, "Runtime Call End", "RUNTIME_CALL_END", false, true, 1),
	LIBRARY_NAME(-1, "Library Name", "LIBRARY_NAME", false, true, 1),
    TRANSACTION_TYPE(11, "Transaction Type", "TRANSACTION_TYPE", true, true, 15 * 256),
	IS_TEST(-1, "IS_TEST", "IS_TEST", false, true, 1),
	ERROR_CODE(-1, "Error Code", "ERROR_CODE", false, true, 1),
	ERROR_DESCRIPTION(-1, "Error Description", "ERROR_DESCRIPTION", false, true, 1),
	CPU_USAGE_AT_START(12 , "CPU Usage at start of tran(%)" , "CPU_USAGE_AT_START" , true , true , 15*256),
	CPU_USAGE(13 , "CPU Usage at end of tran(%)" , "CPU_USAGE" , true , true , 15*256),
	FREE_MEMORY_AT_START(14 , "Memory Usage at start of tran(MB)" , "FREE_MEMORY_AT_START" , true , true , 15*256),
	FREE_MEMORY(15 , "Memory Usage at end of tran(MB)" , "FREE_MEMORY" , true , true , 15*256),
	POOL_NAME(16 , "Pool Name" , "POOL_NAME" , true , true , 15*256),
	IP_AND_PORT(17 , "Modelet Server IP: Port Number" , "IP_AND_PORT" , true , true , 15*256),
	SYSTEM_EXE_TIME(18 , "Platform Execution Time" , " " , true , false , 15*256),
	MODELET_WAIT_TIME(19 , "Modelet Waiting time(ms)" , "ME2_WAITING_TIME" , true , true , 15*256),
	NO_OF_ATTEMPTS(20 , "Number of Tries" , "NO_OF_ATTEMPTS" , true , true , 15*256),
	EXECUTION_ENVIRONMENT(21 , "Execution Environment" , "EXECUTION_ENVIRONMENT" , true , true , 15*256),
	MODELLING_ENVIRONMENT(22 , "Modelling Environment" , "MODEL_EXEC_ENV_NAME" , true , true , 15*256);	
    
	private final int columnIndex;
	private final String excelHeaderName;
	private final String dbColumnName;
	private final boolean reportField;
	private final int cellWidth;
	private final boolean dbField;

	private ExecutionReportEnum(final int columnIndex, final String excelHeaderName, final String dbColumnName, final boolean reportField,
			final boolean dbField, final int cellWidth) {
		this.columnIndex = columnIndex;
		this.excelHeaderName = excelHeaderName;
		this.dbColumnName = dbColumnName;
		this.reportField = reportField;
		this.cellWidth = cellWidth;
		this.dbField = dbField;
	}

	public static ExecutionReportEnum valueOfByColumnName(final String dbColumnName) {
		ExecutionReportEnum executionReportEnum = null;
		final ExecutionReportEnum values[] = ExecutionReportEnum.values();
		for (ExecutionReportEnum value : values) {
			if (value.isDbField() && value.getDbColumnName().equals(dbColumnName)) {
				executionReportEnum = value;
				break;
			}
		}

		return executionReportEnum;
	}

	public static ExecutionReportEnum valueOfByHeaderName(final String headerName) {
		ExecutionReportEnum executionReportEnum = null;
		final ExecutionReportEnum values[] = ExecutionReportEnum.values();
		for (ExecutionReportEnum value : values) {
			if (value.getExcelHeaderName().toLowerCase(Locale.ENGLISH).equals(headerName.toLowerCase(Locale.ENGLISH))) {
				executionReportEnum = value;
				break;
			}
		}

		return executionReportEnum;
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
