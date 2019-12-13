package com.fa.dp.business.weekn.report.bo;

import com.fa.dp.business.validation.file.header.constant.qaReports.DPQAReportHeader;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.String.format;

@Slf4j
@Named
public class DPQAReportUtil {

	public void writeErrorData(final HttpServletResponse response, final String msg) {
		try {
			final String headerValue = format("attachment; filename=\"%s\"", "error.txt");
			response.setHeader("Content-Disposition", headerValue);
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			String errorMsg = null;
			if (msg == null) {
				errorMsg = "System Exception occurred while generating consolidated report";
				response.getOutputStream().write(errorMsg.getBytes());
			} else {
				errorMsg = msg;
				response.getOutputStream().write(errorMsg.getBytes());
			}
		} catch (IOException excep) {
			log.error("Exception occurred  while Writing error data  ", excep);
		}

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

	/**
	 * @param style
	 * @param row
	 * @param colNum Consolidated Report generate Header
	 */
	public void generateConsolidatedHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.SELR_PROP_ID_VC_NN.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.RBID_PROP_ID_VC_PK.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.REO_PROP_STTS_VC.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.PROP_SOLD_DATE_DT.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.PROP_STTS_ID_VC_FK.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.RBID_PROP_LIST_ID_VC_PK.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.LIST_TYPE_ID_VC_FK.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.PREVIOUS_LIST_STRT_DATE.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.PREVIOUS_LIST_END_DATE.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.PREVIOUS_LIST_PRICE.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.CURRENT_LIST_STRT_DATE.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.CURRENT_LIST_END_DATE.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.LIST_STTS_DTLS_VC.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.OCCPNCY_STTS_AT_LST_CREATN.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.ACTUAL_LIST_CYCLE.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.WEEKN_RECOMMENDED_LIST_PRICE_REDUCTION.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.WEEKN_RECOMMENDED_DATE.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.WEEKN_EXCLUSION_REASON.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.PCT_PRICE_CHANGE_FRM_LAST_LIST.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.RULE_VIOLATION.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.WEEKN_MISSINGREPORT.getValue());
		colNum = prepareCell(style, row, colNum, DPQAReportHeader.CLASSIFICATION.getValue());
	}


	/**
	 * @param sheet
	 * @param rowNum
	 * @param colNum
	 * @param paramObject
	 * @return
	 */
	public int prepareConsolidatedData(XSSFSheet sheet, int rowNum, int colNum, WeekNDailyQAReportInfo paramObject) {

		Row row = sheet.createRow(rowNum++);

		colNum = prepareCellValue(row, colNum, paramObject.getSelrPropIdVcNn());
		colNum = prepareCellValue(row, colNum, paramObject.getRbidPropIdVcPk());
		colNum = prepareCellValue(row, colNum, paramObject.getReoPropSttsVc());
		colNum = prepareCellValue(row, colNum, paramObject.getPropSoldDateDt());
		colNum = prepareCellValue(row, colNum, paramObject.getPropSttsIdVcFk());
		colNum = prepareCellValue(row, colNum, paramObject.getRbidPropListIdVcPk());
		colNum = prepareCellValue(row, colNum, paramObject.getListTypeIdVcFk());
		colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getPreviousListStartDate()));
		colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getPreviousListEndDate()));
		colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getPreviousListPrice()));
		colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getCurrentListStartDate()));
		colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getCurrentListEndDate()));
		colNum = prepareCellValue(row, colNum, paramObject.getListSttsDtlsVc());
		colNum = prepareCellValue(row, colNum, paramObject.getOccpncySttsAtLstCreatn());
		colNum = prepareCellValue(row, colNum, paramObject.getActualListCycle());
		colNum = prepareCellValue(row, colNum, paramObject.getWeeknRecommendedListPriceReduction());
		colNum = prepareCellValue(row, colNum, paramObject.getWeeknRecommendedDate());
		colNum = prepareCellValue(row, colNum, paramObject.getWeeknExclusionReason());
		colNum = prepareCellValue(row, colNum, paramObject.getPctPriceChangeFrmLastList());
		colNum = prepareCellValue(row, colNum, paramObject.getRuleViolation());
		colNum = prepareCellValue(row, colNum, paramObject.getWeeknMissingreport());
		colNum = prepareCellValue(row, colNum, paramObject.getClassification());

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


	private int prepareCell(CellStyle style, Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellStyle(style);
		cell.setCellValue(data);
		return colNum;
	}
}
