package com.fa.dp.business.ssinvestor.entity;

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
@Table(name = "SPCL_SERVICING_INVESTORS")
public class SpclServicingInvestor extends AbstractAuditable {

	private static final long serialVersionUID = 7363391219924241918L;
	@ManyToOne
	@JoinColumn(name = "SS_INVESTOR_FILE_ID")
	private SpclServicingInvestorFile ssInvestorFileId;

	@Column(name = "INVESTOR_CODE")
	private String investorCode;

	@Column(name = "INVESTOR_NAME")
	private String investorName;

}
