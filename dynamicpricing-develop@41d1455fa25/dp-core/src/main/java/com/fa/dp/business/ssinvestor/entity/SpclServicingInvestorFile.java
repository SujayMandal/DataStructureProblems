package com.fa.dp.business.ssinvestor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "SPCL_SERVICING_INVESTOR_FILES")
public class SpclServicingInvestorFile extends AbstractAuditable {

	private static final long serialVersionUID = 4176052778440578259L;
	@Column(name = "UPLOADED_FILE_NAME")
	private String uploadedFileName;

	@Column(name = "ACTIVE")
	private boolean active;

}
