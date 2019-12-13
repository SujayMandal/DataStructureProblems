package com.fa.dp.business.task.weekn;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNAssignmentFilter")
public class WeekNAssignmentFilter extends AbstractCommand {

	@Inject
	private DPProcessWeekNFilterDelegate dPProcessWeekNParamEntryInfo;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("weekNAssignmentFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = null;
		if (checkData(data, DPProcessWeekNParamEntryInfo.class)) {
			dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);
			log.info("Enter ::WeekNAssignmentFilterImpl :: execute method");
			try {
				dPProcessWeekNParamEntryInfo.filterRecordsOnAssigment(dpProcessParamEntryInfo);
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
			log.info("Exit ::WeekNAssignmentFilterImpl :: execute method");
		}
		log.info("Time taken for weekNAssignmentFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("weekNAssignmentFilter -> processTask ended.");
	}

}
