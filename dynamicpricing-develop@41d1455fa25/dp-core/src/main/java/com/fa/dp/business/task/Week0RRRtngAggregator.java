package com.fa.dp.business.task;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.rr.rtng.mapper.RrRtngAggregator;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0RRRtngAggregator")
public class Week0RRRtngAggregator extends AbstractCommand {

	@Inject
	private RrRtngAggregator rrRtngAggregator;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("Week0RRRtngAggregator -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessParamEntryInfo dpProcessParamEntryInfo = null;
		try {
			if (checkData(data, DPProcessParamEntryInfo.class)){
				dpProcessParamEntryInfo = ((DPProcessParamEntryInfo) data);
				if(CollectionUtils.isNotEmpty(dpProcessParamEntryInfo.getColumnEntries())){
					rrRtngAggregator.prepareRARespose(dpProcessParamEntryInfo);
				}
			}
		} catch (SystemException e) {
			log.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
		log.info("Time taken for Week0RRRtngAggregator : " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("Week0RRRtngAggregator -> processTask ended.");
	}

}
