/*
 * Model.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.framework.core.bo;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 **/

public class MockEntity extends AbstractAuditable {
	private static final long serialVersionUID = 1080669030501322783L;

	@NotNull(message="Name cannot be null")
	@NotBlank(message="Name cannot be blank")
	@Property
	@Column(name = "NAME")
	private String name;
	
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
}
