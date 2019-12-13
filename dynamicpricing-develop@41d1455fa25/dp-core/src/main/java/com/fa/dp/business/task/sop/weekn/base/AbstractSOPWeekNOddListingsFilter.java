package com.fa.dp.business.task.sop.weekn.base;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.MDC;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNOddListingsFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Slf4j
@Named
public abstract class AbstractSOPWeekNOddListingsFilter implements Command, SOPWeekNOddListingsFilter {
	
	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;
	
	@Override
	public void executeOddListingFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {
		if (!paramEntryInfo.isFetchProcess()) {
			log.info("sopWeekNOddListingsFilter -> processTask started.");
			Long startTime = DateTime.now().getMillis();
			List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())) {
				for (DPSopWeekNParamInfo columnEntry : paramEntryInfo.getColumnEntries()) {
					if (columnEntry.getHubzuDBResponse().getHubzuInfos().size() % 2 != 0) {
						columnEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						columnEntry.setExclusionReason(DPProcessFilterParams.ODD_LISTINGS_EXCLUSION.getValue());
						sopWeekNParamDelegate.saveSopWeekNParamInfo(columnEntry);
					} else {
						successEntries.add(columnEntry);
					}
				}
				paramEntryInfo.setColumnEntries(successEntries);
			}
			log.info("Time taken for sopWeekNOddListingsFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
			log.info("sopWeekNOddListingsFilter -> processTask ended.");
		}
	}
}
