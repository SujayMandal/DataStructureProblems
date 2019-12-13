package com.fa.dp.business.task.sop.qa.report;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.rr.aggregator.RRClassificationAggregator;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

@Slf4j
@Named
@CommandDescription(name = "sopQaReportFetchData")
public class SopQAReportFetchData extends AbstractCommand {

	@Inject
	private RRClassificationAggregator rrClassificationAggregator;

	@Inject
	private DPSopWeekNParamDelegate dPSopWeekNParamDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("sopQaReportFetchData -> processTask ended.");
		Long startTime = DateTime.now().getMillis();

		try {
			DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo = (DPSopWeekNParamEntryInfo) data;
			rrClassificationAggregator.processSopQaReportRRTask(sopWeekNParamEntryInfo);
			//HUBZU_QUERY
			dPSopWeekNParamDelegate.processSopQAReportHubzuData(sopWeekNParamEntryInfo);
		} catch (Exception e) {
			log.error("sopQaReportFetchData failed. {}", e);
		}
		log.info("Time Taken for sopQaReportFetchData is " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("sopQaReportFetchData -> processTask ended.");
	}
}

