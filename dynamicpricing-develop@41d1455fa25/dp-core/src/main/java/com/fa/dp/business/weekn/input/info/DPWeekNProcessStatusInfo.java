package com.fa.dp.business.weekn.input.info;

import lombok.Getter;
import lombok.Setter;

import com.fa.dp.core.base.info.BaseInfo;

/**
* @author misprakh
*/

@Getter
@Setter
public class DPWeekNProcessStatusInfo extends BaseInfo {

	private static final long serialVersionUID = 5873559873926765294L;

	private String inputFileName;

	private String sysGnrtdInputFileName;

	private String ocnOutputFileName;

	private String nrzOutputFileName;

	private String phhOutputFileName;

	private String status;

	private String fetchedDateStr;

	private String process;

	private String emailTimestamp;

	private String toList;

	private String ccList;

	private Long fetchedDate;

}
