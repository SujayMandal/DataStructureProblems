package com.fa.dp.business.task.weekn;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.rr.aggregator.RRClassificationAggregator;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNFetchData")
public class WeekNFetchData extends AbstractCommand {

	@Inject
	private RRClassificationAggregator rrClassificationAggregator;

	@Inject
	private DPProcessWeekNFilterDelegate dPProcessWeekNFilterDelegate;

	@Override
	public void execute(Object data) {
		log.info("WeekNFetchData -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		try {
			DPProcessWeekNParamEntryInfo infoObject = null;
			if (checkData(data, DPProcessWeekNParamEntryInfo.class)) {
				infoObject = ((DPProcessWeekNParamEntryInfo) data);
				if (CollectionUtils.isNotEmpty(infoObject.getColumnEntries())) {
					//RR_CLASSIFICATION
					rrClassificationAggregator.processWeekNTask(infoObject);
					if (null != infoObject.getColumnEntries()) {
						if(!infoObject.isFetchProcess()) {
							dPProcessWeekNFilterDelegate.saveParams(infoObject.getColumnEntries());
						}
						//HUBZU_QUERY
						dPProcessWeekNFilterDelegate.getHubzuData(infoObject);
						//STAGE5_QUERY
						if(!infoObject.isFetchProcess())
						dPProcessWeekNFilterDelegate.getStage5Data(infoObject);
					}
				}
			}
		} catch (SystemException e) {
			log.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		log.info("Time taken for WeekNFetchData : " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("WeekNFetchData -> processTask ended.");
	}

}
