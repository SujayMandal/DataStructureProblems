/**
 * 
 */
package com.ca.umg.sdc.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.dashboard.delegate.DashBoardDelegate;
import com.ca.umg.business.dashboard.info.ModelUsagePattern;
import com.ca.umg.business.dashboard.info.ModelVersionStatus;
import com.ca.umg.business.dashboard.info.SyndicateDataStatistic;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * @author nigampra
 * 
 */

@Controller
@RequestMapping("/businessDashboard")
public class BusinessDashboardController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BusinessDashboardController.class);

	@Inject
	private DashBoardDelegate dashBoardDelegate;
	
	private static final String MILLISECOUND = " ms";

	@RequestMapping(value = "/listAllUniqueModels", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<String>> listAllUniqueModels() {
		LOGGER.info("Fetching unique model list");
		RestResponse<List<String>> response = new RestResponse<>();
		long startTime = System.currentTimeMillis();
		try {
			response.setResponse(dashBoardDelegate.getUniqueModelNames());
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken to fetch all Unique Models At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}

	@RequestMapping(value = "/modelStatistics", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<ModelVersionStatus>> getModelStatistics() {
		LOGGER.info("Fetching model statistics");
		RestResponse<List<ModelVersionStatus>> response = new RestResponse<>();
		long requetStartTime = System.currentTimeMillis();
		try {
			response.setResponse(dashBoardDelegate.getVersionStats());
			response.setMessage("Done");
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long requetEndTime = System.currentTimeMillis();
		LOGGER.info("Time Taken to fetch model statistics At Rest Layer "+(requetEndTime-requetStartTime)+MILLISECOUND);
		return response;
	}

	@RequestMapping(value = "/transactionCount/{days}", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<Long> getTransactionCount(
			@PathVariable("days") Integer days) {
		LOGGER.info("Fetching transactions count");
		long startTime = System.currentTimeMillis();
		RestResponse<Long> response = new RestResponse<>();
		try {
			response.setResponse(dashBoardDelegate
					.getTransactionsCountForDay(days));
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To Fetch Transaction count At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}

	@RequestMapping(value = "/lookupData", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<SyndicateDataStatistic> getLookupDataStatistics() {
		RestResponse<SyndicateDataStatistic> response = new RestResponse<>();
		SyndicateDataStatistic stats = new SyndicateDataStatistic();
		long startTime = System.currentTimeMillis();
		try {
			stats.setActive(dashBoardDelegate.getActiveLookupData());
			stats.setExpired(0L);
			stats.setExpiring(dashBoardDelegate.getExpiringLookupData());
			response.setResponse(stats);
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To Fetch Look Up Data At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}

	@RequestMapping(value = "/successFailureCount", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<Map<String,Long>> getSuccessFailureCount(
			@RequestBody TransactionFilter txnFilter) {
		LOGGER.info("Fetching Success Failure Count");
		long startTime = System.currentTimeMillis();
		RestResponse<Map<String,Long>> response = new RestResponse<>();
		try {
			response.setResponse(dashBoardDelegate
					.getScsFailCntForTransactions(txnFilter));
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To Fetch Success Failure Count At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}
	
	
	@RequestMapping(value = "/statusMetrics", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<Map<String,Object>> getStatusMetrics(
			@RequestBody TransactionFilter txnFilter) {
		LOGGER.info("Fetching Status Metrics");
		long startTime = System.currentTimeMillis();
		RestResponse<Map<String,Object>> response = new RestResponse<>();
		try {
			response.setResponse(dashBoardDelegate
					.getStatusMetrics(txnFilter));
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To Fetch Status Metrics At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}
	
	@RequestMapping(value = "/usageDynamics", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<Map<String,Object>> getUsageDynamics(
			@RequestBody TransactionFilter txnFilter) {
		LOGGER.info("Fetching Usage Dynamics");
		long startTime = System.currentTimeMillis();
		RestResponse<Map<String,Object>> response = new RestResponse<>();
		try {
			response.setResponse(dashBoardDelegate
					.getUsageDynamics(txnFilter));
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To Fetch Usage Dynamics At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}
	@RequestMapping(value = "/getUsageDynamicsGrid", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<Object>> getUsageDynamicsGrid(
			@RequestBody TransactionFilter txnFilter) {
		LOGGER.info("Fetching UsageDynamicsGrid");
		long startTime = System.currentTimeMillis();
		RestResponse<List<Object>> response = new RestResponse<>();
		try {
			response.setResponse(dashBoardDelegate.getUsageDynamicsGrid(txnFilter));
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To fetch UsageDynamicsGrid At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}
	@RequestMapping(value = "/errorTxnList", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<ModelVersionStatus>> getFailTxnList(
			@RequestBody TransactionFilter txnFilter) {
		LOGGER.info("Fetching Error Txn List");
		long startTime = System.currentTimeMillis();
		RestResponse<List<ModelVersionStatus>> response = new RestResponse<>();
		try {
			response.setResponse(dashBoardDelegate
					.getErrorTxnList(txnFilter));
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To Fetch Eroor Txn List At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}
	
	@RequestMapping(value = "/usageTrendLine", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<Map<String,Object>> getRAUsageData(
			@RequestBody TransactionFilter txnFilter) {
		LOGGER.info("Fetching RA Usage Trend line Data");
		long startTime = System.currentTimeMillis();
		RestResponse<Map<String,Object>> response = new RestResponse<>();
		try {
			response.setResponse(dashBoardDelegate
					.getUsageTrendLineData(txnFilter));
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To Fetch RA Usage Trend Line Data At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}
	

	@RequestMapping(value = "/usageReport", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<ModelUsagePattern>> getModelUsageReport() {
		RestResponse<List<ModelUsagePattern>> response = new RestResponse<>();
		long startTime = System.currentTimeMillis();
		try {
			response.setResponse(dashBoardDelegate.getTransactionsCnt());
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time Taken To Fetch Usage Report At Rest Layer "+(endTime-startTime)+MILLISECOUND);
		return response;
	}

}
