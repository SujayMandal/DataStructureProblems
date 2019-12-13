package com.ca.umg.business.syndicatedata.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SyndicateDataQueryObject {

    @Column(name = "SELECT_COMPONENT")
    private String selectString;

    @Column(name = "FROM_COMPONENT")
    private String fromString;

    @Column(name = "WHERE_COMPONENT")
    private String whereClause;

    @Column(name = "ORDER_BY_COMPONENT")
    private String orderByString;

    @Column(name = "EXEC_QUERY")
    private String executableQuery;

    public String getSelectString() {
        return selectString;
    }

    public void setSelectString(String selectString) {
        this.selectString = selectString;
    }

    public String getFromString() {
        return fromString;
    }

    public void setFromString(String fromString) {
        this.fromString = fromString;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public String getOrderByString() {
        return orderByString;
    }

    public void setOrderByString(String orderByString) {
        this.orderByString = orderByString;
    }

    public String getExecutableQuery() {
        return executableQuery;
    }

    public void setExecutableQuery(String executableQuery) {
        this.executableQuery = executableQuery;
    }
}