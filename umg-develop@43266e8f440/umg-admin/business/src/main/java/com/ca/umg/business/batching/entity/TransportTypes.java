package com.ca.umg.business.batching.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;


@Entity
@Table(name = "TRANSPORT_TYPES")
public class TransportTypes extends AbstractAuditable {

	private static final long serialVersionUID = 7625919329127932755L;
	
	@NotNull(message = "Transport type name cannot be null")
    @NotBlank(message = "Transport type name cannot be empty")
    @Column(name = "NAME")
    @Property
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
