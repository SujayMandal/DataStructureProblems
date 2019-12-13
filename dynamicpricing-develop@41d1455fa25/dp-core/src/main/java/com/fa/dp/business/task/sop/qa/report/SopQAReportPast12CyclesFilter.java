package com.fa.dp.business.task.sop.qa.report;

import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

@Slf4j
@Named
@CommandDescription(name = "sopQaReportPast12CyclesFilter")
public class SopQAReportPast12CyclesFilter extends AbstractCommand {
	@Override
	public void execute(Object data) throws SystemException {
		log.info("sopQaReportPast12CyclesFilter -> processTask ended.");
		Long startTime = DateTime.now().getMillis();

		DPSopWeekNParamEntryInfo dpProcessParamEntryInfo = ((DPSopWeekNParamEntryInfo) data);
		if (dpProcessParamEntryInfo != null && CollectionUtils.isNotEmpty(dpProcessParamEntryInfo.getColumnEntries())) {
			for (DPSopWeekNParamInfo columnEntry : dpProcessParamEntryInfo.getColumnEntries()) {
				log.info("sopQaReportPast12CyclesFilter classification : {}, ", columnEntry.getClassification(),
						columnEntry.getHubzuDBResponse() != null && CollectionUtils.isNotEmpty(columnEntry.getHubzuDBResponse().getHubzuInfos()) ?
								columnEntry.getHubzuDBResponse().getHubzuInfos().size() :
								0);
				if (StringUtils
						.equalsAny(columnEntry.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.PHH.getValue())
						&& columnEntry.getHubzuDBResponse() != null && CollectionUtils.isNotEmpty(columnEntry.getHubzuDBResponse().getHubzuInfos())
						&& columnEntry.getHubzuDBResponse().getHubzuInfos().size() > 12) {
					columnEntry.setExclusionReason(DPProcessFilterParams.PAST_12_CYCLES_EXCLUSION.getValue());
				}
			}
		}

		log.info("Time Taken for sopQaReportPast12CyclesFilter is " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("sopQaReportPast12CyclesFilter -> processTask ended.");
	}
}
