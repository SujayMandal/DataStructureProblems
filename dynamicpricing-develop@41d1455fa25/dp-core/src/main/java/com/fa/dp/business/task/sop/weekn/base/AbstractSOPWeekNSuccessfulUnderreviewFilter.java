package com.fa.dp.business.task.sop.weekn.base;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessFilterDelegate;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNSuccessfulUnderreviewFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Named
public abstract class AbstractSOPWeekNSuccessfulUnderreviewFilter implements Command, SOPWeekNSuccessfulUnderreviewFilter {

	@Inject
	private DPSopProcessFilterDelegate dpSopProcessFilterDelegate;

	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;

	@Override
	public void executeSuccessfulUnderReviewFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {

		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())) {
			//List<String> assetNumbers = paramEntryInfo.getColumnEntries().stream().map(entry->entry.getAssetNumber()).collect(Collectors.toList());
			//List<String> filteredAssetNumbers = sopWeekNParamDelegate.searchSopWeekNParamSuccesfulUnderRiview(assetNumbers);
			paramEntryInfo.getColumnEntries().forEach(paramEntry -> {
				List<HubzuInfo> hubzuResponse = paramEntry.getHubzuDBResponse().getHubzuInfos();
				HubzuInfo lastRecord = hubzuResponse.get(hubzuResponse.size() - 1);
				if(StringUtils.isBlank(lastRecord.getListSttsDtlsVc())) {
					successEntries.add(paramEntry);
				} else if(StringUtils.equalsAny(lastRecord.getListSttsDtlsVc(), RAClientConstants.SUCCESSFUL, RAClientConstants.UNDERREVIEW)) {
					//Fetch entries from SOP WeekN db where delivery date is null
					List<DPSopWeekNParamInfo> sopProcessParamList = null;
					try {
						sopProcessParamList = sopWeekNParamDelegate.searchSopWeekNParamSuccesfulUnderRiview(paramEntry.getAssetNumber());
					} catch (SystemException e) {
						log.error("Problem in calling sop successful underriview.", e);
					}
					if(CollectionUtils.isNotEmpty(sopProcessParamList)) {
						if(!paramEntryInfo.isFetchProcess()) {
							paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
							paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
							paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
							paramEntry.setExclusionReason(DPProcessFilterParams.SUCCESFUL_UNDERREVIEW_EXCLUSION.getValue());
							try {
								sopWeekNParamDelegate.saveSopWeekNParamInfo(paramEntry);
							} catch (SystemException e) {
								log.error("sop weekn saving failure", e);
							}
						}
					} else {
						successEntries.add(paramEntry);
					}
				} else {
					successEntries.add(paramEntry);
				}
			});
			paramEntryInfo.setColumnEntries(successEntries);
		}
	}
}
