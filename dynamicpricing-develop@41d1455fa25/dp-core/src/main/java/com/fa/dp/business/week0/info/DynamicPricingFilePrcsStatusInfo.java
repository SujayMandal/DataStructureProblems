package com.fa.dp.business.week0.info;

import com.fa.dp.core.base.info.BaseInfo;

import lombok.Data;

@Data
public class DynamicPricingFilePrcsStatusInfo extends BaseInfo {

	private static final long serialVersionUID = 512053651519075110L;
	private String inputFileName;

	private String ocnOutputFileName;

	private String nrzOutputFileName;

	private String phhOutputFileName;

	private String status;

	private String sysGnrtdInputFileName;

	private String uploadTimestamp;

	private String process;

	private String emailTimestamp;

	private String toList;

	private String ccList;

}