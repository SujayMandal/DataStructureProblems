package com.fa.dp.business.task.sop.weekn.base;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.util.ObjectUtils;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNPast12CyclesFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Slf4j
@Named
public abstract class AbstractSOPWeekNPast12CyclesFilter implements Command, SOPWeekNPast12CyclesFilter {
	
	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;
	
	@Override
	public void executePast12CycleFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {
		log.info("sopWeekNPast12CyclesFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		if (paramEntryInfo.getColumnEntries() != null) {
			for (DPSopWeekNParamInfo columnEntry : paramEntryInfo.getColumnEntries()) {
				if (columnEntry.getFailedStepCommandName() == null) {
					if (!ObjectUtils.isEmpty(columnEntry.getHubzuDBResponse().getHubzuInfos())
							&& columnEntry.getHubzuDBResponse().getHubzuInfos().size() > 12) {
						columnEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
						columnEntry.setListEndDateDtNn(columnEntry.getHubzuDBResponse().getHubzuInfos().get(11).getListEndDateDtNn() == null ?
								RAClientConstants.CHAR_EMPTY :
								columnEntry.getHubzuDBResponse().getHubzuInfos().get(11).getListEndDateDtNn().toString());
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						columnEntry.setExclusionReason(DPProcessFilterParams.PAST_12_CYCLES_EXCLUSION.getValue());
						if (!paramEntryInfo.isFetchProcess())
							sopWeekNParamDelegate.saveSopWeekNParamInfo(columnEntry);
					} else {
						successEntries.add(columnEntry);
					}
				}
			}
			if (!paramEntryInfo.isFetchProcess())
				paramEntryInfo.setColumnEntries(successEntries);
		}
		log.info("Time taken for sopWeekNPast12CyclesFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("sopWeekNPast12CyclesFilter -> processTask ended.");
	}
}
