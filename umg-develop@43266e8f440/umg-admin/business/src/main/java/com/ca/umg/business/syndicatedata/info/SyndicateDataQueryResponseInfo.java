package com.ca.umg.business.syndicatedata.info;

import java.util.List;
import java.util.Map;

public class SyndicateDataQueryResponseInfo {

    private List<Map<String, Object>> queryResponse;

    private List<SyndicateDataQueryParameterInfo> syndicateDataQryOutput;

    private String queryResponseType;

    private String executedQuery;

    private long queryExecutionTime;

    private boolean isResponseArray;

    public List<Map<String, Object>> getQueryResponse() {
        return queryResponse;
    }

    public void setQueryResponse(List<Map<String, Object>> queryResponse) {
        this.queryResponse = queryResponse;
    }

    public String getQueryResponseType() {
        return queryResponseType;
    }

    public void setQueryResponseType(String queryResponseType) {
        this.queryResponseType = queryResponseType;
    }

    public long getQueryExecutionTime() {
        return queryExecutionTime;
    }

    public void setQueryExecutionTime(long queryExecutionTime) {
        this.queryExecutionTime = queryExecutionTime;
    }

    public String getExecutedQuery() {
        return executedQuery;
    }

    public void setExecutedQuery(String executedQuery) {
        this.executedQuery = executedQuery;
    }

    public List<SyndicateDataQueryParameterInfo> getSyndicateDataQryOutput() {
        return syndicateDataQryOutput;
    }

    public void setSyndicateDataQryOutput(List<SyndicateDataQueryParameterInfo> syndicateDataQryOutput) {
        this.syndicateDataQryOutput = syndicateDataQryOutput;
    }

    public boolean isResponseAnArray() {
        return isResponseArray;
    }

    public void setResponseAnArray(boolean isResponseAnArray) {
        this.isResponseArray = isResponseAnArray;
    }
}
