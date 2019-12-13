/*
 * ColumnNames.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.info;

/**
 * 
 * 
 * ColumnNames entity.
 * 
 * @author repvenk
 *
 */
public enum ColumnNames {
    COLUMNNAME("COLUMN_NAME"), DATATYPENAME("TYPE_NAME"), COLUMNSIZE("COLUMN_SIZE"), NULLABLEFIELD("NULLABLE"), INDEXNAME(
            "INDEX_NAME"), POSITION("ORDINAL_POSITION"), DESCRIPTION("REMARKS"), SYND_VER_ID("SYNDICATEDVERID"), PRECISION(
            "DECIMAL_DIGITS"), VERSIONID("VERSION_ID");

    private String name;

    /**
     * Creates a new ColumnNames object.
     *
     * @param name
     *            DOCUMENT ME!
     **/
    private ColumnNames(String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public String getName() {
        return name;
    }
}
