package com.ca.umg.business.migration.info;

import java.io.Serializable;
import java.util.Comparator;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.Property;

public class ColumnMetaDataInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final Comparator<ColumnMetaDataInfo> ALPHABETICAL_ORDER = new Comparator<ColumnMetaDataInfo>() {
        public int compare(ColumnMetaDataInfo col1, ColumnMetaDataInfo col2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(col1.getName(), col2.getName());
            return (res != 0) ? res : col1.getName().compareTo(col2.getName());
        }
    };

    @Property
    private String name;

    @Property
    private String dataType;

    @Property
    private String size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
    
    @Override
    public final boolean equals(Object obj) {
        return Pojomatic.equals(this, obj);
    }

    @Override
    public final int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public String toString() { 
        return Pojomatic.toString(this);
    }
 
}
