package com.fa.dp.business.task.qa.report;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.inject.Named;

@Slf4j
@Named
@CommandDescription(name = "qaReportPast12CyclesFilter")
public class QAReportPast12CyclesFilter extends AbstractCommand {
	@Override
	public void execute(Object data) throws SystemException {
		log.info("qaReportPast12CyclesFilter -> processTask ended.");
		Long startTime = DateTime.now().getMillis();

		DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);
		if (dpProcessParamEntryInfo != null && CollectionUtils.isNotEmpty(dpProcessParamEntryInfo.getColumnEntries())) {
			for (DPProcessWeekNParamInfo columnEntry : dpProcessParamEntryInfo.getColumnEntries()) {
				log.info("qaReportPast12CyclesFilter classification : {}, ", columnEntry.getClassification(),
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

		log.info("Time Taken for qaReportPast12CyclesFilter is " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("qaReportPast12CyclesFilter -> processTask ended.");
	}
}
