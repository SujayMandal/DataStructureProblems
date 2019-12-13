package com.fa.dp.business.task.qa.report;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Named
@CommandDescription(name = "qaReportStateFilter")
public class QAReportStateFilter extends AbstractCommand {

	@Inject
	private DPProcessWeekNFilterDelegate dPProcessWeekNParamEntryInfo;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("qaReportStateFilter -> processTask ended.");
		Long startTime = DateTime.now().getMillis();

		DPProcessWeekNParamEntryInfo infoObject = ((DPProcessWeekNParamEntryInfo) data);
		try {
			dPProcessWeekNParamEntryInfo.filterQAReportState(infoObject);
		} catch (Exception e) {
			log.error("qaReportStateFilter failure. {}", e);
		}

		log.info("Time Taken for qaReportStateFilter is " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("qaReportStateFilter -> processTask ended.");
	}
}
