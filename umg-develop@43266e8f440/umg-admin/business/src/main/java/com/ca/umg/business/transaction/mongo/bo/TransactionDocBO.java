/**
 * 
 */
package com.ca.umg.business.transaction.mongo.bo;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.BasicSearchCriteria;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;

/**
 * @author kamathan
 *
 */
public interface TransactionDocBO {

    public KeyValuePair<String, String> generateTransactionDocQuery(List<BasicSearchCriteria> basicSearchCriterias,
            List<BasicSearchCriteria> nestedSearchCriterias, 
            AdvanceTransactionFilter advanceTransactionFilter) throws BusinessException, SystemException;

    public Page<TransactionDocument> getTransactionDocuments(KeyValuePair<String, String> query, int pageNumber, int pageSize,
            String sortColumn, boolean sortDescending, List<String> criteriaFields, final boolean emptySearch,
            final Boolean isApiSearch, TransactionFilterForApi transactionFilterForApi) throws BusinessException, SystemException;

    public Page<TransactionDocument> getDefaultTransactionDocuments(Integer pageSize) throws BusinessException, SystemException;
}
