package com.ca.umg.sdc.rest.controller;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.transaction.migrate.delegate.MigrateTransactionDelegate;
import com.ca.umg.business.transaction.migrate.execution.StopMigrateTransaction;
import com.ca.umg.business.transaction.migrate.listener.MigrateTransactionListener;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("migrateTransactions")
public class MigrateTransactionController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MigrateTransactionController.class);

	@Inject
	private MigrateTransactionDelegate migrateTransactionDelegate;
	
	@Inject
    private StopMigrateTransaction stopMigrateTransaction;
	
	@Inject 
	private CacheRegistry cacheRegistry;

	@RequestMapping(value = "/start", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<Map<String,Object>>> startMigrateTransactions() {
		RestResponse<List<Map<String,Object>>> response = new RestResponse<List<Map<String,Object>>>();
		List<Map<String,Object>> resultList = null;
		try {
			resultList = migrateTransactionDelegate.moveBlobsOfAllTransactions();
			response.setError(false);
			response.setResponse(resultList);
			LOGGER.info("Removing the entry from cache MigrateTransactionController::startMigrateTransactions");
			cacheRegistry.getMap(MigrateTransactionListener.MIGRATE_TRANSACTION).remove(BusinessConstants.MIGRATION_STARTED);
			LOGGER.info("call to reset the stop migration flag  MigrateTransactionController::startMigrateTransactions");
			stopMigrateTransaction.setStopMigration(Boolean.FALSE);
		} catch (Exception se) { //NOPMD
			LOGGER.error(se.getLocalizedMessage(), se);
			response.setError(true);
			response.setMessage(se.getMessage());
		}
		return response;
	}
	
	@RequestMapping(value = "/stop", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> stopMigrateTransactions() {
		RestResponse<String> response = new RestResponse<String>();
		try {
			response.setError(false);
			response.setResponse("Stopping Transaction Migration....");
			migrateTransactionDelegate.stopTransactionMigration();
		} catch(BusinessException bex) {
			response.setResponse("Stopping Transaction Migration failed : "+bex.getLocalizedMessage());
		}catch (Exception se) { //NOPMD
			LOGGER.error(se.getLocalizedMessage(), se);
			response.setError(true);
			response.setMessage(se.getMessage());
		}
		return response;
	}
}
