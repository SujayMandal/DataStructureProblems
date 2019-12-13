package com.ca.umg.business.batching.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

@Entity
@Table(name="TRANSPORT_PARAMETER_VALUES")
public class TransportParameterValues extends MultiTenantEntity{

	private static final long serialVersionUID = 7212800557033415541L;
	

	@NotNull(message = "Transport name cannot be null")
    @NotBlank(message = "Transport name cannot be empty")
    @Column(name = "TRANSPORT_NAME")
    @Property
	private String transportName;
	
	@NotNull(message = "Parameter name cannot be null")
    @NotBlank(message = "Parameter name cannot be empty")
    @Column(name = "PARAMETER_NAME")
    @Property
	private String parameterName;
	
	@NotNull(message = "Parameter value cannot be null")
    @NotBlank(message = "Parameter value cannot be empty")
    @Column(name = "PARAMETER_VALUE")
    @Property
	private String parameterValue;

	public String getTransportName() {
		return transportName;
	}

	public void setTransportName(String transportName) {
		this.transportName = transportName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	
	
}
