/*
 * SyndicateDataColumnInfo.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.info;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.constants.BusinessConstants;

/**
 * Syndicate Data Column Information.
 *
 * @author mandavak
 **/
public class SyndicateDataColumnInfo extends BaseInfo {
    private static final long serialVersionUID = 3450374889234138914L;

    // no spaces and special characters
    @Pattern(regexp = "^[a-zA-Z0-9_]{1,64}$", message = "Column name cannot contain special charaters and spaces")
    @NotEmpty(message = "Column name cannot be empty")
    @Size(max = BusinessConstants.NUMBER_SIXTY_FOUR, message = "Column name can be maximum 64 characters")
    private String displayName;

    @NotEmpty(message = "Column description cannot be empty")
    @Size(max = BusinessConstants.NUMBER_TWO_HUNDRED, message = "Column description can be maximum 200 characters")
    private String description;

    // String, Integer, Char, Date, Double, Boolean
    @NotEmpty(message = "Data type cannot be empty")
    private String columnType;

    @NotNull(message = "Column size cannot be empty")
    private int columnSize;

    private int precision;

    private boolean mandatory;

    private Integer index;

    private String field;

    private String width;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public String getColumnType() {
        return columnType;
    }

    /**
     * DOCUMENT ME!
     *
     * @param columnType
     *            DOCUMENT ME!
     **/
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public int getColumnSize() {
        return columnSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param columnSize
     *            DOCUMENT ME!
     **/
    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nullable
     *            DOCUMENT ME!
     **/
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @param precision
     *            the precision to set
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }
}
