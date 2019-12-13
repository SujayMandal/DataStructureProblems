package com.fa.dp.business.task.sop.weekn.filters;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

public interface SOPWeekNPast12CyclesFilter {
	void executePast12CycleFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException;
}
