package com.ca.umg.business.transaction.query;

import java.util.List;

import com.ca.framework.core.util.KeyValuePair;

/**
 * 
 * @author kamathan
 *
 */
public class QueryStatement {

    private List<QueryStatement> queryStatements;
    private List<KeyValuePair<String, Object>> paramValues;
    private Operator operator;

    public List<QueryStatement> getQueryStatements() {
        return queryStatements;
    }

    public void setQueryStatements(List<QueryStatement> queryStatements) {
        this.queryStatements = queryStatements;
    }

    public List<KeyValuePair<String, Object>> getParamValues() {
        return paramValues;
    }

    public void setParamValues(List<KeyValuePair<String, Object>> paramValues) {
        this.paramValues = paramValues;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "QueryStatement [queryStatements=" + queryStatements + ", paramValues=" + paramValues + ", operator=" + operator
                + "]";
    }

}
