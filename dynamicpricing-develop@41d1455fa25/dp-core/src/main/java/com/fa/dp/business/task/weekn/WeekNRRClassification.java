package com.fa.dp.business.task.weekn;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.rr.aggregator.RRClassificationAggregator;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope("prototype")
@CommandDescription(name = "weekNRRClassification")
public class WeekNRRClassification extends AbstractCommand {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WeekNRRClassification.class);

	@Inject
	private RRClassificationAggregator rrClassificationAggregator;
	
	@Override
    public void execute(Object data) throws SystemException {
		LOGGER.info("WeekNRRClassification -> processTask started.");
		try {
			DPProcessWeekNParamEntryInfo infoObject = null;
			if (checkData(data, DPProcessWeekNParamEntryInfo.class)){
				infoObject = ((DPProcessWeekNParamEntryInfo) data);
				if(CollectionUtils.isNotEmpty(infoObject.getColumnEntries())){
				rrClassificationAggregator.processWeekNTask(infoObject);
				}
			}
		} catch (SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		LOGGER.info("WeekNRRClassification -> processTask ended.");
	}

}
