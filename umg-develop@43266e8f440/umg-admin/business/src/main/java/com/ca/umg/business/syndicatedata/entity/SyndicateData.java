/*
 * SyndicateData.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * SYNDICATED_DATA table entity class
 * 
 * 
 * @author mandavak
 *
 */
@Entity
@Table(name = "SYNDICATED_DATA")
@Audited
public class SyndicateData extends AbstractAuditable {

    private static final long serialVersionUID = 1L;

    @Property
    @Column(name = "CONTAINER_NAME", nullable = false)
    private String containerName;

    @Property
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Property
    @Column(name = "VERSION_DESCRIPTION", nullable = false)
    private String versionDescription;

    @Property
    @Column(name = "VERSION_NAME", nullable = false)
    private String versionName;

    @Property
    @Column(name = "VERSION_ID", nullable = false)
    private Long versionId;

    @Property
    @Column(name = "TABLE_NAME", nullable = false)
    private String tableName;

    @Property
    @Column(name = "VALID_FROM", nullable = false)
    private Long validFrom;

    @Property
    @Column(name = "VALID_TO", nullable = false)
    private Long validTo;

    /**
     * @return the containerName
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * @param containerName
     *            the containerName to set
     */
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the versionId
     */
    public Long getVersionId() {
        return versionId;
    }

    /**
     * @param versionId
     *            the versionId to set
     */
    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     *            the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the validFrom
     */
    public Long getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom
     *            the validFrom to set
     */
    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public Long getValidTo() {
        return validTo;
    }

    /**
     * @param validTo
     *            the validTo to set
     */
    public void setValidTo(Long validTo) {
        this.validTo = validTo;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
