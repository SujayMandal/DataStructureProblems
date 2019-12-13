package com.fa.dp.business.task.weekn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNActiveListingsFilter")
public class WeekNActiveListingFilter extends AbstractCommand {
	
	@Inject
	private CommandDAO commandDAO;
	
	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;
	
	@Override
	public void execute(Object data) throws SystemException {
		DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = null;
		if (checkData(data, DPProcessWeekNParamEntryInfo.class)) {
			dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);
		if (!dpProcessParamEntryInfo.isFetchProcess()) {
			Long startTime = DateTime.now().getMillis();
			log.info("WeekNActiveListingFilter -> processTask started.");
			List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
			if (!dpProcessParamEntryInfo.getColumnEntries().isEmpty()) {
				
				for (DPProcessWeekNParamInfo columnEntry : dpProcessParamEntryInfo.getColumnEntries()) {
					List<HubzuInfo> hubzuResponse = columnEntry.getHubzuDBResponse().getHubzuInfos();
					HubzuInfo lastRecord = hubzuResponse.get(hubzuResponse.size() - 1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						if (DateConversionUtil.getEstDate(sdf.parse(lastRecord.getCurrentListEndDate()).getTime()).isAfterNow()
								|| DateConversionUtil.getEstDate(sdf.parse(lastRecord.getCurrentListEndDate()).getTime()).isEqual(startTime)) {
							CommandInfo commandInfo;
							if (StringUtils.equalsIgnoreCase(columnEntry.getClassification(), DPProcessParamAttributes.OCN.getValue())) {
								List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN_OCN.getCommmandProcess(), DPAConstants.ACTIVE_LISTINGS_FILTER);
								commandInfo = convert(command.get(0), CommandInfo.class);
							} else if(StringUtils.equalsIgnoreCase(columnEntry.getClassification(), DPProcessParamAttributes.NRZ.getValue())){
								List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN_NRZ.getCommmandProcess(), DPAConstants.ACTIVE_LISTINGS_FILTER);
								commandInfo = convert(command.get(0), CommandInfo.class);
							}else{
								List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN_PHH.getCommmandProcess(), DPAConstants.ACTIVE_LISTINGS_FILTER);
								commandInfo = convert(command.get(0), CommandInfo.class);
							}
							columnEntry.setCommand(commandInfo);
							columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
							columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
							columnEntry.setExclusionReason(DPProcessFilterParams.ACTIVE_LISTINGS_EXCLUSION.getValue());
							dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(columnEntry);
						} else {
							successEntries.add(columnEntry);
						}
					} catch (ParseException e) {
						log.error("Exception in parsing time");
					}
				}
				dpProcessParamEntryInfo.setColumnEntries(successEntries);
			}
			log.info("Time Taken for WeekNActiveListingFilter is "+ (DateTime.now().getMillis() - startTime) + "ms");
			log.info("WeekNActiveListingFilter -> processTask ended.");
		}
	}
	}
}
