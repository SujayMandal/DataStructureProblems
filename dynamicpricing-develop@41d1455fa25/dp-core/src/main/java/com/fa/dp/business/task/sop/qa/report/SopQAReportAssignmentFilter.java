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
@CommandDescription(name = "sopQaReportAssignmentFilter")
public class SopQAReportAssignmentFilter extends AbstractCommand {

	@Inject
	private DPSopWeekNParamDelegate dPSopWeekNParamDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("sopQaReportAssignmentFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();

		DPSopWeekNParamEntryInfo dpProcessParamEntryInfo = ((DPSopWeekNParamEntryInfo) data);
		log.info("Enter ::SopWeekNAssignmentFilterImpl :: execute method");
		try {
			dPSopWeekNParamDelegate.filterSOPQAReportAssigment(dpProcessParamEntryInfo);
		} catch (Exception e) {
			log.error("sopQaReportAssignmentFilter failed {}", e);
		}

		log.info("Time Taken for sopQaReportAssignmentFilter is "+ (DateTime.now().getMillis() - startTime) + "ms");
		log.info("sopQaReportAssignmentFilter -> processTask ended.");
	}
}
