package com.fa.dp.business.filter.delegate;

import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

import java.util.List;

public interface DPProcessWeekNFilterDelegate {

	void filterRecordsOnZipCodeAndState(DPProcessWeekNParamEntryInfo inputParamEntry) throws SystemException;

	void filterRecordsOnSop(DPProcessWeekNParamEntryInfo inputParamEntry) throws SystemException;

	void getHubzuData(DPProcessWeekNParamEntryInfo infoObject);

	void processQAReportHubzuData(DPProcessWeekNParamEntryInfo infoObject);

	void getStage5Data(DPProcessWeekNParamEntryInfo infoObject);

	void saveWeekNProcessStatus(DPProcessWeekNParamEntryInfo dpWeeknParamEntry);

	List<DPProcessWeekNParamInfo> saveParams(List<DPProcessWeekNParamInfo> recordsToSave);

	DPWeekNProcessStatus checkForWeekNPrcsStatus(String fileStatus);

	void updateWeeknPrcsStatus(String status, String id);

	void filterRecordsOnAssigment(DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo);

	void filterSSPmi(DPProcessWeekNParamEntryInfo infoObject);

	void filterQAReportAssigment(DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo);

	void filterQAReportState(DPProcessWeekNParamEntryInfo infoObject);

	void filterQAReportSSPmi(DPProcessWeekNParamEntryInfo infoObject);
}
