/**
 * 
 */
package com.ca.umg.business.transaction.mongo.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.BasicSearchCriteria;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.mongo.dao.MongoTransactionDAO;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.query.Operator;
import com.ca.umg.business.transaction.query.generator.MongoQueryParamGenerator;
import com.ca.umg.business.transaction.query.generator.QueryParamGenerator;

/**
 * @author kamathan
 *
 */

public class TransactionDocBOTest {

    @InjectMocks
    private TransactionDocBO transactionDocBO = new TransactionDocBOImpl();

    @Spy
    private QueryParamGenerator queryParamGenerator = new MongoQueryParamGenerator();

    @Mock
    private MongoTransactionDAO mongoTransactionDAO;

    @Before
    public void setUp() {
        initMocks(this);
    }

    /**
     * Test method for
     * {@link com.ca.umg.business.transaction.mongo.bo.TransactionDocBO#generateTransactionDocQuery(java.util.List, com.ca.umg.business.transaction.info.AdvanceTransactionFilter)}
     * .
     * 
     * @throws SystemException
     * @throws BusinessException
     */
    @Test
    public void testGenerateTransactionDocQuery() throws BusinessException, SystemException {

        List<BasicSearchCriteria> basicSearchCriterias = new ArrayList<BasicSearchCriteria>();
        basicSearchCriterias.add(getBasicSearchCriteria("versionName", "est", Operator.LIKE));

        List<BasicSearchCriteria> nestedSearchCriterias = new ArrayList<BasicSearchCriteria>();
        nestedSearchCriterias.add(getBasicSearchCriteria("errorDescription", "System", Operator.LIKE));
        
        List<BasicSearchCriteria> tranIdCriterias = new ArrayList<BasicSearchCriteria>();
        List<String> txnIds = new ArrayList<>();
        txnIds.add("123");
        tranIdCriterias.add(getBasicSearchCriteria("clientTransactionID", txnIds, Operator.IN));

        AdvanceTransactionFilter advanceTransactionFilter = new AdvanceTransactionFilter();
        advanceTransactionFilter.setCriteria("AND");
        advanceTransactionFilter.setClause1(getBasicSearchCriteria("tenantInput.data.testInt1", 1, Operator.EQUAL));
        advanceTransactionFilter.setClause2(getBasicSearchCriteria("tenantInput.data.testInt2", 0, Operator.GREATER_THAN_EQUAL));

        KeyValuePair<String, String> query = transactionDocBO.generateTransactionDocQuery(basicSearchCriterias,
                nestedSearchCriterias,  advanceTransactionFilter);

        assertNotNull(query);
    }

    @Test
    public void testGenerateTransactionDocBasicQuery() throws BusinessException, SystemException {

        List<BasicSearchCriteria> basicSearchCriterias = new ArrayList<BasicSearchCriteria>();
        basicSearchCriterias.add(getBasicSearchCriteria("versionName", "est", Operator.LIKE));

        KeyValuePair<String, String> query = transactionDocBO.generateTransactionDocQuery(basicSearchCriterias, null, null);

        assertNotNull(query);
    }

    @Test
    public void testGenerateTransactionDocAdvQuery() throws BusinessException, SystemException {

        AdvanceTransactionFilter advanceTransactionFilter = new AdvanceTransactionFilter();
        advanceTransactionFilter.setCriteria("AND");
        advanceTransactionFilter.setClause1(getBasicSearchCriteria("tenantInput.data.testInt1", 1, Operator.EQUAL));
        advanceTransactionFilter.setClause2(getBasicSearchCriteria("tenantInput.data.testInt2", 0, Operator.GREATER_THAN_EQUAL));

        KeyValuePair<String, String> query = transactionDocBO.generateTransactionDocQuery(null, null,  advanceTransactionFilter);

        assertNotNull(query);
    }

    @Test
    public void testGenerateTransactionDocBasicNAdvQuery() throws BusinessException, SystemException {

        List<BasicSearchCriteria> basicSearchCriterias = new ArrayList<BasicSearchCriteria>();
        basicSearchCriterias.add(getBasicSearchCriteria("versionName", "est", Operator.LIKE));

        AdvanceTransactionFilter advanceTransactionFilter = new AdvanceTransactionFilter();
        advanceTransactionFilter.setClause1(getBasicSearchCriteria("tenantInput.data.testInt1", 1, Operator.EQUAL));

        KeyValuePair<String, String> query = transactionDocBO.generateTransactionDocQuery(basicSearchCriterias, null,
                advanceTransactionFilter);

        assertNotNull(query);
    }

    private BasicSearchCriteria getBasicSearchCriteria(String key, Object value, Operator operator) {
        BasicSearchCriteria basicSrchCriteria = new BasicSearchCriteria();
        basicSrchCriteria.setSearchKey(key);
        basicSrchCriteria.setSearchValue(value);
        basicSrchCriteria.setSearchOperator(operator.getOperatoreValue());
        return basicSrchCriteria;
    }

    /**
     * Test method for
     * {@link com.ca.umg.business.transaction.mongo.bo.TransactionDocBO#getTransactionDocuments(java.lang.String, int, int, java.lang.String, boolean)}
     * .
     * 
     * @throws BusinessException
     * @throws SystemException
     */
    @Test
    public void testGetTransactionDocuments() throws SystemException, BusinessException {

        when(mongoTransactionDAO.searchTransactions(any(KeyValuePair.class), anyInt(), anyInt(), anyString(), anyBoolean(),
                        any(List.class), anyBoolean(), anyBoolean(), any(TransactionFilterForApi.class))).thenReturn(buildTransactionDoc());

        List<BasicSearchCriteria> basicSearchCriterias = new ArrayList<BasicSearchCriteria>();
        basicSearchCriterias.add(getBasicSearchCriteria("versionName", "est", Operator.LIKE));
        KeyValuePair<String, String> query = transactionDocBO.generateTransactionDocQuery(basicSearchCriterias, null, null);

        Page<TransactionDocument> page = transactionDocBO.getTransactionDocuments(query, 0, 10, 
                "runAsOfDate", true, new ArrayList<String>() , false, false, new TransactionFilterForApi());
        
        assertNotNull(page);
        assertEquals(1, page.getContent().size());
    }

    private Page<TransactionDocument> buildTransactionDoc() {
        List<TransactionDocument> transactionDocuments = new ArrayList<TransactionDocument>();
        transactionDocuments.add(new TransactionDocument());
        return new PageImpl<TransactionDocument>(transactionDocuments);

    }
}
