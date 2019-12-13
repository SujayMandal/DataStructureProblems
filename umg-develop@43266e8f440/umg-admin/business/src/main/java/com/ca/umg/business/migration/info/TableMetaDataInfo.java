package com.ca.umg.business.migration.info;

import java.io.Serializable;
import java.util.List;

public class TableMetaDataInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tableName;

    private List<ColumnMetaDataInfo> columnMetaData;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnMetaDataInfo> getColumnMetaData() {
        return columnMetaData;
    }

    public void setColumnMetaData(List<ColumnMetaDataInfo> columnMetaData) {
        this.columnMetaData = columnMetaData;
    }

}
