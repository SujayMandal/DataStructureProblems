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
@CommandDescription(name = "qaReportAssignmentFilter")
public class QAReportAssignmentFilter extends AbstractCommand {

	@Inject
	private DPProcessWeekNFilterDelegate dPProcessWeekNParamEntryInfo;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("qaReportAssignmentFilter -> processTask ended.");
		Long startTime = DateTime.now().getMillis();

		DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);
		log.info("Enter ::WeekNAssignmentFilterImpl :: execute method");
		try {
			dPProcessWeekNParamEntryInfo.filterQAReportAssigment(dpProcessParamEntryInfo);
		} catch (Exception e) {
			log.error("qaReportAssignmentFilter failed {}", e);
		}

		log.info("Time Taken for qaReportAssignmentFilter is "+ (DateTime.now().getMillis() - startTime) + "ms");
		log.info("qaReportAssignmentFilter -> processTask ended.");
	}
}
