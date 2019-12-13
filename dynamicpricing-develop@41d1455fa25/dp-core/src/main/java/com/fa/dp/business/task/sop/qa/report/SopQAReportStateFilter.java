package com.fa.dp.business.task.sop.qa.report;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

@Slf4j
@Named
@CommandDescription(name = "sopQaReportStateFilter")
public class SopQAReportStateFilter extends AbstractCommand {

	@Inject
	private DPSopWeekNParamDelegate dPSopWeekNParamDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("sopQaReportStateFilter -> processTask ended.");
		Long startTime = DateTime.now().getMillis();

		DPSopWeekNParamEntryInfo infoObject = ((DPSopWeekNParamEntryInfo) data);
		try {
			dPSopWeekNParamDelegate.filterSopQAReportState(infoObject);
		} catch (Exception e) {
			log.error("sopQaReportStateFilter failure. {}", e);
		}

		log.info("Time Taken for sopQaReportStateFilter is " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("sopQaReportStateFilter -> processTask ended.");
	}
}
