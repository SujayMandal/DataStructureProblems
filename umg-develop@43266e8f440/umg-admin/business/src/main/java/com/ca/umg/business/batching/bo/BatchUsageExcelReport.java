package com.ca.umg.business.batching.bo;

import static com.ca.umg.business.constants.BusinessConstants.UMG_EST_DATE_FORMAT;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.getFormattedDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.getProcessingTime;
import static java.lang.Long.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.umg.business.batching.report.usage.BatchUsageReportColumnEnum;
import com.ca.umg.business.util.AdminUtil;
@SuppressWarnings("PMD")
public class BatchUsageExcelReport {

	private static final Logger LOGGER = getLogger(BatchUsageExcelReport.class);
	
	public static final int HEADER_ROW_NUM = 0;

	public static final String SHEET_NAME = "Sheet";

	public static final int SHEET_MAX_ROWS = 65536;

	public static final String NO_RECORDS_FOUND = "No Records Found";

	public static final int NO_RECORDS_FOUND_COLUMN_INDEX = 0;

	public static final int ACTIVE_SHEET_INDEX = 0;

	public static final String REPORT_FILE_FORMAT = "%s_%s_%s_%s%s";

	public static final String REPORT_EXT = ".xls";

	public static final Long MINUS_ONE = valueOf(-1l);
	
	public static final String BATCH_BULK_TAG = "batch_bulk";

	private final SqlRowSet sqlRowSet;
	private int currentRow;
	private Sheet sheet;
	private Workbook wb;
	private int sheetIndex = 1;
	private Long minCreatedDate = MINUS_ONE;
	private Long maxCreatedDate = MINUS_ONE;

	public BatchUsageExcelReport(final SqlRowSet sqlRowSet) {
		this.sqlRowSet = sqlRowSet;
	}

	public Workbook createBatchReport() throws IOException {
		wb = createWorkBook();
		sheet = createSheet();
		setWorkbookProperties();
		createBatchSheetHeaders();
		populateBatchSheetData(createDateCellStyle());
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

	private void createBatchSheetHeaders() {
		final Row headerRow = sheet.createRow(HEADER_ROW_NUM);
		final BatchUsageReportColumnEnum batchValues[] = BatchUsageReportColumnEnum.values();

		for (BatchUsageReportColumnEnum value : batchValues) {
			if (value.isReportField()) {
				createBatchHeader(headerRow, value);
			}
		}
		
		LOGGER.info("Header sheet is created");
		currentRow = HEADER_ROW_NUM;
	}

	private void createBatchHeader(final Row headerRow, final BatchUsageReportColumnEnum reportField) {
		final int index = reportField.getColumnIndex();
		final String headerName = reportField.getExcelHeaderName();
		final int width = reportField.getCellWidth();
		final Cell cell = headerRow.createCell(index);
		cell.setCellValue(headerName);
		sheet.setColumnWidth(index, width);
	}

	private void populateBatchSheetData(final CellStyle cellStyle) {
		boolean noRecordsFound = true;

		if (sqlRowSet != null) {
			Row dataRow;
			while (sqlRowSet.next()) {
				noRecordsFound = false;
				dataRow = createBatchDataRow();
				populateBatchRowData(dataRow, cellStyle);
			}
		}

		if (noRecordsFound) {
			setNoRecordsFoundCell();
		}
		
		LOGGER.info("ALl rows are populated");
	}

	private void setNoRecordsFoundCell() {
		final Row dataRow = createBatchDataRow();
		final Cell cell = dataRow.createCell(NO_RECORDS_FOUND_COLUMN_INDEX, Cell.CELL_TYPE_STRING);
		setCellValue(cell, NO_RECORDS_FOUND);
	}

	private void populateBatchRowData(final Row dataRow, final CellStyle cellStyle) {
		final BatchUsageReportColumnEnum cellFields[] = BatchUsageReportColumnEnum.values();
		for (BatchUsageReportColumnEnum cellField : cellFields) {
			if (cellField.isReportField()) {
				final Cell cell = createCell(dataRow, cellField, cellStyle);
				final String cellValue = getCellValue(cellField);
				setCellValue(cell, cellValue);
			}
		}
	}

	
	private String getCellValue(final BatchUsageReportColumnEnum cellField) {
		String cellValue;
		int min = 0;
		
		switch (cellField) {
			case TRANSACTION_TYPE:
				final Integer isTest = sqlRowSet.getInt(BatchUsageReportColumnEnum.IS_TEST.getDbColumnName());
				cellValue = (isTest == null || isTest == 0) ? "Prod" : "Test" ;
				break;
				
			case EXECUTION_TIME:
				final Long startTime =  sqlRowSet.getLong(BatchUsageReportColumnEnum.START_TIME.getDbColumnName());
				final Long endTime =  sqlRowSet.getLong(BatchUsageReportColumnEnum.END_TIME.getDbColumnName());
				cellValue = (endTime - startTime) >= 0 ? getProcessingTime(endTime - startTime) : null;
				final Long createdDate = sqlRowSet.getLong(BatchUsageReportColumnEnum.CREATED_ON.getDbColumnName());

				if (maxCreatedDate.compareTo(createdDate) < min) {
					maxCreatedDate = createdDate;
				}

				if (minCreatedDate.equals(MINUS_ONE) || minCreatedDate.compareTo(createdDate) > min) {
					minCreatedDate = createdDate;
				}
				break;

			case START_TIME:
				final Long sTime =  sqlRowSet.getLong(BatchUsageReportColumnEnum.START_TIME.getDbColumnName());
				cellValue = AdminUtil.getDateFormatMillisForEst(sTime, null);
				break;
				
			case END_TIME:
				final Long eTime =  sqlRowSet.getLong(BatchUsageReportColumnEnum.END_TIME.getDbColumnName());
				cellValue = AdminUtil.getDateFormatMillisForEst(eTime, null);
				break;
			
			case EXECUTION_DATE:
				final Long execDate =  sqlRowSet.getLong(BatchUsageReportColumnEnum.EXECUTION_DATE.getDbColumnName());
				cellValue = AdminUtil.getDateFormatMillisForEst(execDate, null);
				break;	
				
			case IN_PROGRESS_COUNT:
				final Long inProgressCount=((sqlRowSet.getLong(BatchUsageReportColumnEnum.TOTAL_RECORDS.getDbColumnName()))- 
						(sqlRowSet.getLong(BatchUsageReportColumnEnum.SUCCESS_COUNT.getDbColumnName())+
								sqlRowSet.getLong(BatchUsageReportColumnEnum.FAIL_COUNT.getDbColumnName())+
										sqlRowSet.getLong(BatchUsageReportColumnEnum.NOT_PICKED_COUNT.getDbColumnName())));
				cellValue=inProgressCount!=null?inProgressCount.toString():"0";
				break;

			default:
				cellValue = sqlRowSet.getString(cellField.getDbColumnName());
				break;
		}

		return cellValue;
	}

	private String getSheetName() {
		return SHEET_NAME + sheetIndex++;
	}

	private Row createBatchDataRow() {
		currentRow++;
		if (isRowsExceededExcelSheetLimit()) {
			sheet = createSheet();
			createBatchSheetHeaders();
		}

		return sheet.createRow(currentRow);
	}

	private boolean isRowsExceededExcelSheetLimit() {
		return currentRow >= SHEET_MAX_ROWS;
	}

	private Cell createCell(final Row dataRow, final BatchUsageReportColumnEnum cellField, final CellStyle cellStyle) {
		Cell cell;

		if (cellField == BatchUsageReportColumnEnum.CREATED_ON) {
			cell = createDateCell(dataRow, cellField, cellStyle);
		} else {
			final int column = cellField.getColumnIndex();
			cell = dataRow.createCell(column, Cell.CELL_TYPE_STRING);
		}

		return cell;
	}

	private Cell createDateCell(final Row dataRow, final BatchUsageReportColumnEnum cellField, final CellStyle cellStyle) {
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
		return String.format(REPORT_FILE_FORMAT, tenantCode, formattedStartDate, formattedEndDate, BATCH_BULK_TAG,  REPORT_EXT);
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