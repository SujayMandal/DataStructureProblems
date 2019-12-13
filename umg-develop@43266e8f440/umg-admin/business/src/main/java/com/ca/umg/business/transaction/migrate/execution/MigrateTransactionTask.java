package com.ca.umg.business.transaction.migrate.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.transaction.migrate.bo.MigrateTransactionBO;

public class MigrateTransactionTask implements Callable<Map<String,Object>> {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MigrateTransactionTask.class);
	
	private final MigrateTransactionBO migrateTransactionBO;
	private final String tenantCode;

	public MigrateTransactionTask (String tntCode, MigrateTransactionBO migrtTransactionBO) {
		this.tenantCode = tntCode;
		this.migrateTransactionBO = migrtTransactionBO;
	}
	
	@Override
	public Map<String, Object> call() {
		Map<String,Object> errorMap = null;
		Map<String,Object> resultMap = null;
		Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        RequestContext requestContext = new RequestContext(properties);
		try {
			LOGGER.info("Transaction Movement started for tenant : "+requestContext.getTenantCode());
			resultMap = new HashMap<String, Object>();
	        errorMap = migrateTransactionBO.moveBlobsOfRuntmTransactions();
	        resultMap.put(tenantCode, errorMap);
	        LOGGER.info("Transaction Movement ended for tenant : "+requestContext.getTenantCode());
		} catch (Exception e) { //NOPMD
			LOGGER.error("Error occured in MigrateTransactionTask : call method : "+e);
			if (resultMap == null) {
				resultMap = new HashMap<String, Object>();
			}
			resultMap.put(tenantCode, e.getMessage());
		} finally {
			requestContext.destroy();
		}
		return resultMap;
	}

}
