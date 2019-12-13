package com.ca.umg.sdc.rest.controller;

import static com.ca.framework.core.util.ConversionUtil.convertToFormattedJsonStringByteArray;
import static com.ca.umg.business.constants.BusinessConstants.HYPHEN;
import static com.ca.umg.business.constants.BusinessConstants.TXN_DOWNLOAD_DATE;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.INPUT_TABULAR_VIEW;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.MODEL_INPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.MODEL_OUTPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.OUTPUT_TABULAR_VIEW;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.TENANT_INPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.TENANT_OUTPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.getModelReport;
import static com.ca.umg.business.tenant.report.model.util.JsonToExcelConverterUtil2.createTenantInputWorkbook;
import static com.ca.umg.business.tenant.report.model.util.JsonToExcelConverterUtil2.createTenantOupputWorkbook;
import static com.ca.umg.business.transaction.util.TransactionUtil.addWorkbookToZipFile;
import static com.ca.umg.business.util.AdminUtil.getDateFormatMillisForEst;
import static com.ca.umg.plugin.commons.excel.util.JsonToExcelConverterUtil.populateSheetData;
import static com.ca.umg.plugin.commons.excel.util.JsonToExcelConverterUtil.populateSheetHeader;
import static com.ca.umg.sdc.rest.constants.RestConstants.CONTROLLER_DONE_MESSAGE;
import static com.ca.umg.sdc.rest.constants.RestConstants.NO_MODEL_REPORT_FOUND;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.tenant.report.model.TenantModelReport;
import com.ca.umg.business.tenant.report.model.TenantModelReportEnum;
import com.ca.umg.business.tenant.report.model.delegate.TenantModelReportDelegate;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;
import com.ca.umg.business.tenant.report.usage.delegate.TenantUsageReportDelegate;
import com.ca.umg.plugin.commons.excel.reader.ReadHeaderSheet;
import com.ca.umg.sdc.rest.utils.RestResponse;

@SuppressWarnings("PMD")
@Controller
@RequestMapping("/modelReport")
public class TenantModelReportController {

	private static final String TXN_ID = "txnId";

	private static final Logger LOGGER = getLogger(TenantModelReportController.class);

	@Inject
	private TenantModelReportDelegate tenantModelReportDelegate;

	@Inject
	private TenantUsageReportDelegate tenantUsageReportDelegate;

	@RequestMapping(value = "/view/{txnId}")
	@ResponseBody
	public RestResponse<TenantModelReport> viewTenantModelJsonData(@PathVariable(TXN_ID) final String txnId) {
		LOGGER.debug("Request reached for fetching Tenant and Model JSON Data:" + txnId);
		final RestResponse<TenantModelReport> response = new RestResponse<TenantModelReport>();
		try {
			final TenantModelReport tenantModelReport = tenantModelReportDelegate.viewTenantModelReport(txnId, true);
			response.setError(false);
			response.setMessage(CONTROLLER_DONE_MESSAGE);
			if (tenantModelReport == null) {
				response.setMessage(NO_MODEL_REPORT_FOUND);
			} else {
				response.setResponse(tenantModelReport);
			}

		} catch (SystemException | BusinessException se) {
			LOGGER.debug("Request reached for fetching Tenant and Model JSON Data: Failed");
			LOGGER.error(se.getLocalizedMessage(), se);
			response.setError(true);
			response.setErrorCode(se.getCode());
			response.setMessage(se.getLocalizedMessage());
		}

		LOGGER.debug("Request reached for fetching Tenant and Model JSON Data: Success");
		return response;
	}

	@RequestMapping(value = "/export")
	@ResponseBody
	public void downloadModelReport(@RequestParam("txnId") final String txnId, @RequestParam("reportName") final String reportName,
			final HttpServletResponse response) {
		LOGGER.debug("Request reached for exporting " + reportName + " of transaction Id :" + txnId);
		try {
			final TenantModelReportEnum report = getModelReport(reportName);
			if (report == INPUT_TABULAR_VIEW || report == OUTPUT_TABULAR_VIEW) {
				exportTabularView(response, txnId);
			} else {
				final TenantModelReport modelReport = tenantModelReportDelegate.exportTenantModelReport(txnId, report);
				if (modelReport != null) {
					final String filename = getFileName(modelReport.getVersionName(), modelReport.getClientTransactionID(), modelReport.getCreatedDate(),
							report.getReportFilename());
					final Map<String, Object> reportData = modelReport.getReportData(report);
					writeDataAndDownloadIO(response, reportData, filename);
				} else {
					writeErrorData(response, txnId, null);
				}
			}
		} catch (SystemException | IOException | BusinessException se) {
			LOGGER.error("Request reached for exporting " + reportName + " of transaction Id :" + txnId + ": Failed");
			LOGGER.error("Error while writing to response outputstream for tenant IO for the transaction : " + txnId, se);
			if (se.getLocalizedMessage() != null) {
				writeErrorData(response, txnId, se.getLocalizedMessage());
			} else {
				writeErrorData(response, txnId, se.getMessage());
			}
		} finally {
			try {
				if(response.getOutputStream() != null){
				response.getOutputStream().close();
				}
			} catch (IOException e) {
				LOGGER.error("Error while closing response outputstream for tenant IO data for the transaction : " + txnId, e);
			}
		}
		LOGGER.debug("Request reached for exporting " + reportName + " of transaction Id :" + txnId + ": Sucess");
	}

	private void writeDataAndDownloadIO(final HttpServletResponse response, final Map<String, Object> data, final String filename)
			throws IOException, SystemException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValueAsBytes(data);
		setResponseHeader(response, filename);
		try {
			response.getOutputStream().write(convertToFormattedJsonStringByteArray(mapper.writeValueAsBytes(data)));
		} finally {
			response.getOutputStream().close();
		}
		response.getOutputStream().flush();
	}

	private void setResponseHeader(final HttpServletResponse response, final String filename) {
		response.setHeader("Content-Type", "text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".txt");
	}

	private void writeErrorData(final HttpServletResponse response, final String txnId, final String msg) {
		try {
			final String headerValue = format("attachment; filename=\"%s\"", "error_" + txnId + ".txt");
			response.setHeader("Content-Disposition", headerValue);
			String errorMsg = null;
			if (msg == null) {
				errorMsg = "No Data found for the transactionId :" + txnId;
				response.getOutputStream().write(errorMsg.getBytes());
			} else {
				errorMsg = msg;
				response.getOutputStream().write(errorMsg.getBytes());
			}
		} catch (IOException excep) {
			LOGGER.error("Error while Writting error data  ", excep);
		}
		
	}

	@RequestMapping(value = "/indexedTxnByFilter", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<TenantModelReport> viewNextTenantModelJsonDataByFilter(@RequestBody final UsageReportFilter filter) {
		LOGGER.debug("Request reached for fetching next or previous Tenant and Model JSON Data:");
		RestResponse<TenantModelReport> response = new RestResponse<TenantModelReport>();
		try {
			filter.setSearchString(null);
			response = viewTenantModelJsonData(getTransactionIdByFilter(filter));
		} catch (SystemException | BusinessException se) {
			LOGGER.debug("Request reached for fetching Tenant and Model JSON Data: Failed");
			LOGGER.error(se.getLocalizedMessage(), se);
			response.setError(true);
			response.setErrorCode(se.getCode());
			response.setMessage(se.getLocalizedMessage());
		}

		return response;
	}

	private String getTransactionIdByFilter(final UsageReportFilter filter) throws SystemException, BusinessException {
		return tenantUsageReportDelegate.getTransactionIdByFilter(filter);
	}

	public static String getFileName(final String modelName, final String clientTxnId, final Long createdDate, final String type) {
		final StringBuilder fileNamePreFix = new StringBuilder();
		final String dateFormate = getDateFormatMillisForEst(createdDate, TXN_DOWNLOAD_DATE);
		final String withoutSpace = dateFormate.replaceAll(" ", HYPHEN);
		final String withoutColon = withoutSpace.replaceAll(":", HYPHEN);
		fileNamePreFix.append(modelName).append(HYPHEN).append(clientTxnId).append(HYPHEN).append(withoutColon);

		if (type != null) {
			fileNamePreFix.append(HYPHEN).append(type);
		}

		return fileNamePreFix.toString();
	}

	private void exportTabularView(final HttpServletResponse response, final String txnId) throws SystemException, BusinessException {
		final TenantModelReport report = tenantModelReportDelegate.viewTenantModelReport(txnId, false);
		final MappingDescriptor mappingDescription = tenantModelReportDelegate.getMappingDescriptor(report);
		if (report != null) {

			try {
				writeDataAndDownloadIO(response, report, mappingDescription);
			} catch (IOException ioe) {
				// TODO
			}
		}
	}

	private void writeDataAndDownloadIO(final HttpServletResponse response, final TenantModelReport report, final MappingDescriptor mappingDescription)
			throws IOException, SystemException, BusinessException {

		final String versionName = report.getVersionName();
		final String clientTransactionID = report.getClientTransactionID();
		final Long createdDate = report.getCreatedDate();

		final String zipFileName = getFileName(versionName, clientTransactionID, createdDate, null);

		final String tenantInputFilename = getFileName(versionName, clientTransactionID, createdDate, TENANT_INPUT.getReportFilename());
		final String tenantOutputFilename = getFileName(versionName, clientTransactionID, createdDate, TENANT_OUTPUT.getReportFilename());
		final String modelInputFilename = getFileName(versionName, clientTransactionID, createdDate, MODEL_INPUT.getReportFilename());
		final String modelOutputFilename = getFileName(versionName, clientTransactionID, createdDate, MODEL_OUTPUT.getReportFilename());

		final ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
		final Workbook tenantInputWorkbook = createTenantInputWorkbook(mappingDescription, report.getTenantInput());
		final Workbook tenantOutputWorkbook = createTenantOupputWorkbook(mappingDescription, report.getTenantOutput());
		final Workbook modelInputWorkbook = createAndPopulateWorksheet(createDataList(report.getModelInput()));
		final Workbook modelOutputWorkbook = createAndPopulateWorksheet(createDataList(report.getModelOutput()));

		setResponseHeaderForZip(response, zipFileName);
		try {
			addWorkbookToZipFile(tenantInputFilename, getBytes(tenantInputWorkbook), zos);
			addWorkbookToZipFile(tenantOutputFilename, getBytes(tenantOutputWorkbook), zos);
			addWorkbookToZipFile(modelInputFilename, getBytes(modelInputWorkbook), zos);
			addWorkbookToZipFile(modelOutputFilename, getBytes(modelOutputWorkbook), zos);
		} finally {
			if(zos != null){
			zos.finish();
			zos.close();
			}
		}
		response.getOutputStream().flush();
	}
	
	private List<Map<String, Object>> createDataList(final Map<String, Object> data) {
		final List<Map<String, Object>> dataList = new ArrayList<>();
		dataList.add(data);
		return dataList;
	}
	
	private List<Map<String, Object>> createTenantInputDataList(final List<TenantModelReport> reports) {
		final List<Map<String, Object>> dataList = new ArrayList<>();
		for (TenantModelReport report : reports) {
			if (report.getTenantInput() != null) {
				dataList.add(report.getTenantInput());
			}
		}
		return dataList;
	}
	
	private List<Map<String, Object>> createTenantOutputDataList(final List<TenantModelReport> reports) {
		final List<Map<String, Object>> dataList = new ArrayList<>();
		for (TenantModelReport report : reports) {
			if (report.getTenantOutput() != null) {
				Map<String, Object> tenantOutput = report.getTenantOutput();
				if(MapUtils.isNotEmpty(tenantOutput)){
					Map<String, Object> headerObj = 	(Map<String, Object>)tenantOutput.get("header");
					if(headerObj!=null){
						headerObj.put(ReadHeaderSheet.TRANSACTION_TYPE, report.getTransactionType());
					}
					Map<String, Object> dataObj = 	(Map<String, Object>)tenantOutput.get("data");
					if(dataObj!=null){
						dataObj.put("umgTransactionId", headerObj.get("umgTransactionId"));
					}
				}
			dataList.add(report.getTenantOutput());
			}
		}
		return dataList;
	}
	
	private List<Map<String, Object>> createModelInputDataList(final List<TenantModelReport> reports) {
		List<Map<String, Object>> dataList = null;
		for (TenantModelReport report : reports) {
			if (report.getModelInput() != null) {
				if(dataList==null){
					dataList = 	new ArrayList<>();
				}
				dataList.add(report.getModelInput());
			}
		}
		return dataList;
	}
	
	private List<Map<String, Object>> createModelOutputDataList(final List<TenantModelReport> reports) {
		List<Map<String, Object>> dataList = null;
		for (TenantModelReport report : reports) {
			if(dataList==null){
				dataList = 	new ArrayList<>();
			}
			if (report.getModelOutput() != null) {
			dataList.add(report.getModelOutput());
			}
		}
		return dataList;
	}

	private Workbook createAndPopulateWorksheet(final List<Map<String, Object>> datum)  {
		final Workbook wb = new HSSFWorkbook();
		int i = 0;
		if (datum != null && datum.size() > 0) {
			for (Map<String, Object> data : datum) {
				for (Object e : data.entrySet()) {
					final Entry x = (Entry) e;
					final String sheetName = x.getKey().toString();
					if (sheetName.equalsIgnoreCase("modelName") || sheetName.equalsIgnoreCase("modelExecutionTime")
							|| sheetName.equalsIgnoreCase("modeletExecutionTime")) {
						continue;
					}

					Sheet sheet = wb.getSheet(sheetName);
					if (sheet == null) {
						sheet = wb.createSheet(sheetName);
						populateSheetHeader(sheet, x.getValue(), false,true);
					}
					populateSheetData(sheet, x.getValue(), i++, false,true);
				}				
			}
		}else{
			Sheet sheet = wb.createSheet();		
			Row row = sheet.createRow(0);					
			row.createCell(0).setCellValue("null");
		}

		return wb;
	}

	private byte[] getBytes(final Workbook wb) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			wb.write(bos);
		} finally {
			if(bos != null){
			bos.close();
			}
		}

		return bos.toByteArray();
	}

	private void setResponseHeaderForZip(final HttpServletResponse response, final String filename) {
		response.setHeader("Content-Type", "application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".zip");
	}

	@RequestMapping(value = "/indexedTxnBySearch", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<TenantModelReport> viewNextTenantModelJsonDataBySearch(@RequestBody final UsageReportFilter filter) {
		LOGGER.debug("Request reached for fetching next or previous Tenant and Model JSON Data:");
		RestResponse<TenantModelReport> response = new RestResponse<TenantModelReport>();
		try {
			response = viewTenantModelJsonData(getTransactionIdBySearch(filter));
		} catch (SystemException | BusinessException se) {
			LOGGER.debug("Request reached for fetching Tenant and Model JSON Data: Failed");
			LOGGER.error(se.getLocalizedMessage(), se);
			response.setError(true);
			response.setErrorCode(se.getCode());
			response.setMessage(se.getLocalizedMessage());
		}

		return response;
	}

	private String getTransactionIdBySearch(final UsageReportFilter filter) throws SystemException, BusinessException {
		return tenantUsageReportDelegate.getTransactionIdBySearch(filter);
	}
	
	@RequestMapping(value = "/rerun", method = RequestMethod.POST)
	@ResponseBody
	public void downloadModelReport(@RequestParam("selectedTransactionList") final List<String> selectedTransactionList,
			final HttpServletResponse response) {
		LOGGER.debug("Request reached for exporting Rerun of transaction Id :" + selectedTransactionList.toString());
		try {
			final List<TenantModelReport> reports = tenantModelReportDelegate.viewTenantModelReport(selectedTransactionList);
			if (reports != null && reports.size() > 0) {
				final MappingDescriptor mappingDescription = tenantModelReportDelegate.getMappingDescriptorForReport(reports.get(0));
				try {
					writeDataAndDownloadIO(response, reports, mappingDescription);
				} catch (IOException ioe) {
					// TODO
				}
			}
		} catch (SystemException | BusinessException se) {
			LOGGER.error("Request reached for exporting Rerun of transaction Id : Failed");
			LOGGER.error("Error while writing to response outputstream for tenant IO for the transaction : ", se);
			if (se.getLocalizedMessage() != null) {
				writeErrorData(response, selectedTransactionList.toString(), se.getLocalizedMessage());
			} else {
				writeErrorData(response, selectedTransactionList.toString(), se.getMessage());
			}
		}  finally {
			try {
				if(response.getOutputStream() != null){
				response.getOutputStream().close();
				}
			} catch (IOException e) {
				LOGGER.error("Error while closing response outputstream for tenant IO data for the transaction : ", e);
			}
		}
		LOGGER.debug("Request reached for exporting Rerun of transaction Id : Sucess");
	}

	private void writeDataAndDownloadIO(final HttpServletResponse response, final List<TenantModelReport> reports, final MappingDescriptor mappingDescription)
			throws IOException, SystemException, BusinessException {

		final TenantModelReport firstResport = reports.get(0);
		final String versionName = firstResport.getVersionName();
		final String clientTransactionID = firstResport.getClientTransactionID();
		final Long createdDate = firstResport.getCreatedDate();

		final String zipFileName = getFileName(versionName, clientTransactionID, createdDate, null);

		final String tenantInputFilename = getFileName(versionName, clientTransactionID, createdDate, TENANT_INPUT.getReportFilename());
		final String tenantOutputFilename = getFileName(versionName, clientTransactionID, createdDate, TENANT_OUTPUT.getReportFilename());
		final String modelInputFilename = getFileName(versionName, clientTransactionID, createdDate, MODEL_INPUT.getReportFilename());
		final String modelOutputFilename = getFileName(versionName, clientTransactionID, createdDate, MODEL_OUTPUT.getReportFilename());

		final ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
		Workbook tenantInputWorkbook = null;
		Workbook tenantOutputWorkbook = null;
		Workbook modelInputWorkbook = null;
		Workbook modelOutputWorkbook =  null;
		try {
		 tenantInputWorkbook = createTenantInputWorkbook(mappingDescription, createTenantInputDataList(reports));
		} catch(Exception se){
			LOGGER.error("The maximum length of cell contents (text) is 32,767 characters");
			LOGGER.error("Error while writing to response outputstream for tenant IO for the transaction : ", se);
			if (se.getLocalizedMessage() != null) {
				addWorkbookToZipFile(tenantInputFilename, se.getLocalizedMessage().getBytes(), zos);
			} else {
				addWorkbookToZipFile(tenantInputFilename, se.getMessage().getBytes(), zos);
			}
		}
		 try {
		 tenantOutputWorkbook = createTenantOupputWorkbook(mappingDescription, createTenantOutputDataList(reports));
		 } catch(Exception se){
				LOGGER.error("The maximum length of cell contents (text) is 32,767 characters");
				LOGGER.error("Error while writing to response outputstream for tenant IO for the transaction : ", se);
				if (se.getLocalizedMessage() != null) {
					addWorkbookToZipFile(tenantOutputFilename, se.getLocalizedMessage().getBytes(), zos);
				} else {
					addWorkbookToZipFile(tenantOutputFilename, se.getMessage().getBytes(), zos);
				}
			}
		 try {
		 modelInputWorkbook = createAndPopulateWorksheet(createModelInputDataList(reports));
		 }catch(Exception se){
				LOGGER.error("The maximum length of cell contents (text) is 32,767 characters");
				LOGGER.error("Error while writing to response outputstream for tenant IO for the transaction : ", se);
				if (se.getLocalizedMessage() != null) {
					addWorkbookToZipFile(modelInputFilename, se.getLocalizedMessage().getBytes(), zos);
				} else {
					addWorkbookToZipFile(modelInputFilename, se.getMessage().getBytes(), zos);
				}
			}
		 try {
		 modelOutputWorkbook = createAndPopulateWorksheet(createModelOutputDataList(reports));
		 }
		 catch(Exception se){
				LOGGER.error("The maximum length of cell contents (text) is 32,767 characters");
				LOGGER.error("Error while writing to response outputstream for tenant IO for the transaction : ", se);
				if (se.getLocalizedMessage() != null) {
					addWorkbookToZipFile(modelOutputFilename, se.getLocalizedMessage().getBytes(), zos);
				} else {
					addWorkbookToZipFile(modelOutputFilename, se.getMessage().getBytes(), zos);
				}
			}
		setResponseHeaderForZip(response, zipFileName);
		try {
			if(tenantInputWorkbook != null) {
			addWorkbookToZipFile(tenantInputFilename, getBytes(tenantInputWorkbook), zos);
			} 
			if(tenantOutputWorkbook != null) {
			addWorkbookToZipFile(tenantOutputFilename, getBytes(tenantOutputWorkbook), zos);
			}
			if(modelInputWorkbook != null){
			addWorkbookToZipFile(modelInputFilename, getBytes(modelInputWorkbook), zos);
			}
			if(modelOutputWorkbook != null) {
			addWorkbookToZipFile(modelOutputFilename, getBytes(modelOutputWorkbook), zos);
			}
			} finally {
			zos.finish();
			zos.close();
		}
		response.getOutputStream().flush();
	}

}