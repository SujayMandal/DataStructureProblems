package com.fa.dp.business.validation.file.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.validation.file.header.constant.DPProcessFileHeader;
import com.fa.dp.business.validation.file.header.constant.DPSopWeekNFileHeader;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.RAClientConstants;
@Slf4j
public class InputFileValidationUtil {

	private static final String XLS_EXTENSION = ".xls";
	
	public static void validateXLSFileName(String fileName) throws SystemException {
		if(fileName == null || (!fileName.equalsIgnoreCase("xls") && !fileName.equalsIgnoreCase("xlsx"))) {
			throw new SystemException(CoreExceptionCodes.DP001, new Object[] {});
		} 
	}
	
	public static void validateHeaderColumns(List<String> columns) throws SystemException {
		List<String> origHeaders = new ArrayList<>();
		origHeaders.add(DPProcessFileHeader.HEADER1.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER2.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER3.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER4.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER5.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER6.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER7.getValue());
		validateHeaders(columns, origHeaders);
	}
	
	/**
	 * Checks if there are any duplicate asset numbers found in the file
	 * @param assetNumbers
	 * @throws SystemException
	 */
	public static void validateAssetNumbers(List<String> assetNumbers) throws SystemException {
		final Set<String> duplicatesSet = new HashSet<>();
		final Set<String> tempSet = new HashSet<>();

		for (String assetNbr : assetNumbers) {
			if (!tempSet.add(assetNbr)) {
				duplicatesSet.add(assetNbr);
			}
		}
		if(duplicatesSet.size() > 0) {
			log.debug("Duplicate Asset numbers found in uploaded file : " + duplicatesSet.toString());
			throw new SystemException(CoreExceptionCodes.DP018, new Object[] {String.join(", ", duplicatesSet)});
		}
	}
	
	/**
	 * Checks if there is any file header(Column) is missing  in the file
	 * @param columns
	 * @throws SystemException
	 */
	public static void validateSOPWeek0FileHeader(List<String> columns) throws  SystemException{
		List<String> origHeaders = new ArrayList<>();
		origHeaders.add(DPProcessFileHeader.HEADER1.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER11.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER31.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER3.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER4.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER5.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER32.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER6.getValue());
		origHeaders.add(DPProcessFileHeader.HEADER7.getValue());
		validateHeaders(columns, origHeaders);
	}


	public static void validateSOPWeekNHeaderColumns(List<String> columns) throws SystemException {

		List<String> origHeaders = new ArrayList<>();
		origHeaders.add(DPSopWeekNFileHeader.HEADER1.getValue());
		/*origHeaders.add(DPSopWeekNFileHeader.HEADER2.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER3.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER4.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER5.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER6.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER7.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER8.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER9.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER10.getValue());
		origHeaders.add(DPSopWeekNFileHeader.HEADER11.getValue());*/

		// Following columns are missing from the uploaded file <column1>, <column2>
		List<String> origHeadersTemp = new ArrayList<>(origHeaders);
		for (String s : columns) {
			origHeadersTemp.remove(s);
		}
		if (origHeadersTemp.size() > 0) {
			throw new SystemException(CoreExceptionCodes.DP003, new Object[] {"\""+String.join("\", \"", origHeadersTemp)+"\""});
		}

		// Extra columns found in the uploaded file <column1>, <column2>
		List<String> columnsTemp = new ArrayList<>(columns);
		for (String s : origHeaders) {
			columnsTemp.remove(s);
		}
		if (columnsTemp.size() > 0) {
			throw new SystemException(CoreExceptionCodes.DP002, new Object[] {String.join(", ", columnsTemp)});
		}

		columnsTemp = new ArrayList<>(columns);
		for (int i = 0; i < origHeaders.size(); i++) {
			if (!columnsTemp.get(i).equals(origHeaders.get(i))) {
				throw new SystemException(CoreExceptionCodes.DP004, new Object[] {});
			}
		}
	}
	
	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Sheet getFileContent(MultipartFile file) throws IOException {
		Sheet sheet = null;
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream());){
			sheet = workbook.getSheetAt(0);
		} 
		return sheet;
	}
	
	/**
	 * @param df
	 * @param sheet
	 * @return
	 */
	public static List<String> extractHeader(DataFormatter df, Sheet datatypeSheet) {
		List<String> headerColumns = new ArrayList<String>();
		Row currentRow = datatypeSheet.getRow(0);
		for (int i = 0; i < currentRow.getPhysicalNumberOfCells(); i++) {
			Cell cell = currentRow.getCell(i);
			if(null != cell) {
				switch (cell.getCellTypeEnum()) {
				case FORMULA:
					switch (cell.getCachedFormulaResultTypeEnum()) {
					case NUMERIC:
						headerColumns.add(String.valueOf(cell.getNumericCellValue()));
					case STRING:
					default:
						headerColumns.add(cell.getRichStringCellValue().toString());
					}
					break;
				case NUMERIC:
				case STRING:
				default:
					headerColumns.add(df.formatCellValue(cell));
				}
			} else {
				headerColumns.add(RAClientConstants.CHAR_EMPTY);
			}
		}
		return headerColumns;
	}
	
	private static void validateHeaders(List<String> columns, List<String> origHeaders) throws SystemException {
		// Following columns are missing from the uploaded file <column1>, <column2>
		List<String> origHeadersTemp = new ArrayList<>(origHeaders);
		for (String s : columns) {
			origHeadersTemp.remove(s);
		}
		if (origHeadersTemp.size() > 0) {
			throw new SystemException(CoreExceptionCodes.DP003, new Object[] {"\""+String.join("\", \"", origHeadersTemp)+"\""});
		}

		// Extra columns found in the uploaded file <column1>, <column2>
		List<String> columnsTemp = new ArrayList<>(columns);
		for (String s : origHeaders) {
			columnsTemp.remove(s);
		}
		if (columnsTemp.size() > 0) {
			throw new SystemException(CoreExceptionCodes.DP002, new Object[] {String.join(", ", columnsTemp)});
		}

		columnsTemp = new ArrayList<>(columns);
		for (int i = 0; i < origHeaders.size(); i++) {
			if (!columnsTemp.get(i).equals(origHeaders.get(i))) {
				throw new SystemException(CoreExceptionCodes.DP004, new Object[] {});
			}
		}
	}
	
	public static String getCellValue(Row currentRow, DataFormatter df, int index) {
		log.debug("getCellValue() current row : " + currentRow);
		Cell cell = currentRow.getCell(index);
		log.debug("getCellValue() cell : " + cell);
		String result = null;
		if(null != cell) {
			cell.setCellType(CellType.STRING);
			result = df.formatCellValue(cell);
		}
		return result;
	}
	
	public static BigDecimal getNumericCellValue(Row currentRow, DataFormatter df, int index) {
		log.debug("getCellValue() current row : " + currentRow);
		Cell cell = currentRow.getCell(index);
		log.debug("getCellValue() cell : " + cell);
		BigDecimal result = null;
		if(null != cell) {
			cell.setCellType(CellType.NUMERIC);
			result = BigDecimal.valueOf(cell.getNumericCellValue());
		}
		return result;
	}
	
	public static String generateFileName(String fileName) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
		return FilenameUtils.getBaseName(fileName) + RAClientConstants.CHAR_UNDER_SCORE + format.format(new Date())
				+ XLS_EXTENSION;
	}
	
	public static void createFile(MultipartFile file, String generatedFileName, String sanLocation) throws SystemException {
		// Creating the directory to store file String rootPath =

		// Create the file on server File serverFile = new
		// File(dir.getAbsolutePath()
		byte[] bytes;
		String fileName = sanLocation + File.separator + generatedFileName;
		File serverFile = new File(fileName);
		try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));) {
			bytes = file.getBytes();
			stream.write(bytes);
		} catch (IOException e) {
			log.error("Exception while storing excel file: {}", e);
			throw new SystemException(CoreExceptionCodes.DP021, new Object[] {});
		}

		log.info("Input File Location=" + fileName);
	}
}
