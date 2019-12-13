package com.fa.dp.business.validation.input.info;

import lombok.Data;

@Data
public class DPFileProcessStatusInfo {

	private String id;

	private String inputFileName;

	private String ocnOutputFileName;

	private String phhOutputFileName;

	private String nrzOutputFileName;

	private String status;

	private String sysGnrtdInputFileName;

	private Long uploadTimestamp;

	private String uploadTimestampStr;

	private String process;

	private String emailTimestamp;

	private String toList;

	private String ccList;

	private Long fetchedDate;

}
