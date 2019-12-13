package com.fa.dp.business.sop.week0.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "DP_SOP_WEEK0_PRCS_STATUS")
public class DPSopWeek0ProcessStatus extends AbstractAuditable {

	private static final long serialVersionUID = 1021243721396042323L;

	@Column(name = "INPUT_FILE_NAME")
	private String inputFileName;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "SYS_GNRTD_INPUT_FILE_NAME")
	private String sysGnrtdInputFileName;

}
