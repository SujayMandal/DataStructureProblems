package com.fa.dp.business.task.weekn;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNSSPmiFilter")
public class WeekNSSPmiFilter extends AbstractCommand {

	@Inject
	private DPProcessWeekNFilterDelegate dpProcessWeekNFilterDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("WeekNSSPmiFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessWeekNParamEntryInfo infoObject = null;
		if (checkData(data, DPProcessWeekNParamEntryInfo.class)){
			infoObject = ((DPProcessWeekNParamEntryInfo) data);
			log.info("Enter ::WeekNSSPmiFilterImpl :: execute method");
			try {
				if (!CollectionUtils.sizeIsEmpty(infoObject.getColumnEntries())) {
					dpProcessWeekNFilterDelegate.filterSSPmi(infoObject);
				}
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}
			log.info("Exit ::WeekNSSPmiFilterImpl :: execute method");
		}
		log.info("Time taken for WeekNSSPmiFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("WeekNSSPmiFilter -> processTask ended.");
	}

}
