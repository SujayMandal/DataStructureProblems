package com.ca.umg.business.transaction.migrate.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.transaction.migrate.dao.MigrateTransactionDAO;
import com.ca.umg.business.transaction.migrate.dao.MigrateTransactionMongoDAO;
import com.ca.umg.business.transaction.migrate.execution.StopMigrateTransaction;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;

@Named
public class MigrateTransactionBOImpl implements MigrateTransactionBO {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MigrateTransactionBOImpl.class);
	
	private static final String STATUS = "Status";
	
	@Inject
	private MigrateTransactionDAO transactionBlobDAO;
	
	@Autowired
	private MigrateTransactionMongoDAO mongoDAO;
	
	@Inject
	private StopMigrateTransaction stopMigrateTransaction; 
	
	@Override
	public Map<String,Object> moveBlobsOfRuntmTransactions() {
		List<String> mysqlTranIdList = null;
		Map<String,Object> errorMap = null;
		Boolean insrtUpdtStatus = Boolean.FALSE;
		Integer successCount = 0;
		Integer failureCount = 0;
		
		try {
			mysqlTranIdList = transactionBlobDAO.getAllRtmTranIds();
			
			if (CollectionUtils.isEmpty(mysqlTranIdList)) {
				LOGGER.info("No Transactions found for Blob movement for tenant: "+RequestContext.getRequestContext().getTenantCode());
				errorMap = new HashMap<>();
				errorMap.put(STATUS, "No Transactions found for Blob movement");
			} else {
				LOGGER.info("Number of Transactions {} found for Blob movement for tenant : {} ",mysqlTranIdList.size() ,RequestContext.getRequestContext().getTenantCode());
				errorMap = new HashMap<>();
				for (String tranId : mysqlTranIdList) {
					if (!stopMigrateTransaction.getStopMigration()) {
                        LOGGER.debug("mvmt started for transactionId : " + tranId);
						insrtUpdtStatus = insertIntoMongoAndUpdateMysql(errorMap,tranId);
                        LOGGER.debug("mvmt ended for transactionId : {} and status is : {} ", tranId, insrtUpdtStatus);
						if (insrtUpdtStatus) {
							successCount++;
						} else {
							failureCount++;
						}
					} else {
                        LOGGER.info("stopping the migration as stop command was received MigrateTransactionBOImpl::moveBlobsOfRuntmTransactions : " + tranId);
						break;
					}
				}
				
				if (MapUtils.isEmpty(errorMap)) {
                    LOGGER.info("total success count "+successCount);
					errorMap.put(STATUS, "Blob movement successful for : "+successCount+" transactionId(s)");
				} else {
					LOGGER.info("total success count : {} and failure count : {}",successCount,failureCount);
					errorMap.put(STATUS, "Blob movement successful for : "+successCount+" "
							+ "transactionId(s) and failed for : "+ failureCount +
							" transactionId(s). Failed transactions are shown with details");
				}
			}
		} catch (Exception e) { // NOPMD
			LOGGER.error("moveBlobsOfRuntmTransactions :: "+e);
			if (errorMap == null) {
				errorMap = new HashMap<>();
				errorMap.put(STATUS,e.getMessage());
			}
		}
		return errorMap;
	}
	
	/**
	 * inserts the blobs fetched from mysql into mongo and marks inserted blobs as null in mysql table
	 * @param errorMap
	 * @param TranId
	 * @return success/failure flag based on state of insert & update in mongo & mysql
	 */
    private Boolean insertIntoMongoAndUpdateMysql(Map<String, Object> erroMap, String tranId) {
        // Some code has been commented as part of the fix : UMG-3242
        // Boolean updateMysqlSuccess = Boolean.FALSE;
		TransactionDocument transactionBlobInfo = null;
		Boolean status = Boolean.FALSE;
		try {
            // updateMysqlSuccess = Boolean.FALSE;
            LOGGER.info("getting blobs from mysql : " + tranId);
			transactionBlobInfo = transactionBlobDAO.getBlobsOfRuntmTransaction(tranId);
            // updateMysqlSuccess = Boolean.TRUE;
            LOGGER.info("inserting blobs to mongo : " + tranId);
            TransactionDocument doc = mongoDAO.findOne(tranId);
            if (doc == null) {
                mongoDAO.insertTransactionData(transactionBlobInfo);
            }
            // Fixed as part of not to update the blobs to null
            /*
             * updateMysqlSuccess = Boolean.FALSE; LOGGER.info("updating status to mysql : " + tranId); updateMysqlSuccess =
             * transactionBlobDAO.updateBlobAsNull(tranId);
             */
			status = Boolean.TRUE;
		} catch (Exception e) { // NOPMD
			LOGGER.error(" Error in getBlobsOfAllRuntmTransactions for transaction id "+e);
            /*
             * if (!updateMysqlSuccess) {
             * LOGGER.error("update failed for mysql update removing transaction from mongo for transaction id " + tranId);
             * mongoDAO.removeInsertedTranData(tranId); }
             */
			erroMap.put(tranId, e.getMessage());
		}
		return status;
	}
}
