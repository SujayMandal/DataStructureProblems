package com.fa.dp.business.task.sop.weekn.filters;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

public interface SOPWeekNRAIntegrarion {
	void executeRACall(DPSopWeekNParamEntryInfo paramEntryInfo, String tenantCode, String modelName, String modelMajorVersion,
			String modelMinorVersion, String authToken) throws SystemException;
}
