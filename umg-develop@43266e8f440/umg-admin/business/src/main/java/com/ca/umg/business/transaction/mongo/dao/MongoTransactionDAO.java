package com.ca.umg.business.transaction.mongo.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.mongodb.Cursor;

/**
 * @author basanaga
 * 
 */
public interface MongoTransactionDAO {

    /**
     * This is used to get the Transaction Document based on transaction Id
     * 
     * @param txnId
     * @return
     */
    public TransactionDocument getTenantAndModelIO(String txnId) throws SystemException;

    public Page<TransactionDocument> searchTransactions(KeyValuePair<String, String> query, int page, int pageSize,
            String sortOn, boolean sortDesc, List<String> criteiraFields, final boolean emptySreach,
            final Boolean isApiSearch, TransactionFilterForApi transactionFilterForApi) throws SystemException, BusinessException;
    
    public Page<TransactionDocument> searchDefaultTransactions(Integer pageSize) throws SystemException, BusinessException;
    
    public List<String> fetchDistinctVersionName() throws SystemException;

	Cursor fetchTransactionCount(Long runAsOfDate)throws SystemException;

	Cursor getSuccessFailTransactionCount(Long startRunAsOfDate, Long endRunAsOfDate,String tenantCode)throws SystemException;

	Long fetchTransactionInDays(Long runAsOfDate)throws SystemException;
	
	Cursor getStatusMetricsTransactionCount(Long startRunAsOfDate, Long endRunAsOfDate,String tenantCode)throws SystemException;
	
	Cursor getUsageDynamicsDetails(Long startRunAsOfDate, Long endRunAsOfDate,String tenantCode)throws SystemException;

	Cursor getUsageDynamicsDetails(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode, String groupBy, String selectionType);

	Cursor getAllTntUsageDynamicsDetails(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode, String groupBy,
			String selectionType);

	Cursor getSelectedUsageDynamicsDetails(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode,
			String groupBy, String selectionType);

	Cursor getUsageDynamicsGrid(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode);
}
