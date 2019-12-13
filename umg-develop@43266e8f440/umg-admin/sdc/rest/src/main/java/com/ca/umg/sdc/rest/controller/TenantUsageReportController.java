package com.ca.umg.sdc.rest.controller;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static com.ca.umg.sdc.rest.constants.RestConstants.CONTROLLER_CANCELLED_MESSAGE;
import static com.ca.umg.sdc.rest.constants.RestConstants.CONTROLLER_DONE_MESSAGE;
import static com.ca.umg.sdc.rest.constants.RestConstants.NO_TRANSACTION_RECORDS_FOUND;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;
import com.ca.umg.business.tenant.report.usage.UsageSearchRequestCancel;
import com.ca.umg.business.tenant.report.usage.UsageTransactionWrapper;
import com.ca.umg.business.tenant.report.usage.bo.UsageExcelReport;
import com.ca.umg.business.tenant.report.usage.delegate.TenantUsageReportDelegate;
import com.ca.umg.sdc.rest.utils.RestResponse;

@SuppressWarnings("PMD")
@Controller
@RequestMapping("/tenantUsage")
public class TenantUsageReportController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TenantUsageReportController.class);

	@Inject
	private TenantUsageReportDelegate tenantUsageReportDelegate;

	@RequestMapping(value = "/getAllModelNames", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<String>> getAllUniqueModelNames() {
		final String tenantCode = getRequestContext().getTenantCode();
		final RestResponse<List<String>> response = new RestResponse<>();
		final List<String> allModelNames = tenantUsageReportDelegate.getAllUniqueModels(tenantCode);
		if (allModelNames != null) {
			response.setResponse(allModelNames);
		} else {
			LOGGER.debug("Models are not found");
			response.setMessage("Model Names not found");
		}
		return response;
	}

	@RequestMapping(value = "/getAllModelVersion", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<String>> getAllUniqueModelVersions(@RequestParam("tenantModelName") final String tenantModelName) {
		final String tenantCode = getRequestContext().getTenantCode();
		final RestResponse<List<String>> response = new RestResponse<>();
		final List<String> allModelVersions = tenantUsageReportDelegate.getAllUniqueModelVersion(tenantCode, tenantModelName);
		if (allModelVersions != null) {
			response.setResponse(allModelVersions);
		} else {
			LOGGER.debug("Model '" + tenantModelName + "' version are not found");
			response.setMessage("Model Version not found");
		}
		return response;
	}

	@RequestMapping(value = "/filterTransactions", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<UsageTransactionWrapper> filterTransactions(@RequestBody final UsageReportFilter filter) {
		final RestResponse<UsageTransactionWrapper> response = new RestResponse<UsageTransactionWrapper>();
		UsageTransactionWrapper usageTransactionWrapper = null;
		try{
			final String tenantCode = getRequestContext().getTenantCode();
			filter.setTenantCode(tenantCode);
			filter.setSearchString(null);
			usageTransactionWrapper = tenantUsageReportDelegate.filterTransactions(filter);
			response.setError(false);
			if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(filter.getCancelRequestId())) {
				response.setMessage(CONTROLLER_DONE_MESSAGE);
				if (usageTransactionWrapper != null && isEmpty(usageTransactionWrapper.getTransactionInfoList())) {
					response.setMessage(NO_TRANSACTION_RECORDS_FOUND);
				}
				response.setResponse(usageTransactionWrapper);
			} else {
				response.setMessage(CONTROLLER_CANCELLED_MESSAGE);
			}
		}catch(BusinessException | SystemException e){
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/downloadUsageReportByFilter")
	@ResponseBody
	public void downloadUsageReportByFilter(
			@RequestParam("tenantModelName") final String tenantModelName,
			@RequestParam("fullVersion") final String fullVersion,
			@RequestParam("runAsOfDateFromString") final String runAsOfDateFromString,
			@RequestParam("runAsOfDateToString") final String runAsOfDateToString,
			@RequestParam("transactionStatus") final String transactionStatus,
			@RequestParam("isCustomDate") final boolean customDate,
			@RequestParam("cancelRequestId") final String cancelRequestId,
			@RequestParam("sortColumn") final String sortColumn,
			@RequestParam("descending") final boolean descending,
			@RequestParam("includeTest") final boolean includeTest,
			final HttpServletResponse response) {
		UsageReportFilter usageReportFilter = new UsageReportFilter();
		usageReportFilter.setTenantModelName(checkForNull(tenantModelName));
		usageReportFilter.setTransactionStatus(checkForNull(transactionStatus));
		usageReportFilter.setRunAsOfDateToString(checkForNull(runAsOfDateToString));
		usageReportFilter.setRunAsOfDateFromString(checkForNull(runAsOfDateFromString));
		usageReportFilter.setTenantModelName(checkForNull(tenantModelName));
		usageReportFilter.setFullVersion(checkForNull(fullVersion));
		usageReportFilter.setIncludeTest(includeTest);
		usageReportFilter.setPage(-1);
		final String tenantCode = getRequestContext().getTenantCode();
		usageReportFilter.setTenantCode(tenantCode);
		usageReportFilter.setCancelRequestId(checkForNull(cancelRequestId));
		usageReportFilter.setCustomDate(customDate);
		usageReportFilter.setSortColumn(sortColumn);
		usageReportFilter.setDescending(descending);
		usageReportFilter.setSearchString(null);

		try {
			LOGGER.info("UsageReportFilter:" + usageReportFilter);
			final SqlRowSet sqlRowSet = tenantUsageReportDelegate.loadTransactionsRowSetByFilter(usageReportFilter);
			final UsageExcelReport report = new UsageExcelReport(sqlRowSet);
			if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(usageReportFilter.getCancelRequestId())) {
				final String reportFileName = report
						.getReportFileName(tenantCode, usageReportFilter.getRunAsOfDateFrom(), usageReportFilter.getRunAsOfDateTo());
				setResponseProperties(response, reportFileName);
				report.createReport(response.getOutputStream());
				response.flushBuffer();
			}
		} catch (IOException se) {
			LOGGER.error("Error while writing Tenant Usage Report to response outputstream ", se);
		} catch (SystemException exception) {
			LOGGER.error("Error while writing to response outputstream for tenant/model IO data  ", exception);
		} catch (BusinessException be) {
			LOGGER.error("Error while dowloading batch report writing to response outputstream for tenant IO for the transaction : ", be);
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				LOGGER.error("Error while closing response outputstream for tenant IO data for the transaction : " + e);
			}
		}
	}

	private String checkForNull(final String value){
		if(value.equalsIgnoreCase("null")||value.isEmpty()){
			return null;
		}else{
			return value;
		}
	}

	@RequestMapping(value = "/canceSearchlRequest", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<String> canceSearchlRequest(@RequestParam("cancelRequestId") final String cancelRequestId) {
		LOGGER.debug("Cancel Request Id : " + cancelRequestId);
		final RestResponse<String> response = new RestResponse<String>();
		try {
			tenantUsageReportDelegate.updateUsageSearchRequestCancel(cancelRequestId, true);
		} catch (BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}

		response.setMessage("Cancelled Request : " + cancelRequestId);
		return response;
	}

	@RequestMapping(value = "/createSearchRequest", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<String> createSearchRequest() {
		final RestResponse<String> response = new RestResponse<String>();
		try {
			final UsageSearchRequestCancel usageSearchRequestCancel = tenantUsageReportDelegate.createUsageSearchRequestCancel();
			response.setError(false);
			response.setMessage(CONTROLLER_DONE_MESSAGE);
			response.setResponse(usageSearchRequestCancel.getId());
		} catch (BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	private void setResponseProperties(final HttpServletResponse response, final String reportFileName) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=" + reportFileName);
	}

	@RequestMapping(value = "/searchTransactions", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<UsageTransactionWrapper> searchTransactions(@RequestBody final UsageReportFilter filter) {
		final RestResponse<UsageTransactionWrapper> response = new RestResponse<UsageTransactionWrapper>();
		UsageTransactionWrapper usageTransactionWrapper = null;
		try {
			final String tenantCode = getRequestContext().getTenantCode();
			filter.setFullVersion(null);
			filter.setRunAsOfDateFromString(null);
			filter.setRunAsOfDateToString(null);
			filter.setTenantModelName(null);
			filter.setTransactionStatus(null);
			filter.setTenantCode(tenantCode);
			usageTransactionWrapper = tenantUsageReportDelegate.searchTransactions(filter);
			response.setError(false);
			response.setMessage(CONTROLLER_DONE_MESSAGE);
			if (usageTransactionWrapper != null && isEmpty(usageTransactionWrapper.getTransactionInfoList())) {
				response.setMessage(NO_TRANSACTION_RECORDS_FOUND);
			}
			response.setResponse(usageTransactionWrapper);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/downloadUsageReportBySearch")
	@ResponseBody
	public void downloadUsageReportBySearch(@RequestParam("searchString") final String searchString,
			@RequestParam("cancelRequestId") final String cancelRequestId, @RequestParam("sortColumn") final String sortColumn,
			@RequestParam("descending") final boolean descending, final HttpServletResponse response) {
		UsageReportFilter usageReportFilter = new UsageReportFilter();
		usageReportFilter.setSearchString(checkForNull(searchString));
		usageReportFilter.setPage(-1);
		final String tenantCode = getRequestContext().getTenantCode();
		usageReportFilter.setTenantCode(tenantCode);
		usageReportFilter.setCancelRequestId(checkForNull(cancelRequestId));
		usageReportFilter.setSortColumn(sortColumn);
		usageReportFilter.setDescending(descending);

		try {
			LOGGER.info("UsageReportFilter:" + usageReportFilter);
			final SqlRowSet sqlRowSet = tenantUsageReportDelegate.loadTransactionsRowSetBySearch(usageReportFilter);
			final UsageExcelReport report = new UsageExcelReport(sqlRowSet);
			if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(usageReportFilter.getCancelRequestId())) {
				Workbook wb = report.createReport();
				final String reportFileName = report.getReportFileName(tenantCode, report.getMinCreatedDate(), report.getMaxCreatedDate());
				setResponseProperties(response, reportFileName);
				wb.write(response.getOutputStream());
				response.flushBuffer();
			}
		} catch (IOException se) {
			LOGGER.error("Error while writing Tenant Usage Report to response outputstream ", se);
		} catch (SystemException exception) {
			LOGGER.error("Error while writing to response outputstream for tenant/model IO data  ", exception);
		} catch (BusinessException be) {
			LOGGER.error("Error while dowloading batch report writing to response outputstream for tenant IO for the transaction : ", be);
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				LOGGER.error("Error while closing response outputstream for tenant IO data for the transaction : " + e);
			}
		}
	}

	@RequestMapping(value = "/downloadUsageReportByTransactionList", method = RequestMethod.POST)
	@ResponseBody
	public void downloadUsageReportByTransactionList(@RequestParam("selectedTransactionList") final List<String> selectedTransactionList,
			@RequestParam("cancelRequestId") final String cancelRequestId, @RequestParam("sortColumn") final String sortColumn,
			@RequestParam("descending") final boolean descending, final HttpServletResponse response) {
		UsageReportFilter usageReportFilter = new UsageReportFilter();
		usageReportFilter.setSelectedTransactions(selectedTransactionList);
		usageReportFilter.setPage(-1);
		final String tenantCode = getRequestContext().getTenantCode();
		usageReportFilter.setTenantCode(tenantCode);
		usageReportFilter.setCancelRequestId(checkForNull(cancelRequestId));
		usageReportFilter.setSortColumn(sortColumn);
		usageReportFilter.setDescending(descending);
		usageReportFilter.setTenantModelName(null);
		usageReportFilter.setSearchString(null);

		try {
			LOGGER.info("UsageReportFilter:" + usageReportFilter);
			final SqlRowSet sqlRowSet = tenantUsageReportDelegate.loadTransactionsRowSetByTransactionList(usageReportFilter);
			final UsageExcelReport report = new UsageExcelReport(sqlRowSet);
			if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(usageReportFilter.getCancelRequestId())) {
				Workbook wb = report.createReport();
				final String reportFileName = report.getReportFileName(tenantCode, report.getMinCreatedDate(), report.getMaxCreatedDate());
				setResponseProperties(response, reportFileName);
				wb.write(response.getOutputStream());
				response.flushBuffer();
			}
		} catch (IOException se) {
			LOGGER.error("Error while writing Tenant Usage Report to response outputstream ", se);
		} catch (SystemException exception) {
			LOGGER.error("Error while writing to response outputstream for tenant/model IO data  ", exception);
		} catch (BusinessException be) {
			LOGGER.error("Error while dowloading batch report writing to response outputstream for tenant IO for the transaction : ", be);
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				LOGGER.error("Error while closing response outputstream for tenant IO data for the transaction : " + e);
			}
		}
	}
	@RequestMapping(value = "/downloadExecReportByTransactionList", method = RequestMethod.POST)
	@ResponseBody
	public void downloadExeReportByTransactionList(@RequestParam("selectedTransactionList") final List<String> selectedTransactionList,
			@RequestParam("cancelRequestId") final String cancelRequestId, @RequestParam("sortColumn") final String sortColumn,
			@RequestParam("descending") final boolean descending, final HttpServletResponse response) {
		UsageReportFilter usageReportFilter = new UsageReportFilter();
		usageReportFilter.setSelectedTransactions(selectedTransactionList);
		usageReportFilter.setPage(-1);
		final String tenantCode = getRequestContext().getTenantCode();
		usageReportFilter.setTenantCode(tenantCode);
		usageReportFilter.setCancelRequestId(checkForNull(cancelRequestId));
		usageReportFilter.setSortColumn(sortColumn);
		usageReportFilter.setDescending(descending);
		usageReportFilter.setTenantModelName(null);
		usageReportFilter.setSearchString(null);

		try {
			LOGGER.info("UsageReportFilter:" + usageReportFilter);
			final SqlRowSet sqlRowSet = tenantUsageReportDelegate.loadTransactionsRowSetByTransactionList(usageReportFilter);
			final UsageExcelReport report = new UsageExcelReport(sqlRowSet);
			if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(usageReportFilter.getCancelRequestId())) {
				Workbook wb = report.createExeReport();
				final String reportFileName = report.getReportFileName(tenantCode, report.getMinCreatedDate(), report.getMaxCreatedDate());
				setResponseProperties(response, reportFileName);
				wb.write(response.getOutputStream());
				response.flushBuffer();
			}
		} catch (IOException se) {
			LOGGER.error("Error while writing Tenant Usage Report to response outputstream ", se);
		} catch (SystemException exception) {
			LOGGER.error("Error while writing to response outputstream for tenant/model IO data  ", exception);
		} catch (BusinessException be) {
			LOGGER.error("Error while dowloading batch report writing to response outputstream for tenant IO for the transaction : ", be);
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				LOGGER.error("Error while closing response outputstream for tenant IO data for the transaction : " + e);
			}
		}
	}
}