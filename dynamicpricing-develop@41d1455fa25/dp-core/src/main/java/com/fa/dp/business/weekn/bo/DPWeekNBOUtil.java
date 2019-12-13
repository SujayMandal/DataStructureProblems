package com.fa.dp.business.weekn.bo;

import com.fa.dp.business.validation.file.header.constant.DPProcessFileHeader;
import com.fa.dp.business.validation.file.header.constant.DPWeekNProcessFileHeader;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.lang.String.format;

@Slf4j
@Named
public class DPWeekNBOUtil {
	/**
	 * @param style
	 * @param row
	 * @param colNum Week n generate Header
	 */
	public void generateHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER18.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER19.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER20.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER21.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER24.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER23.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER30.getValue());
	}

	/**
	 * @param style
	 * @param passed12CyclesRow
	 * @param colNum            Week n generate Header
	 */
	public void generatePast12CyclesHeader(CellStyle style, Row passed12CyclesRow, int colNum) {
		colNum = prepareCell(style, passed12CyclesRow, colNum, DPProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, passed12CyclesRow, colNum, DPProcessFileHeader.HEADER25.getValue());

		colNum = prepareCell(style, passed12CyclesRow, colNum, DPProcessFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, passed12CyclesRow, colNum, DPProcessFileHeader.HEADER30.getValue());
	}

	/**
	 * @param style
	 * @param priorRecommendRow
	 * @param colNum            Week n generate Header
	 */
	public void generatePriorRecommendHeader(CellStyle style, Row priorRecommendRow, int colNum) {
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER15.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPProcessFileHeader.HEADER1.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER12.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER2.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER3.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER4.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER5.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER6.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER14.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER7.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER8.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER9.getValue());
		colNum = prepareCell(style, priorRecommendRow, colNum, DPWeekNProcessFileHeader.HEADER13.getValue());
	}

	/**
	 * @param style
	 * @param row
	 * @param colNum Week 0 generate Header
	 */
	public void generateHeaderForPotential(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER18.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER19.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER20.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER21.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER24.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER28.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER30.getValue());
	}

	private int prepareCell(CellStyle style, Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellStyle(style);
		cell.setCellValue(data);
		return colNum;
	}

	/**
	 * @param sheet
	 * @param rowNum
	 * @param colNum
	 * @param paramObject
	 * @param userSelectedDate
	 * @return
	 */
	public int prepareWeekNOutputData(XSSFSheet sheet, int rowNum, int colNum, DPProcessWeekNParamInfo paramObject, Long userSelectedDate) {

		Row row = sheet.createRow(rowNum++);

		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());

		colNum = prepareCellValue(row, colNum, paramObject.getClassification());

		colNum = prepareCellValue(row, colNum, paramObject.getListEndDateDtNn());

		colNum = prepareCellValue(row, colNum, paramObject.getListSttsDtlsVc());

		colNum = prepareCellValue(row, colNum, paramObject.getDateOfLastReduction());

		colNum = prepareCellValue(row, colNum, DateConversionUtil.getEstDate(userSelectedDate).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));

		colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getLastListCycle() != 0 ? paramObject.getLastListCycle() : null));

		if (paramObject.getCommand() != null) {
			colNum = prepareCellValue(row, colNum, paramObject.getExclusionReason());
		}
		if (paramObject.getCommand() == null) {
			colNum = prepareCellValue(row, colNum, paramObject.getAutoRLSTVc());
		}

		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());

		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());

		return rowNum;
	}

	public int prepareWeekNRecommendedData(XSSFSheet sheet, int rowNum, int colNum, DPProcessWeekNParam paramObject) {

		Row row = sheet.createRow(rowNum++);
		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());
		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListEndDate());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListStatus());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentPropertyStatus());
		colNum = prepareCellValue(row, colNum,
				paramObject.getMostRecentListPrice() != null ? String.valueOf(paramObject.getMostRecentListPrice()) : null);
		colNum = prepareCellValue(row, colNum,
				paramObject.getLpDollarAdjustmentRec() != null ? String.valueOf(paramObject.getLpDollarAdjustmentRec()) : null);
		colNum = prepareCellValue(row, colNum, getNewListPrice(paramObject.getMostRecentListPrice(), paramObject.getLpDollarAdjustmentRec()));
		colNum = prepareCellValue(row, colNum,
				StringUtils.isNotEmpty(paramObject.getModelVersion()) ? paramObject.getModelVersion().split(":")[0] : null);
		colNum = prepareCellValue(row, colNum,
				DateConversionUtil.getEstDate(paramObject.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
		colNum = prepareCellValue(row, colNum, getManualReviewValue(paramObject.getInitialValuation(), paramObject.getLpDollarAdjustmentRec(),
				paramObject.getMostRecentListPrice()));
		colNum = prepareCellValue(row, colNum, paramObject.getInitialValuation() != null ? String.valueOf(paramObject.getInitialValuation()) : null);

		return rowNum;
	}

	public String getNewListPrice(BigDecimal mostRecentListPrice, BigDecimal lpDollarAdjustment) {
		if (mostRecentListPrice != null && lpDollarAdjustment != null) {
			return String.valueOf(mostRecentListPrice.add(lpDollarAdjustment));
		}
		return null;
	}

	public String getManualReviewValue(BigDecimal initialValuation, BigDecimal lpDollarAdjustment, BigDecimal mostRecentListPrice) {
		String newListPrice = getNewListPrice(mostRecentListPrice, lpDollarAdjustment);
		if (initialValuation != null && initialValuation.compareTo(new BigDecimal("1000000")) != -1) {
			if (lpDollarAdjustment != null && mostRecentListPrice != null
					&& lpDollarAdjustment.abs().divide(mostRecentListPrice, 2, BigDecimal.ROUND_CEILING).compareTo(new BigDecimal("0.10")) != -1) {
				return "Current Cycle % Reduction >= 10%";
			} else if (newListPrice != null &&
					initialValuation.subtract(new BigDecimal(newListPrice)).abs().divide(initialValuation, 2, BigDecimal.ROUND_CEILING)
							.compareTo(new BigDecimal("0.30")) != -1) {
				return "Cumulative % Reduction >= 30%";
			}
		}
		return null;
	}

	public int prepareWeekNExcludedData(XSSFSheet sheet, int rowNum, int colNum, DPProcessWeekNParam paramObject) {

		Row row = sheet.createRow(rowNum++);
		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListEndDate());
		colNum = prepareCellValue(row, colNum, paramObject.getExclusionReason());
		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());

		return rowNum;
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
	 * @param response
	 * @param filename set Response Header
	 */
	public void setResponseHeader(final HttpServletResponse response, final String filename) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Expires:", "0"); // eliminates browser caching
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
	}

	public int prepareWeekNSkipped12CyclesOutputData(XSSFSheet sheet, int rowNum, int colNum, DPProcessWeekNParamInfo paramObject) {
		Row row = sheet.createRow(rowNum++);
		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getListEndDateDtNn());
		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());
		return rowNum;
	}

	public int prepareWeekNPriorRecommendationOutputData(XSSFSheet sheet, int rowNum, int colNum, DPProcessWeekNParam param) {
		Row row = sheet.createRow(rowNum++);
		colNum = prepareCellValue(row, colNum, param.getPropTemp());
		colNum = prepareCellValue(row, colNum, param.getAssetNumber());
		colNum = prepareCellValue(row, colNum, param.getOldAssetNumber());
		colNum = prepareCellValue(row, colNum, param.getMostRecentListEndDate());
		colNum = prepareCellValue(row, colNum, param.getMostRecentListStatus());
		colNum = prepareCellValue(row, colNum, param.getMostRecentPropertyStatus());
		colNum = prepareCellValue(row, colNum, param.getMostRecentListPrice() != null ? String.valueOf(param.getMostRecentListPrice()) : null);
		colNum = prepareCellValue(row, colNum, param.getLpDollarAdjustmentRec() != null ? String.valueOf(param.getLpDollarAdjustmentRec()) : null);
		colNum = prepareCellValue(row, colNum, getNewListPrice(param.getMostRecentListPrice(), param.getLpDollarAdjustmentRec()));
		colNum = prepareCellValue(row, colNum, param.getModelVersion());
		colNum = prepareCellValue(row, colNum, param.getDeliveryDate() != null ?
				DateConversionUtil.getUTCDate(param.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER) :
				null);
		colNum = prepareCellValue(row, colNum,
				getManualReviewValue(param.getInitialValuation(), param.getLpDollarAdjustmentRec(), param.getMostRecentListPrice()));
		colNum = prepareCellValue(row, colNum, param.getInitialValuation() != null ? String.valueOf(param.getInitialValuation()) : null);
		return rowNum;
	}

	public void generateWeekNSuccessUnderReviewHeader(CellStyle style, Row row, int colNum) {

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER15.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER12.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER2.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER3.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER4.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER5.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER6.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER14.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER8.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER9.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER13.getValue());

	}

	/**
	 * @param style
	 * @param row
	 * @param colNum Week N generate Header
	 */
	public void generateWeekNRecommendedHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER15.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER12.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER2.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER3.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER4.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER5.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER6.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER14.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER8.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER9.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER13.getValue());

	}

	public void generateExcludedHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER2.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER10.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER12.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER15.getValue());
	}

	public int prepareWeekNsuccessUnderReviewOutputData(XSSFSheet sheet, int rowNum, int colNum, DPProcessWeekNParamInfo paramObject) {
		Row row = sheet.createRow(rowNum++);
		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());
		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListEndDate());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListStatus());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentPropertyStatus());
		colNum = prepareCellValue(row, colNum,
				paramObject.getMostRecentListPrice() != null ? String.valueOf(paramObject.getMostRecentListPrice()) : null);
		colNum = prepareCellValue(row, colNum,
				paramObject.getLpDollarAdjustmentRec() != null ? String.valueOf(paramObject.getLpDollarAdjustmentRec()) : null);
		colNum = prepareCellValue(row, colNum, getNewListPrice(paramObject.getMostRecentListPrice(), paramObject.getLpDollarAdjustmentRec()));
		colNum = prepareCellValue(row, colNum,
				StringUtils.isNotEmpty(paramObject.getModelVersion()) ? paramObject.getModelVersion().split(":")[0] : null);
		colNum = prepareCellValue(row, colNum,
				DateConversionUtil.getEstDate(paramObject.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
		colNum = prepareCellValue(row, colNum, getManualReviewValue(paramObject.getInitialValuation(), paramObject.getLpDollarAdjustmentRec(),
				paramObject.getMostRecentListPrice()));
		colNum = prepareCellValue(row, colNum, paramObject.getInitialValuation() != null ? String.valueOf(paramObject.getInitialValuation()) : null);

		return rowNum;
	}

	public void writeErrorData(final HttpServletResponse response, final String msg) {
		try {
			final String headerValue = format("attachment; filename=\"%s\"", "error.txt");
			response.setHeader("Content-Disposition", headerValue);
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			String errorMsg = null;
			if (msg == null) {
				errorMsg = "SystemException occurred while downloading File";
				response.getOutputStream().write(errorMsg.getBytes());
			} else {
				errorMsg = msg;
				response.getOutputStream().write(errorMsg.getBytes());
			}
		} catch (IOException excep) {
			log.error("Error while Writing error data  ", excep);
		}

	}

	public static String generateWeekNFileName(String fileName) {
		return StringUtils.join(FilenameUtils.getBaseName(fileName), RAClientConstants.CHAR_UNDER_SCORE,
				DateConversionUtil.SYSTEM_GENERATED_FILE_DATE_FORMAT.format(LocalDateTime.now()));
	}

	public static String fetchCelValue(Row currentRow, DataFormatter df, int index) {
		String result = null;
		if (currentRow != null && df != null) {
			log.info("getCellValue() current row : " + currentRow);
			Cell cell = currentRow.getCell(index);
			log.info("getCellValue() cell : " + cell);

			if (null != cell) {
				cell.setCellType(CellType.STRING);
				result = df.formatCellValue(cell);
			}
		}
		return result;
	}
}
