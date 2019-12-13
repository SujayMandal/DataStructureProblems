package com.fa.dp.business.week0.entity;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the DYNAMIC_PRICING_INTG_AUDITS database table.
 * 
 */
@Entity
@Setter
@Getter
@Table(name = "DYNAMIC_PRICING_INTG_AUDITS")
public class DynamicPricingIntgAudit extends AbstractAuditable {

	private static final long serialVersionUID = -8565777672091586644L;
	@Column(name = "END_TIME")
	private BigInteger endTime;

	@Column(name = "EVENT_TYPE")
	private String eventType;

	@Column(name = "START_TIME")
	private BigInteger startTime;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDescription;

	// bi-directional many-to-one association to DPProcessParam
	@ManyToOne
	@JoinColumn(name = "RECORD_ID")
	private DPProcessParam dpProcessParam;

}