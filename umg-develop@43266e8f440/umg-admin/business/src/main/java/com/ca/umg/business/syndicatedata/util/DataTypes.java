package com.ca.umg.business.syndicatedata.util;

public enum DataTypes {

    INTEGER("INTEGER", "INT"), STRING("STRING", "VARCHAR"), DOUBLE("DOUBLE", "DOUBLE"), DECIMAL("DOUBLE", "DECIMAL"), DATE(
            "DATE", "DATE"), DATETIME("DATETIME", "DATETIME"), BOOLEAN("BOOLEAN", "BIT");

    private String uiDataType;
    private String dbDataType;

    private DataTypes(String uiDataType, String dbDataType) {
        this.uiDataType = uiDataType;
        this.dbDataType = dbDataType;
    }

    public String getUiDataType() {
        return uiDataType;
    }

    public String getDbDataType() {
        return dbDataType;
    }

}