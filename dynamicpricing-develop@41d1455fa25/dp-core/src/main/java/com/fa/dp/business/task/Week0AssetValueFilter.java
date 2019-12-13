package com.fa.dp.business.task;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessFilterDelegate;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.core.exception.SystemException;
@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0AssetValueFilter")
public class Week0AssetValueFilter extends AbstractCommand {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Week0DuplicateFilter.class);

	@Inject
	private DPProcessFilterDelegate dpProcessFilterDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		LOGGER.info("week0AssetValueFilter -> processTask started.");
		DPProcessParamEntryInfo dpProcessParamEntryInfo = null;
		Long startTime = DateTime.now().getMillis();
		try {
			if (checkData(data, DPProcessParamEntryInfo.class)){
				dpProcessParamEntryInfo = ((DPProcessParamEntryInfo) data);
				dpProcessFilterDelegate.filterOnAssetValue(dpProcessParamEntryInfo);
			}
		} catch (SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		}
        log.info("Time taken for week0AssetValueFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
		LOGGER.info("week0AssetValueFilter -> processTask ended.");
	}

}
