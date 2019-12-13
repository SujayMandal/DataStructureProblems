package com.ca.umg.business.transaction.query.generator;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.transaction.query.Operator;
import com.ca.umg.business.transaction.query.QueryStatement;

public class MongoQueryParamGeneratorTest {

    @Test
    public void testGenerateQuery() throws SystemException {

        QueryStatement queryStatement = new QueryStatement();
        List<QueryStatement> queryStatements = new ArrayList<QueryStatement>();
        queryStatement.setOperator(Operator.AND);

        List<KeyValuePair<String, Object>> values1 = new ArrayList<KeyValuePair<String, Object>>();
        values1.add(new KeyValuePair<String, Object>("tenantRequest.data.MinBrokerFees", 2500));

        QueryStatement queryStatement1 = new QueryStatement();
        queryStatement1.setParamValues(values1);
        queryStatement1.setOperator(Operator.EQUAL);

        List<KeyValuePair<String, Object>> values2 = new ArrayList<KeyValuePair<String, Object>>();
        values2.add(new KeyValuePair<String, Object>("tenantRequest.data.BrokerFees", 6));

        QueryStatement queryStatement2 = new QueryStatement();
        queryStatement2.setParamValues(values2);
        queryStatement2.setOperator(Operator.EQUAL);

        queryStatements.add(queryStatement1);
        queryStatements.add(queryStatement2);

        QueryStatement wrapperStatement = new QueryStatement();
        wrapperStatement.setOperator(Operator.IN);
        List<KeyValuePair<String, Object>> wrapperValues = new ArrayList<KeyValuePair<String, Object>>();
        wrapperValues
                .add(new KeyValuePair<String, Object>("transactionId", Arrays.asList("f3006744-2d9f-40bb-aae0-c401a98d8afa")));
        wrapperStatement.setParamValues(wrapperValues);
        queryStatements.add(wrapperStatement);

        queryStatement.setQueryStatements(queryStatements);

        MongoQueryParamGenerator generator = new MongoQueryParamGenerator();
        assertNotNull(generator.generateQuery(queryStatement));
    }

    @Test
    public void testGenerateQueryWithLikeClause() throws SystemException {

        QueryStatement queryStatement = new QueryStatement();
        queryStatement.setOperator(Operator.AND);

        List<KeyValuePair<String, Object>> values = new ArrayList<KeyValuePair<String, Object>>();
        values.add(new KeyValuePair<String, Object>("tenantRequest.header.modelName", "TEST_"));

        QueryStatement q2 = new QueryStatement();
        q2.setOperator(Operator.LIKE);
        q2.setParamValues(values);

        List<QueryStatement> list = new ArrayList<QueryStatement>();
        list.add(q2);
        queryStatement.setQueryStatements(list);

        MongoQueryParamGenerator generator = new MongoQueryParamGenerator();
        String q = generator.generateQuery(queryStatement);

        assertNotNull(q);
    }

}
