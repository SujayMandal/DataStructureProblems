package com.fa.dp.business.sop.weekN.util;

import static java.lang.String.format;

import java.io.IOException;
import java.math.BigDecimal;

import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.core.util.DateConversionUtil;

/**
 * @author misprakh
 */
@Slf4j
@Named
public class SopWeekNFileUtil {

	/**
	 * @param style
	 * @param row
	 * @param colNum            Week n generate Header
	 */
	public void generatePriorRecommendHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER30.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER33.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER34.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER35.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER36.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER37.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER38.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER39.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER40.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER41.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER42.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER43.getValue());
	}


	/**
	 *
	 * @param style
	 * @param row
	 * @param colNum
	 */
	public void generateExcludedHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER33.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER34.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER44.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER30.getValue());
	}


	/**
	 * @param style
	 * @param row
	 * @param colNum SOP Week N generate Header
	 */
	public void generateSopWeekNRecommendedHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER30.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER33.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER34.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER35.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER36.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER37.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER38.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER39.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER40.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER41.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER42.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER43.getValue());

	}



	/**
	 * @param style
	 * @param row
	 * @param colNum
	 * 		Week 0 generate Header
	 */
	public void generateHeaderForPotential(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER18.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER19.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER20.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER21.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER24.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER30.getValue());
	}

	/**
	 * @param style
	 * @param row
	 * @param colNum
	 * 		Week n generate Header
	 */
	public void generateHeaderForFailedRecords(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER18.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER19.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER20.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER21.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER24.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER23.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER30.getValue());
	}

	public int prepareSopWeekNOutputSheet(XSSFSheet processedSheet, int processedRowNum, int colNum, DPSopWeekNParamInfo paramObject,
			Long userSelectedDate) {
		Row row = processedSheet.createRow(processedRowNum++);

		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());

		colNum = prepareCellValue(row, colNum, paramObject.getClassification());

		colNum = prepareCellValue(row, colNum, paramObject.getListEndDateDtNn());

		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListStatus());

		colNum = prepareCellValue(row, colNum, paramObject.getDateOfLastReduction());

		colNum = prepareCellValue(row, colNum, DateConversionUtil.getEstDate(paramObject.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));

		colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getLastListCycle() != 0 ? paramObject.getLastListCycle() : null));

		if(paramObject.getFailedStepCommandName() != null) {
			colNum = prepareCellValue(row, colNum, paramObject.getExclusionReason());
		}
		/*if(paramObject.getCommand() == null) {
			colNum = prepareCellValue(row, colNum, paramObject.getAutoRLSTVc());
		}*/

		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());

		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());

		return processedRowNum;
	}

	/**
	 *
	 * @param style
	 * @param row
	 * @param colNum
	 * @param data
	 * @return
	 */
	private int prepareCell(CellStyle style, Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellStyle(style);
		cell.setCellValue(data);
		return colNum;
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
		} finally {
			try {

				response.flushBuffer();
			} catch (IOException e) {
				log.error("Error while Writing error data  ", e);
			}
		}

	}

	/**
	 *
	 * @param response
	 * @param filename
	 */
	public void setResponseHeaderForZip(final HttpServletResponse response, final String filename) {
		response.setHeader("Content-Type", "application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".zip");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
	}

	/**
	 *
	 * @param style
	 * @param row
	 * @param colNum
	 */
	public void generateSopWeekNSuccessUnderReviewHeader(CellStyle style, Row row, int colNum) {

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER30.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER33.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER34.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER35.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER36.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER37.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER38.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER39.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER40.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER41.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER42.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER43.getValue());

	}

	/**
	 *
	 * @param successUnderReviewSheet
	 * @param successUnderReviewRowNum
	 * @param colNum
	 * @param dpSopWeekNParamInfo
	 * @return
	 */
	public int prepareSopWeekNsuccessUnderReviewOutput(XSSFSheet successUnderReviewSheet, int successUnderReviewRowNum, int colNum, DPSopWeekNParamInfo dpSopWeekNParamInfo) {
		Row row = successUnderReviewSheet.createRow(successUnderReviewRowNum++);

		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getPropTemp());
		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getAssetNumber());
		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getOldAssetNumber());
		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getMostRecentListEndDate());
		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getMostRecentListStatus());
		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getMostRecentPropertyStatus());
		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getMostRecentListPrice() != null ? String.valueOf(dpSopWeekNParamInfo.getMostRecentListPrice()) : null);
		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getLpDollarAdjustmentRec() != null ? String.valueOf(dpSopWeekNParamInfo.getLpDollarAdjustmentRec()) : null);
		colNum = prepareCellValue(row, colNum, getNewListPrice(dpSopWeekNParamInfo.getMostRecentListPrice(), dpSopWeekNParamInfo.getLpDollarAdjustmentRec()));
		colNum = prepareCellValue(row, colNum, StringUtils.isNotEmpty(dpSopWeekNParamInfo.getModelVersion()) ? dpSopWeekNParamInfo.getModelVersion().split(":")[0] : null);
		colNum = prepareCellValue(row, colNum, DateConversionUtil.getEstDate(dpSopWeekNParamInfo.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
		colNum = prepareCellValue(row, colNum, getManualReviewValue(dpSopWeekNParamInfo.getInitialValuation(), dpSopWeekNParamInfo.getLpDollarAdjustmentRec(),
				dpSopWeekNParamInfo.getMostRecentListPrice()));
		colNum = prepareCellValue(row, colNum, dpSopWeekNParamInfo.getInitialValuation() != null ? String.valueOf(dpSopWeekNParamInfo.getInitialValuation()) : null);
		return successUnderReviewRowNum;
	}

	public int prepareSopWeekNRecommended(XSSFSheet sheet, int rowNum, int colNum, DPSopWeekNParam paramObject) {
		Row row = sheet.createRow(rowNum++);
		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());
		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListEndDate());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListStatus());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentPropertyStatus());
		colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListPrice() != null ? String.valueOf(paramObject.getMostRecentListPrice()) : null);
		colNum = prepareCellValue(row, colNum, paramObject.getLpDollarAdjustmentRec() != null ? String.valueOf(paramObject.getLpDollarAdjustmentRec()) : null);
		colNum = prepareCellValue(row, colNum, getNewListPrice(paramObject.getMostRecentListPrice(), paramObject.getLpDollarAdjustmentRec()));
		colNum = prepareCellValue(row, colNum, StringUtils.isNotEmpty(paramObject.getModelVersion()) ? paramObject.getModelVersion().split(":")[0] : null);
		colNum = prepareCellValue(row, colNum, DateConversionUtil.getEstDate(paramObject.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
		colNum = prepareCellValue(row, colNum, getManualReviewValue(paramObject.getInitialValuation(), paramObject.getLpDollarAdjustmentRec(),
				paramObject.getMostRecentListPrice()));
		colNum = prepareCellValue(row, colNum, paramObject.getInitialValuation() != null ? String.valueOf(paramObject.getInitialValuation()) : null);

		return rowNum;
	}

	public int prepareSopWeekNFailed(XSSFSheet sheet, int rowNum, int colNum, DPSopWeekNParam paramObject) {
			Row row = sheet.createRow(rowNum++);
			colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
			colNum = prepareCellValue(row, colNum, paramObject.getMostRecentListEndDate());
			colNum = prepareCellValue(row, colNum, paramObject.getExclusionReason());
			colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());
			colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());
			return rowNum;
	}

	public int prepareSopWeekNPriorRecommended(XSSFSheet sheet, int rowNum, int colNum, DPSopWeekNParam param) {
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
		colNum = prepareCellValue(row, colNum, param.getDeliveryDate() != null ? DateConversionUtil.getUTCDate(param.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER) : null);
		colNum = prepareCellValue(row, colNum, getManualReviewValue(param.getInitialValuation(), param.getLpDollarAdjustmentRec(),
				param.getMostRecentListPrice()));
		colNum = prepareCellValue(row, colNum, param.getInitialValuation() != null ? String.valueOf(param.getInitialValuation()) : null);
		return rowNum;
	}


	private String getManualReviewValue(BigDecimal initialValuation, BigDecimal lpDollarAdjustment, BigDecimal mostRecentListPrice) {
		String newListPrice = getNewListPrice(mostRecentListPrice, lpDollarAdjustment);
		if (initialValuation != null && initialValuation.compareTo(new BigDecimal("1000000")) != -1) {
			if (lpDollarAdjustment != null && mostRecentListPrice != null &&
					lpDollarAdjustment.abs().divide(mostRecentListPrice, 2, BigDecimal.ROUND_CEILING).compareTo(new BigDecimal("0.10")) != -1) {
				return "Current Cycle % Reduction >= 10%";
			} else if (newListPrice != null &&
					initialValuation.subtract(new BigDecimal(newListPrice)).abs().divide(initialValuation, 2, BigDecimal.ROUND_CEILING).compareTo(new BigDecimal("0.30")) != -1) {
				return "Cumulative % Reduction >= 30%";
			}
		}
		return null;
	}

	private String getNewListPrice(BigDecimal mostRecentListPrice, BigDecimal lpDollarAdjustment) {
		if (mostRecentListPrice != null && lpDollarAdjustment != null) {
			return String.valueOf(mostRecentListPrice.add(lpDollarAdjustment));
		}
		return null;
	}
	
	/**
	 * @param style
	 * @param row
	 * @param colNum
	 * 		Week 0 generate Header
	 */
	public void generateHeaderSopWeek0(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER18.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER19.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER20.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER21.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER24.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, SopWeekNFileHeader.HEADER30.getValue());
	}
	
	public int prepareSopWeek0OutputSheet(XSSFSheet processedSheet, int processedRowNum, int colNum, DPSopWeek0ParamInfo paramObject) {
		Row row = processedSheet.createRow(processedRowNum++);

		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getState());
		colNum = prepareCellValue(row, colNum, paramObject.getPropertyType());
		colNum = prepareCellValue(row, colNum, paramObject.getStatus());
		colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getAssetValue()));
		colNum = prepareCellValue(row, colNum, paramObject.getAvSetDate());
		colNum = prepareCellValue(row, colNum, paramObject.getReoDate());
		colNum = prepareCellValue(row, colNum, paramObject.getListPrice());
		colNum = prepareCellValue(row, colNum, paramObject.getAssignment());
		colNum = prepareCellValue(row, colNum, paramObject.getEligible());
		//colNum = prepareCellValue(row, colNum, paramObject.getErrorDetail());
		colNum = prepareCellValue(row, colNum, paramObject.getNotes());
		colNum = prepareCellValue(row, colNum, paramObject.getClassification());
		return processedRowNum;
	}
}
