package com.ca.umg.business.transaction.report;

import static com.ca.pool.TransactionMode.BATCH;
import static com.ca.pool.TransactionMode.ONLINE;
import static com.ca.umg.business.transaction.report.TransactionExcelReport.DATE_FORMAT_FOR_FILE;
import static com.ca.umg.business.transaction.report.TransactionExcelReport.HEADER_ROW_NUM;
import static com.ca.umg.business.transaction.report.TransactionExcelReport.SHEET_NAME;
import static com.ca.umg.business.transaction.report.TransactionExcelReport.SYSTEM_EXCEPTION;
import static com.ca.umg.business.transaction.report.TransactionExcelReport.VALIDATION_EXCEPTION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.BATCH_ID;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.ENVIRONMENT;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.ERROR_CODE;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MAJOR_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MINOR_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MODEL;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MODEL_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.PROCESSING_STATUS;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.PROCESSING_TIME;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.REASON;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.RUNTIME_CALL_END;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.RUNTIME_CALL_START;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.RUN_DATE_TIME;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.TENANT_ID;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.TENANT_TRANSACTION_ID;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.TRANSACTION_TYPE;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.UMG_TRANSACTION_ID;
import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.umg.business.util.AdminUtil;

public class TransactionExcelReportTest {

	
	private static final String TENANT_CODE = "local";
	private File reportFile;
	private Long currentMillSeconds;
	
	@Before
	public void setup() {
		reportFile = new File(getFileName());
		currentMillSeconds = System.currentTimeMillis();
	}
	
	@After
	public void delete() {
		reportFile.deleteOnExit();
	}
	
	@Test
	public void testOneRowData() throws FileNotFoundException, IOException {
		 FileOutputStream outputStream = null;
		 FileInputStream inputSteram = null;
		try {
		    outputStream = new FileOutputStream(reportFile);
			final TransactionExcelReport report = new TransactionExcelReport(createRecords());
			report.createReport(outputStream);
		    inputSteram = new FileInputStream(reportFile);
			final Workbook wb = new HSSFWorkbook(inputSteram);
			final Sheet sheet = wb.getSheet(SHEET_NAME + 1);
			final Row batchSucessDataRow = sheet.getRow(HEADER_ROW_NUM + 1);
			
			checkCellWithExpectedData("ID1", UMG_TRANSACTION_ID, batchSucessDataRow);
			checkCellWithExpectedData(TENANT_CODE, TENANT_ID, batchSucessDataRow);
			checkCellWithExpectedData(BATCH.getMode(), TRANSACTION_TYPE, batchSucessDataRow);
			checkCellWithExpectedData("B1", BATCH_ID, batchSucessDataRow);
			checkCellWithExpectedData("UMGModel", MODEL, batchSucessDataRow);
			checkCellWithExpectedData("1.2", MODEL_VERSION, batchSucessDataRow);
			checkCellWithExpectedData("ClientTranId", TENANT_TRANSACTION_ID, batchSucessDataRow);
			
			checkCellWithExpectedData(AdminUtil.getDateFormatMillisForEst(currentMillSeconds,null), RUN_DATE_TIME, batchSucessDataRow);
			//checkCellWithExpectedData(RUN_AS_OF_DATE_FORMAT.get().format(new Date(currentMillSeconds)), RUN_DATE_TIME, batchSucessDataRow);
			checkCellWithExpectedData("Success", PROCESSING_STATUS, batchSucessDataRow);
			checkCellWithExpectedData("1000 ms", PROCESSING_TIME, batchSucessDataRow);
			checkCellWithExpectedData("", REASON, batchSucessDataRow);
			checkCellWithExpectedData("", ENVIRONMENT, batchSucessDataRow);
			
			final Row onlineFailedDataRow = sheet.getRow(HEADER_ROW_NUM + 2);
			
			checkCellWithExpectedData("ID1", UMG_TRANSACTION_ID, onlineFailedDataRow);
			checkCellWithExpectedData(TENANT_CODE, TENANT_ID, onlineFailedDataRow);
			checkCellWithExpectedData(ONLINE.getMode(), TRANSACTION_TYPE, onlineFailedDataRow);
			checkCellWithExpectedData("UMGModel", MODEL, onlineFailedDataRow);
			checkCellWithExpectedData("1.2", MODEL_VERSION, onlineFailedDataRow);
			checkCellWithExpectedData("ClientTranId", TENANT_TRANSACTION_ID, onlineFailedDataRow);
			//checkCellWithExpectedData(RUN_AS_OF_DATE_FORMAT.get().format(new Date(currentMillSeconds)), RUN_DATE_TIME, onlineFailedDataRow);
			checkCellWithExpectedData(AdminUtil.getDateFormatMillisForEst(currentMillSeconds,null), RUN_DATE_TIME, onlineFailedDataRow);
			
			checkCellWithExpectedData("Failure", PROCESSING_STATUS, onlineFailedDataRow);
			checkCellWithExpectedData("1000 ms", PROCESSING_TIME, onlineFailedDataRow);
			checkCellWithExpectedData("", BATCH_ID, onlineFailedDataRow);
			checkCellWithExpectedData(VALIDATION_EXCEPTION, REASON, onlineFailedDataRow);
			checkCellWithExpectedData("Matlab", ENVIRONMENT, onlineFailedDataRow);
			
			final Row onlineErrorDataRow = sheet.getRow(HEADER_ROW_NUM + 3);
			
			checkCellWithExpectedData("ID1", UMG_TRANSACTION_ID, onlineErrorDataRow);
			checkCellWithExpectedData(TENANT_CODE, TENANT_ID, onlineErrorDataRow);
			checkCellWithExpectedData(ONLINE.getMode(), TRANSACTION_TYPE, onlineErrorDataRow);
			checkCellWithExpectedData("UMGModel", MODEL, onlineErrorDataRow);
			checkCellWithExpectedData("1.2", MODEL_VERSION, onlineErrorDataRow);
			checkCellWithExpectedData("ClientTranId", TENANT_TRANSACTION_ID, onlineErrorDataRow);
			//checkCellWithExpectedData(RUN_AS_OF_DATE_FORMAT.get().format(new Date(currentMillSeconds)), RUN_DATE_TIME, onlineFailedDataRow);
			checkCellWithExpectedData(AdminUtil.getDateFormatMillisForEst(currentMillSeconds,null), RUN_DATE_TIME, onlineErrorDataRow);
			
			checkCellWithExpectedData("Failure", PROCESSING_STATUS, onlineErrorDataRow);
			checkCellWithExpectedData("1000 ms", PROCESSING_TIME, onlineErrorDataRow);
			checkCellWithExpectedData("", BATCH_ID, onlineErrorDataRow);
			checkCellWithExpectedData(SYSTEM_EXCEPTION, REASON, onlineErrorDataRow);
			checkCellWithExpectedData("R", ENVIRONMENT, onlineErrorDataRow);
			
			
		} finally {
           closeResources(outputStream, inputSteram);
		}
		
	}

	private void closeResources(FileOutputStream outputStream, FileInputStream inputSteram) throws IOException {
		if(outputStream !=null) {
        	   outputStream.flush();
        	   outputStream.close();
			}
			IOUtils.closeQuietly(inputSteram);
	}	
	
	@Test 
	public void testReportHeaders() throws FileNotFoundException, IOException {	
		 FileOutputStream outputStream = null;
		 FileInputStream inputSteram = null;
		try {
		    outputStream = new FileOutputStream(reportFile);
			final TransactionExcelReport report = new TransactionExcelReport(null);
			report.createReport(outputStream);
			
			
		    inputSteram = new FileInputStream(reportFile);
			final Workbook wb = new HSSFWorkbook(inputSteram);
			final Sheet sheet = wb.getSheet(SHEET_NAME + 1);
			final Row headerRow = sheet.getRow(HEADER_ROW_NUM);
			
			final TransactionExcelReportEnum headers[] = TransactionExcelReportEnum.values();
			for (TransactionExcelReportEnum header : headers) {
				if (header.isReportField()) {				
					final String sheetHeaderName = headerRow.getCell(header.getColumnIndex()).getStringCellValue();
					assertTrue(sheetHeaderName.equals(header.getExcelHeaderName()));
				}
			}
			
			
		} finally {
	           closeResources(outputStream, inputSteram);
			}
		
	}
	
	@Test 
	public void testNORecordsFound() throws FileNotFoundException, IOException {	
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
		    outputStream = new FileOutputStream(reportFile);
			final TransactionExcelReport report = new TransactionExcelReport(null);
			report.createReport(outputStream);
			
			inputStream = new FileInputStream(reportFile);
			final Workbook wb = new HSSFWorkbook(inputStream);
			final Sheet sheet = wb.getSheet(SHEET_NAME + 1);
			final Row noRecordsFoundRow = sheet.getRow(HEADER_ROW_NUM + 1);
			final Cell noRecordsFoundCell = noRecordsFoundRow.getCell(0);
			final String expectedValue = "No Records Found";
			assertTrue(expectedValue.equals(noRecordsFoundCell.getStringCellValue()));
		} finally {
	           closeResources(outputStream, inputStream);
			}
		
	}
	
	
	@Test
	public void testGetReportFileNameEmptyDates() {
		final TransactionExcelReport report = new TransactionExcelReport(null);
		final Long startDateTime = null;
		final Long endDateTime = null;
		final String fileName = report.getReportFileName(TENANT_CODE, startDateTime, endDateTime);
		final String expectedFileName = TENANT_CODE + "_" + "_" + ".xls";
		assertTrue(expectedFileName.equals(fileName));
	}

	@Test
	public void testGetReportFileNameWithSatrtDate() {
		final TransactionExcelReport report = new TransactionExcelReport(null);
		final Long startDateTime = System.currentTimeMillis();
		final Long endDateTime = null;
		final String fileName = report.getReportFileName(TENANT_CODE, startDateTime, endDateTime);
		final String formattedStartDate = DATE_FORMAT_FOR_FILE.get().format(new Date(startDateTime));
		final String expectedFileName = TENANT_CODE + "_" + formattedStartDate + "_" + ".xls";
		assertTrue(expectedFileName.equals(fileName));
	}
	
	@Test
	public void testGetReportFileNameWithEndDate() {
		final TransactionExcelReport report = new TransactionExcelReport(null);
		final Long startDateTime = null;
		final Long endDateTime = System.currentTimeMillis();
		final String fileName = report.getReportFileName(TENANT_CODE, startDateTime, endDateTime);
		final String formattedEndDate = DATE_FORMAT_FOR_FILE.get().format(new Date(endDateTime));
		final String expectedFileName = TENANT_CODE + "_" + "_" + formattedEndDate + ".xls";
		assertTrue(expectedFileName.equals(fileName));
	}
	
	@Test
	public void testGetReportFileNameWithBothDates() {
		final TransactionExcelReport report = new TransactionExcelReport(null);
		final Long startDateTime = System.currentTimeMillis();
		final Long endDateTime = System.currentTimeMillis();
		final String fileName = report.getReportFileName(TENANT_CODE, startDateTime, endDateTime);
		final String formattedStartDate = DATE_FORMAT_FOR_FILE.get().format(new Date(startDateTime));
		final String formattedEndDate = DATE_FORMAT_FOR_FILE.get().format(new Date(endDateTime));
		final String expectedFileName = TENANT_CODE + "_" + formattedStartDate + "_" + formattedEndDate + ".xls";
		assertTrue(expectedFileName.equals(fileName));
	}
	
	private String getFileName() {
		final TransactionExcelReport report = new TransactionExcelReport(null);
		return report.getReportFileName(TENANT_CODE, null, null);
	}
	
	private SqlRowSet createRecords() {
		final SqlRowSet sqlRowSet = Mockito.mock(SqlRowSet.class);
		when(sqlRowSet.next()).thenReturn(true, true, true, false);
		
		// First record for Success Batch and Second one for failed online
		when(sqlRowSet.getInt(MAJOR_VERSION.getDbColumnName())).thenReturn(valueOf(1), valueOf(1), valueOf(1));
		when(sqlRowSet.getInt(MINOR_VERSION.getDbColumnName())).thenReturn(valueOf(2), valueOf(2), valueOf(2));
		
		when(sqlRowSet.getLong(RUNTIME_CALL_END.getDbColumnName())).thenReturn(Long.valueOf(2000), Long.valueOf(2000), Long.valueOf(2000));
		when(sqlRowSet.getLong(RUNTIME_CALL_START.getDbColumnName())).thenReturn(Long.valueOf(1000), Long.valueOf(1000), Long.valueOf(1000));
		
		String value = null;
		when(sqlRowSet.getString(BATCH_ID.getDbColumnName())).thenReturn("B1", "B1", value);
		
		when(sqlRowSet.getLong(RUN_DATE_TIME.getDbColumnName())).thenReturn(currentMillSeconds, currentMillSeconds, currentMillSeconds);
		
		when(sqlRowSet.getString(UMG_TRANSACTION_ID.getDbColumnName())).thenReturn("ID1", "ID1", "ID1");
		when(sqlRowSet.getString(TENANT_ID.getDbColumnName())).thenReturn(TENANT_CODE, TENANT_CODE, TENANT_CODE);
		when(sqlRowSet.getString(MODEL.getDbColumnName())).thenReturn("UMGModel", "UMGModel", "UMGModel");
		when(sqlRowSet.getString(TENANT_TRANSACTION_ID.getDbColumnName())).thenReturn("ClientTranId", "ClientTranId", "ClientTranId");
		when(sqlRowSet.getString(PROCESSING_STATUS.getDbColumnName())).thenReturn("Success", "Failed", "Error");
		when(sqlRowSet.getString(ERROR_CODE.getDbColumnName())).thenReturn("", "RVE00001", "RSE00001");
		when(sqlRowSet.getString(ENVIRONMENT.getDbColumnName())).thenReturn(null, "Matlab", "R");
		
		return sqlRowSet;		
	}
	
	private void checkCellWithExpectedData(final String expectedData, final TransactionExcelReportEnum columnField, 
		final Row dataRow) {
		final Cell cell = dataRow.getCell(columnField.getColumnIndex());
		final String reportCelldata = cell.getStringCellValue();
		assertTrue(expectedData.equals(reportCelldata));
	}
}