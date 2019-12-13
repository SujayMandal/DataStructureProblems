package com.ca.umg.business.batching.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * Entity implementation class for Entity: TransportParameters
 *
 */
@Entity
@Table(name = "TRANSPORT_PARAMETERS")
public class TransportParameters extends AbstractAuditable implements Serializable {

	private static final long serialVersionUID = -7301076859543074455L;
	
	@ManyToOne
    @JoinColumn(name = "TRANSPORT_TYPE_ID")
    @Property
	private TransportTypes transportType;
	
	@NotNull(message = "Transport type parameter cannot be null")
    @NotBlank(message = "Transport type parameter cannot be empty")
    @Column(name = "TRANSPORT_TYPE_PARAMETER")
    @Property
	private String transportTypeParameter;
	
	@NotNull(message = "Transport type name cannot be null")
    @NotBlank(message = "Transport type name cannot be empty")
    @Column(name = "DEFAULT_VALUE")
    @Property
	private String defaultValue;

	public TransportTypes getTransportType() {
		return transportType;
	}

	public void setTransportType(TransportTypes transportType) {
		this.transportType = transportType;
	}

	public String getTransportTypeParameter() {
		return transportTypeParameter;
	}

	public void setTransportTypeParameter(String transportTypeParameter) {
		this.transportTypeParameter = transportTypeParameter;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
	
}
