package com.ca.umg.business.tenant.report.usage.bo;

import static com.ca.umg.business.constants.BusinessConstants.UMG_EST_DATE_FORMAT;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.CREATED_ON;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.values;
import static java.lang.Long.valueOf;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.tenant.report.usage.ExecutionReportEnum;
import com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum;
import com.ca.umg.business.tenant.report.usage.util.UsageReportUtil1;
import com.ca.umg.business.transaction.info.TransactionStatus;
import com.ca.umg.business.transaction.mongo.info.TransactionDocumentInfo;

@SuppressWarnings("PMD")
public class UsageExcelReport1 {

	private static final Logger LOGGER = getLogger(UsageExcelReport1.class);
	
	public static final int HEADER_ROW_NUM = 0;

	public static final String SHEET_NAME = "Sheet";

	public static final int SHEET_MAX_ROWS = 65536;

	public static final String NO_RECORDS_FOUND = "No Records Found";

	public static final int NO_RECORDS_FOUND_COLUMN_INDEX = 0;

	public static final int ACTIVE_SHEET_INDEX = 0;

	public static final String REPORT_FILE_FORMAT = "%s_%s_%s%s";
	
	public static final String REPORT_FILE_FORMAT_NO_ENDDATE = "%s_%s%s";

	public static final String REPORT_EXT = ".xls";

	public static final Long MINUS_ONE = valueOf(-1l);

	//private final SqlRowSet sqlRowSet;
	private final List<TransactionDocumentInfo> transactionDocumentInfos;
	private int currentRow;
	private Sheet sheet;
	private Workbook wb;
	private int sheetIndex = 1;
	private Long minCreatedDate = MINUS_ONE;
	private Long maxCreatedDate = MINUS_ONE;

	public UsageExcelReport1(final List<TransactionDocumentInfo> transactionDocumentInfos) {
		this.transactionDocumentInfos = transactionDocumentInfos;
	}

	@PreAuthorize("hasRole(@accessPrivilege.getDashboardTransactionDownloadExcelUsageReport())")
	public void createReport(final OutputStream outputStream) throws IOException, SystemException {
		wb = createWorkBook();
		sheet = createSheet();
		setWorkbookProperties();
		createSheetHeaders();
		populateSheetData(createDateCellStyle());
		wb.write(outputStream);
		LOGGER.info("Report is created");
	}
	
	public void createExeReport(final OutputStream outputStream) throws IOException, SystemException {
		wb = createWorkBook();
		sheet = createSheet();
		setWorkbookProperties();
		createExeSheetHeaders();
		populateExeSheetData(createDateCellStyle());
		wb.write(outputStream);
		LOGGER.info("Report is created");
	}
	
	public Workbook createReport() throws IOException, SystemException {
		wb = createWorkBook();
		sheet = createSheet();
		setWorkbookProperties();
		createSheetHeaders();
		populateSheetData(createDateCellStyle());
		return wb;
	}

	private CellStyle createDateCellStyle() {
		final CreationHelper createHelper = wb.getCreationHelper();
		final CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(UMG_EST_DATE_FORMAT));
		return cellStyle;
	}

	private void setWorkbookProperties() {
		wb.setActiveSheet(ACTIVE_SHEET_INDEX);
	}

	private Workbook createWorkBook() {
		return new HSSFWorkbook();
	}

	private Sheet createSheet() {
		return wb.createSheet(getSheetName());
	}

	private void createSheetHeaders() {
		final Row headerRow = sheet.createRow(HEADER_ROW_NUM);
		final UsageReportColumnEnum values[] = values();

		for (UsageReportColumnEnum value : values) {
			if (value.isReportField()) {
				createHeader(headerRow, value);
			}
		}
		
		LOGGER.info("Header sheet is created");
		currentRow = HEADER_ROW_NUM;
	}
	
	private void createExeSheetHeaders() {
		final Row headerRow = sheet.createRow(HEADER_ROW_NUM);
		final ExecutionReportEnum values[] = ExecutionReportEnum.values();

		for (ExecutionReportEnum value : values) {
			if (value.isReportField()) {
				createExeHeader(headerRow, value);
			}
		}
		
		LOGGER.info("Header sheet is created");
		currentRow = HEADER_ROW_NUM;
	}

	private void createHeader(final Row headerRow, final UsageReportColumnEnum reportField) {
		final int index = reportField.getColumnIndex();
		final String headerName = reportField.getExcelHeaderName();
		final int width = reportField.getCellWidth();
		final Cell cell = headerRow.createCell(index);
		cell.setCellValue(headerName);
		sheet.setColumnWidth(index, width);
	}
	private void createExeHeader(final Row headerRow, final ExecutionReportEnum reportField) {
		final int index = reportField.getColumnIndex();
		final String headerName = reportField.getExcelHeaderName();
		final int width = reportField.getCellWidth();
		final Cell cell = headerRow.createCell(index);
		cell.setCellValue(headerName);
		sheet.setColumnWidth(index, width);
	}
	private void populateSheetData(final CellStyle cellStyle) throws SystemException {
		boolean noRecordsFound = true;

		if (transactionDocumentInfos != null) {
			Row dataRow;
			/*while (sqlRowSet.next()) {
				noRecordsFound = false;
				dataRow = createDataRow();
				populateRowData(dataRow, cellStyle);
			}*/
			
			for (TransactionDocumentInfo documentInfo :transactionDocumentInfos) {
				noRecordsFound = false;
				dataRow = createDataRow();
				populateRowData(dataRow, cellStyle, documentInfo);
			}
		}

		if (noRecordsFound) {
			setNoRecordsFoundCell();
		}
		
		LOGGER.info("ALl rows are populated");
	}

	private void populateExeSheetData(final CellStyle cellStyle) throws SystemException {
		boolean noRecordsFound = true;

		if (transactionDocumentInfos != null) {
			Row dataRow;
			/*while (sqlRowSet.next()) {
				noRecordsFound = false;
				dataRow = createDataRow();
				populateRowData(dataRow, cellStyle);
			}*/
			
			for (TransactionDocumentInfo documentInfo :transactionDocumentInfos) {
				noRecordsFound = false;
				dataRow = createExeDataRow();
				populateExeRowData(dataRow, cellStyle, documentInfo);
			}
		}

		if (noRecordsFound) {
			setNoRecordsFoundCell();
		}
		
		LOGGER.info("ALl rows are populated");
	}
	private void setNoRecordsFoundCell() {
		final Row dataRow = createDataRow();
		final Cell cell = dataRow.createCell(NO_RECORDS_FOUND_COLUMN_INDEX, Cell.CELL_TYPE_STRING);
		setCellValue(cell, NO_RECORDS_FOUND);
	}

	private void populateRowData(final Row dataRow, final CellStyle cellStyle, TransactionDocumentInfo documentInfo) throws SystemException {
		final UsageReportColumnEnum cellFields[] = values();
		for (UsageReportColumnEnum cellField : cellFields) {
			if (cellField.isReportField()) {
				final Cell cell = createCell(dataRow, cellField, cellStyle);
				final String cellValue = getCellValue(cellField, documentInfo);
				setCellValue(cell, cellValue);
			}
		}
	}
	private void populateExeRowData(final Row dataRow, final CellStyle cellStyle, TransactionDocumentInfo documentInfo) throws SystemException {
		final ExecutionReportEnum cellFields[] = ExecutionReportEnum.values();
		for (ExecutionReportEnum cellField : cellFields) {
			if (cellField.isReportField()) {
				final Cell cell = createExeCell(dataRow, cellField, cellStyle);
				final String cellValue = getExeCellValue(cellField, documentInfo);
				setCellValue(cell, cellValue);
			}
		}
	}
	private String getCellValue(final UsageReportColumnEnum cellField, TransactionDocumentInfo documentInfo) throws SystemException {
		String cellValue = null;
		int min = 0;
		
		switch (cellField) {
			case PROCESSING_TIME:
				try {
					//cellValue = UsageReportUtil1.getProcessingTime(documentInfo.getModelCallEnd() == null ? 0 : (documentInfo.getModelCallEnd()-documentInfo.getModelCallStart()));
					cellValue = String.valueOf(documentInfo.getModelExecutionTime()) + "ms";
				} catch (Exception e) {
					LOGGER.error("Error occured while getting cell value with umg-tran Id : "+documentInfo.getTransactionId()+" and error : ",e);
					SystemException.newSystemException(BusinessExceptionCodes.BSE000996, new Object[] {});
				}
				break;

			case MODEL_VERSION:
				cellValue = UsageReportUtil1.getModelVersion(documentInfo);
				break;

			case CREATED_ON:
				cellValue = UsageReportUtil1.getFormattedRunAsOfDate(documentInfo);
				final Long createdDate = documentInfo.getCreatedDate();

				if (maxCreatedDate.compareTo(createdDate) < min) {
					maxCreatedDate = createdDate;
				}

				if (minCreatedDate.equals(MINUS_ONE) || minCreatedDate.compareTo(createdDate) > min) {
					minCreatedDate = createdDate;
				}

				break;

			case PROCESSING_STATUS:
				cellValue = UsageReportUtil1.getProcessingStatus(documentInfo);
				break;
			case TENANT_TRANSACTION_ID:
				cellValue = documentInfo.getClientTransactionID();
				break;
			case MODEL:
				cellValue = documentInfo.getVersionName();
				break;
			case REASON:
				if (StringUtils.isNotBlank(documentInfo.getErrorCode()) || ! (UsageReportUtil1.getProcessingStatus(documentInfo).equalsIgnoreCase(TransactionStatus.SUCCESS.getStatus()))) {
					if(documentInfo.getErrorDescription() != null){
						cellValue = documentInfo.getErrorDescription()+ "- (" + documentInfo.getErrorCode() + ")";
					} else {
						cellValue = "System Exception";
					}
				}
				break;
			case UMG_TRANSACTION_ID:
				cellValue = documentInfo.getTransactionId();
				break;
			case TRANSACTION_TYPE:
				Boolean isTest = documentInfo.isTest();
				if (isTest) {
					cellValue = "Test";
				} else {
					cellValue = "Prod";
				}
				break;
			case TENANT_ID:
				cellValue = documentInfo.getTenantId();
				break;
			case TRANSACTION_MODE:
				cellValue = documentInfo.getTransactionMode();
				break;
			default:
				cellValue = "";
				break;
		}

		return cellValue;
	}

	private String getExeCellValue(final ExecutionReportEnum cellField, TransactionDocumentInfo documentInfo) throws SystemException {
		String cellValue = null;
		int min = 0;
		
		switch (cellField) {
			case PROCESSING_TIME:
				try {
					cellValue = String.valueOf(documentInfo.getModelExecutionTime()) + "ms";
					//cellValue = UsageReportUtil1.getProcessingTime(documentInfo.getModelCallEnd() == null ? 0 : (documentInfo.getModelCallEnd()-documentInfo.getModelCallStart()));
				} catch (Exception e) {
					LOGGER.error("Error occured while getting cell value with umg-tran Id : "+documentInfo.getTransactionId()+" and error : ",e);
					SystemException.newSystemException(BusinessExceptionCodes.BSE000996, new Object[] {});
				}
				break;

			case MODEL_VERSION:
				cellValue = UsageReportUtil1.getModelVersion(documentInfo);
				break;

			case CREATED_ON:
				cellValue = UsageReportUtil1.getFormattedRunAsOfDate(documentInfo);
				final Long createdDate = documentInfo.getCreatedDate();

				if (maxCreatedDate.compareTo(createdDate) < min) {
					maxCreatedDate = createdDate;
				}

				if (minCreatedDate.equals(MINUS_ONE) || minCreatedDate.compareTo(createdDate) > min) {
					minCreatedDate = createdDate;
				}

				break;

			case PROCESSING_STATUS:
				cellValue = UsageReportUtil1.getProcessingStatus(documentInfo);
				break;
			case TENANT_TRANSACTION_ID:
				cellValue = documentInfo.getClientTransactionID();
				break;
			case MODEL:
				cellValue = documentInfo.getVersionName();
				break;
			case REASON:
				if (StringUtils.isNotBlank(documentInfo.getErrorCode()) || !(UsageReportUtil1.getProcessingStatus(documentInfo).equalsIgnoreCase(TransactionStatus.SUCCESS.getStatus()))) {
					if(documentInfo.getErrorDescription() != null){
						cellValue = documentInfo.getErrorDescription()+ "- (" + documentInfo.getErrorCode() + ")";
					} else {
						cellValue = "System Exception";
					}
				}
				break;
			case UMG_TRANSACTION_ID:
				cellValue = documentInfo.getTransactionId();
				break;
			case TRANSACTION_TYPE:
				Boolean isTest = documentInfo.isTest();
				if (isTest) {
					cellValue = "Test";
				} else {
					cellValue = "Prod";
				}
				break;
			case TENANT_ID:
				cellValue = documentInfo.getTenantId();
				break;
			case CPU_USAGE:
				cellValue = String.valueOf(documentInfo.getCpuUsage());
				break;
			case FREE_MEMORY:
				cellValue = documentInfo.getFreeMemory();
				break;
			case CPU_USAGE_AT_START:
				cellValue = String.valueOf(documentInfo.getCpuUsageAtStart());
				break;
			case FREE_MEMORY_AT_START:
				cellValue = documentInfo.getFreeMemoryAtStart();
				break;
			case POOL_NAME:
				cellValue = "";
				if(documentInfo.getModeletPoolName() != null){
				cellValue = documentInfo.getModeletPoolName();
				}
				break;
			case IP_AND_PORT:
				cellValue = documentInfo.getModeletHostPortInfo();
				break;
			case SYSTEM_EXE_TIME:
				cellValue = "";
				if(documentInfo.getRuntimeCallEnd() != null  &&  documentInfo.getRuntimeCallStart() != null){
				long msValue = documentInfo.getRuntimeCallEnd() - documentInfo.getRuntimeCallStart();
				if(msValue > 0){
				cellValue = String.valueOf(msValue) + "ms" ;
				}
				}
				
				break;
			case MODELET_WAIT_TIME:
				if(documentInfo.getMe2WaitingTime() != null){
				cellValue = String.valueOf(documentInfo.getMe2WaitingTime()) + "ms";
				}
				break;
			case NO_OF_ATTEMPTS:
				cellValue = String.valueOf(documentInfo.getNoOfAttempts());
				break;
			case TRANSACTION_MODE:
				cellValue = documentInfo.getTransactionMode();
				break;
			case EXECUTION_ENVIRONMENT:
				cellValue = documentInfo.getExecEnv();
				break;
			case MODELLING_ENVIRONMENT:
				cellValue = documentInfo.getModellingEnv();
				break;
			default:
				cellValue = "";
				break;
		}

		return cellValue;
	}
	private String getSheetName() {
		return SHEET_NAME + sheetIndex++;
	}

	private Row createDataRow() {
		currentRow++;
		if (isRowsExceededExcelSheetLimit()) {
			sheet = createSheet();
			createSheetHeaders();
		}

		return sheet.createRow(currentRow);
	}
	private Row createExeDataRow() {
		currentRow++;
		if (isRowsExceededExcelSheetLimit()) {
			sheet = createSheet();
			createExeSheetHeaders();
		}

		return sheet.createRow(currentRow);
	}

	private boolean isRowsExceededExcelSheetLimit() {
		return currentRow >= SHEET_MAX_ROWS;
	}

	private Cell createCell(final Row dataRow, final UsageReportColumnEnum cellField, final CellStyle cellStyle) {
		Cell cell;

		if (cellField == CREATED_ON) {
			cell = createDateCell(dataRow, cellField, cellStyle);
		} else {
			final int column = cellField.getColumnIndex();
			cell = dataRow.createCell(column, Cell.CELL_TYPE_STRING);
		}

		return cell;
	}
	private Cell createExeCell(final Row dataRow, final ExecutionReportEnum cellField, final CellStyle cellStyle) {
		Cell cell;

		if (cellField == ExecutionReportEnum.CREATED_ON) {
			cell = createExeDateCell(dataRow, cellField, cellStyle);
		} else {
			final int column = cellField.getColumnIndex();
			cell = dataRow.createCell(column, Cell.CELL_TYPE_STRING);
		}

		return cell;
	}

	private Cell createDateCell(final Row dataRow, final UsageReportColumnEnum cellField, final CellStyle cellStyle) {
		final int column = cellField.getColumnIndex();
		final Cell dateCell = dataRow.createCell(column, Cell.CELL_TYPE_STRING);
		dateCell.setCellStyle(cellStyle);
		return dateCell;
	}
	private Cell createExeDateCell(final Row dataRow, final ExecutionReportEnum cellField, final CellStyle cellStyle) {
		final int column = cellField.getColumnIndex();
		final Cell dateCell = dataRow.createCell(column, Cell.CELL_TYPE_STRING);
		dateCell.setCellStyle(cellStyle);
		return dateCell;
	}

	private void setCellValue(final Cell cell, final String value) {
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(value);
	}

	public String getReportFileName(final String tenantCode, final Long startTime, final Long endTime) {
		final String formattedStartDate = UsageReportUtil1.getFormattedDate(startTime);
		final String formattedEndDate = UsageReportUtil1.getFormattedDate(endTime);
		String filename = null;
		if (isEmpty(formattedEndDate)) {
			filename =  String.format(REPORT_FILE_FORMAT_NO_ENDDATE, tenantCode, formattedStartDate, REPORT_EXT);
		} else {
			filename = String.format(REPORT_FILE_FORMAT, tenantCode, formattedStartDate, formattedEndDate, REPORT_EXT);
		}
		return filename;
	}

	public Long getMinCreatedDate() {
		Long minDate = null;
		if (!minCreatedDate.equals(MINUS_ONE)) {
			minDate = minCreatedDate;
		}
		return minDate;
	}

	public Long getMaxCreatedDate() {
		Long maxDate = null;
		if (!maxCreatedDate.equals(MINUS_ONE)) {
			maxDate = maxCreatedDate;
		}
		
		return maxDate;
	}
}