package com.fa.dp.business.task;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.rr.aggregator.RRClassificationAggregator;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0RRClassification")
public class Week0RRClassification extends AbstractCommand {

	@Inject
	private RRClassificationAggregator rrClassificationAggregator;

	@Override
	public void execute(Object data) throws SystemException {
		log.info("Week0RRClassification -> processTask started.");
        Long startTime = DateTime.now().getMillis();
		DPProcessParamEntryInfo dpProcessParamEntryInfo = null;
		try {
			if (checkData(data, DPProcessParamEntryInfo.class)){
				dpProcessParamEntryInfo = ((DPProcessParamEntryInfo) data);
				if(CollectionUtils.isNotEmpty(dpProcessParamEntryInfo.getColumnEntries())){
					rrClassificationAggregator.processTask(dpProcessParamEntryInfo);
				}
			}
		} /*catch (SystemException e) {
			log.error(e.getLocalizedMessage(), e);
			dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		}*/ catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		}
        log.info("Time taken for Week0RRClassification : " + (DateTime.now().getMillis() - startTime) + "ms");
		log.info("Week0RRClassification -> processTask ended.");
	}
	
}
