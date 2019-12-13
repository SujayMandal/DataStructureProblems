/*
 * Model.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.model.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 **/

@Entity
@Table(name = "MODEL")
@Audited
public class Model extends MultiTenantEntity {
    private static final long serialVersionUID = 1080669030501322783L;

    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    @Column(name = "NAME")
    @Property
    private String name;

    @NotNull(message = "Description cannot be null.")
    @NotBlank(message = "Description cannot be blank.")
    @Column(name = "DESCRIPTION")
    @Property
    private String description;

    @NotNull(message = "Umg name cannot be null.")
    @NotBlank(message = "Umg name cannot be blank.")
    @Column(name = "UMG_NAME", unique = true)
    @Property
    private String umgName;

    @NotNull(message = "IO definition name cannot be null.")
    @NotBlank(message = "IO definition name cannot be blank.")
    @Column(name = "IO_DEFINITION_NAME")
    @Property
    private String ioDefinitionName;

    @NotNull(message = "Documentation name cannot be null.")
    @NotBlank(message = "Documentation name cannot be blank.")
    @Column(name = "DOC_NAME")
    @Property
    private String documentationName;
    
    @Column(name = "ALLOW_NULL")
    @Property
    private boolean allowNull;

    @Column(name = "IO_DEF_EXCEL_NAME")
    @Property
    private String ioDefExcelName;

    @OneToOne(mappedBy = "model", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Property
    private ModelDefinition modelDefinition;

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name
     *            DOCUMENT ME!
     **/
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUmgName() {
        return umgName;
    }

    public void setUmgName(String umgName) {
        this.umgName = umgName;
    }

    public String getIoDefinitionName() {
        return ioDefinitionName;
    }

    public void setIoDefinitionName(String ioDefinitionName) {
        this.ioDefinitionName = ioDefinitionName;
    }

    public String getDocumentationName() {
        return documentationName;
    }

    public void setDocumentationName(String documentationName) {
        this.documentationName = documentationName;
    }

    public ModelDefinition getModelDefinition() {
        return modelDefinition;
    }

    public void setModelDefinition(ModelDefinition modelDefinition) {
        this.modelDefinition = modelDefinition;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    public String getIoDefExcelName() {
        return ioDefExcelName;
    }

    public void setIoDefExcelName(String ioDefExcelName) {
        this.ioDefExcelName = ioDefExcelName;
    }



}
