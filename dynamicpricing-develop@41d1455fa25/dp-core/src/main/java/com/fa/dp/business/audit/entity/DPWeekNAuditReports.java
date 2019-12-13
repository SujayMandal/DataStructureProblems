package com.fa.dp.business.audit.entity;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author misprakh
 */

@Entity
@Setter
@Getter
@Table(name = "DP_WEEKN_AUDIT_REPORTS")
public class DPWeekNAuditReports extends AbstractAuditable {

	private static final long serialVersionUID = 3200102946056940338L;

	@Column(name = "RUN_DATE")
	private Long runDate;

	@Column(name = "DELIVERY_DATE")
	private Long deliveryDate;

	@Column(name = "LOAN_NUMBER")
	private String loanNumber;
	
	@Column(name = "PROP_TEMP")
	private String propTemp;

	@Column(name = "OLD_LOAN_NUMBER")
	private String oldLoanNumber;

	@Column(name = "CLASSIFICATION")
	private String classification;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "PERMANENT_EXCLUSION")
	private Boolean permanentExclusion;
}