package com.fa.dp.business.task.sop.weekn.filters;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

public interface SOPWeekNZipStateFilter {
	void executeZipStateFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException;
}
