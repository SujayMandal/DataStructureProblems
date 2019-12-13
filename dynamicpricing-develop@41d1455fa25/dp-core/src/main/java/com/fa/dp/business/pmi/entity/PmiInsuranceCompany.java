package com.fa.dp.business.pmi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "PMI_INSURANCE_COMPANIES")
public class PmiInsuranceCompany extends AbstractAuditable {

	private static final long serialVersionUID = -1108482116157977101L;

	@ManyToOne
	@JoinColumn(name = "PMI_INSURANCE_COMPANY_FILE_ID")
	private PmiInsuranceCompaniesFile pmiCompaniesFileId;

	@Column(name = "INSURANCE_COMPANY")
	private String insuranceCompany;

	@Column(name = "COMPANY_CODE")
	private String companyCode;

}
