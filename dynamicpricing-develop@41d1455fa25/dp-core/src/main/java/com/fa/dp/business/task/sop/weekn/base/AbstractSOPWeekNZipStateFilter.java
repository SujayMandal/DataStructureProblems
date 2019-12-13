package com.fa.dp.business.task.sop.weekn.base;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNZipStateFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Named
public abstract class AbstractSOPWeekNZipStateFilter implements Command, SOPWeekNZipStateFilter {

	@Value("${weekN.excluded.state}")
	private String[] excludedStates;

	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;

	private List<String> excludedStateList;

	@PostConstruct
	public void init() {
		excludedStateList = Arrays.asList(excludedStates);
	}

	@Override
	public void executeZipStateFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPSopWeekNParamInfo> failEntries = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())) {
			paramEntryInfo.getColumnEntries().stream().filter(d -> StringUtils.isEmpty(d.getFailedStepCommandName())).forEach(paramEntry -> {
				MDC.put(RAClientConstants.LOAN_NUMBER, paramEntry.getAssetNumber());
				if(excludedStateList.contains(paramEntry.getState())) {
					paramEntry.setExclusionReason(DPProcessFilterParams.STATE_LAW.getValue()
							.replace(RAClientConstants.HASH, RAClientConstants.STATE_MARK + paramEntry.getState()));
					paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					failEntries.add(paramEntry);
				} else {
					successEntries.add(paramEntry);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
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
