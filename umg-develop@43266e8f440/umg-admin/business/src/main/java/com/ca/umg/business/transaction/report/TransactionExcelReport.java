package com.ca.umg.business.transaction.report;
import static com.ca.umg.business.constants.BusinessConstants.UMG_EST_DATE_FORMAT;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.BATCH_ID;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.ERROR_CODE;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MAJOR_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MINOR_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.PROCESSING_STATUS;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.RUNTIME_CALL_END;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.RUNTIME_CALL_START;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.RUN_DATE_TIME;
import static java.util.Locale.getDefault;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.pool.TransactionMode;
import com.ca.umg.business.transaction.info.TransactionStatus;
import com.ca.umg.business.util.AdminUtil;


public class TransactionExcelReport {

	public static final String VALIDATION_EXCEPTION = "Validation Exception";

	public static final String SYSTEM_EXCEPTION = "System Exception";

	public static final int HEADER_ROW_NUM = 0;
	
	public static final String SHEET_NAME = "Sheet";
	
	public static final int SHEET_MAX_ROWS = 65536;
	
    public static final String VERSION_SAPERATOR = ".";

	public static final String MS = "ms";
	
	public static final String NO_RECORDS_FOUND = "No Records Found";
	
	public static final int NO_RECORDS_FOUND_COLUMN_INDEX = 0;
	
	public static final int ACTIVE_SHEET_INDEX = 0;
	
	public static final String REPORT_FILE_FORMAT = "%s_%s_%s%s";
	
	public static final String REPORT_EXT = ".xls";
	
	public static final String EMPTY = "";
	
	private final SqlRowSet sqlRowSet;
	private int currentRow;
	private Sheet sheet;
	private Workbook wb;
	private int sheetIndex = 1;

	public static final ThreadLocal<DateFormat> RUN_AS_OF_DATE_FORMAT = new ThreadLocal<DateFormat>();
	
	public static final ThreadLocal<DateFormat> DATE_FORMAT_FOR_FILE = new ThreadLocal<DateFormat>();
	
	public TransactionExcelReport(final SqlRowSet sqlRowSet) {
		this.sqlRowSet = sqlRowSet;
		initDateFormates();
	}

	private void initDateFormates() {
		RUN_AS_OF_DATE_FORMAT.set(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", getDefault()));
		DATE_FORMAT_FOR_FILE.set(new SimpleDateFormat("yyyy-MMM-dd", getDefault()));
	}
		
	public void createReport(final OutputStream outputStream) throws IOException {
		wb = createWorkBook();
		sheet = createSheet();
		setWorkbookProperties();
		createSheetHeaders();
		populateSheetData(createDateCellStyle());
		wb.write(outputStream);
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
		final TransactionExcelReportEnum values[] = TransactionExcelReportEnum.values();
		
		for (TransactionExcelReportEnum value : values) {
			if (value.isReportField()) {
				createHeader(headerRow, value);
			}
		}
		currentRow = HEADER_ROW_NUM;
	}
	
	private void createHeader(final Row headerRow, final TransactionExcelReportEnum reportField) {
		final int index = reportField.getColumnIndex();
		final String headerName = reportField.getExcelHeaderName();
		final int width = reportField.getCellWidth();
		final Cell cell = headerRow.createCell(index);
		cell.setCellValue(headerName);
		sheet.setColumnWidth(index, width);
	}
	
	private void populateSheetData(final CellStyle cellStyle) {
		boolean noRecordsFound = true;

		if (sqlRowSet != null) {
			Row dataRow;
			while (sqlRowSet.next()) {
				noRecordsFound = false;
				dataRow = createDataRow();
				populateRowData(dataRow, cellStyle);
			}
		}
		
		if (noRecordsFound) {
			setNoRecordsFoundCell();
		}
	}
		
	private void setNoRecordsFoundCell() {
		final Row dataRow = createDataRow();
		final Cell cell = dataRow.createCell(NO_RECORDS_FOUND_COLUMN_INDEX, Cell.CELL_TYPE_STRING);
		setCellValue(cell, NO_RECORDS_FOUND);
	}
	
	private void populateRowData(final Row dataRow, final CellStyle cellStyle) {
		final TransactionExcelReportEnum cellFields[] = TransactionExcelReportEnum.values();
		for (TransactionExcelReportEnum cellField : cellFields) {
			if (cellField.isReportField()) {
				final Cell cell = createCell(dataRow, cellField, cellStyle);
				final String cellValue = getCellValue(cellField); 
				setCellValue(cell, cellValue);
			}
		}
	}
	
	private String getCellValue(final TransactionExcelReportEnum cellField) {
		String cellValue;
		switch(cellField) {
		case PROCESSING_TIME :
			cellValue = getProcessingTime();
			break;
			
		case MODEL_VERSION :	
			cellValue = getModelVersion();
			break;
		
		case TRANSACTION_TYPE :	
			cellValue = getTransactionType();
			break;
		
		case RUN_DATE_TIME :
			cellValue = getFormattedRunAsOfDate();
			break;
			
		case PROCESSING_STATUS :
			cellValue = getProcessingStatus();
			break;
			
		case REASON :
			cellValue = getReason();
			break;
					
		default :
			cellValue = sqlRowSet.getString(cellField.getDbColumnName());
			break;
		}		
		
		return cellValue;
	}
	
	private String getReason() {
		String errorCode = sqlRowSet.getString(ERROR_CODE.getDbColumnName());
		String reason = EMPTY;
		if (errorCode != null && !errorCode.equals(EMPTY)) {
			errorCode = errorCode.trim();
			errorCode = errorCode.toUpperCase(getDefault());
			
			if (errorCode.startsWith("RSE") || errorCode.startsWith("ME") || errorCode.startsWith("RME")) {
				reason = SYSTEM_EXCEPTION;
			} else if (errorCode.startsWith("RVE")) {
				reason = VALIDATION_EXCEPTION;
			} else if (!errorCode.equals(EMPTY)) {
				reason = SYSTEM_EXCEPTION;
			}
		}
		
		return reason; 
	}
	
	private String getProcessingStatus() {
		final String cellValue = sqlRowSet.getString(PROCESSING_STATUS.getDbColumnName());
		final TransactionStatus status = TransactionStatus.valuOf(cellValue);
		return status.getReportStatus();
	}
	
    private String getProcessingTime() {
    	final long runtimeEndTime = sqlRowSet.getLong(RUNTIME_CALL_END.getDbColumnName());
    	final long runtimeStartTime = sqlRowSet.getLong(RUNTIME_CALL_START.getDbColumnName());
    	return (runtimeEndTime - runtimeStartTime) + " " + MS;
    }
    
    private String getModelVersion() {
    	final int majorVersion = sqlRowSet.getInt(MAJOR_VERSION.getDbColumnName());
    	final int minorVersion = sqlRowSet.getInt(MINOR_VERSION.getDbColumnName());
    	return majorVersion + VERSION_SAPERATOR + minorVersion;
    }
    
    private String getTransactionType() {
    	final String batchId = sqlRowSet.getString(BATCH_ID.getDbColumnName());
    	final TransactionMode transactionMode = TransactionMode.getTransactionMode(batchId);
    	return transactionMode.getMode();
    }
    
    private String getFormattedRunAsOfDate() {
    	final long runAsOfDateMilliseconds = sqlRowSet.getLong(RUN_DATE_TIME.getDbColumnName());
    	final Date runAsOfDate = new Date(runAsOfDateMilliseconds);   	
    	return AdminUtil.getDateFormatMillisForEst(runAsOfDate.getTime(),null);
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
	
	private boolean isRowsExceededExcelSheetLimit() {
		return currentRow >= SHEET_MAX_ROWS;
	}
	
	private Cell createCell(final Row dataRow, final TransactionExcelReportEnum cellField, final CellStyle cellStyle) {
		Cell cell;
		
		if (cellField == RUN_DATE_TIME) {
			cell = createDateCell(dataRow, cellField, cellStyle);
		} else {
			final int column = cellField.getColumnIndex();
			cell = dataRow.createCell(column, Cell.CELL_TYPE_STRING);			
		}
		
		return cell;
	}
	
	private Cell createDateCell(final Row dataRow, final TransactionExcelReportEnum cellField, final CellStyle cellStyle) {
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
        final String formattedStartDate = getFormattedDate(startTime);
        final String formattedEndDate = getFormattedDate(endTime);
        return String.format(REPORT_FILE_FORMAT, tenantCode, formattedStartDate, formattedEndDate, REPORT_EXT);
	}
 
	private String getFormattedDate(final Long dateTime) {
		String formattedDate = "";
        if (dateTime != null) {
        	formattedDate = DATE_FORMAT_FOR_FILE.get().format(new Date(dateTime));
        }
        
        return formattedDate;
	}
}