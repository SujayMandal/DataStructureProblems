package com.fa.dp.business.week0.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the DYNAMIC_PRICING_FILE_PRCS_STATUS database table.
 */
@Entity
@Setter
@Getter
@Table(name = "DYNAMIC_PRICING_FILE_PRCS_STATUS")
public class DynamicPricingFilePrcsStatus extends AbstractAuditable {

	private static final long serialVersionUID = -6194667822262227020L;
	@Column(name = "INPUT_FILE_NAME")
	private String inputFileName;

	@Column(name = "OCN_OUTPUT_FILE_NAME")
	private String ocnOutputFileName;

	@Column(name = "NRZ_OUTPUT_FILE_NAME")
	private String nrzOutputFileName;

	@Column(name = "PHH_OUTPUT_FILE_NAME")
	private String phhOutputFileName;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "SYS_GNRTD_INPUT_FILE_NAME")
	private String sysGnrtdInputFileName;

	@Column(name = "UPLOAD_TIMESTAMP")
	private Long uploadTimestamp;

	@Column(name = "PROCESS")
	private String process;

	@Column(name = "EMAIL_TIMESTAMP")
	private String emailTimestamp;

	@Column(name = "TO_LIST")
	private String toList;

	@Column(name = "CC_LIST")
	private String ccList;

}