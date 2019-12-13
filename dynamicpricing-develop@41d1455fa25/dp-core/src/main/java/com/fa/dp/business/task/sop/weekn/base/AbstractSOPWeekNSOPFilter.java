package com.fa.dp.business.task.sop.weekn.base;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNSOPFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Slf4j
@Named
public abstract class AbstractSOPWeekNSOPFilter implements Command, SOPWeekNSOPFilter {

	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;

	@Override
	public void executeWeekNSopFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPSopWeekNParamInfo> failEntries = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())) {
			paramEntryInfo.getColumnEntries().stream().filter(d -> StringUtils.isEmpty(d.getFailedStepCommandName())).forEach(paramEntry -> {
				MDC.put(RAClientConstants.LOAN_NUMBER, paramEntry.getAssetNumber());
				if(StringUtils.equalsIgnoreCase(paramEntry.getSellerOccupiedProperty(), RAClientConstants.NO)) {
					paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramEntry.setExclusionReason(DPProcessFilterParams.VACANT_EXCLUSION_REASON.getValue());
					failEntries.add(paramEntry);
				} else {
					successEntries.add(paramEntry);
				}
			});
			if(!paramEntryInfo.isFetchProcess()) {
				paramEntryInfo.setColumnEntries(successEntries);
				if(CollectionUtils.isNotEmpty(failEntries)) {
					sopWeekNParamDelegate.saveSopWeekNParamInfoList(failEntries);
				}
			}
		}
	}
}
