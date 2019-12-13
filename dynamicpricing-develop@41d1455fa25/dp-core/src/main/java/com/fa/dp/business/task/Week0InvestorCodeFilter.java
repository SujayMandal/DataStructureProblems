package com.fa.dp.business.task;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.filter.delegate.DPProcessFilterDelegate;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0InvestorCodeFilter")
public class Week0InvestorCodeFilter extends AbstractCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(Week0InvestorCodeFilter.class);

	@Inject
	private DPProcessFilterDelegate dpProcessFilterDelegate;

	@Override
	public void execute(Object data) throws SystemException {
		LOGGER.info("week0InvestorCodeFilter -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessParamEntryInfo dpProcessParamEntryInfo = null;
		try {
			if (checkData(data, DPProcessParamEntryInfo.class)){
				dpProcessParamEntryInfo = ((DPProcessParamEntryInfo) data);
				dpProcessFilterDelegate.filterOnInvestorCode(dpProcessParamEntryInfo);
			}
		} catch (SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		}
		log.info("Time taken for week0InvestorCodeFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
		LOGGER.info("week0InvestorCodeFilter -> processTask ended.");
	}

}
