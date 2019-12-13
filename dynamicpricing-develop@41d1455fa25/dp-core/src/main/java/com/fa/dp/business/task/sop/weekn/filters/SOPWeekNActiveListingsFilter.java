package com.fa.dp.business.task.sop.weekn.filters;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

public interface SOPWeekNActiveListingsFilter {
	void executeActiveListingFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException;
}
