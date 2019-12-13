package com.fa.dp.business.task.weekn;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNOddListingsFilter")
public class WeekNOddFilter extends AbstractCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(WeekNOddFilter.class);
	
	@Inject
	private CommandDAO commandDAO;
	
	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;
	
	@Override
	public void execute(Object data) throws SystemException {
		DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);
		if (!dpProcessParamEntryInfo.isFetchProcess()) {
			LOGGER.info("weekNOddListingsFilter -> processTask started.");
			Long startTime = DateTime.now().getMillis();
			List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
			if (!dpProcessParamEntryInfo.getColumnEntries().isEmpty()) {
				for (DPProcessWeekNParamInfo columnEntry : dpProcessParamEntryInfo.getColumnEntries()) {
					if (columnEntry.getHubzuDBResponse().getHubzuInfos().size() % 2 != 0) {
						CommandInfo commandInfo;
						if(StringUtils.equalsIgnoreCase(columnEntry.getClassification(), DPProcessParamAttributes.OCN.getValue())) {
							List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN_OCN.getCommmandProcess(), DPAConstants.ODD_LISTING_FILTER);
							commandInfo = convert(command.get(0), CommandInfo.class);
						} else if(StringUtils.equalsIgnoreCase(columnEntry.getClassification(), DPProcessParamAttributes.NRZ.getValue())){
							List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN_NRZ.getCommmandProcess(), DPAConstants.ODD_LISTING_FILTER);
							commandInfo = convert(command.get(0), CommandInfo.class);
						}else{
							List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN_PHH.getCommmandProcess(), DPAConstants.ODD_LISTING_FILTER);
							commandInfo = convert(command.get(0), CommandInfo.class);
						}
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						columnEntry.setExclusionReason(DPProcessFilterParams.ODD_LISTINGS_EXCLUSION.getValue());
						dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(columnEntry);
					} else {
						successEntries.add(columnEntry);
					}
				}
				dpProcessParamEntryInfo.setColumnEntries(successEntries);
			}
			log.info("Time taken for weekNOddListingsFilter : " + (DateTime.now().getMillis() - startTime) + "ms");
			LOGGER.info("weekNOddListingsFilter -> processTask ended.");
		}
	}
	
}
