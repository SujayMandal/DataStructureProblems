package com.ca.umg.business.tenant.report.usage.bo;

import static com.ca.umg.business.constants.BusinessConstants.UMG_EST_DATE_FORMAT;
import static com.ca.umg.business.tenant.report.usage.ExecutionReportEnum.CREATED_ON;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.values;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.getFormattedDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.getFormattedRunAsOfDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.getModelVersion;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.getProcessingStatus;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.getProcessingTime;
import static java.lang.Long.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.umg.business.tenant.report.usage.ExecutionReportEnum;
import com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum;
import com.ca.umg.business.tenant.report.usage.util.UsageReportUtil;
@SuppressWarnings("PMD")
public class UsageExcelReport {

	private static final Logger LOGGER = getLogger(UsageExcelReport.class);
	
	public static final int HEADER_ROW_NUM = 0;

	public static final String SHEET_NAME = "Sheet";

	public static final int SHEET_MAX_ROWS = 65536;

	public static final String NO_RECORDS_FOUND = "No Records Found";

	public static final int NO_RECORDS_FOUND_COLUMN_INDEX = 0;

	public static final int ACTIVE_SHEET_INDEX = 0;

	public static final String REPORT_FILE_FORMAT = "%s_%s_%s%s";

	public static final String REPORT_EXT = ".xls";

	public static final Long MINUS_ONE = valueOf(-1l);

	private final SqlRowSet sqlRowSet;
	private int currentRow;
	private Sheet sheet;
	private Workbook wb;
	private int sheetIndex = 1;
	private Long minCreatedDate = MINUS_ONE;
	private Long maxCreatedDate = MINUS_ONE;

	public UsageExcelReport(final SqlRowSet sqlRowSet) {
		this.sqlRowSet = sqlRowSet;
	}

	public void createReport(final OutputStream outputStream) throws IOException {
		wb = createWorkBook();
		sheet = createSheet();
		setWorkbookProperties();
		createSheetHeaders();
		populateSheetData(createDateCellStyle());
		wb.write(outputStream);
		LOGGER.info("Report is created");
	}
	
	public Workbook createReport() throws IOException {
		wb = createWorkBook();
		sheet = createSheet();
		setWorkbookProperties();
		createSheetHeaders();
		populateSheetData(createDateCellStyle());
		return wb;
	}

	public Workbook createExeReport() throws IOException {
		wb = createWorkBook();
		sheet = createSheet();
		setWorkbookProperties();
		createExeSheetHeaders();
		populateExeSheetData(createDateCellStyle());
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
		
		LOGGER.info("ALl rows are populated");
	}
	private void populateExeSheetData(final CellStyle cellStyle) {
		boolean noRecordsFound = true;

		if (sqlRowSet != null) {
			Row dataRow;
			while (sqlRowSet.next()) {
				noRecordsFound = false;
				dataRow = createExeDataRow();
				populateExeRowData(dataRow, cellStyle);
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

	private void populateRowData(final Row dataRow, final CellStyle cellStyle) {
		final UsageReportColumnEnum cellFields[] = values();
		for (UsageReportColumnEnum cellField : cellFields) {
			if (cellField.isReportField()) {
				final Cell cell = createCell(dataRow, cellField, cellStyle);
				final String cellValue = getCellValue(cellField);
				setCellValue(cell, cellValue);
			}
		}
	}

	private void populateExeRowData(final Row dataRow, final CellStyle cellStyle) {
		final ExecutionReportEnum cellFields[] = ExecutionReportEnum.values();
		for (ExecutionReportEnum cellField : cellFields) {
			if (cellField.isReportField()) {
				final Cell cell = createExeCell(dataRow, cellField, cellStyle);
				final String cellValue = getExeCellValue(cellField);
				setCellValue(cell, cellValue);
			}
		}
	}

	private String getCellValue(final UsageReportColumnEnum cellField) {
		String cellValue = null;
		int min = 0;
		
		switch (cellField) {
			case PROCESSING_TIME:
				cellValue = getProcessingTime(sqlRowSet.getLong(cellField.getDbColumnName()));
				break;

			case MODEL_VERSION:
				cellValue = getModelVersion(sqlRowSet);
				break;

			case CREATED_ON:
				cellValue = getFormattedRunAsOfDate(sqlRowSet);
				final Long createdDate = sqlRowSet.getLong(CREATED_ON.getDbColumnName());

				if (maxCreatedDate.compareTo(createdDate) < min) {
					maxCreatedDate = createdDate;
				}

				if (minCreatedDate.equals(MINUS_ONE) || minCreatedDate.compareTo(createdDate) > min) {
					minCreatedDate = createdDate;
				}

				break;

			case PROCESSING_STATUS:
				cellValue = getProcessingStatus(sqlRowSet);
				break;
			case REASON:
				if (!(sqlRowSet.getString("STATUS").equalsIgnoreCase("SUCCESS"))) {
					InputStream in = null;
					StringWriter writer = null;
					try{
						cellValue = "System Exception";
						if( sqlRowSet.getString("ERROR_DESCRIPTION") != null || StringUtils.isNotBlank(sqlRowSet.getString("ERROR_DESCRIPTION"))){
							 in = ((ResultSetWrappingSqlRowSet)sqlRowSet).getResultSet().getBinaryStream("ERROR_DESCRIPTION");
							 writer = new StringWriter();
							IOUtils.copy(in, writer);
							cellValue = writer.toString();
						} 
							
						}
					
					catch (Exception e) {
						LOGGER.error("Error occured while getting cell value with umg-tran Id : "+ cellValue  + e);
					}
					finally{
						try {
							if(in != null){
							in.close();
							}
							if(writer != null){
								writer.close();
								}
						} catch (IOException e) {
							LOGGER.error("Error in closing input strean : "+ cellValue  + e);
						}
					}
				}
				break;
			default:
				cellValue = sqlRowSet.getString(cellField.getDbColumnName());
				break;
		}

		return cellValue;
	}
	
	private String getExeCellValue(final ExecutionReportEnum cellField) {
		String cellValue = null;
		int min = 0;
		
		switch (cellField) {
			case PROCESSING_TIME:
				cellValue = getProcessingTime(sqlRowSet.getLong(cellField.getDbColumnName()));
				break;

			case MODEL_VERSION:
				cellValue = getModelVersion(sqlRowSet);
				break;

			case CREATED_ON:
				cellValue = getFormattedRunAsOfDate(sqlRowSet);
				final Long createdDate = sqlRowSet.getLong(CREATED_ON.getDbColumnName());

				if (maxCreatedDate.compareTo(createdDate) < min) {
					maxCreatedDate = createdDate;
				}

				if (minCreatedDate.equals(MINUS_ONE) || minCreatedDate.compareTo(createdDate) > min) {
					minCreatedDate = createdDate;
				}

				break;

			case PROCESSING_STATUS:
				cellValue = getProcessingStatus(sqlRowSet);
				break;

			case SYSTEM_EXE_TIME:
				cellValue = UsageReportUtil.getPlatformExecutionTime(sqlRowSet);
				break;
			case IP_AND_PORT:
				cellValue = "";
				if(sqlRowSet.getString(cellField.getDbColumnName()) != null && ! sqlRowSet.getString(cellField.getDbColumnName()).equalsIgnoreCase("null:null")){
					cellValue = sqlRowSet.getString(cellField.getDbColumnName());
				}
				if(sqlRowSet.getInt("R_SERVE_PORT") != 0){
					cellValue = cellValue + "/" + String.valueOf(sqlRowSet.getInt("R_SERVE_PORT"));
				}
				break;
			case REASON:
				if (!(sqlRowSet.getString("STATUS").equalsIgnoreCase("SUCCESS"))) {
					InputStream in = null;
					StringWriter writer = null;
					try{
						cellValue = "System Exception";
						if( sqlRowSet.getString("ERROR_DESCRIPTION") != null || StringUtils.isNotBlank(sqlRowSet.getString("ERROR_DESCRIPTION"))){
							 in = ((ResultSetWrappingSqlRowSet)sqlRowSet).getResultSet().getBinaryStream("ERROR_DESCRIPTION");
							 writer = new StringWriter();
							IOUtils.copy(in, writer);
							cellValue = writer.toString();
						} 
							
						}
					
					catch (Exception e) {
						LOGGER.error("Error occured while getting cell value with umg-tran Id : "+ cellValue  + e);
					}
					finally{
						try {
							if(in != null){
							in.close();
							}
							if(writer != null){
								writer.close();
								}
						} catch (IOException e) {
							LOGGER.error("Error in closing input strean : "+ cellValue  + e);
						}
					}
				}
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

		if (cellField == UsageReportColumnEnum.CREATED_ON) {
			cell = createDateCell(dataRow, cellField, cellStyle);
		} else {
			final int column = cellField.getColumnIndex();
			cell = dataRow.createCell(column, Cell.CELL_TYPE_STRING);
		}

		return cell;
	}
	
	private Cell createExeCell(final Row dataRow, final ExecutionReportEnum cellField, final CellStyle cellStyle) {
		Cell cell;

		if (cellField == CREATED_ON) {
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
		final String formattedStartDate = getFormattedDate(startTime);
		final String formattedEndDate = getFormattedDate(endTime);
		return String.format(REPORT_FILE_FORMAT, tenantCode, formattedStartDate, formattedEndDate, REPORT_EXT);
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