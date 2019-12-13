package com.ca.umg.me2.dao;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.modelet.ModeletClientInfo;
import com.mongodb.DuplicateKeyException;
/**
 * @author basanaga
 * This method  used to update the transaction status to In Execution
 */
@Named
@SuppressWarnings("PMD")
public class MongoTransactionLogDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoTransactionLogDAO.class);
	
    @Inject
    private MongoTemplate mongoTemplate;

    /**
     * 
     * @param txnId
     */
    public void upsertRequestTransactionLogToMongo(final String txnId,ModeletClientInfo modeletDetails) {
    	LOGGER.info("changing the status from Queued to In Execution for transactionId : "+ txnId);
        String tenantCode = RequestContext.getRequestContext().getTenantCode();
        Query query = new Query(Criteria.where("transactionId").is(txnId));
        Update update = new Update().set("status", TransactionStatus.IN_EXECUTION.getStatus()).set("modeletServerHost",modeletDetails.getHost()).set("modeletServerPort",modeletDetails.getPort()).set("modeletPoolName",modeletDetails.getPoolName()).set("transactionId", txnId);       
        try {
            mongoTemplate.upsert(query, update, tenantCode + "_documents");
        	LOGGER.info("changed successfully the status from Queued to In Execution for transactionId : "+ txnId);
        } catch (DuplicateKeyException dke) {//NOPMD
            LOGGER.error("GOT Duplicate Key error, hence updating collection");
            try {
                   mongoTemplate.updateFirst(query, update, tenantCode + "_documents");
            } catch (Exception e) {//NOPMD
	            LOGGER.error("GOT error while updating updating collection", e);                   
            }
      } catch (org.springframework.dao.DuplicateKeyException dke) {
          LOGGER.error("GOT Duplicate Key error, hence updating collection");
          try {
                 mongoTemplate.updateFirst(query, update, tenantCode + "_documents");
          } catch (Exception e) {//NOPMD
	            LOGGER.error("GOT error while updating updating collection", e);                   
          }    	  
      }
    }
}