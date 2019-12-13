package com.fa.dp.business.sop.weekN.input.info;

import com.fa.dp.core.base.info.BaseInfo;
import lombok.Data;

@Data
public class DPSopWeekNProcessStatusInfo extends BaseInfo {

	private static final long serialVersionUID = 6072745577852951527L;
	private String inputFileName;
	private String status;
	private String sysGnrtdInputFileName;
}
