package com.fa.dp.business.pmi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "PMI_INSURANCE_COMPANY_FILES")
public class PmiInsuranceCompaniesFile extends AbstractAuditable {

	private static final long serialVersionUID = 6918735128534499521L;
	@Column(name = "UPLOADED_FILE_NAME")
	private String uploadedFileName;

	@Column(name = "ACTIVE")
	private boolean active;

}
