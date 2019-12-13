package com.ca.umg.plugin.commons.excel.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.ca.umg.plugin.commons.excel.reader.ExcelDetails;
import com.ca.umg.plugin.commons.excel.reader.ExcelDetailsParser;

@Ignore
// TODO fix ignored test cases
public class ExcelWriterHelperTest {

	private final String excelFileWithMissingCorrId = "com/ca/umg/plugin/commons/excel/writer/INPUT_EXCEL_WITH_MISSING_CORRID.xls";

	private final String excelFileWithDupsCorrId = "com/ca/umg/plugin/commons/excel/writer/INPUT_EXCEL_WITH_DUPS_CORRID.xls";

	private final String excelFileForUMG_2229 = "com/ca/umg/plugin/commons/excel/writer/UMG-2229.xls";

	@Test
	@Ignore
	public void testFillMissingCorrelationIds() throws InvalidFormatException, IOException {
		InputStream inputStream = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(excelFileWithMissingCorrId);
			Workbook workBook = WorkbookFactory.create(inputStream);
			Sheet dataSheet = getDataSheet(workBook);
			ExcelDetailsParser excelDetailsParser = new ExcelDetailsParser();
			ExcelDetails xlValDetails = excelDetailsParser.validateExcelForCorrelationId(dataSheet);
			// ExcelWriterHelper.fillMissingCorrelationIds(dataSheet, xlValDetails);
			fos = new FileOutputStream(new File("./filledCorrId.xls"));
			workBook.write(fos);
			fis = FileUtils.openInputStream(new File("./filledCorrId.xls"));
			workBook = WorkbookFactory.create(fis);
			dataSheet = getDataSheet(workBook);
			Row secRow = dataSheet.getRow(2);
			Row fifthRow = dataSheet.getRow(5);
			Cell secRowCell = secRow.getCell(xlValDetails.getCorrelationColumnIndex());
			Cell fifthRowCell = fifthRow.getCell(xlValDetails.getCorrelationColumnIndex());
			assertEquals("UMGMISS-1", secRowCell.getStringCellValue());
			assertEquals("UMGMISS-2", fifthRowCell.getStringCellValue());
			FileUtils.forceDelete(new File("./filledCorrId.xls"));
		} finally {
			if (inputStream != null) {
				IOUtils.closeQuietly(inputStream);
			}

			if (fos != null) {
				fos.flush();
				fos.close();
			}
			if (fis != null) {
				IOUtils.closeQuietly(fis);
			}
		}
	}

	@Test
	@Ignore
	public void testRectifyDupsCorrId() throws InvalidFormatException, IOException {
		InputStream inputStream = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(excelFileWithDupsCorrId);
			Workbook workBook = WorkbookFactory.create(inputStream);
			Sheet dataSheet = getDataSheet(workBook);
			ExcelDetailsParser excelDetailsParser = new ExcelDetailsParser();
			ExcelDetails xlValDetails = excelDetailsParser.validateExcelForCorrelationId(dataSheet);
			xlValDetails = excelDetailsParser.duplicateCheck(dataSheet, xlValDetails);

			// ExcelWriterHelper.rectifyDuplicates(dataSheet, xlValDetails);
			fos = new FileOutputStream(new File("./rectifiedDupsCorrId.xls"));
			workBook.write(fos);
			fis = FileUtils.openInputStream(new File("./rectifiedDupsCorrId.xls"));
			workBook = WorkbookFactory.create(fis);
			dataSheet = getDataSheet(workBook);
			Row fourthRow = dataSheet.getRow(3);
			Row sixthRow = dataSheet.getRow(5);
			Cell secRowCell = fourthRow.getCell(xlValDetails.getCorrelationColumnIndex());
			Cell fifthRowCell = sixthRow.getCell(xlValDetails.getCorrelationColumnIndex());
			System.out.println(secRowCell.getStringCellValue());
			System.out.println(fifthRowCell.getStringCellValue());
			assertEquals("UMGDUPS-11-2", secRowCell.getStringCellValue());
			assertEquals("UMGDUPS-22-1", fifthRowCell.getStringCellValue());
			FileUtils.forceDelete(new File("./rectifiedDupsCorrId.xls"));
		} finally {

			if (inputStream != null) {
				IOUtils.closeQuietly(inputStream);
			}
			if (fis != null) {
				IOUtils.closeQuietly(fis);
			}
			if (fos != null) {
				fos.flush();
				fos.close();
			}

		}
	}

	/**
	 * This analysis w.r.t this issue is that there were empty cells in the first
	 * row of data because of which the column count was coming different for this
	 * particular row. This is now managed by considering the header column index
	 * for correlation & inserting values based on this index for all the rows.
	 * 
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	@Test
	public void testUMG_2229() throws InvalidFormatException, IOException {
		InputStream inputStream = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(excelFileForUMG_2229);
			Workbook workBook = WorkbookFactory.create(inputStream);
			Sheet dataSheet = getDataSheet(workBook);
			ExcelWriterHelper.generateNewCorrelationId(dataSheet);
			fos = new FileOutputStream(new File("./UMG_2229_ModifiedRequest.xls"));
			workBook.write(fos);
			fos.flush();
			fos.close();
			fis = FileUtils.openInputStream(new File("./UMG_2229_ModifiedRequest.xls"));
			workBook = WorkbookFactory.create(fis);
			dataSheet = getDataSheet(workBook);
			Row secondRow = dataSheet.getRow(1);
			Cell cell = secondRow.getCell(40);
			String cellValue = cell.getStringCellValue();
			assertTrue(StringUtils.isNotEmpty(cellValue) && StringUtils.equals(cellValue, "UMGCORR-1"));
			fis.close();
			FileUtils.forceDelete(new File("./UMG_2229_ModifiedRequest.xls"));
		} finally {
			if (inputStream != null) {
				IOUtils.closeQuietly(inputStream);
			}
			if (fis != null) {
				IOUtils.closeQuietly(fis);
			}
			if (fos != null) {
				fos.flush();
				fos.close();
			}
		}
	}

	private Sheet getDataSheet(Workbook workBook) {
		Sheet dataSheet = null;
		for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
			Sheet sheet = workBook.getSheetAt(i);
			if (sheet.getSheetName().equalsIgnoreCase("Data")) {
				dataSheet = sheet;
				break;
			}
		}
		return dataSheet;
	}

}
