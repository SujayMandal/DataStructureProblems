package com.fa.dp.business.task.sop.weekn.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.MDC;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.sop.weekN.bo.DPSopWeekNParamBO;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNActiveListingsFilter;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Slf4j
@Named
public abstract class AbstractSOPWeekNActiveListingsFilter implements SOPWeekNActiveListingsFilter, Command {

	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;
	
	@Inject
	private DPSopWeekNParamBO dpSopWeekNParamBO;

	@Override
	public void executeActiveListingFilter(DPSopWeekNParamEntryInfo paramEntryInfo) throws SystemException {
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPSopWeekNParamInfo> failEntries = new ArrayList<>();
		if (!paramEntryInfo.isFetchProcess()) {
			Long startTime = DateTime.now().getMillis();
			if(CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())) {
				for(DPSopWeekNParamInfo paramEntry : paramEntryInfo.getColumnEntries()){
					List<HubzuInfo> hubzuResponse = paramEntry.getHubzuDBResponse().getHubzuInfos();
					HubzuInfo lastRecord = hubzuResponse.get(hubzuResponse.size() - 1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						if (DateConversionUtil.getEstDate(sdf.parse(lastRecord.getCurrentListEndDate()).getTime()).isAfterNow()
								|| DateConversionUtil.getEstDate(sdf.parse(lastRecord.getCurrentListEndDate()).getTime()).isEqual(startTime)) {
							paramEntry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
							paramEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
							paramEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
							paramEntry.setExclusionReason(DPProcessFilterParams.ACTIVE_LISTINGS_EXCLUSION.getValue());
								dpSopWeekNParamBO.saveSopWeekNParamInfo(paramEntry);
						} else {
							successEntries.add(paramEntry);
						}
					} catch (ParseException e) {
						log.error("Exception in parsing time");
						SystemException.newSystemException(CoreExceptionCodes.DPSOPWKN024);
					}
				};
				paramEntryInfo.setColumnEntries(successEntries);
				if(CollectionUtils.isNotEmpty(failEntries) && !paramEntryInfo.isFetchProcess()) {
					sopWeekNParamDelegate.saveSopWeekNParamInfoList(failEntries);
				}
			}
		}
	}
}
