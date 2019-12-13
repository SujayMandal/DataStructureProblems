package com.fa.dp.business.sop.week0.input.info;

import com.fa.dp.core.base.info.BaseInfo;
import lombok.Data;

@Data
public class DPSopWeek0ProcessStatusInfo extends BaseInfo {

	private static final long serialVersionUID = -2613950639133426133L;

	private String inputFileName;

	private String status;
	
	private String sysGnrtdInputFileName;
}
