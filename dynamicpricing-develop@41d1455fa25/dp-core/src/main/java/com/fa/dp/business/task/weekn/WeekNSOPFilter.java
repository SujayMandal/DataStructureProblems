package com.fa.dp.business.task.weekn;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNSOPFilter")
public class WeekNSOPFilter extends AbstractCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(WeekNSSPmiFilter.class);

	@Inject
	private DPProcessWeekNFilterDelegate dPProcessWeekNParamEntryInfo;

	@Inject
	DPProcessWeekNFilterDelegate dpProcessWeekNFilterDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		LOGGER.info("weekNSOPFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessWeekNParamEntryInfo infoObject = null;
		if (checkData(data, DPProcessWeekNParamEntryInfo.class)) {
			infoObject = ((DPProcessWeekNParamEntryInfo) data);
			LOGGER.info("Enter ::WeekNSOPFilterImpl :: sopFilter method");
			try {
				if (!CollectionUtils.sizeIsEmpty(infoObject.getColumnEntries())) {
					dPProcessWeekNParamEntryInfo.filterRecordsOnSop(infoObject);
				}
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
			LOGGER.info("Exit ::WeekNSOPFilterImpl :: sopFilter method");
		}
		log.info("Time taken for weekNSOPFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
		LOGGER.info("weekNSOPFilter -> processTask ended.");
	}

}