package com.ca.umg.business.transaction.migrate.delegate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.tenant.bo.TenantBO;
import com.ca.umg.business.transaction.migrate.bo.MigrateTransactionBO;
import com.ca.umg.business.transaction.migrate.execution.MigrateTransactionTask;
import com.ca.umg.business.transaction.migrate.execution.StopMigrateTransaction;
import com.ca.umg.business.transaction.migrate.execution.TaskExecutorPool;
import com.ca.umg.business.transaction.migrate.listener.MigrateTransactionListener;

@Named
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class MigrateTransactionDelegateImpl implements MigrateTransactionDelegate {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MigrateTransactionDelegateImpl.class);

	@Inject
	private MigrateTransactionBO blobMovementUtilBO; 
	
	@Inject
	private TenantBO tenantBO;
	
	@Inject 
	private CacheRegistry cacheRegistry;
	
	@Inject
    private StopMigrateTransaction stopMigrateTransaction;
	
	@Override
	public List<Map<String,Object>> moveBlobsOfAllTransactions() {
		List<String> tntList = null;
		String currTenantCode = null;
		List<Map<String,Object>> resultList = null;
		List<Future<Map<String,Object>>> finalList = null;
		List<MigrateTransactionTask> tasks = null;
		TaskExecutorPool blobMovementExecuterPool =  null;
		try{
			checkAndUpdateInCache();
			currTenantCode = RequestContext.getRequestContext().getTenantCode();
			RequestContext.getRequestContext().setAdminAware(true);
			tntList = tenantBO.getListOfTenantCodes();
			RequestContext.getRequestContext().setAdminAware(false);
			if (CollectionUtils.isEmpty(tntList)) {
				LOGGER.info("No Tenants found for migration : ");
				resultList = new ArrayList<>();
				Map<String,Object> errorMap = new HashMap<>();
				errorMap.put("status", "No tenants found");
				resultList.add(errorMap);
			} else {
				blobMovementExecuterPool = new TaskExecutorPool( tntList.size());
				finalList = new ArrayList<>();
				resultList = new ArrayList<>();
				tasks = new ArrayList<>();
				for (String tntCode : tntList) {	
					tasks.add(new MigrateTransactionTask(tntCode,blobMovementUtilBO));
				}
				
				finalList = blobMovementExecuterPool.runTask(tasks);
				for(Future<Map<String,Object>> fut : finalList){
					resultList.add(fut.get());
		        }
			}
		} catch (BusinessException bex) {
			LOGGER.error("Exception occured while start/stop of migration : "+bex);
			if (resultList == null) {
				resultList = new ArrayList<>();
			}
			Map<String,Object> errorMap = new HashMap<>();
			errorMap.put("status", bex.getLocalizedMessage());
			resultList.add(errorMap);
		} catch (Exception e) { // NOPMD
			LOGGER.error("error in getting the tnt list : "+e);
			if (resultList == null) {
				resultList = new ArrayList<>();
			}
			Map<String,Object> errorMap = new HashMap<>();
			errorMap.put("status", "error in getting the tnt list : "+e.getMessage());
			resultList.add(errorMap);
		} finally {
			if(blobMovementExecuterPool != null) {
				blobMovementExecuterPool.shutDown();
			}
			if (StringUtils.isNotEmpty(currTenantCode)) {
				RequestContext.getRequestContext().setTenantCode(currTenantCode);
			}	
		}
		LOGGER.info("Returning the final result from MigrateTransactionDelegateImpl::moveBlobsOfAllTransactions ");
		return resultList;
	}
	
	private void checkAndUpdateInCache () throws BusinessException {
		 InetAddress iAddress;
		try {
			iAddress = InetAddress.getLocalHost();
			String currentIp = iAddress.getHostAddress();
			LOGGER.info("In MigrateTransactionDelegateImpl::checkAndUpdateInCache: "+currentIp);
			if (cacheRegistry.getMap(MigrateTransactionListener.MIGRATE_TRANSACTION).get(BusinessConstants.MIGRATION_STARTED) != null) {
				//raise business exception for already started migration
				LOGGER.info("Migratiopn has already been started on machine : "+currentIp);
				BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000651, new Object[] {});
			} else {
				LOGGER.info("Adding the start migration to cache");
				cacheRegistry.getMap(MigrateTransactionListener.MIGRATE_TRANSACTION).put(BusinessConstants.MIGRATION_STARTED,currentIp);
			}
		} catch (UnknownHostException e) {
			LOGGER.error("error in getting the host when starting migration : "+e);
		}
	}
	
	public void stopTransactionMigration() throws BusinessException {
		LOGGER.info("In MigrateTransactionDelegateImpl::stopTransactionMigration: ");
		try {
			if (cacheRegistry.getMap(MigrateTransactionListener.MIGRATE_TRANSACTION).get(BusinessConstants.MIGRATION_STARTED) != null) {
				String ipAddressFrmCache = (String) cacheRegistry.getMap(MigrateTransactionListener.MIGRATE_TRANSACTION).get(BusinessConstants.MIGRATION_STARTED);
				InetAddress iAddress;
					iAddress = InetAddress.getLocalHost();
					String currentIp = iAddress.getHostAddress();
					if (StringUtils.equals(ipAddressFrmCache, currentIp)) {
						LOGGER.info("Setting the stop migration flag MigrateTransactionDelegateImpl::stopTransactionMigration");
						stopMigrateTransaction.setStopMigration(Boolean.TRUE);
					} else {
						LOGGER.info("raising an event for setting the stop migration flag ");
						cacheRegistry.getTopic(MigrateTransactionListener.MIGRATE_TRANSACTION).publish(Boolean.TRUE);
					}
			} else {
				//raise business exception for transaction not started
				LOGGER.error("Raising business exception as the Transaction movement has not started");
				BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000652, new Object[] {});
			}
		} catch (UnknownHostException e) {
			LOGGER.error("error in getting the host when stopping migration : "+e);
		}
	}
}
