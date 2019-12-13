package com.ca.umg.business.batching.report.usage;

import java.util.Locale;

@SuppressWarnings("PMD")
public enum BatchUsageReportColumnEnum {

	TENANT_TRANSACTION_ID(0, "Batch ID", "ID", true, true, 35 * 256),
	BATCH_INPUT_FILE(1, "Input File Name", "BATCH_INPUT_FILE", true, true, 35 * 256),
	BATCH_OUTPUT_FILE(2, "Output File Name", "BATCH_OUTPUT_FILE", true, true, 35 * 256),
	TRANSACTION_TYPE(3, "Transaction Type", null, true, false, 30 * 256),
	STATUS(4, "Status", "STATUS", true, true, 20 * 256),
	TOTAL_RECORDS(5, "Total Count", "TOTAL_RECORDS", true, true, 16 * 256),
	SUCCESS_COUNT(6, "Success Count", "SUCCESS_COUNT", true, true, 16 * 256),
	NOT_PICKED_COUNT(7, "Not Picked Count", "NOT_PICKED_COUNT", true, true, 16 * 256),  
	FAIL_COUNT(8, "Failure Count", "FAIL_COUNT", true, true, 15 * 256),
	IN_PROGRESS_COUNT(9, "In-Progress Count", "IN_PROGRESS_COUNT", true, false, 16 * 256),  
	START_TIME(10, "Start Time", "START_TIME", true, true, 35 * 256),
	END_TIME(11, "End Time", "END_TIME", true, true, 13 * 256),
	EXECUTION_TIME(12, "Execution Time", null , true, false, 13 * 256),
	EXECUTION_DATE(13, "RA Execution Date", "START_TIME", true, true, 13 * 256),
	TRANSACTION_MODE(14, "Transaction Mode", "TRANSACTION_MODE", true, true, 13 * 256),
	USER(15, "User", "USER", true, false, 13 * 256),
	MODEL_NAME(16, "Model Name", "MODEL_NAME", true, false, 13 * 256),
	MODEL_VERSION(17, "Model Version", "MODEL_VERSION", true, false, 13 * 256),
	CREATED_ON(-1, "Date Time", "CREATED_ON", false , true, 20 * 256),
	IS_TEST(-1,"is test","IS_TEST",false,true,20*256),
	TENANT_ID(-1,"Tenant Code","TENANT_ID",false,true,20*256);

	private final int columnIndex;
	private final String excelHeaderName;
	private final String dbColumnName;
	private final boolean reportField;
	private final int cellWidth;
	private final boolean dbField;

	private BatchUsageReportColumnEnum(final int columnIndex, final String excelHeaderName, final String dbColumnName, final boolean reportField,
			final boolean dbField, final int cellWidth) {
		this.columnIndex = columnIndex;
		this.excelHeaderName = excelHeaderName;
		this.dbColumnName = dbColumnName;
		this.reportField = reportField;
		this.cellWidth = cellWidth;
		this.dbField = dbField;
	}

	public static BatchUsageReportColumnEnum valueOfByColumnName(final String dbColumnName) {
		BatchUsageReportColumnEnum usageReportColumnEnum = null;
		final BatchUsageReportColumnEnum values[] = BatchUsageReportColumnEnum.values();
		for (BatchUsageReportColumnEnum value : values) {
			if (value.isDbField() && value.getDbColumnName().equals(dbColumnName)) {
				usageReportColumnEnum = value;
				break;
			}
		}

		return usageReportColumnEnum;
	}

	public static BatchUsageReportColumnEnum valueOfByHeaderName(final String headerName) {
		BatchUsageReportColumnEnum usageReportColumnEnum = null;
		final BatchUsageReportColumnEnum values[] = BatchUsageReportColumnEnum.values();
		for (BatchUsageReportColumnEnum value : values) {
			if (value.getExcelHeaderName().toLowerCase(Locale.ENGLISH).equals(headerName.toLowerCase(Locale.ENGLISH))) {
				usageReportColumnEnum = value;
				break;
			}
		}

		return usageReportColumnEnum;
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