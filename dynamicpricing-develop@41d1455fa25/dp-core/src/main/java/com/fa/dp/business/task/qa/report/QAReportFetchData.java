package com.fa.dp.business.task.qa.report;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.rr.aggregator.RRClassificationAggregator;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Named
@CommandDescription(name = "qaReportFetchData")
public class QAReportFetchData extends AbstractCommand {

	@Inject
	private RRClassificationAggregator rrClassificationAggregator;

	@Inject
	private DPProcessWeekNFilterDelegate dPProcessWeekNFilterDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("qaReportFetchData -> processTask ended.");
		Long startTime = DateTime.now().getMillis();

		try {
			DPProcessWeekNParamEntryInfo weekNParamEntryInfo = (DPProcessWeekNParamEntryInfo) data;
			rrClassificationAggregator.processQaReportRRTask(weekNParamEntryInfo);
			//HUBZU_QUERY
			dPProcessWeekNFilterDelegate.processQAReportHubzuData(weekNParamEntryInfo);
		} catch (Exception e) {
			log.error("qaReportFetchData failed. {}", e);
		}
		log.info("Time Taken for qaReportFetchData is " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("qaReportFetchData -> processTask ended.");
	}
}

