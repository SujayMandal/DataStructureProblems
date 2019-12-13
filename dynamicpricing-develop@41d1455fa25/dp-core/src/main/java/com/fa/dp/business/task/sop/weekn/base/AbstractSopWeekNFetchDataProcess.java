package com.fa.dp.business.task.sop.weekn.base;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.rr.aggregator.RRClassificationAggregator;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.task.sop.weekn.filter.SopWeekNFetchDataProcess;
import com.fa.dp.core.exception.SystemException;

@Slf4j
public abstract class AbstractSopWeekNFetchDataProcess implements Command, SopWeekNFetchDataProcess {
	
	@Inject
	private RRClassificationAggregator rrClassificationAggregator;
	
	@Inject
	private DPSopWeekNParamDelegate dpSopWeekNParamDelegate;

	@Override
	public void executeWeekNFetchProcess(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {
		if(CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())) {
			//RR_CLASSIFICATION
			rrClassificationAggregator.processSOPWeekNTask(paramEntryInfo);
			if (null != paramEntryInfo.getColumnEntries()) {
				if(!paramEntryInfo.isFetchProcess()) {
					dpSopWeekNParamDelegate.saveSopWeekNParamInfoList(paramEntryInfo.getColumnEntries());
				}
				//HUBZU_QUERY
				dpSopWeekNParamDelegate.getHubzuData(paramEntryInfo);
				//STAGE5_QUERY
				if(!paramEntryInfo.isFetchProcess())
					dpSopWeekNParamDelegate.getStage5Data(paramEntryInfo);
			}
		}
	}
}
