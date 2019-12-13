package com.ca.umg.business.syndicatedata.info;

import org.hibernate.validator.constraints.NotEmpty;

public class SyndicateDataKeyColumnInfo {

    @NotEmpty(message = "Column name cannot be empty")
    private String columnName;

    private boolean status;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
