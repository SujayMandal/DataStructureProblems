package com.fa.dp.business.weekn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;
/**
 * @author misprakh
 *
*/

@Entity
@Setter
@Getter
@Table(name = "DP_WEEKN_PRCS_STATUS")
public class DPWeekNProcessStatus extends AbstractAuditable {

	private static final long serialVersionUID = 3048923443724440731L;
	@Column(name = "INPUT_FILE_NAME")
	private String inputFileName;

	@Column(name = "SYS_GNRTD_INPUT_FILE_NAME")
	private String sysGnrtdInputFileName;

	@Column(name = "OCN_OUTPUT_FILE_NAME")
	private String ocnOutputFileName;

	@Column(name = "NRZ_OUTPUT_FILE_NAME")
	private String nrzOutputFileName;

	@Column(name = "PHH_OUTPUT_FILE_NAME")
	private String phhOutputFileName;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "FETCHED_DATE")
	private Long fetchedDate;

	@Column(name = "PROCESS")
	private String process;

	@Column(name = "EMAIL_TIMESTAMP")
	private String emailTimestamp;

	@Column(name = "TO_LIST")
	private String toList;

	@Column(name = "CC_LIST")
	private String ccList;

}