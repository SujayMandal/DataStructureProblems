/**
 * 
 */
package com.ca.umg.business.transaction.mongo.bo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.BasicSearchCriteria;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.mongo.dao.MongoTransactionDAO;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.query.Operator;
import com.ca.umg.business.transaction.query.QueryStatement;
import com.ca.umg.business.transaction.query.generator.QueryParamGenerator;

/**
 * @author kamathan
 *
 */
@SuppressWarnings("PMD.NPathComplexity")
@Named
public class TransactionDocBOImpl implements TransactionDocBO {

    @Inject
    private QueryParamGenerator queryParamGenerator;

    @Inject
    private MongoTransactionDAO mongoTransactionDAO;
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.transaction.mongo.bo.MongoTransactionBo#getTransactionDocs()
     */
    @Override
    public KeyValuePair<String, String> generateTransactionDocQuery(List<BasicSearchCriteria> basicSearchCriterias,
            List<BasicSearchCriteria> nestedSearchCriterias,
            AdvanceTransactionFilter advanceTransactionFilter)
            throws BusinessException, SystemException {

        String basicQuery = null;

        String advancedQuery = null;

        QueryStatement finalQueryStatement = null;

        List<QueryStatement> combinedChildStatements = new ArrayList<QueryStatement>();

        if (CollectionUtils.isNotEmpty(basicSearchCriterias)) {
            combinedChildStatements.add(buildBasicQueryStatement(basicSearchCriterias));
        }
        if (CollectionUtils.isNotEmpty(nestedSearchCriterias)) {
            combinedChildStatements.add(buildNestedQueryStatement(nestedSearchCriterias));
        }      

        basicQuery = queryParamGenerator.generateQuery(prepareQueryStatement(Operator.AND, null, null, combinedChildStatements));

        QueryStatement advancedQueryStatement = null;

        if (advanceTransactionFilter != null) {
            List<QueryStatement> advancedChildStatements = new ArrayList<QueryStatement>();
            if (advanceTransactionFilter.getClause1() != null) {
                advancedChildStatements.add(prepareQueryStatement(advanceTransactionFilter.getClause1().getSearchOperator(),
                        advanceTransactionFilter.getClause1().getSearchKey(), advanceTransactionFilter.getClause1()
                                .getSearchValue(), null));
            }

            if (advanceTransactionFilter.getClause2() != null) {
                advancedChildStatements.add(prepareQueryStatement(advanceTransactionFilter.getClause2().getSearchOperator(),
                        advanceTransactionFilter.getClause2().getSearchKey(), advanceTransactionFilter.getClause2()
                                .getSearchValue(), null));

            }

            advancedQueryStatement = prepareQueryStatement(Operator.valueOf(StringUtils.isNotBlank(advanceTransactionFilter
                    .getCriteria()) ? advanceTransactionFilter.getCriteria() : "AND"), null, null, advancedChildStatements);

            combinedChildStatements.add(advancedQueryStatement);
        }

        finalQueryStatement = prepareQueryStatement(Operator.AND, null, null, combinedChildStatements);

        if (advancedQueryStatement != null) {
            advancedQuery = queryParamGenerator.generateQuery(finalQueryStatement);
        }

        return new KeyValuePair<String, String>(basicQuery, advancedQuery);

    }

    private QueryStatement buildBasicQueryStatement(List<BasicSearchCriteria> basicSearchCriterias) {
        List<QueryStatement> basicQueryStatements = null;

        if (CollectionUtils.isNotEmpty(basicSearchCriterias)) {
            basicQueryStatements = new ArrayList<QueryStatement>();
            for (BasicSearchCriteria basicSearchCriteria : basicSearchCriterias) {
                basicQueryStatements.add(prepareQueryStatement(basicSearchCriteria.getSearchOperator(),
                        basicSearchCriteria.getSearchKey(), basicSearchCriteria.getSearchValue(), null));
            }
        }
        return prepareQueryStatement(Operator.AND, null, null, basicQueryStatements);

    }

    private QueryStatement buildNestedQueryStatement(List<BasicSearchCriteria> basicSearchCriterias) {
        List<QueryStatement> basicQueryStatements = null;

        if (CollectionUtils.isNotEmpty(basicSearchCriterias)) {
            basicQueryStatements = new ArrayList<QueryStatement>();
            for (BasicSearchCriteria basicSearchCriteria : basicSearchCriterias) {
                basicQueryStatements.add(prepareQueryStatement(basicSearchCriteria.getSearchOperator(),
                        basicSearchCriteria.getSearchKey(), basicSearchCriteria.getSearchValue(), null));
            }
        }
        return prepareQueryStatement(Operator.OR, null, null, basicQueryStatements);

    }

    private QueryStatement prepareQueryStatement(Operator operator, String key, Object value, List<QueryStatement> childStatements) {
        QueryStatement queryStatement = new QueryStatement();
        queryStatement.setOperator(operator);
        if (StringUtils.isNotBlank(key)) {
            List<KeyValuePair<String, Object>> paramValues = new ArrayList<KeyValuePair<String, Object>>();
            paramValues.add(new KeyValuePair<String, Object>(key, value));
            queryStatement.setParamValues(paramValues);
        }
        queryStatement.setQueryStatements(childStatements);
        return queryStatement;
    }

    @Override
    public Page<TransactionDocument> getTransactionDocuments(KeyValuePair<String, String> query, int pageNumber, int pageSize,
            String sortColumn, boolean sortDescending, List<String> criteriaFields, final boolean emptySreach, 
            final Boolean isApiSearch, TransactionFilterForApi transactionFilterForApi) throws BusinessException, SystemException {
        return mongoTransactionDAO.searchTransactions(query, pageNumber, pageSize, sortColumn, sortDescending, 
                criteriaFields, emptySreach, isApiSearch, transactionFilterForApi);
    }
    
    @Override
    public Page<TransactionDocument> getDefaultTransactionDocuments(Integer pageSize) throws BusinessException, SystemException {
        return mongoTransactionDAO.searchDefaultTransactions(pageSize);
    }

}
