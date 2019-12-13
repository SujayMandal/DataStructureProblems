package com.fa.dp.business.task.weekn;

import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Scope;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

@Named
@Slf4j
@Scope("prototype")
@CommandDescription(name = "weekNOutputFileCreate")
public class WeekNOutputFileCreate extends AbstractCommand {

	@Override
	public void execute(Object data) throws SystemException {
		log.info("weekNOutputFileCreate -> processTask started.");
		DPProcessWeekNParamEntryInfo infoObject = null;
		if (checkData(data, DPProcessWeekNParamEntryInfo.class)) {
			infoObject = ((DPProcessWeekNParamEntryInfo) data);

			if (!infoObject.isFetchProcess()) {
//				String ocnFileName = generateOutputFile(infoObject, DPProcessParamAttributes.OCN.getValue());
//				String nrzFileName = generateOutputFile(infoObject, DPProcessParamAttributes.NRZ.getValue());
//
//				infoObject.getDpWeeknProcessStatus().setOcnOutputFileName(ocnFileName != null ? ocnFileName : RAClientConstants.CHAR_EMPTY);
//				infoObject.getDpWeeknProcessStatus().setNrzOutputFileName(nrzFileName != null ? nrzFileName : RAClientConstants.CHAR_EMPTY);
			}

		}
		log.info("weekNOutputFileCreate -> processTask ended.");
	}

}
