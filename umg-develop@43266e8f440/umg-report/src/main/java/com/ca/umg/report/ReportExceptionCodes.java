package com.ca.umg.report;

public enum ReportExceptionCodes {

	REPORT_STORE_ERROR("RE0000001", "Error happened while storing generated report into SAN path"), //
	REPORT_GENERATE_ERROR("RE0000002", "Error happened while generating report"), //
	REPORT_COMPILE_ERROR("RE0000003", "Error happened while compiling report template"), //
	REPORT_SAVE_ERROR("RE0000004", "Error happened while saving report generation status into database"), //
	REPORT_DOWNLOAD_ERROR("RE0000005", "Error happened while downloading report"), //
	REPORT_GENERATE_URL_ERROR("RE0000006", "Error happened while generating URL for report"), //
	REPORT_DOWNLOAD_URL_ERROR("RE0000007", "Error happened while validating Download Report URL"), //
	REPORT_TEMPLATE_NOT_AVL_ERROR("RE0000008", "Report Template is not avaiable"),
	REPORT_GENERATION_FAILED("RE0000009", "Error happend while generating report"), //
	REPORT_VALIDATION_FAILED("RE0000010", "Error happened while generating report"), //
	REPORT_JSON_STR_CONVERTION("RE0000011", "Error happened while converting transaction document to json string"),
	REPORT_DATA_SRC_CREATION_ERROR("RE0000012", "Error happened while creating data source"), //
	REPORT_TRAN_DATA_NOT_AVAILABLE("RE0000013", "Transaction data is not avilable");

	private final String errorCode;
	private final String errorDescription;
	
	private ReportExceptionCodes(final String errorCode, final String errorDescription) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}
	
	public static boolean isReportTemplateNotAvlbCode(final String errorCode) {
		boolean flag = false;
		if (errorCode.equals(REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorCode())) {
			return true;
		} 
		
		return flag;
	}
}