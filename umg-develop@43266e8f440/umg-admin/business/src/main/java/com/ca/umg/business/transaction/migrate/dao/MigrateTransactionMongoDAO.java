package com.ca.umg.business.transaction.migrate.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;

public class MigrateTransactionMongoDAO {

	private MongoOperations mongoOps;
	
	public MigrateTransactionMongoDAO () {
		super();
	}

	public MigrateTransactionMongoDAO(MongoOperations mongoOps) {
		this.mongoOps = mongoOps;
	}
	
	/**
	 * inserts the transaction document in to mongo collection of tenant 
	 * @param transactionDocument
	 */
	public void insertTransactionData (TransactionDocument transactionDocument) {
		String tenantCode = RequestContext.getRequestContext().getTenantCode();
		mongoOps.insert(transactionDocument, tenantCode+"_documents");
	}
	
	/**
	 * removes the inserted transaction from mongo collection
	 * @param transId
	 */
	public void removeInsertedTranData (String transId) {
		String tenantCode = RequestContext.getRequestContext().getTenantCode();
		Query deleteQuery = new Query(Criteria.where("transactionId").is(transId));
		mongoOps.remove(deleteQuery, tenantCode);
	}

    /**
     * get the document for the transactionId if it is present
     * 
     * @param transId
     * @return TransactionDocument
     */
    public TransactionDocument findOne(String transId) {
        String tenantCode = RequestContext.getRequestContext().getTenantCode();
        Query findQuery = new Query(Criteria.where("transactionId").is(transId));
        return mongoOps.findOne(findQuery, TransactionDocument.class, tenantCode + "_documents");
    }
}
