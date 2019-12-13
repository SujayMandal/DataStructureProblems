package com.ca.umg.business.syndicatedata.info;

import java.io.Serializable;

public class SyndicateDataQueryObjectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String selectString;

    private String fromString;

    private String whereClause;

    private String orderByString;

    private String executableQuery;

    public String getExecutableQuery() {
        return executableQuery;
    }

    public void setExecutableQuery(String executableQuery) {
        this.executableQuery = executableQuery;
    }

    public String getFromString() {
        return fromString;
    }

    public void setFromString(String fromString) {
        this.fromString = fromString;
    }

    public String getSelectString() {
        return selectString;
    }

    public void setSelectString(String selectString) {
        this.selectString = selectString;
    }

    public String getOrderByString() {
        return orderByString;
    }

    public void setOrderByString(String orderByString) {
        this.orderByString = orderByString;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }
}
