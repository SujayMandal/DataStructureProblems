/**
 *
 */
package com.fa.dp.business.task;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.validation.file.header.constant.DPProcessFileHeader;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author misprakh
 */
@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0OutputFileCreate")
public class Week0OutputFileCreate extends AbstractCommand {

	private static final String SHEET_WEEK0_DB = "Week0_DB";

	private static final String NRZ_OUTPUT_APPENDER = "_output_NRZ.xlsx";

	private static final String OCN_OUTPUT_APPENDER = "_output_OCN.xlsx";

	private static final String PHH_OUTPUT_APPENDER = "_output_PHH.xlsx";

	private static final Logger LOGGER = LoggerFactory.getLogger(Week0OutputFileCreate.class);

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Override
	public void execute(Object data) throws SystemException {
		LOGGER.info("Week0OutputFileCreate -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessParamEntryInfo infoObject = null;
		if (checkData(data, DPProcessParamEntryInfo.class)) {
			infoObject = ((DPProcessParamEntryInfo) data);

			// Saving all Eligible entry to DB.

			for (DPProcessParamInfo info : infoObject.getColumnEntries()) {
				MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
				info.setEligible(DPProcessParamAttributes.ELIGIBLE.getValue());
				info.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());

				LOGGER.info("Week0OutputFileProcess() week0 price : " + info.getWeek0Price());
				if (null == info.getWeek0Price()) {
					info.setWeek0Price(new BigDecimal(info.getListPrice()));
				}
				DPProcessParam obj = new DPProcessParam();
				obj = convert(info, DPProcessParam.class);
				dpFileProcessBO.saveDPProcessParam(obj);
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}

			String fileName = infoObject.getDPFileProcessStatusInfo().getSysGnrtdInputFileName();
			String ocnFileName = generateOutputFile(infoObject, fileName, DPProcessParamAttributes.OCN.getValue());
			String phhFileName = generateOutputFile(infoObject, fileName, DPProcessParamAttributes.PHH.getValue());
			String nrzFileName = generateOutputFile(infoObject, fileName, DPProcessParamAttributes.NRZ.getValue());

			infoObject.getDPFileProcessStatusInfo().setOcnOutputFileName(ocnFileName != null ? ocnFileName : RAClientConstants.CHAR_EMPTY);
			infoObject.getDPFileProcessStatusInfo().setPhhOutputFileName(StringUtils.isNotEmpty(phhFileName) ? phhFileName : RAClientConstants.CHAR_EMPTY);
			infoObject.getDPFileProcessStatusInfo().setNrzOutputFileName(nrzFileName != null ? nrzFileName : RAClientConstants.CHAR_EMPTY);
		}
		log.info("Time taken for Week0OutputFileCreate : " + (DateTime.now().getMillis() - startTime) + "ms");
		LOGGER.info("Week0OutputFileCreate -> processTask ended.");
	}

	/**
	 * @param infoObject
	 * @param fileName
	 * @return
	 */
	private String generateOutputFile(DPProcessParamEntryInfo infoObject, String fileName, String classification) {
		if (fileName.toLowerCase().endsWith(".xls")) {
			fileName = fileName.substring(0, fileName.length() - 4);
		} else if (fileName.toLowerCase().endsWith(".xlsx")) {
			fileName = fileName.substring(0, fileName.length() - 5);
		}
		if(StringUtils.equals(DPProcessParamAttributes.OCN.getValue(), classification)) {
			fileName += OCN_OUTPUT_APPENDER;
		} else if(StringUtils.equals(DPProcessParamAttributes.PHH.getValue(), classification)) {
			fileName += PHH_OUTPUT_APPENDER;
		} else if(StringUtils.equals(DPProcessParamAttributes.NRZ.getValue(), classification)) {
			fileName += NRZ_OUTPUT_APPENDER;
		}

		String outputFileName = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH) + File.separator + fileName;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(SHEET_WEEK0_DB);

		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);

		int rowNum = 0;

		Row row = sheet.createRow(rowNum++);
		int colNum = 0;
		generateHeader(style, row, colNum);

		for (int i = 1; i <= row.getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}

		List<DPProcessParam> columnEntries = dpFileProcessBO.findDPProcessParamByProcessID(infoObject.getDPFileProcessStatusInfo().getId());

		for (DPProcessParam paramObject : columnEntries) {
			colNum = 0;
			rowNum = prepareOutputData(sheet, rowNum, colNum, paramObject, classification);
		}

		if (rowNum > 1) {
			try {
				FileOutputStream outputStream = new FileOutputStream(outputFileName);
				workbook.write(outputStream);
			} catch (FileNotFoundException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} finally {
				try {
					workbook.close();
				} catch (IOException e) {
					LOGGER.error(e.getLocalizedMessage(), e);
				}
			}
		} else {
			fileName = null;
		}

		return fileName;
	}

	/**
	 * @param sheet
	 * @param rowNum
	 * @param colNum
	 * @param paramObject
	 * @return
	 */
	private int prepareOutputData(XSSFSheet sheet, int rowNum, int colNum, DPProcessParam paramObject, String classification) {
		if (paramObject.getClassification().equalsIgnoreCase(classification)) {
			Row row = sheet.createRow(rowNum++);

			colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());

			colNum = prepareCellValue(row, colNum, paramObject.getClientCode());

			colNum = prepareCellValue(row, colNum, paramObject.getStatus());

			colNum = prepareCellValue(row, colNum, paramObject.getAssetValue().toString());

			colNum = prepareCellValue(row, colNum, paramObject.getAvSetDate());

			colNum = prepareCellValue(row, colNum, paramObject.getListPrice().toString());

			colNum = prepareCellValue(row, colNum, paramObject.getClassification());

			colNum = prepareCellValue(row, colNum, paramObject.getEligible());

			colNum = prepareCellValue(row, colNum, paramObject.getAssignment());

			//Week0 price should be list price if assignment is benchmark.

			if (StringUtils.isNotBlank(paramObject.getAssignment()) && StringUtils
					.equalsIgnoreCase(paramObject.getAssignment(), DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
				colNum = prepareCellValue(row, colNum, paramObject.getListPrice().toString());
			} else {
				colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getWeek0Price()));
			}

			colNum = prepareCellValue(row, colNum, paramObject.getState());

			colNum = prepareCellValue(row, colNum, paramObject.getRtSource());

			colNum = prepareCellValue(row, colNum, paramObject.getNotes());

			colNum = prepareCellValue(row, colNum, paramObject.getPropertyType());

			colNum = prepareCellValue(row, colNum, paramObject.getAssignmentDate() != null ?
					DateConversionUtil.getEstDate(paramObject.getAssignmentDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER) :
					null);

			colNum = prepareCellValue(row, colNum, paramObject.getPctAV());

			colNum = prepareCellValue(row, colNum, paramObject.getWithinBusinessRules());
		}

		return rowNum;
	}

	/**
	 * @param style
	 * @param row
	 * @param colNum
	 */
	private void generateHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER2.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER3.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER4.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER5.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER6.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER8.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER9.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER10.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER11.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER12.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER13.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER14.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER15.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER16.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER17.getValue());
	}

	/**
	 * @param row
	 * @param colNum
	 * @param data
	 * @return
	 */
	private int prepareCellValue(Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellValue(data);
		return colNum;
	}

	/**
	 * @param style
	 * @param row
	 * @param colNum
	 * @return
	 */
	private int prepareCell(CellStyle style, Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellStyle(style);
		cell.setCellValue(data);
		return colNum;
	}

}