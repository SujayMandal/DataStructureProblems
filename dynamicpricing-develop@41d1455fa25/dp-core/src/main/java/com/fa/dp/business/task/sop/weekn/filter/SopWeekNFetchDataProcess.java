package com.fa.dp.business.task.sop.weekn.filter;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

public interface SopWeekNFetchDataProcess {
	void executeWeekNFetchProcess(DPSopWeekNParamEntryInfo data) throws SystemException;
}
