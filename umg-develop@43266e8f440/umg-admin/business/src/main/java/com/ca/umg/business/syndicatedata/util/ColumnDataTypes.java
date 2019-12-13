package com.ca.umg.business.syndicatedata.util;

public enum ColumnDataTypes {

    NUMBER("NUMBER"), INTEGER("INTEGER"), VARCHAR("VARCHAR"), STRING("VARCHAR"), DATE("DATE"), DATETIME("DATETIME"), BOOLEAN(
            "TINTINT"), TIMESTAMP("TIMESTAMP"), DOUBLE("DOUBLE"), DECIMAL("DECIMAL");

    private String dataType;

    private ColumnDataTypes(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }

}
