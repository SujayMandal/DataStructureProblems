package com.ca.umg.business.tenant.report.model;

import static com.google.common.base.Objects.toStringHelper;

import com.ca.umg.plugin.commons.excel.reader.ReadHeaderSheet;

public enum TenantModelReportEnum {

	TRANSACTION_ID("Transaction Id", "transactionId", false, null), //
	CREATED_DATE("Created Date", "createdDate", false, null), //
	CLIENT_TRANSACTION_ID("Client Transaction ID", "clientTransactionID", false, null), //
	VERSION_NAME("Model Name", "versionName", false, null), //
	MAJOR_VERSION("Major Version", "majorVersion", false, null), //
	MINOR_VERSION("Minor Version", "minorVersion", false, null), //
	TENANT_INPUT("tenantInput", "tenantInput", true, "Tenant-Input"), //
	TENANT_OUTPUT("tenantOutput", "tenantOutput", true, "Tenant-Output"), //
	MODEL_INPUT("modelInput", "modelInput", true, "Model-Input"), //
	MODEL_OUTPUT("modelOutput", "modelOutput", true, "Model-Output"), //
	INPUT_TABULAR_VIEW("tabularInputOutput", null, true, "Tabular-view"), //
    OUTPUT_TABULAR_VIEW("tabularInputOutput", null, true, "Tabular-view"), //
    STATUS("Status", "status", false, null), //
    RE_RUN("tabularInputOutput", null, true, "Rerun"),
    TRANSACTION_TYPE(ReadHeaderSheet.TRANSACTION_TYPE, "test", false, null), //
    PAYLOAD_STORAGE(ReadHeaderSheet.PAYLOAD_STORAGE, ReadHeaderSheet.PAYLOAD_STORAGE, false, null), //
    MODELET_POOL_CRIERIA(ReadHeaderSheet.ADD_ON_VALIDATION, "modeletPoolCriteria", false, null), //
    USER(ReadHeaderSheet.USER, "createdBy", false, null); //

	private final String reportName;
	private final String column;
	private final boolean reportField;
	private final String reportFilename;

	private TenantModelReportEnum(final String reportName, final String column, final boolean reportField, final String reportFilename) {
		this.reportName = reportName;
		this.column = column;
		this.reportField = reportField;
		this.reportFilename = reportFilename;
	}

	public String getReportName() {
		return reportName;
	}

	public String getColumn() {
		return column;
	}

	public boolean isReportField() {
		return reportField;
	}

	public String getReportFilename() {
		return reportFilename;
	}

	@Override
	public String toString() {
		return toStringHelper(this.getClass()).add("Header", reportName).add("Column", column).add("Report Filename", reportFilename).toString();
	}

	public static TenantModelReportEnum getModelReport(final String reportName) {
		TenantModelReportEnum value = null;
		if (reportName != null) {
			final TenantModelReportEnum[] values = values();
			for (TenantModelReportEnum v : values) {
				if (v.isReportField() && v.getReportName().equalsIgnoreCase(reportName)) {
					value = v;
					break;
				}
			}
		}

		if (value == null) {
			throw new IllegalArgumentException("Report not found, report name may be null or report name may be wrong");
		}

		return value;
	}
}