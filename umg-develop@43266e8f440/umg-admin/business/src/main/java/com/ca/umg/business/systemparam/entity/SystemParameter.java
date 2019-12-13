/*
 * Model.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.systemparam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 **/

@Entity
@Table(name = "SYSTEM_PARAMETER")
@Audited
public class SystemParameter extends AbstractAuditable{
    private static final long serialVersionUID = 1080669030501322783L;

    @NotNull(message = "System VALUE cannot be null.")
    @NotBlank(message = "System VALUE cannot be blank.")
    @Column(name = "SYS_KEY")
    @Property
    private String sysKey;


    @Column(name = "SYS_VALUE")
    @Property
    private String sysValue;
    
    @Column(name = "DESCRIPTION")
    @Property
    private String description;
    
    @Column(name = "IS_ACTIVE")
    @Property
    private char isActive;
    
    public char getIsActive() {
		return isActive;
	}

	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}

	public String getSysKey() {
		return sysKey;
	}

	public void setSysKey(String sysKey) {
		this.sysKey = sysKey;
	}

	public String getSysValue() {
		return sysValue;
	}

	public void setSysValue(String sysValue) {
		this.sysValue = sysValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
