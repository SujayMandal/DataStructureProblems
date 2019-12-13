package com.fa.dp.business.filter.delegate;

import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

public interface DPProcessFilterDelegate {

	void filterOnDuplicates(DPProcessParamEntryInfo inputParamEntry) throws SystemException;

	void filterOnInvestorCode(DPProcessParamEntryInfo inputParamEntry) throws SystemException;

	void filterOnPropertyType(DPProcessParamEntryInfo inputParamEntry) throws SystemException;

	void filterOnAssetValue(DPProcessParamEntryInfo inputParamEntry) throws SystemException;

}
