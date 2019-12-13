package com.fa.dp.business.task.weekn;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNZipStateFilter")
public class WeekNZipStateFilter extends AbstractCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(WeekNZipStateFilter.class);

	@Inject
	private DPProcessWeekNFilterDelegate dPProcessWeekNParamEntryInfo;

	@Override
	public void execute(Object data) throws SystemException {
		LOGGER.info("WeekNZipStateFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessWeekNParamEntryInfo infoObject = null;
		if (checkData(data, DPProcessWeekNParamEntryInfo.class)){
			infoObject = ((DPProcessWeekNParamEntryInfo) data);
			LOGGER.info("Enter ::WeekNZipStateFilterImpl :: zipStateFilter method");
			try {
				dPProcessWeekNParamEntryInfo.filterRecordsOnZipCodeAndState(infoObject);
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
			LOGGER.info("Exit ::WeekNZipStateFilterImpl :: zipStateFilter method");
		}
		log.info("Time taken for WeekNZipStateFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
		LOGGER.info("WeekNZipStateFilter -> processTask ended.");
	}

}
